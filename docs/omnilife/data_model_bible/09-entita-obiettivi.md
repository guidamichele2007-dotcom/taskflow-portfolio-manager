# 09 · Entità Obiettivi (GOAL)

> Eredita il [MDC](00-modello-dati-comune.md). Modulo v1.x — la feature-firma dell'ecosistema (J5, lo score JTBD più alto): la dimostrazione vivente del [GraphLink](02-entita-cattura-grafo.md#dm-link-01--graphlink-collegamento).

## DM-GOAL-01 · Goal (Obiettivo trasversale)

**Descrizione**: l'entità-ombrello che aggrega, tramite GraphLink, contributi di natura diversa (risparmi, task, abitudini, note, scadenze) in un piano unico verso un traguardo concreto (GOAL-001).

| Campo | Tipo concettuale | Obbligatorio | Note |
|---|---|---|---|
| `titolo` | Testo | Sì | — |
| `data_target` | Data | No | GOAL-001 |
| `descrizione` | Testo | No | — |
| `immagine_simbolica` | Enum dal set di sistema | No | — |
| `milestone` | Lista di (nome, raggiunta_il) | No | GOAL-005 |

**Relazioni**: GraphLink multiplo (1:N) verso: **TaskList/Task** (via TASK-015), **SavingsGoal** (via FIN-008/GOAL-002), **Habit** (via HAB-012), **Note** (via NOTE-003), e concettualmente verso scadenze/eventi (tramite un Task con data o un TimeBox — GOAL-002 non introduce un tipo di collegamento distinto per questo caso, vedi nota nel [report](14-report.md)). Un contributo può servire più Goal contemporaneamente (GOAL-R-01).

**Dipendenze**: Grafo (GraphLink), ogni modulo che offre un tipo di contributo.

**Regole**: GOAL-R-01…05 (richiamate per riferimento). Ai fini dati: **il progresso aggregato non è un campo memorizzato**, è sempre calcolato al momento dalla composizione dei GraphLink attivi (vedi calcolo in [11-versionamento-e-sincronizzazione §5](11-versionamento-e-sincronizzazione.md)) — la fascia complessiva è dominata dal fronte peggiore (GOAL-R-03), mai una media che nasconda un fronte in difficoltà (C-art. 6, onestà del quadro).

**Stati**: eredita MDC §6, **più** stati di dominio derivati: senza_contributi (0 GraphLink attivi) · in_stallo (nessun progresso su un fronte da 30 giorni) · data_target_superata (non è una "scadenza": propone rinegoziazione) · completato (stato permanente consultabile, GOAL-007).

**Eventi collegati**: pubblica `goal.progress.changed`, `goal.milestone.reached`, `goal.completed`; sottoscrive `task.item.completed`, `fin.expense/income.created`, `hab.habit.completed`, `note.item.linked`, `core.module.activated/deactivated`.

**Regola sui moduli disattivati**: un contributo il cui modulo è disattivato risulta "in pausa" ed è escluso dal calcolo della fascia — **mai contato come zero** (GOAL-003 scheda estesa, coerente con MFC-R-13).

**Riferimenti Functional Bible**: GOAL-001…008, GOAL-R-01…05, GOAL-AC-01…04.

---

*Nota di modellazione*: questo modulo non introduce un'entità "GoalContribution" separata — il contributo **è** il GraphLink stesso (ruolo `contributo_obiettivo`), coerente con la scelta architetturale MDEC-02 (§[02](02-entita-cattura-grafo.md)) di unificare tutte le relazioni cross-modulo in un solo tipo di entità.

---

*Prossimo: [Entità Notifiche, Insight, Ricerca](10-entita-notifiche-insight-ricerca.md)*
