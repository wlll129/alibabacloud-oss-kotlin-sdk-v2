package com.aliyun.kotlin.sdk.service.oss2.extension.models

/**
 * The result for the DeleteBucketLifecycle operation.
 */
public class DeleteBucketLifecycleResult(builder: Builder): ResultModel(builder) { 

    public companion object {
        public operator fun invoke(builder: Builder.() -> Unit): DeleteBucketLifecycleResult =
            Builder().apply(builder).build()
    }

    public class Builder: ResultModel.Builder() {
        public fun build(): DeleteBucketLifecycleResult {
            return DeleteBucketLifecycleResult(this)
        }
    }
}
