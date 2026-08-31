# core:core-designtokens

**Scopo**: token del Design System (tipografia, spaziatura, colore, elevazione, raggi/bordi, opacità, motion) come costanti/enum Kotlin puri — nessuna dipendenza da Compose o da alcun framework di rendering (Design System Bible §00: la Bible stessa è indipendente da ogni tecnologia).

**Riferimento**: Design System Bible §01-03. I valori fisici concreti (dp/sp/durate) sono una decisione di Sprint 2, documentata in [TDR-22](../../docs/omnilife/technology_decision_record.md#tdr-22--tecnologia-di-implementazione-del-design-system-libreria-ui-condivisa).

**Contenuto**:
- `OmniSpacing`/`OmniTouchTarget` — scala di spaziatura, unità base derivata dal target di tocco minimo (DS-34)
- `OmniTypography` — scala tipografica a 7 livelli (UX-C-296)
- `OmniShape`/`OmniBorder`/`OmniElevationLevel`/`OmniOpacity` — raggi, bordi, 4 livelli di elevazione, opacità
- `OmniColor`/`OmniColorPair`/`OmniColors`/`OmniAccent` — ruoli colore come coppie chiaro/scuro esplicite (DS-INV-01), insieme chiuso di 6 accenti (SET-001)
- `ColorContrast`/`WcagContrast` — calcolo del rapporto di contrasto WCAG 2.1, verificato per ogni coppia in `commonTest` (DS-26)
- `OmniMotionToken`/`OmniMotionDurationMs`/`OmniMotionScale` — i 9 token di motion ammessi (DS-28)

**Consumato da**: `core-designsystem` (implementazione Compose Multiplatform dei componenti, TDR-22) — questo modulo non dipende da Compose e non lo dipenderà mai, per costruzione.

Vedi [../../README-BUILD.md](../../README-BUILD.md) per le convenzioni comuni a ogni modulo.
