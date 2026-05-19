package com.aliyun.kotlin.sdk.service.oss2.extension.models

import com.aliyun.kotlin.sdk.service.oss2.serialization.xml.XmlElement
import com.aliyun.kotlin.sdk.service.oss2.serialization.xml.XmlRoot
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("ListBucketDataRedundancyTransition")
@XmlRoot
internal data class ListBucketDataRedundancyTransitionXml(
    /**
     * Indicates whether the returned results are truncated. Valid values:
     * - true: indicates that not all results are returned for the request.
     * - false: indicates that all results are returned for the request.
     */
    @XmlElement("IsTruncated") internal var isTruncated: Boolean? = null,

    /**
     * Indicates that this ListUserDataRedundancyTransition request contains subsequent results. You must set NextContinuationToken to continuation-token to continue obtaining the results.
     */
    @XmlElement("NextContinuationToken") internal var nextContinuationToken: String? = null,

    /**
     * The container in which the redundancy type change task is stored.
     */
    @XmlElement("BucketDataRedundancyTransition") internal var bucketDataRedundancyTransitions: List<BucketDataRedundancyTransition>? = null,

    ) {
    internal companion object {
        internal operator fun invoke(builder: ListBucketDataRedundancyTransitionXml.() -> Unit): ListBucketDataRedundancyTransitionXml =
            ListBucketDataRedundancyTransitionXml().apply(builder)
    }
}
