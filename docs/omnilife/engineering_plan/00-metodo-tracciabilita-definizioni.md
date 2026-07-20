# 00 · Metodo, Tracciabilità, Definition of Ready/Done

> **Fonte di verità**: l'intera documentazione esistente — [Product Bible](../product_bible/README.md), [Functional Bible](../functional_bible/README.md), [UX Bible](../ux_bible/README.md), [Data Model Bible](../data_model_bible/README.md), [Technical Architecture Bible](../technical_architecture_bible/README.md), [Design System Bible](../design_system_bible/README.md), e la documentazione tecnica originaria ([docs/omnilife/00-09](../README.md)). Nessun linguaggio, framework, provider cloud o database fisico è scelto in questa Bible.
>
> **Regola anti-duplicazione**: questo piano **non ridescrive** alcuna funzione, entità, flusso o componente — li **organizza in sequenza eseguibile**, citando sempre l'ID sorgente.

## 1. Il principio: il backlog esiste già, va solo organizzato

La [Functional Bible §17](../functional_bible/17-matrici.md) ha già assegnato a ogni funzione un ID stabile, una priorità (Must/Should/Could) e una release (1.0/1.x/2.x/3.x). Questo è, di fatto, **un backlog già prioritizzato**. Il lavoro di questa Bible non è crearne uno nuovo: è **tradurlo in una struttura Epic → Feature → Story → Task eseguibile**, aggiungere le dipendenze tecniche (non solo funzionali), il percorso critico, e le pratiche di ingegneria (branching, review, test, rilascio) che nessun documento precedente copre.

## 2. Mappatura Epic/Feature/Story/Task (ID scheme)

| Livello | Corrisponde a | ID | Fonte |
|---|---|---|---|
| **Epic** | Un modulo ([Technical Architecture Bible §02](../technical_architecture_bible/02-moduli-responsabilita-boundaries.md)) o una capacità trasversale di fondazione | `EPIC-XXX` | 1:1 con i moduli già mappati architetturalmente |
| **Feature** | Un raggruppamento coerente di funzioni dentro un modulo | `FEAT-XXX-n` | Raggruppamento nuovo di questa Bible, su funzioni già esistenti |
| **Story** | **Coincide con l'ID della funzione della Functional Bible** | `TASK-001`, `FIN-005`, … (invariati) | **Nessun nuovo ID inventato per le story di prodotto** — la scheda estesa, gli AC-##, gli edge case della Functional Bible SONO la definizione della story |
| **Story (fondazione)** | Capacità tecniche senza funzione utente diretta (es. scaffold architetturale) | `ENG-XXX-n` | Nuovo, solo per ciò che Technical Architecture/Data Model/Design System Bible richiedono e la Functional Bible non descrive come funzione utente |
| **Task** | Unità di lavoro ingegneristico dentro una story, secondo il **template generico a 6 categorie** (§4) | `T1…T6` per story | Nuovo, ma un template unico riusato per ogni story (evita 153 elenchi diversi) |

**Regola di tracciabilità obbligatoria**: ogni Epic cita i moduli/entità che la compongono; ogni Feature cita gli ID Functional Bible che raggruppa; ogni Story **è** un ID Functional Bible (o `ENG-*`); ogni Task cita l'AC-## (criterio di accettazione) che lo chiude.

## 3. Definition of Ready (DoR)

Una Story può entrare in uno Sprint/iterazione solo se:

