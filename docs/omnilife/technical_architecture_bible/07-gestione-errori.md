# 07 · Gestione degli Errori (architettura concettuale)

> Eredita [00](00-principi-architetturali.md)…[06](06-lifecycle-richieste-ed-eventi.md). Il comportamento **osservabile** dagli errori è già interamente specificato in [UX Bible, Error Experience](../ux_bible/10-error-experience.md) e nel [MUC §7](../ux_bible/00-modello-ux-comune.md#7-pattern-universale-di-errore-eredita-mfc--constitution-art-104). Questo documento descrive **dove** nell'architettura ogni categoria di errore nasce, viene intercettata e tradotta in quel comportamento — mai il messaggio stesso, già definito altrove.

## 1. Principio architetturale: gli errori sono un cittadino di prima classe di ogni confine di layer

Ogni confine tra layer ([01 §4](01-architettura-generale-e-layer.md)) è anche un punto di traduzione degli errori: un errore che nasce in un layer interno non attraversa mai un confine senza essere tradotto nel vocabolario del layer che lo riceve. Un errore di L5 (es. "spazio disco esaurito", un concetto di piattaforma) non raggiunge mai L1 nella sua forma originale: attraversa L4/L3/L2, ciascuno dei quali lo arricchisce o lo traduce, finché L1 riceve solo ciò che la [UX Bible](../ux_bible/10-error-experience.md) ha già definito (categoria, messaggio umano, azione riparatrice).

## 2. Categorie di errore e layer di origine

| Categoria | Layer di origine | Layer di traduzione | Riferimento UX Bible |
|---|---|---|---|
| Rete non disponibile | L5 (Adattatore di rete/sync) | L4 (Motore di Sync distingue "eccezione online" da "sync differibile") | [10 §2.1](../ux_bible/10-error-experience.md) |
| Validazione di un campo | L3 (Dominio, invarianti violati) | Nessuna traduzione necessaria: L3 già esprime l'errore nel vocabolario del proprio dominio, L2 lo passa a L1 senza modifiche | [10 §2.2](../ux_bible/10-error-experience.md) |
| Spazio di archiviazione esaurito | L5 (Adattatore di persistenza) | L4 (Servizio di persistenza distingue scritture bloccate da letture ancora possibili) | [10 §2.3](../ux_bible/10-error-experience.md) |
| Sincronizzazione fallita persistente (>72h) | L4 (Motore di Sync, dopo retry esauriti) | L2 (traduce in notifica informativa, mai bloccante) | [10 §2.4](../ux_bible/10-error-experience.md) |
| Autenticazione fallita | L5 (Adattatore verso L6) | L4 (Servizio di Sicurezza applica il backoff, non L5) | [10 §2.5](../ux_bible/10-error-experience.md) |
| Chiave di recupero errata/persa | L4 (Servizio di Sicurezza) | L2 (instrada verso il percorso onesto dichiarato, mai un fallback tecnico) | [10 §2.6](../ux_bible/10-error-experience.md) |
| Importazione non interpretabile | L3 (Dominio che riceve l'import, es. Finanze/Attività) | L2 (presenta l'anteprima con le righe escluse) | [10 §2.7](../ux_bible/10-error-experience.md) |
| Piattaforma esterna non raggiungibile (Calendario/Salute) | L5 (Adattatore di piattaforma) | L3 (il Dominio Calendario/Salute degrada solo la propria fonte, non l'intero modulo — C-art. 122) | [10 §2.8](../ux_bible/10-error-experience.md) |
| Crash / stato imprevisto | Qualunque layer | L1 alla riapertura, tramite lo stato persistito transazionalmente da L3/L5 (MFC-E-02) | [10 §2.9](../ux_bible/10-error-experience.md) |

## 3. Il principio di non propagazione a cascata (C-art. 122, applicato architetturalmente)

**Un errore in un componente di L3/L4 non deve mai impedire il funzionamento degli altri componenti dello stesso layer.** Architetturalmente, questo si ottiene isolando ogni caso d'uso multi-modulo (§[06 §3](06-lifecycle-richieste-ed-eventi.md)) per modulo: se il Dominio Calendario fallisce nel fornire la propria proiezione alla Home, L2 compone comunque le proiezioni degli altri moduli e marca solo quella sezione come degradata — mai un fallimento che si propaga all'intera composizione.

## 4. Retry, backoff, fallback: collocazione, non policy esatta

- **Retry automatico con backoff crescente**: responsabilità di L4 (Motore di Sync, Servizio di Sicurezza per l'autenticazione) — mai di L1/L2, che non devono conoscere il concetto di "tentativo".
- **Retry manuale**: sempre disponibile come azione esposta da L2 a L1, in aggiunta al retry automatico, mai in sua sostituzione (UX-R-026).
- **Fallback**: ogni Servizio Core con una dipendenza esterna (Sync verso L6, Calendario/Salute verso L5) dichiara un comportamento degradato esplicito che non richiede la disponibilità della dipendenza — coerente con l'offline-first (§[05](05-offline-first-sincronizzazione-caching.md)).

## 5. Logging degli errori: collocazione (dettaglio in [09-osservabilita](09-osservabilita-logging-telemetria.md))

Ogni errore, indipendentemente dal consenso alla telemetria, genera una voce di log **locale** al livello in cui è stato originato (L3/L4/L5) — mai contenuti utente nel log (C-art. 22). La trasmissione remota di un log è un'azione distinta, sempre opt-in, mai automatica (UX-R-025) — architetturalmente, il componente di logging locale e il componente di trasmissione telemetrica sono **due componenti separati** con un confine di consenso esplicito tra loro.

## 6. Errori che non devono mai accadere (invarianti, non gestione)

Alcuni "errori" non sono gestiti perché l'architettura li rende strutturalmente impossibili, coerente con gli invarianti del Data Model Bible:

- Una scrittura parziale visibile: impossibile per costruzione (transazionalità di L3/L5, MFC-E-02, INV-05).
- Un conflitto di sincronizzazione visibile all'utente: impossibile per costruzione (L4 risolve sempre internamente, Data Model Bible INV-08).
- Un GraphLink verso un'entità inesistente: impossibile per costruzione (INV-03, verificato al momento della rimozione definitiva, non successivamente).

Questi non compaiono nel catalogo UX degli errori perché **l'architettura li previene**, non li gestisce — distinzione importante: un errore "gestito bene" è già un compromesso; un errore "reso impossibile" è la soluzione architetturale preferita ovunque sia raggiungibile.

---

*Prossimo: [Scalabilità ed Estendibilità](08-scalabilita-estendibilita.md)*
