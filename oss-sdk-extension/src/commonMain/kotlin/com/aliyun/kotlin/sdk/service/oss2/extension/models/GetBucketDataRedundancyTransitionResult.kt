package com.aliyun.kotlin.sdk.service.oss2.extension.models

/**
 * The result for the GetBucketDataRedundancyTransition operation.
 */
public class GetBucketDataRedundancyTransitionResult(builder: Builder): ResultModel(builder) { 

    /**
     * The container for a specific redundancy type change task.
     */
    public val bucketDataRedundancyTransition: BucketDataRedundancyTransition?
        get() = innerBody as? BucketDataRedundancyTransition
     

    public companion object {
        public operator fun invoke(builder: Builder.() -> Unit): GetBucketDataRedundancyTransitionResult =
            Builder().apply(builder).build()
    }

    public class Builder: ResultModel.Builder() {
        public fun build(): GetBucketDataRedundancyTransitionResult {
            return GetBucketDataRedundancyTransitionResult(this)
        }
    }
}
