package com.omnilife.core.designsystem.catalog

/**
 * Machine-readable registry of every component this library ships (Sprint
 * 2, TDR-22) — the same list backs the preview gallery
 * ([com.omnilife.core.designsystem.gallery.GalleryApp]) and the
 * "componenti creati" table in `docs/omnilife/sprint2_report.md`, so the
 * two can never silently drift apart.
 */
public data class ComponentCatalogEntry(
    val name: String,
    val bibleId: String?,
    val composableName: String,
    val variants: List<String>,
    val states: List<String>,
)

public object ComponentCatalog {
    public val entries: List<ComponentCatalogEntry> =
        listOf(
            ComponentCatalogEntry(
                "Button",
                "CMP-PULSANTE",
                "OmniButton / OmniIconButton",
                listOf("primario", "secondario", "testuale", "solo icona"),
                listOf("default", "premuto", "disabilitato", "in_caricamento"),
            ),
            ComponentCatalogEntry(
                "TextField",
                null,
                "OmniTextField",
                listOf("con etichetta", "con placeholder"),
                listOf("default", "in_evidenza", "disabilitato", "in_errore"),
            ),
            ComponentCatalogEntry(
                "SearchField",
                "CMP-SEARCH",
                "OmniSearchField",
                listOf("globale", "contestuale"),
                listOf("default", "in_evidenza", "vuoto"),
            ),
            ComponentCatalogEntry(
                "Card",
                "CMP-CARD",
                "OmniCard",
                listOf("lista", "stato singolo", "vuota positiva"),
                listOf("default", "in_caricamento", "vuoto"),
            ),
            ComponentCatalogEntry(
                "ListItem",
                "CMP-RIGA-ENTITA",
                "OmniListItem",
                listOf("con completamento", "senza completamento", "con chip di stato"),
                listOf("default", "premuto", "selezionato", "completato", "in_sospeso"),
            ),
            ComponentCatalogEntry(
                "BottomSheet",
                "CMP-SHEET",
                "OmniBottomSheet",
                listOf("dettaglio entità", "selettore"),
                listOf("default", "in_caricamento"),
            ),
            ComponentCatalogEntry(
                "Dialog",
                "CMP-DIALOG",
                "OmniDialog",
                listOf("conferma singola", "conferma distruttiva"),
                listOf("default"),
            ),
            ComponentCatalogEntry(
                "Snackbar",
                "CMP-SNACKBAR",
                "OmniSnackbarHost",
                listOf("con azione di annullo", "solo informativa"),
                listOf("default"),
            ),
            ComponentCatalogEntry(
                "FAB",
                "CMP-FAB",
                "OmniFab",
                listOf("unica"),
                listOf("default", "premuto"),
            ),
            ComponentCatalogEntry(
                "TopBar",
                "CMP-TOPBAR",
                "OmniTopBar",
                listOf("L1 senza freccia", "L2 con freccia"),
                listOf("default", "azione disabilitata"),
            ),
            ComponentCatalogEntry(
                "BottomBar",
                "CMP-TABBAR",
                "OmniBottomBar",
                listOf("unica, 4 slot fissi"),
                listOf("default", "selezionato"),
            ),
            ComponentCatalogEntry(
                "SegmentedControl",
                "CMP-SEGMENT",
                "OmniSegmentedControl",
                listOf("2 segmenti", "3 segmenti", "4 segmenti"),
                listOf("default", "selezionato"),
            ),
            ComponentCatalogEntry(
                "Chip",
                "CMP-CHIP",
                "OmniChip",
                listOf("selezione", "filtro rimovibile", "suggerimento"),
                listOf("default", "selezionato", "disabilitato"),
            ),
            ComponentCatalogEntry(
                "Badge",
                "CMP-BADGE",
                "OmniBadge",
                listOf("numerico"),
                listOf("default", "assente (count=0)"),
            ),
            ComponentCatalogEntry(
                "Toggle",
                "CMP-TOGGLE",
                "OmniToggle",
                listOf("unica"),
                listOf("on", "off", "disabilitato"),
            ),
            ComponentCatalogEntry(
                "Checkbox",
                null,
                "OmniCheckbox",
                listOf("generico (non-entità)"),
                listOf("default", "selezionato", "disabilitato"),
            ),
            ComponentCatalogEntry(
                "Progress",
                "CMP-PROGRESS",
                "OmniProgress",
                listOf("anello", "barra"),
                listOf("default", "attenzione", "completo"),
            ),
            ComponentCatalogEntry(
                "Skeleton",
                "CMP-SKELETON",
                "OmniSkeletonListItem / OmniSkeletonCard",
                listOf("riga entità", "card"),
                listOf("default"),
            ),
            ComponentCatalogEntry(
                "EmptyState",
                "CMP-EMPTY",
                "OmniEmptyState",
                listOf("mai usato", "filtrato"),
                listOf("default"),
            ),
            ComponentCatalogEntry(
                "ErrorState",
                "CMP-EMPTY (variante errore)",
                "OmniErrorState",
                listOf("errore"),
                listOf("default"),
            ),
            ComponentCatalogEntry(
                "LoadingState",
                null,
                "OmniLoadingState",
                listOf("sezione/schermata"),
                listOf("in_caricamento"),
            ),
            ComponentCatalogEntry(
                "CompletionControl",
                "CMP-COMPLETION",
                "OmniCompletionControl",
                listOf("binario"),
                listOf("default", "completato", "disabilitato"),
            ),
        )
}
