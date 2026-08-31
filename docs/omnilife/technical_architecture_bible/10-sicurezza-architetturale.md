# 10 · Sicurezza Architetturale

> Eredita [00](00-principi-architetturali.md)…[09](09-osservabilita-logging-telemetria.md). Le regole di sicurezza sono già normative in Product Constitution (Titolo II, art. 26-50) e Functional Bible (MFC §5, moduli SEC/SET). Questo documento descrive **dove nell'architettura vive ogni confine di fiducia** — non un meccanismo crittografico, non una libreria, non un protocollo.

## 1. Il confine di fiducia come struttura architetturale, non come policy

**Ogni confine tra layer definito in [01](01-architettura-generale-e-layer.md) è anche un confine di fiducia esplicito.** Questo è il principio da cui tutto il resto deriva: la promessa "il server non può leggere i tuoi dati" (Product Constitution art. 1-2, 27) è vera solo se **nessun percorso architetturale** permette a dati in chiaro di attraversare il confine L5→L6. Non è una regola di comportamento del backend: è una proprietà strutturale del client, verificabile indipendentemente da che cosa esiste dall'altra parte del confine.

```
                       CONFINE DI FIDUCIA PRINCIPALE
                                    │
  ┌─────────────────────────────────┼──────────────────────────────┐
  │  ZONA FIDATA (dispositivo)       │   ZONA NON FIDATA (esterna)    │
  │                                  │                                 │
  │  L1 Esperienza                   │                                 │
  │  L2 Applicazione                 │                                 │
  │  L3 Dominio (dati in chiaro,     │                                 │
  │     protetti dallo sblocco       │                                 │
  │     locale — MFC-R-21)           │                                 │
  │  L4 Servizi Core                 │                                 │
  │     └─ Servizio Sicurezza:       │                                 │
  │        cifra PRIMA di consegnare │                                 │
  │        a L5 ─────────────────────┼──► L5 Adattatore Sync ──►  L6   │
  │                                  │      (mai vede testo in chiaro) │
  └──────────────────────────────────┴────────────────────────────────┘
```

## 2. Sotto-confini di fiducia (non solo il confine esterno)

La sicurezza architetturale di OmniLife non ha un solo confine, ne ha quattro, ciascuno con una responsabilità diversa:

| Confine | Che cosa separa | Componente responsabile |
|---|---|---|
| **App bloccata ↔ App sbloccata** | Visibilità dei moduli sensibili sullo schermo (non i dati stessi, che restano cifrati a riposo indipendentemente) | Servizio di Sicurezza (L4), verificato da L1 prima di rendere visibile un contenuto (MFC-R-21/22) |
| **Dispositivo ↔ Confine di sincronizzazione (L5→L6)** | Testo in chiaro vs blob cifrati | Servizio di Sicurezza (L4): unico punto architetturale autorizzato a produrre il testo cifrato che L5 trasmette |
| **App ↔ Piattaforma esterna (Calendario/Salute)** | Dati nostri vs dati di cui non siamo proprietari | Adattatori dedicati (L5): il Dominio Calendario/Salute non tratta mai questi dati come propri (Data Model Bible §9, deroghe) |
| **App ↔ Telemetria** | Dati di dominio vs eventi anonimi aggregabili | Componente Telemetria (§[09](09-osservabilita-logging-telemetria.md)), separato architetturalmente dal logging locale |

## 3. Gerarchia delle chiavi: collocazione, non algoritmo

Il Servizio di Sicurezza (L4) è l'unico componente architetturale a conoscenza della gerarchia di cifratura (credenziali → chiave madre → chiavi per dominio, già descritta concettualmente dal Product Bible, Constitution art. 27-28, 48; SEC-002 per la chiave di recupero). Nessun altro componente — né i Domini di modulo (L3), né gli Adattatori (L5), né tantomeno L1/L2 — ha accesso diretto a materiale crittografico: **ogni Dominio delega sempre al Servizio di Sicurezza** la cifratura/decifratura, non implementa la propria.

- `DM-SYS-05 RecoveryKeyMetadata` (Data Model Bible) vive coerentemente in L3 (entità di sistema) ma **non contiene mai la chiave stessa** — solo i suoi metadati di ciclo di vita, per costruzione (nessun componente architetturale, nemmeno il Dominio Account, possiede la chiave in una forma persistita).
- Il Servizio di Sicurezza espone al resto del sistema solo operazioni ("cifra questo blob", "verifica questa firma"), mai la chiave stessa come valore condiviso — Interface Segregation applicata alla sicurezza.

## 4. Autorizzazione: due modelli distinti, mai confusi (coerente con Data Model Bible §15)

| Modello | Che cosa autorizza | Dove vive |
|---|---|---|
| **Sblocco locale** (biometria/codice) | Visibilità sullo schermo di moduli marcati sensibili | Servizio di Sicurezza (L4), consultato da L1 prima del render |
| **Consenso di sistema** (permessi OS) | Lettura/scrittura verso Calendario, Salute, notifiche, microfono | Adattatori di piattaforma (L5), il cui stato di consenso è consultato dal Dominio proprietario (L3) prima di ogni chiamata |

Nessun terzo modello (permessi multi-utente a grana fine) esiste nell'architettura MVP/v1.x, coerente con la decisione rinviata già registrata nel Data Model Bible (§15, spazi condivisi fase 4) — questa Bible non introduce anticipatamente una struttura per un modello di permessi non ancora specificato funzionalmente.

## 5. Il principio "content-blind" applicato a ogni Servizio Core

Non solo il confine L6: **ogni Servizio Core che potrebbe, per errore di design, finire per aggregare contenuti di più utenti o inviarli altrove, è escluso per costruzione dall'architettura**:

- Il Motore di Insight (L4) **non ha porta verso la rete** — è un vincolo architetturale, non una policy (Functional Bible, INS-003: "il motore non ha accesso alla rete: vincolo architetturale, non policy" — principio qui esteso a livello di Servizio Core).
- Il Servizio di Ricerca (L4) opera **esclusivamente in locale**: nessuna porta verso L6 esiste nel suo contratto (SRCH-R-02).
- La Cache della piattaforma Salute (L5, [05 §4](05-offline-first-sincronizzazione-caching.md)) non ha percorso architetturale verso il Motore di Sync: è strutturalmente isolata dal resto di L4 che comunica con L6.

## 6. Sicurezza e resilienza: il fallimento di un componente non deve compromettere il confine

Coerente con [07 §3](07-gestione-errori.md) (non propagazione a cascata): un errore nel Servizio di Ricerca o nel Motore di Insight non ha mai un percorso, nemmeno in condizione di errore, per raggiungere L6 — l'assenza strutturale della porta (§5) è più forte di qualunque controllo a runtime, perché non richiede che il controllo funzioni correttamente per essere efficace.

## 7. Decisioni esplicitamente rinviate

Algoritmo di cifratura specifico, libreria crittografica, formato dei certificati, meccanismo esatto di attestazione dell'enclave hardware, protocollo di scambio chiavi: tutte decisioni di implementazione — questa Bible fissa **quali componenti hanno il diritto di vedere che cosa**, non **con quale algoritmo lo proteggono**.

---

*Prossimo: [Versionamento dell'Architettura](11-versionamento-architettura.md)*
