# OmniLife — UX Bible

> **Il documento definitivo dell'esperienza utente.** Versione 1.0 · 2026-07-13
>
> Alla fine di questa Bible si sa esattamente: come si muove l'utente, cosa vede, cosa può fare, cosa succede dopo ogni azione, come reagisce l'app, come vengono gestiti tutti gli errori. Nessun codice, nessuna UI finale, nessun mockup: solo comportamento dell'esperienza, al livello di dettaglio necessario perché ogni futura schermata derivi da qui senza ambiguità.
>
> Gerarchia normativa: [Product Constitution](../product_bible/15-product-constitution.md) → [Product Bible](../product_bible/README.md) → [Functional Bible](../functional_bible/README.md) → **UX Bible** → documentazione tecnica. In caso di conflitto, prevale il documento più a monte.

## Come si legge

1. Il **[Modello UX Comune (MUC)](00-modello-ux-comune.md)** definisce una sola volta: timing universale, vocabolario del feedback, animazioni, profondità di navigazione, pattern di stato vuoto/errore/undo, e il **Generic Entity Flow** (il flusso di creare/modificare/duplicare/condividere/archiviare/eliminare/recuperare/cercare/taggare/versionare applicato a ogni entità). Ciò che un documento successivo non specifica si comporta come da MUC.
2. Ogni documento cita gli ID di Product Bible (`P#`, `J#`, `D-##`, `C-art.#`) e Functional Bible (`PREFISSO-###`) da cui deriva.
3. Le funzioni **Must** (MVP) hanno flussi dettagliati passo-passo ([04](04-user-flows-core-mvp.md)); le **Should/Could** seguono lo stesso schema con dettaglio proporzionato alla release ([05](05-user-flows-estesi.md)), completato alla scheda estesa quando entrano in sviluppo (stessa regola della Functional Bible, MFC §1.3).

## Indice

| # | Documento | Contenuto |
|---|-----------|-----------|
| 00 | [Modello UX Comune](00-modello-ux-comune.md) | Timing, feedback, animazioni, navigazione, stati, undo, Generic Entity Flow — normativo |
| 01 | [Information Architecture](01-information-architecture.md) | Mappa completa di ogni destinazione raggiungibile (139 nodi: schermate, fogli, deep link) |
| 02 | [Navigation Bible](02-navigation-bible.md) | Bottom/Top/Side nav, Modal, Bottom Sheet, Dialog, FAB, Gesture nav, Deep/Universal link, Back, Cronologia |
| 03 | [Screen Inventory](03-screen-inventory.md) | 62 schermate/fogli principali con funzioni, origine, destinazioni, principi, JTBD |
| 04 | [User Flows — Core e MVP](04-user-flows-core-mvp.md) | Flussi dettagliati delle funzioni Must |
| 05 | [User Flows Estesi](05-user-flows-estesi.md) | Flussi delle funzioni Should/Could |
| 06 | [Task Flows — Ciclo di vita delle entità](06-task-flows-entita.md) | Creare/modificare/duplicare/condividere/archiviare/eliminare/recuperare/cercare/taggare/versionare per ogni entità |
| 07 | [Microinterazioni](07-microinterazioni.md) | Animazione, durata, curva, aptica, sonoro, microcopy per ogni azione |
| 08 | [Gesture](08-gestures.md) | Tap, long press, swipe, drag, pinch, scroll, edge swipe — motivazione, alternative, conflitti, accessibilità |
| 09 | [Empty States](09-empty-states.md) | 13+ varianti di stato vuoto per ogni schermata |
| 10 | [Error Experience](10-error-experience.md) | Messaggio, spiegazione, azione, ripristino, retry, fallback, logging, telemetria per ogni categoria di errore |
| 11 | [Onboarding Experience](11-onboarding-experience.md) | Prima apertura → prima settimana → primo mese |
| 12 | [Accessibility Bible](12-accessibility-bible.md) | VoiceOver/TalkBack, Switch Control, contrasto, Dynamic Type, focus, comandi vocali |
| 13 | [UX Constitution](13-ux-constitution.md) | **312 regole** organizzate in 14 titoli — il vincolo supremo dell'esperienza |
| 14 | [Matrici](14-matrici.md) | Screen→Feature, Feature→Flow, Flow→JTBD/Principi/Personas/Test/Accessibility |

## Numeri di questa Bible

- **139 nodi** di Information Architecture · **62 schermate/fogli** inventariati · **15 flussi utente** dettagliati (Must) + estensione per tutte le funzioni Should/Could · **1 Generic Entity Flow** che copre 5 tipi di entità con deroghe dichiarate · **312 regole** nella UX Constitution · **7 matrici** di tracciabilità.

## Regole di manutenzione

- Nessuna nuova schermata entra nell'Information Architecture senza un nodo tracciato qui e senza passare i 7 cancelli della [Feature Philosophy](../product_bible/09-feature-philosophy.md).
- Ogni nuova funzione della Functional Bible richiede una riga nelle matrici ([14](14-matrici.md)) prima di entrare in sviluppo.
- La UX Constitution ([13](13-ux-constitution.md)) si emenda solo con lo stesso processo formale del Titolo X della Product Constitution.
- Questa Bible si aggiorna in lockstep con la Functional Bible: nessuna funzione senza comportamento UX definito, nessun comportamento UX senza funzione che lo giustifichi.
