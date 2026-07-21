# Sprint 2 — Core UI Kit — Report

**Perimetro**: trasformare il Design System (documentazione) in codice riutilizzabile — libreria UI condivisa. Nessuna funzionalità di business, nessuna schermata, nessuna logica applicativa, nessun repository/database — per vincolo esplicito del task.

## 1. Componenti creati

Tutti e 22 i componenti richiesti sono stati implementati come composable Compose Multiplatform (`core-designsystem`, TDR-22), ciascuno con le varianti/stati previsti dalla Design System Bible, tema dark/light tramite `OmniTheme`, semantica di accessibilità (etichette, ruoli, live region) e token di motion (`OmniMotionSpecs`) invece di valori inventati per componente.

| # | Componente richiesto | Composable | ID Bible | Varianti | Stati |
|---|---|---|---|---|---|
| 1 | Theme | `OmniTheme` | — | dark/light, 6 accenti | — |
| 2 | Typography | `OmniTypographyStyles` | §01 §2 | 7 livelli | — |
| 3 | Color System | `OmniColorScheme`/`OmniColors`/`OmniAccent` | §01 §3 | — | — |
| 4 | Spacing | `OmniSpacingDp` | §01 §1 | — | — |
| 5 | Shape | `OmniShapes` | §01 §5 | piccolo/medio/grande/pieno | — |
| 6 | Elevation | `OmniElevationModifier` | §01 §4 | 4 livelli | — |
| 7 | Motion Tokens | `OmniMotionSpecs` | §03 §2 | 4 durate/curve | — |
| 8 | Animation Tokens | `OmniMotionScale` + shimmer | §03 §3 | 5 token visivi | — |
| 9 | Button | `OmniButton` | CMP-PULSANTE | primario/secondario/testuale | default/premuto/disabilitato/in_caricamento |
| 10 | IconButton | `OmniIconButton` | CMP-PULSANTE (solo icona) | — | default/premuto/disabilitato |
| 11 | TextField | `OmniTextField` | *(estensione, vedi §4)* | con etichetta/placeholder | default/in_evidenza/disabilitato/in_errore |
| 12 | SearchField | `OmniSearchField` | CMP-SEARCH | globale/contestuale | default/in_evidenza/vuoto |
| 13 | Card | `OmniCard` | CMP-CARD | lista/stato singolo/vuota positiva | default/in_caricamento/vuoto |
| 14 | ListItem | `OmniListItem` | CMP-RIGA-ENTITA | con/senza completamento, con chip | default/premuto/selezionato/completato/in_sospeso |
| 15 | BottomSheet | `OmniBottomSheet` | CMP-SHEET | dettaglio/selettore | default/in_caricamento |
| 16 | Dialog | `OmniDialog` | CMP-DIALOG | conferma singola/distruttiva | default |
| 17 | Snackbar | `OmniSnackbarHost` | CMP-SNACKBAR | con annullo/informativa | default |
| 18 | FAB | `OmniFab` | CMP-FAB | unica | default/premuto |
| 19 | TopBar | `OmniTopBar` | CMP-TOPBAR | L1/L2 | default/azione disabilitata |
| 20 | BottomBar | `OmniBottomBar` | CMP-TABBAR | unica, 4 slot | default/selezionato |
| 21 | Navigation Components | `OmniSegmentedControl` | CMP-SEGMENT | 2-4 segmenti | default/selezionato |
| 22 | Chips | `OmniChip` | CMP-CHIP | selezione/filtro/suggerimento | default/selezionato/disabilitato |
| 23 | Badge | `OmniBadge` | CMP-BADGE | numerico | default/assente (count=0) |
| 24 | Toggle | `OmniToggle` | CMP-TOGGLE | unica | on/off/disabilitato |
| 25 | Checkbox | `OmniCheckbox` | *(estensione di CMP-COMPLETION, vedi §4)* | generico non-entità | default/selezionato/disabilitato |
| 26 | Progress | `OmniProgress` | CMP-PROGRESS | anello/barra | default/attenzione/completo |
| 27 | Skeleton | `OmniSkeletonListItem`/`OmniSkeletonCard` | CMP-SKELETON | riga/card | default |
| 28 | EmptyState | `OmniEmptyState` | CMP-EMPTY | mai usato/filtrato | — |
| 29 | LoadingState | `OmniLoadingState` | *(composizione di Skeleton)* | sezione/schermata | in_caricamento |
| 30 | ErrorState | `OmniErrorState` | CMP-EMPTY (variante errore) | errore | — |

Più `OmniCompletionControl` (CMP-COMPLETION), la primitiva di completamento binario che `ListItem` e `Checkbox` condividono, esplicitamente richiesta implicitamente dalla Bible per `ListItem` anche se non elencata a parte nel task.

**Catalogo macchina-leggibile**: `ComponentCatalog.kt` — lista strutturata (nome, ID Bible, composable, varianti, stati) che alimenta sia questa tabella sia la gallery, così le due non possono divergere silenziosamente.

