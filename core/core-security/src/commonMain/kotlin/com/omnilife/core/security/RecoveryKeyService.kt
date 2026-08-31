package com.omnilife.core.security

import com.omnilife.core.security.crypto.PlatformCrypto

/**
 * SEC-002: a 24-word recovery phrase, shown to the user exactly once at
 * generation time. Equality is by word content.
 */
public data class RecoveryPhrase(public val words: List<String>) {
    init {
        require(words.size == RecoveryKeyService.WORD_COUNT) {
            "RecoveryPhrase must have ${RecoveryKeyService.WORD_COUNT} words, got ${words.size}"
        }
    }
}

/**
 * Generates and verifies the SEC-002 recovery phrase — 24 words from a
 * closed 2048-word list (11 bits/word × 24 = 264 bits of entropy, the same
 * order of magnitude as the BIP39 24-word standard). The word list here is
 * **not** BIP39's canonical list: it's generated deterministically from two
 * small curated adjective/noun lists (`ADJECTIVES × NOUNS`), avoiding the
 * need to embed or verify a large external word list in this sandbox — see
 * TDR-23. Interoperability with BIP39-based tooling is explicitly not a
 * goal (this recovery phrase is internal to OmniLife, never a
 * cryptocurrency seed).
 */
public class RecoveryKeyService {
    /** Fresh, cryptographically random 24-word phrase. */
    public fun generate(): RecoveryPhrase {
        val words =
            (1..WORD_COUNT).map {
                val index = randomIndex()
                WORDLIST[index]
            }
        return RecoveryPhrase(words)
    }

    /**
     * Deterministic key material from a phrase (used both to wrap and to
     * unwrap the KEK — [KeyManager] calls this on both paths). A single
     * low-cost KDF pass is sufficient here: the phrase itself already
     * carries [WORD_COUNT] × 11 bits of entropy from a CSPRNG, unlike a
     * user-chosen passphrase — there is nothing for iterated key-stretching
     * to protect against.
     */
    public fun toKeyMaterial(phrase: RecoveryPhrase): ByteArray {
        val joined = phrase.words.joinToString(separator = " ")
        return PlatformCrypto.pbkdf2Sha256(joined.toCharArray(), FIXED_SALT, iterations = 1, keyLengthBits = 256)
    }

    /** SEC-002's "write down 3 sample words" verification — checks the words at the given (0-based) positions. */
    public fun verifySampleWords(
        phrase: RecoveryPhrase,
        samplePositions: List<Int>,
        userProvidedWords: List<String>,
    ): Boolean {
        if (samplePositions.size != userProvidedWords.size) return false
        return samplePositions.indices.all { i ->
            val position = samplePositions[i]
            position in phrase.words.indices && phrase.words[position].equals(userProvidedWords[i], ignoreCase = true)
        }
    }

    private fun randomIndex(): Int {
        val randomBytes = PlatformCrypto.secureRandomBytes(4)
        val unsigned =
            ((randomBytes[0].toInt() and 0xFF) shl 24) or
                ((randomBytes[1].toInt() and 0xFF) shl 16) or
                ((randomBytes[2].toInt() and 0xFF) shl 8) or
                (randomBytes[3].toInt() and 0xFF)
        return (unsigned.toLong() and 0xFFFFFFFFL).mod(WORDLIST.size.toLong()).toInt()
    }

    public companion object {
        public const val WORD_COUNT: Int = 24
        private val FIXED_SALT = "omnilife-recovery-phrase-kdf-salt".encodeToByteArray()

        private val ADJECTIVES =
            listOf(
                "quiet", "steady", "gentle", "bright", "calm", "clear", "warm", "cool",
                "swift", "solid", "kind", "brave", "wise", "keen", "fresh", "broad",
                "light", "deep", "sharp", "smooth", "soft", "firm", "plain", "grand",
                "loyal", "honest", "patient", "curious", "humble", "gracious", "sturdy", "vivid",
                "amber", "coral", "azure", "ivory", "olive", "maple", "cedar", "willow",
                "meadow", "harbor", "canyon", "summit", "orchard", "prairie",
            )
        private val NOUNS =
            listOf(
                "river", "mountain", "forest", "meadow", "harbor", "canyon", "valley", "island",
                "garden", "bridge", "castle", "compass", "lantern", "anchor", "falcon", "otter",
                "willow", "cedar", "maple", "juniper", "heron", "sparrow", "dolphin", "badger",
                "ember", "granite", "quartz", "amber", "coral", "pearl", "cobalt", "cinder",
                "orchard", "prairie", "summit", "ridge", "cove", "delta", "glacier", "plateau",
                "voyage", "journey", "horizon", "beacon", "harmony", "cadence",
            )
        internal val WORDLIST: List<String> =
            ADJECTIVES.flatMap { adjective -> NOUNS.map { noun -> "$adjective-$noun" } }.distinct().sorted()
    }
}
