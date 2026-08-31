# OmniLife — Technical Architecture Bible

> **L'architettura logica completa di OmniLife.** Versione 1.0 · 2026-07-13
>
> Fonte di verità esclusiva: [Product Bible](../product_bible/README.md), [Functional Bible](../functional_bible/README.md), [UX Bible](../ux_bible/README.md), [Data Model Bible](../data_model_bible/README.md). Nessuna API progettata, nessun database fisico scelto, nessun cloud provider, nessun framework o linguaggio: solo layer, moduli, responsabilità, confini, dipendenze e i loro contratti concettuali — al livello necessario perché ogni futura scelta tecnologica derivi da qui senza dover reinventare la struttura.
>
> Gerarchia normativa: [Product Constitution](../product_bible/15-product-constitution.md) → [Product Bible](../product_bible/README.md) → [Functional Bible](../functional_bible/README.md) → [UX Bible](../ux_bible/README.md) → [Data Model Bible](../data_model_bible/README.md) → **Technical Architecture Bible** → documentazione tecnica di implementazione.

## Come si legge

1. **[00 · Principi Architetturali](00-principi-architetturali.md)** stabilisce il metodo (SOLID, Clean Architecture/Ports & Adapters, modularità come vincolo) che ogni documento successivo applica.
2. **[01 · Architettura Generale e Layer](01-architettura-generale-e-layer.md)** definisce i sei layer logici e la Dependency Rule — la struttura a cui ogni altro documento fa riferimento.
3. I documenti [02](02-moduli-responsabilita-boundaries.md)…[11](11-versionamento-architettura.md) approfondiscono un aspetto trasversale ciascuno (moduli, eventi, plugin, offline/sync/caching, lifecycle, errori, scalabilità, osservabilità, sicurezza, versionamento), sempre citando gli ID delle Bible sorgente (`P#`, `J#`, `D-##`, `C-art.#` per Product Bible; `PREFISSO-###` per Functional Bible; `IA-###`/`FLOW-###` per UX Bible; `DM-###`/`INV-##` per Data Model Bible).
4. **[12](12-diagrammi-testuali.md)** e **[13](13-matrici.md)** consolidano la vista d'insieme. **[14](14-report.md)** è il report finale di questa Bible.

## Indice

| # | Documento | Contenuto |
|---|-----------|-----------|
| 00 | [Principi Architetturali](00-principi-architetturali.md) | SOLID applicato, Clean Architecture, modularità, convenzioni, perimetro |
| 01 | [Architettura Generale e Layer](01-architettura-generale-e-layer.md) | I sei layer logici, Dependency Rule |
| 02 | [Moduli, Responsabilità, Boundaries](02-moduli-responsabilita-boundaries.md) | Elenco moduli, responsabilità, dipendenze consentite/vietate |
| 03 | [Event-Driven Architecture](03-event-driven-architecture.md) | Bus Eventi, forma degli eventi, lifecycle, separazione da sync |
| 04 | [Plugin Architecture](04-plugin-architecture.md) | Contratto di Modulo, ciclo di vita, sandboxing concettuale |
| 05 | [Offline-First, Sincronizzazione, Caching](05-offline-first-sincronizzazione-caching.md) | Motore di Sync come Servizio Core, caching strategy |
| 06 | [Lifecycle di Richieste ed Eventi](06-lifecycle-richieste-ed-eventi.md) | Tassonomia delle richieste, percorsi generici attraverso i layer |
| 07 | [Gestione degli Errori](07-gestione-errori.md) | Categorie per layer di origine, non propagazione a cascata |
| 08 | [Scalabilità ed Estendibilità](08-scalabilita-estendibilita.md) | Scala client-side/ecosistema, estendibilità |
| 09 | [Osservabilità, Logging, Telemetria](09-osservabilita-logging-telemetria.md) | Tre componenti separati, confini di consenso |
| 10 | [Sicurezza Architetturale](10-sicurezza-architetturale.md) | Confine di fiducia, sotto-confini, gerarchia chiavi (collocazione) |
| 11 | [Versionamento dell'Architettura](11-versionamento-architettura.md) | Tre oggetti versionati indipendentemente |
| 12 | [Diagrammi Testuali](12-diagrammi-testuali.md) | 8 diagrammi consolidati |
| 13 | [Matrici](13-matrici.md) | Modulo→Responsabilità, Modulo→Eventi, Modulo→Dipendenze, Layer→Componenti |
| 14 | [Report Finale](14-report.md) | File creati/modificati, incongruenze, decisioni rinviate |

## Numeri di questa Bible

**6 layer logici** · **15 moduli/servizi mappati** · **2 canali di comunicazione cross-modulo ammessi** (Bus Eventi, GraphLink) · **4 sotto-confini di sicurezza** · **8 diagrammi testuali** · **4 matrici** · **0 file delle Bible esistenti modificati** (nessuna incongruenza bloccante; una nota di coesistenza documentale con il precedente `docs/omnilife/03-architettura.md`, non un conflitto — vedi [report](14-report.md)).

## Regole di manutenzione

- Nessun nuovo modulo entra in questa Bible senza corrispondere esattamente a un modulo della Functional Bible.
- Nessuna nuova dipendenza cross-modulo che non sia Bus Eventi o GraphLink, salvo motivazione esplicita registrata come nuova decisione architetturale.
- Ogni scelta tecnologica (linguaggio, framework, database, cloud) resta esplicitamente fuori da questa Bible: appartiene alla fase successiva (API e Implementazione), le cui decisioni rinviate sono catalogate nel [report](14-report.md) §4.
- Questa Bible si aggiorna in lockstep con Functional Bible, UX Bible e Data Model Bible: nessuna nuova funzione, entità o flusso che alteri i confini di modulo senza un aggiornamento corrispondente qui.
