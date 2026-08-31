# 01 · Visione del Prodotto e Analisi

## 1. Visione del prodotto

### 1.1 Dichiarazione di visione

> **OmniLife è il sistema operativo personale della vita quotidiana: un ecosistema mobile modulare in cui ogni persona compone la propria app ideale, scegliendo solo i moduli che le servono, con i propri dati sempre disponibili, sempre privati e sempre connessi tra loro.**

### 1.2 Il cambiamento che vogliamo produrre

Oggi una persona media usa 6–12 app per gestire la propria vita: una per le attività, una per le spese, una per le abitudini, una per le note, una per il calendario, una per la salute. Ognuna ha il proprio account, il proprio abbonamento, il proprio linguaggio visivo, il proprio silos di dati. Il risultato è:

- **Frammentazione cognitiva**: l'utente deve ricordare *dove* si trova ogni informazione prima ancora di usarla.
- **Dati scollegati**: la spesa registrata non sa nulla dell'obiettivo di risparmio; l'abitudine "correre" non sa nulla dell'evento in calendario che la rende impossibile oggi.
- **Costo cumulativo**: 4–5 abbonamenti da 3–10 €/mese ciascuno.
- **Attrito quotidiano**: ogni app ha onboarding, notifiche e convenzioni diverse.

OmniLife elimina la frammentazione senza cadere nell'errore opposto — la super-app monolitica che fa tutto male. La risposta è l'**architettura a moduli**: un nucleo minimo, elegantissimo e velocissimo, su cui l'utente attiva solo ciò che gli serve.

### 1.3 Perché noi, perché ora

- **Tecnologia matura**: i framework dichiarativi (SwiftUI/Jetpack Compose o Flutter), i database locali sincronizzabili (CRDT) e la crittografia end-to-end accessibile rendono possibile oggi ciò che 5 anni fa richiedeva team enormi.
- **Domanda dimostrata**: la crescita di Notion, Todoist, YNAB e Habitica dimostra la disponibilità a pagare per strumenti di vita personale; nessuno di essi però copre l'intero spettro con qualità uniforme.
- **Sfiducia crescente verso il data-harvesting**: un prodotto che fa della privacy end-to-end un pilastro ha un vantaggio narrativo e regolatorio permanente.

### 1.4 Che cosa OmniLife NON è

Definire i confini è una decisione progettuale, non una rinuncia:

- **Non è un social network.** Nessun feed, nessun contenuto di terzi, nessuna economia dell'attenzione.
- **Non è una super-app di servizi** (pagamenti, delivery, messaging). Non intermediamo transazioni con terzi nel core.
- **Non è uno strumento di collaborazione aziendale.** Il team/enterprise è un'estensione futura, non il cuore.
- **Non è un aggregatore di app esterne.** Le integrazioni servono i moduli, non li sostituiscono.

---

## 2. Analisi del problema

### 2.1 Il problema radice

> Le persone non riescono a mantenere una visione unificata e azionabile della propria vita perché gli strumenti digitali che usano frammentano dati, attenzione e abitudini.

### 2.2 Scomposizione del problema

| # | Sotto-problema | Evidenza | Conseguenza per l'utente |
|---|----------------|----------|--------------------------|
| P1 | **Frammentazione degli strumenti** | 6–12 app per la gestione personale; switch di contesto continui | Carico cognitivo, informazioni perse, abbandono degli strumenti |
| P2 | **Dati non integrati** | Nessuna app consumer collega spese ↔ obiettivi ↔ tempo ↔ salute | Decisioni prese senza contesto ("posso permettermi questa vacanza?" richiede 3 app) |
| P3 | **Attrito di inserimento** | La registrazione di una spesa o task richiede 5–10 tocchi nelle app leader | I dati non vengono inseriti → lo strumento perde valore → abbandono |
| P4 | **Fragilità della costanza** | L'80% degli utenti abbandona le app di abitudini entro 3 mesi | Frustrazione, senso di fallimento, sfiducia verso la categoria |
| P5 | **Dipendenza dalla rete** | Molte app leader degradano o si bloccano offline | Inaffidabilità percepita nei momenti d'uso reali (metro, viaggio, roaming) |
| P6 | **Privacy opaca** | Modelli di business basati su dati comportamentali | Autocensura dell'utente: non registra ciò che è davvero sensibile (salute, denaro) |
| P7 | **Costo cumulativo** | 4–5 abbonamenti separati = 15–40 €/mese | Rinuncia a strumenti utili per ragioni economiche |

