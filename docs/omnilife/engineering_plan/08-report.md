# 08 · Report Finale — Engineering Plan

> Consuntivo: file creati, file modificati, rischi principali, blocchi critici, prerequisiti per iniziare il codice.

## 1. File creati

Tutti nuovi, in `docs/omnilife/engineering_plan/`:

| File | Contenuto |
|---|---|
| `README.md` | Indice dell'Engineering Plan |
| `00-metodo-tracciabilita-definizioni.md` | ID scheme (Epic/Feature/Story/Task), template generico a 6 Task, Definition of Ready, Definition of Done |
| `01-mvp-scope-e-backlog.md` | MVP scope (richiamo), 16 Epic, elenco Feature per Epic, dettaglio EPIC-00 (fondazione) |
| `02-matrici-epic-feature-story-task.md` | Matrice Epic→Feature→Story completa; Story→Task dettagliata per l'Epic pilota (Attività) + aggregata per gli altri 14 |
| `03-dipendenze-grafo-percorso-critico.md` | Grafo delle dipendenze a livello di Epic, tabella dipendenze esplicite, percorso critico a 5 anelli |
| `04-roadmap-milestone-release-plan.md` | Innesto degli Epic nella cornice temporale esistente (T0-T52+), 7 nuove milestone ingegneristiche, release plan |
| `05-pratiche-di-sviluppo.md` | Branching strategy, convenzioni Git, code review checklist (10 punti), testing strategy (riuso tassonomia UT/IT/E2E/PT/AT/ST) |
| `06-release-rollback-flag-config-migrazioni.md` | Release strategy, rollback strategy, gestione feature flag, gestione configurazioni (4 tipi distinti), piano di migrazioni |
| `07-refactoring-e-manutenzione.md` | Registro debito tecnico, piano di refactoring, piano di manutenzione (8 attività ricorrenti) |
| `08-report.md` | Questo documento |

**Totale**: 10 file, **16 Epic**, **~42 Feature**, **158 Story** (153 da Functional Bible + 5 `ENG-*` di fondazione), **6 Task-tipo** applicati uniformemente, **7 milestone ingegneristiche nuove**.

## 2. File modificati

Solo gli **indici**, come per ogni Bible precedente:

| File | Modifica |
|---|---|
| `docs/omnilife/README.md` | Aggiunto l'Engineering Plan alla nota sulla gerarchia dei documenti |
| `README.md` (root repo) | Aggiunto un ottavo livello nella descrizione della documentazione OmniLife |

Nessun documento di Product Bible, Functional Bible, UX Bible, Data Model Bible, Technical Architecture Bible, Design System Bible o della documentazione tecnica originaria (`docs/omnilife/00-09`) è stato modificato — questo piano **innesta**, non riscrive.

## 3. Rischi principali (per lo sviluppo, non ripetuti dal registro rischi esistente)

Il registro rischi di prodotto/tecnico esiste già ([docs/omnilife/08-analisi-rischi.md](../08-analisi-rischi.md)) e resta la fonte primaria — qui solo i rischi **specifici all'esecuzione di questo piano**, non già coperti:

| Rischio | Esposizione | Mitigazione |
|---|---|---|
| **EPIC-00 sottostimato**: le 5 Story `ENG-*` di fondazione sembrano "infrastruttura invisibile" e rischiano compressione di tempo sotto pressione di consegna | Alta — blocca tutto il resto (§4) | Trattarlo come l'equivalente ingegneristico dello spike CRDT già protetto in Fase 0 (doc 09): nessuna Story di modulo inizia il proprio T1/T2 prima che ENG-00-1…3 siano in DoD, senza eccezioni negoziate sotto scadenza |
| **EPIC-TASK come pilota rivela problemi architetturali tardi** | Media-Alta — un difetto strutturale scoperto dopo aver già iniziato Finanze/Abitudini in parallelo richiede refactoring su più Epic contemporaneamente | Il percorso critico (§[03](03-dipendenze-grafo-percorso-critico.md)) mette esplicitamente Attività *prima* dell'avvio pieno di Finanze/Abitudini per questo motivo — rispettare l'ordine anche se la pressione di roadmap spingerebbe a parallelizzare tutto subito |
| **Proliferazione di feature flag non rimossi** | Media — debito silenzioso che complica ogni release successiva | Regola esplicita di rimozione entro 2 release train (§[06 §3](06-release-rollback-flag-config-migrazioni.md)), verificata nel gate di debito tecnico di fine Fase |
| **Story Should/Could sviluppate con scheda solo sintetica** | Media — viola la regola Functional Bible §1.3 ("nessuna funzione entra in sviluppo senza scheda estesa approvata") se non disciplinato | La Definition of Ready (§[00 §3](00-metodo-tracciabilita-definizioni.md)) lo rende un blocco esplicito, non un promemoria |
| **Compattezza del piano vs. esaustività**: la matrice Story→Task è aggregata per 14 Epic su 15 (§[02 §4](02-matrici-epic-feature-story-task.md)) | Bassa-Media — un team che legge solo la forma aggregata potrebbe sottostimare il lavoro reale di una Story specifica | La regola di applicazione del template è dichiarata esplicitamente generica: ogni Story eredita i 6 Task salvo le eccezioni dichiarate (Calendario/Salute/servizi senza UI propria) — nessuna ambiguità sulla regola, solo sulla sua ripetizione tabellare |

