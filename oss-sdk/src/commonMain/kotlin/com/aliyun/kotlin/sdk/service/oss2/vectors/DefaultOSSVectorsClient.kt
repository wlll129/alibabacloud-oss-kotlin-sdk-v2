package com.aliyun.kotlin.sdk.service.oss2.vectors

import com.aliyun.kotlin.sdk.service.oss2.ClientConfiguration
import com.aliyun.kotlin.sdk.service.oss2.ClientOptions
import com.aliyun.kotlin.sdk.service.oss2.OperationInput
import com.aliyun.kotlin.sdk.service.oss2.OperationOptions
import com.aliyun.kotlin.sdk.service.oss2.OperationOutput
import com.aliyun.kotlin.sdk.service.oss2.internal.ClientImpl
import com.aliyun.kotlin.sdk.service.oss2.signer.VectorsSignerV4
import com.aliyun.kotlin.sdk.service.oss2.types.AddressStyleType
import com.aliyun.kotlin.sdk.service.oss2.vectors.models.DeleteVectorBucketRequest
import com.aliyun.kotlin.sdk.service.oss2.vectors.models.DeleteVectorBucketResult
import com.aliyun.kotlin.sdk.service.oss2.vectors.models.GetVectorBucketRequest
import com.aliyun.kotlin.sdk.service.oss2.vectors.models.GetVectorBucketResult
import com.aliyun.kotlin.sdk.service.oss2.vectors.models.ListVectorBucketsRequest
import com.aliyun.kotlin.sdk.service.oss2.vectors.models.ListVectorBucketsResult
import com.aliyun.kotlin.sdk.service.oss2.vectors.models.PutVectorBucketRequest
import com.aliyun.kotlin.sdk.service.oss2.vectors.models.PutVectorBucketResult
import com.aliyun.kotlin.sdk.service.oss2.vectors.operations.VectorBucket


public class DefaultOSSVectorsClient(
    config: ClientConfiguration,
    optFns: List<(ClientOptions) -> ClientOptions>? = null
) : OSSVectorsClient {
    private val clientImpl: ClientImpl

    override fun close() {
        clientImpl.close()
    }

    init {
        var config = updateEndpoint(config)
        config = updateSinger(config)
        config = updateUserAgent(config)
        val mutOptFns = optFns?.toMutableList() ?: mutableListOf()
        mutOptFns.add { options ->
            options.copy {
                addressStyle = AddressStyleType.VectorHosted(config.accountId)
            }
        }
        clientImpl = ClientImpl(config, mutOptFns)
    }

    private companion object Companion {
        private fun updateEndpoint(config: ClientConfiguration): ClientConfiguration {
            if (config.endpoint != null) {
                return config
            }

            val endpoint = config.region?.let {
                if (config.useInternalEndpoint == true) {
                    "$it-internal.oss-vectors.aliyuncs.com"
                } else {
                    "$it.oss-vectors.aliyuncs.com"
                }
            }
            return config.apply {
                this.endpoint = endpoint
            }
        }

        fun updateSinger(config: ClientConfiguration): ClientConfiguration {
            return config.apply {
                signer = VectorsSignerV4(config.accountId)
            }
        }

        fun updateUserAgent(config: ClientConfiguration): ClientConfiguration {
            var userAgent = "vectors-client"
            config.userAgent?.let {
                userAgent += "/$it"
            }
            return config.apply {
                this.userAgent = userAgent
            }
        }
    }

    override suspend fun invokeOperation(
        input: OperationInput,
        options: OperationOptions?
    ): OperationOutput {
        return clientImpl.execute(input, options)
    }

    override suspend fun putVectorBucket(
        request: PutVectorBucketRequest,
        options: OperationOptions?
    ): PutVectorBucketResult {
        return VectorBucket.putVectorBucket(clientImpl, request, options)
    }

    override suspend fun getVectorBucket(
        request: GetVectorBucketRequest,
        options: OperationOptions?
    ): GetVectorBucketResult {
        return VectorBucket.getVectorBucket(clientImpl, request, options)
    }

    override suspend fun listVectorBuckets(
        request: ListVectorBucketsRequest,
        options: OperationOptions?
    ): ListVectorBucketsResult {
        return VectorBucket.listVectorBuckets(clientImpl, request, options)
    }

    override suspend fun deleteVectorBucket(
        request: DeleteVectorBucketRequest,
        options: OperationOptions?
    ): DeleteVectorBucketResult {
        return VectorBucket.deleteVectorBucket(clientImpl, request, options)
    }
}
