# Changelog

All notable changes to lib-plugins. Versions follow [Semantic Versioning](https://semver.org/)
(in 0.x, only a new minor version may break the interface; CI's `semver` job enforces it).

## [0.2.1]

### Added
- CI's `semver` job: `cargo-semver-checks` against the last release, so a breaking change fails unless its version and
  this CHANGELOG say so (Droidtop/tracker#867).

### Changed
- `plugins-contexts`: built on lib-sync v0.1.2 (cross-store links, Droidtop/tracker#798), so droidtop-agent can take
  lib-sync v0.1.2 and link one sync-core.

## [0.2.0]

### Added
- `android/aidl`: the plugin host's Android interface (`IPluginRuntime`, `IPluginRuntimeCallback`,
  `IPluginHostBroker`, `IAppInterfaceDoor`, package `dev.droidtop.pluginhost`), which droidtop and Enginehost each
  copied by hand and whose copies had drifted (Enginehost's `IPluginRuntime` still described contract 1). droidtop's
  current files, moved unchanged; CI compiles them with the SDK's `aidl`. Droidtop/tracker#867 step 1.

## [0.1.3]

### Changed
- `plugins-contexts`: a snapshot is never taken over a copy still waiting to be taken in. A copy the adapter
  deferred (the app was open) holds the handheld's changes the app has not got, so `refresh` takes no snapshot
  while a copy is pending, and the copy is delivered first. From droidtop-agent 5222686, which landed in
  droidtop-agent's copy of this crate after it moved here.

## [0.1.2]

### Changed
- Built on lib-sync v0.1.1, as 0.1.1 meant to be. 0.1.1 was released with its pins unchanged (still lib-sync
  v0.1.0), so it is the same as 0.1.0; use 0.1.2.

## [0.1.1]

### Changed
- Built on lib-sync v0.1.1 (sync-core 0.1.1: a forgotten device is told it is not paired), so a program
  using this library and lib-sync links one copy of sync-core.

## [0.1.0]

### Added
- `plugins-contexts`: context adapters: the programs plugins publish to sync their supporting state, offered, approved, installed and run. Moved from droidtop-agent `0ec4110` `crates/contexts` unchanged but for its package name (Droidtop/tracker#867 step 1).
