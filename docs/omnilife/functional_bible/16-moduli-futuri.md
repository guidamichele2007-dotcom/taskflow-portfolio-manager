# 16 · Moduli Futuri e Perimetro (decisioni di scope)

> Questo documento applica la [Feature Philosophy](../product_bible/09-feature-philosophy.md) ai moduli spesso richiesti ma **non previsti dalla Product Bible per le fasi 1–2**. Documentare i "non ora" e i "no" è parte della specifica: impedisce ai fantasmi di rientrare senza processo (FP §4.3) e dà al marketplace (fase 3) una mappa delle opportunità.

## 1. Regola generale

Un nuovo modulo first-party nasce solo se passa i 7 cancelli (FP §2) e le condizioni C-art. 187 (job documentato non servibile dall'esistente). Molti dei moduli qui sotto sono **candidati ideali per il marketplace di terze parti** (fase 3): verticali con nicchie appassionate, dove un sviluppatore dedicato servirà il job meglio di noi — e l'architettura a moduli esiste esattamente per questo (D-01).

## 2. Registro dei moduli candidati

| Modulo richiesto | Verdetto | Motivazione (tracciata) | Condizione di risveglio |
|---|---|---|---|
| **Diario / Journaling** | **Non ora → first-party anno 3** (roadmap PB 13) | Job reale (J17, adiacenza umore/S-02); sinergia forte col grafo; Day One invecchia (PB 05). Non-MVP: D-03 | Dopo Note+Salute a regime; il diario senza umore/contesto sarebbe un clone |
| **Documenti** (scansioni, contratti, ricevute) | **Non ora → candidato anno 3+ / marketplace** | Job vero ("dove ho messo il contratto?") ma: storage pesante sul nostro cloud E2E (costi), OCR on-device immaturo per qualità premium; FIN-013 (scontrini) copre l'intersezione finanziaria | Costi storage/OCR on-device compatibili; domanda organica misurata |
| **Password manager** | **NO first-party (permanente salvo emendamento)** | Fuori job (nessun J coperto: è sicurezza IT, non gestione della vita); competere con manager dedicati e portachiavi di sistema richiede un livello di assurance diverso persino dal nostro; un errore lì brucia la fiducia di TUTTO il prodotto (C-art. 178). Il no protegge il posizionamento | — |
| **Casa** (manutenzioni, utenze, garanzie) | **Non ora → marketplace ideale** | Job reale ma nicchia; l'80% è già componibile con l'esistente (task ricorrenti + spese ricorrenti + note collegate). Un modulo dedicato = superficie perpetua per un job servito all'80% (FP cancello 3) | Se i dati mostrano cluster massicci di task/spese "casa" (segnale di job sotto-servito) |
| **Auto / Veicoli** (scadenze, carburante, manutenzione) | **Non ora → marketplace ideale** | Come Casa: revisione=task ricorrente, bollo=spesa ricorrente, carburante=categoria. La nicchia appassionata (costo/km) merita un plugin verticale | Marketplace attivo |
| **Viaggi** (itinerari, documenti di viaggio, checklist) | **Non ora → marketplace / template** | Il job del viaggio è già il caso d'uso firma di GOAL (UC-06!); l'itinerario vive nel calendario; le checklist in TASK/NOTE. Prima di un modulo: **template di obiettivo "Viaggio"** (fase template gallery, anno 3) | Template misurati; domanda residua |
| **Inventario / Collezioni** | **Non ora → marketplace ideale** | Nicchia verticale per eccellenza (collezionisti, magazzino casa): perfetta per terze parti | Marketplace attivo |
| **Workspace / Progetti team** | **NO (permanente)** | Anti-persona A1 (PB 06); posizionamento personale è identità (D-01). Il "workspace" di coppia/famiglia è altra cosa: fase 4 con spazi condivisi selettivi | — |
| **Contatti / CRM personale** | **Non ora** | Job interessante ("ricordati di sentire nonna") parzialmente servito da task ricorrenti; il CRM personale completo tocca dati di terzi (i contatti) con implicazioni privacy delicate (C-art. 34) | Ricerca utente dedicata; design privacy convincente |
| **Meal planning / Ricette** | **Non ora → marketplace** | Verticale con app dedicate mature; l'intersezione (lista spesa, budget cibo) è già coperta | Marketplace |
| **Studio / Flashcards** | **Non ora → marketplace** | Persona Luca la userebbe, ma la spaced repetition è una scienza a sé (Anki); il nostro valore è il contorno (sessioni=time-boxing, esami=obiettivi) — già esistente | Marketplace |
| **Fitness / Allenamento** (schede, progressioni) | **NO first-party** | Il contenuto fitness è un altro mestiere (PB 04 §1: "il grosso del wellness è contenuto, fuori scope"); noi leggiamo l'esito (HLTH) | Marketplace per i verticali |

## 3. Che cosa questo documento garantisce

1. Ogni "no" e "non ora" qui sopra ha una motivazione tracciata ai documenti normativi: chi vuole riaprire una voce deve confutare la motivazione, non ripetere la richiesta (FP §4.3).
2. La lista delle **condizioni di risveglio** è parte del processo di revisione semestrale della Bible.
3. Per il marketplace (fase 3), questo registro è la **mappa delle opportunità** da offrire agli sviluppatori terzi: le nicchie sono loro, l'infrastruttura è nostra, l'utente vince due volte.

---

*Prossimo: [Matrici di tracciabilità](17-matrici.md)*
