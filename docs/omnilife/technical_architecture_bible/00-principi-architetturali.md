# 00 · Principi Architetturali

> **Fonte di verità esclusiva**: [Product Bible](../product_bible/README.md), [Functional Bible](../functional_bible/README.md), [UX Bible](../ux_bible/README.md), [Data Model Bible](../data_model_bible/README.md). Questo documento **non sceglie tecnologia**: definisce le regole con cui qualunque tecnologia futura dovrà essere organizzata. Nessun linguaggio, framework, database o cloud provider è nominato in questa Bible, per costruzione.
>
> Regola di lettura: ogni documento successivo di questa Bible eredita i principi qui definiti; le deroghe sono sempre esplicite e motivate (stessa disciplina delle Bible precedenti).

## 1. Perché un'architettura logica separata dall'implementazione

Le tre Bible precedenti rispondono a "perché" (Product), "che cosa deve accadere" (Functional), "come lo vive l'utente" (UX) e "quali dati esistono" (Data Model). Questa Bible risponde a **"come si organizza il sistema che rende tutto questo vero"**, senza ancora scegliere con che cosa costruirlo. La separazione non è burocrazia: è l'applicazione al software del principio che governa l'intero progetto — **decidere con disciplina, un livello alla volta** (Product Bible, Feature Philosophy, i "7 cancelli"; qui: nessuna scelta tecnologica finché la forma logica non è stabile).

## 2. Principi SOLID applicati all'architettura di OmniLife

| Principio | Applicazione concreta in questa architettura |
|---|---|
| **S — Single Responsibility** | Ogni modulo di dominio ([02](02-moduli-responsabilita-boundaries.md)) possiede un solo dominio del [Data Model Bible](../data_model_bible/README.md) (Attività possiede Task/TaskList/Subtask, Finanze possiede Transaction/FinancialAccount/…). Ogni layer ([01](01-architettura-generale-e-layer.md)) ha una sola responsabilità trasversale (presentazione, orchestrazione, dominio, servizi condivisi, adattamento di piattaforma) |
| **O — Open/Closed** | Il sistema è aperto all'estensione tramite l'architettura a plugin ([04](04-plugin-architecture.md)) e senza modifiche al Core: un nuovo modulo si aggiunge dichiarando un contratto, mai modificando il bus eventi o gli altri moduli (coerente con C-art. 181-195) |
| **L — Liskov Substitution** | Ogni Adapter di piattaforma ([01 §5](01-architettura-generale-e-layer.md)) è sostituibile senza che i layer interni se ne accorgano: un fornitore di calendario di sistema, un motore di cifratura, un trasporto push sono intercambiabili dietro lo stesso contratto (porta) |
| **I — Interface Segregation** | I contratti (porte) tra layer sono minimi e specifici: il motore di Ricerca dipende da un contratto "entità indicizzabile", non dall'intero modulo che la possiede; il broker di Notifiche dipende da un contratto "richiesta di notifica", non dai dati interni del modulo richiedente (coerente con Data Model Bible, GraphLink come unico tipo di relazione cross-modulo) |
| **D — Dependency Inversion** | Le dipendenze puntano sempre verso l'interno (Dependency Rule, [01 §4](01-architettura-generale-e-layer.md)): i layer esterni (Presentazione, Adattatori di piattaforma) dipendono da astrazioni definite dai layer interni (Dominio, Servizi Core), mai il contrario |

## 3. Clean Architecture / Ports & Adapters come forma

L'architettura di OmniLife è organizzata secondo la **Regola delle Dipendenze** (Dependency Rule) della Clean Architecture, nella sua declinazione a porte e adattatori: il codice che esprime le regole di business (Dominio, coerente con il [Modello Dati Comune](../data_model_bible/00-modello-dati-comune.md)) non conosce né la UI né la piattaforma; ogni comunicazione verso l'esterno passa da un contratto astratto (porta) implementato da un adattatore concreto. Questa forma è scelta perché:

