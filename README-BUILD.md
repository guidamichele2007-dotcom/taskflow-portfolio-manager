# README-BUILD — Infrastruttura di Bootstrap

Questo documento descrive **come è costruito il repository**, non cosa fa il prodotto: per quello vedi le otto Bible e il Technology Decision Record in [`docs/omnilife/`](docs/omnilife/README.md). Ogni scelta qui sotto deriva da quei documenti — questo file non introduce nessuna decisione tecnologica nuova, solo le convenzioni operative per lavorarci.

**Perimetro di questo bootstrap** (Engineering Plan, EPIC-00): solo infrastruttura — struttura delle cartelle, workspace Kotlin Multiplatform, moduli placeholder, configurazione di build/test/lint/CI. Nessuna funzionalità di business, nessuna schermata, nessuna sincronizzazione, autenticazione, database o API.

## 1. Struttura del repository

```
.
├── shared/                  # Modulo aggregatore KMP, confine di export per iOS/Android
├── core/core-*/             # 13 moduli — i Servizi Core (L4) + supporto trasversale
├── domain/domain-*/         # 8 moduli — un modulo per dominio L3
├── feature/feature-*/       # 12 moduli — presentazione L1/L2, uno per Epic
├── platform/platform-*/     # 7 moduli — Adattatori L5
├── androidApp/               # App Android (L1, Kotlin/Compose) — incluso solo se SDK Android presente
├── iosApp/                   # App iOS (L1, Swift/SwiftUI) — Swift Package, richiede Xcode/macOS
├── backend/                  # Confine L6 "content-blind" (Go) — servizio di sincronizzazione esterno
├── build-logic/              # Composite build: convention plugin Gradle condiviso da ogni modulo KMP
├── gradle/libs.versions.toml # Catalogo versioni (dependency management)
├── docs/omnilife/            # Le otto Bible + TDR: unica fonte di verità di prodotto/architettura
└── settings.gradle.kts       # Elenco moduli inclusi, con gating condizionale per androidApp
```

Ogni categoria di modulo corrisponde 1:1 a una sezione già esistente della documentazione — non è stata inventata nessuna nuova tassonomia architetturale in questo bootstrap:

| Categoria | Numero | Corrispondenza |
|---|---|---|
| `core-*` | 13 | 10 Servizi Core L4 ([Technical Architecture Bible §13](docs/omnilife/technical_architecture_bible/13-diagrammi.md) / §02 §1) + `core-common`, `core-designtokens`, `core-testing` (supporto trasversale di bootstrap, non un servizio L4 a sé) |
| `domain-*` | 8 | gli 8 domini L3 ([Technical Architecture Bible §02 §1](docs/omnilife/technical_architecture_bible/02-moduli-responsabilita-boundaries.md)) |
| `feature-*` | 12 | le Epic con schermate dedicate ([Engineering Plan §01 §4](docs/omnilife/engineering_plan/01-epics.md)) |
| `platform-*` | 7 | i 6 Adattatori L5 ([Technical Architecture Bible §01 §5](docs/omnilife/technical_architecture_bible/01-architettura-generale-e-layer.md)) + `platform-widget` (proiezione L1, aggiunta per completezza pratica) |

## 2. Convenzioni di naming

- **Percorso Gradle**: `:<categoria>:<categoria>-<nome>` (es. `:core:core-eventbus`, `:domain:domain-task`). La categoria è ripetuta nel nome del progetto (non solo nella cartella) perché i percorsi Gradle dei sottoprogetti devono essere univoci nell'intera build, e questo li rende leggibili anche fuori contesto (log CI, output di errore).
- **Cartella**: identica al nome del progetto Gradle (`core/core-eventbus/`).
- **Package Kotlin**: `com.omnilife.<categoria senza trattino>` per i moduli `core-*`/`domain-*`/`platform-*` con un solo tipo pubblico (es. `com.omnilife.eventbus`), oppure `com.omnilife.<categoria>.<nome>` dove la gerarchia aiuta la leggibilità. Nessuna eccezione al prefisso `com.omnilife`.
- **Tipi placeholder**: un solo tipo pubblico per modulo bootstrap, con lo stesso nome del modulo in PascalCase (es. `core-eventbus` → `EventBus`, `domain-task` → `Task`). Diventa il punto di estensione naturale nel primo sprint.
- **Package Go**: `github.com/omnilife/backend/<percorso>`, minuscolo, senza trattini (convenzione standard Go).
- **Moduli app**: `androidApp`, `iosApp` — camelCase, per distinguerli a colpo d'occhio dai moduli di libreria `snake-con-trattino`.

