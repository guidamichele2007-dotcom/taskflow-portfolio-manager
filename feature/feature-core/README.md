# feature:feature-core

**Scopo**: Presentazione (L1/L2) della Home "Oggi" (Sprint 4). Onboarding, Galleria Moduli e
Revisione Settimanale (le altre tre funzioni del modulo Core) restano fuori da questo sprint.

**Riferimento**: Functional Bible, doc 01 (HOME-001…008, ONB/GAL/REV); UX Bible IA-001…016;
Technical Architecture Bible §01 §4 (L1/L2); TDR-02 (MVI/UDF).

**Stato**: Home "Oggi" implementata — Dashboard, Today Overview, Agenda, Recent Activity, Global
Search Entry, Quick Actions, Widget System, Progress Cards, Goal/Habit Summary, Finance/Calendar
Summary Placeholder, Notification Center, Sync Status, Empty/Loading/Error States. Dipende
esclusivamente da `core-designsystem` (Core UI Kit), `core-search`, `core-sync`,
`core-notifications` — nessun modulo `domain-*`: ogni sezione che richiederebbe dati di dominio è
un placeholder funzionale (vedi `docs/omnilife/sprint4_report.md`).

## Componenti

| Componente richiesto | File | Ruolo |
|---|---|---|
| Dashboard | `HomeScreen.kt` | Il contenitore L1: TopBar, sync status, ricerca, notifiche, azioni rapide, widget |
| Today Overview / Agenda / Recent Activity / Goal Summary / Habit Summary | `HomeWidget.kt`, `HomeComponents.kt` (`HomeWidgetCard`) | Ognuna un `HomeWidgetKind`, contenuto placeholder finché nessun `domain-*` è collegato |
| Global Search Entry | `HomeComponents.kt` (`GlobalSearchEntry`) | Reale — wired a `core-search`'s `UnifiedSearchService` |
| Quick Actions | `HomeComponents.kt` (`QuickActionsRow`), `HomeUiState.kt` (`HomeQuickAction`) | Descrittori reali; handler placeholder (nessun `domain-*`) |
| Widget System | `HomeWidget.kt` (`HomeWidgetRegistry`) | Attivazione/riordino reali (HOME-002/003); il contenuto resta placeholder |
| Progress Cards | `core-designsystem`'s `OmniProgress` (riusato, non reimplementato) | — |
| Finance/Calendar Summary Placeholder | `HomeWidget.kt` (`FINANCE_SUMMARY`/`CALENDAR_SUMMARY`) | Placeholder esplicitamente richiesti dal task |
| Notification Center | `HomeComponents.kt` (`NotificationCenterPanel`) | Reale — wired a `core-notifications`' `NotificationHistoryStore`/`NotificationBroker` |
| Sync Status | `HomeComponents.kt` (`SyncStatusRow`) | Reale — wired a `core-sync`'s `SyncStateManager`, reattivo (nessun refresh manuale, HOME-007) |
| Empty/Loading/Error States | `HomeSectionState.kt` + `core-designsystem`'s `OmniEmptyState`/`OmniLoadingState`/`OmniErrorState` | Un solo stato condiviso da ogni sezione |
| Facciata MVI | `HomeViewModel.kt`, `HomeUiState.kt`, `HomeIntent.kt` | Pure Kotlin, nessuna dipendenza Compose — stesso pattern di `feature-task`'s `TaskListViewModel` |

## Cosa NON fa questo modulo

- Nessuna dipendenza `domain-*`: ogni sezione di contenuto (Today Overview, Agenda, Recent
  Activity, Goal/Habit/Finance/Calendar Summary) è un placeholder funzionale — struttura reale,
  dati non ancora collegati.
- Nessun intent "Refresh"/pull-to-refresh: HOME-007 lo vieta esplicitamente per design.
- Nessuna Onboarding/Galleria Moduli/Revisione Settimanale — le altre tre funzioni del modulo
  Core del Functional Bible, fuori dal perimetro di questo sprint.

## Verificato in questo sandbox

`compileKotlinJvm`/`compileTestKotlinJvm`/`detekt`/`ktlintCheck` passano. **L'esecuzione di
`jvmTest` non è verificabile in questo sandbox** — stesso limite di rete già documentato nello
Sprint 2 report (dipendenze transitive `androidx.lifecycle`/`androidx.collection`/
`androidx.annotation` di `compose.foundation` risolvibili solo da `dl.google.com`, bloccato dalla
policy di rete): qui si manifesta più ampiamente, perché questo modulo — a differenza di
`core-designsystem`, che non ha mai avuto file `jvmTest` eseguibili dopo lo Sprint 2 — ha test
JVM reali (`HomeViewModelTest`, `HomeWidgetRegistryTest`, `HomeViewModelBenchmark`), quindi il
task `jvmTest` tenta la risoluzione del classpath e fallisce, anche se quei test non toccano mai
Compose. I test compilano correttamente e sarebbero eseguibili in un ambiente con accesso di
rete completo. Vedi `docs/omnilife/sprint4_report.md`.
