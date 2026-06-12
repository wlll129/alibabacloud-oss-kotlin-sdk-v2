package com.aliyun.kotlin.sdk.service.oss2

import com.aliyun.kotlin.sdk.service.oss2.Defaults.CHECK_POINT_FILE_SUFFIX_UPLOADER
import com.aliyun.kotlin.sdk.service.oss2.credentials.StaticCredentialsProvider
import com.aliyun.kotlin.sdk.service.oss2.exceptions.InconsistentException
import com.aliyun.kotlin.sdk.service.oss2.exceptions.OperationException
import com.aliyun.kotlin.sdk.service.oss2.exceptions.RequestException
import com.aliyun.kotlin.sdk.service.oss2.exceptions.ServiceException
import com.aliyun.kotlin.sdk.service.oss2.hash.Crc64
import com.aliyun.kotlin.sdk.service.oss2.hash.Md5
import com.aliyun.kotlin.sdk.service.oss2.hash.md5
import com.aliyun.kotlin.sdk.service.oss2.models.Part
import com.aliyun.kotlin.sdk.service.oss2.models.PutObjectRequest
import com.aliyun.kotlin.sdk.service.oss2.models.UploadCheckpoint
import com.aliyun.kotlin.sdk.service.oss2.progress.ProgressListener
import com.aliyun.kotlin.sdk.service.oss2.transport.HttpTransport
import com.aliyun.kotlin.sdk.service.oss2.transport.RequestMessage
import com.aliyun.kotlin.sdk.service.oss2.transport.RequestOptions
import com.aliyun.kotlin.sdk.service.oss2.transport.ResponseMessage
import com.aliyun.kotlin.sdk.service.oss2.types.ByteStream
import com.aliyun.kotlin.sdk.service.oss2.types.toFlow
import com.aliyun.kotlin.sdk.service.oss2.utils.XmlUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents.Companion.Format
import kotlinx.datetime.format.char
import kotlinx.io.Buffer
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.files.SystemTemporaryDirectory
import kotlinx.io.readByteArray
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.math.max
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

private fun String.asByteStream(): ByteStream = ByteStream.fromString(this)
private fun ByteArray.asByteStream(): ByteStream = ByteStream.fromBytes(this)

class UploaderMockTest {

