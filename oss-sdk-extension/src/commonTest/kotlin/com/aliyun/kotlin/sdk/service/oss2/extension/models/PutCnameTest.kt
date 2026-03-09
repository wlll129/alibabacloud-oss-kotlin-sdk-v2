package com.aliyun.kotlin.sdk.service.oss2.extension.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PutCnameTest {
    @Test
    fun buildRequestWithEmptyValues() {
        val request = PutCnameRequest {}
        assertNull(request.bucket)
        assertNull(request.bucketCnameConfiguration)

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
        val bucketCnameConfiguration = BucketCnameConfiguration {
            cname = Cname {
                certificateConfiguration = CertificateConfiguration {
                    certId = "certId"
                    certificate = "certificate"
                    privateKey = "privateKey"
                    previousCertId = "previousCertId"
                    force = true
                    deleteCertificate = true
                }
                domain = "example.com"
            }
        }
        val request = PutCnameRequest {
            bucket = "bucket"
            this.bucketCnameConfiguration = bucketCnameConfiguration
        }

        assertEquals("bucket", request.bucket)
        assertEquals(bucketCnameConfiguration, request.bucketCnameConfiguration)

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
        val bucketCnameConfiguration = BucketCnameConfiguration {
            cname = Cname {
                certificateConfiguration = CertificateConfiguration {
                    certId = "certId"
                    certificate = "certificate"
                    privateKey = "privateKey"
                    previousCertId = "previousCertId"
                    force = true
                    deleteCertificate = true
                }
                domain = "example.com"
            }
        }
        val builder = PutCnameRequest.Builder()
        builder.bucket = "bucket"
        builder.bucketCnameConfiguration = bucketCnameConfiguration

        val request = PutCnameRequest(builder)
        assertEquals("bucket", request.bucket)
        assertEquals(bucketCnameConfiguration, request.bucketCnameConfiguration)

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
        val result = PutCnameResult {}
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
        val result = PutCnameResult {
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
        val builder = PutCnameResult.Builder()
        builder.status = "OK"
        builder.statusCode = 200
        builder.headers = mutableMapOf("x-oss-request-id" to "id-123")

        val result = PutCnameResult(builder)
        assertEquals(200, result.statusCode)
        assertEquals("OK", result.status)
        assertEquals("id-123", result.requestId)

        assertNotNull(result.headers)
        assertEquals(1, result.headers.size)
        assertEquals("id-123", result.headers["x-oss-request-id"])
    }
}
