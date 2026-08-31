# 14 · Report Finale — Data Model Bible

> Consuntivo della creazione della Data Model Bible: file creati, file modificati, incongruenze trovate, decisioni rinviate.

## 1. File creati

Tutti nuovi, in `docs/omnilife/data_model_bible/`:

| File | Contenuto |
|---|---|
| `README.md` | Indice della Data Model Bible |
| `00-modello-dati-comune.md` | Involucro comune, identificatori, ownership, ciclo di vita, versionamento, sync/conflitti concettuali, tagging, ricerca, ordinamenti/filtri, export/import, audit, permessi, 14 invarianti universali |
| `01-entita-sistema.md` | Account, Device, ModuleActivation, Subscription, RecoveryKeyMetadata, Setting |
| `02-entita-cattura-grafo.md` | CaptureInboxItem, GraphLink (formalizzazione del "grafo dati personale") |
| `03-entita-attivita.md` | Task, TaskList, Subtask |
| `04-entita-finanze.md` | Transaction, FinancialAccount, Category, Budget, SavingsGoal |
| `05-entita-abitudini.md` | Habit, HabitExecution |
| `06-entita-calendario.md` | CalendarSource, EventReference (deroga totale), TimeBox |
| `07-entita-note.md` | Note, NoteVersion (deroga di versionamento a snapshot) |
| `08-entita-salute.md` | HealthPlatformReading (deroga totale), ManualHealthMetric |
| `09-entita-obiettivi.md` | Goal (la feature-firma, aggregazione via GraphLink) |
| `10-entita-notifiche-insight-ricerca.md` | NotificationRequest, InsightRuleConfig, InsightFeedback, RecentSearchQuery |
| `11-versionamento-e-sincronizzazione.md` | Calcolo generazione ricorrenze, aderenza abitudini, aggregazione obiettivi, convergenza CRDT concettuale per tipo di campo |
| `12-audit-permessi-vincoli-invarianti.md` | Audit trail consolidato, modello permessi MVP, 7 vincoli cross-entità, 4 invarianti aggiuntivi |
| `13-erm-e-matrici.md` | ERM testuale completo, matrice Entità→Moduli, Entità→Funzioni, Entità→Eventi |
| `14-report.md` | Questo documento |

**Totale**: 16 file, **26 entità concettuali** modellate, **18 invarianti** (INV-01…18), **6 decisioni di modellazione** (MDEC-01…06) tracciate.

## 2. File modificati

**Nessuno.** Non è stata trovata alcuna incongruenza bloccante tra Product Bible, Functional Bible e UX Bible tale da richiedere una correzione. Coerentemente con l'istruzione "aggiorna i documenti esistenti solo se trovi incongruenze" e "non riscrivere/non duplicare", si è scelto di **non toccare** nessuno dei tre documenti sorgente: le ambiguità minori individuate (§3) sono annotate come chiarimenti in questa Bible, non come correzioni a monte.

Sono stati aggiornati solo gli **indici** (come richiesto):

| File | Modifica |
|---|---|
| `docs/omnilife/README.md` | Aggiunta la Data Model Bible alla nota sulla gerarchia dei documenti |
| `README.md` (root repo) | Aggiunto un quinto livello nella descrizione della documentazione OmniLife |

## 3. Incongruenze trovate

Nessuna incongruenza bloccante (contraddizione diretta tra affermazioni delle tre Bible). Due **ambiguità minori**, non contraddittorie, annotate durante la modellazione:

1. **GOAL-002 e i contributi "scadenze/eventi"** — la Functional Bible (modulo Obiettivi) elenca tra i contributi collegabili a un Goal anche "scadenze/eventi", ma non specifica un meccanismo di collegamento dedicato (a differenza di TASK-015, FIN-008, HAB-012, ciascuno con una riga esplicita) né il modulo Calendario dichiara CAL come dipendenza di GOAL-002. **Non è una contraddizione** (la dipendenza dichiarata è genericamente "moduli"), ma una sotto-specificazione. **Risoluzione adottata in questa Bible**: si modella la contribuzione "scadenza/evento" come un GraphLink verso un Task con data di scadenza (già coperto da TASK-015) o verso un TimeBox — nessuna nuova entità introdotta. Si segnala per una futura revisione della Functional Bible (aggiungere una riga esplicita in GOAL-002, non urgente).
2. **TASK-005 "Area → Lista" senza funzioni dedicate alla gestione dell'Area** — la Functional Bible definisce la gerarchia a 2 livelli ma non ha ID di funzione per creare/rinominare/eliminare un'Area indipendentemente dalla Lista. **Non è una contraddizione**: è una scelta implicita di semplicità (P26/P32) coerente con il resto del prodotto. **Risoluzione adottata in questa Bible** (MDEC-04): l'Area è modellata come etichetta stabile portata dalla TaskList, non come entità con ciclo di vita proprio. Si segnala come punto da confermare esplicitamente con il team funzionale se in futuro servisse una gestione autonoma delle Aree (es. rinomina che si propaghi a più liste in un solo gesto).

