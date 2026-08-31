# 01 · Information Architecture

> Eredita il [Modello UX Comune](00-modello-ux-comune.md). Mappa **ogni destinazione raggiungibile**: non solo le schermate, ma fogli, dialoghi, widget e link profondi. Ogni nodo ha: identificativo, livello, percorso, modulo, dipendenze, motivazione. Fonte funzionale: [Functional Bible](../functional_bible/README.md).

## 1. Livelli (coerenti con P32 — profondità massima 3)

- **L0**: destinazioni di primo livello, sempre raggiungibili dalla tab bar o dal FAB.
- **L1**: liste/viste di modulo raggiunte da L0.
- **L2**: dettaglio di una singola entità o sotto-vista, raggiunta da L1.
- **Overlay** (non conta come livello, §5 MUC): fogli (bottom sheet), dialoghi, toast/snackbar — sovrapposti, mai impilati oltre 1 alla volta.

## 2. Mappa dei nodi L0 (radici di navigazione)

| ID | Nome | Percorso | Modulo | Dipendenze | Motivazione |
|---|---|---|---|---|---|
| IA-001 | Home "Oggi" | `/` (tab 1) | Core | Moduli attivi | HOME-001; J4 — schermata di apertura di default |
| IA-002 | Moduli (hub) | `/moduli` (tab 2) | Core | GAL | Punto di governo della modularità (P77-84) |
| IA-003 | Cerca | `/cerca` (tab 3) | Core | SRCH | J7 |
| IA-004 | Profilo/Impostazioni | `/profilo` (tab 4) | Core | SET, PROF, SEC | J11, J20 |
| IA-005 | Cattura (FAB) | overlay da ogni schermata | Core | CAPT | J1 — non è una destinazione "in coda", è sempre in primo piano |

## 3. Mappa completa L0→L2 per modulo

### 3.1 Core

| ID | Nome | Livello | Percorso | Origine | Dipendenze | Motivazione |
|---|---|---|---|---|---|---|
| IA-010 | Vista "Prossimi giorni" | L1 | `/oggi/prossimi` | IA-001 (swipe) | HOME-006 | J4 esteso |
| IA-011 | Galleria moduli (catalogo) | L1 | `/moduli/galleria` | IA-002 | GAL-001 | UC-09 |
| IA-012 | Scheda modulo (anteprima) | L2 | `/moduli/galleria/:modulo` | IA-011 | GAL-001/002 | Decisione informata pre-attivazione |
| IA-013 | Revisione settimanale | L1 (flusso sequenziale) | `/revisione` | IA-001 (banner), IA-004 | REV-001…004 | J6 |
| IA-014 | Onboarding — benvenuto | L0 (solo primo avvio) | `/onboarding/benvenuto` | Primo avvio app | ONB-001 | TTV < 60s |
| IA-015 | Onboarding — scelta moduli | L0 | `/onboarding/moduli` | IA-014 | ONB-002 | D-03 |
| IA-016 | Onboarding — prima cattura | L0 | `/onboarding/cattura` | IA-015 | ONB-003 | J1 |

### 3.2 Cattura (overlay universale)

| ID | Nome | Livello | Percorso | Origine | Dipendenze | Motivazione |
|---|---|---|---|---|---|---|
| IA-020 | Foglio di cattura | Overlay | modale da ogni schermata | FAB (IA-005), widget, share sheet | CAPT-001/005 | J1-J3 |
| IA-021 | Inbox catture ambigue | L1 | `/inbox` | Badge Home, REV | CAPT-008 | Nessuna cattura va persa |
| IA-022 | Scorciatoie di cattura (long-press FAB) | Overlay | menu radiale | FAB | CAPT-007 | Riduzione tocchi |

### 3.3 Attività

| ID | Nome | Livello | Percorso | Origine | Dipendenze | Motivazione |
|---|---|---|---|---|---|---|
| IA-030 | Vista Oggi (Attività) | L1 | `/attivita/oggi` | IA-002, HOME | TASK-012 | Vista primaria del modulo |
| IA-031 | Vista Prossimi | L1 | `/attivita/prossimi` | IA-030 (segmented) | TASK-012 | — |
| IA-032 | Vista Tutti/Liste | L1 | `/attivita/liste` | IA-030 (segmented) | TASK-005 | P32 |
| IA-033 | Dettaglio lista | L2 | `/attivita/liste/:id` | IA-032 | TASK-005 | — |
| IA-034 | Sezione "In sospeso" | L1 | `/attivita/in-sospeso` | IA-030 (sezione) | TASK-014 | HOME-R-01 |
| IA-035 | Dettaglio task (foglio) | Overlay | espande da qualunque lista | IA-030/031/032/033 | TASK-001…011 | GEF (MUC §9) |
| IA-036 | Time-boxing (drag su agenda) | Overlay su IA-050 | — | IA-035 | TASK-016 | CAL-005 |