### 2.3 Perché il problema persiste

1. **Incentivi disallineati**: le app monofunzione massimizzano l'engagement nella propria nicchia, non il benessere complessivo dell'utente.
2. **Difficoltà tecnica reale**: costruire più domini con qualità uniforme è difficile; le super-app esistenti (es. in ambito produttività) sacrificano la profondità dei singoli moduli.
3. **Lock-in dei dati**: l'export/import tra app è volutamente scomodo.

### 2.4 La nostra tesi di soluzione

- **Un solo nucleo, molti moduli**: qualità uniforme garantita da un design system e da un'infrastruttura dati condivisi (risolve P1, P7).
- **Grafo dati personale**: ogni entità (task, spesa, abitudine, evento, nota) vive in un modello dati comune e collegabile (risolve P2).
- **Inserimento "sotto i 3 secondi"**: cattura rapida universale da qualunque schermata, widget, voce e automazioni (risolve P3).
- **Psicologia della gentilezza**: meccaniche di costanza basate su flessibilità e auto-compassione, non su streak punitive (risolve P4 — dettagli nel doc 04).
- **Offline-First con sync CRDT**: il device è la fonte primaria; il cloud è una replica cifrata (risolve P5, P6).
- **Un solo abbonamento** con prezzo inferiore alla somma delle alternative (risolve P7 — dettagli nel doc 07).

---

## 3. Analisi dei competitor

> Metodo: per ogni competitor identifichiamo i **punti di forza da superare** (non da copiare) e la **debolezza strutturale** che OmniLife sfrutta. L'analisi copre i leader per categoria + le super-app di produttività.

### 3.1 Quadro comparativo

| Competitor | Categoria | Punti di forza (da superare) | Debolezza strutturale (nostra opportunità) |
|---|---|---|---|
| **Todoist** | Task | Cattura rapida con NLP ("domani alle 9"), multipiattaforma impeccabile | Solo task: nessun contesto di vita; gamification (karma) percepita come superflua |
| **Things 3** | Task | Design di riferimento, interazioni curatissime, acquisto one-time | Solo Apple, nessuna collaborazione, nessun altro dominio |
| **TickTick** | Task+ | Combina task, calendario, habit, pomodoro a basso prezzo | Integrazione superficiale tra i domini: sono tab affiancate, non dati collegati |
| **Notion** | Workspace | Flessibilità totale, database componibili | Curva di apprendimento ripida, mobile lento, offline debole; è uno strumento per costruire strumenti, non un prodotto finito |
| **YNAB** | Finanze | Metodo di budgeting fortissimo (zero-based), community fedele | Prezzo alto (~15 $/mese), curva ripida, USA-centrico, nessun altro dominio |
| **Money Manager / Spendee** | Finanze | Inserimento spese semplice | Nessun collegamento con obiettivi o tempo; monetizzazione con pubblicità |
| **Streaks / Habitica** | Abitudini | Streaks: widget eccellenti; Habitica: gamification profonda | Streak punitive → abbandono dopo la prima rottura; Habitica: estetica divisiva |
| **Apple Salute / Google Fit** | Salute | Aggregazione dati sensori a livello OS | Solo lettura/aggregazione: nessuna azione, nessun collegamento con il resto della vita |
| **Obsidian / Bear** | Note | Markdown, velocità, proprietà dei dati (Obsidian) | Nessuna integrazione con azione (task/tempo/denaro); Obsidian mobile complesso |
| **Google Calendar** | Calendario | Standard di fatto, inviti, ubiquità | Nessuna nozione di energia/priorità personale; solo eventi, non vita |
| **Structured / Sunsama** | Daily planner | Time-boxing quotidiano elegante | Richiedono pianificazione manuale quotidiana costosa; Sunsama molto caro (20 $/mese) |

