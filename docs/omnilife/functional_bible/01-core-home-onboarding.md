# 01 · Core — Home "Oggi", Onboarding, Galleria Moduli, Revisione Settimanale

> Eredita il [Modello Funzionale Comune](00-modello-funzionale-comune.md). Qui solo funzioni e deroghe specifiche.

## 1. Scopo e tracciabilità

| Componente | Perché esiste | Principi | JTBD | Decisioni |
|---|---|---|---|---|
| Home "Oggi" | La risposta in 0 tocchi a "cosa conta oggi?" — il quadro unificato che nessun'app monodominio può dare | P25, P54, P82 | **J4**, J5 | D-01, D-04 |
| Onboarding | Time-to-value < 60 s: il valore prima del lavoro | P15, P17 | J1 (primo assaggio) | D-03, D-05 |
| Galleria moduli | La modularità resa visibile e governabile dall'utente | P77–P84 | J18 (anti-manutenzione) | D-01 |
| Revisione settimanale | Trasformare i dati in decisioni in 5 minuti guidati | P28, P52 | J6, J4 | — |

## 2. Funzioni

### 2.1 Home "Oggi"

| ID | Nome | Descrizione e motivo | Pri | Dipendenze |
|----|------|----------------------|-----|------------|
| HOME-001 | Vista composita del giorno | Aggrega i contributi dei moduli attivi (eventi, task del giorno, abitudini del giorno, stato budget) in un'unica vista scorrevole. *Motivo: J4; è la prima schermata a ogni apertura* | M | Moduli attivi; contratto "card della Home" |
| HOME-002 | Composizione dinamica per moduli | La Home include automaticamente le card dei soli moduli attivi; l'attivazione/disattivazione la riconfigura all'istante senza errori | M | GAL-002 |
| HOME-003 | Riordino delle card | L'utente può riordinare le sezioni con pressione lunga. *Motivo: il "mio" ordine di priorità (P82: la Home è dell'utente)* | S | HOME-001 |
| HOME-004 | Azioni dirette dalle card | Spunta abitudine, completa task, apri evento: 1 tocco senza cambiare schermata. *Motivo: P13/P21* | M | Moduli |
| HOME-005 | Banner insight contestuale | Al massimo 1 insight per giorno, archiviabile, mai modale. *Motivo: P36, C-art. 64* | S | INS-001 |
| HOME-006 | Vista "Domani/Prossimi giorni" | Swipe orizzontale per i 7 giorni successivi (sola lettura + pianificazione rapida) | S | HOME-001 |
| HOME-007 | Pull-to-refresh assente per design | La Home è sempre aggiornata (dati locali reattivi): il gesto di refresh non esiste. *Motivo: C-art. 51-52 — niente loop compulsivi; il refresh manuale è un'ammissione di inaffidabilità* | M | — |
| HOME-008 | Saluto e data | Intestazione minimale con data; nessuna metrica di vanità ("sei al 73%!") in testa. *Motivo: P29, tono C-art. 69* | M | — |

**Scheda estesa HOME-001** — *Requisiti*: compone in < 400 ms a freddo con 5 moduli attivi; ogni card ha lo stesso ordine interno (titolo, contenuto max 5 elementi, azione "vedi tutto"); il contenuto del giorno è definito dal fuso corrente (MFC-E-07). *Vincoli*: max 5 elementi per card (P37); la card senza contenuti del giorno mostra lo stato "libero" positivo (es. "nessun impegno oggi"), mai vuoto triste. *Casi limite*: 0 moduli attivi → la Home mostra la cattura e l'invito alla Galleria; giorno con 50+ elementi → raggruppa per modulo con conteggio; mezzanotte a schermo aperto → la vista ruota al nuovo giorno senza perdita di contesto. *Criteri di successo*: la Home risponde a J4 senza scroll nel 80% dei giorni-tipo (max 1 schermata).

### 2.2 Onboarding

| ID | Nome | Descrizione e motivo | Pri | Dipendenze |
|----|------|----------------------|-----|------------|
| ONB-001 | Proposta di valore in una schermata | Una frase, un'azione ("Inizia"). *Motivo: P15* | M | — |
| ONB-002 | Scelta moduli iniziale (1–2) | Griglia illustrata, Attività preselezionato. *Motivo: D-03; ridurre la scelta (P28)* | M | GAL |
| ONB-003 | Primo dato reale immediato | "Scrivi la prima cosa che devi fare" → cattura vera. *Motivo: TTV < 60 s; l'app si impara facendo* | M | CAPT-001 |
| ONB-004 | Account posticipabile | L'app funziona senza registrazione; la registrazione è proposta quando c'è qualcosa da proteggere. *Motivo: D-05, RF-06* | M | SYNC/SEC |
| ONB-005 | Permesso notifiche contestuale | Mai al primo avvio; solo alla prima entità con promemoria. *Motivo: C-art. 32, RF-52* | M | NTF |
| ONB-006 | Telemetria opt-in | Schermata unica, chiara, default off in UE. *Motivo: C-art. 23* | M | SET |
| ONB-007 | Migrazione locale→account | Alla registrazione, i dati locali diventano l'account senza perdita né duplicazione | M | SYNC |

