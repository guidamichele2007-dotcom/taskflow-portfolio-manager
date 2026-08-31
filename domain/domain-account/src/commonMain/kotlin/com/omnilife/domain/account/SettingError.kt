package com.omnilife.domain.account

import com.omnilife.core.common.DomainError

/** Settings-module errors — the vocabulary use cases return in [com.omnilife.core.common.OmniResult.Failure]. */
public sealed class SettingError(override val message: String) : DomainError {
    /** SET-R-01: the catalog is closed — a value outside a key's own domain is rejected, not coerced. */
    public data class InvalidValue(val key: SettingKey, val value: String) :
        SettingError("Valore '$value' non valido per '${key.name}'")
}
