package com.aliyun.kotlin.sdk.service.oss2.extension.models

import com.aliyun.kotlin.sdk.service.oss2.serialization.xml.XmlElement
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * The container that stores the CNAME information.
 */
@Serializable
@SerialName("Cname")
public class Cname(
    /**
     * The container for which the certificate is configured.
     */
    @XmlElement("CertificateConfiguration") public var certificateConfiguration: CertificateConfiguration? = null,

    /**
     * The custom domain name.
     */
    @XmlElement("Domain") public var domain: String? = null
) {
    public companion object {
        public operator fun invoke(builder: Cname.() -> Unit): Cname =
            Cname().apply(builder)
    }
}
