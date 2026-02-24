package com.aliyun.kotlin.sdk.service.oss2.test

import com.aliyun.kotlin.sdk.service.oss2.Downloader
import com.aliyun.kotlin.sdk.service.oss2.exceptions.ServiceException
import com.aliyun.kotlin.sdk.service.oss2.hash.md5
import com.aliyun.kotlin.sdk.service.oss2.models.DeleteBucketRequest
import com.aliyun.kotlin.sdk.service.oss2.models.DeleteObjectRequest
import com.aliyun.kotlin.sdk.service.oss2.models.GetObjectRequest
import com.aliyun.kotlin.sdk.service.oss2.models.HttpRange
import com.aliyun.kotlin.sdk.service.oss2.models.ListObjectsV2Request
import com.aliyun.kotlin.sdk.service.oss2.models.PutBucketRequest
import com.aliyun.kotlin.sdk.service.oss2.models.PutObjectRequest
import com.aliyun.kotlin.sdk.service.oss2.paginator.listObjectsV2Paginator
import com.aliyun.kotlin.sdk.service.oss2.progress.ProgressListener
import com.aliyun.kotlin.sdk.service.oss2.types.ByteStream
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.files.SystemTemporaryDirectory
import kotlinx.io.readByteArray
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DownloaderTest : TestBase() {

    private val bucketName = randomBucketName()
    private val objectKey = randomObjectKey()

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
        defaultClient.deleteBucket(DeleteBucketRequest {
            bucket = bucketName
        })
    }

    @Test
    fun testDownload() = runTest {
        val filePath = createTestFile("downloader-test-source-file", 20 * 1024 * 1024)
        defaultClient.putObject(PutObjectRequest {
            bucket = bucketName
            key = objectKey
            body = ByteStream.fromFile(filePath)
        })

        val downloadFilePath = Path("$SystemTemporaryDirectory/OSS/downloader-destination-file")
        val downloader = Downloader(defaultClient)
        val result = downloader.downloadFile(GetObjectRequest {
            bucket = bucketName
            key = objectKey
        }, downloadFilePath)
        assertEquals(20 * 1024 * 1024, result.written)

        removeTestFile("file")
        SystemFileSystem.delete(downloadFilePath)
    }

    @Test
    fun testDownloadWithProgressListener() = runTest {
        val filePath = createTestFile("file", 20 * 1024 * 1024)
        val downloadFilePath = Path("$SystemTemporaryDirectory/OSS/downloader-destination-file")
        var totalBytesTransferred: Long = 0
        defaultClient.putObject(PutObjectRequest {
            bucket = bucketName
            key = objectKey
            body = ByteStream.fromFile(filePath)
        })

        val downloader = Downloader(defaultClient, {
            it.partSize = 1024 * 1024
        })
        downloader.downloadFile(GetObjectRequest {
            bucket = bucketName
            key = objectKey
            progressListener =
                ProgressListener { bytesReceive, totalBytesReceive, totalBytesExpectedToReceive ->
                    totalBytesTransferred += bytesReceive
                    assertEquals(totalBytesTransferred, totalBytesReceive)
                    assertEquals(20 * 1024 * 1024, totalBytesExpectedToReceive)
                }
        }, downloadFilePath)
        assertEquals(20 * 1024 * 1024, totalBytesTransferred)

        removeTestFile("file")
        SystemFileSystem.delete(downloadFilePath)
    }

    @Test
    fun testDownloadWithRange() = runTest {
        val filePath = createTestFile("downloader-test-source-file", 20 * 1024 * 1024)
        defaultClient.putObject(PutObjectRequest {
            bucket = bucketName
            key = objectKey
            body = ByteStream.fromFile(filePath)
        })

        val downloadFilePath = Path("$SystemTemporaryDirectory/OSS/downloader-destination-file")
        val downloader = Downloader(defaultClient, {
            it.partSize = 1024 * 1024
        })
        val result = downloader.downloadFile(GetObjectRequest {
            bucket = bucketName
            key = objectKey
            range = HttpRange(123, 6 * 1024).toString()
        }, downloadFilePath)
        assertEquals(6 * 1024, result.written)
        val sourceData = SystemFileSystem.source(filePath).buffered().use { source ->
            source.skip(123)
            source.readByteArray(6 * 1024)
        }
        val downloadData = SystemFileSystem.source(downloadFilePath).buffered().use { source ->
            source.readByteArray()
        }
        assertEquals(sourceData.md5().toHexString(), downloadData.md5().toHexString())

        removeTestFile("file")
        SystemFileSystem.delete(downloadFilePath)
    }

    @Test
    fun testDownloadWithEnableCheckpoint() = runTest {
        val filePath = createTestFile("downloader-test-source-file", 20 * 1024 * 1024)
        defaultClient.putObject(PutObjectRequest {
            bucket = bucketName
            key = objectKey
            body = ByteStream.fromFile(filePath)
        })

        val downloadFilePath = Path("$SystemTemporaryDirectory/OSS/downloader-destination-file")
        val downloader = Downloader(defaultClient, {
            it.enableCheckpoint = true
            it.partSize = 1024 * 1024
        })
        launch {
            val exception: Throwable = assertFails {
                downloader.downloadFile(GetObjectRequest {
                    bucket = bucketName
                    key = objectKey
                    progressListener =
                        ProgressListener { bytesReceive, totalBytesReceive, totalBytesExpectedToReceive ->
                            val percentage =
                                totalBytesReceive.toFloat() / totalBytesExpectedToReceive
                            if (percentage > 0.5) {
                                coroutineContext.job.cancel()
                            }
                        }
                }, downloadFilePath)
            }
            assertTrue(exception.cause is CancellationException)
        }.join()

        val result = downloader.downloadFile(GetObjectRequest {
            bucket = bucketName
            key = objectKey
            progressListener =
                ProgressListener { bytesReceive, totalBytesReceive, totalBytesExpectedToReceive ->
                    val percentage =
                        totalBytesReceive.toFloat() / totalBytesExpectedToReceive
                    assertTrue(percentage >= 0.5)
                }
        }, downloadFilePath)
        assertEquals(20 * 1024 * 1024, result.written)

        removeTestFile("file")
        SystemFileSystem.delete(downloadFilePath)
    }

    @Test
    fun testDownloadWithCheckpointDir() = runTest {
        val filePath = createTestFile("downloader-test-source-file", 20 * 1024 * 1024)
        val baseDir = Path("$SystemTemporaryDirectory/OSS/downloader")
        defaultClient.putObject(PutObjectRequest {
            bucket = bucketName
            key = objectKey
            body = ByteStream.fromFile(filePath)
        })

        val downloadFilePath = Path("$SystemTemporaryDirectory/OSS/downloader-destination-file")
        val downloader = Downloader(defaultClient, {
            it.enableCheckpoint = true
            it.partSize = 1024 * 1024
            it.checkpointDir = baseDir
        })
        launch {
            val exception: Throwable = assertFails {
                downloader.downloadFile(GetObjectRequest {
                    bucket = bucketName
                    key = objectKey
                    progressListener =
                        ProgressListener { bytesReceive, totalBytesReceive, totalBytesExpectedToReceive ->
                            val percentage =
                                totalBytesReceive.toFloat() / totalBytesExpectedToReceive
                            if (percentage > 0.5) {
                                coroutineContext.job.cancel()
                            }
                        }
                }, downloadFilePath)
            }
            assertTrue(exception.cause is CancellationException)
            assertTrue(SystemFileSystem.exists(baseDir))
            assertTrue(SystemFileSystem.list(baseDir).size == 1)
        }.join()

        val result = downloader.downloadFile(GetObjectRequest {
            bucket = bucketName
            key = objectKey
            progressListener =
                ProgressListener { bytesReceive, totalBytesReceive, totalBytesExpectedToReceive ->
                    val percentage =
                        totalBytesReceive.toFloat() / totalBytesExpectedToReceive
                    assertTrue(percentage >= 0.5)
                }
        }, downloadFilePath)
        assertEquals(20 * 1024 * 1024, result.written)
        assertTrue(SystemFileSystem.list(baseDir).isEmpty())

        removeTestFile("file")
        SystemFileSystem.delete(downloadFilePath)
    }

    @Test
    fun testDownloadWithAbort() = runTest {
        val filePath = createTestFile("downloader-test-source-file", 20 * 1024 * 1024)
        val baseDir = Path("$SystemTemporaryDirectory/OSS/downloader")
        defaultClient.putObject(PutObjectRequest {
            bucket = bucketName
            key = objectKey
            body = ByteStream.fromFile(filePath)
        })

        val downloadFilePath = Path("$SystemTemporaryDirectory/OSS/downloader-destination-file")
        val downloader = Downloader(defaultClient, {
            it.enableCheckpoint = true
            it.partSize = 1024 * 1024
            it.checkpointDir = baseDir
        })
        launch {
            val exception: Throwable = assertFails {
                downloader.downloadFile(GetObjectRequest {
                    bucket = bucketName
                    key = objectKey
                    progressListener =
                        ProgressListener { bytesReceive, totalBytesReceive, totalBytesExpectedToReceive ->
                            val percentage =
                                totalBytesReceive.toFloat() / totalBytesExpectedToReceive
                            if (percentage > 0.5) {
                                coroutineContext.job.cancel()
                            }
                        }
                }, downloadFilePath)
            }
            assertTrue(exception.cause is CancellationException)
            assertTrue {
                SystemFileSystem.exists(downloadFilePath) ||
                    SystemFileSystem.exists(Path("$downloadFilePath.temp"))
            }
        }.join()

        downloader.abortDownload(GetObjectRequest {
            bucket = bucketName
            key = objectKey
            progressListener =
                ProgressListener { bytesReceive, totalBytesReceive, totalBytesExpectedToReceive ->
                    val percentage =
                        totalBytesReceive.toFloat() / totalBytesExpectedToReceive
                    assertTrue(percentage >= 0.5)
                }
        }, downloadFilePath)
        assertFalse(SystemFileSystem.exists(downloadFilePath))
        assertFalse(SystemFileSystem.exists(Path("$downloadFilePath.temp")))

        removeTestFile("file")
    }

    @Test
    fun testDownloadWithException() = runTest {
        val key = randomObjectKey()
        val downloadFilePath = Path("$SystemTemporaryDirectory/OSS/downloader-destination-file")

        val downloader = Downloader(defaultClient)
        var exception: Throwable =
            assertFailsWith<IllegalArgumentException> { downloader.downloadFile(GetObjectRequest {}, downloadFilePath) }
        assertEquals(exception.message, "request.bucket is required")

        exception = assertFailsWith<IllegalArgumentException> {
            downloader.downloadFile(GetObjectRequest {
                bucket = bucketName
            }, downloadFilePath)
        }
        assertEquals(exception.message, "request.key is required")

        exception = assertFails {
            Downloader(invalidClient).downloadFile(GetObjectRequest {
                bucket = bucketName
                this.key = key
            }, downloadFilePath)
        }
        assertTrue { exception.cause is ServiceException }
        assertEquals((exception.cause as ServiceException).statusCode, 403)
        assertEquals((exception.cause as ServiceException).errorCode, "InvalidAccessKeyId")
    }
}
