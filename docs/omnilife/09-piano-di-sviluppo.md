# 09 · Piano di Sviluppo per Fasi

> Principi del piano: (1) **il rischio più alto si affronta per primo** (spike CRDT/E2E, R-10); (2) ogni fase ha **criteri di uscita misurabili** — non si passa alla successiva sperando; (3) la qualità non è una fase: budget di prestazioni, accessibilità e sicurezza sono gate continui in CI dal giorno 1; (4) le date sono espresse in settimane relative (T0 = inizio sviluppo) perché la durata assoluta dipende dalla dimensione del team (ipotesi: 8–10 persone: 2 iOS, 2 Android, 2 core/KMP, 1 backend, 1 designer, 1 PM/founder, QA distribuita + specialista part-time).

## Roadmap d'insieme

```
Fase 0          Fase 1                    Fase 2            Fase 3
Fondamenta      MVP (core+3 moduli)       Espansione        Ecosistema
T0 ─── T8       T8 ──────────── T30       T30 ─── T52       T52 →
   │               │        │      │         │       │
   Spike ok     Alpha    Beta   Soft      Lancio   v1.x    Plugin SDK,
   Design       (T24)   (T26)  launch    globale  releases marketplace,
   system                       (T30)     (T36)            nuove piattaforme
```

---

## Fase 0 — Fondamenta e de-risking (T0 → T8)

**Obiettivo**: eliminare i rischi capitali prima di costruirci sopra.

| Traccia | Attività |
|---|---|
| **Spike tecnico (critico)** | Prototipo del motore sync CRDT + E2E in KMP: due device, modifiche concorrenti offline, convergenza verificata con test generativi; misura delle prestazioni su device di riferimento. **Go/No-Go a T6**: se no-go → fallback LWW per-campo (R-10) |
| Design | Design system "Omni" v1: token (light+dark), tipografia, componenti core; prototipo navigabile dei flussi UC-01/02/05; primo test di usabilità (5 utenti) sul prototipo |
| Architettura | Module Contract v1 (manifest, event bus, regole di build anti-dipendenze); schema del Personal Data Graph; OpenAPI v1 degli endpoint backend |
| Backend/DevOps | Scheletro servizi (Auth, Sync), IaC, CI/CD con: build, test, benchmark prestazioni, lint accessibilità sui token, SCA/SAST. La pipeline nasce prima del prodotto |
| Sicurezza | Design review crittografica esterna della gerarchia chiavi; threat model v1 |

**Criteri di uscita**: spike convergenza 100% nei test generativi; prototipo usabilità: task success ≥ 80% su cattura e Home; pipeline CI verde con tutti i gate attivi; design crypto approvato dal reviewer esterno.

---

## Fase 1 — MVP (T8 → T30)

**Obiettivo**: core + Attività, Finanze, Abitudini + agenda in lettura, al livello di qualità "top store", fino al soft launch.

### Sequenza interna (release train interno di 2 settimane)

| Finestra | Contenuto (ID doc 02) |
|---|---|
| T8–T14 | App shell + navigazione; store locale cifrato; **Cattura rapida v1** (C-02, senza NLP avanzato); modulo Attività completo (T-01…T-04) |
| T14–T20 | Sync E2E integrata (X-02); backup/ripristino (X-03); modulo Abitudini (H-01…H-03, H-06); Home "Oggi" v1 (C-01); widget base (X-04) |
| T20–T24 | Modulo Finanze (F-01, F-02, F-05); parser NLP it/en (T-05, RF-02); ricerca (C-03); Galleria moduli (C-04); onboarding (C-05); agenda in lettura (CA-02) |
| T24–T26 | **Alpha chiusa** (50–100 utenti): stabilizzazione, prestazioni, accessibilità audit completo, report F-06, export (X-06), impostazioni/sicurezza (C-06, biometria) |
| T26–T30 | **Beta privata** (1.000–3.000 da waiting list): telemetria opt-in, pricing survey, billing (StoreKit2/Play Billing), pen-test esterno, correzioni; preparazione schede store e ASO |

