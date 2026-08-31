# 04 · Entità Finanze (FIN)

> Eredita il [MDC](00-modello-dati-comune.md). Modulo MVP (D-03), a più alta willingness-to-pay.

## DM-FIN-01 · Transaction (Transazione)

**Descrizione**: una spesa, un'entrata o un trasferimento (FIN-001/004/011). Un unico tipo di entità copre tutti e tre i casi (evita di duplicare il concetto — C-art. 61), distinti dal campo `tipo`.

| Campo | Tipo concettuale | Obbligatorio | Note |
|---|---|---|---|
| `importo` | Numero decimale (sempre positivo) | **Sì (unico obbligatorio)** | Il segno è implicito nel `tipo`, mai un numero negativo (scheda estesa FIN-001) |
| `valuta` | Enum ISO 4217 | Sì (default: valuta del conto) | FIN-009 |
| `tipo` | Enum: spesa · entrata · trasferimento | Sì | FIN-004: il trasferimento non conta come spesa/entrata nei report (FIN-R-01) |
| `conto_id` | Riferimento a FinancialAccount | Sì | — |
| `conto_destinazione_id` | Riferimento a FinancialAccount | Condizionale | Solo se `tipo = trasferimento` |
| `categoria_id` | Riferimento a Category | No (default: proposta dal parser o "Senza categoria") | FIN-002 |
| `divisioni` | Lista di (categoria_id, importo_parziale) | No | FIN-011: divisione multi-categoria, alternativa a `categoria_id` singola |
| `data` | Data (+ ora di creazione per l'ordinamento, FIN-R-04) | Sì (default: oggi) | — |
| `nota` | Testo | No | — |
| `immagine_scontrino` | Riferimento a file locale cifrato | No | FIN-013, mai OCR cloud |
| `ricorrenza_id` | Riferimento a RecurrenceRule (concettuale, §MDC) | No | FIN-006 |

**Relazioni**: strutturale N:1 con FinancialAccount (obbligatoria); N:1 opzionale con Category; GraphLink opzionale verso Goal (tramite SavingsGoal, non direttamente — vedi DM-FIN-05).

**Dipendenze**: CAPT (creazione), NTF (ricorrenze), SRCH (filtri FIN-010).

**Regole**: FIN-R-01…08 (richiamate per riferimento). Ai fini dati: il saldo di un conto è **derivato** dalla somma delle transazioni, mai un campo scrivibile direttamente — una "correzione saldo" crea una transazione di rettifica esplicita (FIN-R-02, audit trail). Le transazioni non si bloccano mai in modifica (FIN-R-03): non esiste uno stato "chiuso" per una transazione.

**Stati**: eredita MDC §6. Nessuno stato di dominio aggiuntivo (a differenza di Task/Habit, la transazione non ha uno "stato di aderenza" — è un fatto contabile).

**Eventi collegati**: pubblica `fin.expense.created/updated/deleted`, `fin.income.created…`, `fin.recurrence.executed`; sottoscrive `capt.item.captured`, `core.day.changed`.

**Riferimenti Functional Bible**: FIN-001, 004, 006, 009, 010, 011, 012, 013, 014, FIN-R-01…08.

---

## DM-FIN-02 · FinancialAccount (Conto)

**Descrizione**: un contenitore di denaro (contanti, carta, conto bancario) — FIN-003. Da non confondere con `DM-SYS-01 Account` (l'account utente): nome distinto deliberatamente nella documentazione per evitare ambiguità terminologica (annotato come chiarimento nel [report](14-report.md)).

| Campo | Tipo concettuale | Obbligatorio |
|---|---|---|
| `nome` | Testo | Sì |
| `tipo` | Enum: contanti · carta · conto bancario · altro | Sì |
| `valuta` | Enum ISO 4217 | Sì |
| `e_predefinito` | Booleano | Sì (uno solo per account) |
| `saldo` | **Derivato** (somma delle transazioni, mai scritto direttamente) | — | FIN-R-02 |

**Relazioni**: strutturale 1:N con Transaction (proprietaria: l'eliminazione richiede decisione — sposta le transazioni su altro conto o cestina insieme, FIN comportamenti specifici).

**Regole**: l'eliminazione di un conto non elimina mai silenziosamente le transazioni (MFC-R-12 applicato).

**Stati**: attivo · archiviato · cestinato (con o senza le transazioni, secondo la scelta fatta all'eliminazione).

**Riferimenti Functional Bible**: FIN-003, FIN-004.

---

## DM-FIN-03 · Category (Categoria)

**Descrizione**: la classificazione delle transazioni, gerarchia a 2 livelli (FIN-002).

| Campo | Tipo concettuale | Obbligatorio |
|---|---|---|
| `nome` | Testo | Sì |
| `categoria_padre_id` | Riferimento a Category (stesso tipo, 1 livello di nidificazione) | No |
| `icona` / `colore` | Enum dal set di sistema | Sì (default assegnato) |
| `e_predefinita` | Booleano | Sì |

**Relazioni**: strutturale 1:N con Transaction (non proprietaria in senso stretto: l'eliminazione di una categoria non elimina le transazioni, le riassegna a "Senza categoria" — FIN comportamenti specifici).

**Regole**: l'unione di due categorie riassegna le transazioni in blocco con undo (FIN comportamenti specifici); "Senza categoria" è un valore implicito, non una Category cancellabile.

**Riferimenti Functional Bible**: FIN-002.

---

## DM-FIN-04 · Budget

**Descrizione**: una soglia mensile per categoria (o globale) — FIN-005.

| Campo | Tipo concettuale | Obbligatorio |
|---|---|---|
| `categoria_id` | Riferimento a Category | No (assente = budget globale) |
| `soglia_importo` | Numero decimale | Sì |
| `periodo` | Implicito: mese di calendario del fuso utente | — | Scheda estesa FIN-005 |
| `stato_visivo` | **Derivato**: ok · attenzione (≥80%) · superato | — | Mai un quarto livello (P48) |

**Relazioni**: strutturale N:1 con Category (opzionale).

**Regole**: la soglia di attenzione notifica al massimo 1 volta per soglia per mese (FIN-005 scheda estesa, C-art. 59); il budget creato a metà mese può essere pro-rata o intero, scelta dichiarata (casi limite FIN-005).

**Riferimenti Functional Bible**: FIN-005, FIN-AC-02.

---

## DM-FIN-05 · SavingsGoal (Obiettivo di risparmio)

**Descrizione**: FIN-008 — distinto dall'entità `Goal` del modulo Obiettivi (DM-GOAL-01): questo è l'obiettivo *finanziario* specifico (importo target + data), che può poi diventare un "fronte" di un Goal trasversale tramite GraphLink. **MDEC-05**: la distinzione è nella Functional Bible stessa (GOAL-002 elenca "obiettivi di risparmio (FIN-008)" come uno dei tipi di contributo collegabili a un Goal) — qui solo formalizzata come due entità separate.

| Campo | Tipo concettuale | Obbligatorio |
|---|---|---|
| `nome` | Testo | Sì |
| `importo_target` | Numero decimale | Sì |
| `data_target` | Data | No |
| `conto_collegato_id` | Riferimento a FinancialAccount | No |
| `regola_accantonamento` | Struttura opzionale (manuale · automatica proposta) | No |

**Relazioni**: GraphLink opzionale verso Goal (ruolo `contributo_obiettivo`); non strutturale con Transaction (il progresso è calcolato dagli accantonamenti, non da una relazione di proprietà rigida).

**Regole**: la proiezione ("di questo passo: marzo") dichiara sempre l'ipotesi di calcolo (GOAL-R-04 applicato anche qui, C-art. 145).

**Riferimenti Functional Bible**: FIN-008, GOAL-002/004.

---

*Prossimo: [Entità Abitudini](05-entita-abitudini.md)*
