# 02 · Matrici Epic → Feature → Story → Task

> Eredita [00](00-metodo-tracciabilita-definizioni.md), [01](01-mvp-scope-e-backlog.md). Le Story elencate sono ID già esistenti (Functional Bible) — nessuna ridescrizione, solo organizzazione. Il template Task (T1-T6) è definito una sola volta in [00 §4](00-metodo-tracciabilita-definizioni.md); qui la matrice Story→Task è mostrata **per esteso su EPIC-TASK** (Epic pilota, primo modulo completo, sul percorso critico) e **in forma aggregata** per gli altri Epic — scelta di compattezza dichiarata (la regola di applicazione è generica e identica per ogni Story, ripeterla 158 volte non aggiungerebbe informazione).

## 1. Matrice Epic → Feature (completa)

| Epic | Feature | Story (ID Functional Bible) |
|---|---|---|
| EPIC-00 | *(nessuna Feature intermedia — 5 Story dirette)* | ENG-00-1…5 |
| EPIC-CORE | Home "Oggi" | HOME-001…008 |
| | Onboarding | ONB-001…007 |
| | Galleria Moduli | GAL-001…006 |
| | Revisione Settimanale | REV-001…004 |
| EPIC-CAPT | Cattura Testuale e Parser | CAPT-001,004,005,006,008,010 |
| | Cattura Multicanale | CAPT-002,003,007,009 |
| EPIC-TASK | Creazione e Gestione Base | TASK-001,002,003,006,008,009,011,013 |
| | Organizzazione (Liste/Aree) | TASK-005,018 |
| | Ricorrenze | TASK-004 |
| | Viste e Gestione Massiva | TASK-012,014 |
| | Integrazione Grafo e Calendario | TASK-007,010,015,016,017 |
| EPIC-FIN | Registrazione Transazioni | FIN-001,011,014 |
| | Conti e Trasferimenti | FIN-003,004 |
| | Categorie e Budget | FIN-002,005 |
| | Ricorrenze e Report | FIN-006,007 |
| | Risparmi, Import, Multi-valuta | FIN-008,009,012,013 |
| EPIC-HAB | Abitudini Base | HAB-001,002,003,006 |
| | Costanza Resiliente | HAB-004,005,007,008,013 |
| | Storico e Integrazioni | HAB-009,010,011,012 |
| EPIC-CAL | Lettura Agenda | CAL-001,002,003,007 |
| | Scrittura e Time-boxing | CAL-004,005,006 |
| EPIC-NOTE | Editor e Versioni | NOTE-001,002,006,008,009 |
| | Organizzazione e Grafo | NOTE-003,004,005,007 |
| EPIC-HLTH | Lettura Piattaforma e Auto-completamento | HLTH-001,002,003 |
| | Metriche Manuali | HLTH-004,005 |
| EPIC-GOAL | Ciclo di Vita Obiettivo | GOAL-001,005,006,007,008 |
| | Aggregazione e Grafo | GOAL-002,003,004 |
| EPIC-SRCH | Ricerca Globale | SRCH-001,002,004,005,006 |
| | Filtri Contestuali | SRCH-003 |
| EPIC-NTF | Broker e Budget | NTF-001,002,003,004 |
| | Azionabilità e Governo | NTF-005,006,007,008 |
| EPIC-WID | Widget Base | WID-001,002,003,005 |
| | Widget Estesi | WID-004,006,007 |
| EPIC-SYNC | Sincronizzazione | SYNC-001,002,003 |
| | Backup e Ripristino | BKP-001,002,003,004 |
| | Export e Cancellazione | EXP-001,002,003 |
| EPIC-SET | Account e Sicurezza | SEC-001,002,003, PROF-001 |
| | Impostazioni e Abbonamento | SET-001,002,003,004 |
| EPIC-INS | Motore e Insight Contestuali | INS-001,003 |
| | Digest e Controllo Utente | INS-002,004,005 |

## 2. Matrice Feature → Story: già espressa in §1 (colonna 3) — ogni cella "Story" della tabella sopra è l'elenco puntuale.

## 3. Matrice Story → Task — dettaglio completo per EPIC-TASK (Epic pilota)

