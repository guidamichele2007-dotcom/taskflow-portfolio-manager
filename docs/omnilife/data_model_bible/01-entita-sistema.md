# 01 · Entità di Sistema e Account

> Eredita il [Modello Dati Comune (MDC)](00-modello-dati-comune.md). Entità trasversali che non appartengono a un modulo di dominio ma sostengono l'intero ecosistema.

## DM-SYS-01 · Account

**Descrizione**: l'identità unica del proprietario dei dati (MFC-R-20: un solo utente per account). Può esistere in forma "anonima" (solo locale, pre-registrazione, §5.1 MDC) o registrata.

| Campo | Tipo concettuale | Obbligatorio | Note |
|---|---|---|---|
| `email` | Testo | No (obbligatorio solo se registrato) | PROF-R-01: nessun dato di profilo obbligatorio oltre l'email |
| `stato_registrazione` | Enum: anonimo · registrato | Sì | ONB-004 |
| `nome_visualizzato` | Testo | No | PROF-001 |
| `immagine_profilo` | Riferimento a file locale | No | PROF-001, mai pubblico (C-art. 55) |
| `piano` | Enum: Free · Plus (mensile/annuale) · Lifetime · Trial | Sì | SET-003 |
| `trial_scadenza` | Data | Condizionale | Se piano=Trial |
| `lingua_preferita`, `valuta_primaria`, `formato_data_ora` | Enum/Testo | No (default: sistema) | SET-001 gruppo "Lingua e formati" |
| `telemetria_opt_in` | Booleano | Sì | default off in UE (ONB-006) |

**Relazioni**: proprietario (1:N) di ogni altra entità utente tramite `account_proprietario` (MDC §3). 1:N con Device. 1:N con RecoveryKeyMetadata (storico rigenerazioni).

**Dipendenze**: SEC-001/002/003, SET-003, PROF-001.

**Regole**:
- Un solo account per persona fisica nell'MVP (nessuna condivisione, MDC §5).
- Il cambio email richiede conferma su entrambe le caselle (SET edge case).
- Il downgrade di piano con moduli oltre soglia lascia i moduli eccedenti attivi in sola lettura, mai bloccati a tempo (SET-AC-02).

**Stati**: anonimo · registrato · trial · pagante · grace period (pagamento fallito, mai blocco improvviso — SET §4 edge case) · in cancellazione (attesa 72h, EXP-003).

**Eventi collegati**: nessun evento applicativo pubblicato direttamente (l'account non è un'entità di dominio); genera side-effect su SYNC-001, BKP-001 alla registrazione.

**Riferimenti Functional Bible**: ONB-004/007, SET-001/003, PROF-001, SEC-001/002/003, EXP-003, MFC-R-20/24.

---

## DM-SYS-02 · Device (Dispositivo)

**Descrizione**: un dispositivo su cui l'app è installata e collegata a un Account (SYNC-003).

| Campo | Tipo concettuale | Obbligatorio |
|---|---|---|
| `nome_dispositivo` | Testo (es. "iPhone di Giulia") | Sì |
| `piattaforma` | Enum: iOS · Android | Sì |
| `ultimo_contatto_il` | Timestamp | Sì |
| `stato` | Enum: attivo · revocato | Sì |
| `impostazioni_per_dispositivo` | biometria/timeout, rete per backup | No (SET-R-03) |

**Relazioni**: N:1 con Account. Ogni entità creata porta `creato_da_dispositivo` (MDC §3) come riferimento leggero (non una relazione strutturale forte: il dispositivo può essere revocato senza invalidare i dati che ha creato).

**Dipendenze**: SEC (autenticazione), SYNC-001.

**Regole**: la revoca (SYNC-003) invalida le sessioni al primo contatto; i dati locali sul dispositivo revocato restano cifrati e inaccessibili senza sblocco — **non esiste wipe remoto garantito** (SYNC-R-02, scelta di onestà P6).

**Stati**: attivo · revocato.

**Eventi collegati**: nessuno pubblicato verso i moduli di dominio; consumato da SYNC-002 (pannello stato) e SYNC-003 (registro dispositivi).

**Riferimenti Functional Bible**: SYNC-001/002/003, SEC-001, MFC-E-12.

---

## DM-SYS-03 · ModuleActivation (Attivazione modulo)

