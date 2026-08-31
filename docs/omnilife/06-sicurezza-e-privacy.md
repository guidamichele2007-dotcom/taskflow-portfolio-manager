# 06 · Sicurezza e Privacy

> Principio guida: **la privacy è il prodotto, non una policy.** OmniLife custodisce le informazioni più sensibili di una persona (denaro, salute, pensieri): il progetto assume che l'utente ci affidi ciò che non affiderebbe a nessun'altra app, e che noi si debba meritarlo con architettura, non con promesse.

## 1. Modello di minaccia (sintesi)

| Attaccante | Vettore | Contromisura primaria |
|---|---|---|
| Ladro/curioso con accesso fisico al device | Apertura app, backup locali | Blocco biometrico + cifratura a riposo + timeout |
| Attaccante di rete | MITM, intercettazione | TLS 1.3 + certificate pinning; payload già cifrati E2E |
| Compromissione del backend (breach, insider, richiesta coercitiva) | Accesso a storage/DB | **E2E: il server non possiede chiavi di decifratura dei contenuti** |
| Malware su device / app malevola | Accesso a file, clipboard, screenshot | Sandbox OS, keystore hardware, flag anti-screenshot su schermate sensibili (opzionale utente) |
| Plugin di terze parti malevolo (fase 3+) | Esfiltrazione via SDK | Sandbox, permessi granulari, review, kill-switch remoto |
| Noi stessi (scope creep, errori) | Telemetria eccessiva, log con PII | Telemetria opt-in anonima, linting automatico dei log, DPIA, review privacy per feature |

## 2. Architettura crittografica

### 2.1 Gerarchia delle chiavi

```
Passphrase/credenziali utente ──(KDF: Argon2id)──► Master Key (MK)
                                                    │  mai lasciare il device in chiaro
              ┌─────────────────────────────────────┤
              ▼                                     ▼
      Key Encryption Key (KEK)              Recovery Key (RK)
      cifra le Data Encryption Keys         24 parole / QR, generata al setup,
              │                             mostrata UNA volta, mai sul server
              ▼                             in chiaro (solo wrapped da RK stessa)
      DEK per dominio dati (per modulo)
      cifrano: DB locale, blob di sync, snapshot di backup
      (XChaCha20-Poly1305 / AES-256-GCM)
```

- **A riposo (device)**: database SQLite cifrato (SQLCipher o cifratura a livello di pagina) con DEK custodite nel keystore hardware (Secure Enclave / StrongBox). I file temporanei e le cache seguono la stessa politica.
- **In transito**: TLS 1.3 + pinning; i contenuti viaggiano già cifrati E2E, quindi il TLS protegge solo metadati di trasporto.
- **Sul server**: blob opachi + vettori di versione. Ciò che il server conosce: chi sei (account), quanti byte, quando sincronizzi, quali moduli hai attivi (per il registry). Ciò che non può conoscere: qualunque contenuto.
- **Rotazione**: le DEK sono rotabili senza ri-cifrare tutto (envelope encryption); il cambio passphrase ri-avvolge la KEK, non i dati.

### 2.2 Recupero dell'account (decisione delicata)

E2E significa che **se l'utente perde credenziali e Recovery Key, i dati sono irrecuperabili**. Decisione: accettiamo questo trade-off dichiarandolo con chiarezza, e lo mitighiamo con: (a) Recovery Key obbligatoria al setup con verifica di avvenuto salvataggio, (b) promemoria periodico di verifica, (c) possibilità di custodire la RK nel portachiavi di sistema (iCloud Keychain / Google Password Manager) come scelta esplicita dell'utente, con spiegazione del trade-off. Un "recupero amministrativo" lato server è **vietato dall'architettura**: la sua esistenza annullerebbe l'intera promessa E2E.

### 2.3 Biometria

- Sblocco app con Face ID / Touch ID / BiometricPrompt (classe 3/Strong); fallback al codice app; timeout di blocco configurabile (immediato/1/5/15 min).
- La biometria sblocca le chiavi nel keystore hardware; i template biometrici non lasciano mai l'hardware di sistema (non li tocchiamo mai noi).
- Schermate ad alta sensibilità (report finanziari, note) possono richiedere ri-autenticazione opzionale.

## 3. Backup, ripristino e protezione contro la perdita di dati

- **Locale**: ogni scrittura è transazionale (WAL); crash o kill del processo non producono mai stati corrotti (RNF-A2).
- **Cloud**: snapshot cifrati automatici (giornaliero ×7, settimanale ×4, mensile ×6) + delta continui via sync. Le generazioni multiple proteggono anche da corruzioni logiche propagate ("ho cancellato tutto ieri per errore" → ripristino puntuale).
- **Cestino applicativo**: le cancellazioni sono soft per 30 giorni (recuperabili in app), poi permanenti. Le cancellazioni esplicite "definitive" bypassano il cestino con conferma forte.
- **Ripristino**: UC-08; verificato da test E2E automatici a ogni release (il backup non testato non è un backup).

