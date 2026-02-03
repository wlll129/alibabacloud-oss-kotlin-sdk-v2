package com.aliyun.kotlin.sdk.service.oss2.extension.models

import com.aliyun.kotlin.sdk.service.oss2.serialization.xml.XmlElement
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * The delete operation to perform on objects based on the lifecycle rule. For an object in a versioning-enabled bucket, the delete operation specified by this parameter is performed only on the current version of the object.The period of time from when the objects expire to when the objects are deleted must be longer than the period of time from when the objects expire to when the storage class of the objects is converted to IA or Archive.
 */
@Serializable
@SerialName("Expiration")
public data class Expiration(
    /**
     * The date based on which the lifecycle rule takes effect. OSS performs the specified operation on data whose last modified date is earlier than this date. The value of this parameter is in the yyyy-MM-ddT00:00:00.000Z format.Specify the time in the ISO 8601 standard. The time must be at 00:00:00 in UTC.
     */
    @XmlElement("CreatedBeforeDate") public var createdBeforeDate: String? = null,

    /**
     * The number of days from when the objects were last modified to when the lifecycle rule takes effect.
     */
    @XmlElement("Days") public var days: Int? = null,

    /**
     * Specifies whether to automatically remove expired delete markers.*   true: Expired delete markers are automatically removed. If you set this parameter to true, you cannot specify the Days or CreatedBeforeDate parameter.*   false: Expired delete markers are not automatically removed. If you set this parameter to false, you must specify the Days or CreatedBeforeDate parameter.
     */
    @XmlElement("ExpiredObjectDeleteMarker") public var expiredObjectDeleteMarker: Boolean? = null,

    /**
     * The date after which the lifecycle rule takes effect. If the specified time is earlier than the current moment, it'll takes effect immediately. (This fields is NOT RECOMMENDED, please use Days or CreateDateBefore)
     */
    @XmlElement("Date") public var date: Long? = null
) {
    public companion object {
        public operator fun invoke(builder: Expiration.() -> Unit): Expiration =
            Expiration().apply(builder)
    }
}