### 3.4 Finanze

| ID | Nome | Livello | Percorso | Origine | Dipendenze | Motivazione |
|---|---|---|---|---|---|---|
| IA-040 | Panoramica Finanze | L1 | `/finanze` | IA-002, HOME | FIN-005/007 | Budget + report in un colpo d'occhio |
| IA-041 | Storico transazioni | L1 | `/finanze/storico` | IA-040 | FIN-010 | J7 nel dominio finanze |
| IA-042 | Dettaglio transazione | Overlay | espande da lista | IA-041 | FIN-011 | GEF |
| IA-043 | Gestione conti | L1 | `/finanze/conti` | IA-040 | FIN-003/004 | — |
| IA-044 | Dettaglio conto | L2 | `/finanze/conti/:id` | IA-043 | FIN-003 | — |
| IA-045 | Gestione budget | L1 | `/finanze/budget` | IA-040 | FIN-005 | — |
| IA-046 | Dettaglio/modifica budget | Overlay | — | IA-045 | FIN-005 | — |
| IA-047 | Gestione categorie | L1 | `/finanze/categorie` | IA-040 (impostazioni modulo) | FIN-002 | — |
| IA-048 | Report mensile | L1 | `/finanze/report` | IA-040 | FIN-007 | J6 |
| IA-049 | Obiettivi di risparmio | L1 | `/finanze/risparmi` | IA-040 | FIN-008 | ponte a GOAL |
| IA-04A | Import CSV | L1 (flusso) | `/finanze/import` | IA-041 (impostazioni) | FIN-012 | — |

### 3.5 Abitudini

| ID | Nome | Livello | Percorso | Origine | Dipendenze | Motivazione |
|---|---|---|---|---|---|---|
| IA-060 | Vista Abitudini (oggi) | L1 | `/abitudini` | IA-002, HOME | HAB-001…006 | J8 |
| IA-061 | Dettaglio abitudine | Overlay | espande da lista | IA-060 | HAB-001…013 | GEF + resilienza |
| IA-062 | Griglia storica abitudine | L2 | `/abitudini/:id/storico` | IA-061 | HAB-009 | J16 |
| IA-063 | Tutte le abitudini (gestione) | L1 | `/abitudini/gestisci` | IA-060 | — | Riordino, pausa, archivio |

### 3.6 Calendario

| ID | Nome | Livello | Percorso | Origine | Dipendenze | Motivazione |
|---|---|---|---|---|---|---|
| IA-050 | Agenda (giorno) | L1 | `/calendario/giorno` | IA-002, HOME | CAL-002 | J4/J5 |
| IA-051 | Agenda (settimana) | L1 | `/calendario/settimana` | IA-050 (swipe/segmented) | CAL-002 | — |
| IA-052 | Vista mensile | L1 | `/calendario/mese` | IA-050 | CAL-007 | — |
| IA-053 | Dettaglio evento | Overlay | espande da agenda | IA-050/051/052 | CAL-003 | Sola lettura+ |
| IA-054 | Selezione calendari da mostrare | L1 (impostazioni modulo) | `/calendario/impostazioni` | IA-050 | CAL-001 | Permessi C-art. 32 |

### 3.7 Note

| ID | Nome | Livello | Percorso | Origine | Dipendenze | Motivazione |
|---|---|---|---|---|---|---|
| IA-070 | Elenco note (pin + recenti) | L1 | `/note` | IA-002, HOME | NOTE-004 | — |
| IA-071 | Editor nota | L2 | `/note/:id` | IA-070 | NOTE-001/002 | J1 |
| IA-072 | Archivio note | L1 | `/note/archivio` | IA-070 | NOTE-005 | — |
| IA-073 | Cronologia versioni nota | Overlay | — | IA-071 | NOTE-006 | GEF adattato |
| IA-074 | Collegamenti della nota | Overlay | — | IA-071 | NOTE-003 | J17 |

### 3.8 Salute

| ID | Nome | Livello | Percorso | Origine | Dipendenze | Motivazione |
|---|---|---|---|---|---|---|
| IA-080 | Riepilogo Salute (oggi) | L1 | `/salute` | IA-002, HOME | HLTH-002 | J8/J17 |
| IA-081 | Storico e tendenze | L1 | `/salute/storico` | IA-080 | HLTH-005 | — |
| IA-082 | Metriche manuali (form rapido) | Overlay | — | IA-080, FAB | HLTH-004 | J17 |
| IA-083 | Collegamenti abitudini↔salute | L1 (impostazioni modulo) | `/salute/collegamenti` | IA-080 | HLTH-003 | — |

### 3.9 Obiettivi

