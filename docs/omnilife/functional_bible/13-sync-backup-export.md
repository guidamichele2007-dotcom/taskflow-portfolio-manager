# 13 · Sincronizzazione, Backup, Export (SYNC / BKP / EXP)

> Eredita il [MFC](00-modello-funzionale-comune.md) (che ne definisce già il comportamento trasversale in §3). Qui: le funzioni visibili all'utente e le garanzie. Le fondamenta non si giudicano con metriche d'uso ma con requisiti (Feature Philosophy §6 — "fondamenta").

## 1. Scopo e tracciabilità

| Perché esiste | Principi | JTBD | Decisioni |
|---|---|---|---|
| Le tre promesse di fiducia: mai perdere nulla (J12), funzionare ovunque (J13), poter andarsene (J14) | P1–P12; C-art. Titolo I | **J12, J13, J14** | **D-02, D-09** |

## 2. Funzioni

| ID | Nome | Descrizione e motivo | Pri | Dipendenze |
|----|------|----------------------|-----|------------|
| SYNC-001 | Sync multi-device E2E | Automatica, silenziosa, cifrata; convergenza senza intervento (MFC §3) | M | Account |
| SYNC-002 | Pannello stato sync | Ultimo sync, elementi in coda, dispositivi collegati; l'unico posto dove la sync si vede (il silenzio è successo) | M | — |
| SYNC-003 | Registro dispositivi | Elenco device con revoca remota ("disconnetti") | M | SEC |
| BKP-001 | Backup automatico cifrato | Snapshot: giornaliero ×7, settimanale ×4, mensile ×6; su rete Wi-Fi (opzione dati mobili) | M | Account |
| BKP-002 | Backup locale (senza account) | Per gli utenti anonimi: archivio cifrato esportabile manualmente, con avviso onesto dei limiti | M | ONB-004 |
| BKP-003 | Ripristino completo | Nuovo device: login → verifica → tutto com'era in < 2 min (UC-08); include moduli attivi, impostazioni, ordine delle card | M | SYNC |
| BKP-004 | Ripristino puntuale | Da uno snapshot a scelta (l'errore propagato di ieri si annulla) con anteprima delle differenze e conferma forte | S | BKP-001 |
| EXP-001 | Export completo | JSON (fedele, con collegamenti) + CSV (per tabella: transazioni, task, abitudini) generati sul device; condivisione via share di sistema | M | — |
| EXP-002 | Export selettivo | Per modulo e per periodo | S | EXP-001 |
| EXP-003 | Cancellazione account e dati | Flusso: ri-autenticazione forte → spiegazione onesta → attesa 72 h annullabile → cancellazione device+cloud verificabile (≤ 30 gg per i backup a rotazione) | M | SEC; MFC-R-24 |

**Scheda estesa BKP-003 (ripristino)** — *Requisiti*: 10.000 entità su rete media ≤ 2 min; l'app è utilizzabile appena i dati "caldi" (30 giorni) sono presenti, il resto arriva in background con progresso visibile; interruzione (rete/batteria) → riprendibile senza ricominciare; alla fine, verifica di integrità con esito mostrato. *Casi limite*: ripristino su device con dati locali già presenti → scelta esplicita (unisci / sostituisci) con spiegazione delle conseguenze; versione dell'app più vecchia dei dati → messaggio onesto "aggiorna l'app", mai migrazione al ribasso; chiave di recupero errata 5 volte → backoff progressivo (anti brute-force), mai lock-out permanente del legittimo.

**Scheda estesa EXP-001** — *Requisiti*: il formato JSON è **documentato pubblicamente** (C-art. 20) e stabile (versionato); l'export include: entità, collegamenti del grafo, cronologie, definizioni (categorie, liste), impostazioni esportabili; NON include: dati salute di piattaforma (HLTH-R-02), chiavi, ricerche recenti. *Criterio della verità*: da un export + un'installazione pulita si deve poter ricostruire manualmente ogni informazione utente. *Casi limite*: export con 100k entità → generazione in background con notifica locale al termine; spazio insufficiente → MFC-E-03.

## 3–7. Regole, stati, edge (integrazioni al MFC)

| ID | Regola | Motivo |
|----|--------|--------|
| SYNC-R-01 | La sync non è mai richiesta per usare l'app; il piano Free include il backup cloud di 1 device | C-art. 16, 30 |
| SYNC-R-02 | La revoca di un device (SYNC-003) invalida le sue sessioni al primo contatto; i dati locali sul device revocato restano cifrati e inaccessibili senza sblocco | Sicurezza pragmatica (il wipe remoto garantito è una promessa non mantenibile: non la facciamo — P6) |
| BKP-R-01 | I backup non sono mai disattivabili *per errore*: disattivarli richiede un percorso esplicito con avviso | C-art. 18; protezione dell'utente da sé stesso, senza paternalismo |
| BKP-R-02 | Prima di ogni migrazione di schema: snapshot automatico locale | MFC-E-15 |
| EXP-R-01 | L'export non ha limiti di piano, frequenza o dimensione | C-art. 7-8 |
| EXP-R-02 | La cancellazione account produce conferma finale verificabile (email con esito) | C-art. 9 |

- Stati: "in attesa di sync" (discreto), "backup non recente" (avviso in SET dopo 14 gg senza backup riuscito — mai panico), "ripristino in corso" (progresso), anonimo (BKP-002 con promemoria onesto e raro).
- Edge: due account sullo stesso device (logout/login altro utente) → dati separati per account, nessuna contaminazione; clock sbagliato → MFC-E-10; quota cloud piena (limite tecnico) → le generazioni più vecchie ruotano prima, l'utente è informato.

## 8. Criteri di accettazione

- **SYNC-AC-01** — *Dato* 2 device con lo stesso account, *quando* uno crea 100 entità offline e l'altro ne modifica 50 offline, *allora* al ritorno in rete entrambi convergono allo stesso stato completo senza dialoghi (MFC-AC-04 su scala).
- **BKP-AC-01** — *Dato* un device nuovo e un account con 10k entità, *quando* l'utente completa login+verifica, *allora* i dati degli ultimi 30 giorni sono utilizzabili entro 2 minuti e il totale si completa in background.
- **BKP-AC-02** — *Dato* la cancellazione di massa accidentale di ieri, *quando* l'utente ripristina lo snapshot di 2 giorni fa, *allora* vede l'anteprima delle differenze, conferma, e i dati tornano — con la possibilità di annullare il ripristino stesso (lo stato pre-ripristino diventa uno snapshot).
- **EXP-AC-01** — *Dato* un utente Free con 5 anni di dati, *quando* esporta, *allora* ottiene l'archivio completo senza limitazioni e ogni entità dell'app è presente nel JSON.
- **EXP-AC-02** — *Dato* la cancellazione account confermata e trascorsi 30 giorni, *allora* nessun dato dell'utente esiste su cloud e backup (verificabile da audit interno), e l'utente ha ricevuto conferma.

---

*Prossimo: [Impostazioni, Profilo, Sicurezza](14-impostazioni-profilo-sicurezza.md)*
