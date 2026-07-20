# 06 · Component Library — Contenuto, Dati e Form

> Eredita [00](00-fondamenta.md)…[05](05-componenti-navigazione.md). Stesso formato compatto. Il componente più importante di questo documento è **Riga Entità**: l'"anatomia sola" (P33) condivisa da Task, Transazione, Abitudine, Nota, Obiettivo.

## CMP-RIGA-ENTITA · Riga Entità

| Campo | Specifica |
|---|---|
| Scopo | Rappresentazione di lista unica per ogni tipo di entità del grafo (Data Model Bible) — un'anatomia sola (P33) |
| Anatomia | [Controllo di completamento] + Titolo (`corpo.enfatizzato`) + [metadati secondari, `didascalia`] + [chip di stato/categoria] |
| Proprietà | Presenza del controllo di completamento (Task/Abitudine/Checklist: sì; Transazione/Nota: no); metadati configurabili per tipo |
| Varianti | Task · Transazione · Abitudine (con indicatore di progresso quantitativo) · Nota · Obiettivo (con fascia aggregata) |
| Stati | `default`, `premuto`, `selezionato` (modalità selezione multipla, azioni di massa), `completato` (`opacita.completato`), `in_sospeso`/scaduto (indicatore neutro, mai rosso — P46) |
| Comportamento | Tocco → apre CMP-SHEET; swipe → azione rapida (elimina/completa/posticipa); long-press → menu contestuale (eredita GEF, [UX Bible MUC §9](../ux_bible/00-modello-ux-comune.md)) |
| Accessibilità | Ogni riga è un singolo elemento accessibile con etichetta composita ("Task, Chiamare il commercialista, scade venerdì"); le azioni di swipe hanno equivalente nel menu "···" |
| Motion | `motion.micro` sul completamento (checkbox + barratura), `motion.uscita` sull'eliminazione |
| Vincoli | Stessa anatomia in ogni modulo — nessuna variante strutturale che aggiunga elementi non previsti nell'elenco Anatomia (DS-INV-06) |
| Consentito | Aggiungere un chip di stato specifico di dominio (es. soglia budget) come estensione dei "metadati secondari" |
| Vietato | Introdurre un'anatomia diversa per un nuovo tipo di entità futuro senza passare per l'estensione di questo componente (DS-INV-04) |

## CMP-CARD · Card (Home / Widget)

| Campo | Specifica |
|---|---|
| Scopo | Proiezione aggregata di un modulo nella Home o nel widget (HOME-002) |
| Anatomia | Titolo di sezione + fino a 5 Righe Entità o contenuto equivalente + [azione "vedi tutto"] |
| Proprietà | Numero massimo di elementi: 5 (HOME-001 scheda estesa) |
| Varianti | Card di lista (Attività/Abitudini) · Card di stato singolo (Budget, Salute) · Card vuota positiva ("nessun impegno oggi") |
| Stati | `default`, `in_caricamento` (skeleton se >300ms), `vuoto` (positivo, mai triste) |
| Comportamento | Riordino manuale via pressione lunga (HOME-003); azioni dirette senza cambiare schermata (HOME-004) |
| Accessibilità | Intestazione di sezione annunciata prima del contenuto; l'azione "vedi tutto" è sempre l'ultimo elemento del gruppo |
| Motion | `motion.elevazione.transizione` su hover/riordino; `motion.progressione` per contenuti numerici (es. budget) |
| Vincoli | `elevazione.1` (§[01 §4](01-token-visivi.md)); mai contenuto promozionale o sponsorizzato (C-art. 157, HOME-R-02) |
| Consentito | Un'azione diretta per riga (spunta/completa) |
| Vietato | Superare 5 elementi (raggrupamento per conteggio oltre soglia, HOME-001 caso limite) |

## CMP-CHIP · Chip

