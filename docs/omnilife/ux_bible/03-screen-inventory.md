# 03 · Screen Inventory

> Eredita [MUC](00-modello-ux-comune.md) e [IA](01-information-architecture.md). Elenco completo delle schermate (corrispondenti ai nodi IA di tipo schermata/foglio principale — i dialoghi minori sono nel [MUC §8](00-modello-ux-comune.md#8-pattern-universale-di-annullamento-undo) e in [10-error-experience](10-error-experience.md)). Formato: ID (= ID IA) · Nome · Scopo · Funzioni disponibili · Origine · Destinazioni · Dipendenze · Modulo · Principi · JTBD.

## Core

| ID | Nome | Scopo | Funzioni | Origine | Destinazioni | Modulo | Principi | JTBD |
|---|---|---|---|---|---|---|---|---|
| IA-001 | Home "Oggi" | Risposta in 0 tocchi a "cosa conta oggi" | HOME-001…008 | App launch, tab bar | Ogni dettaglio via card, IA-002/003/004 | Core | P25,54,82 | J4 |
| IA-010 | Prossimi giorni | Sbirciare avanti senza pianificare | HOME-006 | Swipe da IA-001 | IA-035/053/061 | Core | P82 | J4 |
| IA-002 | Moduli (hub) | Governare quali moduli sono attivi | GAL-002/003 | Tab bar | IA-011, ogni modulo L1 | Core | P77-84 | J18 |
| IA-011 | Galleria (catalogo) | Scoprire e attivare nuovi moduli | GAL-001/002 | IA-002 | IA-012 | Core | P36 | J18 |
| IA-012 | Scheda modulo | Decidere con informazione completa | GAL-001, permessi dichiarati | IA-011, GAL-004, IA-DL-07 | Attivazione → modulo L1 | Core | C-art.32 | J18 |
| IA-013 | Revisione settimanale | Decidere in sequenza guidata | REV-001…004 | Banner Home, promemoria | Ritorno a IA-001 | Core | P28,52 | J6 |
| IA-014-016 | Onboarding (3 schermate) | TTV < 60s | ONB-001…007 | Primo avvio | IA-001 | Core | P15,17 | J1 |
| IA-021 | Inbox | Smistare catture ambigue | CAPT-008 | Badge Home, REV | GEF verso ogni modulo | Core/Cattura | J1 | J1 |

## Cattura (overlay universale, presente da ogni schermata)

| ID | Nome | Scopo | Funzioni | Origine | Destinazioni | Modulo | Principi | JTBD |
|---|---|---|---|---|---|---|---|---|
| IA-020 | Foglio di cattura | Scaricare un pensiero in ≤3s | CAPT-001…006,009,010 | FAB, widget, share, voce | Entità creata nel modulo pertinente o IA-021 | Cattura | P13,14,18 | J1,J2,J3 |
| IA-022 | Scorciatoie (long-press FAB) | Saltare la scelta del tipo | CAPT-007 | Long-press FAB | IA-020 precompilato | Cattura | P21 | J3 |

## Attività

| ID | Nome | Scopo | Funzioni | Origine | Destinazioni | Modulo | Principi | JTBD |
|---|---|---|---|---|---|---|---|---|
| IA-030 | Vista Oggi (Attività) | I task di oggi | TASK-012,013 | IA-002, HOME | IA-035 | Attività | P32 | J1,J4 |
| IA-031 | Prossimi | Pianificazione a breve | TASK-012 | Segmented da IA-030 | IA-035 | Attività | — | J4 |
| IA-032 | Liste | Organizzazione per area | TASK-005 | Segmented da IA-030 | IA-033 | Attività | P32 | J7 |
| IA-033 | Dettaglio lista | Task di una lista | TASK-005,013,018 | IA-032 | IA-035 | Attività | — | J7 |
| IA-034 | In sospeso | Scaduti raggruppati, mai mescolati | TASK-014 | Sezione IA-030 | IA-035 | Attività | P46; HOME-R-01 | J6 |
| IA-035 | Dettaglio task | Vedere/modificare tutto di un task | TASK-001…011 | Da ogni lista | Chiusura = ritorno | Attività | P26,27,33 | J1,J15 |

## Finanze

| ID | Nome | Scopo | Funzioni | Origine | Destinazioni | Modulo | Principi | JTBD |
|---|---|---|---|---|---|---|---|---|
| IA-040 | Panoramica Finanze | Stato budget in un colpo d'occhio | FIN-005,007 | IA-002, HOME | IA-041…049 | Finanze | P29,46,48 | J5,J6 |
| IA-041 | Storico transazioni | Trovare/filtrare spese | FIN-010 | IA-040 | IA-042 | Finanze | — | J7 |
| IA-042 | Dettaglio transazione | Modificare/dividere/eliminare | FIN-011 | IA-041 | Chiusura | Finanze | P26 | J6 |
| IA-043 | Conti | Gestione conti e saldi | FIN-003,004 | IA-040 | IA-044 | Finanze | — | J6 |
| IA-044 | Dettaglio conto | Storico e trasferimenti | FIN-003,004 | IA-043 | IA-042 | Finanze | — | — |
| IA-045 | Budget | Gestione soglie | FIN-005 | IA-040 | IA-046 | Finanze | P48 | J6 |
| IA-046 | Modifica budget | Impostare soglia/periodo | FIN-005 | IA-045 | Chiusura | Finanze | — | — |
| IA-047 | Categorie | Personalizzare tassonomia | FIN-002 | IA-040 (impostazioni) | Chiusura | Finanze | — | — |
| IA-048 | Report mensile | Capire il mese in una schermata | FIN-007 | IA-040 | Drill-down IA-041 | Finanze | P29 | J6 |
| IA-049 | Risparmi | Obiettivi finanziari | FIN-008 | IA-040 | IA-091 (ponte GOAL) | Finanze | P45 | J15 |
| IA-04A | Import CSV | Ingresso dati bancari assistito | FIN-012 | IA-041 (impostazioni) | IA-041 | Finanze | C-art.67 | J6 |

## Abitudini

| ID | Nome | Scopo | Funzioni | Origine | Destinazioni | Modulo | Principi | JTBD |
|---|---|---|---|---|---|---|---|---|
| IA-060 | Abitudini (oggi) | Spuntare le abitudini del giorno | HAB-001…006 | IA-002, HOME | IA-061 | Abitudini | P39-41 | J8 |
| IA-061 | Dettaglio abitudine | Gestione completa | HAB-001…013 | IA-060 | IA-062 | Abitudini | P51 | J8,J9,J10 |
| IA-062 | Griglia storica | Vedere il pattern nel tempo | HAB-009 | IA-061 | Chiusura | Abitudini | P29 | J16 |
| IA-063 | Gestione abitudini | Riordino, pausa, archivio di tutte | HAB-008 | IA-060 | IA-061 | Abitudini | — | — |

## Calendario

| ID | Nome | Scopo | Funzioni | Origine | Destinazioni | Modulo | Principi | JTBD |
|---|---|---|---|---|---|---|---|---|
| IA-050 | Agenda giorno | Timeline unificata | CAL-002 | IA-002, HOME | IA-053 | Calendario | P31,85 | J4,J5 |
| IA-051 | Agenda settimana | Vista a più ampio respiro | CAL-002 | Segmented IA-050 | IA-053 | Calendario | — | J4 |
| IA-052 | Vista mensile | Densità del mese | CAL-007 | IA-050 | IA-050 (giorno) | Calendario | — | — |
| IA-053 | Dettaglio evento | Vedere/modificare (se scrivibile) | CAL-003,004,005 | Ogni agenda | Chiusura o app esterna | Calendario | C-art.61 | J4 |
| IA-054 | Impostazioni calendario | Scegliere quali calendari mostrare | CAL-001 | IA-050 (impostazioni) | Chiusura | Calendario | — | — |

## Note

| ID | Nome | Scopo | Funzioni | Origine | Destinazioni | Modulo | Principi | JTBD |
|---|---|---|---|---|---|---|---|---|
| IA-070 | Elenco note | Pin + recenti, mai cartelle | NOTE-004 | IA-002, HOME | IA-071 | Note | P32 | J1,J7 |
| IA-071 | Editor nota | Scrivere e collegare | NOTE-001,002,003 | IA-070 | IA-073,074 | Note | P27 | J1,J17 |
| IA-072 | Archivio note | Note archiviate, ricercabili | NOTE-005 | IA-070 | IA-071 | Note | — | — |
| IA-073 | Cronologia versioni | Recuperare una versione precedente | NOTE-006 | IA-071 | Chiusura | Note | C-art.13 | J12 |
| IA-074 | Collegamenti nota | Vedere/gestire i link al grafo | NOTE-003 | IA-071 | Entità collegata | Note | P2 (integrazione) | J17 |

## Salute

| ID | Nome | Scopo | Funzioni | Origine | Destinazioni | Modulo | Principi | JTBD |
|---|---|---|---|---|---|---|---|---|
| IA-080 | Riepilogo Salute | Contesto del corpo nella giornata | HLTH-002 | IA-002, HOME | IA-081,082 | Salute | P4 | J8,J17 |
| IA-081 | Storico e tendenze | Pattern nel tempo, senza diagnosi | HLTH-005 | IA-080 | Chiusura | Salute | C-art.146 | J17 |
| IA-082 | Metriche manuali | Inserire peso/umore/energia | HLTH-004 | IA-080, FAB | Chiusura | Salute | — | J17 |
| IA-083 | Collegamenti Salute↔Abitudini | Configurare auto-completamento | HLTH-003 | IA-080 (impostazioni) | Chiusura | Salute | C-art.67 | J8 |

## Obiettivi

| ID | Nome | Scopo | Funzioni | Origine | Destinazioni | Modulo | Principi | JTBD |
|---|---|---|---|---|---|---|---|---|
| IA-090 | Elenco obiettivi | Vedere i sogni in corso | GOAL-008 | IA-002, HOME | IA-091 | Obiettivi | P45 | J15,J16 |
| IA-091 | Dettaglio obiettivo | Progresso aggregato per fronti | GOAL-002,003,004 | IA-090 | IA-092, entità collegate | Obiettivi | P77-78 | **J5**,J15,J16 |
| IA-092 | Collega contributo | Estendere il grafo dell'obiettivo | GOAL-002 | IA-091 | Chiusura | Obiettivi | C-art.184 | J5 |
| IA-093 | Crea obiettivo | Dare inizio a un piano | GOAL-001 | IA-090, FAB | IA-091 | Obiettivi | — | J15 |

## Trasversali

| ID | Nome | Scopo | Funzioni | Origine | Destinazioni | Modulo | Principi | JTBD |
|---|---|---|---|---|---|---|---|---|
| IA-100 | Ricerca globale | Trovare tutto, subito | SRCH-001,002,004,005 | Tab bar | GEF verso ogni entità | Ricerca | P32 | **J7** |
| IA-101 | Risultati filtrati | Restringere per tipo/data | SRCH-003,006 | IA-100 | GEF | Ricerca | P16 | J7 |
| IA-110 | Centro notifiche | Trasparenza sugli avvisi | NTF-007 | Campanella IA-001 | Entità collegata | Notifiche | C-art.63 | J19 |
| IA-137 | Cestino | Recuperare l'eliminato | MFC-R-10 | IA-130, ogni modulo | Ripristino in modulo | Trasversale | C-art.13 | J12 |
| IA-130-139 | Impostazioni (indice + 9 sotto-schermate) | Account, sicurezza, aspetto, dati, abbonamento, dispositivi, aiuto, cancellazione | SET-*, SEC-*, PROF-001, EXP-*, BKP-*, SYNC-003 | Tab bar | Ogni sotto-schermata | Impostazioni | P88; Titolo I-II Constitution | J11,J14,J20 |

**Totale schermate/fogli principali inventariati: 62** (corrispondenti ai nodi IA di [01-information-architecture](01-information-architecture.md), esclusi dialoghi minori e stati che sono varianti della stessa schermata — vedi [09-empty-states](09-empty-states.md) e [10-error-experience](10-error-experience.md)).

---

*Prossimo: [User Flows — Core e MVP](04-user-flows-core-mvp.md)*
