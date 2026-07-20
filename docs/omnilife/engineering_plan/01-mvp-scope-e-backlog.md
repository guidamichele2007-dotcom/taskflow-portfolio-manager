# 01 · MVP Scope e Backlog Completo

> Eredita [00](00-metodo-tracciabilita-definizioni.md). MVP scope **non ridefinito**: è quello già stabilito da [Product Bible D-03](../product_bible/14-decision-log.md) (nucleo + Attività, Finanze, Abitudini, agenda in sola lettura) e quantificato in [Functional Bible §17 §3](../functional_bible/17-matrici.md) (78 funzioni Must). Questo documento organizza quelle funzioni, più le Should/Could, in **16 Epic**.

## 1. MVP Scope (richiamo, non ridefinizione)

**Release 1.0 (MVP) = tutte le funzioni Must** della Functional Bible (78 su 153, 51%) — appartengono esclusivamente a: Core, Cattura, Attività, Finanze, Abitudini, Calendario (sola lettura), Ricerca, Notifiche, Widget (base), Sync/Backup/Export, Impostazioni/Sicurezza. **Nessuna funzione Must dipende da una Should** (verificato in Functional Bible §17 §3) — proprietà che rende possibile sviluppare l'MVP come sottoinsieme coerente e stand-alone.

## 2. Le 16 Epic

| Epic | Modulo/capacità | Release che copre | Story totali (Must/Should/Could) |
|---|---|---|---|
| `EPIC-00` | Fondamenta Architetturali (nessuna funzione utente diretta) | Prerequisito di 1.0 | 5 `ENG-*` |
| `EPIC-CORE` | Core: Home, Onboarding, Galleria, Revisione | 1.0 (Home/Onboarding/Galleria) + 1.x (Revisione) | 25 (18M/5S/2C — vedi Functional Bible doc 01) |
| `EPIC-CAPT` | Cattura | 1.0 (base) + 1.x (voce/scorciatoie) | 10 (6M/4S) |
| `EPIC-TASK` | Attività | 1.0 (base) + 1.x (obiettivi/time-boxing) + 2.x (duplica) | 18 (13M/4S/1C) |
| `EPIC-FIN` | Finanze | 1.0 (base) + 1.x (risparmi/import) + 2.x (multi-valuta/scontrino) | 14 (9M/3S/2C — FIN-009 C→S per mercati non-euro) |
| `EPIC-HAB` | Abitudini | 1.0 (base) + 1.x (ridimensionamento/integrazioni) + 2.x (nota giorno) | 13 (8M/4S/1C) |
| `EPIC-CAL` | Calendario | 1.0 (lettura) + 1.x (scrittura/time-boxing) | 7 (3M/4S) |
| `EPIC-NOTE` | Note | 1.x (base) + 2.x (immagini/vocale) | 9 (7S/2C) |
| `EPIC-HLTH` | Salute | 1.x (lettura/auto-completamento) + 2.x (metriche/storico) | 5 (3S/2C) |
| `EPIC-GOAL` | Obiettivi | 1.x (base) + 2.x (milestone) | 8 (7S/1C) |
| `EPIC-SRCH` | Ricerca | 1.0 (base) + 1.x (filtri contestuali) | 6 (5M/1S) |
| `EPIC-NTF` | Notifiche | 1.0 (base) + 1.x (digest/auto-disattivazione) | 8 (5M/3S) |
| `EPIC-WID` | Widget | 1.0 (base) + 1.x/2.x (estesi) | 7 (4M/2S/1C) |
| `EPIC-SYNC` | Sync/Backup/Export | 1.0 (quasi interamente Must) + 1.x (ripristino puntuale/export selettivo) | 10 (8M/2S) |
| `EPIC-SET` | Impostazioni/Sicurezza | 1.0 (quasi interamente Must) + 1.x (2FA) | 8 (7M/1S) |
| `EPIC-INS` | Insight | 1.x (base) + 2.x (correlazioni) | 5 (4S/1C) |

**Totale**: 153 Story di prodotto (Functional Bible) + 5 Story di fondazione (`ENG-*`) = **158 Story**, organizzate in **~42 Feature** (matrice completa in [02](02-matrici-epic-feature-story-task.md)).

