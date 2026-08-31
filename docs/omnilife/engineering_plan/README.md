# OmniLife — Engineering Plan

> **Il piano eseguibile di sviluppo di OmniLife.** Versione 1.0 · 2026-07-13
>
> Fonte di verità: **l'intera documentazione esistente** — [Product Bible](../product_bible/README.md), [Functional Bible](../functional_bible/README.md), [UX Bible](../ux_bible/README.md), [Data Model Bible](../data_model_bible/README.md), [Technical Architecture Bible](../technical_architecture_bible/README.md), [Design System Bible](../design_system_bible/README.md), e la [documentazione tecnica originaria](../README.md) (visione, roadmap, analisi rischi, piano di fase). Nessun linguaggio, framework, provider cloud o database fisico è scelto qui.
>
> **Principio guida**: il backlog non si inventa, si **organizza**. La Functional Bible ha già assegnato a 153 funzioni un ID, una priorità e una release; questo piano le traduce in Epic → Feature → Story → Task eseguibili, con dipendenze tecniche, percorso critico e pratiche di ingegneria — senza mai ridescrivere ciò che le Bible precedenti hanno già specificato.
>
> Gerarchia: [Product Constitution](../product_bible/15-product-constitution.md) → [Product Bible](../product_bible/README.md) → [Functional Bible](../functional_bible/README.md) → [UX Bible](../ux_bible/README.md) → [Data Model Bible](../data_model_bible/README.md) → [Technical Architecture Bible](../technical_architecture_bible/README.md) → [Design System Bible](../design_system_bible/README.md) → **Engineering Plan** → codice.

## Indice

| # | Documento | Contenuto |
|---|-----------|-----------|
| 00 | [Metodo, Tracciabilità, DoR/DoD](00-metodo-tracciabilita-definizioni.md) | ID scheme, template a 6 Task, Definition of Ready, Definition of Done |
| 01 | [MVP Scope e Backlog](01-mvp-scope-e-backlog.md) | 16 Epic, Feature per Epic, dettaglio Epic di fondazione |
| 02 | [Matrici Epic→Feature→Story→Task](02-matrici-epic-feature-story-task.md) | Matrice completa + Story→Task dettagliata (Epic pilota) e aggregata |
| 03 | [Dipendenze, Grafo, Percorso Critico](03-dipendenze-grafo-percorso-critico.md) | Grafo a livello di Epic, dipendenze esplicite, percorso critico a 5 anelli |
| 04 | [Roadmap, Milestone, Release Plan](04-roadmap-milestone-release-plan.md) | Epic innestati nella cornice temporale esistente, 7 milestone nuove |
| 05 | [Pratiche di Sviluppo](05-pratiche-di-sviluppo.md) | Branching, convenzioni Git, code review checklist, testing strategy |
| 06 | [Release, Rollback, Feature Flag, Config, Migrazioni](06-release-rollback-flag-config-migrazioni.md) | Strategie operative di rilascio |
| 07 | [Refactoring e Manutenzione](07-refactoring-e-manutenzione.md) | Registro debito tecnico, piano di refactoring, manutenzione ricorrente |
| 08 | [Report Finale](08-report.md) | File creati/modificati, rischi, blocchi critici, prerequisiti per il codice |

## Numeri di questo piano

**16 Epic** · **~42 Feature** · **158 Story** (153 Functional Bible + 5 di fondazione) · **6 Task-tipo** (template unico) · **7 milestone ingegneristiche nuove** · **1 percorso critico a 5 anelli** · **0 file delle Bible esistenti modificati**.

## Regole di manutenzione

- Ogni nuova funzione della Functional Bible diventa automaticamente una Story — nessuna Story creata senza un ID Functional Bible corrispondente (salvo `ENG-*` di fondazione, giustificate solo da esigenze architetturali).
- Le matrici (§[02](02-matrici-epic-feature-story-task.md)) si aggiornano ad ogni variazione del backlog Functional Bible.
- Nessuna decisione tecnologica entra in questo piano: appartiene alla fase successiva, i cui prerequisiti sono catalogati nel [report](08-report.md) §5.
