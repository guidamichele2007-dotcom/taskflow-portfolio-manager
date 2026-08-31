# 02 · Funzionalità e Priorità

> Regola del brief: **ogni funzione deve avere un motivo preciso per esistere; nessuna funzione duplicata; nessuna schermata inutile.** Per questo ogni funzionalità qui elencata riporta la motivazione e il caso d'uso o problema che serve (riferimenti al doc 01). Le funzioni respinte sono documentate in §5: decidere cosa NON fare è parte del progetto.

## 1. Struttura dell'ecosistema

OmniLife = **Core** (sempre presente, non disattivabile) + **Moduli** (attivabili singolarmente) + **Servizi trasversali** (usati da core e moduli).

```
┌──────────────────────────────────────────────────────┐
│                     MODULI (opt-in)                   │
│  Attività · Finanze · Abitudini · Calendario ·        │
│  Note · Salute · Obiettivi · (futuri: plugin terzi)   │
├──────────────────────────────────────────────────────┤
│                        CORE                           │
│  Home "Oggi" · Cattura rapida · Ricerca · Galleria    │
│  moduli · Impostazioni · Onboarding · Profilo         │
├──────────────────────────────────────────────────────┤
│                 SERVIZI TRASVERSALI                   │
│  Grafo dati personale · Sync E2E · Notifiche ·        │
│  Widget · Insight engine · Export/Backup · Design     │
│  system · Accessibilità · Telemetria privacy-safe     │
└──────────────────────────────────────────────────────┘
```

## 2. Elenco completo delle funzionalità

### 2.1 CORE

| ID | Funzionalità | Descrizione | Motivo di esistere |
|----|--------------|-------------|--------------------|
| C-01 | **Home "Oggi"** | Vista unificata di eventi, task, abitudini e stato budget del giorno; composta dinamicamente dai soli moduli attivi | UC-02; è la risposta a P1. Zero navigazione per il quadro quotidiano |
| C-02 | **Cattura rapida universale** | Pulsante persistente + widget + azione vocale; parser NLP che riconosce tipo, importo, data, categoria | UC-01, UC-11; risolve P3. Funzione più importante del prodotto |
| C-03 | **Ricerca globale** | Ricerca full-text locale e istantanea su tutte le entità di tutti i moduli | P1: un solo posto dove trovare tutto; indispensabile con più moduli attivi |
| C-04 | **Galleria moduli** | Scoperta, anteprima interattiva, attivazione/disattivazione dei moduli | UC-09; concretizza il principio di modularità per l'utente |
| C-05 | **Onboarding progressivo** | ≤ 60 secondi: scegli 1–2 moduli, primo dato inserito subito; il resto si scopre dopo | Persona Anna; il time-to-value determina la retention D1 |
| C-06 | **Impostazioni e profilo** | Account, sicurezza, tema, notifiche, dati | Requisito igienico; include i controlli privacy (doc 06) |
| C-07 | **Revisione settimanale guidata** | Flusso opzionale che ripassa task, abitudini, budget e settimana entrante con azioni a un tocco | UC-04; trasforma i dati in decisioni; rituale di retention |

### 2.2 Modulo ATTIVITÀ (Task)

| ID | Funzionalità | Descrizione | Motivo di esistere |
|----|--------------|-------------|--------------------|
| T-01 | Task con data, ora, ripetizione, promemoria | Modello task completo ma con UI a rivelazione progressiva (i dettagli appaiono solo se richiesti) | Base della categoria; la rivelazione progressiva evita la schermata-formulario |
| T-02 | Liste e aree | Raggruppamento semplice a due livelli (area → lista) | Sufficiente per il 95% degli utenti; le gerarchie profonde sono complessità da anti-persona |
| T-03 | Priorità a 3 livelli | Alta/media/nessuna | Più livelli = paralisi decisionale (evidenza dai competitor) |
| T-04 | Sottotask | Un livello di nidificazione | I task multilivello infiniti duplicano il concetto di "progetto" → rinviato a Obiettivi |
| T-05 | Parsing linguaggio naturale | "venerdì 15 alle 9 chiama il commercialista #lavoro" | Supera Todoist (benchmark di categoria) anche in italiano |
| T-06 | Posticipa intelligente | Suggerimenti di rinvio basati sul calendario reale (non "domani" fisso ma "domani hai 3 riunioni, meglio giovedì") | `[INT]` con Calendario; riduce i rinvii a catena |

