# The plugin host's Android interface (AIDL)

The Binder interface between an Android app's plugin host and the isolated process a plugin runs in:
`IPluginRuntime`, `IPluginRuntimeCallback`, `IPluginHostBroker` and `IAppInterfaceDoor`, package
`dev.droidtop.pluginhost` (droidtop's docs/plugin-api.md). droidtop's `plugin-host` and Enginehost's app each kept
a hand-made copy, and the copies had already drifted: Enginehost's `IPluginRuntime` still described plugin contract
1, which droidtop retired on 2026-10-29 (droidtop 50e3516). These are droidtop's files, the current ones, moved here
unchanged (Droidtop/tracker#867 step 1).

This is the one Android-only part of lib-plugins (#867 D3: platform-neutral by default; AIDL is how two Android
processes talk). An app compiles it straight from its submodule:

```kotlin
android { sourceSets.getByName("main").aidl.srcDir("../vendor/lib-plugins/android/aidl") }
```

CI compiles every file with the Android SDK's `aidl` tool.
