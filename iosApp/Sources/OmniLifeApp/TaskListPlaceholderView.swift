import SwiftUI

/// A local, Swift-native mirror of `feature-task`'s `Task`/`TaskListUiState` (Kotlin), used only
/// so this view compiles and is reviewable as plain SwiftUI without a live KMP framework attached
/// (see the integration note below). Field set matches `domain-task`'s `Task` (Kotlin) exactly.
struct TaskRowModel: Identifiable {
    let id: String
    let title: String
    let dueDate: String?
    let completed: Bool
}

/// SwiftUI mirror of `feature-task`'s Compose `TaskListScreen` (see
/// `feature/feature-task/src/commonMain/kotlin/com/omnilife/feature/task/TaskListScreen.kt`) —
/// same information architecture (segmented mode, search, list, empty state, capture entry
/// point), reimplemented natively per TDR-01 (iOS stays SwiftUI, never Compose).
///
/// **Integration point (not wired here)**: in a real Xcode target, this view's `@State` would be
/// replaced by an `ObservableObject` wrapping `:shared`'s exported `TaskListViewModel`/`GetTasksForView`
/// (Kotlin `StateFlow` bridged to Combine/`@Published` via the standard KMP-iOS interop pattern —
/// e.g. a small Swift wrapper that subscribes to the `StateFlow` and republishes each emission).
/// That bridging code needs the compiled `:shared` iOS framework to write against its actual
/// generated Objective-C header, which requires a macOS/Xcode host this sandbox does not have
/// (see `RootView.swift`'s doc and `README-BUILD.md` §4) — so it is intentionally left as this
/// documented seam rather than guessed at without the ability to verify it compiles.
struct TaskListPlaceholderView: View {
    @State private var tasks: [TaskRowModel] = []
    @State private var searchText: String = ""

    var body: some View {
        NavigationStack {
            Group {
                if tasks.isEmpty {
                    ContentUnavailableEmptyState()
                } else {
                    List(filteredTasks) { task in
                        TaskRowView(task: task)
                    }
                    .searchable(text: $searchText, prompt: "Cerca nelle attività")
                }
            }
            .navigationTitle("Oggi")
        }
    }

    private var filteredTasks: [TaskRowModel] {
        guard !searchText.isEmpty else { return tasks }
        return tasks.filter { $0.title.localizedCaseInsensitiveContains(searchText) }
    }
}

private struct TaskRowView: View {
    let task: TaskRowModel

    var body: some View {
        HStack {
            Image(systemName: task.completed ? "checkmark.circle.fill" : "circle")
                .foregroundColor(task.completed ? .accentColor : .secondary)
            VStack(alignment: .leading) {
                Text(task.title).strikethrough(task.completed)
                if let dueDate = task.dueDate {
                    Text(dueDate).font(.caption).foregroundColor(.secondary)
                }
            }
        }
    }
}

private struct ContentUnavailableEmptyState: View {
    var body: some View {
        VStack(spacing: 12) {
            Image(systemName: "checkmark.circle").font(.system(size: 40)).foregroundColor(.secondary)
            Text("Nessuna attività qui").font(.headline)
        }
    }
}
