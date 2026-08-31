package com.omnilife.core.security

/** JVM/Desktop (TDR-23): no OS-level secure storage exists here — [InMemorySecureStorage] is the honest answer. */
public actual fun platformSecureStorage(): SecureStorage = InMemorySecureStorage()
