package com.aliyun.kotlin.sdk.service.oss2.extension.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CreateCnameTokenTest {
    @Test
    fun buildRequestWithEmptyValues() {
        val request = CreateCnameTokenRequest {}
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
        val request = CreateCnameTokenRequest {
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
        val builder = CreateCnameTokenRequest.Builder()
        builder.bucket = "bucket"
        builder.bucketCnameConfiguration = bucketCnameConfiguration

        val request = CreateCnameTokenRequest(builder)
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
        val result = CreateCnameTokenResult {}
        assertEquals(0, result.statusCode)
        assertEquals("", result.status)
        assertEquals("", result.requestId)
        assertNull(result.cnameToken)

        assertNotNull(result.headers)
        assertTrue {
            result.headers.isEmpty()
        }
    }

    @Test
    fun buildResultWithFullValuesFromDsl() {
        val cnameToken = CnameToken {
            bucket = "examplebucket"
            cname = "example.com"
            token = "be1d49d863dea9ffeff3df7d6455****"
            expireTime = "Wed, 23 Feb 2022 21:16:37 GMT"
        }
        val result = CreateCnameTokenResult {
            status = "OK"
            statusCode = 200
            headers = mutableMapOf("x-oss-request-id" to "id-123")
            innerBody = cnameToken
        }
        assertEquals(200, result.statusCode)
        assertEquals("OK", result.status)
        assertEquals("id-123", result.requestId)
        assertEquals(cnameToken, result.cnameToken)

        assertNotNull(result.headers)
        assertEquals(1, result.headers.size)
        assertEquals("id-123", result.headers["x-oss-request-id"])
    }

    @Test
    fun buildResultFromBuilder() {
        val cnameToken = CnameToken {
            bucket = "examplebucket"
            cname = "example.com"
            token = "be1d49d863dea9ffeff3df7d6455****"
            expireTime = "Wed, 23 Feb 2022 21:16:37 GMT"
        }
        val builder = CreateCnameTokenResult.Builder()
        builder.status = "OK"
        builder.statusCode = 200
        builder.headers = mutableMapOf("x-oss-request-id" to "id-123")
        builder.innerBody = cnameToken

        val result = CreateCnameTokenResult(builder)
        assertEquals(200, result.statusCode)
        assertEquals("OK", result.status)
        assertEquals("id-123", result.requestId)
        assertEquals(cnameToken, result.cnameToken)

        assertNotNull(result.headers)
        assertEquals(1, result.headers.size)
        assertEquals("id-123", result.headers["x-oss-request-id"])
    }
}
