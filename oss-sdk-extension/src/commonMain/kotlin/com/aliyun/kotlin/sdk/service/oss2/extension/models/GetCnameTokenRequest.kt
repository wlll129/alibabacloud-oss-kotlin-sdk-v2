package com.aliyun.kotlin.sdk.service.oss2.extension.models

/**
 * The request for the GetCnameToken operation.
 */
public class GetCnameTokenRequest(builder: Builder): RequestModel(builder) {
    
    /**
     * The name of the bucket.
     */
    public val bucket: String? = builder.bucket
    
    /**
     * The name of the CNAME record that is mapped to the bucket.
     */
    public val cname: String?
        get() = parameters["cname"]
    

    public inline fun copy(block: Builder.() -> Unit = {}): GetCnameTokenRequest = Builder(this).apply(block).build()

    public companion object {
        public operator fun invoke(builder: Builder.() -> Unit): GetCnameTokenRequest =
            Builder().apply(builder).build()
    }

    public class Builder(): RequestModel.Builder() {
    
        /**
        * The name of the bucket.
        */
        public var bucket: String? = null
    
        /**
        * The name of the CNAME record that is mapped to the bucket.
        */
        public var cname: String?
            set(value) {this.parameters["cname"] = requireNotNull(value)}
            get() = parameters["cname"]
    
        
        public fun build(): GetCnameTokenRequest {
            return GetCnameTokenRequest(this)
        }

        public constructor(from: GetCnameTokenRequest): this() {
            this.headers.putAll(from.headers)
            this.parameters.putAll(from.parameters) 
            this.bucket = from.bucket 
        }             
    }

}