    internal class MockHttpClient(
        var type: ResponseErrorType? = null,
        var failInPartNumber: Long? = null,
        var errorCRCValuePartNumber: Long? = null,
    ) : HttpTransport {

        private val uploadedParts: MutableList<Part> = mutableListOf()
        val deleteUploadId: MutableList<String> = mutableListOf()

        var maxParallelCount = 0
        private var uploadingPartCount = 0
        enum class ResponseErrorType {
            Init,
            UploadPart,
            Complete,
            ListPart,
            Put,
            Abort
        }
        override suspend fun execute(
            request: RequestMessage,
            options: RequestOptions
        ): ResponseMessage {
            uploadingPartCount += 1
            maxParallelCount = max(maxParallelCount, uploadingPartCount)
            val errData =
                """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <Error>
                    <Code>InvalidAccessKeyId</Code>
                    <Message>The OSS Access Key Id you provided does not exist in our records.</Message>
                    <RequestId>65467C42E001B4333337****</RequestId>
                    <SignatureProvided>ak</SignatureProvided>
                    <EC>0002-00000040</EC>
                    </Error>
                """.trimIndent().replace("\n", "")

            val response = when (request.method) {
                "POST" -> {
                    if (request.url.contains("uploads")) {
                        if (type == ResponseErrorType.Init) {
                            return ResponseMessage(
                                statusCode = 403,
                                headers = mutableMapOf(
                                    "Content-Type" to "application/xml",
                                    "Content-Length" to errData.length.toString()
                                ),
                                body = errData.asByteStream()
                            )
                        }
                        val body =
                            """
                                <InitiateMultipartUploadResult>
                                <Bucket>bucket</Bucket>
                                <Key>key</Key>
                                <UploadId>uploadId-1234</UploadId>
                                </InitiateMultipartUploadResult>
                            """.trimIndent().replace("\n", "")
                        ResponseMessage(
                            statusCode = 200,
                            headers = mutableMapOf(
                                "Content-Type" to "application/xml",
                                "Content-Length" to body.length.toString()
                            ),
                            body = body.asByteStream()
                        )
                    } else {
                        if (type == ResponseErrorType.Complete) {
                            return ResponseMessage(
                                statusCode = 403,
                                headers = mutableMapOf(
                                    "Content-Type" to "application/xml",
                                    "Content-Length" to errData.length.toString()
                                ),
                                body = errData.asByteStream()
                            )
                        }
                        val body =
                            """
                                <CompleteMultipartUploadResult>
                                <EncodingType>url</EncodingType>
                                <Location>bucket/key</Location>
                                <Bucket>bucket</Bucket>
                                <Key>key</Key>
                                <ETag>etag</ETag>
                                </CompleteMultipartUploadResult>
                            """.trimIndent().replace("\n", "")
                        ResponseMessage(
                            statusCode = 200,
                            headers = mutableMapOf(
                                "Content-Type" to "application/xml",
                                "Content-Length" to body.length.toString(),
                                "x-oss-object-type" to "Multipart"
                            ),
                            body = body.asByteStream()
                        )
                    }
                }
                "PUT" -> {
                    if (request.url.contains("uploadId")) {
                        val query = extractParamsWithoutDecode(request.url)
                        val partNumber = query["partNumber"]!!.toLong()
                        if (type == ResponseErrorType.UploadPart || failInPartNumber == partNumber) {
                            return ResponseMessage(
                                statusCode = 403,
                                headers = mutableMapOf(
                                    "Content-Type" to "application/xml",
                                    "Content-Length" to errData.length.toString()
                                ),
                                body = errData.asByteStream()
                            )
                        }

                        val hash = Md5()
                        val crc64 = Crc64()
                        var size: Long = 0
                        request.body?.toFlow()?.collect { bytes ->
                            size += bytes.size
                            hash.update(bytes, 0, bytes.size)
                            crc64.update(bytes, 0, bytes.size)
                            options.uploadObservers?.forEach {
                                it.data(bytes, 0, bytes.size)
                            }
                        }
                        val eTag = hash.digest().toHexString().uppercase()
                        val crc64ecma = if (errorCRCValuePartNumber == partNumber) {
                            "0"
                        } else {
                            crc64.digestValue.toULong().toString()
                        }

                        uploadedParts.add(
                            Part {
                                this.eTag = eTag
                                this.size = size
                                hashCrc64ecma = crc64ecma
                                this.partNumber = partNumber
                            }
                        )
                        delay(0.1.seconds)
                        ResponseMessage(
                            statusCode = 200,
                            headers = mutableMapOf(
                                "Content-Type" to "application/xml",
                                "x-oss-hash-crc64ecma" to crc64ecma,
                                "ETag" to eTag
                            ),
                        )
                    } else {
                        if (type == ResponseErrorType.Put) {
                            return ResponseMessage(
                                statusCode = 403,
                                headers = mutableMapOf(
                                    "Content-Type" to "application/xml",
                                    "Content-Length" to errData.length.toString()
                                ),
                                body = errData.asByteStream()
                            )
                        }

                        val hash = Md5()
                        val crc64 = Crc64()
                        var size = 0
                        request.body?.toFlow()?.collect { bytes ->
                            size += bytes.size
                            hash.update(bytes, 0, bytes.size)
                            crc64.update(bytes, 0, bytes.size)
                            options.uploadObservers?.forEach {
                                it.data(bytes, 0, bytes.size)
                            }
                        }
                        val eTag = hash.digest().toHexString().uppercase()
                        val crc64ecma = crc64.digestValue.toULong().toString()
                        ResponseMessage(
                            statusCode = 200,
                            headers = mutableMapOf(
                                "Content-Type" to "application/xml",
                                "x-oss-hash-crc64ecma" to crc64ecma,
                                "ETag" to eTag,
                                "x-oss-object-type" to "Normal"
                            ),
                        )
                    }
                }
                "GET" -> {
                    if (type == ResponseErrorType.ListPart) {
                        return ResponseMessage(
                            statusCode = 403,
                            headers = mutableMapOf(
                                "Content-Type" to "application/xml",
                                "Content-Length" to errData.length.toString()
                            ),
                            body = errData.asByteStream()
                        )
                    }
                    val data = buildString {
                        append("<ListPartsResult>")
                        append("<Bucket>bucket</Bucket>")
                        append("<Key>key</Key>")
                        append("<UploadId>uploadId-1234</UploadId>")
                        append("<IsTruncated>false</IsTruncated>")
                        for (part in uploadedParts) {
                            append("<Part>")
                            append("<PartNumber>${part.partNumber}</PartNumber>")
                            append("<LastModified>2012-02-23T07:01:34.000Z</LastModified>")
                            append("<ETag>${part.eTag}</ETag>")
                            append("<Size>${part.size}</Size>")
                            append("<HashCrc64ecma>${part.hashCrc64ecma}</HashCrc64ecma>")
                            append("</Part>")
                        }
                        append("</ListPartsResult>")
                    }
                    ResponseMessage(
                        statusCode = 200,
                        headers = mutableMapOf(
                            "Content-Type" to "application/xml",
                            "Content-Length" to data.length.toString(),
                        ),
                        body = data.asByteStream()
                    )
                }
                "DELETE" -> {
                    if (type == ResponseErrorType.Abort) {
                        return ResponseMessage(
                            statusCode = 403,
                            headers = mutableMapOf(
                                "Content-Type" to "application/xml",
                                "Content-Length" to errData.length.toString()
                            ),
                            body = errData.asByteStream()
                        )
                    }
                    extractParamsWithoutDecode(request.url)["uploadId"]?.let {
                        deleteUploadId.add(it)
                    }
                    ResponseMessage(
                        statusCode = 204,
                        headers = mutableMapOf(),
                    )
                }
                else -> {
                    ResponseMessage()
                }
            }
            uploadingPartCount -= 1
            return response
        }

        private fun extractParamsWithoutDecode(uri: String): MutableMap<String, String> {
            val start = uri.indexOf("?")

            // no query string
            if (start < 0) {
                return mutableMapOf()
            }

            val rawQuery = uri.substring(start + 1)

            if (rawQuery.isNotEmpty()) {
                return rawQuery
                    .split("&").associate { segment ->
                        val parts = segment.split("=")
                        val key = parts[0]
                        val value = when (parts.size) {
                            1 -> ""
                            2 -> parts[1]
                            else -> throw IllegalArgumentException("invalid query string segment $segment")
                        }
                        key to value
                    }
                    .toMutableMap()
            }

            return mutableMapOf()
        }
    }

