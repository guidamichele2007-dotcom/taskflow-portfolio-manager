# 02 · Cattura Rapida (CAPT)

> Eredita il [MFC](00-modello-funzionale-comune.md). **La funzione più importante del prodotto**: se la cattura fallisce, ogni modulo muore di fame di dati (P3, doc Problem Space).

## 1. Scopo e tracciabilità

| Perché esiste | Principi | JTBD | Decisioni |
|---|---|---|---|
| Scaricare qualsiasi pensiero (task, spesa, nota, evento, abitudine) dal cervello al sistema in ≤ 3 secondi / ≤ 3 tocchi, da qualsiasi superficie, anche offline, anche senza guardare lo schermo | P13, P14, P18, P21 | **J1, J2, J3** | D-03; budget RNF-U1 |

## 2. Funzioni

| ID | Nome | Descrizione e motivo | Pri | Dipendenze |
|----|------|----------------------|-----|------------|
| CAPT-001 | Cattura testuale universale | Campo unico sempre raggiungibile (pulsante persistente su ogni schermata): l'utente scrive, il parser propone tipo+campi, conferma a 1 tocco | M | Parser (CAPT-004); moduli attivi |
| CAPT-002 | Cattura da widget | Widget di cattura: 2 tocchi totali dal lockscreen/homescreen | M | WID |
| CAPT-003 | Cattura vocale | Dettatura con parsing identico al testo; conferma aptica/vocale; hands-free via assistente di sistema | S | Permesso microfono/speech |
| CAPT-004 | Parser linguaggio naturale (it/en) | Riconosce: tipo entità, importo+valuta, data/ora assolute e relative ("venerdì", "tra 2 ore"), ricorrenze semplici, categoria, lista di destinazione. On-device, mai cloud | M | — |
| CAPT-005 | Anteprima interpretata modificabile | Il parsing è mostrato come chip modificabili PRIMA del salvataggio: l'utente corregge a 1 tocco. *Motivo: C-art. 67 — proporre, mai imporre* | M | CAPT-004 |
| CAPT-006 | Apprendimento delle correzioni | Le correzioni ai chip (categoria, lista, tipo) alimentano le proposte future, on-device. *Motivo: P18* | S | CAPT-005 |
| CAPT-007 | Scorciatoie tipizzate | Pressione lunga sul pulsante → scorciatoie dirette (Spesa, Task, Nota vocale) che saltano la scelta del tipo | S | CAPT-001 |
| CAPT-008 | Inbox delle catture ambigue | Se l'utente salva senza scegliere il tipo (o il parser non è sicuro), l'elemento va in una "Inbox" da smistare dopo. *Motivo: la cattura non deve MAI aspettare una decisione (J1)* | M | — |
| CAPT-009 | Cattura da condivisione di sistema | Share sheet: testo/URL condivisi da altre app diventano una cattura pre-compilata | S | OS |
| CAPT-010 | Cattura con modulo disattivato | Se il testo implica un modulo non attivo ("30€ cena" senza Finanze), la cattura va in Inbox con suggerimento di attivazione — mai persa, mai bloccata | M | CAPT-008, GAL |

**Scheda estesa CAPT-001/004/005** — *Requisiti*: dall'intento al salvataggio ≤ 3 tocchi e ≤ 3 s (mediana); parser ≥ 90% di precisione sul corpus di test it/en per data/importo/tipo; funzionamento identico offline; il testo non interpretato resta comunque salvabile come nota/inbox (mai "non ho capito" bloccante). *Vincoli*: mai invio del testo a server (C-art. 11); dizionari e modelli locali aggiornabili via release. *Casi limite*: testo multi-entità ("comprare latte e pagare bolletta") → il parser propone la divisione, l'utente decide (C-art. 67); importi ambigui ("30" senza valuta) → valuta del profilo; date passate ("ieri ho speso 20") → transazione retrodatata corretta; 500 catture in un giorno → nessun degrado. *Criteri di successo*: ≥ 70% dei nuovi utenti completa una cattura il giorno 0; ≥ 60% delle catture senza correzioni manuali dopo 30 giorni d'uso.

## 3. Comportamenti specifici

