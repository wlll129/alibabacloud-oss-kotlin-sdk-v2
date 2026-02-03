package com.aliyun.kotlin.sdk.service.oss2.extension.models

import com.aliyun.kotlin.sdk.service.oss2.serialization.xml.XmlElement
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * The conversion of the storage class of objects that match the lifecycle rule when the objects expire.
 * The storage class of the objects can be converted to IA, Archive, and ColdArchive. The storage class of Standard objects in a Standard bucket can be converted to IA, Archive, or Cold Archive.
 * The period of time from when the objects expire to when the storage class of the objects is converted to Archive must be longer than the period of time from when the objects expire to when the storage class of the objects is converted to IA.
 * For example, if the validity period is set to 30 for objects whose storage class is converted to IA after the validity period, the validity period must be set to a value greater than 30 for objects whose storage class is converted to Archive.
 * Either Days or CreatedBeforeDate is required.
 */
@Serializable
@SerialName("Transition")
public data class Transition(
    /**
     * The storage class to which objects are converted. Valid values:
     * *   IA
     * *   Archive
     * *   ColdArchive
     * You can convert the storage class of objects in an IA bucket to only Archive or Cold Archive.
     */
    @XmlElement("StorageClass") public var storageClass: String? = null,

    /**
     * Specifies whether the lifecycle rule applies to objects based on their last access time. Valid values:
     * *   true: The rule applies to objects based on their last access time.
     * *   false: The rule applies to objects based on their last modified time.
     */
    @XmlElement("IsAccessTime") public var isAccessTime: Boolean? = null,

    /**
     * Specifies whether to convert the storage class of non-Standard objects back to Standard after the objects are accessed. This parameter takes effect only when the IsAccessTime parameter is set to true. Valid values:
     * *   true: converts the storage class of the objects to Standard.
     * *   false: does not convert the storage class of the objects to Standard.
     */
    @XmlElement("ReturnToStdWhenVisit") public var returnToStdWhenVisit: Boolean? = null,

    /**
     * Specifies whether to convert the storage class of objects whose sizes are less than 64 KB to IA, Archive, or Cold Archive based on their last access time. Valid values:
     * *   true: converts the storage class of objects that are smaller than 64 KB to IA, Archive, or Cold Archive. Objects that are smaller than 64 KB are charged as 64 KB. Objects that are greater than or equal to 64 KB are charged based on their actual sizes. If you set this parameter to true, the storage fees may increase.
     * *   false: does not convert the storage class of an object that is smaller than 64 KB.
     */
    @XmlElement("AllowSmallFile") public var allowSmallFile: Boolean? = null,

    /**
     * The date based on which the lifecycle rule takes effect. OSS performs the specified operation on data whose last modified date is earlier than this date. Specify the time in the ISO 8601 standard. The time must be at 00:00:00 in UTC.
     */
    @XmlElement("CreatedBeforeDate") public var createdBeforeDate: String? = null,

    /**
     * The number of days from when the objects were last modified to when the lifecycle rule takes effect.
     */
    @XmlElement("Days") public var days: Int? = null
) {
    public companion object {
        public operator fun invoke(builder: Transition.() -> Unit): Transition =
            Transition().apply(builder)
    }
}
