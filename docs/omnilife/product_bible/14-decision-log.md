# 14 · Decision Log

> **Una decisione non registrata non esiste.** Questo registro è la memoria istituzionale del prodotto: impedisce di ridiscutere l'già-deciso, permette di rivedere con onestà ciò che si rivelasse sbagliato, e insegna ai nuovi arrivati *perché* le cose sono come sono.
>
> **Formato obbligatorio** per ogni voce: Problema · Alternative considerate · Decisione · Motivazione · Conseguenze accettate · Condizioni di revisione (che cosa dovrebbe accadere per riaprirla). Le decisioni si numerano D-01, D-02… e non si cancellano mai: si superano con una nuova voce che cita la vecchia.

---

## D-01 · Modularità come architettura di prodotto (non super-app, non monofunzione)

- **Problema**: come coprire più domini di vita senza diventare una super-app confusa né restare un'app di nicchia.
- **Alternative**: (a) super-app monolitica con tutte le funzioni; (b) suite di app separate con un account comune; (c) un'app monofunzione eccellente, poi espansione; (d) nucleo + moduli attivabili.
- **Decisione**: (d) nucleo + moduli attivabili singolarmente.
- **Motivazione**: (a) è l'anti-modello documentato (ClickUp — doc 05); (b) reintroduce la frammentazione che vogliamo eliminare e decuplica il costo di superficie; (c) rimanda la differenziazione (il grafo) a un futuro che il churn di categoria potrebbe non concederci; (d) dà semplicità percepita per-utente e ampiezza strategica insieme.
- **Conseguenze accettate**: complessità architetturale iniziale maggiore (contratti tra moduli); onboarding con una scelta in più (quale modulo).
- **Revisione se**: i dati d'uso mostrassero che > 80% degli utenti resta mono-modulo per sempre E la conversione non dipende dall'espansione.

## D-02 · Privacy end-to-end architetturale (il server non può leggere)

- **Problema**: quale livello di privacy per dati che includono denaro, salute e pensieri.
- **Alternative**: (a) crittografia standard at-rest/in-transit con accesso server (come quasi tutti); (b) E2E opzionale per "vault" selezionati; (c) E2E totale by design.
- **Decisione**: (c) E2E totale; il backend è cieco sui contenuti.
- **Motivazione**: (a) rende la promessa di privacy una policy revocabile — e il caso Mint dimostra dove porta l'incentivo; (b) crea due classi di dati e comunica che il default non è sicuro; (c) è l'unica promessa *verificabile* e non copiabile dagli incumbent ad-funded. È anche il vincolo creativo che ci mantiene onesti: le feature che richiederebbero di leggere i dati sono quasi sempre feature di sorveglianza travestite.
- **Conseguenze accettate**: niente feature server-side sui contenuti (ricerca server, ML cloud); recupero account impossibile senza chiave (mitigato, D-09); più complessità su sync e web futuro.
- **Revisione**: mai sul principio (Constitution art. 1–2); riaprire solo le *implementazioni*.

## D-03 · MVP: 3 moduli eccellenti, non 8 mediocri

- **Problema**: quanti moduli al lancio, dato che la visione ne prevede 8+.
- **Alternative**: (a) tutti i moduli subito, qualità media; (b) 1 modulo solo (entrare come specialista); (c) 3 moduli (Attività, Finanze, Abitudini) + calendario in lettura.
- **Decisione**: (c).
- **Motivazione**: (a) incarna il rischio R-01 ("tuttofare mediocre") e non è finanziabile in qualità; (b) rimanda la prova dell'integrazione — la nostra unica vera tesi — e ci fa competere sul terreno degli specialisti dove non abbiamo (ancora) diritto di vincere; (c) i tre moduli scelti sono i tre a maggiore frequenza d'uso quotidiana + willingness-to-pay, e bastano a dimostrare il grafo (spesa→budget→obiettivo, abitudine→giorno).
- **Conseguenze accettate**: gli utenti di note/salute aspetteranno (gestione aspettative in comunicazione); la feature-firma (obiettivi trasversali) matura solo in v1.x.
- **Revisione se**: la beta mostrasse che l'assenza di un modulo specifico è il blocco n.1 all'adozione.

## D-04 · Il tempo in app è un costo: North Star = WAI, non engagement

