package com.aliyun.kotlin.sdk.service.oss2.test

import com.aliyun.kotlin.sdk.service.oss2.exceptions.ServiceException
import com.aliyun.kotlin.sdk.service.oss2.extension.api.createCnameToken
import com.aliyun.kotlin.sdk.service.oss2.extension.api.getCnameToken
import com.aliyun.kotlin.sdk.service.oss2.extension.api.putBucketCors
import com.aliyun.kotlin.sdk.service.oss2.extension.models.BucketCnameConfiguration
import com.aliyun.kotlin.sdk.service.oss2.extension.models.CORSConfiguration
import com.aliyun.kotlin.sdk.service.oss2.extension.models.CORSConfiguration.Companion.invoke
import com.aliyun.kotlin.sdk.service.oss2.extension.models.Cname
import com.aliyun.kotlin.sdk.service.oss2.extension.models.CreateCnameTokenRequest
import com.aliyun.kotlin.sdk.service.oss2.extension.models.GetCnameTokenRequest
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

class BucketCnameTest: TestBase() {

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
    fun testCreatAndGetBucketCname() = runTest {
        val result = defaultClient.createCnameToken(CreateCnameTokenRequest {
            bucket = bucketName
            bucketCnameConfiguration = BucketCnameConfiguration {
                cname = Cname {
                    domain = "example.com"
                }
            }
        })
        assertEquals(200, result.statusCode)
        assertEquals(bucketName, result.cnameToken?.bucket)
        assertNotNull(result.cnameToken?.cname)
        assertNotNull(result.cnameToken?.token)
        assertNotNull(result.cnameToken?.expireTime)

        val getResult = defaultClient.getCnameToken(GetCnameTokenRequest {
            bucket = bucketName
            cname = result.cnameToken?.cname
        })
        assertEquals(200, getResult.statusCode)
        assertEquals(result.cnameToken?.bucket, getResult.cnameToken?.bucket)
        assertEquals(result.cnameToken?.cname, getResult.cnameToken?.cname)
        assertEquals(result.cnameToken?.token, getResult.cnameToken?.token)
        assertEquals(result.cnameToken?.expireTime, getResult.cnameToken?.expireTime)
    }

    @Test
    fun testCreateCnameTokenWithException() = runTest {
        var exception: Throwable = assertFailsWith<IllegalArgumentException> {
            defaultClient.createCnameToken(CreateCnameTokenRequest {})
        }
        assertEquals(exception.message, "request.bucket is required")

        exception = assertFailsWith<IllegalArgumentException> {
            defaultClient.createCnameToken(CreateCnameTokenRequest {
                bucket = bucketName
            })
        }
        assertEquals(exception.message, "request.bucketCnameConfiguration is required")

        exception = assertFails {
            invalidClient.createCnameToken(CreateCnameTokenRequest {
                bucket = bucketName
                bucketCnameConfiguration = BucketCnameConfiguration{}
            })
        }
        assertTrue { exception.cause is ServiceException }
        assertEquals((exception.cause as ServiceException).statusCode, 403)
        assertEquals((exception.cause as ServiceException).errorCode, "InvalidAccessKeyId")
    }

    @Test
    fun testGetCnameTokenWithException() = runTest {
        var exception: Throwable = assertFailsWith<IllegalArgumentException> {
            defaultClient.getCnameToken(GetCnameTokenRequest {})
        }
        assertEquals(exception.message, "request.bucket is required")

        exception = assertFailsWith<IllegalArgumentException> {
            defaultClient.getCnameToken(GetCnameTokenRequest {
                bucket = bucketName
            })
        }
        assertEquals(exception.message, "request.cname is required")

        exception = assertFails {
            invalidClient.getCnameToken(GetCnameTokenRequest {
                bucket = bucketName
                cname = "cname"
            })
        }
        assertTrue { exception.cause is ServiceException }
        assertEquals((exception.cause as ServiceException).statusCode, 403)
        assertEquals((exception.cause as ServiceException).errorCode, "InvalidAccessKeyId")
    }
}
