# 14 · Impostazioni, Profilo, Sicurezza e Abbonamento (SET / PROF / SEC)

> Eredita il [MFC](00-modello-funzionale-comune.md). Vincolo di design: **ogni impostazione è una sconfitta parziale** (P88) — questo documento elenca TUTTE le impostazioni ammesse; aggiungerne una richiede di passare i cancelli della Feature Philosophy.

## 1. Scopo e tracciabilità

| Perché esiste | Principi | JTBD | Decisioni |
|---|---|---|---|
| Il luogo unico e prevedibile per: account, sicurezza, tema, notifiche, dati, abbonamento — e la sede dei diritti (export, cancellazione, privacy) | P16-17, P88; C-art. Titolo I-II | J11, J14, J20 | D-05, D-09 |

## 2. Il catalogo completo delle impostazioni (chiuso, versionato)

| Gruppo | Impostazioni ammesse | Default |
|---|---|---|
| **Account** | email, password, 2FA (TOTP), registro dispositivi, chiave di recupero (visualizza di nuovo/rigenera), cancella account | 2FA off (proposta, non imposta) |
| **Sicurezza** | blocco biometrico on/off, timeout (subito/1/5/15 min), moduli sensibili (quali richiedono sblocco), contenuti su widget/notifiche (per modulo: pieno/limitato/nulla) | Biometria proposta al primo dato finanziario; sensibili: FIN, HLTH |
| **Aspetto** | tema (sistema/chiaro/scuro), colore accento (dal set), inizio settimana, confine del giorno (00–04, per HAB) | sistema; lunedì per locale it |
| **Notifiche** | budget giornaliero (0–10), orari silenzio, orario digest, controlli per categoria (NTF-007) | 3; 22–8; digest 18:00 |
| **Moduli** | scorciatoia alla Galleria (GAL) — le preferenze di modulo vivono NEI moduli | — |
| **Dati** | export completo/selettivo, import, backup (stato, frequenza, rete), ripristino puntuale, cestino, azzeramento apprendimento parser (CAPT-R-04) | backup on |
| **Privacy** | telemetria on/off con elenco leggibile di cosa viene raccolto, pagina "I tuoi dati" (cosa/dove/come cifrato) | off in UE |
| **Abbonamento** | stato piano, confronto piani, acquisto/ripristino acquisti, disdetta (link diretto allo store), fatture | — |
| **Lingua e formati** | lingua app (o sistema), valuta primaria, formato data/ora | sistema |
| **Accessibilità** | (in aggiunta al rispetto delle preferenze di sistema, che non richiedono impostazioni nostre) dimensione testo relativa in-app, alto contrasto forzato, riduzione animazioni forzata | eredita dal sistema |
| **Aiuto** | FAQ offline, contatta il supporto, novità di versione, stato promesse (link al report trasparenza) | — |

## 3. Funzioni

| ID | Nome | Descrizione e motivo | Pri |
|----|------|----------------------|-----|
| SET-001 | Schermata impostazioni | Il catalogo §2, ricercabile; ogni voce con descrizione a portata | M |
| SET-002 | Pagina "I tuoi dati" | Trasparenza attiva: cosa è salvato, dove, cifrato come, generazioni di backup (C-art. 6; PB doc 06 §8) | M |
| SEC-001 | Configurazione biometria | Attivazione con verifica; fallback codice app (6 cifre min) | M |
| SEC-002 | Chiave di recupero | Generazione al setup account, verifica del salvataggio ("scrivi 3 parole a campione"), rigenerazione con ri-autenticazione (D-09) | M |
| SEC-003 | 2FA TOTP | Setup standard con codici di riserva; obbligatoria per cancellazione account se attiva | S |
| PROF-001 | Profilo | Nome visualizzato (facoltativo), immagine locale (facoltativa); NESSUN profilo pubblico (C-art. 55) | M |
| SET-003 | Gestione abbonamento | Stato, upgrade/downgrade, disdetta facile (C-art. 163), prezzi con tutte le condizioni | M |
| SET-004 | Supporto in-app | FAQ offline + contatto; i ticket non includono dati utente se non allegati esplicitamente dall'utente | M |

**Scheda estesa SEC-002** — *Casi limite*: utente che salta la verifica del salvataggio → può, con avviso; promemoria di ri-verifica a 30/180 giorni (max 2/anno — C-art. 63); smarrimento chiave CON accesso attivo → rigenerazione immediata; smarrimento chiave SENZA accesso → percorso onesto: "i dati non sono recuperabili, ecco perché" con la spiegazione della promessa (D-09) — il copione di questo momento è parte della spec, scritto con la massima cura.

## 4–7. Regole, stati, edge (sintesi)

| ID | Regola | Motivo |
|----|--------|--------|
| SET-R-01 | Il catalogo §2 è chiuso: nuove impostazioni solo via Feature Philosophy (cancello 7 incluso) | P88 |
| SET-R-02 | Ogni impostazione ha effetto immediato e reversibile; nessun "riavvia per applicare" | P2 |
| SET-R-03 | Le impostazioni sincronizzano tra device, tranne: biometria/timeout (per-device), rete backup (per-device) | Sensatezza per-device |
| SET-R-04 | La disdetta è raggiungibile in ≤ 2 tocchi da "Abbonamento" e non apre percorsi di trattenimento ("sei sicuro? ma perché? e se ti diamo…") — max 1 schermata informativa onesta | C-art. 163, 72 |
| PROF-R-01 | Nessun dato di profilo è obbligatorio oltre l'email dell'account | C-art. 4 |

- Stati: anonimo (SET mostra la proposta account al posto delle sezioni account); trial (giorni residui QUI, non altrove); pagamenti falliti (grace period con avviso onesto, mai blocco improvviso dei dati — C-art. 17).
- Edge: cambio email con conferma su entrambe; downgrade con più moduli attivi del nuovo piano → i moduli oltre soglia restano attivi in sola lettura finché l'utente sceglie quali tenere (mai scelta forzata a tempo, mai dati bloccati — MFC §4 stato Free).

## 8. Criteri di accettazione

- **SET-AC-01** — *Dato* un abbonato Plus, *quando* disdice, *allora* completa in ≤ 2 tocchi + conferma store, riceve la data di fine servizio e nessun ulteriore contatto di trattenimento.
- **SEC-AC-01** — *Dato* biometria attiva con timeout 5 min, *quando* l'app torna in foreground dopo 6 min, *allora* richiede lo sblocco e i moduli sensibili restano offuscati fino a sblocco riuscito.
- **SEC-AC-02** — *Dato* la rigenerazione della chiave di recupero, *allora* la vecchia chiave è invalidata e il ripristino funziona solo con la nuova.
- **SET-AC-02** — *Dato* il downgrade a Free con 4 moduli attivi, *allora* l'utente sceglie i 2 da mantenere attivi senza scadenze pressanti, e gli altri 2 restano leggibili ed esportabili per sempre.

---

*Prossimo: [Insight Engine](15-insight.md)*
