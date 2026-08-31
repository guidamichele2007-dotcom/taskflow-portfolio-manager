# 04 · User Flows — Core e MVP (funzioni Must)

> Eredita [MUC](00-modello-ux-comune.md). Flussi completi passo-passo per le funzioni **Must** (MVP, release 1.0 — vedi [Functional Bible §17](../functional_bible/17-matrici.md)), che rappresentano i percorsi primari del prodotto. Ogni passo: azione utente → risposta app → tempo atteso → feedback → animazione → edge case → errori → annullo → ritorno/uscita. Le funzioni Should/Could seguono lo stesso schema con dettaglio ridotto nei flussi estesi ([05](05-user-flows-estesi.md)) e nei task flow generici ([06](06-task-flows-entita.md)).

## FLOW-CAPT-01 · Cattura rapida universale (CAPT-001/004/005/008)

> Il flusso più importante del prodotto (J1, J2 — vedi [Functional Bible 02](../functional_bible/02-cattura-rapida.md)).

| # | Azione utente | Risposta app | Tempo | Feedback/Animazione | Edge case | Errori | Annullo/Ritorno |
|---|---|---|---|---|---|---|---|
| 1 | Tocca il FAB da qualsiasi schermata | Il foglio di cattura si espande dal punto del FAB (MUC §4, standard) | 200 ms | Sfondo sfocato, tastiera già attiva sul campo | FAB assente durante digitazione a schermo intero → non applicabile qui | — | Swipe giù chiude senza creare nulla |
| 2 | Digita "30 cena con Sara" | Il parser (CAPT-004) interpreta in tempo reale; appaiono chip: Spesa · 30€ · Ristoranti · Oggi | ≤ 100 ms dopo ogni pausa di battitura | I chip appaiono con fade-in leggero, uno alla volta se la frase si completa progressivamente | Parser incerto sul tipo → chip "Tipo?" evidenziato in ambra (mai rosso) | Testo non interpretabile → nessun errore mostrato, i chip restano vuoti, il testo grezzo resta valido per l'Inbox | Backspace su un chip lo rimuove tornando al testo libero |
| 3 | Tocca il chip "Ristoranti" per correggere | Selettore a comparsa con 3 categorie recenti + "altro" | ≤ 200 ms | Aptica lieve alla selezione | Categoria non esiste ancora → opzione "crea categoria" in fondo | — | Tocco fuori chiude il selettore senza modificare |
| 4 | Tocca "Salva" (o invio) | L'entità è creata e persistita localmente; il foglio si chiude con transizione di uscita | ≤ 50 ms percepiti | Aptica media di conferma; foglio scende (uscita, 150-200ms) | Doppio tocco rapido → una sola entità (MFC-E-01) | Salvataggio impossibile (disco pieno) → messaggio chiaro, il testo resta nel foglio (mai perso) | Snackbar "Annulla" 7s con apertura diretta dell'entità per rifinitura |
| 5 | (in background) | La transazione appare nella card Budget della Home entro 1s; se offline, resta identica, sync silenziosa al ritorno rete | — | — | Offline per settimane → nessun limite, coda persistente | — | — |

**Varianti brevi**: da widget (CAPT-002) → stesso flusso dal passo 2, 2 tocchi totali; da voce (CAPT-003) → dettatura sostituisce la digitazione, conferma vocale/aptica; da condivisione di sistema (CAPT-009) → il foglio si apre precompilato dal testo condiviso.

## FLOW-HOME-01 · Apertura app e vista Home "Oggi" (HOME-001…004)

