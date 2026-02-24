package com.aliyun.kotlin.sdk.service.oss2.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ModelShareTest {

    @Test
    fun testHttpRange() {
        var httpRange = HttpRange(1, 100)
        assertEquals("bytes=1-100", httpRange.toString())

        httpRange = HttpRange(1, null)
        assertEquals("bytes=1-", httpRange.toString())

        httpRange = HttpRange("bytes=1-100")
        assertEquals(1, httpRange.offset)
        assertEquals(100, httpRange.count)

        httpRange = HttpRange("bytes=1-")
        assertEquals(1, httpRange.offset)
        assertNull(httpRange.count)
    }
}