### 2.3 Modulo FINANZE

| ID | Funzionalità | Descrizione | Motivo di esistere |
|----|--------------|-------------|--------------------|
| F-01 | Registrazione spese/entrate | Cattura in ≤ 3 tocchi con categorie apprese dall'uso | UC-03; P3 |
| F-02 | Budget mensili per categoria | Soglie con stato visivo (verde/ambra/rosso) | Il budget è la ragione per registrare le spese |
| F-03 | Obiettivi di risparmio | Importo target + data; proiezione automatica | UC-06; collega denaro a scopi di vita |
| F-04 | Conti multipli e trasferimenti | Contanti, carte, conti; i trasferimenti non sono spese | Correttezza contabile minima senza diventare un gestionale |
| F-05 | Ricorrenze | Affitto, stipendio, abbonamenti auto-registrati | Elimina l'80% degli inserimenti manuali ripetitivi |
| F-06 | Report mensile | Una schermata: entrate, uscite, top categorie, confronto col mese precedente | Risponde a "come sto andando?" senza dashboard complesse |
| F-07 | Multi-valuta | Valuta per conto con conversione a valuta primaria | Mercato europeo; viaggi (persona Giulia) |

### 2.4 Modulo ABITUDINI

| ID | Funzionalità | Descrizione | Motivo di esistere |
|----|--------------|-------------|--------------------|
| H-01 | Abitudini binarie e quantitative | "Meditare" (sì/no) e "Bere 2L d'acqua" (progressivo) | Copre entrambi i modelli mentali reali |
| H-02 | Frequenze flessibili | "3 volte a settimana" oltre a "ogni giorno" | UC-05; il rigido quotidiano è la causa n.1 di abbandono |
| H-03 | Costanza resiliente | Metrica di costanza che tollera i salti; nessuna streak azzerata | Psicologia comportamentale (doc 04 §5); anti-Streaks |
| H-04 | Riduzione gentile | Dopo salti ripetuti l'app propone di ridimensionare l'obiettivo | Trasforma il fallimento in ricalibrazione; retention emotiva |
| H-05 | Promemoria contestuali | Orario + (opzionale) luogo; raggruppati per non spammare | Le notifiche sono un patto di fiducia (doc 04 §5.3) |
| H-06 | Spunta da widget/notifica | Completamento senza aprire l'app | P3: l'abitudine si segna nel momento in cui accade |

### 2.5 Modulo CALENDARIO

| ID | Funzionalità | Descrizione | Motivo di esistere |
|----|--------------|-------------|--------------------|
| CA-01 | Lettura/scrittura calendari di sistema (EventKit / CalendarProvider) | OmniLife non impone un nuovo silo di eventi | Interoperabilità: il calendario dell'utente esiste già; duplicarlo violerebbe "nessuna funzione duplicata" |
| CA-02 | Vista agenda unificata | Eventi + task pianificati + abitudini nella stessa timeline | `[INT]`; UC-02/UC-04 |
| CA-03 | Time-boxing dei task | Trascina un task nella timeline per riservargli tempo | Supera Structured; collega intenzione e tempo |

### 2.6 Modulo NOTE

| ID | Funzionalità | Descrizione | Motivo di esistere |
|----|--------------|-------------|--------------------|
| N-01 | Note in testo ricco leggero (grassetto, liste, titoli, checklist) | Editor veloce, non un word processor | Cattura di pensiero; il minimalismo è il punto di forza di Bear |
| N-02 | Collegamenti alle entità | Una nota può collegarsi a task, spese, obiettivi, eventi | `[INT]`; la nota diventa contesto del grafo personale |
| N-03 | Pin e archivio | Organizzazione a costo zero | Le gerarchie di cartelle profonde sono attrito; ricerca > organizzazione |