## 3. Regole di dipendenza

Le regole seguono meccanicamente la Dependency Rule già stabilita ([Technical Architecture Bible §01 §4](docs/omnilife/technical_architecture_bible/01-architettura-generale-e-layer.md)):

- `core-*` non dipende mai da `domain-*`, `feature-*` o `platform-*` (i Servizi Core sono a valle di nessun dominio).
- `domain-*` dipende solo da `core-*` (mai da un altro `domain-*`: i domini sono indipendenti fra loro, coerente con l'isolamento dei moduli plugin).
- `feature-*` dipende dal proprio `domain-*` e da `core-designtokens`; mai da `platform-*` direttamente (gli Adattatori sono iniettati, non referenziati).
- `platform-*` implementa le interfacce (`port`) dichiarate in `core-*`; dipende solo da `core-*`.
- `shared` aggrega tutti i moduli `core-*`/`domain-*` (non `feature-*`/`platform-*`: è il confine di export del dominio condiviso verso le UI native, non un bundle applicativo).
- `androidApp`/`iosApp` dipendono **solo** da `shared`, mai da un modulo `core-*`/`domain-*` individuale — questo è ciò che rende verificabile meccanicamente (in futuro, come gate CI dedicato, TDR-14) che la UI nativa non aggiri il confine del dominio condiviso.

Non esiste ancora un controllo automatico di queste regole (nessun gate di dipendenza in CI in questo bootstrap) — è un TODO del primo sprint, vedi report del bootstrap.

## 4. Build system

Gradle multi-modulo con **un solo convention plugin precompilato** (`omnilife.kmp.module`, in `build-logic/src/main/kotlin/`), applicato da ogni modulo `core-*`/`domain-*`/`feature-*`/`platform-*`/`shared`. Motivazione: senza un convention plugin, la configurazione dei target Kotlin Multiplatform andrebbe duplicata in ~40 file `build.gradle.kts` quasi identici — un solo punto di manutenzione invece di quaranta (diretta conseguenza di TDR-18).

`androidApp` e `iosApp` **non** usano questo convention plugin: sono moduli applicazione con le proprie preoccupazioni (manifest, entry point, firma) che non si applicano ai moduli libreria.

### Gating dei target di piattaforma

Ogni modulo dichiara tre target potenziali; solo `jvm()` è incondizionato:

| Target | Condizione | Motivo del gate |
|---|---|---|
| `jvm()` | sempre attivo | stand-in verificabile in qualunque ambiente, incluso questo sandbox |
| `androidTarget()` | `ANDROID_HOME`/`ANDROID_SDK_ROOT` presente, o `local.properties` con `sdk.dir` | gate **ambientale**: Android compila da Linux una volta presente l'SDK, non è un limite di piattaforma |
| `iosArm64()`/`iosSimulatorArm64()`/`iosX64()` | host macOS (`os.name` contiene "Mac") | vincolo **fondamentale** di Kotlin/Native: i target Apple richiedono Xcode su macOS, non è specifico di alcun ambiente |

`build-logic/build.gradle.kts` evita deliberatamente una dipendenza a tempo di compilazione dall'Android Gradle Plugin (che fallirebbe la risoluzione ovunque l'SDK non sia raggiungibile, anche se il branch Android non esegue mai): l'estensione `android {}` viene configurata dinamicamente con `withGroovyBuilder` invece del DSL tipizzato, cosicché AGP si risolve solo se e quando il branch condizionale esegue davvero.

### Cosa è stato effettivamente verificato in questo sandbox

