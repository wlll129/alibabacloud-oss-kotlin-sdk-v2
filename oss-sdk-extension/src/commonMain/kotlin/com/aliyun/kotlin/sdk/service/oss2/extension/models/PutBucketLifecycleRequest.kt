package com.aliyun.kotlin.sdk.service.oss2.extension.models

/**
 * The request for the PutBucketLifecycle operation.
 */
public class PutBucketLifecycleRequest(builder: Builder): RequestModel(builder) {
    
    /**
     * The name of the bucket.
     */
    public val bucket: String? = builder.bucket
    
    /**
     * Specifies whether to allow overlapped prefixes. Valid values:true: Overlapped prefixes are allowed.false: Overlapped prefixes are not allowed.
     */
    public val allowSameActionOverlap: String?
        get() = headers["x-oss-allow-same-action-overlap"]
    
    /**
     * The container of the request body.
     */
    public var lifecycleConfiguration: LifecycleConfiguration? = builder.lifecycleConfiguration
    

    public inline fun copy(block: Builder.() -> Unit = {}): PutBucketLifecycleRequest = Builder(this).apply(block).build()

    public companion object {
        public operator fun invoke(builder: Builder.() -> Unit): PutBucketLifecycleRequest =
            Builder().apply(builder).build()
    }

    public class Builder(): RequestModel.Builder() {
    
        /**
        * The name of the bucket.
        */
        public var bucket: String? = null
    
        /**
        * Specifies whether to allow overlapped prefixes. Valid values:true: Overlapped prefixes are allowed.false: Overlapped prefixes are not allowed.
        */
        public var allowSameActionOverlap: String?
            set(value) {this.headers["x-oss-allow-same-action-overlap"] = requireNotNull(value)}
            get() = headers["x-oss-allow-same-action-overlap"]
    
        /**
        * The container of the request body.
        */
        public var lifecycleConfiguration: LifecycleConfiguration? = null
    
        
        public fun build(): PutBucketLifecycleRequest {
            return PutBucketLifecycleRequest(this)
        }

        public constructor(from: PutBucketLifecycleRequest): this() {
            this.headers.putAll(from.headers)
            this.parameters.putAll(from.parameters) 
            this.bucket = from.bucket 
            this.lifecycleConfiguration = from.lifecycleConfiguration 
        }             
    }

}
