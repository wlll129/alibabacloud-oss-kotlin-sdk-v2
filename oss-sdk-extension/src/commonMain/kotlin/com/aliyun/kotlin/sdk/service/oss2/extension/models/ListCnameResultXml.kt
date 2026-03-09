package com.aliyun.kotlin.sdk.service.oss2.extension.models

import com.aliyun.kotlin.sdk.service.oss2.extension.models.ListCnameResult.Builder
import com.aliyun.kotlin.sdk.service.oss2.serialization.xml.XmlRoot
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("ListCnameResult")
@XmlRoot
internal data class ListCnameResultXml(
    /**
     * The name of the bucket to which the CNAME records you want to query are mapped.
     */
    internal var bucket: String? = null,

    /**
     * The name of the bucket owner.
     */
    internal var owner: String? = null,

    /**
     * The container that is used to store the information about all CNAME records.
     */
    internal var cnames: List<CnameInfo>? = null
) {

    internal companion object {
        internal operator fun invoke(builder: ListCnameResultXml.() -> Unit): ListCnameResultXml =
            ListCnameResultXml().apply(builder)
    }
}
