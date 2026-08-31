package com.omnilife.app

import android.content.Context
import com.omnilife.core.eventbus.EventBus
import com.omnilife.core.eventbus.InMemoryEventBus
import com.omnilife.core.notifications.DefaultLocalNotificationService
import com.omnilife.core.notifications.InMemoryNotificationCategoryRegistry
import com.omnilife.core.notifications.InMemoryNotificationDigest
import com.omnilife.core.notifications.InMemoryNotificationHistoryStore
import com.omnilife.core.notifications.NotificationBroker
import com.omnilife.core.notifications.NotificationBudget
import com.omnilife.core.notifications.NotificationChannelRegistry
import com.omnilife.core.notifications.NotificationHistoryStore
import com.omnilife.core.notifications.NotificationPermissionManager
import com.omnilife.core.notifications.NotificationScheduler
import com.omnilife.core.search.SearchService
import com.omnilife.core.sync.InMemoryChangeTracker
import com.omnilife.core.sync.InMemorySyncStateManager
import com.omnilife.core.sync.SyncStateManager
import com.omnilife.core.sync.persistence.SqlDelightSyncOutboxStore
import com.omnilife.domain.account.SettingsRepository
import com.omnilife.domain.account.persistence.SqlDelightSettingsRepository
import com.omnilife.domain.account.usecase.CompleteOnboarding
import com.omnilife.domain.account.usecase.GetOnboardingState
import com.omnilife.domain.account.usecase.GetSettings
import com.omnilife.domain.account.usecase.ResetOnboarding
import com.omnilife.domain.account.usecase.UpdateSetting
import com.omnilife.domain.task.TaskList
import com.omnilife.domain.task.TaskRepository
import com.omnilife.domain.task.persistence.SqlDelightTaskRepository
import com.omnilife.domain.task.usecase.AddSubtask
import com.omnilife.domain.task.usecase.CompleteTask
import com.omnilife.domain.task.usecase.CreateTask
import com.omnilife.domain.task.usecase.DeleteSubtask
import com.omnilife.domain.task.usecase.DeleteTask
import com.omnilife.domain.task.usecase.GetTasksForView
import com.omnilife.domain.task.usecase.PostponeTask
import com.omnilife.domain.task.usecase.PermanentlyDeleteTask
import com.omnilife.domain.task.usecase.ReorderSubtasks
import com.omnilife.domain.task.usecase.ReorderTasks
import com.omnilife.domain.task.usecase.RestoreTask
import com.omnilife.domain.task.usecase.SearchTasks
import com.omnilife.domain.task.usecase.ToggleSubtask
import com.omnilife.domain.task.usecase.UncompleteTask
import com.omnilife.domain.task.usecase.UpdateTaskFields
import com.omnilife.feature.task.bridge.TaskNotificationBridge
import com.omnilife.feature.task.bridge.TaskSearchIndexBridge
import com.omnilife.feature.task.bridge.TaskSyncOutboxBridge
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

/**
 * Composition root (TDR-19: manual DI, no framework) — the single place in this app that knows
 * about every `core-*`/`domain-*`/`feature-*` concrete type (TDR-37: `androidApp` depends on
 * `shared` plus the specific `feature-*` modules it renders). Wires the real SQLDelight-backed
 * repositories built this sprint to the real bridges (`feature-task`'s `TaskSearchIndexBridge`/
 * `TaskNotificationBridge`/`TaskSyncOutboxBridge`) so the app is genuinely offline-first end to
 * end, not a demo.
 *
 * **Not compiled/verified in this sandbox**: `androidApp` is only ever included in the Gradle
 * build when an Android SDK is present (`settings.gradle.kts`), which this sandbox does not have
 * — see `README.md` and `README-BUILD.md` §4. Every type referenced here is itself verified on
 * the JVM target (this sprint's real test suites); only this wiring file itself is unverified.
 *
 * **Known residual gaps, documented rather than hidden**:
 * - No `Account` entity yet (out of this sprint's scope) — [ownerAccountId]/[deviceId] are fixed
 *   placeholders, not real per-install identity.
 * - `NotificationHistoryStore`/digest/category registry stay in-memory (no SQLDelight-backed
 *   version exists yet) — task reminders themselves persist and reschedule correctly (they live
 *   on the `Task` entity), but the notification *history list* resets on restart.
 * - No [com.omnilife.core.sync.RemoteSyncTransport] is wired — there is no backend to sync
 *   against yet, so [com.omnilife.core.sync.BackgroundSyncCoordinator] is intentionally not
 *   constructed here (a fake transport that always "succeeds" would be exactly the artificial
 *   simulated sync this sprint's instructions forbid). The outbox queues real mutations
 *   ([TaskSyncOutboxBridge]) and [SyncStateManager] reflects real queue depth
 *   ([SyncStateManager.updatePendingCount], TDR-40) — only the "attempt delivery" half is unbuilt,
 *   pending a real backend.
 */
