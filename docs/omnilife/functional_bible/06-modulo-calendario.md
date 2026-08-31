# 06 · Modulo Calendario (CAL)

> Eredita il [MFC](00-modello-funzionale-comune.md). **Principio fondante**: il calendario dell'utente esiste già (sistema/Google/Exchange). Non creiamo un silo di eventi nuovo: leggiamo e scriviamo i calendari di sistema (C-art. 61 — nessuna duplicazione; doc PB 05, riga Google Calendar: "non competiamo: ci integriamo").

## 1. Scopo e tracciabilità

| Perché esiste | Principi | JTBD | Decisioni |
|---|---|---|---|
| Dare la dimensione *tempo* al grafo: un'unica timeline dove eventi (di sistema), task pianificati e abitudini convivono; e il time-boxing che collega intenzione e tempo | P31, P61, P85 | J4, J5 | D-01; ADR (fonte: calendari di sistema) |

## 2. Funzioni

| ID | Nome | Descrizione e motivo | Pri | Dipendenze |
|----|------|----------------------|-----|------------|
| CAL-001 | Lettura calendari di sistema | Import in tempo reale via provider OS (EventKit/CalendarProvider); selezione dei calendari da mostrare | M | Permesso calendario |
| CAL-002 | Agenda unificata | Timeline del giorno/settimana con: eventi + task con ora + abitudini con promemoria. Fonte visiva unica del "tempo impegnato" | M | CAL-001, TASK, HAB |
| CAL-003 | Dettaglio evento (sola lettura+) | Vista evento con azione "apri nell'app calendario" per modifiche complesse; modifiche semplici (orario, titolo) direttamente se il calendario è scrivibile | M / S (scrittura) | CAL-001 |
| CAL-004 | Creazione evento rapido | Da cattura ("pranzo con Leo domani 13") → evento sul calendario di sistema scelto come default | S | CAPT, permesso scrittura |
| CAL-005 | Time-boxing dei task | Trascina un task nella timeline → blocco di tempo scritto sul calendario di sistema (con marcatore OmniLife); spostare il blocco sposta la pianificazione del task | S | TASK-016 |
| CAL-006 | Carico del giorno | Indicatore di densità della giornata (ore impegnate/libere) che alimenta il "posticipa intelligente" | S | CAL-001 |
| CAL-007 | Vista mensile | Panoramica compatta con densità; tocco → giorno | S | — |

**Scheda estesa CAL-001/002** — *Requisiti*: la lettura riflette le modifiche esterne entro pochi secondi dall'apertura (osservatori di sistema); gli eventi restano di proprietà del provider — noi non li dupliciamo mai nel grafo (solo riferimenti); eventi di tutti i tipi (tutto il giorno, multi-giorno, ricorrenti, con fuso proprio) mostrati correttamente. *Casi limite*: permesso negato → il modulo funziona con soli task/abitudini e stato "calendari non collegati" con azione; calendario rimosso dall'account di sistema → i riferimenti (time-box) mostrano "evento non più disponibile", mai crash; eventi con fuso diverso (volo NY): mostrati nel fuso corrente con fuso di origine dichiarato (MFC-E-07). *Criteri di successo*: la vista agenda risponde a "com'è la mia giornata?" senza aprire l'app calendario nel 90% dei casi (misura: tasso di tap-through verso l'app esterna).

## 3. Comportamenti specifici (deroghe)

- **Gli eventi di sistema NON seguono il ciclo di vita MFC §2**: sono entità esterne in sola lettura/scrittura delegata; niente cestino nostro, niente cronologia nostra (la fonte è il provider). *Deroga dichiarata e motivata: la proprietà del dato resta al provider scelto dall'utente.*
- **Offline**: gli eventi mostrano l'ultimo stato noto dal provider locale (i provider OS sono essi stessi offline-capable); i time-box creati offline si scrivono al provider quando possibile, con coda propria.
- **Eliminare un time-box** elimina il blocco dal calendario, MAI il task collegato (MFC-R-12).

## 4. Stati specifici

| Stato | Comportamento |
|---|---|
| Nessun permesso | Modulo utile ridotto (task+abitudini in timeline) + spiegazione beneficio; mai nagging (C-art. 32-33) |
| Nessun evento oggi | "Giornata libera" positivo |
| Calendario esterno in errore (provider) | Stato degradato per la sola fonte in errore; il resto della timeline vive (C-art. 122) |

## 5. Regole di business

| ID | Regola | Motivo |
|----|--------|--------|
| CAL-R-01 | Mai creare un archivio eventi proprietario: la fonte di verità degli eventi è il provider di sistema | C-art. 61; interoperabilità |
| CAL-R-02 | I time-box sono riconoscibili (marcatore) e reversibili; eliminarli non tocca i task | MFC-R-12 |
| CAL-R-03 | La scrittura avviene solo sui calendari che l'utente ha marcato scrivibili; default: nessuno | C-art. 32 (minimo privilegio) |
| CAL-R-04 | Il "carico del giorno" considera solo ore di veglia configurabili (default 8–22) | Sensatezza del suggerimento |

## 6. Eventi

| Direzione | Evento | Effetto |
|---|---|---|
| Pubblica | `cal.day.load.changed` | TASK-010 (posticipa intelligente); Home aggiorna densità |
| Pubblica | `cal.timebox.created / moved / deleted` | TASK aggiorna la pianificazione del task collegato |
| Sottoscrive | `task.item.rescheduled` | Sposta l'eventuale time-box collegato (proponendo, se lo spostamento è ambiguo) |
| Sottoscrive | `capt.item.captured` (tipo evento) | CAL-004 |

## 7. Edge case specifici

- Evento ricorrente di sistema modificato "solo questa occorrenza" esternamente → la vista riflette l'eccezione (deleghiamo al provider la logica).
- Due provider con lo stesso evento (invito duplicato) → mostrati entrambi; offerta di nascondere un calendario, mai dedup automatica di dati non nostri.
- Time-box su evento che l'utente elimina esternamente → il task torna "pianificato senza ora" con segnalazione discreta.
- DST/multi-giorno/mezzanotte: MFC-E-07/08.

## 8. Criteri di accettazione

- **CAL-AC-01** — *Dato* il permesso calendario concesso e 3 eventi oggi, *quando* l'utente apre l'agenda, *allora* vede eventi, task con ora e abitudini con promemoria in un'unica timeline ordinata.
- **CAL-AC-02** — *Dato* un task trascinato alle 15:00 di domani, *allora* esiste un blocco sul calendario scrivibile di default, e spostando il blocco alle 17:00 il task risulta pianificato alle 17:00.
- **CAL-AC-03** — *Dato* permesso negato, *quando* l'utente usa il modulo, *allora* nessun errore: timeline con task/abitudini e invito contestuale non ripetitivo.
- **CAL-AC-04** — *Dato* un evento con fuso di New York visto da Roma, *allora* l'orario mostrato è quello locale corrente con indicazione del fuso di origine.

---

*Prossimo: [Modulo Note](07-modulo-note.md)*
