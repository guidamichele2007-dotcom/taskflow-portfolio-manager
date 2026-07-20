# 03 · Event-Driven Architecture

> Eredita [00](00-principi-architetturali.md), [01](01-architettura-generale-e-layer.md), [02](02-moduli-responsabilita-boundaries.md). Formalizza architetturalmente il "bus eventi" già specificato funzionalmente in [Functional Bible MFC §8](../functional_bible/00-modello-funzionale-comune.md#8-modello-degli-eventi-bus-interno) e catalogato per entità nel [Data Model Bible §13.4](../data_model_bible/13-erm-e-matrici.md#4-matrice-entità--eventi).

## 1. Perché event-driven

L'architettura a eventi è l'unico modo strutturale di rispettare simultaneamente due vincoli già stabiliti: **nessun modulo conosce un altro modulo** (C-art. 184) e **il valore del prodotto nasce dall'integrazione tra moduli** (Product Bible, P2, J5). Un modulo pubblica fatti accaduti nel proprio dominio; altri moduli, se interessati, reagiscono — senza che il publisher sappia chi (se qualcuno) sta ascoltando.

## 2. Collocazione architetturale del Bus Eventi

Il Bus Eventi è un **Servizio Core di L4**, non appartiene a nessun modulo. Ogni modulo di L3 vi accede tramite due porte minime (Interface Segregation, §[00.2](00-principi-architetturali.md)):

- **Porta di pubblicazione**: "pubblica un evento di tipo X con payload minimo" — il modulo non sa (e non deve sapere) chi lo riceverà.
- **Porta di sottoscrizione**: "ricevi eventi di tipo Y" — il modulo dichiara staticamente a quali tipi di evento è interessato (coerente con "se non è dichiarato qui, non esiste", C-art. 184; nella Functional Bible ogni modulo dichiara "Eventi: Pubblica / Sottoscrive" nella propria scheda).

## 3. Forma di un evento (concettuale, non un formato di serializzazione)

Coerente con Functional Bible MFC §8: `modulo.entita.azione` (es. `fin.expense.created`, `task.item.completed`, `hab.adherence.band.changed`). Il payload è minimo per costruzione: **identificatore e tipo dell'entità, timestamp** — mai il contenuto completo (C-art. 7: mai contenuti nei trasporti che potrebbero attraversare confini non fidati; anche localmente, il principio di minimizzazione si applica per coerenza architetturale). Un consumer che ha bisogno del contenuto lo richiede attraverso la porta di lettura del Dominio proprietario (mai attraverso il payload dell'evento) — questo mantiene il modulo proprietario come unica fonte di verità (Data Model Bible, ownership).

## 4. Lifecycle di un evento (attraverso i layer)

```
1. ACCADIMENTO
   Un caso d'uso in L3 (es. Task.completa()) modifica lo stato del Dominio
   e persiste la modifica (invariante rispettato, es. MFC-R-01: locale-prima)
                    │
2. PUBBLICAZIONE
   Il modulo pubblica l'evento sul Bus (L4) — operazione sincrona e locale,
   MAI di rete (MFC §8: "gli eventi sono locali al dispositivo")
                    │
3. DISTRIBUZIONE
   Il Bus consegna l'evento a ogni sottoscrittore dichiarato E ATTIVO
   (verifica contro il Registro Moduli, DM-SYS-03 — un modulo disattivato
   non riceve nulla, senza errore: "tollera silenziosamente l'assenza")
                    │
4. REAZIONE (0..N consumer, in parallelo logico, senza garanzia d'ordine
   tra consumer diversi — ma con garanzia d'ordine di pubblicazione per
   singolo producer)
   Ogni consumer esegue la propria reazione nel proprio Dominio (es. GOAL
   ricalcola il progresso, INS osserva, HOME aggiorna la propria proiezione
   in L2) — MAI una scrittura diretta nel Dominio del producer
                    │
5. EVENTUALE RI-PUBBLICAZIONE
   Una reazione può generare un nuovo evento (es. il completamento di un
   Task può generare goal.progress.changed) — la catena è aciclica per
   costruzione: un modulo non sottoscrive mai un evento che lui stesso,
   direttamente o indirettamente, potrebbe generare in risposta allo
   stesso evento (regola architetturale esplicita, per prevenire loop)
```

