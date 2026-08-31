package com.omnilife.core.security

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RecoveryKeyServiceTest {
    private val service = RecoveryKeyService()

    @Test
    fun `generate produces exactly 24 words, all from the closed word list (SEC-002)`() {
        val phrase = service.generate()

        assertEquals(24, phrase.words.size)
        phrase.words.forEach { word -> assertTrue(word in RecoveryKeyService.WORDLIST, "'$word' not in the word list") }
    }

    @Test
    fun `the word list has no duplicates and is large enough for real entropy`() {
        val wordlistSize = RecoveryKeyService.WORDLIST.size
        assertEquals(wordlistSize, RecoveryKeyService.WORDLIST.distinct().size)
        assertTrue(wordlistSize >= 2000, "word list too small for meaningful entropy: $wordlistSize")
    }

    @Test
    fun `toKeyMaterial is deterministic for the same phrase`() {
        val phrase = service.generate()

        val key1 = service.toKeyMaterial(phrase)
        val key2 = service.toKeyMaterial(phrase)

        assertEquals(key1.toList(), key2.toList())
    }

    @Test
    fun `toKeyMaterial differs between two different phrases`() {
        val phraseA = service.generate()
        val phraseB = service.generate()

        assertEquals(24, phraseA.words.size)
        val keysDiffer = service.toKeyMaterial(phraseA).toList() != service.toKeyMaterial(phraseB).toList()
        assertTrue(keysDiffer || phraseA == phraseB)
    }

    @Test
    fun `verifySampleWords accepts the correct words at the given positions (SEC-002 write-3-words check)`() {
        val phrase = service.generate()

        val sampledWords = listOf(phrase.words[0], phrase.words[5], phrase.words[23])
        val correct = service.verifySampleWords(phrase, listOf(0, 5, 23), sampledWords)

        assertTrue(correct)
    }

    @Test
    fun `verifySampleWords rejects a wrong word at any sampled position`() {
        val phrase = service.generate()

        val sampledWords = listOf(phrase.words[0], "not-the-right-word", phrase.words[23])
        val wrong = service.verifySampleWords(phrase, listOf(0, 5, 23), sampledWords)

        assertFalse(wrong)
    }

    @Test
    fun `verifySampleWords is case-insensitive`() {
        val phrase = service.generate()

        val result = service.verifySampleWords(phrase, listOf(0), listOf(phrase.words[0].uppercase()))

        assertTrue(result)
    }
}
