package com.aliyun.kotlin.sdk.service.oss2.extension.api


import com.aliyun.kotlin.sdk.service.oss2.extension.models.*
import com.aliyun.kotlin.sdk.service.oss2.OSSClient

import com.aliyun.kotlin.sdk.service.oss2.OperationInput
import com.aliyun.kotlin.sdk.service.oss2.OperationMetadataKey.Companion.SUB_RESOURCE
import com.aliyun.kotlin.sdk.service.oss2.OperationOptions
import com.aliyun.kotlin.sdk.service.oss2.utils.MapUtils
import com.aliyun.kotlin.sdk.service.oss2.extension.api.SerdeUtils.serializeInput
import com.aliyun.kotlin.sdk.service.oss2.extension.api.SerdeUtils.addContentMd5
import com.aliyun.kotlin.sdk.service.oss2.types.toByteArray
import com.aliyun.kotlin.sdk.service.oss2.types.toByteStream


public suspend fun OSSClient.listUserDataRedundancyTransition(request: ListUserDataRedundancyTransitionRequest, options: OperationOptions? = null): ListUserDataRedundancyTransitionResult {
    
    requireNotNull(request.bucket) {"request.bucket is required"}

    val input = OperationInput {
        opName = "ListUserDataRedundancyTransition"
        method = "GET"
        // default headers
        headers = MapUtils.headersMap().apply {
            put("Content-Type", "application/xml")
        }  
        // parameters
        parameters = MapUtils.parametersMap().apply { 
            put("redundancyTransition", "")
        } 
    }

    // opMetadata 
    input.opMetadata[SUB_RESOURCE] = listOf("redundancyTransition")
    
    
    serializeInput(request, input) { 
        addContentMd5(this)
    }

    val output = this.invokeOperation(input, options)
    val body = output.body?.toByteArray()

    return ListUserDataRedundancyTransitionResult {
        headers = output.headers
        status = output.status
        statusCode = output.statusCode 
        innerBody = SerdeUtils.deserializeXmlBody<ListBucketDataRedundancyTransitionXml>(body)
    }
}

public suspend fun OSSClient.listBucketDataRedundancyTransition(request: ListBucketDataRedundancyTransitionRequest, options: OperationOptions? = null): ListBucketDataRedundancyTransitionResult {

    requireNotNull(request.bucket) {"request.bucket is required"}

    val input = OperationInput {
        opName = "ListBucketDataRedundancyTransition"
        method = "GET"
        // default headers
        headers = MapUtils.headersMap().apply {
            put("Content-Type", "application/xml")
        }
        // parameters
        parameters = MapUtils.parametersMap().apply {
            put("redundancyTransition", "")
        }
        bucket = request.bucket
    }

    // opMetadata
    input.opMetadata[SUB_RESOURCE] = listOf("redundancyTransition")


    serializeInput(request, input) {
        addContentMd5(this)
    }

    val output = this.invokeOperation(input, options)
    val body = output.body?.toByteArray()

    return ListBucketDataRedundancyTransitionResult {
        headers = output.headers
        status = output.status
        statusCode = output.statusCode
        innerBody = SerdeUtils.deserializeXmlBody<ListBucketDataRedundancyTransition>(body)
    }
}

public suspend fun OSSClient.getBucketDataRedundancyTransition(request: GetBucketDataRedundancyTransitionRequest, options: OperationOptions? = null): GetBucketDataRedundancyTransitionResult {
    
    requireNotNull(request.bucket) {"request.bucket is required"}
    requireNotNull(request.redundancyTransitionTaskid) {"request.redundancyTransitionTaskid is required"}

    val input = OperationInput {
        opName = "GetBucketDataRedundancyTransition"
        method = "GET"
        // default headers
        headers = MapUtils.headersMap().apply {
            put("Content-Type", "application/xml")
        }  
        // parameters
        parameters = MapUtils.parametersMap().apply { 
            put("redundancyTransition", "")
        } 
        bucket = request.bucket 
    }

    // opMetadata 
    input.opMetadata[SUB_RESOURCE] = listOf("redundancyTransition")
    
    
    serializeInput(request, input) { 
        addContentMd5(this)
    }

    val output = this.invokeOperation(input, options)
    val body = output.body?.toByteArray()

    return GetBucketDataRedundancyTransitionResult {
        headers = output.headers
        status = output.status
        statusCode = output.statusCode 
        innerBody = SerdeUtils.deserializeXmlBody<BucketDataRedundancyTransition>(body)
    }
}

public suspend fun OSSClient.createBucketDataRedundancyTransition(request: CreateBucketDataRedundancyTransitionRequest, options: OperationOptions? = null): CreateBucketDataRedundancyTransitionResult {
    
    requireNotNull(request.bucket) {"request.bucket is required"}
    requireNotNull(request.targetRedundancyType) {"request.targetRedundancyType is required"}

    val input = OperationInput {
        opName = "CreateBucketDataRedundancyTransition"
        method = "POST"
        // default headers
        headers = MapUtils.headersMap().apply {
            put("Content-Type", "application/xml")
        }  
        // parameters
        parameters = MapUtils.parametersMap().apply { 
            put("redundancyTransition", "")
        } 
        bucket = request.bucket 
    }

    // opMetadata 
    input.opMetadata[SUB_RESOURCE] = listOf("redundancyTransition")
    
    
    serializeInput(request, input) { 
        addContentMd5(this)
    }

    val output = this.invokeOperation(input, options)
    val body = output.body?.toByteArray()

    return CreateBucketDataRedundancyTransitionResult {
        headers = output.headers
        status = output.status
        statusCode = output.statusCode 
        innerBody = SerdeUtils.deserializeXmlBody<BucketDataRedundancyTransition>(body)
    }
}

public suspend fun OSSClient.deleteBucketDataRedundancyTransition(request: DeleteBucketDataRedundancyTransitionRequest, options: OperationOptions? = null): DeleteBucketDataRedundancyTransitionResult {
    
    requireNotNull(request.bucket) {"request.bucket is required"}

    val input = OperationInput {
        opName = "DeleteBucketDataRedundancyTransition"
        method = "DELETE"
        // default headers
        headers = MapUtils.headersMap().apply {
            put("Content-Type", "application/xml")
        }  
        // parameters
        parameters = MapUtils.parametersMap().apply { 
            put("redundancyTransition", "")
        } 
        bucket = request.bucket 
    }

    // opMetadata 
    input.opMetadata[SUB_RESOURCE] = listOf("redundancyTransition")
    
    
    serializeInput(request, input) { 
        addContentMd5(this)
    }

    val output = this.invokeOperation(input, options)

    return DeleteBucketDataRedundancyTransitionResult {
        headers = output.headers
        status = output.status
        statusCode = output.statusCode 
    }
}

