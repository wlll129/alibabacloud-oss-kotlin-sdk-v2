package com.aliyun.kotlin.sdk.service.oss2.models

import com.aliyun.kotlin.sdk.service.oss2.Defaults.CHECK_POINT_MAGIC
import com.aliyun.kotlin.sdk.service.oss2.hash.Crc64
import com.aliyun.kotlin.sdk.service.oss2.hash.md5
import kotlinx.io.Buffer
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

public class DownloaderOptions(
    public var partSize: Int,
    public var parallelNum: Int,
    public var enableCheckpoint: Boolean = false,
    public var checkpointDir: Path? = null,
    public var verifyData: Boolean? = null,
    public var useTempFile: Boolean = true,
)

public class DownloadResult(
    public val written: Long
)

internal class SourceInfo(
    val modTime: String,
    val eTag: String,
    val sizeInBytes: Long,
    val headers: Map<String, String>?
)

internal class DownloadRange(
    var pos: Long,
    var ePos: Long,
    var rStart: Long
)

internal class DownloadedChunk(
    val start: Long,
    val size: Long,
    val rStart: Long,
    val crc64: Long?
)

internal class DownloadCheckpoint(
    val cpDirPath: Path,
    val cpFilePath: Path,
    val verifyData: Boolean,
    var loaded: Boolean,
    var info: Info
) {

    @Serializable
    class Info(
        val magic: String,
        var md5: String?,
        var data: Data
    ) {

        @Serializable
        class Data(
            val objectInfo: ObjectInfo,
            val objectMeta: ObjectMeta?,
            var downloadInfo: DownloadInfo?,

            val filePath: String,
            val partSize: Int
        ) {

            @Serializable
            data class ObjectInfo(
                val name: String,
                val versionId: String?,
                val range: String?
            )

            @Serializable
            data class ObjectMeta(
                val size: Long,
                val lastModified: String,
                val eTag: String
            )

            @Serializable
            data class DownloadInfo(
                var offset: Long,
                var crc: Long
            )
        }
    }

    fun load() {
        if (!(SystemFileSystem.exists(cpDirPath) && SystemFileSystem.metadataOrNull(cpDirPath)?.isDirectory == true)) {
            return
        }
        if (!(SystemFileSystem.exists(cpFilePath) && SystemFileSystem.metadataOrNull(cpFilePath)?.isDirectory == false)) {
            return
        }

        if (!valid()) {
            remove()
        }

        loaded = true
    }

    fun valid(): Boolean {
        val bytes = SystemFileSystem.source(cpFilePath).buffered().use {
            it.readByteArray()
        }
        val info = Json.decodeFromString<Info>(bytes.decodeToString())
        val data = Json.encodeToString(info.data).toByteArray()
        val md5 = data.md5().toHexString()
        if (!(info.magic == CHECK_POINT_MAGIC && md5 == info.md5)) {
            return false
        }

        if (!(
                info.data.objectInfo == this.info.data.objectInfo &&
                    info.data.objectMeta == this.info.data.objectMeta &&
                    info.data.filePath == this.info.data.filePath &&
                    info.data.partSize == this.info.data.partSize
                )
        ) {
            return false
        }

        val downloadInfo = info.data.downloadInfo ?: return false
        if (downloadInfo.offset == 0.toLong() &&
            downloadInfo.crc != 0.toLong()
        ) {
            return false
        }

        var rOffset: Long = 0
        info.data.objectInfo.range?.let { range ->
            val index = range.indexOf('-')
            if (index != -1) {
                val start = range.substring(6, index)
                rOffset = start.toLong()
            }
        }
        if (downloadInfo.offset < rOffset) {
            return false
        }

        val remains = (downloadInfo.offset - rOffset) % info.data.partSize
        if (remains != 0.toLong()) {
            return false
        }

        // valid data
        if (verifyData && info.data.downloadInfo?.crc != 0.toLong()) {
            val bufferSize = 4 * 1024
            var offset: Long = 0
            val end = downloadInfo.offset - rOffset
            val crc64 = Crc64(0)
            SystemFileSystem.source(Path(info.data.filePath)).buffered().use { source ->
                Buffer().use { sink ->
                    while (offset < end) {
                        source.request(bufferSize.toLong())
                        val rc = source.readAtMostTo(sink, bufferSize.toLong())
                        if (rc != -1L) {
                            crc64.update(sink.readByteArray(), 0, rc.toInt())
                            offset += rc
                        }
                        sink.clear()
                    }
                }
            }

            if (crc64.digestValue != downloadInfo.crc) {
                return false
            }
        }

        // update
        this.info.data.downloadInfo = downloadInfo
        return true
    }

    fun dump() {
        val data = Json.encodeToString(info.data).toByteArray()
        val md5 = data.md5().toHexString()
        val info = this.info
        info.md5 = md5

        val content = Json.encodeToString(info).toByteArray()
        SystemFileSystem.sink(cpFilePath, false).buffered().use {
            it.write(content)
        }
    }

    fun remove() {
        if (SystemFileSystem.exists(cpFilePath)) {
            SystemFileSystem.delete(cpFilePath)
        }
    }
}
