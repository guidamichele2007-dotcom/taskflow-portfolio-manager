# 03 · Problem Space

> Metodo: ogni problema è analizzato in cinque dimensioni — **origine** (perché esiste), **conseguenze** (che cosa produce nella vita delle persone), **dimensione** (quante persone e quanto denaro tocca), **gravità** (quanto fa male, scala 1–5 con motivazione), **soluzione proposta** (come OmniLife lo affronta). I problemi sono ordinati per centralità strategica: P1–P3 sono il cuore; P4–P8 il contorno necessario.
>
> Le stime di dimensione derivano dalla ricerca di mercato (doc 04) e sono ipotesi di lavoro dichiarate, da raffinare con ricerca primaria continua.

---

## P1 · La frammentazione degli strumenti di vita

**Origine.** L'economia delle app ha premiato la specializzazione: fare una cosa sola, farla bene, monetizzarla. Trent'anni di "unbundling" hanno prodotto strumenti eccellenti e incompatibili. Nessun incentivo economico ha mai spinto verso l'integrazione: chi possiede un silo lo difende.

**Conseguenze.** L'utente medio della categoria gestisce 6–12 app di organizzazione personale. Ogni passaggio tra app è uno switch di contesto (costo cognitivo documentato dalla ricerca sull'attention residue); ogni app ha convenzioni, account e notifiche proprie. Il risultato comportamentale osservabile: le informazioni si perdono nei confini tra le app, la manutenzione degli strumenti diventa un lavoro, e una quota crescente di persone regredisce a carta e memoria — non per luddismo, ma per legittima difesa.

**Dimensione.** Ogni possessore di smartphone con vita adulta attiva: nel mondo, oltre 4 miliardi di persone; nel segmento servibile (utenti paganti-in-potenza di strumenti di produttività personale) centinaia di milioni (doc 04 §3). Il costo diretto: 15–40 €/mese di abbonamenti sovrapposti per gli utenti intensivi.

**Gravità: 5/5.** È il problema-radice: tutti gli altri ne derivano o ne sono amplificati.

**Soluzione OmniLife.** Un nucleo unico (identità, cattura, ricerca, design, notifiche) + moduli attivabili: una sola app, un solo linguaggio, un solo abbonamento — con la semplicità di un'app monofunzione, perché ogni utente attiva solo ciò che gli serve.

---

## P2 · I dati non si parlano (il problema dell'integrazione)

**Origine.** Anche quando le app coesistono, i loro dati vivono in schemi proprietari non collegabili. L'integrazione tra domini richiede un modello dati progettato per questo fin dall'inizio — un investimento che nessuno specialista ha ragione di fare e che le piattaforme OS (Salute di Apple, ecc.) fanno solo per domini singoli.

**Conseguenze.** Le decisioni reali della vita sono trasversali, e l'utente le prende alla cieca: "posso permettermi questa vacanza?" coinvolge risparmi, calendario e obiettivi; "perché non riesco a fare sport?" coinvolge agenda, energia e abitudini. Ogni correlazione dev'essere fatta a mano, quindi non viene fatta. Il valore composto dei dati personali — il loro potenziale più alto — resta a zero.

**Dimensione.** Coincide con P1; ma è anche la dimensione della *disponibilità a pagare*: le ricerche sulla categoria mostrano che gli utenti pagano per gli insight, non per i contenitori.

**Gravità: 5/5.** È il problema la cui soluzione è la nostra differenziazione difendibile.

**Soluzione OmniLife.** Il grafo dati personale: ogni entità (task, spesa, abitudine, evento, nota, obiettivo) è collegabile a ogni altra; gli obiettivi trasversali e gli insight nascono dai collegamenti. Nessun competitor lo offre; replicarlo richiede di rifondare l'architettura (doc 05 §conclusioni).

---

## P3 · L'attrito d'inserimento uccide gli strumenti

**Origine.** Le app di gestione personale dipendono dai dati che l'utente inserisce; ma l'inserimento è stato progettato come un modulo da compilare (5–10 tocchi nelle app leader), perché i team ottimizzano le feature visibili, non il gesto invisibile che le alimenta.

**Conseguenze.** Catena documentata: l'inserimento costa → i dati sono incompleti → lo strumento mente ("hai speso 90 €" quando erano 400) → la fiducia crolla → abbandono. L'attrito d'inserimento è la causa di morte silenziosa dell'intera categoria: non si manifesta come lamentela ma come dati vuoti.

