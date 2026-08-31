package com.omnilife.app

/**
 * Navigation Bible §3: exactly 4 fixed tabs — Oggi · Moduli · Cerca · Profilo, never dynamic by
 * active-module count. TDR-38: hand-rolled, no navigation library (same philosophy as TDR-19
 * manual DI, TDR-22 hand-drawn icons, TDR-24 hand-rolled CRDT).
 */
public enum class AppTab(public val label: String) {
    OGGI("Oggi"),
    MODULI("Moduli"),
    CERCA("Cerca"),
    PROFILO("Profilo"),
}
