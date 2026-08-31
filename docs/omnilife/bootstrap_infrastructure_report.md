# Bootstrap Infrastructure Report

> **Stato:** infrastruttura completata · **Data:** 2026-07-20

Questo documento riporta l'esito del bootstrap del repository (Engineering Plan, EPIC-00): preparazione dell'ambiente di sviluppo per OmniLife, **solo infrastruttura** — nessuna funzionalità di business, nessuna schermata, nessuna sincronizzazione, autenticazione, database o API. Ogni scelta è già stata presa nelle otto Bible e nel [Technology Decision Record](technology_decision_record.md); questo bootstrap le traduce in un repository che compila, senza aggiungerne di nuove. Le convenzioni operative (naming, dipendenze, build, test, lint, CI, git hooks) sono documentate una sola volta in [`README-BUILD.md`](../../README-BUILD.md) alla radice del repository — questo report ne riassume l'esito, non le ripete.

## 1. Struttura finale del repository

```
.
├── .editorconfig
├── .github/workflows/ci.yml
├── .gitignore                    # esteso per Gradle/Kotlin/IDE/Xcode/SPM/Go
├── LICENSE
├── README.md                     # indice generale, aggiornato
├── README-BUILD.md               # convenzioni operative del repository
├── build.gradle.kts              # root build script, minimale
├── settings.gradle.kts           # elenco moduli, gating androidApp
├── gradle.properties
├── gradle/
│   ├── libs.versions.toml        # catalogo versioni
│   └── wrapper/
├── gradlew / gradlew.bat
├── config/detekt/detekt.yml      # ruleset detekt condiviso
├── scripts/git-hooks/
│   ├── pre-commit
│   └── README.md
├── build-logic/                  # composite build: convention plugin condiviso
│   ├── build.gradle.kts
│   ├── settings.gradle.kts
│   └── src/main/kotlin/omnilife.kmp.module.gradle.kts
├── shared/                       # modulo aggregatore KMP (confine export)
├── core/core-*/                  # 13 moduli
├── domain/domain-*/              # 8 moduli
├── feature/feature-*/            # 12 moduli
├── platform/platform-*/          # 7 moduli
├── androidApp/                   # app Android (Compose) — incluso se SDK presente
├── iosApp/                       # Swift Package (SwiftUI) — richiede Xcode/macOS
├── backend/                      # confine L6 content-blind (Go)
└── docs/omnilife/                # le otto Bible + TDR + questo report
```

Ogni modulo Kotlin ha la stessa forma: `build.gradle.kts` (applica `omnilife.kmp.module` + dipendenze `project(...)`), `README.md` (Scopo/Riferimento/Stato), `src/commonMain/kotlin/.../<Tipo>.kt` (un placeholder), `src/commonTest/kotlin/.../<Tipo>SmokeTest.kt` (un test di compilazione).

## 2. Moduli creati

**41 moduli Gradle** (oltre a `:build-logic`, composite build):

| Categoria | Conteggio | Corrispondenza documentale |
|---|---|---|
| `shared` | 1 | confine di export KMP verso iOS/Android (TDR-01) |
| `core-*` | 13 | 10 Servizi Core L4 (Technical Architecture Bible §02 §1) + `core-common`, `core-designtokens`, `core-testing` (supporto trasversale di bootstrap) |
| `domain-*` | 8 | gli 8 domini L3 (Technical Architecture Bible §02 §1) |
| `feature-*` | 12 | le Epic con schermate dedicate (Engineering Plan §01 §4) |
| `platform-*` | 7 | i 6 Adattatori L5 (Technical Architecture Bible §01 §5) + `platform-widget` |

Più 3 moduli/workspace non-Gradle-multiplatform:

| Modulo | Linguaggio | Corrispondenza |
|---|---|---|
| `androidApp` | Kotlin/Compose | L1 Esperienza, Android (TDR-01) — incluso in `settings.gradle.kts` solo se un Android SDK è rilevato |
| `iosApp` | Swift/SwiftUI (Swift Package) | L1 Esperienza, iOS (TDR-01) — richiede Xcode su macOS per essere aperto/compilato |
| `backend` | Go | confine L6 "content-blind" (TDR-03) — articolazione interna esplicitamente rinviata dalla Technical Architecture Bible |

**Elenco completo core/domain/feature/platform**:

- `core-common`, `core-eventbus`, `core-graph`, `core-moduleregistry`, `core-capture`, `core-search`, `core-notifications`, `core-insight`, `core-sync`, `core-backup`, `core-security`, `core-designtokens`, `core-testing`
- `domain-task`, `domain-finance`, `domain-habit`, `domain-calendar`, `domain-note`, `domain-health`, `domain-goal`, `domain-account`
- `feature-core`, `feature-capture`, `feature-task`, `feature-finance`, `feature-habit`, `feature-calendar`, `feature-note`, `feature-health`, `feature-goal`, `feature-search`, `feature-notifications`, `feature-settings`
- `platform-persistence`, `platform-calendar`, `platform-health`, `platform-security`, `platform-push`, `platform-storage`, `platform-widget`

## 3. Dipendenze tra moduli

