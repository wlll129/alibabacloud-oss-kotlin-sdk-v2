package com.aliyun.kotlin.sdk.service.oss2.extension.models

/**
 * The result for the GetBucketLifecycle operation.
 */
public class GetBucketLifecycleResult(builder: Builder): ResultModel(builder) { 

    /**
     * The container that stores the lifecycle rules configured for the bucket.
     */
    public val lifecycleConfiguration: LifecycleConfiguration?
        get() = innerBody as? LifecycleConfiguration
     

    public companion object {
        public operator fun invoke(builder: Builder.() -> Unit): GetBucketLifecycleResult =
            Builder().apply(builder).build()
    }

    public class Builder: ResultModel.Builder() {
        public fun build(): GetBucketLifecycleResult {
            return GetBucketLifecycleResult(this)
        }
    }
}