## 3. EPIC-00 — Fondamenta Architetturali (dettaglio, nuovo)

L'unico Epic senza corrispondenza diretta in Functional Bible — deriva da [Technical Architecture Bible](../technical_architecture_bible/README.md), [Data Model Bible §00](../data_model_bible/00-modello-dati-comune.md), [Design System Bible §00-04](../design_system_bible/00-fondamenta.md). **Prerequisito bloccante per ogni altro Epic** (§[03](03-dipendenze-grafo-percorso-critico.md)).

| Story | Descrizione | Fonte |
|---|---|---|
| `ENG-00-1` | Scaffold a 6 layer + meccanismo di verifica delle dipendenze vietate | [Technical Architecture Bible §01-02](../technical_architecture_bible/01-architettura-generale-e-layer.md) |
| `ENG-00-2` | Core Services skeleton: Bus Eventi, Grafo/GraphLink, Registro Moduli | [Technical Architecture Bible §03-04](../technical_architecture_bible/03-event-driven-architecture.md) |
| `ENG-00-3` | Involucro comune delle entità, ciclo di vita, versionamento per-campo | [Data Model Bible §00](../data_model_bible/00-modello-dati-comune.md) |
| `ENG-00-4` | Implementazione dei token di base e shell dei componenti di libreria | [Design System Bible §00-06](../design_system_bible/README.md) |
| `ENG-00-5` | Confine di sicurezza (cifratura, gerarchia chiavi — collocazione) | [Technical Architecture Bible §10](../technical_architecture_bible/10-sicurezza-architetturale.md) |

## 4. Elenco Feature per Epic (sintesi — matrice completa in [02](02-matrici-epic-feature-story-task.md))

| Epic | Feature (raggruppamenti nuovi di questa Bible) |
|---|---|
| `EPIC-CORE` | Home "Oggi" · Onboarding · Galleria Moduli · Revisione Settimanale |
| `EPIC-CAPT` | Cattura Testuale e Parser · Cattura Multicanale |
| `EPIC-TASK` | Creazione e Gestione Base · Organizzazione (Liste/Aree) · Ricorrenze · Viste e Gestione Massiva · Integrazione Grafo e Calendario |
| `EPIC-FIN` | Registrazione Transazioni · Conti e Trasferimenti · Categorie e Budget · Ricorrenze e Report · Risparmi/Import/Multi-valuta |
| `EPIC-HAB` | Abitudini Base · Costanza Resiliente · Storico e Integrazioni |
| `EPIC-CAL` | Lettura Agenda · Scrittura e Time-boxing |
| `EPIC-NOTE` | Editor e Versioni · Organizzazione e Grafo |
| `EPIC-HLTH` | Lettura Piattaforma e Auto-completamento · Metriche Manuali |
| `EPIC-GOAL` | Ciclo di Vita Obiettivo · Aggregazione e Grafo |
| `EPIC-SRCH` | Ricerca Globale · Filtri Contestuali |
| `EPIC-NTF` | Broker e Budget · Azionabilità e Governo |
| `EPIC-WID` | Widget Base · Widget Estesi |
| `EPIC-SYNC` | Sincronizzazione · Backup e Ripristino · Export e Cancellazione |
| `EPIC-SET` | Account e Sicurezza · Impostazioni e Abbonamento |
| `EPIC-INS` | Motore e Insight Contestuali · Digest e Controllo Utente |

## 5. Perché 16 Epic e non uno per fase

Un Epic non è legato a una release: **EPIC-TASK** contiene sia Story 1.0 sia 1.x sia 2.x. Questo è deliberato — mantiene l'Epic ancorato al **modulo** (unità architetturale stabile, [Technical Architecture Bible §02](../technical_architecture_bible/02-moduli-responsabilita-boundaries.md)), mentre la sequenza temporale (quali Story quando) è governata dalla roadmap ([04](04-roadmap-milestone-release-plan.md)) e dal percorso critico ([03](03-dipendenze-grafo-percorso-critico.md)), non dalla struttura del backlog.

---

*Prossimo: [Matrici Epic → Feature → Story → Task](02-matrici-epic-feature-story-task.md)*
