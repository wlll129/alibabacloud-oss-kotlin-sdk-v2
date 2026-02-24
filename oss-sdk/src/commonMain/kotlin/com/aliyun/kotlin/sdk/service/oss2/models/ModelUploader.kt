package com.aliyun.kotlin.sdk.service.oss2.models

import com.aliyun.kotlin.sdk.service.oss2.Defaults.CHECK_POINT_MAGIC
import com.aliyun.kotlin.sdk.service.oss2.hash.md5
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

public class UploaderOptions(
    public var partSize: Int,
    public var parallelNum: Int,
    public var leavePartsOnError: Boolean,
    public var enableCheckpoint: Boolean? = null,
    public var checkpointDir: Path? = null
) {
}

public class UploadResult(builder: Builder): ResultModel(builder) {

    /**
     * The upload ID that uniquely identifies the multipart upload task.
     */
    public var uploadId: String? = builder.uploadId

    /**
     * The entity tag (ETag). An ETag is created when the object is created to identify the content of an object.
     * For an object that is created by calling the PutObject operation, the ETag value of the object is the MD5 hash of the object content.
     * For an object that is created by using another method, the ETag value is not the MD5 hash of the object content but a unique value calculated based on a specific rule.
     * The ETag of an object can be used to check whether the object content changes. However, we recommend that you use the MD5 hash of an object rather than the ETag value of the object to verify data integrity.
     */
    public var eTag: String? = builder.eTag

    /**
     * The version id of the target object.
     */
    public var versionId: String? = builder.versionId

    /**
     * The 64-bit CRC value of the object. This value is calculated based on the ECMA-182 standard.
     */
    public var hashCRC64: Long? = builder.hashCRC64

    public companion object {
        public operator fun invoke(builder: UploadResult.Builder.() -> Unit): UploadResult =
            Builder().apply(builder).build()
    }

    public class Builder() : ResultModel.Builder() {

        /**
         * The upload ID that uniquely identifies the multipart upload task.
         */
        public var uploadId: String? = null

        /**
         * The entity tag (ETag). An ETag is created when the object is created to identify the content of an object.
         * For an object that is created by calling the PutObject operation, the ETag value of the object is the MD5 hash of the object content.
         * For an object that is created by using another method, the ETag value is not the MD5 hash of the object content but a unique value calculated based on a specific rule.
         * The ETag of an object can be used to check whether the object content changes. However, we recommend that you use the MD5 hash of an object rather than the ETag value of the object to verify data integrity.
         */
        public var eTag: String? = null

        /**
         * The version id of the target object.
         */
        public var versionId: String? = null

        /**
         * The 64-bit CRC value of the object. This value is calculated based on the ECMA-182 standard.
         */
        public var hashCRC64: Long? = null

        public fun build(): UploadResult {
            return UploadResult(this)
        }
    }
}

internal class UploadPartCRC(
    val uploadPart: Part,
    val crcValue: Long?,
    val size: Long,
)

internal class TransferredInfo(
    val parts: MutableList<UploadPartCRC> = mutableListOf(),
    var transferred: Long = 0
) {

    fun transferred(part: UploadPartCRC) {
        parts.add(part)
        transferred += part.size
    }
}

internal class UploadInfo(
    val totalSize: Long,
    val partSize: Int,
    val checkpoint: UploadCheckpoint?
)

internal class UploadCheckpoint(
    val cpDirPath: Path,
    val cpFilePath: Path,
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
        data class Data(
            val partSize: Int,
            val fileMeta: FileMeta,
            val objectInfo: ObjectInfo,
            var uploadInfo: UploadInfo?
        ) {
            @Serializable
            data class FileMeta(
                val size: Long,
                val lastModified: String
            )

            @Serializable
            data class ObjectInfo(
                val objectKey: String
            )

            @Serializable
            data class UploadInfo(
                val uploadId: String
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

    fun valid() : Boolean {
        val bytes = SystemFileSystem.source(cpFilePath).buffered().use {
            it.readByteArray()
        }

        val info = Json.decodeFromString<Info>(bytes.decodeToString())
        // TODO: sortedKeys？
        val data = Json.encodeToString(info.data).toByteArray()
        val md5 = data.md5().toHexString()
        if (!(info.magic == CHECK_POINT_MAGIC && md5 == info.md5)) {
            return false
        }

        if (!(info.data.objectInfo == this.info.data.objectInfo &&
                info.data.fileMeta == this.info.data.fileMeta &&
                info.data.partSize == this.info.data.partSize)) {
            return false
        }

        if (info.data.uploadInfo?.uploadId?.isEmpty() == true) {
            return false
        }
        this.info.data.uploadInfo = info.data.uploadInfo
        return true
    }

    fun dump() {
        // TODO: sortedKeys？
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
        SystemFileSystem.delete(cpFilePath)
    }
}
