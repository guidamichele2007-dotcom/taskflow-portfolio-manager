# 06 · Task Flows — Ciclo di vita delle entità

> Applica il **Generic Entity Flow (GEF)** definito in [MUC §9](00-modello-ux-comune.md#9-il-flusso-generico-del-ciclo-di-vita-di-unentità-generic-entity-flow--gef) a ogni tipo di entità del grafo. Per ciascuna: creare, modificare, duplicare, condividere, archiviare, ripristinare, eliminare, recuperare, cercare, taggare/categorizzare, versionare/cronologia. Qui solo le **deroghe e specifiche** rispetto al GEF (P33 — un'anatomia sola, non ripetiamo ciò che è identico).

## 6.1 Task (TASK)

| Operazione | Deroga rispetto al GEF | Rif. funzionale |
|---|---|---|
| Creare | Titolo obbligatorio, tutto il resto opzionale; creazione anche da cattura (FLOW-CAPT-01) | TASK-001 |
| Modificare | Ogni campo inline nel foglio di dettaglio; la ricorrenza chiede "questa occorrenza / tutte le future" alla modifica | TASK-004 |
| Duplicare | Copia senza sottotask completati; priorità e lista mantenute | TASK-017 (Could) |
| Condividere | Non previsto in MVP (i task sono personali; la condivisione arriva con gli spazi familiari, PB roadmap anno 4) | — |
| Archiviare | Le **liste** si archiviano intere (TASK-018), non i singoli task (che si completano o eliminano) | TASK-018 |
| Ripristinare | Dal Cestino, torna nella lista originale; se la lista era archiviata, il task appare comunque, con indicazione | MFC-R-10 |
| Eliminare | Swipe (colore neutro); ricorrenza chiede "solo questa / tutte le future" | TASK-004 |
| Recuperare | Cestino globale (IA-137), filtro "Attività" | MFC-R-10 |
| Cercare | SRCH con filtro tipo "Task" | SRCH-002 |
| Taggare/categorizzare | Lista (2 livelli, TASK-005) + priorità (3 livelli, TASK-006); non esistono tag liberi (P32 — niente tassonomie parallele) | TASK-005/006 |
| Versionare/cronologia | Per campo (MFC-R-07); include completamenti/riaperture; "in sospeso" mostra da quando | MFC-R-07 |

## 6.2 Transazione finanziaria (FIN)

| Operazione | Deroga | Rif. |
|---|---|---|
| Creare | Importo obbligatorio; conto/categoria/data con default intelligenti | FIN-001 |
| Modificare | Ogni campo modificabile; modificare il mese ricalcola i budget di entrambi i mesi coinvolti | FIN-011 |
| Duplicare | Utile per spese ricorrenti non automatizzate ("stessa spesa di ieri") | v1.x |
| Condividere | Non prevista per la singola transazione (dato sensibile); il **report** (IA-048) è condivisibile come immagine/PDF | EXP-002 |
| Archiviare | Non applicabile alle transazioni (sono storiche per natura); si archiviano **categorie** e **conti** | FIN-002/003 |
| Ripristinare | Da Cestino; se il conto d'origine è stato eliminato, richiede scelta di un conto (mai orfana) | MFC-R-12 |
| Eliminare | Swipe; se collegata a un obiettivo di risparmio, il fronte si ricalcola | GOAL-002 |
| Recuperare | Cestino, filtro "Finanze" | MFC-R-10 |
| Cercare | SRCH con filtro tipo + filtri contestuali (importo, conto, categoria) | FIN-010, SRCH-003 |
| Taggare/categorizzare | Categoria (2 livelli) + conto; divisione multi-categoria disponibile (FIN-011) | FIN-002/011 |
| Versionare/cronologia | Per campo; l'audit trail è requisito di fiducia (mai importi modificati "in silenzio") | MFC-R-07 |

## 6.3 Abitudine (HAB)

| Operazione | Deroga | Rif. |
|---|---|---|
| Creare | Frequenza obbligatoria (non solo "ogni giorno" di default: si propone attivamente una scelta prudente) | HAB-003 |
| Modificare | Cambiare la frequenza a metà finestra ricalcola pro-quota, mai penalizzando il passato | HAB-005 |
| Duplicare | Utile per varianti ("Camminata mattina" / "Camminata sera") | v2.x |
| Condividere | Non prevista (nessun confronto sociale, C-art. 55) | — |
| Archiviare | Distinta dalla pausa (HAB-008): l'archiviazione è per abitudini abbandonate consapevolmente, conserva lo storico per statistiche di lungo periodo | MFC-R-09 |
| Ripristinare | Dal Cestino o dall'Archivio; l'aderenza riparte dal punto in cui era (mai azzerata) | HAB-005 |
| Eliminare | Cestino 30gg con tutto lo storico | MFC-R-10 |
| Recuperare | Cestino, filtro "Abitudini" | — |
| Cercare | SRCH con filtro tipo | SRCH-002 |
| Taggare | Nessun tag: solo collegamento opzionale a un Obiettivo (HAB-012) | GOAL-002 |
| Versionare/cronologia | La griglia storica (HAB-009) È la cronologia visiva; il linguaggio non contiene mai "fallito/rotto" (glossario HAB-R) | HAB-009 |

## 6.4 Nota (NOTE)

| Operazione | Deroga | Rif. |
|---|---|---|
| Creare | Nessun campo obbligatorio (anche solo titolo); salvataggio continuo dal primo carattere | NOTE-001 |
| Modificare | Autosave continuo; niente pulsante "salva" | MFC-R-06 |
| Duplicare | Copia integrale senza cronologia; i collegamenti NON si duplicano (evita ambiguità nel grafo) | v1.x |
| Condividere | Esporta come testo/PDF via share di sistema; la nota originale resta privata (esce una copia) | NOTE-007 |
| Archiviare | Standard GEF; la ricerca resta la via primaria di recupero (niente cartelle, P32) | NOTE-005 |
| Ripristinare | Standard; i collegamenti sospesi tornano attivi | MFC-R-12 |
| Eliminare | Standard; conferma se la nota ha collegamenti attivi importanti (informativa, non bloccante) | — |
| Recuperare | Cestino, filtro "Note" | — |
| Cercare | Full-text su titolo e corpo | SRCH-001 |
| Taggare | Nessun sistema di tag: i **collegamenti** (`@entità`) sono la tassonomia (NOTE-003) | NOTE-003 |
| Versionare/cronologia | **Deroga dichiarata al GEF**: versioni a snapshot del contenuto intero (non per campo), perché il testo libero non ha "campi" | NOTE-006 |

## 6.5 Obiettivo (GOAL)

| Operazione | Deroga | Rif. |
|---|---|---|
| Creare | Solo titolo obbligatorio; data target e contributi opzionali fin da subito | GOAL-001 |
| Modificare | Modificare la data target propone il ricalcolo della proiezione (FIN-008) | GOAL-004 |
| Duplicare | Non prevista (ogni obiettivo è unico per natura) | — |
| Condividere | Solo al **completamento** (GOAL-007): immagine generata, condivisione esterna, mai interna/sociale | GOAL-007 |
| Archiviare/Pausa | Sospende senza smontare i collegamenti; i fronti restano "in pausa" | GOAL-006 |
| Ripristinare | Standard; i contributi collegati riprendono a contribuire | MFC-R-12 |
| Eliminare | **Non elimina mai i contributi collegati** (regola cardine, MFC-R-12): task/risparmi/abitudini restano vivi, scollegati | GOAL-R-mfc |
| Recuperare | Cestino, filtro "Obiettivi"; ripristino completo dei collegamenti entro 30gg | — |
| Cercare | SRCH; anche per titolo dei fronti collegati | SRCH-001 |
| Taggare | Nessun tag: la struttura è i "fronti" stessi (risparmio/task/abitudine/nota) | GOAL-002 |
| Versionare/cronologia | Cronologia dei collegamenti (quando aggiunti/rimossi) + milestone raggiunte | GOAL-005 |

## 6.6 Evento di calendario (CAL) — la grande eccezione al GEF

**Deroga architetturale dichiarata**: gli eventi di sistema **non hanno ciclo di vita nostro**. Non esiste "Cestino OmniLife" per gli eventi, non esiste "versione OmniLife": la fonte di verità è il calendario di sistema (C-art. 61 — nessuna duplicazione).

| Operazione | Comportamento |
|---|---|
| Creare | CAL-004: crea sul calendario di sistema scelto come default |
| Modificare | Solo campi semplici se il calendario è scrivibile; altrimenti "Apri nell'app Calendario" |
| Duplicare/Condividere/Taggare | Deleghiamo alla app di sistema (che ha già queste funzioni) |
| Archiviare/Eliminare | L'eliminazione di un **time-box nostro** (CAL-005) non tocca l'evento né il task collegato; l'eliminazione di un evento è sempre nell'app di sistema |
| Ripristinare | Nessun cestino nostro: il ripristino segue le regole del provider (es. Cestino di Google Calendar) |
| Cercare | SRCH include gli eventi come riferimenti in sola lettura |
| Versionare/cronologia | Nessuna: il provider possiede la cronologia |

## 6.7 Dati Salute (HLTH) — seconda eccezione dichiarata

**I dati letti dalla piattaforma salute non seguono il GEF**: sono sola lettura, non nostri (HLTH-R-02). Solo le **metriche manuali** (peso, umore, energia — HLTH-004) seguono il GEF completo, ciclo di vita pieno incluso export ed eliminazione.

## 6.8 Riepilogo — dove il GEF si applica integralmente

| Entità | GEF completo | Deroghe |
|---|---|---|
| Task | ✅ | Ricorrenza (questa/tutte) |
| Transazione | ✅ | No archiviazione diretta; condivisione solo aggregata |
| Abitudine | ✅ | Archivio ≠ pausa; niente tag, solo collegamento |
| Nota | ✅ | Versioni a snapshot, non per campo |
| Obiettivo | ✅ | Eliminazione non tocca contributi; condivisione solo a completamento |
| Evento calendario | ❌ deroga totale | Ciclo di vita del provider OS |
| Dato salute piattaforma | ❌ deroga totale | Sola lettura, non nostro |
| Metrica manuale salute | ✅ | — |

---

*Prossimo: [Microinterazioni](07-microinterazioni.md)*
