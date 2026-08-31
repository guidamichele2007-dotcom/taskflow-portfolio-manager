# Sprint 6 — MVP Hardening + Real Device Readiness — Report

**Obiettivo dello sprint**: non far crescere OmniLife, renderla più affidabile. Nessun modulo di
prodotto nuovo (niente Finanze/Note/Calendario/Abitudini/Obiettivi/Salute/AI). L'audit è partito
dal codice reale (non da ciò che i report precedenti dichiaravano), ha corretto i problemi con
valore pratico reale trovati, e ha lasciato documentati — non nascosti — quelli senza abbastanza
valore pratico per giustificare il rischio di una modifica non verificabile in questo sandbox.

**Risultato**: `OMNILIFE MVP — READY FOR REAL DEVICE TESTING` nel senso preciso della sezione 17 —
non "tutto è verde", ma "ogni area ha uno stato onesto, e le notifiche — il rischio più grave
trovato — ora funzionano davvero invece di non funzionare mai".

## 1. Problemi trovati e risolti

Ordinati per impatto pratico reale (non per modulo).

1. **Le notifiche locali non arrivavano mai alla tray di sistema Android, in nessuna
   circostanza** (il bug più grave trovato). `DefaultLocalNotificationService.show()` programmava
   l'allarme, ma il suo `onFire` aggiornava solo la cronologia interna, senza mai chiamare
   `NotificationManagerCompat`; e nessun `BroadcastReceiver` era comunque registrato per ricevere
   l'allarme di `AlarmManager` — un gap che il codice stesso segnalava esplicitamente da TDR-26/
   Sprint 3 in poi ("a real integration needs a BroadcastReceiver... out of this module"). Il
   manifest non dichiarava nemmeno `POST_NOTIFICATIONS` né `SCHEDULE_EXACT_ALARM`, quindi la
   chiamata a `setExactAndAllowWhileIdle` avrebbe potuto lanciare `SecurityException` su API 31+.
   **Corretto** (TDR-41/TDR-42): `OmniLifeApplication` promuove `AppContainer` a singleton di
   processo; un `NotificationFireReceiver` reale, registrato in manifest, legge la richiesta
   pendente e chiama davvero `NotificationManagerCompat.notify(...)`; permission aggiunte;
   `canScheduleExactAlarms()` guardia la chiamata con un fallback non-esatto invece di crashare.
2. **`RestoreTask`/`PermanentlyDeleteTask` non pubblicavano alcun evento**, dallo Sprint 5 in cui
   sono stati scritti. I tre bridge Task↔Search/Notification/Sync non potevano quindi mai reagire a
   un ripristino dal cestino o a un'eliminazione permanente: un task ripristinato restava indicizzato
   come `TRASHED` a tempo indeterminato, il suo promemoria non veniva mai riprogrammato, la modifica
   non veniva mai messa in coda per la sync. **Corretto** (TDR-45): `TaskEvent.Restored`/
   `PermanentlyDeleted` aggiunti e sottoscritti da tutti e tre i bridge.
3. **`RestoreTask` non aveva alcuna UI che lo chiamasse** — nessun modo per l'utente di annullare
   un'eliminazione, nonostante MFC-R-09/R-11 richiedano esplicitamente "1 gesto + undo immediato" e
   il caso d'uso esistesse (testato) fin dallo Sprint 5. **Corretto**: `TaskListUiState` ottiene un
   `pendingUndoDelete`, `TaskListViewModel` gestisce `Delete`/`UndoDelete`/`DismissUndoDelete`,
   `TaskListScreen` mostra lo snackbar `OmniSnackbarHost` (Design System, esistente ma mai usato da
   questo modulo) con azione "Annulla".
4. **Cambiare tema/accento in Impostazioni non aveva alcun effetto visibile fino al riavvio**
   dell'app — `MainActivity` leggeva `Setting` una sola volta all'avvio. `ThemeMode.SYSTEM`, inoltre,
   risolveva sempre a tema chiaro, mai al tema reale del dispositivo. **Corretto** (TDR-43):
   `UpdateSetting` pubblica `SettingEvent.Updated`; `MainActivity` lo sottoscrive e usa
   `isSystemInDarkTheme()` per `SYSTEM`.
