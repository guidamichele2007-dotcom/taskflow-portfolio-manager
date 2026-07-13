# 08 · Analisi dei Rischi

> Metodo: registro dei rischi con probabilità (P) e impatto (I) su scala 1–5, esposizione = P×I, owner, strategia (mitigare/trasferire/accettare/evitare), mitigazioni concrete e **segnali di allarme anticipati** (leading indicators). Il registro è vivo: revisione mensile in fase di sviluppo, trimestrale a regime.

## 1. Rischi di prodotto e mercato

| ID | Rischio | P | I | Esp. | Mitigazioni | Segnali di allarme |
|----|---------|---|---|------|-------------|--------------------|
| R-01 | **"Tuttofare = mediocre"**: la percezione che un'app multi-dominio non possa avere profondità sufficiente per battere gli specialisti | 4 | 5 | 20 | MVP con soli 3 moduli fatti in modo eccellente (doc 02 §3.2); benchmark pubblici contro il leader di ogni categoria; la Galleria comunica "componi la TUA app", non "abbiamo tutto" | Recensioni "carina ma uso ancora Todoist"; retention per modulo < specialisti |
| R-02 | **Onboarding multi-modulo confonde** e il time-to-value supera i 60 s | 3 | 4 | 12 | Onboarding a 1–2 moduli con default forte; test di usabilità dal prototipo (fase 0); misura D0→prima cattura | Completamento onboarding < 80%; drop alla scelta moduli |
| R-03 | **Retention da categoria** (i life-planner hanno churn alto strutturale) | 4 | 4 | 16 | Costanza gentile (H-03/H-04), widget, digest, revisione settimanale; la metrica nord è la retention degli utenti "integrati" (≥ 2 moduli) | D30 < 15% in beta; utenti mono-modulo che non espandono |
| R-04 | **Nicchia privacy troppo piccola per sostenere il business** (la massa non paga per l'E2E) | 3 | 3 | 9 | La privacy è il differenziatore per Marco/PR, ma la proposta di valore di massa è "tutta la vita in un'app in 3 secondi"; il marketing guida col beneficio, la privacy è la prova di fiducia | Conversion della landing per messaggio; interviste utente |
| R-05 | **Un incumbent copia l'idea** (es. TickTick/Notion lanciano moduli integrati) | 3 | 3 | 9 | La difesa è strutturale: E2E non retrofittabile per chi monetizza dati (doc 01 §3.3); velocità di esecuzione; community | Annunci competitor; assunzioni |
| R-06 | **Willingness-to-pay più bassa del previsto** | 3 | 4 | 12 | Pricing survey in beta (Van Westendorp); prezzi regionali; leva Lifetime per gli avversi all'abbonamento; costo infra basso per design = margine di manovra sul prezzo | Trial→paid < 25%; recensioni sul prezzo |

## 2. Rischi tecnici

| ID | Rischio | P | I | Esp. | Mitigazioni | Segnali di allarme |
|----|---------|---|---|------|-------------|--------------------|
| R-10 | **Complessità del motore CRDT+E2E** (il cuore tecnico): bug di convergenza o perdita dati | 3 | 5 | 15 | Core condiviso KMP scritto una volta (ADR-1); test generativi/fuzzing sulle proprietà di convergenza (RNF-A3); spike tecnico dedicato in fase 0 con go/no-go; fallback progettato: LWW per-campo se i CRDT si rivelassero eccessivi per il nostro modello dati | Spike oltre 4 settimane; bug di merge in alpha |
| R-11 | **Doppio team nativo insostenibile** per una startup (costo/velocità) | 3 | 4 | 12 | KMP massimizza il condiviso (sync, crypto, parser, regole = il 40–50% del codice); design system con token unici riduce il costo di parità visiva; se il vincolo economico lo impone, iOS-first con Android a −8 settimane è il piano B dichiarato | Velocity divergente tra piattaforme; feature gap crescente |
| R-12 | **Prestazioni sotto soglia su device datati** (violazione RNF-P*) | 2 | 4 | 8 | Budget per modulo in CI dal giorno 1 (RNF-P9); device farm con il device di riferimento; il minimalismo UI è anche una scelta di performance | Trend dei benchmark CI; jank in beta |
| R-13 | **Migrazioni di schema dei moduli corrompono dati** | 2 | 5 | 10 | Migrazioni isolate e reversibili (RF-13); test di migrazione su dataset reali anonimizzati sintetici; backup pre-migrazione automatico; cestino 30 gg | Errori di migrazione in staging |
| R-14 | **Dipendenza dalle piattaforme** (cambi policy store, API Health, billing) | 3 | 3 | 9 | Adapter isolati per ogni API di piattaforma; monitoraggio release notes OS; margine nei piani per gli adeguamenti annuali (WWDC/I-O) | Beta OS che rompono; policy update |

## 3. Rischi di sicurezza e conformità

| ID | Rischio | P | I | Esp. | Mitigazioni | Segnali di allarme |
|----|---------|---|---|------|-------------|--------------------|
| R-20 | **Breach del backend** | 2 | 4 | 8 | Architettura content-blind: il bottino è cifrato (doc 06); pen-test annuale; incident response pronto. L'impatto residuo è reputazionale, non sui contenuti | Alert SIEM; bug bounty |
| R-21 | **Utenti che perdono passphrase+Recovery Key** → dati irrecuperabili → recensioni furiose | 4 | 3 | 12 | Verifica del salvataggio RK al setup; promemoria periodici; opzione keychain di sistema; comunicazione ripetuta e chiara del trade-off (doc 06 §2.2) | Ticket di supporto "ho perso tutto" |
| R-22 | **Errore di implementazione crypto** (peggio di nessuna crypto) | 2 | 5 | 10 | Solo primitive standard e librerie auditate (mai crypto fatta in casa); review esterna del design crittografico; pen-test mirato | Audit findings |
| R-23 | **Non conformità GDPR** (dati salute/finanze = alto rischio) | 2 | 4 | 8 | DPIA prima del lancio; privacy owner nel team; cancellazione ≤ 30 gg testata E2E; consulenza legale specializzata | Audit interno; reclami |
| R-24 | **Plugin di terze parti malevoli** (fase 3) | 3 | 4 | 12 | Sandbox, permessi granulari, review obbligatoria, kill-switch (doc 03 §3.4); il rischio si accetta solo quando il sistema di controllo è pronto — data del marketplace vincolata a questo | Findings in review dei plugin |

## 4. Rischi operativi e organizzativi

| ID | Rischio | P | I | Esp. | Mitigazioni | Segnali di allarme |
|----|---------|---|---|------|-------------|--------------------|
| R-30 | **Scope creep**: 8 moduli promessi = pressione a farli tutti subito | 4 | 4 | 16 | La roadmap MoSCoW (doc 02) è vincolante; la lista "Won't have" è pubblica nel team; ogni aggiunta richiede la rimozione di qualcos'altro dalla fase | Slittamenti di milestone; fase 1 che si gonfia |
| R-31 | **Il team perde persone chiave** (bus factor su sync/crypto) | 2 | 4 | 8 | Documentazione di design (questi doc); pairing sul core; nessun modulo di conoscenza a singola persona | Review concentrate su una persona |
| R-32 | **Burn rate vs. tempi**: 12+ mesi prima di ricavi significativi | 3 | 4 | 12 | Fasi con criteri di uscita misurabili (doc 09); soft launch anticipato per validare la monetizzazione; piano B iOS-first (R-11) riduce il burn del 30% | Runway < 12 mesi al soft launch |
| R-33 | **Supporto clienti travolto al lancio** (specie da R-21) | 3 | 2 | 6 | FAQ/self-service in app; macro di risposta; il fondatore fa supporto nelle prime settimane (feedback diretto) | Backlog ticket > 72 h |

## 5. Top 5 per esposizione e risposta strategica

1. **R-01 (20) — Percezione "tuttofare"** → la strategia MVP-3-moduli-eccellenti È la risposta; non negoziabile.
2. **R-03 (16) — Retention di categoria** → la costanza gentile e i loop (doc 07 §2.3) sono feature di retention, prioritarie quanto le funzioni visibili.
3. **R-30 (16) — Scope creep** → governance della roadmap; questo documento serve anche a dire di no.
4. **R-10 (15) — CRDT+E2E** → spike in fase 0 con go/no-go esplicito: il rischio tecnico più alto va bruciato per primo.
5. **R-11/R-32 (12) — Sostenibilità del doppio nativo** → KMP + piano B dichiarato prima di iniziare, non deciso nel panico.
