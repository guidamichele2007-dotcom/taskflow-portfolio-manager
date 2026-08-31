# androidApp

**Scopo**: punto di ingresso dell'applicazione Android (L1 Esperienza, TDR-01: Kotlin/Jetpack Compose). Compone i moduli `feature-*` e dipende da `:shared` come unico confine verso il dominio condiviso — mai da un modulo `core-*`/`domain-*` direttamente (vedi [../README-BUILD.md](../README-BUILD.md)).

**Riferimento**: Technology Decision Record TDR-01 (linguaggio mobile), TDR-18 (build system).

**Stato**: infrastruttura di bootstrap — un solo `MainActivity` placeholder senza navigazione, schermate o logica di business. Nessuna dipendenza sui moduli `feature-*` ancora, verrà collegata a partire dal primo sprint.

**Nota sulla verifica in questo ambiente**: questo modulo è incluso in `settings.gradle.kts` **solo se** un Android SDK è rilevabile (`ANDROID_HOME`/`ANDROID_SDK_ROOT` o `local.properties`). Il sandbox usato per il bootstrap non ha un SDK Android installato (il proxy di rete blocca `dl.google.com`, da cui l'Android Gradle Plugin scarica componenti dell'SDK), quindi **questo modulo non è stato compilato in questo ambiente**. È strutturato secondo le convenzioni standard di un modulo applicazione Android/Compose e si prevede compili correttamente su qualunque macchina con SDK Android 34 installato. Vedi [../README-BUILD.md](../README-BUILD.md) per il dettaglio completo del gating.
