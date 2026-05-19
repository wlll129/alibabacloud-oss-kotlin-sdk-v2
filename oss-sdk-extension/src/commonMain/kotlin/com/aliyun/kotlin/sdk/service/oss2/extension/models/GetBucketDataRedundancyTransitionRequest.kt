package com.aliyun.kotlin.sdk.service.oss2.extension.models

/**
 * The request for the GetBucketDataRedundancyTransition operation.
 */
public class GetBucketDataRedundancyTransitionRequest(builder: Builder): RequestModel(builder) {
    
    /**
     * The name of the bucket.
     */
    public val bucket: String? = builder.bucket
    
    /**
     * The ID of the redundancy change task.
     */
    public val redundancyTransitionTaskid: String?
        get() = parameters["x-oss-redundancy-transition-taskid"]
    

    public inline fun copy(block: Builder.() -> Unit = {}): GetBucketDataRedundancyTransitionRequest = Builder(this).apply(block).build()

    public companion object {
        public operator fun invoke(builder: Builder.() -> Unit): GetBucketDataRedundancyTransitionRequest =
            Builder().apply(builder).build()
    }

    public class Builder(): RequestModel.Builder() {
    
        /**
        * The name of the bucket.
        */
        public var bucket: String? = null
    
        /**
        * The ID of the redundancy change task.
        */
        public var redundancyTransitionTaskid: String?
            set(value) {this.parameters["x-oss-redundancy-transition-taskid"] = requireNotNull(value)}
            get() = parameters["x-oss-redundancy-transition-taskid"]
    
        
        public fun build(): GetBucketDataRedundancyTransitionRequest {
            return GetBucketDataRedundancyTransitionRequest(this)
        }

        public constructor(from: GetBucketDataRedundancyTransitionRequest): this() {
            this.headers.putAll(from.headers)
            this.parameters.putAll(from.parameters) 
            this.bucket = from.bucket 
        }             
    }

}
