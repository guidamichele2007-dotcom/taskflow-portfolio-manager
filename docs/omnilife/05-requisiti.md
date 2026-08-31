# 05 · Requisiti Funzionali e Non Funzionali

> Convenzioni: **RF-x** = requisito funzionale, **RNF-x** = requisito non funzionale. Ogni requisito è (a) tracciato a una funzionalità del doc 02 o a un principio del brief, (b) verificabile — dove possibile con soglia numerica misurata in CI o QA. MoSCoW: M = Must (MVP), S = Should (v1.x), C = Could (v2.x).

## 1. Requisiti funzionali

### 1.1 Core e cattura

| ID | Requisito | Trace | Pri |
|----|-----------|-------|-----|
| RF-01 | Il sistema DEVE offrire una cattura rapida accessibile da ogni schermata, da widget, da quick action e da comando vocale | C-02 | M |
| RF-02 | Il parser di cattura DEVE riconoscere in italiano e inglese: tipo di entità, importo/valuta, data/ora relativa e assoluta, categoria, e proporre un'interpretazione modificabile prima del salvataggio | C-02, T-05 | M |
| RF-03 | Le correzioni dell'utente al parser DEVONO essere apprese on-device e migliorare le proposte successive | C-02 | S |
| RF-04 | La Home "Oggi" DEVE comporsi dinamicamente con i contributi dei soli moduli attivi (eventi, task, abitudini, stato budget) e aggiornarsi in tempo reale alle modifiche | C-01 | M |
| RF-05 | La ricerca DEVE restituire risultati full-text da tutti i moduli attivi, offline, con risultati incrementali durante la digitazione | C-03 | M |
| RF-06 | L'onboarding DEVE consentire l'uso completo dell'app senza registrazione; la registrazione DEVE poter avvenire in qualsiasi momento successivo senza perdita di dati (migrazione locale → account) | C-05 | M |
| RF-07 | La revisione settimanale DEVE presentare in sequenza gli elementi che richiedono decisione (task scaduti/senza data, abitudini in calo, budget in soglia, settimana entrante) con azioni a un tocco e progresso salvato | C-07 | S |

### 1.2 Modularità

| ID | Requisito | Trace | Pri |
|----|-----------|-------|-----|
| RF-10 | L'utente DEVE poter attivare e disattivare ogni modulo singolarmente; la disattivazione NON DEVE cancellare i dati del modulo né compromettere gli altri moduli | C-04, ADR-5 | M |
| RF-11 | Ogni modulo DEVE dichiarare via manifest: entità, contributi UI, eventi, permessi; il Core DEVE rifiutare moduli con contratto incompatibile | §3 doc 03 | M |
| RF-12 | I moduli DEVONO comunicare esclusivamente tramite event bus e archi del grafo; la build DEVE fallire in presenza di dipendenze dirette tra moduli | ADR-5 | M |
| RF-13 | Ogni modulo DEVE poter migrare il proprio schema dati in modo isolato e reversibile all'aggiornamento | §3.3 doc 03 | M |
| RF-14 | Il sistema DEVE supportare l'installazione on-demand dei moduli non attivi ove la piattaforma lo consenta | §3.3 doc 03 | S |

### 1.3 Moduli (sintesi delle capacità obbligatorie)

| ID | Requisito | Trace | Pri |
|----|-----------|-------|-----|
| RF-20 | Attività: creazione, modifica, completamento, ripetizione, promemoria, liste/aree, sottotask a un livello, priorità a 3 livelli | T-01…T-04 | M |
| RF-21 | Finanze: spese/entrate con categoria e conto; budget mensili con stati di soglia; ricorrenze automatiche; report mensile | F-01,02,05,06 | M |
| RF-22 | Finanze: obiettivi di risparmio con proiezione; multi-conto con trasferimenti non conteggiati come spese | F-03, F-04 | S |
| RF-23 | Abitudini: binarie e quantitative; frequenze flessibili (n/settimana); costanza resiliente mai azzerata da un singolo salto; spunta da widget e notifica con undo | H-01…H-03, H-06 | M |
| RF-24 | Abitudini: proposta di ridimensionamento dopo salti ripetuti | H-04 | S |
| RF-25 | Calendario: lettura dei calendari di sistema e vista agenda unificata con task e abitudini | CA-01, CA-02 | M (lettura) |
| RF-26 | Calendario: time-boxing dei task con scrittura sul calendario di sistema | CA-03 | S |
| RF-27 | Note: editor a testo ricco leggero; collegamento di note a entità di altri moduli | N-01, N-02 | S |
| RF-28 | Salute: lettura da HealthKit/Health Connect previo consenso granulare; auto-completamento delle abitudini collegate | S-01, S-03 | S |
| RF-29 | Obiettivi: aggregazione di task, risparmi, abitudini e scadenze con progresso composto | G-01, G-02 | S |
| RF-30 | Insight: generazione on-device di osservazioni trasversali con regole aggiornabili via configurazione firmata, senza invio di contenuti al server | X-05 | S |

