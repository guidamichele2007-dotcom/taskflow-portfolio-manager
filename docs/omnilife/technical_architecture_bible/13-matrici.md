# 13 · Matrici Architetturali

> Le quattro matrici richieste. Fonti: [02](02-moduli-responsabilita-boundaries.md), [03](03-event-driven-architecture.md), [Data Model Bible §13.4](../data_model_bible/13-erm-e-matrici.md#4-matrice-entità--eventi), [01](01-architettura-generale-e-layer.md).

## 1. Matrice Modulo → Responsabilità

| Modulo | Layer primario | Responsabilità architetturale | Non è mai responsabile di |
|---|---|---|---|
| Core | L2 (orchestrazione) + proprietario del Grafo in L4 | Composizione Home, Onboarding, Galleria, Revisione; possiede il GraphLink | Regole di business di un modulo specifico |
| Cattura | L4 (servizio condiviso) | Instradare testo/voce verso il modulo o l'Inbox | Interpretare il *significato* di dominio (delega al modulo destinatario la validazione finale) |
| Attività | L3 | Ciclo di vita di Task/Liste/Sottotask, regole TASK-R-* | Sapere se un Task è collegato a un Obiettivo (lo sa solo il GraphLink) |
| Finanze | L3 | Ciclo di vita di Transazioni/Conti/Categorie/Budget, regole FIN-R-* | Calcolare il progresso di un Obiettivo (lo fa GOAL, leggendo via GraphLink) |
| Abitudini | L3 | Ciclo di vita di Abitudini/Esecuzioni, calcolo dell'aderenza | Sapere quali altri moduli osservano i propri eventi |
| Calendario | L3 (con deroga) | Mediare (non possedere) gli eventi esterni; possedere i TimeBox | Essere fonte di verità per gli eventi (lo è il provider esterno) |
| Note | L3 | Ciclo di vita di Note/Versioni | Generare promemoria (delega a Attività, NOTE-R-02) |
| Salute | L3 (con deroga) | Mediare le letture di piattaforma; possedere le metriche manuali | Sincronizzare dati di piattaforma sul cloud (vietato per costruzione) |
| Obiettivi | L3 | Possedere Goal; aggregare i contributi tramite GraphLink | Possedere i contributi stessi (restano dei moduli di origine) |
| Ricerca | L4 | Indicizzare (proiezione derivata) le entità dei moduli attivi | Essere fonte di verità per qualunque dato |
| Notifiche | L4 | Broker centrale: budget, raggruppamento, silenzi | Decidere il contenuto di dominio di una notifica (lo decide il modulo richiedente) |
| Widget | Proiezione di L1 | Superficie di presentazione alternativa | Contenere logica di dominio propria |
| Sync/Backup/Export | L4 | Convergenza, snapshot, esportazione | Conoscere il significato di dominio dei dati che sincronizza |
| Impostazioni/Sicurezza | L4 + entità di sistema in L3 | Configurazione, autenticazione, cifratura | Contenere logica di un modulo di dominio |
| Insight | L4 | Osservare passivamente gli eventi, generare osservazioni locali | Avere una porta verso la rete (vietato per costruzione) |

## 2. Matrice Modulo → Eventi

*(Derivata da [Data Model Bible §13.4](../data_model_bible/13-erm-e-matrici.md#4-matrice-entità--eventi) e [Functional Bible MFC §8](../functional_bible/00-modello-funzionale-comune.md#8-modello-degli-eventi-bus-interno); qui organizzata per modulo anziché per entità.)*

| Modulo | Eventi pubblicati (principali) | Eventi sottoscritti (principali) |
|---|---|---|
| Core | `core.module.activated/deactivated`, `core.day.changed`, `core.review.completed` | `*.item.created/completed/changed` (per la Home) |
| Cattura | `capt.item.captured`, `capt.inbox.item.added` | `core.module.activated` |
| Attività | `task.item.created/completed/uncompleted/rescheduled/deleted`, `task.overdue.count.changed` | `capt.item.captured`, `cal.day.load.changed`, `core.day.changed` |
| Finanze | `fin.expense.created/updated/deleted`, `fin.income.created…`, `fin.budget.threshold.crossed`, `fin.recurrence.executed` | `capt.item.captured`, `core.day.changed` |
| Abitudini | `hab.habit.completed/progressed/skipped`, `hab.adherence.band.changed` | `hlth.workout.detected`, `hlth.steps.threshold`, `core.day.changed`, `capt.item.captured` |
| Calendario | `cal.day.load.changed`, `cal.timebox.created/moved/deleted` | `task.item.rescheduled`, `capt.item.captured` |
| Note | `note.item.created/linked/unlinked` | `capt.item.captured`, `*.item.trashed` |
| Salute | `hlth.workout.detected`, `hlth.steps.threshold`, `hlth.manual.logged` | `core.module.activated/deactivated` |
| Obiettivi | `goal.progress.changed`, `goal.milestone.reached`, `goal.completed` | `task.item.completed`, `fin.expense/income.created`, `hab.habit.completed`, `note.item.linked`, `core.module.activated/deactivated` |
| Notifiche | `ntf.action.performed` | `ntf.request` (da ogni modulo) |
| Insight | `ins.insight.available`, `ins.digest.ready` | eventi di ogni modulo (osservatore passivo universale) |
| Ricerca | (nessuno pubblicato) | `*.item.*` (per l'aggiornamento incrementale dell'indice) |

## 3. Matrice Modulo → Dipendenze

| Modulo | Dipende da (Servizi Core, L4) | Dipende da (Adattatori, L5) | Dipende da altri moduli (L3)? |
|---|---|---|---|
| Attività | Bus Eventi, Grafo, Notifiche, Ricerca, Sync | Persistenza locale | **No** — riceve solo eventi/GraphLink |
| Finanze | Bus Eventi, Grafo, Notifiche, Ricerca, Sync | Persistenza locale | **No** |
| Abitudini | Bus Eventi, Grafo, Notifiche, Sync | Persistenza locale, osservatore piattaforma Salute (porta dichiarata) | **No** |
| Calendario | Bus Eventi, Sync (solo per i TimeBox) | Fornitore calendario di sistema | **No** — riceve `task.item.rescheduled`, non "conosce" Attività |
| Note | Bus Eventi, Grafo, Ricerca, Sync | Persistenza locale | **No** |
| Salute | Bus Eventi | Piattaforma Salute di sistema, enclave di sicurezza | **No** |
| Obiettivi | Bus Eventi, Grafo | Persistenza locale | **No** — legge i contributi solo tramite GraphLink + eventi |
| Cattura | Registro Moduli | — | **No** — instrada, non legge lo stato interno dei moduli |
| Ricerca | (è essa stessa un Servizio Core) | Persistenza locale (indice) | **No** — opera su un contratto generico "entità indicizzabile" |
| Notifiche | Registro Moduli | Trasporto notifiche (sistema) | **No** — riceve richieste generiche, non conosce il dominio |
| Insight | (è essa stessa un Servizio Core) | — (nessuna porta di rete, per costruzione) | **No** — osservatore passivo via eventi |
| Sync/Backup/Export | Servizio di Sicurezza | Adattatore di rete/sync, storage locale | **No** — opera su entità generiche via porta di persistenza |
| Impostazioni/Sicurezza | — | Enclave di sicurezza, autenticazione di sistema | **No** |

**Lettura della matrice**: la colonna "Dipende da altri moduli" è **sempre No** per ogni modulo di dominio — questa è la verifica visiva dell'invariante centrale dell'architettura ([02 §3-4](02-moduli-responsabilita-boundaries.md)).

## 4. Matrice Layer → Componenti

| Layer | Componenti (esempi rappresentativi, non esaustivi) |
|---|---|
| **L1 Esperienza** | Composizione delle 62 schermate (UX Bible), gestione navigazione, motore di microinterazioni/animazioni |
| **L2 Applicazione** | Orchestratore Home, Orchestratore Ricerca globale, Orchestratore Revisione settimanale, Instradatore di Cattura |
| **L3 Dominio** | Attività, Finanze, Abitudini, Calendario, Note, Salute, Obiettivi (un'unità per modulo) |
| **L4 Servizi Core** | Bus Eventi, Grafo (GraphLink), Motore di Sincronizzazione, Servizio di Ricerca, Broker di Notifiche, Motore di Insight, Parser di Cattura, Registro Moduli, Motore di Backup/Export, Servizio di Sicurezza |
| **L5 Adattatori di piattaforma** | Persistenza locale, Fornitore Calendario di sistema, Piattaforma Salute di sistema, Enclave di sicurezza/biometria, Trasporto notifiche push, Storage file locale, Adattatore di rete/sync |
| **L6 Confine esterno** | Ricezione/distribuzione blob cifrati e metadati di versione (content-blind) — non decomposto in componenti, per costruzione ([01 §6](01-architettura-generale-e-layer.md)) |

---

*Prossimo: [Report Finale](14-report.md)*
