# OmniLife — Design System Bible

> **Il sistema di design completo di OmniLife.** Versione 1.0 · 2026-07-13
>
> Fonte di verità esclusiva: [Product Bible](../product_bible/README.md), [Functional Bible](../functional_bible/README.md), [UX Bible](../ux_bible/README.md), [Data Model Bible](../data_model_bible/README.md), [Technical Architecture Bible](../technical_architecture_bible/README.md). Nessun mockup, schermata, immagine, codice, CSS, Flutter, SwiftUI o Jetpack Compose: solo token, regole e componenti, indipendenti da ogni tecnologia di rendering.
>
> Gerarchia normativa: [Product Constitution](../product_bible/15-product-constitution.md) → [Product Bible](../product_bible/README.md) → [Functional Bible](../functional_bible/README.md) → [UX Bible](../ux_bible/README.md) → [Data Model Bible](../data_model_bible/README.md) → [Technical Architecture Bible](../technical_architecture_bible/README.md) → **Design System Bible** → produzione UI.

## Come si legge

1. **[00-fondamenta](00-fondamenta.md)** eredita (senza ripetere) i principi di design già normativi in Product Bible e UX Bible, e definisce naming conventions, vincoli e 6 invarianti propri.
2. **[01](01-token-visivi.md)** e **[02](02-linguaggio-visivo.md)** sistematizzano per la prima volta i token visivi (tipografia, spaziatura, colore, elevazione, bordi, raggi, opacità, iconografia, griglia) che le Bible precedenti presuppongono ma non quantificano.
3. **[03-motion](03-motion.md)** richiama (non ridefinisce) i token di animazione già normativi nella UX Bible, aggiungendo solo i token visivi di motion propri del sistema di design.
4. **[04](04-stati-e-accessibilita-visiva.md)** definisce il modello generico a 8 stati condiviso da ogni componente e quantifica le regole di accessibilità visiva.
5. **[05](05-componenti-navigazione.md)** e **[06](06-componenti-contenuto.md)** sono la libreria di componenti: 21 componenti, ciascuno con scopo, anatomia, proprietà, varianti, stati, comportamento, accessibilità, motion, vincoli, usi consentiti e vietati.
6. **[07](07-matrici.md)** e **[08](08-report.md)** chiudono con le matrici richieste e il report finale.

## Indice

| # | Documento | Contenuto |
|---|-----------|-----------|
| 00 | [Fondamenta](00-fondamenta.md) | Design principles (eredità), visual language, naming conventions, constraints, 6 invarianti |
| 01 | [Token Visivi](01-token-visivi.md) | Spacing, Typography, Color System, Elevation, Borders, Radius, Opacity |
| 02 | [Linguaggio Visivo](02-linguaggio-visivo.md) | Iconografia, Illustrazione, Griglia, Responsive/Adaptive, Dark/Light |
| 03 | [Motion](03-motion.md) | Motion principles e animation tokens (richiamo UX Bible) + token visivi nuovi |
| 04 | [Stati e Accessibilità Visiva](04-stati-e-accessibilita-visiva.md) | 8 stati generici, focus, contrasto quantificato, target di tocco |
| 05 | [Componenti — Navigazione](05-componenti-navigazione.md) | Tab Bar, Top Bar, FAB, Bottom Sheet, Dialog, Modal, Snackbar, Segmented Control |
| 06 | [Componenti — Contenuto](06-componenti-contenuto.md) | Riga Entità, Card, Chip, Progress, Completion, Badge, Banner, Search, Pulsante, Toggle, Skeleton, Empty, Avatar |
| 07 | [Matrici](07-matrici.md) | Componenti→Moduli, →Stati, →Accessibilità, →Motion |
| 08 | [Report Finale](08-report.md) | File creati/modificati, incongruenze, decisioni rinviate alla fase UI |

## Numeri di questa Bible

**21 componenti** · **36 token/regole numerate** (DS-01…36) · **6 design invariants** (DS-INV-01…06) · **4 matrici** · **0 file delle Bible esistenti modificati** (1 imprecisione interna corretta prima della pubblicazione — vedi [report §3](08-report.md)).

## Regole di manutenzione

- Nessun nuovo token fuori dalle scale definite in [01](01-token-visivi.md) (DS-INV-02).
- Nessun nuovo componente senza aver verificato che un componente esistente, esteso con una variante, non possa già servire lo scopo (DS-INV-04).
- Nessuna eccezione visiva per modulo, mai (DS-INV-06, eredita P55/UX-C-291).
- Ogni scelta tecnologica (valori fisici, font-family, libreria icone, strumento di token) resta esplicitamente fuori da questa Bible: appartiene alla fase UI, le cui decisioni rinviate sono catalogate nel [report](08-report.md) §4.