- **Problema**: quale metrica di successo primaria per un prodotto la cui categoria misura engagement.
- **Alternative**: (a) DAU/tempo in app (standard di settore); (b) sessioni e streak; (c) Weekly Active Integrators (azioni di valore × multi-modulo) con guardia sul tempo per sessione.
- **Decisione**: (c), con anti-metriche esplicite (doc 10 §4).
- **Motivazione**: le metriche (a) e (b) premiano la dipendenza e ci trasformerebbero — incentivo dopo incentivo — nell'opposto del Manifesto. La (c) cresce solo se l'utente riceve valore integrato: è la tesi del prodotto resa numero.
- **Conseguenze accettate**: numeri "peggiori" nei confronti superficiali con i competitor (il nostro DAU-minuti sarà più basso: bene così); più difficile spiegarla a investitori abituati all'engagement — la spieghiamo.
- **Revisione se**: la WAI si rivelasse non correlata a retention/conversione nei dati reali (allora è la definizione da raffinare, non la filosofia).

## D-05 · Freemium per capacità (2 moduli / 1 device), mai per sicurezza

- **Problema**: dove tracciare la linea free/paid.
- **Alternative**: (a) trial-only (tutto a pagamento dopo X giorni); (b) freemium per feature (funzioni chiave nel premium); (c) freemium per capacità (moduli/device/storico) con fondamenta gratis per sempre; (d) tutto gratis + ads.
- **Decisione**: (c). Backup, export, crittografia, offline: mai a pagamento.
- **Motivazione**: (a) uccide il passaparola e il vivaio; (b) genera il risentimento documentato (Productive, doc 05) e paywall-ansia; (d) vietata dalla Constitution e strategicamente suicida (il cliente diventerebbe l'inserzionista). La (c) allinea il pagamento al valore d'espansione: paga chi ha già capito il prodotto.
- **Conseguenze accettate**: conversione più lenta di un hard paywall; free rider di lungo periodo (benvenuti: sono distribuzione).
- **Revisione se**: free→paid < 2% a regime con retention alta (allora la linea è tracciata troppo generosamente: si sposta la capacità, mai le fondamenta).

## D-06 · Costanza resiliente al posto delle streak

- **Problema**: quale meccanica di costanza per il modulo Abitudini.
- **Alternative**: (a) streak classiche (validate commercialmente: Duolingo, Streaks); (b) nessuna metrica di costanza (solo registro); (c) costanza resiliente (media mobile, frequenze flessibili, ridimensionamento gentile).
- **Decisione**: (c).
- **Motivazione**: le streak (a) massimizzano l'engagement a breve *e* l'abbandono al primo inciampo (P4, doc 03) — comprano DAU oggi vendendo l'utente di domani; inoltre violano il principio 40 e l'anti-metrica di dipendenza. La (b) rinuncia al supporto motivazionale legittimo. La (c) è la scommessa che la gentilezza trattiene più della paura: è anche la nostra differenziazione emotiva ("l'app che non ti giudica").
- **Conseguenze accettate**: perdiamo il "brivido della streak" che una parte di utenti dichiara di volere; la meccanica resiliente è più difficile da comunicare in uno screenshot.
- **Revisione se**: i test mostrassero retention *a 90 giorni* peggiore della coorte con meccaniche classiche (misurare a 90, non a 7: è lì che la tesi si decide).

## D-07 · Niente open banking nell'MVP

- **Problema**: collegare i conti bancari (sync automatica delle transazioni) da subito?
- **Alternative**: (a) sì, via aggregatori PSD2; (b) no: inserimento assistito (NLP, ricorrenze, chip) e rivalutazione post-PMF.
- **Decisione**: (b).
- **Motivazione**: il bank-sync è il sogno dichiarato degli utenti ma anche la fonte n.1 di recensioni negative dei competitor EU (connessioni che saltano — doc 05: Spendee, Wallet); costo regolatorio e di affidabilità sproporzionato per un MVP; e le transazioni automatiche *non categorizzate dall'utente* producono budget ignorati (il gesto manuale di 2 tocchi ha un valore comportamentale: consapevolezza). L'80% del valore (ricorrenze + cattura 3 s) si ottiene senza.
- **Conseguenze accettate**: una quota di utenti finance-first non adotterà finché non c'è; i competitor lo useranno nei confronti.
- **Revisione**: a PMF confermato (Anno 2–3), come *opzione* — mai come default che sostituisce la consapevolezza.

## D-08 · Doppio nativo (iOS+Android) con core condiviso, non cross-platform UI

- **Problema**: stack per qualità "top store" su due piattaforme con team startup.
- **Alternative**: (a) Flutter/RN (una codebase); (b) nativo doppio puro; (c) nativo UI + core logico condiviso (KMP); (d) iOS-only first.
- **Decisione**: (c); con (d) come piano B dichiarato se il vincolo economico lo impone.
- **Motivazione**: la qualità premium percepita (widget, aptica, accessibilità di sistema, health, 120 fps) è il nostro standard non negoziabile e su queste superfici il nativo resta superiore; il core condiviso concentra la scrittura-una-volta esattamente dove i bug costano di più (sync, crypto). La (a) resta eccellente per altri prodotti; non per il nostro standard dichiarato.
- **Conseguenze accettate**: costo team maggiore; rischio di divergenza tra piattaforme (mitigato: design token unici, review di parità).
- **Revisione se**: la velocity provasse che la parità di piattaforma non regge (→ attivare piano B, non degradare entrambe).

## D-09 · Recovery Key obbligatoria; nessun recupero amministrativo

- **Problema**: E2E significa che perdere le credenziali può significare perdere i dati.
- **Alternative**: (a) backdoor amministrativa "per emergenze"; (b) custodia opzionale della chiave presso di noi; (c) Recovery Key utente obbligatoria + opzione keychain di sistema, e il trade-off dichiarato apertamente.
- **Decisione**: (c). La (a) non è un'alternativa: è l'annullamento di D-02.
- **Motivazione**: una backdoor "per il bene dell'utente" è una backdoor per chiunque la ottenga (coercizione, breach, insider). La (b) ricrea il single point of trust che l'architettura elimina. Il costo umano della (c) — utenti che perdono tutto — è reale: lo mitighiamo con UX (verifica del salvataggio, promemoria, keychain) e lo accettiamo come il prezzo della promessa.
- **Conseguenze accettate**: ticket dolorosi di supporto; recensioni negative occasionali ("ho perso tutto") — risposta pubblica preparata e onesta.
- **Revisione**: mai sul principio; sempre sulle mitigazioni UX.

## D-10 · Lancio Europa-first (Italia come laboratorio)

- **Problema**: dove lanciare.
- **Alternative**: (a) USA-first (mercato più grande); (b) globale day-one; (c) Italia soft → EU → EN.
- **Decisione**: (c).
- **Motivazione**: (a) è il mercato più affollato e caro, da affrontare con posizionamento già affilato; (b) diluisce l'apprendimento; (c) sfrutta il vantaggio linguistico/culturale del team, un mercato-laboratorio a costo d'errore basso, e il vento regolatorio e culturale europeo sulla privacy (T2) dove il nostro differenziatore pesa di più.
- **Conseguenze accettate**: numeri assoluti del primo semestre più piccoli; necessità di eccellenza EN già dall'Anno 1 per non restare percepiti "locali".
- **Revisione se**: il soft launch italiano desse segnali non generalizzabili (culturalmente anomali) → anticipare un secondo mercato di verifica.

## D-11 · La Product Bible come fonte normativa (questo stesso documento)

- **Problema**: come mantenere coerenza di prodotto per anni, con team che cresce e pressioni che cambiano.
- **Alternative**: (a) cultura orale + leadership carismatica; (b) processi e approvazioni gerarchiche; (c) documentazione normativa (Bible + Constitution) con processi di emendamento espliciti.
- **Decisione**: (c).
- **Motivazione**: la (a) non scala e muore col fondatore assente; la (b) scala la burocrazia, non la coerenza; la (c) rende la coerenza *citabile* ("viola il 39") e il dissenso produttivo (si emenda il documento, non si logora la persona). È il modello delle culture di prodotto più durature.
- **Conseguenze accettate**: costo di manutenzione documentale; rischio di dogmatismo (mitigato: ogni regola ha il suo processo di revisione).
- **Revisione**: il processo di emendamento è dentro la Constitution stessa (Titolo X).

---

## Registro aperto

Le prossime decisioni si aggiungono qui con numerazione progressiva. Candidate già note: D-12 naming e brand finale · D-13 fornitore cloud e region strategy · D-14 struttura societaria e sede (rilevante per la giurisdizione privacy) · D-15 politica di trasparenza dei report annuali.

---

*Prossimo: [Product Constitution](15-product-constitution.md) — le regole che nessuna decisione futura può violare.*