### 1.4 Dati, sync, backup

| ID | Requisito | Trace | Pri |
|----|-----------|-------|-----|
| RF-40 | Ogni funzione dell'app DEVE essere disponibile offline; le modifiche DEVONO sincronizzarsi automaticamente al ritorno della rete | X-02, UC-07 | M |
| RF-41 | Le modifiche concorrenti da più dispositivi DEVONO convergere automaticamente senza richiedere scelte all'utente | ADR-3 | M |
| RF-42 | Il sistema DEVE eseguire backup automatici cifrati con generazioni multiple e DEVE consentire il ripristino completo su nuovo dispositivo | X-03, UC-08 | M |
| RF-43 | L'utente DEVE poter esportare tutti i propri dati in formati aperti (JSON, CSV) generati sul dispositivo | X-06, UC-10 | M |
| RF-44 | L'utente DEVE poter cancellare definitivamente account e dati (device + cloud) con conferma forte; la cancellazione cloud DEVE completarsi entro 30 giorni | doc 06 | M |

### 1.5 Notifiche

| ID | Requisito | Trace | Pri |
|----|-----------|-------|-----|
| RF-50 | Le notifiche DEVONO rispettare un budget giornaliero configurabile, con raggruppamento in digest e orari di silenzio | X-07 | M |
| RF-51 | Le notifiche DEVONO essere azionabili (completa/posticipa) senza aprire l'app | H-06 | M |
| RF-52 | Il permesso notifiche DEVE essere richiesto solo in contesto d'uso, mai al primo avvio | §4.1 doc 04 | M |

## 2. Requisiti non funzionali

### 2.1 Prestazioni (misurate su device di riferimento "basso": iPhone 11 / Android fascia media 2021 con 4 GB RAM)

| ID | Requisito | Soglia | Verifica |
|----|-----------|--------|----------|
| RNF-P1 | Avvio a freddo fino a Home interattiva | ≤ 1,5 s (p90) | Benchmark CI su device farm |
| RNF-P2 | Avvio a caldo | ≤ 400 ms (p90) | idem |
| RNF-P3 | Frame rate nelle transizioni e nello scroll | 0 frame persi percepibili; jank < 1% frame (p95) | Profiling automatico |
| RNF-P4 | Latenza percepita delle scritture (salva, spunta) | ≤ 50 ms alla conferma UI (scrittura locale) | Test strumentati |
| RNF-P5 | Ricerca full-text su 50.000 entità | primi risultati ≤ 100 ms | Benchmark con dataset sintetico |
| RNF-P6 | Memoria dell'app con 3 moduli attivi | ≤ 150 MB in uso attivo | Profiling CI |
| RNF-P7 | Batteria | ≤ 2%/giorno in uso tipico (30 min); sync in background con vincoli di sistema rispettati | Test energetici periodici |
| RNF-P8 | Dimensione installazione | ≤ 60 MB iOS / ≤ 40 MB Android (base) | Gate in CI |
| RNF-P9 | Ogni modulo attivo | ≤ +50 ms all'avvio, ≤ +15 MB RAM | Budget per modulo in CI |
| RNF-P10 | Ripristino completo (10k entità, rete 20 Mbps) | ≤ 2 min | Test E2E |

### 2.2 Affidabilità

