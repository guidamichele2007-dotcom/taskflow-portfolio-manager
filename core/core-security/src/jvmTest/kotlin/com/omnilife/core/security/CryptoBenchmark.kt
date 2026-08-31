package com.omnilife.core.security

import kotlin.system.measureNanoTime
import kotlin.test.Test

/**
 * Hand-rolled micro-benchmark (no JMH — see sprint3_report.md for why), run
 * as a normal JVM test so its numbers are produced by every `gradle test`,
 * not a one-off manual measurement. Prints real, reproducible throughput
 * for the operations `core-security` puts on the hot path of every future
 * `domain-*` write/read (via [FieldCipher]).
 */
class CryptoBenchmark {
    @Test
    fun `benchmark - AES-256-GCM encrypt-decrypt throughput`() {
        val crypto: CryptoService = RealCryptoService()
        val key = crypto.generateKey()
        val plaintext = "Chiamare il commercialista domani alle 15".repeat(4).encodeToByteArray()
        val iterations = 10_000

        val encryptNanos =
            measureNanoTime {
                repeat(iterations) { crypto.encrypt(key, plaintext) }
            }
        val blob = crypto.encrypt(key, plaintext)
        val decryptNanos =
            measureNanoTime {
                repeat(iterations) { crypto.decrypt(key, blob) }
            }

        println(
            "[benchmark] AES-256-GCM encrypt: $iterations ops in ${encryptNanos / 1_000_000}ms " +
                "(${"%.2f".format(iterations / (encryptNanos / 1_000_000_000.0))} ops/s)",
        )
        println(
            "[benchmark] AES-256-GCM decrypt: $iterations ops in ${decryptNanos / 1_000_000}ms " +
                "(${"%.2f".format(iterations / (decryptNanos / 1_000_000_000.0))} ops/s)",
        )
    }

    @Test
    fun `benchmark - PBKDF2WithHmacSHA256 derivation cost at the chosen iteration count`() {
        val crypto: CryptoService = RealCryptoService()
        val salt = crypto.randomSalt()
        val iterations = 20

        val elapsedNanos =
            measureNanoTime {
                repeat(iterations) { crypto.deriveKeyFromPassphrase("benchmark passphrase".toCharArray(), salt) }
            }

        val perDerivationMs = elapsedNanos / 1_000_000.0 / iterations
        println(
            "[benchmark] PBKDF2WithHmacSHA256 (${CryptoService.DEFAULT_PBKDF2_ITERATIONS} iterations): " +
                "%.1fms per derivation (unlock-time cost)".format(perDerivationMs),
        )
    }
}
