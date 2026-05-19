package com.aliyun.kotlin.sdk.service.oss2.extension.models

/**
 * The result for the DeleteBucketDataRedundancyTransition operation.
 */
public class DeleteBucketDataRedundancyTransitionResult(builder: Builder): ResultModel(builder) { 

    public companion object {
        public operator fun invoke(builder: Builder.() -> Unit): DeleteBucketDataRedundancyTransitionResult =
            Builder().apply(builder).build()
    }

    public class Builder: ResultModel.Builder() {
        public fun build(): DeleteBucketDataRedundancyTransitionResult {
            return DeleteBucketDataRedundancyTransitionResult(this)
        }
    }
}
