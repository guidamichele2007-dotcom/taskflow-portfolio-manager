package com.omnilife.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.omnilife.core.designsystem.components.OmniBottomBar
import com.omnilife.core.designsystem.components.OmniLoadingState
import com.omnilife.core.designsystem.components.OmniTabBarItem
import com.omnilife.core.designsystem.theme.OmniIconType
import com.omnilife.core.designsystem.theme.OmniTheme
import com.omnilife.core.designtokens.OmniAccent
import com.omnilife.core.eventbus.subscribe
import com.omnilife.domain.account.AccentColor
import com.omnilife.domain.account.SettingEvent
import com.omnilife.domain.account.SettingKey
import com.omnilife.domain.account.ThemeMode
import com.omnilife.feature.core.HomeIntent
import com.omnilife.feature.core.HomeScreen
import com.omnilife.feature.core.HomeViewModel
import com.omnilife.feature.core.onboarding.OnboardingScreen
import com.omnilife.feature.core.onboarding.OnboardingViewModel
import com.omnilife.feature.search.SearchScreen
import com.omnilife.feature.search.SearchViewModel
import com.omnilife.feature.settings.SettingsScreen
import com.omnilife.feature.settings.SettingsViewModel
import com.omnilife.feature.task.TaskCreateBottomSheet
import com.omnilife.feature.task.TaskCreateViewModel
import com.omnilife.feature.task.TaskDetailBottomSheet
import com.omnilife.feature.task.TaskDetailViewModel
import com.omnilife.feature.task.TaskListScreen
import com.omnilife.feature.task.TaskListViewModel

/**
 * Application entry point (Sprint 5, Macro Sprint "MVP Vertical Slice"; hardened Sprint 6). Real
 * composition of [AppContainer]'s repositories/use cases/bridges into the hand-rolled 4-tab shell
 * (TDR-38, Navigation Bible §3), gated by real onboarding-completion state (`domain-account`).
 *
 * **Sprint 6 fixes** (found during that sprint's threading/state audit, see `sprint6_report.md`):
 * - Startup no longer blocks the main thread with `runBlocking` — [OmniLifeApp] loads onboarding
 *   state, settings, and the default task list asynchronously via [LaunchedEffect], showing
 *   [OmniLoadingState] meanwhile.
 * - Theme/accent are read once *and* kept live: [SettingEvent.Updated] (published by
 *   `UpdateSetting`) is subscribed here, so changing the theme in Settings applies immediately
 *   instead of requiring an app restart. `SYSTEM` now genuinely follows the device's light/dark
 *   setting instead of always resolving to light.
 * - The four tab ViewModels ([HomeViewModel], [TaskListViewModel], [SearchViewModel],
 *   [SettingsViewModel]) are created once in [AppShell] instead of inside the per-tab `when`
 *   branch — the old code destroyed and recreated them (losing search text, scroll position, and
 *   re-fetching Home) every time the user switched bottom tabs.
 *
 * **MVP Release 1.0**: the async startup gate now shows the system splash screen
 * (`androidx.core.splashscreen`, `res/values/styles.xml`'s `Theme.OmniLife.Starting`) instead of a
 * bare white flash before [OmniLoadingState] — `installSplashScreen()` is held on screen via
 * `setKeepOnScreenCondition` until [loadStartupSnapshot] resolves. A real launcher/adaptive icon
 * now exists too (`res/mipmap-anydpi-v26/`), replacing the generic system icon the app installed
 * with before.
 *
 * **Not compiled/verified in this sandbox** — see `README.md`'s note (no Android SDK here).
 */
public class MainActivity : ComponentActivity() {
    // Compose-observable but mutated from plain Activity callbacks (onCreate/onNewIntent) — a
    // legitimate escape hatch for exactly this "external event feeds Compose state" case; safe
    // because both callbacks run on the main thread, same as any recomposition trigger.
    private val pendingDeepLinkTaskId: MutableState<String?> = mutableStateOf(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        // installSplashScreen() must run before super.onCreate() (androidx.core.splashscreen
        // contract) — MVP Release 1.0, Fase 10. Held on screen until the same async startup gate
        // Sprint 6 already built (loadStartupSnapshot) finishes, so the splash covers exactly the
        // window that used to show OmniLoadingState instead of a system-native transition.
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        var keepSplashOnScreen = true
        splashScreen.setKeepOnScreenCondition { keepSplashOnScreen }
        pendingDeepLinkTaskId.value = intent.getStringExtra(EXTRA_OPEN_TASK_ID)
        val container = (application as OmniLifeApplication).container
        setContent {
            OmniLifeApp(
                container = container,
                onReady = { keepSplashOnScreen = false },
                pendingDeepLinkTaskId = pendingDeepLinkTaskId,
            )
        }
    }