## 5. Eventi e sincronizzazione: due canali distinti (già dichiarato in MFC §8, qui formalizzato architetturalmente)

**Il Bus Eventi non è il meccanismo di sincronizzazione multi-dispositivo.** I due canali sono architetturalmente separati:

| Canale | Cosa trasporta | Ambito | Persistenza |
|---|---|---|---|
| Bus Eventi (L4) | Notifiche di fatto accaduto, per reazioni locali immediate | Un solo dispositivo, in memoria/effimero | Non persistito come log — è un meccanismo di notifica, non un event-store |
| Motore di Sincronizzazione (L4, [05](05-offline-first-sincronizzazione-caching.md)) | Stato (entità convergenti), non eventi | Multi-dispositivo | Persistito (outbox, MFC §3) |

Dopo un merge di sincronizzazione su un secondo dispositivo, gli eventi rilevanti **si rigenerano localmente** dal nuovo stato risultante (es. `goal.progress.changed` si ripubblica su quel dispositivo dopo che i dati sincronizzati hanno cambiato il progresso) — mai trasportati come eventi attraverso la rete (MFC §8, ultima riga).

## 6. Lifecycle di una richiesta (end-to-end, attraverso i layer)

Esempio di riferimento: l'utente completa un Task dalla Home (coerente con [UX Bible FLOW-HOME-01](../ux_bible/04-user-flows-core-mvp.md#flow-home-01--apertura-app-e-vista-home-oggi-home-001004)).

```
L1  Utente tocca "completa" sulla card Home
      │  (invoca il caso d'uso esposto da L2, nessuna logica qui)
L2  Applicazione riceve l'intenzione, la instrada al modulo proprietario
      │  (Attività) tramite il contratto del caso d'uso "completa Task"
L3  Dominio Attività:
      - verifica gli invarianti (es. gestione sottotask aperti, TASK-AC-03)
      - applica la regola di business (TASK-008, idempotenza per ID
        occorrenza)
      - aggiorna lo stato dell'entità Task (Data Model Bible §6/7:
        transizione di stato + voce di cronologia)
      - persiste (tramite porta di persistenza verso L5)
      - pubblica `task.item.completed` sul Bus (L4)
L4  Bus Eventi distribuisce l'evento a: Obiettivi (ricalcolo progresso,
    se collegato), Insight (osservazione locale), Motore di Sync (marca
    l'entità come modificata per la prossima convergenza)
L2  Applicazione (sottoscritta anch'essa, per la composizione Home)
    aggiorna la proiezione osservabile della card
L1  Esperienza riceve il nuovo stato osservabile e anima la transizione
    (checkbox riempito, riga attenuata — UX Bible §7 Microinterazioni)
      │
    (tutto quanto sopra: ≤ 50ms percepiti end-to-end, coerente con
    TASK-008 scheda estesa: "latenza percepita ≤ 50 ms")
```

Questo esempio mostra perché la separazione L1/L2/L3/L4 non è astratta: la latenza percepita dichiarata dalla Functional Bible (TASK-008) è raggiungibile solo se nessun passaggio richiede rete o un round-trip verso un confine esterno — l'intero ciclo sopra descritto è **locale al dispositivo**.

## 7. Comunicazione tra moduli: solo due canali, mai un terzo

Coerente con [02 §2](02-moduli-responsabilita-boundaries.md): **Bus Eventi** (fatti, reazioni, effimero) e **GraphLink** (relazioni persistenti tra entità, Data Model Bible §4.2). Nessun modulo comunica con un altro tramite chiamata diretta, variabile condivisa, o lettura diretta di storage altrui — questa è una regola architetturale verificabile, non una convenzione (decisione di come verificarla meccanicamente: rinviata, [15-report](15-report.md)).

---

*Prossimo: [Plugin Architecture](04-plugin-architecture.md)*