| Story | T1 Dominio | T2 Applicativa | T3 Eventi | T4 UI | T5 Design System | T6 Test |
|---|---|---|---|---|---|---|
| TASK-001 | DM-TASK-01 | TASK-R-01 | `task.item.created` | FLOW-TASK-01 | CMP-RIGA-ENTITA, CMP-SHEET | TASK-AC-* pertinenti, MFC-AC-01/08 |
| TASK-002 | DM-TASK-01 campo data | — | — | CMP-SHEET (campo data) | CMP-SHEET | — |
| TASK-003 | DM-TASK-01 promemoria_config | — | richiede NTF-001 (EPIC-NTF) | — | CMP-TOGGLE | NTF-AC-02 |
| TASK-004 | DM-TASK-01 regola_ricorrenza | Calcolo occorrenze ([Data Model Bible §11 §3](../data_model_bible/11-versionamento-e-sincronizzazione.md)) | `task.item.rescheduled` | FLOW-TASK-01 | CMP-SHEET | TASK-AC-01/04 |
| TASK-005 | DM-TASK-02 | TASK-005 | — | IA-032/033 | CMP-CARD | — |
| TASK-006 | DM-TASK-01 priorita | — | — | CMP-SHEET | CMP-CHIP | — |
| TASK-007 | DM-TASK-03 | TASK-AC-03 | — | IA-035 | CMP-COMPLETION | TASK-AC-03 |
| TASK-008 | — | Idempotenza per occorrenza | `task.item.completed` | FLOW-TASK-01 | CMP-COMPLETION | TASK-AC-01/05, MFC-AC-04 |
| TASK-009 | — | TASK-009 | `task.item.rescheduled` | FLOW-TASK-01 passo 5 | CMP-CHIP | — |
| TASK-010 | — | Suggerimento (propone, mai impone) | sottoscrive `cal.day.load.changed` | — | CMP-BANNER | — |
| TASK-011 | GraphLink verso Note | — | — | IA-074 | — | — |
| TASK-012 | — | TASK-R-02 ordinamento | — | IA-030/031/032 | CMP-SEGMENT | — |
| TASK-013 | — | ordine_manuale | — | UX Bible §8 drag | — | — |
| TASK-014 | — | Raggruppamento "in sospeso" | `task.overdue.count.changed` | IA-034 | CMP-BANNER | TASK-AC-02 |
| TASK-015 | GraphLink verso Goal | GOAL-R-01 | — | IA-092 (da EPIC-GOAL) | — | GOAL-AC-01 |
| TASK-016 | DM-CAL-03 (Epic Calendario) | — | `cal.timebox.created` | IA-036 | — | CAL-AC-02 |
| TASK-017 | Copia senza cronologia | — | — | — | — | — |
| TASK-018 | DM-TASK-02 archiviazione | — | — | — | — | — |

## 4. Matrice Story → Task — forma aggregata per gli altri 14 Epic

*(La regola è identica a §3: ogni Story applica il sottoinsieme pertinente di T1-T6, con le eccezioni dichiarate in [00 §4](00-metodo-tracciabilita-definizioni.md) per Calendario/Salute/servizi senza UI propria. Qui: conteggio delle Story per Epic che coinvolgono ciascun Task, utile per la pianificazione di capacità.)*

| Epic | Story con T1 (Dominio) | Story con T2 (Applicativa) | Story con T3 (Eventi) | Story con T4 (UI) | Story con T5 (Design System) | Story con T6 (Test) |
|---|---|---|---|---|---|---|
| EPIC-CORE | 3 (ModuleActivation) | 25 | 12 | 25 | 25 | 25 |
| EPIC-CAPT | 1 (CaptureInboxItem) | 10 | 4 | 6 | 6 | 10 |
| EPIC-FIN | 5 | 14 | 6 | 12 | 12 | 14 |
| EPIC-HAB | 2 | 13 | 5 | 11 | 11 | 13 |
| EPIC-CAL | 2 (in deroga, §00 §4) | 3 | 3 | 7 | 5 | 4 |
| EPIC-NOTE | 2 | 9 | 3 | 9 | 8 | 9 |
| EPIC-HLTH | 1 (deroga, §00 §4) | 3 | 3 | 5 | 4 | 4 |
| EPIC-GOAL | 1 | 8 | 5 | 7 | 6 | 8 |
| EPIC-SRCH | 0 (indice derivato) | 6 | 1 | 6 | 5 | 6 |
| EPIC-NTF | 1 | 8 | 3 | 3 | 4 | 8 |
| EPIC-WID | 0 (proiezione di L1) | 7 | 0 | 7 | 6 | 7 |
| EPIC-SYNC | 0 (opera su entità generiche) | 10 | 0 | 8 | 6 | 10 |
| EPIC-SET | 6 (entità di sistema) | 8 | 0 | 8 | 6 | 8 |
| EPIC-INS | 0 (config, non entità utente) | 5 | 2 | 3 | 3 | 5 |

**Nota di lettura**: "0" non significa assenza di lavoro — significa che quel Task-tipo non si applica per la natura dell'Epic (es. Ricerca non ha T1 perché il suo indice è una proiezione derivata, [Data Model Bible §11](../data_model_bible/00-modello-dati-comune.md)), coerente con le eccezioni dichiarate in [00 §4](00-metodo-tracciabilita-definizioni.md).

---

*Prossimo: [Dipendenze, Grafo, Percorso Critico](03-dipendenze-grafo-percorso-critico.md)*
