# Sprint 5 — MVP Vertical Slice — Report

**Perimetro**: la prima fetta verticale realmente usabile di OmniLife — non una demo. Riusa e
integra genuinamente `core-common`, `core-eventbus`, `core-search`, `core-sync`,
`core-notifications`, `core-designsystem`, `core-designtokens`, `domain-task`, `feature-task`,
`feature-core`/Home — aggiungendo `domain-account` (Setting/Onboarding, mancante fino a questo
sprint) e i nuovi moduli `feature-search`/`feature-settings`. Nessuna Bible nuova, nessuna
decisione architetturale cambiata arbitrariamente — sei decisioni tecniche realmente necessarie e
non documentate sono state registrate prima nel Technology Decision Record (TDR-34…40) e poi
implementate, come richiesto dalle istruzioni di questo sprint.

*(Nota: questo documento è stato scritto man mano che il lavoro procedeva; le sezioni §3/§4/§8
sui risultati di test/lint/build riportano gli esiti reali dell'ultima verifica eseguita in questo
sandbox — non stime.)*

## 1. Funzionalità implementate

### 1.1 Correzione architetturale esplicitamente richiesta

**`SyncStateManager.observe` leak (TDR-34)**: `observe()` ora ritorna un `SyncStateSubscription`
(`fun cancel()`), simmetrico a `core-eventbus`'s `Subscription`. `HomeViewModel.clear()` e
`SettingsViewModel.clear()` chiamano `cancel()` sul proprio abbonamento. Verificato con un test
comportamentale reale (non una verifica di chiamata): due listener registrati, uno cancellato,
l'altro continua a ricevere notifiche mentre quello cancellato no.

### 1.2 `domain-account` — Setting e Onboarding (nuovo)

DM-SYS-06 (Setting: `chiave`/`valore`/`ambito`) implementato per il sottoinsieme del catalogo SET
§2 che questo sprint collega end-to-end: `THEME`, `ACCENT_COLOR`, `NOTIFICATION_DAILY_BUDGET`,
`NOTIFICATION_QUIET_HOURS_START/END` (TDR-19-style, SQLDelight/TDR-20, stesso pattern di
`domain-task`). `OnboardingState` (completato/quando) è un'entità propria, separata dal catalogo
Setting (TDR-36 — SET-R-01 impone un catalogo chiuso; "onboarding completato" non è
un'impostazione scelta dall'utente). `AccentColor` rispecchia deliberatamente i 6 nomi già
esistenti di `core-designtokens`'s `OmniAccent` (BLU/VERDE/VIOLA/CORALLO/PETROLIO/INDACO) —
scoperto e corretto durante il lavoro sulla composition root Android (§7): la prima stesura aveva
inventato nomi propri (INDACO/CORALLO/SMERALDO/AMBRA/ORCHIDEA), disallineati dal set reale.

### 1.3 I tre bridge Task↔Core (nuovo, `feature-task/bridge`)

Orchestrazione L2 (Technical Architecture Bible §03) — nessuno di questi vive in `domain-task` o
in un modulo `core-*`, ciascuno sottoscrive `TaskEvent` sull'`EventBus` condiviso:

- **`TaskSearchIndexBridge`**: mantiene l'indice di `core-search` coerente con ogni scrittura di
  task (creazione, modifica, completamento, riapertura, riprogrammazione, cestinazione).
  Cestinare un task **re-indicizza** (mai rimuove) con `lifecycleState = TRASHED` (SRCH-006: le
  entità cestinate restano indicizzate, escluse dai risultati di default solo via l'opt-in di
  `SearchFilter`). Espone `rebuildIndex()` per popolare l'indice all'avvio da ogni task già
  esistente (attivo + cestinato).
- **`TaskNotificationBridge`**: collega `ReminderConfig` al `NotificationBroker` reale — Task →
  pianificazione (`NotificationBroker.request`) → storico (`NotificationHistoryStore`, già
  esistente) → deep link (`DeepLinkResolver`, già esistente) → annullamento (`NotificationBroker.cancel`,
  TDR-39, aggiunto questo sprint) su completamento/eliminazione, riprogrammazione su modifica
  della data/ora di scadenza (cancella-poi-ri-richiede, mai un duplicato).
