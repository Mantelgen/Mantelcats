# Mantelcats

A fork of [Create: Copycats+](https://github.com/copycats-plus/copycats) focused on fixing
multistate material-storage bugs — crashes, memory leaks and invisible blocks that affect
byte panels, boards, bytes, slabs, half layers and cogwheels.

Mantelcats tracks upstream `copycats-plus/copycats` (`multiloader` branch) and layers
targeted fixes on top. All copycat content, features and behavior come from upstream;
this fork only hardens the code around them.

**Target environment:** Minecraft 1.21.1 · NeoForge · Create 6.x

> **Breaking change (4.0.0):** the registry namespace and mod id are now
> `mantelcats` — every `copycats:*` block/item id moved to `mantelcats:*`.
> Upgrading removes all previously placed copycat content from existing worlds;
> this fork is intended for fresh installs/new worlds. No migration is provided.

## What this fork fixes

All of the following have been verified in-game on a live NeoForge server and client:

- **Client crashes on missing/foreign material data** — every `getMaterialItem` lookup in
  render, interaction, kinetic and transform paths now tolerates absent storage entries,
  stale block entities and partially deserialized data instead of throwing NPEs.
  (upstream #423 follow-up sites, #430, and related chunk-meshing crashes)
- **Crashes on invalid part properties** — Byte/BytePanel/Board property resolution no
  longer throws on foreign or missing keys (e.g. `bottom_southeast` reaching a byte
  panel through stale NBT or cross-part hits), including the `getColorIndex`/`bite` NPE.
  (upstream #396, #431, #371)
- **Multistate memory leak** — `CopycatCogWheelBlockEntity` now clears its material
  storage on `invalidate()`, closing the leak path the upstream #420 fix missed.
- **Invisible unskinned copycats** — block entities redraw on every client sync read,
  not only on material change, so copycats placed by contraption disassembly render
  immediately. (upstream #419)
- **Structure migration NPE** — `MigrationManager` resolves registry access from the
  running server instead of `be.getLevel()`, which is null for detached block entities.
  (upstream #414)
- **Tag-driven release pipeline** — tagging `v*alpha*`, `v*beta*` or `v*` publishes to
  the alpha, beta or release channel on CurseForge/Modrinth automatically once the
  maintainer configures project IDs and tokens.

A complete, issue-by-issue audit of the fork against upstream is in the repository
history; each fix ships as an independently reviewable commit.

## Downloads

- **GitHub**: build from source (below) or grab tagged builds under
  [Releases](https://github.com/Mantelgen/Mantelcats/releases) when published.
- **CurseForge / Modrinth**: publishing is set up but not yet enabled — see the open
  maintainer issues for the remaining configuration steps.

## Building

Requires **Java 21** and a checkout **with git tags** (the build derives its version
from `git describe`):

```bash
git clone https://github.com/Mantelgen/Mantelcats.git
cd Mantelcats
git fetch https://github.com/copycats-plus/copycats.git '+refs/tags/*:refs/tags/*'
./gradlew :neoforge:build
```

The NeoForge jar lands in `neoforge/build/libs/`. Dev runs are available as
`:neoforge:runClient` / `:neoforge:runServer`.

Known build quirks inherited from upstream:

- The `:neoforge` module asks for RenderDoc by default; set `CI=true` (or omit
  `useRenderDoc`) to skip it.
- The `fabric` module does not currently compile — upstream has the Create dependency
  commented out pending a 1.21 port, and `CopycatMaterialStore` was removed upstream.
  NeoForge is the supported loader on this fork.

## Usage

Same usage rules as upstream: all copycat blocks work identically — place a copycat,
right-click it with any block to assign its material, wrench to cycle or toggle
connected textures. See the
[upstream wiki](https://github.com/copycats-plus/copycats/wiki) for feature docs.

## License

The original Copycats+ code is **all rights reserved** by its authors
([LICENSE](LICENSE)); upstream permits modpack inclusion and modification but requires
a substantial portion of the code to be modified before redistribution. Please respect
those terms, and prefer reporting bugs to
[upstream](https://github.com/copycats-plus/copycats/issues) when they are not
fork-specific.

## Contributing

Bug reports and PRs are welcome — see [CONTRIBUTING.md](CONTRIBUTING.md).

## Credits

- [copycats-plus](https://github.com/copycats-plus) — the original mod
- [Creators of Create](https://github.com/Creators-of-Create/Create) — the Create mod
  this is an addon for
