package com.aliyun.kotlin.sdk.service.oss2.extension.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ListBucketDataRedundancyTransitionTest {
    @Test
    fun buildRequestWithEmptyValues() {
        val request = ListBucketDataRedundancyTransitionRequest {}
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
        val request = ListBucketDataRedundancyTransitionRequest {
            bucket = "bucket"
        }

        assertEquals("bucket", request.bucket)

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
        val builder = ListBucketDataRedundancyTransitionRequest.Builder()
        builder.bucket = "bucket"

        val request = ListBucketDataRedundancyTransitionRequest(builder)
        assertEquals("bucket", request.bucket)

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
        val result = ListBucketDataRedundancyTransitionResult {}
        assertEquals(0, result.statusCode)
        assertEquals("", result.status)
        assertEquals("", result.requestId)
        assertNull(result.listBucketDataRedundancyTransition)

        assertNotNull(result.headers)
        assertTrue {
            result.headers.isEmpty()
        }
    }

    @Test
    fun buildResultWithFullValuesFromDsl() {
        val listBucketDataRedundancyTransition = ListBucketDataRedundancyTransition {
            bucketDataRedundancyTransitions = listOf(
                BucketDataRedundancyTransition {
                    taskId = "4be5beb0f74f490186311b268bf6****"
                    status = "Queueing"
                    createTime = "2023-11-17T08:40:17.000Z"
                },
                BucketDataRedundancyTransition {
                    taskId = "4be5beb0f74f490186311b268bf6j****"
                    status = "Processing"
                    createTime = "2023-11-17T08:40:17.000Z"
                    startTime = "2023-11-17T10:40:17.000Z"
                    processPercentage = "50"
                    estimatedRemainingTime = "16"
                },
                BucketDataRedundancyTransition {
                    taskId = "4be5beb0er4f490186311b268bf6j****"
                    status = "Finished"
                    createTime = "2023-11-17T08:40:17.000Z"
                    startTime = "2023-11-17T11:40:17.000Z"
                    processPercentage = "100"
                    estimatedRemainingTime = "0"
                    endTime = "2023-11-18T09:40:17.000Z"
                }
            )
        }
        val result = ListBucketDataRedundancyTransitionResult {
            status = "OK"
            statusCode = 200
            headers = mutableMapOf("x-oss-request-id" to "id-123")
            innerBody = listBucketDataRedundancyTransition
        }
        assertEquals(200, result.statusCode)
        assertEquals("OK", result.status)
        assertEquals("id-123", result.requestId)
        assertEquals(listBucketDataRedundancyTransition, result.listBucketDataRedundancyTransition)

        assertNotNull(result.headers)
        assertEquals(1, result.headers.size)
        assertEquals("id-123", result.headers["x-oss-request-id"])
    }

    @Test
    fun buildResultFromBuilder() {
        val listBucketDataRedundancyTransition = ListBucketDataRedundancyTransition {
            bucketDataRedundancyTransitions = listOf(
                BucketDataRedundancyTransition {
                    taskId = "4be5beb0f74f490186311b268bf6****"
                    status = "Queueing"
                    createTime = "2023-11-17T08:40:17.000Z"
                },
                BucketDataRedundancyTransition {
                    taskId = "4be5beb0f74f490186311b268bf6j****"
                    status = "Processing"
                    createTime = "2023-11-17T08:40:17.000Z"
                    startTime = "2023-11-17T10:40:17.000Z"
                    processPercentage = "50"
                    estimatedRemainingTime = "16"
                },
                BucketDataRedundancyTransition {
                    taskId = "4be5beb0er4f490186311b268bf6j****"
                    status = "Finished"
                    createTime = "2023-11-17T08:40:17.000Z"
                    startTime = "2023-11-17T11:40:17.000Z"
                    processPercentage = "100"
                    estimatedRemainingTime = "0"
                    endTime = "2023-11-18T09:40:17.000Z"
                }
            )
        }
        val builder = ListBucketDataRedundancyTransitionResult.Builder()
        builder.status = "OK"
        builder.statusCode = 200
        builder.headers = mutableMapOf("x-oss-request-id" to "id-123")
        builder.innerBody = listBucketDataRedundancyTransition

        val result = ListBucketDataRedundancyTransitionResult(builder)
        assertEquals(200, result.statusCode)
        assertEquals("OK", result.status)
        assertEquals("id-123", result.requestId)
        assertEquals(listBucketDataRedundancyTransition, result.listBucketDataRedundancyTransition)

        assertNotNull(result.headers)
        assertEquals(1, result.headers.size)
        assertEquals("id-123", result.headers["x-oss-request-id"])
    }
}
