import Foundation

/// Navigation Bible §3: exactly 4 fixed tabs — Oggi · Moduli · Cerca · Profilo, never dynamic by
/// active-module count. Mirrors the Kotlin side's `AppDestination`/tab model conceptually (no
/// shared type crosses the KMP↔Swift boundary for navigation itself — Navigation Bible §3 treats
/// navigation as an app-shell concern, not a `shared` domain concept).
///
/// **Not verified in this sandbox**: no macOS/Xcode host is available here (see
/// `../README.md`) — this file follows standard Swift conventions but has not been compiled by
/// `swift build`.
public enum AppTab: String, CaseIterable, Identifiable {
    case oggi
    case moduli
    case cerca
    case profilo

    public var id: String { rawValue }

    public var title: String {
        switch self {
        case .oggi: return "Oggi"
        case .moduli: return "Moduli"
        case .cerca: return "Cerca"
        case .profilo: return "Profilo"
        }
    }
}
