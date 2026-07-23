# Sprint 4 — Home Dashboard — Report

**Perimetro**: la Home "Oggi" definitiva di OmniLife (`feature-core`), usando esclusivamente Core
UI Kit (`core-designsystem`), Core Search (`core-search`), Core Sync (`core-sync`), Core
Notifications (`core-notifications`). Nessuna Bible nuova, nessuna modifica architetturale.
Nessun modulo `domain-*`: AI, Finanze complete, Calendario completo, Note complete restano
esplicitamente fuori perimetro; ogni sezione che ne dipenderebbe è un placeholder funzionale.

## 1. Schermate create

Un solo schermo L1 (`HomeScreen`), composto da componenti riusabili interamente da
`core-designsystem` più sette nuovi file in `feature-core`:

| File | Contenuto |
|---|---|
| `HomeScreen.kt` | Il contenitore: `OmniTopBar` (titolo "Oggi" + azione notifiche), `SyncStatusRow`, `GlobalSearchEntry`, `NotificationCenterPanel` (condizionale), `QuickActionsRow`, la lista scorrevole dei widget attivi |
| `HomeComponents.kt` | `SyncStatusRow`, `GlobalSearchEntry`, `NotificationCenterPanel`, `QuickActionsRow`, `HomeWidgetCard` — ogni composable stateless, nessun riferimento diretto a un `ViewModel` |
| `HomeWidget.kt` | `HomeWidgetKind` (i 7 widget richiesti), `HomeWidgetRegistry`/`InMemoryHomeWidgetRegistry` (HOME-002/003: attivazione e riordino) |
| `HomeSectionState.kt` | `Loading`/`Empty`/`Error`/`Content<T>` — un solo tipo condiviso da ogni sezione, mai una bandiera booleana per sezione |
| `HomeUiState.kt` | Lo stato MVI: ordine widget, sezioni, azioni rapide, stato sync, riepilogo notifiche, stato ricerca |
| `HomeIntent.kt` | Gli intent utente — **nessun intent "Refresh"**: HOME-007 vieta il pull-to-refresh per design |
| `HomeViewModel.kt` | Lo state holder MVI (TDR-02), stesso pattern di `feature-task`'s `TaskListViewModel` |

Mappatura esplicita degli item richiesti dal task:

| Richiesto | Realizzato come | Reale o placeholder |
|---|---|---|
| Dashboard | `HomeScreen` | Reale (contenitore) |
| Today Overview | `HomeWidgetKind.TODAY_OVERVIEW` | Placeholder funzionale |
| Agenda | `HomeWidgetKind.AGENDA` | Placeholder funzionale |
| Recent Activity | `HomeWidgetKind.RECENT_ACTIVITY` | Placeholder funzionale |
| Global Search Entry | `GlobalSearchEntry` → `UnifiedSearchService` | **Reale** |
| Quick Actions | `QuickActionsRow` | Descrittori reali, handler placeholder |
| Widget System | `HomeWidgetRegistry` | **Reale** (attivazione/riordino) |
| Progress Cards | `OmniProgress` (riusato da `core-designsystem`) | Componente reale, non ancora alimentato da dati |
| Goal Summary | `HomeWidgetKind.GOAL_SUMMARY` | Placeholder funzionale |
| Habit Summary | `HomeWidgetKind.HABIT_SUMMARY` | Placeholder funzionale |
| Finance Summary Placeholder | `HomeWidgetKind.FINANCE_SUMMARY` | Placeholder esplicitamente richiesto |
| Calendar Summary Placeholder | `HomeWidgetKind.CALENDAR_SUMMARY` | Placeholder esplicitamente richiesto |
| Notification Center | `NotificationCenterPanel` → `NotificationHistoryStore`/`NotificationBroker` | **Reale** |
| Sync Status | `SyncStatusRow` → `SyncStateManager` | **Reale**, reattivo |
| Empty States | `HomeSectionState.Empty` → `OmniEmptyState` | Reale |
| Loading States | `HomeSectionState.Loading` → `OmniLoadingState` | Reale |
| Error States | `HomeSectionState.Error` → `OmniErrorState` | Reale |

**Perché Today Overview/Agenda/Recent Activity/Goal/Habit Summary sono placeholder anche se non
esplicitamente etichettati "Placeholder" dal task**: il task impone "usa esclusivamente" i quattro
Core service — nessun `domain-*` è nell'elenco consentito, nemmeno `domain-task` (l'unico modulo
di dominio completo). Popolare quei widget con dati reali avrebbe richiesto una dipendenza non
autorizzata. Ho scelto l'interpretazione più conservativa e coerente con la disciplina già
osservata negli Sprint 2/3 (infrastruttura prima, wiring di dominio dopo): la struttura è reale e
pronta, il contenuto è un placeholder esplicito, mai un dato inventato o mascherato da reale. Ne
discuto l'alternativa in §6.

