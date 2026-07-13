# 03 · Architettura ad Alto Livello

> Obiettivi architetturali derivati dal brief: modularità totale (aggiunta/rimozione/aggiornamento di moduli senza impatti), Offline-First, prestazioni su dispositivi datati, crittografia E2E, scalabilità a milioni di utenti, API documentate, sistema di plugin, versionamento. Questo documento definisce il **come** senza scrivere codice: contratti, confini e responsabilità.

## 1. Vista d'insieme

```
┌────────────────────────── DISPOSITIVO ──────────────────────────┐
│                                                                  │
│  ┌───────────── App Shell (Core) ─────────────┐                  │
│  │ Navigazione · Home · Cattura · Ricerca ·   │                  │
│  │ Galleria moduli · Impostazioni             │                  │
│  └──────┬──────────────────────────┬──────────┘                  │
│         │ Module Contract (API)    │ Event Bus (pub/sub)         │
│  ┌──────┴──────┐ ┌──────────┐ ┌────┴─────┐ ┌──────────┐          │
│  │  Attività   │ │ Finanze  │ │ Abitudini│ │   ...    │  MODULI  │
│  └──────┬──────┘ └────┬─────┘ └────┬─────┘ └────┬─────┘          │
│         │             │            │            │                │
│  ┌──────┴─────────────┴────────────┴────────────┴──────┐         │
│  │              PERSONAL DATA GRAPH (locale)            │         │
│  │  Store locale cifrato · indice ricerca · link engine │         │
│  └──────────────────────────┬───────────────────────────┘        │
│  ┌──────────────────────────┴───────────────────────────┐        │
│  │   SYNC ENGINE (CRDT) · Crypto layer (E2E) · Outbox    │        │
│  └──────────────────────────┬───────────────────────────┘        │
└─────────────────────────────┼────────────────────────────────────┘
                              │ TLS 1.3 (payload già cifrati E2E)
┌─────────────────────────────┴────────────────────────────────────┐
│                          BACKEND (cloud)                          │
│  API Gateway → Auth · Sync Service (blob cifrati + vettori di    │
│  versione) · Backup Service · Push relay · Billing · Module      │
│  Registry (metadati moduli/plugin) · Telemetria anonima          │
│  Il backend NON può leggere i contenuti utente.                  │
└───────────────────────────────────────────────────────────────────┘
```

Decisione chiave: **il backend è "content-blind"**. Sincronizza e conserva blob cifrati e metadati di versionamento, mai contenuti in chiaro. Questo semplifica la conformità, riduce la superficie d'attacco e rende il pilastro privacy verificabile. Il costo (niente ricerca server-side, niente logica server sui contenuti) è accettabile perché l'intelligenza del prodotto è on-device per progetto.

## 2. Scelte di stack (con motivazione)

| Area | Scelta | Motivazione | Alternativa respinta |
|------|--------|-------------|----------------------|
| UI mobile | **Nativo: SwiftUI (iOS) + Jetpack Compose (Android)**, con specifica di design system condivisa e token generati da un'unica fonte | Il brief chiede esperienza Premium, fluidità 120 fps, avvio < 1,5 s, consumo minimo su device datati: il nativo è l'unico livello che garantisce tutto questo senza compromessi (widget, watch, biometria, HealthKit/Health Connect di prima classe) | Flutter/React Native: eccellenti per time-to-market, ma pagano su widget/health/animazioni di sistema e dimensione binario; con qualità "top store" come vincolo primario, il nativo vince. Il costo del doppio team è mitigato dal §3 (logica condivisa) |
| Logica condivisa | **Kotlin Multiplatform (KMP)** per: modello dati, sync engine, crypto, parser NLP, regole insight, validazioni | Una sola implementazione dei componenti più delicati (sync, crypto) = metà dei bug nel punto dove i bug costano di più. UI resta nativa | Logica duplicata: divergenza garantita nel tempo; C++/Rust core: ottimo ma ergonomia peggiore per il team mobile |
| Store locale | **SQLite** (cifrato, vedi doc 06) con schema versionato + indice FTS per la ricerca | Maturo, prevedibile, performante su device datati, strumenti di debug eccellenti | Realm/ObjectBox: lock-in e comportamenti di sync proprietari in conflitto con il nostro CRDT |
| Modello di sync | **CRDT (state-based, delta-compressed)** per entità; Last-Writer-Wins solo per campi scalari non conflittuali | UC-07: riconciliazione automatica senza dialoghi di conflitto; multi-device senza server "arbitro" | OT (operational transform): richiede server che vede i contenuti → incompatibile con E2E |
| Backend | **Servizi stateless containerizzati** (Kubernetes) dietro API Gateway; linguaggio: Go o Kotlin/JVM | Scala orizzontale a milioni di utenti; il backend fa poco (per progetto) quindi deve farlo in modo economico e affidabile | Monolite: ok all'inizio ma il Sync Service ha profilo di carico diverso da Billing/Registry; serverless puro: costi imprevedibili a scala per la sync persistente |
| Database backend | **PostgreSQL** (account, billing, registry) + **object storage** (blob cifrati, backup) + **Redis** (sessioni push, rate limiting) | Ogni dato nel posto giusto per costo e profilo d'accesso | Tutto in Postgres: i blob gonfiano il DB; tutto NoSQL: transazioni account/billing richiedono ACID |
| API | **REST/JSON versionata (`/v1/`)**, OpenAPI 3.1 come fonte di verità, client generati | "API documentate" da brief; la spec genera doc, client e test di contratto | GraphQL: superfluo per un backend content-blind con pochi endpoint |
| Push | APNs + FCM via relay proprio | I payload push non contengono contenuti (solo trigger di sync + notifiche locali) | Push con contenuto: violerebbe E2E |

