import SwiftUI

/// Bootstrap placeholder root view. No navigation, no screens, no business
/// logic — see README-BUILD.md. Real composition of feature modules starts
/// in the first development sprint, once the Xcode app target that embeds
/// this package exists (see ../README.md).
public struct RootView: View {
    public init() {}

    public var body: some View {
        Text("OmniLife — bootstrap")
    }
}