**Preview gallery**: `GalleryScreen`/`GalleryApp` (Desktop, `com.omnilife.core.designsystem.gallery`) — una sezione per famiglia di componenti, ciascuna con istanze live e stato interattivo (toggle dark/light, testo digitabile, segmenti selezionabili). Compila senza errori; **non è stato possibile avviarla in questo sandbox** — vedi §3.

## 2. Copertura test

| Livello | Cosa | Copertura |
|---|---|---|
| `core-designtokens` (framework-agnostico, JVM) | Scala di spaziatura, scala tipografica (rapporti + line-height DS-01), raggi/bordi/elevazione, **contrasto WCAG per ogni coppia di colore** (DS-26), token di motion (DS-28) | **18 test, tutti verdi** — verificato con build completa (`gradle :core:core-designtokens:build`) |
| `core-designsystem` (componenti Compose) | Compilazione di tutti i 22 componenti + tema + icone + catalogo + gallery | **100% compilazione verificata** (`compileKotlinJvm`, ripetuta ad ogni modifica) — nessun errore, nessun warning detekt/ktlint residuo |
| `core-designsystem` — test comportamentali/di accessibilità/regressione visiva | Tentati, **non eseguibili in questo sandbox** | Vedi sotto |

### Perché i test comportamentali/di accessibilità/di regressione visiva non sono stati eseguiti

Non è un problema del codice: è un limite di rete di questo sandbox, scoperto durante l'implementazione. `compose.foundation`/`compose.ui` 1.7.1 dichiarano dipendenze transitive **solo a runtime** (non a compile-time, per questo la compilazione non ne risente) verso artefatti AndroidX reali (`androidx.lifecycle:lifecycle-runtime:2.8.5`, `androidx.annotation:annotation:1.8.0`, `androidx.collection:collection:1.4.0`, `androidx.arch.core:core-common:2.2.0`), pubblicati **esclusivamente** su `dl.google.com`. La policy di rete di questo ambiente blocca quell'host (verificato con `curl` diretto: `403` sul CONNECT; gli stessi artefatti non esistono su Maven Central, verificato con richieste dirette, `404`).

Conseguenza: **qualunque esecuzione JVM che tocchi il runtime Compose fallisce prima di partire**, non solo `compose.desktop.uiTestJUnit4` (provato per primo, scartato) ma anche l'alternativa più leggera `runComposeUiTest`/`compose.uiTest`, e persino l'avvio della `GalleryApp` (`:core:core-designsystem:run`) — la risoluzione di `jvmTestRuntimeClasspath`/`jvmRuntimeClasspath` fallisce in fase di configurazione Gradle, prima che una sola riga di codice venga eseguita.

