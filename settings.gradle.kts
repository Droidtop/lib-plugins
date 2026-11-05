rootProject.name = "lib-plugins"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

// The plugin API's UI-as-data in the form a host's own screens are built from: the catalog model droidtop's
// settings and pages are made of, generic over the host (Droidtop/tracker#867, ui-shared step 3). Plain JVM.
include(":ui-data")
