# 04 · Market Research

> **Nota metodologica e di onestà intellettuale.** Le cifre di questo documento sono stime di lavoro costruite per triangolazione da fonti pubbliche di settore (report di categoria, dati degli store, bilanci pubblici dei player quotati o dichiarazioni di funding) alla data di redazione. Servono per il dimensionamento strategico e per le decisioni d'investimento interne, non come dati contabili. Ogni cifra critica va rivalidata con ricerca primaria prima di decisioni irreversibili. Le assunzioni sono dichiarate in linea.

## 1. Definizione del mercato

OmniLife non compete in una singola categoria: **consolida cinque categorie adiacenti** in una nuova — il "personal OS". Il mercato rilevante è quindi la somma (deduplicata) di:

1. **Productivity apps / task management** (to-do, planner)
2. **Personal finance apps** (budgeting, expense tracking)
3. **Habit tracking & self-improvement**
4. **Note-taking personale**
5. **Wellness/health companion** (la parte "gestionale", non fitness content)

## 2. Dimensioni e crescita (mondo)

| Categoria | Valore stimato 2026 | CAGR atteso 2026–2031 | Note sulle assunzioni |
|---|---|---|---|
| Productivity apps (consumer) | ~12–15 Mld $ | 8–10% | Categoria matura; crescita trainata da subscription e mobile |
| Personal finance apps | ~2–4 Mld $ consumer (escludendo banking/fintech transazionale) | 12–15% | La quota "budgeting standalone" è piccola ma ad alta willingness-to-pay |
| Habit & self-improvement | ~1,5–3 Mld $ | 15–20% | Il segmento a crescita più rapida; churn strutturale alto = tetto attuale, non domanda mancante |
| Note-taking personale | ~2–3 Mld $ | 10–12% | Dominata da free tier; monetizza su power user |
| Wellness companion (gestione) | ~3–5 Mld $ (quota rilevante del wellness digitale) | 12–16% | Con l'avvertenza che il grosso del wellness è contenuto (meditazione, fitness), fuori dal nostro scope |
| **Somma indicativa (TAM di categoria)** | **~20–30 Mld $/anno** | **~11–14%** | Sovrapposizioni deducibili; ordine di grandezza robusto |

**Lettura.** Il TAM è largo, in crescita a doppia cifra, e — punto strategico — **nessun player ne possiede più del 2–3%**: è un mercato grande e polverizzato, la configurazione ideale per una consolidazione guidata dal prodotto.

## 3. TAM → SAM → SOM

| Livello | Definizione | Stima | Assunzioni |
|---|---|---|---|
| **TAM** | Spesa mondiale annua nelle 5 categorie | ~20–30 Mld $ | §2 |
| **SAM** | Utenti raggiungibili dalla nostra proposta nei mercati di lancio (Europa + Nord America + Oceania), disposti a pagare per strumenti personali | ~250–350 M persone; valore monetizzabile ~8–12 Mld $/anno | Smartphone user adulti nei mercati target × tasso di adozione categoria (25–35%) |
| **SOM (5 anni)** | Quota realisticamente conquistabile | 5–10 M utenti registrati; 300–600k paganti; **25–60 M €/anno ARR** | Conversione 4–6%; ARPU pagante ~80 €/anno; execution della roadmap (doc 13) |

## 4. Trend strutturali (i venti che soffiano)

| Trend | Evidenza | Implicazione per OmniLife |
|---|---|---|
| **T1 · Consolidamento / subscription fatigue** | La resistenza al "nuovo abbonamento" cresce; i bundle (Microsoft 365, ecosistemi Apple/Google) vincono | Il nostro argomento economico centrale ("uno al posto di quattro") cavalca il trend |
| **T2 · Privacy come criterio d'acquisto** | GDPR, ATT, crescita dei prodotti privacy-first (mail, browser, messaging) da nicchia a mainstream in Europa | Il pilastro E2E passa da costo a vantaggio competitivo; l'Europa come mercato di lancio è una scelta strategica, non geografica |
| **T3 · Benessere digitale e anti-engagement** | Backlash culturale contro l'economia dell'attenzione; "screen time" come preoccupazione mainstream | La filosofia calm-tech (doc 01 §4.1) è controcorrente rispetto alle big ma allineata alla domanda emergente |
| **T4 · AI on-device** | NPU nei telefoni di fascia media; modelli locali sempre più capaci | L'intelligenza privacy-compatibile (parsing, insight) diventa possibile senza cloud: il nostro vincolo E2E smette di essere un limite funzionale |
| **T5 · Quantified self maturo** | Wearable ovunque; i dati ci sono, l'azione manca | Il nostro ruolo: trasformare dati passivi in decisioni (grafo, insight) |
| **T6 · Creator economy della produttività** | Milioni di follower per i creator di "sistemi di vita" (PKM, budgeting, habit) | Canale di crescita nativo (doc 12); e domanda dimostrata di "sistemi" — che noi diamo già montati |
| **T7 · Maturazione dei mercati emergenti** | Sud-est asiatico, America Latina, India: smartphone-first, offline-friendly necessario | Il nostro offline-first e i prezzi regionali ci rendono adatti alla seconda ondata geografica (anno 3+) |