    /**
     * MVP Release 1.0: `NotificationFireReceiver`'s `PendingIntent` targets this Activity with
     * `FLAG_ACTIVITY_CLEAR_TOP` (and `singleTask` launch mode, manifest) precisely so tapping a
     * notification while the app is already running re-delivers here instead of creating a
     * second instance — without overriding this, the deep link would be silently dropped whenever
     * the app wasn't freshly cold-started.
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        pendingDeepLinkTaskId.value = intent.getStringExtra(EXTRA_OPEN_TASK_ID)
    }

    public companion object {
        /** Matches `NotificationFireReceiver.EXTRA_OPEN_TASK_ID` — see that file's doc comment. */
        public const val EXTRA_OPEN_TASK_ID: String = "openTaskId"
    }
}

private data class StartupSnapshot(
    val onboardingCompleted: Boolean,
    val theme: ThemeMode,
    val accent: AccentColor,
    val defaultListId: String,
)

private suspend fun loadStartupSnapshot(container: AppContainer): StartupSnapshot {
    val onboardingCompleted = container.getOnboardingState().completed
    val settings = container.getSettings()
    val theme = ThemeMode.valueOf(settings.getValue(SettingKey.THEME).value)
    val accent = AccentColor.valueOf(settings.getValue(SettingKey.ACCENT_COLOR).value)
    val listId = container.resolveDefaultListId()
    return StartupSnapshot(onboardingCompleted, theme, accent, listId)
}

private fun AccentColor.toOmniAccent(): OmniAccent = OmniAccent.valueOf(name)

@Composable
private fun resolveDarkTheme(mode: ThemeMode): Boolean =
    when (mode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

@Composable
private fun OmniLifeApp(
    container: AppContainer,
    pendingDeepLinkTaskId: MutableState<String?>,
    onReady: () -> Unit = {},
) {
    var startup by remember { mutableStateOf<StartupSnapshot?>(null) }
    LaunchedEffect(Unit) {
        startup = loadStartupSnapshot(container)
        onReady()
    }

    val snapshot = startup
    if (snapshot == null) {
        OmniTheme(darkTheme = isSystemInDarkTheme()) {
            OmniLoadingState(modifier = Modifier.fillMaxSize())
        }
        return
    }

    var onboardingCompleted by remember { mutableStateOf(snapshot.onboardingCompleted) }
    var theme by remember { mutableStateOf(snapshot.theme) }
    var accent by remember { mutableStateOf(snapshot.accent) }
    DisposableEffect(Unit) {
        val subscription =
            container.eventBus.subscribe<SettingEvent.Updated> { event ->
                when (event.key) {
                    SettingKey.THEME -> theme = ThemeMode.valueOf(event.value)
                    SettingKey.ACCENT_COLOR -> accent = AccentColor.valueOf(event.value)
                    else -> Unit
                }
            }
        onDispose { subscription.cancel() }
    }

    OmniTheme(darkTheme = resolveDarkTheme(theme), accent = accent.toOmniAccent()) {
        if (!onboardingCompleted) {
            OnboardingHost(
                container = container,
                listId = snapshot.defaultListId,
                onCompleted = { onboardingCompleted = true },
            )
        } else {
            AppShell(
                container = container,
                defaultListId = snapshot.defaultListId,
                pendingDeepLinkTaskId = pendingDeepLinkTaskId,
            )
        }
    }
}

@Composable
private fun OnboardingHost(
    container: AppContainer,
    listId: String,
    onCompleted: () -> Unit,
) {
    val onboardingViewModel =
        remember {
            OnboardingViewModel(
                updateSetting = container.updateSetting,
                completeOnboarding = container.completeOnboarding,
                createTask = container.createTask,
                listId = listId,
                ownerAccountId = container.accountId(),
                deviceId = container.deviceIdentifier(),
                onCompleted = onCompleted,
            )
        }
    val state by onboardingViewModel.state.collectAsState()
    OnboardingScreen(state = state, onIntent = onboardingViewModel::dispatch)
}

@Composable
private fun AppShell(
    container: AppContainer,
    defaultListId: String,
    pendingDeepLinkTaskId: MutableState<String?>,
) {
    var selectedTab by remember { mutableStateOf(AppTab.OGGI) }
    var openTaskId by remember { mutableStateOf<String?>(null) }
    var creatingTask by remember { mutableStateOf(false) }

    // MVP Release 1.0: tapping a task-reminder notification now opens that task's Detail sheet
    // directly instead of just the app generically (NotificationFireReceiver/MainActivity).
    // Consumes and clears the source state so it doesn't re-trigger on an unrelated recomposition.
    LaunchedEffect(pendingDeepLinkTaskId.value) {
        val deepLinkTaskId = pendingDeepLinkTaskId.value ?: return@LaunchedEffect
        openTaskId = deepLinkTaskId
        pendingDeepLinkTaskId.value = null
    }

    // Sprint 6: created once here (not inside AppTabContent's `when` branch), so switching tabs
    // no longer destroys and rebuilds each ViewModel — see class doc.
    val homeViewModel = rememberHomeViewModel(container)
    val taskListViewModel = rememberTaskListViewModel(container)
    val searchViewModel = remember { SearchViewModel(container.unifiedSearchService, container.recentSearchStore) }
    val settingsViewModel = rememberSettingsViewModel(container)

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            AppTabContent(
                selectedTab = selectedTab,
                homeViewModel = homeViewModel,
                taskListViewModel = taskListViewModel,
                searchViewModel = searchViewModel,
                settingsViewModel = settingsViewModel,
                onOpenTask = { openTaskId = it },
                onCapture = { creatingTask = true },
            )
        }
        AppTabBar(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
    }

    val openId = openTaskId
    if (openId != null) {
        TaskDetailOverlay(container = container, taskId = openId, onDismiss = { openTaskId = null })
    }
    if (creatingTask) {
        TaskCreateOverlay(
            container = container,
            defaultListId = defaultListId,
            onDismiss = { creatingTask = false },
            onCreated = { createdId ->
                creatingTask = false
                openTaskId = createdId
            },
        )
    }
}

