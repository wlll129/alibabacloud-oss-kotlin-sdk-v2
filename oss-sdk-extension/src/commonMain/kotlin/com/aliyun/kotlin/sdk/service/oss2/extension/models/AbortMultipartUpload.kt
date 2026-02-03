package com.aliyun.kotlin.sdk.service.oss2.extension.models

import com.aliyun.kotlin.sdk.service.oss2.serialization.xml.XmlElement
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * The delete operation that you want OSS to perform on the parts that are uploaded in incomplete multipart upload tasks when the parts expire.
 */
@Serializable
@SerialName("AbortMultipartUpload")
public data class AbortMultipartUpload(
    /**
     * The number of days from when the objects were last modified to when the lifecycle rule takes effect.
     */
    @XmlElement("Days") public var days: Int? = null,

    /**
     * The date based on which the lifecycle rule takes effect. OSS performs the specified operation on data whose last modified date is earlier than this date. Specify the time in the ISO 8601 standard. The time must be at 00:00:00 in UTC.
     */
    @XmlElement("CreatedBeforeDate") public var createdBeforeDate: String? = null

) {
    public companion object {
        public operator fun invoke(builder: AbortMultipartUpload.() -> Unit): AbortMultipartUpload =
            AbortMultipartUpload().apply(builder)
    }
}
