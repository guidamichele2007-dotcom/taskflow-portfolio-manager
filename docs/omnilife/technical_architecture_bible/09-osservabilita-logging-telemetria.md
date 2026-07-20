# 09 · Osservabilità, Logging, Telemetria (concettuale)

> Eredita [00](00-principi-architetturali.md)…[08](08-scalabilita-estendibilita.md). Ogni regola di **consenso e contenuto** è già normativa in Product Constitution (Titolo I-II, art. 23, 41) e Functional Bible (MFC §5, C-art. 22-23). Questo documento descrive solo la **collocazione architetturale** dei tre concetti (osservabilità, log, telemetria), tenendoli esplicitamente distinti — confonderli sarebbe già una violazione architetturale, dato che hanno regimi di consenso diversi.

## 1. Tre concetti distinti, tre confini di consenso diversi

| Concetto | Che cosa cattura | Consenso richiesto | Destinazione |
|---|---|---|---|
| **Osservabilità locale** | Stato interno dei componenti per la diagnosi durante l'uso (es. stato del pannello di sync, SYNC-002) | Nessuno (è già una funzione visibile all'utente, non un meccanismo nascosto) | Solo il dispositivo, mostrato in UI (UX Bible, pannello di stato) |
| **Logging locale** | Eventi tecnici e di errore, per la diagnosi da parte dell'utente o del supporto **su richiesta esplicita** | Nessuno per la scrittura locale; consenso esplicito per la condivisione con il supporto | Solo il dispositivo, salvo condivisione esplicita e volontaria |
| **Telemetria** | Eventi comportamentali anonimi e aggregabili, per il miglioramento del prodotto | **Opt-in esplicito**, default off in UE (C-art. 23) | Trasmessa (solo se opt-in) verso un confine distinto da L6 — mai insieme ai dati di sincronizzazione |

**Regola architetturale non negoziabile**: questi tre concetti sono implementati da **componenti separati**, mai da un unico sistema di logging generico che poi filtra per consenso — la separazione fisica dei componenti è la garanzia strutturale che un errore di configurazione non possa far trapelare in telemetria ciò che doveva restare locale (coerente con "mai contenuti utente in log, crash report o telemetria", C-art. 22).

## 2. Collocazione architetturale

```
┌────────────────────────────────────────────────────────────┐
│  Ogni layer (L1…L5) genera, al proprio confine:              │
│    - eventi di osservabilità (sempre)                        │
│    - voci di log locale (sempre, mai contenuti utente)       │
└───────────────────────────┬──────────────────────────────────┘
                             │
              ┌──────────────┴──────────────┐
              ▼                              ▼
   ┌─────────────────────┐      ┌─────────────────────────┐
   │ Componente Log Locale │      │ Componente Telemetria    │
   │ (sempre attivo)       │      │ (attivo solo se opt-in)  │
   │ Destinazione: device  │      │ Destinazione: confine    │
   └─────────────────────┘      │ separato da L6, mai         │
                                  │ insieme ai dati di sync    │
                                  └─────────────────────────┘
```

Il Componente Telemetria **non ha accesso** ai contenuti delle entità di dominio: riceve solo eventi già anonimizzati e aggregabili al momento della generazione (es. "cattura completata, durata X" — mai il testo catturato), un filtro applicato **alla fonte** (in L3/L4, prima che l'evento esista come dato telemetrico), non in transito.

## 3. Che cosa si osserva a ogni layer

| Layer | Osservabilità tipica |
|---|---|
| L1 Esperienza | Tempo di composizione delle schermate, frame persi (per verificare i budget MUC §2: >300ms → stato di caricamento esplicito) |
| L2 Applicazione | Durata delle orchestrazioni multi-modulo (Home, Ricerca, Revisione) |
| L3 Dominio | Violazioni di invarianti intercettate (mai gli invarianti stessi violati silenziosamente), esiti dei casi d'uso |
| L4 Servizi Core | Stato della coda di sincronizzazione (dimensione, età del più vecchio elemento in coda — SYNC-002), esiti delle richieste di notifica (per NTF-006, già un dato di dominio locale, non telemetria), frequenza di attivazione delle regole di Insight |
| L5 Adattatori | Esiti delle chiamate a piattaforma (permesso concesso/negato, disponibilità del provider) — mai il contenuto restituito |

## 4. Audit trail: relazione con l'osservabilità (nessuna duplicazione)

L'audit trail **utente** (cronologia delle entità) è già interamente definito nel [Data Model Bible §14](../data_model_bible/12-audit-permessi-vincoli-invarianti.md#1-audit-trail--modello-consolidato) e vive nel Dominio (L3), non nel sistema di osservabilità: sono due sistemi distinti con scopi distinti (uno racconta "che cosa è cambiato nei miei dati", l'altro "come si comporta il sistema"). L'audit di sicurezza dell'account (tentativi di accesso, dispositivi) vive nel Servizio di Sicurezza (L4) e, per gli eventi di autenticazione, anche lato L6 (mai le password, MFC §5) — coerente con `DM-SYS-02/05`.

## 5. Principio di minimizzazione applicato all'osservabilità

Coerente con C-art. 4, 45 (minimizzazione): ogni componente di osservabilità/log/telemetria cattura **il minimo sufficiente** al proprio scopo dichiarato — non esiste un "log generico verboso" che cattura tutto e filtra dopo. Questo è anche un principio di Interface Segregation ([00 §2](00-principi-architetturali.md)) applicato all'osservabilità: ogni consumer di dati diagnostici (supporto, telemetria aggregata) riceve solo ciò che gli serve, mai un flusso indifferenziato.

## 6. Decisioni esplicitamente rinviate

Il formato esatto delle voci di log, il protocollo di trasmissione della telemetria, lo strumento o servizio di raccolta, la cadenza di campionamento: tutte decisioni di implementazione — questa Bible fissa solo la separazione dei tre componenti e i confini di consenso.

---

*Prossimo: [Sicurezza Architetturale](10-sicurezza-architetturale.md)*
