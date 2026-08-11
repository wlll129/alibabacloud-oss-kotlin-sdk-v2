@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.aliyun.kotlin.sdk.service.oss2.hash

import io.github.andreypfau.kotlinx.crypto.HMac
import io.github.andreypfau.kotlinx.crypto.Sha256

public actual class Sha256 : HashFunction {
    private var digest = Sha256()
    actual override fun update(input: ByteArray, offset: Int, length: Int) {
        digest = digest.update(input, offset, length)
    }
    actual override fun digest(): ByteArray = digest.digest()
    actual override fun reset(): Unit = digest.reset()
}

public actual fun ByteArray.sha256(): ByteArray = Sha256().apply {
    update(this@sha256, 0, this@sha256.size)
}.digest()

public actual fun ByteArray.hmacSha256(key: ByteArray): ByteArray = HMac(Sha256(), key).apply {
    update(this@hmacSha256, 0, this@hmacSha256.size)
}.digest()
