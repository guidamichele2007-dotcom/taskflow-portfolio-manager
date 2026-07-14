# 11 · Versionamento e Sincronizzazione (approfondimento cross-entità)

> Eredita il [MDC §7-8](00-modello-dati-comune.md). Questo documento consolida in un unico luogo le regole di calcolo e convergenza che i documenti di modulo richiamano per riferimento, per evitare di duplicarle in più schede entità (P31).

## 1. Perché un documento dedicato

Tre calcoli concettuali sono richiamati da più schede entità e meritano una definizione unica e precisa: (a) la generazione delle occorrenze ricorrenti (Task, Finanze), (b) l'aderenza resiliente delle Abitudini (HAB-005), (c) l'aggregazione del progresso degli Obiettivi (GOAL-003). Nessuno dei tre introduce comportamento nuovo: formalizza ciò che la Functional Bible già specifica in linguaggio discorsivo.

## 2. Strategia di versionamento per entità (tabella riepilogativa)

| Entità | Strategia | Deroga? |
|---|---|---|
| Task, Transaction, FinancialAccount, Category, Budget, Habit, Goal, ManualHealthMetric | Per-campo (MFC-R-07) | No |
| Note | A snapshot (NOTE-006) | Sì, dichiarata |
| Subtask, HabitExecution, TimeBox, GraphLink | Per-campo semplificato (poche modifiche possibili nella vita dell'entità) | No, ma cronologia meno profonda per natura dell'entità |
| EventReference | Nessuna (cronologia del provider) | Sì, deroga totale |
| HealthPlatformReading | Nessuna (cache locale, non nostra) | Sì, deroga totale |
| RecentSearchQuery | Nessuna (mai modificata, solo creata/eliminata) | Locale, esclusa da sync |
| Account, Device, Subscription, Setting | Per-campo, ambito account/dispositivo (SET-R-03) | No |

## 3. Generazione delle occorrenze ricorrenti (Task §DM-TASK-01, Finanze §DM-FIN-01)

Regola comune (già dichiarata separatamente in TASK-004 scheda estesa e FIN-006/edge case, qui unificata):

- **Generazione pigra**: la prossima occorrenza si crea **al completamento o alla scadenza** della corrente — mai tutte le occorrenze future materializzate in anticipo (inquinerebbero ricerca, conteggi ed export).
- **Idempotenza per periodo**: l'esecuzione di una ricorrenza (task o transazione) non avviene mai due volte per lo stesso periodo, anche se calcolata da due dispositivi offline in modo indipendente — la chiave logica è (regola_ricorrenza_id, periodo), non il timestamp di esecuzione.
- **Regole di data limite** (comuni a entrambi i moduli, MFC-E-09): occorrenza il 29/2 → nei non bisestili scatta il 28/2; occorrenza il giorno 31 → ultimo giorno del mese nei mesi più corti; mai un "30/2" o date inesistenti.
- **Modifica/eliminazione**: sempre con scelta esplicita "questa occorrenza / tutte le future" — mai un'assunzione implicita (TASK-004, FIN comportamenti specifici).
- **Integrità storica**: completare un'occorrenza non retro-modifica mai le occorrenze passate (TASK-R-08).

## 4. Calcolo dell'aderenza resiliente (Abitudini, §DM-HAB-02)

Formalizzazione del calcolo dichiarato in HAB-005 (Functional Bible):

```
finestra = 28 giorni mobili, terminanti oggi
giorni_attesi = giorni della finestra che, secondo regola_frequenza, richiedevano un'esecuzione
              (esclusi i giorni ricadenti in un intervallo di intervalli_pausa)
esecuzioni_valide = numero di HabitExecution nella finestra (max 1/giorno per le binarie, HAB-R-03)

aderenza_grezza = esecuzioni_valide / giorni_attesi   (se giorni_attesi = 0 → nessuna fascia, stato "senza dati sufficienti")

fascia_verbale = funzione(aderenza_grezza) → { "in ritmo", "quasi in ritmo", "in ripresa" }
```

Vincoli sul funzione di mappatura verso `fascia_verbale` (dichiarati normativamente in HAB-005, qui esplicitati come regole di calcolo):

- **Asimmetria pro-utente deliberata**: un singolo giorno saltato non può far scendere la fascia di più di un livello, indipendentemente da quanto incida sul valore grezzo (HAB-AC-01) — la funzione non è una soglia lineare pura sul valore grezzo, ma una macchina a stati con isteresi che favorisce la stabilità verso l'alto.
- **Recupero più veloce del declino**: il numero di esecuzioni consecutive necessarie per risalire di una fascia è inferiore al numero di salti che ne hanno causato la discesa (asimmetria deliberata, D-06).
- **Mai azzeramento**: la fascia più bassa ("in ripresa") non equivale a zero e non usa mai il linguaggio di fallimento (glossario vietato: "hai perso", "streak", "fallito", "rotto" — C-art. 53-54).
- Cambiare `regola_frequenza` a metà finestra ricalcola `giorni_attesi` pro-quota, senza penalizzare retroattivamente i giorni già passati con la regola precedente.

## 5. Calcolo del progresso aggregato (Obiettivi, §DM-GOAL-01)

Formalizzazione del calcolo dichiarato in GOAL-003 (Functional Bible):

```
per ogni GraphLink attivo (ruolo=contributo_obiettivo) del Goal:
    se il modulo del contributo è disattivato → fronte = "in pausa", ESCLUSO dal calcolo della fascia
    altrimenti:
        fronte.progresso_nativo = calcolato secondo il tipo:
            SavingsGoal      → importo_accantonato / importo_target
            Task/TaskList    → task_completati / task_totali
            Habit            → aderenza (vedi §4)
            scadenza/evento  → superata booleano o giorni_alla_scadenza

fascia_complessiva = MIN(fronte.progresso_nativo per ogni fronte NON in pausa)
                     → il fronte peggiore domina (GOAL-R-03): mai una media che nasconda un fronte in difficoltà
```

- **Nessuna media aritmetica** tra unità incommensurabili (€, task, aderenza, giorni): ogni fronte resta nel proprio asse nativo; solo la *fascia verbale* complessiva è derivata dal fronte peggiore.
- La formula è sempre spiegabile in una frase mostrabile all'utente ("perché vedo questo stato?" — C-art. 6): non è mai un punteggio opaco.
- Un contributo eliminato (non solo sospeso) esce dal calcolo; la cronologia del Goal conserva il fatto che è esistito (GOAL-003 scheda estesa).

## 6. Modello concettuale di convergenza per tipo di campo (CRDT, senza scegliere un'implementazione)

| Tipo di campo | Strategia di convergenza concettuale |
|---|---|
| Scalare singolo (testo breve, numero, data, enum) | Ultima modifica logicamente più recente vince (Last-Writer-Wins per vettore di versione, non wall-clock — MFC-E-10); la perdente resta in cronologia (MFC-R-08) |
| Insieme di riferimenti (GraphLink, checklist di Subtask, milestone di Goal) | Unione insiemistica: le aggiunte concorrenti si sommano, mai si perdono (INV-04); le rimozioni concorrenti rimuovono l'elemento indipendentemente dall'ordine di arrivo |
| Contenuto lungo a snapshot (Note) | Merge per paragrafo dove non ambiguo; altrimenti l'intero snapshot segue la regola scalare, con lo snapshot perdente conservato come versione precedente (mai perso, NOTE-AC-03) |
| Contatore derivato (saldo conto, aderenza abitudine, progresso obiettivo) | **Mai sincronizzato come valore**: è sempre ricalcolato localmente dai dati sorgente (transazioni, esecuzioni, contributi) dopo che questi convergono — elimina per costruzione ogni possibile conflitto sul valore derivato |

**Principio guida**: ogni volta che un valore può essere *calcolato* da altri dati già sincronizzati, si sceglie di calcolarlo piuttosto che sincronizzarlo — questo è il motivo per cui saldo, aderenza e progresso obiettivo non sono mai campi scritti direttamente (coerente con FIN-R-02, HAB-005, GOAL-003).

## 7. Decisioni tecniche esplicitamente rinviate

- Il tipo esatto di struttura CRDT (state-based vs operation-based) per ogni categoria di campo: decisione di implementazione, non di modello dati.
- Il formato esatto dell'identificatore univoco (INV-01/02): decisione tecnica, purché rispetti unicità e ordinabilità temporale.
- La cadenza e il meccanismo esatto di ricalcolo dei contatori derivati (on-write vs on-read con cache): decisione di implementazione.

---

*Prossimo: [Audit, Permessi, Vincoli e Invarianti](12-audit-permessi-vincoli-invarianti.md)*
