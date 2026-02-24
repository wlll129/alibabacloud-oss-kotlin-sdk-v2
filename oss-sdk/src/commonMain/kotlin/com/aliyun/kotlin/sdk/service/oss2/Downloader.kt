package com.aliyun.kotlin.sdk.service.oss2

import com.aliyun.kotlin.sdk.service.oss2.Defaults.CHECK_POINT_FILE_SUFFIX_DOWNLOADER
import com.aliyun.kotlin.sdk.service.oss2.Defaults.CHECK_POINT_MAGIC
import com.aliyun.kotlin.sdk.service.oss2.Defaults.DOWNLOAD_PARALLEL
import com.aliyun.kotlin.sdk.service.oss2.Defaults.DOWNLOAD_PART_SIZE
import com.aliyun.kotlin.sdk.service.oss2.Defaults.TEMP_FILE_SUFFIX
import com.aliyun.kotlin.sdk.service.oss2.Defaults.UPLOAD_PARALLEL
import com.aliyun.kotlin.sdk.service.oss2.Defaults.UPLOAD_PART_SIZE
import com.aliyun.kotlin.sdk.service.oss2.exceptions.InconsistentException
import com.aliyun.kotlin.sdk.service.oss2.exceptions.RequestException
import com.aliyun.kotlin.sdk.service.oss2.exceptions.ResponseException
import com.aliyun.kotlin.sdk.service.oss2.hash.CRC64Observer
import com.aliyun.kotlin.sdk.service.oss2.hash.Crc64
import com.aliyun.kotlin.sdk.service.oss2.hash.combine
import com.aliyun.kotlin.sdk.service.oss2.hash.md5
import com.aliyun.kotlin.sdk.service.oss2.models.DownloadCheckpoint
import com.aliyun.kotlin.sdk.service.oss2.models.DownloadRange
import com.aliyun.kotlin.sdk.service.oss2.models.DownloadResult
import com.aliyun.kotlin.sdk.service.oss2.models.DownloadedChunk
import com.aliyun.kotlin.sdk.service.oss2.models.DownloaderOptions
import com.aliyun.kotlin.sdk.service.oss2.models.GetObjectMetaRequest
import com.aliyun.kotlin.sdk.service.oss2.models.GetObjectRequest
import com.aliyun.kotlin.sdk.service.oss2.models.HttpRange
import com.aliyun.kotlin.sdk.service.oss2.models.SourceInfo
import com.aliyun.kotlin.sdk.service.oss2.progress.ProgressObserver
import com.aliyun.kotlin.sdk.service.oss2.types.ByteStream
import com.aliyun.kotlin.sdk.service.oss2.types.FeatureFlagsType
import com.aliyun.kotlin.sdk.service.oss2.types.StreamObserver
import com.aliyun.kotlin.sdk.service.oss2.types.toByteArray
import com.aliyun.kotlin.sdk.service.oss2.types.toFlow
import com.aliyun.kotlin.sdk.service.oss2.utils.XmlUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import java.io.File
import java.io.RandomAccessFile
import kotlin.math.min

public class Downloader {

    private val client: OSSClient
    private val options: DownloaderOptions

    public constructor(
        client: OSSClient,
        vararg actions: (DownloaderOptions) -> Unit
    ) {
        this.client = client
        val options = DownloaderOptions(
            DOWNLOAD_PART_SIZE,
            DOWNLOAD_PARALLEL,
            false
        )

        for (action in actions) {
            action(options)
        }
        this.options = options
    }

    public suspend fun downloadFile(
        request: GetObjectRequest,
        filePath: Path,
        vararg actions: (DownloaderOptions) -> Unit
    ): DownloadResult {
        requireNotNull(request.bucket) { "request.bucket is required" }
        requireNotNull(request.key) { "request.key is required" }

        val opt = options
        for (action in actions) {
            action(opt)
        }
        if (opt.partSize <= 0) {
            opt.partSize = DOWNLOAD_PART_SIZE
        }
        if (opt.parallelNum <= 0) {
            opt.parallelNum = DOWNLOAD_PARALLEL
        }

        val delegate = DownloadDelegate(client, opt, request)
        return delegate.download(filePath)
    }

