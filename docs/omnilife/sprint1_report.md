# Sprint 1 — Core Engine + Modulo Attività — Report

> **Stato:** completato (perimetro Must, offline) · **Data:** 2026-07-21

Questo report chiude il primo sprint di sviluppo reale (Engineering Plan, `EPIC-TASK` — l'Epic pilota) costruito sopra l'infrastruttura di bootstrap. Fonte di verità: le otto Bible, il Technology Decision Record, e — per lo scope esatto di questo sprint — le istruzioni del task stesso, che delimitano deliberatamente "Core Engine + Modulo Attività" senza sincronizzazione, autenticazione remota, notifiche push, altri moduli, o AI.

## 1. Funzionalità completate

Tutte le funzioni **Must (1.0)** del modulo Attività raggiungibili senza sincronizzazione/notifiche reali, secondo l'ordine di build già raccomandato dall'Engineering Plan (Creazione Base → Organizzazione → Ricorrenze → Viste):

| Story | Funzione | Stato |
|---|---|---|
| TASK-001 | Creazione (titolo unico campo obbligatorio) | ✅ |
| TASK-002 | Scadenza data/ora | ✅ |
| TASK-004 | Ricorrenza (daily/weekly/monthly/yearly/custom-interval), generazione lazy alla chiusura (INV-15) | ✅ |
| TASK-005 | Liste (Area → Lista) | ✅ |
| TASK-006 | Priorità a 3 livelli | ✅ |
| TASK-007 | Sottotask (1 livello), conferma esplicita se aperti alla chiusura del padre (TASK-AC-03) | ✅ |
| TASK-008 | Completamento, undo (Uncomplete) | ✅ |
| TASK-009 | Posticipa (stasera/domani/weekend/data) | ✅ |
| TASK-012 | Viste Oggi/Prossimi/Tutti | ✅ |
| TASK-013 | Riordino manuale (vince sempre, INV-10) | ✅ |
| TASK-014 | "In sospeso" (overdue), mai mischiato con Oggi | ✅ |
| TASK-R-01…08 | Regole di business | ✅ |
| Persistenza locale, ricerca locale, filtri, ordinamenti | (vedi §2) | ✅ |

**Non implementate in questo sprint** (Should/1.x o esplicitamente fuori perimetro): TASK-003 (dispatch reale dei promemoria — solo il dato `ReminderConfig` è modellato, la consegna richiede il Notification Broker), TASK-010 (posticipo intelligente, richiede Calendario), TASK-011/TASK-015/TASK-016 (Note/Obiettivi/Time-boxing via GraphLink), TASK-017 (duplicazione, Could/2.x), TASK-018 (archiviazione liste, Should/1.x). Nessuna di queste era nel perimetro Must richiesto.

**Correzione di perimetro rispetto alla richiesta originale**: è stato chiesto "Tag" tra i campi da implementare. La Data Model Bible (§10) e la UX Bible dichiarano esplicitamente **"non esistono tag liberi"** per Attività (P32) — l'unica classificazione documentata è Lista (Area→Lista) + Priorità. Sprint 1 implementa questi due assi, non un'entità Tag: introdurne una avrebbe contraddetto la fonte di verità esistente. Dettagliato in [README-BUILD.md §11](../../README-BUILD.md#11-convenzioni-di-ingegneria-a-partire-da-sprint-1).

## 2. Cosa è stato costruito, per layer

- **Core Engine** (`core-common`, `core-eventbus`): `Envelope`/`EntityLifecycleState` (Data Model Bible §00), `OmniResult` (tipo di errore esplicito, TDR-21), `EventBus` sincrono in-memory (Technical Architecture Bible §03).
- **Dominio** (`domain-task`): `Task`/`TaskList`/`Subtask` (DM-TASK-01/02/03), `RecurrenceRule` + `RecurrenceCalculator` (motore di ricorrenza scritto da zero, con clamp MFC-E-09 su mesi corti/anni non bisestili), `TaskEvent`, `TaskError`, `TaskRepository` (porta) + `SqlDelightTaskRepository` (implementazione, TDR-20), 9 casi d'uso (Create/Complete/Uncomplete/Delete+Restore+PermanentlyDelete/Postpone/Reorder/subtask ops/CreateList/GetTasksForView/Search/UpdateFields).
- **Presentazione** (`feature-task`): `TaskListViewModel`/`TaskDetailViewModel` — stato MVI (TDR-02) puro Kotlin, senza dipendenza da Compose/SwiftUI, per IA-030/031/032/034/035.
- **UI renderizzata (Compose/SwiftUI)**: **non implementata in questo sprint** — vedi §5 "Blocco per lo Sprint 2".

## 3. Test eseguiti e copertura

Tassonomia di riferimento: Engineering Plan §05 (UT/IT/E2E/PT/AT/ST). A questo stadio (pre-release-train) solo UT/IT sono richiesti; E2E/PT/AT/ST sono legati alla cadenza di release e non si applicano qui.

| Modulo | Test | Tipo |
|---|---|---|
| `core-common` | 7 | UT (Envelope, OmniResult) |
| `core-eventbus` | 7 | UT (pub/sub, ordinamento, cancellazione) |
| `domain-task` (`commonTest`) | 62 | UT (entità, regole, **RecurrenceCalculator: 14 test inclusa una simulazione a 12 mesi per weekly e per monthly-day-31**, casi d'uso con `FakeTaskRepository`) |
| `domain-task` (`jvmTest`) | 9 | IT (`SqlDelightTaskRepositoryTest` — CRUD/ricerca/liste/sottotask contro un vero database SQLite in-memory via driver JDBC) |
| `feature-task` | 14 | UT (`TaskListViewModel`/`TaskDetailViewModel`, incl. la scelta esplicita sui sottotask aperti, TASK-AC-03) |
| **Totale** | **99** | tutti verdi |

Criteri di accettazione della Functional Bible verificati esplicitamente da test dedicati: **TASK-AC-01** (ricorrenza weekly, esattamente una occorrenza generata), **TASK-AC-03** (sottotask aperti richiedono scelta esplicita), **MFC-E-09** (clamp giorno 31/Feb 29). **TASK-AC-04/05** (occorrenza monthly su anno bisestile; idempotenza multi-dispositivo) non sono coperti: il primo è verificato indirettamente dalla simulazione a 12 mesi ma non da un singolo caso isolato; il secondo richiede il Sync Engine (fuori perimetro, vedi §5).

Verifica di qualità eseguita ad ogni passo (non solo alla fine): `detekt` e `ktlint` puliti su `core-common`, `core-eventbus`, `domain-task`, `feature-task` — zero violazioni residue. Due soglie di detekt sono state alzate con motivazione esplicita in [`config/detekt/detekt.yml`](../../config/detekt/detekt.yml) (`TooManyFunctions` per un repository CRUD multi-entità, `ReturnCount`/`LongMethod` per lo stile guard-clause e i dispatcher MVI) — nessuna regola è stata disattivata, solo ricalibrata con motivazione. **Zero warning del compilatore Kotlin** nel codice di questo sprint (un warning residuo sull'API sperimentale di `compilerOptions` esiste in `build-logic`, pre-esistente dal bootstrap, non introdotto qui).

## 4. File creati/modificati

**Nuovi** (63 file sorgente + 4 file di configurazione):

```
core/core-common/src/commonMain/.../{EntityLifecycleState,DomainError,OmniResult}.kt
core/core-common/src/commonTest/.../OmniResultTest.kt
core/core-eventbus/src/commonMain/.../InMemoryEventBus.kt
core/core-eventbus/src/commonTest/.../InMemoryEventBusTest.kt

domain/domain-task/src/commonMain/kotlin/.../
  TaskPriority.kt, RecurrenceRule.kt, RecurrenceCalculator.kt, ReminderConfig.kt,
  TaskList.kt, Subtask.kt, TaskError.kt, TaskEvent.kt, TaskFilter.kt, TaskRepository.kt,
  persistence/{DatabaseDriverFactory,TaskMappers,SqlDelightTaskRepository}.kt,
  usecase/{CreateTask,CompleteTask,UncompleteTask,DeleteTask,PostponeTask,ReorderTasks,
           SubtaskUseCases,CreateTaskList,GetTasksForView,SearchTasks,UpdateTaskFields}.kt
domain/domain-task/src/{android,ios,jvm}Main/.../DatabaseDriverFactory.kt (actual per piattaforma)
domain/domain-task/src/commonMain/sqldelight/.../Task.sq
domain/domain-task/src/commonTest/kotlin/.../ (11 file di test, 62 test)
domain/domain-task/src/jvmTest/kotlin/.../SqlDelightTaskRepositoryTest.kt

feature/feature-task/src/commonMain/kotlin/.../
  TaskListUiState.kt, TaskListIntent.kt, TaskListViewModel.kt,
  TaskDetailUiState.kt, TaskDetailIntent.kt, TaskDetailViewModel.kt
feature/feature-task/src/commonTest/kotlin/.../ (5 file di test, 14 test)

docs/omnilife/technology_decision_record.md   — +TDR-19/20/21
docs/omnilife/sprint1_report.md                — questo file
config/detekt/detekt.yml                       — soglie ricalibrate
README-BUILD.md                                — §11 nuove convenzioni
```

**Modificati**: `domain/domain-task/build.gradle.kts` (plugin SQLDelight/serialization, dipendenze `api`), `build.gradle.kts` root (plugin condivisi con versione pinnata), `build-logic/.../omnilife.kmp.module.gradle.kts` (esclusione codice generato da ktlint, dipendenza `kotlinx-coroutines-test`), `gradle/libs.versions.toml` (+coroutines-test), i placeholder di 9 altri moduli bootstrap (wrap di righe >120 caratteri, nessuna modifica funzionale).

## 5. Decisioni architetturali prese (documentate come richiesto)

Ogni decisione tecnica non coperta dalle Bible esistenti è stata **fermata, proposta, e documentata prima di essere implementata**, seguendo il processo richiesto:

- **TDR-19 · Dependency Injection**: injection manuale via costruttore (nessun framework) — grafo piccolo, verificabile a compile-time.
- **TDR-20 · Libreria SQLite per KMP**: SQLDelight — TDR-06 fissa il motore, non la libreria di accesso.
- **TDR-21 · Tipo di errore**: `OmniResult` sigillato dedicato, non eccezioni — rende verificabile la policy di traduzione errori di Technical Architecture Bible §07.
- **Convenzioni di ingegneria** (README-BUILD.md §11, non un TDR — non richiedono confronto di alternative): forma concreta dell'Event Bus (sincrono, `sealed interface DomainEvent` per modulo), dove vivono Repository/Use Case (stesso modulo `domain-*`, non ancora un modulo `platform-*` dedicato), forma concreta dello stato MVI.
- **Deroga di sicurezza dichiarata**: il database di questo sprint **non è cifrato** nonostante TDR-06 lo richieda — cifrare richiede la gerarchia di chiavi del Servizio di Sicurezza (`core-security`), ancora un'interfaccia segnaposto, esplicitamente fuori perimetro ("non implementare autenticazione remota"). Non è stata inventata una cifratura ad-hoc.

## 6. Blocchi per lo Sprint 2

1. **Schermate renderizzate (Compose/SwiftUI) non implementate.** Motivazione: "usa esclusivamente i componenti del Design System" richiede una libreria di componenti Compose/SwiftUI **che non esiste ancora come codice** — il Design System Bible è solo specifica (nessun componente `core-designtokens`/`platform-*` è stato implementato oltre al placeholder di bootstrap). Costruire schermate ad-hoc avrebbe violato quel vincolo o richiesto di costruire l'intera libreria di componenti qui, uno scope enormemente più grande di "Core Engine + Modulo Attività" e non verificabile in questo sandbox (nessun SDK Android, nessun host macOS). Lo strato di stato MVI (`feature-task`) è già pronto e testato: le schermate reali sono un innesto diretto su quello stato, non un nuovo lavoro di dominio.
2. **Promemoria (TASK-003)**: solo il dato `ReminderConfig` esiste; la programmazione/consegna reale richiede il Notification Broker (`core-notifications`, ancora segnaposto).
3. **Idempotenza multi-dispositivo (TASK-AC-05)**: la generazione della prossima occorrenza ricorrente usa un nuovo ID casuale per dispositivo; la convergenza cross-dispositivo per lo stesso periodo richiede il Sync Engine (`core-sync`, fuori perimetro, nessuna sincronizzazione in questo sprint).
4. **Cifratura del database** (vedi §5) — richiede `core-security`.
5. **Gate di dipendenza automatico**: nessun controllo CI impedisce ancora a un modulo di violare le regole di dipendenza — già annotato come TODO nel report di bootstrap, non ancora affrontato.
6. **TASK-010/011/015/016/017/018**: funzioni Should/Could non incluse in questo sprint Must-only.
7. **Ricerca cross-modulo (SRCH-*)**: `SearchTasks` è locale al modulo Attività, non il motore di ricerca globale (`core-search`, un Servizio Core separato non toccato in questo sprint).
