# Sprint 6: minimal starting point for the `release` build type's isMinifyEnabled/isShrinkResources.
# AGP's own default rules (getDefaultProguardFile("proguard-android-optimize.txt")) already cover
# the Android framework surface; nothing app-specific is known to need a keep rule yet — this file
# exists so `release` has a real, reproducible shrink pass rather than an empty/missing one.
