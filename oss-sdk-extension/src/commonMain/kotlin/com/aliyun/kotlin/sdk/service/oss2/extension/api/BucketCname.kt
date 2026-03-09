package com.aliyun.kotlin.sdk.service.oss2.extension.api


import com.aliyun.kotlin.sdk.service.oss2.OSSClient
import com.aliyun.kotlin.sdk.service.oss2.OperationInput
import com.aliyun.kotlin.sdk.service.oss2.OperationMetadataKey.Companion.SUB_RESOURCE
import com.aliyun.kotlin.sdk.service.oss2.OperationOptions
import com.aliyun.kotlin.sdk.service.oss2.extension.api.SerdeUtils.addContentMd5
import com.aliyun.kotlin.sdk.service.oss2.extension.api.SerdeUtils.serializeInput
import com.aliyun.kotlin.sdk.service.oss2.extension.models.CnameToken
import com.aliyun.kotlin.sdk.service.oss2.extension.models.CreateCnameTokenRequest
import com.aliyun.kotlin.sdk.service.oss2.extension.models.CreateCnameTokenResult
import com.aliyun.kotlin.sdk.service.oss2.extension.models.DeleteCnameRequest
import com.aliyun.kotlin.sdk.service.oss2.extension.models.DeleteCnameResult
import com.aliyun.kotlin.sdk.service.oss2.extension.models.GetCnameTokenRequest
import com.aliyun.kotlin.sdk.service.oss2.extension.models.GetCnameTokenResult
import com.aliyun.kotlin.sdk.service.oss2.extension.models.ListCnameRequest
import com.aliyun.kotlin.sdk.service.oss2.extension.models.ListCnameResult
import com.aliyun.kotlin.sdk.service.oss2.extension.models.ListCnameResultXml
import com.aliyun.kotlin.sdk.service.oss2.extension.models.PutCnameRequest
import com.aliyun.kotlin.sdk.service.oss2.extension.models.PutCnameResult
import com.aliyun.kotlin.sdk.service.oss2.types.toByteArray
import com.aliyun.kotlin.sdk.service.oss2.types.toByteStream
import com.aliyun.kotlin.sdk.service.oss2.utils.MapUtils


public suspend fun OSSClient.putCname(request: PutCnameRequest, options: OperationOptions? = null): PutCnameResult {
    
    val bucket = requireNotNull(request.bucket) {"request.bucket is required"}

    val input = OperationInput {
        opName = "PutCname"
        method = "POST"
        // default headers
        headers = MapUtils.headersMap().apply {
            put("Content-Type", "application/xml")
        }  
        // parameters
        parameters = MapUtils.parametersMap().apply { 
            put("cname", "")
            put("comp", "add")
        } 
        this.bucket = bucket
        // body
        body = SerdeUtils.serializeXmlBody(request.bucketCnameConfiguration).toByteStream()
    }

    // opMetadata 
    input.opMetadata[SUB_RESOURCE] = listOf("cname", "comp")
    
    
    serializeInput(request, input) { 
        addContentMd5(this)
    }

    val output = this.invokeOperation(input, options)

    return PutCnameResult {
        headers = output.headers
        status = output.status
        statusCode = output.statusCode 
    }
}

public suspend fun OSSClient.listCname(request: ListCnameRequest, options: OperationOptions? = null): ListCnameResult {
    
    requireNotNull(request.bucket) {"request.bucket is required"}

    val input = OperationInput {
        opName = "ListCname"
        method = "GET"
        // default headers
        headers = MapUtils.headersMap().apply {
            put("Content-Type", "application/xml")
        }  
        // parameters
        parameters = MapUtils.parametersMap().apply { 
            put("cname", "")
        } 
        bucket = request.bucket 
    }

    // opMetadata 
    input.opMetadata[SUB_RESOURCE] = listOf("cname")
    
    
    serializeInput(request, input) { 
        addContentMd5(this)
    }

    val output = this.invokeOperation(input, options)
    val body = output.body?.toByteArray()

    return ListCnameResult {
        headers = output.headers
        status = output.status
        statusCode = output.statusCode 
        innerBody = SerdeUtils.deserializeXmlBody<ListCnameResultXml>(body)
    }
}

public suspend fun OSSClient.deleteCname(request: DeleteCnameRequest, options: OperationOptions? = null): DeleteCnameResult {
    
    requireNotNull(request.bucket) {"request.bucket is required"}

    val input = OperationInput {
        opName = "DeleteCname"
        method = "POST"
        // default headers
        headers = MapUtils.headersMap().apply {
            put("Content-Type", "application/xml")
        }  
        // parameters
        parameters = MapUtils.parametersMap().apply { 
            put("cname", "")
            put("comp", "delete")
        } 
        bucket = request.bucket 
        // body
        body = SerdeUtils.serializeXmlBody(request.bucketCnameConfiguration).toByteStream()
    }

    // opMetadata 
    input.opMetadata[SUB_RESOURCE] = listOf("cname", "comp")
    
    
    serializeInput(request, input) { 
        addContentMd5(this)
    }

    val output = this.invokeOperation(input, options)

    return DeleteCnameResult {
        headers = output.headers
        status = output.status
        statusCode = output.statusCode 
    }
}

public suspend fun OSSClient.getCnameToken(request: GetCnameTokenRequest, options: OperationOptions? = null): GetCnameTokenResult {
    
    requireNotNull(request.bucket) {"request.bucket is required"}
    val cname = requireNotNull(request.cname) {"request.cname is required"}

    val input = OperationInput {
        opName = "GetCnameToken"
        method = "GET"
        // default headers
        headers = MapUtils.headersMap().apply {
            put("Content-Type", "application/xml")
        }  
        // parameters
        parameters = MapUtils.parametersMap().apply { 
            put("comp", "token")
            put("cname", cname)
        } 
        bucket = request.bucket 
    }

    // opMetadata 
    input.opMetadata[SUB_RESOURCE] = listOf("comp")
    
    
    serializeInput(request, input) { 
        addContentMd5(this)
    }

    val output = this.invokeOperation(input, options)
    val body = output.body?.toByteArray()

    return GetCnameTokenResult {
        headers = output.headers
        status = output.status
        statusCode = output.statusCode 
        innerBody = SerdeUtils.deserializeXmlBody<CnameToken>(body)
    }
}

public suspend fun OSSClient.createCnameToken(request: CreateCnameTokenRequest, options: OperationOptions? = null): CreateCnameTokenResult {
    
    requireNotNull(request.bucket) {"request.bucket is required"}
    requireNotNull(request.bucketCnameConfiguration) {"request.bucketCnameConfiguration is required"}

    val input = OperationInput {
        opName = "CreateCnameToken"
        method = "POST"
        // default headers
        headers = MapUtils.headersMap().apply {
            put("Content-Type", "application/xml")
        }  
        // parameters
        parameters = MapUtils.parametersMap().apply { 
            put("cname", "")
            put("comp", "token")
        } 
        bucket = request.bucket 
        // body
        body = SerdeUtils.serializeXmlBody(request.bucketCnameConfiguration).toByteStream()
    }

    // opMetadata 
    input.opMetadata[SUB_RESOURCE] = listOf("cname", "comp")
    
    
    serializeInput(request, input) { 
        addContentMd5(this)
    }

    val output = this.invokeOperation(input, options)
    val body = output.body?.toByteArray()

    return CreateCnameTokenResult {
        headers = output.headers
        status = output.status
        statusCode = output.statusCode 
        innerBody = SerdeUtils.deserializeXmlBody<CnameToken>(body)
    }
}

