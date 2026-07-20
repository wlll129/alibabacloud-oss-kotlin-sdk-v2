package com.aliyun.kotlin.sdk.service.oss2.types

public sealed class AddressStyleType {
    public object VirtualHosted: AddressStyleType()
    public object Path: AddressStyleType()
    public object CName: AddressStyleType()
    public data class VectorHosted(val accountId: String?): AddressStyleType()
}
