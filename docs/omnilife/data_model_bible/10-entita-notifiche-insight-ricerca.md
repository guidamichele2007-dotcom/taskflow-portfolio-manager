# 10 · Entità Notifiche, Insight, Ricerca (NTF / INS / SRCH)

> Eredita il [MDC](00-modello-dati-comune.md). Entità di servizio trasversale, mai proprietarie di un modulo di dominio.

## DM-NTF-01 · NotificationRequest (Richiesta di notifica)

**Descrizione**: ogni modulo *richiede* una notifica al broker centrale (NTF-001) — nessun modulo notifica direttamente. Questa entità rappresenta la richiesta e il suo esito, necessaria per governare budget, digest e auto-disattivazione (NTF-002/006).

| Campo | Tipo concettuale | Obbligatorio |
|---|---|---|
| `categoria` | Enum (per modulo/tipo, granulare — NTF-007) | Sì |
| `priorita` | Enum: promemoria_utente · utile · informativa | Sì | NTF-001 scheda estesa: solo "utile/informativa" consumano il budget |
| `entita_riferimento` | Riferimento (id + tipo) all'entità che ha originato la richiesta | Sì |
| `programmata_per` | Timestamp | Sì |
| `esito` | Enum: mostrata · azionata · ignorata · assorbita_in_digest | Sì (aggiornato nel tempo) |

**Relazioni**: nessuna relazione strutturale; referenzia (sola lettura) l'entità che ha generato la richiesta.

**Regole**: **generata localmente** — i push remoti sono solo trigger di sync silenziosi, mai contenuti (NTF §2 scheda estesa, C-art. 7). Il conteggio degli esiti "ignorata" per categoria alimenta NTF-006 (dopo 3 consecutivi, proposta di auto-disattivazione).

**Stati**: pianificata · mostrata · azionata · ignorata · scaduta_di_significato (es. un promemoria delle 23 aperto alle 8, NTF-004: non si mostra più, si vede solo in app).

**Eventi collegati**: sottoscrive `ntf.request` (interno, da ogni modulo); pubblica `ntf.action.performed`.

**Riferimenti Functional Bible**: NTF-001…008, NTF-R-01…05.

---

## DM-INS-01 · InsightRuleConfig (Configurazione regole)

**Descrizione**: **non un dato utente** — configurazione di sistema scaricata (firmata), sola lettura per l'app (INS-003). Inclusa qui solo per completezza del modello, con la deroga dichiarata.

| Campo | Note |
|---|---|
| `trigger`, `condizione`, `testo`, `priorita`, `frequenza_massima` | Definiti dalla config firmata, non dall'utente (INS-003) |

**Deroga dichiarata**: non sincronizza dall'app verso il cloud (va nell'altro verso: scaricata dal Registry); non ha ciclo di vita utente; non è mai esportata come dato personale.

**Riferimenti Functional Bible**: INS-003.

---

## DM-INS-02 · InsightFeedback (Riscontro dell'utente)

**Descrizione**: il riscontro "utile/non utile" dell'utente su un insight mostrato (INS-005) — questo **è** un dato utente pieno, a differenza della config delle regole.

| Campo | Tipo concettuale | Obbligatorio |
|---|---|---|
| `famiglia_insight` | Riferimento alla famiglia di regola (INS-003) | Sì |
| `riscontro` | Enum: utile · non_utile | Sì |
| `data` | Timestamp | Sì |

**Relazioni**: nessuna relazione strutturale con altre entità di dominio (riferimento solo alla famiglia di regola, non a un'istanza di insight persistita — gli insight stessi non sono un'entità con ciclo di vita proprio, sono generati e mostrati al momento, mai archiviati come tali).

**Regole**: il feedback tara le regole solo localmente (INS-R-*, mai inviato al server); l'azzeramento dell'apprendimento del parser (CAPT-R-04) azzera anche questi feedback (INS edge case).

**Riferimenti Functional Bible**: INS-005, INS-AC-03.

---

## DM-SRCH-01 · RecentSearchQuery (Ricerca recente) — **entità locale, esclusa da sync**

**Descrizione**: le ultime ricerche dell'utente (SRCH-004), mantenute per comodità di riuso, mai per profilazione.

| Campo | Tipo concettuale | Obbligatorio |
|---|---|---|
| `testo_query` | Testo | Sì |
| `filtri_applicati` | Struttura (tipo, data, categoria…) | No |
| `eseguita_il` | Timestamp | Sì |

**Relazioni**: nessuna.

**Deroga dichiarata (MDC §9)**: **locale al dispositivo, esclusa da sincronizzazione e da backup** (SRCH-R-01, C-art. 45 — minimizzazione: la cronologia di ricerca è tra i dati più rivelatori delle intenzioni di una persona).

**Regole**: massimo 10 ricerche recenti conservate; cancellabili singolarmente e in blocco (SRCH-004).

**Riferimenti Functional Bible**: SRCH-004, SRCH-R-01.

---

*Nota sull'indice di ricerca*: l'indice full-text (SRCH-001) **non è un'entità utente**: è una proiezione derivata dalle entità esistenti (MDC §11), ricostruibile senza perdita — non compare qui come scheda entità perché non ha identità, ciclo di vita o proprietà propri distinti dai dati che indicizza.

---

*Prossimo: [Versionamento e Sincronizzazione](11-versionamento-e-sincronizzazione.md)*
