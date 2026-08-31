# 00 · Modello Dati Comune (MDC)

> **Fonte di verità esclusiva**: [Product Bible](../product_bible/README.md), [Functional Bible](../functional_bible/README.md), [UX Bible](../ux_bible/README.md). Questo documento **non introduce comportamento nuovo**: formalizza in termini di dati ciò che il [Modello Funzionale Comune (MFC)](../functional_bible/00-modello-funzionale-comune.md) e il [Generic Entity Flow (GEF)](../ux_bible/00-modello-ux-comune.md#9-il-flusso-generico-del-ciclo-di-vita-di-unentità-generic-entity-flow--gef) già stabiliscono. Nessun database, nessuna API, nessun codice: solo il modello concettuale.
>
> Regola di lettura: **ciò che un'entità non specifica in deroga si comporta come descritto qui.** Ogni scheda entità ([01](01-entita-sistema.md)…[10](10-entita-notifiche-insight-ricerca.md)) eredita questo modello.

## 1. Convenzioni e tracciabilità

- ID di questa Bible: entità `DM-PREFISSO-##` (es. `DM-TASK-01`), invarianti `INV-##`, decisioni di modellazione `MDEC-##`.
- Ogni entità cita le funzioni della Functional Bible che la producono/consumano (`PREFISSO-###`) e, tramite quelle, eredita la tracciabilità già stabilita verso Product Bible (`P#`, `J#`, `D-##`, `C-art.#`) e UX Bible (`IA-###`, `FLOW-###`). **Questa Bible non ripete quelle citazioni**: le richiama per riferimento.
- Questo documento **non sceglie una tecnologia di persistenza**: "campo", "relazione", "indice" sono concetti logici, non schema fisico (quella è una decisione rinviata, vedi [14-report](14-report.md)).

## 2. Identificatori (INV-01, INV-02)

- **INV-01 — Unicità e stabilità**: ogni entità nasce con un identificatore univoco globale, generabile offline (MFC-R-02), mai riassegnato — nemmeno dopo l'eliminazione definitiva. Estende alle entità dati lo stesso principio già applicato agli ID funzionali (Functional Bible §1.1: "gli ID sono stabili per sempre").
- **INV-02 — Ordinabilità temporale**: l'identificatore è concepito per essere ordinabile per momento di creazione senza bisogno di un campo separato, per supportare la sincronizzazione offline-first e la costruzione di cronologie coerenti anche multi-dispositivo (coerente con MFC-R-02 e con la convergenza multi-device di MFC-R-08). Il formato esatto dell'identificatore è una decisione tecnica rinviata (vedi report).

## 3. Involucro comune (Envelope) — i metadati di ogni entità

Ogni entità del grafo (eccetto le deroghe dichiarate in §9) possiede questo involucro comune, derivato da MFC-R-02/05/07:

| Campo concettuale | Descrizione | Obbligatorio | Rif. |
|---|---|---|---|
| `id` | Identificatore univoco e stabile | Sì | INV-01 |
| `tipo` | Il tipo di entità (Task, Transazione, Abitudine…) | Sì | MFC-R-02 |
| `account_proprietario` | L'account a cui appartiene (§5 Ownership) | Sì (salvo §5.1) | MFC-R-20 |
| `versione_schema` | Versione dello schema del modulo con cui l'entità è stata scritta l'ultima volta | Sì | MFC-R-02; INV-09 |
| `creato_il` / `creato_da_dispositivo` | Timestamp di creazione e dispositivo di origine | Sì | MFC-R-02 |
| `modificato_il` / `modificato_da_dispositivo` | Ultimo aggiornamento e dispositivo | Sì | MFC-R-07 |
| `stato_ciclo_vita` | attiva · archiviata · cestinata · eliminata_definitivamente (§6) | Sì | MFC §2 |
| `cestinata_il` | Presente solo se `stato_ciclo_vita = cestinata`; usato per calcolare la scadenza dei 30 giorni | Condizionale | MFC-R-10; INV-06 |
| `campi_specifici` | I campi propri del tipo di entità (documentati nelle schede [01](01-entita-sistema.md)…[10](10-entita-notifiche-insight-ricerca.md)) | Sì | — |

**MDEC-01**: l'involucro comune è una decisione di modellazione (non una funzione nuova): rende esplicito ciò che MFC-R-02/05/07/09 già impongono a ogni entità, così che ogni scheda entità non debba ripeterlo.

## 4. Tipi di relazione

Due tipi di relazione, mai un terzo, per mantenere il modello semplice (coerente con P26/P32 applicati ai dati):

### 4.1 Relazioni strutturali (proprietà)

Relazioni obbligatorie, definite dal modulo proprietario, dove un'entità **appartiene** a un'altra e il ciclo di vita è in parte dipendente (es. una Transazione appartiene a un Conto; un Sottotask appartiene a un Task). Cardinalità e comportamento alla cancellazione dell'entità padre sono specificati nella scheda del modulo (es. FIN: eliminare un conto richiede decisione sulle transazioni — FIN comportamenti specifici).

### 4.2 Collegamenti del grafo (GraphLink)

Relazioni **opzionali, tipizzate, bidirezionali, mai proprietarie**, tra entità anche di moduli diversi: la sostanza del "grafo dati personale" già presente nella Functional Bible come dipendenza dichiarata di più moduli (NOTE-003 "Grafo"; TASK-015 "arco nel grafo"; HAB-012; GOAL-002 "Grafo; moduli"). **MDEC-02**: questa Bible formalizza tali collegamenti in un'unica entità concettuale, [`DM-LINK-01` (GraphLink)](02-entita-cattura-grafo.md#dm-link-01--graphlink-collegamento), invece di modellare un tipo di relazione diverso per ogni coppia di moduli — è l'applicazione ai dati del principio "un'anatomia sola" (P33) e del vincolo "nessuna dipendenza implicita, i moduli comunicano solo per eventi e collegamenti" (C-art. 184).

- **INV-03**: un GraphLink non può mai esistere con un endpoint verso un'entità che non esiste. Se un'entità raggiunge lo stato `eliminata_definitivamente`, ogni GraphLink che la referenzia viene rimosso **come conseguenza di quella distruzione, mai prima** (coerente con MFC-R-12: l'eliminazione — non definitiva — di un'entità non elimina mai le collegate, sospende soltanto).
- **INV-04**: i GraphLink offline-creati convergono per unione insiemistica (mai persi, i duplicati si normalizzano) — comportamento già dichiarato per i contributi di Obiettivo (Functional Bible, modulo GOAL, edge case: "i collegamenti sono un set CRDT") ed esteso qui a ogni GraphLink per coerenza.

## 5. Ownership (proprietà dei dati)

- **MVP (single-owner)**: ogni entità appartiene a un solo account (MFC-R-20: "un solo utente per account; ogni dato è privato per default"). Non esiste condivisione tra account nell'MVP.
- **5.1 Caso speciale — utente anonimo**: prima della registrazione (ONB-004, D-05), l'"account proprietario" coincide con l'identità locale del dispositivo; alla registrazione, la proprietà migra all'account senza perdita né duplicazione (ONB-007). Questo è l'unico caso in cui `account_proprietario` cambia nel tempo per una stessa entità.
- **Condivisione futura (fuori scope MVP/v1.x)**: la Product Bible prevede "spazi condivisi selettivi" (famiglia/coppia) come espansione di fase 4 (Business Strategy §4.3). Il modello di ownership multi-account (chi vede/modifica cosa in uno spazio condiviso) **è una decisione rinviata** esplicitamente a quella fase (vedi [14-report](14-report.md)) — nessuna entità di questa Bible presuppone una struttura di permessi multi-utente.

## 6. Ciclo di vita (eredita integralmente MFC §2)

Il ciclo `creazione → attiva ⇄ modificata → archiviata → cestinata → eliminata definitivamente` (MFC §2.1-2.3) si applica a ogni entità **salvo le deroghe esplicite di §9**. Non ripetuto qui: vedi [MFC §2](../functional_bible/00-modello-funzionale-comune.md#2-il-ciclo-di-vita-universale-delle-entità) e il [GEF della UX Bible](../ux_bible/00-modello-ux-comune.md#9-il-flusso-generico-del-ciclo-di-vita-di-unentità-generic-entity-flow--gef).

- **INV-05**: `stato_ciclo_vita` segue solo le transizioni ammesse dal diagramma MFC §2 (nessuno stato intermedio non documentato).
- **INV-06**: ogni entità con `stato_ciclo_vita = cestinata` ha una scadenza calcolabile = `cestinata_il + 30 giorni` (MFC-R-10); allo scadere, transizione automatica a `eliminata_definitivamente`.

## 7. Versionamento e cronologia (eredita MFC §2.4/R-07, deroghe dichiarate)

Due strategie di versionamento, mai una terza (coerenza con P26):

| Strategia | Applicazione | Descrizione |
|---|---|---|
| **Per-campo** (default) | Task, Transazione, Conto, Categoria, Budget, Abitudine, Obiettivo | Ogni modifica genera una voce di cronologia (campo, valore precedente, timestamp, dispositivo); profondità minima 90 giorni o 50 revisioni (MFC-R-07) |
| **A snapshot** (deroga dichiarata) | Nota | L'intero contenuto si versiona a istantanee (NOTE-006): il testo libero non ha "campi" discreti. Il ripristino di uno snapshot precedente è una nuova versione, mai una riscrittura della storia (MFC §2.4) |

- **Nessuna cronologia nostra** per: eventi di Calendario esterni, dati grezzi della piattaforma Salute (§9 — la cronologia è del provider, non nostra).
- **INV-07**: il ripristino di una versione precedente (qualunque strategia) è sempre una nuova voce di cronologia, mai una cancellazione delle voci successive.

## 8. Sincronizzazione logica e gestione dei conflitti (concettuale — eredita MFC §3, D-02/ADR-3)

- La sincronizzazione trasporta **stato**, non eventi (MFC §8): ogni entità converge in modo indipendente dalle altre.
- **Convergenza per-campo**: per i campi scalari (testo breve, numero, data, enum), la convergenza usa un confronto logico di versione (non l'orologio di parete del dispositivo, per resistere a MFC-E-10) e non un dialogo di conflitto — la modifica logicamente più recente vince, la perdente resta in cronologia (MFC-R-08).
- **Convergenza per insiemi**: i GraphLink e ogni struttura a insieme (es. l'insieme dei contributi di un Obiettivo) convergono per unione, mai per sostituzione (INV-04).
- **Deroga Nota**: merge per paragrafo dove non ambiguo; altrove, l'intero snapshot segue la regola per-campo con la versione precedente conservata (§7; nulla è mai perso, coerente con NOTE-AC-03).
- **INV-08**: nessuna entità mostra mai all'utente uno stato "in conflitto" (MFC §4 — il conflitto non è uno stato visibile, esiste solo in cronologia).

## 9. Deroghe dichiarate al modello comune (entità non pienamente "nostre")

| Entità | Deroga | Motivo |
|---|---|---|
| Evento di Calendario esterno (`DM-CAL-02`) | Nessun ciclo di vita MFC, nessuna cronologia nostra, nessuna sincronizzazione nostra: la fonte di verità è il provider di sistema | CAL-R-01, C-art. 61 |
| Lettura dalla piattaforma Salute (`DM-HLTH-01`) | Sola lettura, cache locale a finestra (90 giorni), **mai sincronizzata sul cloud**, nessun export nostro dei dati grezzi | HLTH-R-02, C-art. 43/45 |
| Ricerche recenti (`DM-SRCH-01`) | Locale al dispositivo, esclusa da sincronizzazione e backup | SRCH-R-01, C-art. 45 |
| Config delle regole di Insight (`DM-INS-01`) | Non è un dato utente: è configurazione di sistema scaricata (firmata), sola lettura per l'app | INS-003 |

## 10. Tagging e categorizzazione (filosofia)

**Nessuna tassonomia libera generalizzata.** Ogni modulo ha la propria classificazione tipizzata, coerente con P32 (gerarchie profonde sono un fallimento) e C-art. 61 (nessuna duplicazione di concetti):

| Modulo | Meccanismo di classificazione | Livelli |
|---|---|---|
| Attività | Area → Lista (TASK-005) | 2 (max) |
| Finanze | Categoria → Sottocategoria (FIN-002) | 2 (max) |
| Abitudini | Nessuna tassonomia: solo collegamento opzionale a un Obiettivo (GraphLink) | — |
| Note | Nessuna tassonomia: i collegamenti `@entità` sono la struttura (NOTE-R-01) | — |
| Obiettivi | Nessuna tassonomia: la struttura sono i "fronti" (i GraphLink stessi) | — |

**MDEC-03**: Area (Attività) e Categoria (Finanze) condividono la stessa forma concettuale — una gerarchia tipizzata a 2 livelli — pur essendo entità distinte per modulo (nessuna tabella condivisa: sarebbe un accoppiamento tra moduli vietato da C-art. 181). Si tratta di un pattern riconosciuto, non di un'entità comune.

## 11. Ricerca (modello concettuale)

- L'indice di ricerca (SRCH-001) è una **proiezione derivata** costruita a partire dalle entità dei moduli attivi (titolo, contenuto testuale, categoria/lista) — non è esso stesso una fonte di verità e deve essere interamente ricostruibile dai dati originali senza perdita (coerente con lo stato "degradato/in ricostruzione" di MFC §4).
- Le entità con `stato_ciclo_vita = cestinata` sono escluse dall'indice di default; incluse solo con il filtro esplicito (SRCH-006).
- Le entità di moduli sensibili bloccati (MFC-R-21) sono indicizzate ma i risultati sono offuscati finché l'app non è sbloccata (MFC-R-22).

## 12. Ordinamenti e filtri (convenzioni comuni)

- **INV-10**: ogni entità elencabile in una lista ha un ordinamento di default dichiarato dal proprio modulo (es. TASK-R-02, FIN-R-04, HAB-R-06); un eventuale ordine manuale esplicito dell'utente (campo opzionale `ordine_manuale`) prevale sempre dove presente (P82).
- **INV-11**: i filtri disponibili per ogni tipo di entità sono un insieme chiuso dichiarato dal modulo (es. TASK-R-05, FIN-010) — mai un costruttore di query libero (P16).

## 13. Esportazione e importazione (modello concettuale)

- **Criterio di verità** (già stabilito in EXP-001): da un export completo + un'installazione pulita si deve poter ricostruire manualmente ogni informazione utente. Include: entità con il loro involucro, collegamenti del grafo, cronologie, definizioni (categorie, liste), impostazioni esportabili. Esclude: chiavi crittografiche, ricerche recenti, dati grezzi della piattaforma Salute (§9).
- **INV-12**: l'export non ha mai limiti di piano, frequenza o dimensione (EXP-R-01, C-art. 7-8).
- **Import**: ogni importazione (TASK-R-06, FIN-R-06) è un processo di **anteprima → conferma → annullamento in blocco**, mai un'scrittura diretta; la deduplicazione è euristica e proposta, mai automatica silenziosa (MFC-E-13, C-art. 67).

## 14. Audit trail (modello concettuale)

L'audit trail coincide con la cronologia per-campo o a snapshot (§7): non esiste un log di controllo separato per l'utente finale. Per la sicurezza dell'account (tentativi di accesso, dispositivi, rigenerazioni di chiavi), l'audit è descritto nella scheda [Account/Device](01-entita-sistema.md) — locale + lato server per gli eventi di autenticazione (mai le password, MFC §5).

## 15. Permessi (modello concettuale, MVP)

Nell'MVP e in v1.x non esiste un modello di permessi multi-utente: ogni entità ha un solo proprietario (§5). L'unica granularità di accesso è:

1. **Blocco per modulo sensibile** (MFC-R-21): un controllo di accesso *locale al dispositivo* (biometria/codice), non un permesso sui dati stessi — i dati restano gli stessi, cambia solo la loro visibilità sullo schermo.
2. **Permessi di sistema per entità esterne** (Calendario, Salute): concessi dall'utente al sistema operativo, non gestiti da noi (§9).

Il modello di permessi multi-utente (spazi condivisi, fase 4) è **rinviato** (§5, [14-report](14-report.md)).

## 16. Invarianti universali (catalogo, non duplicato nei moduli)

| ID | Invariante | Rif. |
|---|---|---|
| INV-01 | Ogni ID è univoco globale e mai riassegnato | MFC-R-02 |
| INV-02 | Gli ID sono ordinabili temporalmente per costruzione | MFC-R-02, MFC-R-08 |
| INV-03 | Un GraphLink non referenzia mai un'entità eliminata definitivamente | MFC-R-12 |
| INV-04 | I GraphLink convergono per unione insiemistica, mai persi | Edge case GOAL |
| INV-05 | Le transizioni di `stato_ciclo_vita` seguono solo il diagramma MFC §2 | MFC §2 |
| INV-06 | Ogni entità cestinata ha scadenza calcolabile a 30 giorni | MFC-R-10 |
| INV-07 | Il ripristino di una versione è sempre una nuova voce di cronologia | MFC §2.4 |
| INV-08 | Nessuno stato "in conflitto" è mai visibile all'utente | MFC §4 |
| INV-09 | La versione di schema di un'entità non retrocede mai nel tempo | MFC-R-02; decisione tecnica di dettaglio rinviata |
| INV-10 | L'ordine manuale, se presente, prevale sempre sull'ordinamento di default | P82 |
| INV-11 | I filtri per tipo di entità sono un insieme chiuso, mai libero | P16 |
| INV-12 | L'export non ha mai limiti di piano, frequenza o dimensione | C-art. 7-8 |
| INV-13 | Nessuna entità utente esiste senza un `account_proprietario` (incluso il caso anonimo, §5.1) | MFC-R-20 |
| INV-14 | Un'entità archiviata non compare mai nei conteggi delle viste attive, ma è sempre inclusa nella ricerca con filtro esplicito | MFC-R-09; §11 |

---

*Indice: [README](README.md) · Prossimo: [Entità di Sistema e Account](01-entita-sistema.md)*
