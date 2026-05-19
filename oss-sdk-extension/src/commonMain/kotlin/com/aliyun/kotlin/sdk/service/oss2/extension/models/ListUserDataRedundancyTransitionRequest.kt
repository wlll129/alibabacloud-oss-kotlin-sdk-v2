package com.aliyun.kotlin.sdk.service.oss2.extension.models

public class ListUserDataRedundancyTransitionRequest(builder: Builder): RequestModel(builder) {

    /**
     * The name of the bucket.
     */
    public val bucket: String? = builder.bucket

    /**
     * The token from which the list operation must start.
     */
    public val continuationToken: String?
        get() = parameters["continuation-token"]

    /**
     * The maximum number of redundancy type change tasks that can be returned. Valid values: 1 to 100.
     */
    public val maxKeys: Int?
        get() = parameters["max-keys"]?.toInt()

    public inline fun copy(block: Builder.() -> Unit = {}): ListUserDataRedundancyTransitionRequest = Builder(this).apply(block).build()

    public companion object {
        public operator fun invoke(builder: Builder.() -> Unit): ListUserDataRedundancyTransitionRequest =
            Builder().apply(builder).build()
    }

    public class Builder(): RequestModel.Builder() {

        /**
         * The name of the bucket.
         */
        public var bucket: String? = null

        /**
         * The token from which the list operation must start.
         */
        public var continuationToken: String?
            get() = parameters["continuation-token"]
            set(value) {
                value?.let { this.parameters["continuation-token"] = it }
            }

        /**
         * The maximum number of redundancy type change tasks that can be returned. Valid values: 1 to 100.
         */
        public var maxKeys: Int?
            get() = parameters["max-keys"]?.toInt()
            set(value) {
                value?.let { this.parameters["max-keys"] = it.toString() }
            }


        public fun build(): ListUserDataRedundancyTransitionRequest {
            return ListUserDataRedundancyTransitionRequest(this)
        }

        public constructor(from: ListUserDataRedundancyTransitionRequest): this() {
            this.headers.putAll(from.headers)
            this.parameters.putAll(from.parameters)
            this.bucket = from.bucket
        }
    }
}