## 5. Segmentazione geografica

| Mercato | Attrattività | Quando | Perché |
|---|---|---|---|
| **Italia** | Media (mercato piccolo) ma nostra | Lancio (soft) | Vantaggio linguistico/culturale del team; mercato-laboratorio |
| **DACH + Nordics + Benelux + FR** | **Alta** | Anno 1 | Massima sensibilità privacy d'Europa; alto ARPU; willingness-to-pay dimostrata |
| **UK + Nord America** | Alta | Anno 1–2 | Il mercato più grande e competitivo; si entra con posizionamento già affilato |
| **Oceania** | Media-alta | Anno 2 | ARPU alto, competizione media, costo d'ingresso basso (EN) |
| **Giappone/Corea** | Alta ma difficile | Anno 3+ | Willingness-to-pay alta; richiede localizzazione profonda, non traduzione |
| **America Latina, Sud-est asiatico, India** | Volume | Anno 3–5 | Prezzi regionali; offline-first come vantaggio reale; LTV più basso ma massa critica per l'ecosistema |

## 6. Dinamiche competitive del mercato (sintesi; dettaglio nel doc 05)

- **Polverizzazione**: migliaia di app, nessun "sistema operativo personale" affermato. La categoria che vogliamo creare **non ha ancora un re** — l'analogo di dove era il project management pre-Notion/Linear.
- **Gli incumbent hanno vincoli strutturali**: le big tech monetizzano l'attenzione o l'hardware (l'integrazione profonda cross-dominio con E2E non è nei loro incentivi); gli specialisti hanno architetture monodominio e basi di codice decennali.
- **La finestra è aperta ma non per sempre**: T4 (AI on-device) abbasserà le barriere di costruzione per tutti. Stima interna: 24–36 mesi per stabilire il posizionamento prima che la categoria si affolli. La velocità di esecuzione è parte della strategia.

## 7. Mercati futuri (opzionalità a 5–10 anni)

Queste opzioni sono registrate perché il loro valore dipende da scelte architetturali odierne (modularità, grafo, piattaforma plugin):

1. **Marketplace di moduli di terze parti** — da prodotto a piattaforma: ricavi da revenue share, difendibilità da ecosistema (doc 11 §6).
2. **Famiglia e coppia** — budget condivisi, calendari familiari, cura di familiari anziani: espansione naturale del grafo a più persone, con lo stesso vincolo privacy.
3. **B2B2C / employee wellbeing** — OmniLife offerto come benefit aziendale (l'azienda paga, l'individuo possiede i dati — coerente con la Constitution; l'azienda non vede nulla).
4. **Educazione** — pacchetto studenti (metodo di studio, budget, abitudini): pipeline demografica di lungo periodo.
5. **Intelligenza personale on-device** — quando T4 matura: il "chief of staff" personale che ragiona sul grafo *senza che nulla lasci il dispositivo*. È l'evoluzione finale della visione, possibile solo per chi ha il grafo e la fiducia.

## 8. Implicazioni strategiche (che cosa questa ricerca decide)

1. **Lancio in Europa** con la privacy come lancia e il consolidamento economico come argomento di massa (T1+T2).
2. **La categoria va nominata e posseduta**: "personal OS" — investimento di posizionamento dal giorno 1 (doc 11 §1).
3. **Velocità**: finestra 24–36 mesi → la roadmap (doc 13) privilegia il tempo-al-posizionamento sulla completezza.
4. **L'architettura di oggi compra le opzioni di domani**: modularità e grafo non sono solo scelte tecniche, sono il biglietto per marketplace, famiglia e AI personale (§7).

---

*Prossimo: [Competitor Bible](05-competitor-bible.md) — chi c'è già in questo mercato, e perché nessuno fa ciò che facciamo noi.*