| # | Azione utente | Risposta app | Tempo | Feedback | Edge case | Errori | Ritorno |
|---|---|---|---|---|---|---|---|
| 1 | Tocca l'icona dell'app (cold start) | Splash minimale → Home popolata | ≤ 1,5s totali (RNF-P1) | Skeleton delle card se oltre 300ms (raro: dati locali) | 0 moduli attivi → Home mostra solo cattura + invito Galleria | Crash al lancio (mai accettabile) → vedi [10-error-experience](10-error-experience.md) | — |
| 2 | Osserva le card (eventi, task, abitudini, budget) | Card ordinate secondo priorità utente o default (vincolato→volitivo) | — | — | Giorno senza impegni → card "libero" positiva, non vuota triste | — | — |
| 3 | Tocca "completa" su un task dalla card | Il task si marca completato inline, aptica media | ≤ 50 ms | Riga attenuata con segno di spunta, resta visibile fino a cambio vista | Task già completato da altro device (sync arrivata nel frattempo) → nessuna doppia animazione, stato già coerente | — | Snackbar "Annulla" 7s |
| 4 | Swipe orizzontale su una card | Transizione verso "Prossimi giorni" (IA-010) | 200-250ms | Scroll orizzontale con inerzia naturale | — | — | Swipe opposto per tornare |
| 5 | Pull verso il basso | **Nessuna azione** (HOME-007: pull-to-refresh assente per design) | — | Eventuale resistenza elastica di sistema, poi torna a posto senza azione | — | — | — |

## FLOW-ONB-01 · Onboarding (ONB-001…007)

| # | Azione utente | Risposta app | Tempo | Feedback | Edge case | Errori | Uscita |
|---|---|---|---|---|---|---|---|
| 1 | Apre l'app la prima volta | Schermata di benvenuto: 1 frase + "Inizia" | Istantaneo | Illustrazione statica sobria | — | — | Non c'è uscita: è il primo passo obbligato ma brevissimo |
| 2 | Tocca "Inizia" | Griglia moduli (Attività preselezionato) | 200ms transizione | Chip di selezione con stato "selezionato" evidente | — | — | — |
| 3 | Tocca "Continua" (con ≥1 modulo scelto) | Schermata "Scrivi la prima cosa da fare" | 200ms | Campo di testo con focus automatico e tastiera già aperta | 0 moduli selezionati → "Continua" disabilitato con microcopy "scegline almeno uno" | — | — |
| 4 | Digita e conferma la prima cattura | Entità creata; transizione diretta alla Home popolata | ≤ 50ms percepiti | Aptica media, breve conferma visiva ("Fatto!" sobrio) | Utente salta la cattura (se permesso) → Home comunque raggiunta, stato vuoto didattico | — | — |
| 5 | (successivo, contestuale) | Al primo elemento con orario, viene chiesto il permesso notifiche con spiegazione del beneficio | — | Dialogo di sistema preceduto da una schermata "pre-permesso" nostra che spiega perché | Permesso negato → l'app continua identica, il promemoria specifico resta silenzioso con segnalazione in Impostazioni | — | — |

**Tempo totale atteso**: mediana ≤ 60 secondi (ONB-AC-01). Nessun account, nessun pagamento, nessun permesso è richiesto in questo flusso (ONB-R-01).

## FLOW-TASK-01 · Creare, completare, posticipare un task (TASK-001…009)

| # | Azione utente | Risposta app | Tempo | Feedback | Edge case | Errori | Annullo |
|---|---|---|---|---|---|---|---|
| 1 | Da Vista Oggi (Attività), tocca "+" in fondo alla lista | Nuova riga inserita in cima con focus sul titolo | ≤ 50ms | Cursore lampeggiante immediato | — | — | Riga vuota abbandonata (nessun testo) → non si crea nulla |
| 2 | Digita titolo, tocca invio | Task creato, riga si "assesta" nella lista secondo l'ordinamento di default | 100ms | Aptica lieve | — | — | Tocco sulla riga per aprire dettaglio e aggiungere data/priorità |
| 3 | Apre il dettaglio, imposta ricorrenza "ogni lunedì" | Campo ricorrenza mostra riepilogo leggibile ("Si ripete ogni lunedì") | Immediato | — | 31/29 febbraio → regola dichiarata inline (MFC-E-09) | — | Cronologia registra il cambio |
| 4 | Torna alla lista, tocca lo spunta sul task | Task completato, riga attenuata e barrata, resta visibile fino a chiusura vista | ≤ 50ms | Aptica media | Task ricorrente → la prossima occorrenza appare al prossimo caricamento della lista, non istantaneamente sovrapposta | — | Snackbar "Annulla" 7s ripristina esattamente (incluso lo stato "non generata" della prossima occorrenza) |
| 5 | Su un task scaduto, swipe verso destra | Menu rapido: "Oggi / Domani / Weekend / Scegli" | 150ms | — | — | — | Tocco fuori annulla il menu |

