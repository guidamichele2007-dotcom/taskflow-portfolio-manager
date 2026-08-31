package com.omnilife.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.omnilife.core.designsystem.components.OmniBottomBar
import com.omnilife.core.designsystem.components.OmniTabBarItem
import com.omnilife.core.designsystem.theme.OmniIconType
import com.omnilife.core.designsystem.theme.OmniTheme
import com.omnilife.core.designtokens.OmniAccent
import com.omnilife.domain.account.AccentColor
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
import kotlinx.coroutines.runBlocking

/**
 * Application entry point (Sprint 5, Macro Sprint "MVP Vertical Slice"). Real composition of
 * [AppContainer]'s repositories/use cases/bridges into the hand-rolled 4-tab shell (TDR-38,
 * Navigation Bible §3), gated by real onboarding-completion state (`domain-account`).
 *
 * **Not compiled/verified in this sandbox** — see `README.md`'s note (no Android SDK here).
 */
public class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = AppContainer(this)
        setContent {
            var onboardingCompleted by
                remember { mutableStateOf(runBlocking { container.getOnboardingState().completed }) }
            var theme by remember { mutableStateOf(ThemeMode.SYSTEM) }
            var accent by remember { mutableStateOf(AccentColor.INDACO) }
            DisposableEffect(Unit) {
                val settings = runBlocking { container.getSettings() }
                theme = ThemeMode.valueOf(settings.getValue(SettingKey.THEME).value)
                accent = AccentColor.valueOf(settings.getValue(SettingKey.ACCENT_COLOR).value)
                onDispose {}
            }

            OmniTheme(darkTheme = theme == ThemeMode.DARK, accent = accent.toOmniAccent()) {
                if (!onboardingCompleted) {
                    val onboardingViewModel =
                        remember {
                            OnboardingViewModel(
                                updateSetting = container.updateSetting,
                                completeOnboarding = container.completeOnboarding,
                                createTask = container.createTask,
                                listId = container.defaultListId,
                                ownerAccountId = container.accountId(),
                                deviceId = container.deviceIdentifier(),
                                onCompleted = { onboardingCompleted = true },
                            )
                        }
                    val state by onboardingViewModel.state.collectAsState()
                    OnboardingScreen(state = state, onIntent = onboardingViewModel::dispatch)
                } else {
                    AppShell(container)
                }
            }
        }
    }
}

private fun AccentColor.toOmniAccent(): OmniAccent = OmniAccent.valueOf(name)

