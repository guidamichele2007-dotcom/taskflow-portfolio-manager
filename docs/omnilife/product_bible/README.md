# OmniLife — Product Bible

> **Il documento di riferimento assoluto del progetto.**
> Versione 1.0 · 2026-07-13 · Stato: Ratificata
>
> Ogni decisione di design, UX, sviluppo, marketing e monetizzazione deve derivare da questa documentazione. Se una decisione non è riconducibile a un principio, una regola o una scelta registrata qui, la decisione va sospesa e la Bible va aggiornata prima — mai il contrario.

## Che cos'è questo documento

La Product Bible non è la documentazione tecnica (quella vive in [`docs/omnilife/`](../README.md)): è **l'identità del prodotto**. La tecnica dice *come* costruire; la Bible dice *perché esiste il prodotto, che cosa è, che cosa non sarà mai*. In caso di conflitto tra un documento tecnico e la Bible, prevale la Bible.

## Indice

| # | Documento | Che cosa stabilisce |
|---|-----------|---------------------|
| 00 | [Executive Summary](00-executive-summary.md) | OmniLife in una pagina: perché esiste, per chi, visione a 10 anni |
| 01 | [Mission](01-mission.md) | Missione, visione, valori, filosofia, promesse verso utenti e team |
| 02 | [Product Manifesto](02-product-manifesto.md) | Perché il mondo ha bisogno di OmniLife; gli errori dei concorrenti; ciò che non comprometteremo mai |
| 03 | [Problem Space](03-problem-space.md) | I problemi che risolviamo: origine, conseguenze, dimensione, gravità, soluzione |
| 04 | [Market Research](04-market-research.md) | Il mercato mondiale: dimensioni, trend, crescita, categorie, mercati futuri |
| 05 | [Competitor Bible](05-competitor-bible.md) | Analisi di 50+ competitor con tabella comparativa finale |
| 06 | [Personas](06-personas.md) | 20 personas dettagliate |
| 07 | [Jobs To Be Done](07-jobs-to-be-done.md) | Tutti i JTBD, prioritizzati e motivati |
| 08 | [Product Principles](08-product-principles.md) | 100+ principi progettuali |
| 09 | [Feature Philosophy](09-feature-philosophy.md) | Perché una funzione entra o viene rifiutata; anti-feature-creep |
| 10 | [Success Metrics](10-success-metrics.md) | North Star, retention, attivazione, LTV/CAC, churn, Time To Value |
| 11 | [Business Strategy](11-business-strategy.md) | Posizionamento, pricing, mercati, espansione, marketplace, enterprise |
| 12 | [Growth Strategy](12-growth-strategy.md) | Strategia di crescita a 10 anni |
| 13 | [Roadmap](13-roadmap.md) | Anno 1, 2, 3, 5, 10 — con obiettivi misurabili |
| 14 | [Decision Log](14-decision-log.md) | Registro delle decisioni: problema, alternative, decisione, motivazione, conseguenze |
| 15 | [Product Constitution](15-product-constitution.md) | **200+ regole inviolabili.** Il documento supremo |

## Gerarchia normativa

```
1. Product Constitution (doc 15)     ← inviolabile; modificabile solo con processo formale
2. Mission & Manifesto (doc 01–02)   ← l'identità
3. Product Principles (doc 08)       ← le regole del progettare
4. Feature Philosophy (doc 09)       ← le regole del decidere
5. Strategia (doc 03–07, 10–13)      ← il contesto e la direzione
6. Decision Log (doc 14)             ← la memoria; si aggiorna sempre
7. Documentazione tecnica (../)      ← deriva da tutto quanto sopra
```

## Come si usa

- **Prima di proporre una feature** → doc 09 (criteri di ingresso) e doc 07 (quale job serve?).
- **Prima di una scelta di design** → doc 08; in caso di dubbio, doc 15.
- **Prima di una scelta di business** → doc 11 e 12; verificare i vincoli etici nel doc 15, Titolo VII.
- **Dopo ogni decisione importante** → registrarla nel doc 14. Una decisione non registrata non esiste.
- **Per emendare la Constitution** → processo descritto nel doc 15, Titolo X.

## Manutenzione

La Bible è viva ma stabile: i doc 00–02, 08, 15 cambiano raramente e con processo formale; i doc 03–07 e 10–13 si aggiornano a ogni revisione strategica (semestrale); il doc 14 si aggiorna continuamente.