## 4. Blocchi critici (prerequisiti bloccanti, non aggirabili)

| Blocco | Perché è bloccante | Sblocca |
|---|---|---|
| `ENG-00-1` (scaffold a 6 layer) | Nessun modulo può rispettare "nessuna dipendenza vietata" senza il meccanismo che la renda verificabile | Ogni Epic modulo |
| `ENG-00-2` (Bus Eventi + Grafo + Registro Moduli) | I due soli canali di comunicazione cross-modulo non esistono altrimenti | Ogni Feature "Integrazione Grafo" (TASK-015, HAB-012, NOTE-003, GOAL-002) |
| `ENG-00-5` (confine di sicurezza) | Nessun dato può attraversare L5→L6 in modo conforme alla promessa E2E senza questo | `EPIC-SYNC` per intero |
| Esito dello **spike di convergenza** (già previsto Fase 0, doc 09, go/no-go a T6) | Determina se il modello di sincronizzazione dichiarato in Data Model Bible §8/11 è implementabile come descritto o richiede il fallback LWW già previsto | `EPIC-SYNC`, e indirettamente ogni Epic (nessun modulo è utile senza sincronizzazione affidabile) |
| Completamento di `EPIC-TASK` (Epic pilota) in DoD | Prima prova end-to-end dell'intera catena (dominio→eventi→UI→design system→test) | Fiducia per procedere in parallelo su Finanze/Abitudini senza rischio strutturale nascosto |

## 5. Prerequisiti per iniziare il codice

Nell'ordine in cui devono essere soddisfatti prima che la prima riga di codice di produzione (non di spike) venga scritta:

1. **Decisione tecnologica** (esplicitamente fuori perimetro di questo piano e di tutte le Bible precedenti): linguaggio/i, framework applicativo, motore di persistenza locale, provider cloud per il confine L6, libreria di cifratura. Nessuna Story può iniziare il proprio Task T1 prima che queste decisioni esistano — sono l'unico vero prerequisito "mancante" nell'intera catena documentale.
2. **Esito positivo dello spike di convergenza** (§4) — o l'adozione esplicita e documentata del fallback LWW.
3. **`ENG-00-1…3` in Definition of Done** (§4).
4. **Design review crittografica esterna approvata** (già richiesta in Fase 0, doc 09) — precondizione per `ENG-00-5`.
5. **Ambiente di CI/CD minimo funzionante** con i gate della Testing Strategy (§[05 §4](05-pratiche-di-sviluppo.md)) attivi almeno per `UT` — coerente con "la pipeline nasce prima del prodotto" (doc 09, Fase 0).
6. **Design System Bible token implementati come artefatti consumabili** (`ENG-00-4`) — nessuna Story T4/T5 può iniziare prima.

## 6. Coerenza con la documentazione esistente (verifica di chiusura)

- Ogni Epic corrisponde 1:1 a un modulo già mappato in Technical Architecture Bible; ogni Story è un ID Functional Bible invariato; ogni Feature è un raggruppamento nuovo ma non una ridescrizione.
- La cornice temporale (Fasi, T0-T52+) e il registro rischi restano quelli già esistenti — questo piano li **usa**, non li duplica né li sostituisce.
- La gerarchia documentale resta: Product Constitution → Product Bible → Functional Bible → UX Bible → Data Model Bible → Technical Architecture Bible → Design System Bible → **Engineering Plan** → codice (non ancora iniziato). Nessuna scelta di linguaggio, framework, provider cloud o database fisico è stata presa in questo piano, come da mandato.

---

*Indice: [README](README.md)*
