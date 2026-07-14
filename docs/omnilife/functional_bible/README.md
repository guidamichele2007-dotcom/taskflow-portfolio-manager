# OmniLife — Functional Bible

> **La descrizione completa del comportamento del prodotto.** Versione 1.0 · 2026-07-13
>
> Ogni futura schermata, API, schema dati e riga di codice deriva da questa documentazione. Niente qui è implementazione: sono **comportamenti, regole, stati e criteri verificabili**. In caso di conflitto: prevale la [Product Constitution](../product_bible/15-product-constitution.md), poi la [Product Bible](../product_bible/README.md), poi questa Functional Bible, poi la [documentazione tecnica](../README.md).

## Come si legge (il patto anti-ambiguità)

1. Il **[Modello Funzionale Comune (MFC)](00-modello-funzionale-comune.md)** definisce una sola volta: ciclo di vita delle entità, offline/sync, stati universali, autorizzazioni, edge case universali (fusi, DST, bisestili, doppio tocco, disco pieno…), criteri di accettazione universali. **Ciò che un modulo non specifica si comporta come da MFC; ogni deroga è esplicita e motivata.**
2. Ogni modulo segue la **struttura fissa**: Scopo e tracciabilità → Funzioni (ID univoci) → Comportamenti → Stati → Regole di business motivate → Eventi (mai dipendenze implicite) → Edge case → Acceptance criteria (Dato/Quando/Allora, pronti per i test automatici).
3. Ogni elemento cita gli ID della Product Bible: principi `P#`, job `J#`, decisioni `D-##`, articoli `C-art. #`. La tracciabilità completa è nelle [Matrici](17-matrici.md).

## Indice

| # | Documento | ID funzioni | Pri dominante |
|---|-----------|-------------|----------------|
| 00 | [Modello Funzionale Comune](00-modello-funzionale-comune.md) | MFC-R/E/AC | — (normativo) |
| 01 | [Core: Home, Onboarding, Galleria, Revisione](01-core-home-onboarding.md) | HOME, ONB, GAL, REV | Must |
| 02 | [Cattura Rapida](02-cattura-rapida.md) | CAPT | Must |
| 03 | [Modulo Attività](03-modulo-attivita.md) | TASK | Must |
| 04 | [Modulo Finanze](04-modulo-finanze.md) | FIN | Must |
| 05 | [Modulo Abitudini](05-modulo-abitudini.md) | HAB | Must |
| 06 | [Modulo Calendario](06-modulo-calendario.md) | CAL | Must (lettura) |
| 07 | [Modulo Note](07-modulo-note.md) | NOTE | Should |
| 08 | [Modulo Salute](08-modulo-salute.md) | HLTH | Should |
| 09 | [Modulo Obiettivi](09-modulo-obiettivi.md) | GOAL | Should (feature-firma) |
| 10 | [Ricerca Globale](10-ricerca.md) | SRCH | Must |
| 11 | [Notifiche](11-notifiche.md) | NTF | Must |
| 12 | [Widget e Superfici Esterne](12-widget.md) | WID | Must |
| 13 | [Sync, Backup, Export](13-sync-backup-export.md) | SYNC, BKP, EXP | Must |
| 14 | [Impostazioni, Profilo, Sicurezza](14-impostazioni-profilo-sicurezza.md) | SET, SEC, PROF | Must |
| 15 | [Insight Engine](15-insight.md) | INS | Should |
| 16 | [Moduli Futuri e Perimetro](16-moduli-futuri.md) | — (decisioni di scope) | — |
| 17 | [Matrici di Tracciabilità](17-matrici.md) | tutte | — |

**153 funzioni specificate** (78 Must / 55 Should / 20 Could) + il MFC che governa tutte. Riepiloghi e coperture: [Matrici §3–5](17-matrici.md).

## Nota di scope (deliberata, non un'omissione)

I moduli **Dashboard** (= Home "Oggi", doc 01), **Task** (= Attività, doc 03), **Workspace**, **Documenti**, **Casa**, **Auto**, **Viaggi**, **Inventario**, **Password** sono trattati così: i primi due sono specificati con il loro nome di prodotto; gli altri hanno un **verdetto motivato e tracciato** nel doc [16-moduli-futuri](16-moduli-futuri.md) (per la Feature Philosophy della Product Bible, un modulo non specificato È una decisione, e va documentata come tale — molti sono candidati ideali per il marketplace di fase 3).

## Regole di manutenzione

- Gli ID non si riusano mai; le funzioni rimosse restano nel documento con stato "ritirata" e motivo.
- Nessuna funzione entra in sviluppo senza scheda estesa approvata (MFC §1.3) e senza riga nelle matrici.
- Ogni nuova funzione passa i 7 cancelli della [Feature Philosophy](../product_bible/09-feature-philosophy.md) PRIMA di essere aggiunta qui.
- La revisione della Functional Bible segue le release: ciò che il prodotto fa e ciò che questo documento dice devono coincidere sempre (P6 applicato a noi stessi).
