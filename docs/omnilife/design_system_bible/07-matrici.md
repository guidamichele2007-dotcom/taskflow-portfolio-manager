# 07 · Matrici del Design System

> Le quattro matrici richieste. Fonti: [05](05-componenti-navigazione.md), [06](06-componenti-contenuto.md), [04](04-stati-e-accessibilita-visiva.md), [03](03-motion.md).

## 1. Matrice Componenti → Moduli

*(Un componente della libreria è condiviso per costruzione — DS-INV-06 — quindi ogni riga elenca i moduli che lo usano più intensamente, non un "possesso" esclusivo, che non esiste.)*

| Componente | Moduli che lo usano |
|---|---|
| CMP-TABBAR | Core (trasversale a tutti) |
| CMP-TOPBAR | Tutti i moduli con vista L1/L2 |
| CMP-FAB | Cattura (trasversale a tutti) |
| CMP-SHEET | Attività, Finanze, Abitudini, Calendario, Note, Obiettivi (dettaglio entità) |
| CMP-DIALOG | Impostazioni/Sicurezza (cancellazione account, disconnessione dispositivo), Attività/Finanze/Note (eliminazione di massa) |
| CMP-MODAL | Core (Onboarding, Revisione), Finanze (Import), Impostazioni (Cancellazione account, Ripristino) |
| CMP-SNACKBAR | Tutti i moduli (undo su ogni GEF) |
| CMP-SEGMENT | Attività (Oggi/Prossimi/Tutti), Calendario (Giorno/Settimana/Mese) |
| CMP-RIGA-ENTITA | Attività, Finanze, Abitudini, Note, Obiettivi, Ricerca (risultati) |
| CMP-CARD | Core/Home, Widget, Galleria moduli |
| CMP-CHIP | Cattura (parsing), Finanze (categorie/importi rapidi), Ricerca (filtri) |
| CMP-PROGRESS | Finanze (budget), Abitudini (aderenza), Obiettivi (progresso aggregato) |
| CMP-COMPLETION | Attività (task/sottotask), Abitudini, Note (checklist) |
| CMP-BADGE | Cattura (Inbox) — esclusivo |
| CMP-BANNER | Core/Home (insight), Impostazioni (stato backup/trial) |
| CMP-SEARCH | Ricerca, Finanze (storico) |
| CMP-PULSANTE | Trasversale a ogni modulo |
| CMP-TOGGLE | Impostazioni/Sicurezza |
| CMP-SKELETON | Trasversale (ogni caricamento >300ms) |
| CMP-EMPTY | Trasversale (ogni modulo al primo uso o senza risultati) |
| CMP-AVATAR | Profilo |

## 2. Matrice Componenti → Stati

*(Stati generici da [04 §1](04-stati-e-accessibilita-visiva.md); "—" = stato non applicabile al componente.)*

| Componente | default | in_evidenza | premuto | selezionato | disabilitato | in_errore | in_caricamento | vuoto |
|---|---|---|---|---|---|---|---|---|
| CMP-TABBAR | ✓ | — | — | ✓ | — | — | — | — |
| CMP-TOPBAR | ✓ | — | — | — | ✓ (azioni) | — | — | — |
| CMP-FAB | ✓ | — | ✓ | — | — | — | — | — |
| CMP-SHEET | ✓ | — | — | — | — | — | ✓ | — |
| CMP-DIALOG | ✓ | — | — | — | — | — | — | — |
| CMP-MODAL | ✓ | — | — | — | — | — | ✓ | — |
| CMP-SNACKBAR | ✓ | — | — | — | — | — | — | — |
| CMP-SEGMENT | ✓ | — | ✓ | ✓ | — | — | — | — |
| CMP-RIGA-ENTITA | ✓ | ✓ | ✓ | ✓ | — | — | — | — |
| CMP-CARD | ✓ | — | — | — | — | — | ✓ | ✓ |
| CMP-CHIP | ✓ | — | ✓ | ✓ | ✓ | — | — | — |
| CMP-PROGRESS | ✓ | — | — | — | — | — | — | — |
| CMP-COMPLETION | ✓ | ✓ | ✓ | — | ✓ | — | — | — |
| CMP-BADGE | ✓ | — | — | — | — | — | — | ✓ (assente) |
| CMP-BANNER | ✓ | — | — | — | — | — | — | — |
| CMP-SEARCH | ✓ | ✓ | — | — | — | — | ✓ | ✓ |
| CMP-PULSANTE | ✓ | ✓ | ✓ | — | ✓ | — | ✓ | — |
| CMP-TOGGLE | ✓ | ✓ | — | ✓ (on) | ✓ | — | — | — |
| CMP-SKELETON | ✓ | — | — | — | — | — | ✓ (è lo stato stesso) | — |
| CMP-EMPTY | — | — | — | — | — | ✓ (variante) | — | ✓ (è lo stato stesso) |
| CMP-AVATAR | ✓ | — | — | — | — | — | — | ✓ (iniziali) |
| *(Campo di testo, forme incluse in CMP-PULSANTE/CMP-SEARCH per composizione)* | ✓ | ✓ | — | — | ✓ | ✓ | — | ✓ |

