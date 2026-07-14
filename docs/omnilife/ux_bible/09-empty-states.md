# 09 · Empty States

> Eredita [MUC §6](00-modello-ux-comune.md#6-pattern-universale-di-stato-vuoto-eredita-mfc-4). Anatomia fissa a 3 elementi (illustrazione/icona → beneficio → azione). Qui: la variante specifica per ciascuna delle 13 situazioni richieste, applicata trasversalmente a tutte le schermate, con esempi per modulo.

## 1. Nessun dato (mai usato)

**Pattern**: illustrazione sobria + frase orientata al beneficio + azione primaria verso la cattura/creazione. **Esempi**: Attività → "Qui vedrai le cose da fare. Comincia scrivendone una." + `+`; Note → "Le tue idee, in un posto solo." + `+`; Obiettivi → "Trasforma un sogno in un piano." + `+`.
**MFC rif.**: stato "Vuoto" universale.

## 2. Nessun dato (filtrato)

**Pattern**: distinto dal vuoto assoluto — "Nessun risultato per questi filtri" + azzeramento filtri a 1 tocco. **Mai** la stessa illustrazione dello stato "mai usato" (comunicherebbe erroneamente l'assenza totale di dati). Esempio: Ricerca con filtro "Task" + "Alimentari" senza risultati → "Nessun task in questa categoria" + `Rimuovi filtri`.

## 3. Offline

**Pattern**: **non è uno stato vuoto distinto** per i dati locali (identici a online, MUC §10) — si applica SOLO alle 4 funzioni-eccezione (registrazione, ripristino cloud, acquisto, attivazione modulo on-demand non scaricato). Es.: attivazione modulo senza rete → "Questo modulo richiede una connessione una tantum per il download" + icona di rete, azione "Riprova".

## 4. Errore

**Pattern**: rimando completo a [10-error-experience](10-error-experience.md); qui solo la sua manifestazione come "vuoto di errore" quando un'intera sezione non carica: icona neutra (non allarmante) + "Qualcosa non ha funzionato nel caricamento di [sezione]" + `Riprova` + il resto della schermata resta operativo (C-art. 122).

## 5. Prima apertura (onboarding di modulo)

**Pattern**: più ricco dello stato vuoto standard — include un esempio **con dati fittizi interattivi** (non uno screenshot statico) per insegnare l'interazione. Esempio: prima apertura Abitudini → un'abitudine di esempio già presente ("Bevi un bicchiere d'acqua", spuntabile per prova) + invito a crearne una vera.

## 6. Trial

**Pattern**: mai una schermata dedicata — un banner discreto SOLO in Impostazioni > Abbonamento ("Trial Plus: 11 giorni rimanenti"), mai nelle schermate d'uso quotidiano (C-art. 64, MUC/MFC §4). Nessuna icona "PRO" ripetuta sulle funzioni in prova.

## 7. Premium (Plus attivo)

**Pattern**: **assenza totale di badge celebrativi permanenti** — le funzioni Plus sono semplicemente presenti, senza corona/stella/etichetta ricorrente (P160-161, C-art. 64: la differenza si vede nella capacità, non nella decorazione).

## 8. Business/B2B2C (futuro)

**Pattern**: identico a Plus in ogni schermata utente; l'unica differenza visibile è in Impostazioni > Abbonamento ("Gestito da [organizzazione]" senza altri dettagli, C-art. 174).

## 9. Sync (in corso)

**Pattern**: mai una schermata di attesa; indicatore discreto nel pannello di stato (icona che ruota solo mentre attiva, §MUC 10). Se una lista sta ricevendo dati in arrivo (es. subito dopo un ripristino), le righe non ancora arrivate NON mostrano placeholder infiniti: la lista mostra ciò che c'è, con una nota in fondo "altri elementi in arrivo" se pertinente.

## 10. Loading (caricamento)

**Pattern**: skeleton screen (forme grigie della UI reale, mai spinner a tutto schermo) se > 300ms (MUC §2); per i dati locali, quasi sempre impercettibile. Esempio: apertura Home a freddo con 5 moduli → skeleton delle card per max 300-400ms.

## 11. Aggiornamento (dell'app o di un modulo)

**Pattern**: mai un blocco totale; l'aggiornamento di un modulo avviene in background con l'indicatore nella sua scheda in Galleria ("Aggiornamento in corso…"), il modulo resta usabile con la versione precedente finché il nuovo pacchetto non è pronto (mai un'interruzione).

## 12. Recupero (dati/ripristino)

**Pattern**: barra di progresso con stima onesta ("Circa 1 minuto rimanente"), possibilità di usare l'app nel frattempo per i dati già arrivati (FLOW-SYNC-01). Se il recupero fallisce a metà: ripristino riprendibile, mai "ricomincia da zero" silenzioso.

## 13. Conflitto

**Pattern**: **non esiste come stato visibile all'utente** (MFC-R-08 — risolto automaticamente). L'unica traccia è nella Cronologia dell'entità, mai un dialogo "quale versione tieni?" (D-02/ADR-3).

## Stati aggiuntivi rilevanti (oltre i 13 richiesti)

| Stato | Pattern |
|---|---|
| **Elemento archiviato** | Visivamente attenuato (opacità ridotta ~70%), escluso da conteggi e viste attive, etichetta "Archiviato" discreta, azione "Ripristina" a 1 tocco |
| **Elemento eliminato (nel Cestino)** | Visibile solo nella vista Cestino, con "Eliminato il [data] · Scade tra N giorni" e azione "Ripristina" in evidenza |
| **Elemento condiviso** (futuro, spazi familiari) | Indicatore "Condiviso con [nome]" + icona dedicata, mai ambigua rispetto ai dati privati |
| **Sola lettura** (evento calendario esterno, dato salute) | Etichetta esplicita + azione "Apri nella fonte" al posto dei controlli di modifica |
| **Degradato** (es. indice ricerca in ricostruzione) | Funzione ridotta dichiarata + auto-riparazione in background, mai un errore bloccante |
| **Modulo sensibile bloccato** (biometria) | Card/risultati offuscati con pattern visivo di "vetro smerigliato" + icona lucchetto + azione di sblocco a 1 tocco |
| **Free al limite** | Comunicato *prima* del blocco creativo, mai come sorpresa; contenuti esistenti sempre leggibili/esportabili (MFC §4) |

## Principio di coerenza visiva tra stati

**UX-R-020**: i 13+ stati non condividono mai la stessa illustrazione per situazioni concettualmente diverse (vuoto assoluto ≠ vuoto filtrato ≠ errore) — la distinzione visiva immediata evita che l'utente interpreti erroneamente la causa. **UX-R-021**: ogni stato ha un annuncio screen reader dedicato e distinto (accessibilità, C-art. 99).

---

*Prossimo: [Error Experience](10-error-experience.md)*
