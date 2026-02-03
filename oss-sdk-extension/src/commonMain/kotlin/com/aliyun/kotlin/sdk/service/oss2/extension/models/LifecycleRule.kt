package com.aliyun.kotlin.sdk.service.oss2.extension.models

import com.aliyun.kotlin.sdk.service.oss2.serialization.xml.XmlElement
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * The container that stores lifecycle rules.
 * *   A lifecycle rule cannot be configured to convert the storage class of objects in an Archive bucket.
 * *   The period of time from when the objects expire to when the objects are deleted must be longer than the period of time from when the objects expire to when the storage class of the objects is converted to IA or Archive.
 */
@Serializable
@SerialName("LifecycleRule")
public data class LifecycleRule(
    /**
     * Specifies whether to enable the rule. Valid values:
     * *   Enabled: enables the rule. OSS periodically executes the rule.
     * *   Disabled: does not enable the rule. OSS ignores the rule.
     */
    @XmlElement("Status") public var status: String? = null,

    /**
     * The delete operation to perform on objects based on the lifecycle rule. For an object in a versioning-enabled bucket,
     * the delete operation specified by this parameter is performed only on the current version of the object.The period of time from when the objects expire to when the objects are deleted must be longer
     * than the period of time from when the objects expire to when the storage class of the objects is converted to IA or Archive.
     */
    @XmlElement("Expiration") public var expiration: Expiration? = null,

    /**
     * The delete operation that you want OSS to perform on the parts that are uploaded in incomplete multipart upload tasks when the parts expire.
     */
    @XmlElement("AbortMultipartUpload") public var abortMultipartUpload: AbortMultipartUpload? = null,

    /**
     * The tag of the objects to which the lifecycle rule applies. You can specify multiple tags.
     */
    @XmlElement("Tag") public var tags: List<Tag>? = null,

    /**
     * The container that stores the Not parameter that is used to filter objects.
     */
    @XmlElement("Filter") public var filter: LifecycleRuleFilter? = null,

    /**
     * The prefix in the names of the objects to which the rule applies. The prefixes specified by different rules cannot overlap.
     * *   If Prefix is specified, this rule applies only to objects whose names contain the specified prefix in the bucket.
     * *   If Prefix is not specified, this rule applies to all objects in the bucket.
     */
    @XmlElement("Prefix") public var prefix: String? = null,

    /**
     * The conversion of the storage class of objects that match the lifecycle rule when the objects expire.
     * The storage class of the objects can be converted to IA, Archive, and ColdArchive.
     * The storage class of Standard objects in a Standard bucket can be converted to IA, Archive, or Cold Archive.
     * The period of time from when the objects expire to when the storage class of the objects is converted to Archive must be longer than the period of time from when the objects expire to when the storage class of the objects is converted to IA.
     * For example, if the validity period is set to 30 for objects whose storage class is converted to IA after the validity period, the validity period must be set to a value greater than 30 for objects whose storage class is converted to Archive.
     * Either Days or CreatedBeforeDate is required.
     */
    @XmlElement("Transition") public var transitions: List<Transition>? = null,

    /**
     * The delete operation that you want OSS to perform on the previous versions of the objects that match the lifecycle rule when the previous versions expire.
     */
    @XmlElement("NoncurrentVersionExpiration") public var noncurrentVersionExpiration: NoncurrentVersionExpiration? = null,

    /**
     * The conversion of the storage class of previous versions of the objects that match the lifecycle rule when the previous versions expire.
     * The storage class of the previous versions can be converted to IA or Archive.
     * The period of time from when the previous versions expire to when the storage class of the previous versions is converted to Archive must be longer than the period of time from when the previous versions expire to when the storage class of the previous versions is converted to IA.
     */
    @XmlElement("NoncurrentVersionTransition") public var noncurrentVersionTransitions: List<NoncurrentVersionTransition>? = null,

    /**
     * Timestamp for when access tracking was enabled.
     */
    @XmlElement("AtimeBase") public var atimeBase: Long? = null,

    /**
     * The ID of the lifecycle rule. The ID can contain up to 255 characters. If you do not specify the ID, OSS automatically generates a unique ID for the lifecycle rule.
     */
    @XmlElement("ID") public var id: String? = null
) {
    public companion object {
        public operator fun invoke(builder: LifecycleRule.() -> Unit): LifecycleRule =
            LifecycleRule().apply(builder)
    }
}
