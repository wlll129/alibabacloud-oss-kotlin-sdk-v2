package com.aliyun.kotlin.sdk.service.oss2.extension.models

/**
 * The result for the CreateBucketDataRedundancyTransition operation.
 */
public class CreateBucketDataRedundancyTransitionResult(builder: Builder): ResultModel(builder) { 

    /**
     * The container in which the redundancy type conversion task is stored.
     */
    public val bucketDataRedundancyTransition: BucketDataRedundancyTransition?
        get() = innerBody as? BucketDataRedundancyTransition
     

    public companion object {
        public operator fun invoke(builder: Builder.() -> Unit): CreateBucketDataRedundancyTransitionResult =
            Builder().apply(builder).build()
    }

    public class Builder: ResultModel.Builder() {
        public fun build(): CreateBucketDataRedundancyTransitionResult {
            return CreateBucketDataRedundancyTransitionResult(this)
        }
    }
}
