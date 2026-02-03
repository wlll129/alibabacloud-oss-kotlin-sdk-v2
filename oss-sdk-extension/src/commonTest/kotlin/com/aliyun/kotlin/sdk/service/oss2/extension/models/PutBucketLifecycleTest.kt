package com.aliyun.kotlin.sdk.service.oss2.extension.models

import com.aliyun.kotlin.sdk.service.oss2.extension.api.SerdeUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PutBucketLifecycleTest {
    @Test
    fun buildRequestWithEmptyValues() {
        val request = PutBucketLifecycleRequest {}
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
        val configuration = LifecycleConfiguration {
            rules = listOf(
                LifecycleRule{
                    status = "Enabled"
                    expiration = Expiration {
                        days = 1
                        expiredObjectDeleteMarker = true
                    }
                    abortMultipartUpload = AbortMultipartUpload {
                        days = 1
                    }
                    tags = listOf(
                        Tag {
                            key = "k1"
                            value = "v1"
                        }
                    )
                    filter = LifecycleRuleFilter {
                        nots = listOf(
                            LifecycleRuleNot{
                                prefix = "pre1"
                                tag = Tag {
                                    key = "k1"
                                    value = "v1"
                                }
                            }
                        )
                        objectSizeLessThan = 1
                        objectSizeGreaterThan = 2
                    }
                    prefix = "pre"
                    transitions = listOf(
                        Transition {
                            storageClass = "IA"
                            isAccessTime = true
                            returnToStdWhenVisit = true
                            allowSmallFile = true
                            days = 1
                        }
                    )
                    noncurrentVersionExpiration = NoncurrentVersionExpiration {
                        noncurrentDays = 1
                    }
                    noncurrentVersionTransitions = listOf(
                        NoncurrentVersionTransition {
                            noncurrentDays = 1
                            storageClass = "IA"
                            isAccessTime = true
                            returnToStdWhenVisit = true
                            allowSmallFile = true
                        }
                    )
                    atimeBase = 1
                    id = "id1"
                },
                LifecycleRule{
                    status = "Enabled"
                    expiration = Expiration {
                        createdBeforeDate = "2002-10-11T00:00:00.000Z"
                        expiredObjectDeleteMarker = true
                    }
                    abortMultipartUpload = AbortMultipartUpload {
                        createdBeforeDate = "2002-10-11T00:00:00.000Z"
                    }
                    tags = listOf(
                        Tag {
                            key = "k2"
                            value = "v2"
                        }
                    )
                    filter = LifecycleRuleFilter {
                        nots = listOf(
                            LifecycleRuleNot{
                                prefix = "pre2"
                                tag = Tag {
                                    key = "k2"
                                    value = "v2"
                                }
                            }
                        )
                        objectSizeLessThan = 1
                        objectSizeGreaterThan = 2
                    }
                    prefix = "pre"
                    transitions = listOf(
                        Transition {
                            storageClass = "IA"
                            isAccessTime = true
                            returnToStdWhenVisit = true
                            allowSmallFile = true
                            createdBeforeDate = "2002-10-11T00:00:00.000Z"
                        }
                    )
                    noncurrentVersionExpiration = NoncurrentVersionExpiration {
                        noncurrentDays = 1
                    }
                    noncurrentVersionTransitions = listOf(
                        NoncurrentVersionTransition {
                            noncurrentDays = 1
                            storageClass = "IA"
                            isAccessTime = true
                            returnToStdWhenVisit = true
                            allowSmallFile = true
                        }
                    )
                    atimeBase = 1
                    id = "id2"
                }
            )
        }
        val request = PutBucketLifecycleRequest {
            bucket = "bucket"
            this.lifecycleConfiguration = configuration
        }

        assertEquals("bucket", request.bucket)
        assertEquals(configuration, request.lifecycleConfiguration)

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
        val configuration = LifecycleConfiguration {
            rules = listOf(
                LifecycleRule{
                    status = "Enabled"
                    expiration = Expiration {
                        days = 1
                        expiredObjectDeleteMarker = true
                    }
                    abortMultipartUpload = AbortMultipartUpload {
                        days = 1
                    }
                    tags = listOf(
                        Tag {
                            key = "k1"
                            value = "v1"
                        }
                    )
                    filter = LifecycleRuleFilter {
                        nots = listOf(
                            LifecycleRuleNot{
                                prefix = "pre1"
                                tag = Tag {
                                    key = "k1"
                                    value = "v1"
                                }
                            }
                        )
                        objectSizeLessThan = 1
                        objectSizeGreaterThan = 2
                    }
                    prefix = "pre"
                    transitions = listOf(
                        Transition {
                            storageClass = "IA"
                            isAccessTime = true
                            returnToStdWhenVisit = true
                            allowSmallFile = true
                            days = 1
                        }
                    )
                    noncurrentVersionExpiration = NoncurrentVersionExpiration {
                        noncurrentDays = 1
                    }
                    noncurrentVersionTransitions = listOf(
                        NoncurrentVersionTransition {
                            noncurrentDays = 1
                            storageClass = "IA"
                            isAccessTime = true
                            returnToStdWhenVisit = true
                            allowSmallFile = true
                        }
                    )
                    atimeBase = 1
                    id = "id1"
                },
                LifecycleRule{
                    status = "Enabled"
                    expiration = Expiration {
                        createdBeforeDate = "2002-10-11T00:00:00.000Z"
                        expiredObjectDeleteMarker = true
                    }
                    abortMultipartUpload = AbortMultipartUpload {
                        createdBeforeDate = "2002-10-11T00:00:00.000Z"
                    }
                    tags = listOf(
                        Tag {
                            key = "k2"
                            value = "v2"
                        }
                    )
                    filter = LifecycleRuleFilter {
                        nots = listOf(
                            LifecycleRuleNot{
                                prefix = "pre2"
                                tag = Tag {
                                    key = "k2"
                                    value = "v2"
                                }
                            }
                        )
                        objectSizeLessThan = 1
                        objectSizeGreaterThan = 2
                    }
                    prefix = "pre"
                    transitions = listOf(
                        Transition {
                            storageClass = "IA"
                            isAccessTime = true
                            returnToStdWhenVisit = true
                            allowSmallFile = true
                            createdBeforeDate = "2002-10-11T00:00:00.000Z"
                        }
                    )
                    noncurrentVersionExpiration = NoncurrentVersionExpiration {
                        noncurrentDays = 1
                    }
                    noncurrentVersionTransitions = listOf(
                        NoncurrentVersionTransition {
                            noncurrentDays = 1
                            storageClass = "IA"
                            isAccessTime = true
                            returnToStdWhenVisit = true
                            allowSmallFile = true
                        }
                    )
                    atimeBase = 1
                    id = "id2"
                }
            )
        }
        val builder = PutBucketLifecycleRequest.Builder()
        builder.bucket = "bucket"
        builder.lifecycleConfiguration = configuration

        val request = PutBucketLifecycleRequest(builder)
        assertEquals("bucket", request.bucket)
        assertEquals(configuration, request.lifecycleConfiguration)

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
        val result = PutBucketLifecycleResult {}
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
        val result = PutBucketLifecycleResult {
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
        val builder = PutBucketLifecycleResult.Builder()
        builder.status = "OK"
        builder.statusCode = 200
        builder.headers = mutableMapOf("x-oss-request-id" to "id-123")

        val result = PutBucketLifecycleResult(builder)
        assertEquals(200, result.statusCode)
        assertEquals("OK", result.status)
        assertEquals("id-123", result.requestId)

        assertNotNull(result.headers)
        assertEquals(1, result.headers.size)
        assertEquals("id-123", result.headers["x-oss-request-id"])
    }
}
