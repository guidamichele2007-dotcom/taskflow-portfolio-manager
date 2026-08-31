package com.omnilife.core.security

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/** SEC-001/MFC-R-21 timeout options — a closed set, not a free-form duration. */
public enum class AppLockTimeout(public val minutes: Int?) {
    IMMEDIATE(0),
    ONE_MINUTE(1),
    FIVE_MINUTES(5),
    FIFTEEN_MINUTES(15),
}

/**
 * MFC-R-21/22's lock state machine — pure, deterministic, testable without
 * any real biometric hardware (the platform biometric prompt is a separate
 * concern this class never touches; it only tracks *when* a re-unlock is
 * due and *which* modules stay obscured until then). Screen-visibility
 * gating only: it never touches whether the underlying data is encrypted
 * at rest (that's [KeyManager]'s job, independent of app-lock state per
 * Technical Architecture Bible §10 §2).
 */
public class AppLockService(
    private val clock: Clock = Clock.System,
    private var timeout: AppLockTimeout = AppLockTimeout.FIVE_MINUTES,
    private val sensitiveModules: MutableSet<String> = mutableSetOf(),
) {
    private var lastUnlockAt: Instant? = null
    private var backgroundedAt: Instant? = null
    private var locked = true

    public fun configureTimeout(timeout: AppLockTimeout) {
        this.timeout = timeout
    }

    public fun markSensitiveModule(moduleId: String) {
        sensitiveModules.add(moduleId)
    }

    public fun unmarkSensitiveModule(moduleId: String) {
        sensitiveModules.remove(moduleId)
    }

    public fun isSensitiveModule(moduleId: String): Boolean = moduleId in sensitiveModules

    /** Call when biometric/passcode verification succeeds. */
    public fun recordSuccessfulUnlock() {
        locked = false
        lastUnlockAt = clock.now()
        backgroundedAt = null
    }

    /** Call when the app moves to the background (SEC-AC-01's timeout clock starts here, not at the last touch). */
    public fun recordBackgrounded() {
        backgroundedAt = clock.now()
    }

    /**
     * Call when the app returns to the foreground. [AppLockTimeout.IMMEDIATE]
     * always relocks; otherwise relocks only if [timeout] has elapsed since
     * [recordBackgrounded] (SEC-AC-01).
     */
    public fun recordForegrounded() {
        val backgroundedInstant = backgroundedAt ?: return
        val elapsedMinutes = (clock.now() - backgroundedInstant).inWholeMinutes
        if (timeout == AppLockTimeout.IMMEDIATE || elapsedMinutes >= (timeout.minutes ?: 0)) {
            locked = true
        }
        backgroundedAt = null
    }

    public fun isLocked(): Boolean = locked

    /** MFC-R-22: whether [moduleId]'s content may render in clear right now. */
    public fun isModuleVisible(moduleId: String): Boolean = !locked || moduleId !in sensitiveModules
}
