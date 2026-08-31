# 06 · Entità Calendario (CAL)

> Eredita il [MDC](00-modello-dati-comune.md). **Attenzione**: questo modulo contiene la deroga più ampia al modello comune (MDC §9) — per principio architetturale (CAL-R-01, C-art. 61): non duplichiamo mai un archivio eventi proprietario.

## DM-CAL-01 · CalendarSource (Fonte calendario)

**Descrizione**: un calendario di sistema che l'utente ha scelto di mostrare/scrivere (CAL-001/CAL-054).

| Campo | Tipo concettuale | Obbligatorio |
|---|---|---|
| `nome_provider` | Testo (es. "Google — Personale") | Sì |
| `visibile_in_agenda` | Booleano | Sì |
| `scrivibile` | Booleano | Sì (default falso, CAL-R-03: minimo privilegio) |
| `e_calendario_predefinito_scrittura` | Booleano | Sì (uno solo, per CAL-004/CAL-005) |

**Relazioni**: nessuna relazione strutturale con EventReference (che appartiene al provider, non a noi — vedi sotto).

**Riferimenti Functional Bible**: CAL-001, CAL-R-03.

---

## DM-CAL-02 · EventReference (Riferimento a evento) — **entità in deroga totale**

**Descrizione**: **non è un'entità nostra**: è un riferimento in sola lettura (o scrittura delegata) a un evento che vive nel calendario di sistema. Formalizzata qui solo per completezza del modello concettuale, con la deroga esplicita dichiarata.

| Campo (di sola visualizzazione, mai persistito da noi come fonte di verità) | Note |
|---|---|
| `id_esterno_provider` | Riferimento tecnico al provider OS, non un ID nostro (INV-01 non si applica) |
| `titolo`, `orario_inizio/fine`, `fuso_orario`, `tutto_il_giorno`, `ricorrenza` | Letti in tempo reale dal provider (CAL-001 scheda estesa) |

**Relazioni**: nessun GraphLink diretto persistito da noi verso un EventReference (poiché l'evento non ha un ID stabile nostro) — l'unico collegamento concettuale (es. per GOAL-002 "scadenze/eventi") avviene tramite un **TimeBox** (che quello sì è nostro, vedi sotto) o tramite un Task con data di scadenza.

**Deroga dichiarata (MDC §9)**:
- Nessun ciclo di vita MFC (niente cestino nostro, niente cronologia nostra).
- Nessuna sincronizzazione nostra (il provider ha il proprio sync).
- Nessun export nostro dei suoi dati grezzi (l'export non include eventi di calendario, solo gli eventuali TimeBox creati da noi).

**Stati**: sola lettura (o scrittura delegata se `scrivibile`); "non più disponibile" se l'evento è stato eliminato esternamente (CAL comportamenti specifici).

**Riferimenti Functional Bible**: CAL-001/002/003, CAL-R-01.

---

## DM-CAL-03 · TimeBox (Blocco di tempo)

**Descrizione**: **questa sì è un'entità nostra** — il blocco di tempo che riserviamo su un calendario scrivibile per un Task (CAL-005/TASK-016). A differenza di EventReference, il TimeBox ha un ID nostro stabile e un ciclo di vita gestito da noi, anche se il suo effetto si manifesta come evento nel calendario esterno.

| Campo | Tipo concettuale | Obbligatorio |
|---|---|---|
| `task_id` | Riferimento a Task | Sì |
| `orario_inizio` / `orario_fine` | Timestamp | Sì |
| `calendario_destinazione_id` | Riferimento a CalendarSource (scrivibile) | Sì |
| `marcatore_omnilife` | Booleano implicito (sempre vero: riconoscibile nel calendario esterno) | — |

**Relazioni**: strutturale N:1 con Task (non proprietaria in senso stretto: eliminare il TimeBox non elimina mai il Task collegato, CAL-R-02/MFC-R-12).

**Regole**: spostare il blocco sposta la pianificazione del task collegato (CAL-AC-02); se il task viene ripianificato altrove, il TimeBox si sposta di conseguenza (proponendo, se ambiguo — evento `task.item.rescheduled`).

**Stati**: eredita MDC §6 in forma ridotta (attivo · eliminato — non si archivia, non ha cronologia propria oltre a creazione/spostamento/eliminazione).

**Eventi collegati**: pubblica `cal.timebox.created/moved/deleted`; sottoscrive `task.item.rescheduled`.

**Riferimenti Functional Bible**: CAL-005, CAL-R-02, TASK-016, CAL-AC-02.

---

*Prossimo: [Entità Note](07-entita-note.md)*
