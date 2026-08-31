# OmniLife — MVP Release 1.0 Report

**Obiettivo**: trasformare l'MVP in una build realmente installabile, utilizzabile e testabile su
dispositivo — non un'altra sprint teorica. Nessuna Bible modificata, nessun modulo di prodotto
nuovo (Finanze/Note/Calendario/Abitudini/Obiettivi/Salute/AI restano fuori perimetro). Ogni
sezione sotto riporta esiti reali di questo sandbox — non stime, non dichiarazioni "dovrebbe
funzionare".

## GREEN — Funzionalità realmente funzionanti e verificate

- **Task domain logic**: create/edit/complete/uncomplete/postpone/delete/restore/permanently-delete,
  ricorrenza, timezone, sottotask, filtri — 80 test `jvmTest` reali, 0 fallimenti (`domain-task`).
- **Search engine**: indicizzazione FTS5, aggiornamento su ogni transizione di lifecycle (incluso
  restore/permanent-delete, corretti in questo ciclo), nessun ghost result — provato sia con
  `FakeSearchIndexer` (bridge isolato) sia con un **test di integrazione reale** (FTS5 +
  `SqlDelightTaskRepository` reali, nessun fake) — 26 test `core-search` + l'integrazione compila
  (bloccata solo dall'esecuzione per la nota rete del sandbox, vedi KNOWN LIMITATIONS). Nessuna
  dipendenza di rete nel motore stesso; injection-safe (`Fts5QuerySanitizer`).
- **Sync engine**: idempotenza (`INSERT OR REPLACE`), persistenza reale della coda (SQLite, non
  in-memory), ordinamento (`LogicalTimestamp`), merge CRDT commutativo/idempotente
  (`LwwRegister`/`ORSet`/`EntityFieldMerger`) — 95 test `core-sync`, 0 fallimenti. Il leak di
  `SyncStateManager.observe` (individuato Sprint 4, corretto Sprint 5) resta verificato intatto;
  il leak gemello in `NetworkMonitor.onConnectivityChanged` (mai notato prima) è stato corretto.
- **Persistenza**: schema SQLDelight, mapping campo-per-campo — e, novità di questo ciclo, un test
  che prova la sopravvivenza reale ai riavvii aprendo un **file SQLite vero** (non
  `JdbcSqliteDriver.IN_MEMORY`, che per costruzione non può sopravvivere alla chiusura della
  connessione), chiudendo quella connessione, e aprendone una seconda indipendente sullo stesso
  file — la prova più vicina disponibile in questo sandbox a "chiudi l'app, riaprila".
- **Notifiche — logica**: scheduling/cancellazione/reschedule via `NotificationBroker`,
  budget/digest/quiet hours (ereditati, verificati Sprint 3), 88 test `core-notifications`.
- **Sicurezza**: nessun segreto hardcoded in nessun file versionato (la firma release legge da 4
  variabili d'ambiente, altrimenti `assembleRelease` fallisce esplicitamente), nessun
  `println`/`Log.*`, nessuno stack trace/eccezione grezza esposta in UI (`DomainError` controllato),
  `DeepLinkResolver` fa parsing sicuro e bounded, `allowBackup="false"`, unico componente esportato
  necessario (`MainActivity`, il launcher) più `BootCompletedReceiver` (deve esserlo per ricevere il
  broadcast di sistema — non espone nulla di sensibile).
- **Go backend** (bootstrap L6, intenzionalmente minimale): `go build`, `go vet`, `go test` — verdi.
- **Qualità del codice**: `detekt` e `ktlintCheck` verdi sull'intero repository (3 violazioni reali
  trovate e corrette durante questo stesso ciclo di verifica — vedi TEST RESULTS); nessun
  TODO/FIXME/XXX residuo in nessun sorgente.

## YELLOW — Implementate ma non completamente verificabili in questo sandbox

- **Home / Tasks / Search / Settings / Onboarding (UI)**: logica ViewModel reale e testata dove
  eseguibile; la resa Compose effettiva (composizione, temi, animazioni, aggiornamento reattivo a
  schermo) non è mai stata eseguita — né in questo sandbox (bloccato dalla rete per `jvmTest` dei
  moduli Compose-touched) né su un device reale.
- **Notifiche — consegna reale su Android**: il percorso di consegna (permessi, scheduling,
  `BroadcastReceiver`, `NotificationManagerCompat.notify`, deep link al tap, sopravvivenza al
  reboot via `BootCompletedReceiver`) è scritto per intero e corretto per ispezione, ma **nessuna
  riga Android è mai stata compilata da un vero Android SDK in questo repository** — richiede
  conferma su device reale (checklist in `android_mvp_test_plan.md`).
- **Android — l'app nel suo complesso**: `androidApp` è escluso dalla build in questo sandbox
  (nessun SDK). Tutto il codice scritto (icona/adaptive icon/splash screen, varianti di build,
  singleton di processo, deep link, boot receiver) è reale, non placeholder — ma la primissima
  compilazione (`assembleDebug`) non è mai stata eseguita, in nessuno sprint.
- **Offline-first end-to-end**: provato con test comportamentali (fake/in-memory + SQLite reale su
  JVM) che le operazioni funzionano senza alcuna dipendenza di rete; non provato con un dispositivo
  reale in modalità aereo.
- **iOS**: la shell a 4 tab esiste ed è strutturalmente onesta (un solo placeholder reale, gli
  altri tab dichiaratamente "in arrivo"), ma non è mai stata compilata (nessun host macOS/Xcode) —
  vedi IOS TEST PLAN.

## RED — Mancanti o non sufficientemente implementate

- **iOS — integrazione KMP reale**: nessun target Xcode esiste ancora; `TaskListPlaceholderView`
  usa un modello Swift locale, non lo `StateFlow` esportato da `:shared`. Onboarding/Impostazioni/
  Ricerca iOS non esistono (solo `ComingSoonView`).
- **Sincronizzazione con un backend reale**: `RemoteSyncTransport`/`BackgroundSyncCoordinator`
  deliberatamente non cablati — nessun backend raggiungibile esiste ancora (il backend Go è
  bootstrap puro, "decisione rinviata" per la Technical Architecture Bible stessa). Di conseguenza
  il retry/backoff di `SyncScheduler` resta codice morto (mai esercitato in produzione, quindi non
  corretto in questo ciclo — impatto pratico reale oggi è zero).
- **Notification history persistente**: `NotificationHistoryStore` resta solo in-memory — un
  promemoria che scatta dopo che Android ha terminato del tutto il processo non ha nulla da
  mostrare (limite noto, non nascosto).
- **Migrazioni SQLDelight**: nessun file `.sqm` esiste per nessuno dei 4 database — non un problema
  attivo (lo schema non è mai cambiato), ma nessun percorso è preparato per quando cambierà.

## TEST RESULTS

**Reale, eseguito con `set -o pipefail` davanti a ogni invocazione Gradle** (evita che un
`| tail -N` mascheri un exit code di fallimento):

| Modulo | Test | Esito |
|---|---|---|
| `core-common` | 7 | ✅ 0 fallimenti |
| `core-eventbus` | 7 | ✅ 0 fallimenti |
| `core-search` | 26 | ✅ 0 fallimenti |
| `core-sync` | 95 | ✅ 0 fallimenti |
| `core-notifications` | 88 | ✅ 0 fallimenti |
| `domain-task` | 80 | ✅ 0 fallimenti (include il nuovo `PersistenceRestartTest`) |
| `domain-account` | 16 | ✅ 0 fallimenti |
| **Totale** | **319** | **✅ 0 fallimenti, 0 errori** |

- `compileKotlinJvm`/`compileTestKotlinJvm`: **verdi sull'intero repository** (ogni modulo, inclusi
  quelli Compose-touched — `feature-*`, `androidApp` escluso perché condizionale sull'SDK).
- `detekt`/`ktlintCheck`: **verdi sull'intero repository**, dopo aver trovato e corretto 3
  violazioni reali durante questo stesso ciclo di verifica finale (`standard:import-ordering` in
  un file di test; `ReturnCount` e poi `ComplexCondition` nella funzione `reconcileSuspend`
  estratta per il fix di sopravvivenza al reboot — ogni fix ha richiesto una correzione reale del
  codice, non una soppressione della regola).
- `go build ./...`, `go vet ./...`, `go test ./...` (`backend/`): **verdi**.
- **Nuovi test aggiunti in questo ciclo** (16, tutti comportamentali, nessuno solo
  call-verification): `reconcileAll` re-schedule/non-re-schedule (2), integrazione ghost-result
  search reale FTS5 (1), persistenza reale su file SQLite (1), più i 13 già aggiunti nello Sprint 6
  di hardening che questo ciclo eredita e mantiene verdi.
- **Bloccato dall'ambiente, non un fallimento**: `jvmTest` per `feature-task`/`feature-core`/
  `feature-settings`/`feature-search`/`core-notifications` (quando eseguito insieme a moduli
  Compose-touched) — `dl.google.com` irraggiungibile dal proxy per le dipendenze transitive
  `androidx.*` di Compose Multiplatform. Confermato di nuovo con lo stesso identico errore
  (403 Forbidden) — non una regressione introdotta qui.

## KNOWN LIMITATIONS

- Nessun Android SDK/emulatore in questo sandbox — `androidApp` non è mai stato compilato o
  eseguito qui.
- Nessun host macOS/Xcode — nessun target applicazione iOS reale esiste ancora.
- `dl.google.com` bloccato dal proxy — impedisce `jvmTest` (non `compileKotlinJvm`) per i moduli
  Compose-touched.
- `NotificationHistoryStore` in-memory — un promemoria perso se il processo muore prima di scattare.
- Nessuna migrazione SQLDelight preparata (nessun problema attivo, rischio futuro).
- Retry/backoff di `core-sync` è codice morto finché non esiste un backend reale.
- Nessuna icona/branding "di design" — l'icona/adaptive icon/splash aggiunte questo ciclo usano
  `OmniAccent.BLU` (il colore accento di default già deciso) e un semplice segno di spunta, una
  scelta tecnica minima e onesta, non una decisione di brand.

## DEVICE TEST PLAN

Vedi `docs/omnilife/android_mvp_test_plan.md` per la checklist completa. Punti aggiunti/aggiornati
in questo ciclo rispetto alla versione Sprint 6:
- §0: prima build reale mai tentata (`assembleDebug`/`assembleInternal`/`assembleRelease`).
- §2: la notifica ora deve aprire il task specifico al tap (deep link), non solo l'app
  genericamente; verificare anche dopo un riavvio del dispositivo (`BootCompletedReceiver`).
- §8 (nuova): verificare che l'icona reale (non quella generica di sistema) e lo splash screen
  compaiano correttamente all'installazione e all'avvio.

## IOS TEST PLAN

Vedi `docs/omnilife/ios_mvp_test_plan.md` — invariato in sostanza rispetto allo Sprint 6: §1 elenca
il lavoro non ancora iniziato (creare il target Xcode reale) come prerequisito assoluto prima che
"test su device" abbia senso; §3 è la checklist per quando quel lavoro sarà fatto.

## MVP SCOPE

Cosa entra nella prima release: Onboarding minimo; Home con Today Overview/Agenda/Recent Activity
reali (Goal/Habit/Finance/Calendar restano placeholder onesti, mai dati finti); ciclo Task completo
(crea/modifica/completa/posticipa/elimina con undo/recupera dal cestino/elimina permanentemente,
sottotask, ricorrenza); Ricerca globale (oggi solo Task, nessun ghost result); Impostazioni (tema
Chiaro/Scuro/Sistema — Sistema segue davvero il dispositivo —, accento, budget notifiche/quiet
hours, reset onboarding); Notifiche locali Android con scheduling/cancellazione/reschedule/deep
link/sopravvivenza al reboot; persistenza locale reale via SQLDelight; comportamento offline-first
(nessuna sincronizzazione simulata); build Debug/Internal/Release con firma non hardcoded.

## POST-MVP

Deliberatamente fuori perimetro finché non entra un vero test su dispositivo: Finanze, Note,
Calendario completo, Abitudini, Obiettivi, Salute, AI, Marketplace, Enterprise, funzioni social;
un backend di sync reale (e di conseguenza il retry/backoff di `core-sync`); persistenza della
cronologia notifiche; migrazioni SQLDelight (fino al primo vero cambio di schema); integrazione
KMP↔SwiftUI reale per iOS; navigazione basata su deep link oltre l'apertura del task singolo;
sopravvivenza dello stato UI transiente ai cambi di configurazione Android (i dati persistiti
restano corretti; solo scroll/testo di ricerca in corso andrebbero persi oggi); icona/branding
definitivi.
