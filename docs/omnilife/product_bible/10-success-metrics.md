# 10 · Success Metrics

> Principio di misura: **misuriamo ciò che vogliamo diventare, perché diventeremo ciò che misuriamo.** Per questo la North Star non è il tempo in app né le sessioni: sono metriche che ci trasformerebbero in ciò che il Manifesto condanna. Ogni metrica ha: definizione esatta, target per fase, e — dove serve — un **contro-metrica di guardia** che impedisce di ottimizzarla nel modo sbagliato.
>
> Vincolo di coerenza: tutte le misurazioni rispettano la telemetria privacy-safe (opt-in, anonima, mai contenuti). Accettiamo consapevolmente dati meno completi in cambio della coerenza con la Constitution: i target tengono conto del campionamento.

## 1. North Star Metric

> **WAI — Weekly Active Integrators**: utenti che in una settimana (a) usano ≥ 2 moduli attivi e (b) completano ≥ 5 azioni di valore (cattura, spunta, registrazione, pianificazione).

**Perché questa.** Cattura simultaneamente: valore ricevuto (azioni reali, non aperture), tesi del prodotto (l'integrazione multi-modulo è la differenziazione), e retention futura (i dati di categoria mostrano che l'uso multi-dominio è il predittore n.1 di permanenza e conversione). Una metrica che cresce solo se l'utente vive meglio la nostra promessa.

**Contro-metrica di guardia**: tempo mediano per sessione — deve restare **basso** (target < 90 s per le sessioni quotidiane). Se WAI cresce ma il tempo per sessione esplode, stiamo diventando un time-sink: allarme.

Gerarchia: `WAI ← attivazione × retention × espansione moduli` — ogni team ottimizza un fattore, la North Star li integra.

## 2. Le metriche del funnel

### 2.1 Acquisizione

| Metrica | Definizione | Target Anno 1 | Target Anno 3 |
|---|---|---|---|
| Download → install rate (store CVR) | Visite scheda → installazioni | ≥ 30% | ≥ 35% |
| Costo di acquisizione (CAC blended) | Spesa marketing totale / nuovi utenti registrati | < 5 € (organico dominante) | < 10 € |
| Quota organica | % installazioni non-paid | ≥ 80% | ≥ 70% |

### 2.2 Attivazione e Time To Value

| Metrica | Definizione | Target |
|---|---|---|
| **TTV (Time To Value)** | Tempo dal primo avvio alla prima azione di valore completata (prima cattura/spunta) | **mediana < 60 s** |
| Attivazione D0 | % nuovi utenti che completano ≥ 1 azione di valore il giorno 0 | ≥ 70% |
| Attivazione piena (aha moment) | % che entro 7 giorni ha: 1 modulo con ≥ 5 entità + 1 widget o promemoria attivo | ≥ 40% |
| Onboarding completion | % che completa l'onboarding (≤ 60 s di percorso) | ≥ 85% |

L'"aha moment" (la definizione operativa di quando un utente "ha capito") va **validato empiricamente in beta**: la definizione sopra è l'ipotesi di partenza, la correlazione con la retention D30 dirà se è quella giusta.

### 2.3 Retention (la metrica regina della categoria)

| Metrica | Definizione | Beta | Anno 1 | Anno 3 (best-in-class) |
|---|---|---|---|---|
| D1 | % attivi il giorno dopo l'install | ≥ 40% | ≥ 45% | ≥ 50% |
| D7 | | ≥ 25% | ≥ 30% | ≥ 35% |
| D30 | | ≥ 15% | ≥ 20% | ≥ 25% |
| D90 | | — | ≥ 12% | ≥ 18% |
| Retention curve shape | La curva deve **appiattirsi** (plateau = product-market fit); una curva che scende senza plateau è il segnale di allarme n.1 | plateau visibile | plateau ≥ 10% | plateau ≥ 15% |
| Resurrezione | % utenti dormienti (28+ gg) che tornano entro il trimestre | — | ≥ 8% | ≥ 12% |

Contro-metrica: la retention **non** si compra con le notifiche — il budget notifiche (median push/utente/giorno ≤ 2) è vincolante; se la retention regge solo alzando le notifiche, è retention falsa.

### 2.4 Engagement (misurato come valore, non come tempo)

