package com.omnilife.core.designsystem.state

/**
 * The 8 generic component states the Design System Bible allows, no more
 * (§04-stati-e-accessibilita-visiva §1, DS-INV-03): every interactive
 * component in this library exposes a subset of these, never a state
 * outside this list. A domain-specific variant (e.g. "budget in attenzione")
 * is a color/text variant applied on top of one of these 8, never a 9th
 * structural state (DS-31).
 */
public enum class OmniComponentState {
    DEFAULT,
    IN_EVIDENZA,
    PREMUTO,
    SELEZIONATO,
    DISABILITATO,
    IN_ERRORE,
    IN_CARICAMENTO,
    VUOTO,
}
