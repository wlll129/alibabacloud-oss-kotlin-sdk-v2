package com.aliyun.kotlin.sdk.service.oss2.extension.models

import com.aliyun.kotlin.sdk.service.oss2.serialization.xml.XmlElement
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The condition that is matched by objects to which the lifecycle rule does not apply.
 */
@Serializable
@SerialName("LifecycleRuleNot")
public data class LifecycleRuleNot(
    /**
     * The prefix in the names of the objects to which the lifecycle rule does not apply.
     */
    @XmlElement("Prefix") public var prefix: String? = null,

    /**
     * The tag of the objects to which the lifecycle rule does not apply.
     */
    @XmlElement("Tag") public var tag: Tag? = null
) {
    public companion object {
        public operator fun invoke(builder: LifecycleRuleNot.() -> Unit): LifecycleRuleNot =
            LifecycleRuleNot().apply(builder)
    }
}