5. **Cambiare tab (Oggi/Moduli/Cerca/Profilo) distruggeva e ricostruiva da zero il ViewModel di
   quella tab** — testo di ricerca perso, Home ricaricata da capo, ogni volta. Causa: i quattro
   ViewModel venivano creati dentro il branch `when` di `AppTabContent`, quindi Compose li
   dismetteva quando la tab non era selezionata. **Corretto**: creazione spostata in `AppShell`
   (mai smontato finché l'utente resta nella app), sopravvive ai cambi tab.
6. **`MainActivity.onCreate` bloccava il thread principale con tre chiamate `runBlocking`**
   (stato onboarding, impostazioni, risoluzione della lista predefinita) durante la prima
   composizione — un rischio concreto di jank/ANR all'avvio, non solo teorico. **Corretto**: gate di
   avvio asincrono (`LaunchedEffect` + `OmniLoadingState`), `AppContainer.resolveDefaultListId()`
   diventata una funzione `suspend` invece di una property `by lazy { runBlocking {...} }`.
7. **`NetworkMonitor.onConnectivityChanged` aveva lo stesso leak-shape senza unsubscribe** che
   TDR-34 aveva corretto per `SyncStateManager.observe` — mai applicato qui. Nessun chiamante di
   produzione esiste ancora (nessun rischio reale oggi), ma il prossimo che avesse cablato questo
   monitor avrebbe ereditato lo stesso leak. **Corretto** (TDR-44): `NetworkMonitorSubscription`,
   stessa forma di `SyncStateSubscription`.
8. **Nessuna variante di build Debug/Internal/Release**, nessuna configurazione di firma —
   `androidApp/build.gradle.kts` aveva solo i due tipi impliciti di default di AGP. **Corretto**
   (TDR-46): tre `buildTypes` reali, firma release letta solo da 4 variabili d'ambiente (mai
   hardcoded — un `assembleRelease` senza di esse fallisce con un errore di firma chiaro).
9. **Un vero `standard:import-ordering`** trovato dal proprio giro `detekt`/`ktlintCheck` di
   questo sprint (`SettingsUseCasesTest.kt`) — corretto.

## 2. Cosa è stato verificato solido, nessuna modifica necessaria

- **Task**: ricorrenza (`RecurrenceCalculator`, clamp giorno-31/29-febbraio corretto), gestione
  timezone (ogni conversione `Instant`↔`LocalDate`/`LocalTime` passa per un `TimeZone` esplicito),
  task senza scadenza (nessun crash/misclassificazione in `GetTasksForView`), sottotask (nessun
  orfano dopo eliminazione permanente), mappatura persistenza campo-per-campo.
- **Sync engine**: idempotenza (`INSERT OR REPLACE` per id), persistenza della coda (SQLite reale,
  non in-memory), ordinamento (`LogicalTimestamp` monotono + tie-break su deviceId), merge CRDT
  (`LwwRegister`/`ORSet`/`EntityFieldMerger` genuinamente commutativi/idempotenti — nessun
  controesempio trovato), il fix `SyncStateManager.observe`/`cancel` dello Sprint 5 resta intatto e
  testato.
- **Search engine**: indicizzazione (aggiornata su ogni evento Task, ora incluso restore/permanent
  delete), persistenza reale via FTS5/SQLDelight (non in-memory), nessuna dipendenza di rete,
  `Fts5QuerySanitizer` protegge da injection di sintassi FTS5, nessuna scansione O(n²).
- **Sicurezza**: nessun segreto hardcoded, nessun `println`/`Log.*` nel codice, nessuno stack trace
  o messaggio di eccezione grezzo esposto in UI (ogni `errorMessage` passa per `DomainError`, un
  tipo controllato), `DeepLinkResolver.parseDeepLink` fa parsing sicuro e bounded, `allowBackup=false`
  già impostato, nessun componente esportato oltre `MainActivity` (il nuovo `NotificationFireReceiver`
  è `exported="false"`).

## 3. Rischio riconosciuto, non corretto (con motivazione)

