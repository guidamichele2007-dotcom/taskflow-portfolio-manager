# Android MVP — Piano di Test su Dispositivo Reale

**Perché questo documento esiste**: questo sandbox non ha un Android SDK/emulatore (vedi
`README-BUILD.md` §4) — `androidApp` non è mai stato compilato né eseguito qui. Ogni riga di
questo file è quindi una **checklist da eseguire realmente su un dispositivo o emulatore Android**,
non un resoconto di test già passati. Sprint 6 ("MVP Hardening + Real Device Readiness") ha
verificato tutto ciò che è genuinamente verificabile senza SDK — compilazione reale
(`compileKotlinJvm`/`compileTestKotlinJvm`), test comportamentali della logica condivisa
(`jvmTest`), `detekt`/`ktlintCheck` — e ha corretto diversi bug reali trovati leggendo il codice
Android-specifico con attenzione (vedi `sprint6_report.md` §6). Quello che segue è ciò che resta.

## 0. Prerequisiti per il primo build reale

- [ ] Android Studio o SDK command-line tools installati, `ANDROID_HOME`/`local.properties`
  configurati (`settings.gradle.kts` include `:androidApp` solo se rilevato).
- [ ] `./gradlew :androidApp:assembleDebug` completa senza errori — **prima verifica reale mai
  eseguita su questo modulo**.
- [ ] `./gradlew :androidApp:assembleInternal` e `:androidApp:assembleRelease` (quest'ultimo fallisce
  volutamente senza le 4 variabili d'ambiente di firma — vedi §7 "Release build" sotto — verificare
  che fallisca con un errore di firma chiaro, non con un crash generico).

## 1. Avvio e onboarding

- [ ] Prima installazione: l'app si avvia, mostra `OmniLoadingState` per un istante, poi
  l'onboarding (mai bloccato/bianco/ANR — Sprint 6 ha rimosso 3 chiamate `runBlocking` che
  bloccavano il thread principale qui).
- [ ] L'onboarding è breve (3 step), tema/accento sono opzionali e sempre saltabili.
- [ ] Dopo aver completato l'onboarding, riavviare l'app: deve aprirsi direttamente sulla Home
  (Oggi), non ripetere l'onboarding.
- [ ] Impostazioni → "Reset onboarding" → riavvio: l'onboarding deve ripresentarsi.

## 2. Notifiche (la parte con più rischio reale — vedi sprint6_report.md §6)

- [ ] Alla creazione di un task con promemoria, il sistema chiede il permesso
  `POST_NOTIFICATIONS` (Android 13+). Negarlo: nessun crash, il promemoria resta "silenzioso" (nessuna
  eccezione, MFC §NTF P6).
- [ ] Concederlo: creare un task con scadenza + promemoria a 1-2 minuti nel futuro, mettere l'app in
  background (non chiuderla dal task switcher). La notifica **deve comparire davvero nella tray di
  sistema** — questo è il fix principale di questo sprint: prima non esisteva alcun
  `BroadcastReceiver` che ricevesse l'allarme, quindi *nessuna* notifica arrivava mai, in nessuna
  circostanza.
- [ ] Ripetere lo stesso test dopo aver **forzato la chiusura del processo** (non solo background,
  ma "Termina" dalle impostazioni di sistema o swipe-away con "rimuovi dalla recenti" su alcuni
  OEM). **Risultato atteso, e limite noto**: la notifica potrebbe non comparire — `NotificationHistoryStore`
  è solo in-memory (nessuna persistenza SQLDelight ancora), quindi se il processo muore prima che
  l'allarme scatti, `NotificationFireReceiver` non trova nulla da mostrare. Non è un bug di questo
  test plan da "correggere sul momento": è un rischio residuo documentato, richiede uno store
  persistente per essere chiuso davvero (vedi sprint6_report.md §7).
- [ ] Su Android 13+, verificare in Impostazioni di sistema → App → OmniLife → "Allarmi e promemoria"
  se il toggle è già attivo o va concesso manualmente; con il toggle negato l'app deve comunque
  programmare un promemoria (con un ritardo leggermente meno preciso, `setAndAllowWhileIdle`), mai
  crashare con `SecurityException` (guardia aggiunta in `NotificationScheduler.kt` questo sprint).
- [ ] Tap sulla notifica: deve aprire l'app (verificato che il `PendingIntent` apra `MainActivity`);
  **limite noto**: apre l'app alla schermata normale, non salta ancora direttamente al task specifico
  (il deep link `omnilife://task/<id>` esiste come stringa ma non è ancora interpretato da
  `MainActivity` — nessuna navigazione basata su Intent è cablata).