### 3.2 Sintesi strategica

1. **Nessun competitor integra davvero i domini.** Chi ne copre più d'uno (TickTick, Notion) li giustappone senza collegarli. Il **grafo dati personale** è la nostra differenziazione difendibile.
2. **Il design di Things 3 è il livello da raggiungere** — ma su entrambe le piattaforme e su tutti i moduli.
3. **La cattura rapida di Todoist è il benchmark di attrito** — il nostro obiettivo è batterla: qualunque entità registrabile in < 3 secondi.
4. **Il fallimento delle streak punitive** è documentato dall'abbandono di massa: la nostra meccanica di costanza flessibile è un'opportunità di retention, non un dettaglio.
5. **Prezzo**: posizionarsi sotto la somma delle alternative (YNAB + Todoist + Streaks ≈ 25 €/mese) con un'offerta unica a ~8 €/mese crea un value gap immediato e comunicabile.

### 3.3 Barriere difensive che costruiamo

- **Effetto rete interno ai dati**: più moduli l'utente attiva, più il grafo personale diventa prezioso e insostituibile (switching cost positivo, non coercitivo: l'export completo è sempre disponibile).
- **Fiducia E2E**: la crittografia end-to-end è difficile da retrofittare per incumbent che monetizzano i dati.
- **Piattaforma di plugin** (fase 3+): l'ecosistema di moduli di terze parti moltiplica i casi d'uso senza moltiplicare il nostro costo di sviluppo.

---

## 4. Definizione degli utenti

### 4.1 Segmento primario

**Adulti 22–45 anni, digitalmente maturi, con vita "multi-dominio" attiva** (lavoro + finanze personali + salute + progetti personali), che già usano ≥ 2 app di organizzazione personale e provano frustrazione per la frammentazione. Mercato iniziale: Italia + Europa occidentale, poi mondo anglofono.

### 4.2 Personas

#### Persona 1 — «Giulia», la professionista sovraccarica (persona primaria)
- **Età/contesto**: 31 anni, product designer, vive a Milano, pendolare.
- **Strumenti attuali**: Todoist, Google Calendar, foglio Excel per le spese, Apple Salute.
- **Obiettivi**: sentirsi in controllo senza dedicare tempo alla "manutenzione degli strumenti"; risparmiare per un mutuo.
- **Frustrazioni**: dimentica di registrare le spese; il budget vive in un foglio che apre una volta al mese; la pianificazione della giornata richiede 3 app.
- **Momenti d'uso**: in metro (offline!), pause pranzo, domenica sera (pianificazione settimanale).
- **Cosa la conquista**: cattura in 2 tocchi, vista "Oggi" unificata, insight ("questo mese hai speso il 30% in meno in delivery").
- **Cosa la fa scappare**: onboarding lungo, notifiche aggressive, qualsiasi lentezza.

#### Persona 2 — «Marco», l'ottimizzatore quantificato
- **Età/contesto**: 27 anni, sviluppatore, appassionato di quantified self.
- **Strumenti attuali**: Obsidian, Habitica, spreadsheet elaborati, smartwatch.
- **Obiettivi**: tracciare tutto, correlare tutto, possedere i propri dati.
- **Frustrazioni**: i dati sono in silos; l'export è scomodo; le app "carine" sono poco potenti.
- **Cosa lo conquista**: export completo, API/plugin, correlazioni tra domini, crittografia E2E verificabile.
- **Cosa lo fa scappare**: lock-in dei dati, funzioni "magiche" non spiegate, mancanza di densità informativa nelle viste avanzate.
- **Ruolo strategico**: early adopter, evangelista nelle community (Reddit, HN), beta tester esigente.

#### Persona 3 — «Anna», la neofita delle buone abitudini
- **Età/contesto**: 42 anni, insegnante, due figli, poco tempo e poca pazienza tecnologica.
- **Strumenti attuali**: note sparse sul telefono, memoria, carta.
- **Obiettivi**: ricominciare a fare movimento, tenere d'occhio le spese familiari, non dimenticare scadenze.
- **Frustrazioni**: le app la fanno sentire inadeguata; ha abbandonato 3 habit tracker per streak rotte.
- **Cosa la conquista**: onboarding di 60 secondi con un solo modulo attivo, tono gentile, recupero delle abitudini saltate senza punizione, testo grande e leggibile.
- **Cosa la fa scappare**: complessità visibile, gergo, paywall aggressivo al primo avvio.
- **Ruolo strategico**: rappresenta la massa di mercato; se il prodotto funziona per Anna, la scalabilità del funnel è dimostrata.

