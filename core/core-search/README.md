# core:core-search

**Scopo**: Servizio di Ricerca — indice globale SQLite FTS5, indicizzazione incrementale, ricerca unificata, ranking, filtri, ricerche recenti (Sprint 3, Core Platform).

**Riferimento**: Functional Bible SRCH-001…006; Data Model Bible §11 (indice come proiezione derivata); Technical Architecture Bible §13 §4; TDR-06, TDR-25.

## Componenti

| Componente | File | Ruolo |
|---|---|---|
| `IndexableEntity` | `IndexableEntity.kt` | Contratto generico — nessuna dipendenza da `domain-task`/altri moduli |
| `SearchIndexer` | `SearchIndexer.kt` | Indicizzazione incrementale (`index`/`remove`/`rebuild`) |
| `UnifiedSearchService` | `UnifiedSearchService.kt` | Ricerca (`search`), `SearchFilter`, `SearchResult` |
| `Fts5QuerySanitizer` | `Fts5QuerySanitizer.kt` | Query letterali, mai sintassi FTS5 nascosta (MFC-E-17) |
| `SqlDelightSearchIndex` | `persistence/SqlDelightSearchIndex.kt` | Implementazione reale — tabella virtuale FTS5, ranking a comparatore esplicito (TDR-25) |
| `RecentSearchStore` | `RecentSearchStore.kt` | Ultime 10 ricerche, locali, mai sincronizzate (SRCH-004/R-01) |
| `SearchSuggestions` | `SearchSuggestions.kt` | "Stavi cercando…" — tolleranza fuzzy leggera dichiarata |
| `SearchService` | `SearchService.kt` | Facciata che compone i precedenti |

## Cosa NON fa questo modulo

- Non dipende da `domain-task` né da alcun modulo `domain-*` — indicizza solo `IndexableEntity` generici. Mappare `Task`/`Note`/etc. a questo contratto e chiamare `index()`/`remove()` dentro la stessa transazione di scrittura è un blocco esplicito per Sprint 4.
- Non implementa il merge per paragrafo o alcuna logica di dominio — solo il motore di ricerca.
- Non usa `bm25()` nativo di FTS5 come ranking finale (vedi TDR-25): FTS5 seleziona i candidati, un comparatore Kotlin esplicito li ordina secondo la regola a 3 assi della Bible.

## Verificato in questo sandbox

Target **JVM** con SQLite FTS5 reale (via `sqlite-driver`, lo stesso già verificato in `domain-task` dallo Sprint 1) — inclusi un benchmark reale contro **MFC-AC-07** (≤100ms su 50.000 entità, non un numero sintetico). Android/iOS `actual` scritti ma non verificati (nessun SDK/host).

Vedi [../../README-BUILD.md](../../README-BUILD.md) per le convenzioni comuni a ogni modulo.
