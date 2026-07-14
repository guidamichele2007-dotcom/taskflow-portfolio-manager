# 04 · Modulo Finanze (FIN)

> Eredita il [MFC](00-modello-funzionale-comune.md).

## 1. Scopo e tracciabilità

| Perché esiste | Principi | JTBD | Decisioni |
|---|---|---|---|
| Dare consapevolezza e controllo del denaro quotidiano (spese, budget, obiettivi di risparmio) senza fare del rigore contabile un lavoro; il modulo a più alta willingness-to-pay | P29, P46, P48, P49 | J1, J5, J6, J15 | D-03 (MVP), **D-07 (niente open banking MVP)**, D-06 (tono per Davide S6) |

## 2. Funzioni

| ID | Nome | Descrizione e motivo | Pri | Dipendenze |
|----|------|----------------------|-----|------------|
| FIN-001 | Registrazione spesa/entrata | Importo obbligatorio; categoria, conto, data, nota opzionali con default (ultimo conto, oggi, categoria proposta). ≤ 3 tocchi | M | CAPT |
| FIN-002 | Categorie | Set predefinito modificabile (rinomina, unisci, archivia, crea; icona+colore dal set di sistema). Max 2 livelli | M | — |
| FIN-003 | Conti multipli | Contanti, carta, conto; saldo per conto; conto di default | M | — |
| FIN-004 | Trasferimenti tra conti | Movimento doppio non conteggiato come spesa/entrata. *Motivo: correttezza minima senza partita doppia* | M | FIN-003 |
| FIN-005 | Budget mensili per categoria | Soglia per categoria (o globale); stato visivo a 3 livelli (ok / attenzione ≥ 80% / superato) — mai rosso-allarme (P48) | M | FIN-002 |
| FIN-006 | Ricorrenze | Spese/entrate fisse auto-registrate alla scadenza con notifica raggruppata; modificabili prima dell'esecuzione | M | NTF |
| FIN-007 | Report mensile | Una schermata: entrate, uscite, top 5 categorie, confronto mese precedente; parole prima dei numeri (P29) | M | — |
| FIN-008 | Obiettivi di risparmio | Importo target + data; accantonamenti manuali o regola automatica proposta; proiezione onesta ("di questo passo: marzo") | S | GOAL |
| FIN-009 | Multi-valuta | Valuta per conto; conversione a valuta primaria con tasso aggiornato quando online (ultimo tasso noto offline, dichiarato) | C→S per mercati non-euro | — |
| FIN-010 | Storico e filtri | Elenco transazioni con filtri: periodo, categoria, conto, importo min/max, testo | M | SRCH |
| FIN-011 | Modifica/divisione transazione | Ogni campo modificabile; una transazione può essere divisa in più categorie ("spesa 80 € = cibo 60 + casa 20") | M / S (divisione) | — |
| FIN-012 | Import CSV bancario | Import manuale con mappatura colonne guidata, anteprima, dedup e undo di blocco. *Ponte in attesa di D-07-rev* | S | — |
| FIN-013 | Immagine scontrino | Foto allegata alla transazione (solo storage locale/cifrato; nessun OCR cloud) | C | — |
| FIN-014 | Chip di inserimento rapido | Importi e categorie recenti come chip a 1 tocco nel tastierino | M | — |

**Scheda estesa FIN-001** — *Requisiti*: tastierino dedicato con chip (FIN-014); default intelligenti appresi (P18); retrodatazione libera; importo zero rifiutato con messaggio chiaro; importi negativi non esistono (il tipo spesa/entrata è esplicito). *Vincoli*: nessun invio a server (E2E); la valuta si mostra sempre (MFC-E-17). *Casi limite*: transazione a cavallo di mezzanotte → conta nel giorno scelto dall'utente (default: ora locale della creazione); modifica del *mese* di una transazione → i budget di entrambi i mesi si ricalcolano. *Criteri di successo*: mediana di inserimento ≤ 3 s; % transazioni con categoria corretta senza correzione ≥ 80% dopo 30 giorni.

**Scheda estesa FIN-005 (budget)** — *Requisiti*: il periodo del budget è il mese di calendario del fuso utente; le soglie di attenzione notificano al massimo 1 volta per soglia per mese (C-art. 59); superare il budget non blocca nulla — informa e propone (P49: "puoi spostare 20 € da Svago"). *Casi limite*: budget creato a metà mese → pro-rata dichiarato o intero a scelta; transazioni retrodatate su mesi chiusi → il report storico si aggiorna con nota di modifica; cambio fuso a cavallo del cambio mese → il mese è quello del fuso corrente, i totali non contano mai doppio (MFC-E-07).

## 3. Comportamenti specifici

- **Eliminazione conto** → richiede decisione sulle transazioni (sposta su altro conto / cestina con il conto); mai eliminazione silenziosa di transazioni (MFC-R-12).
- **Eliminazione categoria** → le transazioni passano a "Senza categoria" (mai perse); l'unione di categorie riassegna in blocco con undo.
- **Cronologia** → ogni modifica di importo/categoria è tracciata (MFC-R-07): la fiducia contabile richiede l'audit trail.
- **Autorizzazioni** → Finanze è modulo sensibile di default (MFC-R-21): sblocco richiesto se biometria attiva; importi mai in chiaro su widget/notifiche senza opt-in (MFC-R-22).

