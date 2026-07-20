# 04 · Component States (modello generico) e Accessibility Visual Rules

> Eredita [00-fondamenta](00-fondamenta.md)…[03-motion](03-motion.md). Le regole di accessibilità **comportamentale** sono già normative in [UX Bible, Accessibility Bible](../ux_bible/12-accessibility-bible.md) e [UX Constitution Titolo IX](../ux_bible/13-ux-constitution.md) — qui solo le loro **conseguenze visive** (valori di contrasto, token di focus, dimensioni), non ripetute ma quantificate per il sistema.

## 1. Modello generico degli stati di componente

Ogni componente interattivo della libreria (§[05](05-componenti-navigazione.md)-[06](06-componenti-contenuto.md)) espone un sottoinsieme di questi 8 stati generici — mai uno stato non elencato qui (DS-INV-03).

| Stato | Descrizione visiva | Canali (mai uno solo, DS-INV-05) |
|---|---|---|
| `default` | Stato di riposo | — |
| `in_evidenza` (hover/focus non a tocco, tastiera/puntatore esterno) | Bordo di focus visibile (§2) | `bordo.focus` + eventuale variazione tonale |
| `premuto` | Durante l'interazione attiva | `motion.scala.pressione` + variazione tonale minima |
| `selezionato` | Scelto tra alternative (es. chip di categoria, segmento attivo) | Riempimento con `accento.base` + spunta/icona, mai il solo colore |
| `disabilitato` | Non interagibile | `opacita.disabilitato` + rimozione dell'interattività (mai solo visivo) |
| `in_errore` | Validazione fallita (UX Bible §10) | Bordo in `stato.attenzione` (mai `stato.critico`, riservato a perdita dati/sicurezza) + testo di errore inline |
| `in_caricamento` | Dato non ancora disponibile (>300ms) | `motion.shimmer`, mai spinner a schermo pieno |
| `vuoto` | Nessun contenuto (§[UX Bible 09](../ux_bible/09-empty-states.md)) | Illustrazione + testo, mai solo assenza di contenuto |

**DS-31**: uno stato aggiuntivo specifico di dominio (es. "budget in attenzione", "abitudine in pausa") non è un nuovo stato generico: è una variante cromatica/testuale applicata sopra uno di questi 8 — mai un nono stato strutturale.

## 2. Focus Visibility

| Regola | Valore/vincolo |
|---|---|
| **DS-32** | Ogni elemento interattivo ha un indicatore di focus **sempre visibile** quando raggiunto da tastiera/puntatore esterno — eredita [UX-C-195](../ux_bible/13-ux-constitution.md), qui quantificato: spessore `bordo.spessore.focus` (§[01 §5](01-token-visivi.md)), colore `bordo.focus`, mai rimovibile per estetica |
| **DS-33** | L'ordine di focus segue l'ordine di lettura dichiarato in [UX Bible Accessibility §6](../ux_bible/12-accessibility-bible.md) (non ridefinito) |

## 3. Contrasto (valori quantificati, eredità diretta da UX Bible)

| Contesto | Rapporto minimo | Fonte |
|---|---|---|
| Testo normale su superficie | 4.5:1 (AA) | [UX Bible Accessibility §3](../ux_bible/12-accessibility-bible.md), non ridefinito |
| Testo grande / componenti UI (bordi, icone significative) | 3:1 (AA) | idem |
| Verifica indipendente chiaro/scuro | Obbligatoria per ogni coppia di token (DS-26) | idem + [02 §6](02-linguaggio-visivo.md) |
| Modalità alto contrasto di sistema | Varianti dedicate dei token, non un semplice aumento automatico | [UX Bible Accessibility §3](../ux_bible/12-accessibility-bible.md) |

## 4. Target di Tocco

| Regola | Valore |
|---|---|
| **DS-34** | Ogni componente interattivo ≥ 44×44pt / 48×48dp, **inclusi** i controlli dentro widget e notifiche | Eredita [UX Bible Accessibility §4](../ux_bible/12-accessibility-bible.md); i valori esatti in unità fisiche restano decisione di fase UI, il vincolo relativo (rapporto con `spazio.1`, §[01 §1](01-token-visivi.md)) è normativo qui |
| **DS-35** | Icone visivamente più piccole del target minimo (§[02 §1](02-linguaggio-visivo.md), DS-06) hanno comunque un'area di tocco espansa fino al minimo, invisibile ma presente | Nuovo, coerente con DS-34 |

## 5. Riduzione del movimento e Dynamic Type (richiamo, non ridefinizione)

- Ogni token di motion ha equivalente statico (§[03 §4](03-motion.md), DS-30) — non ripetuto.
- Ogni token tipografico regge il 200% di scala con reflow (§[01 §2](01-token-visivi.md) DS-02, §[02 §4](02-linguaggio-visivo.md) DS-17) — non ripetuto.

## 6. Principio di ridondanza (sintesi del capitolo)

**DS-36**: ogni stato, ogni categoria semantica di colore, ogni componente di questa libreria è verificato contro la domanda "questo è comprensibile anche senza colore? Anche senza movimento? Anche a voce, tramite screen reader?" — se la risposta a una qualunque è no, il componente non è conforme (operazionalizzazione di DS-INV-05).

---

*Prossimo: [Component Library — Navigazione e Struttura](05-componenti-navigazione.md)*
