# OmniLife — Data Model Bible

> **Il modello dati concettuale completo di OmniLife.** Versione 1.0 · 2026-07-13
>
> Fonte di verità esclusiva: [Product Bible](../product_bible/README.md), [Functional Bible](../functional_bible/README.md), [UX Bible](../ux_bible/README.md). Nessun database scelto, nessuna API progettata, nessun codice: solo entità, attributi, relazioni, ownership, ciclo di vita, versionamento, sincronizzazione logica e vincoli — al livello concettuale necessario perché ogni futuro schema fisico e ogni futura API derivino da qui senza ambiguità.
>
> Gerarchia normativa: [Product Constitution](../product_bible/15-product-constitution.md) → [Product Bible](../product_bible/README.md) → [Functional Bible](../functional_bible/README.md) → [UX Bible](../ux_bible/README.md) → **Data Model Bible** → documentazione tecnica.

## Come si legge

1. Il **[Modello Dati Comune (MDC)](00-modello-dati-comune.md)** definisce una sola volta: involucro comune (envelope) di ogni entità, identificatori, i due soli tipi di relazione (strutturale e **GraphLink**), ownership, ciclo di vita (per riferimento al MFC), versionamento, sincronizzazione/conflitti concettuali, tagging, ricerca, ordinamenti/filtri, export/import, audit, permessi e 14 invarianti universali.
2. Ogni scheda entità (documenti 01-10) usa il formato fisso: **ID · Descrizione · Campi · Relazioni · Dipendenze · Regole · Stati · Eventi collegati · Riferimenti Functional Bible**.
3. Il documento [11](11-versionamento-e-sincronizzazione.md) consolida i tre calcoli richiamati da più entità (generazione ricorrenze, aderenza abitudini, aggregazione obiettivi) per evitare di ripeterli.
4. Il documento [12](12-audit-permessi-vincoli-invarianti.md) raccoglie solo ciò che è **cross-entità** — le regole di un solo modulo restano nella Functional Bible e sono richiamate per riferimento, mai duplicate.

## Indice

| # | Documento | Contenuto |
|---|-----------|-----------|
| 00 | [Modello Dati Comune](00-modello-dati-comune.md) | Envelope, identificatori, relazioni, ownership, ciclo di vita, versionamento, sync/conflitti, tagging, ricerca, export/import, audit, permessi, 14 invarianti — normativo |
| 01 | [Entità di Sistema e Account](01-entita-sistema.md) | Account, Device, ModuleActivation, Subscription, RecoveryKeyMetadata, Setting |
| 02 | [Cattura e Grafo](02-entita-cattura-grafo.md) | CaptureInboxItem, **GraphLink** (il grafo dati personale formalizzato) |
| 03 | [Entità Attività](03-entita-attivita.md) | Task, TaskList, Subtask |
| 04 | [Entità Finanze](04-entita-finanze.md) | Transaction, FinancialAccount, Category, Budget, SavingsGoal |
| 05 | [Entità Abitudini](05-entita-abitudini.md) | Habit, HabitExecution |
| 06 | [Entità Calendario](06-entita-calendario.md) | CalendarSource, EventReference (deroga totale), TimeBox |
| 07 | [Entità Note](07-entita-note.md) | Note, NoteVersion (deroga: versionamento a snapshot) |
| 08 | [Entità Salute](08-entita-salute.md) | HealthPlatformReading (deroga totale), ManualHealthMetric |
| 09 | [Entità Obiettivi](09-entita-obiettivi.md) | Goal — la feature-firma, aggregazione via GraphLink |
| 10 | [Notifiche, Insight, Ricerca](10-entita-notifiche-insight-ricerca.md) | NotificationRequest, InsightRuleConfig, InsightFeedback, RecentSearchQuery |
| 11 | [Versionamento e Sincronizzazione](11-versionamento-e-sincronizzazione.md) | Calcoli consolidati: ricorrenze, aderenza abitudini, aggregazione obiettivi, CRDT concettuale |
| 12 | [Audit, Permessi, Vincoli, Invarianti](12-audit-permessi-vincoli-invarianti.md) | Catalogo cross-entità: 7 vincoli, 4 invarianti aggiuntivi |
| 13 | [ERM e Matrici](13-erm-e-matrici.md) | Entity Relationship Map testuale, Entità→Moduli, Entità→Funzioni, Entità→Eventi |
| 14 | [Report Finale](14-report.md) | File creati/modificati, incongruenze trovate, decisioni rinviate |

## Numeri di questa Bible

**26 entità concettuali** · **18 invarianti** (INV-01…18) · **6 decisioni di modellazione** (MDEC-01…06) · **7 vincoli cross-entità** (VCB-01…07) · **0 file delle Bible esistenti modificati** (nessuna incongruenza bloccante trovata, 2 ambiguità minori annotate — vedi [report](14-report.md)).

## Regole di manutenzione

- Nessuna nuova entità senza almeno una funzione della Functional Bible che la produca.
- Nessuna nuova relazione cross-modulo che non sia un GraphLink (MDEC-02), salvo motivazione esplicita registrata come nuova MDEC.
- Le deroghe al Modello Dati Comune (Calendario, Salute, Note, Ricerca recente) sono chiuse: nuove deroghe richiedono la stessa disciplina di dichiarazione esplicita usata qui.
- Questa Bible si aggiorna in lockstep con la Functional Bible: nessuna funzione che introduca un campo o una relazione nuova senza un aggiornamento corrispondente qui.
