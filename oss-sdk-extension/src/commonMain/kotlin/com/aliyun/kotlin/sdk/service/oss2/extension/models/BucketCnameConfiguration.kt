package com.aliyun.kotlin.sdk.service.oss2.extension.models

import com.aliyun.kotlin.sdk.service.oss2.serialization.xml.XmlElement
import com.aliyun.kotlin.sdk.service.oss2.serialization.xml.XmlRoot
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * The container that stores the CNAME record.
 */
@Serializable
@SerialName("BucketCnameConfiguration")
@XmlRoot
public class BucketCnameConfiguration(
    /**
     * The container that stores the CNAME information.
     */
    @XmlElement("Cname") public var cname: Cname? = null
) {
    public companion object {
        public operator fun invoke(builder: BucketCnameConfiguration.() -> Unit): BucketCnameConfiguration =
            BucketCnameConfiguration().apply(builder)
    }
}
