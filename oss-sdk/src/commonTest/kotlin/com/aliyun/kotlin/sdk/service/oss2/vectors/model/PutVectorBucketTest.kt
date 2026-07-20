package com.aliyun.kotlin.sdk.service.oss2.vectors.model

import com.aliyun.kotlin.sdk.service.oss2.vectors.models.PutVectorBucketRequest
import com.aliyun.kotlin.sdk.service.oss2.vectors.models.PutVectorBucketResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PutVectorBucketTest {

    @Test
    fun buildRequestWithEmptyValues() {
        val request = PutVectorBucketRequest {}
        assertNull(request.bucket)
        assertNull(request.acl)
        assertNull(request.resourceGroupId)
        assertNull(request.bucketTagging)

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
        val request = PutVectorBucketRequest {
            bucket = "my-bucket"
            acl = "private"
            resourceGroupId = "resourceGroupId1"
            bucketTagging = "A=B"
        }

        assertEquals("my-bucket", request.bucket)
        assertEquals("private", request.acl)
        assertEquals("resourceGroupId1", request.resourceGroupId)
        assertEquals("A=B", request.bucketTagging)

        assertNotNull(request.headers)
        assertTrue {
            request.headers.containsKey("x-oss-acl")
            request.headers.containsKey("x-oss-resource-group-id")
            request.headers.containsKey("x-oss-bucket-tagging")
        }
        assertNotNull(request.parameters)
        assertTrue {
            request.parameters.isEmpty()
        }
    }

    @Test
    fun buildRequestFromBuilder() {
        val builder = PutVectorBucketRequest.Builder()
        builder.bucket = "my-bucket"
        builder.acl = "private"
        builder.resourceGroupId = "resourceGroupId1"
        builder.bucketTagging = "A=B"

        val request = PutVectorBucketRequest(builder)
        assertEquals("my-bucket", request.bucket)
        assertEquals("private", request.acl)
        assertEquals("resourceGroupId1", request.resourceGroupId)
        assertEquals("A=B", request.bucketTagging)

        assertNotNull(request.headers)
        assertTrue {
            request.headers.containsKey("x-oss-acl")
            request.headers.containsKey("x-oss-resource-group-id")
            request.headers.containsKey("x-oss-bucket-tagging")
        }
        assertNotNull(request.parameters)
        assertTrue {
            request.parameters.isEmpty()
        }
    }

    @Test
    fun buildResultWithEmptyValues() {
        val result = PutVectorBucketResult {}
        assertEquals(0, result.statusCode)
        assertEquals("", result.status)
        assertEquals("", result.requestId)

        assertNotNull(result.headers)
        assertTrue {
            result.headers.isEmpty()
        }
    }

    @Test
    fun buildResultWithFullValuesFromDsl() {
        val result = PutVectorBucketResult {
            status = "OK"
            statusCode = 200
            headers = mutableMapOf("x-oss-request-id" to "id-123")
            innerBody = null
        }
        assertEquals(200, result.statusCode)
        assertEquals("OK", result.status)
        assertEquals("id-123", result.requestId)

        assertNotNull(result.headers)
        assertEquals(1, result.headers.size)
        assertEquals("id-123", result.headers["x-oss-request-id"])
    }

    @Test
    fun buildResultFromBuilder() {
        val builder = PutVectorBucketResult.Builder()
        builder.status = "OK"
        builder.statusCode = 200
        builder.headers = mutableMapOf("x-oss-request-id" to "id-123")

        val result = PutVectorBucketResult(builder)
        assertEquals(200, result.statusCode)
        assertEquals("OK", result.status)
        assertEquals("id-123", result.requestId)

        assertNotNull(result.headers)
        assertEquals(1, result.headers.size)
        assertEquals("id-123", result.headers["x-oss-request-id"])
    }
}
