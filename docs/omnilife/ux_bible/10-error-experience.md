# 10 · Error Experience

> Eredita [MUC §7](00-modello-ux-comune.md#7-pattern-universale-di-errore-eredita-mfc--constitution-art-104). Per ogni categoria di errore: messaggio, spiegazione, azione suggerita, ripristino, retry, fallback, logging, telemetria. Principio: **C-art. 104** — gli errori parlano da umani, mai codici nudi, mai colpe all'utente.

## 1. Struttura universale del messaggio d'errore

```
[Icona neutra]  Che cosa è successo (1 frase, linguaggio quotidiano)
                Perché, se conosciuto (1 frase opzionale)
                [Azione riparatrice primaria]   [Azione secondaria, se utile]
```

**Vietato per costruzione**: codici di errore nudi ("Error 0x8007"), termini tecnici non spiegati ("timeout", "payload"), colpevolizzazione ("hai inserito dati non validi"), punti esclamativi multipli, icone allarmanti (rosso acceso, triangoli lampeggianti).

## 2. Catalogo delle categorie di errore

### 2.1 Errore di rete (funzioni online-eccezione: §3 MFC)

| Aspetto | Specifica |
|---|---|
| Messaggio | "Non riesco a connettermi in questo momento." |
| Spiegazione | "Controlla la connessione e riprova." |
| Azione | `Riprova` (retry manuale a 1 tocco) |
| Ripristino | Automatico: se la funzione può attendere (es. sync), l'app riprova da sola con backoff, senza ulteriori interruzioni |
| Fallback | Per le funzioni realmente online-only (registrazione, ripristino, acquisto, download modulo): l'app resta bloccata SOLO su quella funzione specifica, mai sull'intera app |
| Logging | Locale, con timestamp e funzione coinvolta; nessun contenuto utente |
| Telemetria | Evento anonimo aggregato "errore_rete{funzione}" solo se opt-in (C-art. 23) |

### 2.2 Errore di validazione (campo)

| Aspetto | Specifica |
|---|---|
| Messaggio | Inline, sotto il campo: "Serve un importo per salvare questa spesa" |
| Spiegazione | Il messaggio stesso è la spiegazione (nessuna generalità tipo "campo non valido") |
| Azione | Il focus resta sul campo, tastiera già aperta |
| Ripristino | Nessun blocco della chiusura del foglio: si salva l'ultimo stato valido, il campo mancante resta segnalato per la prossima apertura |
| Retry | Immediato, alla digitazione successiva |
| Fallback | — |
| Logging/Telemetria | Nessuno (evento locale di UI, non un errore di sistema) |

### 2.3 Errore di spazio disco esaurito (MFC-E-03)

| Aspetto | Specifica |
|---|---|
| Messaggio | "Lo spazio sul dispositivo è esaurito: non riesco a salvare." |
| Spiegazione | "Libera un po' di spazio e i tuoi dati saranno salvati normalmente." |
| Azione | `Come liberare spazio` (apre le impostazioni di sistema pertinenti se la piattaforma lo consente) |
| Ripristino | Il testo/dato in corso resta in memoria (non su disco) finché non si libera spazio: nessuna perdita se l'utente risolve entro la sessione |
| Fallback | Le funzioni di sola lettura restano operative |
| Logging | Locale |
| Telemetria | Evento aggregato "disco_pieno" (opt-in) — utile per capire la frequenza reale del problema |

### 2.4 Errore di sincronizzazione persistente (> 72h)

| Aspetto | Specifica |
|---|---|
| Messaggio | Notifica locale informativa (mai un'interruzione): "Alcuni dati non si sincronizzano da qualche giorno." |
| Spiegazione | Nel pannello di stato: dettaglio di quanti elementi in coda e da quando |
| Azione | `Dettagli` → pannello sync (SYNC-002) con `Riprova ora` |
| Ripristino | L'outbox non si svuota mai senza conferma del server (MFC §3): zero perdita anche in caso di errore prolungato |
| Fallback | L'app resta pienamente utilizzabile offline nel frattempo |
| Logging | Locale, dettagliato per diagnosi |
| Telemetria | Evento aggregato "sync_fallita_persistente" (opt-in) |

### 2.5 Errore di autenticazione (login/2FA)

| Aspetto | Specifica |
|---|---|
| Messaggio | "Email o password non corrette." (mai specificare quale dei due, per sicurezza) |
| Azione | `Riprova` / `Password dimenticata?` |
| Ripristino | Dopo 5 tentativi: backoff progressivo (attesa crescente), mai lock-out permanente del legittimo (SEC-002) |
| Fallback | Recupero via chiave di recupero (SEC-002) o reset password via email |
| Logging | Locale + lato server (tentativi, mai le password) |
| Telemetria | Eventi di sicurezza (non opt-in: rientrano nella protezione dell'account, C-art. 41) |

### 2.6 Errore di chiave di recupero errata

| Aspetto | Specifica |
|---|---|
| Messaggio | "Questa chiave non corrisponde al tuo account." |
| Spiegazione | "Controlla di aver scritto tutte le parole nell'ordine corretto." |
| Azione | `Riprova` |
| Ripristino | Dopo tentativi ripetuti falliti: backoff; **se davvero persa**, percorso onesto dedicato (D-09): "Senza questa chiave non possiamo recuperare i tuoi dati cifrati. Ecco perché: [spiegazione breve del modello E2E]" — mai un tono che minimizzi la gravità, mai un tono che colpevolizzi |
| Fallback | Nessuno tecnico (per architettura); fallback umano: contatto supporto per assistenza al processo, mai per bypassare la cifratura |
| Logging | Locale + tentativo lato server (mai la chiave) |
| Telemetria | Evento aggregato "recovery_key_fallita" (per capire quanto spesso serve migliorare la UX di verifica al setup) |

### 2.7 Errore di importazione (CSV bancario)

| Aspetto | Specifica |
|---|---|
| Messaggio | "Non riesco a leggere questo file." / "Alcune righe non sono chiare: controlla la mappatura delle colonne." |
| Azione | `Rimappa colonne` / `Salta le righe non chiare` |
| Ripristino | L'intero import resta annullabile in blocco dopo l'anteprima (FIN-R-06) |
| Fallback | Import parziale con dichiarazione esplicita di cosa è stato escluso |
| Logging | Locale |
| Telemetria | Evento aggregato "import_fallito{motivo}" |

### 2.8 Errore di piattaforma esterna (calendario/salute non raggiungibile)

| Aspetto | Specifica |
|---|---|
| Messaggio | "Non riesco a leggere questo calendario in questo momento." |
| Ripristino | Il resto della timeline (task/abitudini) resta visibile e operativo (C-art. 122); il modulo si degrada solo per la fonte in errore |
| Fallback | Azione "Apri le impostazioni di [Calendario/Salute]" per verificare i permessi |
| Logging | Locale |
| Telemetria | Evento aggregato (opt-in) |

### 2.9 Crash / stato imprevisto

| Aspetto | Specifica |
|---|---|
| Messaggio | Alla riapertura: nessun messaggio colpevolizzante; l'app riparte sull'ultimo stato coerente (MFC-E-02) |
| Ripristino | Draft recuperati; nessuna scrittura parziale visibile (transazionalità) |
| Logging | Crash report sanitizzato (nessun contenuto utente, C-art. 22) inviato solo con telemetria opt-in |
| Telemetria | Se opt-in: stack trace tecnico, MAI contenuti |

## 3. Regole trasversali (UX Constitution, sezione errori — sintesi qui, elenco completo in [13](13-ux-constitution.md))

- **UX-R-022**: ogni errore è recuperabile con un'azione a 1 tocco, salvo impossibilità architetturale dichiarata onestamente (2.6).
- **UX-R-023**: nessun errore di una sezione blocca il resto della schermata (C-art. 122).
- **UX-R-024**: nessun errore usa un linguaggio che implichi colpa dell'utente.
- **UX-R-025**: ogni errore ha un log locale, indipendentemente dal consenso telemetria (per il supporto tecnico su richiesta esplicita dell'utente); la trasmissione remota richiede sempre opt-in.
- **UX-R-026**: retry automatico con backoff per gli errori transitori; retry manuale sempre disponibile in aggiunta, mai al suo posto.

---

*Prossimo: [Onboarding Experience](11-onboarding-experience.md)*