- **JVM**: `gradle build` (Gradle 8.14.3 di sistema — il wrapper genera correttamente ma non può scaricare la distribuzione in questo sandbox, rete bloccata verso i redirect di `services.gradle.org`) — **successo**, tutti i 41 moduli inclusi compilano il target `jvm()`, i test `commonTest`/`jvmTest` passano.
- **Android** (`androidApp` + target `androidTarget()` di ogni modulo): **non verificato** — nessun Android SDK disponibile in questo sandbox (il proxy di rete blocca `dl.google.com`).
- **iOS** (`iosApp` + target `iosArm64`/`iosSimulatorArm64`/`iosX64`): **non verificato** — richiede un host macOS con Xcode, strutturalmente non ottenibile in un sandbox Linux.
- **Go** (`backend/`): `go build ./...`, `go vet ./...`, `go test ./...`, `gofmt -l .`, `golangci-lint run ./...` — **successo**.

In qualunque ambiente con i toolchain reali (macchina di sviluppo o CI configurata come da [`.github/workflows/ci.yml`](.github/workflows/ci.yml)), `./gradlew build` scarica la distribuzione Gradle normalmente e i target Android/iOS si attivano secondo la tabella sopra.

## 5. Dependency management

- **Kotlin/KMP**: catalogo versioni centralizzato in [`gradle/libs.versions.toml`](gradle/libs.versions.toml) (Kotlin 2.0.21, coroutines 1.9.0, kotlinx-datetime 0.6.1, kotlinx-serialization 1.7.3, AGP 8.5.2). Repository: Maven Central + Google (per AGP/AndroidX) + Gradle Plugin Portal.
- **Go**: `go.mod` in `backend/`, nessuna dipendenza esterna per ora (solo standard library).
- Nessuna scansione automatica delle vulnerabilità (SCA/SBOM) è ancora collegata in CI in questo bootstrap, nonostante sia già decisa (TDR-16) — è un TODO esplicito del primo sprint.

## 6. Test configuration

- Ogni modulo Kotlin ha un `commonTest` con un solo test *smoke*: verifica che il modulo compili e che il suo tipo placeholder sia risolvibile (`assertTrue(true, "<modulo> module scaffold is in place")`). Nessun test di comportamento — non c'è comportamento da testare in questo bootstrap.
- Runner: JUnit Platform (`useJUnitPlatform()`, configurato una volta nel convention plugin).
- La tassonomia completa dei livelli di test (UT/IT/E2E/PT/AT/ST) è già definita in [Engineering Plan §05](docs/omnilife/engineering_plan/05-pratiche-di-sviluppo.md) e la scelta degli strumenti in TDR-13 — questo bootstrap implementa solo il livello UT più elementare (compilazione), gli altri livelli entrano con le prime funzionalità reali.
- Go: `go test ./...`, un test per pacchetto placeholder.

## 7. Lint e formattazione

