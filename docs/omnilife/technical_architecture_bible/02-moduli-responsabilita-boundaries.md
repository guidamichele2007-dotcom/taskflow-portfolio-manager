# 02 · Moduli, Responsabilità, Boundaries

> Eredita [00](00-principi-architetturali.md) e [01](01-architettura-generale-e-layer.md). I moduli qui elencati coincidono esattamente con i moduli della [Functional Bible](../functional_bible/README.md) — nessun modulo nuovo, nessuna fusione: questa Bible aggiunge solo la vista architetturale (responsabilità, confini, dipendenze) a ciò che è già specificato funzionalmente.

## 1. Elenco dei moduli e responsabilità architetturale

| Modulo (L3 Dominio) | Responsabilità architetturale | Entità possedute (Data Model Bible) | Rif. Functional Bible |
|---|---|---|---|
| **Core** (non un modulo di dominio: orchestratore di L2 + proprietario del Grafo in L4) | Composizione Home, Onboarding, Galleria moduli, Revisione settimanale | `DM-SYS-03 ModuleActivation`, `DM-LINK-01 GraphLink` (posseduto dal Core, non da un modulo — [Data Model Bible §4.2](../data_model_bible/00-modello-dati-comune.md)) | Doc 01 |
| **Cattura** (servizio Core, L4, non modulo di dominio) | Instradamento del testo/voce catturato verso il modulo di destinazione o l'Inbox | `DM-CAPT-01 CaptureInboxItem` | Doc 02 |
| **Attività** | Possiede il ciclo di vita di Task/Liste/Sottotask | `DM-TASK-01/02/03` | Doc 03 |
| **Finanze** | Possiede transazioni, conti, categorie, budget, obiettivi di risparmio | `DM-FIN-01…05` | Doc 04 |
| **Abitudini** | Possiede abitudini ed esecuzioni, calcola l'aderenza resiliente | `DM-HAB-01/02` | Doc 05 |
| **Calendario** | Media (non possiede) gli eventi del provider di sistema; possiede i TimeBox | `DM-CAL-01/02/03` (deroga totale su 02) | Doc 06 |
| **Note** | Possiede note e versioni a snapshot | `DM-NOTE-01/02` | Doc 07 |
| **Salute** | Media (non possiede) le letture della piattaforma di sistema; possiede le metriche manuali | `DM-HLTH-01/02` (deroga totale su 01) | Doc 08 |
| **Obiettivi** | Possiede l'entità Goal; aggrega — non possiede — i contributi di altri moduli tramite GraphLink | `DM-GOAL-01` | Doc 09 |
| **Ricerca** (servizio Core, L4) | Proiezione derivata e ricostruibile da ogni modulo attivo | Nessuna entità propria (indice derivato, [Data Model Bible §11](../data_model_bible/00-modello-dati-comune.md)); `DM-SRCH-01` locale | Doc 10 |
| **Notifiche** (servizio Core, L4) | Broker centrale: nessun modulo notifica direttamente | `DM-NTF-01` | Doc 11 |
| **Widget** (proiezione di L1, non modulo autonomo) | Superficie di presentazione alternativa che invoca gli stessi casi d'uso di L2 | — | Doc 12 |
| **Sync/Backup/Export** (servizi Core, L4) | Motore di convergenza, snapshot, esportazione | Nessuna entità propria: opera sulle entità di ogni modulo | Doc 13 |
| **Impostazioni/Sicurezza** (servizio Core, L4 + entità di sistema in L3) | Catalogo chiuso di configurazione, autenticazione, cifratura concettuale | `DM-SYS-01/02/04/05/06` | Doc 14 |
| **Insight** (servizio Core, L4) | Osservatore passivo degli eventi di dominio, non ha stato di dominio proprio | `DM-INS-01/02` | Doc 15 |

**Nota architetturale**: alcuni "moduli" della Functional Bible non sono Domini di L3 in senso stretto ma servizi trasversali di L4 o proiezioni di L1 — la Functional Bible li documenta con la stessa struttura per uniformità funzionale (MFC §9), ma architetturalmente occupano layer diversi. Questa distinzione **non introduce comportamento nuovo**: è la lettura architetturale di ciò che la Functional Bible già implica (es. NTF-001: "nessun modulo notifica direttamente" implica strutturalmente che Notifiche sia un servizio condiviso, non un dominio pari agli altri).

## 2. Boundaries (confini) di un modulo di Dominio (L3)

Ogni modulo di dominio è un **confine di coerenza** (consistency boundary): possiede le proprie entità (Data Model Bible), applica le proprie regole di business (R-* della Functional Bible) e non permette a nessun altro componente di modificare direttamente il proprio stato interno.

