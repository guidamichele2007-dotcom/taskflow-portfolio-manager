# 07 · Piano di Refactoring e Piano di Manutenzione

> Eredita [00](00-metodo-tracciabilita-definizioni.md)…[06](06-release-rollback-flag-config-migrazioni.md). Applica al codice la disciplina "la rimozione è una feature" già stabilita per il prodotto ([Product Constitution art. 105](../product_bible/15-product-constitution.md), [Feature Philosophy §4](../product_bible/09-feature-philosophy.md)).

## 1. Registro del Debito Tecnico

| Regola | Descrizione |
|---|---|
| **Ogni debito è dichiarato al momento in cui si contrae** | Nessun debito tecnico "silenzioso" — coerente con [Product Constitution art. 205](../product_bible/15-product-constitution.md): "il debito tecnico si dichiara, si registra e si ripaga: mai debito contratto in silenzio" |
| **Fonte tipica di debito ammesso** | Un flag di rollout non ancora rimosso oltre i 2 release train (§[06 §3](06-release-rollback-flag-config-migrazioni.md)); una Story Should implementata con la scheda sintetica prima che la scheda estesa sia completata (eccezione temporanea, mai permanente — Functional Bible §1.3) |
| **Gate di chiusura fase** | Il registro è revisionato a ogni confine di Fase (§[04 §4](04-roadmap-milestone-release-plan.md), "Gate di debito tecnico") — nessuna fase si chiude con debito non dichiarato |

## 2. Piano di Refactoring

| Trigger | Azione | Vincolo |
|---|---|---|
| **Una funzione Should/Could entra in sviluppo** | Verifica se un componente/modulo esistente può essere esteso invece di duplicato ([Design System Bible DS-INV-04](../design_system_bible/00-fondamenta.md), [Product Bible P61](../product_bible/08-product-principles.md): "migliorare l'esistente batte aggiungere il nuovo") | Il refactoring preventivo è preferito alla duplicazione, mai il contrario |
| **Revisione di sottrazione trimestrale** | Ogni modulo propone almeno una candidata alla rimozione/semplificazione, sulla base dei dati d'uso (già previsto in [Feature Philosophy §4](../product_bible/09-feature-philosophy.md)) | Applicato qui anche al codice: componenti UI o Task non più esercitati da alcuna Story attiva vengono rimossi, non lasciati "per sicurezza" |
| **Un Epic supera il proprio budget di superficie** | Refactoring per riportare l'Epic dentro i vincoli dichiarati (es. Home max 5 elementi per card, catalogo Impostazioni chiuso) | I budget non si alzano per comodità implementativa: si refattorizza per rispettarli |
| **Violazione di un invariante scoperta dopo il rilascio** | Refactoring immediato, priorità sopra ogni nuova Story (coerente con [Product Constitution art. 132](../product_bible/15-product-constitution.md): "un bug che perde dati ferma la linea") |

## 3. Piano di Manutenzione

| Attività | Cadenza | Riferimento |
|---|---|---|
| Revisione del registro rischi | Mensile in Fase 0-1, trimestrale dopo | Già stabilito, [doc 08-analisi-rischi](../08-analisi-rischi.md) |
| Revisione del registro di debito tecnico | Ad ogni confine di Fase | §1 |
| Audit di accessibilità (automatico + manuale) | Ad ogni release pubblica | Già stabilito, [UX Bible Accessibility Bible §10](../ux_bible/12-accessibility-bible.md) |
| Penetration test esterno | Prima del lancio pubblico, poi annuale | Già stabilito, doc tecnico sicurezza |
| Revisione delle regole dell'Insight Engine (config firmata) | Continua, senza richiedere release (già normato, INS-003) | — |
| Rimozione dei feature flag scaduti | Ad ogni release train | [06 §3](06-release-rollback-flag-config-migrazioni.md) |
| Verifica di compatibilità N-1 del Contratto di Modulo | Ad ogni release che tocca un contratto di modulo | [Technical Architecture Bible §11 §3](../technical_architecture_bible/11-versionamento-architettura.md) |
| Revisione dei budget di prestazione contro il device di riferimento | Ad ogni release pubblica (gate `PT`, [05 §4](05-pratiche-di-sviluppo.md)) | — |

## 4. Principio di chiusura

Refactoring e manutenzione non sono un'attività residuale: sono **la stessa disciplina di sottrazione** che governa il prodotto (Product Constitution art. 105, "la rimozione è una feature"), applicata al codice. Un Epic che accumula debito non dichiarato o componenti duplicati sta violando la stessa Constitution che il prodotto promette di rispettare — non è un problema "solo tecnico".

---

*Prossimo: [Report Finale](08-report.md)*
