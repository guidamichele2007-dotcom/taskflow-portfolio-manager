# 04 · UX, Design System e Navigazione

> Vincoli dal brief: minimalismo elegante e Premium, Dark/Light Mode completi, animazioni morbide, tipografia moderna, massima leggibilità, accessibilità completa, minor numero di tocchi possibile, carico cognitivo ridotto, scoperta progressiva delle funzionalità, design senza tempo.

## 1. Filosofia di design

1. **Calma, non stimolo.** OmniLife gestisce la vita dell'utente: l'interfaccia deve abbassare il battito, non alzarlo. Niente colori urlati, niente badge rossi ovunque, niente dark pattern.
2. **Il contenuto è l'interfaccia.** Chrome ridotto al minimo; i dati dell'utente (i suoi task, i suoi numeri) sono i protagonisti visivi.
3. **Rivelazione progressiva.** Ogni schermata mostra il minimo necessario; la profondità si apre su richiesta (es. un task appare come una riga; i dettagli si espandono solo se toccato).
4. **Senza tempo.** Nessun trend visivo effimero (niente glassmorphism spinto, gradienti di moda): geometria semplice, spazio bianco, gerarchia tipografica. Ciò che è di moda invecchia; ciò che è chiaro no.
5. **Ogni tocco conta.** Ogni flusso ha un "budget di tocchi" dichiarato e misurato (vedi §4). Se una revisione supera il budget, il flusso torna in design.

## 2. Design System «Omni»

Il design system è un progetto interno a sé, con versionamento proprio: è ciò che rende possibile che 8 moduli sviluppati da persone diverse sembrino disegnati da una sola mano (requisito: componenti condivisi in ogni schermata).

### 2.1 Fondamenta (token)

- **Colore**: palette neutra (superfici, testo) + **un colore d'accento del brand** + un colore semantico per modulo (usato con parsimonia: icona e micro-accenti, mai superfici intere). Tutti i token hanno variante Light e Dark definite insieme, mai derivate automaticamente. Contrasto minimo: 4,5:1 testo normale, 3:1 testo grande e componenti (WCAG 2.2 AA), verificato in CI sui token.
- **Tipografia**: font di sistema (SF Pro / Roboto o Inter) — scelta deliberata: rendering perfetto, zero costo di caricamento, familiarità. Scala tipografica a 7 gradini con line-height generosi; supporto completo a Dynamic Type / font scaling fino al 200% senza rotture di layout.
- **Spaziatura**: griglia a 4 pt; raggi, elevazioni e bordi tokenizzati.
- **Iconografia**: un solo set, stroke uniforme (SF Symbols + equivalenti Material custom armonizzati), dimensioni ottiche coerenti.
- **Motion**: durate 150–350 ms, curve spring morbide standardizzate in 3 preset (enfasi, standard, uscita). Regole: le animazioni comunicano relazioni spaziali e stato, mai decorazione; tutte disattivabili con "riduci animazioni" di sistema.
- **Aptica**: vocabolario definito (successo lieve, avviso, completamento abitudine) coerente tra iOS e Android.

### 2.2 Componenti condivisi (libreria)

