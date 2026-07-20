# 01 · Architettura Generale e Layer

> Eredita [00 · Principi Architetturali](00-principi-architetturali.md).

## 1. Vista d'insieme (in una frase)

OmniLife è un sistema **client-centrico, offline-first, a moduli disaccoppiati**, in cui un nucleo di servizi condivisi (Core Services) media tra un insieme di moduli di dominio indipendenti e un confine esterno di sincronizzazione che non può leggere i contenuti dell'utente — la stessa architettura "content-blind" richiesta dalla promessa di privacy end-to-end (Product Bible, Constitution Titolo II; Functional Bible, D-02 richiamato in [Data Model Bible §4.2](../data_model_bible/00-modello-dati-comune.md)).

## 2. I sei layer logici

```
┌──────────────────────────────────────────────────────────────┐
│  L1 · ESPERIENZA (Presentazione)                              │
│  Implementa: UX Bible (schermate, flussi, microinterazioni)   │
│  Responsabilità: mostrare stato, catturare intenzione utente  │
│  NON contiene: regole di business, calcoli, accesso ai dati   │
└───────────────────────────┬─────────────────────────────────-┘
                             │ invoca casi d'uso, riceve stato osservabile
┌───────────────────────────▼───────────────────────────────────┐
│  L2 · APPLICAZIONE (Orchestrazione)                            │
│  Implementa: Functional Bible (flussi, HOME-002 composizione,  │
│  REV-001 sequenze guidate, CAPT-001 instradamento cattura)     │
│  Responsabilità: orchestrare casi d'uso multi-modulo, comporre │
│  le viste aggregate (Home, Ricerca, Revisione) senza possedere │
│  dati di dominio                                                │
└───────────────────────────┬─────────────────────────────────-┘
                             │ invoca contratti di modulo e servizi Core
┌───────────────────────────▼───────────────────────────────────┐
│  L3 · DOMINIO (Moduli)                                          │
│  Implementa: Data Model Bible (entità, invarianti, regole R-*) │
│  Un'unità di dominio per modulo: Attività, Finanze, Abitudini, │
│  Calendario, Note, Salute, Obiettivi                            │
│  Responsabilità: possedere le proprie entità, applicare le      │
│  proprie regole di business, pubblicare/sottoscrivere eventi    │
│  NON conosce altri moduli, NON conosce la UI                    │
└───────────────────────────┬─────────────────────────────────-┘
                             │ usa porte esposte dai Servizi Core
┌───────────────────────────▼───────────────────────────────────┐
│  L4 · SERVIZI CORE (condivisi, posseduti dal Core non da un    │
│  modulo)                                                        │
│  Grafo (GraphLink) · Motore di Sincronizzazione (concettuale)  │
│  · Motore di Ricerca · Broker di Notifiche · Motore di Insight │
│  · Parser di Cattura · Bus Eventi · Registro Moduli ·          │
│  Backup/Export · Servizi di Sicurezza (cifratura concettuale)  │
│  Responsabilità: fornire capacità trasversali dietro contratti  │
│  stabili, mai contenere regole specifiche di un modulo          │
└───────────────────────────┬─────────────────────────────────-┘
                             │ usa porte implementate dagli Adapter
┌───────────────────────────▼───────────────────────────────────┐
│  L5 · ADATTATORI DI PIATTAFORMA                                 │
│  Persistenza locale · Fornitore Calendario di sistema ·        │
│  Piattaforma Salute di sistema · Enclave di sicurezza/biometria│
│  · Trasporto notifiche push · Storage file locale               │
│  Responsabilità: tradurre le porte astratte nei servizi reali   │
│  offerti dal sistema operativo — sostituibili singolarmente     │
│  (Liskov, §00.2)                                                 │
└───────────────────────────┬─────────────────────────────────-┘
                             │ confine di rete, dati già cifrati
┌───────────────────────────▼───────────────────────────────────┐
│  L6 · CONFINE DI SINCRONIZZAZIONE ESTERNO ("content-blind")     │
│  Riceve/serve solo blob cifrati + metadati di versione;         │
│  non è un layer applicativo del client: è un confine di fiducia │
│  esterno, esplicitamente non in grado di leggere i contenuti    │
└──────────────────────────────────────────────────────────────┘
```

## 3. Perché sei layer e non meno

Ogni confine risolve un problema specifico già posto dalle Bible a monte:

