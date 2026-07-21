package com.omnilife.core.designsystem.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.omnilife.core.designsystem.components.OmniBadge
import com.omnilife.core.designsystem.components.OmniButton
import com.omnilife.core.designsystem.components.OmniButtonVariant
import com.omnilife.core.designsystem.components.OmniCard
import com.omnilife.core.designsystem.components.OmniCheckbox
import com.omnilife.core.designsystem.components.OmniChip
import com.omnilife.core.designsystem.components.OmniChipVariant
import com.omnilife.core.designsystem.components.OmniCompletionControl
import com.omnilife.core.designsystem.components.OmniEmptyState
import com.omnilife.core.designsystem.components.OmniErrorState
import com.omnilife.core.designsystem.components.OmniIconButton
import com.omnilife.core.designsystem.components.OmniListItem
import com.omnilife.core.designsystem.components.OmniLoadingState
import com.omnilife.core.designsystem.components.OmniProgress
import com.omnilife.core.designsystem.components.OmniProgressShape
import com.omnilife.core.designsystem.components.OmniProgressStatus
import com.omnilife.core.designsystem.components.OmniSearchField
import com.omnilife.core.designsystem.components.OmniSegmentedControl
import com.omnilife.core.designsystem.components.OmniSkeletonCard
import com.omnilife.core.designsystem.components.OmniSkeletonListItem
import com.omnilife.core.designsystem.components.OmniTextField
import com.omnilife.core.designsystem.components.OmniToggle
import com.omnilife.core.designsystem.components.OmniTopBar
import com.omnilife.core.designsystem.components.OmniTopBarAction
import com.omnilife.core.designsystem.theme.OmniIconType
import com.omnilife.core.designsystem.theme.OmniTheme

/**
 * The runnable preview gallery (Sprint 2 deliverable "preview gallery"):
 * one live instance per [com.omnilife.core.designsystem.catalog.ComponentCatalog]
 * entry, with a dark/light toggle so both [OmniTheme] pairs (DS-INV-01) are
 * reachable without recompiling.
 */
@Composable
public fun GalleryScreen() {
    var darkTheme by remember { mutableStateOf(false) }
    OmniTheme(darkTheme = darkTheme) {
        Column(modifier = Modifier.fillMaxSize().background(OmniTheme.colors.superficieBase)) {
            OmniTopBar(
                title = "OmniLife Design System — Galleria",
                actions =
                    listOf(
                        OmniTopBarAction(
                            icon = OmniIconType.INFO,
                            contentDescription = if (darkTheme) "Passa a chiaro" else "Passa a scuro",
                            onClick = { darkTheme = !darkTheme },
                        ),
                    ),
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(OmniTheme.spacing.spazio4),
                verticalArrangement = Arrangement.spacedBy(OmniTheme.spacing.spazio6),
            ) {
                items(gallerySections()) { section -> section() }
            }
        }
    }
}

private fun gallerySections(): List<@Composable () -> Unit> =
    listOf(
        { ButtonSection() },
        { InputSection() },
        { ContainerSection() },
        { NavigationSection() },
        { FeedbackSection() },
        { StatusSection() },
    )

@Composable
private fun SectionTitle(text: String) {
    BasicText(text = text, style = OmniTheme.typography.titoloSezione.copy(color = OmniTheme.colors.testoPrimario))
    Spacer(Modifier.height(OmniTheme.spacing.spazio2))
}

@Composable
private fun ButtonSection() {
    Column {
        SectionTitle("Button / IconButton (CMP-PULSANTE)")
        Row(horizontalArrangement = Arrangement.spacedBy(OmniTheme.spacing.spazio2)) {
            OmniButton(text = "Primario", onClick = {})
            OmniButton(text = "Secondario", onClick = {}, variant = OmniButtonVariant.SECONDARIO)
            OmniButton(text = "Testuale", onClick = {}, variant = OmniButtonVariant.TESTUALE)
            OmniButton(text = "Disabilitato", onClick = {}, enabled = false)
            OmniButton(text = "Caricamento", onClick = {}, loading = true)
            OmniIconButton(icon = OmniIconType.ADD, contentDescription = "Aggiungi", onClick = {})
        }
    }
}

