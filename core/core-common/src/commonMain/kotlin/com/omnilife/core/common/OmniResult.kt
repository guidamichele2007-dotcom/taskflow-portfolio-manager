package com.omnilife.core.common

/**
 * Explicit result type for use cases (TDR-21). Every predictable failure
 * (invariant violation, business rule) is a typed [Failure], never a thrown
 * exception — makes the layer-translation policy of Technical Architecture
 * Bible §07 checkable by the compiler instead of by convention.
 */
public sealed class OmniResult<out T> {
    public data class Success<out T>(val value: T) : OmniResult<T>()

    public data class Failure(val error: DomainError) : OmniResult<Nothing>()
}

public inline fun <T, R> OmniResult<T>.map(transform: (T) -> R): OmniResult<R> =
    when (this) {
        is OmniResult.Success -> OmniResult.Success(transform(value))
        is OmniResult.Failure -> this
    }

public inline fun <T> OmniResult<T>.onSuccess(action: (T) -> Unit): OmniResult<T> {
    if (this is OmniResult.Success) action(value)
    return this
}

public inline fun <T> OmniResult<T>.onFailure(action: (DomainError) -> Unit): OmniResult<T> {
    if (this is OmniResult.Failure) action(error)
    return this
}

public fun <T> OmniResult<T>.getOrNull(): T? = (this as? OmniResult.Success)?.value
