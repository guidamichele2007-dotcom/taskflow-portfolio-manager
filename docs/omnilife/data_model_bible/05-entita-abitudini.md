# 05 · Entità Abitudini (HAB)

> Eredita il [MDC](00-modello-dati-comune.md). Modulo MVP (D-03, D-06 — costanza resiliente).

## DM-HAB-01 · Habit (Abitudine)

**Descrizione**: un comportamento che l'utente vuole sostenere con costanza (HAB-001/002).

| Campo | Tipo concettuale | Obbligatorio | Note |
|---|---|---|---|
| `nome` | Testo | Sì | — |
| `tipo` | Enum: binaria · quantitativa | Sì | HAB-001/002 |
| `unita_misura` | Testo (solo se quantitativa: L, min, passi…) | Condizionale | HAB-002 |
| `target_giornaliero` | Numero (solo se quantitativa) | Condizionale | — |
| `regola_frequenza` | Struttura: ogni giorno · N×/settimana · giorni specifici · N×/mese | Sì | HAB-003 |
| `promemoria_config` | Struttura (orario, per-giorno) | No | HAB-006 |
| `intervalli_pausa` | Lista di (da, a) | No | HAB-008; **MDEC-06**: modellati come attributo del Habit (non entità indipendente), poiché la pausa non ha ciclo di vita proprio né necessità di essere referenziata da altre entità |
| `confine_giorno` | Ora (default 00:00, fino a 04:00) | No | HAB comportamenti specifici — per i "nottambuli" |
| `collegamento_salute` | Struttura opzionale (tipo dato, policy auto/chiedi) | No | HLTH-003/HAB-010 |

**Relazioni**: strutturale 1:N con **HabitExecution** (proprietaria: eliminare l'abitudine cestina tutto lo storico insieme, HAB comportamenti specifici). GraphLink opzionale verso Goal (HAB-012).

**Dipendenze**: NTF (promemoria), HLTH (auto-completamento), GOAL (collegamento).

**Regole**: HAB-R-01…07 (richiamate per riferimento). Ai fini dati: **nessun campo memorizza una "streak"** — l'aderenza è sempre calcolata (vedi HabitExecution sotto), mai un contatore che possa essere azzerato (HAB-R-01, D-06).

**Stati**: eredita MDC §6, **più** stati di dominio derivati: in_pausa (intervallo attivo in `intervalli_pausa`), in_ripresa (post-salti, puramente linguistico in UX, non un campo persistito).

**Eventi collegati**: pubblica `hab.habit.completed/progressed/skipped(pause)`, `hab.adherence.band.changed`; sottoscrive `hlth.workout.detected`, `hlth.steps.threshold`, `core.day.changed`, `capt.item.captured`.

**Riferimenti Functional Bible**: HAB-001…013, HAB-R-01…07.

---

## DM-HAB-02 · HabitExecution (Esecuzione giornaliera)

**Descrizione**: il record dell'avanzamento di un'abitudine in un singolo giorno — la base da cui si **calcola** (mai si memorizza direttamente) l'aderenza. Un'entità per (abitudine, giorno).

| Campo | Tipo concettuale | Obbligatorio | Note |
|---|---|---|---|
| `data` | Data | Sì | Secondo il `confine_giorno` dell'abitudine |
| `valore` | Booleano (binaria) o Numero (quantitativa, accumulo fino al target) | Sì | HAB-R-03: max 1 esecuzione/giorno per le binarie |
| `fonte` | Enum: manuale · automatica_salute | Sì | HAB-010; idempotenza tra le due fonti nello stesso giorno |
| `nota` | Testo | No | HAB-011 |
| `retroattiva` | Booleano (implicito: `creato_il` posteriore a `data`) | — | Consentito fino a 7 giorni indietro senza conferma extra, oltre con conferma (HAB comportamenti specifici) |

**Relazioni**: strutturale N:1 con Habit (proprietaria e totale).

**Regole**: **la metrica di aderenza è una vista derivata, non un campo**: aderenza = esecuzioni nella finestra mobile di 28 giorni / attese secondo `regola_frequenza`, con i giorni in `intervalli_pausa` esclusi dal denominatore (HAB-005 scheda estesa). Il calcolo esatto e le sue soglie/fasce sono specificati in [11-versionamento-e-sincronizzazione §4](11-versionamento-e-sincronizzazione.md). Un salto singolo non può spostare la fascia derivata di più di un livello (HAB-005); il recupero è sempre più rapido del declino (asimmetria pro-utente deliberata).

**Stati**: non ha stati di ciclo di vita propri oltre a quelli ereditati da MDC §6 (un'esecuzione si può eliminare/annullare — MFC-R-11 — ma non si archivia separatamente dall'abitudine).

**Eventi collegati**: la sua creazione genera `hab.habit.completed` o `hab.habit.progressed`.

**Riferimenti Functional Bible**: HAB-004, HAB-005 (scheda estesa), HAB-R-03, HAB-AC-01/02/05.

---

*Prossimo: [Entità Calendario](06-entita-calendario.md)*
