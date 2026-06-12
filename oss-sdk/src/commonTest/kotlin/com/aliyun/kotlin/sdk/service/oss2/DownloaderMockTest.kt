package com.aliyun.kotlin.sdk.service.oss2

import com.aliyun.kotlin.sdk.service.oss2.Defaults.DOWNLOAD_PART_SIZE
import com.aliyun.kotlin.sdk.service.oss2.UploaderMockTest.MockHttpClient
import com.aliyun.kotlin.sdk.service.oss2.credentials.StaticCredentialsProvider
import com.aliyun.kotlin.sdk.service.oss2.exceptions.InconsistentException
import com.aliyun.kotlin.sdk.service.oss2.exceptions.RequestException
import com.aliyun.kotlin.sdk.service.oss2.exceptions.ServiceException
import com.aliyun.kotlin.sdk.service.oss2.hash.Crc64
import com.aliyun.kotlin.sdk.service.oss2.hash.md5
import com.aliyun.kotlin.sdk.service.oss2.models.DownloadCheckpoint.Info
import com.aliyun.kotlin.sdk.service.oss2.models.GetObjectRequest
import com.aliyun.kotlin.sdk.service.oss2.models.HttpRange
import com.aliyun.kotlin.sdk.service.oss2.progress.ProgressListener
import com.aliyun.kotlin.sdk.service.oss2.transport.HttpTransport
import com.aliyun.kotlin.sdk.service.oss2.transport.RequestMessage
import com.aliyun.kotlin.sdk.service.oss2.transport.RequestOptions
import com.aliyun.kotlin.sdk.service.oss2.transport.ResponseMessage
import com.aliyun.kotlin.sdk.service.oss2.types.ByteStream
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.files.SystemTemporaryDirectory
import kotlinx.io.readByteArray
import kotlinx.serialization.json.Json
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private fun String.asByteStream(): ByteStream = ByteStream.fromString(this)
private fun ByteArray.asByteStream(): ByteStream = ByteStream.fromBytes(this)

class DownloaderMockTest {

    @BeforeTest
    fun cleanup() {
        val base = Path("$SystemTemporaryDirectory/kotlin-sdk-test/download")
        if (SystemFileSystem.exists(base)) {
            SystemFileSystem.list(base).forEach { name ->
                val path = Path("$base/$name")
                if (SystemFileSystem.exists(path)) {
                    SystemFileSystem.delete(path)
                }
            }
        }
    }

    internal class MockHttpClient @OptIn(ExperimentalTime::class) constructor(
        val data: ByteArray,
        var failInPartNumber: Long? = null,
        val partSize: Long? = null,
        val offset: Long = 0,
        var fileWillChange: Boolean = false,
        val lastModified: String = Clock.System.now().format(DateTimeComponents.Formats.RFC_1123),
        var crcValueError: Boolean = false,
        var halfBodyErr: Boolean = false
    ) : HttpTransport {

        var maxParallelCount = 0
        private var uploadingPartCount = 0
        private var uploadedPartCount = 0

        override suspend fun execute(
            request: RequestMessage,
            options: RequestOptions
        ): ResponseMessage {
            uploadingPartCount += 1
            maxParallelCount = max(maxParallelCount, uploadingPartCount)
            val errData = """
                <?xml version="1.0" encoding="UTF-8"?>
                <Error>
                <Code>InvalidAccessKeyId</Code>
                <Message>The OSS Access Key Id you provided does not exist in our records.</Message>
                <RequestId>65467C42E001B4333337****</RequestId>
                <SignatureProvided>ak</SignatureProvided>
                <EC>0002-00000040</EC>
                </Error>
            """.trimIndent().replace("\n", "")
            val crc64Value = if (crcValueError) {
                "0"
            } else {
                Crc64(data, data.size).digestValue.toULong().toString()
            }

            val response = when (request.method) {
                "HEAD" -> {
                    ResponseMessage(
                        statusCode = 200,
                        headers = mutableMapOf(
                            "Content-Length" to "${data.size}",
                            "Content-Type" to "text/plain",
                            "ETag" to "fba9dede5f27731c9771645a3986****",
                            "Last-Modified" to lastModified,
                            "x-oss-hash-crc64ecma" to crc64Value
                        )
                    )
                }
                "GET" -> {
                    val subData = request.headers["Range"]?.let {
                        val range = HttpRange(it)
                        failInPartNumber?.let { failInPartNumber ->
                            if (range.offset == failInPartNumber * (partSize ?: -1) + offset) {
                                return ResponseMessage(
                                    statusCode = 403,
                                    headers = mutableMapOf(
                                        "Content-Length" to "${errData.length}",
                                        "Content-Type" to "application/xml"
                                    ),
                                    body = errData.asByteStream()
                                )
                            }
                        }
                        val subData = data.copyOfRange(range.offset.toInt(), range.offset.toInt() + (range.count?.toInt() ?: 0))
                        if (halfBodyErr) {
                            halfBodyErr = false
                            subData.copyOfRange(0, subData.size / 2)
                        } else {
                            subData
                        }
                    } ?: data

                    val eTag = if (uploadedPartCount > 0 && fileWillChange) {
                        "2ba9dede5f27731c9771645a3986****"
                    } else {
                        "fba9dede5f27731c9771645a3986****"
                    }
                    ResponseMessage(
                        statusCode = 206,
                        headers = mutableMapOf(
                            "Content-Length" to "${subData.size}",
                            "Content-Type" to "text/plain",
                            "ETag" to eTag,
                            "Last-Modified" to lastModified,
                            "x-oss-hash-crc64ecma" to crc64Value
                        ),
                        body = subData.asByteStream()
                    )
                }

                else -> ResponseMessage()
            }
            uploadingPartCount--
            uploadedPartCount++

            return response
        }
    }

