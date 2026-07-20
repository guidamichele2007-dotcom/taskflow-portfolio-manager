# 05 · Offline-First, Sincronizzazione Logica, Caching (concettuale)

> Eredita [00](00-principi-architetturali.md)…[04](04-plugin-architecture.md). Il comportamento di sincronizzazione **è già interamente specificato** in [Functional Bible MFC §3](../functional_bible/00-modello-funzionale-comune.md#3-comportamento-offline--online--sync-universale) e [Data Model Bible §8/11](../data_model_bible/00-modello-dati-comune.md#8-sincronizzazione-logica-e-gestione-dei-conflitti-concettuale--eredita-mfc-3-d-02adr-3): questo documento **non ripete quelle regole**, ne descrive solo la collocazione architetturale (dove vive ogni responsabilità, quali componenti la implementano, quali porte espone).

## 1. Offline-first come vincolo architetturale, non come feature

"Offline è lo stato normale" (Product Bible, principio cardinale 2) impone che **ogni caso d'uso di L2/L3 sia eseguibile senza che L4/L5 raggiungano mai la rete**, salvo le sole quattro eccezioni già dichiarate (MFC §3: registrazione, ripristino cloud, acquisto, download di un modulo on-demand). Architetturalmente, questo si traduce in: **il Dominio (L3) non ha mai una dipendenza — nemmeno indiretta — da un componente che richiede una connessione di rete per rispondere.** La persistenza locale (una porta di L5) è sufficiente per ogni caso d'uso ordinario.

## 2. Il Motore di Sincronizzazione come Servizio Core (L4)

Il Motore di Sincronizzazione è un servizio di L4 che **non contiene regole di dominio**: osserva le modifiche prodotte dai moduli (tramite la stessa porta di persistenza usata per scrivere localmente, non tramite il Bus Eventi — §[03 §5](03-event-driven-architecture.md)), le accoda (outbox, MFC §3), e le scambia con il confine L6 quando la rete è disponibile.

```
┌─────────────┐  scrive localmente   ┌──────────────────┐
│ Dominio (L3)│ ───────────────────► │ Porta Persistenza │
└─────────────┘                      └─────────┬────────┘
                                                │ osserva le scritture
                                      ┌─────────▼────────┐
                                      │ Motore di Sync    │  L4
                                      │ (outbox persistente,
                                      │  priorità dati "caldi")
                                      └─────────┬────────┘
                                                │ quando la rete è disponibile
                                      ┌─────────▼────────┐
                                      │ Adattatore di Sync │  L5
                                      │ (cifra, trasmette) │
                                      └─────────┬────────┘
                                                │ solo blob cifrati + metadati
                                      ┌─────────▼────────┐
                                      │  L6 Confine esterno│
                                      │  (content-blind)   │
                                      └───────────────────┘
```

**Principio di collocazione**: la cifratura avviene **prima** che il Motore di Sync (L4) consegni i dati all'Adattatore (L5) — l'Adattatore non vede mai testo in chiaro, per costruzione (confine di fiducia, §[10-sicurezza-architetturale](10-sicurezza-architetturale.md)).

## 3. Gestione dei conflitti: dove vive la logica, non come funziona

La logica di convergenza (per-campo, per-insieme, a snapshot) è già interamente definita nel [Data Model Bible §8](../data_model_bible/00-modello-dati-comune.md) e [§11.6](../data_model_bible/11-versionamento-e-sincronizzazione.md#6-modello-concettuale-di-convergenza-per-tipo-di-campo-crdt-senza-scegliere-unimplementazione). Collocazione architetturale:

- La **convergenza per-campo/per-insieme** è responsabilità del Motore di Sync (L4) — è indipendente dal modulo (funziona identica per Task, Transaction, Habit…), quindi vive correttamente in un Servizio Core condiviso e non in ogni Dominio separatamente (evita duplicazione, P31).
- Il **calcolo dei valori derivati** (saldo, aderenza, progresso obiettivo) **non** partecipa mai alla convergenza come valore sincronizzato: resta nel Dominio proprietario (L3), che lo ricalcola localmente dopo che i dati sorgente sono convertiti (Data Model Bible, VCB-05). Questo significa architetturalmente che il Motore di Sync **non conosce** l'esistenza di "saldo" o "aderenza" — sincronizza solo Transaction ed HabitExecution, mai i loro derivati.
- Nessuno stato di conflitto attraversa mai L2/L1 (Data Model Bible, INV-08) — il Motore di Sync risolve internamente a L4, senza mai richiedere una decisione a L2.

## 4. Caching strategy (concettuale)

Tre categorie di "cache", ciascuna con collocazione e ciclo di vita distinti — nessuna delle tre è una fonte di verità:

| Categoria | Che cosa | Dove vive | Ricostruibilità |
|---|---|---|---|
| **Indice di ricerca** | Proiezione derivata da titolo/contenuto/categoria di ogni entità dei moduli attivi | L4 (Servizio Ricerca) | Interamente ricostruibile dalle entità originali senza perdita (Data Model Bible §11); uno stato "degradato/in ricostruzione" è previsto (MFC §4) |
| **Cache della piattaforma Salute** | Finestra di 90 giorni di letture della piattaforma di sistema | L5 (Adattatore Salute) o al confine L3/L5, **mai in L4/L6** | Non è una cache nel senso classico: è l'unica copia locale ammessa, e non è mai sincronizzata (deroga architetturale che rispecchia HLTH-R-02) |
| **Valori derivati** (saldo, aderenza, progresso) | Calcolati da L3 a partire dai dati sorgente | L3 (Dominio proprietario) | Sempre ricalcolabili dai dati sorgente; la cadenza esatta (on-write vs on-read) è una decisione di implementazione rinviata |

**Principio guida** (già enunciato in [00 §5](00-principi-architetturali.md)): se un valore può essere calcolato da altri dati già persistiti, l'architettura lo tratta come cache, mai come fonte di verità sincronizzata — questo elimina per costruzione un'intera classe di conflitti di sincronizzazione.

## 5. Le quattro eccezioni online: collocazione architetturale

| Eccezione (MFC §3) | Componente coinvolto | Comportamento a rete assente |
|---|---|---|
| Registrazione account | Adattatore di Sync (L5) verso L6, tramite Servizio Sicurezza (L4) | L'app resta pienamente utilizzabile in modalità anonima (Data Model Bible §5.1) |
| Ripristino da cloud | Motore di Backup (L4) → Adattatore (L5) → L6 | Nessun dato locale preesistente da perdere: l'operazione semplicemente non può iniziare, stato esplicito (UX Bible, [09-empty-states](../ux_bible/09-empty-states.md)) |
| Acquisto abbonamento | Adattatore di piattaforma commerciale (L5, gestito dal sistema operativo) | Il piano corrente resta valido; nessun dato bloccato (Data Model Bible, MDC §6 stato Free) |
| Download di un modulo on-demand | Registro Moduli (L4) → Adattatore di distribuzione (L5) | Il modulo resta "dichiarato" ma non attivabile finché la rete non è disponibile una tantum |

## 6. Che cosa resta esplicitamente fuori da questo documento

Il tipo esatto di struttura dati per la convergenza (CRDT state-based vs operation-based), il protocollo di trasporto verso L6, la cadenza esatta di retry/backoff, il formato dei vettori di versione: tutte decisioni di implementazione, esplicitamente rinviate ([15-report](15-report.md)) — questa Bible fissa **dove** vive ogni responsabilità, non **come** è implementata.

---

*Prossimo: [Lifecycle di Richieste ed Eventi](06-lifecycle-richieste-ed-eventi.md)*
