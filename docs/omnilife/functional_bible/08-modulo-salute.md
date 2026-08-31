# 08 · Modulo Salute (HLTH)

> Eredita il [MFC](00-modello-funzionale-comune.md). **Principio fondante**: non siamo un sensore né un medico. Leggiamo i dati dalla piattaforma salute del sistema (HealthKit / Health Connect) con consenso granulare, aggiungiamo contesto di vita, e non diamo mai consigli sanitari (C-art. 146).

## 1. Scopo e tracciabilità

| Perché esiste | Principi | JTBD | Decisioni |
|---|---|---|---|
| Collegare il corpo al resto della vita: i dati passivi (passi, sonno, allenamenti) diventano contesto per abitudini, insight e obiettivi — il "e poi?" che Apple Salute non fa (PB doc 05, #47) | P4 (minimizzazione), P43 | J8, J17 | v1.x (roadmap); S-01/S-03 design doc |

## 2. Funzioni

| ID | Nome | Descrizione e motivo | Pri | Dipendenze |
|----|------|----------------------|-----|------------|
| HLTH-001 | Lettura selettiva dalla piattaforma salute | Tipi supportati: passi, allenamenti, sonno, battito a riposo, peso. Consenso per-tipo, revocabile; leggiamo SOLO i tipi per cui esiste una funzione attiva (C-art. 4) | S | Permessi piattaforma |
| HLTH-002 | Riepilogo del giorno | Card Home (se modulo attivo): passi, sonno di stanotte, allenamenti di oggi — parole prima dei numeri | S | HLTH-001 |
| HLTH-003 | Auto-completamento abitudini | Il dato rilevato completa l'abitudine collegata (policy: automatico / chiedi conferma) | S | HAB-010 |
| HLTH-004 | Metriche manuali | Peso, umore (1–5), energia (1–5): inserimento a 2 tocchi; l'umore/energia alimentano gli insight trasversali | C | CAPT |
| HLTH-005 | Storico e tendenze | Grafici semplici settimana/mese con descrizione testuale accessibile; nessuna diagnosi, nessun "punteggio salute" | C | — |

**Scheda estesa HLTH-001/003** — *Requisiti*: i dati letti restano nel dominio della piattaforma OS + cache locale cifrata minima (finestra: 90 giorni) per le funzioni attive; **mai sincronizzati sul nostro cloud** (deroga alla sync dichiarata: il dato sanitario grezzo non lascia il device — C-art. 43/45; la piattaforma OS ha già il suo sync); ciò che sincronizza è solo l'effetto (es. abitudine completata). *Casi limite*: permesso revocato dopo il collegamento → le abitudini tornano manuali con segnalazione una tantum; dati incoerenti dal sensore (10 milioni di passi) → soglie di plausibilità, il dato anomalo è mostrato con cautela e mai usato per auto-completare; doppio conteggio da 2 fonti (telefono+watch) → deleghiamo la dedup alla piattaforma OS, dichiarandolo. *Criteri di successo*: ≥ 50% degli utenti col modulo attivo collega almeno 1 abitudine (altrimenti il modulo è un lettore passivo: ripensare, P104).

## 3. Comportamenti specifici (deroghe)

- I dati della piattaforma sono **sola lettura, non nostri**: niente ciclo di vita MFC, niente cestino, niente export nostro dei dati grezzi di piattaforma (l'export cita i soli dati manuali HLTH-004 e i collegamenti). L'utente li gestisce nella app di sistema.
- Il modulo è sensibile di default (MFC-R-21): biometria per aprirlo se attiva; MAI dati salute in widget/notifiche in chiaro (MFC-R-22, non derogabile qui).

## 4. Stati specifici

Permesso non concesso → modulo utile con sole metriche manuali + spiegazione; Piattaforma assente (device senza Health) → idem; Nessun dato oggi → onesto, mai imbarazzante ("nessun dato" ≠ "0 passi", distinzione obbligatoria — P6).

## 5. Regole di business

| ID | Regola | Motivo |
|----|--------|--------|
| HLTH-R-01 | Mai consigli medici, diagnosi, o "punteggi di salute"; il linguaggio descrive, non prescrive | C-art. 146; responsabilità |
| HLTH-R-02 | Nessun dato salute grezzo sul nostro cloud, mai | C-art. 43/45; minimizzazione |
| HLTH-R-03 | Ogni tipo di dato letto è giustificato da una funzione attiva e revocabile singolarmente | C-art. 4, 32 |
| HLTH-R-04 | L'umore/energia manuali sono dati utente pieni (ciclo MFC completo, export incluso) | Distinzione netta manuale/piattaforma |

## 6. Eventi

Pubblica: `hlth.workout.detected`, `hlth.steps.threshold`, `hlth.manual.logged` (HAB auto-completa; INS osserva localmente). Sottoscrive: `core.module.activated/deactivated` (avvia/ferma gli osservatori di piattaforma).

## 7. Edge case specifici

- Allenamento a cavallo di mezzanotte → conta nel giorno di inizio (regola dichiarata).
- Watch scarico / dati arrivati in ritardo di ore → l'auto-completamento retroattivo funziona entro le 48 h, con evento datato correttamente.
- Fuso: i dati piattaforma portano il proprio timestamp; la "giornata" segue MFC-E-07.

## 8. Criteri di accettazione

- **HLTH-AC-01** — *Dato* il consenso ai soli "allenamenti", *quando* il modulo opera, *allora* nessun altro tipo di dato è letto (verificabile dalle impostazioni di piattaforma).
- **HLTH-AC-02** — *Dato* un allenamento "corsa 30 min" rilevato e policy "automatico", *allora* l'abitudine collegata risulta completata entro 5 min dall'apertura dell'app, una sola volta.
- **HLTH-AC-03** — *Dato* permesso revocato dalla piattaforma, *quando* l'utente apre il modulo, *allora* trova metriche manuali funzionanti e una segnalazione non ripetitiva del collegamento interrotto.
- **HLTH-AC-04** — *Dato* l'ispezione del traffico di rete dell'app, *allora* nessun payload contiene dati salute di piattaforma, in nessuna circostanza.

---

*Prossimo: [Modulo Obiettivi](09-modulo-obiettivi.md)*
