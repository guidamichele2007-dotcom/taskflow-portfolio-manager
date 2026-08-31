package com.omnilife.core.notifications

/**
 * Resolves a notification's target entity to/from a deep-link URI
 * (`omnilife://<entityType>/<entityId>`) — a plain string contract every module can produce and
 * the app-shell router consumes, so this module never imports a domain-* navigation type.
 */
public object DeepLinkResolver {
    private const val SCHEME_PREFIX = "omnilife://"

    public fun buildDeepLink(entityReference: EntityReference): String =
        "$SCHEME_PREFIX${entityReference.entityType}/${entityReference.entityId}"

    /** Null if [uri] isn't a well-formed omnilife deep link (e.g. a foreign scheme). */
    public fun parseDeepLink(uri: String): EntityReference? {
        if (!uri.startsWith(SCHEME_PREFIX)) return null
        val remainder = uri.removePrefix(SCHEME_PREFIX)
        val parts = remainder.split("/", limit = 2)
        if (parts.size != 2 || parts[0].isBlank() || parts[1].isBlank()) return null
        return EntityReference(entityId = parts[1], entityType = parts[0])
    }
}