| Confine | Problema che risolve | Fonte |
|---|---|---|
| L1 ↔ L2 | La UX Bible descrive comportamento (flussi, stati, microinterazioni) indipendentemente da come i dati vengono calcolati: separare Presentazione da Applicazione rende quella indipendenza reale, non solo dichiarata | UX Bible, [MUC](../ux_bible/00-modello-ux-comune.md) |
| L2 ↔ L3 | La composizione multi-modulo (Home "Oggi" aggrega card di moduli diversi — HOME-002) non può vivere dentro un modulo senza violare "nessun modulo conosce un altro modulo" | Functional Bible, HOME-001/002, C-art. 184 |
| L3 ↔ L4 | Le regole di dominio (es. l'aderenza di un'Abitudine, il progresso di un Obiettivo) devono restare pure e calcolabili senza dipendere da come i dati arrivano o si sincronizzano | Data Model Bible, [11](../data_model_bible/11-versionamento-e-sincronizzazione.md) |
| L4 ↔ L5 | I Servizi Core (in particolare Sync e Sicurezza) devono restare validi anche se il sistema operativo o il fornitore di storage cambiano | Product Bible, indipendenza tecnologica implicita nell'architettura content-blind |
| L5 ↔ L6 | Il confine di fiducia più importante del prodotto (E2E): tutto ciò che esce dal dispositivo è già cifrato prima di attraversarlo | Product Constitution, art. 1-2, 27-28 |
| Interno a L3 | Ogni modulo è isolato dagli altri moduli (non solo dai layer) | Constitution art. 181-187 |

## 4. La Regola delle Dipendenze (Dependency Rule)

**Le dipendenze del codice puntano sempre verso l'interno (da L1 verso L4/L3), mai verso l'esterno.** Un layer interno non importa mai un layer esterno; espone porte astratte che i layer esterni implementano o consumano.

- L1 (Esperienza) dipende da L2 (Applicazione): invoca casi d'uso, osserva stato — non contiene mai logica di calcolo (coerente con [UX Bible MUC](../ux_bible/00-modello-ux-comune.md), che descrive solo comportamento osservabile).
- L2 (Applicazione) dipende da L3 (Dominio) e da porte di L4 (Servizi Core) — mai da un Adattatore concreto di L5.
- L3 (Dominio) **non dipende da nulla al di fuori di sé stesso** oltre alle porte minime di L4 che gli servono (es. il Dominio Abitudini usa la porta "osservatore piattaforma salute" per HAB-010, ma non conosce l'Adattatore concreto che la implementa).
- L4 (Servizi Core) definisce le porte che L5 implementa: **l'inversione è qui** — è il Servizio Core a dichiarare di che cosa ha bisogno, ed è l'Adattatore a doverlo fornire, mai il contrario.
- L5 (Adattatori) non dipende da nulla sopra di sé: riceve le porte da implementare e le implementa.
- L6 non è un layer del client: è un confine esterno che L5 (tramite l'Adattatore di sincronizzazione) attraversa **solo con dati già cifrati dal Servizio Core di Sicurezza (L4)** — mai in chiaro.

## 5. Contenuto tipico di ciascun layer (mappatura sintetica)

| Layer | Contenuto tipico | Non contiene mai |
|---|---|---|
| L1 Esperienza | Composizione delle 62 schermate ([UX Bible Screen Inventory](../ux_bible/03-screen-inventory.md)), gestione dello stato di navigazione, microinterazioni | Regole di business, accesso diretto ai dati |
| L2 Applicazione | Orchestrazione dei flussi multi-modulo (Home, Ricerca globale, Revisione settimanale, Cattura con instradamento), composizione delle viste aggregate | Regole di dominio specifiche di un modulo (es. il calcolo dell'aderenza abitudini vive in L3, non qui) |
| L3 Dominio (per modulo) | Entità e invarianti ([Data Model Bible](../data_model_bible/README.md)), regole di business del modulo (TASK-R-*, FIN-R-*, HAB-R-*, …), pubblicazione/sottoscrizione di eventi propri | Riferimenti diretti ad altri moduli, dettagli di presentazione |
| L4 Servizi Core | Grafo (GraphLink), Sincronizzazione concettuale, Ricerca, Notifiche (broker centrale), Insight, Parser di Cattura, Bus Eventi, Registro Moduli, Backup/Export, Sicurezza | Regole di business specifiche di un modulo |
| L5 Adattatori | Traduzione porta↔piattaforma reale | Qualunque logica di dominio o di orchestrazione |
| L6 Confine esterno | Ricezione/distribuzione di blob cifrati e metadati di versione | Qualunque capacità di lettura dei contenuti |

## 6. Nota sul confine L6 (non un layer client)

Il confine di sincronizzazione esterno è descritto come "content-blind" già nella Functional Bible (SYNC-001, D-02) e nel Data Model Bible (§9 deroghe): riceve blob cifrati e metadati di versione, non contenuti. Questa Bible tratta L6 come **confine di fiducia**, non come componente da progettare (la sua eventuale articolazione interna — servizi, storage, instradamento — è **fuori perimetro**, decisione rinviata: vedi [15-report](15-report.md)).

---

*Prossimo: [Moduli, Responsabilità, Boundaries](02-moduli-responsabilita-boundaries.md)*
