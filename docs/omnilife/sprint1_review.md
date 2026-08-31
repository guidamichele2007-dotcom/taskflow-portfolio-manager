# Sprint 1 — Code Review Tecnica

**Perimetro**: revisione tecnica del codice consegnato nello Sprint 1 (Core Engine + Modulo Attività — `core-common`, `core-eventbus`, `domain-task`, `feature-task`, con relativi test). Nessuna nuova funzionalità è stata introdotta. Le correzioni applicate sono limitate a problemi oggettivi (bug, violazioni meccaniche di lint/detekt, dipendenze morte, duplicazione di logica). I problemi progettuali sono documentati come proposta, non implementati.

Riferimento al deliverable originale: `docs/omnilife/sprint1_report.md`.

---

## 1. Architettura

**Conformità al modello a 6 layer (Technical Architecture Bible)**: rispettata. La direzione delle dipendenze è corretta e verificata leggendo i `build.gradle.kts`:

- `domain-task` dipende solo da `core-common` (Envelope, OmniResult, DomainError) e `core-eventbus` (EventBus) — nessuna dipendenza verso `feature-*` o `platform-*`.
- `feature-task` dipende da `domain-task` e `core-designtokens` — mai il contrario.
- La Repository Port (`TaskRepository`, interfaccia a 15 metodi) vive nello stesso modulo `domain-task` dell'entità che possiede (Dependency Inversion rispettata: l'interfaccia è dichiarata nel layer interno, l'implementazione SQLDelight nello stesso modulo per semplicità — decisione esplicita, README-BUILD.md §11).
- Event Bus (`core-eventbus`) è consumato da `domain-task` solo in scrittura (`publish`) in questo sprint — vedi §5 "Rischio: Event Bus write-only" più sotto.
- MVI (`TaskListViewModel`, `TaskDetailViewModel`) vive in `feature-task`, in Kotlin puro, senza dipendenza da Compose/SwiftUI — coerente con la decisione documentata di rinviare le schermate renderizzate.

Nessuna violazione della Dependency Rule trovata. Nessun ciclo tra moduli.

**Finding oggettivo corretto**: `domain/domain-task/build.gradle.kts` dichiarava `implementation(project(":core:core-graph"))` senza che nessun file sorgente di `domain-task` referenziasse `com.omnilife.core.graph.*` (verificato via grep, zero occorrenze). Dipendenza residua di scaffolding, non giustificata da alcun uso — rimossa. Il modulo continua a buildare e passare tutti i test dopo la rimozione (verificato con build dedicata).

---

## 2. Modularità

I confini di modulo rispettano l'Engineering Plan (un modulo `domain-*` per Epic, un modulo `feature-*` per superficie UI). Il modulo Attività non "trapela" tipi verso altri domini: `Task`, `Subtask`, `TaskList`, `TaskEvent` sono usati solo all'interno di `domain-task`/`feature-task`.

**Problema progettuale (proposto, non implementato)**: `FakeTaskRepository` esiste in due copie quasi identiche — `domain/domain-task/src/commonTest/.../usecase/FakeTaskRepository.kt` e `feature/feature-task/src/commonTest/.../FakeTaskRepository.kt` — che implementano lo stesso contratto `TaskRepository` con la stessa logica di filtro/ordinamento in-memory. Sono `internal`, quindi non condivisibili tra moduli senza un nuovo modulo. **Proposta**: introdurre un modulo `core-testing` (già presente come placeholder in `settings.gradle.kts`) che esponga fixture di test condivise (`FakeTaskRepository`, builder di `Task`/`Envelope`) come dipendenza `testImplementation` per entrambi i moduli. Non implementato in questo sprint perché richiederebbe promuovere le classi da `internal` a `public` e introdurre un nuovo confine di modulo — una decisione architetturale, non un fix meccanico.

---

## 3. Duplicazioni

**Trovate e corrette (oggettive)**:

1. **Logica di ordinamento (`TaskSort`) duplicata tre volte** — in `SqlDelightTaskRepository.findTasks` (via una funzione privata `applySort`), nel `FakeTaskRepository` di `domain-task` (che *ignorava* del tutto il parametro `sort`) e nel `FakeTaskRepository` di `feature-task` (stesso problema). Le tre implementazioni erano già divergenti: la versione reale applicava l'ordinamento, le due fake no — un test scritto contro una fake sarebbe passato anche con un bug di ordinamento nella query reale, e viceversa (violazione di Liskov Substitution per i test double). **Fix**: estratta un'unica funzione pura `List<Task>.sortedByTaskSort(sort: TaskSort)` in `domain/domain-task/src/commonMain/.../TaskSorting.kt`, richiamata identicamente dalle tre implementazioni.

2. **Predicato di filtro `matchesFilter`** in `SqlDelightTaskRepository.findTasks`, estratto come funzione privata di file (non di classe, per non far scattare `TooManyFunctions` — vedi §12) per separare la selezione SQL (`selectRows`) dal filtro applicato in memoria, altrimenti impossibile da leggere in un'unica funzione con la nuova condizione aggiunta nel fix del bug SQL (§4).

**Non corrette (proposta)**: la duplicazione delle due `FakeTaskRepository` (vedi §2) resta — è una duplicazione di modulo, non di funzione, e richiede la decisione architetturale già descritta.

---

## 4. Complessità

Nessuna funzione supera le soglie configurate in `config/detekt/detekt.yml` dopo le correzioni. In particolare:

- `SqlDelightTaskRepository.findTasks` era salita a complessità ciclomatica 16 (soglia 15) dopo l'aggiunta della condizione `priority` mancante nel filtro in-memory (§9 — fix del bug). **Fix**: la funzione è stata scomposta in `findTasks` (orchestrazione, 3 chiamate in pipeline) + `selectRows` (il `when` di selezione SQL, membro privato) + `matchesFilter` (il predicato di filtro, funzione di estensione privata a livello di file). Nessuna soglia violata; nessuna funzione supera i 20 statement.
- `TaskListViewModel.dispatch` e `TaskDetailViewModel.dispatch` restano `when` lunghi (un branch per intent, in linea con MVI/TDR-02) — già coperti da `LongMethod.threshold = 70` con giustificazione esplicita in `detekt.yml`.

Nessun'altra funzione richiede refactoring per complessità.

---

## 5. Performance