    val tempPath = "$SystemTemporaryDirectory/kotlin-sdk-test"

    @Test
    fun testUploadSinglePart() = runTest {
        val bucket = "bucket"
        val key = "key"

        val mockHandler = MockHttpClient()
        val config = ClientConfiguration().apply {
            region = "cn-hangzhou"
            credentialsProvider = StaticCredentialsProvider("ak", "sk")
            httpTransport = mockHandler
        }

        OSSClient.create(config).use { client ->
            val uploader = Uploader(client)
            val result = uploader.upload(
                PutObjectRequest {
                    this.bucket = bucket
                    this.key = key
                    body = "Hello oss.".asByteStream()
                }
            )
            assertEquals(200, result.statusCode)
            assertEquals("Normal", result.headers["x-oss-object-type"])
        }

        removeTestFile("file")
    }

    @Test
    fun testUploadSequential() = runTest {
        val bucket = "bucket"
        val key = "key"

        val mockHandler = MockHttpClient()
        val config = ClientConfiguration().apply {
            region = "cn-hangzhou"
            credentialsProvider = StaticCredentialsProvider("ak", "sk")
            httpTransport = mockHandler
        }

        OSSClient.create(config).use { client ->

            val uploader = Uploader(client, {
                it.partSize = 1 * 1024 * 1024
                it.parallelNum = 1
            })
            val result = uploader.upload(
                PutObjectRequest {
                    this.bucket = bucket
                    this.key = key
                    body = Random.nextBytes(10 * 1024 * 1024).asByteStream()
                }
            )
            assertEquals(200, result.statusCode)
            assertEquals("Multipart", result.headers["x-oss-object-type"])
            assertEquals(1, mockHandler.maxParallelCount)
        }
    }

    @Test
    fun testUploadParallel() = runTest {
        val bucket = "bucket"
        val key = "key"

        val mockHandler = MockHttpClient()
        val config = ClientConfiguration().apply {
            region = "cn-hangzhou"
            credentialsProvider = StaticCredentialsProvider("ak", "sk")
            httpTransport = mockHandler
        }

        OSSClient.create(config).use { client ->

            val uploader = Uploader(
                client,
                {
                    it.partSize = 1 * 1024 * 1024
                    it.parallelNum = 4
                }
            )
            val result = uploader.upload(
                PutObjectRequest {
                    this.bucket = bucket
                    this.key = key
                    body = Random.nextBytes(10 * 1024 * 1024).asByteStream()
                }
            )
            assertEquals(200, result.statusCode)
            assertEquals("Multipart", result.headers["x-oss-object-type"])
            assertEquals(4, mockHandler.maxParallelCount)
        }
    }

    @Test
    fun testUploadArgumentCheck() = runTest {
        val bucket = "bucket"
        val key = "key"

        val mockHandler = MockHttpClient()
        val config = ClientConfiguration().apply {
            region = "cn-hangzhou"
            credentialsProvider = StaticCredentialsProvider("ak", "sk")
            httpTransport = mockHandler
        }

        OSSClient.create(config).use { client ->

            val uploader = Uploader(client, {
                it.partSize = 1 * 1024 * 1024
                it.parallelNum = 4
            })
            var exception: Throwable = assertFailsWith<IllegalArgumentException> {
                uploader.upload(PutObjectRequest {})
            }
            assertEquals("request.bucket is required", exception.message)

            exception = assertFailsWith<IllegalArgumentException> {
                uploader.upload(
                    PutObjectRequest {
                        this.bucket = bucket
                    }
                )
            }
            assertEquals("request.key is required", exception.message)

            exception = assertFailsWith<IllegalArgumentException> {
                uploader.upload(
                    PutObjectRequest {
                        this.bucket = bucket
                        this.key = key
                    }
                )
            }
            assertEquals("request.body is required", exception.message)

            exception = assertFailsWith<RequestException> {
                uploader.upload(
                    PutObjectRequest {
                        this.bucket = bucket
                        this.key = key
                        body = ByteStream.fromFile(Path("error"))
                    }
                )
            }
            assertEquals("Cannot get the size of the body", exception.message)
        }

        removeTestFile("file")
    }