Un file di test scritto durante lo sviluppo (`OmniButtonTest.kt`: click→callback, stato disabilitato ignora il click, etichetta accessibile su un pulsante solo-icona, rifiuto di un `OmniButton` senza testo né icona) **compilava correttamente**, verificato prima di essere rimosso. È stato rimosso deliberatamente — lasciarlo avrebbe reso `:core:core-designsystem:check` (e quindi `checkAll`/l'intero `gradle build`) permanentemente rosso in questo ambiente: una CI rotta è un problema più grave di un gap di copertura dichiarato onestamente. Lo stesso codice, in un ambiente con accesso di rete normale (una macchina di sviluppo, Android Studio, GitHub Actions — dove `dl.google.com` non è bloccato), compilerebbe e girerebbe senza modifiche.

**Cosa è stato effettivamente verificato, quindi**: correttezza strutturale e di tipo di ogni componente (il compilatore Kotlin/Compose accetta ogni firma, ogni uso dei token, ogni blocco `@Composable`); conformità di lint/stile (ktlint) e di qualità (detekt) su tutto il modulo; il layer dei token (che non dipende da Compose) è invece verificato a runtime con veri asserti, inclusi quelli di accessibilità (contrasto colore). Il comportamento a runtime dei componenti Compose (interazioni, stati, semantica effettiva, rendering) **non è verificato in questo sandbox** — solo per costruzione del codice.

## 3. Componenti mancanti / funzionalità non implementate

Onestamente dichiarati, coerentemente con la pratica di questo progetto (vedi Sprint 1 Report per il precedente):

1. **Controparte SwiftUI/iOS** — TDR-22 sceglie deliberatamente di non scriverla in questo sprint (nessun host macOS/Xcode disponibile per verificarla; scriverla senza `swiftc` significherebbe dichiarare "fatto" codice mai compilato). Resta un blocco esplicito per uno sprint futuro.
2. **Swipe-to-reveal quick actions su `OmniListItem`** (elimina/completa/posticipa via swipe) — la Bible lo richiede (CMP-RIGA-ENTITA §Comportamento); `onLongClick` è esposto per un futuro menu contestuale, ma né lo swipe né il menu "···" sono implementati.
3. **Menu radiale del FAB** (pressione lunga, CAPT-007) — `onLongClick` è esposto su `OmniFab`, il menu stesso non esiste.
4. **Stroke della spunta animato progressivamente** (CMP-COMPLETION) — sostituito con un fade+scale (`motion.micro`) sull'icona di spunta già disegnata, non un vero tracciamento progressivo del segno di stroke. Approssimazione dichiarata nel KDoc del componente.
5. **Area di tocco "invisibile" più grande della resa visiva** (DS-35) — per Chip/Badge/Toggle/Checkbox il target minimo è garantito rendendo il **componente visivamente più grande**, non con un'area di tocco invisibile estesa oltre una resa visiva più piccola. Soddisfa comunque DS-34 (mai sotto il minimo), ma è una semplificazione rispetto alla lettera di DS-35.
6. **Regressione visiva basata su screenshot** (`captureToImage`) — non tentata: richiede la stessa esecuzione runtime bloccata da §3 sopra.
7. **Selezione dell'accento in tempo reale nella gallery** — la gallery ha un toggle dark/light ma non un selettore dei 6 accenti; ognuno dei 6 è comunque verificato per contrasto nei test di `core-designtokens`.
8. **Set iconografico ridotto** — solo gli 11 glifi effettivamente usati dai componenti di questo sprint (check, chiudi, chevron ×2, altro, cerca, aggiungi, errore, attenzione, info, indietro); mancano ancora le icone specifiche di dominio (calendario, budget, abitudine, ecc.) che serviranno ai moduli futuri.

## 4. Miglioramenti proposti al Design System (proposte, non implementate)

1. **Formalizzare `OmniTextField` come componente della Bible.** Il Design System Bible specifica `CMP-SEARCH` ma non un campo di testo generico, pur essendo indispensabile per ogni form (titolo/note del task, nome lista...). Ho esteso il token esistente "raggio.medio, campi di input" (§01-token-visivi §5) per costruirlo (coerente con DS-INV-04, estendere invece di inventare), ma resta un'estensione non documentata nella Bible stessa — propongo di aggiungerla esplicitamente al catalogo §06 come componente proprio, non solo come nota di implementazione.
2. **Chiarire il rapporto esatto tra `raggio.*` e `spazio.*`.** La Bible lega i raggi alla scala di spaziatura ("coerente con", §01-token-visivi §5) senza fissare un fattore. Ho usato una metà (spazio/2, TDR-22) per restare proporzionato senza produrre forme a pillola; propongo di rendere questo fattore normativo nella Bible stessa, cosicché un futuro sprint non debba riscoprirlo/reinventarlo.
3. **Rivalutare DS-27 (accento desaturato in tema scuro).** Ho scelto di mantenere `accento.base` identico in entrambi i temi per garantire un solo `testo.su_accento` bianco verificabile meccanicamente (TDR-22); la Bible permette esplicitamente un accento più desaturato in scuro. Se l'estetica "accento più tenue in scuro" è prioritaria, serve una decisione esplicita su come gestire il contrasto testo-su-accento quando i due temi hanno luminosità diverse (es. invertire il colore del testo per tema).
4. **Set iconografico**: la Bible lascia aperta la scelta di libreria (§02-linguaggio-visivo §1, rinviato a "fase UI", questo sprint). Ho disegnato un set minimale a mano con primitive di `DrawScope` invece di importare una libreria di icone, per restare senza dipendenze esterne aggiuntive — propongo di validare questa scelta (o sostituirla con una libreria di icone open-source dedicata) prima che il set cresca oltre le poche decine di glifi che i moduli futuri richiederanno.
5. **Strategia di test/CI per Compose Multiplatform.** La scoperta di §3 (dl.google.com bloccato in questo sandbox) è rilevante oltre questo sprint: qualunque futuro lavoro Compose in questo ambiente avrà lo stesso limite. Propongo di annotarlo esplicitamente nell'Engineering Plan/TDR-14 (CI/CD) come requisito per l'ambiente di build reale: verificare che il runner CI (GitHub Actions o altro) abbia accesso di rete a `dl.google.com`/`google()` prima di fare affidamento su test Compose automatizzati — questo sandbox di sviluppo non lo garantisce, un runner CI standard sì, ma va verificato esplicitamente, non assunto.
6. **DS-35 (area di tocco invisibile).** Propongo di specificare nella Bible un meccanismo di riferimento (es. un modificatore condiviso "hit area minima, resa visiva libera") cosicché l'implementazione futura di Chip/Badge/Toggle non debba scegliere tra "componente visivamente più grande" (questa implementazione) e "area di tocco invisibile estesa" (lettera della Bible) senza guida esplicita.

---

*Riferimenti: [Sprint 1 Report](sprint1_report.md), [TDR-22](technology_decision_record.md#tdr-22--tecnologia-di-implementazione-del-design-system-libreria-ui-condivisa), [Design System Bible](design_system_bible/README.md).*
