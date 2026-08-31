# 05 · User Flows Estesi (funzioni Should/Could — v1.x/v2.x)

> Eredita [MUC](00-modello-ux-comune.md) e [04](04-user-flows-core-mvp.md). Le funzioni Should/Could seguono lo stesso schema con dettaglio proporzionato alla loro maturità di rilascio ([Functional Bible §17](../functional_bible/17-matrici.md)); ogni scheda estesa sarà completata al momento dell'ingresso in sviluppo (regola MFC §1.3 applicata anche qui).

## FLOW-CAL-01 · Consultare l'agenda unificata e fare time-boxing (CAL-002, 005)

| # | Azione utente | Risposta app | Tempo | Feedback | Edge case | Annullo |
|---|---|---|---|---|---|---|
| 1 | Apre Calendario | Timeline del giorno: eventi + task con ora + abitudini | ≤ 300ms | Skeleton se lento | Permesso negato → timeline solo task/abitudini + invito | — |
| 2 | Trascina un task senza ora nella fascia 15:00 | Si crea un blocco (time-box) sul calendario di default | Durante il drag: feedback continuo di posizione (snap ai 15 min) | Aptica alla conclusione del drag | Fascia già occupata → il blocco si affianca, mai sovrascrive | Trascinare il blocco fuori dall'agenda lo rimuove (con undo) |
| 3 | Tocca un evento esterno | Dettaglio in sola lettura + "Apri nell'app Calendario" per modifiche complesse | 200ms | — | Evento cancellato esternamente nel frattempo → "non più disponibile" | Chiusura del foglio |

## FLOW-NOTE-01 · Scrivere una nota e collegarla a un obiettivo (NOTE-001…003)

| # | Azione utente | Risposta app | Tempo | Feedback | Edge case | Annullo |
|---|---|---|---|---|---|---|
| 1 | Apre Note, tocca "+" | Editor vuoto con focus immediato | ≤ 50ms | Cursore attivo | — | — |
| 2 | Scrive testo, digita "@" | Ricerca inline delle entità del grafo (GOAL, TASK…) | ≤ 100ms | Elenco a comparsa sopra la tastiera | Nessuna corrispondenza → "crea nuovo obiettivo" in fondo | Backspace rimuove il trigger |
| 3 | Seleziona l'obiettivo "Giappone" | Il collegamento appare come chip inline nel testo | ≤ 100ms | Aptica lieve | — | Tocco sul chip → rimuovi collegamento |
| 4 | Chiude la nota (swipe giù) | Salvataggio automatico già avvenuto durante la digitazione | — | — | Nota vuota (solo titolo) → si salva comunque, appare negli stati vuoti come bozza | — |

## FLOW-HLTH-01 · Auto-completamento di un'abitudine da un allenamento rilevato (HLTH-003)

| # | Azione utente | Risposta app | Tempo | Feedback | Edge case | Annullo |
|---|---|---|---|---|---|---|
| 1 | (passivo) Termina una corsa rilevata dal watch | La piattaforma OS registra l'allenamento | — | — | — | — |
| 2 | Apre l'app OmniLife | Notifica in-app discreta: "Corsa rilevata: segnare 'Correre' come fatta?" (se policy = chiedi) | — | Card temporanea in Home, non un blocco | Policy = automatico → l'abitudine risulta già spuntata, con nota "da Salute" | Tocco su "annulla" de-spunta |
| 3 | Conferma | L'abitudine si spunta, stessa animazione di FLOW-HAB-01 | ≤ 300ms | Aptica lieve | Spunta manuale già presente → nessun doppio conteggio (idempotenza) | — |

## FLOW-GOAL-01 · Creare un obiettivo trasversale e collegare contributi (GOAL-001…003)

| # | Azione utente | Risposta app | Tempo | Feedback | Edge case | Annullo |
|---|---|---|---|---|---|---|
| 1 | Da Obiettivi, tocca "+" | Foglio creazione: titolo + data target opzionale | ≤ 50ms | — | — | — |
| 2 | Conferma "Vacanza in Giappone" | Obiettivo creato, vuoto di contributi, con invito a collegare | 200ms | Stato vuoto didattico (§6 MUC) | — | Snackbar 7s |
| 3 | Tocca "Collega" → sceglie "Obiettivo di risparmio" | Crea/collega un FIN-008 esistente o nuovo | 200ms | — | — | — |
| 4 | Ripete per una lista task e un'abitudine | Il dettaglio obiettivo mostra 3 fronti con progresso nativo ciascuno | — | Aggiornamento reattivo a ogni evento dei moduli collegati | Un fronte in modulo disattivato → "in pausa", escluso dal calcolo | Scollega da menu contestuale del fronte |

