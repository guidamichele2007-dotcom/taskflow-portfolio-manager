# 14 · Matrici di Tracciabilità UX

> Collegano schermate, funzionalità, flussi, JTBD, principi, personas, test e accessibilità. Fonte degli ID: [Screen Inventory](03-screen-inventory.md), [Functional Bible](../functional_bible/17-matrici.md), [Product Bible](../product_bible/README.md).

## 1. Matrice Screen → Feature (estratto rappresentativo; completa per derivazione da [03-screen-inventory](03-screen-inventory.md) che già lista le funzioni per schermata)

| Schermata | Funzioni (Functional Bible) |
|---|---|
| IA-001 Home | HOME-001…008 |
| IA-020 Cattura | CAPT-001…010 |
| IA-030…036 Attività | TASK-001…018 |
| IA-040…04A Finanze | FIN-001…014 |
| IA-060…063 Abitudini | HAB-001…013 |
| IA-050…054 Calendario | CAL-001…007 |
| IA-070…074 Note | NOTE-001…009 |
| IA-080…083 Salute | HLTH-001…005 |
| IA-090…093 Obiettivi | GOAL-001…008 |
| IA-100…101 Ricerca | SRCH-001…006 |
| IA-110/111 Notifiche | NTF-001…008 |
| IA-130…139 Impostazioni | SET-*, SEC-*, PROF-001, EXP-*, BKP-*, SYNC-* |

## 2. Matrice Feature → Flow