| Campo | Specifica |
|---|---|
| Scopo | Selezione rapida (categoria, data, priorità), filtro, o valore ricorrente (CAPT-005, FIN-014) |
| Anatomia | [Icona] + Testo breve (`etichetta`) + [azione di rimozione, solo variante filtro] |
| Proprietà | Rimovibile (filtri attivi) o non rimovibile (selettore) |
| Varianti | Selezione singola (categoria) · Filtro attivo (rimovibile) · Suggerimento rapido (chip di valore recente) |
| Stati | `default`, `selezionato`, `disabilitato` |
| Comportamento | Tocco su chip di parsing (CAPT-005) apre selettore a comparsa con max 3 opzioni + "altro" |
| Accessibilità | Ruolo di selezione (singola o multipla secondo contesto) esposto a screen reader |
| Motion | `motion.micro` sulla selezione |
| Vincoli | `raggio.piccolo` o `raggio.pieno` (pillola); mai usato come pulsante di azione primaria |
| Consentito | Gruppi di chip in scroll orizzontale per liste lunghe di opzioni |
| Vietato | Sostituire un campo di testo libero quando l'input non è enumerabile |

## CMP-PROGRESS · Indicatore di Progresso

| Campo | Specifica |
|---|---|
| Scopo | Rappresentare visivamente un valore derivato (aderenza abitudine, budget, progresso obiettivo — Data Model Bible §11) |
| Anatomia | Traccia (`raggio.pieno` per anello, `raggio.medio` per barra) + riempimento proporzionale + [valore testuale] |
| Proprietà | Forma: anello (abitudini, obiettivi) · barra (budget); il valore testuale è **sempre presente** (mai il solo grafico, DS-INV-05) |
| Varianti | Determinato (percentuale nota) · Fascia verbale (aderenza/obiettivo: "in ritmo" prima che numerica, P29/GOAL-003) |
| Stati | `default` (in corso), `attenzione` (`stato.attenzione`, mai critico), `completo` (`stato.positivo_sobrio`) |
| Comportamento | Il riempimento si anima con `motion.progressione`, mai un salto diretto |
| Accessibilità | Valore letto come testo ("82% del budget Alimentari"), non dedotto dal solo angolo/lunghezza del riempimento |
| Motion | `motion.progressione` |
| Vincoli | **Mai** un quarto livello di stato oltre i 3 di §Stati (P48: mai rosso-allarme) |
| Consentito | Combinare fascia verbale + valore nativo (GOAL-003: "ogni fronte mostra il proprio asse") |
| Vietato | Un punteggio aggregato opaco senza spiegazione disponibile (C-art. 6) |

## CMP-COMPLETION · Controllo di Completamento

