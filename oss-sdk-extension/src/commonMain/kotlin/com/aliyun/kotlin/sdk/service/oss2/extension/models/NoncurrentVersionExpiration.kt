package com.aliyun.kotlin.sdk.service.oss2.extension.models

import com.aliyun.kotlin.sdk.service.oss2.serialization.xml.XmlElement
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * The delete operation that you want OSS to perform on the previous versions of the objects that match the lifecycle rule when the previous versions expire.
 */
@Serializable
@SerialName("NoncurrentVersionExpiration")
public data class NoncurrentVersionExpiration(
    /**
     * The number of days from when the objects became previous versions to when the lifecycle rule takes effect.
     */
    @XmlElement("NoncurrentDays") public var noncurrentDays: Int? = null
) {
    public companion object {
        public operator fun invoke(builder: NoncurrentVersionExpiration.() -> Unit): NoncurrentVersionExpiration =
            NoncurrentVersionExpiration().apply(builder)
    }
}
