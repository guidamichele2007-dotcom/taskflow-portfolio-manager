# 03 · Entità Attività (TASK)

> Eredita il [MDC](00-modello-dati-comune.md). Modulo MVP (D-03).

## DM-TASK-01 · Task

**Descrizione**: un impegno volitivo (una cosa da fare). L'unica entità con campo obbligatorio ridotto al minimo assoluto (TASK-R-01).

| Campo | Tipo concettuale | Obbligatorio | Note |
|---|---|---|---|
| `titolo` | Testo | **Sì (unico obbligatorio)** | TASK-R-01, MFC-R-03 |
| `data_scadenza` | Data (ora opzionale) | No | TASK-002; senza ora niente promemoria |
| `priorita` | Enum: alta · media · nessuna | No (default nessuna) | TASK-006; solo 3 livelli (P28) |
| `regola_ricorrenza` | Struttura (tipo, intervallo, eccezioni) | No | TASK-004, vedi [11-versionamento §3](11-versionamento-e-sincronizzazione.md) per la generazione delle occorrenze |
| `lista_id` | Riferimento a TaskList | No (default: lista "Attività") | TASK-005 |
| `nota_descrittiva` | Testo | No | TASK-011 |
| `completato` / `completato_il` | Booleano / Timestamp | Sì (default falso) | TASK-008 |
| `ordine_manuale` | Intero | No | TASK-013; prevale sempre (INV-10) |
| `promemoria_config` | Struttura (orario/anticipo) | No | TASK-003 |

**Relazioni**:
- Strutturale N:1 con **TaskList** (`lista_id`).
- Strutturale 1:N con **Subtask** (i figli seguono il padre nel ciclo di vita).
- GraphLink opzionale verso **Goal** (TASK-015, ruolo `contributo_obiettivo`).
- GraphLink opzionale verso **Note** (TASK-011/NOTE-003).
- Relazione non strutturale (di sola visualizzazione, non proprietà) con **TimeBox** del modulo Calendario (TASK-016): il time-box referenzia il task, non viceversa — eliminare il time-box non tocca il task (CAL-R-02).

**Dipendenze**: CAPT (creazione), NTF (promemoria), CAL in lettura (TASK-010 posticipa intelligente), GOAL (collegamento).

**Regole di business** (già stabilite in Functional Bible, richiamate per riferimento): TASK-R-01…08. In sintesi qui solo ciò che riguarda i dati: il completamento di un'occorrenza ricorrente non retro-modifica le occorrenze passate (TASK-R-08); l'eliminazione di una ricorrenza chiede esplicitamente "solo questa / tutte le future" (scheda estesa TASK-004); i task senza data non hanno scadenza né generano solleciti (TASK-R-03).

**Stati**: eredita il ciclo di vita MDC §6 (attivo/archiviato¹/cestinato/eliminato) **più** stati di dominio derivati (non persistiti come enum separato, calcolati): scaduto (data_scadenza < oggi e non completato), in_sospeso (raggruppamento UX di "scaduto"), ricorrenza_in_pausa (TASK sospensione dichiarata). ¹Nota: i Task non si archiviano singolarmente (solo le TaskList si archiviano intere, TASK-018) — per un Task, l'unico percorso oltre "attivo" è completato o cestinato.

**Eventi collegati**: pubblica `task.item.created/completed/uncompleted/rescheduled/deleted`, `task.overdue.count.changed`; sottoscrive `capt.item.captured`, `cal.day.load.changed`, `core.day.changed`.

**Riferimenti Functional Bible**: TASK-001…018, TASK-R-01…08, MFC-R-02/05/07/09-13.

---

## DM-TASK-02 · TaskList (Lista)

**Descrizione**: il contenitore di raggruppamento a 2 livelli (Area → Lista, TASK-005, P32). **MDEC-04**: modelliamo "Area" come un'etichetta stabile (non una gerarchia di entità indipendente con proprio ciclo di vita) portata dalla Lista, poiché la Functional Bible non definisce funzioni dedicate alla gestione autonoma delle Aree (nessun `AREA-00x`) — vedi nota nel [report finale](14-report.md) come decisione rinviata.

| Campo | Tipo concettuale | Obbligatorio | Note |
|---|---|---|---|
| `nome` | Testo | Sì | — |
| `area` | Testo (etichetta stabile, non entità indipendente — MDEC-04) | No | Raggruppamento di 2° livello |
| `e_predefinita` | Booleano | Sì | La lista "Attività" iniziale non è eliminabile, solo rinominabile |
| `ordine_manuale` | Intero | No | — |

**Relazioni**: strutturale 1:N con Task (proprietaria: l'eliminazione di una lista impone una decisione sui suoi task, mai un'eliminazione silenziosa — coerente con MFC-R-12 applicato all'interno del modulo).

**Regole**: solo le liste (non i singoli task) si archiviano per intero (TASK-018); l'archiviazione di una lista non elimina i suoi task, li nasconde secondo lo stato "archiviata" ereditato dal MDC.

**Stati**: attiva · archiviata · cestinata (con i suoi task, se la scelta alla cancellazione lo prevede).

**Riferimenti Functional Bible**: TASK-005, TASK-018.

---

## DM-TASK-03 · Subtask

**Descrizione**: un elemento di checklist interno a un Task, a un solo livello di nidificazione (TASK-007) — non un Task ricorsivo: non ha data propria, priorità propria né promemoria propria.

| Campo | Tipo concettuale | Obbligatorio |
|---|---|---|
| `titolo` | Testo | Sì |
| `completato` | Booleano | Sì |
| `ordine` | Intero | Sì |

**Relazioni**: strutturale N:1 con Task (proprietaria e totale: un Subtask non esiste senza il suo Task; segue il padre in ogni transizione di stato — cestino, ripristino — TASK comportamenti specifici).

**Regole**: il completamento del Task padre con sottotask aperti richiede una scelta esplicita (completa tutti / mantieni aperti) — mai una modifica silenziosa dei figli (TASK-AC-03).

**Stati**: eredita lo stato del Task padre (nessun ciclo di vita indipendente).

**Riferimenti Functional Bible**: TASK-007, TASK-AC-03.

---

*Prossimo: [Entità Finanze](04-entita-finanze.md)*