@Composable
private fun AppShell(container: AppContainer) {
    var selectedTab by remember { mutableStateOf(AppTab.OGGI) }
    var openTaskId by remember { mutableStateOf<String?>(null) }
    var creatingTask by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                // HomeScreen's widget rows don't yet carry a per-entry onClick (Sprint 4's
                // HomeSectionState/HomeListEntry shape has no click callback) — tapping a task
                // from Today Overview to open its detail sheet is a residual gap, not wired here.
                AppTab.OGGI -> HomeTab(container = container, onCapture = { creatingTask = true })

                AppTab.MODULI ->
                    TaskListTab(
                        container = container,
                        onOpenTask = { openTaskId = it },
                        onCapture = { creatingTask = true },
                    )

                AppTab.CERCA -> SearchTab(container = container, onOpenTask = { openTaskId = it })
                AppTab.PROFILO -> SettingsTab(container = container)
            }
        }
        // OmniIconType (core-designsystem) has no dedicated home/list/profile glyphs yet (Sprint 2
        // scoped icons to what Home/Task/Notifications needed, not a 4-tab bar) — reusing the
        // closest existing icons rather than inventing new ones outside this sprint's scope.
        OmniBottomBar(
            items =
                listOf(
                    OmniTabBarItem(OmniIconType.INFO, OmniIconType.INFO, AppTab.OGGI.label),
                    OmniTabBarItem(OmniIconType.CHECK, OmniIconType.CHECK, AppTab.MODULI.label),
                    OmniTabBarItem(OmniIconType.SEARCH, OmniIconType.SEARCH, AppTab.CERCA.label),
                    OmniTabBarItem(OmniIconType.MORE_HORIZONTAL, OmniIconType.MORE_HORIZONTAL, AppTab.PROFILO.label),
                ),
            selectedIndex = AppTab.entries.indexOf(selectedTab),
            onItemSelected = { selectedTab = AppTab.entries[it] },
        )
    }

    val openId = openTaskId
    if (openId != null) {
        val detailViewModel =
            remember(openId) {
                TaskDetailViewModel(
                    taskId = openId,
                    repository = container.taskRepository,
                    updateTaskFields = container.updateTaskFields,
                    deleteTask = container.deleteTask,
                    addSubtask = container.addSubtask,
                    toggleSubtask = container.toggleSubtask,
                    deleteSubtask = container.deleteSubtask,
                    reorderSubtasks = container.reorderSubtasks,
                )
            }
        DisposableEffect(openId) { onDispose { detailViewModel.clear() } }
        val state by detailViewModel.state.collectAsState()
        TaskDetailBottomSheet(state = state, onIntent = detailViewModel::dispatch, onDismiss = { openTaskId = null })
    }

    if (creatingTask) {
        val createViewModel =
            remember {
                TaskCreateViewModel(
                    createTask = container.createTask,
                    listId = container.defaultListId,
                    ownerAccountId = container.accountId(),
                    deviceId = container.deviceIdentifier(),
                )
            }
        DisposableEffect(Unit) { onDispose { createViewModel.clear() } }
        val state by createViewModel.state.collectAsState()
        TaskCreateBottomSheet(
            state = state,
            onIntent = createViewModel::dispatch,
            onDismiss = { creatingTask = false },
            onCreated = { createdId ->
                creatingTask = false
                openTaskId = createdId
            },
        )
    }
}

@Composable
private fun HomeTab(
    container: AppContainer,
    onCapture: () -> Unit,
) {
    val viewModel =
        remember {
            HomeViewModel(
                syncStateManager = container.syncStateManager,
                notificationBroker = container.notificationBroker,
                notificationHistoryStore = container.notificationHistoryStore,
                searchService = container.unifiedSearchService,
                taskRepository = container.taskRepository,
                eventBus = container.eventBus,
                getTasksForView = container.getTasksForView,
                recentSearchStore = container.recentSearchStore,
            )
        }
    DisposableEffect(Unit) { onDispose { viewModel.clear() } }
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
    container: AppContainer,
    onOpenTask: (String) -> Unit,
    onCapture: () -> Unit,
) {
    val viewModel =
        remember {
            TaskListViewModel(
                getTasksForView = container.getTasksForView,
                completeTask = container.completeTask,
                uncompleteTask = container.uncompleteTask,
                deleteTask = container.deleteTask,
                postponeTask = container.postponeTask,
                reorderTasks = container.reorderTasks,
                searchTasks = container.searchTasks,
            )
        }
    DisposableEffect(Unit) { onDispose { viewModel.clear() } }
    val state by viewModel.state.collectAsState()
    TaskListScreen(state = state, onIntent = viewModel::dispatch, onTaskClick = onOpenTask, onCapture = onCapture)
}

@Composable
private fun SearchTab(
    container: AppContainer,
    onOpenTask: (String) -> Unit,
) {
    val viewModel = remember { SearchViewModel(container.unifiedSearchService, container.recentSearchStore) }
    val state by viewModel.state.collectAsState()
    SearchScreen(
        state = state,
        onIntent = viewModel::dispatch,
        onResultClick = { result -> if (result.entityType == "task") onOpenTask(result.id) },
    )
}

@Composable
private fun SettingsTab(container: AppContainer) {
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
    val state by viewModel.state.collectAsState()
    SettingsScreen(state = state, onIntent = viewModel::dispatch)
}
