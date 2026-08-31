# 06 · Release Strategy, Rollback, Feature Flag, Configurazioni, Migrazioni

> Eredita [00](00-metodo-tracciabilita-definizioni.md), [04](04-roadmap-milestone-release-plan.md), [05](05-pratiche-di-sviluppo.md). Nessuno strumento di feature-flagging, nessun database di configurazione fisico scelto.

## 1. Release Strategy

Cadenza già stabilita in [04 §3](04-roadmap-milestone-release-plan.md) (train interno 2 settimane, pubblico 3 settimane). Regole aggiuntive di esecuzione:

| Regola | Descrizione |
|---|---|
| **Rollout graduale obbligatorio** | 5%→25%→100% per ogni release pubblica (già stabilito, doc 09) — nessuna release salta questa sequenza, incluse le release che toccano un solo Epic |
| **Criterio di avanzamento tra soglie** | Nessuna regressione sui criteri MFC-AC universali (§[05 §4](05-pratiche-di-sviluppo.md)) e sui budget di prestazione della funzione toccata, osservata per almeno 24h alla soglia corrente prima di avanzare |
| **Composizione per Epic, non per singola Story** | Una release non contiene mai una Feature parzialmente completa (nessuna Story Must di una Feature attiva resta fuori) — evita stati intermedi non testati end-to-end |
| **Congelamento pre-release** | Nelle 48h precedenti una release pubblica, solo correzioni (nessuna nuova Story), coerente con la disciplina "qualità non è una fase" (doc 09 §Principi) |

## 2. Rollback Strategy

| Livello | Meccanismo | Trigger |
|---|---|---|
| **Rollback di rollout** | Interruzione della progressione 5%→25%→100%, ritorno alla soglia precedente o a 0% | Regressione su MFC-AC o su un budget di prestazione dichiarato |
| **Rollback applicativo** | Disattivazione del modulo/Feature via feature flag (§3) senza richiedere una nuova release | Difetto isolabile a un singolo Epic/Feature, il resto dell'app resta operativo (coerente con [Technical Architecture Bible §07 §3](../technical_architecture_bible/07-gestione-errori.md): non propagazione a cascata) |
| **Rollback di dati** | Ripristino da snapshot (BKP-004, già specificato in Functional Bible) — **mai** un rollback di schema che perda dati scritti nel frattempo: ogni migrazione (§5) è concepita per essere reversibile senza perdita |
| **Comunicazione** | Ogni rollback applicativo è silenzioso per l'utente se possibile (l'app degrada, non si interrompe); un rollback di dati (raro) segue il pattern onesto già definito in [UX Bible, Error Experience](../ux_bible/10-error-experience.md) |

**Regola cardine**: **nessun rollback può violare "mai perdere un dato confermato"** (Product Constitution art. 3) — un rollback applicativo (disattiva Feature) è sempre preferito a un rollback di dati; quest'ultimo è l'ultima risorsa, mai il meccanismo di prima linea.

## 3. Gestione Feature Flag

| Aspetto | Regola |
|---|---|
| **Un flag per Epic/Feature, non per Story** | Coerente con "composizione per Epic" (§1) — evita una proliferazione di flag a grana fine che nessuno spegnerebbe mai (debito) |
| **Governance** | Il Registro Moduli ([Technical Architecture Bible §04 §3](../technical_architecture_bible/04-plugin-architecture.md), `DM-SYS-03 ModuleActivation`) è il meccanismo **già esistente** per disattivare un intero modulo — i feature flag di rollout sono un meccanismo distinto e temporaneo, usato solo durante il rollout graduale (§1), non come sostituto della disattivazione utente-facing dei moduli |
| **Kill-switch** | Ogni Epic corrispondente a un modulo eredita il kill-switch già previsto per il marketplace ([Technical Architecture Bible §04 §4](../technical_architecture_bible/04-plugin-architecture.md)) — applicabile fin da subito ai moduli interni, non solo ai futuri plugin di terze parti |
| **Rimozione del flag** | Ogni flag di rollout si rimuove entro 2 release train dal raggiungimento del 100% stabile — un flag permanente è debito tecnico (tracciato in [07](07-refactoring-e-manutenzione.md)) |
| **Nessun flag lato utente confuso con le Impostazioni** | Il catalogo Impostazioni resta quello chiuso di [Functional Bible SET-001 §2](../functional_bible/14-impostazioni-profilo-sicurezza.md) — i feature flag sono infrastruttura interna, mai esposti come opzione utente (che violerebbe SET-R-01) |

## 4. Gestione delle Configurazioni

Distinzione esplicita da non confondere (coerente con [Design System Bible §08 report](../design_system_bible/08-report.md) e [Technical Architecture Bible §09](../technical_architecture_bible/09-osservabilita-logging-telemetria.md)):

| Tipo | Esempi | Dove vive | Chi la cambia |
|---|---|---|---|
| **Impostazioni utente** | Catalogo chiuso SET-001 §2 (già specificato, non ridefinito qui) | Per account/dispositivo (Data Model Bible `DM-SYS-06`) | L'utente |
| **Configurazione applicativa** | Budget di notifiche di default, soglie di prestazione, parametri regionali (valuta/formato data di default) | Build/ambiente | Il team, per ambiente (non per singolo utente) |
| **Configurazione di regole dichiarative** | Regole dell'Insight Engine (INS-003, "config firmata scaricabile") | Registry esterno, sola lettura per l'app | Il team, distribuita senza richiedere una nuova release dell'app |
| **Feature flag** | Vedi §3 | Registro Moduli / meccanismo di rollout | Il team, temporaneo |

**Regola**: nessuna configurazione applicativa o di regole è mai scritta con permessi utente-modificabili al di fuori del catalogo Impostazioni — altrimenti diventerebbe silenziosamente una nuova Impostazione non passata dai 7 cancelli della Feature Philosophy (Product Bible doc 09).

## 5. Piano di Migrazioni (esecuzione)

La **politica** di migrazione è già normata in [Data Model Bible §7](../data_model_bible/00-modello-dati-comune.md) (INV-09, versione di schema mai retrocede) e [Technical Architecture Bible §11 §4](../technical_architecture_bible/11-versionamento-architettura.md) (isolamento per modulo, orchestrazione dell'ordine dal Core). Qui il **piano di esecuzione**:

| Fase | Attività |
|---|---|
| **Pre-migrazione** | Snapshot automatico locale (già richiesto, MFC-E-15); verifica che la migrazione sia transazionale e riprendibile (requisito, non opzione) |
| **Ordine** | Il Registro Moduli orchestra: `EPIC-00` (schema dei servizi Core) sempre prima; poi ogni modulo migra le proprie entità in isolamento, senza ordine reciproco imposto (nessuna dipendenza dati tra moduli, coerente con [Technical Architecture Bible §02](../technical_architecture_bible/02-moduli-responsabilita-boundaries.md)) |
| **Fallimento parziale** | Un modulo la cui migrazione fallisce risulta "non disponibile" (stato coerente con `ModuleActivation`), gli altri moduli procedono — mai un blocco totale dell'avvio (§[Technical Architecture Bible §11 §4](../technical_architecture_bible/11-versionamento-architettura.md)) |
| **Verifica post-migrazione** | Il set completo `MFC-AC-01…08` gira come suite di regressione dopo ogni migrazione, non solo i test specifici del modulo |
| **Rollback di migrazione** | Ripristino dallo snapshot pre-migrazione (§2) se la verifica fallisce — mai un tentativo di "migrazione inversa" automatica non testata |

---

*Prossimo: [Piano di Refactoring e Manutenzione](07-refactoring-e-manutenzione.md)*
