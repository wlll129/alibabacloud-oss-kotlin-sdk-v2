package com.aliyun.kotlin.sdk.service.oss2.test

import com.aliyun.kotlin.sdk.service.oss2.Uploader
import com.aliyun.kotlin.sdk.service.oss2.exceptions.ServiceException
import com.aliyun.kotlin.sdk.service.oss2.models.AbortMultipartUploadRequest
import com.aliyun.kotlin.sdk.service.oss2.models.DeleteBucketRequest
import com.aliyun.kotlin.sdk.service.oss2.models.DeleteObjectRequest
import com.aliyun.kotlin.sdk.service.oss2.models.HeadObjectRequest
import com.aliyun.kotlin.sdk.service.oss2.models.ListMultipartUploadsRequest
import com.aliyun.kotlin.sdk.service.oss2.models.ListObjectsV2Request
import com.aliyun.kotlin.sdk.service.oss2.models.PutBucketRequest
import com.aliyun.kotlin.sdk.service.oss2.models.PutObjectRequest
import com.aliyun.kotlin.sdk.service.oss2.paginator.listMultipartUploadsPaginator
import com.aliyun.kotlin.sdk.service.oss2.paginator.listObjectsV2Paginator
import com.aliyun.kotlin.sdk.service.oss2.progress.ProgressListener
import com.aliyun.kotlin.sdk.service.oss2.types.ByteStream
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.files.SystemTemporaryDirectory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UploaderTest : TestBase() {

    private val bucketName = randomBucketName()

    @BeforeTest
    fun putBucket() = runTest {
        defaultClient.putBucket(PutBucketRequest {
            bucket = bucketName
        })
    }

    @AfterTest
    fun cleanAndDeleteBucket() = runTest {
        defaultClient.listObjectsV2Paginator(ListObjectsV2Request {
            bucket = bucketName
        }).collect {
            it.contents?.forEach { obj ->
                defaultClient.deleteObject(DeleteObjectRequest {
                    bucket = bucketName
                    key = obj.key
                })
            }
        }
        defaultClient.listMultipartUploadsPaginator(ListMultipartUploadsRequest {
            bucket = bucketName
        }).collect {
            it.uploads?.forEach { obj ->
                defaultClient.abortMultipartUpload(AbortMultipartUploadRequest {
                    bucket = bucketName
                    key = obj.key
                    uploadId = obj.uploadId
                })
            }
        }
        defaultClient.deleteBucket(DeleteBucketRequest {
            bucket = bucketName
        })
    }

    @Test
    fun testUploadMultiPart() = runTest {
        val key = randomObjectKey()
        val filePath = createTestFile("file", 20 * 1024 * 1024)

        val uploader = Uploader(defaultClient)
        val result = uploader.upload(PutObjectRequest {
            bucket = bucketName
            this.key = key
            body = ByteStream.fromFile(filePath)
        })
        assertEquals(200, result.statusCode)

        val headResult = defaultClient.headObject(HeadObjectRequest {
            bucket = bucketName
            this.key = key
        })
        assertEquals("Multipart", headResult.objectType)

        removeTestFile("file")
    }

    @Test
    fun testUploadSinglePart() = runTest {
        val key = randomObjectKey()
        val filePath = createTestFile("file", 2 * 1024 * 1024)

        val uploader = Uploader(defaultClient)
        val result = uploader.upload(PutObjectRequest {
            bucket = bucketName
            this.key = key
            body = ByteStream.fromFile(filePath)
        })
        assertEquals(200, result.statusCode)

        val headResult = defaultClient.headObject(HeadObjectRequest {
            bucket = bucketName
            this.key = key
        })
        assertEquals("Normal", headResult.objectType)

        removeTestFile("file")
    }

    @Test
    fun testUploadWithProgressListener() = runTest {
        val key = randomObjectKey()
        val filePath = createTestFile("file", 20 * 1024 * 1024)
        var totalBytesTransferred: Long = 0

        val uploader = Uploader(defaultClient, {
            it.partSize = 256 * 1024
        })
        val result = uploader.upload(PutObjectRequest {
            bucket = bucketName
            this.key = key
            body = ByteStream.fromFile(filePath)
            progressListener =
                ProgressListener { bytesSent, totalBytesSent, totalBytesExpectedToSend ->
                    totalBytesTransferred += bytesSent
                    assertEquals(totalBytesTransferred, totalBytesSent)
                    assertEquals(256 * 1024, bytesSent)
                    assertEquals(20 * 1024 * 1024, totalBytesExpectedToSend)
                }
        })
        assertEquals(200, result.statusCode)
        assertEquals(20 * 1024 * 1024, totalBytesTransferred)

        removeTestFile("file")
    }

    @Test
    fun testUploadWithEnableCheckPoint() = runTest {
        val key = randomObjectKey()
        val filePath = createTestFile("file", 20 * 1024 * 1024)

        val uploader = Uploader(defaultClient, {
            it.enableCheckpoint = true
            it.partSize = 256 * 1024
        })
        launch {
            val exception: Throwable = assertFails {
                uploader.upload(PutObjectRequest {
                    bucket = bucketName
                    this.key = key
                    body = ByteStream.fromFile(filePath)
                    progressListener =
                        ProgressListener { bytesSent, totalBytesSent, totalBytesExpectedToSend ->
                            val percentage = totalBytesSent.toFloat() / totalBytesExpectedToSend
                            if (percentage > 0.5) {
                                this@launch.cancel()
                            }
                        }
                })
            }
            assertTrue(exception.cause is CancellationException)
        }.join()

        val result = uploader.upload(PutObjectRequest {
            bucket = bucketName
            this.key = key
            body = ByteStream.fromFile(filePath)
            progressListener =
                ProgressListener { bytesSent, totalBytesSent, totalBytesExpectedToSend ->
                    val percentage = totalBytesSent.toFloat() / totalBytesExpectedToSend
                    assertTrue(percentage >= 0.5)
                }
        })
        assertEquals(200, result.statusCode)

        removeTestFile("file")
    }

    @Test
    fun testUploadWithCheckPointDir() = runTest {
        val key = randomObjectKey()
        val filePath = createTestFile("file", 20 * 1024 * 1024)
        val baseDir = Path("$SystemTemporaryDirectory/OSS/uploader")

        val uploader = Uploader(defaultClient, {
            it.enableCheckpoint = true
            it.partSize = 256 * 1024
            it.checkpointDir = baseDir
        })
        launch {
            val exception: Throwable = assertFails {
                uploader.upload(PutObjectRequest {
                    bucket = bucketName
                    this.key = key
                    body = ByteStream.fromFile(filePath)
                    progressListener =
                        ProgressListener { bytesSent, totalBytesSent, totalBytesExpectedToSend ->
                            val percentage = totalBytesSent.toFloat() / totalBytesExpectedToSend
                            if (percentage > 0.5) {
                                this@launch.cancel()
                            }
                        }
                })
            }
            assertTrue(exception.cause is CancellationException)
            assertTrue(SystemFileSystem.exists(baseDir))
            assertTrue(SystemFileSystem.list(baseDir).size == 1)
        }.join()

        val result = uploader.upload(PutObjectRequest {
            bucket = bucketName
            this.key = key
            body = ByteStream.fromFile(filePath)
            progressListener =
                ProgressListener { bytesSent, totalBytesSent, totalBytesExpectedToSend ->
                    val percentage = totalBytesSent.toFloat() / totalBytesExpectedToSend
                    assertTrue(percentage >= 0.5)
                }
        })
        assertEquals(200, result.statusCode)
        assertTrue(SystemFileSystem.list(baseDir).isEmpty())

        removeTestFile("file")
    }

    @Test
    fun testUploadWithAbort() = runTest {
        val key = randomObjectKey()
        val filePath = createTestFile("file", 20 * 1024 * 1024)
        val baseDir = Path("$SystemTemporaryDirectory/OSS/uploader")

        val uploader = Uploader(defaultClient, {
            it.enableCheckpoint = true
            it.partSize = 256 * 1024
            it.checkpointDir = baseDir
        })
        launch {
            val exception: Throwable = assertFails {
                uploader.upload(PutObjectRequest {
                    bucket = bucketName
                    this.key = key
                    body = ByteStream.fromFile(filePath)
                    progressListener =
                        ProgressListener { bytesSent, totalBytesSent, totalBytesExpectedToSend ->
                            val percentage = totalBytesSent.toFloat() / totalBytesExpectedToSend
                            if (percentage > 0.5) {
                                this@launch.cancel()
                            }
                        }
                })
            }
            assertTrue(exception.cause is CancellationException)
            assertTrue(SystemFileSystem.exists(baseDir))
            assertTrue(SystemFileSystem.list(baseDir).size == 1)
        }.join()

        uploader.abortUpload(PutObjectRequest {
            bucket = bucketName
            this.key = key
            body = ByteStream.fromFile(filePath)
            progressListener =
                ProgressListener { bytesSent, totalBytesSent, totalBytesExpectedToSend ->
                    val percentage = totalBytesSent.toFloat() / totalBytesExpectedToSend
                    assertTrue(percentage >= 0.5)
                }
        })
        assertTrue(SystemFileSystem.list(baseDir).isEmpty())
        val result = defaultClient.listMultipartUploads(ListMultipartUploadsRequest {
            bucket = bucketName
        })
        assertNull(result.uploads)

        removeTestFile("file")
    }

    @Test
    fun testUploadWithException() = runTest {
        val key = randomObjectKey()

        val uploader = Uploader(defaultClient)
        var exception: Throwable =
            assertFailsWith<IllegalArgumentException> { uploader.upload(PutObjectRequest {}) }
        assertEquals(exception.message, "request.bucket is required")

        exception = assertFailsWith<IllegalArgumentException> {
            uploader.upload(PutObjectRequest {
                bucket = bucketName
            })
        }
        assertEquals(exception.message, "request.key is required")

        exception = assertFailsWith<IllegalArgumentException> {
            uploader.upload(PutObjectRequest {
                bucket = bucketName
                this.key = key
            })
        }
        assertEquals(exception.message, "request.body is required")

        exception = assertFails {
            Uploader(invalidClient).upload(PutObjectRequest {
                bucket = bucketName
                this.key = key
                body = ByteStream.fromString("Hello oss.")
            })
        }
        assertTrue { exception.cause is ServiceException }
        assertEquals((exception.cause as ServiceException).statusCode, 403)
        assertEquals((exception.cause as ServiceException).errorCode, "InvalidAccessKeyId")

    }
}
