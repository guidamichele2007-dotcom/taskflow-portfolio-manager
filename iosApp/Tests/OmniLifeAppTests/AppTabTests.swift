import XCTest
@testable import OmniLifeApp

/// Not verified in this sandbox (see RootView.swift's doc) — written to standard XCTest
/// conventions but not run by `swift test`.
final class AppTabTests: XCTestCase {
    func testExactlyFourTabs() {
        XCTAssertEqual(AppTab.allCases.count, 4)
    }

    func testEveryTabHasANonEmptyTitle() {
        for tab in AppTab.allCases {
            XCTAssertFalse(tab.title.isEmpty)
        }
    }
}
