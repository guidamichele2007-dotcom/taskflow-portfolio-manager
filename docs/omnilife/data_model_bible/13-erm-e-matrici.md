# 13 · Entity Relationship Map e Matrici

> Eredita tutte le schede entità ([01](01-entita-sistema.md)…[10](10-entita-notifiche-insight-ricerca.md)). Nessun diagramma grafico: mappa testuale, come richiesto. Nessuno schema fisico: relazioni concettuali.

## 1. Entity Relationship Map (testuale)

```
DM-SYS-01 Account (1) ──── possiede tutto ────► ogni entità (account_proprietario)
   │
   ├──(1:N)── DM-SYS-02 Device
   ├──(1:N)── DM-SYS-03 ModuleActivation
   ├──(1:1)── DM-SYS-04 Subscription
   ├──(1:1..N)── DM-SYS-05 RecoveryKeyMetadata (storico rigenerazioni)
   └──(1:N)── DM-SYS-06 Setting

── CATTURA ──
DM-CAPT-01 CaptureInboxItem ──(si trasforma in)──► [Task | Transaction | Habit | Note | CAL-004 evento]
   (nessuna relazione strutturale persistente: la trasformazione consuma l'Inbox item)

── GRAFO (trasversale) ──
DM-LINK-01 GraphLink (entita_a, entita_b, ruolo)
   collega opzionalmente, in qualsiasi combinazione:
   Task/TaskList ⇄ Goal            (ruolo: contributo_obiettivo, via TASK-015/GOAL-002)
   SavingsGoal   ⇄ Goal            (ruolo: contributo_obiettivo, via FIN-008/GOAL-002)
   Habit         ⇄ Goal            (ruolo: contributo_obiettivo, via HAB-012/GOAL-002)
   Note          ⇄ [Task|Transaction|Habit|Goal]  (ruolo: collegamento_nota, via NOTE-003)

── ATTIVITÀ ──
DM-TASK-02 TaskList (1) ──(1:N, proprietaria)──► DM-TASK-01 Task
DM-TASK-01 Task (1) ──(1:N, proprietaria)──► DM-TASK-03 Subtask
DM-TASK-01 Task (1) ──(0:1, non proprietaria)──◄ DM-CAL-03 TimeBox (referenzia il task)
DM-TASK-01 Task ──(GraphLink)──► DM-GOAL-01 Goal
DM-TASK-01 Task ──(GraphLink)──► DM-NOTE-01 Note

── FINANZE ──
DM-FIN-02 FinancialAccount (1) ──(1:N, proprietaria)──► DM-FIN-01 Transaction
DM-FIN-03 Category (1) ──(1:N, non proprietaria)──► DM-FIN-01 Transaction
DM-FIN-03 Category (1) ──(0:1, gerarchia)──► DM-FIN-03 Category (sottocategoria)
DM-FIN-04 Budget (0:1) ──► DM-FIN-03 Category (soglia per categoria, o globale se assente)
DM-FIN-05 SavingsGoal (0:1) ──► DM-FIN-02 FinancialAccount (conto collegato)
DM-FIN-05 SavingsGoal ──(GraphLink)──► DM-GOAL-01 Goal

── ABITUDINI ──
DM-HAB-01 Habit (1) ──(1:N, proprietaria)──► DM-HAB-02 HabitExecution
DM-HAB-01 Habit ──(GraphLink)──► DM-GOAL-01 Goal
DM-HLTH-01 HealthPlatformReading ──(genera evento consumato da)──► DM-HAB-02 HabitExecution (fonte=automatica_salute)

── CALENDARIO (deroga) ──
DM-CAL-01 CalendarSource (1) ──(0:N, non nostra)──► DM-CAL-02 EventReference (esterna, sola lettura)
DM-CAL-01 CalendarSource (1) ──(0:N, proprietaria)──► DM-CAL-03 TimeBox
DM-CAL-03 TimeBox ──(N:1, non proprietaria)──► DM-TASK-01 Task

── NOTE ──
DM-NOTE-01 Note (1) ──(1:N, proprietaria, versionamento a snapshot)──► DM-NOTE-02 NoteVersion
DM-NOTE-01 Note ──(GraphLink, multiplo)──► [qualsiasi entità]

── SALUTE ──
DM-HLTH-01 HealthPlatformReading (deroga totale, cache locale, mai su cloud)
DM-HLTH-02 ManualHealthMetric (entità piena, ciclo MFC completo)

── OBIETTIVI (l'ombrello) ──
DM-GOAL-01 Goal ──(GraphLink, multiplo, ruolo=contributo_obiettivo)──► [Task/TaskList | SavingsGoal | Habit | Note]

── SERVIZI ──
DM-NTF-01 NotificationRequest ──(riferimento in sola lettura)──► [qualsiasi entità che genera un promemoria]
DM-INS-01 InsightRuleConfig (config di sistema, non utente)
DM-INS-02 InsightFeedback ──► DM-INS-01 (riferimento a famiglia di regola)
DM-SRCH-01 RecentSearchQuery (locale, isolata, nessuna relazione)
```

## 2. Matrice Entità → Moduli

