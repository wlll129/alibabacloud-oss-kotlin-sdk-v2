package com.aliyun.kotlin.sdk.service.oss2.models

public class HttpRange {

    public val offset: Long
    public val count: Long?

    public constructor(offset: Long, count: Long?) {
        this.offset = offset
        this.count = count
    }

    public constructor(range: String) {
        val range = range.replace("bytes=", "")
        val startAndEnd = range.split("-")
        if (startAndEnd.isEmpty()) {
            throw RuntimeException("Invalid range $range")
        }
        val start = startAndEnd.first().toLong()
        val end = if (startAndEnd.size == 2 && startAndEnd.last().isNotEmpty()) {
            startAndEnd.last().toLong()
        } else { null }

        this.offset = start
        this.count = end?.let { it - start + 1 }
    }

    public override fun toString(): String {
        return "bytes=$offset-${count?.let { "${offset + it - 1}" } ?: ""}"
    }
}
