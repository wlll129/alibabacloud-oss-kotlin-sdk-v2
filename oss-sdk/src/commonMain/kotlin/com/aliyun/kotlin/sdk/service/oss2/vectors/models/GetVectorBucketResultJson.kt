package com.aliyun.kotlin.sdk.service.oss2.vectors.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class GetVectorBucketResultJson(
    @SerialName("BucketInfo") val bucketInfo: BucketInfo? = null
)
