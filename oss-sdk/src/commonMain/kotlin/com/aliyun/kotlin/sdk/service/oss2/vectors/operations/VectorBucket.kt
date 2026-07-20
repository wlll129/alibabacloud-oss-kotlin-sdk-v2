package com.aliyun.kotlin.sdk.service.oss2.vectors.operations

import com.aliyun.kotlin.sdk.service.oss2.OperationInput
import com.aliyun.kotlin.sdk.service.oss2.OperationMetadataKey.Companion.SUB_RESOURCE
import com.aliyun.kotlin.sdk.service.oss2.OperationOptions
import com.aliyun.kotlin.sdk.service.oss2.internal.ClientImpl
import com.aliyun.kotlin.sdk.service.oss2.models.DeleteBucketRequest
import com.aliyun.kotlin.sdk.service.oss2.models.DeleteBucketResult
import com.aliyun.kotlin.sdk.service.oss2.models.DeleteBucketResult.Companion.invoke
import com.aliyun.kotlin.sdk.service.oss2.transform.SerdeUtils
import com.aliyun.kotlin.sdk.service.oss2.transform.SerdeUtils.addContentMd5
import com.aliyun.kotlin.sdk.service.oss2.transform.SerdeUtils.serializeInput
import com.aliyun.kotlin.sdk.service.oss2.transform.fromXmlBucketInfo
import com.aliyun.kotlin.sdk.service.oss2.transform.fromXmlListAllMyBucketsResult
import com.aliyun.kotlin.sdk.service.oss2.types.toByteArray
import com.aliyun.kotlin.sdk.service.oss2.utils.MapUtils
import com.aliyun.kotlin.sdk.service.oss2.vectors.models.BucketInfo
import com.aliyun.kotlin.sdk.service.oss2.vectors.models.DeleteVectorBucketRequest
import com.aliyun.kotlin.sdk.service.oss2.vectors.models.DeleteVectorBucketResult
import com.aliyun.kotlin.sdk.service.oss2.vectors.models.GetVectorBucketRequest
import com.aliyun.kotlin.sdk.service.oss2.vectors.models.GetVectorBucketResult
import com.aliyun.kotlin.sdk.service.oss2.vectors.models.GetVectorBucketResultJson
import com.aliyun.kotlin.sdk.service.oss2.vectors.models.ListAllMyBucketsResult
import com.aliyun.kotlin.sdk.service.oss2.vectors.models.ListVectorBucketsRequest
import com.aliyun.kotlin.sdk.service.oss2.vectors.models.ListVectorBucketsResult
import com.aliyun.kotlin.sdk.service.oss2.vectors.models.ListVectorBucketsResultJson
import com.aliyun.kotlin.sdk.service.oss2.vectors.models.PutVectorBucketRequest
import com.aliyun.kotlin.sdk.service.oss2.vectors.models.PutVectorBucketResult

internal object VectorBucket {

    internal suspend fun putVectorBucket(impl: ClientImpl, request: PutVectorBucketRequest, options: OperationOptions?): PutVectorBucketResult {
        requireNotNull(request.bucket) { "request.bucket is required" }

        val input = OperationInput {
            opName = "PutVectorBucket"
            method = "PUT"
            // default headers
            headers = MapUtils.headersMap().apply {
                put("Content-Type", "application/xml")
            }
            bucket = request.bucket
        }

        // opMetadata

        serializeInput(request, input) {
            addContentMd5(this)
        }

        val output = impl.execute(input, options)

        return PutVectorBucketResult {
            headers = output.headers
            status = output.status
            statusCode = output.statusCode
        }
    }

    internal suspend fun getVectorBucket(impl: ClientImpl, request: GetVectorBucketRequest, options: OperationOptions?): GetVectorBucketResult {
        requireNotNull(request.bucket) { "request.bucket is required" }

        val input = OperationInput {
            opName = "GetVectorBucket"
            method = "GET"
            // default headers
            headers = MapUtils.headersMap().apply {
                put("Content-Type", "application/xml")
            }
            // parameters
            parameters = MapUtils.parametersMap().apply {
                put("bucketInfo", "")
            }
            bucket = request.bucket
        }

        // opMetadata
        input.opMetadata[SUB_RESOURCE] = listOf("bucketInfo")

        serializeInput(request, input) {
            addContentMd5(this)
        }

        val output = impl.execute(input, options)
        val body = output.body?.toByteArray()

        return GetVectorBucketResult {
            headers = output.headers
            status = output.status
            statusCode = output.statusCode
            innerBody = SerdeUtils.deserializeJsonBody<GetVectorBucketResultJson>(body).bucketInfo
        }
    }

    internal suspend fun listVectorBuckets(impl: ClientImpl, request: ListVectorBucketsRequest, options: OperationOptions?): ListVectorBucketsResult {
        val input = OperationInput {
            opName = "ListVectorBuckets"
            method = "GET"
            // default headers
            headers = MapUtils.headersMap().apply {
                put("Content-Type", "application/xml")
            }
        }

        // opMetadata

        serializeInput(request, input) {
            addContentMd5(this)
        }

        val output = impl.execute(input, options)
        val body = output.body?.toByteArray()

        return ListVectorBucketsResult {
            headers = output.headers
            status = output.status
            statusCode = output.statusCode
            innerBody = SerdeUtils.deserializeJsonBody<ListVectorBucketsResultJson>(body).result
        }
    }

    internal suspend fun deleteVectorBucket(impl: ClientImpl, request: DeleteVectorBucketRequest, options: OperationOptions?): DeleteVectorBucketResult {
        requireNotNull(request.bucket) { "request.bucket is required" }

        val input = OperationInput {
            opName = "DeleteBucket"
            method = "DELETE"
            // default headers
            headers = MapUtils.headersMap().apply {
                put("Content-Type", "application/xml")
            }
            bucket = request.bucket
        }

        // opMetadata

        serializeInput(request, input) {
            addContentMd5(this)
        }

        val output = impl.execute(input, options)

        return DeleteVectorBucketResult {
            headers = output.headers
            status = output.status
            statusCode = output.statusCode
        }
    }
}
