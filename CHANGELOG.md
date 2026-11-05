# Changelog

All notable changes to lib-plugins. Versions follow [Semantic Versioning](https://semver.org/)
(0.x releases may change anything).

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