- **`SyncScheduler`/`BackgroundSyncCoordinator`: il retry/backoff è codice morto** —
  `RetryEngine.delayForAttempt` non viene mai chiamato, un elemento fallito ridiventa "eligible" al
  giro successivo senza alcun ritardo. **Perché non corretto ora**: nessun backend è cablato in
  questo sprint (deliberatamente — vedi sprint5_report.md, "mai simulare una sync riuscita"), quindi
  questo percorso non viene mai esercitato in produzione oggi; impatto pratico reale = zero finché
  non esiste un backend contro cui sincronizzare. Va risolto prima di cablare un
  `RemoteSyncTransport` reale, non prima.
- **Nessuna migrazione SQLDelight (`.sqm`) esiste per nessuno dei 4 database** — lo schema non è
  mai cambiato da quando ciascun modulo è stato creato, quindi `Schema.version` è sempre 1 e nulla
  si rompe oggi. Il rischio è futuro: il primo vero cambio di schema post-release non ha ancora un
  percorso di migrazione preparato.
- **Nessun `PRAGMA foreign_keys = ON`** in nessun driver SQLDelight — i vincoli `FOREIGN KEY`
  dichiarati nello schema sono oggi puramente documentali, non applicati da SQLite. Impatto pratico
  basso: il codice applicativo cancella già correttamente in cascata (verificato — `PermanentlyDeleteTask`
  cancella i sottotask prima del task), quindi non è emerso alcun orfano reale.
- **`NotificationHistoryStore` resta solo in-memory** — `NotificationFireReceiver` (punto 1 sopra)
  funziona finché il processo dell'app è vivo, ma un promemoria che scatta dopo che il sistema ha
  terminato del tutto il processo non trova nulla da mostrare. Serve uno store persistente per
  chiudere questo rischio davvero; è un lavoro più grande della correzione di questo sprint.
- **Deep link non ancora instradati**: `NotificationFireReceiver` apre `MainActivity` genericamente
  al tap, non salta al task specifico — nessuna navigazione basata su Intent è cablata nella app
  shell.
- **Stato UI transiente perso al cambio di configurazione** (rotazione schermo) — i ViewModel non
  sono ancora `androidx.lifecycle.ViewModel`/`SavedStateHandle`-backed (restano oggetti Kotlin puri,
  TDR-19), quindi sopravvivono ai cambi tab (fix di questo sprint) ma non a una vera ricreazione di
  Activity. I dati persistiti restano corretti; solo scroll/testo di ricerca in corso si perdono.
- **Nessuna icona reale dell'app** (`res/` vuoto) — decisione di brand/design, fuori perimetro di
  un audit tecnico.

## 4. Test

13 nuovi test comportamentali reali aggiunti questo sprint (non call-verification-only), a
copertura diretta di ogni fix sopra:

