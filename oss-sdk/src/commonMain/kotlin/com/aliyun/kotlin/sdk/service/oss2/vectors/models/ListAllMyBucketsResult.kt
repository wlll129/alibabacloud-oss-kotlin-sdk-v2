package com.aliyun.kotlin.sdk.service.oss2.vectors.models

import com.aliyun.kotlin.sdk.service.oss2.vectors.models.ListVectorBucketsRequest.Builder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ListAllMyBucketsResult(
    /**
     * The prefix contained in the names of returned buckets.
     */
    @SerialName("Prefix") var prefix: String? = null,

    /**
     * The name of the bucket from which the buckets are returned.
     */
    @SerialName("Marker") var marker: String? = null,

    /**
     * The maximum number of buckets that can be returned.
     */
    @SerialName("MaxKeys") var maxKeys: Long? = null,

    /**
     * Indicates whether all results are returned.
     * Valid values:
     * - true: All results are not returned in the response.
     * - false: All results are returned in the response.
     */
    @SerialName("IsTruncated") var isTruncated: Boolean? = null,

    /**
     * The marker for the next ListBuckets (GetService) request. You can use the value of this parameter as the value of marker in the next ListBuckets (GetService) request to retrieve the unreturned results.
     */
    @SerialName("NextMarker") var nextMarker: String? = null,

    /**
     * The container that stores the information about multiple vector buckets.
     */
    @SerialName("Buckets") var buckets: List<BucketInfo>? = null
) {
    companion object Companion {
        operator fun invoke(builder: ListAllMyBucketsResult.() -> Unit): ListAllMyBucketsResult =
            ListAllMyBucketsResult().apply(builder)
    }
}
