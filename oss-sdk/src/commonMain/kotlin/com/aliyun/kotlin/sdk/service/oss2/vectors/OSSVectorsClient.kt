package com.aliyun.kotlin.sdk.service.oss2.vectors

import com.aliyun.kotlin.sdk.service.oss2.ClientConfiguration
import com.aliyun.kotlin.sdk.service.oss2.ClientOptions
import com.aliyun.kotlin.sdk.service.oss2.OperationInput
import com.aliyun.kotlin.sdk.service.oss2.OperationOptions
import com.aliyun.kotlin.sdk.service.oss2.OperationOutput
import com.aliyun.kotlin.sdk.service.oss2.vectors.models.DeleteVectorBucketRequest
import com.aliyun.kotlin.sdk.service.oss2.vectors.models.DeleteVectorBucketResult
import com.aliyun.kotlin.sdk.service.oss2.vectors.models.GetVectorBucketRequest
import com.aliyun.kotlin.sdk.service.oss2.vectors.models.GetVectorBucketResult
import com.aliyun.kotlin.sdk.service.oss2.vectors.models.ListVectorBucketsRequest
import com.aliyun.kotlin.sdk.service.oss2.vectors.models.ListVectorBucketsResult
import com.aliyun.kotlin.sdk.service.oss2.vectors.models.PutVectorBucketRequest
import com.aliyun.kotlin.sdk.service.oss2.vectors.models.PutVectorBucketResult

public interface OSSVectorsClient : AutoCloseable {

    public companion object {

        /**
         * Creates an instance that implements the OSSVectorsClient interface.
         *
         * @param config A [ClientConfiguration]
         * @param optFns The options.
         * @return A [OSSVectorsClient]
         */
        public fun create(
            config: ClientConfiguration,
            optFns: List<(ClientOptions) -> ClientOptions>? = null
        ): OSSVectorsClient {
            return DefaultOSSVectorsClient(config, optFns)
        }
    }

    // common api

    /**
     * Invoke operation
     *
     * @param input A [OperationInput] for common operation.
     * @param options The operation options.
     * @return A [OperationOutput] for common operation.
     * @throws RuntimeException If an error occurs
     */
    public suspend fun invokeOperation(input: OperationInput, options: OperationOptions? = null): OperationOutput {
        throw UnsupportedOperationException()
    }

    /**
     * Creates a vector bucket.
     *
     * @param request A [PutVectorBucketRequest] for PutVectorBucket operation.
     * @param options The operation options.
     * @return A [PutVectorBucketResult] for PutVectorBucket operation.
     * @throws RuntimeException If an error occurs
     */
    public suspend fun putVectorBucket(request: PutVectorBucketRequest, options: OperationOptions? = null): PutVectorBucketResult {
        throw UnsupportedOperationException()
    }

    /**
     * Queries the information about a vector bucket.
     *
     * @param request A [GetVectorBucketRequest] for GetVectorBucket operation.
     * @param options The operation options.
     * @return A [GetVectorBucketResult] for GetVectorBucket operation.
     * @throws RuntimeException If an error occurs
     */
    public suspend fun getVectorBucket(request: GetVectorBucketRequest, options: OperationOptions? = null): GetVectorBucketResult {
        throw UnsupportedOperationException()
    }

    /**
     * Queries the information about a vector bucket.
     *
     * @param request A [ListVectorBucketsRequest] for ListVectorBuckets operation.
     * @param options The operation options.
     * @return A [ListVectorBucketsResult] for ListVectorBuckets operation.
     * @throws RuntimeException If an error occurs
     */
    public suspend fun listVectorBuckets(request: ListVectorBucketsRequest, options: OperationOptions? = null): ListVectorBucketsResult {
        throw UnsupportedOperationException()
    }

    /**
     * Queries the information about a vector bucket.
     *
     * @param request A [DeleteVectorBucketRequest] for DeleteVectorBucket operation.
     * @param options The operation options.
     * @return A [DeleteVectorBucketResult] for DeleteVectorBucket operation.
     * @throws RuntimeException If an error occurs
     */
    public suspend fun deleteVectorBucket(request: DeleteVectorBucketRequest, options: OperationOptions? = null): DeleteVectorBucketResult {
        throw UnsupportedOperationException()
    }
}