    @Test
    fun testDownloadSinglePart() = runTest {
        val data = Random.nextBytes(3 * 1024 * 1024 + 1234)
        val bucket = "bucket"
        val key = "key"
        val filePath = Path("$SystemTemporaryDirectory/kotlin-sdk-test/download/file")
        if (!SystemFileSystem.exists(filePath.parent!!)) {
            SystemFileSystem.createDirectories(filePath.parent!!)
        }

        val mockHandler = MockHttpClient(
            data
        )
        val config = ClientConfiguration().apply {
            region = "cn-hangzhou"
            credentialsProvider = StaticCredentialsProvider("ak", "sk")
            httpTransport = mockHandler
        }

        OSSClient.create(config).use { client ->
            val downloader = Downloader(client, {
                it.parallelNum = 1
                it.partSize = 1 * 1024 * 1024
            })

            val result = downloader.downloadFile(
                GetObjectRequest {
                    this.bucket = bucket
                    this.key = key
                },
                filePath
            )

            assertEquals(data.size.toLong(), result.written)
            SystemFileSystem.source(filePath).buffered().use { source ->
                val destinationMD5 = source.readByteArray().md5().toHexString()
                val sourceMD5 = data.md5().toHexString()
                assertEquals(sourceMD5, destinationMD5)
            }

            if (SystemFileSystem.exists(filePath)) {
                SystemFileSystem.delete(filePath)
            }
        }
    }

    @Test
    fun testDownloadLoopSinglePart() = runTest {
        val data = Random.nextBytes(1234)
        val bucket = "bucket"
        val key = "key"
        val filePath = Path("$SystemTemporaryDirectory/kotlin-sdk-test/download/file")
        if (!SystemFileSystem.exists(filePath.parent!!)) {
            SystemFileSystem.createDirectories(filePath.parent!!)
        }

        val mockHandler = MockHttpClient(
            data
        )
        val config = ClientConfiguration().apply {
            region = "cn-hangzhou"
            credentialsProvider = StaticCredentialsProvider("ak", "sk")
            httpTransport = mockHandler
        }

        OSSClient.create(config).use { client ->
            val downloader = Downloader(client)

            for (i in 1..20) {
                if (SystemFileSystem.exists(filePath)) {
                    SystemFileSystem.delete(filePath)
                }
                val result = downloader.downloadFile(
                    GetObjectRequest {
                        this.bucket = bucket
                        this.key = key
                    },
                    filePath,
                    {
                        it.parallelNum = 1
                        it.partSize = i
                    }
                )

                assertEquals(data.size.toLong(), result.written)
                SystemFileSystem.source(filePath).buffered().use { source ->
                    val destinationMD5 = source.readByteArray().md5().toHexString()
                    val sourceMD5 = data.md5().toHexString()
                    assertEquals(sourceMD5, destinationMD5)
                }
            }

            if (SystemFileSystem.exists(filePath)) {
                SystemFileSystem.delete(filePath)
            }
        }
    }

    @Test
    fun testDownloadLoopSinglePartWithRange() = runTest {
        val data = Random.nextBytes(63)
        val bucket = "bucket"
        val key = "key"
        val filePath = Path("$SystemTemporaryDirectory/kotlin-sdk-test/download/file")
        if (!SystemFileSystem.exists(filePath.parent!!)) {
            SystemFileSystem.createDirectories(filePath.parent!!)
        }

        val mockHandler = MockHttpClient(
            data
        )
        val config = ClientConfiguration().apply {
            region = "cn-hangzhou"
            credentialsProvider = StaticCredentialsProvider("ak", "sk")
            httpTransport = mockHandler
        }

        OSSClient.create(config).use { client ->
            val downloader = Downloader(client)

            for (rs in 0..<7) {
                for (rCount in 1..<data.size) {
                    for (i in 1..3) {
                        if (SystemFileSystem.exists(filePath)) {
                            SystemFileSystem.delete(filePath)
                        }
                        val result = downloader.downloadFile(
                            GetObjectRequest {
                                this.bucket = bucket
                                this.key = key
                                range = HttpRange(offset = rs.toLong(), count = rCount.toLong()).toString()
                            },
                            filePath,
                            {
                                it.parallelNum = 1
                                it.partSize = i
                            }
                        )

                        val expectLen = min(data.size - rs, rCount)
                        assertEquals(expectLen.toLong(), result.written)
                        SystemFileSystem.source(filePath).buffered().use { source ->
                            val destinationMD5 = source.readByteArray().md5().toHexString()
                            val sourceMD5 = data.copyOfRange(rs, min(rs + rCount, data.size)).md5().toHexString()
                            assertEquals(sourceMD5, destinationMD5)
                        }
                    }
                }
            }

            if (SystemFileSystem.exists(filePath)) {
                SystemFileSystem.delete(filePath)
            }
        }
    }

    @Test
    fun testDownloadParallel() = runTest {
        val data = Random.nextBytes(3 * 1024 * 1024 + 1234)
        val bucket = "bucket"
        val key = "key"
        val filePath = Path("$SystemTemporaryDirectory/kotlin-sdk-test/download/file")
        if (!SystemFileSystem.exists(filePath.parent!!)) {
            SystemFileSystem.createDirectories(filePath.parent!!)
        }

        val mockHandler = MockHttpClient(
            data
        )
        val config = ClientConfiguration().apply {
            region = "cn-hangzhou"
            credentialsProvider = StaticCredentialsProvider("ak", "sk")
            httpTransport = mockHandler
        }

        OSSClient.create(config).use { client ->
            val downloader = Downloader(
                client,
                {
                    it.parallelNum = 3
                    it.partSize = 1 * 1024 * 1024
                }
            )

            val result = downloader.downloadFile(
                GetObjectRequest {
                    this.bucket = bucket
                    this.key = key
                },
                filePath
            )

            assertEquals(data.size.toLong(), result.written)
            SystemFileSystem.source(filePath).buffered().use { source ->
                val destinationMD5 = source.readByteArray().md5().toHexString()
                val sourceMD5 = data.md5().toHexString()
                assertEquals(sourceMD5, destinationMD5)
            }

            if (SystemFileSystem.exists(filePath)) {
                SystemFileSystem.delete(filePath)
            }
        }
    }

