import SwiftUI

/// Root view: the 4-tab shell (Navigation Bible §3). Real screens exist so far only for "Oggi"
/// (`TaskListView`, a best-effort SwiftUI mirror of `feature-task`'s Compose `TaskListScreen`) —
/// every other tab stays an honest placeholder (`ComingSoonView`), never invented content.
///
/// **Not verified in this sandbox**: no macOS/Xcode host is available here (see `../README.md`
/// and `README-BUILD.md` §4) — every file in this package follows standard SwiftUI/Swift Package
/// Manager conventions but has not been compiled by `swift build`, let alone run. The Kotlin side
/// of the shared abstractions this view would consume (`:shared`'s iOS framework, exposing
/// `domain-task`/`domain-account` use cases) is real and unit-tested on the JVM target, but its
/// `iosArm64`/`iosSimulatorArm64` targets are equally unverified here for the same reason.
public struct RootView: View {
    @State private var selectedTab: AppTab = .oggi

    public init() {}

    public var body: some View {
        TabView(selection: $selectedTab) {
            ForEach(AppTab.allCases) { tab in
                tabContent(for: tab)
                    .tabItem { Text(tab.title) }
                    .tag(tab)
            }
        }
    }

    @ViewBuilder
    private func tabContent(for tab: AppTab) -> some View {
        switch tab {
        case .oggi:
            TaskListPlaceholderView()
        case .moduli:
            ComingSoonView(title: "Moduli", message: "La Galleria dei moduli arriva in un prossimo sprint.")
        case .cerca:
            ComingSoonView(title: "Cerca", message: "La ricerca globale arriva in un prossimo sprint.")
        case .profilo:
            ComingSoonView(title: "Profilo", message: "Le impostazioni arrivano in un prossimo sprint.")
        }
    }
}

/// Honest placeholder (never fake content) for a tab whose real SwiftUI screen isn't written yet.
struct ComingSoonView: View {
    let title: String
    let message: String

    var body: some View {
        VStack(spacing: 12) {
            Text(title).font(.title2).bold()
            Text(message).foregroundColor(.secondary).multilineTextAlignment(.center).padding(.horizontal, 32)
        }
    }
}