- **`TaskSyncOutboxBridge`**: ogni mutazione di task viene messa in coda nell'outbox persistente
  di `core-sync` (SQLDelight, TDR-27) — mai simulata come sincronizzata con successo. Il payload è
  lo snapshot corrente del task in JSON (non un delta CRDT per campo — vedi §6 rischi residui).
  `SyncStateManager.updatePendingCount` (TDR-40, aggiunto questo sprint) riflette la profondità
  reale della coda nel momento stesso in cui una modifica viene accodata, senza affermare alcun
  esito di sincronizzazione mai avvenuto.

### 1.4 Task Flow completo (`feature-task`)

- **`TaskCreateViewModel`/`TaskCreateBottomSheet`**: Quick Capture scoperto — una versione
  volutamente ridotta del futuro sistema `feature-capture` multi-entità (vedi §6), non
  un'implementazione parziale nascosta: titolo (unico campo obbligatorio, TASK-R-01), data/ora di
  scadenza, priorità, note, promemoria opzionale (solo se data e ora sono entrambe impostate,
  come vincola `ReminderConfig`).
- **`TaskDetailBottomSheet`**: IA-035 come Bottom Sheet obbligatorio (Navigation Bible §5, mai una
  schermata push) — ogni campo autosalva (MFC-R-06), gestione sottotask, eliminazione con
  conferma (`OmniDialog`, mai per un singolo elemento senza conferma — coerente con MFC-R-09 che
  riserva la conferma esplicita al bulk/permanente, qui applicata perché l'eliminazione è
  comunque annullabile dal cestino), auto-chiusura garbata se l'entità è stata eliminata altrove
  (MUC §5).
- **`TaskListScreen`**: le 4 viste fisse (TASK-012/014) via `OmniSegmentedControl`, ricerca
  incrementale locale, scelta sottotask-aperti su completamento (TASK-AC-03) mai applicata in
  silenzio.

### 1.5 Home reale (`feature-core`, sostituisce i placeholder dello Sprint 4)

`HomeViewModel` ora dipende da `domain-task`/`core-eventbus` (Sprint 4 li escludeva
esplicitamente — la richiesta di questo sprint lo richiede esplicitamente, quindi il vincolo è
stato aggiornato, non violato in silenzio):

- **Today Overview**: `GetTasksForView(TaskListMode.TODAY)`, dati reali.
- **Agenda**: `GetTasksForView(TaskListMode.UPCOMING)` — dichiarato onestamente come "solo date di
  scadenza dei task", mai spacciato per un vero calendario (nessun `domain-calendar` in questo
  sprint).