@Composable
private fun rememberHomeViewModel(container: AppContainer): HomeViewModel {
    val viewModel =
        remember {
            HomeViewModel(
                syncStateManager = container.syncStateManager,
                notificationBroker = container.notificationBroker,
                notificationHistoryStore = container.notificationHistoryStore,
                searchService = container.unifiedSearchService,
                taskRepository = container.taskRepository,
                eventBus = container.eventBus,
                recentSearchStore = container.recentSearchStore,
            )
        }
    DisposableEffect(Unit) { onDispose { viewModel.clear() } }
    return viewModel
}

@Composable
private fun rememberTaskListViewModel(container: AppContainer): TaskListViewModel {
    val viewModel =
        remember {
            TaskListViewModel(
                getTasksForView = container.getTasksForView,
                completeTask = container.completeTask,
                uncompleteTask = container.uncompleteTask,
                deleteTask = container.deleteTask,
                restoreTask = container.restoreTask,
                postponeTask = container.postponeTask,
                reorderTasks = container.reorderTasks,
                searchTasks = container.searchTasks,
            )
        }
    DisposableEffect(Unit) { onDispose { viewModel.clear() } }
    return viewModel
}

@Composable
private fun rememberSettingsViewModel(container: AppContainer): SettingsViewModel {
    val viewModel =
        remember {
            SettingsViewModel(
                getSettings = container.getSettings,
                updateSetting = container.updateSetting,
                resetOnboarding = container.resetOnboarding,
                syncStateManager = container.syncStateManager,
            )
        }
    DisposableEffect(Unit) { onDispose { viewModel.clear() } }
    return viewModel
}

