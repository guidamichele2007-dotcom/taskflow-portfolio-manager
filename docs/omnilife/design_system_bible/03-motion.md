# 03 · Motion Principles e Animation Tokens

> Eredita [00-fondamenta](00-fondamenta.md). **I token di durata e curva sono già normativi in [UX Bible MUC §4](../ux_bible/00-modello-ux-comune.md#4-animazione-durate-e-curve-token-condivisi-con-il-design-system-del-doc-tecnico-04) e nelle [Microinterazioni](../ux_bible/07-microinterazioni.md) — non ripetuti qui, solo richiamati.** Il valore aggiunto di questo documento: i token **visivi** di motion (elevazione in transizione, shimmer di caricamento) che la UX Bible non definisce, perché appartengono al sistema di design, non al comportamento.

## 1. Motion Principles (eredità diretta)

| Principio | Fonte |
|---|---|
| Le animazioni comunicano relazione spaziale o di stato, mai decorazione | P57, UX-C-096 |
| Ogni animazione ≤ 350ms | [UX Bible MUC §4](../ux_bible/00-modello-ux-comune.md) |
| Ogni animazione ha una variante statica per "riduci movimento" | UX-C-191, DS-23 |
| Le celebrazioni durano max 800ms, mai bloccanti | P45, UX Bible §7 Microinterazioni |

## 2. Animation Tokens (richiamo tabellare, fonte UX Bible MUC §4 — non ridefiniti)

| Token | Durata | Curva | Uso tipico |
|---|---|---|---|
| `motion.micro` | 100-150ms | ease-out | Tocchi, spunte, piccoli cambi di stato |
| `motion.standard` | 200-250ms | spring morbido (damping alto) | Apertura fogli, transizioni tra viste |
| `motion.enfasi` | 300-350ms | spring con overshoot ≤4% | Completamenti importanti, milestone |
| `motion.uscita` | 150-200ms | ease-in | Chiusure, dismiss, eliminazioni |

*(Tabella identica a [UX Bible MUC §4](../ux_bible/00-modello-ux-comune.md#4-animazione-durate-e-curve-token-condivisi-con-il-design-system-del-doc-tecnico-04): riportata qui per completezza di consultazione della Design System Bible, non come ridefinizione — l'unica fonte di verità per queste durate resta la UX Bible.)*

## 3. Token visivi di motion (nuovi — propri di questa Bible)

Non definiti dalla UX Bible perché appartengono alla resa visiva, non al comportamento osservabile:

| Token | Descrizione | Uso |
|---|---|---|
| `motion.elevazione.transizione` | Variazione di `elevazione` (§[01 §4](01-token-visivi.md)) accompagna sempre `motion.standard`, mai un salto istantaneo di ombra/tono | Apertura di un foglio di dettaglio |
| `motion.shimmer` | Pattern di scorrimento per lo skeleton screen (mai uno spinner, [MUC §2](../ux_bible/00-modello-ux-comune.md)) | Stato di caricamento >300ms |
| `motion.scala.pressione` | Riduzione di scala minima (~2-3%) durante la pressione di un componente interattivo | Feedback tattile visivo su ogni componente premibile |
| `motion.scala.trascinamento` | Aumento di scala minimo (~3%) dell'elemento sollevato durante un drag (UX Bible §8 Gestures) | Riordino manuale |
| `motion.progressione` | Interpolazione del valore (mai un salto diretto al valore finale) per anelli/barre di progresso | Spunta abitudine quantitativa, avanzamento budget |

## 4. Vincoli trasversali

- **DS-28**: nessun componente introduce una durata o curva fuori dai quattro token di §2 più i cinque di §3 — nove token totali coprono ogni caso (DS-INV-02 applicato al motion).
- **DS-29**: `motion.shimmer` e `motion.progressione` sono le due sole animazioni ammesse a ripetersi/durare oltre 350ms **in loop continuo limitato allo stato di attesa** — cessano immediatamente al cambio di stato, mai un'animazione perenne che comunicherebbe un problema costante (coerente con [UX Bible §7.8](../ux_bible/07-microinterazioni.md), pannello sync).
- **DS-30**: ogni token di §3 eredita la regola "riduci movimento" (§[02 §5](02-linguaggio-visivo.md), DS-23): `motion.shimmer` diventa un blocco statico attenuato, `motion.scala.*` si azzera, `motion.progressione` salta direttamente al valore finale.

---

*Prossimo: [Stati e Accessibilità Visiva](04-stati-e-accessibilita-visiva.md)*