## FLOW-SRCH-01 · Ricerca globale (SRCH-001…005)

| # | Azione utente | Risposta app | Tempo | Feedback | Edge case | Annullo |
|---|---|---|---|---|---|---|
| 1 | Tocca tab Cerca, digita "dentista" | Risultati incrementali da tutti i moduli attivi | ≤ 100ms (RNF-P5) | Evidenziazione del termine | Modulo sensibile bloccato → risultati offuscati con invito a sblocco | — |
| 2 | Tocca un chip filtro "Task" | Lista ristretta ai soli task | ≤ 100ms | Chip evidenziato | 0 risultati → stato vuoto con suggerimento di rimuovere filtri | Tocco di nuovo sul chip rimuove il filtro |
| 3 | Tocca un risultato | Apertura diretta del dettaglio (GEF) | 200ms | — | Entità cestinata → risultato mostrato con etichetta "nel cestino" + azione ripristina | Chiusura torna ai risultati con scroll preservato |

## FLOW-REV-01 · Revisione settimanale guidata (REV-001…004)

| # | Azione utente | Risposta app | Tempo | Feedback | Edge case | Uscita |
|---|---|---|---|---|---|---|
| 1 | Tocca il banner "Revisione settimanale pronta" | Modal a schermo intero, prima card: task scaduti | 200ms | Contatore "1 di 9" | 0 elementi da rivedere → schermata di congratulazioni breve, chiusura immediata | X in alto chiude, stato salvato |
| 2 | Su ogni card, sceglie un'azione (Pianifica/Rimanda/Archivia) | Azione eseguita, transizione automatica alla card successiva | ≤ 300ms per transizione | Aptica lieve a ogni decisione | Indeciso → può saltare la card (torna in fondo alla coda) | — |
| 3 | Raggiunge l'ultima card (budget/settimana entrante) | Riepilogo finale: cosa è stato deciso | — | Nessun punteggio, nessun giudizio | — | Tocco "Fine" → torna a Home |
| 4 | Chiude a metà (card 4 di 9) | Stato salvato | — | — | Riapertura entro la settimana → riprende da card 5 | — |

## FLOW-SYNC-01 · Cambio dispositivo e ripristino (BKP-003, SYNC-001/003)

| # | Azione utente | Risposta app | Tempo | Feedback | Edge case | Errori |
|---|---|---|---|---|---|---|
| 1 | Installa l'app su un nuovo telefono, effettua login | Verifica identità (password + eventuale 2FA) | — | — | Credenziali errate → messaggio chiaro, backoff dopo tentativi ripetuti | Vedi [10-error-experience](10-error-experience.md) |
| 2 | Conferma chiave di recupero (o biometria da device già fidato) | Inizia il ripristino: dati "caldi" (30gg) prima, resto in background | ≤ 2 min per i dati caldi (BKP-003) | Barra di progresso con stima | Interruzione di rete → ripristino riprendibile, non riparte da zero | Chiave errata 5 volte → backoff progressivo, mai lock-out permanente del legittimo |
| 3 | Usa l'app durante il completamento in background | Tutto ciò che è già arrivato è pienamente utilizzabile | — | Indicatore discreto "sincronizzazione in corso" nel pannello stato, mai bloccante | — | — |
| 4 | Il ripristino completa | Notifica silenziosa (nessun'interruzione), verifica di integrità positiva | — | — | Verifica fallita (raro) → segnalazione chiara con azione di supporto | — |

## FLOW-SET-01 · Disdire l'abbonamento (SET-003)

| # | Azione utente | Risposta app | Tempo | Feedback | Edge case | Uscita |
|---|---|---|---|---|---|---|
| 1 | Profilo → Abbonamento → "Disdici" | Una sola schermata informativa onesta (cosa perde, quando) | 200ms | — | — | Back annulla senza conseguenze |
| 2 | Conferma | Reindirizzamento al meccanismo nativo dello store (obbligo piattaforma) | — | — | — | — |
| 3 | Torna all'app | Stato "Plus fino al [data]" visibile in Abbonamento, nessun ulteriore contatto di trattenimento (C-art. 163) | — | — | — | — |

---

*Prossimo: [Task Flows — ciclo di vita delle entità](06-task-flows-entita.md)*