Regole generali in [`README-BUILD.md` §3](../../README-BUILD.md#3-regole-di-dipendenza). Grafo effettivo (estratto dai `build.gradle.kts` reali):

```
core-common          (nessuna dipendenza — fondamento)
core-designtokens    (nessuna dipendenza)
core-eventbus        → core-common
core-graph           → core-common
core-moduleregistry  → core-common
core-search          → core-common
core-sync            → core-common
core-backup          → core-common
core-security        → core-common
core-testing         → core-common
core-capture         → core-common, core-eventbus
core-notifications   → core-common, core-eventbus
core-insight         → core-common, core-eventbus

domain-calendar      → core-common, core-eventbus
domain-health        → core-common, core-eventbus
domain-account       → core-common, core-security
domain-task          → core-common, core-graph, core-eventbus
domain-finance       → core-common, core-graph, core-eventbus
domain-habit         → core-common, core-graph, core-eventbus
domain-note          → core-common, core-graph, core-eventbus
domain-goal          → core-common, core-graph, core-eventbus

feature-core          → core-moduleregistry, core-designtokens
feature-capture       → core-capture, core-designtokens
feature-search        → core-search, core-designtokens
feature-notifications → core-notifications, core-designtokens
feature-task          → domain-task, core-designtokens
feature-finance       → domain-finance, core-designtokens
feature-habit         → domain-habit, core-designtokens
feature-calendar      → domain-calendar, core-designtokens
feature-note          → domain-note, core-designtokens
feature-health        → domain-health, core-designtokens
feature-goal          → domain-goal, core-designtokens
feature-settings      → domain-account, core-designtokens

platform-persistence → core-common
platform-calendar    → core-common
platform-health      → core-common
platform-storage     → core-common
platform-security    → core-common, core-security
platform-push        → core-common, core-notifications
platform-widget      → core-common, core-designtokens

shared → tutti i 12 core-* (esclusi core-testing) + tutti gli 8 domain-*

androidApp → shared (unica dipendenza)
iosApp     → framework prodotto da shared per iOS (integrazione da completare nel primo sprint, vedi iosApp/README.md)
backend    → nessuna dipendenza sui moduli Kotlin (L6 è un confine separato, mai integrato nel client)
```

Nessun modulo `feature-*` dipende da un `platform-*` (gli Adattatori sono iniettati, mai referenziati direttamente — regola verificabile ma non ancora applicata come gate CI, vedi TODO).

## 4. Verifica

| Ambito | Comando | Esito in questo sandbox |
|---|---|---|
| Kotlin, target JVM (tutti i 41 moduli) | `gradle build` / `gradle checkAll` | ✅ successo — compilazione, test di compilazione (`commonTest`), `ktlintCheck`, `detekt` |
| Kotlin, target Android | — | non verificato — nessun SDK Android in questo sandbox (proxy blocca `dl.google.com`) |
| Kotlin/Swift, target iOS | — | non verificato — richiede host macOS con Xcode |
| Go (`backend/`) | `go build ./...`, `go vet ./...`, `go test ./...`, `gofmt -l .`, `golangci-lint run ./...` | ✅ successo |

Dettaglio completo del gating dei target in [`README-BUILD.md` §4](../../README-BUILD.md#4-build-system).

## 5. TODO per il primo sprint

1. **Gate di dipendenza in CI**: nessun controllo automatico oggi impedisce a un `feature-*` di dipendere da un `platform-*` o a un `domain-*` di dipendere da un altro `domain-*` — le regole di [`README-BUILD.md` §3](../../README-BUILD.md#3-regole-di-dipendenza) sono rispettate per disciplina, non ancora per costruzione (TDR-14 lo prevede come gate dedicato).
2. **SCA/SBOM continui** (TDR-16): non ancora collegati in CI.
3. **Job CI per Android e iOS**: `.github/workflows/ci.yml` copre solo il target JVM e il backend Go; servono runner con SDK Android e con macOS/Xcode.
4. **Verifica reale dei moduli `androidApp` e `iosApp`**: nessuno dei due è stato compilato in questo bootstrap (vedi §4); il primo sprint deve validarli su toolchain reali.
5. **`iosApp`**: creare in Xcode il vero target applicazione (bundle id, `Info.plist`, firma) che incorpora questo Swift Package e il framework `:shared` — passo che richiede necessariamente l'IDE su macOS (vedi `iosApp/README.md`).
6. **Sostituire i placeholder con i primi tipi reali**: ogni `object`/`interface` segnaposto (un tipo per modulo) diventa il punto di innesto della prima Story reale — a partire dagli Epic a priorità più alta della roadmap (Engineering Plan).
7. **Wiring completo del catalogo versioni**: `gradle/libs.versions.toml` esiste ma non è ancora consumato ovunque tramite accessor `libs.*` (alcune versioni restano dichiarate a mano nel convention plugin e in `androidApp/build.gradle.kts`, coerentemente per necessità con `build-logic` che è una build separata).
8. **Vault dei segreti** (TDR-15): non ancora introdotto — non necessario finché non esistono credenziali reali da gestire, ma da pianificare prima del primo deploy del `backend`.
9. **Articolazione interna di `backend`** (L6): la Technical Architecture Bible rinvia esplicitamente questa decisione (§01 §6) — il primo sprint che tocca la sincronizzazione dovrà prenderla.
