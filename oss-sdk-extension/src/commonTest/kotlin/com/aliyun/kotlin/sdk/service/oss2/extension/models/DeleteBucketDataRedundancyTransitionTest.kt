package com.aliyun.kotlin.sdk.service.oss2.extension.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DeleteBucketDataRedundancyTransitionTest {
    @Test
    fun buildRequestWithEmptyValues() {
        val request = DeleteBucketDataRedundancyTransitionRequest {}
        assertNull(request.bucket)
        assertNull(request.redundancyTransitionTaskid)

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
        val request = DeleteBucketDataRedundancyTransitionRequest {
            bucket = "bucket"
            redundancyTransitionTaskid = "4be5beb0f74f490186311b268bf6****"
        }

        assertEquals("bucket", request.bucket)

        assertNotNull(request.headers)
        assertTrue {
            request.headers.isEmpty()
        }
        assertNotNull(request.parameters)
        assertTrue {
            request.parameters.contains("x-oss-redundancy-transition-taskid")
        }
    }

    @Test
    fun buildRequestFromBuilder() {
        val builder = DeleteBucketDataRedundancyTransitionRequest.Builder()
        builder.bucket = "bucket"
        builder.redundancyTransitionTaskid = "4be5beb0f74f490186311b268bf6****"

        val request = DeleteBucketDataRedundancyTransitionRequest(builder)
        assertEquals("bucket", request.bucket)

        assertNotNull(request.headers)
        assertTrue {
            request.headers.isEmpty()
        }
        assertNotNull(request.parameters)
        assertTrue {
            request.parameters.contains("x-oss-redundancy-transition-taskid")
        }
    }

    @Test
    fun buildResultWithEmptyValues() {
        val result = DeleteBucketDataRedundancyTransitionResult {}
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
        val result = DeleteBucketDataRedundancyTransitionResult {
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
        val builder = DeleteBucketDataRedundancyTransitionResult.Builder()
        builder.status = "OK"
        builder.statusCode = 200
        builder.headers = mutableMapOf("x-oss-request-id" to "id-123")

        val result = DeleteBucketDataRedundancyTransitionResult(builder)
        assertEquals(200, result.statusCode)
        assertEquals("OK", result.status)
        assertEquals("id-123", result.requestId)

        assertNotNull(result.headers)
        assertEquals(1, result.headers.size)
        assertEquals("id-123", result.headers["x-oss-request-id"])
    }
}
