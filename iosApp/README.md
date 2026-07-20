# iosApp

**Scopo**: sorgente Swift/SwiftUI del punto di ingresso dell'applicazione iOS (L1 Esperienza, TDR-01). Consumerà il framework prodotto dal target `iosArm64`/`iosSimulatorArm64` del modulo `:shared` come unico confine verso il dominio condiviso.

**Riferimento**: Technology Decision Record TDR-01 (linguaggio mobile), TDR-18 (build system).

**Perché uno Swift Package e non un `.xcodeproj`**: un progetto Xcode è un formato di file in gran parte binario/plist generato e mantenuto dall'IDE; scriverlo a mano fuori da Xcode produce facilmente file corrotti o non apribili, e non è verificabile in questo ambiente. Questa cartella è invece uno **Swift Package** (`Package.swift`) — testo semplice, versionabile in modo pulito — che contiene il codice SwiftUI del bootstrap (`RootView`, nessuna schermata reale). Il primo sprint di sviluppo dovrà:

1. Creare in Xcode (su macOS) il vero target applicazione iOS (bundle id, Info.plist, firma) — l'unico passo che richiede l'IDE stesso.
2. Aggiungere questo package come dipendenza locale del target app.
3. Aggiungere il framework KMP prodotto da `:shared` come dipendenza del target app (integrazione standard KMP↔Xcode, TDR-18).
4. Far invocare `RootView()` dal punto di ingresso `@main` del target app.

**Stato**: infrastruttura di bootstrap — un solo `RootView` placeholder senza navigazione, schermate o logica di business.

**Nota sulla verifica in questo ambiente**: la compilazione di target Apple (`iosArm64`/`iosSimulatorArm64`/`iosX64` nel modulo `:shared`, e di questo stesso pacchetto Swift) richiede un host macOS con Xcode installato — un vincolo di Kotlin/Native e di Swift stessi, non specifico di questo sandbox. Il sandbox Linux usato per il bootstrap non ha alcun toolchain Swift disponibile, quindi né questo package né il framework `:shared` per iOS sono stati compilati o altrimenti verificati qui: la struttura segue le convenzioni standard di Swift Package Manager ma non è stata testata da `swift build`. Vedi [../README-BUILD.md](../README-BUILD.md) per il dettaglio completo del gating dei target.
