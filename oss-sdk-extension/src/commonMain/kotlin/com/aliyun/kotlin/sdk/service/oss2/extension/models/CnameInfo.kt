package com.aliyun.kotlin.sdk.service.oss2.extension.models

import com.aliyun.kotlin.sdk.service.oss2.serialization.xml.XmlElement
import com.aliyun.kotlin.sdk.service.oss2.serialization.xml.XmlRoot
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * The information about the CNAME records.
 */
@Serializable
@SerialName("CnameInfo")
@XmlRoot
public class CnameInfo(
    /**
     * The time when the custom domain name was mapped.
     */
    @XmlElement("LastModified") public var lastModified: String? = null,

    /**
     * The status of the domain name. Valid values:
     * *   Enabled
     * *   Disabled
     */
    @XmlElement("Status") public var status: String? = null,

    /**
     * The container in which the certificate information is stored.
     */
    @XmlElement("Certificate") public var certificate: CnameCertificate? = null,

    /**
     * The custom domain name.
     */
    @XmlElement("Domain") public var domain: String? = null
) {
    public companion object {
        public operator fun invoke(builder: CnameInfo.() -> Unit): CnameInfo =
            CnameInfo().apply(builder)
    }
}