- **Un modulo espone solo**: (a) casi d'uso invocabili da L2 (creare/modificare/eliminare/ecc. — il [Generic Entity Flow](../ux_bible/00-modello-ux-comune.md#9-il-flusso-generico-del-ciclo-di-vita-di-unentità-generic-entity-flow--gef) è il caso d'uso più comune), (b) eventi pubblicati (§[03](03-event-driven-architecture.md)), (c) una proiezione per la Home (contratto di card, HOME-002), (d) eventuali contributi alla cattura (chip riconosciuti dal parser) e alla ricerca (campi indicizzabili).
- **Un modulo non espone mai**: le proprie entità interne per lettura/scrittura diretta da parte di un altro modulo — ogni accesso cross-modulo passa dal Grafo (GraphLink) o dal bus eventi.

## 3. Dipendenze consentite

| Da → A | Consentita? | Come |
|---|---|---|
| Modulo di dominio → Servizi Core (L4) | ✅ Sì | Tramite porte esposte da L4 (es. Attività → porta "richiedi notifica" del broker) |
| Modulo di dominio → Adattatori di piattaforma (L5) | ✅ Sì, ma solo tramite porta dichiarata da L4/L3 | Es. il modulo Calendario dipende dalla porta "fornitore calendario di sistema", mai dall'Adattatore concreto |
| Modulo di dominio → Modulo di dominio | ❌ **Mai direttamente** | Solo tramite eventi (§[03](03-event-driven-architecture.md)) o GraphLink (Data Model Bible §4.2) |
| L2 Applicazione → più moduli di dominio | ✅ Sì (è il suo compito) | Orchestrazione, mai accesso a dati interni di un modulo |
| L1 Esperienza → L3 Dominio | ❌ Mai direttamente | Sempre tramite L2 |
| Servizio Core (L4) → Modulo di dominio specifico | ❌ Mai in modo nominato | Un Servizio Core (es. Ricerca) opera su un contratto generico ("entità indicizzabile"), mai su "Task" o "Transaction" nominati esplicitamente nel suo codice |
| Adattatore di piattaforma (L5) → qualunque layer interno | ❌ Mai | L5 implementa porte, non conosce chi le consuma |

## 4. Dipendenze vietate (esplicite, con motivazione)

| Dipendenza vietata | Motivazione |
|---|---|
| Un modulo importa il modello dati interno di un altro modulo | Viola l'isolamento del Dominio (C-art. 181, 184); rende impossibile disattivare un modulo senza rompere l'altro (MFC-R-13) |
| Un modulo chiama direttamente una funzione di un altro modulo (bypassando eventi/Grafo) | Crea accoppiamento temporale e d'ordine di inizializzazione, in contraddizione con "nessuna dipendenza implicita" (C-art. 184) |
| Un Servizio Core (L4) contiene una regola di business specifica di un modulo (es. la formula di aderenza delle Abitudini dentro il motore di Sincronizzazione) | Sposterebbe la fonte di verità fuori dal Dominio che la possiede (Data Model Bible §11, calcoli specifici del modulo) |
| L1 (Esperienza) contiene una regola di business o un calcolo (es. calcolare l'aderenza nella UI invece di leggerla da L3) | Duplicherebbe la logica in due posti, violando P31/C-art. 61 e rendendo la UX Bible non l'unica fonte di verità del comportamento osservabile |
| Un Adattatore di piattaforma (L5) contiene logica di dominio | Renderebbe l'Adattatore non sostituibile (viola Liskov, §[00.2](00-principi-architetturali.md)) |
| Il confine L6 (sincronizzazione esterna) riceve dati non cifrati | Violerebbe il vincolo architetturale supremo del prodotto (Product Constitution art. 1-2, 27-28) — questa non è una preferenza, è un invariante di sicurezza (§[10](10-sicurezza-architetturale.md)) |
| Un modulo disattivato continua a essere invocato da L2/L4 | Violerebbe MFC-R-13 (disattivare non tocca i dati, ma neppure deve continuare a produrre effetti); il Registro Moduli (`DM-SYS-03`) è l'unica fonte di verità su quali moduli sono invocabili |

## 5. Il modulo Core come caso speciale

Il "Core" non è un modulo di dominio come gli altri: è **la sede di L2 (orchestrazione) e il proprietario di L4/GraphLink**. Questa distinzione architetturale (assente come tale nella Functional Bible, che tratta "Core" come un modulo documentale per uniformità — MFC §9) è una sintesi propria di questa Bible, coerente con l'osservazione che il Grafo non può essere posseduto da un singolo modulo senza violare l'isolamento (§2) e che la composizione Home richiede necessariamente una vista su più moduli (violando altrimenti "un modulo non conosce un altro modulo" se fosse un modulo qualunque a farlo).

---

*Prossimo: [Event-Driven Architecture](03-event-driven-architecture.md)*
