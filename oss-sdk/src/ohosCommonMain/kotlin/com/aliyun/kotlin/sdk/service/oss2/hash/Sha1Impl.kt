@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.aliyun.kotlin.sdk.service.oss2.hash

import io.github.andreypfau.kotlinx.crypto.HMac
import io.github.andreypfau.kotlinx.crypto.Sha1

public actual class Sha1 : HashFunction {
    private var digest = Sha1()
    actual override fun update(input: ByteArray, offset: Int, length: Int) {
        digest = digest.update(input, offset, length)
    }
    actual override fun digest(): ByteArray = digest.digest()
    actual override fun reset(): Unit = digest.reset()
}

public actual fun ByteArray.sha1(): ByteArray = Sha1().apply {
    update(this@sha1, 0, this@sha1.size)
}.digest()

public actual fun ByteArray.hmacSha1(key: ByteArray): ByteArray = HMac(Sha1(), key).apply {
    update(this@hmacSha1, 0, this@hmacSha1.size)
}.digest()
