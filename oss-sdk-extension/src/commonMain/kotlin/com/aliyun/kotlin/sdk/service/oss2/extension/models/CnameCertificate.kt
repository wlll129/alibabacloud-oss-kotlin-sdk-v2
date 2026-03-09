package com.aliyun.kotlin.sdk.service.oss2.extension.models

import com.aliyun.kotlin.sdk.service.oss2.serialization.xml.XmlElement
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * The information about the certificate.
 */
@Serializable
@SerialName("CnameCertificate")
public class CnameCertificate(
    /**
     * The status of the certificate.Valid values:
     * *   Enabled
     * *   Disabled
     */
    @XmlElement("Status") public var status: String? = null,

    /**
     * The time when the certificate was bound.
     */
    @XmlElement("CreationDate") public var creationDate: String? = null,

    /**
     * The signature of the certificate.
     */
    @XmlElement("Fingerprint") public var fingerprint: String? = null,

    /**
     * The time when the certificate takes effect.
     */
    @XmlElement("ValidStartDate") public var validStartDate: String? = null,

    /**
     * The time when the certificate expires.
     */
    @XmlElement("ValidEndDate") public var validEndDate: String? = null,

    /**
     * The source of the certificate.Valid values:
     * *   CAS
     * *   Upload
     */
    @XmlElement("Type") public var type: String? = null,

    /**
     * The ID of the certificate.
     */
    @XmlElement("CertId") public var certId: String? = null
) {
    public companion object {
        public operator fun invoke(builder: CnameCertificate.() -> Unit): CnameCertificate =
            CnameCertificate().apply(builder)
    }
}
