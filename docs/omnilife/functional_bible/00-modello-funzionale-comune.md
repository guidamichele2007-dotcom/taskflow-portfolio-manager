# 00 · Modello Funzionale Comune (MFC)

> **Il documento più importante della Functional Bible.** Definisce una sola volta i comportamenti, gli stati, le regole e i casi limite che valgono per **ogni funzione di ogni modulo**. I documenti di modulo specificano solo ciò che è specifico o in deroga. Regola di lettura: **ciò che un modulo non specifica, si comporta come descritto qui; le deroghe sono sempre esplicite e motivate.** Questo elimina l'ambiguità senza duplicare le regole (P31 — nessuna duplicazione).

## 1. Convenzioni

### 1.1 Identificativi

- Funzioni: `PREFISSO-nnn` (es. `TASK-001`). Prefissi: HOME, ONB, CAPT, TASK, FIN, HAB, CAL, NOTE, HLTH, GOAL, SRCH, NTF, WID, SYNC, BKP, EXP, SET, PROF, SEC, GAL, REV, INS.
- Regole di business: `PREFISSO-R-nn` · Eventi: `evento.in.minuscolo` · Criteri di accettazione: `PREFISSO-AC-nn` · Regole comuni: `MFC-R-nn` · Edge case comuni: `MFC-E-nn`.
- Gli ID sono **stabili per sempre**: una funzione rimossa non libera il suo ID.

### 1.2 Tracciabilità (obbligatoria)

Ogni funzione cita: principi (`P1…P110`, [Product Principles](../product_bible/08-product-principles.md)), job (`J1…J20`, [JTBD](../product_bible/07-jobs-to-be-done.md)), decisioni (`D-01…`, [Decision Log](../product_bible/14-decision-log.md)), articoli della Constitution (`C-art. n`) quando vincolanti. Una funzione senza tracciatura non è valida (P104, P109).

### 1.3 Priorità e profondità di specifica

- **M (Must)** = MVP · **S (Should)** = v1.x · **C (Could)** = v2.x+ — coerenti con D-03 e la roadmap.
- Le funzioni **M** hanno scheda estesa (requisiti, vincoli, casi limite, criteri dedicati). Le **S/C** hanno scheda sintetica ed ereditano integralmente il MFC; la loro scheda estesa viene completata nel ciclo in cui entrano in sviluppo (regola di processo: **nessuna funzione entra in sviluppo senza scheda estesa approvata**).

## 2. Il ciclo di vita universale delle entità