| ID | Nome | Livello | Percorso | Origine | Dipendenze | Motivazione |
|---|---|---|---|---|---|---|
| IA-090 | Elenco obiettivi | L1 | `/obiettivi` | IA-002, HOME | GOAL-008 | J15 |
| IA-091 | Dettaglio obiettivo (fronti) | L2 | `/obiettivi/:id` | IA-090 | GOAL-002/003 | Feature-firma |
| IA-092 | Collega contributo | Overlay | — | IA-091 | GOAL-002 | Grafo |
| IA-093 | Creazione obiettivo | Overlay | — | IA-090, FAB | GOAL-001 | — |

### 3.10 Ricerca, Notifiche, Widget, Impostazioni

| ID | Nome | Livello | Percorso | Origine | Dipendenze | Motivazione |
|---|---|---|---|---|---|---|
| IA-100 | Ricerca globale | L1 | `/cerca` | Tab 3 | SRCH-001 | J7 |
| IA-101 | Risultati filtrati | L1 (stato) | `/cerca?tipo=` | IA-100 | SRCH-002/003 | — |
| IA-110 | Centro notifiche | L1 | `/notifiche` | Icona campanella (IA-001) | NTF-007 | Trasparenza |
| IA-111 | Impostazioni notifiche | L2 | `/profilo/notifiche` | IA-004 | NTF-002/003/004 | Governo del budget |
| IA-120 | Configurazione widget | Overlay di sistema | fuori app (OS) | Home screen OS | WID-001…004 | — |
| IA-130 | Impostazioni (indice) | L1 | `/profilo` | Tab 4 | SET-001 | — |
| IA-131 | Account e sicurezza | L2 | `/profilo/sicurezza` | IA-130 | SEC-001/002/003 | J11 |
| IA-132 | Aspetto | L2 | `/profilo/aspetto` | IA-130 | SET-001 | — |
| IA-133 | Dati (export/backup/import) | L2 | `/profilo/dati` | IA-130 | EXP, BKP | J14 |
| IA-134 | Pagina "I tuoi dati" | L2 | `/profilo/i-tuoi-dati` | IA-133 | SET-002 | Trasparenza C-art. 6 |
| IA-135 | Abbonamento | L2 | `/profilo/abbonamento` | IA-130 | SET-003 | — |
| IA-136 | Registro dispositivi | L2 | `/profilo/dispositivi` | IA-131 | SYNC-003 | J12 |
| IA-137 | Cestino (globale, filtrabile per modulo) | L1 | `/cestino` | IA-130, ogni modulo | MFC-R-10 | J12 |
| IA-138 | Aiuto e supporto | L2 | `/profilo/aiuto` | IA-130 | SET-004 | — |
| IA-139 | Cancellazione account | L2 (flusso) | `/profilo/sicurezza/cancella` | IA-131 | EXP-003 | C-art. 9 |

## 4. Deep link e Universal link (destinazioni raggiungibili dall'esterno)

| ID | Schema | Destinazione | Motivazione |
|---|---|---|---|
| IA-DL-01 | `omnilife://capture?text=` | IA-020 precompilato | Share sheet di sistema (CAPT-009) |
| IA-DL-02 | `omnilife://task/:id` | IA-035 | Da notifica (NTF-005) o widget |
| IA-DL-03 | `omnilife://habit/:id` | IA-061 | Da notifica/widget |
| IA-DL-04 | `omnilife://goal/:id` | IA-091 | Da digest (INS-002) |
| IA-DL-05 | `omnilife://review` | IA-013 | Da promemoria REV-004 |
| IA-DL-06 | `https://app.omnilife.com/invite/:code` | Onboarding o Impostazioni referral | Universal link, funziona anche senza app installata (store) |
| IA-DL-07 | `omnilife://module/:id/activate` | IA-012 | Da suggerimento contestuale GAL-004 |

**UX-R-013**: ogni deep link verso un'entità che non esiste più (eliminata/cestinata da altro device) mostra un messaggio gentile e reindirizza alla lista del modulo — mai una schermata rotta.

## 5. Grafo delle dipendenze di navigazione (sintesi)

```
Onboarding (IA-014→016, solo prima volta)
        │
        ▼
   Home "Oggi" (IA-001) ──┬── Moduli (IA-002) ──┬── Galleria (IA-011→012)
        │ (tab bar)        │                     └── [ogni modulo L1→L2]
        ├── Cerca (IA-100→101)
        ├── Profilo (IA-130) ── Sicurezza/Aspetto/Dati/Abbonamento/Dispositivi/Cestino/Aiuto
        │
   FAB Cattura (IA-020, overlay sempre raggiungibile) ──► Inbox (IA-021) se ambigua
```

**UX-R-014**: nessun nodo L1 è raggiungibile solo da un altro modulo (ogni modulo ha ingresso diretto da IA-002 o da una card Home) — coerente con P77 (ogni modulo sta in piedi da solo).

---

*Prossimo: [Navigation Bible](02-navigation-bible.md)*
