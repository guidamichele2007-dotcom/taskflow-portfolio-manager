package com.omnilife.core.common

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private data class SampleError(override val message: String) : DomainError

class OmniResultTest {
    @Test
    fun `map transforms a Success value`() {
        val result: OmniResult<Int> = OmniResult.Success(2)
        val mapped = result.map { it * 10 }
        assertEquals(OmniResult.Success(20), mapped)
    }

    @Test
    fun `map passes through a Failure unchanged`() {
        val result: OmniResult<Int> = OmniResult.Failure(SampleError("bad"))
        val mapped = result.map { it * 10 }
        assertEquals(result, mapped)
    }

    @Test
    fun `onSuccess runs only for Success`() {
        var ran = false
        (OmniResult.Success(1) as OmniResult<Int>).onSuccess { ran = true }
        assertTrue(ran)

        var ranOnFailure = false
        (OmniResult.Failure(SampleError("bad")) as OmniResult<Int>).onSuccess { ranOnFailure = true }
        assertTrue(!ranOnFailure)
    }

    @Test
    fun `onFailure runs only for Failure`() {
        var captured: DomainError? = null
        (OmniResult.Failure(SampleError("bad")) as OmniResult<Int>).onFailure { captured = it }
        assertEquals("bad", captured?.message)
    }

    @Test
    fun `getOrNull returns value only for Success`() {
        assertEquals(5, OmniResult.Success(5).getOrNull())
        assertEquals(null, OmniResult.Failure(SampleError("bad")).getOrNull())
    }
}