    @Test
    fun testDownloadLoopParallel() = runTest {
        val data = Random.nextBytes(1234)
        val bucket = "bucket"
        val key = "key"
        val filePath = Path("$SystemTemporaryDirectory/kotlin-sdk-test/download/file")
        if (!SystemFileSystem.exists(filePath.parent!!)) {
            SystemFileSystem.createDirectories(filePath.parent!!)
        }

        val mockHandler = MockHttpClient(
            data
        )
        val config = ClientConfiguration().apply {
            region = "cn-hangzhou"
            credentialsProvider = StaticCredentialsProvider("ak", "sk")
            httpTransport = mockHandler
        }

        OSSClient.create(config).use { client ->
            val downloader = Downloader(client)

            for (i in 1..20) {
                if (SystemFileSystem.exists(filePath)) {
                    SystemFileSystem.delete(filePath)
                }
                val result = downloader.downloadFile(
                    GetObjectRequest {
                        this.bucket = bucket
                        this.key = key
                    },
                    filePath,
                    {
                        it.parallelNum = 3
                        it.partSize = i
                    }
                )

                assertEquals(data.size.toLong(), result.written)
                SystemFileSystem.source(filePath).buffered().use { source ->
                    val destinationMD5 = source.readByteArray().md5().toHexString()
                    val sourceMD5 = data.md5().toHexString()
                    assertEquals(sourceMD5, destinationMD5)
                }
            }

            if (SystemFileSystem.exists(filePath)) {
                SystemFileSystem.delete(filePath)
            }
        }
    }

    @Test
    fun testDownloadLoopParallelWithRange() = runTest {
        val data = Random.nextBytes(63)
        val bucket = "bucket"
        val key = "key"
        val filePath = Path("$SystemTemporaryDirectory/kotlin-sdk-test/download/file")
        if (!SystemFileSystem.exists(filePath.parent!!)) {
            SystemFileSystem.createDirectories(filePath.parent!!)
        }

        val mockHandler = MockHttpClient(
            data
        )
        val config = ClientConfiguration().apply {
            region = "cn-hangzhou"
            credentialsProvider = StaticCredentialsProvider("ak", "sk")
            httpTransport = mockHandler
        }

        OSSClient.create(config).use { client ->
            val downloader = Downloader(client)

            for (rs in 0..<7) {
                for (rCount in 1..<data.size) {
                    for (i in 1..3) {
                        if (SystemFileSystem.exists(filePath)) {
                            SystemFileSystem.delete(filePath)
                        }
                        val result = downloader.downloadFile(
                            GetObjectRequest {
                                this.bucket = bucket
                                this.key = key
                                range = HttpRange(offset = rs.toLong(), count = rCount.toLong()).toString()
                            },
                            filePath,
                            {
                                it.parallelNum = 3
                                it.partSize = i
                            }
                        )

                        val expectLen = min(data.size - rs, rCount)
                        assertEquals(expectLen.toLong(), result.written)
                        SystemFileSystem.source(filePath).buffered().use { source ->
                            val destinationMD5 = source.readByteArray().md5().toHexString()
                            val sourceMD5 = data.copyOfRange(rs, min(rs + rCount, data.size)).md5().toHexString()
                            assertEquals(sourceMD5, destinationMD5)
                        }
                    }
                }
            }

            if (SystemFileSystem.exists(filePath)) {
                SystemFileSystem.delete(filePath)
            }
        }
    }

    @Test
    fun testDownloadArgumentCheck() = runTest {
        val bucket = "bucket"
        val filePath = Path("$SystemTemporaryDirectory/kotlin-sdk-test/download/file")
        if (!SystemFileSystem.exists(filePath.parent!!)) {
            SystemFileSystem.createDirectories(filePath.parent!!)
        }

        val mockHandler = MockHttpClient()
        val config = ClientConfiguration().apply {
            region = "cn-hangzhou"
            credentialsProvider = StaticCredentialsProvider("ak", "sk")
            httpTransport = mockHandler
        }

        OSSClient.create(config).use { client ->

            val uploader = Downloader(
                client,
                {
                    it.partSize = 1 * 1024 * 1024
                    it.parallelNum = 4
                }
            )
            var exception: Throwable = assertFailsWith<IllegalArgumentException> {
                uploader.downloadFile(
                    GetObjectRequest {},
                    filePath
                )
            }
            assertEquals("request.bucket is required", exception.message)

            exception = assertFailsWith<IllegalArgumentException> {
                uploader.downloadFile(
                    GetObjectRequest {
                        this.bucket = bucket
                    },
                    filePath
                )
            }
            assertEquals("request.key is required", exception.message)
        }

        if (SystemFileSystem.exists(filePath)) {
            SystemFileSystem.delete(filePath)
        }
    }

    @Test
    fun testDownloadSinglePartWithoutTempFile() = runTest {
        val data = Random.nextBytes(3 * DOWNLOAD_PART_SIZE + 1234)
        val bucket = "bucket"
        val key = "key"
        val filePath = Path("$SystemTemporaryDirectory/kotlin-sdk-test/download/file")
        if (!SystemFileSystem.exists(filePath.parent!!)) {
            SystemFileSystem.createDirectories(filePath.parent!!)
        }

        val mockHandler = MockHttpClient(
            data
        )
        val config = ClientConfiguration().apply {
            region = "cn-hangzhou"
            credentialsProvider = StaticCredentialsProvider("ak", "sk")
            httpTransport = mockHandler
        }

        OSSClient.create(config).use { client ->
            val downloader = Downloader(
                client,
                {
                    it.parallelNum = 1
                    it.partSize = 1 * 1024 * 1024
                }
            )

            val result = downloader.downloadFile(
                GetObjectRequest {
                    this.bucket = bucket
                    this.key = key
                },
                filePath,
                {
                    it.parallelNum = 2
                    it.partSize = 1024 * 1024
                    it.useTempFile = false
                }
            )

            assertEquals(data.size.toLong(), result.written)
            SystemFileSystem.source(filePath).buffered().use { source ->
                val destinationMD5 = source.readByteArray().md5().toHexString()
                val sourceMD5 = data.md5().toHexString()
                assertEquals(sourceMD5, destinationMD5)
            }

            if (SystemFileSystem.exists(filePath)) {
                SystemFileSystem.delete(filePath)
            }
        }
    }

