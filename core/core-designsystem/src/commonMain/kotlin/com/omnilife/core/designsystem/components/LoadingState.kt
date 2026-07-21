package com.omnilife.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/**
 * A section/screen-level `in_caricamento` state: a stack of
 * [OmniSkeletonListItem]s announced once as "Caricamento in corso" (Bible:
 * "mai letto elemento per elemento") — never a full-screen spinner
 * ([OmniSkeletonListItem]'s own constraint, inherited here).
 */
@Composable
public fun OmniLoadingState(
    modifier: Modifier = Modifier,
    itemCount: Int = 3,
    announcement: String = "Caricamento in corso",
) {
    Column(
        modifier = modifier.fillMaxWidth().semantics(mergeDescendants = true) { contentDescription = announcement },
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        repeat(itemCount) {
            OmniSkeletonListItem()
        }
    }
}
