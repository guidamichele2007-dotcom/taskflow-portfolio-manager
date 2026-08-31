# 04 · Roadmap Tecnica, Milestone, Release Plan

> Eredita [00](00-metodo-tracciabilita-definizioni.md)…[03](03-dipendenze-grafo-percorso-critico.md). La cornice temporale (fasi, finestre T0-T52+, criteri di uscita) è **già stabilita** in [docs/omnilife/09-piano-di-sviluppo.md](../09-piano-di-sviluppo.md) e nella [Product Bible, Roadmap](../product_bible/13-roadmap.md) — **non ridefinita qui**. Questo documento **innesta gli Epic** (§[01](01-mvp-scope-e-backlog.md)) e il percorso critico (§[03](03-dipendenze-grafo-percorso-critico.md)) dentro quella cornice già esistente, e aggiunge milestone di livello ingegneristico che i due documenti sorgente non specificano.

## 1. Roadmap cronologica (Epic × Fase, richiamo + innesto)

| Fase (fonte: doc 09) | Finestra | Epic attivi | Milestone ingegneristiche nuove |
|---|---|---|---|
| **Fase 0 — Fondamenta** | T0→T8 | `EPIC-00` (tutte le 5 Story), avvio `EPIC-SET`/Account e Sicurezza | **M0**: scaffold a 6 layer buildabile e testabile; **M1**: spike di convergenza multi-device verde (go/no-go già previsto in doc 09) |
| **Fase 1 — MVP** | T8→T30 | `EPIC-TASK` (pilota, T8-T14) → `EPIC-CORE`/Home+Onboarding, `EPIC-SYNC` (T14-T20) → `EPIC-FIN`, `EPIC-CAPT` (parser completo), `EPIC-SRCH`, `EPIC-CORE`/Galleria (T20-T24) → `EPIC-HAB`, `EPIC-NTF`, `EPIC-WID`, `EPIC-SET` completo (già in corso dalla Fase 0) | **M2** (T14): primo Epic (TASK) supera la sua Definition of Done per intero, inclusa sincronizzazione multi-device reale; **M3** (T24): Alpha chiusa — criterio di uscita già in doc 09; **M4** (T30): Beta privata — criterio di uscita già in doc 09 |
| **Fase 2 — Lancio ed espansione** | T30→T52 | `EPIC-CAL` (lettura, se non già chiuso in Fase 1) → `EPIC-CORE`/Revisione, `EPIC-GOAL`, `EPIC-NOTE` (T36-T44) → `EPIC-HLTH`, `EPIC-INS`, `EPIC-CAL`/scrittura (T44-T52) | **M5** (T30/T36): Soft launch → Lancio globale (criteri già in doc 09/doc 07); **M6** (T44): primo Epic v1.x (Obiettivi) supera DoD — valida la scommessa J5/D-01 sul grafo in produzione |
| **Fase 3 — Ecosistema** | T52→ | Apertura `EPIC-00`/Contratto di Modulo a terze parti ([Technical Architecture Bible §04](../technical_architecture_bible/04-plugin-architecture.md)), Epic v2.x residui (FIN-009, HAB-011, NOTE-008/009, GOAL-005) | **M7**: primo modulo di terze parti pubblicato nel marketplace (criterio già in doc 09: "≥10 plugin di qualità nel primo anno") |

## 2. Perché nessuna nuova data assoluta

Le finestre T0-T52+ restano quelle del piano esistente: introdurre una seconda numerazione temporale duplicherebbe una decisione già presa (P31). Il valore aggiunto di questo documento è **cosa succede dentro ogni finestra a livello di Epic**, non *quando* — coerente con il percorso critico di [03](03-dipendenze-grafo-percorso-critico.md), che è la vera novità ingegneristica non presente nel piano esistente (il piano esistente sequenzia per *modulo prodotto*, questa Bible sequenzia per *Epic con dipendenze tecniche esplicite*, incluso l'Epic di Fondamenta che il piano esistente cita solo come "Architettura: Module Contract v1" senza scomporlo).

## 3. Release Plan (cadenza, non date)

| Aspetto | Regola | Fonte |
|---|---|---|
| **Cadenza pre-lancio (Fase 0-1)** | Release train interno ogni 2 settimane (finestre T8-T14, T14-T20, ecc.) | doc 09, già stabilito |
| **Cadenza post-lancio (Fase 2+)** | Release pubblica ogni 3 settimane, rollout graduale 5%→25%→100% | doc 09, già stabilito |
| **Hotfix** | < 48h dalla scoperta di un difetto critico | doc 09, già stabilito |
| **Contenuto di una release** | Un insieme di Story **complete secondo DoD** ([00 §5](00-metodo-tracciabilita-definizioni.md)) — mai una Story parziale rilasciata, coerente con "nessuna funzione entra in sviluppo senza scheda estesa approvata" (Functional Bible §1.3) esteso a "nessuna Story si rilascia senza DoD" |
| **Ordine di composizione di una release** | Segue l'Epic attivo nella finestra corrente (§1); dentro l'Epic, l'ordine di Feature segue le dipendenze deboli interne (es. dentro EPIC-TASK: Creazione Base → Organizzazione → Ricorrenze → Viste → Integrazione Grafo) |

## 4. Milestone di governance (nuove, non presenti nel piano esistente)

| Milestone | Trigger | Azione |
|---|---|---|
| **Gate di Epic** | Un Epic raggiunge il 100% delle sue Story Must in DoD | Revisione formale prima di dichiararlo "pronto per Alpha/Beta/Lancio" — analogo alla "Review di fase" già prevista in doc 09 §Governance, applicata qui a grana di Epic invece che di Fase |
| **Gate di percorso critico** | Uno qualunque dei 5 anelli critici (§[03 §3](03-dipendenze-grafo-percorso-critico.md)) supera la propria finestra prevista di 2+ settimane | Escalation immediata: rivalutazione delle Story Should incluse nella finestra corrente (posticipabili) prima di toccare le Must |
| **Gate di debito tecnico** | Fine di ogni Fase (0/1/2/3) | Revisione del registro di debito tecnico ([07](07-refactoring-e-manutenzione.md)) — nessuna fase chiude con debito non dichiarato |

---

*Prossimo: [Pratiche di Sviluppo](05-pratiche-di-sviluppo.md)*
