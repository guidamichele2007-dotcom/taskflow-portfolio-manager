# 09 · Feature Philosophy

> Prima dell'elenco delle funzionalità viene la filosofia con cui le funzionalità si scelgono. Questo documento è il sistema immunitario del prodotto: il feature creep non si combatte con la buona volontà, si combatte con un processo che rende **il no la risposta di default** e il sì una conquista documentata.

## 1. La premessa: ogni feature è un debito

Una funzione non è un regalo agli utenti: è un impegno perpetuo. Ogni feature aggiunta comporta, per sempre:

- **costo cognitivo** per ogni utente (anche per chi non la usa: la vede, la scarta, la ri-scarta);
- **costo di manutenzione** (bug, aggiornamenti OS, test, accessibilità, localizzazione — per sempre);
- **costo di coerenza** (ogni feature futura dovrà conviverci);
- **costo di opportunità** (l'energia spesa qui non migliora l'esistente — principio 61).

Il valore, invece, non è garantito. Quindi l'onere della prova è di chi propone: **una feature è colpevole finché non dimostrata innocente.** ClickUp e l'Evernote del declino (doc 05) sono ciò che succede quando l'onere della prova si inverte.

## 2. Perché una funzione entra: i sette cancelli

Una proposta diventa feature solo se supera **tutti e sette** i cancelli, in ordine. Ogni cancello ha un proprietario che può dire no.

| # | Cancello | Domanda | Bocciata se… |
|---|----------|---------|--------------|
| 1 | **Job** | Quale [job documentato](07-jobs-to-be-done.md) serve, per quali [personas](06-personas.md)? | Il job non esiste nel doc 07 (prima si discute il job, poi la feature) |
| 2 | **Problema, non soluzione** | Qual è il problema osservato (dati, ricerca, recensioni), non l'idea carina? | Nasce da "sarebbe bello", da un competitor ("ce l'hanno loro"), o da un singolo utente rumoroso |
| 3 | **Non duplicazione** | Un'evoluzione dell'esistente servirebbe lo stesso job? | Sì — allora si migliora l'esistente (principio 61; Constitution art. 61) |
| 4 | **Coerenza** | Rispetta Constitution e principi? Funziona offline? È accessibile? Regge nei moduli? | Viola anche un solo articolo; richiede lettura server dei contenuti; è online-only |
| 5 | **Costo totale** | Qual è il costo perpetuo (manutenzione, cognitivo, coerenza), non solo il costo di costruzione? | Il proponente non sa stimarlo — tornare quando lo sa |
| 6 | **Misurabilità** | Qual è la metrica di successo e la soglia di fallimento, decise PRIMA di costruire? | "Lo sapremo quando lo vedremo" |
| 7 | **Sottrazione** | Che cosa togliamo, semplifichiamo o decliniamo per fare spazio? | Niente — la capacità del prodotto non è infinita: chi entra deve pagare un posto |

**Il verdetto ha tre esiti**: *Sì* (in roadmap con metrica e proprietario) · *No* (registrato nel Decision Log con la motivazione: i no documentati sono il patrimonio che ci evita di ridiscutere tutto ogni sei mesi) · *Non ora* (il problema è vero, il momento no: parcheggiata con la condizione di risveglio esplicita, es. "rivalutare a 100k MAU").

## 3. Perché una funzione viene rifiutata: il catalogo dei no

I pattern di rifiuto ricorrenti, nominati per essere riconosciuti al volo:

1. **Il no da specchietto** ("i competitor ce l'hanno"): la parità di feature è la strategia di chi non ha strategia. Si valuta il *job*, non la feature altrui.
2. **Il no da power user**: la richiesta sofisticata del 2% che complica il 98%. Risposta: profondità progressiva se il costo è nullo per gli altri; altrimenti no.
3. **Il no da metrica avida**: la feature che gonfia una metrica (engagement, sessioni) senza servire un job. È il cancello 1 + Constitution Titolo III.
4. **Il no da fondatore innamorato**: l'idea interna carina senza problema documentato. I cancelli valgono anche — soprattutto — per noi.
5. **Il no da cliente enorme** (futuro enterprise/B2B2C): il paying customer che chiede la feature fuori posizionamento. Il prezzo di listino non compra la roadmap (principio 95).
6. **Il no da tecnologia di moda**: "dobbiamo avere l'AI/il web3/il social". La tecnologia entra quando serve un job meglio di com'era servito prima, mai come fine.
7. **Il no da configurabilità** ("rendiamola un'opzione"): l'opzione è il modo più vile di non decidere. Ogni setting è una decisione delegata all'utente con costo perpetuo (anti-J18). Le impostazioni si contano e si contingentano.

## 4. Come evitiamo il feature creep: i meccanismi strutturali

I buoni propositi non bastano; servono strutture:

1. **Il budget di superficie.** Ogni modulo ha un tetto dichiarato di: schermate, voci di impostazioni, elementi nella vista principale. Superarlo richiede un emendamento motivato, non un merge.
2. **La revisione di sottrazione trimestrale.** Ogni trimestre, ogni modulo propone almeno una candidata alla rimozione o semplificazione (dati d'uso alla mano). La rimozione è una feature (principio 105); le feature sotto la soglia d'uso per 2 cicli di misura escono (con deprecazione gentile e preavviso).
3. **Il Decision Log dei no.** I no motivati sono ricchezza: impediscono ai fantasmi di tornare ogni sei mesi con un nome nuovo.
4. **La regola del cancello 7** (sottrazione obbligatoria): il costo di ogni sì è visibile e pagato subito.
5. **La metrica con la data di verifica.** Ogni feature rilasciata ha già appuntamento con il proprio giudizio: alla data X, se la metrica è sotto la soglia Y, si discute la rimozione — di default, non per iniziativa di qualcuno.
6. **Il guardiano della semplicità.** In ogni review di prodotto una persona ha il ruolo formale di avvocato della sottrazione: il suo compito è argomentare contro l'aggiunta. Se nessuno riesce a fare quel ruolo con convinzione, la review non è valida.

## 5. Il ciclo di vita di una feature

```
Problema documentato → 7 cancelli → Roadmap (con metrica + proprietario + data di verifica)
     → Costruzione (Definition of Done: prestazioni, accessibilità, offline, telemetria)
     → Rollout graduale → Verifica alla data → [Vive | Si semplifica | Si rimuove]
```

Nessuna feature è mai "finita e dimenticata": o dimostra valore ricorrente, o restituisce la sua superficie.

## 6. Le eccezioni oneste

Tre categorie non passano i cancelli feature-per-feature, e lo dichiariamo:

- **Obblighi** (legali, di store, di piattaforma): entrano perché devono; il design li rende comunque degni.
- **Fondamenta** (sync, backup, ricerca, accessibilità): non servono "un job" — servono tutti i job. Si giudicano con i requisiti (doc tecnico 05), non con le metriche d'uso.
- **Scommesse strategiche** (es. il grafo nell'MVP prima che gli insight lo ripaghino): decisioni da Decision Log con orizzonte dichiarato, non feature ordinarie. Sono poche, grandi e firmate.

---

*Prossimo: [Success Metrics](10-success-metrics.md) — come misuriamo che tutto questo funzioni.*