| Modulo | File | Nuovi test |
|---|---|---|
| `domain-account` | `SettingsUseCasesTest` | 2 (evento pubblicato al successo, non pubblicato al fallimento) |
| `domain-task` | `DeleteTaskTest` | 2 (`Restored`/`PermanentlyDeleted` pubblicati) |
| `core-sync` | `NetworkMonitorTest` | 2 (cancel ferma le notifiche, non tocca gli altri listener) |
| `feature-task/bridge` | `TaskSearchIndexBridgeTest` | 2 (restore re-indicizza ACTIVE, permanent delete rimuove dall'indice) |
| `feature-task/bridge` | `TaskNotificationBridgeTest` | 1 (restore riprogramma il promemoria) |
| `feature-task/bridge` | `TaskSyncOutboxBridgeTest` | 1 (restore mette in coda per la sync) |
| `feature-task` | `TaskListViewModelTest` | 3 (delete offre l'undo, UndoDelete ripristina, DismissUndoDelete non tocca il task) |

**Esecuzione reale, risultato finale**: 318 test `jvmTest` su 7 moduli senza dipendenza Compose nel
proprio classpath di test (`core-common`=7, `core-eventbus`=7, `core-search`=26, `core-sync`=95,
`core-notifications`=88, `domain-task`=79, `domain-account`=16) — **0 fallimenti, 0 errori**, con
`set -o pipefail` davanti a ogni invocazione Gradle. `compileKotlinJvm`/`compileTestKotlinJvm`
**verdi sull'intero repository** (tutti i moduli, inclusi quelli Compose-touched). `detekt` e
`ktlintCheck` **verdi sull'intero repository** (un vero `standard:import-ordering` trovato e
corretto durante questo stesso giro, vedi §1.9). `go build ./...`, `go vet ./...`, `go test ./...`
in `backend/` **verdi**. Nessun TODO/FIXME/XXX residuo in nessun sorgente del repository.

`jvmTest` per `feature-task`/`feature-core`/`feature-settings`/`feature-search`/
`core-notifications` (quando eseguito nello stesso invocation di moduli Compose-touched) resta
**bloccato** dalla stessa limitazione di rete del sandbox già documentata negli Sprint 2/4/5
(`dl.google.com` irraggiungibile dal proxy per le dipendenze transitive `androidx.*` di
`compose.foundation`) — confermato di nuovo questo sprint con lo stesso identico messaggio d'errore
(403 Forbidden). Non è una regressione: il codice di questi moduli compila realmente (main e test
sources, verificato).

## 5. Benchmark (numeri reali, confrontati con i budget della Bible)

- `SearchBenchmark`: ricerca sotto 100ms a 50.000 entità indicizzate (MFC-AC-07) — **passato**.
- `SyncBenchmark`: `EntityFieldMerger` a 100.000 entità, `ORSet.merge` a 100.000 link per
  dispositivo (MFC-E-14) — **entrambi passati**.
- `PersistentOutboxBenchmark`: throughput di enqueue e latenza di `peekNext` a 10.000 elementi in
  coda — **entrambi passati**.
- `HomeViewModelBenchmark` (budget <400ms composizione a freddo, da Sprint 4): file esiste, ma non
  eseguibile in questo sandbox per lo stesso motivo di rete di `feature-core`'s `jvmTest` — non
  dichiarato verde senza esecuzione reale.
- Nessun benchmark dedicato "creazione task" esiste ancora — gap onesto, non inventato: l'uso
  singolo di `CreateTask` è un solo insert SQLite, a basso rischio, ma non misurato con un numero.

## 6. Limiti dell'ambiente (invariati rispetto agli Sprint precedenti, riconfermati)

- **Android**: nessun SDK/emulatore in questo sandbox — `androidApp` non è mai stato compilato né
  eseguito qui, in nessuno sprint. Ogni riga Android-specifica scritta questo sprint (permission,
  `NotificationFireReceiver`, `OmniLifeApplication`, build variants) è codice reale ma non verificato
  da un compilatore Android — vedi `docs/omnilife/android_mvp_test_plan.md` per la checklist.
- **iOS**: nessun host macOS/Xcode — vincolo di Kotlin/Native e Swift stessi. Nessun vero target
  Xcode esiste ancora (solo un package Swift bootstrap) — vedi `docs/omnilife/ios_mvp_test_plan.md`.
- **`dl.google.com`**: bloccato dal proxy di questo sandbox — impedisce `jvmTest` (non
  `compileKotlinJvm`) per ogni modulo che tocca Compose Multiplatform nel proprio classpath di test.

## 7. Classificazione GREEN / YELLOW / RED

| Area | Stato | Motivazione |
|---|---|---|
| **Home** | 🟡 YELLOW | Dati reali, reattività e persistenza tra tab corrette questo sprint; comportamento Compose/lifecycle mai eseguito su device reale o in questo sandbox (blocco di rete su `jvmTest`). |
| **Tasks** | 🟡 YELLOW | Dominio (`domain-task`) genuinamente verde e testato (79 test); ciclo intero incluso notifiche/UI non confermato su device reale. |
| **Search** | 🟢 GREEN | Motore genuinamente testato (26 test + benchmark reale sotto budget), nessuna dipendenza di rete, injection-safe. |
| **Notifications** | 🟡 YELLOW | Era il rischio più grave dello sprint (nessuna notifica arrivava mai) — ora il percorso di consegna è reale e scritto correttamente, ma **non eseguibile in questo sandbox**: la prima vera conferma richiede un dispositivo. |
| **Sync** | 🟢 GREEN | Motore (idempotenza, persistenza coda, CRDT, leak fix) genuinamente verificato; il backend resta deliberatamente non cablato (non è una carenza di questo motore). |
| **Settings** | 🟡 YELLOW | Reattività tema/accento corretta questo sprint; UI mai eseguita su device. |
| **Onboarding** | 🟡 YELLOW | Logica testata (feature-core), UI mai eseguita su device. |
| **Android** | 🟡 YELLOW | Codice reale e sostanzioso scritto, ma **la prima vera compilazione (`assembleDebug`) non è mai stata eseguita in nessuno sprint** — vedi `android_mvp_test_plan.md` §0. |
| **iOS** | 🔴 RED | Solo una shell a 4 tab e un placeholder esistono; nessuna integrazione reale con `:shared`, nessun target Xcode ancora creato. |
| **Offline** | 🟡 YELLOW | Logica offline-first provata con test comportamentali (fake/in-memory + SQLite reale su JVM); non verificato con un dispositivo reale in modalità aereo. |
| **Persistence** | 🟢 GREEN | SQLDelight reale (non in-memory su JVM), round-trip campo-per-campo verificato; gap di migrazione futura documentato ma non attivo oggi. |
| **Security** | 🟢 GREEN | Audit completo, nessuna vulnerabilità concreta trovata; nessun segreto, nessun log sensibile, error message controllati, deep link parsing sicuro. |

## 8. Decisioni tecniche

TDR-41…46 (vedi `technology_decision_record.md`): singleton di processo `AppContainer`
(`OmniLifeApplication`), consegna reale delle notifiche Android (`NotificationFireReceiver`),
`SettingEvent`, `NetworkMonitorSubscription`, `TaskEvent.Restored`/`PermanentlyDeleted`, varianti di
build Debug/Internal/Release. Nessuna decisione cambia in modo sostanziale Product/Functional/UX/
Data Model/Technical Architecture Bible — tutte estendono pattern già approvati (Event Bus,
manual DI, expect/actual) a casi non ancora coperti.

## 9. Cosa manca per una vera release

1. Un dispositivo/emulatore Android reale su cui eseguire `docs/omnilife/android_mvp_test_plan.md`
   per intero — nessuna riga di codice Android è mai stata compilata in questo repository finora.
2. Un host macOS/Xcode per iniziare davvero il lavoro iOS (§1 di `ios_mvp_test_plan.md`) — oggi
   sotto la soglia minima per "test su device" (nessun target applicazione esiste).
3. Uno store persistente per `NotificationHistoryStore`, per chiudere il limite di sopravvivenza al
   kill del processo del fix di questo sprint.
4. Un backend reale (`RemoteSyncTransport`) prima che il retry/backoff di `core-sync` valga la pena
   di essere corretto.
5. Un'icona/branding reali.
6. Una prima migrazione SQLDelight (`.sqm`) preparata **prima** del primo cambio di schema post-MVP,
   non dopo.

## 10. Cosa NON deve essere implementato prima del test reale

Per esplicita regola di questo sprint: nessuna funzionalità di prodotto nuova (Finanze/Note/
Calendario/Abitudini/Obiettivi/Salute/AI, Marketplace, Enterprise, social). Inoltre, scoperto
durante questo sprint: **non** costruire ora un `WorkManager`/nuova tecnologia di scheduling per le
notifiche (il gap reale era l'assenza di un `BroadcastReceiver`, non l'assenza di WorkManager — TDR-42);
**non** introdurre ora un framework DI per risolvere la sopravvivenza dello stato UI ai cambi di
configurazione — la soluzione minima è probabilmente `androidx.lifecycle.ViewModel`/
`SavedStateHandle`, una decisione da registrare esplicitamente in un TDR quando si deciderà di
affrontarla, non da anticipare qui; **non** costruire un backend di sync solo per "far vedere" una
sync riuscita — resta vietato simulare un esito che non è mai avvenuto.
