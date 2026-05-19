package com.aliyun.kotlin.sdk.service.oss2.extension.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ListUserDataRedundancyTransitionTest {
    @Test
    fun buildRequestWithEmptyValues() {
        val request = ListUserDataRedundancyTransitionRequest {}
        assertNull(request.bucket)
        assertNull(request.maxKeys)
        assertNull(request.continuationToken)

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
        val request = ListUserDataRedundancyTransitionRequest {
            bucket = "bucket"
            maxKeys = 100
            continuationToken = "abc"
        }

        assertEquals("bucket", request.bucket)

        assertNotNull(request.headers)
        assertTrue {
            request.headers.isEmpty()
        }
        assertNotNull(request.parameters)
        assertTrue {
            request.parameters.contains("max-keys")
            request.parameters.contains("continuation-token")
        }
    }

    @Test
    fun buildRequestFromBuilder() {
        val builder = ListUserDataRedundancyTransitionRequest.Builder()
        builder.bucket = "bucket"
        builder.maxKeys = 100
        builder.continuationToken = "abc"

        val request = ListUserDataRedundancyTransitionRequest(builder)
        assertEquals("bucket", request.bucket)

        assertNotNull(request.headers)
        assertTrue {
            request.headers.isEmpty()
        }
        assertNotNull(request.parameters)
        assertTrue {
            request.parameters.contains("max-keys")
            request.parameters.contains("continuation-token")
        }
    }

    @Test
    fun buildResultWithEmptyValues() {
        val result = ListUserDataRedundancyTransitionResult {}
        assertEquals(0, result.statusCode)
        assertEquals("", result.status)
        assertEquals("", result.requestId)
        assertNull(result.isTruncated)
        assertNull(result.nextContinuationToken)
        assertNull(result.bucketDataRedundancyTransitions)

        assertNotNull(result.headers)
        assertTrue {
            result.headers.isEmpty()
        }
    }

    @Test
    fun buildResultWithFullValuesFromDsl() {
        val listBucketDataRedundancyTransitionXml = ListBucketDataRedundancyTransitionXml {
            isTruncated = true
            nextContinuationToken = "abc"
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
        val result = ListUserDataRedundancyTransitionResult {
            status = "OK"
            statusCode = 200
            headers = mutableMapOf("x-oss-request-id" to "id-123")
            innerBody = listBucketDataRedundancyTransitionXml
        }
        assertEquals(200, result.statusCode)
        assertEquals("OK", result.status)
        assertEquals("id-123", result.requestId)
        assertEquals(true, result.isTruncated)
        assertEquals("abc", result.nextContinuationToken)
        assertEquals(listBucketDataRedundancyTransitionXml.bucketDataRedundancyTransitions, result.bucketDataRedundancyTransitions)

        assertNotNull(result.headers)
        assertEquals(1, result.headers.size)
        assertEquals("id-123", result.headers["x-oss-request-id"])
    }

    @Test
    fun buildResultFromBuilder() {
        val listBucketDataRedundancyTransitionXml = ListBucketDataRedundancyTransitionXml {
            isTruncated = true
            nextContinuationToken = "abc"
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
        val builder = ListUserDataRedundancyTransitionResult.Builder()
        builder.status = "OK"
        builder.statusCode = 200
        builder.headers = mutableMapOf("x-oss-request-id" to "id-123")
        builder.innerBody = listBucketDataRedundancyTransitionXml

        val result = ListUserDataRedundancyTransitionResult(builder)
        assertEquals(200, result.statusCode)
        assertEquals("OK", result.status)
        assertEquals("id-123", result.requestId)
        assertEquals(true, result.isTruncated)
        assertEquals("abc", result.nextContinuationToken)
        assertEquals(listBucketDataRedundancyTransitionXml.bucketDataRedundancyTransitions, result.bucketDataRedundancyTransitions)

        assertNotNull(result.headers)
        assertEquals(1, result.headers.size)
        assertEquals("id-123", result.headers["x-oss-request-id"])
    }
}