- [ ] Completare/eliminare un task con promemoria attivo: il promemoria programmato deve sparire
  (verificabile indirettamente aspettando l'orario programmato e controllando che non compaia).
- [ ] Eliminare un task con promemoria, poi toccare "Annulla" nello snackbar entro 7s: il promemoria
  deve essere ri-programmato (fix di questo sprint — `RestoreTask` ora notifica il bridge).

## 3. Task — ciclo completo

- [ ] Crea → compare subito nella lista Oggi/Prossimi coerentemente con la data scelta.
- [ ] Modifica ogni campo (titolo, priorità, note, sottotask) dal Detail: autosalva, nessun
  pulsante "Salva".
- [ ] Completa → sparisce dalla vista Oggi, riappare in "Tutte" con lo stato completato.
- [ ] Posticipa → la data cambia, il task si sposta di vista se necessario.
- [ ] Elimina → sparisce dalla lista, appare lo snackbar "Annulla" (fix di questo sprint — prima
  non c'era alcun modo di annullare un'eliminazione dall'interfaccia).
- [ ] "Annulla" entro 7s → il task torna, identico a prima, e torna anche cercabile (indice di
  ricerca aggiornato — altro fix di questo sprint).
- [ ] Riavvia l'app (kill completo del processo): ogni modifica sopra deve essere sopravvissuta
  (persistenza SQLDelight reale, non in-memory).

## 4. Home

- [ ] Today Overview/Agenda mostrano task reali, non placeholder.
- [ ] Creare un task da un'altra tab, poi tornare su Oggi: la lista si aggiorna **senza bisogno di
  un refresh manuale** (via Event Bus).
- [ ] Cambiare tab (Oggi → Moduli → Cerca → Profilo → Oggi): lo stato di ogni tab (testo di ricerca,
  scroll, dati Home) deve **sopravvivere** — prima di questo sprint ogni cambio tab ricreava da zero
  il ViewModel di quella tab, perdendo tutto.
- [ ] Ruotare lo schermo o passare ad un'altra app e tornare (background/foreground):
  **limite noto** — se Android ricrea l'Activity per un cambio di configurazione, lo stato
  in-memory dei ViewModel (non essendoci ancora un vero `androidx.lifecycle.ViewModel`/
  `SavedStateHandle`) viene perso e ricostruito da zero. I dati stessi (task, impostazioni) restano
  corretti perché persistiti, ma la posizione di scroll/il testo di ricerca in corso no. Documentato
  come rischio residuo, non risolto questo sprint (vedi sprint6_report.md §7).

## 5. Impostazioni / tema

- [ ] Cambiare tema Chiaro/Scuro/Sistema in Impostazioni: l'app **si aggiorna immediatamente**, senza
  riavvio (fix di questo sprint — prima il tema veniva letto una sola volta all'avvio).
  "Sistema" deve seguire davvero il tema del dispositivo, non risolvere sempre a chiaro (altro bug
  corretto questo sprint).
  Cambiare colore accento: idem, immediato.
- [ ] Cambiare budget notifiche/quiet hours: persiste dopo riavvio.

## 6. Offline

- [ ] Attivare la modalità aereo, aprire l'app: si apre normalmente (nessuna dipendenza di rete al
  avvio).
- [ ] Creare/modificare/completare/cercare task offline: tutto funziona, nessun errore di rete
  visibile.
- [ ] Chiudere e riaprire l'app offline: i dati sono ancora lì.
- [ ] Riattivare la rete: lo stato di sync (in Impostazioni) deve riflettere il numero di modifiche
  in coda (`pendingCount`), **mai** dichiarare una sincronizzazione riuscita — non esiste ancora un
  backend reale (`RemoteSyncTransport` non è cablato, deliberatamente — vedi sprint5/6 report).

## 7. Release build

- [ ] `assembleRelease` senza `OMNILIFE_RELEASE_STORE_FILE`/`_STORE_PASSWORD`/`_KEY_ALIAS`/
  `_KEY_PASSWORD` impostate: deve fallire con un errore di firma chiaro (comportamento voluto — mai
  una firma finta o hardcoded).
- [ ] Con quelle 4 variabili impostate verso un keystore reale: `assembleRelease` produce un APK
  firmato, minificato (`isMinifyEnabled = true`), installabile.
- [ ] `assembleInternal` produce un APK firmato con la keystore di debug, installabile insieme a
  Debug/Release sullo stesso dispositivo (applicationId con suffisso diverso per ciascuna variante).
- [ ] Verificare che il minify (`proguard-rules.pro`, ancora minimale) non rompa nulla di visibile —
  prima verifica reale di questo file mai eseguita.

## 8. Icona e branding

- [ ] **Gap noto, non un bug**: l'app non ha ancora un'icona reale (`res/` è vuoto, nessun
  `android:icon` nel manifest) — installa con l'icona generica di sistema. È una decisione di
  brand/design fuori dal perimetro di un audit di hardening tecnico; da fare prima di qualunque
  distribuzione reale (anche interna).

## 9. Accessibilità (spot-check su dispositivo reale)

- [ ] TalkBack attivo: ogni pulsante icona-only (FAB, checkbox completamento, elimina sottotask) ha
  una `contentDescription` letta correttamente (già implementate nel Design System — verificare che
  arrivino davvero al framework di accessibilità Android, non solo in Compose Preview).
  Ingrandire il testo di sistema al massimo: nessun testo tagliato in modo illeggibile nelle
  schermate Task/Home/Impostazioni.
