# 03 · Dipendenze, Grafo, Percorso Critico

> Eredita [00](00-metodo-tracciabilita-definizioni.md), [01](01-mvp-scope-e-backlog.md), [02](02-matrici-epic-feature-story-task.md). Le dipendenze **funzionali** (quale funzione richiede quale) sono già dichiarate in [Functional Bible, colonna "Dipendenze" di ogni scheda](../functional_bible/README.md); le dipendenze **architetturali** (quale layer/servizio deve esistere prima) sono in [Technical Architecture Bible §02/§13](../technical_architecture_bible/13-matrici.md). Questo documento le combina in un grafo a livello di Epic (la granularità utile per sequenziare il lavoro) e ne estrae il percorso critico.

## 1. Grafo delle dipendenze (testuale, a livello di Epic)

```
EPIC-00 (Fondamenta)
   │  blocca tutto — nessun Epic può iniziare la propria Story T1/T2 prima
   │  che ENG-00-1…3 siano completi (scaffold + Core Services + modello dati)
   ▼
EPIC-SET (Account e Sicurezza, sotto-feature "Account e Sicurezza")
   │  ENG-00-5 + SEC-001/002 devono esistere prima che EPIC-SYNC possa
   │  cifrare qualunque dato (Technical Architecture Bible §10)
   ▼
EPIC-CAPT (Cattura)
   │  CAPT-004 (parser) è dipendenza dichiarata di TASK-001, FIN-001,
   │  HAB-001 "creazione da cattura" — ma ogni modulo può comunque
   │  offrire creazione diretta (CMP-SHEET "+") senza attendere il parser
   │  completo: la dipendenza è FORTE per l'esperienza completa, DEBOLE
   │  per lo sviluppo (i moduli possono procedere in parallelo con
   │  un'interfaccia di creazione minima, poi integrare CAPT)
   ▼
┌──────────────┬──────────────┬──────────────┐
│  EPIC-TASK   │  EPIC-FIN    │  EPIC-HAB    │   nessuna dipendenza reciproca
│  (pilota)    │              │              │   (Technical Architecture Bible
└──────┬───────┴──────┬───────┴──────┬───────┘   §02 §3: nessun modulo dipende
       │              │              │           da un altro modulo)
       ▼              ▼              ▼
   EPIC-CORE (Home) ── richiede almeno 1 modulo con contributo Home pronto
       │
       ├──► EPIC-SRCH (Ricerca) ── richiede entità indicizzabili da ≥1 modulo
       ├──► EPIC-NTF (Notifiche) ── richiede richieste di notifica da ≥1 modulo
       ├──► EPIC-WID (Widget) ── richiede casi d'uso di L2 già esposti
       └──► EPIC-SYNC (Sync/Backup/Export) ── richiede entità di ≥1 modulo
                 │                              da sincronizzare
                 ▼
            [ALPHA] ────► [BETA] ────► [SOFT LAUNCH — fine Release 1.0]
                 │
                 ▼
       EPIC-CAL (lettura, Must residuo) ── indipendente, può slittare
       senza bloccare Alpha/Beta se necessario (priorità M ma isolato)
                 │
                 ▼
        Release 1.x: EPIC-NOTE, EPIC-GOAL (dipende da EPIC-TASK/FIN/HAB
        via GraphLink, ma solo in lettura — non li blocca), EPIC-HLTH,
        EPIC-INS (osservatore passivo di tutti gli altri — naturalmente
        ultimo)
```

## 2. Dipendenze esplicite (tabella, non solo diagramma)

