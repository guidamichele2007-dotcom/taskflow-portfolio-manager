# 03 · Modulo Attività (TASK)

> Eredita il [MFC](00-modello-funzionale-comune.md).

## 1. Scopo e tracciabilità

| Perché esiste | Principi | JTBD | Decisioni |
|---|---|---|---|
| Gestire gli impegni volitivi (le cose da fare) con il minimo attrito e il massimo contesto: il modulo d'ingresso più frequente della categoria | P13, P26, P27, P32 | J1, J4, J7, J15 | D-03 (modulo MVP) |

## 2. Funzioni

| ID | Nome | Descrizione e motivo | Pri | Dipendenze |
|----|------|----------------------|-----|------------|
| TASK-001 | Creazione task | Titolo obbligatorio; tutto il resto opzionale con default (MFC-R-03). *Motivo: J1* | M | CAPT |
| TASK-002 | Scadenza con data e/o ora | Data senza ora ammessa ("entro venerdì"); l'ora abilita il promemoria | M | — |
| TASK-003 | Promemoria | Notifica all'ora del task o anticipo configurabile; rispetta il budget notifiche | M | NTF |
| TASK-004 | Ripetizione | Ricorrenze: giornaliera, settimanale (giorni scelti), mensile (giorno n / ultimo giorno), annuale, personalizzata ("ogni 3 giorni"). Regole date limite: MFC-E-09 | M | — |
| TASK-005 | Liste | Raggruppamento a 2 livelli max: Area → Lista (P32). Lista di default: "Attività" | M | — |
| TASK-006 | Priorità a 3 livelli | Alta / media / nessuna. *Motivo: più livelli = paralisi (P28)* | M | — |
| TASK-007 | Sottotask (1 livello) | Checklist interna al task; il completamento del padre chiede conferma se i figli sono aperti | M | — |
| TASK-008 | Completamento | 1 tocco, ovunque il task appaia (lista, Home, widget, ricerca, notifica); undo 7 s | M | — |
| TASK-009 | Posticipa | Rinvio rapido: stasera / domani / weekend / scegli data | M | — |
| TASK-010 | Posticipa intelligente | Suggerimento di rinvio basato sul carico reale del calendario ("giovedì è più libero"). Propone, mai impone | S | CAL (lettura) |
| TASK-011 | Note e allegati leggeri al task | Testo descrittivo; collegamento a note del modulo Note | S | NOTE |
| TASK-012 | Vista Oggi/Prossimi/Tutti | Tre viste fisse; nessuna vista configurabile complessa (P16) | M | — |
| TASK-013 | Riordino manuale | Trascinamento entro lista e nella vista Oggi | M | — |
| TASK-014 | Task scaduti ("In sospeso") | Sezione dedicata, mai mescolata a oggi (HOME-R-01); azione di massa "ripianifica tutti" | M | — |
| TASK-015 | Collegamento a Obiettivi | Un task può contribuire a un obiettivo (arco nel grafo) | S | GOAL |
| TASK-016 | Time-boxing | Trascina un task nell'agenda per riservare tempo (scrive sul calendario di sistema) | S | CAL |
| TASK-017 | Duplicazione task | Copia con campi identici (senza cronologia) | C | — |
| TASK-018 | Archiviazione lista | Le liste completate/stagionali si archiviano intere | S | — |

**Scheda estesa TASK-004 (ripetizione)** — *Requisiti*: la prossima occorrenza si genera **al completamento o alla scadenza** della corrente (mai tutte le occorrenze future materializzate: inquinerebbero ricerca e conteggi); modificare "questa occorrenza" vs "tutte le future" è una scelta esplicita; eliminare una ricorrenza chiede: solo questa / tutte le future. *Casi limite*: ricorrenza completata in anticipo → la prossima si calcola dalla regola, non dal giorno del completamento (salvo ricorrenze "ogni N giorni dall'ultimo completamento", che sono un tipo distinto e dichiarato); cambio fuso: MFC-E-07; 29/2 e giorno 31: MFC-E-09. *Criteri di successo*: zero occorrenze duplicate o saltate su 12 mesi simulati con cambi DST.

**Scheda estesa TASK-008 (completamento)** — *Requisiti*: latenza percepita ≤ 50 ms; effetto propagato a Home/widget/obiettivi entro 1 s; il completato resta visibile attenuato nella vista corrente fino a cambio vista (feedback di progresso, P45). *Casi limite*: completamento da 2 device offline → converge senza doppio evento verso gli obiettivi (idempotenza per ID occorrenza); completamento di task ricorrente da notifica → genera la prossima occorrenza correttamente.

## 3. Comportamenti specifici