Nessun'altra incongruenza è stata riscontrata tra le regole di business dei moduli (TASK-R-*, FIN-R-*, HAB-R-*, CAL-R-*, NOTE-R-*, GOAL-R-*, SET-R-*, SYNC-R-*/BKP-R-*/EXP-R-*, NTF-R-*, INS-R-*), i cataloghi di stato, gli eventi dichiarati e i criteri di accettazione: sono risultati coerenti tra loro e con i principi/JTBD/decisioni della Product Bible.

## 4. Decisioni di modellazione introdotte (MDEC) — da NON confondere con nuove funzionalità

Queste sono scelte di **rappresentazione dei dati**, non nuove funzioni: formalizzano concetti già presenti nella Functional Bible senza aggiungere comportamento.

| ID | Decisione | Motivazione |
|---|---|---|
| MDEC-01 | Involucro comune (envelope) esplicito per ogni entità | Rende sistematico ciò che MFC-R-02/05/07/09 già impone |
| MDEC-02 | Un'unica entità **GraphLink** per tutte le relazioni cross-modulo (invece di un tipo per coppia di moduli) | Coerente con "un'anatomia sola" (P33) e "nessuna dipendenza implicita" (C-art. 184); evita 4+ tipi di relazione quasi identici |
| MDEC-03 | Area (Attività) e Categoria (Finanze) riconosciute come lo stesso pattern (gerarchia tipizzata a 2 livelli) ma **non** unificate in un'entità condivisa | Unificarle accoppierebbe due moduli, vietato da C-art. 181 |
| MDEC-04 | Area modellata come etichetta stabile sulla TaskList, non come entità indipendente | Nessuna funzione dedicata nella Functional Bible la richiede oggi (vedi incongruenza minore #2) |
| MDEC-05 | SavingsGoal (Finanze) e Goal (Obiettivi) mantenute come due entità distinte | Già implicito nella Functional Bible (GOAL-002 le tratta come tipi diversi di contributo) |
| MDEC-06 | Le pause di un'Abitudine (HAB-008) modellate come attributo (lista di intervalli), non come entità indipendente | Non hanno bisogno di essere referenziate da altre entità né di un ciclo di vita proprio |

## 5. Decisioni rinviate alle fasi successive

Esplicitamente **fuori perimetro** di questa Bible (concettuale, non tecnica) — da affrontare quando si progetteranno rispettivamente lo schema fisico, le API e l'architettura di sincronizzazione:

1. **Formato esatto degli identificatori** (INV-01/02): quale schema tecnico garantisce unicità globale e ordinabilità temporale — decisione di implementazione.
2. **Tipo esatto di struttura CRDT** per ogni categoria di campo ([11 §6](11-versionamento-e-sincronizzazione.md#6-modello-concettuale-di-convergenza-per-tipo-di-campo-crdt-senza-scegliere-unimplementazione)) — state-based vs operation-based, struttura dati precisa.
3. **Meccanica esatta delle migrazioni di schema** (INV-09: la versione di schema non retrocede mai) — come si esegue in pratica una migrazione multi-dispositivo con versioni dell'app disallineate.
4. **Modello di permessi multi-utente** per gli "spazi condivisi selettivi" (Product Bible, Business Strategy §4.3, fase 4/Anno 4+) — chi vede/modifica cosa quando un'entità è condivisa tra due account. Nessuna entità di questa Bible presuppone tale struttura.
5. **Cadenza di ricalcolo dei contatori derivati** (saldo conto, aderenza abitudine, progresso obiettivo) — on-write vs on-read con cache, puramente implementativo.
6. **Le due ambiguità minori del §3** — se e come formalizzarle con un aggiornamento mirato alla Functional Bible (non necessario per procedere, ma utile per la prossima revisione semestrale).
7. **Scelta del database/motore di persistenza fisico e del linguaggio di query** — esplicitamente escluso dal perimetro di questo task, come da istruzione.

## 6. Coerenza con le Bible esistenti (verifica di chiusura)

- Ogni entità cita almeno una funzione della Functional Bible che la produce (§3 di ogni scheda) — nessuna entità "orfana" senza funzione che la giustifichi (coerente con Feature Philosophy, Product Bible doc 09).
- Ogni deroga dichiarata (Calendario, Salute, Note, Ricerca recente) è la stessa già dichiarata nella Functional Bible — nessuna deroga nuova introdotta da questa Bible.
- La gerarchia documentale resta: Product Constitution → Product Bible → Functional Bible → UX Bible → **Data Model Bible** → documentazione tecnica. Questa Bible non ha introdotto alcun conflitto con i livelli superiori.

---

*Indice: [README](README.md)*
