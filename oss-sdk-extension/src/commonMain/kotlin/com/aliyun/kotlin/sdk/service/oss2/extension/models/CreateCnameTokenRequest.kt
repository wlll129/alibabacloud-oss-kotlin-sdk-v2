package com.aliyun.kotlin.sdk.service.oss2.extension.models

/**
 * The request for the CreateCnameToken operation.
 */
public class CreateCnameTokenRequest(builder: Builder): RequestModel(builder) {
    
    /**
     * The name of the bucket.
     */
    public val bucket: String? = builder.bucket
    
    /**
     * The request body schema.
     */
    public var bucketCnameConfiguration: BucketCnameConfiguration? = builder.bucketCnameConfiguration
    

    public inline fun copy(block: Builder.() -> Unit = {}): CreateCnameTokenRequest = Builder(this).apply(block).build()

    public companion object {
        public operator fun invoke(builder: Builder.() -> Unit): CreateCnameTokenRequest =
            Builder().apply(builder).build()
    }

    public class Builder(): RequestModel.Builder() {
    
        /**
        * The name of the bucket.
        */
        public var bucket: String? = null
    
        /**
        * The request body schema.
        */
        public var bucketCnameConfiguration: BucketCnameConfiguration? = null
    
        
        public fun build(): CreateCnameTokenRequest {
            return CreateCnameTokenRequest(this)
        }

        public constructor(from: CreateCnameTokenRequest): this() {
            this.headers.putAll(from.headers)
            this.parameters.putAll(from.parameters) 
            this.bucket = from.bucket 
            this.bucketCnameConfiguration = from.bucketCnameConfiguration 
        }             
    }

}
