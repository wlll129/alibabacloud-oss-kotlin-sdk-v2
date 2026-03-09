package com.aliyun.kotlin.sdk.service.oss2.extension.models

/**
 * The result for the DeleteCname operation.
 */
public class DeleteCnameResult(builder: Builder): ResultModel(builder) { 

    public companion object {
        public operator fun invoke(builder: Builder.() -> Unit): DeleteCnameResult =
            Builder().apply(builder).build()
    }

    public class Builder: ResultModel.Builder() {
        public fun build(): DeleteCnameResult {
            return DeleteCnameResult(this)
        }
    }
}
