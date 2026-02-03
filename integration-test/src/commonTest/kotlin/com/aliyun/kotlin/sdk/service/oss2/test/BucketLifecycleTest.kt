package com.aliyun.kotlin.sdk.service.oss2.test

import com.aliyun.kotlin.sdk.service.oss2.exceptions.ServiceException
import com.aliyun.kotlin.sdk.service.oss2.extension.api.deleteBucketLifecycle
import com.aliyun.kotlin.sdk.service.oss2.extension.api.getBucketLifecycle
import com.aliyun.kotlin.sdk.service.oss2.extension.api.putBucketLifecycle
import com.aliyun.kotlin.sdk.service.oss2.extension.models.AbortMultipartUpload
import com.aliyun.kotlin.sdk.service.oss2.extension.models.DeleteBucketLifecycleRequest
import com.aliyun.kotlin.sdk.service.oss2.extension.models.Expiration
import com.aliyun.kotlin.sdk.service.oss2.extension.models.GetBucketLifecycleRequest
import com.aliyun.kotlin.sdk.service.oss2.extension.models.LifecycleConfiguration
import com.aliyun.kotlin.sdk.service.oss2.extension.models.LifecycleRule
import com.aliyun.kotlin.sdk.service.oss2.extension.models.NoncurrentVersionExpiration
import com.aliyun.kotlin.sdk.service.oss2.extension.models.NoncurrentVersionTransition
import com.aliyun.kotlin.sdk.service.oss2.extension.models.PutBucketLifecycleRequest
import com.aliyun.kotlin.sdk.service.oss2.extension.models.Transition
import com.aliyun.kotlin.sdk.service.oss2.models.DeleteBucketRequest
import com.aliyun.kotlin.sdk.service.oss2.models.PutBucketRequest
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BucketLifecycleTest: TestBase() {

    val bucketName: String = randomBucketName()

    @BeforeTest
    fun putBucket() = runTest {
        defaultClient.putBucket(PutBucketRequest {
            bucket = bucketName
        })
    }

    @AfterTest
    fun cleanAndDeleteBucket() = runTest {
        defaultClient.deleteBucket(DeleteBucketRequest {
            bucket = bucketName
        })
    }

    @Test
    fun testPutAndGetBucketLifecycle() = runTest {
        val configuration = LifecycleConfiguration {
            rules = listOf(
                LifecycleRule{
                    status = "Enabled"
                    expiration = Expiration {
                        days = 180
                    }
                    abortMultipartUpload = AbortMultipartUpload {
                        days = 30
                    }
                    prefix = "p1"
                    transitions = listOf(
                        Transition {
                            storageClass = "IA"
                            days = 30
                        }
                    )
                    noncurrentVersionExpiration = NoncurrentVersionExpiration {
                        noncurrentDays = 180
                    }
                    noncurrentVersionTransitions = listOf(
                        NoncurrentVersionTransition {
                            noncurrentDays = 30
                            storageClass = "IA"
                        }
                    )
                    id = "id1"
                },
                LifecycleRule{
                    status = "Enabled"
                    expiration = Expiration {
                        createdBeforeDate = "2025-04-05T00:00:00.000Z"
                    }
                    abortMultipartUpload = AbortMultipartUpload {
                        createdBeforeDate = "2025-04-05T00:00:00.000Z"
                    }
                    prefix = "p2"
                    transitions = listOf(
                        Transition {
                            storageClass = "IA"
                            createdBeforeDate = "2025-05-05T00:00:00.000Z"
                        }
                    )
                    noncurrentVersionExpiration = NoncurrentVersionExpiration {
                        noncurrentDays = 180
                    }
                    noncurrentVersionTransitions = listOf(
                        NoncurrentVersionTransition {
                            noncurrentDays = 30
                            storageClass = "IA"
                        }
                    )
                    id = "id2"
                }
            )
        }
        val putResult = defaultClient.putBucketLifecycle(PutBucketLifecycleRequest {
            bucket = bucketName
            lifecycleConfiguration = configuration
        })
        assertEquals(200, putResult.statusCode)

        val result = defaultClient.getBucketLifecycle(GetBucketLifecycleRequest {
            bucket = bucketName
        })
        assertEquals(200, result.statusCode)
        assertEquals(2, result.lifecycleConfiguration?.rules?.size)
        assertEquals(configuration.rules?.get(0)?.id, result.lifecycleConfiguration?.rules?.get(0)?.id)
        assertEquals(configuration.rules?.get(0)?.expiration?.days, result.lifecycleConfiguration?.rules?.get(0)?.expiration?.days)
        assertEquals(configuration.rules?.get(0)?.expiration?.expiredObjectDeleteMarker, result.lifecycleConfiguration?.rules?.get(0)?.expiration?.expiredObjectDeleteMarker)
        assertEquals(configuration.rules?.get(0)?.abortMultipartUpload?.days, result.lifecycleConfiguration?.rules?.get(0)?.abortMultipartUpload?.days)
        assertNull(result.lifecycleConfiguration?.rules?.get(0)?.tags)
        assertNull(result.lifecycleConfiguration?.rules?.get(0)?.filter)
        assertEquals(configuration.rules?.get(0)?.prefix, result.lifecycleConfiguration?.rules?.get(0)?.prefix)
        assertEquals(1, result.lifecycleConfiguration?.rules?.get(0)?.transitions?.size)
        assertEquals(configuration.rules?.get(0)?.transitions?.get(0)?.days, result.lifecycleConfiguration?.rules?.get(0)?.transitions?.get(0)?.days)
        assertEquals(configuration.rules?.get(0)?.transitions?.get(0)?.returnToStdWhenVisit, result.lifecycleConfiguration?.rules?.get(0)?.transitions?.get(0)?.returnToStdWhenVisit)
        assertEquals(configuration.rules?.get(0)?.transitions?.get(0)?.isAccessTime, result.lifecycleConfiguration?.rules?.get(0)?.transitions?.get(0)?.isAccessTime)
        assertEquals(configuration.rules?.get(0)?.transitions?.get(0)?.storageClass, result.lifecycleConfiguration?.rules?.get(0)?.transitions?.get(0)?.storageClass)
        assertEquals(configuration.rules?.get(0)?.transitions?.get(0)?.returnToStdWhenVisit, result.lifecycleConfiguration?.rules?.get(0)?.transitions?.get(0)?.returnToStdWhenVisit)
        assertEquals(configuration.rules?.get(0)?.noncurrentVersionExpiration?.noncurrentDays, result.lifecycleConfiguration?.rules?.get(0)?.noncurrentVersionExpiration?.noncurrentDays)
        assertEquals(1, result.lifecycleConfiguration?.rules?.get(0)?.noncurrentVersionTransitions?.size)
        assertEquals(configuration.rules?.get(0)?.noncurrentVersionTransitions?.get(0)?.returnToStdWhenVisit, result.lifecycleConfiguration?.rules?.get(0)?.noncurrentVersionTransitions?.get(0)?.returnToStdWhenVisit)
        assertEquals(configuration.rules?.get(0)?.noncurrentVersionTransitions?.get(0)?.noncurrentDays, result.lifecycleConfiguration?.rules?.get(0)?.noncurrentVersionTransitions?.get(0)?.noncurrentDays)
        assertEquals(configuration.rules?.get(0)?.noncurrentVersionTransitions?.get(0)?.storageClass, result.lifecycleConfiguration?.rules?.get(0)?.noncurrentVersionTransitions?.get(0)?.storageClass)
        assertEquals(configuration.rules?.get(0)?.noncurrentVersionTransitions?.get(0)?.isAccessTime, result.lifecycleConfiguration?.rules?.get(0)?.noncurrentVersionTransitions?.get(0)?.isAccessTime)
        assertEquals(configuration.rules?.get(0)?.noncurrentVersionTransitions?.get(0)?.allowSmallFile, result.lifecycleConfiguration?.rules?.get(0)?.noncurrentVersionTransitions?.get(0)?.allowSmallFile)
        assertEquals(configuration.rules?.get(0)?.atimeBase, result.lifecycleConfiguration?.rules?.get(0)?.atimeBase)
        assertEquals(configuration.rules?.get(1)?.expiration?.createdBeforeDate, result.lifecycleConfiguration?.rules?.get(1)?.expiration?.createdBeforeDate)
        assertEquals(configuration.rules?.get(1)?.expiration?.expiredObjectDeleteMarker, result.lifecycleConfiguration?.rules?.get(1)?.expiration?.expiredObjectDeleteMarker)
        assertEquals(configuration.rules?.get(1)?.abortMultipartUpload?.createdBeforeDate, result.lifecycleConfiguration?.rules?.get(1)?.abortMultipartUpload?.createdBeforeDate)
        assertNull(result.lifecycleConfiguration?.rules?.get(1)?.tags)
        assertNull(result.lifecycleConfiguration?.rules?.get(1)?.filter)
        assertEquals(configuration.rules?.get(1)?.prefix, result.lifecycleConfiguration?.rules?.get(1)?.prefix)
        assertEquals(1, result.lifecycleConfiguration?.rules?.get(1)?.transitions?.size)
        assertEquals(configuration.rules?.get(1)?.transitions?.get(0)?.createdBeforeDate, result.lifecycleConfiguration?.rules?.get(1)?.transitions?.get(0)?.createdBeforeDate)
        assertEquals(configuration.rules?.get(1)?.transitions?.get(0)?.returnToStdWhenVisit, result.lifecycleConfiguration?.rules?.get(1)?.transitions?.get(0)?.returnToStdWhenVisit)
        assertEquals(configuration.rules?.get(1)?.transitions?.get(0)?.isAccessTime, result.lifecycleConfiguration?.rules?.get(1)?.transitions?.get(0)?.isAccessTime)
        assertEquals(configuration.rules?.get(1)?.transitions?.get(0)?.storageClass, result.lifecycleConfiguration?.rules?.get(1)?.transitions?.get(0)?.storageClass)
        assertEquals(configuration.rules?.get(1)?.transitions?.get(0)?.returnToStdWhenVisit, result.lifecycleConfiguration?.rules?.get(1)?.transitions?.get(0)?.returnToStdWhenVisit)
        assertEquals(configuration.rules?.get(1)?.noncurrentVersionExpiration?.noncurrentDays, result.lifecycleConfiguration?.rules?.get(1)?.noncurrentVersionExpiration?.noncurrentDays)
        assertEquals(1, result.lifecycleConfiguration?.rules?.get(1)?.noncurrentVersionTransitions?.size)
        assertEquals(configuration.rules?.get(1)?.noncurrentVersionTransitions?.get(0)?.returnToStdWhenVisit, result.lifecycleConfiguration?.rules?.get(1)?.noncurrentVersionTransitions?.get(0)?.returnToStdWhenVisit)
        assertEquals(configuration.rules?.get(1)?.noncurrentVersionTransitions?.get(0)?.noncurrentDays, result.lifecycleConfiguration?.rules?.get(1)?.noncurrentVersionTransitions?.get(0)?.noncurrentDays)
        assertEquals(configuration.rules?.get(1)?.noncurrentVersionTransitions?.get(0)?.storageClass, result.lifecycleConfiguration?.rules?.get(1)?.noncurrentVersionTransitions?.get(0)?.storageClass)
        assertEquals(configuration.rules?.get(1)?.noncurrentVersionTransitions?.get(0)?.isAccessTime, result.lifecycleConfiguration?.rules?.get(1)?.noncurrentVersionTransitions?.get(0)?.isAccessTime)
        assertEquals(configuration.rules?.get(1)?.noncurrentVersionTransitions?.get(0)?.allowSmallFile, result.lifecycleConfiguration?.rules?.get(1)?.noncurrentVersionTransitions?.get(0)?.allowSmallFile)
        assertEquals(configuration.rules?.get(1)?.atimeBase, result.lifecycleConfiguration?.rules?.get(1)?.atimeBase)
    }

    @Test
    fun testPutBucketCorsWithException() = runTest {
        var exception: Throwable = assertFailsWith<IllegalArgumentException> {
            defaultClient.putBucketLifecycle(PutBucketLifecycleRequest {})
        }
        assertEquals(exception.message, "request.bucket is required")

        exception = assertFailsWith<IllegalArgumentException> {
            defaultClient.putBucketLifecycle(PutBucketLifecycleRequest {
                bucket = bucketName
            })
        }
        assertEquals(exception.message, "request.lifecycleConfiguration is required")

        exception = assertFails {
            invalidClient.putBucketLifecycle(PutBucketLifecycleRequest {
                bucket = bucketName
                lifecycleConfiguration = LifecycleConfiguration{}
            })
        }
        assertTrue { exception.cause is ServiceException }
        assertEquals((exception.cause as ServiceException).statusCode, 403)
        assertEquals((exception.cause as ServiceException).errorCode, "InvalidAccessKeyId")
    }

    @Test
    fun testGetBucketCorsWithException() = runTest {
        var exception: Throwable = assertFailsWith<IllegalArgumentException> {
            defaultClient.getBucketLifecycle(GetBucketLifecycleRequest {})
        }
        assertEquals(exception.message, "request.bucket is required")

        exception = assertFails {
            invalidClient.getBucketLifecycle(GetBucketLifecycleRequest {
                bucket = bucketName
            })
        }
        assertTrue { exception.cause is ServiceException }
        assertEquals((exception.cause as ServiceException).statusCode, 403)
        assertEquals((exception.cause as ServiceException).errorCode, "InvalidAccessKeyId")
    }

    @Test
    fun testDeleteAndGetBucketLifecycle() = runTest {
        val configuration = LifecycleConfiguration {
            rules = listOf(
                LifecycleRule{
                    status = "Enabled"
                    expiration = Expiration {
                        days = 180
                    }
                    abortMultipartUpload = AbortMultipartUpload {
                        days = 30
                    }
                    prefix = "p1"
                    transitions = listOf(
                        Transition {
                            storageClass = "IA"
                            days = 30
                        }
                    )
                    noncurrentVersionExpiration = NoncurrentVersionExpiration {
                        noncurrentDays = 180
                    }
                    noncurrentVersionTransitions = listOf(
                        NoncurrentVersionTransition {
                            noncurrentDays = 30
                            storageClass = "IA"
                        }
                    )
                    id = "id1"
                },
                LifecycleRule{
                    status = "Enabled"
                    expiration = Expiration {
                        createdBeforeDate = "2025-04-05T00:00:00.000Z"
                    }
                    abortMultipartUpload = AbortMultipartUpload {
                        createdBeforeDate = "2025-04-05T00:00:00.000Z"
                    }
                    prefix = "p2"
                    transitions = listOf(
                        Transition {
                            storageClass = "IA"
                            createdBeforeDate = "2025-05-05T00:00:00.000Z"
                        }
                    )
                    noncurrentVersionExpiration = NoncurrentVersionExpiration {
                        noncurrentDays = 180
                    }
                    noncurrentVersionTransitions = listOf(
                        NoncurrentVersionTransition {
                            noncurrentDays = 30
                            storageClass = "IA"
                        }
                    )
                    id = "id2"
                }
            )
        }
        val putResult = defaultClient.putBucketLifecycle(PutBucketLifecycleRequest {
            bucket = bucketName
            lifecycleConfiguration = configuration
        })
        assertEquals(200, putResult.statusCode)

        val result = defaultClient.deleteBucketLifecycle(DeleteBucketLifecycleRequest {
            bucket = bucketName
        })
        assertEquals(204, result.statusCode)
    }

    @Test
    fun testDeleteBucketCorsWithException() = runTest {
        var exception: Throwable = assertFailsWith<IllegalArgumentException> {
            defaultClient.deleteBucketLifecycle(DeleteBucketLifecycleRequest {})
        }
        assertEquals(exception.message, "request.bucket is required")

        exception = assertFails {
            invalidClient.deleteBucketLifecycle(DeleteBucketLifecycleRequest {
                bucket = bucketName
            })
        }
        assertTrue { exception.cause is ServiceException }
        assertEquals((exception.cause as ServiceException).statusCode, 403)
        assertEquals((exception.cause as ServiceException).errorCode, "InvalidAccessKeyId")
    }
}
