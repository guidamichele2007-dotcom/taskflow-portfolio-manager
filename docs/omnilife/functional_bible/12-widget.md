# 12 · Widget e Superfici Esterne (WID)

> Eredita il [MFC](00-modello-funzionale-comune.md). I widget sono il canale di ritorno quotidiano più efficace e la via maestra del "aprire l'app non è necessario" (P21).

## 1. Scopo e tracciabilità

| Perché esiste | Principi | JTBD | Decisioni |
|---|---|---|---|
| Portare consultazione e azione a costo zero di apertura: home screen, lock screen, quick actions, assistente vocale | P13, P21, P56 | J1, J3, J4, J8 | Benchmark Streaks (PB 05 #39) |

## 2. Funzioni

| ID | Nome | Descrizione e motivo | Pri | Dipendenze |
|----|------|----------------------|-----|------------|
| WID-001 | Widget "Oggi" | Piccolo/medio/grande: gli elementi del giorno (come Home ridotta); interattivo dove la piattaforma lo consente (spunta diretta) | M | HOME |
| WID-002 | Widget Abitudini | Griglia delle abitudini del giorno con spunta a 1 tocco (HAB-004) | M | HAB |
| WID-003 | Widget Cattura | Pulsante di cattura → campo diretto (CAPT-002) | M | CAPT |
| WID-004 | Widget Budget | Stato dei budget principali; importi solo con opt-in (MFC-R-22) | S | FIN |
| WID-005 | Quick actions da icona | Cattura, nuova spesa, cerca | M | OS |
| WID-006 | Integrazione assistente vocale | Scorciatoie di sistema: cattura e spunta a voce (UC-11) | S | CAPT, HAB |
| WID-007 | Lock screen / complicazioni | Varianti minimali per lock screen e (fase 2) watch | S/C | OS |

**Scheda estesa WID-001/002** — *Requisiti*: dati sempre coerenti con l'app (aggiornamento immediato all'azione, entro i vincoli di refresh della piattaforma — il ritardo di piattaforma è dichiarato accettabile ma mai > 15 min per i contenuti passivi); l'azione dal widget scrive con le stesse garanzie MFC-R-01 e undo alla prossima apertura widget/app; con app bloccata: MFC-R-22 (contenuti secondo consenso). *Casi limite*: widget di modulo disattivato → stato "modulo non attivo" con azione di apertura Galleria (mai widget rotto); più widget della stessa famiglia con configurazioni diverse → indipendenti; storage inaccessibile al widget (device appena riavviato, prima dello sblocco) → placeholder neutro senza dati sensibili.

## 3–7. Regole, stati, eventi, edge (sintesi)

- **WID-R-01**: i widget non mostrano mai badge di marketing o inviti all'upgrade. *(C-art. 64)*
- **WID-R-02**: ogni widget è configurabile nel contenuto (quale lista, quale budget) alla collocazione.
- **WID-R-03**: le azioni dai widget sono idempotenti (MFC-E-01) e funzionano offline.
- Eventi: i widget leggono lo stato condiviso e pubblicano le stesse azioni dell'app (`task.item.completed` da widget è indistinguibile).
- Edge: fuso/DST → il "giorno" del widget segue MFC-E-07; l'interattività non supportata dalla piattaforma → il tocco apre l'app sull'entità (fallback dichiarato).

## 8. Criteri di accettazione

- **WID-AC-01** — *Dato* il widget Abitudini sulla home screen, *quando* l'utente spunta "meditare", *allora* l'abitudine risulta completata nell'app senza che l'app sia stata aperta, e il widget riflette lo stato.
- **WID-AC-02** — *Dato* biometria attiva e opt-in importi negato, *allora* il widget Budget non mostra cifre in nessuno stato del device.
- **WID-AC-03** — *Dato* modalità aereo, *quando* l'utente cattura dal widget, *allora* l'elemento esiste nell'app e sincronizza al ritorno della rete.
- **WID-AC-04** — *Dato* il modulo del widget disattivato, *allora* il widget mostra lo stato dedicato e nessun dato residuo.

---

*Prossimo: [Sync, Backup, Export](13-sync-backup-export.md)*