| Metrica | Definizione | Target Anno 1 |
|---|---|---|
| DAU / WAU / MAU | Attivi = ≥ 1 azione di valore (non la sola apertura) | DAU/MAU ≥ 35% (uso quasi quotidiano) |
| **WAI (North Star)** | §1 | ≥ 25% dei WAU al mese 6; ≥ 35% al mese 18 |
| Azioni di valore / utente attivo / giorno | catture + spunte + registrazioni + pianificazioni | ≥ 4 |
| Espansione moduli | % utenti che attivano il 2° modulo entro 30 gg; il 3° entro 90 gg | ≥ 35%; ≥ 20% |
| Tempo per sessione (guardia) | mediana sessioni quotidiane | **< 90 s** (deve restare basso) |

### 2.5 Monetizzazione

| Metrica | Definizione | Target Anno 1 | Target Anno 3 |
|---|---|---|---|
| Trial start rate | % eleggibili che avviano il trial (al 3° modulo) | ≥ 15% | ≥ 20% |
| Trial → paid | % trial che convertono | ≥ 30% | ≥ 40% |
| **Free → paid (cumulativa a 12 mesi)** | % registrati che diventano paganti entro 12 mesi | ≥ 3% | ≥ 5–6% |
| ARPU pagante | Ricavo medio annuo per abbonato (mix mensile/annuale/lifetime) | ≥ 55 € | ≥ 70 € |
| **Churn abbonati** | Cancellazioni nette mensili (mensili+annuali normalizzati) | < 4%/mese | < 3%/mese |
| **LTV** | ARPU × margine × durata media | ≥ 90 € | ≥ 150 € |
| **LTV : CAC** | | ≥ 3:1 | ≥ 4:1 |
| Payback CAC | Mesi per ripagare l'acquisizione | < 12 | < 6 |
| Refund/dispute rate | Guardia di onestà del paywall | < 1% | < 0,5% |

### 2.6 Qualità e fiducia (le metriche che proteggono tutte le altre)

| Metrica | Target | Perché è qui |
|---|---|---|
| Crash-free sessions | ≥ 99,8% | La fiducia muore a ogni crash |
| Incidenti di perdita dati | **0, sempre** | Principio 1: non è una metrica, è un giuramento |
| Store rating | ≥ 4,6 sostenuto | Il compound del passaparola e dell'ASO |
| NPS | ≥ 50 (beta ≥ 40) | Il predittore del referral organico |
| Ticket di supporto / 1000 MAU | < 5 | La misura della chiarezza del prodotto |
| Tempo di risposta supporto | < 24 h mediana | La promessa verso gli utenti |
| Accessibilità | 100% flussi core AA a ogni release | Constitution, Titolo IV |
| Sync convergence failures | 0 casi con intervento utente | La promessa offline-first |

## 3. Metriche per fase di vita (che cosa guardiamo quando)

| Fase | Le 3 metriche che comandano | Perché |
|---|---|---|
| Alpha/Beta | Crash-free · TTV · D7 | Prima stabilità e valore immediato; tutto il resto è rumore |
| Soft launch | D30 + forma della curva · attivazione piena · store CVR | Product-market fit prima della scala |
| Lancio → Anno 1 | **WAI** · free→trial→paid · CAC organico | La macchina completa |
| Anno 2–3 | WAI · LTV:CAC · espansione moduli · churn | Economia sostenibile |
| Anno 3+ | WAI · quota "integratori profondi" (3+ moduli) · metriche marketplace | La piattaforma |

## 4. Anti-metriche (dichiarate per iscritto)

Metriche che **non useremo mai come obiettivo**, perché il loro massimo coincide con il tradimento della missione:

- ❌ Tempo totale in app (lo vogliamo *basso* a parità di valore)
- ❌ Sessioni per giorno oltre il necessario (l'ossessione da apertura è dipendenza)
- ❌ Notifiche inviate / tassi di apertura push come obiettivo di crescita
- ❌ DAU gonfiato da streak-anxiety o loop compulsivi
- ❌ Numero di feature rilasciate per trimestre (output ≠ outcome)
- ❌ Dati raccolti per utente

Chi propone un OKR basato su un'anti-metrica sta proponendo un emendamento alla Constitution, e va trattato come tale.

## 5. Governance della misura

- **Una sola fonte di verità** per le definizioni (questo documento); ogni dashboard le cita per nome.
- **Revisione mensile** del quadro completo; **revisione strategica semestrale** dei target.
- **Regola dell'esperimento**: ogni A/B test dichiara prima metrica primaria, guardie e durata; niente peeking, niente "vince perché ci piace".
- **Regola della guardia**: nessun risultato è "verde" se una contro-metrica di guardia è rossa.

---

*Prossimo: [Business Strategy](11-business-strategy.md) — il modello economico che queste metriche alimentano.*
