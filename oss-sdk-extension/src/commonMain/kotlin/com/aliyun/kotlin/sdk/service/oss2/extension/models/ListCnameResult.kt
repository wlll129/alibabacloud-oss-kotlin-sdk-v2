package com.aliyun.kotlin.sdk.service.oss2.extension.models

/**
 * The result for the ListCname operation.
 */
public class ListCnameResult(builder: Builder): ResultModel(builder) {

    internal val delegate = innerBody as? ListCnameResultXml

    /**
     * The name of the bucket to which the CNAME records you want to query are mapped.
     */
    public val bucket: String?
        get() = delegate?.bucket
    
    /**
     * The name of the bucket owner.
     */
    public val owner: String?
        get() = delegate?.owner
    
    /**
     * The container that is used to store the information about all CNAME records.
     */
    public val cnames: List<CnameInfo>?
        get() = delegate?.cnames
     

    public companion object {
        public operator fun invoke(builder: Builder.() -> Unit): ListCnameResult =
            Builder().apply(builder).build()
    }

    public class Builder: ResultModel.Builder() {
        public fun build(): ListCnameResult {
            return ListCnameResult(this)
        }
    }
}
