package com.aliyun.kotlin.sdk.service.oss2.test

import com.aliyun.kotlin.sdk.service.oss2.exceptions.ServiceException
import com.aliyun.kotlin.sdk.service.oss2.extension.api.createBucketDataRedundancyTransition
import com.aliyun.kotlin.sdk.service.oss2.extension.api.getBucketDataRedundancyTransition
import com.aliyun.kotlin.sdk.service.oss2.extension.api.listBucketDataRedundancyTransition
import com.aliyun.kotlin.sdk.service.oss2.extension.api.listUserDataRedundancyTransition
import com.aliyun.kotlin.sdk.service.oss2.extension.api.putBucketCors
import com.aliyun.kotlin.sdk.service.oss2.extension.models.CORSConfiguration
import com.aliyun.kotlin.sdk.service.oss2.extension.models.CORSConfiguration.Companion.invoke
import com.aliyun.kotlin.sdk.service.oss2.extension.models.CreateBucketDataRedundancyTransitionRequest
import com.aliyun.kotlin.sdk.service.oss2.extension.models.GetBucketDataRedundancyTransitionRequest
import com.aliyun.kotlin.sdk.service.oss2.extension.models.ListBucketDataRedundancyTransitionRequest
import com.aliyun.kotlin.sdk.service.oss2.extension.models.ListUserDataRedundancyTransitionRequest
import com.aliyun.kotlin.sdk.service.oss2.extension.models.PutBucketCorsRequest
import com.aliyun.kotlin.sdk.service.oss2.extension.models.PutBucketCorsRequest.Companion.invoke
import com.aliyun.kotlin.sdk.service.oss2.models.DeleteBucketRequest
import com.aliyun.kotlin.sdk.service.oss2.models.PutBucketRequest
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BucketRedundancyTransitionTest: TestBase() {

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
    fun testCreateAndGetBucketDataRedundancyTransitionSuccess() = runTest {
        val createResult = defaultClient.createBucketDataRedundancyTransition(
            CreateBucketDataRedundancyTransitionRequest {
                bucket = bucketName
                targetRedundancyType = "ZRS"
            })
        assertEquals(200, createResult.statusCode)
        assertNotNull(createResult.bucketDataRedundancyTransition?.taskId)

        val getResult = defaultClient.getBucketDataRedundancyTransition(
            GetBucketDataRedundancyTransitionRequest {
                bucket = bucketName
                redundancyTransitionTaskid = createResult.bucketDataRedundancyTransition?.taskId
            })
        assertNotNull(getResult.bucketDataRedundancyTransition?.taskId)
        assertNotNull(getResult.bucketDataRedundancyTransition?.status)
        assertNotNull(getResult.bucketDataRedundancyTransition?.createTime)
    }

    @Test
    fun testCreateBucketDataRedundancyTransitionWithException() = runTest {
        var exception: Throwable = assertFailsWith<IllegalArgumentException> {
            defaultClient.createBucketDataRedundancyTransition(CreateBucketDataRedundancyTransitionRequest {})
        }
        assertEquals(exception.message, "request.bucket is required")

        exception = assertFailsWith<IllegalArgumentException> {
            defaultClient.createBucketDataRedundancyTransition(CreateBucketDataRedundancyTransitionRequest {
                bucket = bucketName
            })
        }
        assertEquals(exception.message, "request.targetRedundancyType is required")

        exception = assertFails {
            invalidClient.createBucketDataRedundancyTransition(CreateBucketDataRedundancyTransitionRequest {
                bucket = bucketName
                targetRedundancyType = "ZRS"
            })
        }
        assertTrue { exception.cause is ServiceException }
        assertEquals((exception.cause as ServiceException).statusCode, 403)
        assertEquals((exception.cause as ServiceException).errorCode, "InvalidAccessKeyId")
    }

    @Test
    fun testGetBucketDataRedundancyTransitionWithException() = runTest {
        var exception: Throwable = assertFailsWith<IllegalArgumentException> {
            defaultClient.getBucketDataRedundancyTransition(GetBucketDataRedundancyTransitionRequest {})
        }
        assertEquals(exception.message, "request.bucket is required")

        exception = assertFailsWith<IllegalArgumentException> {
            defaultClient.getBucketDataRedundancyTransition(GetBucketDataRedundancyTransitionRequest {
                bucket = bucketName
            })
        }
        assertEquals(exception.message, "request.redundancyTransitionTaskid is required")

        exception = assertFails {
            invalidClient.getBucketDataRedundancyTransition(GetBucketDataRedundancyTransitionRequest {
                bucket = bucketName
                redundancyTransitionTaskid = "xxx"
            })
        }
        assertTrue { exception.cause is ServiceException }
        assertEquals((exception.cause as ServiceException).statusCode, 403)
        assertEquals((exception.cause as ServiceException).errorCode, "InvalidAccessKeyId")
    }

    @Test
    fun testListUserDataRedundancyTransitionSuccess() = runTest {
        val createResult = defaultClient.createBucketDataRedundancyTransition(
            CreateBucketDataRedundancyTransitionRequest {
                bucket = bucketName
                targetRedundancyType = "ZRS"
            })
        val result = defaultClient.listUserDataRedundancyTransition(
            ListUserDataRedundancyTransitionRequest {
                bucket = bucketName
            })
        assertEquals(1, result.bucketDataRedundancyTransitions?.size)
        assertEquals(createResult.bucketDataRedundancyTransition?.taskId, result.bucketDataRedundancyTransitions?.get(0)?.taskId)
        assertNotNull(result.bucketDataRedundancyTransitions?.get(0)?.createTime)
        assertNotNull(result.bucketDataRedundancyTransitions?.get(0)?.status)
    }

    @Test
    fun testListUserDataRedundancyTransitionWithException() = runTest {
        var exception: Throwable = assertFailsWith<IllegalArgumentException> {
            defaultClient.listUserDataRedundancyTransition(ListUserDataRedundancyTransitionRequest {})
        }
        assertEquals(exception.message, "request.bucket is required")

        exception = assertFails {
            invalidClient.listUserDataRedundancyTransition(ListUserDataRedundancyTransitionRequest {
                bucket = bucketName
            })
        }
        assertTrue { exception.cause is ServiceException }
        assertEquals((exception.cause as ServiceException).statusCode, 403)
        assertEquals((exception.cause as ServiceException).errorCode, "InvalidAccessKeyId")
    }

    @Test
    fun testListBucketDataRedundancyTransitionSuccess() = runTest {
        val createResult = defaultClient.createBucketDataRedundancyTransition(
            CreateBucketDataRedundancyTransitionRequest {
                bucket = bucketName
                targetRedundancyType = "ZRS"
            })
        val result = defaultClient.listBucketDataRedundancyTransition(
            ListBucketDataRedundancyTransitionRequest {
                bucket = bucketName
            })
        assertEquals(1, result.listBucketDataRedundancyTransition?.bucketDataRedundancyTransitions?.size)
        assertEquals(createResult.bucketDataRedundancyTransition?.taskId, result.listBucketDataRedundancyTransition?.bucketDataRedundancyTransitions?.get(0)?.taskId)
        assertNotNull(result.listBucketDataRedundancyTransition?.bucketDataRedundancyTransitions?.get(0)?.createTime)
        assertNotNull(result.listBucketDataRedundancyTransition?.bucketDataRedundancyTransitions?.get(0)?.status)
    }

    @Test
    fun testListBucketDataRedundancyTransitionWithException() = runTest {
        var exception: Throwable = assertFailsWith<IllegalArgumentException> {
            defaultClient.listBucketDataRedundancyTransition(ListBucketDataRedundancyTransitionRequest {})
        }
        assertEquals(exception.message, "request.bucket is required")

        exception = assertFails {
            invalidClient.listBucketDataRedundancyTransition(ListBucketDataRedundancyTransitionRequest {
                bucket = bucketName
            })
        }
        assertTrue { exception.cause is ServiceException }
        assertEquals((exception.cause as ServiceException).statusCode, 403)
        assertEquals((exception.cause as ServiceException).errorCode, "InvalidAccessKeyId")
    }
}
