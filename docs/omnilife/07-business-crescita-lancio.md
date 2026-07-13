# 07 · Monetizzazione, Crescita e Strategia di Lancio

## 1. Piano di monetizzazione

### 1.1 Principi

1. **Il modello di business deve essere allineato all'interesse dell'utente**: guadagniamo solo se l'utente riceve valore continuativo → abbonamento, mai pubblicità né dati.
2. **Il piano gratuito deve essere genuinamente utile per sempre** (persona Luca/Anna): un free tier zoppo genera recensioni negative e uccide il passaparola, che è il nostro canale principale.
3. **Il paywall non limita mai la sicurezza dei dati**: backup, export e crittografia sono di tutti. Monetizzare la paura di perdere i dati sarebbe il dark pattern definitivo.

### 1.2 Struttura dell'offerta

| | **Free** | **OmniLife Plus** (abbonamento) | **Plus Lifetime** (una tantum) |
|---|---|---|---|
| Moduli attivi contemporaneamente | fino a 2 | illimitati | illimitati |
| Entità, cattura, ricerca, offline | illimitate | illimitate | illimitate |
| Sync multi-device | 1 dispositivo + backup cloud | dispositivi illimitati | idem |
| Storico report e insight | 3 mesi | illimitato + insight trasversali | idem |
| Obiettivi trasversali | 1 attivo | illimitati | idem |
| Widget | base | tutti + interattivi avanzati | idem |
| Revisione settimanale | ✓ | ✓ | ✓ |
| Backup cifrato, export, biometria | ✓ (sempre) | ✓ | ✓ |
| Prezzo indicativo | 0 € | **7,99 €/mese o 59,99 €/anno** (−37%) | 199 € |

Motivazioni:
- **La leva è "moduli illimitati + multi-device"**: è la manifestazione naturale del valore dell'ecosistema — chi vuole più moduli è esattamente chi ha capito il prodotto. La conversione avviene per desiderio, non per ricatto.
- **Annuale scontato come default proposto**: LTV più alto, churn più basso, cash flow anticipato.
- **Lifetime**: cattura la persona "Marco" (avversione agli abbonamenti, forte nei mercati europei); prezzo = ~3 anni di annuale; quota limitata dell'utenza, ottimo per il lancio.
- **Prezzo sotto la somma delle alternative** (Todoist + YNAB + Streaks ≈ 25 €/mese): il value gap è l'argomento di vendita più semplice da comunicare.
- **Prezzi regionali** (purchasing power parity) per i mercati non-premium; **sconto studenti** −40% (persona Luca).
- **Trial**: 14 giorni di Plus completo all'attivazione del 3° modulo (il momento di massima percezione del valore), non al primo avvio.

### 1.3 Obiettivi economici (ipotesi di lavoro, da validare)

- Conversione free→paid a 12 mesi: 4–6% (benchmark categoria produttività premium: 2–8%).
- Churn mensile abbonati: < 3,5% (annuale molto più basso).
- LTV target: > 90 €; CAC blended target: < 15 € (crescita prevalentemente organica).
- Break-even operativo: ~60–80k abbonati (team di 8–12 persone).
- Costi infra contenuti per design: il backend content-blind è economico (blob + metadati; niente elaborazione server dei contenuti).

### 1.4 Fase 3+: revenue share plugin