## 4. Stati specifici

| Stato | Comportamento |
|---|---|
| Primo uso | Stato didattico: 1 conto proposto ("Contanti"), esempio di spesa, invito al primo budget dopo 5 transazioni (P36) |
| Budget in attenzione/superato | Ambra con proposta concreta; MAI rosso-panico né notifiche ripetute (P46/48/49; persona Davide) |
| Tasso di cambio non aggiornato (offline) | Il valore convertito mostra "al tasso del [data]" — mai numeri spacciati per attuali (P6) |
| Mese senza transazioni | Report onesto "nessun dato per questo mese", con distinzione da "zero spese" |

## 5. Regole di business

| ID | Regola | Motivo |
|----|--------|--------|
| FIN-R-01 | I trasferimenti non sono mai entrate/uscite nei report | Correttezza: gonfierebbero i totali |
| FIN-R-02 | Il saldo di un conto è derivato dalle transazioni; la "correzione saldo" crea una transazione di rettifica esplicita, mai una modifica magica | Audit trail; C-art. 6 |
| FIN-R-03 | Le transazioni non si bloccano mai in modifica (nessuna "chiusura contabile" consumer); i report storici riflettono sempre i dati correnti con nota di ricalcolo | Semplicità (P26) su rigore ragionieristico non richiesto dal job J6 |
| FIN-R-04 | Ordinamento default: data desc; in un giorno: ora di creazione desc | Prevedibilità |
| FIN-R-05 | Free: tutte le funzioni M senza limiti di transazioni; storico report oltre 3 mesi = Plus | D-05: capacità, non dignità; i dati restano sempre esportabili (C-art. 17) |
| FIN-R-06 | Import CSV: mai auto-import senza anteprima; duplicati rilevati per (data, importo, descrizione) e proposti per l'esclusione | C-art. 67; MFC-E-13 |
| FIN-R-07 | Export: transazioni, budget, conti e categorie in CSV/JSON con tutte le valute originali | C-art. 7, 20 |
| FIN-R-08 | Nessun consiglio d'investimento, mai; gli insight parlano di comportamenti osservati, non di prodotti finanziari | Perimetro anti-persona A2; C-art. 146 |

## 6. Eventi

| Direzione | Evento | Effetto |
|---|---|---|
| Pubblica | `fin.expense.created / updated / deleted` · `fin.income.created…` | Home aggiorna la card budget; GOAL ricalcola risparmi collegati; INS osserva |
| Pubblica | `fin.budget.threshold.crossed` (80% / 100%) | Home mostra lo stato; NTF valuta una notifica (nel budget quotidiano) |
| Pubblica | `fin.recurrence.executed` | NTF raggruppa nel digest |
| Sottoscrive | `capt.item.captured` (tipo spesa/entrata) | Crea la transazione |
| Sottoscrive | `core.day.changed` | Esegue ricorrenze scadute; ricalcola il mese se cambiato |

## 7. Edge case specifici

- Ricorrenza in scadenza durante un periodo offline lungo → si esegue al primo avvio successivo con la data corretta (retro-registrata), notifica raggruppata.
- Cambio della valuta primaria → i report ricalcolano; i valori storici restano nella valuta originale con conversione dichiarata.
- Importi enormi (> 10^9) o micro (0,01) → gestiti senza errori di arrotondamento visibili; l'arrotondamento segue la valuta (JPY senza decimali).
- Stessa ricorrenza modificata su 2 device offline → converge (MFC-R-08); l'esecuzione non avviene mai due volte per lo stesso periodo (chiave di idempotenza per periodo).
- 10 anni di storico (50k+ transazioni) → report mensili e ricerca nei budget di prestazione (MFC-E-14).

## 8. Criteri di accettazione

- **FIN-AC-01** — *Dato* il tastierino spese, *quando* l'utente inserisce 82 € su categoria proposta e conferma, *allora* la transazione esiste con conto default e data odierna, in ≤ 3 tocchi dal tocco su cattura.
- **FIN-AC-02** — *Dato* un budget "Alimentari 400 €" con 310 € spesi, *quando* l'utente registra 45 €, *allora* lo stato passa ad "attenzione" (≥ 80%), la card Home si aggiorna e al massimo una notifica di soglia viene valutata per il mese.
- **FIN-AC-03** — *Dato* un trasferimento di 200 € tra due conti, *quando* l'utente consulta il report mensile, *allora* entrate e uscite non includono il trasferimento e i saldi dei due conti sono corretti.
- **FIN-AC-04** — *Dato* un CSV bancario con 2 righe identiche a transazioni esistenti, *quando* l'utente importa, *allora* l'anteprima segnala i 2 duplicati esclusi per default e l'intero import è annullabile in blocco.
- **FIN-AC-05** — *Dato* biometria attiva e widget budget consentito senza importi, *quando* il telefono è bloccato, *allora* il widget mostra lo stato del budget senza cifre.
- **FIN-AC-06** — *Dato* una ricorrenza mensile eseguita mentre due device erano offline, *quando* sincronizzano, *allora* esiste una sola transazione per il periodo.

---

*Prossimo: [Modulo Abitudini](05-modulo-abitudini.md)*
