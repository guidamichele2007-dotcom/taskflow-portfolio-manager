# 02 · Navigation Bible

> Eredita [MUC](00-modello-ux-comune.md) e [IA](01-information-architecture.md). Per ogni elemento di navigazione: quando compare, quando scompare, quando è disabilitato, perché.

## 1. Bottom Navigation (tab bar)

| Aspetto | Specifica |
|---|---|
| **Composizione** | 4 destinazioni fisse: Oggi (IA-001) · Moduli (IA-002) · Cerca (IA-100) · Profilo (IA-130). Mai dinamica per numero di moduli attivi (design doc 04 §3.1: una tab bar che cambia con i moduli sarebbe imprevedibile) |
| **Quando compare** | Sempre, su ogni schermata L0/L1, tranne durante l'onboarding (IA-014-016) e i flussi a schermo intero (Revisione settimanale IA-013, Import IA-04A, Cancellazione account IA-139) |
| **Quando scompare** | Nei flussi sequenziali a scopo unico (sopra) e quando si apre un foglio/dialogo (resta sotto, sfocata, non interattiva) |
| **Quando è disabilitata** | Mai singolarmente: se un modulo non è attivo, la sua card non appare ma la tab bar resta 4 destinazioni fisse (Moduli include sempre la Galleria) |
| **Perché** | P82 (la Home è governata dall'utente, ma il *sistema* di navigazione resta prevedibile); C-art. 89 (max 3 livelli, la tab bar è il livello 0 stabile) |
| **Personalizzazione ammessa** | Pressione lunga su "Moduli" per fissare un modulo preferito come scorciatoia (design doc 04 §3.1) — non altera le 4 destinazioni, aggiunge un badge di scorciatoia |

## 2. Top Navigation (barra superiore)

| Aspetto | Specifica |
|---|---|
| **Composizione L1** | Titolo della vista (statico, mai un logo che occupa spazio) + azioni contestuali (max 2 icone: es. filtro, impostazioni di modulo) |
| **Composizione L2/dettaglio** | Freccia indietro (mai "Home" — la navigazione è sempre relativa al punto di provenienza, UX-R-006) + titolo + azione contestuale singola (es. "···" menu) |
| **Quando compare** | Su ogni L1/L2 |
| **Quando scompare** | Durante lo scroll immersivo di contenuti lunghi (nota, agenda), riappare allo scroll verso l'alto (pattern standard di piattaforma) |
| **Quando è disabilitata** | Le azioni contestuali si disabilitano (opacità ridotta, non nascoste) quando non applicabili (es. filtro assente se lista vuota) — mai icone che spariscono e riappaiono causando "salti" (P113) |
| **Perché** | P25 (una vista, un concetto); coerenza cross-piattaforma di significato (P63) |

## 3. Side Navigation

**Decisione: assente su mobile.** Motivazione: P32 (gerarchie profonde sono un fallimento) e P89 (design per il dispositivo, non per il desktop): un cassetto laterale nasconde le destinazioni dietro un gesto non scopribile e duplica la tab bar (C-art. 61 — nessuna duplicazione). Riconsiderata solo per un eventuale client desktop/tablet futuro (PB doc 11 §5), mai per il telefono.

## 4. Modal (schermo intero, a scopo unico)

| Aspetto | Specifica |
|---|---|
| **Quando compare** | Solo per flussi che richiedono concentrazione esclusiva e hanno un inizio/fine netti: Onboarding, Revisione settimanale, Import CSV, Cancellazione account, Ripristino da backup |
| **Quando scompare** | Al completamento o all'uscita esplicita (sempre disponibile, mai forzata — C-art. 56) |
| **Quando è disabilitata** | N/A — è binario (presente/assente) |
| **Perché** | Questi flussi sono sequenziali e l'attenzione divisa produrrebbe errori (import) o mancherebbe di rispetto al momento (cancellazione account) |
| **Uscita** | Sempre una "X" o "Annulla" in alto a sinistra/destra secondo piattaforma; lo stato parziale si salva come draft dove sensato (es. Revisione ricorda a che punto era, MFC-R-06) |

## 5. Bottom Sheet (foglio)

| Aspetto | Specifica |
|---|---|
| **Quando compare** | Il pattern di default per il **dettaglio di un'entità** (GEF, MUC §9): task, transazione, abitudine, nota breve, evento, obiettivo; e per selettori (categoria, data, condivisione) |
| **Quando scompare** | Swipe verso il basso, tocco fuori dall'area, o azione esplicita di chiusura; mai per timeout |
| **Altezza** | Adattiva al contenuto; espandibile a schermo intero con un trascinamento (per editor lunghi come Note) |
| **Quando è disabilitato** | Non si apre un secondo foglio sopra un foglio aperto (mai più di 1 livello di overlay, MUC §5) — un'azione che richiederebbe un secondo foglio (es. selettore data dentro il dettaglio task) sostituisce temporaneamente il contenuto del foglio corrente con transizione orizzontale, poi torna |
| **Perché** | Mantiene il contesto della lista sottostante (percepibilmente sfocata) — l'utente non perde "dove si trovava" (P82; UX-R-006) |

## 6. Dialog (finestra di conferma)

| Aspetto | Specifica |
|---|---|
| **Quando compare** | SOLO per: eliminazione definitiva, cancellazione account, disconnessione dispositivo, azioni di blocco > 20 elementi, errori bloccanti irreversibili (rarissimi) | 
| **Quando NON compare** | Mai per conferme di azioni reversibili (UX-R-008); mai per "sei sicuro?" generico |
| **Composizione** | Titolo che dichiara la conseguenza (non la domanda: "Questi 45 task saranno eliminati definitivamente" non "Sei sicuro?") + 2 azioni max, quella distruttiva mai pre-selezionata/mai a destra per abitudine di tocco veloce senza lettura |
| **Perché** | C-art. 14; il dialogo è riservato all'irreversibile, così mantiene il suo peso semantico (se lo usassimo ovunque, l'utente smetterebbe di leggerlo) |

