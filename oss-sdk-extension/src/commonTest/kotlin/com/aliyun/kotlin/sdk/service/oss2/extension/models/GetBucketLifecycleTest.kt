package com.aliyun.kotlin.sdk.service.oss2.extension.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GetBucketLifecycleTest {
    @Test
    fun buildRequestWithEmptyValues() {
        val request = GetBucketLifecycleRequest {}
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
        val request = GetBucketLifecycleRequest {
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
        val builder = GetBucketLifecycleRequest.Builder()
        builder.bucket = "bucket"

        val request = GetBucketLifecycleRequest(builder)
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
        val result = GetBucketLifecycleResult {}
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
        val result = GetBucketLifecycleResult {
            status = "OK"
            statusCode = 200
            headers = mutableMapOf("x-oss-request-id" to "id-123")
            innerBody = configuration
        }
        assertEquals(200, result.statusCode)
        assertEquals("OK", result.status)
        assertEquals("id-123", result.requestId)
        assertEquals(2, result.lifecycleConfiguration?.rules?.size)
        assertEquals(configuration.rules?.get(0)?.id, result.lifecycleConfiguration?.rules?.get(0)?.id)
        assertEquals(configuration.rules?.get(0)?.expiration?.days, result.lifecycleConfiguration?.rules?.get(0)?.expiration?.days)
        assertEquals(configuration.rules?.get(0)?.expiration?.expiredObjectDeleteMarker, result.lifecycleConfiguration?.rules?.get(0)?.expiration?.expiredObjectDeleteMarker)
        assertEquals(configuration.rules?.get(0)?.abortMultipartUpload?.days, result.lifecycleConfiguration?.rules?.get(0)?.abortMultipartUpload?.days)
        assertEquals(1, result.lifecycleConfiguration?.rules?.get(0)?.tags?.size)
        assertEquals(configuration.rules?.get(0)?.tags?.get(0)?.key, result.lifecycleConfiguration?.rules?.get(0)?.tags?.get(0)?.key)
        assertEquals(configuration.rules?.get(0)?.tags?.get(0)?.value, result.lifecycleConfiguration?.rules?.get(0)?.tags?.get(0)?.value)
        assertEquals(1, result.lifecycleConfiguration?.rules?.get(0)?.filter?.nots?.size)
        assertEquals(configuration.rules?.get(0)?.filter?.nots?.get(0)?.prefix, result.lifecycleConfiguration?.rules?.get(0)?.filter?.nots?.get(0)?.prefix)
        assertEquals(configuration.rules?.get(0)?.filter?.nots?.get(0)?.tag?.key, result.lifecycleConfiguration?.rules?.get(0)?.filter?.nots?.get(0)?.tag?.key)
        assertEquals(configuration.rules?.get(0)?.filter?.nots?.get(0)?.tag?.value, result.lifecycleConfiguration?.rules?.get(0)?.filter?.nots?.get(0)?.tag?.value)
        assertEquals(configuration.rules?.get(0)?.filter?.objectSizeLessThan, result.lifecycleConfiguration?.rules?.get(0)?.filter?.objectSizeLessThan)
        assertEquals(configuration.rules?.get(0)?.filter?.objectSizeGreaterThan, result.lifecycleConfiguration?.rules?.get(0)?.filter?.objectSizeGreaterThan)
        assertEquals(configuration.rules?.get(0)?.prefix, result.lifecycleConfiguration?.rules?.get(0)?.prefix)
        assertEquals(1, result.lifecycleConfiguration?.rules?.get(0)?.transitions?.size)
        assertEquals(configuration.rules?.get(0)?.transitions?.get(0)?.days, result.lifecycleConfiguration?.rules?.get(0)?.transitions?.get(0)?.days)
        assertEquals(configuration.rules?.get(0)?.transitions?.get(0)?.returnToStdWhenVisit, result.lifecycleConfiguration?.rules?.get(0)?.transitions?.get(0)?.returnToStdWhenVisit)
        assertEquals(configuration.rules?.get(0)?.transitions?.get(0)?.isAccessTime, result.lifecycleConfiguration?.rules?.get(0)?.transitions?.get(0)?.isAccessTime)
        assertEquals(configuration.rules?.get(0)?.transitions?.get(0)?.storageClass, result.lifecycleConfiguration?.rules?.get(0)?.transitions?.get(0)?.storageClass)
        assertEquals(configuration.rules?.get(0)?.transitions?.get(0)?.returnToStdWhenVisit, result.lifecycleConfiguration?.rules?.get(0)?.transitions?.get(0)?.returnToStdWhenVisit)
        assertEquals(configuration.rules?.get(0)?.noncurrentVersionExpiration?.noncurrentDays, result.lifecycleConfiguration?.rules?.get(0)?.noncurrentVersionExpiration?.noncurrentDays)
        assertEquals(1, result.lifecycleConfiguration?.rules?.get(0)?.noncurrentVersionTransitions?.size)
        assertEquals(configuration.rules?.get(0)?.noncurrentVersionTransitions?.get(0)?.returnToStdWhenVisit, result.lifecycleConfiguration?.rules?.get(0)?.noncurrentVersionTransitions?.get(0)?.returnToStdWhenVisit)
        assertEquals(configuration.rules?.get(0)?.noncurrentVersionTransitions?.get(0)?.noncurrentDays, result.lifecycleConfiguration?.rules?.get(0)?.noncurrentVersionTransitions?.get(0)?.noncurrentDays)
        assertEquals(configuration.rules?.get(0)?.noncurrentVersionTransitions?.get(0)?.storageClass, result.lifecycleConfiguration?.rules?.get(0)?.noncurrentVersionTransitions?.get(0)?.storageClass)
        assertEquals(configuration.rules?.get(0)?.noncurrentVersionTransitions?.get(0)?.isAccessTime, result.lifecycleConfiguration?.rules?.get(0)?.noncurrentVersionTransitions?.get(0)?.isAccessTime)
        assertEquals(configuration.rules?.get(0)?.noncurrentVersionTransitions?.get(0)?.allowSmallFile, result.lifecycleConfiguration?.rules?.get(0)?.noncurrentVersionTransitions?.get(0)?.allowSmallFile)
        assertEquals(configuration.rules?.get(0)?.atimeBase, result.lifecycleConfiguration?.rules?.get(0)?.atimeBase)
        assertEquals(configuration.rules?.get(1)?.expiration?.createdBeforeDate, result.lifecycleConfiguration?.rules?.get(1)?.expiration?.createdBeforeDate)
        assertEquals(configuration.rules?.get(1)?.expiration?.expiredObjectDeleteMarker, result.lifecycleConfiguration?.rules?.get(1)?.expiration?.expiredObjectDeleteMarker)
        assertEquals(configuration.rules?.get(1)?.abortMultipartUpload?.createdBeforeDate, result.lifecycleConfiguration?.rules?.get(1)?.abortMultipartUpload?.createdBeforeDate)
        assertEquals(1, result.lifecycleConfiguration?.rules?.get(1)?.tags?.size)
        assertEquals(configuration.rules?.get(1)?.tags?.get(0)?.key, result.lifecycleConfiguration?.rules?.get(1)?.tags?.get(0)?.key)
        assertEquals(configuration.rules?.get(1)?.tags?.get(0)?.value, result.lifecycleConfiguration?.rules?.get(1)?.tags?.get(0)?.value)
        assertEquals(1, result.lifecycleConfiguration?.rules?.get(0)?.filter?.nots?.size)
        assertEquals(configuration.rules?.get(1)?.filter?.nots?.get(0)?.prefix, result.lifecycleConfiguration?.rules?.get(1)?.filter?.nots?.get(0)?.prefix)
        assertEquals(configuration.rules?.get(1)?.filter?.nots?.get(0)?.tag?.key, result.lifecycleConfiguration?.rules?.get(1)?.filter?.nots?.get(0)?.tag?.key)
        assertEquals(configuration.rules?.get(1)?.filter?.nots?.get(0)?.tag?.value, result.lifecycleConfiguration?.rules?.get(1)?.filter?.nots?.get(0)?.tag?.value)
        assertEquals(configuration.rules?.get(1)?.filter?.objectSizeLessThan, result.lifecycleConfiguration?.rules?.get(1)?.filter?.objectSizeLessThan)
        assertEquals(configuration.rules?.get(1)?.filter?.objectSizeGreaterThan, result.lifecycleConfiguration?.rules?.get(1)?.filter?.objectSizeGreaterThan)
        assertEquals(configuration.rules?.get(1)?.prefix, result.lifecycleConfiguration?.rules?.get(1)?.prefix)
        assertEquals(1, result.lifecycleConfiguration?.rules?.get(1)?.transitions?.size)
        assertEquals(configuration.rules?.get(1)?.transitions?.get(0)?.createdBeforeDate, result.lifecycleConfiguration?.rules?.get(1)?.transitions?.get(0)?.createdBeforeDate)
        assertEquals(configuration.rules?.get(1)?.transitions?.get(0)?.returnToStdWhenVisit, result.lifecycleConfiguration?.rules?.get(1)?.transitions?.get(0)?.returnToStdWhenVisit)
        assertEquals(configuration.rules?.get(1)?.transitions?.get(0)?.isAccessTime, result.lifecycleConfiguration?.rules?.get(1)?.transitions?.get(0)?.isAccessTime)
        assertEquals(configuration.rules?.get(1)?.transitions?.get(0)?.storageClass, result.lifecycleConfiguration?.rules?.get(1)?.transitions?.get(0)?.storageClass)
        assertEquals(configuration.rules?.get(1)?.transitions?.get(0)?.returnToStdWhenVisit, result.lifecycleConfiguration?.rules?.get(1)?.transitions?.get(0)?.returnToStdWhenVisit)
        assertEquals(configuration.rules?.get(1)?.noncurrentVersionExpiration?.noncurrentDays, result.lifecycleConfiguration?.rules?.get(1)?.noncurrentVersionExpiration?.noncurrentDays)
        assertEquals(1, result.lifecycleConfiguration?.rules?.get(1)?.noncurrentVersionTransitions?.size)
        assertEquals(configuration.rules?.get(1)?.noncurrentVersionTransitions?.get(0)?.returnToStdWhenVisit, result.lifecycleConfiguration?.rules?.get(1)?.noncurrentVersionTransitions?.get(0)?.returnToStdWhenVisit)
        assertEquals(configuration.rules?.get(1)?.noncurrentVersionTransitions?.get(0)?.noncurrentDays, result.lifecycleConfiguration?.rules?.get(1)?.noncurrentVersionTransitions?.get(0)?.noncurrentDays)
        assertEquals(configuration.rules?.get(1)?.noncurrentVersionTransitions?.get(0)?.storageClass, result.lifecycleConfiguration?.rules?.get(1)?.noncurrentVersionTransitions?.get(0)?.storageClass)
        assertEquals(configuration.rules?.get(1)?.noncurrentVersionTransitions?.get(0)?.isAccessTime, result.lifecycleConfiguration?.rules?.get(1)?.noncurrentVersionTransitions?.get(0)?.isAccessTime)
        assertEquals(configuration.rules?.get(1)?.noncurrentVersionTransitions?.get(0)?.allowSmallFile, result.lifecycleConfiguration?.rules?.get(1)?.noncurrentVersionTransitions?.get(0)?.allowSmallFile)
        assertEquals(configuration.rules?.get(1)?.atimeBase, result.lifecycleConfiguration?.rules?.get(1)?.atimeBase)


        assertNotNull(result.headers)
        assertEquals(1, result.headers.size)
        assertEquals("id-123", result.headers["x-oss-request-id"])
    }

    @Test
    fun buildResultFromBuilder() {
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
        val builder = GetBucketLifecycleResult.Builder()
        builder.status = "OK"
        builder.statusCode = 200
        builder.headers = mutableMapOf("x-oss-request-id" to "id-123")
        builder.innerBody = configuration

        val result = GetBucketLifecycleResult(builder)
        assertEquals(200, result.statusCode)
        assertEquals("OK", result.status)
        assertEquals("id-123", result.requestId)
        assertEquals(2, result.lifecycleConfiguration?.rules?.size)
        assertEquals(configuration.rules?.get(0)?.id, result.lifecycleConfiguration?.rules?.get(0)?.id)
        assertEquals(configuration.rules?.get(0)?.expiration?.days, result.lifecycleConfiguration?.rules?.get(0)?.expiration?.days)
        assertEquals(configuration.rules?.get(0)?.expiration?.expiredObjectDeleteMarker, result.lifecycleConfiguration?.rules?.get(0)?.expiration?.expiredObjectDeleteMarker)
        assertEquals(configuration.rules?.get(0)?.abortMultipartUpload?.days, result.lifecycleConfiguration?.rules?.get(0)?.abortMultipartUpload?.days)
        assertEquals(1, result.lifecycleConfiguration?.rules?.get(0)?.tags?.size)
        assertEquals(configuration.rules?.get(0)?.tags?.get(0)?.key, result.lifecycleConfiguration?.rules?.get(0)?.tags?.get(0)?.key)
        assertEquals(configuration.rules?.get(0)?.tags?.get(0)?.value, result.lifecycleConfiguration?.rules?.get(0)?.tags?.get(0)?.value)
        assertEquals(1, result.lifecycleConfiguration?.rules?.get(0)?.filter?.nots?.size)
        assertEquals(configuration.rules?.get(0)?.filter?.nots?.get(0)?.prefix, result.lifecycleConfiguration?.rules?.get(0)?.filter?.nots?.get(0)?.prefix)
        assertEquals(configuration.rules?.get(0)?.filter?.nots?.get(0)?.tag?.key, result.lifecycleConfiguration?.rules?.get(0)?.filter?.nots?.get(0)?.tag?.key)
        assertEquals(configuration.rules?.get(0)?.filter?.nots?.get(0)?.tag?.value, result.lifecycleConfiguration?.rules?.get(0)?.filter?.nots?.get(0)?.tag?.value)
        assertEquals(configuration.rules?.get(0)?.filter?.objectSizeLessThan, result.lifecycleConfiguration?.rules?.get(0)?.filter?.objectSizeLessThan)
        assertEquals(configuration.rules?.get(0)?.filter?.objectSizeGreaterThan, result.lifecycleConfiguration?.rules?.get(0)?.filter?.objectSizeGreaterThan)
        assertEquals(configuration.rules?.get(0)?.prefix, result.lifecycleConfiguration?.rules?.get(0)?.prefix)
        assertEquals(1, result.lifecycleConfiguration?.rules?.get(0)?.transitions?.size)
        assertEquals(configuration.rules?.get(0)?.transitions?.get(0)?.days, result.lifecycleConfiguration?.rules?.get(0)?.transitions?.get(0)?.days)
        assertEquals(configuration.rules?.get(0)?.transitions?.get(0)?.returnToStdWhenVisit, result.lifecycleConfiguration?.rules?.get(0)?.transitions?.get(0)?.returnToStdWhenVisit)
        assertEquals(configuration.rules?.get(0)?.transitions?.get(0)?.isAccessTime, result.lifecycleConfiguration?.rules?.get(0)?.transitions?.get(0)?.isAccessTime)
        assertEquals(configuration.rules?.get(0)?.transitions?.get(0)?.storageClass, result.lifecycleConfiguration?.rules?.get(0)?.transitions?.get(0)?.storageClass)
        assertEquals(configuration.rules?.get(0)?.transitions?.get(0)?.returnToStdWhenVisit, result.lifecycleConfiguration?.rules?.get(0)?.transitions?.get(0)?.returnToStdWhenVisit)
        assertEquals(configuration.rules?.get(0)?.noncurrentVersionExpiration?.noncurrentDays, result.lifecycleConfiguration?.rules?.get(0)?.noncurrentVersionExpiration?.noncurrentDays)
        assertEquals(1, result.lifecycleConfiguration?.rules?.get(0)?.noncurrentVersionTransitions?.size)
        assertEquals(configuration.rules?.get(0)?.noncurrentVersionTransitions?.get(0)?.returnToStdWhenVisit, result.lifecycleConfiguration?.rules?.get(0)?.noncurrentVersionTransitions?.get(0)?.returnToStdWhenVisit)
        assertEquals(configuration.rules?.get(0)?.noncurrentVersionTransitions?.get(0)?.noncurrentDays, result.lifecycleConfiguration?.rules?.get(0)?.noncurrentVersionTransitions?.get(0)?.noncurrentDays)
        assertEquals(configuration.rules?.get(0)?.noncurrentVersionTransitions?.get(0)?.storageClass, result.lifecycleConfiguration?.rules?.get(0)?.noncurrentVersionTransitions?.get(0)?.storageClass)
        assertEquals(configuration.rules?.get(0)?.noncurrentVersionTransitions?.get(0)?.isAccessTime, result.lifecycleConfiguration?.rules?.get(0)?.noncurrentVersionTransitions?.get(0)?.isAccessTime)
        assertEquals(configuration.rules?.get(0)?.noncurrentVersionTransitions?.get(0)?.allowSmallFile, result.lifecycleConfiguration?.rules?.get(0)?.noncurrentVersionTransitions?.get(0)?.allowSmallFile)
        assertEquals(configuration.rules?.get(0)?.atimeBase, result.lifecycleConfiguration?.rules?.get(0)?.atimeBase)
        assertEquals(configuration.rules?.get(1)?.expiration?.createdBeforeDate, result.lifecycleConfiguration?.rules?.get(1)?.expiration?.createdBeforeDate)
        assertEquals(configuration.rules?.get(1)?.expiration?.expiredObjectDeleteMarker, result.lifecycleConfiguration?.rules?.get(1)?.expiration?.expiredObjectDeleteMarker)
        assertEquals(configuration.rules?.get(1)?.abortMultipartUpload?.createdBeforeDate, result.lifecycleConfiguration?.rules?.get(1)?.abortMultipartUpload?.createdBeforeDate)
        assertEquals(1, result.lifecycleConfiguration?.rules?.get(1)?.tags?.size)
        assertEquals(configuration.rules?.get(1)?.tags?.get(0)?.key, result.lifecycleConfiguration?.rules?.get(1)?.tags?.get(0)?.key)
        assertEquals(configuration.rules?.get(1)?.tags?.get(0)?.value, result.lifecycleConfiguration?.rules?.get(1)?.tags?.get(0)?.value)
        assertEquals(1, result.lifecycleConfiguration?.rules?.get(0)?.filter?.nots?.size)
        assertEquals(configuration.rules?.get(1)?.filter?.nots?.get(0)?.prefix, result.lifecycleConfiguration?.rules?.get(1)?.filter?.nots?.get(0)?.prefix)
        assertEquals(configuration.rules?.get(1)?.filter?.nots?.get(0)?.tag?.key, result.lifecycleConfiguration?.rules?.get(1)?.filter?.nots?.get(0)?.tag?.key)
        assertEquals(configuration.rules?.get(1)?.filter?.nots?.get(0)?.tag?.value, result.lifecycleConfiguration?.rules?.get(1)?.filter?.nots?.get(0)?.tag?.value)
        assertEquals(configuration.rules?.get(1)?.filter?.objectSizeLessThan, result.lifecycleConfiguration?.rules?.get(1)?.filter?.objectSizeLessThan)
        assertEquals(configuration.rules?.get(1)?.filter?.objectSizeGreaterThan, result.lifecycleConfiguration?.rules?.get(1)?.filter?.objectSizeGreaterThan)
        assertEquals(configuration.rules?.get(1)?.prefix, result.lifecycleConfiguration?.rules?.get(1)?.prefix)
        assertEquals(1, result.lifecycleConfiguration?.rules?.get(1)?.transitions?.size)
        assertEquals(configuration.rules?.get(1)?.transitions?.get(0)?.createdBeforeDate, result.lifecycleConfiguration?.rules?.get(1)?.transitions?.get(0)?.createdBeforeDate)
        assertEquals(configuration.rules?.get(1)?.transitions?.get(0)?.returnToStdWhenVisit, result.lifecycleConfiguration?.rules?.get(1)?.transitions?.get(0)?.returnToStdWhenVisit)
        assertEquals(configuration.rules?.get(1)?.transitions?.get(0)?.isAccessTime, result.lifecycleConfiguration?.rules?.get(1)?.transitions?.get(0)?.isAccessTime)
        assertEquals(configuration.rules?.get(1)?.transitions?.get(0)?.storageClass, result.lifecycleConfiguration?.rules?.get(1)?.transitions?.get(0)?.storageClass)
        assertEquals(configuration.rules?.get(1)?.transitions?.get(0)?.returnToStdWhenVisit, result.lifecycleConfiguration?.rules?.get(1)?.transitions?.get(0)?.returnToStdWhenVisit)
        assertEquals(configuration.rules?.get(1)?.noncurrentVersionExpiration?.noncurrentDays, result.lifecycleConfiguration?.rules?.get(1)?.noncurrentVersionExpiration?.noncurrentDays)
        assertEquals(1, result.lifecycleConfiguration?.rules?.get(1)?.noncurrentVersionTransitions?.size)
        assertEquals(configuration.rules?.get(1)?.noncurrentVersionTransitions?.get(0)?.returnToStdWhenVisit, result.lifecycleConfiguration?.rules?.get(1)?.noncurrentVersionTransitions?.get(0)?.returnToStdWhenVisit)
        assertEquals(configuration.rules?.get(1)?.noncurrentVersionTransitions?.get(0)?.noncurrentDays, result.lifecycleConfiguration?.rules?.get(1)?.noncurrentVersionTransitions?.get(0)?.noncurrentDays)
        assertEquals(configuration.rules?.get(1)?.noncurrentVersionTransitions?.get(0)?.storageClass, result.lifecycleConfiguration?.rules?.get(1)?.noncurrentVersionTransitions?.get(0)?.storageClass)
        assertEquals(configuration.rules?.get(1)?.noncurrentVersionTransitions?.get(0)?.isAccessTime, result.lifecycleConfiguration?.rules?.get(1)?.noncurrentVersionTransitions?.get(0)?.isAccessTime)
        assertEquals(configuration.rules?.get(1)?.noncurrentVersionTransitions?.get(0)?.allowSmallFile, result.lifecycleConfiguration?.rules?.get(1)?.noncurrentVersionTransitions?.get(0)?.allowSmallFile)
        assertEquals(configuration.rules?.get(1)?.atimeBase, result.lifecycleConfiguration?.rules?.get(1)?.atimeBase)

        assertNotNull(result.headers)
        assertEquals(1, result.headers.size)
        assertEquals("id-123", result.headers["x-oss-request-id"])
    }
}
