package com.omnilife.core.notifications

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlin.time.Duration

/**
 * NTF-005/NTF-AC-02: "posticipa" options offered on the notification itself. [FixedDuration]
 * covers "tra 10 minuti"/"tra 1 ora"; [ThisEvening] resolves to a wall-clock local hour (default
 * 19:00) via [TimezoneHandler] — "stasera" is a clock time, not a duration from now.
 */
public sealed interface SnoozeOption {
    public data class FixedDuration(public val duration: Duration) : SnoozeOption

    public data class ThisEvening(public val eveningHour: Int = 19) : SnoozeOption
}

/**
 * NTF-AC-02: "il task è ripianificato senza apertura dell'app e la notifica scompare da tutti i
 * device" — snoozing produces a *new* [NotificationRequest] (new id, [NotificationState.PIANIFICATA]);
 * the original is left to be marked [NotificationState.AZIONATA] by the caller (posticipare is
 * itself an action, NTF-005), and its removal from other devices is a sync concern, out of this
 * module's scope (see core-sync).
 */
public object SnoozeManager {
    public fun snooze(
        request: NotificationRequest,
        option: SnoozeOption,
        now: Instant,
        zone: TimeZone,
        newId: () -> String,
    ): NotificationRequest {
        val newTime =
            when (option) {
                is SnoozeOption.FixedDuration -> now + option.duration
                is SnoozeOption.ThisEvening ->
                    TimezoneHandler.nextOccurrenceOfLocalTime(now, zone, option.eveningHour)
            }
        return request.copy(
            id = newId(),
            scheduledFor = newTime,
            state = NotificationState.PIANIFICATA,
            outcome = null,
        )
    }
}