| Entità | Modulo proprietario |
|---|---|
| DM-SYS-01…06 | Core / Sistema (Account, Impostazioni, Sicurezza) |
| DM-CAPT-01 | Cattura |
| DM-LINK-01 | Core (il grafo è posseduto dal Core, non da un modulo) |
| DM-TASK-01…03 | Attività |
| DM-FIN-01…05 | Finanze |
| DM-HAB-01…02 | Abitudini |
| DM-CAL-01…03 | Calendario |
| DM-NOTE-01…02 | Note |
| DM-HLTH-01…02 | Salute |
| DM-GOAL-01 | Obiettivi |
| DM-NTF-01 | Notifiche |
| DM-INS-01…02 | Insight |
| DM-SRCH-01 | Ricerca |

## 3. Matrice Entità → Funzioni (Functional Bible)

| Entità | Funzioni che la creano/modificano | Funzioni che la consumano |
|---|---|---|
| Account | ONB-004/007, SET-003, PROF-001 | SEC-*, EXP-003 |
| Device | SYNC-001, SEC-001 | SYNC-002/003 |
| ModuleActivation | GAL-002/003 | HOME-002, ogni funzione con dipendenza "moduli attivi" |
| Subscription | SET-003 | MFC §4 stati Free/Trial/Premium |
| RecoveryKeyMetadata | SEC-002 | SEC-AC-01/02 |
| Setting | SET-001…004 | Ogni funzione che eredita un'impostazione (es. NTF budget) |
| CaptureInboxItem | CAPT-008/010 | REV-001 (smistamento) |
| GraphLink | NOTE-003, TASK-015, HAB-012, GOAL-002 | GOAL-003 (aggregazione), NOTE-074/IA-074 (UX) |
| Task | TASK-001…018 | HOME-001/004, CAL-002/005, GOAL-002/003, SRCH |
| TaskList | TASK-005/018 | TASK-012 (viste) |
| Subtask | TASK-007 | TASK-AC-03 |
| Transaction | FIN-001/004/006/011/012/013/014 | FIN-005/007, GOAL-004, HOME |
| FinancialAccount | FIN-003/004 | FIN-001/010 |
| Category | FIN-002 | FIN-001/005/010 |
| Budget | FIN-005 | HOME, NTF |
| SavingsGoal | FIN-008 | GOAL-002/004 |
| Habit | HAB-001…013 | HOME-001/004, GOAL-002/003 |
| HabitExecution | HAB-004/010 | HAB-005/009 |
| CalendarSource | CAL-001/054 | CAL-002/004/005 |
| EventReference | CAL-001/002/003 | CAL-002, GOAL (indirettamente) |
| TimeBox | CAL-005 | TASK-016, CAL-002 |
| Note | NOTE-001…009 | SRCH, GOAL-002/003 |
| NoteVersion | NOTE-006 | NOTE-AC-01/03 |
| HealthPlatformReading | HLTH-001 | HAB-010, HLTH-002 |
| ManualHealthMetric | HLTH-004 | HLTH-005, INS |
| Goal | GOAL-001…008 | HOME, REV-001, INS |
| NotificationRequest | NTF-001…008 | NTF-006/007 |
| InsightRuleConfig | INS-003 (Registry) | INS-001/002/004 |
| InsightFeedback | INS-005 | INS-003 (taratura) |
| RecentSearchQuery | SRCH-004 | SRCH-001 (suggerimenti) |

## 4. Matrice Entità → Eventi

| Entità | Eventi pubblicati | Eventi sottoscritti (che la aggiornano) |
|---|---|---|
| ModuleActivation | `core.module.activated`, `core.module.deactivated` | — |
| CaptureInboxItem | `capt.inbox.item.added`, `capt.item.captured` (alla risoluzione) | `core.module.activated` |
| Task | `task.item.created/completed/uncompleted/rescheduled/deleted`, `task.overdue.count.changed` | `capt.item.captured`, `cal.day.load.changed`, `core.day.changed` |
| Transaction | `fin.expense.created/updated/deleted`, `fin.income.created…`, `fin.recurrence.executed` | `capt.item.captured`, `core.day.changed` |
| Budget | `fin.budget.threshold.crossed` | `fin.expense.created` (ricalcolo) |
| Habit / HabitExecution | `hab.habit.completed/progressed/skipped`, `hab.adherence.band.changed` | `hlth.workout.detected`, `hlth.steps.threshold`, `core.day.changed`, `capt.item.captured` |
| CalendarSource / TimeBox | `cal.day.load.changed`, `cal.timebox.created/moved/deleted` | `task.item.rescheduled`, `capt.item.captured` |
| Note | `note.item.created/linked/unlinked` | `capt.item.captured`, `*.item.trashed` |
| HealthPlatformReading | `hlth.workout.detected`, `hlth.steps.threshold` | `core.module.activated/deactivated` |
| ManualHealthMetric | `hlth.manual.logged` | — |
| Goal | `goal.progress.changed`, `goal.milestone.reached`, `goal.completed` | `task.item.completed`, `fin.expense/income.created`, `hab.habit.completed`, `note.item.linked`, `core.module.activated/deactivated` |
| NotificationRequest | `ntf.action.performed` | `ntf.request` (interno, da ogni modulo) |
| InsightFeedback | (nessuno pubblicato) | — |
| — (Insight, generico) | `ins.insight.available`, `ins.digest.ready` | eventi di ogni modulo (osservatore passivo) |

---

*Prossimo: [Report finale](14-report.md)*