## 7. FAB (Floating Action Button)

| Aspetto | Specifica |
|---|---|
| **Quando compare** | Sempre, su ogni schermata L0/L1 (persistente — CAPT, P21) |
| **Quando scompare** | Durante la digitazione in un campo a schermo intero (per non coprire la tastiera/contenuto); durante i Modal a scopo unico (§4) |
| **Quando è disabilitato** | Mai — la cattura non si disabilita mai (CAPT-R-01) |
| **Comportamento** | Tocco → foglio di cattura universale (IA-020); pressione lunga → menu radiale di scorciatoie (IA-022, CAPT-007) |
| **Perché** | J1 è il job più importante: deve essere raggiungibile da ovunque, sempre, senza eccezioni |

## 8. Gesture Navigation (rimando a [08-gestures](08-gestures.md) per il dettaglio)

Sintesi ai fini di navigazione: swipe orizzontale tra viste segmentate dello stesso livello (es. Oggi↔Prossimi); swipe verticale per aprire/chiudere fogli; edge-swipe per il back di sistema (mai intercettato, §5 MUC).

## 9. Deep Link e Universal Link

| Aspetto | Specifica |
|---|---|
| **Deep link (`omnilife://`)** | Da notifiche, widget, share sheet, assistente vocale; funziona solo con app installata | 
| **Universal link (`https://app.omnilife.com/...`)** | Da web/messaggi/email; se l'app non è installata, apre la pagina store con contesto preservato (es. invito) — mai un errore 404 (IA-DL-06) |
| **Comportamento di arrivo** | Ogni link porta l'utente al contenuto con la tab bar visibile e la cronologia impostata come se fosse arrivato da Home → destinazione (il back riporta a Home, non chiude l'app) |
| **Quando è disabilitato/degradato** | Entità non più esistente → UX-R-013; permesso/modulo necessario non attivo → schermata di attivazione contestuale, mai un vicolo cieco |

## 10. Back Navigation, Cronologia, Ripristino Stato

| Aspetto | Specifica |
|---|---|
| **Back (gesture/tasto)** | Sempre disponibile; naviga alla schermata di provenienza reale (mai un "back" fittizio verso Home) |
| **Cronologia di navigazione** | Uno stack per tab (le 4 tab mantengono ciascuna la propria cronologia indipendente — pattern standard iOS/Android); tornare a una tab già visitata la ripresenta esattamente dove l'utente l'aveva lasciata |
| **Ripristino stato dopo kill/background** | Fino a 30 min in background: stato identico (scroll, filtri, draft aperti). Oltre 30 min o dopo kill del processo: riapre su Home, MA ogni draft non salvato è comunque recuperato (MFC-E-02) e un eventuale flusso modale interrotto (Revisione, Import) offre "Riprendi da dove eri" |
| **Perché** | C-art. 113 (mai spostare il contenuto sotto il dito), UX-R-006, MFC-E-18 |

---

*Prossimo: [Screen Inventory](03-screen-inventory.md)*