- **Kotlin**: [ktlint](https://pinterest.github.io/ktlint/) (formattazione, applicato via plugin Gradle nel convention plugin) + [detekt](https://detekt.dev/) (analisi statica, configurazione in [`config/detekt/detekt.yml`](config/detekt/detekt.yml)). Nessuna delle due scelte è un TDR — sono i due strumenti de facto dell'ecosistema Kotlin, adottati per bootstrap senza bisogno di un confronto di alternative formale.
- **Go**: `gofmt` (built-in) + [golangci-lint](https://golangci-lint.run/) (config in [`backend/.golangci.yml`](backend/.golangci.yml)).
- **Editor**: [`.editorconfig`](.editorconfig) alla radice per la coerenza di base (indentazione, fine riga, encoding) indipendente dall'IDE.

## 8. Git hooks

Hook condivisi in [`scripts/git-hooks/`](scripts/git-hooks/) (non in `.git/hooks/`, che non è versionato). Attivazione locale:

```sh
git config core.hooksPath scripts/git-hooks
```

Il hook `pre-commit` esegue `ktlintCheck` e `gofmt -l` sui soli file modificati — nessun blocco di rete, nessuna dipendenza da servizi esterni.

## 9. CI

[`​.github/workflows/ci.yml`](.github/workflows/ci.yml): pipeline as-code, runner containerizzati (TDR-14). Esegue `gradle checkAll` (target JVM, l'unico verificabile senza SDK Android/host macOS dedicati) e `go build`/`go vet`/`go test`/lint per `backend/`. Non è legata a un fornitore cloud specifico, coerente con TDR-14. Job Android/iOS dedicati (runner con SDK/macOS) sono un TODO del primo sprint quando i team di piattaforma saranno operativi.

## 10. TODO strutturali lasciati al primo sprint

Vedi [`docs/omnilife/bootstrap_infrastructure_report.md`](docs/omnilife/bootstrap_infrastructure_report.md) per l'elenco completo, la struttura finale del repository, i moduli creati e le dipendenze fra moduli.

## 11. Convenzioni di ingegneria (a partire da Sprint 1)

Queste convenzioni riempiono gli spazi che la Technical Architecture Bible lascia deliberatamente aperti (§07 traduce gli errori tra layer senza fissarne il tipo; §02-03 definiscono i confini senza fissare nomi di classi). Non sono decisioni tecnologiche (per quelle vedi TDR-19…21) — sono convenzioni di codice, applicate per la prima volta nel modulo Attività e da riusare identiche in ogni Epic futuro.

**Porta di Persistenza / Repository**: l'astrazione (`interface XxxRepository`) vive nello stesso modulo `domain-*` che possiede l'entità (Dependency Inversion: il layer interno dichiara il contratto). In questo sprint, per semplicità e perché il grafo dei moduli è piccolo, anche l'implementazione concreta (SQLDelight, TDR-20) vive nello stesso modulo `domain-*`, in un sotto-package `persistence`. Se in futuro un'implementazione dovesse essere condivisa da più domini, si estrarrà in un modulo `platform-*` dedicato — non prima che serva davvero.

**Caso d'uso (Application layer, L2)**: una classe per caso d'uso, nome `VerboEntità` (es. `CreateTask`, `CompleteTask`, non `TaskUseCase` generico), con un solo metodo `operator fun invoke(...)`. Restituisce sempre `OmniResult<T>` (TDR-21), mai lancia eccezioni per errori di dominio previsti.

**Event Bus — forma concreta**: il contratto Technical Architecture Bible §03 fissa solo due porte concettuali (pubblica/sottoscrivi), non una firma. Convenzione adottata: eventi come sottotipi di una `sealed interface DomainEvent` (uno per modulo, es. `TaskEvent`), bus in-memory, **sincrono** per rispettare "operazione sincrona e locale, mai di rete" (§03 §4) — niente `Flow`/coroutine per la consegna stessa (i souscrittori possono internamente lanciare lavoro asincrono, ma la chiamata di pubblicazione ritorna solo dopo aver notificato ogni sottoscrittore attivo, in ordine di sottoscrizione). Nessuna garanzia d'ordine tra producer diversi (già dichiarato non richiesta dalla Bible).

**Stato UI (MVI, TDR-02) — forma concreta**: `data class XxxUiState` immutabile + `sealed interface XxxIntent` + una classe `XxxViewModel`/`XxxStore` con `val state: StateFlow<XxxUiState>` e `fun dispatch(intent: XxxIntent)`. Vive nel modulo `feature-*`, in Kotlin puro (nessuna dipendenza da Compose/SwiftUI) — verificabile con test JVM ordinari anche in questo sandbox, senza bisogno di un motore di rendering.

**Correzione di perimetro — "Tag" nel modulo Attività**: la Data Model Bible (§10 "Tagging e categorizzazione") e la UX Bible dichiarano esplicitamente **"non esistono tag liberi"** per il modulo Attività (P32 — nessuna tassonomia parallela); l'unica classificazione documentata è Lista (Area→Lista, 2 livelli, TASK-005) + Priorità (3 livelli, TASK-006). Sprint 1 implementa questi due assi, non un'entità Tag — introdurne una contraddirebbe la fonte di verità esistente ("non introdurre funzionalità non documentate").
