package com.aliyun.kotlin.sdk.service.oss2.test

import com.aliyun.kotlin.sdk.service.oss2.ClientConfiguration
import com.aliyun.kotlin.sdk.service.oss2.credentials.StaticCredentialsProvider
import com.aliyun.kotlin.sdk.service.oss2.exceptions.ServiceException
import com.aliyun.kotlin.sdk.service.oss2.models.ListBucketsRequest
import com.aliyun.kotlin.sdk.service.oss2.vectors.OSSVectorsClient
import com.aliyun.kotlin.sdk.service.oss2.vectors.models.DeleteVectorBucketRequest
import com.aliyun.kotlin.sdk.service.oss2.vectors.models.GetVectorBucketRequest
import com.aliyun.kotlin.sdk.service.oss2.vectors.models.ListVectorBucketsRequest
import com.aliyun.kotlin.sdk.service.oss2.vectors.models.PutVectorBucketRequest
import kotlinx.coroutines.test.runTest
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.time.Clock

class VectorBucketTest: TestBase() {

    val vectorClient: OSSVectorsClient by lazy (mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        OSSVectorsClient.create(ClientConfiguration.loadDefault().apply{
            region = OSS_TEST_REGION
            endpoint = OSS_TEST_ENDPOINT
            credentialsProvider = StaticCredentialsProvider(OSS_TEST_ACCESS_KEY_ID, OSS_TEST_ACCESS_KEY_SECRET)
            disableSsl = true
            accountId = OSS_TEST_RAM_UID
        })
    }

    val invalidVectorClient: OSSVectorsClient by lazy (mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        OSSVectorsClient.create(ClientConfiguration.loadDefault().apply{
            region = OSS_TEST_REGION
            endpoint = OSS_TEST_ENDPOINT
            credentialsProvider = StaticCredentialsProvider("ak", "sk")
            accountId = OSS_TEST_RAM_UID
        })
    }

    fun randomVectorBucketName(): String {
        val value = Random.nextInt(50000).toLong()
        return "$BUCKET_NAME_PREFIX$value"
    }

    @Test
    fun testPutAndDeleteVectorBucket() = runTest {
        val bucket = randomVectorBucketName()

        val result = vectorClient.putVectorBucket(PutVectorBucketRequest {
            this.bucket = bucket
        })
        assertEquals(result.statusCode, 200)

        vectorClient.deleteVectorBucket(DeleteVectorBucketRequest {
            this.bucket = bucket
        })
    }

    @Test
    fun testPutBucketWithException() = runTest {
        val bucket = randomVectorBucketName()

        var exception: Throwable = assertFailsWith<IllegalArgumentException> { invalidVectorClient.putVectorBucket(PutVectorBucketRequest {}) }
        assertEquals(exception.message, "request.bucket is required")

        exception = assertFails { invalidVectorClient.putVectorBucket(PutVectorBucketRequest {
            this.bucket = bucket
        }) }
        assertTrue { exception.cause is ServiceException }
        assertEquals((exception.cause as ServiceException).statusCode, 403)
        assertEquals((exception.cause as ServiceException).errorCode, "InvalidAccessKeyId")
    }

    @Test
    fun testDeleteVectorBucketWithException() = runTest {
        val bucket = randomVectorBucketName()

        var exception: Throwable = assertFailsWith<IllegalArgumentException> { invalidVectorClient.deleteVectorBucket(DeleteVectorBucketRequest {}) }
        assertEquals(exception.message, "request.bucket is required")

        exception = assertFails { vectorClient.deleteVectorBucket(DeleteVectorBucketRequest {
            this.bucket = bucket
        }) }
        assertTrue { exception.cause is ServiceException }
        assertEquals((exception.cause as ServiceException).statusCode, 404)
        assertEquals((exception.cause as ServiceException).errorCode, "NoSuchBucket")
    }

    @Test
    fun testGetVectorBucket() = runTest {
        val bucket = randomVectorBucketName()

        vectorClient.putVectorBucket(PutVectorBucketRequest {
            this.bucket = bucket
            acl = "private"
        })

        val result = vectorClient.getVectorBucket(GetVectorBucketRequest {
            this.bucket = bucket
        })
        assertEquals(result.statusCode, 200)
        assertEquals("acs:ossvector:$OSS_TEST_REGION:$OSS_TEST_RAM_UID:$bucket", result.bucketInfo?.name)
        assertNotNull(result.bucketInfo?.creationDate)
        assertEquals("oss-$OSS_TEST_REGION", result.bucketInfo?.location)
        assertNotNull(result.bucketInfo?.intranetEndpoint)
        assertNotNull(result.bucketInfo?.extranetEndpoint)
        assertEquals(OSS_TEST_REGION, result.bucketInfo?.region)

        vectorClient.deleteVectorBucket(DeleteVectorBucketRequest {
            this.bucket = bucket
        })
    }

    @Test
    fun testGetVectorBucketWithException() = runTest {
        val bucket = randomVectorBucketName()

        var exception: Throwable = assertFailsWith<IllegalArgumentException> { invalidVectorClient.getVectorBucket(GetVectorBucketRequest {}) }
        assertEquals(exception.message, "request.bucket is required")

        exception = assertFails { invalidVectorClient.getVectorBucket(GetVectorBucketRequest {
            this.bucket = bucket
        }) }
        assertTrue { exception.cause is ServiceException }
        assertEquals((exception.cause as ServiceException).statusCode, 404)
        assertEquals((exception.cause as ServiceException).errorCode, "NoSuchBucket")
    }

    @Test
    fun testListVectorBuckets() = runTest {
        val bucket = randomVectorBucketName()

        vectorClient.putVectorBucket(PutVectorBucketRequest {
            this.bucket = bucket
            acl = "private"
        })

        val result = vectorClient.listVectorBuckets(ListVectorBucketsRequest {})
        assertEquals(200, result.statusCode)
        assertEquals(1, result.buckets?.size)
        assertEquals(OSS_TEST_REGION, result.buckets?.get(0)?.region)
        assertEquals("acs:ossvector:$OSS_TEST_REGION:$OSS_TEST_RAM_UID:$bucket", result.buckets?.get(0)?.name)
        assertEquals("oss-$OSS_TEST_REGION", result.buckets?.get(0)?.location)
        assertNotNull(result.buckets?.get(0)?.creationDate)
        assertNotNull(result.buckets?.get(0)?.intranetEndpoint)
        assertNotNull(result.buckets?.get(0)?.extranetEndpoint)

        vectorClient.deleteVectorBucket(DeleteVectorBucketRequest {
            this.bucket = bucket
        })
    }

    @Test
    fun testListVectorBucketsWithException() = runTest {
        try {
            val result = invalidVectorClient.listVectorBuckets(
                ListVectorBucketsRequest{}
            )
            assertFails{"should not here"}
        } catch (e: Exception) {
            val se = ServiceException.asCause(e)
            assertNotNull(se)
            assertEquals(403, se.statusCode)
            assertEquals("InvalidAccessKeyId", se.errorCode)
        }
    }
}
