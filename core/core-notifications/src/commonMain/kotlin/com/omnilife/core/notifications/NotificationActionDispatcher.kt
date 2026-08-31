package com.omnilife.core.notifications

import com.omnilife.core.eventbus.EventBus
import kotlinx.datetime.Instant

/**
 * NTF-005: dispatches an action performed *from the notification itself* (complete/postpone/check,
 * "senza aprire l'app"). This module never executes the action — that's the owning module's job
 * (this module has zero domain-* dependencies); the dispatcher's only job is publishing
 * [NotificationEvent.NtfActionPerformed] so the right module reacts, and marking the request
 * AZIONATA.
 */
public class NotificationActionDispatcher(private val eventBus: EventBus) {
    public fun dispatch(
        request: NotificationRequest,
        actionId: String,
        at: Instant,
    ): NotificationRequest {
        require(request.actions.any { it.actionId == actionId }) {
            "Action '$actionId' is not declared on request '${request.id}'"
        }
        eventBus.publish(NotificationEvent.NtfActionPerformed(request.entityReference, actionId, at))
        return request.copy(state = NotificationState.AZIONATA, outcome = NotificationOutcome.AZIONATA)
    }
}
