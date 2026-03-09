package com.aliyun.kotlin.sdk.service.oss2.extension.models

import com.aliyun.kotlin.sdk.service.oss2.serialization.xml.XmlElement
import com.aliyun.kotlin.sdk.service.oss2.serialization.xml.XmlRoot
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * The container that stores the CNAME token.
 */
@Serializable
@SerialName("CnameToken")
@XmlRoot
public class CnameToken(
    /**
     * The name of the bucket to which the CNAME record is mapped.
     */
    @XmlElement("Bucket") public var bucket: String? = null,

    /**
     * The name of the CNAME record that is mapped to the bucket.
     */
    @XmlElement("Cname") public var cname: String? = null,

    /**
     * The CNAME token that is returned by OSS.
     */
    @XmlElement("Token") public var token: String? = null,

    /**
     * The time when the CNAME token expires.
     */
    @XmlElement("ExpireTime") public var expireTime: String? = null
) {
    public companion object {
        public operator fun invoke(builder: CnameToken.() -> Unit): CnameToken =
            CnameToken().apply(builder)
    }
}