- **Salvataggio**: sempre locale-prima (MFC-R-01); la cattura non ha stato "in invio".
- **Annullo**: dopo il salvataggio, snackbar con "Annulla" (7 s) + apertura diretta dell'entità creata per rifinitura.
- **Draft**: il testo digitato sopravvive a kill/navigazione (MFC-E-02); un solo draft attivo.
- **Autorizzazioni**: la cattura funziona anche con app bloccata da biometria SOLO come "cassetta delle lettere" (l'elemento si salva cifrato, visibile dopo lo sblocco). *Motivo: J1 batte tutto, ma i dati non si mostrano (MFC-R-22).*

## 4. Stati specifici

| Stato | Comportamento |
|---|---|
| Parser incerto | Chip "tipo?" evidenziato + default Inbox; mai blocco |
| Microfono negato | CAPT-003 nascosta con spiegazione in Impostazioni; il resto intatto |
| Inbox con elementi | Badge numerico discreto sulla Home (unico badge ammesso: rappresenta lavoro dell'utente, non nostro engagement) |
| Free | Nessun limite alla cattura, mai (la cattura non è mai una leva commerciale — C-art. 170) |

## 5. Regole di business

| ID | Regola | Motivo |
|----|--------|--------|
| CAPT-R-01 | La cattura non richiede mai più di: aprire → scrivere/dettare → confermare | P14; RNF-U1 |
| CAPT-R-02 | Nessuna cattura viene mai rifiutata: il fallback universale è l'Inbox | J1 — lo scarico mentale è sacro |
| CAPT-R-03 | Il parser propone, l'utente dispone: nessun salvataggio automatico di interpretazioni senza conferma (tranne il testo grezzo in Inbox) | C-art. 67 |
| CAPT-R-04 | L'apprendimento delle correzioni è locale, per-utente, azzerabile dalle impostazioni | C-art. 11, 23 |
| CAPT-R-05 | Gli elementi in Inbox più vecchi di 14 giorni compaiono nella Revisione settimanale (mai notifiche assillanti) | P42; C-art. 60 |

## 6. Eventi

| Direzione | Evento | Effetto |
|---|---|---|
| Pubblica | `capt.item.captured` (tipo, modulo destinazione) | Il modulo destinatario crea l'entità; Home si aggiorna; INS conta (anonimo, locale) |
| Pubblica | `capt.inbox.item.added` | Badge Inbox; REV include lo smistamento |
| Sottoscrive | `core.module.activated` | Il parser abilita i tipi del nuovo modulo |

## 7. Edge case specifici

- Dettatura interrotta da chiamata → il parziale è in draft.
- Lingua del testo ≠ lingua UI (utente bilingue) → il parser tenta entrambe le lingue attive.
- Emoji/valute non standard nel testo → conservati nel titolo, ignorati dal parsing.
- Cattura durante l'onboarding (ONB-003) → identica alla cattura normale: nessuna versione finta.
- Doppio invio rapido → una sola entità (MFC-E-01).

## 8. Criteri di accettazione

- **CAPT-AC-01** — *Dato* l'app su qualsiasi schermata, *quando* l'utente tocca cattura, scrive "30 cena con Sara" e conferma, *allora* esiste una spesa di 30 € (valuta profilo) con data oggi e categoria proposta, creata con ≤ 3 tocchi totali.
- **CAPT-AC-02** — *Dato* il testo "chiamare il medico venerdì alle 9", *quando* l'utente conferma, *allora* esiste un task con scadenza il venerdì successivo alle 9:00 locali.
- **CAPT-AC-03** — *Dato* il parser che propone categoria errata, *quando* l'utente corregge il chip e conferma, *allora* l'entità ha la categoria corretta e la correzione influenza le proposte successive per testi simili.
- **CAPT-AC-04** — *Dato* modalità aereo, *quando* l'utente cattura 10 elementi, *allora* tutti sono creati e visibili; al ritorno della rete sincronizzano senza duplicati.
- **CAPT-AC-05** — *Dato* un testo non interpretabile, *quando* l'utente conferma, *allora* l'elemento è nell'Inbox integralmente e nessun contenuto è andato perso.
- **CAPT-AC-06** — *Dato* modulo Finanze disattivato, *quando* l'utente cattura "30€ benzina", *allora* l'elemento va in Inbox con proposta di attivazione, senza attivazioni automatiche.

---

*Prossimo: [Modulo Attività](03-modulo-attivita.md)*
