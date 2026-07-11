// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt.android) apply false
}

// agconnect plugin is not available in this environment.
// For production builds, the CI uses Huawei Maven repo + classpath directly.
// For local development, compile with huaweiDebug using fallback implementation.
