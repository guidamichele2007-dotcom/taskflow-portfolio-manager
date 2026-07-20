# 05 · Component Library — Navigazione e Struttura

> Eredita [00](00-fondamenta.md)…[04](04-stati-e-accessibilita-visiva.md). Formato compatto: una tabella per componente. Ogni comportamento citato rimanda alla [UX Bible Navigation Bible](../ux_bible/02-navigation-bible.md) — non ridefinito, solo tradotto in specifica visiva.

## CMP-TABBAR · Tab Bar (Bottom Navigation)

| Campo | Specifica |
|---|---|
| Scopo | Le 4 destinazioni di primo livello sempre raggiungibili |
| Anatomia | Contenitore fisso + 4 slot icona+etichetta |
| Proprietà | Numero di slot: **fisso a 4**, mai variabile (eredita [UX Bible §1](../ux_bible/02-navigation-bible.md): la tab bar non cambia con i moduli attivi) |
| Varianti | Nessuna — un solo aspetto in tutto il prodotto (DS-INV-06) |
| Stati | `default`, `selezionato` (tab corrente) |
| Comportamento | Pressione lunga su "Moduli" aggiunge un badge di scorciatoia (design doc UX §1), non altera il numero di slot |
| Accessibilità | Ogni slot ≥ target minimo (§[04 §4](04-stati-e-accessibilita-visiva.md)); stato selezionato mai comunicato dal solo colore (icona piena vs. contorno) |
| Motion | `motion.micro` sul cambio di selezione |
| Vincoli | Sempre visibile su L0/L1, eccetto flussi a schermo intero (Onboarding, Revisione, Import, Cancellazione account — [UX Bible §1](../ux_bible/02-navigation-bible.md)) |
| Consentito | Solo le 4 destinazioni canoniche |
| Vietato | Badge numerici di vanità; icone che cambiano significato tra contesti (UX-C-137) |

## CMP-TOPBAR · Top Bar

| Campo | Specifica |
|---|---|
| Scopo | Titolo di contesto + azioni contestuali di una vista L1/L2 |
| Anatomia | [Freccia indietro (solo L2)] + Titolo + [max 2 icone di azione] |
| Proprietà | Presenza della freccia indietro, numero di azioni (0-2) |
| Varianti | L1 (senza freccia) · L2/dettaglio (con freccia) |
| Stati | `default`; azioni contestuali in `disabilitato` quando non applicabili (mai nascoste, [UX Bible §2](../ux_bible/02-navigation-bible.md)) |
| Comportamento | Si nasconde durante lo scroll immersivo di contenuti lunghi, riappare allo scroll verso l'alto |
| Accessibilità | Il titolo è sempre annunciato per primo nell'ordine di lettura della schermata |
| Motion | `motion.micro` per mostra/nascondi |
| Vincoli | Mai più di 2 azioni contestuali (P28, non affollare) |
| Consentito | Azioni dirette alla vista corrente (filtro, impostazioni di modulo) |
| Vietato | Azioni di navigazione verso Home (il back è sempre relativo, mai un salto a Home — UX-C-118) |

## CMP-FAB · Pulsante di Cattura (FAB)

