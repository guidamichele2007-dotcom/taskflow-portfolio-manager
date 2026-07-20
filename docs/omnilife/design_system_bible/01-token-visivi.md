# 01 · Token Visivi — Tipografia, Spaziatura, Colore, Elevazione, Bordi, Raggi, Opacità

> Eredita [00-fondamenta](00-fondamenta.md). Ogni scala qui definita è **sistemica** (rapporti e ruoli), non tecnologica: nessun valore in pixel/pt/dp assoluto è vincolante — le proporzioni e i ruoli semantici sì. La conversione in unità fisiche è una decisione della fase UI ([08-report §4](08-report.md)).

## 1. Spacing System

Unità base astratta `u` (un multiplo, non un'unità fisica). Scala geometrica corta per restare prevedibile (coerente con P37 "non affollare" e UX-C-110 "il carico visivo si misura").

| Token | Multiplo | Uso |
|---|---|---|
| `spazio.05` | 0.5u | Spaziatura interna minima (tra icona e testo in un chip) |
| `spazio.1` | 1u | Spaziatura minima tra elementi correlati |
| `spazio.2` | 2u | Spaziatura standard interna ai componenti (padding di default) |
| `spazio.3` | 3u | Spaziatura tra gruppi di controlli correlati |
| `spazio.4` | 4u | Spaziatura tra sezioni di una schermata |
| `spazio.6` | 6u | Separazione tra blocchi indipendenti (es. tra card della Home) |
| `spazio.8` | 8u | Margine esterno di massimo respiro (apertura/chiusura di una vista) |

**DS-INV-02** (§[00 §5](00-fondamenta.md#5-design-invariants-nuovi-propri-di-questa-bible)): nessun valore fuori da questa scala. **Regola**: `u` è scelto in fase UI in modo che `spazio.1` coincida con il **quarto** del target di tocco minimo (UX Bible, 44pt/48dp) — vincolo di coerenza, non di valore.

## 2. Typography

Scala fissa **a 7 gradini**, già dichiarata come vincolo in [UX-C-296](../ux_bible/13-ux-constitution.md) ("la tipografia segue una scala fissa a 7 gradini con contrasto pieno") — qui **sistematizzata per la prima volta** con ruoli e rapporti (non definiti altrove).

| Livello | Ruolo | Rapporto rispetto al corpo | Peso | Uso |
|---|---|---|---|---|
| 1 | `titolo.grande` | ×2.0 | Deciso | Titolo di benvenuto onboarding, milestone |
| 2 | `titolo.schermata` | ×1.5 | Deciso | Titolo di ogni schermata L1/L2 (UX Bible IA) |
| 3 | `titolo.sezione` | ×1.25 | Deciso | Intestazione di card, gruppo in lista |
| 4 | `corpo.enfatizzato` | ×1.0 | Enfatizzato | Titolo di entità in una riga (Task, Transazione…) |
| 5 | `corpo.default` | ×1.0 (base) | Regolare | Testo corrente, descrizioni, contenuto di Note |
| 6 | `etichetta` | ×0.875 | Regolare/Enfatizzato | Chip, etichette di campo, microcopy |
| 7 | `didascalia` | ×0.75 | Regolare | Timestamp, metadati secondari, note a piè di card |

**Regole trasversali**:
- **DS-01**: line-height minimo 1.4× la dimensione del corpo (leggibilità, P59); ridotto solo per `titolo.grande`/`titolo.schermata` (1.15-1.25×).
- **DS-02**: ogni livello resta leggibile e distinto fino al 200% di scala (eredita [UX Bible Accessibility §5](../ux_bible/12-accessibility-bible.md), non ridefinito qui) — la scala **non collassa** i 7 livelli a dimensioni grandi: il rapporto si mantiene, lo spazio si riorganizza (reflow, non troncamento — già normato).
- **DS-03**: un solo font-family di sistema per l'intera scala (P59 non impone un font specifico; questa Bible non lo sceglie — decisione di fase UI).

## 3. Color System (ruoli semantici, non valori)

Coerente con [00 §1](00-fondamenta.md#1-design-principles-eredità-diretta-non-ridefiniti) (P55/UX-C-291): **nessuna palette per modulo**. La palette è unica, a ruoli, ciascuno definito come coppia chiaro/scuro (DS-INV-01).

### 3.1 Ruoli di superficie e contenuto

| Ruolo | Uso |
|---|---|
| `superficie.base` | Sfondo della schermata |
| `superficie.elevata` | Card, fogli, dialoghi (distinta da `base` per elevazione, §4 — non per un'ombra sola) |
| `superficie.overlay` | Scrim dietro un foglio/dialogo aperto |
| `testo.primario` | Titoli, contenuto principale |
| `testo.secondario` | Metadati, didascalie |
| `testo.su_accento` | Testo sopra una superficie di accento (deve garantire il contrasto AA, §[04](04-stati-e-accessibilita-visiva.md)) |
| `bordo.default` | Separatori, contorni di campo |
| `bordo.focus` | Indicatore di focus tastiera (sempre visibile, UX-C-195) |

### 3.2 Ruoli semantici (stato, non modulo)

| Ruolo | Significato | Vincolo |
|---|---|---|
| `accento.base` | Il colore di brand/azione primaria, usato con parsimonia (P54) | **Un solo ruolo semantico**, applicato uniformemente in tutto il sistema, mai per modulo; il *valore* di `accento.base` è scelto dall'utente tra un insieme chiuso di opzioni (Functional Bible, SET-001 §2 "Aspetto: colore accento dal set") — resta comunque un solo valore attivo alla volta, mai una palette per modulo |
| `stato.positivo_sobrio` | Completamento, successo — **mai** un festeggiamento visivo sproporzionato (P45, UX-C-068) | Uso ridotto: icona/testo, mai riempimento di superficie ampio |
| `stato.attenzione` | Soglia raggiunta (es. budget 80%) — **ambra, mai rosso** (P48, FIN-005) | Riservato esclusivamente al caso "soglia", mai a scopo decorativo |
| `stato.critico` | **Riservato esclusivamente** a perdita di dati e sicurezza (UX-C-045/073) | Vietato altrove — nessuna eccezione, nemmeno per errori di validazione comuni |
| `stato.informativo` | Messaggi neutri, badge Inbox (unico badge ammesso, CAPT §4) | — |

**DS-03 (vincolo forte)**: `stato.critico` (rosso) compare **solo** nei due casi dichiarati altrove come suoi unici usi legittimi (perdita dati/sicurezza) — questa Bible non introduce nuovi usi, li eredita e li rende strutturalmente rari (un solo token, non una famiglia di rossi).

## 4. Elevation (nuovo — non definito nelle Bible precedenti)

Sistema a 4 livelli, per ordinare la profondità percepita senza fare affidamento solo sull'ombra (accessibilità in condizioni di scarso contrasto, coerente con DS-INV-05: mai un solo canale percettivo).

| Livello | Uso | Canali (mai uno solo) |
|---|---|---|
| `elevazione.0` | Superficie base | — |
| `elevazione.1` | Card, riga in evidenza | Leggero scostamento tonale di superficie + ombra minima |
| `elevazione.2` | Foglio di dettaglio, menu contestuale | Scostamento tonale più marcato + ombra + eventuale sfocatura dello sfondo sottostante (già normato per il foglio di cattura, UX Bible FLOW-CAPT-01) |
| `elevazione.3` | Dialogo (riservato alle azioni irreversibili, [UX Bible Navigation Bible §6](../ux_bible/02-navigation-bible.md)) | Scostamento tonale massimo + ombra + scrim pieno dietro |

**DS-04**: in tema scuro, l'elevazione si esprime **prevalentemente tramite variazione tonale della superficie**, non tramite ombra (che ha basso contrasto su sfondi scuri) — l'ombra resta un canale secondario, mai l'unico (coerente con DS-INV-05).

## 5. Borders & Radius

| Token | Valore relativo | Uso |
|---|---|---|
| `bordo.spessore.default` | 1 unità minima di resa nitida | Separatori, contorni di campo |
| `bordo.spessore.focus` | 2× il default | Indicatore di focus, sempre visibile (UX-C-195) |
| `raggio.piccolo` | Coerente con `spazio.1` | Chip, badge |
| `raggio.medio` | Coerente con `spazio.2` | Card, campi di input |
| `raggio.grande` | Coerente con `spazio.4` | Fogli di dettaglio (angolo superiore) |
| `raggio.pieno` | 50% dell'altezza | Pulsanti pillola, avatar, indicatori circolari di progresso |

**DS-INV-02** si applica: nessun raggio o spessore fuori da questa scala.

## 6. Opacity

| Token | Valore | Uso |
|---|---|---|
| `opacita.disabilitato` | ~38% | Stato disabilitato di ogni componente (§[04](04-stati-e-accessibilita-visiva.md)) — **mai** l'unico segnale di disabilitazione (accompagnato da rimozione dell'interattività) |
| `opacita.archiviato` | ~70% | Entità archiviata (MFC-R-09, Data Model Bible §6) — attenuazione visiva già richiesta funzionalmente, qui quantificata |
| `opacita.scrim` | ~50% | Overlay dietro dialoghi/fogli |
| `opacita.completato` | ~60% | Riga di entità completata, in attesa di undo (UX Bible §7 Microinterazioni) |

---

*Prossimo: [Linguaggio Visivo — Iconografia, Griglia, Responsive, Dark/Light](02-linguaggio-visivo.md)*
