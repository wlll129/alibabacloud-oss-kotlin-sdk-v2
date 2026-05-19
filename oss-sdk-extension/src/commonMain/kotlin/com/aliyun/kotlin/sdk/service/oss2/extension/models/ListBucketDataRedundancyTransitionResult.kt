package com.aliyun.kotlin.sdk.service.oss2.extension.models

/**
 * The result for the ListBucketDataRedundancyTransition operation.
 */
public class ListBucketDataRedundancyTransitionResult(builder: Builder) : ResultModel(builder) {

    /**
     * The container for listed redundancy type conversion tasks.
     */
    public val listBucketDataRedundancyTransition: ListBucketDataRedundancyTransition?
        get() = innerBody as? ListBucketDataRedundancyTransition


    public companion object {
        public operator fun invoke(builder: Builder.() -> Unit): ListBucketDataRedundancyTransitionResult =
            Builder().apply(builder).build()
    }

    public class Builder : ResultModel.Builder() {
        public fun build(): ListBucketDataRedundancyTransitionResult {
            return ListBucketDataRedundancyTransitionResult(this)
        }
    }
}