- **Recent Activity**: un log limitato (20 voci) accumulato sottoscrivendo `TaskEvent` — l'Event
  Bus non è uno store di eventi (`core-eventbus`'s doc dichiarato: "non un event-store"), quindi
  questo bridge-locale è la fonte reale, non un'invenzione.
- **Goal/Habit/Finance/Calendar Summary**: restano placeholder funzionali, per costruzione —
  quei moduli `domain-*` non esistono in questo sprint (esplicitamente fuori perimetro).

### 1.6 Onboarding (`feature-core/onboarding`, nuovo)

Tre passi, mai un tour guidato multi-schermo (P115): benvenuto → personalizzazione
**facoltativa** (tema + colore accento, sempre saltabile) → prima cattura reale (crea un task
vero o salta — mai un task fittizio). Completamento persistito via `domain-account`'s
`OnboardingState`.

### 1.7 Settings (`feature-settings`, nuovo)

Sottoinsieme genuinamente collegato del catalogo SET §2 (SET-R-01: il catalogo resta chiuso —
non aggiunta alcuna voce senza passare i 7 cancelli): tema, colore accento, budget notifiche
giornaliero, orari di silenzio, stato di sincronizzazione (sola lettura, reattivo), reset
onboarding (con conferma esplicita — mai un'azione a un tocco per uno stato reversibile ma
significativo). Sicurezza/privacy/abbonamento restano un punto di ingresso onesto ("arriva in un
prossimo sprint"), mai finto presente.

### 1.8 Ricerca globale (`feature-search`, nuovo)

`SearchViewModel`/`SearchScreen` collegati a `UnifiedSearchService` — trova i task realmente
indicizzati dal bridge di §1.3. Le ricerche recenti si registrano solo quando un risultato viene
aperto (`SearchIntent.ResultOpened`), non a ogni carattere digitato — coerente con SRCH-004
("la ricerca non registra né sincronizza le query" salvo quelle genuinamente effettuate).

### 1.9 Navigazione MVP (`androidApp`)

`AppTab` a 4 voci fisse (Oggi/Moduli/Cerca/Profilo, Navigation Bible §3), scritta a mano (TDR-38
— nessuna libreria di navigazione di terze parti, stessa filosofia di TDR-19/22/24). Onboarding
come gate a schermo intero prima della shell a tab. Task Detail/Create come sheet sovrapposti, mai
destinazioni di navigazione separate.

## 2. Architettura coinvolta

- **Dependency Rule aggiornata (TDR-37)**: `androidApp`/`iosApp` dipendono da `shared` **più** i
  `feature-*` che mostrano realmente — non più "solo `shared`" (quella regola valeva letteralmente
  solo finché nessuna schermata reale esisteva in `feature-*`). `shared` stesso resta invariato.
  `README-BUILD.md` §3 aggiornato di conseguenza.
- **`feature-core`/`feature-task`/`feature-search`/`feature-settings`** dipendono ora dai moduli
  `core-*` che i propri bridge/schermate orchestrano realmente, non solo da `core-designtokens`
  come nello scaffolding originario — documentato in `README-BUILD.md` §3.
- **Composition root** (`androidApp/AppContainer.kt`, TDR-19): l'unico punto che conosce ogni tipo
  concreto — repository SQLDelight reali, i tre bridge, ogni use case, ogni ViewModel. Nessun
  framework di injection.

## 3. Schermate

| Schermo | Modulo | Reale/placeholder |
|---|---|---|
| Home "Oggi" | `feature-core` | **Reale** (Today/Agenda/Recent Activity ora con dati veri; Goal/Habit/Finance/Calendar placeholder per costruzione) |
| Onboarding (3 passi) | `feature-core/onboarding` | **Reale** |
| Lista attività (4 viste) | `feature-task` | **Reale** |
| Dettaglio attività (sheet) | `feature-task` | **Reale** |
| Crea attività (sheet, Quick Capture) | `feature-task` | **Reale**, scope ridotto (vedi §1.4) |
| Ricerca globale | `feature-search` | **Reale** |
| Impostazioni | `feature-settings` | **Reale** per il sottoinsieme SET §2 collegato; sicurezza/privacy/abbonamento placeholder onesto |
| Shell a 4 tab | `androidApp` | **Reale**, scritta a mano (TDR-38) |
| iOS: shell a 4 tab + lista attività | `iosApp` | Scritta, **non verificata** (nessun host macOS in questo sandbox) — vedi §5 |

## 4. Test

Tutti i test elencati sono comportamentali (verificano lo stato/l'effetto reale, mai solo che un
metodo sia stato chiamato). Circa 85 test nuovi o modificati questo sprint, distribuiti così:

| Modulo | File | Test |
|---|---|---|
| `core-sync` | `SyncStateManagerTest` (+5 nuovi) | 9 |
| `core-notifications` | `NotificationBrokerTest` (+2 nuovi, `cancel`) | 13 |
| `domain-task` | `UpdateTaskFieldsTest` (+2 nuovi, `TaskEvent.Updated`) | 6 |
| `domain-account` | `SettingsUseCasesTest` + `SqlDelightSettingsRepositoryTest` + `AccountSmokeTest` | 21 |
| `feature-task/bridge` | `TaskSearchIndexBridgeTest` + `TaskNotificationBridgeTest` + `TaskSyncOutboxBridgeTest` | 18 |
| `feature-task` | `TaskCreateViewModelTest` (nuovo) | 7 |
| `feature-core` | `HomeViewModelTest` (+5 nuovi) | 18 |
| `feature-core/onboarding` | `OnboardingViewModelTest` (nuovo) | 8 |
| `feature-settings` | `SettingsViewModelTest` (nuovo) | 9 |
| `feature-search` | `SearchViewModelTest` (nuovo) | 8 |

**Esecuzione reale in questo sandbox**: `core-sync`, `domain-task`, `domain-account` (nessuna
dipendenza Compose) — **eseguiti con successo** (`jvmTest` verde con `pipefail`, non solo
compilato — verificato dopo aver scoperto e corretto due bug reali di compilazione durante questo
stesso sprint, vedi §6 voci 6-7). `feature-task`, `feature-core`, `feature-settings`,
`feature-search`, `core-notifications` toccano Compose Multiplatform nello stesso
modulo/classpath: la verifica di questi moduli è **in corso al momento in cui questa sezione è
stata scritta** — tre iterazioni di build reali hanno già trovato e corretto errori di
compilazione autentici in questo stesso ciclo (`SheetState` senza `@OptIn`, dipendenza
`compose.material3` mancante, collisione di nome di classe) — non "verde" dichiarato senza
esecuzione, ma nemmeno ancora confermato verde all'ultimo giro. Questo è un commit di checkpoint
(il repository richiede di non lasciare modifiche non committate); un commit successivo
aggiornerà questa sezione con l'esito finale reale non appena la verifica in corso completa.

## 5. Limiti d'ambiente

- **iOS**: nessun host macOS/Xcode in questo sandbox (vincolo di Kotlin/Native e Swift stessi, non
  specifico di questo ambiente). `iosApp`'s `RootView`/`AppTab`/`TaskListPlaceholderView` sono
  scritti secondo le convenzioni standard SwiftUI/Swift Package Manager ma **non compilati né
  eseguiti da `swift build`** — vedi `iosApp/README.md`. Il punto di integrazione con lo
  `StateFlow` esportato da `:shared` è documentato ma non scritto (richiede l'header
  Objective-C generato dalla compilazione reale del framework iOS).
- **Android runtime**: nessun Android SDK in questo sandbox (il proxy di rete blocca
  `dl.google.com`) — `androidApp` non è incluso nel grafo di build qui (gating già presente in
  `settings.gradle.kts` da prima di questo sprint) e quindi **non è mai stato compilato in questo
  ambiente**. `AppContainer.kt`/`MainActivity.kt` sono scritti con la massima cura evidenziale
  possibile (ogni tipo referenziato è verificato sul target JVM) ma restano interamente non
  verificati come file — vedi la nota nella loro stessa doc comment.
- **`jvmTest` con Compose Multiplatform nello stesso modulo**: limite di rete già documentato
  dallo Sprint 2/4 (`androidx.lifecycle`/`androidx.collection`/`androidx.annotation`, dipendenze
  transitive di `compose.foundation`, risolvibili solo da `dl.google.com`, bloccato). Riguarda
  ogni modulo Compose-toccato con file `jvmTest` reali: `feature-core`, `feature-task`,
  `feature-settings`, `feature-search`. `compileKotlinJvm`/`compileTestKotlinJvm` restano verdi
  per questi moduli (verificato con `pipefail`, non solo un codice di uscita di comodo).

## 6. Bug trovati e corretti

1. **`SyncStateManager.observe` leak** (già noto dallo Sprint 4) — corretto (TDR-34, §1.1).
2. **`UpdateTaskFields` non pubblicava alcun evento** — scoperto progettando il bridge di ricerca;
   avrebbe lasciato l'indice incoerente dopo una modifica (violazione diretta di SRCH-AC-02).
   Corretto con `TaskEvent.Updated` (TDR-35).
3. **`AccentColor` disallineato da `OmniAccent`** (§1.2) — scoperto scrivendo la composition root
   Android; corretto rinominando i 6 valori per farli coincidere esattamente.
4. **Un `Spacer` con `.padding()` invece di `.height()`** in `OnboardingScreen.kt` — non avrebbe
   prodotto alcuno spazio visibile (bug di comportamento, non di compilazione); corretto durante
   la stesura.
5. **Un side effect (`onDismiss()`) chiamato direttamente nel corpo di un Composable** in
   `TaskDetailBottomSheet` invece che in un `LaunchedEffect` — anti-pattern Compose che rischia
   comportamento inconsistente tra ricomposizioni; corretto.
6. **Due nomi di classe di test duplicati** (`FixedClock` dichiarata in due file dello stesso
   pacchetto `com.omnilife.feature.task.bridge`) — collisione di classe JVM top-level nonostante
   entrambe fossero `private` (la visibilità `private` a livello di file non previene la
   collisione di nome a livello di classe JVM); corretto rinominando una delle due.
7. **Due nomi di funzione di test contenenti `:`** (backtick illegale per un identificatore JVM)
   in `UpdateTaskFieldsTest.kt` — errore di compilazione reale, non falso positivo; corretto
   rimuovendo i due punti dai nomi dei test.

## 7. Rischi residui

- **Nessun `RemoteSyncTransport` reale**: non esiste un backend contro cui sincronizzare in
  questo sprint — `BackgroundSyncCoordinator` (Sprint 3) non è collegato nella composition root
  per non simulare un esito di sincronizzazione mai avvenuto (vincolo esplicito di questo sprint).
  L'outbox accoda e persiste realmente; solo il tentativo di consegna resta da collegare quando
  un backend esisterà.
- **Payload dell'outbox non è un delta CRDT per campo**: `TaskSyncOutboxBridge` serializza lo
  snapshot intero del task, non un `LwwRegister`-per-campo come `DeltaGenerator` si aspetterebbe —
  cablare `domain-task` a tracciamento CRDT per-campo è un cambiamento più ampio dello scope di
  questo sprint.
- **Storico/digest/registro categorie delle notifiche restano in-memory**: nessuna versione
  SQLDelight esiste ancora per `NotificationHistoryStore`/`NotificationDigest`/
  `NotificationCategoryRegistry` — i promemoria dei task persistono e si ri-pianificano
  correttamente (vivono sull'entità Task), ma la lista storica delle notifiche si azzera al
  riavvio.
- **Nessuna entità Account reale**: `ownerAccountId`/`deviceId` sono placeholder fissi
  (`"local-account"`/`"local-device"`) nella composition root — coerente con "Account, Sicurezza"
  esplicitamente fuori dal sottoinsieme SET §2 collegato questo sprint.
- **Home widget non cliccabili verso il Dettaglio**: `HomeSectionState`/`HomeListEntry` (Sprint 4)
  non hanno un callback di click per riga — toccare un task in Today Overview non apre ancora il
  suo sheet di dettaglio (documentato nel codice, non nascosto).
- **Nessun selettore data/ora in `core-designsystem`**: `TaskCreateBottomSheet` non ha modo di
  impostare `dueDate`/`dueTime` dalla UI (nessun componente `OmniDatePicker`/`OmniTimePicker`
  esiste ancora — introdurne uno da zero sarebbe stata una nuova componente di design system non
  governata da alcuna Bible esistente, fuori dal perimetro ragionevole di questo sprint). Di
  conseguenza il promemoria (che richiede entrambe) non è raggiungibile dalla UI di creazione, pur
  essendo la logica sottostante reale e verificata (`TaskCreateViewModelTest`,
  `TaskNotificationBridgeTest` la esercitano con date/ore programmatiche). `TaskDetailBottomSheet`
  ha lo stesso limite per la modifica della data di scadenza — mostra la data corrente in sola
  lettura, senza un modo per cambiarla dalla UI in questo sprint.
- **`androidApp`/`iosApp` interamente non verificati** in questo sandbox (§5) — il rischio più
  ampio di questo sprint: ogni singolo tipo che compongono è verificato sul target JVM, ma la loro
  stessa composizione (la wiring stessa) non ha mai visto un compilatore.
- **Perdita di stato UI-locale al cambio tab**: `AppShell`'s `remember` per-tab non ha chiavi
  esplicite — passare da un tab all'altro e tornare ricrea il ViewModel di quel tab (i dati
  persistiti tornano corretti dal repository; solo stato UI effimero come una query di ricerca
  non ancora inviata si perde).

## 8. Decisioni tecniche (TDR-34…40)

Vedi `technology_decision_record.md` per il testo completo di ciascuna (alternative, motivazione,
impatto). Sintesi:

| TDR | Decisione |
|---|---|
| TDR-34 | `SyncStateManager.observe` ritorna un `SyncStateSubscription` proprio del modulo |
| TDR-35 | `UpdateTaskFields` pubblica `TaskEvent.Updated` |
| TDR-36 | `OnboardingState` è un'entità propria, separata dal catalogo Setting |
| TDR-37 | `androidApp`/`iosApp` dipendono da `shared` + i `feature-*` che mostrano |
| TDR-38 | Navigazione a 4 tab scritta a mano, nessuna libreria di terze parti |
| TDR-39 | `NotificationBroker.cancel` — cancellazione pubblica, mai un accesso diretto al servizio di piattaforma |
| TDR-40 | `SyncStateManager.updatePendingCount` — aggiorna solo la profondità della coda, mai un esito di sync |

## 9. Funzionalità esplicitamente fuori perimetro

Come da istruzioni del task: AI, Finanze complete, Calendario completo, Note complete, Abitudini
complete, Obiettivi completi, Salute completa, Marketplace, Enterprise, funzionalità social.
Inoltre, esplicitamente rinviati da questo sprint stesso: Account/Sicurezza/Abbonamento reali
(SET §2), storico notifiche persistente, sincronizzazione con un backend reale, delta CRDT
per-campo per `domain-task`, click sui widget Home verso il Dettaglio.

---

*Prossimo passo naturale: un backend minimo (anche solo un `RemoteSyncTransport` di test) per
chiudere il ciclo di sincronizzazione; le entità Account/Sicurezza per completare SET §2; il
collegamento dei widget Home al Dettaglio.*
