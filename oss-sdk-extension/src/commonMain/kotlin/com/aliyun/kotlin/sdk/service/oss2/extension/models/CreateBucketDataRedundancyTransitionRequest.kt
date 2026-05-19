package com.aliyun.kotlin.sdk.service.oss2.extension.models

/**
 * The request for the CreateBucketDataRedundancyTransition operation.
 */
public class CreateBucketDataRedundancyTransitionRequest(builder: Builder): RequestModel(builder) {
    
    /**
     * The name of the bucket.
     */
    public val bucket: String? = builder.bucket
    
    /**
     * The redundancy type to which you want to convert the bucket. You can only convert the redundancy type of a bucket from LRS to ZRS.
     */
    public val targetRedundancyType: String?
        get() = parameters["x-oss-target-redundancy-type"]
    

    public inline fun copy(block: Builder.() -> Unit = {}): CreateBucketDataRedundancyTransitionRequest = Builder(this).apply(block).build()

    public companion object {
        public operator fun invoke(builder: Builder.() -> Unit): CreateBucketDataRedundancyTransitionRequest =
            Builder().apply(builder).build()
    }

    public class Builder(): RequestModel.Builder() {
    
        /**
        * The name of the bucket.
        */
        public var bucket: String? = null
    
        /**
        * The redundancy type to which you want to convert the bucket. You can only convert the redundancy type of a bucket from LRS to ZRS.
        */
        public var targetRedundancyType: String?
            set(value) {this.parameters["x-oss-target-redundancy-type"] = requireNotNull(value)}
            get() = parameters["x-oss-target-redundancy-type"]
    
        
        public fun build(): CreateBucketDataRedundancyTransitionRequest {
            return CreateBucketDataRedundancyTransitionRequest(this)
        }

        public constructor(from: CreateBucketDataRedundancyTransitionRequest): this() {
            this.headers.putAll(from.headers)
            this.parameters.putAll(from.parameters) 
            this.bucket = from.bucket 
        }             
    }

}
