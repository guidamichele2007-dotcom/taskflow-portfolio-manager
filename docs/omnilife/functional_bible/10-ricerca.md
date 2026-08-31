# 10 · Ricerca Globale (SRCH)

> Eredita il [MFC](00-modello-funzionale-comune.md).

## 1. Scopo e tracciabilità

| Perché esiste | Principi | JTBD | Decisioni |
|---|---|---|---|
| "So che l'ho messo da qualche parte" → trovato in un unico posto, subito, offline: la ricerca è ciò che rende sostenibile l'assenza di gerarchie (P32) | P32, C-art. 116 | **J7** | D-03 (MVP) |

## 2. Funzioni

| ID | Nome | Descrizione e motivo | Pri | Dipendenze |
|----|------|----------------------|-----|------------|
| SRCH-001 | Ricerca full-text globale | Su tutte le entità dei moduli attivi (titoli, contenuti, note, categorie); risultati incrementali durante la digitazione; interamente locale | M | Indice locale |
| SRCH-002 | Filtri per tipo | Chip: Task, Spese, Abitudini, Note, Obiettivi… (solo moduli attivi) | M | — |
| SRCH-003 | Filtri contestuali | Per tipo selezionato: data, lista, categoria, importo, stato (attivo/archiviato/cestino) | S | — |
| SRCH-004 | Ricerche recenti | Ultime 10, cancellabili singolarmente e in blocco | M | — |
| SRCH-005 | Azioni dai risultati | Completa/apri/ripristina direttamente dal risultato (P13) | M | Moduli |
| SRCH-006 | Ricerca nei contenuti archiviati/cestinati | Inclusi con filtro esplicito (default: esclusi) | M | — |

**Scheda estesa SRCH-001** — *Requisiti*: primi risultati ≤ 100 ms su 50.000 entità (MFC-AC-07); indice aggiornato in transazione con le scritture (mai risultati fantasma o mancanti dopo una modifica); ranking: corrispondenza titolo > contenuto, recente > vecchio, attivo > archiviato — regole fisse e prevedibili, niente "rilevanza" opaca (C-art. 6); tolleranza ai refusi (fuzzy leggero) dichiarata nei risultati ("stavi cercando…"). *Casi limite*: query di 1 carattere → solo prefissi sui titoli; caratteri speciali/emoji → letterali, mai sintassi nascosta che sorprende (MFC-E-17); indice corrotto → ricostruzione automatica in background con stato degradato dichiarato (MFC §4); moduli sensibili bloccati → i risultati appaiono offuscati con invito allo sblocco, il contenuto non trapela nei frammenti (MFC-R-22).

## 3–7. Comportamenti, stati, regole, eventi, edge

- **Regola SRCH-R-01**: la ricerca non registra né sincronizza le query (le "recenti" sono locali al device, escluse dal backup). *Motivo: minimizzazione C-art. 45.*
- **Regola SRCH-R-02**: nessuna ricerca server-side, mai (D-02).
- Eventi: sottoscrive `*.item.*` per l'aggiornamento incrementale dell'indice; non pubblica nulla.
- Stati: vuoto (suggerisce le recenti e i filtri), nessun risultato (offre di includere archivio/cestino), degradato (ricostruzione indice).
- Edge: cambio lingua non invalida l'indice (tokenizzazione multilingua); 0 moduli attivi → cerca comunque nell'Inbox di cattura.

## 8. Criteri di accettazione

- **SRCH-AC-01** — *Dato* 50.000 entità, *quando* l'utente digita "dentista", *allora* i primi risultati appaiono entro 100 ms e includono task, spese e note contenenti la parola, ordinati per le regole fisse.
- **SRCH-AC-02** — *Dato* una spesa appena modificata da "cena" a "pranzo", *quando* l'utente cerca "pranzo", *allora* la trova; cercando "cena" non la trova.
- **SRCH-AC-03** — *Dato* modalità aereo, *allora* la ricerca è identica all'online.
- **SRCH-AC-04** — *Dato* il modulo Finanze bloccato da biometria, *quando* una query intercetta spese, *allora* i risultati finanziari sono offuscati e nessun importo appare nei frammenti.

---

*Prossimo: [Notifiche](11-notifiche.md)*