- **Apertura task** → foglio di dettaglio espandibile (mantiene il contesto della lista); tutti i campi modificabili inline (MFC-R-05/06).
- **Eliminazione** → cestino 30 gg (MFC-R-10); i sottotask seguono il padre; i collegamenti a obiettivi si sospendono (MFC-R-12).
- **Cronologia** → per campo (MFC-R-07), inclusi completamenti/riaperture.
- **Offline** → tutto identico (MFC §3).

## 4. Stati specifici

| Stato | Comportamento |
|---|---|
| Lista vuota | Stato didattico con esempio di task + cattura (MFC §4) |
| Tutti completati (vista Oggi) | Stato positivo sobrio |
| Task scaduto | Indicatore temporale neutro (né rosso né colpevolizzante — P46, C-art. 73): "da ieri", con azioni di ripianificazione |
| Ricorrenza in pausa | L'utente può sospendere una ricorrenza (vacanza) senza eliminarla; stato "in pausa" visibile |

## 5. Regole di business

| ID | Regola | Motivo |
|----|--------|--------|
| TASK-R-01 | Il titolo è l'unico campo obbligatorio | MFC-R-03; J1 |
| TASK-R-02 | Ordinamento di default vista Oggi: ora pianificata → priorità → ordine manuale; l'ordine manuale prevale sempre dove esiste | Prevedibilità + controllo utente (P82) |
| TASK-R-03 | I task senza data non scadono mai né generano solleciti; emergono solo nella Revisione settimanale | C-art. 54, 60 |
| TASK-R-04 | Il numero di task è illimitato in ogni piano | La cattura non si monetizza (C-art. 170) |
| TASK-R-05 | Filtri disponibili: lista, priorità, data, con/senza obiettivo; ricerca full-text via SRCH | P16 — filtri finiti, niente query builder |
| TASK-R-06 | Import: da testo/CSV e dai formati dei principali competitor (v1.x); ogni import è previsualizzato e annullabile in blocco | C-art. 7 (portabilità in ingresso); MFC-E-13 |
| TASK-R-07 | Export: inclusi nel formato completo (JSON/CSV) con liste, sottotask, cronologia | C-art. 7, 20 |
| TASK-R-08 | Completare una ricorrenza non può mai retro-modificare le occorrenze passate | Integrità storica (statistiche oneste) |

## 6. Eventi

| Direzione | Evento | Effetto |
|---|---|---|
| Pubblica | `task.item.created / completed / uncompleted / rescheduled / deleted` | Home e widget si aggiornano; GOAL ricalcola il progresso; INS osserva (locale) |
| Pubblica | `task.overdue.count.changed` | Home aggiorna la sezione "in sospeso"; REV la include |
| Sottoscrive | `capt.item.captured` (tipo task) | Crea il task |
| Sottoscrive | `cal.day.load.changed` | Alimenta TASK-010 (posticipa intelligente) |
| Sottoscrive | `core.day.changed` | Ricalcola Oggi/scaduti |

## 7. Edge case specifici

- 300 task scaduti dopo un mese di assenza → "in sospeso" raggruppa e offre ripianificazione/archiviazione di massa; mai 300 notifiche (C-art. 59).
- Task con ora 23:59 e completamento a mezzanotte passata → conta nel giorno di completamento effettivo; la statistica distingue pianificato/completato.
- Sottotask completati con padre eliminato → seguono il padre nel cestino e tornano con lui.
- Promemoria con permesso notifiche revocato dopo la creazione → il task resta; Impostazioni segnala i promemoria inattivi (P6 — non mentire).

## 8. Criteri di accettazione

- **TASK-AC-01** — *Dato* un task ricorrente "ogni lunedì 9:00", *quando* l'utente lo completa lunedì, *allora* esiste una sola occorrenza per il lunedì successivo e la cronologia registra il completamento.
- **TASK-AC-02** — *Dato* un task scaduto da 2 giorni, *quando* l'utente apre la vista Oggi, *allora* il task appare nella sezione "in sospeso" con indicazione neutra e azioni di ripianificazione, non mescolato agli elementi di oggi.
- **TASK-AC-03** — *Dato* un task con 3 sottotask aperti, *quando* l'utente completa il padre, *allora* riceve una scelta esplicita (completa tutto / mantieni aperti) e nessun sottotask è modificato senza consenso.
- **TASK-AC-04** — *Dato* una ricorrenza mensile il 31, *quando* arriva febbraio, *allora* l'occorrenza cade l'ultimo giorno di febbraio e la regola resta "giorno 31" per i mesi successivi.
- **TASK-AC-05** — *Dato* lo stesso task completato su due device offline, *quando* sincronizzano, *allora* il task risulta completato una volta sola e l'obiettivo collegato è incrementato una volta sola.

---

*Prossimo: [Modulo Finanze](04-modulo-finanze.md)*
