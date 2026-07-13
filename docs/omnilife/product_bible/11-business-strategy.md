# 11 · Business Strategy

> La strategia di business deriva dalla missione, non viceversa: il modello economico è progettato per rendere *conveniente* mantenere le promesse (utente = cliente, privacy = architettura, gentilezza = retention). Ogni scelta qui sotto è vincolata dalla [Constitution, Titolo VII](15-product-constitution.md).

## 1. Posizionamento

### 1.1 La categoria: crearla, non entrarci

Non ci posizioniamo come "la migliore app di produttività" (categoria affollata, confronto al ribasso). **Creiamo e nominiamo la categoria: il Personal OS** — il sistema operativo personale. Chi definisce la categoria ne scrive i criteri di giudizio: integrazione tra domini, proprietà dei dati, attrito zero — i criteri dove siamo imbattibili per architettura.

### 1.2 Statement di posizionamento

> Per le persone che gestiscono la propria vita su troppe app che non si parlano, **OmniLife è il sistema operativo personale** che unifica attività, denaro, abitudini, tempo e salute in un unico luogo — componibile come vuoi tu, veloce da usare in 3 secondi, e cifrato in modo che i tuoi dati restino matematicamente solo tuoi. A differenza delle super-app confuse e delle app monofunzione in silos, OmniLife collega tutto senza possedere niente di te.

### 1.3 I tre pilastri comunicativi (in ordine)

1. **"Tutta la tua vita, un posto solo"** (il beneficio di massa — P1/P2)
2. **"Tre secondi e hai fatto"** (la prova quotidiana — P3)
3. **"Solo tuo. Matematicamente."** (la ragione per fidarsi — P6)

La privacy è il terzo pilastro, non il primo: guida gli early adopter e la stampa, ma la massa compra il beneficio, non la crittografia (doc 03, R-04 nel registro rischi tecnico).

## 2. Pricing

### 2.1 Architettura dell'offerta (dal design doc, qui ratificata con razionale di business)

| Piano | Prezzo | Contenuto | Funzione strategica |
|---|---|---|---|
| **Free** | 0 € | 2 moduli, 1 device + backup, storico 3 mesi, tutte le fondamenta (sicurezza, export, offline) | Il motore del passaparola; la promessa "gratis vero"; il vivaio dei paganti |
| **Plus mensile** | 7,99 €/mese | Tutto illimitato | Punto d'ingresso psicologico; confrontabile con "un caffè a settimana" |
| **Plus annuale** | 59,99 €/anno (−37%) | Tutto illimitato | Il piano di default: churn strutturalmente più basso, cash-flow anticipato |
| **Lifetime** | 199 € | Tutto, per sempre (quota limitata per coorte) | Cattura gli anti-abbonamento (forte in EU); finanziamento del lancio; segnale di fiducia nel lungo periodo |
| Studenti/regionale | −40% / PPP | | Accessibilità economica (principio 76) e semina LTV |

### 2.2 Le regole del pricing (vincolanti)

1. La leva di conversione è **moduli illimitati + multi-device**: converte per desiderio d'espansione, mai per ricatto (principio 99).
2. **Mai nel paywall**: backup, export, crittografia, ripristino, accessibilità (principio 96 — non si monetizza la paura).
3. Il trial (14 giorni, completo) si attiva **al momento di massimo valore percepito** (attivazione del 3° modulo), non al primo avvio.
4. Aumenti di prezzo: mai retroattivi sugli abbonati esistenti (grandfathering); annunciati con 60 giorni d'anticipo.
5. Un solo sconto ricorrente l'anno (es. lancio/anniversario): la scarsità finta e i countdown permanenti sono vietati (Constitution art. 147).

### 2.3 Perché non altri modelli (decisioni registrate)

