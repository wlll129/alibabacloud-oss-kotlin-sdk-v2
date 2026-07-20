package com.aliyun.kotlin.sdk.service.oss2.vectors.models

import com.aliyun.kotlin.sdk.service.oss2.models.ResultModel

/**
 * The result for the DeleteVectorBucket operation.
 */
public class DeleteVectorBucketResult(builder: Builder) : ResultModel(builder) {

    public companion object Companion {
        public operator fun invoke(builder: Builder.() -> Unit): DeleteVectorBucketResult =
            Builder().apply(builder).build()
    }

    public class Builder : ResultModel.Builder() {
        public fun build(): DeleteVectorBucketResult {
            return DeleteVectorBucketResult(this)
        }
    }
}
