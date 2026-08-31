# 15 · Insight Engine (INS)

> Eredita il [MFC](00-modello-funzionale-comune.md). Il motore che trasforma il grafo in conoscenza di sé (J17) — **interamente on-device** (D-02).

## 1. Scopo e tracciabilità

| Perché esiste | Principi | JTBD | Decisioni |
|---|---|---|---|
| Il valore composto dei dati integrati: osservazioni trasversali che nessuna app monodominio può fare ("spendi di più nelle settimane con poco sonno") — senza inviare nulla a nessuno | P29, P36; C-art. 11, 145-146 | **J17**, J6, J5 | D-02; Exist.io come proof di domanda (PB 05 #51) |

## 2. Funzioni

| ID | Nome | Descrizione e motivo | Pri | Dipendenze |
|----|------|----------------------|-----|------------|
| INS-001 | Insight contestuali | Osservazioni singole mostrate in Home (max 1/giorno — HOME-005): soglie, tendenze, correlazioni semplici | S | Moduli; regole |
| INS-002 | Digest settimanale | La sintesi trasversale (UC-12): numeri della settimana con drill-down nei moduli | S | NTF-008 |
| INS-003 | Motore a regole dichiarative | Le regole sono dati (config firmata, aggiornabile senza release); ogni regola dichiara: trigger, condizione, testo, priorità, frequenza massima | S | Registry (config) |
| INS-004 | Correlazioni trasversali | Correlazioni tra domini calcolate localmente, con soglia di significatività e linguaggio prudente ("sembra che…", mai causalità — C-art. 146) | C | ≥ 2 moduli con storico |
| INS-005 | Controllo utente | Ogni insight: utile/non utile; il feedback tara le regole localmente; disattivazione per famiglia di insight | S | — |

**Scheda estesa INS-003** — *Requisiti*: nessuna regola può inviare dati fuori dal device (il motore non ha accesso alla rete: vincolo architetturale, non policy); ogni regola ha frequenza massima (default: 1 volta/30 gg per famiglia); il tono delle regole segue il glossario (mai colpa — C-art. 53); le regole citano i dati con trasparenza ("basato sulle tue ultime 4 settimane"). *Casi limite*: dati insufficienti → nessun insight (mai insight banali per riempire — P37); insight su modulo appena disattivato → la regola tace (C-art. 184); config firmata non valida → si usa l'ultima valida.

## 3–7. Regole, stati, edge (sintesi)

| ID | Regola | Motivo |
|----|--------|--------|
| INS-R-01 | Mai insight che confrontino l'utente con altri utenti | C-art. 55 |
| INS-R-02 | Mai insight commerciali ("con Plus sapresti…") | C-art. 64, 172 |
| INS-R-03 | Ogni insight è archiviabile e la famiglia disattivabile dall'insight stesso | C-art. 63, 72 |
| INS-R-04 | Le correlazioni sotto la soglia statistica non si mostrano; il linguaggio è sempre probabilistico | C-art. 145-146 |
| INS-R-05 | Free: insight di soglia e tendenza; trasversali (INS-004) e storico illimitato: Plus | D-05 — il valore composto è la leva naturale |

- Eventi: sottoscrive gli eventi dei moduli (osservatore); pubblica `ins.insight.available` (Home), `ins.digest.ready` (NTF-008).
- Stati: silente (dati insufficienti — nessuno stato visibile: l'assenza è il default), digest vuoto (non si invia — mai contenuto di riempimento).
- Edge: cambio fuso/DST nelle aggregazioni settimanali → settimane di durata anomala gestite senza doppi conteggi (MFC-E-07/08); azzeramento apprendimento (SET) → azzera anche i feedback INS-005.

## 8. Criteri di accettazione

- **INS-AC-01** — *Dato* un utente con 6 settimane di dati Finanze+Abitudini, *quando* una regola trasversale supera la soglia, *allora* l'insight appare al massimo una volta, in Home, con spiegazione della base dati, archiviabile.
- **INS-AC-02** — *Dato* il device in modalità aereo per una settimana, *allora* insight e digest funzionano identici (tutto è locale).
- **INS-AC-03** — *Dato* il feedback "non utile" su una famiglia 2 volte, *allora* la frequenza della famiglia si riduce e la terza occorrenza offre la disattivazione.
- **INS-AC-04** — *Dato* l'analisi del traffico di rete, *allora* nessun dato degli insight lascia il device; l'unico traffico correlato è il download della config firmata delle regole.

---

*Prossimo: [Moduli Futuri](16-moduli-futuri.md)*
