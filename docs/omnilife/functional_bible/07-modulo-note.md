# 07 · Modulo Note (NOTE)

> Eredita il [MFC](00-modello-funzionale-comune.md).

## 1. Scopo e tracciabilità

| Perché esiste | Principi | JTBD | Decisioni |
|---|---|---|---|
| Catturare pensiero non strutturato e dargli contesto: la nota che si collega a task, spese, obiettivi ed eventi diventa memoria del grafo — non un archivio separato (anti-silo, P2) | P27, P54 | J1, J7, J17 | v1.x (roadmap PB 13) |

## 2. Funzioni

| ID | Nome | Descrizione e motivo | Pri | Dipendenze |
|----|------|----------------------|-----|------------|
| NOTE-001 | Nota a testo ricco leggero | Titoli, grassetto/corsivo, liste, checklist, separatori. NON un word processor (P26): niente tabelle, colonne, font | S | CAPT |
| NOTE-002 | Checklist nelle note | Caselle spuntabili; una checklist NON è un task (non ha data/promemoria); la conversione in task è offerta esplicitamente | S | TASK |
| NOTE-003 | Collegamento a entità | Una nota si collega a task/spese/obiettivi/abitudini; il collegamento è bidirezionale e visibile da entrambi i lati | S | Grafo |
| NOTE-004 | Pin | Note fissate in cima; max 5 (oltre, il pin perde significato) | S | — |
| NOTE-005 | Archivio | Come MFC; la ricerca resta la via primaria (P32: ricerca > cartelle) | S | SRCH |
| NOTE-006 | Versioni complete | Cronologia per versioni del contenuto (non per campo — deroga MFC dichiarata: il testo lungo si versiona a snapshot); ripristino di versione = nuova versione | S | — |
| NOTE-007 | Condivisione in uscita | Esporta/condividi come testo/PDF via share di sistema (la nota resta privata; esce una copia) | S | OS |
| NOTE-008 | Immagini inline | Immagini nel corpo (storage locale cifrato; compressione automatica) | C | — |
| NOTE-009 | Nota vocale trascritta | Registrazione + trascrizione on-device dove disponibile | C | Permesso microfono |

**Scheda estesa NOTE-001/003** — *Requisiti*: editor reattivo fino a 100.000 caratteri (MFC-E-14); salvataggio continuo (MFC-R-06) con draft resistente al kill; i collegamenti si creano da menu contestuale o scrivendo `@` (ricerca inline delle entità). *Casi limite*: nota collegata a entità poi cestinata → il collegamento mostra lo stato (MFC-R-12); incolla di testo enorme con formattazione esterna → normalizzato al nostro set minimo, mai crash; modifica simultanea della stessa nota su 2 device → merge per paragrafi dove non ambiguo, altrimenti ultima scrittura vince con versione precedente in cronologia (MFC-R-08 adattata + NOTE-006: nulla è mai perso). *Criteri di successo*: ≥ 30% delle note attive ha almeno un collegamento entro 90 giorni (misura che il grafo viene usato — altrimenti NOTE è solo un notes-clone e va ripensato, P104).

## 3. Comportamenti specifici

- Apertura → ultima posizione di scrittura; modifica → autosave; eliminazione → cestino MFC.
- Le note sono modulo sensibile *opzionale* (default: non bloccato; l'utente può marcarlo — MFC-R-21).

## 4. Stati specifici

Vuoto didattico (esempio con checklist e un collegamento dimostrativo) · Conflitto di versione → risolto con cronologia, mai dialogo · Nota di sola lettura (condivisa futura) → etichetta chiara.

## 5. Regole di business

| ID | Regola | Motivo |
|----|--------|--------|
| NOTE-R-01 | Nessuna gerarchia di cartelle; organizzazione = pin + archivio + collegamenti + ricerca | P32; l'anti-J18 (manutenzione zero) |
| NOTE-R-02 | Una checklist non genera promemoria: per i promemoria c'è TASK (conversione a 1 tocco) | C-art. 61 — nessuna duplicazione di concetti |
| NOTE-R-03 | Le versioni si conservano: 50 versioni o 12 mesi (il maggiore); Free: 10 versioni | D-05 (capacità); mai meno di 10 per nessuno |
| NOTE-R-04 | Export: markdown + JSON con collegamenti espressi come riferimenti | C-art. 7, 20 |

## 6. Eventi

Pubblica: `note.item.created/linked/unlinked` (GOAL e i moduli mostrano le note collegate; INS osserva). Sottoscrive: `capt.item.captured` (tipo nota); `*.item.trashed` (aggiorna lo stato dei collegamenti).

## 7. Edge case specifici

- Immagine che porta il DB oltre soglie di spazio → MFC-E-03 con suggerimento di pulizia media.
- Trascrizione vocale non disponibile sul device → NOTE-009 salva solo l'audio con nota onesta.
- RTL/emoji/markdown incollato: MFC-E-17.

## 8. Criteri di accettazione

- **NOTE-AC-01** — *Dato* una nota aperta, *quando* il processo viene ucciso durante la digitazione, *allora* alla riapertura il testo digitato è presente fino all'ultimo carattere confermato dal salvataggio continuo (perdita massima: 1 s di digitazione).
- **NOTE-AC-02** — *Dato* `@` digitato nel corpo, *quando* l'utente cerca "Giappone" e seleziona l'obiettivo, *allora* la nota è collegata e l'obiettivo mostra la nota tra i suoi collegamenti.
- **NOTE-AC-03** — *Dato* la stessa nota modificata su 2 device offline in paragrafi diversi, *quando* sincronizzano, *allora* entrambe le modifiche sono presenti e nessuna versione è persa.
- **NOTE-AC-04** — *Dato* una checklist di 5 voci, *quando* l'utente converte una voce in task, *allora* esiste un task con quel titolo collegato alla nota, e la voce resta nella nota marcata come convertita.

---

*Prossimo: [Modulo Salute](08-modulo-salute.md)*
