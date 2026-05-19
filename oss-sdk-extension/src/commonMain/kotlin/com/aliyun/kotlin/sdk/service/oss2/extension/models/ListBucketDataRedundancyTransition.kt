package com.aliyun.kotlin.sdk.service.oss2.extension.models

import com.aliyun.kotlin.sdk.service.oss2.serialization.xml.XmlElement
import com.aliyun.kotlin.sdk.service.oss2.serialization.xml.XmlRoot
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * The container for listed redundancy type conversion tasks.
 */
@Serializable
@SerialName("ListBucketDataRedundancyTransition")
@XmlRoot
public data class ListBucketDataRedundancyTransition(
    /**
     * The information about the redundancy type conversion task.
     */
    @XmlElement("BucketDataRedundancyTransition") public var bucketDataRedundancyTransitions: List<BucketDataRedundancyTransition>? = null
) {
    public companion object {
        public operator fun invoke(builder: ListBucketDataRedundancyTransition.() -> Unit): ListBucketDataRedundancyTransition =
            ListBucketDataRedundancyTransition().apply(builder)
    }
}
