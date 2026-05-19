package com.aliyun.kotlin.sdk.service.oss2.extension.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GetBucketDataRedundancyTransitionTest {
    @Test
    fun buildRequestWithEmptyValues() {
        val request = GetBucketDataRedundancyTransitionRequest {}
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
        val request = GetBucketDataRedundancyTransitionRequest {
            bucket = "bucket"
            redundancyTransitionTaskid = "909c6c818dd041d1a44e0fdc66aa****"
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
        val builder = GetBucketDataRedundancyTransitionRequest.Builder()
        builder.bucket = "bucket"
        builder.redundancyTransitionTaskid = "909c6c818dd041d1a44e0fdc66aa****"

        val request = GetBucketDataRedundancyTransitionRequest(builder)
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
        val result = GetBucketDataRedundancyTransitionResult {}
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
        val bucketDataRedundancyTransition = BucketDataRedundancyTransition {
            taskId = "909c6c818dd041d1a44e0fdc66aa****"
            createTime = "2023-11-17T09:14:39.000Z"
            startTime = "2023-11-17T09:14:39.000Z"
            endTime = "2023-11-18T09:14:39.000Z"
            status = "Finished"
            estimatedRemainingTime = "100"
            processPercentage = "0"
        }
        val result = GetBucketDataRedundancyTransitionResult {
            status = "OK"
            statusCode = 200
            headers = mutableMapOf("x-oss-request-id" to "id-123")
            innerBody = bucketDataRedundancyTransition
        }
        assertEquals(200, result.statusCode)
        assertEquals("OK", result.status)
        assertEquals("id-123", result.requestId)
        assertEquals(bucketDataRedundancyTransition, result.bucketDataRedundancyTransition)

        assertNotNull(result.headers)
        assertEquals(1, result.headers.size)
        assertEquals("id-123", result.headers["x-oss-request-id"])
    }

    @Test
    fun buildResultFromBuilder() {
        val bucketDataRedundancyTransition = BucketDataRedundancyTransition {
            taskId = "909c6c818dd041d1a44e0fdc66aa****"
            createTime = "2023-11-17T09:14:39.000Z"
            startTime = "2023-11-17T09:14:39.000Z"
            endTime = "2023-11-18T09:14:39.000Z"
            status = "Finished"
            estimatedRemainingTime = "100"
            processPercentage = "0"
        }
        val builder = GetBucketDataRedundancyTransitionResult.Builder()
        builder.status = "OK"
        builder.statusCode = 200
        builder.headers = mutableMapOf("x-oss-request-id" to "id-123")
        builder.innerBody = bucketDataRedundancyTransition

        val result = GetBucketDataRedundancyTransitionResult(builder)
        assertEquals(200, result.statusCode)
        assertEquals("OK", result.status)
        assertEquals("id-123", result.requestId)
        assertEquals(bucketDataRedundancyTransition, result.bucketDataRedundancyTransition)

        assertNotNull(result.headers)
        assertEquals(1, result.headers.size)
        assertEquals("id-123", result.headers["x-oss-request-id"])
    }
}