### 2.7 Modulo SALUTE

| ID | Funzionalità | Descrizione | Motivo di esistere |
|----|--------------|-------------|--------------------|
| S-01 | Lettura da HealthKit / Health Connect | Passi, sonno, battito, allenamenti — mai raccolti da noi direttamente | Il dato sanitario resta nell'enclave OS; noi aggiungiamo contesto, non sensori |
| S-02 | Metriche manuali | Peso, umore, energia (scala 1–5) | L'umore/energia alimenta gli insight trasversali |
| S-03 | Collegamento abitudini ↔ salute | "Corsa 3×/settimana" si completa da sola con il workout rilevato | `[INT]`; zero attrito = costanza |

### 2.8 Modulo OBIETTIVI

| ID | Funzionalità | Descrizione | Motivo di esistere |
|----|--------------|-------------|--------------------|
| G-01 | Obiettivi trasversali | Un obiettivo aggrega task, risparmi, abitudini, scadenze | UC-06: la funzione-firma dell'ecosistema |
| G-02 | Progresso aggregato | Percentuale composta e proiezione realistica | Rende visibile il valore dell'integrazione |
| G-03 | Traguardi intermedi | Milestone con celebrazioni sobrie | Psicologia: il progresso percepito sostiene la motivazione |

### 2.9 Servizi trasversali

| ID | Funzionalità | Descrizione | Motivo di esistere |
|----|--------------|-------------|--------------------|
| X-01 | Grafo dati personale | Ogni entità è collegabile a ogni altra; le connessioni alimentano insight e obiettivi | Fondamento di tutti gli `[INT]`; differenziazione architetturale |
| X-02 | Sync E2E multi-device | CRDT, offline-first, riconciliazione automatica | UC-07; P5, P6 |
| X-03 | Backup automatico cifrato + ripristino | Snapshot periodici, chiave di recupero | UC-08; requisito del brief "protezione contro perdita dati" |
| X-04 | Widget (iOS/Android) e Watch companion (fase 2+) | Oggi, cattura, abitudini, budget | P3; i widget sono il canale di ritorno quotidiano più efficace |
| X-05 | Insight engine | Regole locali (on-device) che generano osservazioni trasversali | UC-03, UC-12; il valore composto dei dati, senza inviare nulla al server |
| X-06 | Export completo | JSON + CSV, generato on-device | UC-10; GDPR art. 20 |
| X-07 | Notifiche digest | Raggruppamento intelligente; budget di notifiche giornaliero | Anti-spam: proteggere l'attenzione è parte del prodotto |
| X-08 | Sistema di plugin (fase 3+) | SDK per moduli di terze parti, sandbox, review | Scalabilità dell'ecosistema (brief: "sistema di plugin") |

## 3. Priorità delle funzionalità

### 3.1 Metodo

Doppio filtro:
1. **MoSCoW** per l'appartenenza alla release (Must/Should/Could/Won't-now).
2. **RICE** (Reach × Impact × Confidence ÷ Effort) per l'ordinamento dentro ogni fascia. Punteggi: Reach = % utenti attivi toccati/mese; Impact = 0,25–3; Confidence = 0,5–1; Effort = settimane-persona.

### 3.2 MVP (v1.0) — Must have

Regola dell'MVP: **il nucleo + 3 moduli (Attività, Finanze, Abitudini) fatti in modo eccellente** battono 8 moduli mediocri. Calendario in sola lettura per la vista unificata.