- **Pubblicità**: vietata dalla Constitution; economicamente inferiore nel nostro segmento (ARPU ads < 1/10 dell'abbonamento) e distruttiva del posizionamento.
- **Freemium per-feature** (paywall su singole funzioni): frammenta la proposta, genera risentimento, complica la comunicazione. Il nostro freemium è per-capacità (quanti moduli/device), non per-dignità.
- **Solo one-time**: insostenibile con costi ricorrenti (sync, sviluppo perpetuo); il Lifetime esiste come opzione, non come modello.

## 3. Mercati ed espansione geografica

Sequenza (dal doc 04 §5, qui con razionale di business): **Italia (laboratorio) → DACH/Nordics/FR/Benelux (arbitraggio privacy) → UK/Nord America (scala) → Oceania → Asia premium (JP/KR, anno 3+) → emergenti (anno 3–5, prezzi PPP)**.

Regola d'ingresso in un mercato: si entra quando (a) la localizzazione è *culturale* e non solo linguistica, (b) il supporto regge nella lingua, (c) i prezzi sono adattati. Un mercato entrato male è più costoso di un mercato non entrato.

## 4. Le fasi del business (10 anni)

| Fase | Anni | Modello dominante | Obiettivo economico |
|---|---|---|---|
| **1 · Prodotto** | 1–2 | Abbonamento consumer B2C | Product-market fit; 25k–60k paganti; break-even operativo in vista |
| **2 · Consolidamento** | 2–4 | B2C + espansione moduli e mercati | 150k–400k paganti; redditività; brand di categoria |
| **3 · Piattaforma** | 3–6 | + Marketplace (rev-share 80/20) | L'ecosistema come fossato; ricavi piattaforma 10–20% del totale |
| **4 · Estensione** | 5–10 | + Famiglia/coppia; + B2B2C wellbeing | Nuovi grafi (multi-persona), nuovi paganti (aziende), stessa promessa |

### 4.1 Marketplace (fase 3) — le regole del gioco

- Revenue share **80/20 a favore degli sviluppatori** (più generoso degli store: l'ecosistema è il fossato, non la vacca da mungere).
- Ogni plugin rispetta la Constitution tecnica: sandbox, permessi espliciti, zero esfiltrazione, review obbligatoria. **Il marketplace eredita la nostra promessa o non esiste**: un solo plugin-scandalo costerebbe più di tutti i ricavi della piattaforma.
- Categorie attese: moduli verticali di nicchia (giardinaggio, collezioni, malattie croniche, GTD avanzato), template di metodo (zero-based, PARA), integrazioni locali per mercato.

### 4.2 Enterprise / B2B2C (fase 4) — il perimetro invalicabile

Modello: l'azienda paga OmniLife Plus come benefit ai dipendenti. Vincoli non negoziabili (già ratificati per non doverli negoziare sotto pressione commerciale, quando sarà difficile dire no):

- L'azienda **non vede nulla**: né dati, né aggregati individuali, né "engagement del dipendente". Al massimo: numero di licenze attivate.
- Nessuna feature di "produttività sorvegliata". Se il mercato la chiede, il mercato ha sbagliato fornitore.
- Il dipendente che lascia l'azienda porta con sé l'account (convertito a personale): il grafo è della persona, sempre.

### 4.3 Famiglia e coppia (fase 4)

Spazi condivisi **selettivi**: si condivide *un* budget, *un* calendario, *una* lista — mai "tutto l'account". Privacy anche dentro la famiglia (persona E2, doc 06). Pricing: piano famiglia (2 adulti + minori) a ~1,6× il singolo.

## 5. Versioni future del prodotto (opzioni strategiche, non promesse)

| Opzione | Condizione di attivazione | Nota |
|---|---|---|
| Client desktop/web | Domanda dimostrata dai paganti + soluzione E2E senza compromessi | Il web con E2E vero è difficile: meglio tardi che bucato |
| Intelligenza on-device ("chief of staff" personale) | Maturità dei modelli locali (T4, doc 04) | L'evoluzione naturale del grafo; mai contenuti nel cloud |
| Modulo Diario/Journaling | Richiesta organica + sinergia col grafo (umore ↔ resto) | Adiacenza fortissima (Day One invecchia, doc 05) |
| Hardware companion (?) | Non prima dell'anno 7; solo se il personal OS lo giustifica | Registrata come opzione remota, con scetticismo di default |

## 6. Difendibilità economica (perché questo business regge)

1. **Fossato architetturale**: grafo + E2E non retrofittabili dagli incumbent (doc 03, doc 05 §9).
2. **Fossato di fiducia**: anni di promesse mantenute non si comprano con un rebranding; la fiducia è il vero switching cost che costruiamo — nell'unico modo etico: meritandola.
3. **Economia leggera**: backend content-blind = costi infrastrutturali per utente tra i più bassi della categoria; il margine finanzia qualità e prezzi onesti.
4. **Ricavi anti-fragili**: base abbonamenti annuali + lifetime + (poi) piattaforma: nessun singolo cliente, canale o inserzionista può ricattarci. L'indipendenza è una scelta di struttura dei ricavi.

---

*Prossimo: [Growth Strategy](12-growth-strategy.md) — come questo business cresce per dieci anni.*
