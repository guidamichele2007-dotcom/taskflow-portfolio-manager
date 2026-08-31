package com.omnilife.core.security.crypto

/**
 * Standard Base64 (RFC 4648, with padding) — pure common Kotlin so
 * [com.omnilife.core.security.FieldCipher] never needs a platform-specific
 * `expect`/`actual` for something this mechanical (unlike the AEAD cipher
 * and KDF in [PlatformCrypto], there is no platform-security nuance here to
 * get subtly wrong).
 */
public object Base64 {
    private const val ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
    private val DECODE_TABLE =
        IntArray(128) { -1 }.also { table -> ALPHABET.forEachIndexed { i, c -> table[c.code] = i } }

    public fun encode(bytes: ByteArray): String {
        val output = StringBuilder(((bytes.size + 2) / 3) * 4)
        var i = 0
        while (i < bytes.size) {
            val b0 = bytes[i].toInt() and 0xFF
            val b1 = if (i + 1 < bytes.size) bytes[i + 1].toInt() and 0xFF else 0
            val b2 = if (i + 2 < bytes.size) bytes[i + 2].toInt() and 0xFF else 0
            val triple = (b0 shl 16) or (b1 shl 8) or b2

            output.append(ALPHABET[(triple shr 18) and 0x3F])
            output.append(ALPHABET[(triple shr 12) and 0x3F])
            output.append(if (i + 1 < bytes.size) ALPHABET[(triple shr 6) and 0x3F] else '=')
            output.append(if (i + 2 < bytes.size) ALPHABET[triple and 0x3F] else '=')
            i += 3
        }
        return output.toString()
    }

    public fun decode(text: String): ByteArray {
        val clean = text.trimEnd('=')
        val output = ArrayList<Byte>(clean.length * 3 / 4)
        var buffer = 0
        var bitsCollected = 0
        for (char in clean) {
            val value = DECODE_TABLE[char.code]
            require(value >= 0) { "Base64.decode: invalid character '$char'" }
            buffer = (buffer shl 6) or value
            bitsCollected += 6
            if (bitsCollected >= 8) {
                bitsCollected -= 8
                output.add(((buffer shr bitsCollected) and 0xFF).toByte())
            }
        }
        return output.toByteArray()
    }
}
