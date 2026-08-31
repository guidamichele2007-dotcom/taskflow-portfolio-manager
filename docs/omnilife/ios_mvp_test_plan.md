# iOS MVP — Piano di Test su Dispositivo Reale

**Perché questo documento esiste**: nessun host macOS/Xcode è disponibile in questo sandbox — un
vincolo di Kotlin/Native e Swift stessi, non specifico di questo ambiente (vedi
`iosApp/README.md` e `README-BUILD.md` §4). Né `:shared`'s target Apple (`iosArm64`/
`iosSimulatorArm64`/`iosX64`) né il pacchetto Swift in `iosApp/` sono mai stati compilati qui.
A differenza del piano Android, questo non è "codice scritto e da verificare su device": **iOS non
ha ancora un vero target applicazione**, solo un pacchetto Swift bootstrap con una shell a 4 tab e
un solo schermo placeholder. La sezione 1 elenca cosa manca prima che "test su device" abbia
senso; le sezioni successive sono la checklist per quando quel lavoro sarà fatto.

## 1. Prerequisiti — lavoro non ancora iniziato (fuori perimetro di questo sprint)

- [ ] Creare in Xcode (su macOS) il vero target applicazione iOS: bundle id, `Info.plist`,
  capabilities (notifiche push locali, background modes se necessari), firma/provisioning.
- [ ] Aggiungere `iosApp` (questo Swift Package) come dipendenza locale del target.
- [ ] Aggiungere il framework KMP prodotto da `:shared` (target `iosArm64`/`iosSimulatorArm64`)
  come dipendenza del target — integrazione standard KMP↔Xcode (TDR-18).
- [ ] Far invocare `RootView()` dal punto di ingresso `@main`.
- [ ] Solo a questo punto `swift build`/Xcode possono compilare e girare qualcosa di reale — nessuno
  di questi passaggi è stato eseguito finora in nessuno sprint.

## 2. Cosa esiste oggi (verificato solo per struttura/convenzioni, mai compilato)

- `RootView.swift`: shell `TabView` a 4 tab (Oggi/Moduli/Cerca/Profilo, Navigation Bible §3).
- Solo "Oggi" ha uno schermo non-placeholder: `TaskListPlaceholderView`, un mirror SwiftUI
  scritto a mano di `feature-task`'s `TaskListScreen` Compose — usa un modello locale
  (`TaskRowModel`), **non consuma ancora il framework `:shared`**. Il punto di integrazione con lo
  `StateFlow` esportato da Kotlin/Native (richiede l'header Objective-C generato dalla compilazione
  reale del framework) è documentato nel file ma non scritto.
- Gli altri 3 tab sono `ComingSoonView` — placeholder onesti, mai contenuto inventato.
- Nessuna persistenza, nessuna notifica, nessun onboarding, nessuna integrazione Impostazioni/
  Ricerca reale esiste lato iOS.

## 3. Checklist di verifica una volta che il target Xcode esiste

### 3.1 Build e avvio
- [ ] `swift build` e la build Xcode del target completano senza errori.
- [ ] L'app si avvia su simulatore e su device fisico senza crash all'avvio.

### 3.2 Integrazione KMP↔SwiftUI
- [ ] `TaskListPlaceholderView` sostituita da una vista reale che osserva lo `StateFlow` di
  `TaskListViewModel` (via l'interop Kotlin/Native↔Swift standard — `Kotlinx-coroutines`
  `SkieSwiftFlow`/wrapper manuale, la decisione tecnica specifica non è stata presa: se cambia in
  modo sostanziale l'architettura di integrazione, va registrata nel Technology Decision Record
  prima di essere implementata, come richiesto dalle istruzioni di questo sprint).
- [ ] Onboarding, Home, Impostazioni, Ricerca reali (oggi solo `ComingSoonView`).

### 3.3 Ciclo Task (una volta implementato)
- [ ] Stesso ciclo Create→Persist→Display→Edit→Complete→Postpone→Search→Reopen del piano Android
  (§3), verificato su questa piattaforma separatamente — le `expect`/`actual` KMP condivise sono le
  stesse, ma il layer SwiftUI è scritto a mano e va verificato a parte.

### 3.4 Notifiche
- [ ] `NotificationScheduler`/`NotificationChannelRegistry`/`NotificationPermissionManager` hanno
  già un `actual` iOS (`UNUserNotificationCenter`, vedi `core-notifications/src/iosMain`) — mai
  compilato né eseguito. Verificare: richiesta permesso reale, notifica locale mostrata a orario,
  azioni (completa/posticipa dalla notifica), quiet hours, gestione del deep link al tap.
- [ ] A differenza di Android, iOS non ha l'equivalente problema di "nessun BroadcastReceiver
  registrato" — `UNUserNotificationCenter` gestisce la consegna a livello di sistema — ma questo
  **non è stato verificato**, solo dedotto dalla forma dell'`actual`; va confermato su device reale.

### 3.5 Persistenza e offline
- [ ] SQLDelight `NativeSqliteDriver` (native-driver, già dichiarato nei `build.gradle.kts` dei
  moduli `domain-*`/`core-*` sotto `isMacOsHost`) — mai esercitato. Stesso piano offline del
  documento Android (§6), ripetuto qui.

### 3.6 Accessibilità
- [ ] VoiceOver attivo: ogni elemento interattivo ha una label comprensibile.
- [ ] Dynamic Type al massimo: nessun testo tagliato.

## 4. Nota per chi riprende questo lavoro

Non "fingere" un progresso che non esiste: finché §1 non è completato, ogni riga di questo
documento oltre quella sezione resta una checklist futura, non uno stato attuale. Questo Sprint 6
non ha aggiunto funzionalità iOS (fuori perimetro esplicito — "MVP Hardening", non crescita) —
ha solo confermato, leggendo il codice, che la shell a 4 tab esiste ed è strutturalmente onesta
(nessun placeholder spacciato per reale) e che tutte le `actual` iOS lato `core-*`/`domain-*`
esistono già in codice, pronte per essere esercitate quando un host macOS sarà disponibile.