Ogni entità utente (task, spesa, abitudine, nota, evento locale, obiettivo, collegamento) segue questo ciclo. I moduli definiscono i *campi*; il ciclo è identico ovunque (P33 — un'anatomia sola).

```
   creazione ──► attiva ◄──► modificata
                   │  ▲
        archivia   │  │ ripristina
                   ▼  │
                archiviata
                   │
        elimina    ▼            svuota/30gg
                cestinata ────────────────► eliminata definitivamente
                   ▲   │ ripristina
                   └───┘
```

### 2.1 Creazione (MFC-R-01…04)

- **MFC-R-01**: ogni creazione scrive prima in locale, in transazione; la conferma UI arriva solo a persistenza avvenuta (≤ 50 ms percepiti). *Motivo: P1, C-art. 3 — mai perdere un dato confermato.*
- **MFC-R-02**: ogni entità nasce con: ID univoco generabile offline, timestamp UTC di creazione, dispositivo di origine, versione di schema del modulo. *Motivo: sync senza server-arbitro (D-02), audit.*
- **MFC-R-03**: i campi obbligatori sono il minimo assoluto (di norma: solo il titolo/importo); tutto il resto ha default intelligenti modificabili. *Motivo: P15, P17.*
- **MFC-R-04**: creazione sempre possibile offline, da app, widget, voce e condivisione di sistema. *Motivo: C-art. 120, J1/J3.*

### 2.2 Modifica (MFC-R-05…08)

- **MFC-R-05**: ogni campo modificabile dall'utente lo è sempre, salvo deroga esplicita del modulo (es. importi di transazioni riconciliate future). Nessun "lucchetto" arbitrario. *Motivo: i dati sono dell'utente (C-art. 1).*
- **MFC-R-06**: le modifiche si salvano automaticamente (niente pulsante "salva" per i campi singoli; i form multi-campo confermano esplicitamente). *Motivo: P13; il salvataggio manuale è un residuo del desktop.*
- **MFC-R-07**: ogni modifica genera una voce di cronologia locale (campo, valore precedente, timestamp, dispositivo) consultabile dall'entità; profondità minima: 90 giorni o 50 revisioni. *Motivo: fiducia e undo profondo; C-art. 13.*
- **MFC-R-08**: modifiche concorrenti da più dispositivi convergono automaticamente per-campo; in caso di conflitto semantico non risolvibile (stesso campo, stesso intervallo), vince la modifica più recente e **la versione perdente resta in cronologia** — mai dialoghi di conflitto. *Motivo: D-02/ADR-3, C-art. 121.*

### 2.3 Archiviazione, eliminazione, ripristino (MFC-R-09…13)

- **MFC-R-09**: *archiviare* nasconde dalle viste attive ma conserva tutto (ricerca la trova con filtro); *eliminare* sposta nel cestino; *eliminare definitivamente* distrugge. Tre azioni distinte, mai confuse nel linguaggio. *Motivo: P30 — chiarezza; C-art. 13–14.*
- **MFC-R-10**: il cestino conserva 30 giorni, poi svuota automaticamente; il ripristino ricolloca l'entità con tutti i collegamenti. *Motivo: C-art. 13.*
- **MFC-R-11**: l'eliminazione è a 1 gesto + undo immediato (snackbar 7 s), mai dialogo di conferma preventiva; l'eliminazione **definitiva** richiede conferma forte. *Motivo: P2.*
- **MFC-R-12**: eliminare un'entità **non elimina mai** le entità collegate: i collegamenti si sospendono e restano visibili come "riferimento a elemento eliminato" finché nel cestino. *Motivo: principio 80/81 — nessuna cascata distruttiva implicita.*
- **MFC-R-13**: disattivare un modulo non tocca i suoi dati (C-art. 183): le entità restano nel grafo, invisibili, e riappaiono identiche alla riattivazione.

### 2.4 Cronologia e versionamento

Ogni entità espone: "creato il / da", "ultima modifica il / da", cronologia per campo (MFC-R-07). Il ripristino di una versione precedente è una *nuova modifica* (la cronologia non si riscrive mai). Le note (contenuto lungo) hanno versioni complete, non per-campo (deroga dichiarata in NOTE).

## 3. Comportamento offline / online / sync (universale)

| Aspetto | Comportamento normativo |
|---|---|
| **Offline** | Tutto funziona: creare, modificare, eliminare, cercare, consultare, configurare. Nessuna funzione utente richiede rete, salvo le 4 eccezioni dichiarate: registrazione account, ripristino da cloud, acquisto abbonamento, attivazione modulo on-demand non ancora scaricato. *(C-art. 16, 120, 123)* |
| **Coda** | Le modifiche offline si accodano localmente (outbox persistente, sopravvive al kill); nessun limite pratico di durata offline (settimane) |
| **Ritorno online** | Sync automatica in background, senza notifica se tutto converge (il silenzio è successo). Priorità: dati di oggi/settimana → resto |
| **Stato visibile** | Un indicatore discreto (mai modale, mai bloccante) mostra "in attesa di sincronizzazione" a livello app; le singole entità non mostrano badge di sync salvo errore persistente. *(P6 — non mentire; P37 — non affollare)* |
| **Errore di sync** | Retry con backoff automatico; dopo 72 h di fallimenti persistenti, notifica locale informativa con azione "dettagli". Mai perdita: l'outbox non si svuota mai senza conferma del server |
| **Multi-device** | Convergenza automatica per-campo (MFC-R-08); l'ordine di arrivo non altera il risultato finale (proprietà verificata dai test del motore) |
| **Rete a consumo** | Sync ridotta ai delta essenziali; backup pesanti solo su Wi-Fi salvo consenso |

## 4. Catalogo universale degli stati

Ogni schermata/funzione deve definire il proprio comportamento in **tutti** questi stati. Il MFC fissa il default; i moduli specificano solo il contenuto.

| Stato | Comportamento default |
|---|---|
| **Vuoto (mai usato)** | Stato didattico: un esempio concreto + una azione primaria (P34/C-art. 103). Mai una pagina bianca |
| **Nessun dato (filtrato)** | Distinto dal vuoto: "nessun risultato per questi filtri" + azzeramento filtri a 1 tocco |
| **Caricamento** | Skeleton (mai spinner a schermo pieno); se > 300 ms c'è un problema di progetto (P23). Dati locali: il caricamento è di norma impercettibile |
| **Errore** | Messaggio umano: cosa, perché, azione riparatrice (C-art. 104). Mai codici nudi. L'errore di una sezione non rompe la schermata (C-art. 122) |
| **Offline** | Identico a online per i dati locali; le sole funzioni-eccezione (§3) mostrano lo stato "richiede connessione" con spiegazione |
| **Sincronizzazione** | Silenziosa; indicatore discreto solo se l'utente apre il pannello stato |
| **Conflitto** | Non esiste come stato visibile all'utente (risolto automaticamente, MFC-R-08); esiste solo in cronologia |
| **Free (al limite)** | Il limite si comunica *prima* di raggiungerlo (es. all'attivazione del 2° modulo: "questo è l'ultimo incluso nel piano Free"), mai come sorpresa punitiva. Contenuti oltre il limite: **mai bloccati in lettura/export** (C-art. 17, 159–160) — solo la *creazione* oltre soglia richiede Plus |
| **Trial** | Identico a Plus + banner discreto con giorni residui in Impostazioni (non nelle schermate d'uso); promemoria 3 giorni prima della fine (C-art. 164) |
| **Premium (Plus)** | Nessun badge celebrativo permanente; le funzioni ci sono e basta |
| **Business/B2B2C** *(futuro)* | Identico a Plus; nessuna differenza visibile né dati visibili al datore (C-art. 174) |
| **Archiviato** | Visivamente attenuato, escluso dalle viste attive e dai conteggi, incluso in ricerca con filtro |
| **Cestinato** | Visibile solo nel cestino, con giorni residui e ripristino a 1 tocco |
| **Condiviso** *(futuro, fase famiglia)* | Indicatore di condivisione + con chi; le regole nel modulo futuro dedicato |
| **Sola lettura** | Per entità da fonti esterne (eventi di calendari altrui, dati Salute di sistema): modificabile solo alla fonte, con azione "apri nella fonte" |
| **Degradato** | Se un sottosistema fallisce (es. indice di ricerca corrotto), la funzione si degrada con messaggio e auto-riparazione in background — mai crash a cascata |

## 5. Autorizzazioni e sicurezza funzionale

- **MFC-R-20**: un solo utente per account; ogni dato è privato per default. Le future condivisioni sono opt-in granulari (C-art. 49).
- **MFC-R-21**: lo sblocco biometrico (se attivo) protegge l'apertura dell'app e, opzionalmente, i moduli marcati sensibili dall'utente (default suggerito: Finanze, Note, Salute). Fallback: codice app. Timeout configurabile: immediato/1/5/15 min.
- **MFC-R-22**: con app bloccata: i widget mostrano solo dati che l'utente ha esplicitamente consentito in chiaro (default: titoli sì per Attività/Abitudini, importi NO per Finanze — configurabile); le notifiche seguono la stessa regola (C-art. 7).
- **MFC-R-23**: i permessi di sistema si chiedono contestualmente (C-art. 32–33); ogni modulo elenca i propri nella scheda modulo, con comportamento a permesso negato definito.
- **MFC-R-24**: le azioni distruttive di account (eliminazione account, svuota tutto) richiedono ri-autenticazione forte + attesa di sicurezza (72 h con possibilità di annullamento via email). *Motivo: protezione da coercizione/accesso momentaneo.*

## 6. Edge case universali (validi per ogni funzione)

| ID | Caso | Comportamento normativo |
|----|------|-------------------------|
| MFC-E-01 | **Doppio tocco / tocco ripetuto** | Ogni azione è idempotente a livello UI: il secondo tocco entro il debounce non crea duplicati né doppie navigazioni |
| MFC-E-02 | **Kill del processo / crash a metà operazione** | Le scritture sono transazionali: o complete o assenti; alla riapertura lo stato è coerente e i draft (testi in composizione) sono recuperati |
| MFC-E-03 | **Spazio disco esaurito** | Le scritture falliscono con messaggio chiaro e azione ("libera spazio"); mai corruzione; i dati esistenti restano leggibili; la sync in ingresso si sospende |
| MFC-E-04 | **Batteria quasi scarica / risparmio energetico** | Sync e lavori in background si sospendono (riprendono poi); le funzioni interattive restano complete |
| MFC-E-05 | **Memoria insufficiente (device datato)** | Le liste sono virtualizzate; il sistema può ricostruire cache/indici senza perdita; budget RNF-P6/P9 |
| MFC-E-06 | **Perdita di connessione a metà operazione** | Nessuna operazione utente fallisce per rete: tutto è locale-prima; le 4 eccezioni online (§3) si interrompono in modo pulito e riprendibile |
| MFC-E-07 | **Cambio fuso orario / viaggio** | Le entità con orario mantengono l'orario *locale dell'intenzione* (una sveglia alle 7 resta alle 7 locali); gli eventi di calendario seguono il fuso dell'evento; la "giornata" (per Home/abitudini/budget) è quella del fuso corrente del device; i cambi di fuso non fanno mai sparire né duplicare elementi del giorno |
| MFC-E-08 | **Ora legale** | I giorni da 23/25 ore non rompono ricorrenze, streak resilienti, né budget; le ricorrenze alle 2:30 nei giorni di cambio scattano all'orario valido più vicino |
| MFC-E-09 | **Anno bisestile / date limite** | Ricorrenza del 29/2: nei non bisestili scatta il 28/2 (regola dichiarata all'utente alla creazione); ricorrenza del 31: ultimo giorno del mese. Nessun 30/2, mai |
| MFC-E-10 | **Orologio di sistema errato / che salta** | I timestamp usano l'orologio monotono dove possibile; se il clock arretra, la sync non genera paradossi (vettori di versione, non wall-clock); date palesemente assurde nei dati importati → segnalate, non corrette in silenzio |
| MFC-E-11 | **Cambio lingua** | Tutta la UI cambia; i contenuti utente restano intatti; i valori generati (categorie predefinite) mostrano il nome nella nuova lingua mantenendo l'identità |
| MFC-E-12 | **Cambio dispositivo / secondo dispositivo** | UC-08: ripristino completo; l'uso simultaneo su 2+ device converge (MFC-R-08); il registro dispositivi consente revoca remota |
| MFC-E-13 | **Dati duplicati dall'utente** | La duplicazione intenzionale è lecita (mai bloccata); il sistema può *segnalare* sospetti duplicati (stessa entità creata 2 volte in 5 min) offrendo l'unione, mai unendo da solo (C-art. 67) |
| MFC-E-14 | **Volumi estremi** | 100.000+ entità: nessun degrado percepibile oltre i budget (RNF-P5); testo di 100.000 caratteri in un campo: gestito con virgolettatura in liste |
| MFC-E-15 | **Migrazione interrotta** (aggiornamento app) | Le migrazioni di schema sono transazionali e riprendibili; backup automatico pre-migrazione; l'app non parte mai in stato ibrido |
| MFC-E-16 | **Font 200% / screen reader / riduzione movimento** | Non sono edge case ma stati di prima classe (C-art. 98–102): ogni funzione li supporta per Definition of Done |
| MFC-E-17 | **Input malevoli o estremi** | Emoji, RTL, caratteri di controllo, stringhe vuote, numeri al limite: accettati o rifiutati con messaggio chiaro; mai crash, mai injection nei campi di ricerca/filtri |
| MFC-E-18 | **Aggiornamento app mentre in uso** | Lo stato di navigazione e i draft sopravvivono all'aggiornamento |

## 7. Criteri di accettazione universali (si applicano a OGNI funzione)

Formato Dato/Quando/Allora, pensati per l'automazione futura. Ogni modulo aggiunge i propri; questi valgono sempre:

- **MFC-AC-01** — *Dato* il dispositivo in modalità aereo, *quando* l'utente esegue qualsiasi funzione di creazione/modifica/eliminazione/consultazione, *allora* l'esito è identico alla modalità online e la modifica risulta sincronizzata entro 60 s dal ritorno della rete.
- **MFC-AC-02** — *Dato* un'entità appena eliminata, *quando* l'utente tocca "Annulla" entro 7 s, *allora* l'entità è ripristinata identica, collegamenti inclusi.
- **MFC-AC-03** — *Dato* un processo terminato forzatamente durante una scrittura, *quando* l'app riparte, *allora* nessun dato confermato è perso e nessun dato parziale è visibile.
- **MFC-AC-04** — *Dato* lo stesso campo modificato su due dispositivi offline con valori diversi, *quando* entrambi tornano online, *allora* entrambi convergono allo stesso valore senza intervento e il valore perdente è in cronologia.
- **MFC-AC-05** — *Dato* font di sistema al 200% e screen reader attivo, *quando* l'utente percorre il flusso primario della funzione, *allora* ogni elemento è leggibile, etichettato e completabile.
- **MFC-AC-06** — *Dato* un utente Free oltre un limite di piano, *quando* accede a contenuti esistenti oltre soglia, *allora* lettura, modifica ed export restano disponibili; solo la creazione oltre soglia propone Plus (senza bloccare la navigazione).
- **MFC-AC-07** — *Dato* qualsiasi lista con 50.000 entità, *quando* l'utente scorre o cerca, *allora* nessun blocco percepibile e primi risultati ≤ 100 ms.
- **MFC-AC-08** — *Dato* un doppio tocco rapido su qualsiasi azione di creazione, *allora* viene creata una sola entità.

## 8. Modello degli eventi (bus interno)

- Ogni funzione dichiara nel suo documento: **eventi pubblicati**, **eventi sottoscritti**, **effetti su altri moduli**. Nessuna dipendenza implicita: se non è dichiarato qui, non esiste (C-art. 184).
- Convenzione: `modulo.entita.azione` (es. `fin.expense.created`). Payload minimo: ID entità, tipo, timestamp — mai contenuti completi (i consumer leggono dal grafo).
- I consumer devono tollerare l'assenza del producer (modulo disattivato): nessun errore, semplicemente nessun evento.
- Gli eventi sono **locali al dispositivo**; la sync trasporta stato, non eventi (gli eventi si rigenerano localmente dopo il merge quando rilevante, es. `goal.progress.changed`).

## 9. Struttura standard dei documenti di modulo

Ogni documento di modulo segue questo schema fisso: **1. Scopo e tracciabilità** → **2. Funzioni** (tabella + schede estese per le Must) → **3. Comportamenti specifici** (solo deroghe/aggiunte al MFC) → **4. Stati specifici** → **5. Regole di business** (numerate e motivate) → **6. Eventi** → **7. Edge case specifici** → **8. Criteri di accettazione**.

---

*Indice della Functional Bible: [README](README.md) · Matrici di tracciabilità: [17-matrici](17-matrici.md)*
