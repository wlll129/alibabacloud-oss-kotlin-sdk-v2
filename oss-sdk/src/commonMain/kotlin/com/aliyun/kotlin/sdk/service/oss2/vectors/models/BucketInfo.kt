package com.aliyun.kotlin.sdk.service.oss2.vectors.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("BucketInfo")
public data class BucketInfo(
    /**
     * The time when the vector bucket is created.
     */
    @SerialName("CreationDate") public var creationDate: String? = null,

    /**
     * The internal endpoint of the vector bucket.
     */
    @SerialName("IntranetEndpoint") public var intranetEndpoint: String? = null,

    /**
     * The public endpoint of the vector bucket.
     */
    @SerialName("ExtranetEndpoint") public var extranetEndpoint: String? = null,

    /**
     * The region in which the vector bucket is located.
     */
    @SerialName("Location") public var location: String? = null,

    /**
     * The name of the vector bucket.
     */
    @SerialName("Name") public var name: String? = null,

    /**
     * The region of the vector bucket.
     */
    @SerialName("Region") public var region: String? = null
) {
    public companion object Companion {
        public operator fun invoke(builder: BucketInfo.() -> Unit): BucketInfo =
            BucketInfo().apply(builder)
    }
}