## 3. Matrice Componenti → Accessibilità

| Componente | Requisito di accessibilità primario | Rif. |
|---|---|---|
| CMP-TABBAR | Stato selezionato mai dal solo colore | DS-08 |
| CMP-TOPBAR | Titolo primo nell'ordine di lettura | §[05](05-componenti-navigazione.md) |
| CMP-FAB | Sempre raggiungibile via tastiera/screen reader | §[05](05-componenti-navigazione.md) |
| CMP-SHEET | Focus spostato automaticamente all'apertura | §[05](05-componenti-navigazione.md) |
| CMP-DIALOG | Focus intrappolato, titolo annunciato per primo | §[05](05-componenti-navigazione.md) |
| CMP-MODAL | Annuncio del passo corrente | §[05](05-componenti-navigazione.md) |
| CMP-SNACKBAR | Live region non invasiva, azione raggiungibile da tastiera | §[05](05-componenti-navigazione.md) |
| CMP-SEGMENT | Ruolo di gruppo di selezione singola | §[05](05-componenti-navigazione.md) |
| CMP-RIGA-ENTITA | Etichetta composita singola, azioni swipe con equivalente menu | §[06](06-componenti-contenuto.md) |
| CMP-CARD | Intestazione annunciata prima del contenuto | §[06](06-componenti-contenuto.md) |
| CMP-CHIP | Ruolo di selezione esposto | §[06](06-componenti-contenuto.md) |
| CMP-PROGRESS | Valore letto come testo, mai dedotto dal grafico | §[06](06-componenti-contenuto.md), DS-INV-05 |
| CMP-COMPLETION | Annuncio esplicito del cambio di stato | §[06](06-componenti-contenuto.md) |
| CMP-BADGE | Annunciato come parte dell'etichetta dell'icona ospite | §[06](06-componenti-contenuto.md) |
| CMP-BANNER | Non interrompe il focus corrente | §[06](06-componenti-contenuto.md) |
| CMP-SEARCH | Annuncio non invasivo del numero di risultati | §[06](06-componenti-contenuto.md) |
| CMP-PULSANTE | Target minimo anche per varianti solo-icona | DS-06, DS-34 |
| CMP-TOGGLE | Stato on/off annunciato esplicitamente | §[06](06-componenti-contenuto.md) |
| CMP-SKELETON | Annunciato come "caricamento", mai elemento per elemento | §[06](06-componenti-contenuto.md) |
| CMP-EMPTY | Illustrazione ignorata da screen reader, testo/azione sempre annunciati | §[06](06-componenti-contenuto.md) |
| CMP-AVATAR | Etichetta descrittiva ("Foto profilo di…") | §[06](06-componenti-contenuto.md) |

## 4. Matrice Componenti → Motion

| Componente | Token di motion primario | Token secondario |
|---|---|---|
| CMP-TABBAR | `motion.micro` | — |
| CMP-TOPBAR | `motion.micro` | — |
| CMP-FAB | `motion.standard` (apertura foglio) | `motion.scala.pressione` |
| CMP-SHEET | `motion.standard` / `motion.uscita` | `motion.elevazione.transizione` |
| CMP-DIALOG | `motion.enfasi` / `motion.uscita` | — |
| CMP-MODAL | `motion.standard` | — |
| CMP-SNACKBAR | `motion.standard` (ingresso) | `motion.uscita` (scadenza) |
| CMP-SEGMENT | `motion.micro` | — |
| CMP-RIGA-ENTITA | `motion.micro` (completamento) | `motion.uscita` (eliminazione) |
| CMP-CARD | `motion.elevazione.transizione` | `motion.progressione` |
| CMP-CHIP | `motion.micro` | — |
| CMP-PROGRESS | `motion.progressione` | — |
| CMP-COMPLETION | `motion.micro` | — |
| CMP-BADGE | `motion.micro` | — |
| CMP-BANNER | `motion.standard` (ingresso) | `motion.uscita` (archiviazione) |
| CMP-SEARCH | `motion.micro` | — |
| CMP-PULSANTE | `motion.scala.pressione` | — |
| CMP-TOGGLE | `motion.micro` | — |
| CMP-SKELETON | `motion.shimmer` | — |
| CMP-EMPTY | `motion.standard` (ingresso) | — |
| CMP-AVATAR | — | — |

---

*Prossimo: [Report Finale](08-report.md)*