## FLOW-FIN-01 · Registrare una spesa e vedere l'impatto sul budget (FIN-001, 005, 014)

| # | Azione utente | Risposta app | Tempo | Feedback | Edge case | Errori | Annullo |
|---|---|---|---|---|---|---|---|
| 1 | Cattura "82€ supermercato" (FLOW-CAPT-01) | Transazione creata con categoria proposta e conto di default | ≤ 3s totali | Chip categoria/conto modificabili prima del salvataggio | — | — | Snackbar 7s |
| 2 | (automatico) Il budget "Alimentari" ricalcola | Se supera l'80%, la card Home passa da verde ad ambra | ≤ 1s | Transizione di colore morbida (mai un lampeggio d'allarme) | Budget non ancora creato → nessun avviso, solo il totale speso | — | — |
| 3 | Tocca la card ambra | Apre Panoramica Finanze con il budget in evidenza | 200ms | — | — | — | Back torna a Home |
| 4 | Tocca "sposta 20€ da Svago" (proposta concreta) | Anteprima del ribilanciamento, conferma a 1 tocco | ≤ 200ms | — | Categoria "Svago" senza margine → proposta alternativa o nessuna proposta (mai forzata) | — | Annullabile subito dopo (snackbar) |

## FLOW-HAB-01 · Spuntare un'abitudine e vivere un salto (HAB-001…008)

| # | Azione utente | Risposta app | Tempo | Feedback | Edge case | Errori | Annullo |
|---|---|---|---|---|---|---|---|
| 1 | Dal widget o dalla Home, tocca l'abitudine "Camminata" | Spuntata, anello di progresso si riempie | ≤ 50ms | Aptica lieve, animazione di riempimento 150ms | Doppio tocco → un solo conteggio (HAB-R-03) | — | Snackbar 7s / secondo tocco per de-spuntare |
| 2 | (giorno successivo) Non spunta l'abitudine | Nessuna notifica di colpa; la fascia di aderenza scende al massimo di un livello | — | — | — | — | — |
| 3 | Apre il dettaglio abitudine dopo il salto | Vede la fascia "in ripresa" con linguaggio gentile, mai "streak persa" | — | Colore neutro/informativo, mai rosso | 2 settimane sotto il 50% → proposta di ridimensionamento (HAB-007) | — | Rifiuto della proposta senza conseguenze, non riproposta per 2 settimane |
| 4 | Spunta retroattivamente "ieri" dalla griglia storica | Il giorno si marca, la fascia si ricalcola | ≤ 300ms | Aptica lieve | Oltre 7 giorni indietro → richiede conferma dalla vista storica estesa | — | Tocco di nuovo per rimuovere |

## FLOW-GAL-01 · Attivare un nuovo modulo (GAL-001/002)

| # | Azione utente | Risposta app | Tempo | Feedback | Edge case | Errori | Uscita |
|---|---|---|---|---|---|---|---|
| 1 | Da Moduli, tocca "Galleria" | Catalogo con schede illustrate | 200ms | — | — | — | Back |
| 2 | Tocca la scheda "Finanze" | Dettaglio con anteprima interattiva (dati d'esempio), permessi dichiarati | 200ms | — | — | — | Back |
| 3 | Tocca "Attiva" | Se Free con già 2 moduli attivi: proposta Plus chiara (contenuto/prezzo/disdetta); altrimenti attivazione immediata | ≤ 300ms | Aptica media alla conferma | Free al limite → dialogo NON bloccante, l'utente può rifiutare e restare operativo | — | Rifiuto → torna alla scheda, nessuna penalità |
| 4 | (se attivato) Micro-onboarding di modulo (≤ 3 schermate) | Configurazione minima (es. primo conto) | — | — | — | — | Skip sempre disponibile con default applicati |
| 5 | Completa onboarding di modulo | Il modulo appare in Home e nella tab Moduli | Immediato | Aptica media | — | — | — |

---

*Prossimo: [User Flows estesi (Should/Could)](05-user-flows-estesi.md)*
