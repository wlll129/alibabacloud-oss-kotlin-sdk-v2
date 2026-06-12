package com.aliyun.kotlin.sdk.service.oss2

import com.aliyun.kotlin.sdk.service.oss2.types.FeatureFlagsType
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

internal object Defaults {

    const val PRODUCT: String = "oss"

    const val DISABLE_SSL: Boolean = false

    const val HTTP_SCHEME: String = "https"

    /**
     * Default transport 's connect timeout is 10, the unit is second
     */
    val CONNECT_TIMEOUT: Duration = 10.toDuration(DurationUnit.SECONDS)

    /**
     * Default transport 's readwrite timeout is 20, the unit is second
     */
    val READWRITE_TIMEOUT: Duration = 20.toDuration(DurationUnit.SECONDS)

    const val PART_SIZE: Int = 6 * 1024 * 1024
    const val UPLOAD_PART_SIZE = PART_SIZE

    const val DOWNLOAD_PART_SIZE = PART_SIZE

    const val CHECK_POINT_MAGIC = "92611BED-89E2-46B6-89E5-72F273D4B0A3"

    const val MAX_UPLOAD_PARTS = 10000

    const val PARALLEL: Int = 3
    const val UPLOAD_PARALLEL = PARALLEL

    const val DOWNLOAD_PARALLEL = PARALLEL

    const val CHECK_POINT_FILE_SUFFIX_UPLOADER = ".ucp"
    const val CHECK_POINT_FILE_SUFFIX_DOWNLOADER = ".dcp"
    const val TEMP_FILE_SUFFIX = ".temp"

    // defaults for retryer
    const val MAX_ATTEMPTS: Int = 3
    val MAX_BACKOFF: Duration = 20.toDuration(DurationUnit.SECONDS)
    val BASE_DELAY: Duration = 200.toDuration(DurationUnit.MILLISECONDS)

    // feature flags
    val FEATURE_FLAGS: FeatureFlagsType
        get() = FeatureFlagsType.combine(
            FeatureFlagsType.ENABLE_CRC64_CHECK_UPLOAD,
            FeatureFlagsType.ENABLE_CRC64_CHECK_DOWNLOAD,
            FeatureFlagsType.AUTO_DETECT_MIMETYPE,
            FeatureFlagsType.CORRECT_CLOCK_SKEW
        )!!
}
