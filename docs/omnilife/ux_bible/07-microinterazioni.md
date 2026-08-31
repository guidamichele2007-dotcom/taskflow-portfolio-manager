# 07 · Microinterazioni

> Eredita [MUC §2-4](00-modello-ux-comune.md). Per ogni azione ricorrente: animazione, durata, curva, feedback aptico, feedback sonoro, transizione, microcopy, stato finale. Le durate/curve sono quelle tokenizzate nel MUC §4 (Micro/Standard/Enfasi/Uscita); qui l'applicazione puntuale.

## 1. Completamento (task, checklist, abitudine)

| Aspetto | Specifica |
|---|---|
| Animazione | Il cerchio/checkbox si riempie con un "check" disegnato (stroke animato, non un'icona che appare di scatto) | 
| Durata/curva | 150ms, ease-out (Micro) |
| Aptico | Lieve (singolo impulso breve) |
| Sonoro | Assente di default (attivabile in Impostazioni > Aspetto, per chi lo desidera) |
| Transizione | La riga si attenua (opacità 60%) e il testo si barra con un'animazione di 100ms |
| Microcopy | Nessuno per il singolo elemento (il segno di spunta parla da solo); annuncio screen reader: "Completato: [titolo]" |
| Stato finale | Riga barrata visibile fino al cambio vista o refresh naturale (mai sparisce di scatto — permette l'undo) |

## 2. Spunta abitudine quantitativa (incremento progressivo)

| Aspetto | Specifica |
|---|---|
| Animazione | L'anello di progresso avanza dell'incremento (non salta al valore finale: interpola) |
| Durata/curva | 200ms, ease-in-out (Standard) |
| Aptico | Lieve a ogni incremento; medio al raggiungimento del target |
| Transizione | Il numero centrale (es. "1.2L / 2L") si aggiorna con un conteggio animato breve |
| Microcopy | "+250ml" transitorio sopra l'anello, dissolve in 600ms |
| Stato finale | Anello pieno = colore di completamento sobrio (non un fuoco d'artificio) |

## 3. Eliminazione (swipe)

| Aspetto | Specifica |
|---|---|
| Animazione | La riga segue il dito durante lo swipe; oltre la soglia, si espande un'icona neutra (cestino), mai rossa per default (P48) |
| Durata/curva | Uscita: 150-200ms, ease-in, la riga collassa in altezza dopo l'uscita orizzontale |
| Aptico | Medio al superamento della soglia di eliminazione (feedback tattile del "punto di non ritorno" leggero, che è comunque reversibile) |
| Transizione | Snackbar sale dal basso (200ms) mentre la riga scompare |
| Microcopy | Snackbar: "Eliminato. [Annulla]" |
| Stato finale | Riga assente dalla lista; snackbar visibile 7s |

## 4. Creazione rapida (cattura)

| Aspetto | Specifica |
|---|---|
| Animazione | Foglio si espande dal punto di tocco del FAB (origin-aware, non un semplice slide-up generico) |
| Durata/curva | 200-250ms, spring morbido (Standard) |
| Aptico | Lieve all'apertura, medio alla conferma di salvataggio |
| Transizione chip parser | Fade-in sequenziale (50ms di sfalsamento tra un chip e l'altro) man mano che il parser interpreta |
| Microcopy | Placeholder del campo: "Scrivi qualsiasi cosa…" (mai "Inserisci testo qui") |
| Stato finale | Foglio chiuso, entità visibile nella lista pertinente con evidenziazione breve (highlight che dissolve in 600ms) |

## 5. Errore di soglia budget

| Aspetto | Specifica |
|---|---|
| Animazione | La barra di progresso del budget cambia colore con transizione morbida (mai un lampeggio) |
| Durata/curva | 300ms, ease-in-out |
| Aptico | Nessuno (non è un'azione dell'utente, è uno stato: l'aptica è riservata alle azioni dirette, C-art. 62) |
| Microcopy | "Alimentari: 82% del budget di luglio" — mai punti esclamativi, mai "ATTENZIONE" |
| Stato finale | Card ambra persistente finché lo stato non cambia |

## 6. Milestone/traguardo raggiunto (obiettivo, abitudine)

| Aspetto | Specifica |
|---|---|
| Animazione | Breve composizione (icona + testo) che appare e si ritira da sola | 
| Durata/curva | Enfasi: 300-350ms con overshoot ≤ 4%, poi permane 800ms max, poi fade-out 200ms |
| Aptico | Medio (distinto dal completamento singolo, ma non eccessivo) |
| Sonoro | Assente di default |
| Microcopy | Frase breve e concreta ("Obiettivo raggiunto: Fondo emergenza") mai iperbolica ("INCREDIBILE!!!") |
| Stato finale | Torna alla vista normale; nessun badge permanente invasivo (P45, C-art. 68) |

## 7. Errore recuperabile (rete, validazione)

| Aspetto | Specifica |
|---|---|
| Animazione | Il campo o la sezione in errore ha un bordo che si anima in ambra (mai rosso acceso) per 200ms poi resta stabile |
| Aptico | Di allerta (impulso doppio breve, distinguibile dagli altri due livelli) |
| Microcopy | Sempre in linguaggio umano (vedi [10-error-experience](10-error-experience.md)) |
| Stato finale | L'elemento resta editabile, l'azione riparatrice è a un tocco |

## 8. Passaggio di stato del pannello sync

| Aspetto | Specifica |
|---|---|
| Animazione | Icona di sync che ruota SOLO mentre attiva; mai un'animazione perenne (comunicherebbe un problema costante) |
| Aptico | Nessuno (evento di sistema, non azione utente) |
| Microcopy | "Tutto sincronizzato" / "In attesa di rete" — mai tecnicismi ("Errore 503") |

## 9. Riordino per trascinamento (drag)

| Aspetto | Specifica |
|---|---|
| Animazione | L'elemento trascinato si solleva leggermente (ombra/scala 1.03x), gli altri elementi si scostano con animazione fluida (150ms per elemento, sfalsata) |
| Aptico | Lieve al sollevamento, lieve al rilascio |
| Stato finale | Nuovo ordine persistito immediatamente (nessun "salva ordine") |

## 10. Apertura/chiusura foglio di dettaglio (GEF, MUC §9)

| Aspetto | Specifica |
|---|---|
| Animazione | Espansione dal punto di tocco (apertura) / collasso verso il basso (chiusura via swipe) |
| Durata/curva | 200-250ms standard (apertura) / 150-200ms uscita (chiusura) |
| Aptico | Nessuno per apertura (azione di navigazione, non di conferma), lieve per chiusura via swipe (conferma del gesto) |
| Stato finale | Lista sottostante ripristinata esattamente com'era (scroll, filtri) |

**UX-R-015**: nessuna microinterazione richiede più di 350ms totali (P57). **UX-R-016**: ogni microinterazione con aptica ha un corrispettivo visivo, mai l'aptica da sola (accessibilità per chi disattiva le vibrazioni). **UX-R-017**: il feedback sonoro è sempre opt-in, mai attivo di default (rispetto dei contesti d'uso silenziosi — ufficio, notte).

---

*Prossimo: [Gesture](08-gestures.md)*
