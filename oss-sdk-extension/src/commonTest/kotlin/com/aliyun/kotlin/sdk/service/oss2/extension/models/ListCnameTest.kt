package com.aliyun.kotlin.sdk.service.oss2.extension.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ListCnameTest {
    @Test
    fun buildRequestWithEmptyValues() {
        val request = ListCnameRequest {}
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
        val request = ListCnameRequest {
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
        val builder = ListCnameRequest.Builder()
        builder.bucket = "bucket"

        val request = ListCnameRequest(builder)
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
        val result = ListCnameResult {}
        assertEquals(0, result.statusCode)
        assertEquals("", result.status)
        assertEquals("", result.requestId)
        assertNull(result.bucket)
        assertNull(result.owner)
        assertNull(result.cnames)

        assertNotNull(result.headers)
        assertTrue {
            result.headers.isEmpty()
        }
    }

    @Test
    fun buildResultWithFullValuesFromDsl() {
        val resultXml = ListCnameResultXml {
            bucket = "targetbucket"
            owner = "testowner"
            cnames = listOf(
                CnameInfo {
                    domain = "example.com"
                    lastModified = "2021-09-15T02:35:07.000Z"
                    status = "Enabled"
                    certificate = CnameCertificate {
                        type = "CAS"
                        certId = "493****-cn-hangzhou"
                        status = "Enabled"
                        creationDate = "Wed, 15 Sep 2021 02:35:06 GMT"
                        fingerprint = "DE:01:CF:EC:7C:A7:98:CB:D8:6E:FB:1D:97:EB:A9:64:1D:4E:**:**"
                        validStartDate = "Wed, 12 Apr 2023 10:14:51 GMT"
                        validEndDate = "Mon, 4 May 2048 10:14:51 GMT"
                    }
                },
                CnameInfo {
                    domain = "example.org"
                    lastModified = "2021-09-15T02:34:58.000Z"
                    status = "Enabled"
                }
            )
        }
        val result = ListCnameResult {
            status = "OK"
            statusCode = 200
            headers = mutableMapOf("x-oss-request-id" to "id-123")
            innerBody = resultXml
        }
        assertEquals(200, result.statusCode)
        assertEquals("OK", result.status)
        assertEquals("id-123", result.requestId)
        assertEquals(resultXml.bucket, result.bucket)
        assertEquals(resultXml.owner, result.owner)
        assertEquals(resultXml.cnames, result.cnames)

        assertNotNull(result.headers)
        assertEquals(1, result.headers.size)
        assertEquals("id-123", result.headers["x-oss-request-id"])
    }

    @Test
    fun buildResultFromBuilder() {
        val resultXml = ListCnameResultXml {
            bucket = "targetbucket"
            owner = "testowner"
            cnames = listOf(
                CnameInfo {
                    domain = "example.com"
                    lastModified = "2021-09-15T02:35:07.000Z"
                    status = "Enabled"
                    certificate = CnameCertificate {
                        type = "CAS"
                        certId = "493****-cn-hangzhou"
                        status = "Enabled"
                        creationDate = "Wed, 15 Sep 2021 02:35:06 GMT"
                        fingerprint = "DE:01:CF:EC:7C:A7:98:CB:D8:6E:FB:1D:97:EB:A9:64:1D:4E:**:**"
                        validStartDate = "Wed, 12 Apr 2023 10:14:51 GMT"
                        validEndDate = "Mon, 4 May 2048 10:14:51 GMT"
                    }
                },
                CnameInfo {
                    domain = "example.org"
                    lastModified = "2021-09-15T02:34:58.000Z"
                    status = "Enabled"
                }
            )
        }
        val builder = ListCnameResult.Builder()
        builder.status = "OK"
        builder.statusCode = 200
        builder.headers = mutableMapOf("x-oss-request-id" to "id-123")
        builder.innerBody = resultXml

        val result = ListCnameResult(builder)
        assertEquals(200, result.statusCode)
        assertEquals("OK", result.status)
        assertEquals("id-123", result.requestId)
        assertEquals(resultXml.bucket, result.bucket)
        assertEquals(resultXml.owner, result.owner)
        assertEquals(resultXml.cnames, result.cnames)

        assertNotNull(result.headers)
        assertEquals(1, result.headers.size)
        assertEquals("id-123", result.headers["x-oss-request-id"])
    }
}