| Campo | Specifica |
|---|---|
| Scopo | Spunta di Task/Sottotask/Checklist/Abitudine binaria |
| Anatomia | Forma circolare o quadrata (coerenza per tipo, mai mista nello stesso contesto) + segno di spunta animato |
| Proprietà | Target di tocco esteso oltre la dimensione visiva (DS-35) |
| Varianti | Binario (fatto/non fatto) · Incrementale (abitudine quantitativa, avanza di uno step per tocco) |
| Stati | `default`, `completato`, `disabilitato` (entità in sola lettura) |
| Comportamento | 1 tocco, ovunque l'entità appaia (Riga, Card, widget, notifica — TASK-008) |
| Accessibilità | Annuncio esplicito del cambio di stato ("Completato: [titolo]") |
| Motion | `motion.micro`, stroke della spunta animato (non un'icona che appare di scatto) |
| Vincoli | Mai un dialogo di conferma prima della spunta (P2, undo dopo) |
| Consentito | — |
| Vietato | Usarlo per azioni non binarie/incrementali (es. non è un pulsante generico) |

## CMP-BADGE · Badge

| Campo | Specifica |
|---|---|
| Scopo | Segnalare lavoro dell'utente in attesa (Inbox) — **unico uso ammesso** |
| Anatomia | Contenitore `raggio.pieno` piccolo + numero |
| Proprietà | Numerico, mai un punto generico senza numero |
| Varianti | Nessuna |
| Stati | `default`, assente (0 elementi = badge non renderizzato, mai "0" visibile) |
| Comportamento | Aggiornamento reattivo al variare del conteggio Inbox |
| Accessibilità | Annunciato come parte dell'etichetta dell'icona che lo ospita ("Moduli, 3 elementi in attesa") |
| Motion | `motion.micro` sul cambio di valore |
| Vincoli | **Vietato ovunque tranne l'Inbox** (CAPT §4: "unico badge ammesso: rappresenta lavoro dell'utente, non nostro engagement") |
| Consentito | Solo il conteggio Inbox |
| Vietato | Contatori di notifiche non lette, contatori social, badge "novità" di marketing (C-art. 222) |

## CMP-BANNER · Banner di Stato/Insight

| Campo | Specifica |
|---|---|
| Scopo | Comunicazione contestuale non bloccante (insight, HOME-005; stato trial, solo in Impostazioni) |
| Anatomia | [Icona] + Testo breve + [azione] + azione di archiviazione |
| Proprietà | Max 1 per giorno per tipo insight (HOME-005) |
| Varianti | Insight · Informativo di sistema (es. "backup non recente") |
| Stati | `default` |
| Comportamento | Mai modale, sempre archiviabile con un tocco |
| Accessibilità | Annunciato come contenuto informativo, non interrompe il focus corrente |
| Motion | `motion.standard` in ingresso, `motion.uscita` all'archiviazione |
| Vincoli | Mai contenuto commerciale (INS-R-02, C-art. 64) |
| Consentito | Un solo banner visibile per schermata |
| Vietato | Banner impilati; banner persistente oltre la sua rilevanza |

## CMP-SEARCH · Campo di Ricerca

| Campo | Specifica |
|---|---|
| Scopo | Ricerca globale o filtrata (SRCH-001) |
| Anatomia | Icona di ricerca + campo di testo + [azione di cancellazione] |
| Proprietà | Risultati incrementali (nessun pulsante "cerca" da premere) |
| Varianti | Globale (tab Cerca) · Contestuale (dentro un modulo, es. storico Finanze) |
| Stati | `default`, `in_evidenza` (focus attivo), `vuoto`/nessun risultato (distinto, DS-12 equivalente testuale) |
| Comportamento | Suggerisce le ricerche recenti quando vuoto (SRCH-004) |
| Accessibilità | Annuncio del numero di risultati trovati ad ogni digitazione (non invasivo) |
| Motion | `motion.micro` |
| Vincoli | Mai un secondo campo di ricerca visibile contemporaneamente |
| Consentito | Chip di filtro adiacenti (CMP-CHIP) |
| Vietato | Query libere non filtrabili (P16: filtri finiti, mai un costruttore di query) |

## CMP-PULSANTE · Pulsante (incl. varianti a icona)

| Campo | Specifica |
|---|---|
| Scopo | Azione esplicita primaria/secondaria in form e flussi guidati |
| Anatomia | [Icona] + [Testo] — almeno uno dei due sempre presente |
| Proprietà | Peso visivo: primario (riempito, `accento.base`) · secondario (contorno) · testuale (solo testo) |
| Varianti | Con testo · Solo icona (con etichetta accessibile obbligatoria, DS-07) |
| Stati | `default`, `premuto`, `disabilitato`, `in_caricamento` (raro: solo per le 4 eccezioni online, MFC §3) |
| Comportamento | Un solo pulsante primario per schermata/passo (evita ambiguità di azione, P28) |
| Accessibilità | Target minimo anche per varianti solo-icona (DS-06) |
| Motion | `motion.scala.pressione` |
| Vincoli | Il pulsante primario non è mai l'azione distruttiva in un CMP-DIALOG (§[05](05-componenti-navigazione.md)) |
| Consentito | Combinazione icona+testo per azioni ambigue senza testo |
| Vietato | Più di un pulsante primario per vista |

## CMP-TOGGLE · Interruttore (Switch)

| Campo | Specifica |
|---|---|
| Scopo | Impostazioni booleane (catalogo chiuso, SET-001 §2) |
| Anatomia | Traccia + cursore |
| Proprietà | Effetto immediato (SET-R-02: nessun "salva/applica") |
| Varianti | Nessuna |
| Stati | `default` (on/off), `disabilitato` |
| Comportamento | Cambio istantaneo, reversibile |
| Accessibilità | Stato on/off annunciato esplicitamente, mai dedotto dalla sola posizione del cursore |
| Motion | `motion.micro` |
| Vincoli | Riservato alle voci del catalogo Impostazioni chiuso (SET-R-01) |
| Consentito | — |
| Vietato | Introdurlo per impostazioni non enumerate nel catalogo |

## CMP-SKELETON · Skeleton Loader

| Campo | Specifica |
|---|---|
| Scopo | Stato di caricamento esplicito >300ms ([MUC §2](../ux_bible/00-modello-ux-comune.md)) |
| Anatomia | Forme grigie che rispecchiano la UI reale in arrivo |
| Proprietà | Forma specifica per contesto (mai un blocco generico) |
| Varianti | Skeleton di Riga Entità · Skeleton di Card |
| Stati | `default` (unico stato: cessa al termine del caricamento) |
| Comportamento | Sostituito dal contenuto reale senza salto di layout (C-art. 113) |
| Accessibilità | Annunciato come "caricamento in corso", mai letto elemento per elemento |
| Motion | `motion.shimmer` |
| Vincoli | Mai uno spinner a schermo pieno (MUC §2) |
| Consentito | — |
| Vietato | Persistere oltre il tempo reale di attesa dei dati locali (di norma impercettibile) |

## CMP-EMPTY · Blocco di Stato Vuoto

| Campo | Specifica |
|---|---|
| Scopo | Anatomia fissa a 3 elementi per ogni variante di vuoto ([UX Bible MUC §6](../ux_bible/00-modello-ux-comune.md), [09-empty-states](../ux_bible/09-empty-states.md)) |
| Anatomia | Illustrazione/icona (§[02 §2](02-linguaggio-visivo.md)) + frase orientata al beneficio + azione primaria |
| Proprietà | Variante interattiva con dati di esempio (primo uso di modulo) |
| Varianti | Mai usato (didattico) · Filtrato (nessun risultato) · Errore (rimando a [10-error-experience](../ux_bible/10-error-experience.md)) |
| Stati | Nessuno stato interno (è esso stesso uno stato di un altro componente) |
| Comportamento | L'azione primaria porta sempre a cattura o alla funzione rilevante |
| Accessibilità | Illustrazione decorativa (ignorata da screen reader), testo e azione sempre annunciati |
| Motion | Ingresso con `motion.standard` |
| Vincoli | Le 3 varianti non condividono mai la stessa illustrazione (DS-12) |
| Consentito | — |
| Vietato | Pagina bianca senza i 3 elementi |

## CMP-AVATAR · Avatar / Immagine Profilo

| Campo | Specifica |
|---|---|
| Scopo | Immagine profilo opzionale (PROF-001) |
| Anatomia | Forma `raggio.pieno` con immagine o iniziali |
| Proprietà | Facoltativo — default a iniziali, mai un'immagine generica stock |
| Varianti | Immagine locale · Iniziali |
| Stati | `default` |
| Comportamento | Solo locale, mai un profilo pubblico (C-art. 55) |
| Accessibilità | Etichetta "Foto profilo di [nome]" o "Iniziali" |
| Motion | Nessuna |
| Vincoli | Dimensione fissa per contesto (Impostazioni vs. eventuali spazi condivisi futuri) |
| Consentito | — |
| Vietato | Uso come identificatore visivo pubblico/condiviso (nessuna funzione di condivisione sociale nel prodotto, C-art. 55) |

---

*Prossimo: [Matrici](07-matrici.md)*