## 3. Il Module Contract (cuore della modularità)

Ogni modulo — interno o di terze parti — implementa lo stesso contratto. **Nessun modulo importa codice di un altro modulo.** Le uniche dipendenze permesse sono verso il Core SDK.

### 3.1 Cosa dichiara un modulo (manifest)

- **Identità**: id, nome, icona, versione (semver), versione minima del Core richiesta.
- **Tipi di entità** che possiede (es. `task`, `expense`) con schema versionato.
- **Contributi alla UI**: schermata radice, card per la Home "Oggi", tipi di widget, azioni di cattura rapida ("questo testo sembra una spesa"), voci di ricerca.
- **Eventi** pubblicati e sottoscritti (vedi 3.2).
- **Permessi richiesti** (notifiche, posizione, HealthKit…): dichiarativi, mostrati all'attivazione.

### 3.2 Comunicazione tra moduli: solo eventi e link

- **Event Bus locale (pub/sub tipizzato)**: es. Finanze pubblica `expense.created`; l'Insight engine e il modulo Obiettivi la consumano. Il publisher non sa chi ascolta → rimuovere un modulo non rompe nulla (le sottoscrizioni semplicemente cessano).
- **Link nel Data Graph**: le relazioni tra entità di moduli diversi (task ↔ obiettivo) vivono nel grafo come archi first-class, posseduti dal Core, non dai moduli. Se un modulo viene disattivato, i suoi archi restano "sospesi" e riprendono vita alla riattivazione: **disattivare un modulo non cancella mai dati** (la cancellazione è un'azione separata ed esplicita).
- **Vietato**: chiamate dirette tra moduli, import di modelli altrui, query sulle tabelle di un altro modulo.

### 3.3 Ciclo di vita e aggiornabilità

- I moduli interni sono feature-module compilati nell'app ma **attivabili a runtime** (delivery on-demand dove la piattaforma lo consente: Play Feature Delivery; su iOS lazy-loading di risorse).
- Ogni modulo ha **migrazioni di schema proprie e isolate**, eseguite all'attivazione/aggiornamento. Il Core garantisce l'ordine e il rollback.
- **Versionamento a tre livelli**: app (semver), Module Contract (intero, con finestra di compatibilità N-1), schema entità per modulo (migrazioni lineari). Un modulo dichiara il range di contratto supportato → un modulo aggiornato gira su Core vecchio di una versione e viceversa.

### 3.4 Sistema di plugin (fase 3+)

Lo stesso contratto, esposto a terze parti tramite SDK pubblico. Aggiunte rispetto ai moduli interni: sandbox di esecuzione, permessi granulari approvati dall'utente, review di sicurezza in un registry curato, kill-switch remoto per plugin malevoli. Le API del contratto sono progettate fin da ora "come se" fossero pubbliche: è il modo più economico per assicurare che i confini restino puliti.

## 4. Personal Data Graph

- **Entità**: ogni oggetto (task, spesa, abitudine, nota, evento, obiettivo) ha un ID globale (UUIDv7: ordinabile temporalmente, generabile offline), tipo, versione di schema, payload del modulo, metadati comuni (creazione, modifica, dispositivo di origine).
- **Archi**: relazioni tipizzate (`contributes_to`, `linked_note`, `scheduled_as`…), possedute dal Core.
- **Indice di ricerca**: FTS locale aggiornato in transazione con le scritture; la ricerca è istantanea e offline (C-03).
- **Insight engine**: motore di regole dichiarative che osserva eventi e grafo **esclusivamente on-device**. Le regole sono dati (aggiornabili via config firmata), non codice: nuovi insight senza release.

## 5. Sync Engine (Offline-First)

### 5.1 Principi

1. Il **device è la fonte primaria**; ogni operazione scrive prima in locale (latenza percepita: 0).
2. Un **outbox** persistente accumula i delta CRDT; la sync è opportunistica (rete disponibile, batteria non critica, backoff esponenziale).
3. Il server conserva **blob cifrati per-entità + vettori di versione**; fa merge di metadati di versionamento, mai di contenuti.
4. La riconciliazione avviene sul device alla ricezione; i CRDT garantiscono convergenza senza intervento dell'utente (UC-07).
5. **Sync intelligente** (brief): priorità ai dati "caldi" (oggi/settimana corrente), batch per i dati storici; su rete a consumo, solo delta essenziali.

### 5.2 Backup e ripristino

- Snapshot periodici cifrati (chiave derivata dall'utente, doc 06) su object storage, con generazioni multiple (giornaliero ×7, settimanale ×4, mensile ×6).
- Ripristino su nuovo device: download snapshot + replay dei delta successivi → UC-08 in < 2 minuti su rete media.

## 6. Backend: servizi e scalabilità

| Servizio | Responsabilità | Note di scala |
|----------|----------------|---------------|
| **Auth** | Registrazione, login, sessioni, device registry, 2FA | Stateless + Postgres; token brevi con refresh |
| **Sync** | Ingest/serve delta cifrati, vettori di versione, fan-out push "c'è novità" | Il più caldo: partizionato per utente (sharding per user-id), nessuna transazione cross-utente → scala linearmente |
| **Backup** | Gestione snapshot su object storage | I/O bound; presigned URL per upload diretto |
| **Billing** | Abbonamenti via StoreKit 2 / Play Billing + server notifications | Fonte di verità: ricevute store validate server-side |
| **Module Registry** | Metadati moduli/plugin, versioni minime, kill-switch, config regole insight (firmata) | Contenuti pubblici e cacheabili via CDN |
| **Telemetry** | Eventi anonimi e aggregati (opt-in, doc 06) | Write-only pipeline → warehouse |

- **Capacità**: obiettivo di progetto 5 M utenti registrati / 1 M DAU; il collo di bottiglia è Sync → dimensionato per 10k richieste/s con p99 < 300 ms; tutto lo stato partizionabile per utente.
- **Affidabilità**: multi-AZ, RPO ≤ 15 min, RTO ≤ 1 h; e in ogni caso **l'app resta pienamente usabile durante un'indisponibilità totale del backend** — è il vantaggio strutturale dell'Offline-First.
- **DevOps**: IaC (Terraform), CI/CD con ambienti dev/staging/prod, canary release, feature flag remoti, observability (metriche, tracing, log strutturati senza PII).

## 7. Architettura client (per piattaforma)

- **Pattern UI**: unidirezionale (MVI/UDF) — stato immutabile, eventi espliciti; è il pattern con cui SwiftUI/Compose rendono deterministico il rendering e testabile la logica.
- **Struttura a livelli per modulo**: UI → ViewModel/Presenter → UseCase → Repository (contratto nel KMP core). I moduli dipendono dal Core SDK, mai tra loro (verificato da regole di build: dipendenze illegali = build rossa).
- **Budget prestazioni per modulo** (enforced in CI, dettagli doc 05): contributo all'avvio ≤ 50 ms, memoria ≤ 15 MB attivo, zero jank nelle transizioni.
- **App size**: target ≤ 60 MB iOS / ≤ 40 MB Android all'installazione (moduli on-demand esclusi).

## 8. Decisioni registrate (ADR sintetici)

| # | Decisione | Stato | Conseguenza principale |
|---|-----------|-------|------------------------|
| ADR-1 | UI nativa doppia + core KMP | Accettata | +costo team, +qualità percepita; sync/crypto scritti una sola volta |
| ADR-2 | Backend content-blind (E2E) | Accettata | Niente feature server-side sui contenuti; conformità e fiducia massime |
| ADR-3 | CRDT per la sync | Accettata | Complessità concentrata nel core condiviso; zero dialoghi di conflitto |
| ADR-4 | SQLite + FTS locale | Accettata | Ricerca offline istantanea; portabilità |
| ADR-5 | Module Contract con event bus, niente dipendenze inter-modulo | Accettata | La modularità è garantita dal build system, non dalla disciplina |
| ADR-6 | REST versionato + OpenAPI | Accettata | Doc e client generati; contratti testabili |
| ADR-7 | Niente open banking nell'MVP | Accettata | Rivalutazione a ≥ 100k MAU (vedi doc 08, R-07) |
