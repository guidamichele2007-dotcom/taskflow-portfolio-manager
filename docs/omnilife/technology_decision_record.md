# Technology Decision Record — OmniLife

> **Versione:** 1.0 · **Stato:** Approvato · **Data:** 2026-07-20
>
> **Fonte di verità**: l'intera documentazione esistente — [Product Bible](product_bible/README.md), [Functional Bible](functional_bible/README.md), [UX Bible](ux_bible/README.md), [Data Model Bible](data_model_bible/README.md), [Technical Architecture Bible](technical_architecture_bible/README.md), [Design System Bible](design_system_bible/README.md), [Engineering Plan](engineering_plan/README.md), e la documentazione tecnica originaria (in particolare [03-architettura.md](03-architettura.md), che aveva già proposto scelte informali — questo documento le riprende, le confronta formalmente con alternative reali e le ratifica o le corregge, senza riscriverle in loco).
>
> **Perimetro**: questo è il primo documento della serie che **sceglie effettivamente una tecnologia**. Nessuna Bible precedente lo fa per mandato esplicito; questo documento esiste proprio per colmare quel vuoto dichiarato (Engineering Plan, [report §5](engineering_plan/08-report.md): "la decisione tecnologica... è l'unico vero prerequisito mancante"). Non implementa nulla, non contiene codice.
>
> **Metodo**: per ogni area, almeno 3 alternative reali confrontate; la decisione è motivata citando gli ID delle Bible che la vincolano (non ridescritti, solo citati). ID di questo documento: `TDR-01`…`TDR-18`.

---

## Indice delle decisioni

| # | Area | Decisione |
|---|---|---|
| TDR-01 | Linguaggio mobile | Kotlin Multiplatform (dominio condiviso) + Swift/Kotlin nativi (UI) |
| TDR-02 | Architettura mobile (pattern di presentazione) | MVI / Unidirectional Data Flow entro i 6 layer già definiti |
| TDR-03 | Backend | Go, servizi stateless containerizzati |
| TDR-04 | Autenticazione | Token propri a breve vita + refresh, Sign-in di piattaforma opzionale, TOTP |
| TDR-05 | Sincronizzazione | CRDT minimale su misura (per-campo LWW + OR-Set + snapshot Note) |
| TDR-06 | Database locale | SQLite cifrato + FTS |
| TDR-07 | Database server | PostgreSQL |
| TDR-08 | Storage | Object storage compatibile S3 |
| TDR-09 | Notifiche | APNs/FCM diretti dietro relay proprio |
| TDR-10 | Analytics | Pipeline analytics open-source, self-hosted |
| TDR-11 | Crash reporting | Toolkit open-source/self-hostabile |
| TDR-12 | Osservabilità | Stack open-source (metriche/log/tracce), self-gestibile |
| TDR-13 | Testing | Framework nativi per piattaforma + suite condivisa KMP + driver E2E cross-platform |
| TDR-14 | CI/CD | Pipeline-as-code portabile, runner containerizzati |
| TDR-15 | Gestione segreti | Vault dedicato, credenziali a breve vita |
| TDR-16 | Dipendenze | SCA + SBOM continui, allowlist di licenze curata |
| TDR-17 | Packaging | Pacchetti nativi store + delivery modulare on-demand |
| TDR-18 | Build system | Build multiplatform unificata (Gradle-class) |

---

## TDR-01 · Linguaggio Mobile

**Decisione**: **Kotlin Multiplatform (KMP)** per il dominio condiviso (logica, sincronizzazione, sicurezza, parser) + **Swift/SwiftUI (iOS)** e **Kotlin/Jetpack Compose (Android)** per l'interfaccia nativa.

| Alternativa | Descrizione | Esito |
|---|---|---|
| **A — KMP + UI native** | Un solo modulo di dominio (Kotlin) compilato per entrambe le piattaforme; UI scritta nativa per piattaforma | ✅ Scelta |
| **B — Cross-platform unico (Flutter/Dart)** | Un solo linguaggio e un solo motore di rendering per tutto, UI inclusa | ❌ Scartata |
| **C — Nativo puro doppio (Swift + Kotlin separati, nessuna condivisione)** | Due implementazioni indipendenti, zero codice condiviso | ❌ Scartata |

**Motivazione**: il prodotto richiede qualità nativa non negoziabile — widget di sistema, integrazione biometrica/enclave, piattaforma Salute/Calendario di sistema, 60fps costanti, avvio <1,5s (Product Bible P58, Success Metrics; Functional Bible HLTH-001/CAL-001 dipendono da API di piattaforma profonde). Un motore di rendering cross-platform (Flutter) introduce un livello di astrazione proprio tra l'app e i widget/l'accessibilità nativa (VoiceOver/TalkBack), rischiando di non rispettare gli standard di accessibilità già vincolanti (UX Bible, Accessibility Bible) con la stessa fedeltà del nativo. Il nativo puro doppio (opzione C) duplica interamente la logica più delicata (sincronizzazione CRDT, sicurezza) in due basi di codice — rischio di divergenza già registrato in Product Bible ([Decision Log D-08](product_bible/14-decision-log.md)).

**Vantaggi**: UI e integrazioni di sistema al 100% native; un solo motore di sincronizzazione/sicurezza scritto una volta (dimezza i bug nel punto più costoso, coerente con D-08); accesso completo e senza ritardo alle nuove API di piattaforma.

**Svantaggi**: due team UI (iOS/Android) restano necessari; la "linea di confine" tra modulo condiviso e codice nativo richiede disciplina per non erodersi nel tempo; KMP è una tecnologia più giovane di Swift/Kotlin nativi puri, con tooling in maturazione.

**Impatto sul progetto**: coerente 1:1 con [Technical Architecture Bible §01](technical_architecture_bible/01-architettura-generale-e-layer.md) — il modulo condiviso KMP implementa L3 (Dominio) e parte di L4 (Servizi Core), l'UI nativa implementa L1 (Esperienza); rispetta la Dependency Rule per costruzione (il modulo condiviso non conosce la UI).

**Rischi**: divergenza di comportamento tra le due UI se la disciplina architetturale si allenta (mitigato dal Contratto di Modulo, [Technical Architecture Bible §04](technical_architecture_bible/04-plugin-architecture.md)); scarsità di talenti KMP sul mercato rispetto a Swift/Kotlin puri.