**Descrizione**: rappresenta se un modulo (Attività, Finanze, Abitudini, Calendario, Note, Salute, Obiettivi) è attivo per l'account, e da quando. **Non** rappresenta i dati del modulo (quelli restano nel grafo indipendentemente, MFC-R-13): è solo lo stato di visibilità/governo.

| Campo | Tipo concettuale | Obbligatorio |
|---|---|---|
| `modulo` | Enum (uno per modulo del catalogo GAL-001) | Sì |
| `stato` | Enum: attivo · disattivato | Sì |
| `attivato_il` | Timestamp | Sì |
| `ordine_in_home` | Intero (per HOME-003 riordino) | No |

**Relazioni**: N:1 con Account. Non ha relazioni strutturali con le entità di dominio del modulo (per costruzione: disattivare non tocca i dati, MFC-R-13, C-art. 183).

**Dipendenze**: GAL-001/002/003.

**Regole**: il conteggio dei moduli Free considera solo le attivazioni con stato=attivo contemporaneamente (GAL-R-01, D-05); riattivare un modulo ripristina `ordine_in_home` precedente se esisteva.

**Stati**: attivo · disattivato.

**Eventi collegati**: pubblica `core.module.activated` / `core.module.deactivated` (consumato da Home, Cattura/parser, Insight, ogni modulo con collegamenti verso quel modulo).

**Riferimenti Functional Bible**: GAL-001/002/003, HOME-002, MFC-R-13, C-art. 181-187.

---

## DM-SYS-04 · Subscription (stato abbonamento)

**Descrizione**: lo stato commerciale derivato dallo store (non la fonte di verità, che resta lo store stesso — SET-003 rimanda alla piattaforma per l'acquisto/disdetta).

| Campo | Tipo concettuale | Obbligatorio |
|---|---|---|
| `piano` | Enum: Free · Plus mensile · Plus annuale · Lifetime | Sì |
| `stato` | Enum: attivo · in_prova · scaduto · in_grace_period · disdetto | Sì |
| `rinnovo_il` / `scaduto_il` | Data | Condizionale |
| `origine_acquisto` | Enum: App Store · Play Store | Sì |

**Relazioni**: 1:1 con Account.

**Regole**: la disdetta reindirizza al meccanismo nativo dello store (obbligo di piattaforma, SET-R-04); nessun percorso di trattenimento oltre 1 schermata informativa onesta.

**Stati**: coincide con `stato` sopra.

**Riferimenti Functional Bible**: SET-003, C-art. 163.

---

## DM-SYS-05 · RecoveryKeyMetadata (metadati della chiave di recupero)

**Descrizione**: **non la chiave stessa** (che non è mai conservata da noi in chiaro) — solo i metadati sul suo ciclo di vita, necessari a governare i promemoria di verifica (SEC-002).

| Campo | Tipo concettuale | Obbligatorio |
|---|---|---|
| `generata_il` | Timestamp | Sì |
| `verificata_il` | Timestamp | No (finché l'utente non conferma il salvataggio) |
| `ultimo_promemoria_il` | Timestamp | No |
| `rigenerata_il` | Timestamp | No (ogni rigenerazione invalida la precedente) |

**Relazioni**: 1:1 con Account (con storico delle rigenerazioni).

**Regole**: promemoria di ri-verifica al massimo 2 volte l'anno (C-art. 63); la rigenerazione invalida la chiave precedente (SEC-AC-02).

**Riferimenti Functional Bible**: SEC-002, D-09.

---

## DM-SYS-06 · Setting (impostazione)

**Descrizione**: le voci del catalogo chiuso di Impostazioni (SET-001 §2). Non un'entità libera: ogni voce ammessa è enumerata nel catalogo della Functional Bible.

| Campo | Tipo concettuale | Obbligatorio |
|---|---|---|
| `chiave` | Enum (una per voce del catalogo SET §2) | Sì |
| `valore` | Dipende dalla voce | Sì |
| `ambito` | Enum: sincronizzata tra device · solo questo dispositivo | Sì |

**Relazioni**: N:1 con Account (o con Device per le impostazioni per-dispositivo, SET-R-03).

**Regole**: **SET-R-01** — il catalogo è chiuso; nessuna nuova chiave senza passare i 7 cancelli della Feature Philosophy (Product Bible doc 09). Ogni impostazione ha effetto immediato e reversibile (SET-R-02).

**Riferimenti Functional Bible**: SET-001…004, tabella catalogo §2.

---

*Prossimo: [Cattura e Grafo](02-entita-cattura-grafo.md)*