@Composable
private fun AppTabContent(
    selectedTab: AppTab,
    homeViewModel: HomeViewModel,
    taskListViewModel: TaskListViewModel,
    searchViewModel: SearchViewModel,
    settingsViewModel: SettingsViewModel,
    onOpenTask: (String) -> Unit,
    onCapture: () -> Unit,
) {
    when (selectedTab) {
        // HomeScreen's widget rows don't yet carry a per-entry onClick (Sprint 4's
        // HomeSectionState/HomeListEntry shape has no click callback) — tapping a task from
        // Today Overview to open its detail sheet is a residual gap, not wired here.
        AppTab.OGGI -> HomeTab(viewModel = homeViewModel, onCapture = onCapture)
        AppTab.MODULI -> TaskListTab(viewModel = taskListViewModel, onOpenTask = onOpenTask, onCapture = onCapture)
        AppTab.CERCA -> SearchTab(viewModel = searchViewModel, onOpenTask = onOpenTask)
        AppTab.PROFILO -> SettingsTab(viewModel = settingsViewModel)
    }
}

@Composable
private fun AppTabBar(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
) {
    // OmniIconType (core-designsystem) has no dedicated home/list/profile glyphs yet (Sprint 2
    // scoped icons to what Home/Task/Notifications needed, not a 4-tab bar) — reusing the closest
    // existing icons rather than inventing new ones outside this sprint's scope.
    OmniBottomBar(
        items =
            listOf(
                OmniTabBarItem(OmniIconType.INFO, OmniIconType.INFO, AppTab.OGGI.label),
                OmniTabBarItem(OmniIconType.CHECK, OmniIconType.CHECK, AppTab.MODULI.label),
                OmniTabBarItem(OmniIconType.SEARCH, OmniIconType.SEARCH, AppTab.CERCA.label),
                OmniTabBarItem(OmniIconType.MORE_HORIZONTAL, OmniIconType.MORE_HORIZONTAL, AppTab.PROFILO.label),
            ),
        selectedIndex = AppTab.entries.indexOf(selectedTab),
        onItemSelected = { onTabSelected(AppTab.entries[it]) },
    )
}

@Composable
private fun TaskDetailOverlay(
    container: AppContainer,
    taskId: String,
    onDismiss: () -> Unit,
) {
    val detailViewModel =
        remember(taskId) {
            TaskDetailViewModel(
                taskId = taskId,
                repository = container.taskRepository,
                updateTaskFields = container.updateTaskFields,
                deleteTask = container.deleteTask,
                addSubtask = container.addSubtask,
                toggleSubtask = container.toggleSubtask,
                deleteSubtask = container.deleteSubtask,
                reorderSubtasks = container.reorderSubtasks,
            )
        }
    DisposableEffect(taskId) { onDispose { detailViewModel.clear() } }
    val state by detailViewModel.state.collectAsState()
    TaskDetailBottomSheet(state = state, onIntent = detailViewModel::dispatch, onDismiss = onDismiss)
}

@Composable
private fun TaskCreateOverlay(
    container: AppContainer,
    defaultListId: String,
    onDismiss: () -> Unit,
    onCreated: (String) -> Unit,
) {
    val createViewModel =
        remember {
            TaskCreateViewModel(
                createTask = container.createTask,
                listId = defaultListId,
                ownerAccountId = container.accountId(),
                deviceId = container.deviceIdentifier(),
            )
        }
    DisposableEffect(Unit) { onDispose { createViewModel.clear() } }
    val state by createViewModel.state.collectAsState()
    TaskCreateBottomSheet(
        state = state,
        onIntent = createViewModel::dispatch,
        onDismiss = onDismiss,
        onCreated = onCreated,
    )
}

@Composable
private fun HomeTab(
    viewModel: HomeViewModel,
    onCapture: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    HomeScreen(
        state = state,
        onIntent = { intent ->
            if (intent is HomeIntent.PerformQuickAction && intent.actionId == "new-task") onCapture()
            viewModel.dispatch(intent)
        },
    )
}

@Composable
private fun TaskListTab(
    viewModel: TaskListViewModel,
    onOpenTask: (String) -> Unit,
    onCapture: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    TaskListScreen(state = state, onIntent = viewModel::dispatch, onTaskClick = onOpenTask, onCapture = onCapture)
}

@Composable
private fun SearchTab(
    viewModel: SearchViewModel,
    onOpenTask: (String) -> Unit,
) {
    val state by viewModel.state.collectAsState()
    SearchScreen(
        state = state,
        onIntent = viewModel::dispatch,
        onResultClick = { result -> if (result.entityType == "task") onOpenTask(result.id) },
    )
}

@Composable
private fun SettingsTab(viewModel: SettingsViewModel) {
    val state by viewModel.state.collectAsState()
    SettingsScreen(state = state, onIntent = viewModel::dispatch)
}
