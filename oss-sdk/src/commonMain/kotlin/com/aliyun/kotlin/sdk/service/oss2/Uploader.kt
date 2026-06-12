package com.aliyun.kotlin.sdk.service.oss2

import com.aliyun.kotlin.sdk.service.oss2.Defaults.CHECK_POINT_FILE_SUFFIX_UPLOADER
import com.aliyun.kotlin.sdk.service.oss2.Defaults.CHECK_POINT_MAGIC
import com.aliyun.kotlin.sdk.service.oss2.Defaults.MAX_UPLOAD_PARTS
import com.aliyun.kotlin.sdk.service.oss2.Defaults.UPLOAD_PARALLEL
import com.aliyun.kotlin.sdk.service.oss2.Defaults.UPLOAD_PART_SIZE
import com.aliyun.kotlin.sdk.service.oss2.exceptions.InconsistentException
import com.aliyun.kotlin.sdk.service.oss2.exceptions.OperationException
import com.aliyun.kotlin.sdk.service.oss2.exceptions.RequestException
import com.aliyun.kotlin.sdk.service.oss2.exceptions.ResponseException
import com.aliyun.kotlin.sdk.service.oss2.hash.Crc64
import com.aliyun.kotlin.sdk.service.oss2.hash.combine
import com.aliyun.kotlin.sdk.service.oss2.hash.md5
import com.aliyun.kotlin.sdk.service.oss2.models.AbortMultipartUploadRequest
import com.aliyun.kotlin.sdk.service.oss2.models.CompleteMultipartUpload
import com.aliyun.kotlin.sdk.service.oss2.models.CompleteMultipartUploadRequest
import com.aliyun.kotlin.sdk.service.oss2.models.InitiateMultipartUploadRequest
import com.aliyun.kotlin.sdk.service.oss2.models.ListPartsRequest
import com.aliyun.kotlin.sdk.service.oss2.models.Part
import com.aliyun.kotlin.sdk.service.oss2.models.PutObjectRequest
import com.aliyun.kotlin.sdk.service.oss2.models.TransferredInfo
import com.aliyun.kotlin.sdk.service.oss2.models.UploadCheckpoint
import com.aliyun.kotlin.sdk.service.oss2.models.UploadInfo
import com.aliyun.kotlin.sdk.service.oss2.models.UploadPartCRC
import com.aliyun.kotlin.sdk.service.oss2.models.UploadPartRequest
import com.aliyun.kotlin.sdk.service.oss2.models.UploadResult
import com.aliyun.kotlin.sdk.service.oss2.models.UploaderOptions
import com.aliyun.kotlin.sdk.service.oss2.paginator.listPartsPaginator
import com.aliyun.kotlin.sdk.service.oss2.progress.ProgressObserver
import com.aliyun.kotlin.sdk.service.oss2.types.ByteStream
import com.aliyun.kotlin.sdk.service.oss2.types.FeatureFlagsType
import com.aliyun.kotlin.sdk.service.oss2.types.FileContent
import com.aliyun.kotlin.sdk.service.oss2.utils.XmlUtils
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents.Companion.Format
import kotlinx.datetime.format.char
import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray
import java.io.File
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

public class Uploader {
    private val client: OSSClient
    private val options: UploaderOptions

    public constructor(
        client: OSSClient,
        vararg actions: (UploaderOptions) -> Unit
    ) {
        this.client = client
        val options = UploaderOptions(
            UPLOAD_PART_SIZE,
            UPLOAD_PARALLEL,
            false
        )

        for (action in actions) {
            action(options)
        }
        this.options = options
    }

    public suspend fun upload(
        request: PutObjectRequest,
        vararg actions: (UploaderOptions) -> Unit
    ): UploadResult {
        requireNotNull(request.bucket) { "request.bucket is required" }
        requireNotNull(request.key) { "request.key is required" }
        val body = requireNotNull(request.body) { "request.body is required" }
        val totalSize =
            body.contentLength ?: throw RequestException("Cannot get the size of the body")

        val opt = this.options
        for (action in actions) {
            action(opt)
        }
        if (opt.partSize <= 0) {
            opt.partSize = UPLOAD_PART_SIZE
        }
        if (opt.parallelNum <= 0) {
            opt.parallelNum = UPLOAD_PARALLEL
        }

        val delegate = UploaderDelegate(
            client,
            opt,
            request
        )
        val partSize = delegate.applySource(totalSize)
        val checkpoint = when (body) {
            is FileContent -> {
                delegate.checkCheckpoint(
                    body.path,
                    opt.checkpointDir,
                    partSize
                )
            }

            else -> null
        }
        val uploadInfo = UploadInfo(
            totalSize,
            partSize,
            checkpoint
        )
        val result = delegate.upload(uploadInfo)
        checkpoint?.remove()
        return result
    }

