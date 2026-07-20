# 04 · Plugin Architecture (Contratto di Modulo)

> Eredita [00](00-principi-architetturali.md)…[03](03-event-driven-architecture.md). Formalizza in termini architetturali ciò che la Product Constitution (Titolo VIII, art. 181-195) impone al prodotto e ciò che la struttura stessa dei documenti di modulo della Functional Bible già presuppone ([MFC §9](../functional_bible/00-modello-funzionale-comune.md#9-struttura-standard-dei-documenti-di-modulo): ogni modulo dichiara scopo, funzioni, comportamenti, stati, regole, eventi). **Nota di metodo**: questo documento è una sintesi architetturale originale — non copia una specifica di manifest da alcuna Bible sorgente, che non ne definisce il formato tecnico; ne deriva la forma dai vincoli già stabiliti.

## 1. Perché un'architettura a plugin fin dal primo modulo

Il Product Bible (Business Strategy §6) prevede un marketplace di moduli di terze parti in fase 3+; la Constitution (art. 175, 186) impone che "l'ecosistema erediti integralmente" le stesse regole dei moduli interni. **Conseguenza architetturale**: non esiste una versione "semplice" dell'architettura per i moduli interni e una "seria" per quelli di terze parti — **ogni modulo, dal primo giorno, è trattato come se fosse un plugin**, incluso Attività o Finanze. Questo è coerente con Constitution art. 86 (Functional Bible, doc 16): "il contratto dei moduli è progettato come se fosse pubblico dal giorno 1: la disciplina di oggi è il marketplace di domani."

## 2. Il Contratto di Modulo (concettuale — non un formato)

Ogni modulo, per esistere nel sistema, dichiara — non implementa ancora, **dichiara** — quattro categorie di informazione. Questa quadripartizione è dedotta direttamente dalla struttura che ogni documento della Functional Bible già segue (MFC §9) e dal Data Model Bible (ownership, eventi):

| Categoria del contratto | Corrisponde a (Functional Bible) | Corrisponde a (Data Model Bible) |
|---|---|---|
| **Identità** | Intestazione "Scopo e tracciabilità" di ogni documento di modulo | `DM-SYS-03 ModuleActivation.modulo` |
| **Entità possedute** | Implicito in ogni scheda funzione (campi, regole) | Le entità elencate nella scheda del modulo, es. `DM-TASK-01/02/03` |
| **Contributi di superficie** | HOME-002 (composizione dinamica), CAPT-004 (tipi riconosciuti dal parser), SRCH-001 (campi indicizzabili) | — |
| **Eventi ed autorizzazioni** | Sezione "Eventi" di ogni modulo (MFC §8); tabelle permessi (es. CAL-R-03, HLTH-001) | `DM-LINK-01`, eventi in [Data Model Bible §13.4](../data_model_bible/13-erm-e-matrici.md) |

### 2.1 Identità
Nome, versione del modulo, versione minima di Contratto di Modulo richiesta per essere ospitato (compatibilità, [12-versionamento](12-versionamento-architettura.md)).

### 2.2 Entità possedute
L'elenco dei tipi di entità che il modulo introduce nel Data Model Bible, con la propria strategia di versionamento ([Data Model Bible §7](../data_model_bible/00-modello-dati-comune.md)). Nessun altro modulo può dichiarare di possedere le stesse entità.

### 2.3 Contributi di superficie (verso L2)
Ciò che il modulo offre alla composizione orchestrata da L2, senza che L2 debba conoscerne i dettagli interni: una proiezione per la card Home (HOME-002), i tipi riconoscibili dal parser di Cattura (CAPT-004), i campi indicizzabili dalla Ricerca (SRCH-001), le voci di menu della Galleria (GAL-001).

### 2.4 Eventi e permessi
Gli eventi pubblicati e sottoscritti (§[03](03-event-driven-architecture.md)) e i permessi di sistema richiesti, con il comportamento dichiarato a permesso negato (pattern già normativo: CAL "nessun permesso → modulo utile ridotto", HLTH idem — mai un modulo che smette di funzionare per un permesso negato).

## 3. Ciclo di vita architetturale di un modulo

```
DICHIARATO (il contratto esiste nel Registro Moduli, ma non è attivo)
      │  GAL-002 (attivazione)
      ▼
ATTIVO (il modulo riceve eventi, pubblica eventi, contribuisce a L2)
      │  GAL-003 (disattivazione) — MFC-R-13: i dati restano, invisibili
      ▼
DISATTIVATO (il modulo non riceve né pubblica; le sue entità restano nel
             Data Model, sospese nel Grafo — INV su GraphLink, Data Model
             Bible §16)
      │  riattivazione
      ▼
ATTIVO (stato identico a prima della disattivazione)
```

Questo ciclo è governato dal Registro Moduli (`DM-SYS-03 ModuleActivation`, L4) — **l'unica fonte di verità** su quali moduli sono invocabili in un dato momento (§[02.4](02-moduli-responsabilita-boundaries.md)).

## 4. Sandboxing concettuale (per i moduli di terze parti, fase 3+)

Coerente con Constitution art. 189-195 (sandbox, permessi granulari, review, kill-switch): un modulo di terze parti opera dietro le **stesse porte** di un modulo interno (§2), con tre differenze architetturali aggiuntive, qui dichiarate come principi (il meccanismo di enforcement è una decisione di implementazione, rinviata):

1. **Enumerazione esplicita dei permessi**: un modulo di terze parti dichiara ogni porta di L4/L5 che intende usare; l'accesso a porte non dichiarate è negato per costruzione (principio di minimo privilegio, coerente con C-art. 4, 32).
2. **Nessun accesso diretto a un altro modulo, nemmeno di terze parti**: la regola di [02 §3-4](02-moduli-responsabilita-boundaries.md) si applica identica, senza eccezioni per i plugin.
3. **Interruttore di emergenza (kill-switch)**: il Registro Moduli deve poter forzare la transizione di un modulo verso "disattivato" da una fonte esterna al modulo stesso (governance del marketplace, Constitution art. 189-191) — un plugin non può impedire la propria disattivazione.

## 5. Perché questo non è "over-engineering" per un MVP di 3 moduli

Obiezione naturale: l'MVP ha solo Attività, Finanze, Abitudini (D-03) — perché trattarli già come plugin? Risposta architetturale: il costo di **non** farlo dal primo modulo è il refactoring strutturale necessario per introdurlo più tardi (violando la regola "nessun modulo conosce un altro modulo" già nel primo modulo scritto), mentre il costo di farlo da subito è marginale se l'architettura è già a layer (§[01](01-architettura-generale-e-layer.md)) — il Contratto di Modulo è semplicemente la formalizzazione esplicita di confini che l'architettura a layer impone comunque.

---

*Prossimo: [Offline-First, Sincronizzazione, Caching](05-offline-first-sincronizzazione-caching.md)*
