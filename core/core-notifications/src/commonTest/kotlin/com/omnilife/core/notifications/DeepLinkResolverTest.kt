package com.omnilife.core.notifications

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DeepLinkResolverTest {
    @Test
    fun `buildDeepLink produces the omnilife scheme URI`() {
        val uri = DeepLinkResolver.buildDeepLink(EntityReference("task-42", "task"))
        assertEquals("omnilife://task/task-42", uri)
    }

    @Test
    fun `parseDeepLink round-trips buildDeepLink`() {
        val reference = EntityReference("habit-7", "habit")
        val parsed = DeepLinkResolver.parseDeepLink(DeepLinkResolver.buildDeepLink(reference))
        assertEquals(reference, parsed)
    }

    @Test
    fun `parseDeepLink rejects a foreign scheme`() {
        assertNull(DeepLinkResolver.parseDeepLink("https://example.com/task/1"))
    }

    @Test
    fun `parseDeepLink rejects a malformed omnilife URI missing the id`() {
        assertNull(DeepLinkResolver.parseDeepLink("omnilife://task"))
        assertNull(DeepLinkResolver.parseDeepLink("omnilife://task/"))
        assertNull(DeepLinkResolver.parseDeepLink("omnilife:///id-only"))
    }
}