#### Persona 4 — «Luca», lo studente pianificatore
- **Età/contesto**: 21 anni, studente universitario fuori sede, budget strettissimo.
- **Obiettivi**: gestire sessioni di studio, spese con coinquilini, abitudini di sonno.
- **Cosa lo conquista**: piano gratuito genuinamente utile, widget, dark mode, prezzo studenti.
- **Ruolo strategico**: acquisizione organica via passaparola e social; futuro utente pagante ad alto LTV.

### 4.3 Anti-personas (fuori target deliberato per il core)

- **Team aziendali** che cercano project management collaborativo (→ Asana, Linear). Sarebbe una distrazione dal posizionamento personale.
- **Trader/investitori attivi** che cercano portfolio tracking in tempo reale: il modulo Finanze gestisce budget e obiettivi, non il trading.
- **Utenti che vogliono un social**: nessuna funzione di confronto pubblico tra utenti nel core.

---

## 5. Casi d'uso

> Formato: attore, trigger, flusso principale, valore generato. I casi d'uso con `[INT]` dimostrano l'integrazione tra moduli — il cuore della differenziazione.

### UC-01 · Cattura rapida universale
**Attore**: qualsiasi persona. **Trigger**: un pensiero da non perdere (task, spesa, idea), in qualsiasi momento.
**Flusso**: (1) apre l'app o il widget → (2) tocca il pulsante di cattura, sempre presente → (3) scrive o detta in linguaggio naturale ("30€ cena con Sara stasera") → il parser propone entità e modulo (Spesa, 30 €, categoria Ristoranti, oggi) → (4) conferma con un tocco.
**Valore**: inserimento in ≤ 3 secondi e ≤ 3 tocchi; il dato entra nel sistema anche offline. **Questo caso d'uso è il più importante dell'intero prodotto**: se fallisce, tutti i moduli perdono i dati che li alimentano.

### UC-02 · La mia giornata in un colpo d'occhio
**Attore**: Giulia. **Trigger**: inizio giornata.
**Flusso**: apre l'app → la Home "Oggi" mostra in un'unica vista: eventi del calendario, task pianificati, abitudini del giorno, saldo del budget mensile. Nessuna navigazione richiesta.
**Valore**: la domanda "cosa devo fare oggi?" ha una risposta in 0 tocchi dopo l'apertura.

### UC-03 · Registrare una spesa e vederne l'impatto `[INT]`
**Attore**: Anna. **Trigger**: ha appena pagato la spesa al supermercato.
**Flusso**: cattura rapida "82€ supermercato" → il modulo Finanze aggiorna il budget "Alimentari" → se il budget supera la soglia dell'80%, la Home mostra un insight discreto (non una notifica push) → l'obiettivo di risparmio collegato ricalcola la proiezione.
**Valore**: ogni spesa risponde implicitamente a "come sto andando?", senza aprire report.

### UC-04 · Pianificazione settimanale guidata `[INT]`
**Attore**: Giulia. **Trigger**: domenica sera (rituale suggerito, mai imposto).
**Flusso**: apre la "Revisione settimanale" → il sistema presenta in sequenza: task in scadenza, task senza data, abitudini con costanza in calo, eventi della settimana entrante, stato dei budget → per ogni elemento un'azione a un tocco (pianifica, rimanda, archivia, rivedi).
**Valore**: la pianificazione settimanale passa da 30 minuti su 3 app a 5 minuti guidati su una.

