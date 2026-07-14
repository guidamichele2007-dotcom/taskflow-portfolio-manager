# 00 · Modello UX Comune (MUC)

> **Il documento più importante della UX Bible.** Come il [Modello Funzionale Comune](../functional_bible/00-modello-funzionale-comune.md) evita di ripetere 150 volte le stesse regole funzionali, questo documento definisce **una sola volta** i pattern di navigazione, feedback, tempistiche, gesti, errori, stati vuoti e cronologia che si applicano a ogni schermata e ogni flusso. I documenti successivi citano questo modello invece di ripeterlo. Regola di lettura: **ciò che un flusso non specifica si comporta come descritto qui.**

## 1. Tracciabilità e convenzioni

- Ogni pattern cita: principi Product Bible (`P#`), articoli Constitution (`C-art. #`), funzioni Functional Bible (`PREFISSO-###`), JTBD (`J#`).
- ID di questa Bible: schermate `SCR-###`, nodi IA `IA-###`, flussi `FLOW-PREFISSO-##`, regole UX `UX-R-###`.
- Fonte di verità per priorità e release: [Matrice Funzioni](../functional_bible/17-matrici.md); questa Bible non ridefinisce priorità, le eredita.

## 2. Timing universale (il vocabolario del tempo)

| Soglia | Comportamento | Motivo |
|---|---|---|
| **0–50 ms** | Percepito come istantaneo: nessun feedback di attesa, solo lo stato finale | RNF-P4; P22 |
| **50–300 ms** | Micro-transizione (fade/scale) che accompagna il cambio di stato | Continuità percettiva |
| **> 300 ms** | **Deve** comparire uno stato di caricamento esplicito (skeleton, mai spinner a schermo pieno) | **UX-R-001**: P23, C-art. 116-118 |
| **> 3 s** | Lo stato di caricamento include un messaggio testuale + possibilità di annullare, se l'operazione lo consente | Rispetto dell'attesa dell'utente |
| **> 10 s** (solo operazioni online: ripristino, import) | Barra di progresso con stima e possibilità di continuare in background | BKP-003, EXP-001 |
| **Snackbar di annullamento (undo)** | Sempre 7 secondi, posizione fissa (basso, sopra la tab bar), un solo undo alla volta (il secondo sostituisce il primo, eseguendo il primo silenziosamente) | MFC-R-11; P2 |
| **Celebrazione/completamento** | 800 ms massimo, mai bloccante, sempre interrompibile da un tocco altrove | P45, C-art. 68 |

## 3. Vocabolario del feedback (multi-canale, sempre coordinato)

Ogni azione rilevante risponde su **fino a 4 canali coordinati**, mai in conflitto tra loro:

| Canale | Uso | Regola |
|---|---|---|
| **Visivo** | Sempre presente: cambio di stato, colore, icona, posizione | Unico canale obbligatorio (accessibilità: mai l'unico portatore di significato — C-art. 97) |
| **Aptico** | Conferma di azioni dirette (spunta, swipe di eliminazione, soglia di drag raggiunta) | Vocabolario a 3 livelli: *lieve* (selezione/spunta), *medio* (conferma/completamento), *di allerta* (errore/limite) — mai oltre questi 3, mai a ogni scroll (P62) |
| **Sonoro** | Opzionale e disattivabile, mai per azioni frequenti (spunte, digitazione) | Riservato a eventi rari e volute dall'utente (fine focus/timer, se presente in v2.x) |
| **Testuale (microcopy)** | Conferma leggibile per screen reader di ciò che è successo | Ogni feedback visivo/aptico ha un annuncio equivalente per l'accessibilità (C-art. 99) |

**UX-R-002**: nessun feedback richiede più di un canale per essere compreso (ridondanza intenzionale, mai dipendenza — C-art. 71, 97).

## 4. Animazione: durate e curve (token condivisi con il Design System del doc tecnico 04)

| Categoria | Durata | Curva | Uso |
|---|---|---|---|
| **Micro** | 100–150 ms | ease-out | Tocchi, spunte, cambi di stato di piccoli elementi |
| **Standard** | 200–250 ms | ease-in-out (spring morbido, damping alto) | Apertura fogli, transizioni tra viste, espansioni |
| **Enfasi** | 300–350 ms | spring con leggero overshoot (≤ 4%) | Completamenti, conferme importanti, milestone |
| **Uscita** | 150–200 ms | ease-in | Chiusure, dismiss, eliminazioni |

**UX-R-003**: ogni animazione ha una variante statica (durata 0, solo cambio di stato) attivata da "riduci movimento" di sistema — nessuna eccezione (C-art. 96, 101). **UX-R-004**: nessuna animazione supera 350 ms (P57). **UX-R-005**: le animazioni comunicano relazione spaziale o causale, mai decorazione pura (P57, C-art. 96).

## 5. Pattern universale di navigazione: profondità e ritorno

- **Profondità massima**: 3 livelli (tab → lista → dettaglio), come da P32/design doc 04 §3.2. Un foglio di dettaglio (bottom sheet) NON conta come livello di profondità: mantiene il contesto della vista sottostante (percepito, non impilato).
- **Il ritorno è sempre nello stesso posto**: chiudere un dettaglio riporta esattamente al punto di scroll e al filtro della lista di provenienza (**UX-R-006**, stato ripristinato — MFC-E-18 applicato alla UX).
- **Back gesture / tasto back di sistema**: sempre disponibile, mai intercettato per scopi diversi dalla navigazione (mai "back" riprogrammato per aprire un paywall — C-art. 71). Eccezione dichiarata: durante la digitazione in un campo con draft non salvato, back chiude prima la tastiera (comportamento di piattaforma standard, non nostro).
- **Cronologia di navigazione**: sopravvive al passaggio in background fino a 30 minuti (poi l'app riapre sulla Home — continuità senza eternizzare stati dimenticati). Sopravvive sempre al cambio di orientamento e al multitasking di sistema.

## 6. Pattern universale di stato vuoto (eredita [MFC §4](../functional_bible/00-modello-funzionale-comune.md#4-catalogo-universale-degli-stati))

Ogni schermata vuota, in ogni sua variante, segue questa anatomia fissa a 3 elementi: **illustrazione o icona sobria** (mai spinner, mai emoji generiche) → **una frase che spiega il beneficio, non la funzione** ("Qui vedrai le tue spese del mese" non "Nessuna spesa trovata") → **un'azione primaria a 1 tocco** che porta alla cattura o alla funzione rilevante. Dettaglio completo per ogni variante: [09-empty-states](09-empty-states.md).

## 7. Pattern universale di errore (eredita MFC + Constitution art. 104)

Ogni errore, ovunque, segue: **che cosa è successo** (linguaggio umano, mai codice) → **perché, se conosciuto** → **azione riparatrice a 1 tocco** (riprova / vai altrove / contatta supporto) → **il resto dello schermo resta operativo** (C-art. 122). Dettaglio completo: [10-error-experience](10-error-experience.md).

## 8. Pattern universale di annullamento (undo)

**UX-R-007**: ogni azione distruttiva o di completamento ha undo immediato via snackbar (7 s, §2); **UX-R-008**: mai un dialogo di conferma preventivo per azioni singole reversibili (P2) — la conferma preventiva esiste SOLO per: eliminazione definitiva, cancellazione account, disconnessione dispositivo, azioni che toccano > 20 elementi in blocco. **UX-R-009**: l'undo ripristina lo stato esatto (posizione in lista, collegamenti, ordine) — mai un ripristino "approssimato".

## 9. Il flusso generico del ciclo di vita di un'entità (Generic Entity Flow — GEF)

> Questo è il template che copre l'80% delle "Task Flows" richieste (creare/modificare/duplicare/condividere/archiviare/ripristinare/eliminare/recuperare/cercare/taggare/versionare/cronologia) per **ogni** entità del grafo (task, spesa, abitudine, nota, obiettivo — P33: un'anatomia sola). I documenti di modulo in [06-task-flows](06-task-flows-entita.md) specificano solo le deroghe.

| Passo | Azione utente | Risposta app | Tempo | Feedback | Edge case | Annullo |
|---|---|---|---|---|---|---|
| **Apri lista** | Tocca il modulo/tab | Vista lista, ultimo stato di scroll/filtro ripristinato | ≤ 300 ms | Skeleton se > 300 ms | Lista vuota → stato vuoto (§6); lista enorme → virtualizzata (MFC-E-14) | — |
| **Crea** | Tocca `+` o cattura (CAPT-001) | Foglio di creazione o entità inserita direttamente in lista con focus sul titolo | ≤ 50 ms percepiti | Aptica lieve alla conferma | Doppio tocco → 1 sola entità (MFC-E-01) | Snackbar "Annulla" 7 s |
| **Apri dettaglio** | Tocca l'entità in lista | Si espande un foglio di dettaglio (bottom sheet), lista sottostante visibile sfocata | 200–250 ms (standard) | Transizione di espansione dal punto di tocco | Entità eliminata nel frattempo da altro device → "Elemento non più disponibile" con chiusura automatica gentile | Swipe giù o tocco fuori chiude senza side-effect |
| **Modifica campo** | Tocca il campo, digita/seleziona | Salvataggio automatico silenzioso (nessun pulsante Salva) | ≤ 50 ms | Nessun feedback invasivo: il valore aggiornato è il feedback | Campo vuoto per un obbligatorio → bordo di attenzione + microcopy, mai bloccante la chiusura (si salva l'ultimo valido) | Cronologia registra ogni valore (MFC-R-07) |
| **Duplica** | Azione da menu contestuale (long-press o "···") | Nuova entità identica (senza cronologia), inserita subito sopra l'originale, in modifica del titolo | ≤ 300 ms | Aptica lieve | — | Snackbar "Annulla" 7 s |
| **Condividi** (dove previsto) | Azione da menu contestuale | Foglio di condivisione di sistema con formato leggibile pre-generato | ≤ 300 ms | — | Contenuto troppo grande → generazione in background con notifica | Chiusura del foglio = nessuna azione eseguita |
| **Tagga/categorizza** | Tocca il chip categoria/lista | Selettore a comparsa (max 3 opzioni visibili + "altro") | ≤ 200 ms | Aptica lieve alla selezione | Categoria eliminata nel frattempo → torna a "Senza categoria" | Il cambio è immediato, cronologia registra il prima |
| **Archivia** | Swipe laterale o azione da menu | L'entità scompare dalla lista attiva con transizione di uscita; resta in Archivio | 150–200 ms (uscita) | Aptica lieve + snackbar "Annulla" | — | Snackbar 7 s |
| **Elimina** | Swipe (colore neutro, mai rosso allarmante — P48) o azione da menu | Sparisce dalla lista attiva; va in Cestino | 150–200 ms | Aptica media + snackbar "Annulla" | Eliminazione di blocco (> 20) → conferma esplicita (UX-R-008) | Snackbar 7 s; oltre, ripristino da Cestino (30 gg) |
| **Ripristina** (da archivio/cestino) | Azione da menu nella vista Archivio/Cestino | L'entità torna nella lista attiva, stessa posizione logica | ≤ 300 ms | Aptica lieve + conferma testuale | Collegamenti sospesi tornano attivi (GOAL) | — |
| **Elimina definitivamente** | Azione esplicita da Cestino | **Conferma forte** (dialogo con testo da digitare per volumi grandi, altrimenti doppia conferma) → distruzione | Immediato dopo conferma | Aptica di allerta | Nessuno | **Nessuno**: è l'unica azione senza undo, dichiarata come tale nel dialogo stesso |
| **Cerca** | Digita in SRCH | Risultati incrementali filtrati per tipo | ≤ 100 ms (RNF-P5) | Evidenziazione del termine trovato | 0 risultati → stato vuoto specifico (§6) | — |
| **Versione/Cronologia** | Azione "Cronologia" dal dettaglio | Elenco cronologico di modifiche (campo, valore, quando, da quale device) | ≤ 300 ms | — | Nessuna modifica ancora → stato vuoto minimale | Ripristinare una versione crea una nuova modifica (mai riscrive la storia — MFC §2.4) |

**Deroghe dichiarate per tipo di entità**: Note usa versioni a snapshot invece che per-campo (NOTE-006); Calendario (eventi di sistema) non ha Cestino proprio (CAL, sola lettura/scrittura delegata); Salute (dati di piattaforma) non ha ciclo di vita nostro (HLTH-R-02). Ogni deroga è ripetuta nel documento di modulo pertinente.

## 10. Pattern universale di sincronizzazione (percezione utente)

**UX-R-010**: la sync non genera mai una schermata dedicata di attesa: è invisibile per default; **UX-R-011**: l'unico luogo dove si "vede" è il pannello di stato (SYNC-002), mai un badge invasivo sull'icona app o sulle singole entità salvo errore persistente > 72 h (NTF/SYNC). **UX-R-012**: nessuna azione utente è mai bloccata in attesa della sync (offline-first, C-art. 120-123).

## 11. Pattern universale di accessibilità (baseline applicata da ogni schermata)

Ogni schermata, per costruzione, rispetta: focus order logico (top-to-bottom, left-to-right nel layout LTR, invertito in RTL) → ogni elemento interattivo ha un'etichetta e un ruolo → ogni feedback visivo ha un annuncio equivalente → target di tocco ≥ 44×44pt/48×48dp → funzionamento completo con Dynamic Type/font scaling 200% → nessuna informazione veicolata dal solo colore. Dettaglio completo: [12-accessibility-bible](12-accessibility-bible.md).

---

*Indice: [README](README.md) · Prossimo: [Information Architecture](01-information-architecture.md)*