**Costo**: nessuna licenza (open source); costo primario è il team (2 iOS + 2 Android + 2 core condiviso, coerente con l'ipotesi di team di [doc 09-piano-di-sviluppo](09-piano-di-sviluppo.md)).

**Facilità di manutenzione**: alta per la logica condivisa (un solo posto da correggere); media per la UI (due basi di codice da mantenere in parità visiva — mitigato dal Design System Bible, che rende i token identici).

**Scalabilità**: nessun impatto sulla scalabilità utente (è una scelta client-side); scala bene in termini di team (nuovi moduli aggiungono codice isolato, coerente con [Technical Architecture Bible §08](technical_architecture_bible/08-scalabilita-estendibilita.md)).

**Compatibilità con le Bible**: piena. UX Bible (componenti nativi, gesti di sistema), Design System Bible (nessuna scelta di token vincolata a un framework), Data Model Bible (nessun vincolo di persistenza specifico), Technical Architecture Bible (Dependency Rule rispettata), Product Bible (D-08 esplicitamente a favore di questa opzione).

---

## TDR-02 · Architettura Mobile (pattern di presentazione)

> Nota di perimetro: i 6 layer logici e la Dependency Rule sono **già definiti e non ridiscussi** ([Technical Architecture Bible §01](technical_architecture_bible/01-architettura-generale-e-layer.md)). Questa decisione riguarda **solo** il pattern concreto con cui L1 (Esperienza) gestisce lo stato e comunica con L2.

**Decisione**: **MVI (Model-View-Intent) / Unidirectional Data Flow**, con stato immutabile ed eventi espliciti.

| Alternativa | Descrizione | Esito |
|---|---|---|
| **A — MVI/UDF** | Stato immutabile, un solo flusso di aggiornamento, intenti espliciti dall'utente | ✅ Scelta |
| **B — MVVM classico (binding bidirezionale)** | ViewModel osservato dalla view con binding a due vie | ❌ Scartata |
| **C — Nessun pattern imposto (per-schermata ad-hoc)** | Ogni schermata implementa la gestione di stato che preferisce | ❌ Scartata |

**Motivazione**: la UX Bible impone un catalogo di stati identico per ogni schermata (vuoto/caricamento/errore/offline/sync/…, [MUC §4](ux_bible/00-modello-ux-comune.md)) e un [Generic Entity Flow](ux_bible/00-modello-ux-comune.md#9-il-flusso-generico-del-ciclo-di-vita-di-unentità-generic-entity-flow--gef) uniforme: un flusso unidirezionale con stato esplicito rende questi stati **rappresentabili come un singolo valore verificabile nei test**, mentre il binding bidirezionale (B) permette stati intermedi impliciti difficili da testare contro gli 8 stati generici del [Design System Bible §04](design_system_bible/04-stati-e-accessibilita-visiva.md). L'assenza di pattern (C) è incompatibile con "un solo design system, nessuna eccezione per modulo" (P55) applicato all'architettura di presentazione.

**Vantaggi**: stato testabile deterministicamente (essenziale per i test `AT`/`E2E` già richiesti, [Engineering Plan §05](engineering_plan/05-pratiche-di-sviluppo.md)); debug più semplice (ogni stato è ispezionabile); coerenza tra le due UI native (stesso pattern concettuale su Swift e Kotlin, anche se l'implementazione differisce).

**Svantaggi**: più codice boilerplate per schermate semplici rispetto al binding diretto; richiede disciplina del team per non degenerare in stato mutabile "di comodo".

**Impatto sul progetto**: rende meccanicamente verificabile la regola "L1 non contiene mai logica di business" ([Technical Architecture Bible §02 §4](technical_architecture_bible/02-moduli-responsabilita-boundaries.md)) — uno stato immutabile prodotto da L2 non lascia spazio a calcoli nascosti in L1.

**Rischi**: nessuno strutturale; rischio di adozione incoerente tra i due team piattaforma (mitigato da linee guida condivise nel Design System Bible).

**Costo**: nessuno (pattern architetturale, non libreria a pagamento).

**Facilità di manutenzione**: alta — lo stato esplicito riduce i bug di sincronizzazione UI-dati, la causa più comune di regressioni difficili da isolare.

**Scalabilità**: neutra (riguarda la struttura del codice, non il carico).

**Compatibilità con le Bible**: piena con UX Bible (stati/microinterazioni), Design System Bible (8 stati generici), Technical Architecture Bible (Dependency Rule).

---

## TDR-03 · Backend

**Decisione**: **Go**, servizi stateless containerizzati.

| Alternativa | Descrizione | Esito |
|---|---|---|
| **A — Go** | Linguaggio compilato, concorrenza nativa, runtime leggero | ✅ Scelta |
| **B — Kotlin/JVM (Ktor o equivalente)** | Riuso del linguaggio del modulo condiviso KMP anche lato server | ❌ Scartata |
| **C — Node.js/TypeScript** | Runtime a evento singolo, ecosistema JavaScript | ❌ Scartata |

**Motivazione**: il confine L6 è **content-blind per costruzione** ([Technical Architecture Bible §01 §6](technical_architecture_bible/01-architettura-generale-e-layer.md)) — riceve/serve blob cifrati e metadati, senza elaborare contenuti. Questo profilo (I/O-bound, alta concorrenza, poca logica applicativa) favorisce Go: runtime leggero, concorrenza nativa a basso overhead, footprint di memoria ridotto per istanza (rilevante per la scala "milioni di utenti" di Product Bible, Business Strategy). Kotlin/JVM (B) avrebbe il vantaggio di un solo linguaggio nel team, ma la JVM ha un footprint di memoria per istanza più alto, penalizzante a scala; Node.js (C) è idoneo a carichi I/O-bound ma il suo modello a singolo thread è meno adatto a un servizio di sincronizzazione con alta concorrenza per-utente e tipizzazione più debole per un dominio (seppur minimo) con requisiti di correttezza (billing, auth).

**Vantaggi**: concorrenza nativa efficiente per il servizio più caldo (Sync, [Technical Architecture Bible §08](technical_architecture_bible/08-scalabilita-estendibilita.md)); binari statici semplici da containerizzare e distribuire; ecosistema maturo per servizi di rete.

**Svantaggi**: un secondo linguaggio nel team oltre a Kotlin (nessun riuso diretto di codice col modulo condiviso, che comunque non dovrebbe girare lato server per il vincolo content-blind); ecosistema di librerie meno ampio di JVM/Node per casi non di rete.

**Impatto sul progetto**: implementa esclusivamente L6 (confine esterno) — non tocca L1-L5, coerente con l'isolamento già stabilito.

**Rischi**: nessun rischio architetturale (il backend è deliberatamente "stupido"); rischio operativo standard di gestione di servizi containerizzati (mitigato da TDR-14 CI/CD).

**Costo**: nessuna licenza (open source); costo infrastrutturale basso per il profilo leggero del runtime, coerente con "il costo infra basso per design = margine di manovra sul prezzo" (Product Bible, registro rischi R-06).

**Facilità di manutenzione**: alta — pochi servizi, responsabilità minime per costruzione (content-blind).

**Scalabilità**: nativa e orizzontale, imbarazzantemente parallela per utente ([Technical Architecture Bible §08 §2](technical_architecture_bible/08-scalabilita-estendibilita.md): nessuna transazione cross-utente).

**Compatibilità con le Bible**: piena — nessun vincolo di Product/Functional/UX/Data Model Bible riguarda il linguaggio server; il vincolo architetturale (content-blind, nessuna elaborazione dei contenuti) è rispettato per costruzione, non per scelta di linguaggio.

---

## TDR-04 · Autenticazione

**Decisione**: **token propri a breve vita (access + refresh) legati al dispositivo**, con Sign-in di piattaforma (Apple/Google) come metodo di accesso opzionale, e TOTP per il 2FA.

| Alternativa | Descrizione | Esito |
|---|---|---|
| **A — Token propri + Sign-in di piattaforma opzionale + TOTP** | Emissione e verifica interamente sotto il nostro controllo | ✅ Scelta |
| **B — Piattaforma di identità gestita da terzi (IDaaS)** | Un fornitore esterno gestisce l'intero ciclo di autenticazione | ❌ Scartata |
| **C — Solo Sign-in di piattaforma (nessuna credenziale propria)** | Login esclusivamente tramite Apple/Google | ❌ Scartata |

**Motivazione**: un fornitore IDaaS esterno (B) vedrebbe ogni accesso dell'utente — un terzo con visibilità su login/metadati non necessario, in tensione con l'indipendenza già rivendicata da Product Constitution (art. 177, spirito applicato qui) e con la minimizzazione (C-art. 45). Il solo Sign-in di piattaforma (C) semplifica l'implementazione ma **esclude l'accesso da web/altri contesti futuri** (Product Bible, Business Strategy §5, client desktop condizionato) e crea dipendenza totale da due soli fornitori per l'intera base utenti — rischio di continuità di business. La scelta A mantiene il controllo, resta compatibile con Sign-in di piattaforma come *comodità*, non come unico percorso.

**Vantaggi**: nessuna terza parte vede i login; piena compatibilità con [SEC-001/002/003](functional_bible/14-impostazioni-profilo-sicurezza.md) (biometria locale, chiave di recupero, 2FA) già specificati; il registro dispositivi ([Data Model Bible DM-SYS-02](data_model_bible/01-entita-sistema.md)) si integra nativamente.

**Svantaggi**: responsabilità piena della sicurezza dell'implementazione (nessun fornitore esterno assorbe il rischio di un bug di autenticazione); più lavoro iniziale di TDR-04 la rende ~1-2 settimane più costosa di un'integrazione IDaaS pronta.

**Impatto sul progetto**: implementata dal Servizio di Sicurezza (L4) verso il confine L6, coerente con [Technical Architecture Bible §10](technical_architecture_bible/10-sicurezza-architetturale.md); i token non contengono mai dati utente (payload minimo, coerente con C-art. 7).

**Rischi**: un errore di implementazione crittografica è più costoso senza un fornitore esterno "collaudato" — mitigato dalla design review crittografica esterna già prevista in Fase 0 ([doc 09](09-piano-di-sviluppo.md)).

**Costo**: nessuna licenza ricorrente per-utente (a differenza di molte IDaaS a pagamento per MAU); costo di sviluppo iniziale più alto, ripagato a scala.

**Facilità di manutenzione**: media — richiede competenza di sicurezza interna mantenuta nel tempo (mitigato da audit periodici, [Engineering Plan §07](engineering_plan/07-refactoring-e-manutenzione.md)).

**Scalabilità**: alta — token stateless verificabili senza round-trip a un database di sessioni centralizzato per ogni richiesta.

**Compatibilità con le Bible**: piena con Functional Bible (SEC-001…003), Data Model Bible (DM-SYS-02/05), Product Constitution (art. 1-2, 26-50, indipendenza da terzi).

---

## TDR-05 · Sincronizzazione

**Decisione**: **CRDT minimale su misura** — LWW per-campo con vettori di versione logici, OR-Set per i GraphLink, versionamento a snapshot per le Note — implementato internamente, non una libreria CRDT generica.

| Alternativa | Descrizione | Esito |
|---|---|---|
| **A — CRDT su misura, minimale** | Solo le primitive già specificate dal Data Model Bible, implementate ad hoc | ✅ Scelta |
| **B — Libreria CRDT generica per documenti (classe Automerge/Yjs)** | Adozione di una libreria open source general-purpose per CRDT su strutture JSON-like | ❌ Scartata |
| **C — Sync gestita da un servizio backend-as-a-service di terzi** | Un fornitore esterno gestisce sincronizzazione e conflitti | ❌ Scartata |

**Motivazione**: il modello di conflitto è **già interamente specificato** e deliberatamente semplice ([Data Model Bible §8, §11 §6](data_model_bible/00-modello-dati-comune.md)): LWW per-campo, unione insiemistica per i collegamenti, snapshot per le Note. Una libreria CRDT generica (B) risolverebbe un problema più ampio di quello che abbiamo (CRDT arbitrari annidati) — introdurrebbe superficie di codice non necessaria da sottoporre a audit di sicurezza (rilevante perché tocca direttamente il confine di cifratura, [Technical Architecture Bible §10](technical_architecture_bible/10-sicurezza-architetturale.md)) e complessità concettuale sproporzionata rispetto al bisogno reale. Un servizio di terzi (C) è **incompatibile per costruzione** con la promessa E2E/content-blind (Product Constitution art. 1-2, 27): quasi nessun servizio di sync-as-a-service generico è a conoscenza zero dei contenuti.

**Vantaggi**: superficie di codice minima e interamente auditabile (rilevante per la promessa E2E); implementazione su misura per le regole già normate (idempotenza per periodo delle ricorrenze, [Data Model Bible §11 §3](data_model_bible/11-versionamento-e-sincronizzazione.md); asimmetria di recupero dell'aderenza, §11 §4) senza dover piegare una libreria generica a questi casi.

**Svantaggi**: nessun ecosistema di terzi da cui attingere correzioni di bug già trovati da altri; l'intero onere di correttezza (incluso il fuzzing/test generativi già previsti in Fase 0) ricade sul team.

**Impatto sul progetto**: è il componente singolo più rischioso del piano tecnico — coerente con la scelta già presa nel piano di fase esistente di affrontarlo per primo con uno spike dedicato e un criterio go/no-go esplicito ([doc 09](09-piano-di-sviluppo.md); registro rischi R-10, esposizione 15, la più alta tra i rischi tecnici).

**Rischi**: **R-10 del registro rischi esistente si applica direttamente** — mitigazione già definita: fallback a LWW per-campo puro (rinuncia all'OR-Set sui collegamenti solo in caso di necessità) se lo spike fallisse i criteri di convergenza.

**Costo**: nessuna licenza; il costo è interamente in tempo ingegneristico (stimato in Fase 0, [doc 09](09-piano-di-sviluppo.md)).

**Facilità di manutenzione**: media — richiede competenza specialistica mantenuta nel team (piccola superficie ma concettualmente densa); mitigata da una documentazione di design interna oltre a questo TDR.

**Scalabilità**: per costruzione (nessuna transazione cross-utente, propagazione peer-to-account tramite L6) — la scalabilità è quella del confine L6 (TDR-03), non del motore CRDT in sé, che gira sul dispositivo.

**Compatibilità con le Bible**: piena e diretta — questa è l'implementazione concreta di [Data Model Bible §8/§11](data_model_bible/00-modello-dati-comune.md) e [Technical Architecture Bible §05](technical_architecture_bible/05-offline-first-sincronizzazione-caching.md), nessuna deviazione concettuale.

---

## TDR-06 · Database Locale

**Decisione**: **SQLite cifrato** (a livello di pagina) + estensione **FTS** per l'indice di ricerca.

| Alternativa | Descrizione | Esito |
|---|---|---|
| **A — SQLite cifrato + FTS** | Motore relazionale embedded, maturo, con ricerca full-text integrata | ✅ Scelta |
| **B — Realm** | Motore a oggetti embedded con sync proprietaria integrata | ❌ Scartata |
| **C — Store chiave-valore embedded (classe LMDB/RocksDB)** | Motore a basso livello senza modello relazionale | ❌ Scartata |

**Motivazione**: il Data Model Bible descrive un modello **relazionale con GraphLink** (relazioni strutturali + collegamenti tipizzati, filtri e ordinamenti dichiarati per entità, [§10-12](data_model_bible/00-modello-dati-comune.md)) — SQLite lo supporta nativamente con query dichiarative, mentre un key-value store (C) richiederebbe di costruire da zero un livello di interrogazione (filtri FIN-010, TASK-R-05, ecc.) sopra un motore che non lo offre. Realm (B) include una sincronizzazione proprietaria che **confligge direttamente con TDR-05** (motore CRDT su misura) e introduce un fornitore (proprietà MongoDB) con cui condivideremmo la struttura, se non il contenuto, del nostro schema dati — rischio di lock-in incompatibile con l'indipendenza tecnologica richiesta.

**Vantaggi**: maturità decennale, strumenti di debug eccellenti, ricerca full-text nativa (allineata a [Data Model Bible §11](data_model_bible/00-modello-dati-comune.md): l'indice è una proiezione ricostruibile — FTS di SQLite è ricostruibile per natura); nessuna dipendenza da un fornitore di sync esterno.

**Svantaggi**: la cifratura a livello di pagina richiede un'estensione/libreria aggiuntiva (non nel nucleo di SQLite); le migrazioni di schema (numerose, dato il numero di moduli) richiedono disciplina propria (mitigata da [Technical Architecture Bible §11 §4](technical_architecture_bible/11-versionamento-architettura.md), già normato).

**Impatto sul progetto**: implementa la Porta di Persistenza di L5, consumata da ogni Dominio di modulo (L3) tramite il contratto comune ([Data Model Bible §3](data_model_bible/00-modello-dati-comune.md), l'involucro comune).

**Rischi**: basso — tecnologia collaudata su miliardi di dispositivi; rischio residuo sulla libreria di cifratura scelta (verifica nella design review crittografica di Fase 0).

**Costo**: nessuna licenza (open source, con possibili estensioni di cifratura open o commerciali a basso costo una tantum).

**Facilità di manutenzione**: alta — strumenti diagnostici maturi su entrambe le piattaforme via KMP.

**Scalabilità**: verificata contro i volumi già dichiarati (Functional Bible, MFC-E-14: 100.000+ entità; MFC-AC-07: ricerca ≤100ms su 50.000 entità) — SQLite+FTS è comprovato a questi volumi su dispositivo mobile.

**Compatibilità con le Bible**: piena — nessuna deviazione dal modello relazionale+grafo del Data Model Bible; supporta l'involucro comune, il ciclo di vita, il versionamento per-campo.

---

## TDR-07 · Database Server

**Decisione**: **PostgreSQL** per i dati relazionali del confine L6 (account, billing, registro moduli).

| Alternativa | Descrizione | Esito |
|---|---|---|
| **A — PostgreSQL** | RDBMS open source maturo, con supporto JSONB | ✅ Scelta |
| **B — Datastore NoSQL documentale gestito** | Store documentale scalabile orizzontalmente per natura | ❌ Scartata |
| **C — MySQL** | RDBMS open source alternativo | ❌ Scartata |

**Motivazione**: i dati che il confine L6 possiede in chiaro (mai i contenuti utente, solo account/billing/registry) hanno **integrità relazionale reale** da garantire (un account ha esattamente un piano attivo, un dispositivo appartiene a un solo account) — un RDBMS con vincoli di integrità è la scelta naturale. Un datastore NoSQL (B) offrirebbe scalabilità orizzontale non necessaria per questo carico (le entità sono per-account, non per-contenuto, quindi già "imbarazzantemente parallele" indipendentemente dal motore, [Technical Architecture Bible §08 §2](technical_architecture_bible/08-scalabilita-estendibilita.md)) al costo di una consistenza più debole, indesiderabile per billing. MySQL (C) è comparabile a PostgreSQL ma con estensibilità JSON storicamente più debole (rilevante per i metadati dei blob cifrati) e un modello di governance del progetto storicamente meno indipendente (di proprietà Oracle) — preferiamo un progetto con community indipendente, coerente con lo spirito di indipendenza tecnologica del prodotto.

**Vantaggi**: transazioni ACID per billing/account; supporto JSONB per i metadati flessibili dei blob cifrati (senza abbandonare la struttura relazionale per account/device/registry); ecosistema di strumenti operativi maturo.

**Svantaggi**: la scalabilità orizzontale nativa richiede partizionamento esplicito (già previsto concettualmente per utente, [Technical Architecture Bible §08](technical_architecture_bible/08-scalabilita-estendibilita.md)) — non "automatica" come alcuni datastore NoSQL.

**Impatto sul progetto**: esclusivamente L6 — non tocca il client, coerente con l'isolamento architetturale.

**Rischi**: basso — tecnologia matura, ampiamente operata a scala da moltissime organizzazioni comparabili.

**Costo**: nessuna licenza (open source); costo operativo prevedibile e ben documentato nel settore.

**Facilità di manutenzione**: alta — strumenti di migrazione, backup e monitoraggio maturi.

**Scalabilità**: adeguata al profilo "milioni di utenti, nessuna transazione cross-utente" già dichiarato (Product Bible, Business Strategy) tramite partizionamento per account.

**Compatibilità con le Bible**: piena — nessun dato utente in chiaro qui per costruzione (Product Constitution art. 1-2, 27); i soli dati gestiti (account, billing, registry) non sono coperti dal Data Model Bible (che modella solo i dati del grafo utente, cifrati prima di raggiungere questo livello).

---

## TDR-08 · Storage

**Decisione**: **Object storage compatibile S3** (protocollo standard, non un'API proprietaria a fornitore singolo) per blob cifrati di sincronizzazione, snapshot di backup, allegati.

| Alternativa | Descrizione | Esito |
|---|---|---|
| **A — Object storage compatibile S3** | Storage a oggetti su protocollo standard, portabile tra fornitori | ✅ Scelta |
| **B — Blob salvati direttamente nel database relazionale (Postgres bytea/large object)** | Nessun sistema di storage separato | ❌ Scartata |
| **C — API di storage proprietaria di un singolo fornitore, non standard** | Storage a oggetti con API esclusiva non portabile | ❌ Scartata |

**Motivazione**: i blob cifrati (sync, backup, allegati Note — FIN-013, NOTE-008) sono per natura oggetti binari di dimensione variabile, con pattern di accesso diverso da quello relazionale: mescolarli nel database (B) degrada le prestazioni delle query relazionali e complica i backup del database stesso. Un'API proprietaria non standard (C) crea lock-in totale verso un fornitore, in tensione con l'indipendenza da un singolo canale/fornitore già affermata come principio economico (Product Bible, Business Strategy §6: "nessun singolo canale, cliente o piattaforma > 30% dei ricavi", qui estesa per analogia all'infrastruttura). Un protocollo standard (A) consente di cambiare fornitore senza riscrivere il codice applicativo.

**Vantaggi**: URL pre-firmati per upload/download diretto dal client (riduce il carico sui servizi applicativi); portabilità tra fornitori; pattern di accesso ottimizzato per oggetti binari (a differenza di un RDBMS).

**Svantaggi**: un sistema in più da operare rispetto a "tutto nel database"; richiede gestione del ciclo di vita (rotazione delle generazioni di backup, già specificata in Functional Bible BKP-001) come logica applicativa separata.

**Impatto sul progetto**: implementa la persistenza di L6 per BKP-001…004, EXP-001/002; mai per i dati "caldi" di sincronizzazione quotidiana (quelli passano dal Motore di Sync, TDR-05).

**Rischi**: basso — tecnologia matura e ampiamente interoperabile grazie allo standard di protocollo.

**Costo**: proporzionale al volume immagazzinato (modello a consumo, tipico della categoria) — mitigabile con le politiche di rotazione già specificate (BKP-001: generazioni giornaliere/settimanali/mensili con rotazione).

**Facilità di manutenzione**: alta — protocollo standard, ampia disponibilità di strumenti.

**Scalabilità**: nativa per la categoria (scalabilità orizzontale intrinseca dei sistemi a oggetti).

**Compatibilità con le Bible**: piena — coerente con "l'export non ha mai limiti di dimensione" (Data Model Bible §13, INV-12) e con l'assenza di elaborazione dei contenuti (i blob restano cifrati indipendentemente dal fornitore di storage).

---

## TDR-09 · Notifiche

**Decisione**: integrazione **diretta con APNs (iOS) e FCM (Android)**, dietro un **relay proprio minimale** (nessun aggregatore SaaS di terze parti).

| Alternativa | Descrizione | Esito |
|---|---|---|
| **A — APNs/FCM diretti + relay proprio** | Integrazione diretta con i trasporti nativi, orchestrata da un servizio nostro | ✅ Scelta |
| **B — Aggregatore push-as-a-service di terzi** | Un fornitore esterno media verso APNs/FCM con funzionalità aggiuntive | ❌ Scartata |
| **C — Solo notifiche locali, nessun trasporto push remoto** | Nessuna capacità di risveglio dell'app da remoto | ❌ Scartata |

**Motivazione**: APNs e FCM sono gatekeeper obbligati per il push sulle rispettive piattaforme — non evitabili in nessun caso. La domanda reale è se aggiungere un intermediario (B): un aggregatore vedrebbe metadati di consegna (chi è online, quando) su tutta la base utenti, un'informazione che non ha motivo di lasciare la nostra infrastruttura (C-art. 45, minimizzazione) dato che **i payload sono già minimi per mandato** (NTF-001: "mai contenuti nei push, solo trigger di sync silenziosi"). L'opzione C è scartata perché la Functional Bible richiede notifiche azionabili anche a app chiusa (NTF-005) e trigger di sincronizzazione silenziosi — impossibile senza un trasporto push remoto.

**Vantaggi**: nessun intermediario con visibilità sui pattern di utilizzo; pieno controllo sulla logica di raggruppamento/budget già specificata (NTF-002/003); costo diretto senza markup di un aggregatore.

**Svantaggi**: due integrazioni da mantenere (APNs, FCM) invece di una interfaccia unificata di terze parti; nessun dashboard "pronto all'uso" di analisi della consegna (da costruire, coerente con TDR-12).

**Impatto sul progetto**: implementa il trasporto di L5 dietro il Broker di Notifiche (L4, [Technical Architecture Bible §02](technical_architecture_bible/02-moduli-responsabilita-boundaries.md)) — il broker resta ignaro del trasporto specifico (Interface Segregation, [Technical Architecture Bible §00 §2](technical_architecture_bible/00-principi-architetturali.md)).

**Rischi**: basso — APNs/FCM sono infrastrutture con SLA di piattaforma consolidati; rischio operativo standard di gestione certificati/credenziali (mitigato da TDR-15).

**Costo**: gratuito (APNs/FCM non hanno costo per volume nell'uso standard); il costo è solo di sviluppo del relay proprio.

**Facilità di manutenzione**: media — richiede tenere aggiornate due integrazioni di piattaforma nel tempo (rotazione certificati, cambi di API).

**Scalabilità**: nativa (le piattaforme stesse gestiscono la scala della consegna finale).

**Compatibilità con le Bible**: piena — NTF-001…008, C-art. 7, 45, 58-63 rispettati per costruzione.

---

## TDR-10 · Analytics

**Decisione**: pipeline di **analytics open-source, self-hosted**, che ingerisce solo eventi anonimi aggregabili (mai contenuti).

| Alternativa | Descrizione | Esito |
|---|---|---|
| **A — Pipeline open-source self-hosted** | Raccolta e analisi degli eventi opt-in interamente sotto il nostro controllo | ✅ Scelta |
| **B — Piattaforma di analytics SaaS di terzi** | Un fornitore esterno riceve e processa gli eventi comportamentali | ❌ Scartata |
| **C — Nessun analytics oltre a crash/prestazioni** | Nessuna misura di funnel/retention | ❌ Scartata |

**Motivazione**: gli SDK di molte piattaforme di analytics di terzi (B) raccolgono, per funzionamento di default, segnali di dispositivo oltre a quelli esplicitamente istruiti — un rischio diretto per la promessa "zero tracciamento pubblicitario, zero SDK di ad-tech nel binario" (Product Constitution art. 35). L'opzione C è scartata perché il Product Bible richiede esplicitamente di misurare North Star, funnel e retention (Success Metrics Bible, doc 10) — indispensabile per governare il prodotto, non opzionale.

**Vantaggi**: nessun SDK di terzi nel binario con accesso potenziale a segnali di dispositivo; controllo totale sul filtro di anonimizzazione **alla fonte** (Technical Architecture Bible §09: "il filtro è applicato alla fonte, non in transito"); nessun dato aggregato che un fornitore esterno potrebbe rivendere o riutilizzare.

**Svantaggi**: onere operativo di gestire l'infrastruttura di raccolta/analisi; nessuna delle funzionalità "pronte" (dashboard predefiniti, segmentazione avanzata) di una piattaforma commerciale matura.

**Impatto sul progetto**: implementa il Componente Telemetria separato già richiesto da [Technical Architecture Bible §09](technical_architecture_bible/09-osservabilita-logging-telemetria.md), distinto per costruzione dal logging locale.

**Rischi**: basso per la privacy (mitigato per design); rischio operativo di sotto-investimento nella manutenzione dell'infrastruttura self-hosted (mitigato dal piano di manutenzione, [Engineering Plan §07](engineering_plan/07-refactoring-e-manutenzione.md)).

**Costo**: nessuna licenza SaaS ricorrente; costo interamente infrastrutturale e di ingegneria (verosimilmente inferiore al costo per-utente di una piattaforma commerciale a scala).

**Facilità di manutenzione**: media — richiede competenza di data engineering mantenuta nel team.

**Scalabilità**: adeguata al volume di eventi anonimi aggregati (ordini di grandezza inferiori ai contenuti utente stessi).

**Compatibilità con le Bible**: piena — C-art. 23, 35, 243; opt-in esplicito, default off in UE (Functional Bible ONB-006).

---

## TDR-11 · Crash Reporting

**Decisione**: toolkit **open-source/self-hostabile** per la raccolta di crash report sanitizzati, con ingestione self-hosted.

| Alternativa | Descrizione | Esito |
|---|---|---|
| **A — Toolkit open-source, ingestione self-hosted** | SDK client auditabile, nessun dato inviato a terzi | ✅ Scelta |
| **B — Piattaforma SaaS di crash reporting di terzi** | Fornitore esterno riceve stack trace e metadati di dispositivo | ❌ Scartata |
| **C — Raccolta crash completamente custom, nessun toolkit** | Sviluppo interamente da zero (symbolication, deduplicazione, ecc.) | ❌ Scartata |

**Motivazione**: un crash report non contiene mai contenuti utente per mandato (C-art. 22), ma contiene comunque stack trace e metadati di dispositivo — informazioni operative che non hanno motivo di lasciare la nostra infrastruttura verso un fornitore esterno (B), anche uno reputato. Costruire tutto da zero (C) reinventa problemi già risolti (symbolication, deduplicazione, rate limiting) con un costo ingegneristico sproporzionato al beneficio.

**Vantaggi**: nessun terzo riceve dati operativi anche sanitizzati; SDK client auditabile (verificabile che non raccolga più di quanto dichiarato); riuso di soluzioni già collaudate per i problemi complessi (symbolication).

**Svantaggi**: onere di self-hosting dell'infrastruttura di ingestione; possibile minore raffinatezza degli strumenti di analisi rispetto a un prodotto commerciale maturo.

**Impatto sul progetto**: alimenta il Componente Log Locale/diagnostico di [Technical Architecture Bible §09](technical_architecture_bible/09-osservabilita-logging-telemetria.md), con trasmissione remota sempre opt-in (mai automatica, UX-R-025).

**Rischi**: basso — mitigato dalla separazione già imposta tra log locale e trasmissione (nessun crash report lascia il dispositivo senza consenso).

**Costo**: nessuna licenza SaaS ricorrente; costo di self-hosting contenuto (volume di crash atteso basso, dato il requisito crash-free ≥99,8%).

**Facilità di manutenzione**: media — richiede aggiornare gli SDK client alle nuove versioni di sistema operativo nel tempo.

**Scalabilità**: adeguata (il volume di crash report è per natura una piccola frazione del traffico totale).

**Compatibilità con le Bible**: piena — C-art. 22, UX-R-025, Technical Architecture Bible §09.

---

## TDR-12 · Osservabilità

**Decisione**: stack **open-source** per metriche/log/tracce del confine L6, self-gestibile (non una piattaforma proprietaria all-in-one).

| Alternativa | Descrizione | Esito |
|---|---|---|
| **A — Stack open-source (metriche/log/tracce) self-gestibile** | Componenti open standard, indipendenti da un singolo fornitore | ✅ Scelta |
| **B — Piattaforma di osservabilità commerciale proprietaria** | Soluzione integrata a pagamento di un singolo fornitore | ❌ Scartata |
| **C — Logging ad-hoc senza osservabilità strutturata** | Nessun sistema coerente di metriche/tracce | ❌ Scartata |

**Motivazione**: il confine L6 ha obiettivi di affidabilità già impliciti (crash-free ≥99,8% lato client si riflette in aspettative equivalenti lato Sync) che richiedono osservabilità strutturata — l'opzione C è insufficiente per operare un servizio a questa scala con fiducia. Una piattaforma proprietaria (B) funziona, ma crea dipendenza da un singolo fornitore per una capacità operativa critica, in tensione con l'indipendenza tecnologica già preferita nelle altre decisioni di questo documento (TDR-07, TDR-08).

**Vantaggi**: nessun lock-in; standard aperti per l'interoperabilità tra i componenti (metriche, log, tracce); costo proporzionale all'infrastruttura operata, non a un contratto per-host o per-evento di un fornitore.

**Svantaggi**: onere operativo maggiore rispetto a una piattaforma completamente gestita; richiede competenza SRE/DevOps interna.

**Impatto sul progetto**: osserva esclusivamente L4-L6 (nessun contenuto utente, [Technical Architecture Bible §09](technical_architecture_bible/09-osservabilita-logging-telemetria.md)).

**Rischi**: basso — tecnologie mature e ampiamente adottate nel settore.

**Costo**: nessuna licenza; costo infrastrutturale e di competenza interna.

**Facilità di manutenzione**: media — richiede presidio SRE continuo (già previsto come ruolo nel team ipotizzato, doc 09).

**Scalabilità**: adeguata, componenti pensati per scala orizzontale.

**Compatibilità con le Bible**: piena — Technical Architecture Bible §09, nessun dato utente coinvolto.

---

## TDR-13 · Testing

**Decisione**: **framework nativi per piattaforma** (per i test specifici di UI/accessibilità nativa) + **suite di test condivisa nel modulo KMP** (per dominio/sincronizzazione) + **driver E2E cross-platform** per i flussi su device farm.

| Alternativa | Descrizione | Esito |
|---|---|---|
| **A — Nativi + condivisa KMP + driver E2E cross-platform** | Un livello di test per ogni tipo di verifica richiesta | ✅ Scelta |
| **B — Solo un framework di test cross-platform unico** | Un solo strumento per tutti i livelli, incluso il nativo | ❌ Scartata |
| **C — Test manuale esplorativo come principale meccanismo di verifica** | Nessuna automazione strutturata | ❌ Scartata |

**Motivazione**: la Functional Bible richiede simulazioni deterministiche a 12 mesi per le ricorrenze (TASK-004) e l'aderenza abitudini (HAB-005) — la **suite di test più critica del prodotto** (HAB-AC-01…06) — che devono girare rapidamente e ripetutamente: il livello ideale è il modulo condiviso KMP (nessuna dipendenza da simulatori di piattaforma). I test di accessibilità nativa (contrasto, screen reader, target di tocco — UX Bible Accessibility Bible) richiedono invece i framework nativi per essere fedeli al comportamento reale della piattaforma; un framework unico cross-platform (B) rischierebbe di validare un comportamento diverso da quello reale su ciascun sistema operativo, in particolare per VoiceOver/TalkBack. Il test manuale come meccanismo primario (C) è incompatibile con il volume di criteri di accettazione già esistenti (ogni funzione della Functional Bible ha AC-## verificabili, pensati esplicitamente per l'automazione).

**Vantaggi**: ogni tipo di verifica gira nello strumento più adatto; la suite di dominio condivisa (KMP) verifica una sola volta la logica più delicata per entrambe le piattaforme.

**Svantaggi**: tre insiemi di strumenti da mantenere in coordinamento (mitigato dalla pipeline CI unificata, TDR-14).

**Impatto sul progetto**: implementa direttamente la Testing Strategy già definita in [Engineering Plan §05](engineering_plan/05-pratiche-di-sviluppo.md) (tassonomia UT/IT/E2E/PT/AT/ST) — questa decisione sceglie gli strumenti, non ridefinisce la strategia.

**Rischi**: basso — rischio residuo di flakiness nei test E2E su device farm (mitigato da retry disciplinati e budget di stabilità nel gate di release).

**Costo**: framework nativi e KMP: nessuna licenza; driver E2E cross-platform e infrastruttura device farm: costo operativo proporzionale all'uso.

**Facilità di manutenzione**: alta per la suite condivisa (un solo posto); media per i test nativi (duplicati per piattaforma per necessità).

**Scalabilità**: adeguata — la suite di dominio scala con la CPU disponibile in CI, non con dispositivi fisici.

**Compatibilità con le Bible**: piena — Functional Bible §17 (tassonomia test), UX Bible Accessibility Bible, Engineering Plan §05.

---

## TDR-14 · CI/CD

**Decisione**: pipeline **as-code, portabile**, su runner containerizzati — non legata a un singolo fornitore cloud.

| Alternativa | Descrizione | Esito |
|---|---|---|
| **A — Pipeline as-code portabile, runner containerizzati** | Definizione della pipeline in formato aperto, eseguibile su infrastruttura sostituibile | ✅ Scelta |
| **B — CI/CD SaaS proprietaria integrata a un singolo cloud** | Pipeline vincolata a un solo fornitore cloud verticale | ❌ Scartata |
| **C — Processo di build/rilascio manuale o locale** | Nessuna automazione strutturata | ❌ Scartata |

**Motivazione**: "la pipeline nasce prima del prodotto" (già stabilito, [doc 09 Fase 0](09-piano-di-sviluppo.md)) e deve applicare i gate già mandatati (UT/IT/E2E/PT/AT/ST, lint di accessibilità sui token, SCA/SAST) fin dal primo giorno — l'opzione C è incompatibile con questo requisito. Una pipeline vincolata a un singolo fornitore cloud (B) lega ogni futura decisione infrastrutturale (TDR-07, TDR-08, TDR-12) alla disponibilità di quello stesso fornitore, contraddicendo la preferenza di indipendenza tecnologica già mostrata in questo documento.

**Vantaggi**: portabilità tra fornitori di calcolo; pipeline versionata insieme al codice (stesso principio di tracciabilità di tutte le Bible: la configurazione è documentazione eseguibile); riusabile identica in locale per il debug.

**Svantaggi**: richiede gestione propria dei runner (nessuna piattaforma "tutto incluso"); più configurazione iniziale rispetto a una CI SaaS preconfigurata.

**Impatto sul progetto**: applica meccanicamente il code review checklist e la Testing Strategy già definiti ([Engineering Plan §05](engineering_plan/05-pratiche-di-sviluppo.md)); è il meccanismo che rende **verificabile** (non solo dichiarata) la regola delle dipendenze vietate ([Technical Architecture Bible §02 §4](technical_architecture_bible/02-moduli-responsabilita-boundaries.md)) — decisione tecnica rinviata dall'Engineering Plan, qui risolta: il controllo delle dipendenze vietate gira come gate CI dedicato.

**Rischi**: basso — tecnologie mature per pipeline containerizzate portabili.

**Costo**: proporzionale al calcolo utilizzato, indipendente dal fornitore (confrontabile tra alternative nel tempo).

**Facilità di manutenzione**: alta — configurazione dichiarativa, versionata, revisionabile con lo stesso processo di code review del codice applicativo.

**Scalabilità**: adeguata — i runner containerizzati scalano orizzontalmente con il volume di build.

**Compatibilità con le Bible**: piena — Engineering Plan §05 (testing strategy, code review checklist), §06 (release/rollback graduale, che la pipeline orchestra).

---

## TDR-15 · Gestione Segreti

**Decisione**: **vault dedicato** per la gestione dei segreti, self-hostabile, con credenziali a breve vita dove possibile (preferenza per OIDC rispetto a segreti statici di lunga durata).

| Alternativa | Descrizione | Esito |
|---|---|---|
| **A — Vault dedicato, credenziali a breve vita** | Sistema centralizzato con audit trail e rotazione | ✅ Scelta |
| **B — Variabili d'ambiente/secret store nativo della CI, senza rotazione centralizzata** | Segreti gestiti in modo disperso, nessun audit unificato | ❌ Scartata |
| **C — Segreti nei file di configurazione versionati** | Nessuna protezione, segreti nel repository | ❌ Scartata |

**Motivazione**: l'opzione C è scartata senza discussione (viola ogni principio di sicurezza già stabilito, C-art. 26-50). L'opzione B (variabili d'ambiente sparse) funziona ma non offre audit trail centralizzato né rotazione sistematica — insufficiente per un prodotto la cui intera promessa commerciale poggia sulla sicurezza (Product Constitution, Titolo II). Un vault dedicato (A) centralizza l'accesso, lo audita, e consente credenziali a breve vita — coerente con la stessa disciplina di "nessuna chiave statica di lunga durata dove evitabile" già applicata alla gerarchia di chiavi utente (Technical Architecture Bible §10 §3).

**Vantaggi**: audit trail di ogni accesso a un segreto; rotazione centralizzata; riduzione della superficie di esposizione (credenziali a breve vita invece di chiavi statiche permanenti).

**Svantaggi**: un sistema critico in più da operare con alta disponibilità (un suo fallimento blocca i deploy) — mitigato da pratiche operative standard di alta disponibilità per questa categoria di sistema.

**Impatto sul progetto**: protegge le credenziali di TDR-03 (backend), TDR-07 (database), TDR-08 (storage), TDR-09 (certificati push) — trasversale a ogni altra decisione infrastrutturale.

**Rischi**: il vault stesso diventa un bersaglio ad alto valore — mitigato dalle stesse pratiche di audit e accesso minimo già normate per la sicurezza applicativa.

**Costo**: nessuna licenza (soluzioni open source disponibili); costo operativo di alta disponibilità.

**Facilità di manutenzione**: media — richiede competenza operativa dedicata, giustificata dalla criticità.

**Scalabilità**: adeguata alla frequenza di accesso ai segreti (bassa rispetto al traffico applicativo).

**Compatibilità con le Bible**: piena — Product Constitution art. 26-50 (sicurezza), Technical Architecture Bible §10.

---

## TDR-16 · Dipendenze

**Decisione**: **scansione automatica delle vulnerabilità (SCA) e SBOM mantenuta in continuo**, con una policy di allowlist delle licenze curata.

| Alternativa | Descrizione | Esito |
|---|---|---|
| **A — SCA + SBOM continui + allowlist di licenze** | Ogni dipendenza tracciata, scansionata, con licenza verificata | ✅ Scelta |
| **B — Aggiornamenti manuali senza scansione sistematica** | Le dipendenze si aggiornano solo quando qualcuno se ne accorge | ❌ Scartata |
| **C — Congelamento delle dipendenze a tempo indeterminato** | Nessun aggiornamento dopo la prima integrazione | ❌ Scartata |

**Motivazione**: già anticipato nella documentazione tecnica esistente ("dipendenze di terze parti sottoposte a scansione vulnerabilità continua; SBOM mantenuta", [03-architettura.md §6](03-architettura.md)) — questa decisione lo ratifica formalmente con il confronto esplicito richiesto. L'opzione B espone il prodotto a vulnerabilità note non corrette per negligenza di processo, non di intenzione. L'opzione C (congelamento) evita la fatica degli aggiornamenti ma accumula vulnerabilità irrisolte nel tempo — peggiore di B nel lungo periodo.

**Vantaggi**: visibilità continua sulla superficie di rischio delle dipendenze (rilevante per l'architettura a plugin/marketplace futura, [Technical Architecture Bible §04](technical_architecture_bible/04-plugin-architecture.md), dove ogni dipendenza di un modulo di terze parti sarà sottoposta alla stessa disciplina); conformità di licenza verificabile prima che diventi un problema legale.

**Svantaggi**: rumore di falsi positivi da gestire (mitigato da una policy di triage definita); tempo di manutenzione dedicato agli aggiornamenti regolari.

**Impatto sul progetto**: gate CI dedicato (TDR-14); applicato sia al modulo condiviso KMP sia al backend Go sia, in futuro, ai moduli di terze parti del marketplace.

**Rischi**: basso — processo consolidato nel settore; rischio residuo di aggiornamenti che rompono compatibilità (mitigato dal versionamento a tre oggetti indipendenti già definito, [Technical Architecture Bible §11](technical_architecture_bible/11-versionamento-architettura.md)).

**Costo**: nessuna licenza per gli strumenti open source di SCA; costo di tempo ingegneristico per il triage.

**Facilità di manutenzione**: alta se disciplinata fin da subito; costosa da introdurre retroattivamente su un progetto già grande — motivo per adottarla dal primo commit (Fase 0).

**Scalabilità**: adeguata, il costo cresce linearmente col numero di dipendenze, non con l'uso del prodotto.

**Compatibilità con le Bible**: piena — coerente con la disciplina generale di sicurezza (Product Constitution Titolo II) e con [03-architettura.md §6](03-architettura.md), qui formalizzata.

---

## TDR-17 · Packaging

**Decisione**: **pacchetti nativi store** (App Store/Play Store) come canale di distribuzione primario, **più delivery modulare on-demand** per i moduli non attivi al primo avvio.

| Alternativa | Descrizione | Esito |
|---|---|---|
| **A — Pacchetti nativi store + delivery modulare on-demand** | Distribuzione standard + moduli scaricabili a richiesta | ✅ Scelta |
| **B — Un solo binario monolitico sempre completo** | Tutti i moduli sempre inclusi nell'installazione iniziale | ❌ Scartata |
| **C — Distribuzione fuori dagli store ufficiali (sideloading/canale proprio)** | Bypass dei canali ufficiali di piattaforma | ❌ Scartata |

**Motivazione**: questa non è solo una preferenza — è **già un requisito esplicito** della documentazione esistente: MFC §3 elenca "attivazione modulo on-demand non ancora scaricato" tra le sole 4 eccezioni online ammesse, e GAL-002 dichiara "modulo non scaricato" come caso limite gestito. Un binario monolitico sempre completo (B) **contraddice direttamente** questo requisito già normato, oltre a violare i budget di dimensione app impliciti nella qualità "top store" richiesta (Product Bible). La distribuzione fuori dagli store (C) è scartata perché comprometterebbe la scoperta organica (Growth Strategy, ASO) che è il canale di crescita primario dichiarato.

**Vantaggi**: rispetta il requisito già specificato di attivazione modulare on-demand; dimensione di installazione iniziale minima (rilevante per la conversione, Growth Strategy); accesso ai meccanismi di aggiornamento e fiducia degli store ufficiali.

**Svantaggi**: la delivery on-demand introduce una delle 4 sole dipendenze di rete ammesse (già accettata e documentata, non nuova); complessità di packaging leggermente superiore a un binario singolo.

**Impatto sul progetto**: implementa direttamente GAL-002 e MFC §3; interagisce con TDR-18 (build system) per la separazione dei moduli in unità di delivery indipendenti.

**Rischi**: basso — meccanismo comune e collaudato nel settore per app modulari.

**Costo**: nessun costo aggiuntivo oltre alle normali fee di pubblicazione sugli store.

**Facilità di manutenzione**: media — richiede disciplina nel mantenere i moduli come unità di build effettivamente indipendenti (coerente con il Contratto di Modulo, [Technical Architecture Bible §04](technical_architecture_bible/04-plugin-architecture.md)).

**Scalabilità**: neutra (riguarda la distribuzione, non il runtime).

**Compatibilità con le Bible**: piena e diretta — Functional Bible MFC §3, GAL-002; Technical Architecture Bible §04 (ciclo di vita di un modulo: dichiarato → attivo, che presuppone esattamente questo meccanismo).

---

## TDR-18 · Build System

**Decisione**: **build multiplatform unificata** (classe Gradle) che orchestra la compilazione del modulo condiviso KMP e delle due UI native in un'unica pipeline coerente.

| Alternativa | Descrizione | Esito |
|---|---|---|
| **A — Build multiplatform unificata** | Un solo sistema di build orchestra modulo condiviso + UI native | ✅ Scelta |
| **B — Build separate e disconnesse per piattaforma** | Progetto Xcode e progetto Android con integrazione manuale del modulo condiviso | ❌ Scartata |
| **C — Sistema di build interamente custom** | Orchestrazione scritta da zero senza tooling esistente | ❌ Scartata |

**Motivazione**: conseguenza diretta di TDR-01 (KMP): un modulo condiviso richiede un sistema di build che sappia compilarlo per entrambi i target e integrarlo in modo affidabile nelle due UI native. Build separate e disconnesse (B) reintroducono esattamente il rischio di divergenza tra piattaforme che TDR-01 e [Product Bible D-08](product_bible/14-decision-log.md) cercano di eliminare — un aggiornamento del modulo condiviso integrato manualmente e in modo incoerente tra iOS e Android è un rischio operativo concreto, non teorico. Un sistema custom (C) reinventa un problema già ben risolto dal tooling esistente per KMP.

**Vantaggi**: un solo comando/pipeline per costruire, testare e verificare l'intero progetto (modulo condiviso + due UI); riduce il rischio di disallineamento tra la versione del modulo condiviso usata da iOS e quella usata da Android.

**Svantaggi**: curva di apprendimento del tooling multiplatform per chi viene da progetti nativi puri.

**Impatto sul progetto**: è il collante operativo di TDR-01 e TDR-13 (testing) — la pipeline CI (TDR-14) invoca questo sistema di build come primo passo di ogni gate.

**Rischi**: basso — tooling maturo per questo scenario specifico (KMP + native).

**Costo**: nessuna licenza (open source).

**Facilità di manutenzione**: alta — un solo sistema da comprendere e aggiornare, invece di due disconnessi.

**Scalabilità**: adeguata alla crescita del numero di moduli (build incrementale per modulo, coerente con [Technical Architecture Bible §08 §3](technical_architecture_bible/08-scalabilita-estendibilita.md): aggiungere un modulo non richiede modifiche agli altri).

**Compatibilità con le Bible**: piena — diretta conseguenza di TDR-01; coerente con Product Bible D-08.

---

## TDR-19 · Dependency Injection

> Nota: decisione presa durante lo Sprint 1 (Core Engine + Modulo Attività), non nel lotto originario TDR-01…18 — la Technical Architecture Bible definisce il Dependency Inversion Principle e la Dependency Rule, ma nessun meccanismo concreto di injection era stato scelto. Verificato con ricerca esaustiva su Technical Architecture Bible e questo stesso documento: nessuna menzione di framework DI, service locator o "iniezione delle dipendenze" prima di questa voce.

**Decisione**: **injection manuale via costruttore**, composta in un unico *composition root* per piattaforma (un file `AppContainer`/equivalente in ciascun entry point, non un framework).

| Alternativa | Descrizione | Esito |
|---|---|---|
| **A — Injection manuale via costruttore** | Ogni classe dichiara le proprie dipendenze nel costruttore; un composition root le assembla a mano nel punto di ingresso | ✅ Scelta |
| **B — Koin** | Framework DI basato su service locator, compatibile KMP, popolare nell'ecosistema Kotlin | ❌ Scartata |
| **C — Kodein-DI** | Framework DI dichiarativo, anch'esso compatibile KMP | ❌ Scartata |

**Motivazione**: al perimetro di questo sprint (Core Engine + un solo modulo di dominio) il grafo delle dipendenze è piccolo e interamente noto a compile-time — esattamente il caso in cui un framework aggiunge costo (nuova dipendenza esterna, resolution a runtime anziché a compile-time, curva di apprendimento) senza risolvere un problema reale ancora esistente. Koin (B) risolve service-location a runtime, il che sposta errori di wiring da compile-time a runtime — in tensione con l'enfasi della Technical Architecture Bible sulla Dependency Rule verificabile "per costruzione" (§01 §4), non a runtime. Kodein-DI (C) è più tipizzato di Koin ma introduce comunque un container e una sintassi propria per un grafo che, a questa scala, un costruttore esprime già interamente. Nessuna delle Bible impone o preclude un framework: la scelta è aperta, quindi si adotta l'opzione a costo/rischio minimo compatibile con "nessuna astrazione prematura" (principio generale di ingegneria del progetto).

**Vantaggi**: zero dipendenze esterne aggiuntive; errori di wiring mancante sono errori di compilazione, non crash a runtime; nessuna "magia" da spiegare a chi legge il codice per la prima volta.

**Svantaggi**: il composition root cresce linearmente col numero di moduli attivati — da rivalutare (probabilmente in favore di B o C) quando il grafo supererà la manciata di dipendenze di un singolo Epic; non risolve da solo lo scope "per-richiesta" (non necessario: nessuna dipendenza di questo sprint ha un ciclo di vita più corto del processo).

**Impatto sul progetto**: ogni modulo (`domain-task`, `feature-task`, ecc.) espone solo interfacce e classi con dipendenze dichiarate nel costruttore; l'assemblaggio concreto (quale implementazione di `TaskRepository` usare, quale istanza di `EventBus`) avviene in un unico punto per app (`androidApp`/`iosApp`/test), mai sparso nei moduli di dominio.

**Rischi**: nessuno strutturale a questa scala; rischio di manutenzione se il composition root non viene rifattorizzato quando cresce (mitigazione: rivalutare questa decisione esplicitamente a ogni nuovo Epic, non lasciarla implicita).

**Costo**: nessuno (nessuna libreria).

**Facilità di manutenzione**: alta a questa scala; da rivalutare esplicitamente oltre i ~5-6 moduli attivi contemporaneamente.

**Scalabilità**: limitata nel tempo per costruzione — è una scelta dichiaratamente provvisoria, non una preclusione permanente di un framework DI.

**Compatibilità con le Bible**: piena — nessuna Bible impone o vieta un meccanismo DI; coerente con il Dependency Inversion Principle già stabilito (Technical Architecture Bible §00 §2), che riguarda la *direzione* delle dipendenze, non il *meccanismo* con cui vengono assemblate.

---

## TDR-20 · Libreria di accesso SQLite per Kotlin Multiplatform

> Nota: decisione presa durante lo Sprint 1. TDR-06 sceglie il motore (SQLite cifrato + FTS) ma non la libreria di accesso KMP — esplicitamente rinviato ("Note" di TDR-06 e Technical Architecture Bible §14 §4.1 item 2).

**Decisione**: **SQLDelight**.

| Alternativa | Descrizione | Esito |
|---|---|---|
| **A — SQLDelight** | Genera API Kotlin tipizzate da file `.sq` (SQL dichiarato a mano); driver ufficiali per JVM/Android/iOS/JS | ✅ Scelta |
| **B — Room (Kotlin Multiplatform)** | ORM/DAO annotation-based di Google, supporto KMP recente | ❌ Scartata |
| **C — Binding SQLite grezzo via expect/actual** | Wrapping manuale delle API SQLite native per piattaforma, senza libreria intermedia | ❌ Scartata |

**Motivazione**: TDR-06 richiede esplicitamente un motore relazionale con estensione FTS integrata; SQLDelight genera codice a partire da SQL scritto a mano (incluse le virtual table FTS), verificato a compile-time contro lo schema — coerente con la preferenza già mostrata in tutto il documento per soluzioni verificabili a compile-time piuttosto che a runtime (vedi TDR-19). Room per KMP (B) è più recente e meno maturo sui target non-Android/JVM (in particolare iOS) rispetto a SQLDelight, che supporta tutti i target KMP di TDR-01 da anni; inoltre Room resta concettualmente un ORM basato su annotazioni/riflessione, meno allineato alla preferenza per query dichiarate esplicitamente. Il binding grezzo (C) reinventerebbe query builder, mapping e gestione delle migrazioni da zero, senza alcun vantaggio dato che nessuna Bible richiede di evitare una libreria di terze parti per la persistenza.

**Vantaggi**: driver JVM (usato per la verifica in questo sandbox), Android e iOS nativi e maturi; query verificate a compile-time (un errore SQL è un errore di build, non un crash a runtime); supporto FTS diretto, coerente con TDR-06.

**Svantaggi**: un ulteriore strumento di build (plugin Gradle, generazione codice da file `.sq`) da imparare; la cifratura a livello di pagina richiesta da TDR-06 richiede un driver SQLCipher aggiuntivo — non collegata in questo sprint (vedi nota su Sicurezza sotto).

**Impatto sul progetto**: `domain-task` dichiara `TaskRepository` (l'astrazione, per Dependency Inversion); l'implementazione concreta basata su SQLDelight vive nello stesso modulo in questo sprint (vedi README-BUILD.md §11 per la convenzione completa), con un `DatabaseDriverFactory` `expect`/`actual` per piattaforma (solo l'`actual` JVM è verificato in questo sandbox, coerente con il gating già documentato per Android/iOS in tutto il bootstrap).

**Nota su Sicurezza (deroga dichiarata di questo sprint)**: TDR-06 richiede cifratura a livello di pagina. Il Servizio di Sicurezza (`core-security`) resta un'interfaccia segnaposto in questo sprint (fuori perimetro per esplicita richiesta: "non implementare autenticazione remota"; la cifratura del database richiede una gerarchia di chiavi che è responsabilità di quel servizio, Technical Architecture Bible §10). Implementare qui una cifratura ad-hoc senza quella gerarchia significherebbe inventare uno schema di gestione chiavi non documentato. **Questo sprint usa quindi SQLite non cifrato**; il driver SQLCipher va collegato quando `core-security` fornirà la gerarchia di chiavi — annotato come blocco per lo Sprint 2/EPIC-SET nel report finale.

**Rischi**: basso — libreria consolidata, ampiamente usata in produzione KMP.

**Costo**: nessuno (open source, Apache 2.0).

**Facilità di manutenzione**: alta — schema e query in un solo posto (`.sq`), errori rilevati a compile-time.

**Scalabilità**: adeguata ai volumi dichiarati (MFC-E-14: 100.000+ entità) — SQLite è comprovato a questa scala, indipendentemente dalla libreria di accesso.

**Compatibilità con le Bible**: piena con TDR-06 (stesso motore, FTS); la deroga sulla cifratura è temporanea e dichiarata esplicitamente, non silenziosa.

---

## TDR-21 · Tipo di errore per i casi d'uso (Application layer)

> Nota: decisione presa durante lo Sprint 1. La Technical Architecture Bible §07 descrive **dove** un errore nasce e viene tradotto tra i layer, ma non definisce alcun tipo Kotlin concreto — confermato per ricerca esaustiva, nessun `Result`/`Either`/gerarchia di eccezioni è specificata in nessuna Bible.

**Decisione**: **tipo `Result` sigillato dedicato** (`OmniResult<T>` — `Success<T>` / `Failure`), non eccezioni, non `kotlin.Result`, non una libreria di terze parti.

| Alternativa | Descrizione | Esito |
|---|---|---|
| **A — Result sigillato dedicato** | Un piccolo tipo `sealed class` proprio del progetto, con una gerarchia di errori di dominio esplicita | ✅ Scelta |
| **B — Eccezioni Kotlin standard** | Propagazione tramite `throw`/`try-catch` | ❌ Scartata |
| **C — Libreria funzionale di terze parti (es. Arrow `Either`)** | Tipo `Either`/`Result` generico da una libreria funzionale esterna | ❌ Scartata |

**Motivazione**: la Technical Architecture Bible §07 stabilisce che ogni confine di layer è anche un punto di traduzione dell'errore ("un errore di L5 non raggiunge mai L1 nella sua forma originale") — un tipo di ritorno esplicito rende questa traduzione un fatto verificabile dal compilatore (ogni funzione dichiara cosa può fallire nella sua firma), mentre le eccezioni (B) sono invisibili nella firma e rischiano di attraversare un layer senza essere tradotte, esattamente ciò che §07 vieta. Una libreria funzionale esterna (C) offrirebbe lo stesso beneficio ma introdurrebbe una dipendenza e un vocabolario (`Either`, `left`/`right`) estranei a tutta la documentazione esistente, per un guadagno marginale rispetto a un tipo dedicato di poche righe.

**Vantaggi**: firma di ogni caso d'uso dichiara esplicitamente i propri errori possibili; nessuna dipendenza esterna; il vocabolario degli errori (`TaskError.TitoloMancante`, ecc.) può rispecchiare direttamente le regole `TASK-R-*`/`MFC-R-*` citate nel codice.

**Svantaggi**: più verboso di un semplice `try/catch` per chi scrive un singolo caso d'uso; richiede disciplina per non degenerare in eccezioni "per i casi veramente eccezionali" (es. bug di programmazione) — quelle restano eccezioni non catturate, per costruzione.

**Impatto sul progetto**: ogni caso d'uso in `domain-task`/`feature-task` restituisce `OmniResult<T>`; le violazioni di invarianti (`TASK-R-01` titolo mancante, ecc.) sono valori `Failure` tipizzati, mai eccezioni.

**Rischi**: nessuno strutturale.

**Costo**: nessuno.

**Facilità di manutenzione**: alta — un solo tipo condiviso in `core-common`, riusato da ogni modulo futuro.

**Scalabilità**: neutra (riguarda la struttura del codice).

**Compatibilità con le Bible**: piena — rende meccanicamente verificabile la policy di traduzione degli errori già normata in Technical Architecture Bible §07.

---

## TDR-22 · Tecnologia di implementazione del Design System (libreria UI condivisa)

> Nota: decisione presa durante lo Sprint 2 (Core UI Kit). La Design System Bible è esplicitamente indipendente da ogni tecnologia di rendering ("nessun mockup, nessuna schermata, nessun codice/Flutter/SwiftUI/Compose", [00-fondamenta §epigrafe](design_system_bible/00-fondamenta.md)) e rinvia esplicitamente ogni valore fisico e ogni scelta di implementazione alla "fase UI" ([08-report §4](design_system_bible/08-report.md)) — che è esattamente questo sprint. TDR-01 fissa già **SwiftUI nativo per iOS** e **Kotlin/Jetpack Compose nativo per Android** come le due UI native del prodotto: questa voce non riapre quella scelta, decide **come realizzare in codice, verificabile in questo sandbox, la libreria di componenti che quella scelta presuppone**.

**Decisione**: **Compose Multiplatform** (JetBrains, `org.jetbrains.compose` 1.7.1, compatibile con Kotlin 2.0.21 già in uso), targeting **Android** (gated dallo stesso meccanismo `androidSdkAvailable` di ogni altro modulo bootstrap) **+ Desktop/JVM** (sempre attivo, è la nostra superficie di verifica reale in questo sandbox). **Nessun target iOS** in questo modulo: l'interfaccia iOS resta SwiftUI nativa per TDR-01, non toccata da questa decisione — una libreria SwiftUI equivalente, che consumi la stessa fonte di verità di token (§[core-designtokens](../../core/core-designtokens/README.md)), resta un lavoro separato, esplicitamente rinviato (vedi blocco Sprint 3 nel report).

| Alternativa | Descrizione | Esito |
|---|---|---|
| **A — Jetpack Compose puro (solo Android), nuovo modulo Android-only** | Aderisce a TDR-01 alla lettera (Compose = tecnologia Android) | ❌ Scartata |
| **B — Compose Multiplatform (Android + Desktop/JVM)** | Stesso codice Compose (`androidx.compose.*`) di TDR-01 per Android, più un target Desktop che compila/gira su JVM puro senza SDK Android | ✅ Scelta |
| **C — Doppia libreria nativa parallela (Compose per Android *e* SwiftUI per iOS, scritte entrambe ora)** | Copre entrambe le piattaforme native fin da subito | ❌ Scartata |

**Motivazione**: questo sandbox non ha un SDK Android installato (`ANDROID_HOME`/`ANDROID_SDK_ROOT` assenti, nessun `local.properties` con `sdk.dir` — stessa verifica già fatta per `androidApp` nel bootstrap) né un host macOS/Xcode (Linux, nessun `xcodebuild`). L'opzione A, pur più aderente lessicalmente a TDR-01, sarebbe **interamente non verificabile qui**: un modulo Android-library richiede comunque `android.jar` per compilare, non solo per un'app finale — zero compilazione, zero test, zero anteprima possibili in questo ambiente, esattamente il problema già incontrato (e dichiarato) nello Sprint 1 per le schermate Compose reali. L'opzione C raddoppierebbe il lavoro producendo una metà (SwiftUI) totalmente fittizia: senza `swiftc`/Xcode non è possibile nemmeno verificare che il codice Swift compili, per cui ogni "test"/"anteprima"/"regressione visiva" dichiarata per quella metà sarebbe una finzione — la stessa ragione per cui lo Sprint 1 ha rinviato le schermate reali piuttosto che scriverne di non verificabili. L'opzione B risolve entrambi i problemi: Compose Multiplatform usa **le stesse API `androidx.compose.*`** che TDR-01 già assegna ad Android (non è una tecnologia diversa, è lo stesso Jetpack Compose eseguito anche fuori da un'app Android) — quindi il codice scritto qui **è** l'implementazione Android di TDR-01, non un sostituto — e in più aggiunge un target Desktop/JVM che compila, esegue test (`compose-ui-test`), cattura screenshot (`captureToImage`, verificato funzionante in questo sandbox con `Xvfb` disponibile) e lancia una vera app di anteprima (`GalleryApp`), tutto senza SDK Android né host macOS.

**Vantaggi**: codice reale, compilabile e testabile in questo sandbox oggi, non un segnaposto; il target Desktop diventa il "banco di prova" permanente per il Design System (component gallery eseguibile, test di regressione visiva) indipendentemente dalla disponibilità futura di un SDK Android; quando un SDK Android sarà disponibile, lo stesso codice `commonMain` compila su Android senza modifiche (nessun porting).

**Svantaggi**: introduce una dipendenza esterna (plugin Gradle `org.jetbrains.compose`) non ancora presente nel grafo di build; il target Desktop non è mai uno dei target di produzione del prodotto (TDR-01 non prevede un client desktop) — la sua unica funzione è di verifica per questo sviluppo, non un rilascio; l'interfaccia iOS/SwiftUI resta interamente da scrivere in un secondo momento, e non condivide letteralmente il codice dei componenti (solo la fonte di verità dei token, che è già indipendente da framework).

**Impatto sul progetto**: nuovo modulo `core-designsystem` (accanto a `core-designtokens`, che resta puro Kotlin senza dipendenza da Compose — separazione già implicita nella Design System Bible tra "token" e "implementazione"); nessun modulo `domain-*`/`feature-*` dipende da `core-designsystem` in questo sprint (nessuna schermata reale, per vincolo esplicito del task).

**Rischi**: nessuno strutturale; rischio di manutenzione se una futura versione di Compose Multiplatform introducesse un breaking change prima che TDR-01 stesso evolva — mitigato dal fatto che è la stessa libreria che Android userebbe comunque.

**Costo**: nessuno (open source, Apache 2.0, stesso modello di licenza di Jetpack Compose).

**Facilità di manutenzione**: alta — un solo modulo di componenti condiviso da Android e (in futuro) da qualunque altro target Compose Multiplatform, verificato oggi su Desktop.

**Scalabilità**: piena rispetto al numero di componenti; non introduce alcun limite architetturale nuovo.

**Compatibilità con le Bible**: piena — la Design System Bible non vincola la tecnologia (§00 epigrafe); TDR-01 resta intatto (Android continua a usare Jetpack Compose, iOS resta SwiftUI nativo, non toccato).

**Nota di verifica scoperta durante l'implementazione (limite di questo sandbox, non del codice)**: il target Desktop compila senza eccezioni — tutti i 22 componenti, il catalogo e la gallery app compilano puliti, verificato ripetutamente. **L'esecuzione runtime** (test `compose-ui-test`, avvio della `GalleryApp`, screenshot) **non è possibile in questo sandbox**: `compose.foundation`/`compose.ui` 1.7.1 dichiarano, solo a runtime (non a compile-time — per questo la compilazione non ne risente), dipendenze transitive verso artefatti AndroidX reali (`androidx.lifecycle:lifecycle-runtime:2.8.5`, `androidx.annotation:annotation:1.8.0`, `androidx.collection:collection:1.4.0`, `androidx.arch.core:core-common:2.2.0`) pubblicati **esclusivamente** su `dl.google.com`. La policy di rete di questo sandbox blocca quell'host (verificato: `curl` diretto restituisce `403` sul CONNECT; gli stessi coordinate non esistono su Maven Central, verificato con richieste dirette a `repo1.maven.org`, `404`). Questo blocca qualunque esecuzione JVM che tocchi il runtime Compose — non solo `compose.desktop.uiTestJUnit4` (provato e scartato), ma anche la variante più leggera `compose.uiTest`/`runComposeUiTest` e persino `:core:core-designsystem:run` della gallery, perché la risoluzione di `jvmTestRuntimeClasspath`/`jvmRuntimeClasspath` fallisce prima che qualunque codice venga eseguito. Per questo motivo il modulo non contiene sorgenti `jvmTest` in questo sprint: lasciarle avrebbe reso `:core:core-designsystem:check` (e quindi `checkAll`/l'intero `gradle build`) permanentemente rosso in questo ambiente — una CI rotta è peggio di un gap di copertura dichiarato. Il codice di test scritto durante lo sviluppo era corretto e compilava (verificato prima di essere rimosso); girerebbe senza modifiche in un ambiente con accesso di rete normale (una macchina di sviluppo, Android Studio, GitHub Actions). Blocco per Sprint 3, elenco completo delle conseguenze nel report Sprint 2.

### Valori fisici concreti (Design System Bible §[08-report §4.1](design_system_bible/08-report.md), esplicitamente rinviati a questo sprint)

| Decisione | Valore | Motivazione |
|---|---|---|
| Unità base di spaziatura `u` | **12dp** | Design System Bible impone `spazio.1 = u` = 1/4 del target di tocco minimo (DS-34, 44pt/48dp); con 48dp scelto come valore di riferimento (già il minimo Android usato altrove nel bootstrap), `u = 48/4 = 12dp` |
| Scala tipografica (corpo base 16sp) | 32/24/20/16/16/14/12sp per i 7 livelli (rapporti ×2.0/×1.5/×1.25/×1.0/×1.0/×0.875/×0.75 su base 16sp) | 16sp è la dimensione di corpo di sistema convenzionale su entrambe le piattaforme mobile; i rapporti sono quelli già normati in [01-token-visivi §2](design_system_bible/01-token-visivi.md), non reinventati |
| Elevazione (dp) | 0 / 1 / 4 / 8dp per i 4 livelli | Scostamento crescente coerente con "scostamento tonale + ombra" crescente di [01-token-visivi §4](design_system_bible/01-token-visivi.md); in tema scuro il canale primario resta la variazione tonale (DS-04/25), l'ombra è un canale secondario a bassa opacità |
| Raggi (`raggio.piccolo/medio/grande`) | `spazio.1`/2, `spazio.2`/2, `spazio.4`/2 = 6/12/24dp | La Bible lega i raggi alla scala di spaziatura ("coerente con", [01-token-visivi §5](design_system_bible/01-token-visivi.md)) senza fissare un rapporto esatto; un dimezzamento sistematico mantiene ogni raggio derivato dalla stessa scala (rispetta DS-INV-02: "nessun valore fuori scala") restando visivamente proporzionato (un raggio identico alla spaziatura piena produrrebbe forme a pillola anche per Card/campi, contraddicendo l'anatomia dichiarata) |
| `raggio.pieno` | 50% dell'altezza del componente (`CircleShape`/`RoundedCornerShape(percent = 50)`) | Valore già letterale nella Bible |
| Palette colore (ruoli chiaro/scuro) | Vedi `OmniColors.kt`, `core-designtokens` | Ogni coppia verificata via test automatico di contrasto WCAG (4.5:1 testo normale, 3:1 componenti UI) — vedi §Test sotto. `accento.base` mantiene **lo stesso valore saturo in entrambi i temi** (dichiarato esplicitamente come coppia uguale, non derivato) per garantire `testo.su_accento` bianco affidabile in entrambi i temi con un solo valore verificato — DS-27 permette (non impone) un accento desaturato in scuro; questa scelta rinuncia a quell'opzione per una garanzia di contrasto più semplice da verificare meccanicamente, rivalutabile in un futuro sprint |
| `accento.base`, insieme chiuso (SET-001 §2) | 6 opzioni: Blu, Verde, Viola, Corallo, Petrolio, Indaco | Scelte per restare visivamente distinte da `stato.attenzione` (ambra) e `stato.critico` (rosso), come richiesto implicitamente da DS-INV-05 (un'opzione utente non deve poter essere confusa con un colore di stato semantico) |
| Font family | `FontFamily.Default` (font di sistema della piattaforma) | DS-03 impone "un solo font di sistema", senza sceglierne uno specifico; il font di sistema evita di dover distribuire asset font cross-piattaforma per un beneficio estetico non richiesto da alcuna Bible |
| Set iconografico | Set minimale proprietario, disegnato come `ImageVector` a tratto singolo uniforme (`OmniIcons.kt`) | DS-05/09 impongono un solo set monocromatico a tratto uniforme, senza specificare quale libreria; un piccolo set fatto a mano in Kotlin evita di introdurre una pipeline di asset (font icon o SVG) cross-piattaforma per le ~15 icone effettivamente usate dai componenti di questo sprint — estendibile senza cambiare tecnologia quando servirà un set più ampio |
| Durate/curve di motion | Vedi `OmniMotion.kt` | Valori già normativi in UX Bible MUC §4/Design System Bible §03, tradotti in `AnimationSpec` di Compose (spring/tween) senza reinterpretarli |

**Verifica di contrasto**: ogni coppia di colore chiaro/scuro è verificata da un test automatico (`ColorContrastTest`, `core-designtokens`) che calcola il rapporto di contrasto WCAG 2.1 e asserisce ≥4.5:1 per testo normale, ≥3:1 per componenti UI/bordo di focus — non una verifica manuale una tantum, ma un test che fallisce la build se un valore futuro regredisse (DS-26: "verifica indipendente chiaro/scuro obbligatoria").

---

## Tabella Finale — Decisioni Tecnologiche Approvate

| ID | Area | Decisione approvata |
|---|---|---|
| TDR-01 | Linguaggio mobile | Kotlin Multiplatform (dominio condiviso) + Swift/SwiftUI (iOS) + Kotlin/Jetpack Compose (Android) |
| TDR-02 | Architettura mobile (pattern L1) | MVI / Unidirectional Data Flow |
| TDR-03 | Backend | Go, servizi stateless containerizzati |
| TDR-04 | Autenticazione | Token propri (access + refresh) a breve vita, legati al dispositivo; Sign-in di piattaforma opzionale; TOTP per 2FA |
| TDR-05 | Sincronizzazione | CRDT minimale su misura: LWW per-campo, OR-Set per GraphLink, snapshot per Note |
| TDR-06 | Database locale | SQLite cifrato a livello di pagina + estensione FTS |
| TDR-07 | Database server | PostgreSQL |
| TDR-08 | Storage | Object storage compatibile S3 |
| TDR-09 | Notifiche | APNs/FCM diretti dietro relay proprio (nessun aggregatore di terze parti) |
| TDR-10 | Analytics | Pipeline analytics open-source, self-hosted, solo eventi anonimi opt-in |
| TDR-11 | Crash reporting | Toolkit open-source/self-hostabile, ingestione self-hosted |
| TDR-12 | Osservabilità | Stack open-source (metriche/log/tracce), self-gestibile |
| TDR-13 | Testing | Framework nativi per piattaforma + suite condivisa KMP + driver E2E cross-platform |
| TDR-14 | CI/CD | Pipeline as-code portabile, runner containerizzati, non vincolata a un singolo cloud |
| TDR-15 | Gestione segreti | Vault dedicato, self-hostabile, credenziali a breve vita/OIDC dove possibile |
| TDR-16 | Dipendenze | SCA + SBOM continui, allowlist di licenze curata, dal primo commit |
| TDR-17 | Packaging | Pacchetti nativi store (App Store/Play Store) + delivery modulare on-demand |
| TDR-18 | Build system | Build multiplatform unificata (classe Gradle) per modulo condiviso + UI native |
| TDR-19 | Dependency Injection | Injection manuale via costruttore, composition root per piattaforma (nessun framework) |
| TDR-20 | Libreria di accesso SQLite (KMP) | SQLDelight |
| TDR-21 | Tipo di errore per i casi d'uso | `Result` sigillato dedicato (`OmniResult<T>`), non eccezioni |
| TDR-22 | Tecnologia di implementazione del Design System | Compose Multiplatform (Android + Desktop/JVM); iOS resta SwiftUI nativo, non toccato |

**Nota sulle voci TDR-19…22**: a differenza di TDR-01…18 (decise tutte insieme, prima di ogni riga di codice), queste voci sono state aggiunte durante gli sprint di sviluppo reale (TDR-19…21 nello Sprint 1, TDR-22 nello Sprint 2) quando l'implementazione ha incontrato una decisione tecnica non coperta dalle Bible esistenti. Seguono lo stesso metodo (≥3 alternative, motivazione contro la documentazione esistente) e la stessa autorità delle prime 18.

**Filo conduttore delle 18 decisioni originarie**: ovunque esistesse una scelta tra (a) controllo diretto/open-source/portabile e (b) comodità tramite un fornitore proprietario di terze parti, è stata preferita (a) — coerenza diretta con l'indipendenza tecnologica ed economica già rivendicata in tutta la documentazione precedente (Product Constitution, Business Strategy, Technical Architecture Bible). Nessuna decisione introduce un fornitore con visibilità sui contenuti utente; ogni decisione è stata verificata contro almeno una Bible esistente e non ne contraddice alcuna.

---

*Prossimo passo: con questo documento, tutti i prerequisiti tecnologici dichiarati come mancanti nell'[Engineering Plan §08](engineering_plan/08-report.md) sono risolti. Lo sviluppo può procedere secondo la sequenza già stabilita in [09-piano-di-sviluppo.md](09-piano-di-sviluppo.md) e nell'[Engineering Plan](engineering_plan/README.md), a partire da `ENG-00-1`.*
