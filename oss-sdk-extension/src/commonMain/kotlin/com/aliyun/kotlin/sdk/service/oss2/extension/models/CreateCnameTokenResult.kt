package com.aliyun.kotlin.sdk.service.oss2.extension.models

/**
 * The result for the CreateCnameToken operation.
 */
public class CreateCnameTokenResult(builder: Builder): ResultModel(builder) { 

    /**
     * The container in which the CNAME token is stored.
     */
    public val cnameToken: CnameToken?
        get() = innerBody as? CnameToken
     

    public companion object {
        public operator fun invoke(builder: Builder.() -> Unit): CreateCnameTokenResult =
            Builder().apply(builder).build()
    }

    public class Builder: ResultModel.Builder() {
        public fun build(): CreateCnameTokenResult {
            return CreateCnameTokenResult(this)
        }
    }
}
