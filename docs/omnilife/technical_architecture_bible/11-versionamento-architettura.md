# 11 · Versionamento dell'Architettura

> Eredita [00](00-principi-architetturali.md)…[10](10-sicurezza-architetturale.md). Il versionamento **dei dati** è già interamente specificato nel [Data Model Bible §7](../data_model_bible/00-modello-dati-comune.md#7-versionamento-e-cronologia-eredita-mfc-24r-07-deroghe-dichiarate) (INV-09: la versione di schema non retrocede mai). Questo documento tratta il versionamento a un livello superiore: **l'architettura stessa** (i contratti tra layer e tra moduli) e la sua evoluzione nel tempo senza rompere la promessa di modularità.

## 1. Tre oggetti versionati, tre cicli di vita distinti

| Oggetto versionato | Che cosa cambia | Frequenza attesa | Compatibilità richiesta |
|---|---|---|---|
| **Contratto di Modulo** ([04](04-plugin-architecture.md)) | Le porte che un modulo espone/consuma (identità, entità, contributi, eventi) | Bassa (è la superficie pubblica di un modulo) | Deve restare compatibile con il Core per almeno una versione precedente (finestra N-1, coerente con lo spirito di stabilità richiesto dal marketplace, Constitution art. 192-194) |
| **Schema di un'entità** (Data Model Bible) | I campi di una singola entità (es. Task, Transaction) | Più alta del Contratto di Modulo | Ogni entità porta la propria `versione_schema` (Data Model Bible §3); la migrazione è responsabilità del solo modulo proprietario, mai del Core |
| **Forma degli eventi** (§[03](03-event-driven-architecture.md)) | Il payload minimo di un tipo di evento | Bassa (il payload è già minimo per costruzione) | Un consumer più vecchio deve poter ignorare campi aggiunti a un payload (estensione additiva, mai un campo rimosso senza deprecazione) |

## 2. Perché tre cicli di vita separati (e non uno solo)

Se il versionamento dell'architettura, dei dati e degli eventi fossero un unico numero di versione, ogni piccola modifica a un'entità interna di un modulo forzerebbe una nuova versione del Contratto di Modulo — violando l'isolamento che l'intera architettura protegge ([02](02-moduli-responsabilita-boundaries.md)). Separare i tre cicli è quindi una conseguenza diretta, non una scelta indipendente, del principio di isolamento dei moduli.

## 3. Compatibilità del Contratto di Modulo (finestra N-1)

Un modulo aggiornato deve poter operare con una versione del Core più vecchia di una release, e viceversa (principio di stabilità per l'ecosistema, coerente con Constitution art. 194: "le API pubbliche si versionano e si deprecano con preavvisi lunghi"). Architetturalmente:

- Le porte esposte dal Core verso i moduli (L4) sono **additive per default**: una nuova capacità si aggiunge come nuova porta opzionale, mai come modifica di una porta esistente che romperebbe i consumatori correnti.
- La rimozione di una porta segue un ciclo di deprecazione esplicito (annuncio → periodo di coesistenza → rimozione), mai una rimozione immediata.
- Un modulo che dichiara una versione minima di Contratto non supportata dal Core installato viene trattato come "non attivabile" (stato coerente con `DM-SYS-03 ModuleActivation`), mai come un crash.

## 4. Migrazione di schema: responsabilità e isolamento

Coerente con Data Model Bible (INV-09) e Functional Bible (MFC-E-15: "le migrazioni di schema sono transazionali e riprendibili; backup automatico pre-migrazione"): ogni Dominio di modulo (L3) è responsabile della migrazione delle **proprie** entità. Architetturalmente:

- Il Core (L4, Registro Moduli) orchestra **l'ordine** delle migrazioni all'avvio (nessun modulo migra prima che le sue dipendenze dichiarate — es. verso il Grafo — siano pronte), ma non conosce **il contenuto** della migrazione di un modulo.
- Una migrazione fallita di un modulo non blocca l'avvio degli altri moduli (isolamento, coerente con [07 §3](07-gestione-errori.md)) — il modulo in errore risulta "non disponibile" fino a risoluzione, gli altri operano normalmente.

## 5. Versionamento del Bus Eventi

Un tipo di evento (`modulo.entita.azione`) è, una volta pubblicato in una versione del sistema, **stabile per convenzione**: un nuovo campo nel payload è sempre opzionale per i consumer esistenti (estensione additiva, coerente con Interface Segregation — un consumer vecchio ignora ciò che non conosce senza errore). Un cambiamento non additivo (rinominare o rimuovere un campo, cambiare semantica) richiede un nuovo tipo di evento, mai la modifica silenziosa di uno esistente — altrimenti un consumer non aggiornato riceverebbe dati che non si aspetta, violando l'isolamento tra moduli con versioni diverse.

## 6. Versionamento del confine L6 (sincronizzazione esterna)

Poiché L6 è content-blind ([01 §6](01-architettura-generale-e-layer.md)), il suo unico "contratto" verso il client è: ricevere/servire blob cifrati con metadati di versione. Il formato interno del blob cifrato (che è responsabilità del Servizio di Sicurezza + del Dominio proprietario, non di L6) può evolvere senza richiedere modifiche a L6 — questo è un vantaggio architetturale diretto della scelta content-blind: **L6 non deve mai essere aggiornato in lockstep con l'evoluzione dello schema delle entità**.

## 7. Decisioni esplicitamente rinviate

Il numero di versione esatto (semver o altro schema), lo strumento di gestione delle migrazioni, il meccanismo tecnico con cui un modulo dichiara la propria versione minima richiesta: decisioni di implementazione — questa Bible fissa solo **quali oggetti si versionano indipendentemente e con quali garanzie di compatibilità**, coerentemente con quanto già stabilito per lo schema dati (Data Model Bible, INV-09, dove la "decisione tecnica di dettaglio" era già dichiarata rinviata).

---

*Prossimo: [Diagrammi Testuali](12-diagrammi-testuali.md)*
