package com.omnilife.core.notifications

import com.omnilife.core.eventbus.EventBus
import com.omnilife.core.eventbus.InMemoryEventBus

/**
 * Facade composing this module's components with in-memory/JVM defaults — a single point a
 * future app entry point wires up, mirroring `core-sync`'s `SyncEngine`. Each component also
 * works standalone; nothing here depends on any `domain-*` module.
 */
public class NotificationEngine(
    public val eventBus: EventBus = InMemoryEventBus(),
    public val categoryRegistry: NotificationCategoryRegistry = InMemoryNotificationCategoryRegistry(),
    public val historyStore: NotificationHistoryStore = InMemoryNotificationHistoryStore(),
    public val budget: NotificationBudget = NotificationBudget(),
    public val digest: NotificationDigest = InMemoryNotificationDigest(),
    public val scheduler: NotificationScheduler = NotificationScheduler(),
    public val channelRegistry: NotificationChannelRegistry = NotificationChannelRegistry(),
    public val permissionManager: NotificationPermissionManager = NotificationPermissionManager(),
    public val localNotificationService: LocalNotificationService =
        DefaultLocalNotificationService(scheduler, channelRegistry, permissionManager),
    public val broker: NotificationBroker =
        NotificationBroker(categoryRegistry, historyStore, budget, digest, localNotificationService, eventBus),
    public val backgroundDelivery: BackgroundDeliveryCoordinator = BackgroundDeliveryCoordinator(broker),
)
