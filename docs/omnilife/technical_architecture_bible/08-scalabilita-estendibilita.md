# 08 · Scalabilità ed Estendibilità

> Eredita [00](00-principi-architetturali.md)…[07](07-gestione-errori.md). Due forme distinte di scala, entrambe già implicite negli obiettivi di Product/Functional/UX Bible: **scala del dispositivo** (un utente con moduli/dati crescenti) e **scala dell'ecosistema** (milioni di utenti, marketplace di moduli). Nessuna infrastruttura è scelta qui: solo le proprietà architetturali che la rendono possibile.

## 1. Scalabilità sul dispositivo (client-side)

### 1.1 Scala dei dati per utente

La Functional Bible dichiara esplicitamente i volumi target (MFC-E-14: 100.000+ entità; MFC-AC-07: 50.000 entità con risultati di ricerca ≤100ms; Data Model Bible §11: 10 anni di storico finanziario). L'architettura sostiene questi volumi tramite:

- **Isolamento per modulo** ([02](02-moduli-responsabilita-boundaries.md)): il volume di dati di un modulo non degrada le prestazioni di un altro — nessuna query né indice condiviso tra Domini.
- **Indice di Ricerca come proiezione separata** ([05 §4](05-offline-first-sincronizzazione-caching.md)): la Ricerca scala indipendentemente dal volume di ciascun modulo, poiché è un servizio dedicato con la propria struttura ottimizzata per l'accesso in lettura.
- **Valori derivati mai ricalcolati per l'intero storico ad ogni lettura** (principio, non meccanismo: la cadenza esatta di ricalcolo — es. incrementale vs completo — è una decisione di implementazione rinviata).

### 1.2 Scala del numero di moduli attivi

Il Registro Moduli (`DM-SYS-03`) e la composizione a fan-out di L2 ([06 §3](06-lifecycle-richieste-ed-eventi.md)) scalano linearmente con il numero di moduli attivi (non quadraticamente): ogni modulo contribuisce alla Home in modo indipendente, senza che l'aggiunta di un modulo richieda modifiche agli altri (Open/Closed, [00 §2](00-principi-architetturali.md)) — coerente con il budget dichiarato dalla Functional Bible (HOME-001: "compone in <400ms a freddo con 5 moduli attivi").

### 1.3 Scala del grafo (GraphLink)

Il Data Model Bible (GOAL edge case) dichiara esplicitamente un caso a 100 contributi per Obiettivo con raggruppamento nella vista. Architetturalmente, il Grafo (L4) è progettato come un indice separato dalle entità che collega (§[05 §4](05-offline-first-sincronizzazione-caching.md) — non è parte dello storage di un modulo), consentendo di query-are "tutti i collegamenti di un'entità" senza attraversare i Domini dei moduli coinvolti.

## 2. Scalabilità dell'ecosistema (verso il confine L6 e oltre)

Questa Bible **non progetta l'infrastruttura del confine L6** (per costruzione, [01 §6](01-architettura-generale-e-layer.md)): descrive solo le proprietà che qualunque sua implementazione futura deve rispettare per essere coerente con l'architettura del client.

| Proprietà richiesta a L6 (qualunque sia l'implementazione) | Perché |
|---|---|
| Nessuna transazione cross-utente | Il confine è content-blind e per-account (Product Bible, Business Strategy); ogni utente è indipendente dagli altri, quindi la scala orizzontale è imbarazzantemente parallela per costruzione |
| Nessuna elaborazione dei contenuti lato L6 | Coerente con Product Constitution art. 1-2, 27-28: L6 riceve solo blob cifrati — questo è anche ciò che rende L6 economicamente e architetturalmente semplice da scalare (nessuna logica applicativa da eseguire sui dati) |
| Idempotenza delle operazioni di sincronizzazione | Necessaria per tollerare retry (§[05](05-offline-first-sincronizzazione-caching.md)) senza duplicazione, indipendentemente da quanti nodi del confine L6 servano la richiesta |

## 3. Estendibilità

### 3.1 Aggiungere un modulo (interno o di terze parti)

Coerente con [04 · Plugin Architecture](04-plugin-architecture.md): un nuovo modulo si aggiunge dichiarando un Contratto di Modulo, senza modificare il Bus Eventi, il Grafo, o alcun modulo esistente. Questa è la definizione operativa di "Open/Closed" applicata all'intero sistema, non solo a una classe.

### 3.2 Aggiungere un nuovo tipo di collegamento (ruolo di GraphLink)

Il Data Model Bible (MDEC-02) unifica ogni relazione cross-modulo in un solo tipo di entità (`GraphLink`) distinto solo dal campo `ruolo`. Aggiungere un nuovo tipo di relazione (es. un futuro "collegamento familiare" per gli spazi condivisi, Product Bible fase 4) richiede di aggiungere un nuovo valore di `ruolo`, **non** un nuovo tipo di entità né una nuova tabella di relazione — questa è la ragione architetturale, non solo di modellazione dati, per cui MDEC-02 è stata una scelta preventiva di estendibilità.

### 3.3 Aggiungere una nuova superficie di presentazione (es. una futura app orologio o tablet)

Poiché L1 (Esperienza) dipende da L2 (Applicazione) tramite casi d'uso astratti, e mai da L3/L4 direttamente ([01 §4](01-architettura-generale-e-layer.md)), una nuova superficie di presentazione (Widget è già un esempio — [12](12-versionamento-architettura.md) tratta Widget come proiezione di L1) può essere aggiunta senza toccare L2/L3/L4: consuma gli stessi casi d'uso già esposti. Questo è coerente con il Product Bible (Business Strategy §5: watch companion, tablet, futuro client desktop) senza che l'architettura debba essere riprogettata per accoglierli.

### 3.4 Aggiungere un nuovo mercato/valuta/lingua

Non richiede modifiche architetturali: la localizzazione (MFC-E-11) e la multi-valuta (FIN-009) sono trattate come dati di configurazione consumati da L1/L3, non come rami di codice separati — un'architettura a layer con dominio puro (L3 senza dipendenze di presentazione) rende questo naturale, non richiede una decisione aggiuntiva qui.

## 4. Che cosa NON è coperto (deliberatamente, per restare tecnologicamente neutrale)

Capacità numeriche precise di throughput per il confine L6, meccanismi di partizionamento o bilanciamento del carico, scelta di infrastruttura containerizzata o serverless: tutte decisioni di implementazione, esplicitamente rinviate ([15-report](15-report.md)) — coerenti con il perimetro dichiarato di questa Bible (nessun cloud provider, nessuna scelta infrastrutturale).

---

*Prossimo: [Osservabilità, Logging, Telemetria](09-osservabilita-logging-telemetria.md)*