| Funzione | Flow di riferimento |
|---|---|
| CAPT-001/004/005/008 | FLOW-CAPT-01 |
| HOME-001…004/007 | FLOW-HOME-01 |
| ONB-001…007 | FLOW-ONB-01 |
| TASK-001…009 | FLOW-TASK-01 |
| FIN-001/005/014 | FLOW-FIN-01 |
| HAB-001…008 | FLOW-HAB-01 |
| GAL-001/002 | FLOW-GAL-01 |
| CAL-002/005 | FLOW-CAL-01 |
| NOTE-001…003 | FLOW-NOTE-01 |
| HLTH-003 | FLOW-HLTH-01 |
| GOAL-001…003 | FLOW-GOAL-01 |
| SRCH-001…005 | FLOW-SRCH-01 |
| REV-001…004 | FLOW-REV-01 |
| BKP-003, SYNC-001/003 | FLOW-SYNC-01 |
| SET-003 | FLOW-SET-01 |
| Ogni entità (TASK/FIN/HAB/NOTE/GOAL) | Generic Entity Flow (GEF, [MUC §9](00-modello-ux-comune.md#9-il-flusso-generico-del-ciclo-di-vita-di-unentità-generic-entity-flow--gef)) + [06-task-flows-entita](06-task-flows-entita.md) |

## 3. Matrice Flow → JTBD

| Flow | JTBD |
|---|---|
| FLOW-CAPT-01 | **J1, J2, J3** |
| FLOW-HOME-01 | J4 |
| FLOW-ONB-01 | J1 (primo assaggio), TTV |
| FLOW-TASK-01 | J1, J4, J15 |
| FLOW-FIN-01 | J1, J5, J6 |
| FLOW-HAB-01 | **J8, J9, J10** |
| FLOW-GAL-01 | J18 |
| FLOW-CAL-01 | J4, J5 |
| FLOW-NOTE-01 | J1, J7, J17 |
| FLOW-HLTH-01 | J8, J17 |
| FLOW-GOAL-01 | **J5**, J15, J16 |
| FLOW-SRCH-01 | **J7** |
| FLOW-REV-01 | J6 |
| FLOW-SYNC-01 | **J12, J13** |
| FLOW-SET-01 | J20 |
| GEF (ciclo di vita entità) | J12 (mai perdere), J14 (libertà di uscita) |

## 4. Matrice Flow → Product Principles (chiave, non esaustiva)

| Flow | Principi |
|---|---|
| FLOW-CAPT-01 | P13, P14, P18, P21 |
| FLOW-HOME-01 | P25, P54, P82, HOME-007→C-art.51-52 |
| FLOW-ONB-01 | P15, P17, D-05 |
| FLOW-TASK-01 | P26, P33 |
| FLOW-FIN-01 | P29, P46, P48-49 |
| FLOW-HAB-01 | P39-41, P51, D-06 |
| FLOW-GAL-01 | P77-84, D-05 |
| FLOW-NOTE-01 | P27, P32 |
| FLOW-GOAL-01 | P45, P77-78 |
| FLOW-SRCH-01 | P32 |
| FLOW-REV-01 | P28, P52 |
| FLOW-SYNC-01 | Titolo I Constitution, D-02, D-09 |
| FLOW-SET-01 | C-art.163, P88 |
| UX Constitution (globale) | Tutti i 110 principi Product Bible mappati per Titolo (vedi [13-ux-constitution](13-ux-constitution.md) intestazioni) |

## 5. Matrice Flow → Personas

| Flow | Personas primarie coinvolte |
|---|---|
| FLOW-CAPT-01 | Giulia (P1), Luca (P4) — velocità di cattura |
| FLOW-HOME-01 | Giulia (P1) — quadro del giorno in pendolarismo |
| FLOW-ONB-01 | Anna (P3) — time-to-value senza attrito tecnico |
| FLOW-TASK-01 | Giulia (P1), Chiara (S1) |
| FLOW-FIN-01 | Davide (S6) — tono non giudicante; Luca (P4) — budget stretto |
| FLOW-HAB-01 | Anna (P3), Elena (S5) — costanza gentile |
| FLOW-GAL-01 | Anna (P3) — scoperta progressiva senza sovraccarico |
| FLOW-NOTE-01 | Marco (P2), Sofia (S3) |
| FLOW-GOAL-01 | Giulia (P1), Martina&Paolo (E2) |
| FLOW-SRCH-01 | Marco (P2) — densità informativa |
| FLOW-REV-01 | Giulia (P1) — pianificazione settimanale in 5 minuti |
| FLOW-SYNC-01 | Marco (P2) — fiducia E2E; Ahmed (S4) — multi-device in viaggio |
| Accessibilità (trasversale) | Franca (S7) — riferimento primario di ogni verifica |

## 6. Matrice Flow → Test Case (rimando ai suite test della Functional Bible + estensioni UX)

| Flow | Test Case | Suite |
|---|---|---|
| FLOW-CAPT-01 | Mediana tocchi ≤3, tempo ≤3s; parser it/en corpus | E2E-CAPT, UT-parser (Functional Bible 17) |
| FLOW-HOME-01 | Avvio ≤1,5s con 5 moduli; composizione dinamica | E2E-HOME, PT-avvio |
| FLOW-ONB-01 | Tempo mediano ≤60s; 0 permessi/account richiesti | E2E-ONB, AT (accessibilità) |
| FLOW-TASK-01 | Ricorrenze su 12 mesi simulati (DST/bisestili) | UT-ricorrenze, E2E-TASK |
| FLOW-FIN-01 | Budget soglia 80%/100%, mai notifica ripetuta | UT-budget, E2E-FIN |
| FLOW-HAB-01 | Simulazione salti/pause su 12 mesi, nessun azzeramento | **UT-resilienza** (suite critica) |
| FLOW-GAL-01 | Limite Free 2 moduli, proposta non bloccante | E2E-GAL |
| FLOW-SRCH-01 | 50.000 entità, primi risultati ≤100ms | PT-100ms/50k |
| FLOW-SYNC-01 | Convergenza multi-device offline (fuzzing generativo) | Test generativi convergenza, IT-multi-device |
| GEF (ogni entità) | MFC-AC-01…08 ereditati; undo 7s; cestino 30gg | MFC (suite ereditata) |
| UX Constitution (globale) | Audit accessibilità automatico+manuale per release | AT (§12) |

## 7. Matrice Flow → Accessibility (rimando puntuale a [12-accessibility-bible](12-accessibility-bible.md))

| Flow | Requisiti di accessibilità specifici |
|---|---|
| FLOW-CAPT-01 | Cattura vocale end-to-end senza tocchi (§7); chip correggibili via screen reader |
| FLOW-HOME-01 | Ordine di lettura card coerente con priorità visiva; card sensibili offuscate annunciate correttamente |
| FLOW-ONB-01 | Font scaling 200% su tutte e 3 le schermate; focus automatico sul campo di cattura |
| FLOW-TASK-01 | Swipe di eliminazione con equivalente menu "···"; drag riordino con alternativa "sposta su/giù" |
| FLOW-FIN-01 | Stati budget mai solo colore (sempre testo/icona) |
| FLOW-HAB-01 | Griglia storica con descrizione testuale equivalente per screen reader |
| FLOW-GOAL-01 | Grafico di progresso aggregato con tabella dati equivalente |
| FLOW-SRCH-01 | Risultati incrementali annunciati in modo non invasivo (live region moderata) |
| Ogni flusso con animazione (§7 Microinterazioni) | Variante statica per "riduci movimento" verificata |

---

*Torna all'[indice](README.md)*