| Rank | ID | RICE | Nota |
|------|----|------|------|
| 1 | C-02 Cattura rapida | 100×3×0,9÷6 = 45,0 | Il moltiplicatore di tutto |
| 2 | X-02 Sync E2E offline-first | 100×3×0,7÷14 = 15,0 | Costoso ma fondativo: non retrofittabile |
| 3 | C-01 Home "Oggi" | 100×2×0,9÷5 = 36,0 | Prima schermata, prima impressione |
| 4 | T-01…T-05 Task core | 90×2×0,9÷8 = 20,3 | Categoria d'ingresso più frequente |
| 5 | H-01…H-03, H-06 Abitudini core | 70×2×0,8÷6 = 18,7 | Motore di apertura quotidiana |
| 6 | F-01, F-02, F-05 Finanze core | 60×3×0,8÷8 = 18,0 | Categoria con maggiore willingness-to-pay |
| 7 | C-05 Onboarding | 100×2×0,8÷3 = 53,3 | Determina la conversione D0→D1 |
| 8 | X-03 Backup/ripristino | 100×2×0,9÷4 = 45,0 | Fiducia; obbligo del brief |
| 9 | C-03 Ricerca | 80×1×0,9÷3 = 24,0 | Igienico con 3 moduli |
| 10 | X-04 Widget base | 70×2×0,8÷4 = 28,0 | Retention loop quotidiano |
| 11 | CA-02 Agenda unificata (lettura) | 60×2×0,7÷4 = 21,0 | Dimostra l'integrazione già nell'MVP |
| 12 | C-04 Galleria moduli | 100×1×0,9÷3 = 30,0 | La modularità deve esistere dal giorno 1, anche con soli 3 moduli |
| 13 | C-06, X-06 Impostazioni + export | — | Obblighi legali e igienici |

### 3.3 v1.x — Should have (primi 6 mesi post-lancio)

C-07 Revisione settimanale · G-01/G-02 Obiettivi trasversali · N-01/N-02 Note · F-03/F-04/F-06 Finanze estese · H-04/H-05 · T-06 · CA-03 Time-boxing · X-05 Insight engine · X-07 Digest · S-01/S-03 Salute (lettura + auto-completamento).

Motivazione della sequenza: prima si consolida la retention (revisione, insight, digest), poi si allarga la superficie (note, salute). Gli Obiettivi trasversali arrivano appena esistono abbastanza dati collegabili.

### 3.4 v2.x — Could have

F-07 Multi-valuta · S-02 Metriche manuali · G-03 Milestone · Watch app · tablet/iPad layout · import da competitor (Todoist, CSV bancari) · condivisione selettiva (es. budget di coppia).

### 3.5 Won't have (per ora) — respinte con motivo

| Proposta | Perché respinta |
|----------|-----------------|
| Chat/social interno | Anti-persona; economia dell'attenzione contraria alla missione |
| Collegamento diretto ai conti bancari (open banking) nell'MVP | Costo regolatorio (PSD2) e di sicurezza sproporzionato; rivalutare a scala. L'inserimento assistito + ricorrenze copre l'80% del valore |
| Client web nell'MVP | Diluisce la qualità mobile; la sync E2E lo rende complesso; fase 3 |
| AI generativa in-app nell'MVP | Nessun caso d'uso che superi il costo (privacy, latenza, prezzo); il parsing NLP locale è sufficiente. Rivalutare per insight avanzati on-device |
| Gamification a punti/avatar | Divisiva (evidenza Habitica); la nostra retention è basata su valore e gentilezza |
| Temi personalizzabili illimitati | Diluisce l'identità visiva; offriamo Dark/Light + accento, fine |

## 4. Criterio di qualità per l'accettazione di ogni funzionalità

Una funzionalità è "fatta" solo se: (a) rispetta i budget di prestazioni (doc 05 RNF), (b) è accessibile (WCAG 2.2 AA), (c) funziona offline, (d) ha telemetria privacy-safe per misurarne l'uso, (e) ha una definizione di successo misurabile (es. cattura rapida: mediana < 3 s). Le funzioni che dopo 2 cicli di misura non dimostrano uso vengono rimosse: la rimozione è una feature.

## 5. Tracciabilità

Ogni ID di questo documento è riferito nei requisiti (doc 05), nei flussi (doc 04) e nel piano (doc 09). Nessuna funzione può entrare in sviluppo senza un ID qui definito.