    public suspend fun abortDownload(
        request: GetObjectRequest,
        filePath: Path
    ) {
        requireNotNull(request.bucket) { "request.bucket is required" }
        requireNotNull(request.key) { "request.key is required" }

        val delegate = DownloadDelegate(client, options, request)
        delegate.abortDownload(filePath)
    }
}

internal class DownloadDelegate {

    private val client: OSSClient
    private var options: DownloaderOptions
    private val request: GetObjectRequest

    val tempFileDir = Path("${System.getProperty("user.home")}/OSS")

    constructor(
        client: OSSClient,
        options: DownloaderOptions,
        request: GetObjectRequest
    ) {
        this.client = client
        this.options = options
        this.request = request

        if (this.options.partSize <= 0) {
            this.options.partSize = DOWNLOAD_PART_SIZE
        }
        if (this.options.parallelNum <= 0) {
            this.options.parallelNum = DOWNLOAD_PARALLEL
        }
    }

    fun checkCheckpoint(
        sourceInfo: SourceInfo,
        filePath: Path,
        baseDir: Path?,
        partSize: Int,
        downloadRange: DownloadRange
    ): DownloadCheckpoint? {
        if (!options.enableCheckpoint) {
            return null
        }

        val name = "${request.bucket}/${request.key}"
        val srcHash =
            "oss://${XmlUtils.escapeText(name)}\n${request.versionId ?: ""}\n${request.range ?: ""}".toByteArray()
                .md5().toHexString()
        val destHash = filePath.toString().toByteArray().md5().toHexString()

        val cpFileDir = baseDir ?: tempFileDir
        if (!SystemFileSystem.exists(cpFileDir)) {
            SystemFileSystem.createDirectories(cpFileDir)
        }

        val cpFilePath = "$cpFileDir/$srcHash-$destHash$CHECK_POINT_FILE_SUFFIX_DOWNLOADER"

        val checkpoint = DownloadCheckpoint(
            cpFileDir,
            Path(cpFilePath),
            options.verifyData ?: false,
            false,
            DownloadCheckpoint.Info(
                CHECK_POINT_MAGIC,
                null,
                DownloadCheckpoint.Info.Data(
                    DownloadCheckpoint.Info.Data.ObjectInfo(
                        "oss://${name}",
                        request.versionId,
                        request.range
                    ),
                    DownloadCheckpoint.Info.Data.ObjectMeta(
                        sourceInfo.sizeInBytes,
                        sourceInfo.modTime,
                        sourceInfo.eTag
                    ),
                    DownloadCheckpoint.Info.Data.DownloadInfo(0, 0),
                    filePath.toString(),
                    partSize
                )
            )
        )

        checkpoint.load()
        if (checkpoint.loaded) {
            checkpoint.info.data.downloadInfo?.offset?.let {
                downloadRange.pos = it
            }
        } else {
            checkpoint.info.data.downloadInfo?.offset = downloadRange.pos
        }
        return checkpoint
    }

    suspend fun checkSource(): SourceInfo {
        val result = client.getObjectMeta(
            GetObjectMetaRequest {
                bucket = request.bucket
                key = request.key
            }
        )

        val size = result.contentLength ?: throw ResponseException("Can't get size of object")
        val lastModified =
            result.lastModified ?: throw ResponseException("Can't get lastModified of object")
        val eTag = result.eTag ?: throw ResponseException("Can't get eTag of object")
        return SourceInfo(
            lastModified,
            eTag,
            size,
            result.headers
        )
    }

