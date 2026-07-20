package com.aliyun.kotlin.sdk.service.oss2.vectors.models

import com.aliyun.kotlin.sdk.service.oss2.models.ResultModel

/**
 * The result for the GetVectorBucket operation.
 */
public class GetVectorBucketResult(builder: Builder) : ResultModel(builder) {

    /**
     * The container that stores the information about the vector bucket.
     */
    public val bucketInfo: BucketInfo?
        get() = innerBody as? BucketInfo

    public companion object Companion {
        public operator fun invoke(builder: Builder.() -> Unit): GetVectorBucketResult =
            Builder().apply(builder).build()
    }

    public class Builder : ResultModel.Builder() {
        public fun build(): GetVectorBucketResult {
            return GetVectorBucketResult(this)
        }
    }
}