public class AppContainer(context: Context) {
    private val appContext = context.applicationContext

    // Single-account, single-device scope this sprint (see class doc).
    private val ownerAccountId = "local-account"
    private val deviceId = "local-device"

    public val eventBus: EventBus = InMemoryEventBus()

    public val taskRepository: TaskRepository =
        SqlDelightTaskRepository(com.omnilife.domain.task.persistence.DatabaseDriverFactory(appContext).createDriver())

    public val settingsRepository: SettingsRepository =
        SqlDelightSettingsRepository(
            com.omnilife.domain.account.persistence.DatabaseDriverFactory(appContext).createDriver(),
        )

    private val searchService =
        SearchService(com.omnilife.core.search.persistence.DatabaseDriverFactory(appContext))

    private val syncOutboxStore =
        SqlDelightSyncOutboxStore(com.omnilife.core.sync.persistence.DatabaseDriverFactory(appContext).createDriver())
    public val syncStateManager: SyncStateManager = InMemorySyncStateManager()

    public val notificationHistoryStore: NotificationHistoryStore = InMemoryNotificationHistoryStore()
    // Named (not inlined into DefaultLocalNotificationService below) because NotificationFireReceiver
    // (Sprint 6) also needs it, via the same process-wide AppContainer singleton (OmniLifeApplication).
    public val notificationPermissionManager: NotificationPermissionManager = NotificationPermissionManager(appContext)
    public val notificationBroker: NotificationBroker =
        NotificationBroker(
            categoryRegistry = InMemoryNotificationCategoryRegistry(),
            historyStore = notificationHistoryStore,
            budget = NotificationBudget(),
            digest = InMemoryNotificationDigest(),
            localNotificationService =
                DefaultLocalNotificationService(
                    scheduler = NotificationScheduler(appContext),
                    channelRegistry = NotificationChannelRegistry(appContext),
                    permissionManager = notificationPermissionManager,
                ),
            eventBus = eventBus,
        )

    // Task use cases (TDR-19: constructor-injected, no framework).
    public val createTask: CreateTask = CreateTask(taskRepository, eventBus, newId = ::newId)
    public val completeTask: CompleteTask = CompleteTask(taskRepository, eventBus, newId = ::newId)
    public val uncompleteTask: UncompleteTask = UncompleteTask(taskRepository, eventBus)
    public val deleteTask: DeleteTask = DeleteTask(taskRepository, eventBus)
    public val restoreTask: RestoreTask = RestoreTask(taskRepository, eventBus)
    public val permanentlyDeleteTask: PermanentlyDeleteTask = PermanentlyDeleteTask(taskRepository, eventBus)
    public val postponeTask: PostponeTask = PostponeTask(taskRepository, eventBus)
    public val reorderTasks: ReorderTasks = ReorderTasks(taskRepository)
    public val searchTasks: SearchTasks = SearchTasks(taskRepository)
    public val getTasksForView: GetTasksForView = GetTasksForView(taskRepository)
    public val updateTaskFields: UpdateTaskFields = UpdateTaskFields(taskRepository, eventBus)
    public val addSubtask: AddSubtask = AddSubtask(taskRepository, newId = ::newId)
    public val toggleSubtask: ToggleSubtask = ToggleSubtask(taskRepository)
    public val deleteSubtask: DeleteSubtask = DeleteSubtask(taskRepository)
    public val reorderSubtasks: ReorderSubtasks = ReorderSubtasks(taskRepository)