**Dimensione.** Universale nella categoria. Nei financial tracker, la registrazione manuale incompleta è il motivo di abbandono più citato nelle recensioni negative (doc 05).

**Gravità: 4/5.** Non è il problema più visibile, ma è quello con l'effetto moltiplicatore più alto: risolverlo alza il valore di ogni modulo.

**Soluzione OmniLife.** Cattura universale ≤ 3 secondi/3 tocchi da ogni superficie (app, widget, voce, notifiche), parser in linguaggio naturale che apprende, ricorrenze automatiche, auto-completamento dai sensori (salute). Il budget di tocchi è un requisito di release, non un auspicio.

---

## P4 · La costanza fragile (il fallimento dei habit tracker)

**Origine.** La gamification di prima generazione ha importato dalle slot machine la meccanica sbagliata: streak, perdita, ansia. Funziona per giorni, ferisce per sempre: la prima streak rotta trasforma lo specchio del progresso in specchio del fallimento.

**Conseguenze.** Abbandono di massa (l'evidenza di categoria: la maggioranza degli utenti abbandona entro 90 giorni), e un danno peggiore: la persona conclude "non sono capace", quando è lo strumento a essere progettato male. Il churn qui non è solo economico; è emotivo.

**Dimensione.** Il solo mercato delle habit app vale miliardi ed è tra i più crescenti del self-improvement (doc 04); l'abbandono precoce ne è il tetto strutturale.

**Gravità: 4/5.** Per il segmento abitudini è il problema n.1; per noi è anche un'opportunità di posizionamento ("l'app che non ti giudica").

**Soluzione OmniLife.** Costanza resiliente (metriche che tollerano i salti), frequenze flessibili, ridimensionamento gentile proposto dopo le difficoltà, fresh start effect al posto della colpevolizzazione. La gentilezza come meccanica di retention, con vincolo etico esplicito (doc 15, Titolo III).

---

## P5 · La dipendenza dalla rete

**Origine.** Il decennio del cloud-first ha reso economicamente conveniente costruire app che sono terminali di un server. L'offline è stato retrocesso a "modalità degradata" perché costa: sync, conflitti, testing.

**Conseguenze.** Le app falliscono nei momenti in cui la vita accade: metropolitana, aereo, roaming, zone bianche, o semplicemente il server del fornitore che ha un cattivo giorno. Ogni fallimento insegna all'utente a non fidarsi — e uno strumento di vita di cui non ti fidi è già morto.

**Dimensione.** Trasversale; particolarmente acuta per pendolari e viaggiatori (una quota dominante del nostro target) e nei mercati con connettività discontinua — che sono anche i mercati di crescita futura (doc 04 §5).

**Gravità: 3/5.** Non è quotidiano per tutti, ma ogni singolo episodio ha un effetto sproporzionato sulla fiducia.

**Soluzione OmniLife.** Offline-first architetturale: il dispositivo è la fonte primaria, tutto funziona senza rete, la sincronizzazione riconcilia da sola senza mai chiedere all'utente di scegliere una versione.

---

## P6 · La sorveglianza come modello di business

**Origine.** Quando il prodotto è gratuito e i costi sono reali, i dati diventano il ricavo. La categoria della vita personale è la più esposta: denaro, salute, pensieri e posizione sono i dati più preziosi del mercato pubblicitario.

**Conseguenze.** Due danni distinti: (a) il danno diretto di profilazione e rivendita; (b) il danno comportamentale dell'**autocensura** — l'utente non registra ciò che è davvero sensibile ("meglio non scriverlo lì"), e proprio i dati più importanti restano fuori dallo strumento, azzoppandolo. La fiducia mancata è un tetto funzionale, non solo etico.

**Dimensione.** La sensibilità è in crescita strutturale (GDPR, ATT di Apple, la generazione post-Cambridge Analytica); in Europa è già mainstream: la privacy è passata da preferenza di nicchia a criterio d'acquisto (doc 04 §4).

**Gravità: 4/5.** Per il segmento privacy-conscious è eliminatorio; per il mass market è un differenziatore di fiducia crescente.

**Soluzione OmniLife.** Crittografia end-to-end architettuale (non possiamo leggere i dati), zero advertising, zero data broker, telemetria opt-in anonima, promessa pubblica verificabile (doc 01 §6). Il modello di business — l'abbonamento — è l'unica fonte di ricavo e allinea i nostri incentivi ai suoi.

---

## P7 · Il costo cumulativo degli abbonamenti

**Origine.** Lo shift al SaaS ha moltiplicato gli abbonamenti da 3–15 € l'uno; la "subscription fatigue" è la reazione documentata degli ultimi anni.

**Conseguenze.** Gli utenti rinunciano a strumenti utili per saturazione economica e psicologica ("non un altro abbonamento"); oppure accumulano abbonamenti dimenticati, alimentando risentimento verso l'intera categoria.

**Dimensione.** L'utente intensivo di produttività personale spende 15–40 €/mese in strumenti sovrapposti; la resistenza al "nuovo abbonamento" è oggi uno dei primi ostacoli di conversione della categoria.

**Gravità: 3/5.** Non è un dolore acuto, ma sposta le decisioni d'acquisto.

**Soluzione OmniLife.** Un solo abbonamento sotto il prezzo della somma (7,99 €/mese contro ~25 € di stack equivalente), opzione Lifetime per gli avversi alla ricorrenza, free tier genuino. Il consolidamento non è solo UX: è un argomento economico immediato.

---

## P8 · Il sovraccarico cognitivo degli strumenti "potenti"

**Origine.** I prodotti flessibili (workspace componibili, sistemi di produttività elaborati) hanno spostato il costo di progettazione sull'utente: prima di usare lo strumento devi costruirlo, e per costruirlo devi diventare esperto di produttività — un hobby a sé, con i suoi guru e i suoi template da 40 blocchi.

**Conseguenze.** Due popolazioni di vittime: chi abbandona subito (soverchiato), e chi resta intrappolato nel meta-lavoro (ottimizza il sistema invece di vivere). In entrambi i casi lo strumento ha fallito.

**Dimensione.** È il motivo di abbandono più citato per i workspace flessibili (doc 05, recensioni Notion mobile e simili); è anche la barriera che tiene la maggioranza non-tecnica fuori dalla categoria.

**Gravità: 3/5.** Selettivo ma decisivo per il mass market: la persona "Anna" (doc 06) non adotterà mai uno strumento che chiede progettazione.

**Soluzione OmniLife.** Prodotto finito, non kit di montaggio: default eccellenti, zero configurazione richiesta, personalizzazione possibile ma mai necessaria, rivelazione progressiva della profondità. La potenza è nostra responsabilità di design, non un compito a casa per l'utente.

---

## Mappa di sintesi

| # | Problema | Gravità | Chi lo sente di più | Risposta OmniLife | Difendibilità della risposta |
|---|----------|---------|---------------------|-------------------|------------------------------|
| P1 | Frammentazione | 5 | Tutti | Nucleo + moduli | Media (replicabile male: super-app) |
| P2 | Dati scollegati | 5 | Utenti multi-strumento | Grafo personale | **Alta** (richiede rifondazione architetturale) |
| P3 | Attrito d'inserimento | 4 | Tutti | Cattura ≤ 3 s | Media (richiede ossessione, non tecnologia) |
| P4 | Costanza fragile | 4 | Segmento abitudini | Gentilezza meccanica | Media-alta (contraria agli incentivi altrui) |
| P5 | Dipendenza dalla rete | 3 | Pendolari, viaggiatori | Offline-first | Alta (non retrofittabile a basso costo) |
| P6 | Sorveglianza | 4 | Privacy-conscious → mass | E2E architetturale | **Alta** (incompatibile con business dei dati) |
| P7 | Costo cumulativo | 3 | Budget-sensitive | Un solo abbonamento | Bassa (chiunque può abbassare i prezzi) |
| P8 | Sovraccarico cognitivo | 3 | Mass market non-tech | Prodotto finito | Media (è cultura di prodotto, non feature) |

**Lettura strategica.** La difendibilità si concentra dove l'architettura incontra gli incentivi: P2 (grafo) e P6 (E2E) sono i fossati; P1 e P3 sono il campo di battaglia quotidiano; P4, P5, P7, P8 sono i moltiplicatori di fiducia. La strategia di prodotto (doc 09) e di business (doc 11) derivano da questa mappa.

---

*Prossimo: [Market Research](04-market-research.md) — quanto vale questo spazio e dove sta andando.*
