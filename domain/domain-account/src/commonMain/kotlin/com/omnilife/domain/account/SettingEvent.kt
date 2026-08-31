package com.omnilife.domain.account

import com.omnilife.core.eventbus.DomainEvent

/**
 * Events the Account/Settings module publishes on the Bus Eventi (Functional Bible §6,
 * `modulo.entita.azione` naming), mirroring `domain-task`'s `TaskEvent` (TDR-35). Added Sprint 6
 * after finding that a theme/accent change in Settings had no visible effect until the app was
 * force-restarted — `MainActivity` read [Setting] values once at startup and never again. Payload
 * stays minimal (Technical Architecture Bible §03 §3): just enough for a subscriber to know
 * something changed and re-read what it needs.
 */
public sealed interface SettingEvent : DomainEvent {
    public data class Updated(val key: SettingKey, val value: String) : SettingEvent
}