@Composable
private fun InputSection() {
    Column {
        SectionTitle("TextField / SearchField")
        var text by remember { mutableStateOf("") }
        var query by remember { mutableStateOf("") }
        OmniTextField(value = text, onValueChange = { text = it }, label = "Titolo", placeholder = "Chiamare...")
        Spacer(Modifier.height(OmniTheme.spacing.spazio2))
        OmniTextField(
            value = "",
            onValueChange = {},
            label = "Con errore",
            isError = true,
            errorMessage = "Il titolo è obbligatorio",
        )
        Spacer(Modifier.height(OmniTheme.spacing.spazio2))
        OmniSearchField(query = query, onQueryChange = { query = it })
    }
}

@Composable
private fun ContainerSection() {
    Column {
        SectionTitle("Card / ListItem (CMP-CARD / CMP-RIGA-ENTITA)")
        OmniCard(title = "Oggi") {
            OmniListItem(
                title = "Chiamare il commercialista",
                secondaryText = "Scade venerdì",
                completed = false,
                onCompletedChange = {},
            )
            OmniListItem(
                title = "Pagare bolletta",
                secondaryText = "In ritardo",
                overdue = true,
                completed = false,
                onCompletedChange = {},
            )
            OmniListItem(title = "Comprare il latte", completed = true, onCompletedChange = {})
        }
    }
}

@Composable
private fun NavigationSection() {
    Column {
        SectionTitle("SegmentedControl / Chip / Badge")
        var segment by remember { mutableStateOf(0) }
        OmniSegmentedControl(
            segments =
                listOf(
                    "Oggi",
                    "Prossimi",
                    "Tutti",
                ),
            selectedIndex = segment,
            onSegmentSelected = {
                segment = it
            },
        )
        Spacer(Modifier.height(OmniTheme.spacing.spazio2))
        Row(horizontalArrangement = Arrangement.spacedBy(OmniTheme.spacing.spazio1)) {
            OmniChip(text = "Lavoro", selected = true, onClick = {})
            OmniChip(text = "Casa", selected = false, onClick = {})
            OmniChip(
                text = "Filtro attivo",
                selected = true,
                onClick = {},
                variant = OmniChipVariant.FILTRO,
                onRemove = {},
            )
            OmniBadge(count = 3)
        }
    }
}

@Composable
private fun FeedbackSection() {
    Column {
        SectionTitle("Toggle / Checkbox / CompletionControl")
        var toggled by remember { mutableStateOf(true) }
        var checked by remember { mutableStateOf(false) }
        var completed by remember { mutableStateOf(false) }
        Row(horizontalArrangement = Arrangement.spacedBy(OmniTheme.spacing.spazio3)) {
            OmniToggle(checked = toggled, onCheckedChange = { toggled = it })
            OmniCheckbox(checked = checked, onCheckedChange = { checked = it }, contentDescription = "Opzione")
            OmniCompletionControl(
                completed = completed,
                onToggle = { completed = !completed },
                entityLabel = "Voce di esempio",
            )
        }
    }
}

@Composable
private fun StatusSection() {
    Column {
        SectionTitle("Progress / Skeleton / Empty / Error / Loading")
        OmniProgress(
            value = 0.65f,
            label = "65% del budget Alimentari",
            shape = OmniProgressShape.BARRA,
            status = OmniProgressStatus.ATTENZIONE,
        )
        Spacer(Modifier.height(OmniTheme.spacing.spazio2))
        OmniProgress(value = 0.4f, label = "40% dell'obiettivo", shape = OmniProgressShape.ANELLO)
        Spacer(Modifier.height(OmniTheme.spacing.spazio2))
        OmniSkeletonListItem()
        Spacer(Modifier.height(OmniTheme.spacing.spazio2))
        OmniSkeletonCard()
        Spacer(Modifier.height(OmniTheme.spacing.spazio2))
        OmniEmptyState(
            icon = OmniIconType.SEARCH,
            message = "Nessuna attività ancora: inizia catturandone una",
            actionLabel = "Aggiungi",
            onActionClick = {},
        )
        Spacer(Modifier.height(OmniTheme.spacing.spazio2))
        OmniErrorState(message = "Impossibile caricare i dati", actionLabel = "Riprova", onActionClick = {})
        Spacer(Modifier.height(OmniTheme.spacing.spazio2))
        OmniLoadingState(itemCount = 2)
    }
}
