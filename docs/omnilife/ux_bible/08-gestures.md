# 08 · Gesture

> Eredita [MUC](00-modello-ux-comune.md). Per ogni gesto: motivazione, vantaggi, alternative scartate, conflitti, accessibilità. Principio guida: **ogni gesto ha sempre un equivalente a tocco** (C-art. 100) — nessuna funzione è raggiungibile solo tramite gesto.

## 1. Tap (tocco singolo)

- **Uso**: azione primaria di ogni elemento (apri, seleziona, spunta).
- **Motivazione**: il gesto più naturale e accessibile; zero apprendimento (P13).
- **Vantaggi**: universale, compatibile con ogni tecnologia assistiva.
- **Alternative scartate**: nessuna — è il gesto di base, non ha sostituti.
- **Conflitti**: nessuno se i target rispettano ≥ 44×44pt (C-art. 102).
- **Accessibilità**: pienamente compatibile con screen reader (doppio tap per attivare l'elemento a fuoco, standard di piattaforma), Switch Control, puntatore esterno.

## 2. Double tap (doppio tocco)

- **Uso**: **non utilizzato come gesto applicativo distinto** in OmniLife (deroga deliberata). Motivazione: P26/P31 — un secondo significato sullo stesso gesto del tap crea ambiguità e rischio di attivazione accidentale (specialmente rilevante per la persona Franca, S7, a bassa competenza tecnologica).
- **Eccezione di sistema**: il doppio-tap per zoom su immagini (note, allegati) è ereditato dal comportamento standard di piattaforma, non un gesto nostro.
- **Alternative preferite**: azioni secondarie passano da long-press → menu esplicito (più scopribile, meno ambiguo).

## 3. Triple tap

- **Uso**: non utilizzato in nessuna funzione applicativa (riservato alle funzioni di accessibilità di sistema, es. zoom di sistema su iOS/Android — mai intercettato o sovrascritto da OmniLife).
- **Motivazione**: rispetto delle convenzioni di sistema (C-art. 106).

## 4. Long press (pressione prolungata)

- **Uso**: apre menu contestuale (duplica/condividi/archivia/elimina — GEF); su liste, avvia riordino per trascinamento; sul FAB, apre le scorciatoie di cattura (CAPT-007).
- **Motivazione**: gesto scopribile (feedback progressivo durante la pressione) per azioni secondarie, senza affollare l'interfaccia di icone (P37).
- **Vantaggi**: riduce il carico visivo mantenendo le azioni accessibili.
- **Alternative scartate**: icona "···" sempre visibile su ogni riga (scelta comunque presente come **equivalente a tocco obbligatorio**, C-art. 100) — il long press è la scorciatoia, l'icona è la via primaria accessibile.
- **Conflitti**: con lo scroll verticale della lista (mitigato: la pressione richiede immobilità per ~400ms prima di attivarsi, distinguendola dall'inizio di uno scroll).
- **Accessibilità**: screen reader espone le stesse azioni tramite "azioni personalizzate" (rotor/menu), mai solo tramite gesto fisico.

## 5. Swipe (scorrimento direzionale)

- **Uso**: swipe orizzontale su riga → eliminazione (con soglia e conferma visiva) o azioni rapide (posticipa task); swipe orizzontale su vista → cambio di sotto-vista segmentata (Oggi↔Prossimi); swipe verticale verso il basso → chiusura foglio di dettaglio.
- **Motivazione**: riduzione tocchi (P13/P21) per le azioni più frequenti.
- **Vantaggi**: velocità per utenti esperti.
- **Alternative scartate**: pulsanti visibili permanenti su ogni riga (troppo affollamento, P37) — mantenuti solo come equivalente nel menu "···".
- **Conflitti**: con lo scroll verticale della lista (gesti su assi diversi, nessun conflitto reale); con il back-gesture di sistema ai bordi schermo (**edge swipe**, vedi §7 — le nostre swipe orizzontali sulle righe iniziano a metà riga, mai dal bordo, per non intercettare il back di sistema).
- **Accessibilità**: ogni swipe ha equivalente: long-press → menu, oppure icona esplicita nel dettaglio.

## 6. Drag & Drop (trascinamento)

- **Uso**: riordino manuale in liste (task, abitudini); time-boxing (trascina un task sull'agenda, CAL-005); riordino card Home (HOME-003).
- **Motivazione**: manipolazione diretta = controllo percepito più alto (P82 — l'ordine è dell'utente).
- **Vantaggi**: immediato, non richiede menu.
- **Alternative scartate**: frecce su/giù per riordinare (più lento, più tocchi) — mantenute come equivalente accessibile (vedi sotto).
- **Conflitti**: con lo scroll della lista durante il trascinamento (mitigato: auto-scroll quando l'elemento trascinato si avvicina al bordo).
- **Accessibilità**: **equivalente obbligatorio** per chi non può eseguire drag (motorio, screen reader): azione "Sposta su/giù" nel menu contestuale o nelle azioni del rotor (C-art. 100).

## 7. Edge Swipe (scorrimento dal bordo)

- **Uso**: **riservato esclusivamente al back-gesture di sistema** (iOS: bordo sinistro; Android: bordo sinistro/destro secondo configurazione di sistema). OmniLife non intercetta mai questa zona per gesti propri.
- **Motivazione**: C-art. 106, MUC §5 — il back di sistema non si tocca mai, è un diritto di navigazione dell'utente.

## 8. Pinch / Zoom

- **Uso**: solo su contenuti che lo richiedono naturalmente — immagini allegate (Note), grafici storici (Abitudini/Salute/Finanze) per ispezione di dettaglio.
- **Motivazione**: gesto standard e atteso su contenuti visivi densi.
- **Alternative scartate**: nessuna necessaria — è già il gesto convenzionale.
- **Accessibilità**: i grafici hanno sempre un **equivalente testuale/tabellare** (C-art. 97) raggiungibile senza pinch, per chi non può eseguire gesti a due dita.

## 9. Scroll

- **Uso**: navigazione verticale di liste e contenuti lunghi; standard, inerziale, con overscroll leggero (mai pull-to-refresh, HOME-007).
- **Motivazione**: pattern universale, zero apprendimento.
- **Accessibilità**: pienamente supportato da screen reader (scorrimento a gesti dedicati di sistema) e tastiera esterna (frecce/paging).

## 10. Hold (mantenimento, distinto dal long-press per azioni continue)

- **Uso**: mantenimento del pulsante di registrazione vocale (CAPT-003) — rilascio = fine registrazione e avvio parsing.
- **Motivazione**: pattern convenzionale (messaggi vocali), feedback continuo (waveform) durante la pressione.
- **Alternative scartate**: tap per iniziare / tap per fermare (più propenso a registrazioni dimenticate attive) — **entrambe le modalità sono comunque offerte** (tocco breve attiva una modalità "tap-to-toggle" per chi ha difficoltà a mantenere la pressione — accessibilità motoria).
- **Accessibilità**: equivalente a doppio-tap (start/stop) sempre disponibile nelle impostazioni di accessibilità del modulo cattura.

## 11. Tabella riassuntiva dei conflitti risolti

| Gesto A | Gesto B | Conflitto potenziale | Risoluzione |
|---|---|---|---|
| Swipe riga (elimina) | Scroll verticale lista | Asse diverso | Nessuno (assi ortogonali) |
| Long press (menu) | Scroll verticale lista | Long press durante scroll | Soglia di immobilità ~400ms prima dell'attivazione |
| Swipe riga | Edge swipe (back di sistema) | Zona di partenza del gesto | Le righe non reagiscono al tocco iniziato nei 20pt del bordo schermo |
| Drag riordino | Scroll lista | Trascinamento verso il bordo | Auto-scroll assistito durante il drag |
| Pinch grafico | Scroll verticale schermata | Gesto a 2 dita vs 1 dito | Nessuno (numero di dita distingue i gesti) |

**UX-R-018**: nessun gesto è l'unico modo di raggiungere una funzione (C-art. 100, verificato per ogni funzione della Functional Bible). **UX-R-019**: ogni gesto personalizzato (non di sistema) richiede una soglia di attivazione che lo distingua da scroll/tap accidentali.

---

*Prossimo: [Empty States](09-empty-states.md)*