Con il marketplace dei plugin: 80/20 a favore degli sviluppatori (più generoso degli store: attrattività dell'ecosistema > margine immediato).

## 2. Piano di crescita

### 2.1 Motore di crescita primario: prodotto → passaparola

Il prodotto in questa categoria cresce se: (a) il time-to-value è < 60 s, (b) la retention D30 è alta, (c) il valore è raccontabile in una frase ("un'app sola per tutta la mia vita, e i dati sono solo miei"). Tutto il funnel è progettato su questi tre assi.

**Metrica nord**: utenti con ≥ 2 moduli attivi e ≥ 5 catture/settimana (WAU "integrati") — è il segmento che non abbandona e che parla del prodotto.

| Fase funnel | Leva | Target |
|---|---|---|
| Acquisizione | ASO (§4), contenuti, community, PR privacy | CAC < 15 € |
| Attivazione | Onboarding 60 s; prima cattura nel primo minuto | ≥ 70% completa la prima cattura D0 |
| Retention | Widget, digest, revisione settimanale, costanza gentile | D1 ≥ 45%, D7 ≥ 30%, D30 ≥ 20% |
| Revenue | Trial contestuale al 3° modulo | free→paid ≥ 4% a 12 mesi |
| Referral | Condivisione insight/traguardi (immagine generata, opt-in), sconto bilaterale | K-factor misurato da v1.2 |

### 2.2 Canali (in ordine di investimento)

1. **ASO** — il canale a ROI più alto e duraturo della categoria (§4).
2. **Content marketing utile**: guide reali (budgeting, planning settimanale, abitudini) in it/en; SEO su intenti "migliore app per…"; nessun clickbait.
3. **Community**: presenza autentica dove vive la persona Marco (Reddit r/productivity, r/privacy, HN, Product Hunt); il whitepaper di sicurezza (doc 06 §8) come contenuto-esca tecnico.
4. **PR di posizionamento**: la storia "l'anti-super-app: modulare e cieca sui tuoi dati" per stampa tech europea; angolo GDPR/E2E forte in Germania/Francia/Italia.
5. **Creator di nicchia produttività** (YouTube/TikTok): sponsorship selettive solo dopo product-market fit misurato.
6. **Paid**: solo per scalare ciò che già converte organicamente, mai per comprare la trazione.

### 2.3 Loop di retention (il vero motore)

- **Loop quotidiano**: widget/notifica gentile → cattura o spunta (≤ 3 s) → Home "Oggi" aggiornata → micro-progresso visibile.
- **Loop settimanale**: digest lunedì → revisione settimanale 5 min → settimana pianificata → più dati → insight migliori.
- **Loop di espansione**: valore in un modulo → suggerimento contestuale di un secondo modulo (UC-09) → grafo più ricco → insight trasversali → lock-in positivo (con export sempre libero).

## 3. Strategia di lancio

### 3.1 Sequenza

| Fase | Durata | Obiettivo | Criterio di passaggio |
|---|---|---|---|
| **Alpha chiusa** | 6 sett. | 50–100 utenti interni + amici; stabilità e flussi core | Crash-free ≥ 99,5%; cattura mediana < 3 s |
| **Beta privata** (TestFlight / Play Internal→Closed) | 8–10 sett. | 1.000–3.000 utenti da waiting list; retention e pricing survey | D7 ≥ 25%; NPS ≥ 40; zero perdite dati |
| **Soft launch** | 6 sett. | Rilascio pubblico in Italia (+ eventualmente NL/nordici come proxy EN): funnel reale, ASO, prezzo | D30 ≥ 15%; free→trial ≥ 8%; unit economics plausibili |
| **Lancio globale (EN+IT)** | — | Product Hunt + PR + community coordinati nella stessa settimana | — |

Motivazioni: la waiting list (aperta già in beta con landing page) crea scarsità legittima e primo giorno di lancio caldo; il soft launch in Italia sfrutta il vantaggio linguistico del team e un mercato abbastanza piccolo da poter sbagliare.

### 3.2 Momento del lancio globale

- **Product Hunt** con asset preparati (video 60 s, GIF dei flussi, founder story sulla privacy).
- **Press kit** pronto: whitepaper sicurezza, screenshot, posizionamento anti-super-app.
- **Offerta di lancio**: sconto founder sul piano annuale + quota Lifetime limitata e numerata (urgenza onesta: finita davvero).
- **Canale di feedback pubblico** (roadmap votabile): trasparenza come estensione del brand.

### 3.3 Criteri "non lanciamo se"

Pen-test non superato · RNF-P1/P4 fuori soglia sul device di riferimento · ripristino backup non verificato E2E · accessibilità sotto AA sui flussi core · Data Safety/Privacy Labels non accurate. (Il lancio si sposta; la reputazione non si recupera.)

## 4. ASO — App Store & Google Play

### 4.1 Strategia comune

- **Categoria**: Produttività (primaria); Lifestyle valutata come secondaria su App Store.
- **Cluster keyword**: it: "agenda", "gestione spese", "abitudini", "organizzazione personale", "to do list"; en: "life planner", "habit tracker", "budget planner", "daily planner", "all in one planner". Strategia: long-tail multi-intento — è la nostra natura: intercettiamo le ricerche di ogni modulo, i competitor monofunzione solo la propria.
- **Screenshot narrativi** (non feature-list): 1) "La tua giornata in un colpo d'occhio" 2) "Registra tutto in 3 secondi" 3) "Scegli i tuoi moduli" 4) "I tuoi dati, cifrati, solo tuoi" 5) Dark mode. Primi 2 screenshot = 80% dell'impatto: A/B test continui.
- **Recensioni**: prompt di valutazione solo dopo un momento di valore compiuto (es. 4ª settimana di costanza, obiettivo raggiunto), mai a freddo; risposta a ogni recensione negativa < 48 h.
- **Localizzazione della scheda** in 6+ lingue anche dove l'app è EN (la scheda localizzata converte comunque).

### 4.2 App Store (iOS)

- Titolo (30c): `OmniLife — Agenda e Vita` (it) / `OmniLife: Life Planner` (en); sottotitolo per il cluster secondario; keyword field ottimizzato senza duplicare parole già in titolo/sottotitolo.
- **In-App Events** per feature release; **Custom Product Pages** per canale (pagina "privacy" per il traffico da community, pagina "budget" per il traffico finance).
- Privacy Nutrition Labels come **asso di marketing**: la nostra scheda mostrerà "Data Not Collected" per quasi tutto — rarità assoluta nella categoria.
- Candidatura ai feature editoriali di Apple: la qualità nativa (widget, Dynamic Type, accessibilità) è esattamente ciò che Apple mette in vetrina; prepariamo il pitch editoriale al lancio.

### 4.3 Google Play

- Titolo (30c) + short description (80c) ad alta densità keyword naturale; long description strutturata per crawl.
- **Custom Store Listings** per paese/canale; **LiveOps/Promotional content** per gli eventi di release.
- Grafica funzione-per-funzione nel feature graphic; video breve (≤ 30 s) con i flussi reali.
- **Pre-registrazione** attiva durante la beta per accumulare il primo giorno.
- Data Safety form impeccabile e coerente con le label iOS; vitals (ANR < 0,47%, crash < 1,09%) monitorati come vincolo ASO (il ranking Play penalizza i vitals scarsi).

### 4.4 Governance ASO

Revisione keyword mensile; A/B test (Product Page Optimization / Play Experiments) sempre attivi su un solo elemento alla volta; dashboard con conversion rate per sorgente; l'ASO è un processo permanente, non un'attività di lancio.
