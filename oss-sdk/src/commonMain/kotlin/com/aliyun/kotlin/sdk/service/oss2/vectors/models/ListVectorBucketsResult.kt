package com.aliyun.kotlin.sdk.service.oss2.vectors.models

import com.aliyun.kotlin.sdk.service.oss2.models.ResultModel

/**
 * The result for the ListBuckets operation.
 */
public class ListVectorBucketsResult(builder: Builder) : ResultModel(builder) {

    private val delegate = builder.innerBody as? ListAllMyBucketsResult

    /**
     * The prefix contained in the names of returned buckets.
     */
    public val prefix: String?
        get() = delegate?.prefix

    /**
     * The name of the bucket from which the buckets are returned.
     */
    public val marker: String?
        get() = delegate?.marker

    /**
     * The maximum number of buckets that can be returned.
     */
    public val maxKeys: Long?
        get() = delegate?.maxKeys

    /**
     * Indicates whether all results are returned.
     * Valid values:
     * - true: All results are not returned in the response.
     * - false: All results are returned in the response.
     */
    public val isTruncated: Boolean?
        get() = delegate?.isTruncated

    /**
     * The marker for the next ListBuckets (GetService) request. You can use the value of this parameter as the value of marker in the next ListBuckets (GetService) request to retrieve the unreturned results.
     */
    public val nextMarker: String?
        get() = delegate?.nextMarker

    /**
     * The container that stores the information about multiple buckets.
     */
    public val buckets: List<BucketInfo>?
        get() = delegate?.buckets

    public companion object Companion {
        public operator fun invoke(builder: Builder.() -> Unit): ListVectorBucketsResult =
            Builder().apply(builder).build()
    }

    public class Builder : ResultModel.Builder() {
        public fun build(): ListVectorBucketsResult {
            return ListVectorBucketsResult(this)
        }
    }
}