**Icone aggiunte a `core-designsystem`**: `NOTIFICATIONS` (campana) e `SYNC` (frecce circolari) —
il set esistente (11 glifi) non copriva né il centro notifiche né lo stato di sincronizzazione.
Disegnate a mano con le stesse primitive `DrawScope` di ogni altro glifo (TDR-22), stesso stile a
tratto unico.

## 2. Integrazione tra moduli

- **Core Search**: `HomeViewModel` chiama direttamente `UnifiedSearchService.search(query,
  filter)` — nessun adattamento, nessun wrapper. Query vuota → svuota i risultati e mostra le
  ricerche recenti (`RecentSearchStore.recent()`); query non vuota → risultato sincrono da FTS5.
  Deliberatamente non registra ogni tasto premuto in `RecentSearchStore` (SRCH è incrementale,
  senza invio — registrare ogni carattere digitato non è "una ricerca effettuata").
- **Core Sync**: `HomeViewModel` legge lo stato iniziale via `SyncStateManager.current()` e si
  iscrive a `observe()` per aggiornamenti reattivi — mai un poll, mai un pulsante di refresh
  (coerente con HOME-007 e con l'assenza strutturale di un intent `Refresh`).
  `SyncStatusRow` traduce `SyncPhase` in un messaggio/colore utente (IDLE→"Sincronizzato",
  SYNCING→"in corso", OFFLINE→messaggio esplicito, ERROR→`lastError`).
- **Core Notifications**: `NotificationCenterPanel` legge `NotificationHistoryStore.recent()`;
  `HomeIntent.DismissNotification` richiama `NotificationBroker.recordOutcome(request,
  IGNORATA)`, poi ricarica il riepilogo — lo stesso percorso NTF-006 già verificato nello
  Sprint 3 (3 ignorate consecutive propongono la disattivazione), qui semplicemente riusato, non
  reimplementato.
- **Core UI Kit**: nessun componente Home reimplementa ciò che `core-designsystem` offre già —
  `OmniCard`/`OmniListItem`/`OmniEmptyState`/`OmniLoadingState`/`OmniErrorState`/
  `OmniSearchField`/`OmniTopBar`/`OmniButton` sono riusati direttamente, mai copiati o
  parzialmente reimplementati nel modulo Home.

## 3. Test

19 test unitari scritti (`HomeViewModelTest`: 12, `HomeWidgetRegistryTest`: 7), coprono:

- Stato iniziale: sync status caricato, ogni `HomeWidgetKind` parte come `Empty` (mai bloccato
  in `Loading`).
- HOME-002/003: `SetWidgetActive`/`ReorderWidgets` aggiornano `widgetOrder` correttamente;
  `HomeWidgetRegistry` rifiuta un riordino che non è una permutazione valida; riattivare un
  widget lo ripristina nella sua posizione originale.
- Global Search Entry: query vuota vs non vuota, delega corretta a `UnifiedSearchService`.
- Quick Actions: l'intent è ricevuto e registrato come placeholder, mai un'eccezione.
- Notification Center: il conteggio "in attesa" considera solo `MOSTRATA` (non
  `AZIONATA`/`IGNORATA`); `DismissNotification` registra l'esito e aggiorna il riepilogo;
  un id sconosciuto è un no-op sicuro.
- Reattività: un cambiamento di `SyncStateManager` si riflette nello stato senza alcuna azione
  esplicita dell'utente.

**Nessuno di questi test è stato eseguito in questo sandbox** — vedi §5 per il motivo esatto
(un limite di rete, non un difetto del codice o dei test). Tutti compilano correttamente
(`compileTestKotlinJvm` verde).

## 4. Benchmark

**HOME-001**, testuale: *"compone in < 400 ms a freddo con 5 moduli attivi."* Il benchmark
(`HomeViewModelBenchmark`) è più severo del testo della Bible: misura la costruzione a freddo di
`HomeViewModel` con **tutti e 7** i widget attivi (non solo 5), asserendo `< 400ms`. Scritto,
compila correttamente, **non eseguibile in questo sandbox** per lo stesso motivo di §5 — il file
vive in `jvmTest`, che condivide il classpath di runtime bloccato con gli altri test del modulo.
La stima realistica (basata sui benchmark equivalenti già misurati per `core-sync`/
`core-notifications` in questo stesso ambiente, dove costruire alcuni oggetti in-memory e
popolare una manciata di stati placeholder richiede singole millisecondi) è che il budget sarebbe
rispettato con ampio margine — ma questa è una stima, non una misura, e va dichiarata come tale.

## 5. Problemi trovati

- **`jvmTest` non eseguibile in `feature-core`** (blocco ambientale, non un difetto): lo stesso
  limite di rete documentato nello Sprint 2 report (le dipendenze transitive
  `androidx.lifecycle`/`androidx.collection`/`androidx.annotation` di `compose.foundation` sono
  risolvibili solo da `dl.google.com`, bloccato dalla policy di rete di questo sandbox — verificato
  con l'errore Gradle esplicito: `403 Forbidden` su ogni tentativo). La differenza rispetto allo
  Sprint 2: lì il problema riguardava solo i test comportamentali/di regressione visiva basati su
  `compose-ui-test` (mai eseguiti, poi rimossi da `core-designsystem`); qui riguarda **l'intero
  task `jvmTest`**, inclusi test che non toccano mai Compose (`HomeViewModelTest`,
  `HomeWidgetRegistryTest`), perché condividono lo stesso modulo — e quindi lo stesso classpath di
  runtime — con le schermate Compose. Verificato isolando il problema: `compileKotlinJvm`,
  `compileTestKotlinJvm`, `detekt`, `ktlintCheck` passano tutti puliti per `feature-core`; solo
  `:feature:feature-core:jvmTest` fallisce. Confermato con una build completa del repository:
  **l'unico task che fallisce su oltre 40 moduli è questo uno**.
- **`SyncStateManager.observe` non ha un meccanismo di disiscrizione** (scoperto usandolo da
  `HomeViewModel.init`): `core-sync`'s `InMemorySyncStateManager.observe(listener)` aggiunge il
  listener a una lista senza mai restituire un modo per rimuoverlo. `HomeViewModel.clear()` non
  può quindi disiscrivere il proprio listener — accettato per questo sprint (il listener vive
  quanto il `SyncStateManager` stesso, che in una composition root reale ha lo stesso ciclo di
  vita dell'intera sessione app), ma è un difetto latente se in futuro si creano e distruggono
  molte istanze di `HomeViewModel` sullo stesso `SyncStateManager` (accumulo di listener). Non
  corretto in questo sprint per non modificare `core-sync` fuori dal perimetro dichiarato
  ("Non modificare l'architettura").
- **Nessuna Bible dichiara un formato per "Widget System" come componente riordinabile**:
  HOME-003 dice solo "l'utente può riordinare le sezioni con pressione lunga", senza specificare
  se l'ordine persiste tra sessioni, è per-dispositivo o sincronizzato. `InMemoryHomeWidgetRegistry`
  non persiste nulla (si azzera a ogni riavvio) — una scelta dichiarata qui, non nascosta, perché
  nessun meccanismo di persistenza è nel perimetro Core consentito da questo sprint (persisterlo
  richiederebbe `core-sync`'s CRDT o uno storage locale dedicato, entrambi fuori scope esplicito
  di "solo la Home").

## 6. Miglioramenti proposti

- **Wiring dei moduli `domain-*` reali** (prossimo sprint naturale): collegare `domain-task` a
  `TODAY_OVERVIEW`/`AGENDA`/`RECENT_ACTIVITY`, `domain-goal` a `GOAL_SUMMARY`, `domain-habit` a
  `HABIT_SUMMARY` — la struttura (`HomeSectionState`, `HomeListEntry`, `HomeWidgetRegistry`) è
  già pronta a riceverli senza cambiare la forma pubblica di `HomeViewModel`/`HomeUiState`.
- **Persistenza dell'ordine dei widget**: quando `core-sync`/uno storage locale saranno nel
  perimetro di un modulo Home futuro, sostituire `InMemoryHomeWidgetRegistry` con una versione
  persistente (stesso pattern già usato per la Local Change Queue di `core-sync`, TDR-27).
- **Aggiungere un meccanismo di disiscrizione a `SyncStateManager.observe`**: restituire un
  oggetto `Subscription`/`cancel()`, come già fa `core-eventbus`'s `EventBus.subscribe` — piccola
  modifica retrocompatibile a `core-sync`, non fatta qui per restare dentro il perimetro
  dichiarato di questo sprint.
- **Onboarding/Galleria Moduli/Revisione Settimanale**: le altre tre funzioni del modulo Core
  (Functional Bible doc 01) restano da implementare in un futuro sprint dedicato — questo sprint
  ha scelto deliberatamente di realizzare solo la Home "Oggi", come richiesto.
- **Ricerca "stavi cercando…"**: `core-search`'s `SearchSuggestions.didYouMean` esiste già ma non
  è ancora collegato a `GlobalSearchEntry` (si attiverebbe solo su zero risultati) — un'estensione
  naturale e a basso costo, rimandata per restare nel perimetro minimo richiesto.
