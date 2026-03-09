package com.aliyun.kotlin.sdk.service.oss2.extension.models

import com.aliyun.kotlin.sdk.service.oss2.serialization.xml.XmlElement
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * The container for which the certificate is configured.
 */
@Serializable
@SerialName("CertificateConfiguration")
public class CertificateConfiguration(
    /**
     * The ID of the certificate.
     */
    @XmlElement("CertId") public var certId: String? = null,

    /**
     * The public key of the certificate.
     */
    @XmlElement("Certificate") public var certificate: String? = null,

    /**
     * The private key of the certificate.
     */
    @XmlElement("PrivateKey") public var privateKey: String? = null,

    /**
     * The ID of the certificate. If the Force parameter is not set to true, the OSS server checks whether the value of the Force parameter matches the current certificate ID.
     * If the value does not match the certificate ID, an error is returned.noticeIf you do not specify the PreviousCertId parameter when you bind a certificate, you must set the Force parameter to true./notice
     */
    @XmlElement("PreviousCertId") public var previousCertId: String? = null,

    /**
     * Specifies whether to overwrite the certificate. Valid values:
     * - true: overwrites the certificate.
     * - false: does not overwrite the certificate.
     */
    @XmlElement("Force") public var force: Boolean? = null,

    /**
     * Specifies whether to delete the certificate. Valid values:
     * - true: deletes the certificate.
     * - false: does not delete the certificate.
     */
    @XmlElement("DeleteCertificate") public var deleteCertificate: Boolean? = null
) {
    public companion object {
        public operator fun invoke(builder: CertificateConfiguration.() -> Unit): CertificateConfiguration =
            CertificateConfiguration().apply(builder)
    }
}
