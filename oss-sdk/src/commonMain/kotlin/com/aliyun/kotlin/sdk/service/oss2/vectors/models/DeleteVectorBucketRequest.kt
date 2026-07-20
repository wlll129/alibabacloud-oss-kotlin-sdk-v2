package com.aliyun.kotlin.sdk.service.oss2.vectors.models

import com.aliyun.kotlin.sdk.service.oss2.models.RequestModel

/**
 * The request for the DeleteVectorBucket operation.
 */
public class DeleteVectorBucketRequest(builder: Builder) : RequestModel(builder) {

    /**
     * The name of the vector bucket.
     */
    public val bucket: String? = builder.bucket

    public inline fun copy(block: Builder.() -> Unit = {}): DeleteVectorBucketRequest = Builder(this).apply(block).build()

    public companion object Companion {
        public operator fun invoke(builder: Builder.() -> Unit): DeleteVectorBucketRequest =
            Builder().apply(builder).build()
    }

    public class Builder() : RequestModel.Builder() {

        /**
         * The name of the vector bucket.
         */
        public var bucket: String? = null

        public fun build(): DeleteVectorBucketRequest {
            return DeleteVectorBucketRequest(this)
        }

        public constructor(from: DeleteVectorBucketRequest) : this() {
            this.headers.putAll(from.headers)
            this.parameters.putAll(from.parameters)
            this.bucket = from.bucket
        }
    }
}