    @Test
    fun testDownloadSinglePartWithInvalidPartSizeAndParallelNum() = runTest {
        val data = Random.nextBytes(3 * DOWNLOAD_PART_SIZE + 1234)
        val bucket = "bucket"
        val key = "key"
        val filePath = Path("$SystemTemporaryDirectory/kotlin-sdk-test/download/file")
        if (!SystemFileSystem.exists(filePath.parent!!)) {
            SystemFileSystem.createDirectories(filePath.parent!!)
        }

        val mockHandler = MockHttpClient(
            data
        )
        val config = ClientConfiguration().apply {
            region = "cn-hangzhou"
            credentialsProvider = StaticCredentialsProvider("ak", "sk")
            httpTransport = mockHandler
        }

        OSSClient.create(config).use { client ->
            val downloader = Downloader(client, {
                it.parallelNum = 1
                it.partSize = 1 * 1024 * 1024
            })

            val result = downloader.downloadFile(
                GetObjectRequest {
                    this.bucket = bucket
                    this.key = key
                },
                filePath,
                {
                    it.parallelNum = 0
                    it.partSize = 0
                }
            )

            assertEquals(data.size.toLong(), result.written)
            SystemFileSystem.source(filePath).buffered().use { source ->
                val destinationMD5 = source.readByteArray().md5().toHexString()
                val sourceMD5 = data.md5().toHexString()
                assertEquals(sourceMD5, destinationMD5)
            }

            if (SystemFileSystem.exists(filePath)) {
                SystemFileSystem.delete(filePath)
            }
        }
    }

    @Test
    fun testDownloadWhenFileSizeLessPartSize() = runTest {
        val data = Random.nextBytes(1234)
        val bucket = "bucket"
        val key = "key"
        val filePath = Path("$SystemTemporaryDirectory/kotlin-sdk-test/download/file")
        if (!SystemFileSystem.exists(filePath.parent!!)) {
            SystemFileSystem.createDirectories(filePath.parent!!)
        }

        val mockHandler = MockHttpClient(
            data
        )
        val config = ClientConfiguration().apply {
            region = "cn-hangzhou"
            credentialsProvider = StaticCredentialsProvider("ak", "sk")
            httpTransport = mockHandler
        }

        OSSClient.create(config).use { client ->
            val downloader = Downloader(client, {
                it.parallelNum = 1
                it.partSize = 1 * 1024 * 1024
            })

            val result = downloader.downloadFile(
                GetObjectRequest {
                    this.bucket = bucket
                    this.key = key
                },
                filePath,
                {
                    it.parallelNum = 1
                    it.partSize = 1024 * 1024
                }
            )

            assertEquals(data.size.toLong(), result.written)
            SystemFileSystem.source(filePath).buffered().use { source ->
                val destinationMD5 = source.readByteArray().md5().toHexString()
                val sourceMD5 = data.md5().toHexString()
                assertEquals(sourceMD5, destinationMD5)
            }

            if (SystemFileSystem.exists(filePath)) {
                SystemFileSystem.delete(filePath)
            }
        }
    }

    @Test
    fun testDownloadFileWillChange() = runTest {
        val data = Random.nextBytes(1234)
        val bucket = "bucket"
        val key = "key"
        val filePath = Path("$SystemTemporaryDirectory/kotlin-sdk-test/download/file")
        if (!SystemFileSystem.exists(filePath.parent!!)) {
            SystemFileSystem.createDirectories(filePath.parent!!)
        }

        val mockHandler = MockHttpClient(
            data,
            fileWillChange = true
        )
        val config = ClientConfiguration().apply {
            region = "cn-hangzhou"
            credentialsProvider = StaticCredentialsProvider("ak", "sk")
            httpTransport = mockHandler
        }

        OSSClient.create(config).use { client ->
            val downloader = Downloader(client, {
                it.parallelNum = 1
                it.partSize = 1 * 1024 * 1024
            })

            val exception = assertFailsWith<RequestException> {
                downloader.downloadFile(
                    GetObjectRequest {
                        this.bucket = bucket
                        this.key = key
                    },
                    filePath,
                    {
                        it.parallelNum = 1
                        it.partSize = 1024 * 1024
                    }
                )
            }
            assertEquals("Source file is changed", exception.message)

            if (SystemFileSystem.exists(filePath)) {
                SystemFileSystem.delete(filePath)
            }
        }
    }

