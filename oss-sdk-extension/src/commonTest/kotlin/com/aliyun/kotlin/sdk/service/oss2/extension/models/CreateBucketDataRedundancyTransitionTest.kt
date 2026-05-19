package com.aliyun.kotlin.sdk.service.oss2.extension.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CreateBucketDataRedundancyTransitionTest {
    @Test
    fun buildRequestWithEmptyValues() {
        val request = CreateBucketDataRedundancyTransitionRequest {}
        assertNull(request.bucket)
        assertNull(request.targetRedundancyType)

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
        val request = CreateBucketDataRedundancyTransitionRequest {
            bucket = "bucket"
            targetRedundancyType = "ZRS"
        }

        assertEquals("bucket", request.bucket)
        assertEquals("ZRS", request.targetRedundancyType)

        assertNotNull(request.headers)
        assertTrue {
            request.headers.isEmpty()
        }
        assertNotNull(request.parameters)
        assertTrue {
            request.parameters.contains("x-oss-target-redundancy-type")
        }
    }

    @Test
    fun buildRequestFromBuilder() {
        val builder = CreateBucketDataRedundancyTransitionRequest.Builder()
        builder.bucket = "bucket"
        builder.targetRedundancyType = "ZRS"

        val request = CreateBucketDataRedundancyTransitionRequest(builder)
        assertEquals("bucket", request.bucket)
        assertEquals("ZRS", request.targetRedundancyType)

        assertNotNull(request.headers)
        assertTrue {
            request.headers.isEmpty()
        }
        assertNotNull(request.parameters)
        assertTrue {
            request.parameters.contains("x-oss-target-redundancy-type")
        }
    }

    @Test
    fun buildResultWithEmptyValues() {
        val result = CreateBucketDataRedundancyTransitionResult {}
        assertEquals(0, result.statusCode)
        assertEquals("", result.status)
        assertEquals("", result.requestId)
        assertNull(result.bucketDataRedundancyTransition)

        assertNotNull(result.headers)
        assertTrue {
            result.headers.isEmpty()
        }
    }

    @Test
    fun buildResultWithFullValuesFromDsl() {
        val result = CreateBucketDataRedundancyTransitionResult {
            status = "OK"
            statusCode = 200
            headers = mutableMapOf("x-oss-request-id" to "id-123")
            innerBody = BucketDataRedundancyTransition {
                taskId = "4be5beb0f74f490186311b268bf6****"
            }
        }
        assertEquals(200, result.statusCode)
        assertEquals("OK", result.status)
        assertEquals("id-123", result.requestId)
        assertEquals("4be5beb0f74f490186311b268bf6****", result.bucketDataRedundancyTransition?.taskId)

        assertNotNull(result.headers)
        assertEquals(1, result.headers.size)
        assertEquals("id-123", result.headers["x-oss-request-id"])
    }

    @Test
    fun buildResultFromBuilder() {
        val builder = CreateBucketDataRedundancyTransitionResult.Builder()
        builder.status = "OK"
        builder.statusCode = 200
        builder.headers = mutableMapOf("x-oss-request-id" to "id-123")
        builder.innerBody = BucketDataRedundancyTransition {
            taskId = "4be5beb0f74f490186311b268bf6****"
        }

        val result = CreateBucketDataRedundancyTransitionResult(builder)
        assertEquals(200, result.statusCode)
        assertEquals("OK", result.status)
        assertEquals("id-123", result.requestId)
        assertEquals("4be5beb0f74f490186311b268bf6****", result.bucketDataRedundancyTransition?.taskId)

        assertNotNull(result.headers)
        assertEquals(1, result.headers.size)
        assertEquals("id-123", result.headers["x-oss-request-id"])
    }
}
