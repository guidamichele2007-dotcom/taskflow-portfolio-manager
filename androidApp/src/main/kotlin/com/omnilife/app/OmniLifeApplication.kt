package com.omnilife.app

import android.app.Application

/**
 * Sprint 6: promotes [AppContainer] to a genuine process-wide singleton. Previously
 * `MainActivity.onCreate` constructed a fresh `AppContainer(this)` every time — harmless for a
 * single long-lived Activity, but it meant nothing outside that Activity (in particular a
 * `BroadcastReceiver`, which the OS instantiates fresh with no constructor arguments) could ever
 * reach the same in-memory `EventBus`/`NotificationHistoryStore`/`SyncStateManager` instances.
 * `NotificationFireReceiver` needs exactly that to look up a fired reminder's content — this is
 * what makes it possible without inventing a DI framework (still TDR-19: manual DI).
 */
public class OmniLifeApplication : Application() {
    public val container: AppContainer by lazy { AppContainer(this) }
}
