# 17 · Matrici di Tracciabilità

> Le matrici collegano ogni funzione a: modulo, priorità, versione di rilascio, JTBD, principi chiave, futura area API e futura suite di test. Sono lo strumento con cui si verifica che **nulla esista senza motivo e nulla di motivato sia dimenticato**. Nota di metodo: "Futura API" indica l'*area di contratto* che la funzione richiederà (senza progettarla — fase successiva); "Local" = nessuna API remota: funzione interamente on-device (la maggioranza, per D-02).

## 1. Matrice Moduli → Funzioni

| Modulo | Funzioni | Tot |
|---|---|---|
| Core Home/Onboarding/Galleria/Revisione | HOME-001…008 · ONB-001…007 · GAL-001…006 · REV-001…004 | 25 |
| Cattura | CAPT-001…010 | 10 |
| Attività | TASK-001…018 | 18 |
| Finanze | FIN-001…014 | 14 |
| Abitudini | HAB-001…013 | 13 |
| Calendario | CAL-001…007 | 7 |
| Note | NOTE-001…009 | 9 |
| Salute | HLTH-001…005 | 5 |
| Obiettivi | GOAL-001…008 | 8 |
| Ricerca | SRCH-001…006 | 6 |
| Notifiche | NTF-001…008 | 8 |
| Widget | WID-001…007 | 7 |
| Sync/Backup/Export | SYNC-001…003 · BKP-001…004 · EXP-001…003 | 10 |
| Impostazioni/Profilo/Sicurezza | SET-001…004 · SEC-001…003 · PROF-001 | 8 |
| Insight | INS-001…005 | 5 |
| **Totale funzioni specificate** | | **153** |

## 2. Matrice consolidata Funzioni → Priorità · Release · JTBD · Principi · Futura API · Futuri Test

Legenda release: **1.0** = MVP · **1.x** = anno 1 post-lancio · **2.x** = anno 2 · **3.x** = anno 3+. Suite di test: `UT`=unit, `IT`=integrazione moduli/eventi, `E2E`=flusso completo su device, `PT`=performance, `AT`=accessibilità, `ST`=sicurezza. Ogni funzione eredita comunque MFC-AC-01…08 (suite `MFC`).

### Core

| Funzione | Pri | Rel | JTBD | Principi chiave | Futura API | Test |
|---|---|---|---|---|---|---|
| HOME-001/002/004/007/008 | M | 1.0 | J4 | P25, P54, P82 | Local | E2E-HOME, PT-avvio, AT |
| HOME-003/006 | S | 1.x | J4 | P82 | Local | E2E-HOME |
| HOME-005 | S | 1.x | J17 | P36 | Local | IT-INS |
| ONB-001…007 | M | 1.0 | J1 | P15, P17 | Auth API (solo ONB-004/007) | E2E-ONB, AT |
| GAL-001…003 | M | 1.0 | J18 | P77–84 | Module Registry API | E2E-GAL, IT-eventi |
| GAL-004/005 | S | 1.x | J18 | P36 | Registry API | IT |
| GAL-006 | C | 3.x | — | C-art. 189-195 | Marketplace API | ST, E2E |
| REV-001…004 | S | 1.x | J6 | P28, P52 | Local | E2E-REV, AT |

### Cattura

| Funzione | Pri | Rel | JTBD | Principi | Futura API | Test |
|---|---|---|---|---|---|---|
| CAPT-001/004/005/008/010 | M | 1.0 | **J1, J2** | P13, P14 | Local (parser on-device) | UT-parser (corpus it/en), E2E-CAPT, PT |
| CAPT-002 | M | 1.0 | J3 | P21 | Local | E2E-WID |
| CAPT-003/007/009 | S | 1.x | J3 | P21 | Local | E2E, AT |
| CAPT-006 | S | 1.x | J2 | P18 | Local | UT-learning |

### Attività

| Funzione | Pri | Rel | JTBD | Principi | Futura API | Test |
|---|---|---|---|---|---|---|
| TASK-001…009, 012…014 | M | 1.0 | J1, J4, J7 | P13, P26, P32 | Sync API (trasporto cifrato) | UT-ricorrenze (DST/bisestili), E2E-TASK, MFC |
| TASK-010 | S | 1.x | J4 | P43 (C-art. 67) | Local | IT-CAL |
| TASK-011/015/018 | S | 1.x | J15 | P77-78 | Local | IT-grafo |
| TASK-016 | S | 1.x | J5 | P85 | Local (provider OS) | E2E-CAL |
| TASK-017 | C | 2.x | — | — | Local | UT |

