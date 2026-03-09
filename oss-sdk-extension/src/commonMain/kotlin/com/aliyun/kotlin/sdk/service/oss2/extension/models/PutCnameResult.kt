package com.aliyun.kotlin.sdk.service.oss2.extension.models

/**
 * The result for the PutCname operation.
 */
public class PutCnameResult(builder: Builder): ResultModel(builder) { 

    public companion object {
        public operator fun invoke(builder: Builder.() -> Unit): PutCnameResult =
            Builder().apply(builder).build()
    }

    public class Builder: ResultModel.Builder() {
        public fun build(): PutCnameResult {
            return PutCnameResult(this)
        }
    }
}