| Da (Epic/Feature) | Richiede prima | Tipo | Fonte |
|---|---|---|---|
| Ogni Epic | EPIC-00 completo (almeno ENG-00-1…3) | Bloccante | Technical Architecture Bible §01 (layer) |
| EPIC-SYNC | EPIC-00 (ENG-00-5, confine di sicurezza) | Bloccante | Technical Architecture Bible §10 |
| EPIC-CORE / Home | Almeno un Epic modulo con contributo Home (§HOME-002) pronto | Bloccante parziale | Functional Bible HOME-002 |
| EPIC-CORE / Onboarding | GAL-002 (EPIC-CORE / Galleria) e almeno un modulo attivabile | Bloccante | ONB-002 dipendenza dichiarata "GAL" |
| EPIC-CORE / Revisione | EPIC-TASK, EPIC-FIN, EPIC-HAB (legge scaduti/budget/aderenza) | Bloccante | REV-001 dipendenze |
| EPIC-SRCH | Almeno un modulo con entità indicizzabili | Bloccante parziale | SRCH-001 |
| EPIC-NTF | Almeno un modulo che richieda notifiche (TASK-003, HAB-006, FIN-005) | Bloccante parziale | NTF-001 |
| EPIC-WID | EPIC-CAPT (CAPT-002), EPIC-TASK/HAB per i widget Oggi/Abitudini | Bloccante parziale | WID-001/002 |
| EPIC-TASK / Feature "Integrazione Grafo e Calendario" (TASK-015/016) | EPIC-GOAL, EPIC-CAL | Debole (le Story restano "pronte" ma incomplete finché l'altro Epic non esiste) | TASK-015/016 dipendenze |
| EPIC-GOAL | ENG-00-2 (GraphLink) + almeno 2 moduli contributori (Attività, Finanze o Abitudini) | Bloccante | GOAL-002 |
| EPIC-HLTH / Feature "Lettura e Auto-completamento" | EPIC-HAB (HAB-010) | Debole (Salute può esporre le sole metriche manuali senza Abitudini) | HLTH-003 |
| EPIC-INS | Eventi pubblicati da ogni altro modulo attivo | Bloccante parziale (osservatore) | INS-003 |

## 3. Percorso Critico (Critical Path)

Il percorso critico è la sequenza di Epic/Feature che, se ritardata, ritarda l'intero rilascio 1.0 (soft launch). Calcolato sulle dipendenze bloccanti di §2, non su quelle deboli.

```
EPIC-00 (ENG-00-1…3)
   → EPIC-SET / Account e Sicurezza (SEC-001/002, prerequisito di Sync)
   → EPIC-TASK (Epic pilota: prova l'intera catena architetturale end-to-end
     su un modulo reale — la Feature "Creazione e Gestione Base" è la più
     critica in assoluto, perché valida lo scaffold per tutti gli altri moduli)
   → EPIC-CORE / Home "Oggi" (richiede Attività come primo contributore)
   → EPIC-CORE / Onboarding (richiede Home + Galleria)
   → EPIC-SYNC / Sincronizzazione (richiede entità reali di Attività da
     sincronizzare, e il confine di sicurezza di EPIC-SET)
   → EPIC-SYNC / Backup e Ripristino (BKP-003 è criterio di uscita Fase 1,
     doc 09-piano-di-sviluppo)
   → [ALPHA]
   → EPIC-FIN + EPIC-HAB (possono procedere in PARALLELO da subito dopo
     EPIC-00, ma sul percorso critico "solo" come gate di completezza
     prima di Beta — non bloccano Alpha, che può iniziare con Attività sola,
     coerente con Alpha "50-100 utenti" del piano di fase esistente)
   → EPIC-SRCH + EPIC-NTF + EPIC-WID (Must residui, paralleli tra loro)
   → [BETA]
   → EPIC-CAL (lettura) — può slittare oltre Beta se necessario senza
     bloccare, essendo isolato (§2)
   → [SOFT LAUNCH — chiusura Release 1.0]
```

**Il nodo critico più stretto**: `EPIC-00 → EPIC-SET (sicurezza) → EPIC-TASK (pilota) → EPIC-CORE/Home → EPIC-SYNC`. Ogni ritardo su questi cinque anelli si propaga 1:1 sulla data di Alpha — coerente con la scelta già registrata nel piano di fase esistente di affrontare per primo il rischio tecnico più alto (R-10, motore di sincronizzazione).

## 4. Perché EPIC-TASK è l'Epic pilota (non una scelta arbitraria)

Tre motivazioni convergenti, tutte già presenti nella documentazione esistente: (a) Attività è il modulo con la sequenza T8-T14 più precoce nel piano di fase esistente; (b) Attività ha zero dipendenze da altri moduli di dominio ma è dipendenza (debole) di Calendario, Obiettivi e Revisione — costruirlo per primo massimizza quanti altri Epic può sbloccare; (c) la sua Feature "Ricorrenze" (TASK-004) esercita la parte più delicata del modello di sincronizzazione ([Data Model Bible §11 §3](../data_model_bible/11-versionamento-e-sincronizzazione.md): generazione pigra, idempotenza per periodo) — se lo scaffold regge questa Feature, regge la maggior parte dei casi degli altri moduli.

---

*Prossimo: [Roadmap, Milestone, Release Plan](04-roadmap-milestone-release-plan.md)*