    @Test
    fun testUploadSinglePartFromFile() {
        runTest {
            val filePath = createTestFile("file", 1 * 1024 * 1024)
            val bucket = "bucket"
            val key = "key"

            val mockHandler = MockHttpClient()
            val config = ClientConfiguration().apply {
                region = "cn-hangzhou"
                credentialsProvider = StaticCredentialsProvider("ak", "sk")
                httpTransport = mockHandler
            }

            OSSClient.create(config).use { client ->

                val uploader = Uploader(client)
                val result = uploader.upload(
                    PutObjectRequest {
                        this.bucket = bucket
                        this.key = key
                        body = ByteStream.fromFile(filePath)
                    }
                )
                assertEquals(200, result.statusCode)
                assertEquals("Normal", result.headers["x-oss-object-type"])
            }

            removeTestFile("file")
        }
    }

    @Test
    fun testUploadSequentialFromFile() = runTest {
        val filePath = createTestFile("file", 10 * 1024 * 1024)
        val bucket = "bucket"
        val key = "key"

        val mockHandler = MockHttpClient()
        val config = ClientConfiguration().apply {
            region = "cn-hangzhou"
            credentialsProvider = StaticCredentialsProvider("ak", "sk")
            httpTransport = mockHandler
        }

        OSSClient.create(config).use { client ->

            val uploader = Uploader(client, {
                it.partSize = 1 * 1024 * 1024
                it.parallelNum = 1
            })
            val result = uploader.upload(
                PutObjectRequest {
                    this.bucket = bucket
                    this.key = key
                    body = ByteStream.fromFile(filePath)
                }
            )
            assertEquals(200, result.statusCode)
            assertEquals("Multipart", result.headers["x-oss-object-type"])
            assertEquals(1, mockHandler.maxParallelCount)
        }

        removeTestFile("file")
    }

    @Test
    fun testUploadParallelFromFile() = runTest {
        val filePath = createTestFile("file", 10 * 1024 * 1024)
        val bucket = "bucket"
        val key = "key"

        val mockHandler = MockHttpClient()
        val config = ClientConfiguration().apply {
            region = "cn-hangzhou"
            credentialsProvider = StaticCredentialsProvider("ak", "sk")
            httpTransport = mockHandler
        }

        OSSClient.create(config).use { client ->

            val uploader = Uploader(client, {
                it.partSize = 1 * 1024 * 1024
                it.parallelNum = 4
            })
            val result = uploader.upload(
                PutObjectRequest {
                    this.bucket = bucket
                    this.key = key
                    body = ByteStream.fromFile(filePath)
                }
            )
            assertEquals(200, result.statusCode)
            assertEquals("Multipart", result.headers["x-oss-object-type"])
            assertEquals(4, mockHandler.maxParallelCount)
        }

        removeTestFile("file")
    }

    @Test
    fun testUploadSinglePartFail() = runTest {
        val bucket = "bucket"
        val key = "key"

        val mockHandler = MockHttpClient(
            MockHttpClient.ResponseErrorType.Put
        )
        val config = ClientConfiguration().apply {
            region = "cn-hangzhou"
            credentialsProvider = StaticCredentialsProvider("ak", "sk")
            httpTransport = mockHandler
        }

        OSSClient.create(config).use { client ->

            val uploader = Uploader(client)
            val exception = assertFails {
                uploader.upload(
                    PutObjectRequest {
                        this.bucket = bucket
                        this.key = key
                        body = "Hello oss.".asByteStream()
                    }
                )
            }
            assertTrue(exception.cause is ServiceException)
            assertEquals(403, (exception.cause as ServiceException).statusCode)
        }
    }

    @Test
    fun testUploadSequentialInitiateMultipartUploadFail() = runTest {
        val bucket = "bucket"
        val key = "key"

        val mockHandler = MockHttpClient(
            MockHttpClient.ResponseErrorType.Init
        )
        val config = ClientConfiguration().apply {
            region = "cn-hangzhou"
            credentialsProvider = StaticCredentialsProvider("ak", "sk")
            httpTransport = mockHandler
        }

        OSSClient.create(config).use { client ->

            val uploader = Uploader(
                client,
                {
                    it.partSize = 1 * 1024 * 1024
                    it.parallelNum = 1
                }
            )
            val exception = assertFails {
                uploader.upload(
                    PutObjectRequest {
                        this.bucket = bucket
                        this.key = key
                        body = Random.nextBytes(10 * 1024 * 1024).asByteStream()
                    }
                )
            }
            assertTrue(exception.cause is ServiceException)
            assertEquals(403, (exception.cause as ServiceException).statusCode)
        }
    }