### Finanze

| Funzione | Pri | Rel | JTBD | Principi | Futura API | Test |
|---|---|---|---|---|---|---|
| FIN-001…007, 010, 011(base), 014 | M | 1.0 | J1, J6 | P29, P46, P48-49 | Sync API | UT-budget (mesi/fusi), E2E-FIN, ST (MFC-R-21/22) |
| FIN-008 | S | 1.x | J15 | P45 | Local | IT-GOAL |
| FIN-011(divisione)/012 | S | 1.x | J6 | C-art. 67 | Local | UT-import (dedup) |
| FIN-009 | C→S | 2.x | J13 (Ahmed S4) | P6 | Tassi di cambio (sola lettura pubblica) | UT-valute |
| FIN-013 | C | 2.x | — | C-art. 45 | Local | ST |

### Abitudini

| Funzione | Pri | Rel | JTBD | Principi | Futura API | Test |
|---|---|---|---|---|---|---|
| HAB-001…006, 008 | M | 1.0 | **J8, J9** | P39-41 | Sync API | **UT-resilienza (la suite più importante del modulo: simulazioni 12 mesi con salti/pause/fusi)**, E2E-HAB |
| HAB-007/009/012/013 | S | 1.x | J10, J16 | P51, C-art. 74 | Local | UT, AT (griglia) |
| HAB-010 | S | 1.x | J8 | P43 | Local (piattaforma salute) | IT-HLTH (idempotenza) |
| HAB-011 | C | 2.x | — | — | Local | UT |

### Calendario · Note · Salute · Obiettivi

| Funzione | Pri | Rel | JTBD | Principi | Futura API | Test |
|---|---|---|---|---|---|---|
| CAL-001/002/003(lettura) | M | 1.0 | J4 | C-art. 61 | Local (provider OS) | IT-provider, E2E-CAL |
| CAL-003(scrittura)/004…007 | S | 1.x | J4, J5 | P85 | Local | E2E-CAL |
| NOTE-001…007 | S | 1.x | J1, J7 | P27, P32 | Sync API | UT-merge (paragrafi), E2E-NOTE |
| NOTE-008/009 | C | 2.x | — | C-art. 45 | Local | PT (media) |
| HLTH-001…003 | S | 1.x | J8, J17 | C-art. 43-45 | **Nessuna (vietata per HLTH-R-02)** | ST (verifica di non-trasmissione), IT-HAB |
| HLTH-004/005 | C | 2.x | J17 | P29 | Sync API (soli dati manuali) | UT |
| GOAL-001…004, 006…008 | S | 1.x | **J15, J16, J5** | P77-78 | Sync API | UT-aggregazione, IT-eventi (tutti i moduli), E2E-GOAL |
| GOAL-005 | C | 2.x | J16 | P45 | Local | UT |

### Servizi trasversali

| Funzione | Pri | Rel | JTBD | Principi | Futura API | Test |
|---|---|---|---|---|---|---|
| SRCH-001/002/004/005/006 | M | 1.0 | **J7** | P32 | **Nessuna (SRCH-R-02)** | PT-100ms/50k, E2E-SRCH, ST (offuscamento) |
| SRCH-003 | S | 1.x | J7 | P16 | Local | UT |
| NTF-001…005, 007 | M | 1.0 | J8, J19 | C-art. 58-63 | Push relay (solo trigger, zero contenuti) | IT-budget, E2E-NTF, ST (payload) |
| NTF-006/008 | S | 1.x | J19 | C-art. 63 | Local | IT |
| WID-001/002/003/005 | M | 1.0 | J1, J3, J4 | P21 | Local | E2E-WID, ST (lockscreen) |
| WID-004/006/007 | S/C | 1.x/2.x | J3 | P21 | Local | E2E |
| SYNC-001…003 | M | 1.0 | **J12, J13** | C-art. Titolo I | **Sync API** (delta cifrati + vettori versione) · Auth API | **Test generativi convergenza (fuzzing)**, IT-multi-device, ST, PT |
| BKP-001…003 | M | 1.0 | J12 | C-art. 18-19 | Backup API (blob cifrati) | E2E-ripristino (ogni release), PT-2min |
| BKP-004 | S | 1.x | J12 | C-art. 13 | Backup API | E2E |
| EXP-001/003 | M | 1.0 | **J14** | C-art. 7-9 | Account API (cancellazione) | E2E-export (completezza), ST-cancellazione |
| EXP-002 | S | 1.x | J14 | C-art. 7 | Local | UT |
| SET-001…004, SEC-001/002, PROF-001 | M | 1.0 | J11, J20 | P88; C-art. 163 | Auth/Billing API | E2E-SET, ST-biometria, E2E-disdetta |
| SEC-003 | S | 1.x | J11 | C-art. Titolo II | Auth API | ST |
| INS-001…003, 005 | S | 1.x | **J17** | C-art. 145-146 | Config firmata (sola lettura) | UT-regole, ST (nessuna trasmissione) |
| INS-004 | C | 2.x | J17 | C-art. 146 | Local | UT-statistica |