| ID | Requisito | Soglia |
|----|-----------|--------|
| RNF-A1 | Crash-free sessions | ≥ 99,8% |
| RNF-A2 | Perdita di dati confermati alla UI | 0 — le scritture locali sono transazionali e durabili |
| RNF-A3 | Convergenza della sync multi-device | 100% dei casi senza intervento utente; proprietà verificate con test generativi/fuzzing sul motore CRDT |
| RNF-A4 | Disponibilità backend | ≥ 99,9% mensile; l'app resta pienamente funzionante al 100% durante indisponibilità |
| RNF-A5 | RPO/RTO backend | ≤ 15 min / ≤ 1 h |

### 2.3 Sicurezza e privacy (dettagli nel doc 06)

| ID | Requisito |
|----|-----------|
| RNF-S1 | Contenuti utente cifrati end-to-end: il backend non deve poter decifrare i contenuti in nessuna circostanza |
| RNF-S2 | Dati a riposo sul device cifrati (SQLCipher o equivalente + keystore hardware) |
| RNF-S3 | Sblocco app con biometria (Face ID/Touch ID/BiometricPrompt) con fallback a codice; timeout configurabile |
| RNF-S4 | Tutte le comunicazioni su TLS 1.3 con certificate pinning |
| RNF-S5 | Nessun contenuto utente in log, crash report, notifiche push o telemetria |
| RNF-S6 | Conformità GDPR e CCPA; privacy label store accurate; DPIA completata prima del lancio |
| RNF-S7 | Dipendenze di terze parti sottoposte a scansione vulnerabilità continua; SBOM mantenuta |

### 2.4 Usabilità e accessibilità

| ID | Requisito | Verifica |
|----|-----------|----------|
| RNF-U1 | I budget di tocchi dei flussi (doc 04 §4) sono requisiti: cattura ≤ 3 tocchi/3 s; spunta abitudine 1 tocco; attivazione modulo ≤ 4 tocchi | Checklist QA per release |
| RNF-U2 | Conformità WCAG 2.2 AA su tutte le schermate | Audit automatico + manuale per release |
| RNF-U3 | Funzionamento corretto con font scaling 200%, screen reader, riduzione movimento, alto contrasto | Suite di test dedicata |
| RNF-U4 | Localizzazione it/en completa al lancio; architettura pronta per RTL | Review linguistica |
| RNF-U5 | Task success rate ≥ 90% sui flussi core in test di usabilità moderati (5+ utenti per ciclo) | Ricerca UX pre-release |

### 2.5 Scalabilità e manutenibilità

| ID | Requisito |
|----|-----------|
| RNF-M1 | Backend dimensionato per 5 M utenti registrati / 1 M DAU; Sync p99 < 300 ms a 10k rps; scala orizzontale senza re-architettura (partizionamento per utente) |
| RNF-M2 | API REST versionate con OpenAPI come fonte di verità; breaking change solo con nuova versione major e finestra di deprecazione ≥ 6 mesi |
| RNF-M3 | Module Contract versionato con compatibilità N-1 garantita da test di contratto |
| RNF-M4 | Copertura test: core condiviso (sync, crypto, parser) ≥ 90%; logica moduli ≥ 80%; test E2E sui flussi core per release |
| RNF-M5 | CI/CD: ogni merge produce build installabile; release train regolare (doc 09); feature flag per rollout graduale e kill-switch |
| RNF-M6 | Observability senza PII: metriche, tracce e log strutturati; alert su SLO |
| RNF-M7 | Compatibilità OS: iOS N-2, Android API 26+ (copertura ≥ 95% del parco device target) |

## 3. Matrice di tracciabilità (estratto)

| Principio del brief | Requisiti che lo garantiscono |
|---|---|
| Offline First | RF-40, RF-41, RNF-A4 |
| Minor numero di tocchi | RNF-U1, RF-01, RF-51 |
| Modularità totale | RF-10…RF-14, RNF-M3 |
| Sicurezza/crittografia/biometria | RNF-S1…S7, RF-42, RF-44 |
| Prestazioni su device datati | RNF-P1…P10 |
| Accessibilità completa | RNF-U2, RNF-U3 |
| Scalabilità a milioni di utenti | RNF-M1, RNF-M2 |
| Backup e ripristino | RF-42, RNF-A2, RNF-A5 |
