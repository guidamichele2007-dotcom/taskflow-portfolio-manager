# 06 · Lifecycle delle Richieste e degli Eventi

> Eredita [00](00-principi-architetturali.md)…[05](05-offline-first-sincronizzazione-caching.md). [03 §6](03-event-driven-architecture.md#6-lifecycle-di-una-richiesta-end-to-end-attraverso-i-layer) ha già mostrato un esempio concreto (completamento Task); questo documento generalizza i **tipi** di richiesta e di evento e il loro percorso attraverso i layer, incluse le forme che 03 non copre (letture, orchestrazioni multi-modulo, le eccezioni online).

## 1. Tassonomia delle richieste

| Tipo di richiesta | Esempio | Layer coinvolti | Tocca la rete? |
|---|---|---|---|
| **Lettura semplice** | Aprire il dettaglio di un Task | L1 → L2 → L3 (sola lettura) | Mai |
| **Comando su una singola entità** | Creare/modificare/eliminare un'entità (Generic Entity Flow, [UX Bible MUC §9](../ux_bible/00-modello-ux-comune.md#9-il-flusso-generico-del-ciclo-di-vita-di-unentità-generic-entity-flow--gef)) | L1 → L2 → L3 (dominio proprietario) → L4 (persistenza, sync, ricerca) | Mai per l'esecuzione; L4 accoda per la sync differita |
| **Orchestrazione multi-modulo (lettura)** | Composizione della Home (HOME-001/002), Ricerca globale (SRCH-001) | L1 → L2 (interroga più L3 in sola lettura, compone) | Mai |
| **Orchestrazione multi-modulo (comando)** | Collegare un contributo a un Obiettivo (GOAL-002): scrive nel Grafo (L4), non nei due moduli coinvolti | L1 → L2 → L4 (GraphLink) → eventi verso L3 coinvolti | Mai |
| **Richiesta con eccezione online** | Registrazione account, ripristino da cloud, acquisto, download modulo on-demand (MFC §3) | L1 → L2 → L4 → L5 → L6 | Sì, dichiarata esplicitamente come eccezione |
| **Richiesta di sistema (non iniziata dall'utente)** | Esecuzione di una ricorrenza scaduta (`core.day.changed`), sincronizzazione opportunistica | L4 (Bus/Motore Sync) → L3 | Solo per la sync differita, mai per l'esecuzione |

## 2. Lifecycle generico di una richiesta (comando su singola entità)

```
1. INTENZIONE — L1 cattura l'azione dell'utente (tocco, gesto — UX Bible §8)
                nessuna decisione qui: solo raccolta dell'intento
2. INVOCAZIONE — L1 invoca il caso d'uso esposto da L2, con i soli dati
                necessari (mai lo stato interno di L1 passato a L3)
3. INSTRADAMENTO — L2 identifica il modulo (L3) proprietario e invoca
                    il suo caso d'uso, senza logica di dominio propria
4. VALIDAZIONE — L3 verifica gli invarianti (Data Model Bible) e le
                  regole di business (R-* del modulo) PRIMA di ogni
                  effetto collaterale
5. EFFETTO — L3 applica la modifica al proprio stato, tramite la porta
              di persistenza (L4/L5): scrittura locale-prima (MFC-R-01),
              transazionale (MFC-E-02)
6. CONFERMA — L3 restituisce lo stato aggiornato a L2, che lo espone
              come nuovo stato osservabile a L1 — QUESTO è il momento
              in cui la UI mostra il risultato (≤ 50ms percepiti,
              TASK-008)
7. EFFETTI DIFFERITI (asincroni rispetto al passo 6, mai bloccanti per
   l'utente):
   a. L3 pubblica l'evento sul Bus (L4) → §07 di questo documento
   b. Il Motore di Sync (L4) osserva la scrittura e la accoda
   c. Il Servizio di Ricerca (L4) aggiorna la propria proiezione
```

**Regola architetturale**: **il passo 6 (conferma all'utente) non attende mai il passo 7** — è questo disaccoppiamento, non l'assenza di lavoro da fare, che garantisce la latenza percepita dichiarata dalla Functional Bible per ogni comando (es. TASK-008, FIN-001 "≤ 3 tocchi e ≤ 3 s").

## 3. Lifecycle generico di un'orchestrazione multi-modulo (lettura) — esempio Home

```
1. L1 richiede la composizione "Oggi" a L2 (all'apertura app o al cambio
   di stato rilevante — HOME-007: mai un refresh manuale, la
   composizione è reattiva)
2. L2 interroga, in sola lettura e in parallelo logico, ogni modulo
   ATTIVO (verifica contro il Registro Moduli, L4) che dichiara un
   contributo alla Home (§04 §2.3): Attività, Finanze, Abitudini,
   Calendario (se attivi)
3. Ogni L3 restituisce la propria proiezione (card) senza conoscere
   le altre — L2 è l'unico punto che vede la composizione completa
4. L2 compone il risultato secondo l'ordine dichiarato (di default o
   personalizzato dall'utente, HOME-003) e lo espone a L1
5. L1 anima la transizione (skeleton se >300ms, MUC §2 — di norma
   impercettibile poiché tutti i dati sono locali)
```

**Nota architetturale**: questo pattern (fan-out di sola lettura verso i moduli attivi, fan-in in L2) è lo stesso usato per la Ricerca globale (SRCH-001) e per la Revisione settimanale (REV-001) — un solo pattern riutilizzato, non tre implementazioni parallele (P31 applicato all'architettura).

## 4. Lifecycle generico di un evento (generalizza [03 §4](03-event-driven-architecture.md#4-lifecycle-di-un-evento-attraverso-i-layer))

Aggiunge qui le garanzie e i casi limite non coperti in 03:

| Garanzia | Descrizione |
|---|---|
| **Ordine di pubblicazione per singolo producer** | Gli eventi dello stesso modulo si consegnano nello stesso ordine in cui sono stati pubblicati |
| **Nessuna garanzia d'ordine tra producer diversi** | Un consumer che dipende da eventi di più moduli deve trattarli come indipendenti (coerente con l'assenza di stato condiviso tra moduli) |
| **Tolleranza all'assenza del producer** | Un consumer sottoscritto a eventi di un modulo disattivato non riceve nulla e non genera errore (MFC §8) — verificato tramite il Registro Moduli prima della distribuzione |
| **Non ripetibilità volontaria** | Gli eventi non sono un log persistente da poter "ri-processare": rappresentano notifiche di un fatto già persistito nel Dominio; se un consumer perde un evento (es. crash a metà elaborazione), la coerenza si ristabilisce rileggendo lo stato dal Dominio proprietario al riavvio, non ri-inviando l'evento (il Dominio è sempre la fonte di verità, l'evento è solo un trigger di reazione) |
| **Rigenerazione post-sincronizzazione** | Dopo un merge multi-dispositivo (§[05 §3](05-offline-first-sincronizzazione-caching.md)), gli eventi rilevanti si rigenerano localmente sul dispositivo che ha ricevuto il merge, confrontando lo stato pre e post convergenza — mai trasportati come eventi di rete (MFC §8) |

## 5. Comunicazione tra moduli: riepilogo dei percorsi ammessi

Coerente con [02 §3-4](02-moduli-responsabilita-boundaries.md) e [03 §7](03-event-driven-architecture.md#7-comunicazione-tra-moduli-solo-due-canali-mai-un-terzo): ogni richiesta o evento che attraversa un confine di modulo passa per **uno dei due soli canali** (Bus Eventi per i fatti, GraphLink per le relazioni persistenti) o per **L2 come orchestratore in sola lettura** (mai in scrittura diretta su un altro Dominio). Non esistono altri percorsi legittimi — questo è l'invariante architetturale più importante di questo documento.

---

*Prossimo: [Gestione degli Errori](07-gestione-errori.md)*
