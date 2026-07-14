# 09 · Modulo Obiettivi (GOAL)

> Eredita il [MFC](00-modello-funzionale-comune.md). **La funzione-firma dell'ecosistema**: l'obiettivo trasversale è ciò che nessun competitor può replicare senza rifondare la propria architettura (J5 — lo score JTBD più alto).

## 1. Scopo e tracciabilità

| Perché esiste | Principi | JTBD | Decisioni |
|---|---|---|---|
| Trasformare un sogno concreto in un piano che attraversa denaro, tempo e azioni, con progresso aggregato visibile: la dimostrazione vivente del grafo | P77–78 (valore dell'integrazione), P45 | **J15, J16, J5** | D-01, D-03 (v1.x — richiede dati collegabili) |

## 2. Funzioni

| ID | Nome | Descrizione e motivo | Pri | Dipendenze |
|----|------|----------------------|-----|------------|
| GOAL-001 | Creazione obiettivo | Titolo + (opzionali) data target, descrizione, immagine simbolica dal set | S | — |
| GOAL-002 | Collegamento contributi | All'obiettivo si collegano: liste/task (TASK-015), obiettivi di risparmio (FIN-008), abitudini (HAB-012), scadenze/eventi, note (NOTE-003). Il collegamento avviene da entrambi i lati | S | Grafo; moduli |
| GOAL-003 | Progresso aggregato | Composizione dei progressi dei contributi in una vista unica; ogni contributo mostra il proprio stato; l'aggregato è *descrittivo* prima che percentuale (P29) | S | GOAL-002 |
| GOAL-004 | Proiezione onesta | "Di questo passo, il risparmio arriva a marzo (dopo la data target)": proiezioni con le ipotesi dichiarate, mai promesse (P6, C-art. 145) | S | FIN-008 |
| GOAL-005 | Traguardi intermedi | Milestone manuali con celebrazione sobria (P45/68) | C | — |
| GOAL-006 | Pausa/archiviazione obiettivo | La vita cambia: l'obiettivo si sospende senza smontare i collegamenti | S | MFC |
| GOAL-007 | Completamento | Chiusura celebrata sobriamente + proposta di condivisione ESTERNA (immagine, dati scelti dall'utente — C-art. 55: mai social interno) | S | — |
| GOAL-008 | Vista "i miei obiettivi" | Elenco con stato sintetico; max consigliato 5 attivi (suggerimento, non limite) | S | — |

**Scheda estesa GOAL-003 (progresso aggregato — la specifica della feature-firma)** — *Definizione normativa*: il progresso NON è una media forzata di unità incommensurabili. Ogni contributo espone il proprio asse: risparmio = €/target; task = completati/totali; abitudine = aderenza nel periodo; scadenze = superate/venture. L'aggregato mostra: (a) fascia verbale complessiva ("ben avviato", "in corso", "in stallo su un fronte"), calcolata con regole trasparenti (il peggiore dei fronti attivi domina la fascia — un obiettivo non è "quasi fatto" se i soldi mancano tutti); (b) i fronti, ciascuno col suo progresso nativo. *Vincolo*: la formula è spiegabile in una frase nella UI ("perché vedo questo stato?") — mai un punteggio opaco (C-art. 6). *Casi limite*: obiettivo senza contributi → mostra solo la data e invita a collegare; contributo in modulo disattivato → "in pausa", escluso dalla fascia (mai contato zero — GAL-003); contributo eliminato → esce dall'aggregato, la cronologia dell'obiettivo lo ricorda. *Criterio di successo*: gli utenti con ≥ 1 obiettivo con ≥ 2 fronti hanno D90 superiore di ≥ 1,5× rispetto agli utenti senza (la scommessa J5 misurata).

## 3. Comportamenti specifici

- Ciclo MFC completo; l'eliminazione di un obiettivo **non tocca mai** i contributi collegati (MFC-R-12: task, risparmi e abitudini restano vivi, scollegati).
- I contributi aggiornano l'obiettivo via eventi (mai polling, mai lettura incrociata diretta — C-art. 184).

## 4. Stati specifici

Senza contributi (didattico: proponi 3 collegamenti tipo) · In stallo (nessun progresso su un fronte da 30 gg → tono di ripartenza, appare in REV — mai push di colpa, C-art. 53) · Data target superata (l'obiettivo non "scade": propone rinegoziazione della data o chiusura onesta) · Completato (stato permanente consultabile).

## 5. Regole di business

| ID | Regola | Motivo |
|----|--------|--------|
| GOAL-R-01 | Un contributo può servire più obiettivi (una corsa può servire "maratona" e "salute") | Realismo del grafo |
| GOAL-R-02 | Free: 1 obiettivo attivo; Plus: illimitati | D-05 — la leva di conversione naturale del valore composto |
| GOAL-R-03 | La fascia complessiva è dominata dal fronte peggiore | Onestà del quadro (P6): l'ottimismo finto è un debito |
| GOAL-R-04 | Le proiezioni dichiarano sempre l'ipotesi ("al ritmo delle ultime 8 settimane") | C-art. 145 |
| GOAL-R-05 | Export: obiettivi con l'elenco dei riferimenti ai contributi | C-art. 7 |

## 6. Eventi

Sottoscrive: `task.item.completed`, `fin.expense/income.created` (per i risparmi collegati), `hab.habit.completed`, `note.item.linked`, `core.module.activated/deactivated` → ricalcolo. Pubblica: `goal.progress.changed`, `goal.milestone.reached`, `goal.completed` (Home/widget aggiornano; INS osserva; REV include gli obiettivi in stallo).

## 7. Edge case specifici

- Contributi con date incoerenti (task oltre la data target) → segnalazione discreta alla creazione del collegamento, mai blocco.
- Obiettivo con 100 contributi → la vista raggruppa per fronte; prestazioni nei budget.
- Merge multi-device di collegamenti creati offline → unione insiemistica (i collegamenti sono un set CRDT: mai persi, i doppi si normalizzano).

## 8. Criteri di accettazione

- **GOAL-AC-01** — *Dato* l'obiettivo "Giappone" con risparmio (500/2500 €), lista task (3/8) e abitudine "giapponese 3×/sett" all'80%, *quando* l'utente apre l'obiettivo, *allora* vede i 3 fronti con i progressi nativi e una fascia complessiva coerente con la regola del fronte peggiore, con spiegazione disponibile.
- **GOAL-AC-02** — *Dato* una spesa che alimenta il risparmio collegato, *quando* viene registrata, *allora* il progresso dell'obiettivo si aggiorna entro 1 s senza aprire il modulo Finanze.
- **GOAL-AC-03** — *Dato* il modulo Abitudini disattivato, *quando* l'utente guarda l'obiettivo, *allora* il fronte abitudine è "in pausa", escluso dalla fascia, e ritorna identico alla riattivazione.
- **GOAL-AC-04** — *Dato* l'eliminazione dell'obiettivo, *allora* task, risparmi e abitudini collegati esistono ancora, scollegati, e il cestino consente il ripristino completo dei collegamenti per 30 giorni.

---

*Prossimo: [Ricerca](10-ricerca.md)*
