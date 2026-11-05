plugins {
    kotlin("jvm") version "2.2.21" apply false
    // Every Kotlin module's public API is a checked-in dump (<module>/api/<module>.api); apiCheck fails on any
    // difference (Droidtop/tracker#867: stable library interfaces). scripts/check-api-compat.sh adds the version rule.
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.18.2"
}

subprojects {
    group = "dev.droidtop.lib.plugins"
    // The Kotlin modules release with the repository's version, the root Cargo.toml's.
    version = Regex("""^version = "([^"]+)"""", RegexOption.MULTILINE).find(rootProject.file("Cargo.toml").readText())!!.groupValues[1]
}