    fun checkDestination(filePath: Path): Path {
        require(filePath.toString().isNotEmpty()) { "filePath is invalid" }

        return if (options.useTempFile) {
            Path(filePath.toString() + TEMP_FILE_SUFFIX)
        } else {
            filePath
        }
    }

    fun adjustRange(sizeInBytes: Long): DownloadRange {
        var pos: Long = 0
        var rStart: Long = 0
        var ePos: Long = sizeInBytes

        request.range?.let { range ->
            val httpRange = HttpRange(range)
            if (httpRange.offset >= sizeInBytes) {
                throw RequestException("Invalid range $range, object size: $sizeInBytes")
            }
            pos = httpRange.offset
            rStart = pos
            httpRange.count?.let {
                val end = httpRange.offset + it
                if (it > 0) {
                    ePos = min(end, sizeInBytes)
                }
            }
        }

        return DownloadRange(pos, ePos, rStart)
    }

    suspend fun adjustWriter(
        fileWriter: FileWriter,
        downloadRange: DownloadRange
    ) {
        val pos = downloadRange.pos
        val ePos = downloadRange.ePos
        val rStart = downloadRange.rStart

        val expectSize = ePos - rStart
        if (fileWriter.file.length() > expectSize) {
            fileWriter.truncate(pos - rStart)
        }
    }

    suspend fun download(filePath: Path): DownloadResult {
        val mutex = Mutex()
        val semaphore = Semaphore(permits = options.parallelNum)
        val checkCrc =
            ((client as? DefaultOSSClient)?.clientImpl?.featureFlags?.contains(FeatureFlagsType.ENABLE_CRC64_CHECK_DOWNLOAD)
                ?: false) && request.range == null
        var cpChunks = mutableListOf<DownloadedChunk>()

        val sourceInfo = checkSource()
        val tempFilePath = checkDestination(filePath)
        val downloadRange = adjustRange(sourceInfo.sizeInBytes)
        val checkpoint = checkCheckpoint(
            sourceInfo,
            tempFilePath,
            options.checkpointDir,
            options.partSize,
            downloadRange
        )
        val calcCRC = (checkpoint?.verifyData == true) || checkCrc
        var written = downloadRange.pos - downloadRange.rStart
        var tOffset = checkpoint?.info?.data?.downloadInfo?.offset ?: 0
        var tCRC64 = checkpoint?.info?.data?.downloadInfo?.crc ?: 0
        val observer = request.progressListener?.let {
            ProgressObserver(
                it,
                sourceInfo.sizeInBytes,
                written
            )
        }
        try {
            FileWriter(tempFilePath.toString()).use { fileWriter ->
                adjustWriter(fileWriter, downloadRange)
                val ePos = downloadRange.ePos
                val pos = downloadRange.pos
                val size = min(ePos - pos, options.partSize.toLong())

                coroutineScope {
                    for (offset in pos..<ePos step size) {
                        semaphore.acquire()
                        val chunk = DownloadedChunk(
                            offset,
                            min(size, ePos - offset),
                            downloadRange.rStart,
                            null
                        )
                        launch {
                            val dChunk = downloadChunk(
                                fileWriter,
                                chunk,
                                sourceInfo,
                                observer,
                                calcCRC
                            )
                            written += dChunk.size
                            mutex.withLock {
                                cpChunks.add(dChunk)
                                cpChunks.sortedBy { it.start }

                                var newOffset = tOffset
                                var i = 0
                                for (cpChunk in cpChunks) {
                                    if (cpChunk.start == newOffset) {
                                        newOffset += cpChunk.size
                                        i++
                                    } else {
                                        break
                                    }
                                }

                                if (newOffset != tOffset) {
                                    //remove updated chunk in cpChunks
                                    if (calcCRC) {
                                        for (ii in 0..<i) {
                                            cpChunks[ii].crc64?.let { crc ->
                                                tCRC64 = tCRC64.combine(crc, cpChunks[ii].size)
                                            }
                                        }
                                    }
                                    tOffset = newOffset
                                    cpChunks = cpChunks.subList(i, cpChunks.size)
                                    checkpoint?.let { checkpoint ->
                                        checkpoint.info.data.downloadInfo?.offset = tOffset
                                        checkpoint.info.data.downloadInfo?.crc = tCRC64
                                        checkpoint.dump()
                                    }
                                }
                            }
                            semaphore.release()
                        }
                    }
                }

                if (checkCrc) {
                    sourceInfo.headers?.get("x-oss-hash-crc64ecma")?.let { serverCrc ->
                        if (cpChunks.isNotEmpty()) {
                            cpChunks.sortedBy { it.start }.forEach { chunk ->
                                chunk.crc64?.let { crc ->
                                    tCRC64 = tCRC64.combine(crc, chunk.size)
                                }
                            }
                        }
                        val clientCrc = tCRC64.toULong().toString()
                        if (clientCrc != serverCrc) {
                            throw InconsistentException(clientCrc, serverCrc, sourceInfo.headers)
                        }
                    }
                }
                if (options.useTempFile) {
                    fileWriter.file.renameTo(File(filePath.toString()))
                }
                checkpoint?.remove()
                return DownloadResult(written)
            }
        } catch (e: Exception) {
            if (!options.enableCheckpoint && SystemFileSystem.exists(tempFilePath)) {
                SystemFileSystem.delete(tempFilePath)
            }
            throw e
        }
    }

