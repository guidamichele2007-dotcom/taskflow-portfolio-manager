# 05 · Modulo Abitudini (HAB)

> Eredita il [MFC](00-modello-funzionale-comune.md). Il modulo dove la filosofia della gentilezza (D-06) diventa meccanica funzionale precisa.

## 1. Scopo e tracciabilità

| Perché esiste | Principi | JTBD | Decisioni |
|---|---|---|---|
| Sostenere la costanza sui comportamenti scelti dall'utente, con meccaniche che perdonano l'inciampo invece di punirlo — la causa n.1 di abbandono della categoria trasformata in vantaggio | P39–P41, P46, P51 | **J8, J9, J10**, J16 | **D-06 (costanza resiliente)**, D-03 |

## 2. Funzioni

| ID | Nome | Descrizione e motivo | Pri | Dipendenze |
|----|------|----------------------|-----|------------|
| HAB-001 | Abitudine binaria | "Fatto/non fatto" (meditare, leggere) | M | — |
| HAB-002 | Abitudine quantitativa | Target numerico con unità (2 L d'acqua, 30 min, 10k passi); progresso incrementale nella giornata | M | — |
| HAB-003 | Frequenza flessibile | Ogni giorno · N volte a settimana · giorni specifici · N volte al mese. *Motivo: J10; il quotidiano rigido è la trappola (D-06)* | M | — |
| HAB-004 | Spunta rapida ovunque | 1 tocco da Home, widget, notifica; quantitative: incremento a step configurato | M | WID, NTF |
| HAB-005 | **Costanza resiliente** | Metrica di aderenza su finestra mobile (28 gg) rispetto alla frequenza scelta; **nessun contatore che si azzera**. Un giorno saltato riduce gradualmente, mai a zero | M | — |
| HAB-006 | Promemoria contestuali | Orario scelto; opzionale per-giorno; raggruppati nel budget notifiche | M | NTF |
| HAB-007 | Ridimensionamento gentile | Dopo 2 settimane sotto il 50% di aderenza, l'app propone (mai impone): ridurre la frequenza, cambiare orario, o mettere in pausa. *Motivo: J10; C-art. 74* | S | HAB-005 |
| HAB-008 | Pausa dichiarata | Sospensione (vacanza, malattia) che congela la metrica senza penalità; ripresa a 1 tocco. *Motivo: la vita reale ha stagioni* | M | — |
| HAB-009 | Griglia storica | Vista mensile delle esecuzioni (pattern visivo alla "Everyday", validato — doc 05 PB); descrizione testuale accessibile equivalente | S | — |
| HAB-010 | Auto-completamento da Salute | Il workout/passi rilevati completano l'abitudine collegata, con conferma configurabile (auto/chiedi) | S | HLTH |
| HAB-011 | Nota sul giorno | Micro-annotazione sull'esecuzione ("5 km, pioggia") | C | — |
| HAB-012 | Collegamento a Obiettivi | L'abitudine contribuisce a un obiettivo trasversale | S | GOAL |
| HAB-013 | Ripartenza guidata | Dopo 14+ giorni di inattività totale, al ritorno: proposta di fresh start (riparti con 1 abitudine) — mai il muro delle metriche crollate | S | — |

**Scheda estesa HAB-005 (costanza resiliente — la specifica che definisce il prodotto)** — *Definizione normativa*: aderenza = esecuzioni nella finestra / attese nella finestra secondo la frequenza scelta, espressa in fasce verbali ("in ritmo", "quasi in ritmo", "in ripresa") prima che numeriche (P29). *Requisiti*: un salto singolo non può mai spostare la fascia di più di un livello; il recupero è sempre più veloce del declino (asimmetria pro-utente, deliberata); il giorno "in pausa" (HAB-008) è escluso dal denominatore; il linguaggio non contiene mai: "hai perso", "streak", "fallito", "rotto" (vietato dal glossario di modulo — C-art. 53-54). *Casi limite*: frequenza cambiata a metà finestra → la metrica ricalcola pro-quota senza penalizzare il passato; abitudine "3×/settimana" con 3 esecuzioni il lunedì? No: max 1 esecuzione conteggiata per giorno per le binarie (regola HAB-R-03). *Criterio di successo (la scommessa D-06)*: retention a 90 giorni degli utenti con ≥ 1 salto nel primo mese ≥ 1,5× il benchmark dei tracker a streak (misurato in beta).

## 3. Comportamenti specifici

- **Spunta retroattiva**: consentita fino a 7 giorni indietro ("ieri l'ho fatto ma non l'ho segnato") — la memoria imperfetta non è una colpa; oltre 7 giorni, dalla vista storica con conferma. *Deroga dichiarata: l'onestà del dato è dell'utente, non nostra (C-art. 1).*
- **Undo spunta**: sempre (MFC-R-11), anche su giorni passati.
- **Eliminazione abitudine** → cestino con tutto lo storico; l'archiviazione conserva lo storico per le statistiche di lungo periodo (differenza spiegata nel momento della scelta).
- **Mezzanotte**: il "giorno" dell'abitudine segue il fuso corrente; per i nottambuli, l'utente può impostare il confine del giorno (default 00:00, opzione fino alle 04:00). *Motivo: la corsa alle 00:30 appartiene psicologicamente a "stasera".*

## 4. Stati specifici

| Stato | Comportamento |
|---|---|
| Prima abitudine | Onboarding di modulo: 1 abitudine proposta con frequenza *suggerita* prudente (3×/settimana, non 7) — impostare al successo, non all'ideale |
| Giorno di riposo (frequenza flessibile soddisfatta) | L'abitudine appare come "riposo meritato", non come "da fare" — il non-dovere è visibile |
| In pausa | Attenuata con etichetta e ripresa a 1 tocco; esclusa da Home e notifiche |
| In ripresa (post-salti) | Linguaggio di ripartenza, mai di recupero-del-perso (P51) |

## 5. Regole di business

| ID | Regola | Motivo |
|----|--------|--------|
| HAB-R-01 | Nessuna metrica visibile all'utente può azzerarsi per un singolo giorno saltato | D-06; C-art. 54 |
| HAB-R-02 | Nessun confronto tra utenti, classifica o condivisione competitiva | C-art. 55 |
| HAB-R-03 | Le binarie contano max 1 esecuzione/giorno; le quantitative accumulano fino al target (oltre: registrato ma senza gamification dell'eccesso) | Integrità della metrica; niente incentivi malsani (C-art. 77) |
| HAB-R-04 | Max 3 promemoria per abitudine al giorno; il totale rispetta il budget notifiche globale | C-art. 58-59 |
| HAB-R-05 | Il ridimensionamento proposto (HAB-007) appare al massimo 1 volta ogni 2 settimane per abitudine | Il consiglio ripetuto diventa assillo (C-art. 63) |
| HAB-R-06 | Ordinamento: manuale; default per orario del promemoria | P82 |
| HAB-R-07 | Export: definizioni + storico esecuzioni completo (CSV/JSON) | C-art. 7 |

## 6. Eventi

| Direzione | Evento | Effetto |
|---|---|---|
| Pubblica | `hab.habit.completed / progressed / skipped(pause)` | Home/widget aggiornano; GOAL ricalcola; INS osserva |
| Pubblica | `hab.adherence.band.changed` | REV la include se in calo; mai notifica push diretta per il calo (C-art. 53) |
| Sottoscrive | `hlth.workout.detected` / `hlth.steps.threshold` | HAB-010 completa/incrementa con la policy scelta |
| Sottoscrive | `core.day.changed` | Rollover del giorno secondo il confine configurato |
| Sottoscrive | `capt.item.captured` (tipo abitudine) | Crea l'abitudine |

## 7. Edge case specifici

- Viaggio con fuso −9 h: il giorno "si allunga" → nessuna doppia esecuzione binaria (HAB-R-03), nessun giorno saltato fantasma (MFC-E-07).
- Auto-completamento (HAB-010) + spunta manuale nello stesso giorno → un solo conteggio (idempotenza per giorno).
- Abitudine quantitativa con unità cambiata (min → km) → lo storico conserva l'unità originale per giorno; i grafici segmentano.
- 100 abitudini attive → consentito ma la Home mostra solo quelle del giorno; INS può osservare il sovraccarico e suggerire (1 volta) la riduzione.
- DST: giorno da 23 h con promemoria alle 2:30 → MFC-E-08.

## 8. Criteri di accettazione

- **HAB-AC-01** — *Dato* un'abitudine "3×/settimana" con 2 esecuzioni fatte, *quando* la settimana termina con la terza mancante, *allora* la fascia di aderenza scende al massimo di un livello e nessun contatore si azzera.
- **HAB-AC-02** — *Dato* un giorno saltato ieri, *quando* l'utente spunta retroattivamente ieri, *allora* l'esecuzione conta per ieri e la metrica si ricalcola.
- **HAB-AC-03** — *Dato* la pausa attivata per 10 giorni, *quando* l'utente riprende, *allora* l'aderenza è identica a quella pre-pausa.
- **HAB-AC-04** — *Dato* 2 settimane sotto il 50%, *quando* l'utente apre l'abitudine, *allora* trova la proposta di ridimensionamento con 3 opzioni (riduci/sposta/pausa), rifiutabile senza conseguenze e non riproposta per 2 settimane.
- **HAB-AC-05** — *Dato* il workout "corsa" rilevato dalla piattaforma salute e la policy "chiedi", *quando* l'utente conferma, *allora* l'abitudine "correre" risulta completata oggi una sola volta anche se spuntata pure a mano.
- **HAB-AC-06** — *Dato* uno screen reader attivo, *quando* l'utente esplora la griglia storica, *allora* ogni giorno è annunciato con data e stato, e il riassunto verbale della metrica è disponibile.

---

*Prossimo: [Modulo Calendario](06-modulo-calendario.md)*
