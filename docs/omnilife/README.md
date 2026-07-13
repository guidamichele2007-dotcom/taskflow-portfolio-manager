# OmniLife — Documentazione Tecnica di Progetto

> **Versione:** 1.0 · **Stato:** Approvata per fase di design dettagliato · **Data:** 2026-07-13

OmniLife è un ecosistema mobile personale, modulare e Offline-First, che unifica la gestione della vita quotidiana (attività, finanze, salute, abitudini, note, calendario) in un'unica esperienza Premium, senza diventare una "super-app" confusa: ogni modulo è indipendente, attivabile a scelta, e perfettamente integrato con gli altri.

Questa cartella contiene la base progettuale completa che guida tutte le fasi successive dello sviluppo. **Nessun codice viene prodotto in questa fase**, per scelta deliberata: ogni riga di codice scritta prima di aver validato visione, architettura e priorità è un costo, non un progresso.

---

> **Nota sulla gerarchia**: l'identità, la strategia e le regole inviolabili del prodotto sono definite nella **[Product Bible](product_bible/README.md)** (`product_bible/`), che prevale su questa documentazione tecnica in caso di conflitto. Questa cartella descrive *come* costruire; la Bible stabilisce *perché e che cosa non si fa mai*.

## Indice dei documenti

| # | Documento | Contenuto |
|---|-----------|-----------|
| 01 | [Visione e Analisi](01-visione-e-analisi.md) | Visione del prodotto, analisi del problema, analisi dei competitor, definizione degli utenti (personas), casi d'uso |
| 02 | [Funzionalità e Priorità](02-funzionalita-e-priorita.md) | Elenco completo delle funzionalità per modulo, matrice di priorità (RICE + MoSCoW), motivazioni |
| 03 | [Architettura](03-architettura.md) | Architettura ad alto livello, modularità, sistema di plugin, sync Offline-First, versionamento, API |
| 04 | [UX, Design e Navigazione](04-ux-design-navigazione.md) | Design system, flussi utente, struttura di navigazione, accessibilità, psicologia comportamentale |
| 05 | [Requisiti](05-requisiti.md) | Requisiti funzionali (RF) e non funzionali (RNF), misurabili e verificabili |
| 06 | [Sicurezza e Privacy](06-sicurezza-e-privacy.md) | Modello di minaccia, crittografia, biometria, backup/ripristino, conformità GDPR/CCPA |
| 07 | [Monetizzazione, Crescita e Lancio](07-business-crescita-lancio.md) | Piano di monetizzazione, piano di crescita, strategia di lancio, ASO (App Store + Google Play) |
| 08 | [Analisi dei Rischi](08-analisi-rischi.md) | Registro dei rischi con probabilità, impatto e mitigazioni |
| 09 | [Piano di Sviluppo](09-piano-di-sviluppo.md) | Fasi di sviluppo, milestone, criteri di uscita, strategia QA e DevOps |

---

## Principi non negoziabili

Questi principi sono vincolanti per ogni decisione futura. Qualsiasi proposta che li viola deve essere respinta o motivata per iscritto.

1. **Modularità reale.** Ogni modulo può essere aggiunto, rimosso e aggiornato senza compromettere gli altri. Nessuna dipendenza diretta tra moduli: comunicano solo attraverso contratti (eventi e API interne).
2. **Offline-First.** L'app è pienamente funzionante senza rete. La sincronizzazione è un miglioramento, mai un requisito.
3. **Meno tocchi possibile.** Ogni flusso è progettato misurando il numero di interazioni. Un flusso che richiede più tocchi dell'alternativa migliore sul mercato non è accettabile.
4. **Nessuna schermata inutile, nessuna funzione duplicata.** Ogni funzione deve avere un motivo documentato per esistere (vedi doc 02).
5. **Privacy by design.** I dati sensibili sono cifrati end-to-end; il modello di business non dipende mai dai dati dell'utente.
6. **Qualità Premium.** 60/120 fps costanti, avvio a freddo < 1,5 s, accessibilità completa (WCAG 2.2 AA), Dark/Light Mode nativi.
7. **Non copiare: superare.** Studiamo i punti di forza dei competitor per superarli con scelte originali, mai per replicarli.

## Come leggere questa documentazione

- Chi deve capire **perché** esiste il prodotto: doc 01.
- Chi deve capire **cosa** costruire e in che ordine: doc 02 e 09.
- Chi deve capire **come** costruirlo: doc 03, 05, 06.
- Chi deve capire **come apparirà e si userà**: doc 04.
- Chi deve capire **come diventa un business**: doc 07 e 08.