| Campo | Specifica |
|---|---|
| Scopo | Cattura universale sempre raggiungibile ([UX Bible §7](../ux_bible/02-navigation-bible.md)) |
| Anatomia | Cerchio (`raggio.pieno`) con icona centrale |
| Proprietà | Posizione fissa (basso, sopra la tab bar) |
| Varianti | Nessuna variante di forma; il contenuto del foglio che apre varia, non il pulsante |
| Stati | `default`, `premuto` |
| Comportamento | Tocco → foglio di cattura; pressione lunga → menu radiale di scorciatoie (CAPT-007) |
| Accessibilità | Etichetta "Cattura rapida", sempre raggiungibile via tastiera/screen reader indipendentemente dallo scroll della schermata sottostante |
| Motion | Apertura del foglio con `motion.standard`, origin-aware dal punto del FAB |
| Vincoli | Un solo FAB per schermata, sempre lo stesso significato (mai riutilizzato per un'azione diversa dalla cattura) |
| Consentito | — |
| Vietato | Nascondersi durante lo scroll di liste (si nasconde solo nei Modal a scopo unico, [UX Bible §4](../ux_bible/02-navigation-bible.md), e durante digitazione a schermo intero) |

## CMP-SHEET · Foglio (Bottom Sheet)

| Campo | Specifica |
|---|---|
| Scopo | Dettaglio di un'entità (GEF) e selettori, mantenendo il contesto della lista sottostante |
| Anatomia | Maniglia di trascinamento + contenuto + [azione di chiusura] |
| Proprietà | Altezza adattiva al contenuto, espandibile a schermo intero (editor lunghi, es. Note) |
| Varianti | Dettaglio entità (GEF) · Selettore (categoria/data/condivisione) |
| Stati | `default`, `in_caricamento` (raro, dati locali) |
| Comportamento | Swipe verso il basso o tocco fuori chiude senza side-effect (eredita [UX Bible MUC §9](../ux_bible/00-modello-ux-comune.md)) |
| Accessibilità | Focus spostato automaticamente al contenuto del foglio all'apertura; annuncio di apertura per screen reader |
| Motion | `motion.standard` in apertura (espansione dal punto di tocco), `motion.uscita` in chiusura + `motion.elevazione.transizione` |
| Vincoli | **Mai più di un foglio aperto contemporaneamente** (eredita [UX Bible Navigation Bible §5](../ux_bible/02-navigation-bible.md)) |
| Consentito | Sostituire temporaneamente il proprio contenuto con una transizione orizzontale per un selettore secondario (mai un secondo foglio impilato) |
| Vietato | Contenere una seconda tab bar o un secondo FAB |

## CMP-DIALOG · Dialogo

| Campo | Specifica |
|---|---|
| Scopo | Conferma di azioni **irreversibili** — riservato, mai generico |
| Anatomia | Titolo che dichiara la conseguenza + [testo esplicativo] + max 2 azioni |
| Proprietà | Azione distruttiva mai preselezionata |
| Varianti | Conferma singola (2 azioni) · Conferma con testo da digitare (volumi grandi, eliminazione definitiva) |
| Stati | `default` |
| Comportamento | Compare **solo** per: eliminazione definitiva, cancellazione account, disconnessione dispositivo, azioni di blocco >20 elementi ([UX Bible Navigation Bible §6](../ux_bible/02-navigation-bible.md)) |
| Accessibilità | Focus intrappolato nel dialogo finché non risolto; titolo annunciato per primo |
| Motion | `motion.enfasi` in apertura (rarità = enfasi maggiore), `motion.uscita` in chiusura |
| Vincoli | **Vietato per ogni azione reversibile** (UX-R-008) — l'uso improprio svaluta il peso semantico del componente |
| Consentito | Le 4 categorie di §Comportamento, nessun'altra |
| Vietato | "Sei sicuro?" generico; percorsi di trattenimento commerciale (C-art. 163) |

## CMP-MODAL · Modal a Schermo Intero

| Campo | Specifica |
|---|---|
| Scopo | Flussi sequenziali a scopo unico che richiedono concentrazione esclusiva |
| Anatomia | [Indicatore di progresso] + contenuto + azione di uscita esplicita |
| Proprietà | Con o senza progresso (Onboarding/Revisione: sì; Cancellazione account: no) |
| Varianti | Con progresso passo-passo · Singolo scopo (import, cancellazione) |
| Stati | `default`, `in_caricamento` |
| Comportamento | Tab bar e FAB nascosti; uscita sempre disponibile (C-art. 56); stato parziale salvato dove sensato (Revisione, MFC-R-06) |
| Accessibilità | Focus vincolato al modal; annuncio del passo corrente su ogni avanzamento |
| Motion | `motion.standard` in apertura/chiusura (transizione a schermo intero) |
| Vincoli | Riservato a: Onboarding, Revisione settimanale, Import, Cancellazione account, Ripristino da backup |
| Consentito | — |
| Vietato | Usare il Modal per un flusso che potrebbe essere un Foglio (violerebbe la minima invasività, P13) |

## CMP-SNACKBAR · Snackbar (conferma/annulla)

| Campo | Specifica |
|---|---|
| Scopo | Feedback di completamento con possibilità di annullo immediato |
| Anatomia | Testo breve + [azione "Annulla"] |
| Proprietà | Durata fissa 7s (eredita [MUC §2](../ux_bible/00-modello-ux-comune.md), non ridefinita) |
| Varianti | Con azione di annullo · Solo informativa (rara) |
| Stati | `default` |
| Comportamento | Un secondo snackbar sostituisce il primo eseguendo silenziosamente l'azione precedente in coda (MUC §8) |
| Accessibilità | Annunciata come live region non invasiva; l'azione "Annulla" è raggiungibile da tastiera prima dello scadere |
| Motion | `motion.standard` in ingresso dal basso, `motion.uscita` allo scadere |
| Vincoli | Posizione fissa: basso, sopra la tab bar; mai più di uno visibile |
| Consentito | Ogni azione distruttiva o di completamento reversibile |
| Vietato | Usarla per errori bloccanti (quelli usano il pattern di [10-error-experience](../ux_bible/10-error-experience.md), inline) |

## CMP-SEGMENT · Controllo Segmentato

| Campo | Specifica |
|---|---|
| Scopo | Selezione tra viste dello stesso livello (Oggi/Prossimi/Tutti — TASK-012; Giorno/Settimana/Mese — CAL) |
| Anatomia | Contenitore + 2-4 segmenti testuali |
| Proprietà | Numero di segmenti: 2-4 (mai oltre, P28: max 3 opzioni proposte — tollerata la 4ª solo per viste temporali standard già normate) |
| Varianti | Nessuna |
| Stati | `default` per segmento, `selezionato` per il segmento attivo |
| Comportamento | Cambio segmento anche via swipe orizzontale sul contenuto (equivalenza gesto/tocco, UX-C-141) |
| Accessibilità | Ruolo di gruppo di selezione singola per screen reader; il segmento attivo è annunciato |
| Motion | `motion.micro` sullo spostamento dell'indicatore di selezione |
| Vincoli | Mai usato per una scelta con conseguenze distruttive (solo navigazione tra viste) |
| Consentito | Viste temporali/filtri di primo livello dentro un modulo |
| Vietato | Sostituire la tab bar o introdurre un livello di navigazione nascosto |

---

*Prossimo: [Component Library — Contenuto, Dati e Form](06-componenti-contenuto.md)*
