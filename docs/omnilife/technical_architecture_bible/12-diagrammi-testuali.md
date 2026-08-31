# 12 · Diagrammi Testuali dell'Architettura

> Consolida in un solo luogo i diagrammi già introdotti nei documenti precedenti (con rimando) più due diagrammi di sintesi nuovi (vista d'insieme, ciclo di vita di un modulo). Nessun diagramma grafico: solo notazione testuale, come richiesto.

## 1. Vista d'insieme a sei layer

*(Diagramma completo con motivazione: [01 §2](01-architettura-generale-e-layer.md#2-i-sei-layer-logici))*

```
L1 Esperienza  →  L2 Applicazione  →  L3 Dominio (moduli)  →  L4 Servizi Core  →  L5 Adattatori  →  L6 Confine esterno
   (UX Bible)      (orchestrazione)     (Data Model Bible)     (grafo, sync,        (piattaforma)     (content-blind)
                                                                 ricerca, notifiche,
                                                                 insight, sicurezza)
```

## 2. Confini di modulo e canali di comunicazione ammessi

*(Motivazione completa: [02](02-moduli-responsabilita-boundaries.md), [03 §7](03-event-driven-architecture.md#7-comunicazione-tra-moduli-solo-due-canali-mai-un-terzo))*

```
        ┌──────────┐        ┌──────────┐        ┌──────────┐
        │ Attività │        │ Finanze  │        │Abitudini │   ... ogni modulo isolato
        └────┬─────┘        └────┬─────┘        └────┬─────┘
             │                   │                   │
             │  MAI import diretto tra moduli (✗)     │
             │                   │                   │
             ▼                   ▼                   ▼
        ┌─────────────────────────────────────────────────┐
        │            BUS EVENTI (L4)  —  fatti, effimero    │
        └─────────────────────────────────────────────────┘
             ▲                   ▲                   ▲
             │                   │                   │
        ┌─────────────────────────────────────────────────┐
        │      GRAFO / GraphLink (L4)  —  relazioni,        │
        │      persistenti, posseduto dal Core               │
        └─────────────────────────────────────────────────┘
                             ▲
                             │ orchestrazione di sola lettura
                        ┌────┴────┐
                        │   L2    │  (Home, Ricerca, Revisione)
                        └─────────┘
```

## 3. Lifecycle di una richiesta (comando su singola entità)

*(Diagramma completo: [06 §2](06-lifecycle-richieste-ed-eventi.md#2-lifecycle-generico-di-una-richiesta-comando-su-singola-entità))*

```
Intenzione (L1) → Invocazione (L1→L2) → Instradamento (L2→L3) →
Validazione (L3) → Effetto (L3, persistenza locale-prima) →
Conferma (L3→L2→L1, ≤50ms percepiti) ⇉ [in parallelo, non bloccante]
   → Pubblicazione evento (L3→L4)
   → Accodamento sync (L4)
   → Aggiornamento indice ricerca (L4)
```

## 4. Lifecycle di un evento

*(Diagramma completo: [03 §4](03-event-driven-architecture.md#4-lifecycle-di-un-evento-attraverso-i-layer))*

```
Accadimento (L3) → Pubblicazione (L3→Bus L4) → Distribuzione
(Bus L4 → ogni sottoscrittore ATTIVO, verifica Registro Moduli) →
Reazione (0..N consumer in L3, in parallelo logico) →
[eventuale] Ri-pubblicazione (nuovo evento, catena aciclica per costruzione)
```

## 5. Flusso di sincronizzazione (offline-first)

*(Diagramma completo: [05 §2](05-offline-first-sincronizzazione-caching.md#2-il-motore-di-sincronizzazione-come-servizio-core-l4))*

```
Dominio (L3) scrive localmente
        │
        ▼
Porta di Persistenza (L4/L5) — MFC-R-01: locale-prima, transazionale
        │  osservato da
        ▼
Motore di Sync (L4) — outbox persistente, priorità dati "caldi"
        │  quando la rete è disponibile
        ▼
Adattatore di Sync (L5) — riceve SOLO dopo cifratura da parte del
        │                  Servizio di Sicurezza (L4)
        ▼
L6 Confine esterno (content-blind) — blob cifrati + metadati di versione
```

## 6. Confine di fiducia e sotto-confini di sicurezza

*(Diagramma completo: [10 §1-2](10-sicurezza-architetturale.md#1-il-confine-di-fiducia-come-struttura-architetturale-non-come-policy))*

```
ZONA FIDATA (dispositivo)                    │  ZONA NON FIDATA (esterna)
  L1 → L2 → L3 (dati in chiaro, protetti      │
  dallo sblocco locale) → L4 (Servizio        │
  Sicurezza cifra) ────────────────────────────┼──► L5 → L6
                                               │
  Sotto-confini interni al dispositivo:        │
  - App bloccata ↔ sbloccata (moduli sensibili)│
  - App ↔ Calendario/Salute (dati non nostri)  │
  - App ↔ Telemetria (solo eventi anonimi)     │
```

## 7. Ciclo di vita di un modulo (attivazione/disattivazione)

*(Diagramma completo: [04 §3](04-plugin-architecture.md#3-ciclo-di-vita-architetturale-di-un-modulo))*

```
DICHIARATO ──(GAL-002)──► ATTIVO ──(GAL-003)──► DISATTIVATO
                             ▲                        │
                             └────── riattivazione ────┘
   (le entità del modulo, in ogni stato "non attivo", restano nel
    Data Model, sospese nel Grafo — mai eliminate, MFC-R-13)
```

## 8. Vista consolidata: dalla Home al Dominio e ritorno (esempio end-to-end)

```
                     UTENTE APRE L'APP
                              │
                              ▼
   L1 Home ── richiede composizione ──► L2 Applicazione
                              │
        (fan-out di sola lettura verso i moduli ATTIVI)
                              │
        ┌─────────────┬──────┴──────┬─────────────┐
        ▼             ▼             ▼             ▼
   L3 Attività   L3 Finanze   L3 Abitudini   L3 Calendario
   (proiezione)  (proiezione) (proiezione)   (proiezione, via
                                              L5 Adattatore
                                              Calendario)
        └─────────────┴──────┬──────┴─────────────┘
                              │  (fan-in, composizione ordinata)
                              ▼
                     L2 restituisce la vista aggregata
                              │
                              ▼
                     L1 anima e mostra la Home
```

---

*Prossimo: [Matrici](13-matrici.md)*