**Scheda estesa ONB-004/007** — *Requisiti*: tutti i dati creati da anonimo sono già cifrati con chiavi locali; la registrazione ri-avvolge le chiavi senza ri-cifrare i dati; se l'utente non si registra mai, l'app resta pienamente funzionante per sempre (backup solo locale, con avviso onesto del rischio). *Casi limite*: registrazione fallita a metà (rete) → riprendibile, mai stato ibrido; email già esistente → offrire login e **unione guidata** (i dati locali si aggiungono all'account, con anteprima e undo).

### 2.3 Galleria moduli

| ID | Nome | Descrizione e motivo | Pri | Dipendenze |
|----|------|----------------------|-----|------------|
| GAL-001 | Catalogo moduli | Elenco moduli disponibili con scheda: cosa fa, permessi richiesti, anteprima con dati d'esempio interattivi. *Motivo: UC-09; C-art. 32 (permessi dichiarati prima)* | M | Registry |
| GAL-002 | Attivazione modulo | 1 tocco + micro-onboarding ≤ 3 schermate; il modulo appare in Home e navigazione. *Free: max 2 moduli attivi (D-05); il limite è dichiarato prima* | M | Billing per contatore |
| GAL-003 | Disattivazione modulo | Il modulo scompare dalle viste; i dati restano (MFC-R-13); riattivazione = tutto com'era | M | — |
| GAL-004 | Suggerimento contestuale di modulo | Max 1 proposta, basata su segnali d'uso locali (es. 3ª spesa scritta nelle note → proponi Finanze); archiviabile per sempre. *Motivo: loop di espansione; P36* | S | INS |
| GAL-005 | Aggiornamento modulo | I moduli si aggiornano con l'app; changelog per modulo nella scheda | S | — |
| GAL-006 | Moduli di terze parti | Ingresso marketplace (fase 3): stessa scheda + firma sviluppatore, permessi granulari, recensioni | C | Marketplace |

**Scheda estesa GAL-002/003** — *Casi limite*: disattivazione del modulo con entità collegate a obiettivi → l'obiettivo mostra il contributo "in pausa" (mai perso, mai contato a zero retroattivamente); Free con 2 moduli che ne disattiva 1 e ne attiva un altro → lecito, illimitato (il limite è sui *contemporanei*); attivazione offline di modulo on-demand non scaricato → messaggio onesto "richiede connessione una tantum" (unica eccezione ammessa, MFC §3).

### 2.4 Revisione settimanale

| ID | Nome | Descrizione e motivo | Pri | Dipendenze |
|----|------|----------------------|-----|------------|
| REV-001 | Sequenza di decisione guidata | Card una alla volta: task scaduti/senza data, abitudini in calo, budget in soglia, settimana entrante. Azioni a 1 tocco (pianifica/rimanda/archivia). *Motivo: J6; P28 — una decisione per schermata* | S | TASK, HAB, FIN, CAL |
| REV-002 | Progresso e uscita libera | "4 di 9", uscita in ogni momento, ripresa da dove si era. *Motivo: mai sequestrare l'utente (C-art. 56)* | S | REV-001 |
| REV-003 | Riepilogo finale | Una schermata: cosa è stato deciso; nessun voto, nessun giudizio | S | REV-001 |
| REV-004 | Promemoria del rituale | Opt-in, giorno/ora scelti dall'utente (default proposto: domenica 18:00) | S | NTF |

## 3. Comportamenti specifici (deroghe/aggiunte al MFC)

- **Apertura app** → sempre la Home (o l'ultima schermata se l'app era in background da < 30 min: continuità di contesto).
- **Home offline** = identica (tutti i dati sono locali). L'unica differenza ammessa: gli eventi di calendari remoti non ancora sincronizzati dal sistema mostrano l'ultimo stato noto.
- **Eliminazione dalla Home**: le azioni dalla card (completa/spunta) hanno undo inline come da MFC-R-11.
- **Autorizzazioni**: la Home rispetta MFC-R-21/22 (moduli sensibili bloccati → card con contenuto offuscato "sblocca per vedere", mai dati in chiaro).

## 4. Stati specifici

| Stato | Comportamento |
|---|---|
| Vuoto assoluto (primo giorno) | La Home è l'onboarding continuato: card didattiche per il modulo attivo + cattura in evidenza |
| Giornata completata | Stato positivo sobrio ("tutto fatto per oggi") — nessuna animazione invasiva (P45/P68) |
| Trial/Free | Nessuna differenza nella Home (mai pubblicità del piano nella vista quotidiana — C-art. 64) |
| Modulo sensibile bloccato | Card offuscata con azione di sblocco biometrico |

## 5. Regole di business

| ID | Regola | Motivo |
|----|--------|--------|
| HOME-R-01 | La Home mostra solo elementi del giorno corrente (fuso corrente); gli scaduti dei giorni precedenti compaiono in una sezione "in sospeso" distinta, mai mescolati | Chiarezza temporale; niente colpa diffusa (P46) |
| HOME-R-02 | Nessun elemento della Home è sponsorizzato, promosso o ordinato per interesse nostro | C-art. 157, 172 |
| HOME-R-03 | L'ordine di default delle card: eventi → task → abitudini → budget (dal vincolato al volitivo); l'ordine utente prevale sempre | Logica del giorno; P82 |
| ONB-R-01 | Nessun passo dell'onboarding può richiedere: account, permessi, pagamento | D-05; C-art. 32 |
| GAL-R-01 | Il conteggio moduli Free considera solo i moduli *attivi contemporaneamente* | D-05 — leva per capacità, non ricatto |
| REV-R-01 | La revisione non è mai obbligatoria né bloccante; saltarla non produce alcun segnale negativo | C-art. 54 |

## 6. Eventi

| Direzione | Evento | Effetto |
|---|---|---|
| Pubblica | `core.module.activated` / `core.module.deactivated` | Home si ricompone; NTF ricalcola i canali; INS aggiorna le regole attive |
| Pubblica | `core.day.changed` (mezzanotte/fuso) | Tutti i moduli ricalcolano "oggi" |
| Pubblica | `core.review.completed` | INS registra il rituale (per digest); nessun obbligo altrui |
| Sottoscrive | `*.item.created/completed/changed` (dai moduli) | Aggiornamento reattivo delle card |
| Sottoscrive | `ins.insight.available` | Mostra banner (max 1/giorno) |

## 7. Edge case specifici

- Mezzanotte con revisione in corso → la sessione continua sui dati della settimana per cui è iniziata.
- Cambio fuso con la Home aperta → ricomposizione al nuovo "oggi" con transizione visibile (mai elementi che spariscono in silenzio — MFC-E-07).
- 0 moduli attivi (tutti disattivati) → Home = cattura + Galleria; mai schermata rotta.
- Modulo disattivato mentre la sua card è a schermo → la card scompare con transizione; l'eventuale azione in corso completa comunque (i dati restano, MFC-R-13).

## 8. Criteri di accettazione

- **HOME-AC-01** — *Dato* un utente con 3 moduli attivi e dati per oggi, *quando* apre l'app, *allora* la Home mostra eventi, task, abitudini e stato budget del giorno senza alcuna interazione, entro i budget di avvio.
- **HOME-AC-02** — *Dato* un task visibile nella Home, *quando* l'utente lo completa dalla card, *allora* il task risulta completato nel modulo Attività e la card si aggiorna senza cambio di schermata, con undo disponibile 7 s.
- **HOME-AC-03** — *Dato* un modulo disattivato, *quando* l'utente osserva la Home, *allora* nessuna card né dato del modulo è visibile, e alla riattivazione ricompare identico.
- **ONB-AC-01** — *Dato* un nuovo utente, *quando* completa l'onboarding scegliendo un modulo e catturando il primo elemento, *allora* il tempo totale mediano è ≤ 60 s e nessun permesso/account è stato richiesto.
- **ONB-AC-02** — *Dato* un utente anonimo con 100 entità locali, *quando* si registra, *allora* tutte le entità risultano nell'account senza duplicati e senza perdita, e il device risulta nel registro dispositivi.
- **GAL-AC-01** — *Dato* un utente Free con 2 moduli attivi, *quando* tenta di attivarne un terzo, *allora* riceve una proposta Plus chiara (contenuto, prezzo, disdetta) e può rifiutare restando pienamente operativo sui 2 moduli.
- **REV-AC-01** — *Dato* un utente che interrompe la revisione alla card 4 di 9, *quando* la riapre entro la stessa settimana, *allora* riprende dalla card 5 con le decisioni precedenti salvate.

---

*Prossimo modulo: [Cattura Rapida](02-cattura-rapida.md)*