    @Test
    fun testUploadSequentialUploadPartFail() = runTest {
        val bucket = "bucket"
        val key = "key"

        val mockHandler = MockHttpClient(
            MockHttpClient.ResponseErrorType.UploadPart
        )
        val config = ClientConfiguration().apply {
            region = "cn-hangzhou"
            credentialsProvider = StaticCredentialsProvider("ak", "sk")
            httpTransport = mockHandler
        }

        OSSClient.create(config).use { client ->

            val uploader = Uploader(
                client,
                {
                    it.partSize = 1 * 1024 * 1024
                    it.parallelNum = 1
                }
            )
            val exception = assertFails {
                uploader.upload(
                    PutObjectRequest {
                        this.bucket = bucket
                        this.key = key
                        body = Random.nextBytes(10 * 1024 * 1024).asByteStream()
                    }
                )
            }
            assertTrue(exception.cause is ServiceException)
            assertEquals(403, (exception.cause as ServiceException).statusCode)
        }
    }

    @Test
    fun testUploadSequentialCompleteMultipartUploadFail() = runTest {
        val bucket = "bucket"
        val key = "key"

        val mockHandler = MockHttpClient(
            MockHttpClient.ResponseErrorType.Complete
        )
        val config = ClientConfiguration().apply {
            region = "cn-hangzhou"
            credentialsProvider = StaticCredentialsProvider("ak", "sk")
            httpTransport = mockHandler
        }

        OSSClient.create(config).use { client ->

            val uploader = Uploader(
                client,
                {
                    it.partSize = 1 * 1024 * 1024
                    it.parallelNum = 1
                }
            )
            val exception = assertFails {
                uploader.upload(
                    PutObjectRequest {
                        this.bucket = bucket
                        this.key = key
                        body = Random.nextBytes(10 * 1024 * 1024).asByteStream()
                    }
                )
            }
            assertTrue(exception.cause is ServiceException)
            assertEquals(403, (exception.cause as ServiceException).statusCode)
        }
    }

    @Test
    fun testUploadSequentialParallelUploadFail() = runTest {
        val bucket = "bucket"
        val key = "key"

        val mockHandler = MockHttpClient(
            MockHttpClient.ResponseErrorType.UploadPart
        )
        val config = ClientConfiguration().apply {
            region = "cn-hangzhou"
            credentialsProvider = StaticCredentialsProvider("ak", "sk")
            httpTransport = mockHandler
        }

        OSSClient.create(config).use { client ->

            val uploader = Uploader(
                client,
                {
                    it.partSize = 1 * 1024 * 1024
                    it.parallelNum = 4
                }
            )
            val exception = assertFails {
                uploader.upload(
                    PutObjectRequest {
                        this.bucket = bucket
                        this.key = key
                        body = Random.nextBytes(10 * 1024 * 1024).asByteStream()
                    }
                )
            }
            assertTrue(exception.cause is ServiceException)
            assertEquals(403, (exception.cause as ServiceException).statusCode)
        }
    }