**QA in fase 1**: piramide test (unit sul core ≥ 90%, integrazione moduli, E2E sui 7 flussi del doc 04 §4 automatizzati su device farm); test manuale esplorativo settimanale; beta con canale feedback in-app.

**Criteri di uscita (= criteri di soft launch, doc 07 §3.1)**: crash-free ≥ 99,8%; RNF-P1…P10 verdi sul device di riferimento; ripristino backup E2E testato; WCAG AA sui flussi core; D7 beta ≥ 25%; NPS ≥ 40; pen-test superato; zero incidenti di perdita dati in beta.

---

## Fase 2 — Lancio ed espansione del valore (T30 → T52)

**Obiettivo**: soft launch → lancio globale → consolidare retention e monetizzazione con i moduli "Should".

| Finestra | Contenuto |
|---|---|
| T30–T36 | **Soft launch Italia**: iterazione su funnel reale (onboarding, paywall, ASO); revisione settimanale (C-07); digest e budget notifiche (X-07); fix da campo |
| T36 | **Lancio globale** (Product Hunt + PR, doc 07 §3.2) |
| T36–T44 | Obiettivi trasversali (G-01, G-02) — la funzione-firma; Note (N-01, N-02); Finanze estese (F-03, F-04, F-06 completo); posticipa intelligente (T-06) |
| T44–T52 | Salute in lettura + auto-completamento abitudini (S-01, S-03); Insight engine v1 (X-05) e digest insight (UC-12); time-boxing (CA-03); riduzione gentile (H-04); import da competitor |

**Cadenza release pubblica**: ogni 3 settimane, con feature flag e rollout graduale (5→25→100%); hotfix < 48 h.

**Criteri di uscita**: D30 ≥ 20%; utenti "integrati" (≥ 2 moduli) ≥ 35% dei WAU; free→paid ≥ 4% annualizzato; churn abbonati < 3,5%/mese; vitals store nelle soglie (doc 07 §4.3).

---

## Fase 3 — Ecosistema (T52 →)

**Obiettivo**: da prodotto a piattaforma, solo su fondamenta economiche dimostrate.

- **Plugin SDK + marketplace curato** (X-08): apertura del Module Contract a terzi con sandbox, review, kill-switch (vincolata alla prontezza del sistema di sicurezza, R-24); revenue share 80/20.
- **Superfici aggiuntive**: watch app (spunta e cattura), iPad/tablet con layout adattivi, eventuale client web/desktop (decisione dedicata: la sync E2E lo rende oneroso — si fa solo se i dati di domanda lo giustificano).
- **Moduli v2**: multi-valuta (F-07), metriche manuali salute (S-02), milestone obiettivi (G-03), condivisione selettiva (budget di coppia).
- **Intelligenza on-device evoluta**: insight correlazionali più ricchi; rivalutazione di modelli locali (privacy-compatibili) per suggerimenti — mai contenuti verso il cloud.
- **Internazionalizzazione**: +4–6 lingue, prezzi regionali completi, ASO per mercato.

**Criteri di successo**: ≥ 10 plugin di qualità nel primo anno di marketplace; ricavi da nuove superfici > costo; NPS stabile ≥ 45.

---

## Governance del piano

- **Review di fase**: a ogni confine di fase, revisione formale contro i criteri di uscita; chi propone di procedere senza criteri verdi deve motivare per iscritto (di norma: non si procede).
- **Registro rischi** (doc 08) rivisto mensilmente in fase 0–1, trimestralmente dopo.
- **Il documento vive**: ogni scostamento significativo dalla roadmap aggiorna questo piano con motivazione, così la storia delle decisioni resta leggibile.
- **Definizione di "fatto"** per ogni feature: doc 02 §4 (prestazioni + accessibilità + offline + telemetria + metrica di successo).

## Cosa succede dopo questa documentazione

Il primo artefatto della fase 0 non è codice di prodotto: è lo **spike del motore di sync** e il **prototipo navigabile**. Solo il loro esito valida (o corregge) le decisioni di questi documenti. La documentazione è la mappa; lo spike è il primo passo sul terreno.