    @Test
    fun testDownloadEnableCheckpointNormal() = runTest {
        val data = Random.nextBytes(1234)
        val bucket = "bucket"
        val key = "key"
        val filePath = Path("$SystemTemporaryDirectory/kotlin-sdk-test/download/check-point-to-check-no-surfix")
        if (!SystemFileSystem.exists(filePath.parent!!)) {
            SystemFileSystem.createDirectories(filePath.parent!!)
        }

        val mockHandler = MockHttpClient(
            data,
        )
        val config = ClientConfiguration().apply {
            region = "cn-hangzhou"
            credentialsProvider = StaticCredentialsProvider("ak", "sk")
            httpTransport = mockHandler
        }

        OSSClient.create(config).use { client ->
            val downloader = Downloader(client, {
                it.parallelNum = 3
                it.partSize = 128
                it.enableCheckpoint = true
            })

            val result = downloader.downloadFile(
                GetObjectRequest {
                    this.bucket = bucket
                    this.key = key
                },
                filePath,
                {
                    it.partSize = 128
                    it.parallelNum = 3
                    it.enableCheckpoint = true
                    it.verifyData = true
                }
            )

            assertEquals(data.size.toLong(), result.written)
            SystemFileSystem.source(filePath).buffered().use { source ->
                val destinationMD5 = source.readByteArray().md5().toHexString()
                val sourceMD5 = data.md5().toHexString()
                assertEquals(sourceMD5, destinationMD5)
            }

            if (SystemFileSystem.exists(filePath)) {
                SystemFileSystem.delete(filePath)
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun testDownloadEnableCheckpointNormal2() = runTest {
        val data = Random.nextBytes(1234)
        val bucket = "bucket"
        val key = "key"
        val lastModified = Clock.System.now().format(DateTimeComponents.Formats.RFC_1123)
        val localFilePath = Path("$SystemTemporaryDirectory/kotlin-sdk-test/download/check-point-to-check-no-suffix")
        val localFilePathTemp = Path("$SystemTemporaryDirectory/kotlin-sdk-test/download/check-point-to-check-no-suffix.temp")
        val cpFileName = "ddaf063c8f69766ecc8e4a93b6402e3e-${localFilePathTemp.toString().toByteArray().md5().toHexString()}.dcp"
        val cpFilePath = Path("${System.getProperty("user.home")}/OSS/$cpFileName")
        if (!SystemFileSystem.exists(localFilePath.parent!!)) {
            SystemFileSystem.createDirectories(localFilePath.parent!!)
        }

        val mockHandler = MockHttpClient(
            data,
            failInPartNumber = 6,
            partSize = 128,
            lastModified = lastModified
        )
        val config = ClientConfiguration().apply {
            region = "cn-hangzhou"
            credentialsProvider = StaticCredentialsProvider("ak", "sk")
            httpTransport = mockHandler
        }

        OSSClient.create(config).use { client ->
            val downloader = Downloader(client, {
                it.parallelNum = 3
                it.partSize = 128
                it.enableCheckpoint = true
            })

            val exception = assertFails {
                downloader.downloadFile(
                    GetObjectRequest {
                        this.bucket = bucket
                        this.key = key
                    },
                    localFilePath
                )
            }
            assertTrue(exception.cause is ServiceException)
            assertEquals(403, (exception.cause as ServiceException).statusCode)

            val bytes = SystemFileSystem.source(cpFilePath).buffered().use {
                it.readByteArray()
            }
            val info = Json.decodeFromString<Info>(bytes.decodeToString())
            assertEquals("fba9dede5f27731c9771645a3986****", info.data.objectMeta?.eTag)
            assertEquals(lastModified, info.data.objectMeta?.lastModified)
            assertEquals(1234, info.data.objectMeta?.size)
            assertEquals("oss://bucket/key", info.data.objectInfo.name)
            assertNull(info.data.objectInfo.versionId)
            assertNull(info.data.objectInfo.range)
            assertEquals(localFilePathTemp.toString(), info.data.filePath)
            assertEquals(128, info.data.partSize)
            assertEquals(6 * 128, info.data.downloadInfo?.offset)
            assertEquals(Crc64(data.copyOfRange(0, 6 * 128), 6 * 128).digestValue, info.data.downloadInfo?.crc)

            // resume from checkpoint
            mockHandler.failInPartNumber = null
            val result = downloader.downloadFile(
                GetObjectRequest {
                    this.bucket = bucket
                    this.key = key
                },
                localFilePath,
                {
                    it.partSize = 128
                    it.parallelNum = 3
                    it.enableCheckpoint = true
                    it.verifyData = true
                }
            )

            assertEquals(data.size.toLong(), result.written)
            SystemFileSystem.source(localFilePath).buffered().use { source ->
                val destinationMD5 = source.readByteArray().md5().toHexString()
                val sourceMD5 = data.md5().toHexString()
                assertEquals(sourceMD5, destinationMD5)
            }

            if (SystemFileSystem.exists(localFilePath)) {
                SystemFileSystem.delete(localFilePath)
            }
            if (SystemFileSystem.exists(cpFilePath)) {
                SystemFileSystem.delete(cpFilePath)
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun testDownloadEnableCheckpointNormalWithRange() = runTest {
        val data = Random.nextBytes(1234)
        val bucket = "bucket"
        val key = "key"
        val rs = 5
        val rCount = 832
        val lastModified = Clock.System.now().format(DateTimeComponents.Formats.RFC_1123)
        val localFilePath = Path("$SystemTemporaryDirectory/kotlin-sdk-test/download/check-point-to-check-no-suffix")
        val localFilePathTemp = Path("$SystemTemporaryDirectory/kotlin-sdk-test/download/check-point-to-check-no-suffix.temp")
        val cpFileName = "0fbbf3bb7c80debbecb37dca52a646eb-${localFilePathTemp.toString().toByteArray().md5().toHexString()}.dcp"
        val cpFilePath = Path("${System.getProperty("user.home")}/OSS/$cpFileName")
        if (!SystemFileSystem.exists(localFilePath.parent!!)) {
            SystemFileSystem.createDirectories(localFilePath.parent!!)
        }

        val mockHandler = MockHttpClient(
            data,
            failInPartNumber = 6,
            partSize = 128,
            offset = rs.toLong(),
            lastModified = lastModified
        )
        val config = ClientConfiguration().apply {
            region = "cn-hangzhou"
            credentialsProvider = StaticCredentialsProvider("ak", "sk")
            httpTransport = mockHandler
        }

        OSSClient.create(config).use { client ->
            val downloader = Downloader(
                client,
                {
                    it.parallelNum = 3
                    it.partSize = 128
                    it.enableCheckpoint = true
                }
            )

            val exception = assertFails {
                downloader.downloadFile(
                    GetObjectRequest {
                        this.bucket = bucket
                        this.key = key
                        range = HttpRange(rs.toLong(), rCount.toLong()).toString()
                    },
                    localFilePath
                )
            }
            assertTrue(exception.cause is ServiceException)
            assertEquals(403, (exception.cause as ServiceException).statusCode)

            val bytes = SystemFileSystem.source(cpFilePath).buffered().use {
                it.readByteArray()
            }
            val info = Json.decodeFromString<Info>(bytes.decodeToString())
            assertEquals("fba9dede5f27731c9771645a3986****", info.data.objectMeta?.eTag)
            assertEquals(lastModified, info.data.objectMeta?.lastModified)
            assertEquals(1234, info.data.objectMeta?.size)
            assertEquals("oss://bucket/key", info.data.objectInfo.name)
            assertNull(info.data.objectInfo.versionId)
            assertEquals("bytes=5-836", info.data.objectInfo.range)
            assertEquals(localFilePathTemp.toString(), info.data.filePath)
            assertEquals(128, info.data.partSize)
            assertEquals((128 * 6 + rs).toLong(), info.data.downloadInfo?.offset)
            assertEquals(0, info.data.downloadInfo?.crc)

            // resume from checkpoint
            mockHandler.failInPartNumber = null
            val result = downloader.downloadFile(
                GetObjectRequest {
                    this.bucket = bucket
                    this.key = key
                    range = HttpRange(rs.toLong(), rCount.toLong()).toString()
                },
                localFilePath,
                {
                    it.partSize = 128
                    it.parallelNum = 3
                    it.enableCheckpoint = true
                    it.verifyData = true
                }
            )

            assertEquals(rCount.toLong(), result.written)
            SystemFileSystem.source(localFilePath).buffered().use { source ->
                val destinationMD5 = source.readByteArray().md5().toHexString()
                val sourceMD5 = data.copyOfRange(5, 836 + 1).md5().toHexString()
                assertEquals(sourceMD5, destinationMD5)
            }

            if (SystemFileSystem.exists(localFilePath)) {
                SystemFileSystem.delete(localFilePath)
            }
        }
    }

    @Test
    fun testDownloadWithError() = runTest {
        val data = Random.nextBytes(3 * 1024 * 1024 + 1234)
        val bucket = "bucket"
        val key = "key"
        val filePath = Path("$SystemTemporaryDirectory/kotlin-sdk-test/download/file")
        if (!SystemFileSystem.exists(filePath.parent!!)) {
            SystemFileSystem.createDirectories(filePath.parent!!)
        }

        val mockHandler = MockHttpClient(
            data
        )
        val config = ClientConfiguration().apply {
            region = "cn-hangzhou"
            credentialsProvider = StaticCredentialsProvider("ak", "sk")
            httpTransport = mockHandler
        }

        OSSClient.create(config).use { client ->
            val downloader = Downloader(
                client,
                {
                    it.parallelNum = 1
                    it.partSize = 1 * 1024 * 1024
                }
            )

            // filePath is invalid
            var exception = assertFailsWith<IllegalArgumentException> {
                downloader.downloadFile(
                    GetObjectRequest {
                        this.bucket = bucket
                        this.key = key
                    },
                    Path("")
                )
            }
            assertEquals("filePath is invalid", exception.message)

            // fRange is invalid
            exception = assertFailsWith<IllegalArgumentException> {
                downloader.downloadFile(
                    GetObjectRequest {
                        this.bucket = bucket
                        this.key = key
                        range = "invalid range"
                    },
                    filePath
                )
            }
            assertEquals("For input string: \"invalid range\"", exception.message)

            if (SystemFileSystem.exists(filePath)) {
                SystemFileSystem.delete(filePath)
            }
        }
    }

    @Test
    fun testDownloadCheckCRC() = runTest {
        val data = Random.nextBytes(5 * 100 * 1024 + 1234)
        val bucket = "bucket"
        val key = "key"
        val filePath = Path("$SystemTemporaryDirectory/kotlin-sdk-test/download/test-check-crc-file")
        if (!SystemFileSystem.exists(filePath.parent!!)) {
            SystemFileSystem.createDirectories(filePath.parent!!)
        }

        val mockHandler = MockHttpClient(
            data,
            crcValueError = true
        )
        OSSClient.create(
            ClientConfiguration().apply {
                region = "cn-hangzhou"
                credentialsProvider = StaticCredentialsProvider("ak", "sk")
                httpTransport = mockHandler
            }
        ).use { client ->
            val downloader = Downloader(client, {
                it.parallelNum = 3
                it.partSize = 100 * 1024
            })

            val exception = assertFailsWith<InconsistentException> {
                downloader.downloadFile(
                    GetObjectRequest {
                        this.bucket = bucket
                        this.key = key
                    },
                    filePath
                )
            }
            assertTrue(exception.message!!.contains("crc is inconsistent"))
        }

        OSSClient.create(
            ClientConfiguration().apply {
                region = "cn-hangzhou"
                credentialsProvider = StaticCredentialsProvider("ak", "sk")
                httpTransport = mockHandler
                disableDownloadCRC64Check = true
            }
        ).use { client ->
            val downloader = Downloader(client, {
                it.parallelNum = 3
                it.partSize = 100 * 1024
            })

            val result = downloader.downloadFile(
                GetObjectRequest {
                    this.bucket = bucket
                    this.key = key
                },
                filePath
            )

            assertEquals(data.size.toLong(), result.written)
            SystemFileSystem.source(filePath).buffered().use { source ->
                val destinationMD5 = source.readByteArray().md5().toHexString()
                val sourceMD5 = data.md5().toHexString()
                assertEquals(sourceMD5, destinationMD5)
            }
        }

        if (SystemFileSystem.exists(filePath)) {
            SystemFileSystem.delete(filePath)
        }
    }

    @Test
    fun testDownloadCheckCRCWithResume() = runTest {
        val data = Random.nextBytes(5 * 100 * 1024 + 1234)
        val bucket = "bucket"
        val key = "key"
        val filePath = Path("$SystemTemporaryDirectory/kotlin-sdk-test/download/file")
        if (!SystemFileSystem.exists(filePath.parent!!)) {
            SystemFileSystem.createDirectories(filePath.parent!!)
        }

        val mockHandler = MockHttpClient(
            data,
            halfBodyErr = true
        )
        OSSClient.create(
            ClientConfiguration().apply {
                region = "cn-hangzhou"
                credentialsProvider = StaticCredentialsProvider("ak", "sk")
                httpTransport = mockHandler
            }
        ).use { client ->
            val downloader = Downloader(client, {
                it.parallelNum = 3
                it.partSize = 100 * 1024
            })

            val result = downloader.downloadFile(
                GetObjectRequest {
                    this.bucket = bucket
                    this.key = key
                },
                filePath
            )

            assertEquals(data.size.toLong(), result.written)
            SystemFileSystem.source(filePath).buffered().use { source ->
                val destinationMD5 = source.readByteArray().md5().toHexString()
                val sourceMD5 = data.md5().toHexString()
                assertEquals(sourceMD5, destinationMD5)
            }
        }

        if (SystemFileSystem.exists(filePath)) {
            SystemFileSystem.delete(filePath)
        }
    }

    @Test
    fun testDownloadProgress() = runTest {
        val data = Random.nextBytes(5 * 1024 * 1024 + 123)
        val bucket = "bucket"
        val key = "key"
        val filePath = Path("$SystemTemporaryDirectory/kotlin-sdk-test/download/file")
        if (!SystemFileSystem.exists(filePath.parent!!)) {
            SystemFileSystem.createDirectories(filePath.parent!!)
        }
        var totalBytesTransferred: Long = 0

        val mockHandler = MockHttpClient(
            data,
        )
        OSSClient.create(
            ClientConfiguration().apply {
                region = "cn-hangzhou"
                credentialsProvider = StaticCredentialsProvider("ak", "sk")
                httpTransport = mockHandler
            }
        ).use { client ->
            var downloader = Downloader(client, {
                it.parallelNum = 1
                it.partSize = 1024 * 1024
            })
            downloader.downloadFile(
                GetObjectRequest {
                    this.bucket = bucket
                    this.key = key
                    progressListener = ProgressListener { bytesReceive, totalBytesReceive, totalBytesExpectedToReceive ->
                        totalBytesTransferred += bytesReceive
                        assertEquals(totalBytesTransferred, totalBytesReceive)
                        assertTrue(((1024 * 1024).toLong() == bytesReceive) || (123.toLong() == bytesReceive))
                        assertEquals(5 * 1024 * 1024 + 123, totalBytesExpectedToReceive)
                    }
                },
                filePath
            )
            assertEquals(5 * 1024 * 1024 + 123, totalBytesTransferred)

            totalBytesTransferred = 0
            downloader = Downloader(client, {
                it.parallelNum = 3
                it.partSize = 1024 * 1024
            })
            downloader.downloadFile(
                GetObjectRequest {
                    this.bucket = bucket
                    this.key = key
                    progressListener = ProgressListener { bytesReceive, totalBytesReceive, totalBytesExpectedToReceive ->
                        totalBytesTransferred += bytesReceive
                        assertEquals(totalBytesTransferred, totalBytesReceive)
                        assertTrue(((1024 * 1024).toLong() == bytesReceive) || (123.toLong() == bytesReceive))
                        assertEquals(5 * 1024 * 1024 + 123, totalBytesExpectedToReceive)
                    }
                },
                filePath
            )
            assertEquals(5 * 1024 * 1024 + 123, totalBytesTransferred)
        }
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun testDownloadEnableCheckpointProgress() = runTest {
        val data = Random.nextBytes(1234)
        val bucket = "bucket"
        val key = "key"
        val lastModified = Clock.System.now().format(DateTimeComponents.Formats.RFC_1123)
        val localFilePath = Path("$SystemTemporaryDirectory/kotlin-sdk-test/download/check-point-to-check-no-suffix")
        val localFilePathTemp = Path("$SystemTemporaryDirectory/kotlin-sdk-test/download/check-point-to-check-no-suffix.temp")
        val cpFileName = "ddaf063c8f69766ecc8e4a93b6402e3e-${localFilePathTemp.toString().toByteArray().md5().toHexString()}.dcp"
        val cpFilePath = Path("${System.getProperty("user.home")}/OSS/$cpFileName")
        if (!SystemFileSystem.exists(localFilePath.parent!!)) {
            SystemFileSystem.createDirectories(localFilePath.parent!!)
        }
        var totalBytesTransferred: Long = 0

        val mockHandler = MockHttpClient(
            data,
            failInPartNumber = 6,
            partSize = 128,
            lastModified = lastModified
        )
        val config = ClientConfiguration().apply {
            region = "cn-hangzhou"
            credentialsProvider = StaticCredentialsProvider("ak", "sk")
            httpTransport = mockHandler
        }

        OSSClient.create(config).use { client ->
            var downloader = Downloader(client, {
                it.parallelNum = 1
                it.partSize = 128
                it.enableCheckpoint = true
            })

            var exception = assertFails {
                downloader.downloadFile(
                    GetObjectRequest {
                        this.bucket = bucket
                        this.key = key
                        progressListener = ProgressListener { bytesReceive, totalBytesReceive, totalBytesExpectedToReceive ->
                            totalBytesTransferred += bytesReceive
                            assertEquals(totalBytesTransferred, totalBytesReceive)
                            assertTrue(((128).toLong() == bytesReceive) || (82.toLong() == bytesReceive))
                            assertEquals(1234, totalBytesExpectedToReceive)
                        }
                    },
                    localFilePath
                )
            }
            assertTrue(exception.cause is ServiceException)
            assertEquals(403, (exception.cause as ServiceException).statusCode)

            var bytes = SystemFileSystem.source(cpFilePath).buffered().use {
                it.readByteArray()
            }
            var info = Json.decodeFromString<Info>(bytes.decodeToString())
            assertEquals("fba9dede5f27731c9771645a3986****", info.data.objectMeta?.eTag)
            assertEquals(lastModified, info.data.objectMeta?.lastModified)
            assertEquals(1234, info.data.objectMeta?.size)
            assertEquals("oss://bucket/key", info.data.objectInfo.name)
            assertNull(info.data.objectInfo.versionId)
            assertNull(info.data.objectInfo.range)
            assertEquals(localFilePathTemp.toString(), info.data.filePath)
            assertEquals(128, info.data.partSize)
            assertEquals(6 * 128, info.data.downloadInfo?.offset)
            assertEquals(Crc64(data.copyOfRange(0, 6 * 128), 6 * 128).digestValue, info.data.downloadInfo?.crc)

            // resume from checkpoint
            mockHandler.failInPartNumber = null
            var result = downloader.downloadFile(
                GetObjectRequest {
                    this.bucket = bucket
                    this.key = key
                    progressListener = ProgressListener { bytesReceive, totalBytesReceive, totalBytesExpectedToReceive ->
                        totalBytesTransferred += bytesReceive
                        assertEquals(totalBytesTransferred, totalBytesReceive)
                        assertTrue(((128).toLong() == bytesReceive) || (82.toLong() == bytesReceive))
                        assertEquals(1234, totalBytesExpectedToReceive)
                    }
                },
                localFilePath,
                {
                    it.partSize = 128
                    it.parallelNum = 3
                    it.enableCheckpoint = true
                    it.verifyData = true
                }
            )

            assertEquals(1234, totalBytesTransferred)
            assertEquals(data.size.toLong(), result.written)
            SystemFileSystem.source(localFilePath).buffered().use { source ->
                val destinationMD5 = source.readByteArray().md5().toHexString()
                val sourceMD5 = data.md5().toHexString()
                assertEquals(sourceMD5, destinationMD5)
            }

            if (SystemFileSystem.exists(localFilePath)) {
                SystemFileSystem.delete(localFilePath)
            }
            if (SystemFileSystem.exists(cpFilePath)) {
                SystemFileSystem.delete(cpFilePath)
            }

            // parallel
            totalBytesTransferred = 0
            mockHandler.failInPartNumber = 6
            downloader = Downloader(client, {
                it.parallelNum = 3
                it.partSize = 128
                it.enableCheckpoint = true
            })

            exception = assertFails {
                downloader.downloadFile(
                    GetObjectRequest {
                        this.bucket = bucket
                        this.key = key
                        progressListener = ProgressListener { bytesReceive, totalBytesReceive, totalBytesExpectedToReceive ->
                            totalBytesTransferred += bytesReceive
                            assertEquals(totalBytesTransferred, totalBytesReceive)
                            assertTrue(((128).toLong() == bytesReceive) || (82.toLong() == bytesReceive))
                            assertEquals(1234, totalBytesExpectedToReceive)
                        }
                    },
                    localFilePath
                )
            }
            assertTrue(exception.cause is ServiceException)
            assertEquals(403, (exception.cause as ServiceException).statusCode)

            bytes = SystemFileSystem.source(cpFilePath).buffered().use {
                it.readByteArray()
            }
            info = Json.decodeFromString<Info>(bytes.decodeToString())
            assertEquals("fba9dede5f27731c9771645a3986****", info.data.objectMeta?.eTag)
            assertEquals(lastModified, info.data.objectMeta?.lastModified)
            assertEquals(1234, info.data.objectMeta?.size)
            assertEquals("oss://bucket/key", info.data.objectInfo.name)
            assertNull(info.data.objectInfo.versionId)
            assertNull(info.data.objectInfo.range)
            assertEquals(localFilePathTemp.toString(), info.data.filePath)
            assertEquals(128, info.data.partSize)
            assertEquals(6 * 128, info.data.downloadInfo?.offset)
            assertEquals(Crc64(data.copyOfRange(0, 6 * 128), 6 * 128).digestValue, info.data.downloadInfo?.crc)

            // resume from checkpoint
            mockHandler.failInPartNumber = null
            result = downloader.downloadFile(
                GetObjectRequest {
                    this.bucket = bucket
                    this.key = key
                    progressListener = ProgressListener { bytesReceive, totalBytesReceive, totalBytesExpectedToReceive ->
                        totalBytesTransferred += bytesReceive
                        assertEquals(totalBytesTransferred, totalBytesReceive)
                        assertTrue(((128).toLong() == bytesReceive) || (82.toLong() == bytesReceive))
                        assertEquals(1234, totalBytesExpectedToReceive)
                    }
                },
                localFilePath,
                {
                    it.partSize = 128
                    it.parallelNum = 3
                    it.enableCheckpoint = true
                    it.verifyData = true
                }
            )

            assertEquals(1234, totalBytesTransferred)
            assertEquals(data.size.toLong(), result.written)
            SystemFileSystem.source(localFilePath).buffered().use { source ->
                val destinationMD5 = source.readByteArray().md5().toHexString()
                val sourceMD5 = data.md5().toHexString()
                assertEquals(sourceMD5, destinationMD5)
            }

            if (SystemFileSystem.exists(localFilePath)) {
                SystemFileSystem.delete(localFilePath)
            }
            if (SystemFileSystem.exists(cpFilePath)) {
                SystemFileSystem.delete(cpFilePath)
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun testAbortDownload() = runTest {
        val data = Random.nextBytes(1234)
        val bucket = "bucket"
        val key = "key"
        val lastModified = Clock.System.now().format(DateTimeComponents.Formats.RFC_1123)
        val localFilePath = Path("$SystemTemporaryDirectory/kotlin-sdk-test/download/check-point-to-check-no-suffix")
        val localFilePathTemp = Path("$SystemTemporaryDirectory/kotlin-sdk-test/download/check-point-to-check-no-suffix.temp")
        val cpFileName = "ddaf063c8f69766ecc8e4a93b6402e3e-${localFilePathTemp.toString().toByteArray().md5().toHexString()}.dcp"
        val cpFilePath = Path("${System.getProperty("user.home")}/OSS/$cpFileName")
        if (!SystemFileSystem.exists(localFilePath.parent!!)) {
            SystemFileSystem.createDirectories(localFilePath.parent!!)
        }

        val mockHandler = MockHttpClient(
            data,
            failInPartNumber = 6,
            partSize = 128,
            lastModified = lastModified
        )
        val config = ClientConfiguration().apply {
            region = "cn-hangzhou"
            credentialsProvider = StaticCredentialsProvider("ak", "sk")
            httpTransport = mockHandler
        }

        OSSClient.create(config).use { client ->
            val downloader = Downloader(client, {
                it.parallelNum = 3
                it.partSize = 128
                it.enableCheckpoint = true
            })

            val exception = assertFails {
                downloader.downloadFile(
                    GetObjectRequest {
                        this.bucket = bucket
                        this.key = key
                    },
                    localFilePath
                )
            }
            assertTrue(exception.cause is ServiceException)
            assertEquals(403, (exception.cause as ServiceException).statusCode)
            assertTrue(SystemFileSystem.exists(localFilePathTemp))
            assertTrue(SystemFileSystem.exists(cpFilePath))

            downloader.abortDownload(
                GetObjectRequest {
                    this.bucket = bucket
                    this.key = key
                },
                localFilePath
            )
            assertFalse(SystemFileSystem.exists(localFilePathTemp))
            assertFalse(SystemFileSystem.exists(cpFilePath))

            if (SystemFileSystem.exists(localFilePath)) {
                SystemFileSystem.delete(localFilePath)
            }
        }
    }
}