    @Test
    fun testUploadEnableCheckpointNotUseCp() = runTest {
        val filePath = createTestFile("file", 10 * 1024 * 1024)
        val bucket = "bucket"
        val key = "key"

        val mockHandler = MockHttpClient()
        val config = ClientConfiguration().apply {
            region = "cn-hangzhou"
            credentialsProvider = StaticCredentialsProvider("ak", "sk")
            httpTransport = mockHandler
        }

        OSSClient.create(config).use { client ->

            val uploader = Uploader(client, {
                it.partSize = 1 * 1024 * 1024
                it.enableCheckpoint = true
            })
            val result = uploader.upload(
                PutObjectRequest {
                    this.bucket = bucket
                    this.key = key
                    body = ByteStream.fromFile(filePath)
                }
            )
            assertEquals(200, result.statusCode)
            assertEquals("Multipart", result.headers["x-oss-object-type"])
        }

        removeTestFile("file")
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun testUploadEnableCheckpointUseCp() = runTest {
        val filePath = createTestFile("test-usecp-file", 10 * 1024 * 1024)
        val checkpointDir = Path("$SystemTemporaryDirectory/oss-kotlin-sdk-test/checkpoint/")
        val bucket = "bucket"
        val key = "key"
        val name = "$bucket/$key"
        val destHash = "oss://${XmlUtils.escapeText(name)}".toByteArray().md5().toHexString()
        val srcHash = filePath.toString().toByteArray().md5().toHexString()
        val cpFilePath = Path("$checkpointDir/$srcHash-$destHash$CHECK_POINT_FILE_SUFFIX_UPLOADER")
        val lastModified = Instant.fromEpochMilliseconds(File(filePath.toString()).lastModified()).format(
            Format {
                year()
                char('-')
                monthNumber()
                char('-')
                day()
                char('T')
                hour()
                char(':')
                minute()
                char(':')
                second()
                chars("Z")
            }
        )

        val mockHandler = MockHttpClient()
        val config = ClientConfiguration().apply {
            region = "cn-hangzhou"
            credentialsProvider = StaticCredentialsProvider("ak", "sk")
            httpTransport = mockHandler
        }

        OSSClient.create(config).use { client ->

            // Case 2, fail in part number 4
            mockHandler.failInPartNumber = 4
            val uploader = Uploader(
                client,
                {
                    it.partSize = 1 * 1024 * 1024
                    it.enableCheckpoint = true
                    it.checkpointDir = checkpointDir
                }
            )
            var exception = assertFails {
                uploader.upload(
                    PutObjectRequest {
                        this.bucket = bucket
                        this.key = key
                        body = ByteStream.fromFile(filePath)
                    }
                )
            }
            assertTrue(exception.cause is ServiceException)
            assertEquals(403, (exception.cause as ServiceException).statusCode)
            assertTrue(SystemFileSystem.exists(cpFilePath))
            var bytes = SystemFileSystem.source(cpFilePath).buffered().use {
                it.readByteArray()
            }
            var info = Json.decodeFromString<UploadCheckpoint.Info>(bytes.decodeToString())
            assertEquals(1 * 1024 * 1024, info.data.partSize)
            assertEquals("uploadId-1234", info.data.uploadInfo?.uploadId)
            assertEquals("oss://${XmlUtils.escapeText(name)}", info.data.objectInfo.objectKey)
            assertEquals(10 * 1024 * 1024, info.data.fileMeta.size)
            assertEquals(lastModified, info.data.fileMeta.lastModified)

            mockHandler.failInPartNumber = null
            var result = uploader.upload(
                PutObjectRequest {
                    this.bucket = bucket
                    this.key = key
                    body = ByteStream.fromFile(filePath)
                }
            )
            assertEquals(200, result.statusCode)
            assertEquals("Multipart", result.headers["x-oss-object-type"])
            assertEquals(0, SystemFileSystem.list(checkpointDir).size)

            // Case 2, fail in part number 1
            mockHandler.failInPartNumber = 1
            exception = assertFails {
                uploader.upload(
                    PutObjectRequest {
                        this.bucket = bucket
                        this.key = key
                        body = ByteStream.fromFile(filePath)
                    }
                )
            }
            assertTrue(exception.cause is ServiceException)
            assertEquals(403, (exception.cause as ServiceException).statusCode)
            assertTrue(SystemFileSystem.exists(cpFilePath))
            bytes = SystemFileSystem.source(cpFilePath).buffered().use {
                it.readByteArray()
            }
            info = Json.decodeFromString<UploadCheckpoint.Info>(bytes.decodeToString())
            assertEquals(1 * 1024 * 1024, info.data.partSize)
            assertEquals("uploadId-1234", info.data.uploadInfo?.uploadId)
            assertEquals("oss://${XmlUtils.escapeText(name)}", info.data.objectInfo.objectKey)
            assertEquals(10 * 1024 * 1024, info.data.fileMeta.size)
            assertEquals(lastModified, info.data.fileMeta.lastModified)

            mockHandler.failInPartNumber = null
            result = uploader.upload(
                PutObjectRequest {
                    this.bucket = bucket
                    this.key = key
                    body = ByteStream.fromFile(filePath)
                }
            )
            assertEquals(200, result.statusCode)
            assertEquals("Multipart", result.headers["x-oss-object-type"])
            assertEquals(0, SystemFileSystem.list(checkpointDir).size)

            // Case 3, list Parts Fail
            mockHandler.failInPartNumber = 3
            exception = assertFails {
                uploader.upload(
                    PutObjectRequest {
                        this.bucket = bucket
                        this.key = key
                        body = ByteStream.fromFile(filePath)
                    }
                )
            }
            assertTrue(exception.cause is ServiceException)
            assertEquals(403, (exception.cause as ServiceException).statusCode)
            assertTrue(SystemFileSystem.exists(cpFilePath))
            bytes = SystemFileSystem.source(cpFilePath).buffered().use {
                it.readByteArray()
            }
            info = Json.decodeFromString<UploadCheckpoint.Info>(bytes.decodeToString())
            assertEquals(1 * 1024 * 1024, info.data.partSize)
            assertEquals("uploadId-1234", info.data.uploadInfo?.uploadId)
            assertEquals("oss://${XmlUtils.escapeText(name)}", info.data.objectInfo.objectKey)
            assertEquals(10 * 1024 * 1024, info.data.fileMeta.size)
            assertEquals(lastModified, info.data.fileMeta.lastModified)

            mockHandler.type = MockHttpClient.ResponseErrorType.ListPart
            mockHandler.failInPartNumber = null
            exception = assertFails {
                uploader.upload(
                    PutObjectRequest {
                        this.bucket = bucket
                        this.key = key
                        body = ByteStream.fromFile(filePath)
                    }
                )
            }
            assertTrue(exception.cause is ServiceException)
            assertEquals(403, (exception.cause as ServiceException).statusCode)
            assertTrue(SystemFileSystem.exists(cpFilePath))
            bytes = SystemFileSystem.source(cpFilePath).buffered().use {
                it.readByteArray()
            }
            info = Json.decodeFromString<UploadCheckpoint.Info>(bytes.decodeToString())
            assertEquals(1 * 1024 * 1024, info.data.partSize)
            assertEquals("uploadId-1234", info.data.uploadInfo?.uploadId)
            assertEquals("oss://${XmlUtils.escapeText(name)}", info.data.objectInfo.objectKey)
            assertEquals(10 * 1024 * 1024, info.data.fileMeta.size)
            assertEquals(lastModified, info.data.fileMeta.lastModified)

            mockHandler.type = null
            result = uploader.upload(
                PutObjectRequest {
                    this.bucket = bucket
                    this.key = key
                    body = ByteStream.fromFile(filePath)
                }
            )
            assertEquals(200, result.statusCode)
            assertEquals("Multipart", result.headers["x-oss-object-type"])
            assertEquals(0, SystemFileSystem.list(checkpointDir).size)
        }

        removeTestFile("file")
    }

    @Test
    fun testUploadCheckCRC64Fail() = runTest {
        val bucket = "bucket"
        val key = "key"

        val mockHandler = MockHttpClient(
            errorCRCValuePartNumber = 4
        )
        val config = ClientConfiguration().apply {
            region = "cn-hangzhou"
            credentialsProvider = StaticCredentialsProvider("ak", "sk")
            httpTransport = mockHandler
        }

        OSSClient.create(config).use { client ->

            val uploader = Uploader(client, {
                it.partSize = 1 * 1024 * 1024
            })
            val exception = assertFailsWith<OperationException> {
                uploader.upload(
                    PutObjectRequest {
                        this.bucket = bucket
                        this.key = key
                        body = Random.nextBytes(10 * 1024 * 1024).asByteStream()
                    }
                )
            }
            assertTrue(exception.cause is InconsistentException)
        }

        // disable check crc
        OSSClient.create(
            ClientConfiguration().apply {
                region = "cn-hangzhou"
                credentialsProvider = StaticCredentialsProvider("ak", "sk")
                httpTransport = mockHandler
                disableUploadCRC64Check = true
            }
        ).use { client ->
            val uploader = Uploader(client, {
                it.partSize = 1 * 1024 * 1024
            })
            val result = uploader.upload(
                PutObjectRequest {
                    this.bucket = bucket
                    this.key = key
                    body = Random.nextBytes(10 * 1024 * 1024).asByteStream()
                }
            )
            assertEquals(200, result.statusCode)
            assertEquals("Multipart", result.headers["x-oss-object-type"])
        }
    }

    @Test
    fun testUploadSinglePartWithProgress() = runTest {
        val bucket = "bucket"
        val key = "key"
        var totalBytesTransferred: Long = 0

        val mockHandler = MockHttpClient()
        val config = ClientConfiguration().apply {
            region = "cn-hangzhou"
            credentialsProvider = StaticCredentialsProvider("ak", "sk")
            httpTransport = mockHandler
        }

        OSSClient.create(config).use { client ->

            val uploader = Uploader(client)
            val result = uploader.upload(
                PutObjectRequest {
                    this.bucket = bucket
                    this.key = key
                    body = Random.nextBytes(1 * 1024 * 1024).asByteStream()
                    progressListener = ProgressListener { bytesSent, totalBytesSent, totalBytesExpectedToSend ->
                        totalBytesTransferred += bytesSent
                        assertEquals(totalBytesTransferred, totalBytesSent)
                        assertEquals(1 * 1024 * 1024, totalBytesExpectedToSend)
                    }
                }
            )
            assertEquals(200, result.statusCode)
            assertEquals("Normal", result.headers["x-oss-object-type"])
            assertEquals(1 * 1024 * 1024, totalBytesTransferred)
        }

        removeTestFile("file")
    }

    @Test
    fun testUploadEnableCheckpointUseCpProgress() = runTest {
        val filePath = createTestFile("file", 10 * 1024 * 1024)
        val bucket = "bucket"
        val key = "key"
        var totalBytesTransferred: Long = 0

        val mockHandler = MockHttpClient()
        val config = ClientConfiguration().apply {
            region = "cn-hangzhou"
            credentialsProvider = StaticCredentialsProvider("ak", "sk")
            httpTransport = mockHandler
        }

        OSSClient.create(config).use { client ->

            val uploader = Uploader(client, {
                it.partSize = 1 * 1024 * 1024
                it.enableCheckpoint = true
            })

            mockHandler.failInPartNumber = 4
            val exception = assertFails {
                uploader.upload(
                    PutObjectRequest {
                        this.bucket = bucket
                        this.key = key
                        body = ByteStream.fromFile(filePath)
                        progressListener = ProgressListener { bytesSent, totalBytesSent, totalBytesExpectedToSend ->
                            totalBytesTransferred += bytesSent
                            assertEquals(totalBytesTransferred, totalBytesSent)
                            assertEquals(10 * 1024 * 1024, totalBytesExpectedToSend)
                        }
                    }
                )
            }
            assertTrue { exception.cause is ServiceException }
            assertEquals(403, (exception.cause as ServiceException).statusCode)

            mockHandler.failInPartNumber = null
            val result = uploader.upload(
                PutObjectRequest {
                    this.bucket = bucket
                    this.key = key
                    body = ByteStream.fromFile(filePath)
                    progressListener = ProgressListener { bytesSent, totalBytesSent, totalBytesExpectedToSend ->
                        assertTrue(totalBytesSent >= 3 * 1024 * 1024)
                        totalBytesTransferred += bytesSent
                        assertEquals(totalBytesTransferred, totalBytesSent)
                        assertEquals(10 * 1024 * 1024, totalBytesExpectedToSend)
                    }
                }
            )
            assertEquals(200, result.statusCode)
            assertEquals("Multipart", result.headers["x-oss-object-type"])
            assertEquals(10 * 1024 * 1024, totalBytesTransferred)
        }

        removeTestFile("file")
    }

    fun createTestFile(fileName: String, content: ByteArray): Path {
        val fs = SystemFileSystem
        Path(tempPath).also {
            if (!fs.exists(it)) {
                fs.createDirectories(it)
            }
        }
        val path = Path("$tempPath/$fileName")
        if (fs.exists(path)) {
            fs.delete(path)
        }
        fs.sink(path).use { it0 ->
            it0.write(Buffer().also { it.write(content) }, content.size.toLong())
        }
        return path
    }

    fun createTestFile(fileName: String, size: Int): Path {
        val bytes = Random.nextBytes(size)
        return createTestFile(fileName, bytes)
    }

    fun removeTestFile(fileName: String) {
        val fs = SystemFileSystem
        val path = Path("$tempPath/$fileName")
        if (fs.exists(path)) {
            fs.delete(path)
        }
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun testAbortUpload() = runTest {
        val filePath = createTestFile("test-abort-file", 10 * 1024 * 1024)
        val checkpointDir = "${System.getProperty("user.home")}/OSS"
        val bucket = "bucket"
        val key = "key"
        val name = "$bucket/$key"
        val destHash = "oss://${XmlUtils.escapeText(name)}".toByteArray().md5().toHexString()
        val srcHash = filePath.toString().toByteArray().md5().toHexString()
        val cpFilePath = Path("$checkpointDir/$srcHash-$destHash$CHECK_POINT_FILE_SUFFIX_UPLOADER")
        val lastModified = Instant.fromEpochMilliseconds(File(filePath.toString()).lastModified()).format(
            Format {
                year()
                char('-')
                monthNumber()
                char('-')
                day()
                char('T')
                hour()
                char(':')
                minute()
                char(':')
                second()
                chars("Z")
            }
        )

        val mockHandler = MockHttpClient()
        val config = ClientConfiguration().apply {
            region = "cn-hangzhou"
            credentialsProvider = StaticCredentialsProvider("ak", "sk")
            httpTransport = mockHandler
        }

        OSSClient.create(config).use { client ->

            val uploader = Uploader(client, {
                it.partSize = 1 * 1024 * 1024
                it.enableCheckpoint = true
            })
            mockHandler.failInPartNumber = 4
            val exception = assertFails {
                uploader.upload(
                    PutObjectRequest {
                        this.bucket = bucket
                        this.key = key
                        body = ByteStream.fromFile(filePath)
                    }
                )
            }
            assertTrue(exception.cause is ServiceException)
            assertEquals(403, (exception.cause as ServiceException).statusCode)
            assertTrue(SystemFileSystem.exists(cpFilePath))
            val bytes = SystemFileSystem.source(cpFilePath).buffered().use {
                it.readByteArray()
            }
            val info = Json.decodeFromString<UploadCheckpoint.Info>(bytes.decodeToString())
            assertEquals(1 * 1024 * 1024, info.data.partSize)
            assertEquals("uploadId-1234", info.data.uploadInfo?.uploadId)
            assertEquals("oss://${XmlUtils.escapeText(name)}", info.data.objectInfo.objectKey)
            assertEquals(10 * 1024 * 1024, info.data.fileMeta.size)
            assertEquals(lastModified, info.data.fileMeta.lastModified)

            assertEquals(0, mockHandler.deleteUploadId.size)
            uploader.abortUpload(
                PutObjectRequest {
                    this.bucket = bucket
                    this.key = key
                    body = ByteStream.fromFile(filePath)
                }
            )
            assertFalse(SystemFileSystem.exists(cpFilePath))
            assertTrue(mockHandler.deleteUploadId.contains("uploadId-1234"))
        }

        removeTestFile("test-abort-file")
    }
}
