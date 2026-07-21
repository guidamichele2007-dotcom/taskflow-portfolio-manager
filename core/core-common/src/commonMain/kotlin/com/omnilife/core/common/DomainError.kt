package com.omnilife.core.common

/**
 * Base type for every domain-level error (TDR-21). Each module declares its
 * own sealed hierarchy implementing this — never a generic exception.
 */
public interface DomainError {
    public val message: String
}
