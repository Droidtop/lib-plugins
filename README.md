# lib-plugins

Plugins: their manifests, grants and hosts. One of the shared library families of Droidtop/tracker#867: the libraries belong to no program;
each program has its own scaffold and pins a tagged release of each library it uses (#867 D2).

| Part | Where | Holds |
|---|---|---|
| `plugins-contexts` | [`crates/contexts`](crates/contexts) | context adapters: the programs plugins publish to sync their supporting state, offered, approved, installed and run |
| AIDL (Android only) | [`android/aidl`](android/README.md) | the plugin host's Android interface: `IPluginRuntime`, `IPluginRuntimeCallback`, `IPluginHostBroker`, `IAppInterfaceDoor` |

Used by: droidtop-agent (and through it droidtop) for the crate; droidtop and Enginehost for the AIDL, once each
moves its copy onto this one.

Licence: GPL-3.0-only (see `LICENSE`).

## Where the code came from

#867 step 1 moves existing code before changing it ("reuse and contract first, rewrites second"). These
crates are droidtop-agent's, moved as they were:
  - `plugins-contexts` from droidtop-agent `0ec4110` `crates/contexts`

Only the package names changed. Code still imports each crate by its old name (`droidtop_agent_*`), so
nothing that uses it had to be edited; renaming the imports is a later, mechanical change.

## Build and test

```bash
cargo fmt --all -- --check
cargo clippy --workspace --all-targets -- -D warnings
cargo test --workspace
```

CI (`.github/workflows/ci.yml`) runs those on Linux, Windows and macOS. It then builds and tests every
program that uses this library against the commit (#867 D2): droidtop-agent, and droidtop's `native/` on the
runner and for both Android ABIs, each through `scripts/with-this-library.sh`, which moves the program's lock to
this checkout first and fails if a patch goes unused. A `ci/` branch of the same name in the program is built
with this one, for a change both must make. Work lands on `main` from `ci/*` branches
once CI is green.

## Versions

**The interface stays compatible within a version** (Droidtop/tracker#867, owner 2026-11-04: "I should be able to
bump the version number and immediately get the benefit"). A program takes a fix by moving its pin, with no change
to its own code. CI's `semver` job runs `cargo-semver-checks` against the last release: a breaking change fails unless
the version says so (in 0.x, a new minor version) and the CHANGELOG says what broke and what a program changes.

A release is a tag `vX.Y.Z`. CI creates it on a green `main` whose root `Cargo.toml` names a version not
yet tagged, so to release, raise that version (and the CHANGELOG) in the change that needs it. Programs,
and the other libraries, depend on a tag:

```toml
droidtop-agent-core = { package = "sync-core", git = "https://github.com/Droidtop/lib-sync", tag = "v0.1.0" }
```