Riga-entità (task/abitudine/spesa condividono la stessa anatomia) · Card della Home · Foglio di cattura · Foglio di dettaglio espandibile · Selettore data/ora "umano" (oggi/domani/weekend/scegli) · Anello/barra di progresso · Grafici (linea, barre, anello budget) con stile unico · Stato vuoto (sempre: illustrazione sobria + una frase + un'azione) · Banner insight · Celebrazione sobria (per completamenti: micro-animazione + aptica, 800 ms, mai modale).

Regola: **un modulo non può introdurre un componente visivo nuovo se ne esiste uno equivalente nella libreria**; le proposte di nuovi componenti passano dalla review del design system.

### 2.3 Dark Mode

Progettata in parallelo, non convertita: superfici scure con elevazione tramite tono (non ombre), accenti desaturati per ridurre l'affaticamento, test di contrasto dedicati. Segue il sistema di default, con override manuale (C-06).

## 3. Struttura della navigazione

### 3.1 Modello

```
┌──────────────────────────────────────────────┐
│                 SCHERMATA                     │
│                                               │
│   [Contenuto della sezione corrente]          │
│                                               │
│                                   ( + )  ←── Cattura rapida:
│  ┌─────────────────────────────────────┐      FAB persistente,
│  │  Oggi   Moduli   Cerca   Profilo    │      sempre visibile
│  └─────────────────────────────────────┘
│         Tab bar fissa (4 destinazioni)
└──────────────────────────────────────────────┘
```

- **Tab 1 — Oggi (Home)**: la vista unificata (C-01). È la risposta di default all'apertura dell'app.
- **Tab 2 — Moduli**: hub con i moduli attivi dell'utente (griglia riordinabile) + accesso alla Galleria (C-04). **Scelta chiave**: i moduli NON occupano tab dedicate — con moduli dinamici una tab bar dinamica diventerebbe imprevedibile (violazione di coerenza) e non scalerebbe oltre 5 moduli. L'utente può però **fissare un modulo preferito** con pressione lunga sulla tab (personalizzazione senza caos).
- **Tab 3 — Cerca**: ricerca globale (C-03) + filtri per tipo; le ricerche recenti come scorciatoie.
- **Tab 4 — Profilo**: impostazioni, sicurezza, abbonamento, insight/report aggregati, revisione settimanale.
- **(+) Cattura**: pulsante flottante persistente su ogni schermata (C-02). Tocco = cattura testuale con parser; pressione lunga = scorciatoie (spesa, task, nota vocale).

### 3.2 Regole di profondità

- Profondità massima: **3 livelli** (tab → lista → dettaglio). Qualsiasi flusso che richieda un quarto livello va ripensato.
- I dettagli si aprono come **fogli espandibili** (sheet) e non come push di navigazione, quando l'utente deve mantenere il contesto: meno disorientamento, ritorno a un gesto.
- Back gesture/predictive back pienamente supportati; stato di navigazione ripristinato dopo kill del processo.

### 3.3 Superfici esterne all'app (riduzione tocchi)

- **Widget** home screen: Oggi (lista interattiva), Abitudini (spunta diretta), Budget (stato). Le spunte avvengono nel widget senza aprire l'app (H-06).
- **Quick actions** da icona app: Cattura, Nuova spesa, Cerca.
- **Notifiche azionabili**: completa/posticipa dall'avviso.
- **Voce**: Siri Shortcuts / App Actions per cattura e spunta (UC-11).

## 4. Flussi utente principali (con budget di tocchi)

> Notazione: numero minimo di interazioni dall'intento al risultato. Il budget è un requisito verificato in QA (doc 05 RNF-U1).

### 4.1 Onboarding (C-05) — budget: 60 secondi, ≤ 6 tocchi
1. Splash → proposta di valore in una frase, `Inizia` (1).
2. Scelta di 1–2 moduli da griglia illustrata (2–3). Consiglio pre-selezionato: Attività.
3. Primo dato reale: "Scrivi la prima cosa che devi fare" → cattura (4–5).
4. Richiesta notifiche **posticipata** al primo momento di valore (quando l'utente crea qualcosa con scadenza: "Vuoi che te lo ricordi?") — mai al primo avvio: il tasso di consenso raddoppia quando la richiesta ha contesto.
5. Account/registrazione **rimandabile**: l'app funziona subito in locale; la registrazione viene proposta quando c'è qualcosa da proteggere ("Vuoi mettere al sicuro i tuoi dati?"). Riduce l'abbandono al primo avvio, coerente con Offline-First.

### 4.2 Cattura rapida (UC-01) — budget: ≤ 3 tocchi / ≤ 3 secondi
1. `+` (1) → digita/detta "30 cena con Sara" (testo) → il parser mostra l'anteprima interpretata (Spesa · 30 € · Ristoranti · oggi) con i chip modificabili → `Salva` (2–3).
- Errore del parser? Un tocco sul chip sbagliato lo corregge; il sistema **apprende** le correzioni (es. "cena con Sara" → categoria Ristoranti) on-device.
- Da widget: 2 tocchi totali. Da voce: 0 tocchi (conferma aptica).

### 4.3 Spunta abitudine (UC-05) — budget: 1 tocco
Dal widget o dalla notifica: 1 tocco. Dall'app: Home → riga abitudine, 1 tocco. Undo immediato disponibile (nessun dialogo di conferma: la conferma preventiva è attrito; l'undo è gratis).

### 4.4 Registrazione spesa (UC-03) — budget: ≤ 3 tocchi
Via cattura (4.2) oppure pressione lunga su `+` → `Spesa` → tastierino con importi e categorie recenti come chip (1 tocco ciascuno) → salva. Le ricorrenze (F-05) azzerano i tocchi per le spese fisse.

### 4.5 Pianificare la settimana (UC-04) — budget: ≤ 12 tocchi per ~10 decisioni
Profilo → `Revisione settimanale` → sequenza di card, una decisione per schermata, azioni a un tocco (Pianifica/Rimanda/Archivia) con swipe equivalenti → riepilogo finale. Progresso visibile ("4 di 9"), uscita possibile in ogni momento senza perdere i progressi.

### 4.6 Attivare un modulo (UC-09) — budget: ≤ 4 tocchi
Moduli → Galleria → scheda modulo (anteprima con **dati di esempio interattivi**, non screenshot) → `Attiva` → micro-onboarding di 1–3 schermate → prima entità creata.

### 4.7 Ripristino su nuovo device (UC-08) — budget: ≤ 5 tocchi
Login → biometria/chiave di recupero → schermata di progresso con stima → fine. Nessuna decisione richiesta all'utente durante il ripristino.

## 5. Psicologia comportamentale applicata (con etica esplicita)

**Linea rossa**: usiamo la psicologia per aiutare l'utente a fare ciò che LUI ha dichiarato di volere; mai per estrarre attenzione o denaro contro il suo interesse. Niente streak-anxiety, niente FOMO, niente paywall mascherati da progressi.

1. **Modello Fogg (B = MAP)**: massimizziamo l'abilità (cattura a 3 tocchi, widget) e progettiamo i prompt (notifiche contestuali) invece di sperare nella motivazione.
2. **Costanza resiliente (H-03)**: la metrica tollera i salti (es. media mobile su 7 giorni). Motivo: la letteratura sull'abandonment mostra che la rottura della streak è il principale evento di churn nei habit tracker. La perdita (loss aversion) va evitata, non sfruttata.
3. **Fresh start effect**: proposte di ripartenza a inizio settimana/mese ("Ricominciamo da lunedì?") dopo periodi di inattività — al posto di notifiche colpevolizzanti.
4. **Progresso visibile** (effetto Zeigarnik / goal gradient): anelli e barre mostrano quanto manca, non quanto si è falliti; le milestone (G-03) celebrano in modo sobrio.
5. **Riduzione del carico cognitivo**: una decisione per schermata nei flussi guidati; default intelligenti ovunque (data = oggi, conto = ultimo usato); scelta tra al massimo 3 opzioni nei dialoghi.
6. **Budget di notifiche (X-07)**: massimo N notifiche/giorno (default 3, configurabile), raggruppamento in digest, orari di silenzio automatici (notte, e "focus" di sistema rispettati). Una notifica ignorata 3 volte propone la propria disattivazione. **Le notifiche sono un patto di fiducia**: ogni notifica inutile erode la permission più preziosa che abbiamo.

## 6. Accessibilità (WCAG 2.2 AA come minimo, non come traguardo)

- **Screen reader**: ogni componente della libreria nasce con etichette, ruoli, ordine di focus e annunci di stato (VoiceOver/TalkBack); i grafici hanno descrizioni testuali equivalenti e tabelle dati accessibili.
- **Dynamic Type/Font scaling fino a 200%**: i layout sono progettati per riflow, non truncation; test automatici a 3 taglie.
- **Target di tocco**: ≥ 44×44 pt / 48×48 dp ovunque, inclusi i widget.
- **Contrasto**: verificato sui token in CI (§2.1); modalità alto contrasto rispettata.
- **Riduzione movimento**: ogni animazione ha variante statica; nessuna informazione veicolata solo dal movimento o solo dal colore.
- **Input alternativi**: tutte le azioni raggiungibili senza gesture complesse (le swipe hanno sempre equivalente a tocco); supporto tastiera esterna e Switch Control/Access.
- **Linguaggio**: tono gentile, frasi brevi, niente gergo (persona Anna); localizzazione it/en al lancio con testi scritti, non tradotti meccanicamente.
- **Processo**: audit di accessibilità a ogni release; una persona del team ha la responsabilità formale dell'accessibilità; test con utenti reali con disabilità almeno 2 volte l'anno.

## 7. Scoperta delle funzionalità

- **Contestuale, non frontale**: mai tour di 10 schermate. I suggerimenti appaiono quando l'azione è rilevante (es. dopo la 3ª spesa "sai che puoi creare un budget?"), massimo uno per sessione, sempre archiviabili.
- **Empty state didattici**: ogni stato vuoto insegna l'azione principale con un esempio concreto.
- **Galleria moduli** come luogo di scoperta esplicita e volontaria (UC-09).
- **Novità di release**: una card discreta nella Home, mai modale bloccante.
