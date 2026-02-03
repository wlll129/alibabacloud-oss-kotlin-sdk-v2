package com.aliyun.kotlin.sdk.service.oss2.extension.models

import com.aliyun.kotlin.sdk.service.oss2.serialization.xml.XmlElement
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Filter for object names.
 */
@Serializable
@SerialName("Key")
public data class Key(
    /**
     * The prefix of an object name.
     */
    @XmlElement("Prefix") public var prefix: String? = null,

    /**
     * The suffix of an object name.
     */
    @XmlElement("Suffix") public var suffix: String? = null
) {
    public companion object {
        public operator fun invoke(builder: Key.() -> Unit): Key =
            Key().apply(builder)
    }
}
