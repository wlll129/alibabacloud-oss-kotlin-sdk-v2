package com.aliyun.kotlin.sdk.service.oss2.extension.models

import com.aliyun.kotlin.sdk.service.oss2.serialization.xml.XmlElement
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Filter for event.
 */
@Serializable
@SerialName("Filter")
public data class LifecycleRuleFilter(
    /**
     * The condition that is matched by objects to which the lifecycle rule does not apply.
     */
    @XmlElement("Not") public var nots: List<LifecycleRuleNot>? = null,

    /**
     * This lifecycle rule only applies to files larger than this size.
     */
    @XmlElement("ObjectSizeGreaterThan") public var objectSizeGreaterThan: Int? = null,

    /**
     * This lifecycle rule only applies to files smaller than this size.
     */
    @XmlElement("ObjectSizeLessThan") public var objectSizeLessThan: Int? = null
) {
    public companion object Companion {
        public operator fun invoke(builder: LifecycleRuleFilter.() -> Unit): LifecycleRuleFilter =
            LifecycleRuleFilter().apply(builder)
    }
}
