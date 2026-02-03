package com.aliyun.kotlin.sdk.service.oss2.extension.models

import com.aliyun.kotlin.sdk.service.oss2.serialization.xml.XmlElement
import com.aliyun.kotlin.sdk.service.oss2.serialization.xml.XmlRoot
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * The container that stores lifecycle configurations. The container can contain up to 1,000 lifecycle rules.
 */
@Serializable
@SerialName("LifecycleConfiguration")
@XmlRoot
public data class LifecycleConfiguration(
    /**
     * The container that stores the lifecycle rules. The period of time after which objects expire must be longer than the period of time after which the storage class of the same objects is converted to Infrequent Access (IA) or Archive.
     */
    @XmlElement("Rule") public var rules: List<LifecycleRule>? = null
) {
    public companion object {
        public operator fun invoke(builder: LifecycleConfiguration.() -> Unit): LifecycleConfiguration =
            LifecycleConfiguration().apply(builder)
    }
}
