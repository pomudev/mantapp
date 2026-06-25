plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt.android) apply false
}

allprojects {
    if (tasks.findByName("prepareKotlinBuildScriptModel") == null) {
        tasks.register("prepareKotlinBuildScriptModel") {
            group = "ide"
            description = "Compatibility task for Android Studio Gradle Kotlin build script model sync."
        }
    }
}
