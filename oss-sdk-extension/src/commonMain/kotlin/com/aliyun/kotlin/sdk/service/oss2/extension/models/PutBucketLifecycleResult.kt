package com.aliyun.kotlin.sdk.service.oss2.extension.models

/**
 * The result for the PutBucketLifecycle operation.
 */
public class PutBucketLifecycleResult(builder: Builder): ResultModel(builder) { 

    public companion object {
        public operator fun invoke(builder: Builder.() -> Unit): PutBucketLifecycleResult =
            Builder().apply(builder).build()
    }

    public class Builder: ResultModel.Builder() {
        public fun build(): PutBucketLifecycleResult {
            return PutBucketLifecycleResult(this)
        }
    }
}