### UC-05 · Mantenere un'abitudine con flessibilità
**Attore**: Anna. **Trigger**: sera, promemoria gentile dell'abitudine "camminata".
**Flusso**: completa con un tocco dal widget o dalla notifica → se salta un giorno, la costanza non si azzera: il sistema usa una "resilienza" (es. 5 giorni su 7 = obiettivo raggiunto) → dopo 2 salti consecutivi, l'app propone di ridurre l'obiettivo invece di colpevolizzare.
**Valore**: retention emotiva; l'utente non abbandona per senso di fallimento.

### UC-06 · Obiettivo trasversale «Vacanza in Giappone» `[INT]`
**Attore**: Marco. **Trigger**: decide di partire tra 10 mesi.
**Flusso**: crea un Obiettivo → collega: un obiettivo di risparmio (2.500 €, dal modulo Finanze), una lista di task (visto, voli, itinerario), un'abitudine (studio giapponese 3×/settimana), una scadenza in calendario → la vista Obiettivo mostra il progresso aggregato di tutte le dimensioni.
**Valore**: è il caso d'uso che nessun competitor può replicare senza rifare la propria architettura dati.

### UC-07 · Uso completamente offline
**Attore**: Giulia in metro / in volo. **Trigger**: nessuna connettività.
**Flusso**: ogni funzione (cattura, spunta, consultazione, modifica, persino creazione di budget) funziona identica → al ritorno della rete, la sincronizzazione riconcilia in background senza conflitti visibili → in caso di modifica concorrente da due device, risoluzione automatica CRDT (nessun dialogo "quale versione tieni?").
**Valore**: affidabilità percepita assoluta; differenziatore diretto contro Notion.

### UC-08 · Nuovo dispositivo, ripristino totale
**Attore**: chiunque cambi telefono. **Trigger**: primo avvio su device nuovo.
**Flusso**: login → verifica biometrica/chiave di recupero → i dati cifrati vengono scaricati e decifrati localmente → l'app è identica a prima, moduli attivi inclusi, in < 2 minuti.
**Valore**: fiducia; il backup automatico cifrato elimina la paura di perdita dati (requisito del brief).

### UC-09 · Attivare un nuovo modulo
**Attore**: Anna, dopo 3 settimane di solo modulo Abitudini. **Trigger**: la Home suggerisce (una sola volta, in modo contestuale) il modulo Finanze.
**Flusso**: apre la Galleria Moduli → scheda del modulo con anteprima interattiva → attiva con un tocco → onboarding del modulo in ≤ 3 schermate → il modulo appare nella navigazione.
**Valore**: crescita dell'engagement per espansione progressiva, non per sovraccarico iniziale.

### UC-10 · Esportare tutto e andarsene
**Attore**: Marco. **Trigger**: vuole verificare di non essere prigioniero.
**Flusso**: Impostazioni → Esporta dati → archivio completo (JSON + CSV) generato sul device → condivisione con qualunque mezzo.
**Valore**: paradossalmente aumenta la retention: la libertà di uscire è il motivo per restare. È anche un obbligo GDPR (portabilità).

### UC-11 · Dettatura vocale di un task con scadenza
**Attore**: Luca, in bicicletta si ferma un attimo. **Trigger**: si ricorda della consegna.
**Flusso**: pressione lunga sul widget → dettatura "consegnare tesina di statistica venerdì alle 14" → parsing NLP → task creato con data e ora, conferma vocale/aptica.
**Valore**: cattura senza guardare lo schermo; accessibilità per utenti con disabilità visive o motorie.

### UC-12 · Insight settimanale trasversale `[INT]`
**Attore**: Marco. **Trigger**: lunedì mattina, digest settimanale (opt-in).
**Flusso**: una schermata: "La settimana scorsa: 18/22 task completati, 92% abitudini, 340 € spesi (−12% vs media), 6h12m di sonno medio" → ogni riga tocca → drill-down nel modulo.
**Valore**: il valore composto dei dati integrati diventa visibile e abituale (retention loop).

### Mappa casi d'uso → problemi

| Caso d'uso | Problemi risolti (§2.2) |
|---|---|
| UC-01, UC-11 | P3 |
| UC-02, UC-04, UC-12 | P1, P2 |
| UC-03, UC-06 | P2 |
| UC-05 | P4 |
| UC-07 | P5 |
| UC-08, UC-10 | P6 |
| UC-09 | P1, P7 |
