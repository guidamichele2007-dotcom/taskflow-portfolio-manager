# 07 · Entità Note (NOTE)

> Eredita il [MDC](00-modello-dati-comune.md). Modulo v1.x. Deroga di versionamento dichiarata (a snapshot, non per-campo).

## DM-NOTE-01 · Note

**Descrizione**: pensiero non strutturato con formattazione leggera (NOTE-001) — memoria del grafo, non un archivio separato.

| Campo | Tipo concettuale | Obbligatorio | Note |
|---|---|---|---|
| `contenuto` | Testo ricco leggero (titoli, grassetto/corsivo, liste, checklist, separatori — nessuna tabella/colonna/font, P26) | No (anche solo titolo è valido) | Fino a 100.000 caratteri (MFC-E-14) |
| `pin` | Booleano | Sì (default falso) | NOTE-004: max 5 note pinnate contemporaneamente |
| `checklist_items` | Lista di (testo, completato) | No | NOTE-002; **non** genera promemoria (NOTE-R-02: quello è compito di Task) |

**Relazioni**: GraphLink opzionale (multiplo) verso qualsiasi entità (task, spese, obiettivi, abitudini — NOTE-003), bidirezionale e visibile da entrambi i lati.

**Dipendenze**: CAPT (creazione), TASK (conversione checklist→task), Grafo.

**Regole**: NOTE-R-01…04 (richiamate per riferimento). Nessuna gerarchia di cartelle: organizzazione = pin + archivio + collegamenti + ricerca (NOTE-R-01, P32).

**Stati**: eredita MDC §6. Nessuno stato di dominio aggiuntivo.

**Eventi collegati**: pubblica `note.item.created/linked/unlinked`; sottoscrive `capt.item.captured`, `*.item.trashed` (per aggiornare lo stato dei collegamenti mostrati).

**Riferimenti Functional Bible**: NOTE-001…009, NOTE-R-01…04.

---

## DM-NOTE-02 · NoteVersion (Versione della nota) — **deroga di versionamento**

**Descrizione**: **deroga esplicita al modello comune** (MDC §7): il contenuto della nota si versiona a **snapshot completo**, non per singolo campo, perché il testo libero non ha "campi" discreti da tracciare separatamente (NOTE-006).

| Campo | Tipo concettuale | Obbligatorio |
|---|---|---|
| `contenuto_snapshot` | Copia integrale del contenuto al momento del salvataggio | Sì |
| `creato_il` / `creato_da_dispositivo` | Vedi Envelope MDC §3 | Sì |

**Relazioni**: strutturale N:1 con Note (proprietaria e totale).

**Regole**: si conservano 50 versioni o 12 mesi (il maggiore); Free: 10 versioni, mai meno per nessuno (NOTE-R-03, D-05). Il ripristino di una versione precedente crea una **nuova** versione (mai una riscrittura della cronologia, INV-07). In caso di modifica simultanea multi-device: merge per paragrafo dove non ambiguo, altrimenti ultima scrittura vince con la versione precedente conservata qui (NOTE-AC-03) — nulla è mai perso.

**Riferimenti Functional Bible**: NOTE-006, NOTE-R-03, NOTE-AC-03.

---

*Prossimo: [Entità Salute](08-entita-salute.md)*