## 4. Sicurezza applicativa

- **SDL (Security Development Lifecycle)**: threat model per ogni feature nuova; review di sicurezza obbligatoria per codice che tocca crypto, sync, auth, billing; SAST/dipendenze (SCA) in CI con SBOM; segreti mai nel repo (vault + OIDC in CI).
- **Autenticazione**: token di sessione brevi + refresh revocabili per device; registry dei dispositivi visibile all'utente ("disconnetti questo device"); 2FA (TOTP) disponibile, obbligatoria per operazioni distruttive (cancellazione account).
- **Rate limiting e abuse prevention** su tutti gli endpoint; protezione brute-force su login (backoff + blocco progressivo).
- **Penetration test** di terza parte prima del lancio pubblico e poi annuale; programma di responsible disclosure con canale dedicato dal giorno 1 (security.txt).
- **Piano di incident response**: ruoli, runbook, tempi di notifica (GDPR: 72 h all'autorità se applicabile), comunicazione trasparente agli utenti.

## 5. Permessi e minimizzazione

Principio: **ogni permesso è chiesto nel momento del bisogno, con la spiegazione del beneficio, e l'app funziona anche senza**.

| Permesso | Quando viene chiesto | Se negato |
|---|---|---|
| Notifiche | Alla prima entità con promemoria (RF-52) | Tutto funziona; nessun promemoria |
| Calendario | All'attivazione del modulo Calendario | Il modulo mostra solo task/abitudini |
| HealthKit / Health Connect | All'attivazione del modulo Salute, con selezione granulare dei tipi di dato | Metriche manuali soltanto |
| Posizione (solo "mentre in uso") | Solo se l'utente crea un promemoria basato su luogo | Promemoria solo a orario |
| Microfono/riconoscimento vocale | Al primo uso della cattura vocale | Cattura testuale |

Nessun permesso "preventivo", nessun accesso a contatti, nessun tracciamento pubblicitario (**niente IDFA/AAID: la richiesta ATT non esisterà mai** perché non tracciamo).

## 6. Telemetria privacy-safe

- **Opt-in esplicito** durante l'onboarding (default: off nell'UE).
- Solo eventi comportamentali anonimi e aggregabili (es. "cattura completata", durata) con ID di installazione ruotato, **mai** contenuti, importi, titoli o testo libero.
- Lint automatico in CI che blocca l'aggiunta di proprietà di telemetria non presenti nell'allow-list revisionata dal privacy owner.
- Crash report sanitizzati (scrubbing di path, testi, token).

## 7. Conformità normativa

| Norma | Applicazione |
|---|---|
| **GDPR** | Basi giuridiche documentate; privacy by design/default (questa architettura); diritti dell'interessato self-service in app: accesso ed export (RF-43), rettifica (l'app stessa), cancellazione (RF-44, completata ≤ 30 giorni anche nei backup a rotazione), portabilità (JSON/CSV); DPIA prima del lancio (dati salute/finanze = alto rischio); registro trattamenti; DPA con i sub-processor (cloud provider); dati UE ospitati in region UE |
| **CCPA/CPRA** | Diritti equivalenti; nessuna "vendita" di dati (non ne abbiamo la possibilità tecnica) |
| **ePrivacy** | Nessun tracciamento; comunicazioni marketing solo opt-in |
| **App Store / Play policy** | Privacy Nutrition Labels e Data Safety form accurati e auditati a ogni release; account deletion in-app (obbligo store) = RF-44 |
| **PSD2/open banking** | Non applicabile nell'MVP (nessun collegamento bancario, ADR-7); rivalutazione con consulenza legale se attivato |
| **Minori** | Target 16+; nessuna feature rivolta a minori; age gate soft nell'onboarding |

## 8. Trasparenza come vantaggio competitivo

- **Whitepaper di sicurezza pubblico** (architettura E2E, gerarchia chiavi, cosa vediamo/non vediamo) scritto per essere leggibile: è uno strumento di marketing verso la persona "Marco" e la stampa tech.
- Pagina "I tuoi dati" in app: cosa è salvato, dove, cifrato come, con quali generazioni di backup — visibile in ogni momento.
- Impegno pubblico: nessuna pubblicità, nessuna vendita di dati, business model dichiarato (doc 07). Se il modello economico cambiasse, questo impegno è il vincolo.