| Criterio | Verifica |
|---|---|
| Ha una scheda Functional Bible completa (Must: estesa; Should/Could: almeno sintetica + scheda estesa completata prima dell'inizio, per regola già vigente in [Functional Bible §1.3](../functional_bible/00-modello-funzionale-comune.md)) | Riferimento diretto all'ID |
| Ha almeno un flusso UX Bible associato (GEF o flusso dedicato) | [UX Bible §17 matrici](../ux_bible/14-matrici.md) |
| Ha le entità/campi coinvolti già presenti nel Data Model Bible | [Data Model Bible §13](../data_model_bible/13-erm-e-matrici.md) |
| Ha i componenti di Design System necessari già catalogati (o la loro assenza è dichiarata come nuova variante, mai nuovo componente non giustificato — DS-INV-04) | [Design System Bible §07](../design_system_bible/07-matrici.md) |
| Le sue dipendenze (§[03](03-dipendenze-grafo-percorso-critico.md)) sono già "Fatto" o pianificate nella stessa iterazione senza ciclo |
| Non viola nessun invariante o vincolo delle Bible precedenti (verifica rapida, non un audit completo) |

## 4. Template generico di Task (si applica a ogni Story, salvo dichiarata esclusione)

| Task | Descrizione | Chiude quando |
|---|---|---|
| **T1 — Modellazione di dominio** | Implementare l'entità/i campi coinvolti secondo l'involucro comune e gli invarianti | Rispetta [Data Model Bible §00](../data_model_bible/00-modello-dati-comune.md) e le regole `R-*` della funzione |
| **T2 — Logica applicativa** | Casi d'uso, regole di business, transizioni di stato | Rispetta le regole `R-*` e gli stati dichiarati nella scheda Functional Bible |
| **T3 — Integrazione eventi** | Pubblicazione/sottoscrizione sul Bus Eventi, collegamenti GraphLink se pertinenti | Coerente con [Technical Architecture Bible §03](../technical_architecture_bible/03-event-driven-architecture.md) e la matrice Modulo→Eventi |
| **T4 — Interfaccia utente** | Implementazione dello schermo/flusso | Coerente con il flusso UX Bible citato (passo per passo) |
| **T5 — Applicazione Design System** | Uso dei componenti/token catalogati, nessuna deviazione non giustificata | Coerente con [Design System Bible §05-06](../design_system_bible/05-componenti-navigazione.md) |
| **T6 — Test e criteri di accettazione** | Copertura degli AC-## della funzione + i criteri MFC-AC universali | Ogni AC-## della scheda passa; suite di test secondo [05-pratiche-di-sviluppo §3](05-pratiche-di-sviluppo.md) |

**Eccezioni dichiarate** (non tutti i 6 task si applicano sempre): le entità in deroga (Calendario, Salute — [Data Model Bible §9](../data_model_bible/00-modello-dati-comune.md)) non hanno T1 pieno (nessun ciclo di vita nostro) ma hanno T3/T4 rinforzati (adattatore di piattaforma); i servizi Core senza UI propria (Ricerca, Insight) non hanno T4/T5.

## 5. Definition of Done (DoD)

Una Story è "Fatta" solo se, oltre al superamento di ogni Task (§4):

| Criterio | Fonte |
|---|---|
| Tutti gli AC-## della funzione superano i test automatici | Functional Bible, scheda della funzione |
| I criteri MFC-AC-01…08 universali non regrediscono | [Functional Bible MFC §7](../functional_bible/00-modello-funzionale-comune.md) |
| Il flusso UX corrispondente supera la verifica di accessibilità (contrasto, target di tocco, screen reader) | [UX Bible Accessibility Bible](../ux_bible/12-accessibility-bible.md) + [Design System Bible §04](../design_system_bible/04-stati-e-accessibilita-visiva.md) |
| Nessun invariante di Data Model Bible o Technical Architecture Bible violato (dipendenze vietate, INV-*) | [Data Model Bible §16](../data_model_bible/00-modello-dati-comune.md), [Technical Architecture Bible §02](../technical_architecture_bible/02-moduli-responsabilita-boundaries.md) |
| Il codice review checklist (§[05](05-pratiche-di-sviluppo.md)) è superato | — |
| Feature flag associato (se previsto, §[06](06-release-rollback-flag-config-migrazioni.md)) è configurato correttamente |
| Nessun budget di prestazione dichiarato altrove regredisce (es. Functional Bible HOME-001 "<400ms", CAPT-001 "≤3s") | Functional/Product Bible, citati per funzione |

## 6. Che cosa questa Bible NON fa

Non sceglie linguaggio, framework, provider cloud, database fisico (come da mandato). Non riscrive alcuna funzione, entità, flusso o componente. Non introduce nuove funzionalità di prodotto: gli `ENG-*` sono strettamente infrastrutturali/architetturali, mai una funzione visibile all'utente non già presente nella Functional Bible.

---

*Indice: [README](README.md) · Prossimo: [MVP Scope e Backlog](01-mvp-scope-e-backlog.md)*