    // Settings/onboarding use cases.
    public val getSettings: GetSettings = GetSettings(settingsRepository)
    public val updateSetting: UpdateSetting = UpdateSetting(settingsRepository, eventBus)
    public val completeOnboarding: CompleteOnboarding = CompleteOnboarding(settingsRepository)
    public val resetOnboarding: ResetOnboarding = ResetOnboarding(settingsRepository)
    public val getOnboardingState: GetOnboardingState = GetOnboardingState(settingsRepository)

    public val searchIndexer = searchService.indexer
    public val unifiedSearchService = searchService.search
    public val recentSearchStore = searchService.recentSearches

    // L2 orchestration bridges (feature-task) — kept alive for the app's lifetime, same
    // CoroutineScope every bridge and ViewModel this container creates shares.
    public val appScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val searchIndexBridge = TaskSearchIndexBridge(taskRepository, searchIndexer, eventBus, appScope)

    // Public (not private, unlike its siblings): BootCompletedReceiver calls reconcileAll() on it
    // directly (MVP Release 1.0) — no other bridge needs an entry point from outside this class.
    public val notificationBridge: TaskNotificationBridge =
        TaskNotificationBridge(taskRepository, notificationBroker, eventBus, appScope)
    private val syncOutboxBridge =
        TaskSyncOutboxBridge(
            repository = taskRepository,
            outboxStore = syncOutboxStore,
            changeTracker = InMemoryChangeTracker(),
            syncStateManager = syncStateManager,
            deviceId = deviceId,
            eventBus = eventBus,
            scope = appScope,
        )

    public fun accountId(): String = ownerAccountId

    public fun deviceIdentifier(): String = deviceId

    init {
        // Bridges are constructed (subscriptions registered) above; this just forces them to be
        // referenced so they aren't flagged unused — their whole contract is side-effecting.
        searchIndexBridge.hashCode()
        notificationBridge.hashCode()
        syncOutboxBridge.hashCode()
        appScope.launch { searchIndexBridge.rebuildIndex() }
    }

    /**
     * TASK-005: every account needs at least the non-deletable default "Attività" list before
     * Quick Capture can attach a task to one. Idempotent (checks [TaskRepository.findAllLists]
     * first).
     *
     * Sprint 6: this used to be a `defaultListId: String by lazy { runBlocking { ... } }`
     * property, resolved synchronously on whichever thread first touched it — in practice the
     * Compose main thread during `MainActivity`'s first composition, a real blocking-the-UI-thread
     * bug (found during this sprint's threading audit). Now a plain suspend function;
     * `MainActivity` awaits it once during its async startup gate (see `MainActivity.kt`) and
     * passes the resolved id down, so nothing on the UI thread blocks on database I/O.
     */
    public suspend fun resolveDefaultListId(): String {
        val existing = taskRepository.findAllLists().firstOrNull { it.isDefault }
        if (existing != null) return existing.envelope.id
        val now = Clock.System.now()
        val list =
            TaskList(
                envelope =
                    com.omnilife.core.common.Envelope(
                        id = newId(),
                        ownerAccountId = ownerAccountId,
                        schemaVersion = 1,
                        createdAt = now,
                        createdByDevice = deviceId,
                        modifiedAt = now,
                        modifiedByDevice = deviceId,
                    ),
                name = "Attività",
                isDefault = true,
            )
        taskRepository.insertList(list)
        return list.envelope.id
    }

    private fun newId(): String = java.util.UUID.randomUUID().toString()
}
