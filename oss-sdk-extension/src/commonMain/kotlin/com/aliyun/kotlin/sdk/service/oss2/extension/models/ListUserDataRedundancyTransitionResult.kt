package com.aliyun.kotlin.sdk.service.oss2.extension.models

public class ListUserDataRedundancyTransitionResult(builder: Builder) : ResultModel(builder) {


    internal val delegate: ListBucketDataRedundancyTransitionXml? = innerBody as? ListBucketDataRedundancyTransitionXml

    /**
     * Indicates whether the returned results are truncated. Valid values:
     * - true: indicates that not all results are returned for the request.
     * - false: indicates that all results are returned for the request.
     */
    public var isTruncated: Boolean? = delegate?.isTruncated

    /**
     * Indicates that this ListUserDataRedundancyTransition request contains subsequent results. You must set NextContinuationToken to continuation-token to continue obtaining the results.
     */
    public var nextContinuationToken: String? = delegate?.nextContinuationToken

    /**
     * The container in which the redundancy type change task is stored.
     */
    public var bucketDataRedundancyTransitions: List<BucketDataRedundancyTransition>? = delegate?.bucketDataRedundancyTransitions

    public companion object {
        public operator fun invoke(builder: Builder.() -> Unit): ListUserDataRedundancyTransitionResult =
            Builder().apply(builder).build()
    }

    public class Builder : ResultModel.Builder() {
        public fun build(): ListUserDataRedundancyTransitionResult {
            return ListUserDataRedundancyTransitionResult(this)
        }
    }
}
