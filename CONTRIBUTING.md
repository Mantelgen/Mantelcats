# Contributing to Mantelcats

Mantelcats is a bug-fix fork of [Create: Copycats+](https://github.com/copycats-plus/copycats).
Contributions that make multistate copycats more correct, more stable or cheaper at
runtime are welcome; new content blocks belong upstream.

## Bug reports

Open an [issue](https://github.com/Mantelgen/Mantelcats/issues) and include:

- Mantelcats version, Minecraft version, NeoForge version, Create version
- `latest.log` / crash report — the stack trace is the most useful part
- Reproduction steps: which copycat, which material, what you did before it broke

If the bug also exists in unmodified upstream Copycats+, please file it
[upstream](https://github.com/copycats-plus/copycats/issues) as well — fixes flow
back to the community faster that way.

## Pull requests

- Base PRs on `multiloader` and keep them single-purpose — one fix per PR.
- Match the surrounding code style; this codebase mirrors upstream conventions.
- `./gradlew :neoforge:build` must pass. If your change touches shared code, also
  run `:common:compileJava`.
- Describe the upstream issue you are fixing and how you verified it in-game.
  Compile-only evidence is not enough for render/interaction fixes.
- Do not bump versions or edit the changelog in fix PRs; releases are maintainer-driven.

## Setup

See [README](README.MD#building) — Java 21, git tags fetched from upstream, and the
`:neoforge` module is the supported build target.
