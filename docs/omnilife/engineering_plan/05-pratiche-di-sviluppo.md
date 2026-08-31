# 05 · Pratiche di Sviluppo — Branching, Convenzioni Git, Code Review, Testing Strategy

> Eredita [00](00-metodo-tracciabilita-definizioni.md). Indipendente da ogni tecnologia (nessuno strumento CI/CD, nessun hosting Git specifico nominato).

## 1. Strategia di Branching

| Elemento | Regola |
|---|---|
| Ramo principale | Sempre rilasciabile (coerente con [Product Constitution art. 202](../product_bible/15-product-constitution.md): "mai rilasciare ciò che non rispetta la Definition of Done") |
| Un ramo per Story | Nome: `story/<ID>-<slug>` (es. `story/task-004-ricorrenze`) — 1:1 con l'unità di lavoro tracciabile di [00 §2](00-metodo-tracciabilita-definizioni.md) |
| Integrazione | Ogni ramo di Story confluisce nel principale solo dopo Code Review (§3) e DoD (§[00 §5](00-metodo-tracciabilita-definizioni.md)) superati |
| Rami di rilascio | Un ramo per release pubblica (§[04 §3](04-roadmap-milestone-release-plan.md)), da cui si tagliano gli hotfix — mai sviluppo nuovo direttamente su un ramo di rilascio |
| Rami sperimentali (spike) | Isolati, mai integrati direttamente: uno spike produce una decisione (go/no-go, coerente con lo spike CRDT già previsto in Fase 0), non codice di produzione diretto |

## 2. Convenzioni Git

| Convenzione | Regola |
|---|---|
| Messaggio di commit | Riferimento obbligatorio all'ID Story o Task (`TASK-004: genera occorrenza al completamento`) — la tracciabilità di [00 §2](00-metodo-tracciabilita-definizioni.md) si estende alla storia del codice |
| Dimensione del commit | Un commit = un Task (T1…T6) completo e verificabile, mai un accumulo di più Task eterogenei |
| Nessuna riscrittura della storia pubblica | Coerente con la disciplina "mai perdere lavoro" applicata al codice, non solo ai dati utente (Product Constitution, spirito degli art. 1-3 esteso al processo) |
| Tag di versione | Allineati al versionamento a tre oggetti indipendenti già definito in [Technical Architecture Bible §11](../technical_architecture_bible/11-versionamento-architettura.md) (Contratto di Modulo, schema entità, forma eventi) — un tag di release non implica automaticamente un cambio di versione di ciascuno dei tre |

## 3. Code Review Checklist

Ogni Pull Request/Merge Request verifica, in ordine (fermarsi al primo fallimento):

| # | Verifica | Rif. |
|---|---|---|
| 1 | La Story ha una scheda Functional Bible completa e gli AC-## citati sono coperti da test | [00 §5](00-metodo-tracciabilita-definizioni.md) |
| 2 | Nessuna dipendenza vietata introdotta (modulo→modulo diretto, layer esterno→interno) | [Technical Architecture Bible §02 §4](../technical_architecture_bible/02-moduli-responsabilita-boundaries.md) |
| 3 | Nessun invariante di Data Model Bible violato (INV-01…18, VCB-01…07) | [Data Model Bible §16](../data_model_bible/00-modello-dati-comune.md), [§12](../data_model_bible/12-audit-permessi-vincoli-invarianti.md) |
| 4 | Ogni token/componente usato appartiene al Design System (nessun valore fuori scala, nessun componente nuovo non giustificato) | [Design System Bible DS-INV-02/04](../design_system_bible/00-fondamenta.md) |
| 5 | Comportamento offline verificato (nessuna funzione ordinaria richiede rete) | [Functional Bible MFC §3](../functional_bible/00-modello-funzionale-comune.md) |
| 6 | Accessibilità: contrasto, target di tocco, screen reader, riduzione movimento | [UX Bible Accessibility Bible](../ux_bible/12-accessibility-bible.md) |
| 7 | Nessun dato utente in log/telemetria senza consenso esplicito | [Technical Architecture Bible §09](../technical_architecture_bible/09-osservabilita-logging-telemetria.md) |
| 8 | I budget di prestazione dichiarati per la funzione non regrediscono | Functional Bible, scheda della funzione |
| 9 | Test della categoria T6 verdi (§[00 §4](00-metodo-tracciabilita-definizioni.md)) | — |
| 10 | Il messaggio di commit/PR cita l'ID Story/Task | §2 |

**Regola di governance**: chiunque nel team può bloccare una review citando un punto di questa checklist per numero — stessa disciplina della Product Constitution (art. 196, 309) estesa al codice.

## 4. Testing Strategy

Riusa integralmente la tassonomia già definita in [Functional Bible §17 §2](../functional_bible/17-matrici.md) (colonna "Test"): `UT` (unit), `IT` (integrazione moduli/eventi), `E2E` (flusso completo), `PT` (performance), `AT` (accessibilità), `ST` (sicurezza) — più la suite `MFC` (criteri di accettazione universali).

| Livello | Copertura richiesta | Quando gira |
|---|---|---|
| `UT` | Ogni Task T1 (dominio) e T2 (applicativa) — invarianti e regole di business, in particolare la simulazione a 12 mesi delle ricorrenze (TASK-004, FIN-006) e dell'aderenza abitudini (HAB-005, la suite più critica del prodotto) | Ad ogni commit |
| `IT` | Ogni Task T3 (eventi): un modulo pubblica, un altro reagisce, senza dipendenza diretta | Ad ogni Pull Request |
| `E2E` | Ogni flusso `FLOW-*` della UX Bible, su device reali o equivalenti | Ad ogni release train |
| `PT` | Ogni budget di prestazione dichiarato per funzione (es. CAPT-001 "≤3s", HOME-001 "<400ms", SRCH "50k entità ≤100ms") | Ad ogni release train, gate bloccante |
| `AT` | Ogni Task T4/T5 (UI/Design System): contrasto, target di tocco, screen reader, riduzione movimento, 200% font scaling | Ad ogni Pull Request che tocca UI |
| `ST` | Confini di sicurezza ([Technical Architecture Bible §10](../technical_architecture_bible/10-sicurezza-architetturale.md)): nessun dato in chiaro attraversa L5→L6, nessuna porta di rete nei servizi Core esclusi per costruzione (Ricerca, Insight) | Ad ogni release pubblica + audit periodico |
| `MFC` | Gli 8 criteri universali (MFC-AC-01…08): offline, undo, crash, convergenza multi-device, accessibilità, limiti Free, volumi, idempotenza | Ad ogni release train, su ogni Epic toccato |

**Piramide**: la base (`UT`) copre la maggioranza dei casi, `IT`/`AT` il livello intermedio, `E2E`/`PT`/`ST` il vertice — coerente con "unit sul core ≥90%, integrazione moduli, E2E sui flussi core" già dichiarato nel piano di fase esistente (doc 09 §QA Fase 1), qui esteso a tutte le fasi.

---

*Prossimo: [Release, Rollback, Feature Flag, Configurazioni, Migrazioni](06-release-rollback-flag-config-migrazioni.md)*