1. **Rende reale la modularità promessa dal prodotto** (Product Bible, principi P77-P84; Constitution Titolo VIII): un modulo che dipende solo da porte astratte può essere disattivato, aggiornato o rimosso senza che gli altri moduli se ne accorgano (MFC-R-13).
2. **Rende testabile la promessa di offline-first e di sincronizzazione senza conflitti visibili** (Data Model Bible §8, [11](../data_model_bible/11-versionamento-e-sincronizzazione.md)): la logica di convergenza vive nel Dominio/Servizi Core, indipendente da come i dati arrivano fisicamente sul dispositivo.
3. **Protegge la promessa di sicurezza end-to-end** (Product Constitution Titolo II): il confine di fiducia tra "ciò che è nostro" e "ciò che è esterno/non fidato" coincide esattamente con un confine architetturale ([10-sicurezza-architetturale](10-sicurezza-architetturale.md)), non con una convenzione di codice.

## 4. Modularità come vincolo architetturale, non come stile

La modularità non è un pattern tra tanti: è un requisito del prodotto (Product Bible D-01, Constitution art. 181-195) reso eseguibile solo se l'architettura lo impone strutturalmente. Da qui derivano due regole non negoziabili, sviluppate in [02](02-moduli-responsabilita-boundaries.md):

- **Nessun modulo di dominio importa un altro modulo di dominio.** L'unica comunicazione ammessa è tramite il bus eventi ([03](03-event-driven-architecture.md)) e il grafo dei collegamenti ([GraphLink](../data_model_bible/02-entita-cattura-grafo.md#dm-link-01--graphlink-collegamento)).
- **La build (in senso lato: la composizione del sistema) deve poter rifiutare una dipendenza vietata.** Questa non è una regola di comportamento del team, è una regola che l'architettura deve rendere verificabile meccanicamente — una decisione di dettaglio implementativo rinviata ([15-report](15-report.md)), ma il **principio** (verificabilità strutturale, non disciplina) è normativo qui.

## 5. Convenzioni progettuali trasversali

| Convenzione | Descrizione |
|---|---|
| **Un solo linguaggio di dominio** | I nomi delle entità, eventi e stati usati in questa Bible sono identici a quelli del Data Model Bible e della Functional Bible — nessuna traduzione tecnica parallela (coerente con P83, C-art. 61: nessuna duplicazione di concetti) |
| **Ogni componente dichiara i propri contratti prima della propria implementazione** | Un modulo, un servizio Core o un adattatore si progetta prima come insieme di porte (ciò che espone, ciò che richiede), poi come implementazione — riflette a livello di codice la stessa disciplina "prima il problema, poi la soluzione" della Product Bible (Feature Philosophy) |
| **Ogni confine architetturale è anche un confine di fiducia** | Un layer più esterno non è mai automaticamente fidato da un layer più interno: gli Adattatori di piattaforma e il confine di sincronizzazione verso l'esterno sono trattati come non fidati fino a validazione ([10](10-sicurezza-architetturale.md)) |
| **L'architettura non presume una tecnologia specifica** | Ogni scelta di linguaggio, framework, database fisico o fornitore cloud è esplicitamente **fuori perimetro** di questa Bible (vedi [15-report §5](15-report.md)) — l'architettura deve restare valida anche se la tecnologia cambia |
| **Un solo stato di verità per ogni informazione** | Coerente con il Data Model Bible (§16, INV-05, VCB-05): un valore derivato (saldo, aderenza, progresso obiettivo) non ha mai una copia scritta indipendentemente — l'architettura riflette questo con componenti di calcolo, non di storage, per quei valori ([07-caching](../data_model_bible/README.md) → vedi [05](05-offline-first-sincronizzazione-caching.md) qui) |

## 6. Che cosa questa Bible NON fa (perimetro esplicito)

Coerentemente con l'istruzione ricevuta, questa Bible **non**: progetta API (endpoint, contratti di rete concreti), non sceglie un database fisico, non sceglie un cloud provider, non sceglie framework o linguaggi di programmazione, non scrive codice. Ogni punto in cui una decisione tecnologica sarebbe stata naturale è esplicitamente segnalato come **decisione rinviata alla fase API/Implementazione** (catalogate in [15-report](15-report.md)).

---

*Indice: [README](README.md) · Prossimo: [Architettura Generale e Layer](01-architettura-generale-e-layer.md)*
