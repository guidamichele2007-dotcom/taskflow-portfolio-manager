package com.omnilife.core.notifications

import com.omnilife.core.eventbus.EventBus
import com.omnilife.core.eventbus.InMemoryEventBus

/**
 * Facade composing this module's components — a single point a future app entry point wires up,
 * mirroring `core-sync`'s `SyncEngine`. Each component also works standalone; nothing here
 * depends on any `domain-*` module.
 *
 * [localNotificationService] has no default: assembling one requires the platform-specific
 * `expect` classes ([NotificationScheduler], [NotificationChannelRegistry],
 * [NotificationPermissionManager]), whose real constructors differ per platform (e.g. Android's
 * take a `Context`) — common code can only default-construct an `expect` class that declares an
 * explicit no-arg constructor shared by every platform, which these don't. The composition root
 * on each platform builds a [DefaultLocalNotificationService] from its own platform primitives
 * and passes it in, exactly like `SyncEngine` never default-constructs a platform transport.
 */
public class NotificationEngine(
    public val localNotificationService: LocalNotificationService,
    public val eventBus: EventBus = InMemoryEventBus(),
    public val categoryRegistry: NotificationCategoryRegistry = InMemoryNotificationCategoryRegistry(),
    public val historyStore: NotificationHistoryStore = InMemoryNotificationHistoryStore(),
    public val budget: NotificationBudget = NotificationBudget(),
    public val digest: NotificationDigest = InMemoryNotificationDigest(),
    public val broker: NotificationBroker =
        NotificationBroker(categoryRegistry, historyStore, budget, digest, localNotificationService, eventBus),
    public val backgroundDelivery: BackgroundDeliveryCoordinator = BackgroundDeliveryCoordinator(broker),
)
