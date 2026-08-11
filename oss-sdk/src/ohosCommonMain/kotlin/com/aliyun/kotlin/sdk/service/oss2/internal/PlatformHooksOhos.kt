
package com.aliyun.kotlin.sdk.service.oss2.internal

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import platform.posix.getenv

@OptIn(ExperimentalForeignApi::class)
internal actual fun getPlatformEnv(name: String): String? {
    return getenv(name)?.toKString()
}

internal actual inline fun <T> platformSynchronized(lock: Any, block: () -> T): T {
    return block()
}

internal actual val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