    public suspend fun abortUpload(request: PutObjectRequest) {
        requireNotNull(request.bucket) { "request.bucket is required" }
        requireNotNull(request.key) { "request.key is required" }
        val body = requireNotNull(request.body) { "request.body is required" }
        val totalSize =
            body.contentLength ?: throw RequestException("Cannot get the size of the body")

        val delegate = UploaderDelegate(
            client,
            options,
            request
        )
        val partSize = delegate.applySource(totalSize)
        val checkpoint = when (body) {
            is FileContent -> {
                delegate.checkCheckpoint(
                    body.path,
                    options.checkpointDir,
                    partSize
                )
            }
            else -> null
        }
        client.abortMultipartUpload(
            AbortMultipartUploadRequest {
                bucket = request.bucket
                key = request.key
                uploadId = checkpoint?.info?.data?.uploadInfo?.uploadId
            }
        )
        checkpoint?.remove()
    }
}

internal class UploaderDelegate(
    private val client: OSSClient,
    private var options: UploaderOptions,
    private val request: PutObjectRequest
) {

    val tempFileDir = Path("${System.getProperty("user.home")}/OSS")

    fun applySource(totalSize: Long): Int {
        var partSize = options.partSize
        while (totalSize / partSize.toLong() > MAX_UPLOAD_PARTS) {
            partSize += options.partSize
        }

        return partSize
    }

    @OptIn(ExperimentalTime::class)
    fun checkCheckpoint(filePath: Path, baseDir: Path?, partSize: Int): UploadCheckpoint? {
        if (options.enableCheckpoint != true) {
            return null
        }

        val name = "${request.bucket}/${request.key}"
        val destHash = "oss://${XmlUtils.escapeText(name)}".toByteArray().md5().toHexString()
        val srcHash = filePath.toString().toByteArray().md5().toHexString()

        val cpFileDir = baseDir ?: tempFileDir
        if (!SystemFileSystem.exists(cpFileDir)) {
            SystemFileSystem.createDirectories(cpFileDir)
        }

        val cpFilePath = "$cpFileDir/$srcHash-$destHash$CHECK_POINT_FILE_SUFFIX_UPLOADER)"

        val metadata = SystemFileSystem.metadataOrNull(filePath)
        val size = metadata?.size ?: throw OperationException("Cannot get file size")
        val lastModified = File(filePath.toString()).lastModified()

        val checkpoint = UploadCheckpoint(
            cpFileDir,
            Path(cpFilePath),
            false,
            UploadCheckpoint.Info(
                CHECK_POINT_MAGIC,
                null,
                UploadCheckpoint.Info.Data(
                    partSize,
                    UploadCheckpoint.Info.Data.FileMeta(
                        size,
                        Instant.fromEpochMilliseconds(lastModified).format(
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
                    ),
                    UploadCheckpoint.Info.Data.ObjectInfo("oss://$name"),
                    null
                )
            )
        )
        checkpoint.load()

        options.leavePartsOnError = true
        return checkpoint
    }

    private suspend fun getUploadId(): String {
        val result = client.initiateMultipartUpload(
            InitiateMultipartUploadRequest {
                bucket = request.bucket
                key = request.key
            }
        )
        return result.uploadId ?: throw ResponseException("No uploadId was obtained")
    }

    internal suspend fun adjustSource(uploadId: String?): TransferredInfo? {
        val uploadId = uploadId ?: return null

        val transferredInfo = TransferredInfo()
        client.listPartsPaginator(
            ListPartsRequest {
                bucket = request.bucket
                key = request.key
                this.uploadId = uploadId
            }
        ).collect {
            it.parts?.forEach { part ->
                val size = part.size ?: throw ResponseException("Can't get part size.")
                transferredInfo.transferred(
                    UploadPartCRC(
                        Part {
                            eTag = part.eTag
                            partNumber = part.partNumber
                        },
                        part.hashCrc64ecma?.toULong()?.toLong(),
                        size
                    )
                )
            }
        }

        return transferredInfo
    }

    suspend fun upload(uploadInfo: UploadInfo): UploadResult {
        val totalSize = uploadInfo.totalSize
        return if (totalSize > 0 && totalSize <= uploadInfo.partSize) {
            singlePart()
        } else {
            multiPart(uploadInfo)
        }
    }

    private suspend fun singlePart(): UploadResult {
        val result = client.putObject(
            PutObjectRequest {
                bucket = request.bucket
                key = request.key
                body = request.body
                progressListener = request.progressListener
            }
        )
        return UploadResult {
            headers = result.headers
            status = result.status
            statusCode = result.statusCode
            uploadId = null
            eTag = result.eTag
            versionId = result.versionId
            hashCRC64 = result.hashCrc64ecma
        }
    }

    private suspend fun multiPart(uploadInfo: UploadInfo): UploadResult {
        val semaphore = Semaphore(permits = options.parallelNum)
        val enableCRC =
            (client as? DefaultOSSClient)?.clientImpl?.featureFlags?.contains(FeatureFlagsType.ENABLE_CRC64_CHECK_UPLOAD)
                ?: false

        val partCount = (uploadInfo.totalSize / uploadInfo.partSize).let {
            if (uploadInfo.totalSize % uploadInfo.partSize > 0) {
                it + 1
            } else {
                it
            }
        }
        var uploadId = uploadInfo.checkpoint?.info?.data?.uploadInfo?.uploadId
        val transferredInfo = if (uploadId == null) {
            uploadId = getUploadId()
            uploadInfo.checkpoint?.let {
                it.info.data.uploadInfo = UploadCheckpoint.Info.Data.UploadInfo(uploadId)
                it.dump()
            }
            TransferredInfo()
        } else {
            adjustSource(uploadId) ?: TransferredInfo()
        }

        val observer = request.progressListener?.let {
            ProgressObserver(
                it,
                uploadInfo.totalSize,
                transferredInfo.transferred
            )
        }

        try {
            val source = requireNotNull(request.body?.buffered()) { "request.body is required" }
            source.use { source ->
                coroutineScope {
                    val partsIterator =
                        transferredInfo.parts.sortedBy { it.uploadPart.partNumber }.iterator()
                    for (partNumber in 1..partCount) {
                        if (partsIterator.hasNext() &&
                            partsIterator.next().uploadPart.partNumber == partNumber
                        ) {
                            continue
                        }

                        semaphore.acquire()
                        launch {
                            val partBytes = source.readByteArrayAtMost(options.partSize.toLong())
                                ?: return@launch
                            val result = client.uploadPart(
                                UploadPartRequest {
                                    bucket = request.bucket
                                    key = request.key
                                    this.uploadId = uploadId
                                    this.partNumber = partNumber
                                    body = ByteStream.fromBytes(partBytes)
                                }
                            )
                            val part = Part {
                                eTag = result.eTag
                                this.partNumber = partNumber
                            }
                            val crcValue = if (enableCRC) {
                                result.headers["x-oss-hash-crc64ecma"]?.toULong()?.toLong()
                            } else {
                                null
                            }
                            transferredInfo.transferred(
                                UploadPartCRC(
                                    part,
                                    crcValue,
                                    partBytes.size.toLong()
                                )
                            )

                            observer?.data(partBytes, 0, partBytes.size)
                            semaphore.release()
                        }
                    }
                }
            }
            val sortedParts = transferredInfo.parts.sortedBy { it.uploadPart.partNumber }
            val parts = sortedParts.map { it.uploadPart }
            val completeResult = client.completeMultipartUpload(
                CompleteMultipartUploadRequest {
                    bucket = request.bucket
                    key = request.key
                    this.uploadId = uploadId
                    completeMultipartUpload = CompleteMultipartUpload {
                        this.parts = parts
                    }
                }
            )

            if (enableCRC) {
                val clientCRCValue = sortedParts.fold(Crc64(0)) { crc, part ->
                    crc.combine(Crc64(part.crcValue ?: 0), part.size)
                }

                completeResult.headers["x-oss-hash-crc64ecma"]?.let {
                    val serverCrc64 = it
                    val clientCrc64 = clientCRCValue.digestValue.toULong().toString()
                    if (clientCrc64 != serverCrc64) {
                        throw InconsistentException(
                            clientCrc64,
                            serverCrc64,
                            completeResult.headers
                        )
                    }
                }
            }

            return UploadResult {
                headers = completeResult.headers
                status = completeResult.status
                statusCode = completeResult.statusCode
                this.uploadId = uploadId
                eTag = completeResult.eTag
                versionId = completeResult.versionId
                hashCRC64 = completeResult.headers["x-oss-hash-crc64ecma"]?.toULong()?.toLong()
            }
        } catch (e: Exception) {
            if (!options.leavePartsOnError) {
                client.abortMultipartUpload(
                    AbortMultipartUploadRequest {
                        bucket = request.bucket
                        key = request.key
                        this.uploadId = uploadId
                    }
                )
            }
            throw e
        }
    }
}

internal fun ByteStream.buffered(): Source = when (val stream = this) {
    is ByteStream.Buffer -> Buffer().also { it.write(stream.bytes()) }
    is ByteStream.SourceStream -> stream.readFrom().buffered()
}

internal fun Source.readByteArrayAtMost(count: Long): ByteArray? {
    return Buffer().use { sink ->
        request(count)
        val rc = readAtMostTo(sink, count)
        if (rc == -1L) {
            null
        } else if (sink.size >= count) {
            sink.readByteArray(count.toInt())
        } else {
            sink.readByteArray()
        }
    }
}
