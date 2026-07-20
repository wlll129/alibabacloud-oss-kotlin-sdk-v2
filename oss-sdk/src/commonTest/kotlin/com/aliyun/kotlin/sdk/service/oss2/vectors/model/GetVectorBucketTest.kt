package com.aliyun.kotlin.sdk.service.oss2.vectors.model

import com.aliyun.kotlin.sdk.service.oss2.vectors.models.BucketInfo
import com.aliyun.kotlin.sdk.service.oss2.vectors.models.GetVectorBucketRequest
import com.aliyun.kotlin.sdk.service.oss2.vectors.models.GetVectorBucketResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.text.get

class GetVectorBucketTest {

    @Test
    fun buildRequestWithEmptyValues() {
        val request = GetVectorBucketRequest {}
        assertNull(request.bucket)

        assertNotNull(request.headers)
        assertTrue {
            request.headers.isEmpty()
        }
        assertNotNull(request.parameters)
        assertTrue {
            request.parameters.isEmpty()
        }
    }

    @Test
    fun buildRequestWithFullValuesFromDsl() {
        val request = GetVectorBucketRequest {
            bucket = "my-bucket"
        }

        assertEquals("my-bucket", request.bucket)

        assertNotNull(request.headers)
        assertTrue {
            request.headers.isEmpty()
        }
        assertNotNull(request.parameters)
        assertTrue {
            request.parameters.isEmpty()
        }
    }

    @Test
    fun buildRequestFromBuilder() {
        val builder = GetVectorBucketRequest.Builder()
        builder.bucket = "my-bucket"

        val request = GetVectorBucketRequest(builder)
        assertEquals("my-bucket", request.bucket)

        assertNotNull(request.headers)
        assertTrue {
            request.headers.isEmpty()
        }
        assertNotNull(request.parameters)
        assertTrue {
            request.parameters.isEmpty()
        }
    }

    @Test
    fun buildResultWithEmptyValues() {
        val result = GetVectorBucketResult {}
        assertEquals(0, result.statusCode)
        assertEquals("", result.status)
        assertEquals("", result.requestId)
        assertNull(result.bucketInfo)

        assertNotNull(result.headers)
        assertTrue {
            result.headers.isEmpty()
        }
    }

    @Test
    fun buildResultWithFullValuesFromDsl() {
        val result = GetVectorBucketResult {
            status = "OK"
            statusCode = 200
            headers = mutableMapOf("x-oss-request-id" to "id-123")
            innerBody = BucketInfo {
                intranetEndpoint = "oss-cn-hangzhou-internal.aliyuncs.com"
                location = "oss-cn-hangzhou"
                creationDate = "2013-07-31T10:56:21.000Z"
                extranetEndpoint = "oss-cn-hangzhou.aliyuncs.com"
                name = "oss-example"
                region = "cn-hangzhou"
            }
        }
        assertEquals(200, result.statusCode)
        assertEquals("OK", result.status)
        assertEquals("id-123", result.requestId)
        assertEquals("oss-cn-hangzhou-internal.aliyuncs.com", result.bucketInfo?.intranetEndpoint)
        assertEquals("oss-cn-hangzhou", result.bucketInfo?.location)
        assertEquals("2013-07-31T10:56:21.000Z", result.bucketInfo?.creationDate)
        assertEquals("oss-cn-hangzhou.aliyuncs.com", result.bucketInfo?.extranetEndpoint)
        assertEquals("oss-example", result.bucketInfo?.name)
        assertEquals("cn-hangzhou", result.bucketInfo?.region)

        assertNotNull(result.headers)
        assertEquals(1, result.headers.size)
        assertEquals("id-123", result.headers["x-oss-request-id"])
    }

    @Test
    fun buildResultFromBuilder() {
        val builder = GetVectorBucketResult.Builder()
        builder.status = "OK"
        builder.statusCode = 200
        builder.headers = mutableMapOf("x-oss-request-id" to "id-123")
        builder.innerBody = BucketInfo {
            intranetEndpoint = "oss-cn-hangzhou-internal.aliyuncs.com"
            location = "oss-cn-hangzhou"
            creationDate = "2013-07-31T10:56:21.000Z"
            extranetEndpoint = "oss-cn-hangzhou.aliyuncs.com"
            name = "oss-example"
            region = "cn-hangzhou"
        }

        val result = GetVectorBucketResult(builder)
        assertEquals(200, result.statusCode)
        assertEquals("OK", result.status)
        assertEquals("id-123", result.requestId)
        assertEquals("oss-cn-hangzhou-internal.aliyuncs.com", result.bucketInfo?.intranetEndpoint)
        assertEquals("oss-cn-hangzhou", result.bucketInfo?.location)
        assertEquals("2013-07-31T10:56:21.000Z", result.bucketInfo?.creationDate)
        assertEquals("oss-cn-hangzhou.aliyuncs.com", result.bucketInfo?.extranetEndpoint)
        assertEquals("oss-example", result.bucketInfo?.name)
        assertEquals("cn-hangzhou", result.bucketInfo?.region)

        assertNotNull(result.headers)
        assertEquals(1, result.headers.size)
        assertEquals("id-123", result.headers["x-oss-request-id"])
    }
}