## 3. Matrice Funzioni → Priorità MVP (riepilogo numerico)

| Priorità | Funzioni | % | Release |
|---|---|---|---|
| **Must (MVP)** | 78 | 51% | 1.0 |
| **Should** | 55 | 36% | 1.x |
| **Could** | 20 | 13% | 2.x/3.x |

Coerenza con D-03 verificata: tutte le Must appartengono a Core, Cattura, Attività, Finanze, Abitudini, Calendario-lettura e servizi trasversali. Nessuna funzione Must dipende da una Should.

## 4. Matrice di copertura JTBD → Funzioni (verifica inversa: nessun job orfano)

| Job | Coperto da | Verdetto |
|---|---|---|
| J1-J2-J3 (catturare) | CAPT-*, WID-003, ONB-003 | ✅ MVP |
| J4 (quadro del giorno) | HOME-*, CAL-002, WID-001 | ✅ MVP |
| J5 (decidere trasversale) | GOAL-003/004, CAL-006, INS-004 | ✅ v1.x (per D-03, maturazione dichiarata) |
| J6 (com'è andata) | FIN-007, REV-*, INS-002 | ✅ MVP+1.x |
| J7 (trovare) | SRCH-* | ✅ MVP |
| J8-J9-J10 (costanza gentile) | HAB-* (in part. 005/007/008/013) | ✅ MVP |
| J11 (certezza privacy) | SEC-*, SET-002, architettura D-02 | ✅ MVP |
| J12-J13 (mai perdere, sempre funzionare) | SYNC-*, BKP-*, MFC §3 | ✅ MVP |
| J14 (libertà di uscita) | EXP-* | ✅ MVP |
| J15-J16 (dal sogno al piano) | GOAL-*, FIN-008, TASK-015, HAB-012 | ✅ v1.x |
| J17 (conoscersi) | INS-*, HLTH-004, NOTE-003 | ✅ v1.x |
| J18-J19-J20 (anti-job) | Vincoli: SET-R-01, NTF-R-01, GAL, MFC | ✅ trasversale |

## 5. Copertura della Product Bible (tabella richiesta)

| Elemento Product Bible | Copertura nella Functional Bible | Stato |
|---|---|---|
| D-01 modularità | GAL-*, MFC-R-13, C-art. 181-187 applicati in ogni modulo | ✅ |
| D-02 E2E content-blind | MFC §3/5, SYNC/BKP, HLTH-R-02, SRCH-R-02, INS (no rete) | ✅ |
| D-03 MVP 3 moduli | Matrice §3: Must solo su moduli MVP | ✅ |
| D-04 WAI/anti-engagement | HOME-007 (no pull-refresh), NTF-R-01, INS-R-02 | ✅ |
| D-05 freemium per capacità | Stati Free nel MFC §4, GAL-R-01, FIN-R-05, NOTE-R-03, GOAL-R-02, EXP-R-01 | ✅ |
| D-06 costanza resiliente | HAB-005 (specifica normativa completa) | ✅ |
| D-07 no open banking | FIN-012 come ponte; assenza di funzioni bank-sync | ✅ |
| D-09 recovery key | SEC-002 con il copione del caso peggiore | ✅ |
| Principi P1-P110 | Citati puntualmente nelle regole di ogni modulo | ✅ campione verificato |
| Constitution | Ogni regola di business cita gli articoli vincolanti | ✅ campione verificato |
| Personas | Richiamate dove decidono il comportamento (Davide→FIN, Anna→HAB, Franca→AT, Marco→EXP/ST) | ✅ |
| Moduli evocati ma non specificati (Documenti, Casa, Auto, Viaggi, Inventario, Password, Workspace…) | [16-moduli-futuri](16-moduli-futuri.md) con verdetto e condizioni di risveglio | ✅ deliberato |

---

*Indice completo: [README](README.md)*