`SqlDelightTaskRepository.findTasks` applica un solo predicato come indice SQL (list o priority, mai entrambi) e il resto del filtro (incluso l'altro predicato, `includeCompleted`, `dueBefore`/`dueAfter`) e l'intero ordinamento in memoria dopo il fetch. Per i volumi di uno Sprint 1 (uso singolo utente, poche centinaia di task) questo è accettabile e già dichiarato esplicitamente non conforme al budget MFC-AC-07 (50.000 entità / 100ms) nel commento del file — non è un problema nuovo, è una limitazione nota e documentata. Nessun altro hot path individuato: `searchTasks` usa una query SQL dedicata (`LIKE`), non un fetch-and-filter.

Nessuna azione richiesta in questo sprint (limite già dichiarato, in scope per un'ottimizzazione futura con indici compositi o query dinamica).

---

## 6. Memoria / Concorrenza

**Bug oggettivo corretto — race condition "ultima richiesta vince" in `TaskListViewModel`**: `refresh()` e la ricerca (`runSearch`) lanciavano ciascuna una nuova coroutine su `scope.launch` senza tracciare quella precedente. Cambiare rapidamente filtro/modalità/query (es. utente digita nella ricerca mentre un `refresh()` precedente non è ancora tornato) poteva far sì che una `refresh()` più lenta sovrascrivesse lo stato con dati superati dopo che una ricerca più recente aveva già aggiornato la UI. **Fix**: aggiunto un campo `loadJob: Job?` che traccia la coroutine di caricamento corrente; sia `refresh()` sia `runSearch()` cancellano il job precedente prima di lanciarne uno nuovo.

Nessun'altra condizione di gara individuata: `EventBus.publish` è sincrono per design (documentato, non un bug) e ogni `ViewModel` opera su un proprio `CoroutineScope` con `SupervisorJob`, senza stato condiviso mutabile tra istanze.

`InMemoryEventBus` non è thread-safe (dichiarato esplicitamente nel proprio KDoc) — accettabile perché l'intero Sprint 1 gira su un singolo thread applicativo (`Dispatchers.Default` per il lavoro, ma senza publisher concorrenti); da rivalutare se un futuro modulo pubblicasse eventi da più thread contemporaneamente.

---

## 7. Sicurezza

Nessuna vulnerabilità OWASP-class individuata nel codice applicativo:

- Tutte le query SQLDelight (`Task.sq`) sono parametrizzate — nessuna concatenazione di stringhe verso SQL, nessun rischio di SQL injection anche per `searchTasksByLifecycle` (query `LIKE` con parametro bind, non stringa costruita a mano).
- Nessun dato sensibile loggato (nessun logging esiste ancora — vedi §11 "Rischio: assenza di logging").
- Nessuna dipendenza di rete introdotta in questo sprint (nessuna sincronizzazione cloud, come da vincolo esplicito del task).

**Rischio noto, non nuovo, riconfermato**: il database SQLite locale (`DatabaseDriverFactory`) non è cifrato — qualunque titolo/nota di task è leggibile in chiaro sul filesystem del dispositivo. Già segnalato come gap nel report Sprint 1 e nella Security & Privacy Bible come requisito futuro (SQLCipher o equivalente); non risolvibile con un fix meccanico, richiede una libreria aggiuntiva e una migrazione dello schema — proposta per Sprint 2, non implementata qui.

---

## 8. Test

Suite attuale (dopo le aggiunte di questa review):

| Modulo | Test | Note |
|---|---|---|
| `core-common` (`commonTest`) | 7 | `OmniResult`, `Envelope` |
| `core-eventbus` (`commonTest`) | 7 | `InMemoryEventBus` |
| `domain-task` (`commonTest`) | 62 | entità, regole, casi d'uso via `FakeTaskRepository` |
| `domain-task` (`jvmTest`) | 13 | integrazione contro SQLite reale (era 9 — vedi sotto) |
| `feature-task` (`commonTest`) | 14 | `TaskListViewModel`, `TaskDetailViewModel` |
| **Totale** | **103** | (era 99 nel report Sprint 1) |

**Test aggiunti in questa review (regressioni per i bug oggettivi corretti)**:
- `findTasks combines list id and priority instead of dropping one` — riproduce il bug §9.
- `TaskSort DUE_DATE orders by due date, undated tasks last`
- `TaskSort PRIORITY orders highest priority first`
- `TaskSort DEFAULT lets manual order win over due date and priority (INV-10)`

Queste tre ultime chiudono un buco di copertura reale: prima di questa review, `TaskSort` non era testato affatto contro l'implementazione SQLDelight (solo l'ordinamento di default era esercitato indirettamente da altri test), e la fake lo ignorava silenziosamente — nessun test avrebbe mai potuto accorgersi di una regressione nell'ordinamento.

Nessun test aggiunto per la race condition di `TaskListViewModel` (§6) o per il fix di error-surfacing dei ViewModel: entrambi i fix toccano un `internal`/comportamento di gestione degli errori che richiederebbe un `TestDispatcher` con controllo esplicito dell'ordine di esecuzione per essere verificato in modo deterministico (già usato altrove nel modulo, `UnconfinedTestDispatcher`) — non aggiunto qui per restare nel perimetro "correggi solo problemi oggettivi", ma segnalato come lavoro di copertura mancante per Sprint 2.

---

## 9. Naming

Convenzioni rispettate in modo consistente: `VerboEntità` per i casi d'uso (`CreateTask`, `CompleteTask`, non `TaskUseCase`), `XxxUiState`/`XxxIntent`/`XxxViewModel` per MVI, `Sql*Repository` per le implementazioni concrete.

**Problema progettuale (proposto, non implementato)**: `SqlDelightTaskRepository.updateTask`, `updateSubtask` e `updateList` sono implementati come `insertOrReplaceX(...)` (upsert), non come un vero `UPDATE`. Il nome del metodo pubblico (`updateTask`) promette una semantica di aggiornamento (fallisce se la riga non esiste), ma il comportamento reale è upsert (crea se assente). Nessun test attuale dipende dalla differenza, quindi non è un bug oggi, ma è un contratto di interfaccia (`TaskRepository.updateTask`) che mente sul proprio comportamento a un futuro chiamante. **Proposta**: o rinominare l'operazione a livello di interfaccia (`upsertTask`) se l'upsert è il comportamento voluto, oppure implementare un vero `UPDATE ... WHERE id = ?` che fallisca esplicitamente (`OmniResult.Failure`) se la riga non esiste. Decisione di semantica del dominio, non un refactoring meccanico — non implementata qui.

---

## 10. API interne

`TaskRepository` (15 metodi) è un port ragionevole per l'ampiezza dell'aggregato Task/Subtask/TaskList in questo sprint; la soglia `TooManyFunctions.thresholdInInterfaces = 17` in `detekt.yml` è stata dimensionata esplicitamente per questo (con motivazione in commento).

`OmniResult`/`onSuccess`/`onFailure`/`map`/`getOrNull` sono funzioni di estensione di `core-common` che richiedono import espliciti nei moduli consumer (non risolte automaticamente per file nello stesso package del chiamante, se il chiamante è in un package diverso) — già causa di un bug reale nello Sprint 1 (fixato allora). Nessuna nuova occorrenza trovata in questa review.

**Finding oggettivo corretto**: `TaskListViewModel.reportError` usava il tipo `com.omnilife.core.common.DomainError` per nome completamente qualificato invece di un import, mentre il resto del file usa import standard — incoerenza di stile, non un errore, corretta aggiungendo l'import mancante.

---

## 11. Qualità del codice / Debito tecnico

**Bug oggettivo corretto — errori di dominio silenziosamente scartati nei ViewModel**: sia `TaskListViewModel` sia `TaskDetailViewModel` chiamavano i casi d'uso che restituiscono `OmniResult<T>` (es. `uncompleteTask`, `deleteTask`, `postponeTask`, `reorderTasks` in `TaskListViewModel`; `updateTaskFields`, `addSubtask`, `toggleSubtask`, `deleteSubtask`, `reorderSubtasks` in `TaskDetailViewModel`) senza mai ispezionare l'esito. `TaskListUiState.errorMessage` e `TaskDetailUiState.errorMessage` esistono entrambi nello stato ma restavano permanentemente `null` per qualunque fallimento diverso da `OpenSubtasksRequireChoice` (che era l'unico caso già gestito, in `TaskListViewModel.handleComplete`). Concretamente: un `updateTaskFields` con titolo vuoto restituiva `OmniResult.Failure(TaskError.MissingTitle)` che spariva silenziosamente — la UI non avrebbe mai potuto mostrare il messaggio d'errore, nonostante lo stato fosse già stato disegnato per contenerlo. **Fix**: aggiunto un helper privato `reportError(error: DomainError)` in entrambi i ViewModel, cablato con `.onFailure(::reportError)` su ogni intent che invoca un caso d'uso capace di fallire.

**Problemi progettuali (proposti, non implementati)**:

- **`CompleteTask` viola SRP**: un'unica classe gestisce il completamento del task, l'eventuale completamento a cascata dei sottotask, la pubblicazione dell'evento di dominio *e* la generazione lazy della prossima occorrenza ricorrente (TASK-004/INV-15). Sono quattro responsabilità distinte cucite in un solo caso d'uso da ~50 righe. Non è un bug (i test coprono il comportamento combinato correttamente), ma un futuro cambiamento a una sola di queste responsabilità (es. logica di ricorrenza più elaborata) rischia di richiedere di toccare l'intera classe. **Proposta**: estrarre la generazione dell'occorrenza successiva in un caso d'uso/collaboratore dedicato (`GenerateNextOccurrence`) invocato da `CompleteTask`, seguendo lo stesso pattern "un caso d'uso, una responsabilità" già usato altrove.

- **`ReorderTasks` non atomico**: itera `orderedTaskIds` e chiama `repository.updateTask` una riga alla volta; se un ID a metà lista non esiste, la funzione ritorna `OmniResult.Failure` ma le righe già aggiornate prima di quel punto restano modificate — uno stato parzialmente applicato, senza rollback. Con lo storage locale attuale (SQLDelight su SQLite) è risolvibile con una singola transazione (`database.transaction { }`), ma è una decisione di come esporre le transazioni attraverso il port `TaskRepository` (che oggi non ha alcun concetto di transazione) — proposta per Sprint 2, non implementata qui per non introdurre un cambiamento di contratto dell'interfaccia senza discuterlo.

- **Robustezza della (de)serializzazione**: `TaskMappers.kt` chiama `Instant.parse`, `LocalDate::parse`, `LocalTime::parse` e `taskJson.decodeFromString(...)` senza alcun controllo — se una riga del database contenesse un valore malformato (es. da una futura migrazione di schema incompleta, o da corruzione del file SQLite), la lettura lancerebbe un'eccezione non gestita invece di un `OmniResult.Failure`, rompendo la garanzia "mai eccezioni per errori di dominio previsti" (TDR-21) proprio nel punto più vicino all'I/O. Non è un bug osservato (i dati scritti da questo stesso codice sono sempre ben formati), ma è un gap di robustezza verso dati esterni/futuri. **Proposta**: avvolgere il mapping riga→dominio in un `runCatching` e propagare un `TaskError` dedicato (es. `TaskError.CorruptedRecord`) invece di lasciar propagare l'eccezione.

- **`TaskError` contiene varianti morte**: `TaskListNotFound` e `BulkActionRequiresConfirmation` sono definite nel sealed class ma non sono mai restituite da alcun caso d'uso implementato in questo sprint (le azioni bulk non sono state implementate; nessun caso d'uso attuale opera su liste per id in un modo che possa fallire con "lista non trovata"). Non causano bug, ma sono debito morto nel vocabolario degli errori. **Proposta**: o rimuoverle finché non serve davvero un chiamante (YAGNI), o lasciarle con un commento esplicito "riservato per Sprint 2, azioni bulk" se la Functional Bible già le anticipa — verificare contro l'Engineering Plan prima di decidere quale delle due.

---

## 12. Conformità alle Bible

- **Functional Bible**: CRUD Task completo, Sottotask, Priorità (3 livelli), Liste (Area→Lista), Date/orario, Stati (attivo/completato/cestinato) — tutti implementati come da specifica. Confermato durante questa review che nessuna funzionalità fuori perimetro (sync cloud, notifiche push, AI) è stata introdotta.
- **Data Model Bible**: la scelta Sprint 1 di implementare Lista+Priorità e non un'entità "Tag" libera resta corretta — la Bible vieta esplicitamente tag liberi per il modulo Attività (già documentato in README-BUILD.md §11, riconfermato in questa review leggendo di nuovo la Bible).
- **Technical Architecture Bible**: Event Bus, Repository Port, layer L1-L6 rispettati (§1 sopra). Nessuna violazione nuova trovata.
- **UX/Design System Bible**: nessuna schermata Compose/SwiftUI esiste ancora (decisione già documentata e non in discussione in questa review, che riguarda solo il codice esistente).
- **Security & Privacy Bible**: gap di cifratura del DB locale riconfermato (§7), nessun requisito violato ulteriormente.

Nessuna non conformità nuova rilevata rispetto a quanto già dichiarato nel report Sprint 1.

---

## 13. Rischi

1. **Event Bus write-only**: `domain-task` pubblica `TaskEvent.Completed`, `TaskEvent.Deleted`, `TaskEvent.Created` ma nessun modulo in questo sprint si sottoscrive a nessuno di essi — il bus è verificato solo unitariamente (`InMemoryEventBusTest`), mai end-to-end con un consumer reale. Rischio: un bug nella forma concreta degli eventi (payload, tipo) potrebbe non emergere finché un secondo modulo (Sprint 2+) non tenta di sottoscriversi.
2. **Assenza di logging**: nessun modulo applicativo ha un logger. In produzione, un fallimento silenzioso (es. un `OmniResult.Failure` che una UI futura ignora per errore) non lascerebbe traccia diagnostica. Non bloccante per Sprint 1 (tutto verificato via test), ma da introdurre prima che il codice giri su dispositivi reali.
3. **Assenza di un gate di verifica delle dipendenze in CI**: la pipeline (`ci.yml`) esegue build/lint/test ma non verifica lockfile/checksum delle dipendenze Gradle scaricate (nessun `dependencyLocking` configurato) — una dipendenza transitiva compromessa non verrebbe rilevata dalla CI attuale.
4. **Cifratura del database locale mancante** (riconfermato da §7).
5. **`ReorderTasks` non atomico** (§11) — rischio concreto solo se un ID nella lista da riordinare sparisce a metà operazione (es. cancellato da un'altra sessione), scenario raro in single-device ma non impossibile con più finestre app aperte.

---

## Punteggi

| Dimensione | Punteggio (0-10) |
|---|---|
| **Architettura** | 8.5 |
| **Codice** | 8 |
| **Test** | 8 |
| **Manutenibilità** | 7.5 |

**Motivazione sintetica**: l'architettura rispetta la Dependency Rule e le convenzioni documentate senza eccezioni trovate; penalità minima per la dipendenza morta ora rimossa e per l'Event Bus mai esercitato end-to-end. Il codice era corretto nella quasi totalità ma conteneva due bug oggettivi reali (filtro SQL che perdeva un predicato, errori di dominio scartati nei ViewModel) e una violazione di Liskov nei test double — tutti corretti in questa review. I test sono ampi (103, +4 da questa review) e la nuova copertura chiude un buco reale (`TaskSort` mai testato contro l'implementazione reale), ma mancano ancora test per race condition/concorrenza nei ViewModel. La manutenibilità è penalizzata dai problemi progettuali documentati ma non risolti (SRP di `CompleteTask`, duplicazione delle fake, naming upsert-vs-update, robustezza della deserializzazione) — nessuno bloccante, tutti già proposti sopra con una soluzione concreta.

## Priorità per Sprint 2

1. Risolvere il blocco Sync Engine (idempotenza cross-device per la ricorrenza, TASK-AC-05) — già segnalato come blocco principale nel report Sprint 1.
2. Introdurre `core-testing` e consolidare le due `FakeTaskRepository` in un'unica fixture condivisa (§2).
3. Decidere e implementare la semantica upsert-vs-update per `SqlDelightTaskRepository` (§9).
4. Rendere `ReorderTasks` atomico con una transazione esplicita (§11).
5. Aggiungere un logger applicativo minimale prima di qualunque test su dispositivo reale (§13.2).
6. Valutare la cifratura del database locale (SQLCipher o equivalente) contro la Security & Privacy Bible (§7).
7. Aggiungere test di concorrenza/race-condition per i ViewModel (§8) e robustezza della deserializzazione con dati malformati (§11).
