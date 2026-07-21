package com.omnilife.core.designsystem.gallery

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

/**
 * Desktop entry point for the Design System preview gallery (Sprint 2,
 * TDR-22) — run with `./gradlew :core:core-designsystem:run`. This is the
 * verification surface for the whole component library in this sandbox
 * (no Android SDK, no macOS/Xcode host): a real window, real rendering,
 * real interaction, without either.
 */
public fun main() {
    application {
        Window(onCloseRequest = ::exitApplication, title = "OmniLife Design System") {
            GalleryScreen()
        }
    }
}
