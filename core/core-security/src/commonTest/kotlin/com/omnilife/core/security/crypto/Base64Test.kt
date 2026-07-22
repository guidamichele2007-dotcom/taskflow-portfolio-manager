package com.omnilife.core.security.crypto

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class Base64Test {
    @Test
    fun `round-trips byte arrays of every padding remainder`() {
        for (size in 0..8) {
            val bytes = ByteArray(size) { it.toByte() }
            val decoded = Base64.decode(Base64.encode(bytes))
            assertEquals(bytes.toList(), decoded.toList(), "size=$size")
        }
    }

    @Test
    fun `matches a known vector`() {
        assertEquals("aGVsbG8=", Base64.encode("hello".encodeToByteArray()))
        assertEquals("hello", Base64.decode("aGVsbG8=").decodeToString())
    }

    @Test
    fun `rejects invalid characters`() {
        assertFailsWith<IllegalArgumentException> { Base64.decode("not valid base64!!") }
    }
}
