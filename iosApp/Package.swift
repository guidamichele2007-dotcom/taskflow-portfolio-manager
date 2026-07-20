// swift-tools-version:5.10
// Bootstrap scope only (Engineering Plan, EPIC-00). See iosApp/README.md for
// why this is a Swift Package rather than a hand-authored .xcodeproj, and
// for what the first development sprint still needs to add.
import PackageDescription

let package = Package(
    name: "OmniLifeApp",
    platforms: [
        .iOS(.v17),
    ],
    products: [
        .library(name: "OmniLifeApp", targets: ["OmniLifeApp"]),
    ],
    targets: [
        .target(
            name: "OmniLifeApp",
            path: "Sources/OmniLifeApp"
        ),
        .testTarget(
            name: "OmniLifeAppTests",
            dependencies: ["OmniLifeApp"],
            path: "Tests/OmniLifeAppTests"
        ),
    ]
)