    suspend fun downloadChunk(
        fileWriter: FileWriter,
        chunk: DownloadedChunk,
        sourceInfo: SourceInfo,
        observer: ProgressObserver?,
        calcCRC: Boolean
    ): DownloadedChunk {
        while (true) {
            val result = client.getObjectAsStream(GetObjectRequest {
                bucket = request.bucket
                key = request.key
                range = HttpRange(chunk.start, chunk.size).toString()
                rangeBehavior = "standard"
            })
            if (result.eTag != sourceInfo.eTag) {
                throw RequestException("Source file is changed")
            }
            val stream = result.body ?: throw ResponseException("Can't obtain body data.")
            val observers = mutableListOf<StreamObserver>().also {
                observer?.let { observer -> it.add(observer) }
            }
            val crC64Observer = if (calcCRC) {
                CRC64Observer(0).also {
                    observers.add(it)
                }
            } else { null }
            val writeLength = fileWriter.writeAt(chunk.start - chunk.rStart, stream, observers)
            if (writeLength == chunk.size) {
                return DownloadedChunk(
                    chunk.start,
                    writeLength,
                    chunk.rStart,
                    crC64Observer?.checksum?.digestValue
                )
            }
        }
    }

    suspend fun abortDownload(filePath: Path) {
        val tempFilePath = checkDestination(filePath)
        val sourceInfo = checkSource()
        val downloadRange = adjustRange(sourceInfo.sizeInBytes)
        val checkpoint = checkCheckpoint(
            sourceInfo,
            tempFilePath,
            options.checkpointDir,
            options.partSize,
            downloadRange
        )
        checkpoint?.remove()
        SystemFileSystem.delete(tempFilePath)
    }
}

internal class FileWriter(
    private val filePath: String,
) : AutoCloseable {

    val file = File(filePath)
    private val raf = RandomAccessFile(file, "rw")

    private val mutex = Mutex()
    suspend fun writeAt(
        position: Long,
        stream: ByteStream,
        observers: List<StreamObserver>
    ): Long {
        mutex.withLock {
            raf.seek(position)
            var size: Long = 0
            stream.toFlow().collect {
                size += it.size
                raf.write(it)
                for (observer in observers) {
                    observer.data(it, 0 ,it.size)
                }
            }
            return size
        }
    }

    suspend fun truncate(offset: Long) {
        mutex.withLock {
            raf.setLength(offset)
        }
    }

    override fun close() {
        raf.close()
    }
}

