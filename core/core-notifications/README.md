# core:core-notifications

**Scopo**: Broker centrale delle Notifiche — budget, raggruppamento in digest, orari di silenzio,
notifiche locali/ricorrenti/azionabili, deep link, snooze, riprogrammazione intelligente,
gestione fuso orario, consegna in background, retry, cronologia. Nessun modulo notifica
direttamente.

**Riferimento**: Functional Bible NTF-001…008, NTF-R-01…05; Data Model Bible DM-NTF-01; Technical
Architecture Bible §02/§13; TDR-21, TDR-26, TDR-29…33.

**Stato**: implementazione completa (Sprint 3 — Scope Change approvato: le notifiche, inizialmente
escluse dal perimetro raffinato dello Sprint 3, sono state reintrodotte su richiesta esplicita).

## Componenti

| Componente richiesto | File | Ruolo |
|---|---|---|
| Notification Scheduler | `NotificationScheduler.kt` (expect/actual) | "A questo istante, esegui questo callback" — la primitiva di basso livello (TDR-26) |
| Local Notifications | `LocalNotificationService.kt` | Compone scheduler + canale + permesso per mostrare davvero una notifica (NTF §2: "generate localmente") |
| Recurring Notifications | `RecurringNotificationPlanner.kt` | Cadenza propria di un promemoria ricorrente (giornaliera/settimanale/intervallo) — non la RecurrenceRule di `domain-task`, questo modulo non dipende da alcun `domain-*` |
| Notification Actions | `NotificationActionDispatcher.kt` | NTF-005: completa/posticipa/spunta dalla notifica, pubblica `NtfActionPerformed`, mai esegue l'azione stessa |
| Deep Links | `DeepLinkResolver.kt` | URI `omnilife://<tipo>/<id>` (TDR-32) — costruzione/parsing, nessuna dipendenza di navigazione |
| Notification Categories | `NotificationCategory.kt` (`NotificationCategoryRegistry`) | Granularità per NTF-007/R-03; traccia gli ignorati consecutivi per NTF-006 |
| Notification Permissions | `NotificationPermissionManager.kt` (expect/actual) | Stato del permesso di sistema; NTF §2 edge case: negato → contenuti solo in-app (P6) |
| Notification Channels | `NotificationChannelRegistry.kt` (expect/actual) | Canali Android (importanza derivata dalla priorità, TDR-33); no-op su iOS/JVM |
| Snooze | `SnoozeManager.kt` | NTF-AC-02: nuova richiesta con nuovo id, orario fisso o "stasera" (via `TimezoneHandler`) |
| Smart Reschedule | `SmartRescheduler.kt` | NTF-AC-03: un promemoria soppresso dal silenzio si mostra al risveglio solo se ancora rilevante (finestra 4h, TDR-29) |
| Timezone Handling | `TimezoneHandler.kt` | MFC-E-07: l'orario locale dell'intenzione sopravvive ai cambi di fuso; MFC-E-08 (DST) delegato alla conversione di piattaforma |
| Quiet Hours | `QuietHours.kt` | NTF-004: finestra notturna di default 22-8, valutata in orario locale |
| Background Delivery | `BackgroundDeliveryCoordinator.kt` | Ciò che un job di piattaforma invoca al risveglio: risolve i differiti e il digest dovuto |
| Retry Logic | `NotificationRetryEngine.kt` + `showWithRetry` | Backoff proprio (indipendente da quello di `core-sync`, per restare un modulo autonomo) per un fallimento della chiamata di piattaforma (TDR-31) |
| Notification History | `NotificationHistoryStore.kt` | NTF-007: sorgente dati del centro notifiche in-app |
| Facciata | `NotificationBroker.kt` | NTF-001: l'unico punto d'ingresso — applica budget, digest, silenzi, categorie, poi consegna |
| Facciata di composizione | `NotificationEngine.kt` | Compone tutto con implementazioni in-memory di default, mirror di `core-sync`'s `SyncEngine` |
| Errori | `NotificationError.kt` | `CategoryDisabled`/`RequestNotFound`/`PermissionDenied` (TDR-21) |

## Cosa NON fa questo modulo

- Non dipende da alcun modulo `domain-*` — `NotificationRecurrenceRule` è la cadenza propria di un
  promemoria, non la `RecurrenceRule` di `domain-task` (L4 non dipende mai da L3).
- Non esegue l'azione di un'azionabile (completa/posticipa) — pubblica solo `NtfActionPerformed`;
  il modulo proprietario dell'entità la esegue.
- Non implementa un vero trasporto di rete: `NotificationScheduler` è locale-solo, coerente con
  NTF §2 ("i push remoti servono solo da trigger di sync silenziosi, mai contenuti").
- Non implementa il collegamento reale al job scheduler di piattaforma
  (WorkManager/BGTaskScheduler) — `BackgroundDeliveryCoordinator.runOnce` è la logica pura che un
  simile job invocherebbe; il trigger stesso è wiring app-shell, fuori da questo modulo.

## Verificato in questo sandbox

Solo il target JVM è compilato/testato (nessun SDK Android, nessun host macOS/Xcode —
[README-BUILD.md](../../README-BUILD.md) §4). `NotificationScheduler`'s JVM `actual`
(`ScheduledExecutorService`) è reale e verificato con timing test reali (non simulati);
`NotificationPermissionManager`/`NotificationChannelRegistry` JVM `actual` sono stand-in
in-memory testabili, non chiamate di piattaforma reali. Tutta la logica di decisione (budget,
digest, silenzi, retry, ricorrenza, smart reschedule, deep link) è Kotlin puro, verificata
indipendentemente da qualunque piattaforma.
