# 11 · Notifiche (NTF)

> Eredita il [MFC](00-modello-funzionale-comune.md). **Le notifiche sono un patto di fiducia** (P42, C-art. 58–63): questo documento è il contratto che lo rende esecutivo.

## 1. Scopo e tracciabilità

| Perché esiste | Principi | JTBD | Decisioni |
|---|---|---|---|
| Portare l'informazione giusta al momento giusto SENZA erodere l'attenzione: il sistema centrale che governa ogni notifica di ogni modulo (nessun modulo notifica per conto proprio) | P42, P58–63 (Constitution Titolo III) | J8 (prompt), J19 (anti-job) | D-04 (anti-metriche) |

## 2. Funzioni

| ID | Nome | Descrizione e motivo | Pri | Dipendenze |
|----|------|----------------------|-----|------------|
| NTF-001 | Broker centrale delle notifiche | Ogni modulo *richiede* una notifica al broker; il broker applica budget, raggruppamento, silenzi e priorità. Nessuna notifica diretta dai moduli | M | — |
| NTF-002 | Budget giornaliero | Default 3 notifiche/giorno (configurabile 0–10); i promemoria espliciti dell'utente (task con ora, abitudini) NON consumano budget — il budget governa solo le notifiche di sistema/insight. *Motivo: ciò che l'utente ha chiesto è suo; ciò che proponiamo noi è contingentato* | M | NTF-001 |
| NTF-003 | Raggruppamento in digest | Le non-urgenti si accorpano (ricorrenze eseguite, soglie budget) in 1 digest a orario scelto | M | — |
| NTF-004 | Orari di silenzio | Notte (default 22–8) + rispetto dei focus di sistema; le notifiche represse durante il silenzio si presentano al risveglio SOLO se ancora rilevanti (un promemoria delle 23 per le 23 è morto alle 8 — si vede in app, non in push) | M | — |
| NTF-005 | Notifiche azionabili | Completa/posticipa/spunta dall'avviso, senza aprire l'app | M | Moduli |
| NTF-006 | Auto-disattivazione proposta | Una categoria ignorata 3 volte consecutive propone la propria disattivazione (C-art. 63) | S | — |
| NTF-007 | Centro notifiche in-app | Pannello con le notifiche recenti e i controlli per categoria (per modulo, granulari) | M | SET |
| NTF-008 | Digest settimanale | Il lunedì (opt-in): la sintesi trasversale della settimana (UC-12) | S | INS |

**Scheda estesa NTF-001/002** — *Requisiti*: le notifiche sono generate **localmente** (i push remoti servono solo da trigger di sync silenziosi — C-art. 7: mai contenuti nei push); ogni notifica dichiara: categoria, priorità (promemoria-utente | utile | informativa), azioni; il broker registra l'esito (mostrata/azionata/ignorata) SOLO localmente per NTF-006. *Casi limite*: 50 richieste in un'ora (rientro da offline lungo) → il broker collassa in 1 digest "mentre eri via"; permesso di sistema revocato → tutte le categorie mostrano lo stato in NTF-007, i contenuti restano in app (P6); due device → le notifiche appaiono su tutti ma l'azione su uno cancella gli altri (best effort alla sync successiva).

## 3–7. Regole, eventi, stati, edge

| ID | Regola | Motivo |
|----|--------|--------|
| NTF-R-01 | Mai notifiche di marketing, riattivazione emotiva ("ci manchi") o upsell | C-art. 60, 64, 168 |
| NTF-R-02 | Mai due notifiche per lo stesso fatto; l'aggiornamento sostituisce (stesso thread di sistema) | Rispetto dell'attenzione |
| NTF-R-03 | Ogni notifica è disattivabile alla granularità a cui è generata, dal suo stesso menu | C-art. 63, 72 |
| NTF-R-04 | Il contenuto sensibile segue MFC-R-22 (mai importi/salute in chiaro senza opt-in) | C-art. 7 |
| NTF-R-05 | Il tono segue il glossario di modulo: mai urgenza artificiale, mai colpa | C-art. 53, 147 |

- Eventi: sottoscrive le richieste dei moduli (`ntf.request` con categoria); pubblica `ntf.action.performed` (il modulo esegue l'azione).
- Stati: permesso negato (l'app vive; il centro in-app mostra tutto), silenzio attivo, budget esaurito (le "utili" slittano al digest).
- Edge: cambio fuso → i promemoria seguono MFC-E-07; DST → MFC-E-08; notifica azionata su entità nel frattempo eliminata → esito gentile "già rimosso", mai errore.

## 8. Criteri di accettazione

- **NTF-AC-01** — *Dato* budget 3 e 5 notifiche "utili" candidate in un giorno, *allora* al massimo 3 vengono mostrate singolarmente e le restanti confluiscono nel digest.
- **NTF-AC-02** — *Dato* un promemoria task alle 15:00, *quando* l'utente tocca "posticipa a stasera" dalla notifica, *allora* il task è ripianificato senza apertura dell'app e la notifica scompare da tutti i device.
- **NTF-AC-03** — *Dato* silenzio 22–8 e una soglia budget superata alle 23, *allora* nessuna notifica fino alle 8, e alle 8 appare solo se lo stato è ancora rilevante.
- **NTF-AC-04** — *Dato* una categoria ignorata 3 volte, *allora* la quarta occorrenza include l'azione "non ricevere più questi avvisi", che disattiva la categoria a 1 tocco.
- **NTF-AC-05** — *Dato* l'analisi dei push remoti ricevuti dal device, *allora* nessun payload contiene contenuti utente.

---

*Prossimo: [Widget](12-widget.md)*
