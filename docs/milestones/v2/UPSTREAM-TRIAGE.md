# Upstream issue triage — copycats-plus/copycats

Snapshot of all **183 open issues** on `copycats-plus/copycats` as of this writing,
classified against Mantelcats (`multiloader`, MC 1.21.1 / NeoForge 21.1.x).

> The fork has GitHub issues disabled (by design); upstream fixes are delivered as PRs on
> `fix/upstream-NNN-*` branches. This document is the bookkeeping surface.

## Delivered via PR

| Upstream issue(s) | Subject | Fork PR |
|---|---|---|
| #194, #211 | Config to not consume material blocks | PR #17 |
| #375 | Config to disable Copycat Cats | PR #18 |
| #314 | `Discarding @Unique` mixin log spam | PR #19 |
| #238, #373, #403 | Missing-texture particles (landing/sprint/break) | PR #20 |
| #377 | Missing door/trapdoor block+item tags | PR #14 |
| #282 | Super Glue on ghost blocks | PR #15 |
| #425 | SU impact/capacity config ignored | PR #16 |
| #369 | Vertical slope hitbox offset/gap (`CCShapes.VERTICAL_SLOPE` off-by-one) | PR #22 |
| #182 | Modded light emission not delegated on NeoForge | PR #24 |
| #334 | Door texture doesn't rotate with panel when opened | PR #23 |
| #410 | Materials lose part assignment on structure rotate/mirror (BE NBT never transformed) | PR #25 |
| #307 | Adjacent boards cull front board's interior back face | PR #26 |
| #412 | Doors/sliding doors counted 2× by schematicannon (per-half requirements) | PR #27 |
| #414 | NPE in `MigrationManager.migrateStructure` | PR #6 (merged) |
| #417, #420 | Memory leak: multistate BEs never released | PR #3 (merged) |
| #419 | Copycats stay invisible after contraption disassembly | PR #5 (merged) |
| #371, #423, #431 | NPE on null `MaterialItem` lookups (incl. contraption sprint crash) | PR #2 (merged) |
| #396 | Crash running over Copycat Byte Panel | PR #2/#4 (merged) |
| #430 | Copycat slabs crash clients (3.0.9) | PR #2/#3 (merged) |
| #428, #58 | Copycat Casing | PR #12 (merged) |
| #399 | Copycat Corner Half Panels | PR #13 (merged) |
| #392 | Multi-state for slices | PR #13 (merged) |

## Already fixed in the fork (verified in code/data)

| Issue | Evidence |
|---|---|
| #259, #166 | Missing `criteria` in recipe advancements — generated advancement JSONs all contain `criteria` (0 files missing). |
| #202 | Vertical stairs ↔ stairs conversion recipes — correct shapeless recipes in both directions in generated data. |
| #160 | Copycat stonecutter recipes — generated stonecutting recipes present. |
| #42 | Feature selections ignored — generated recipes carry `copycats:feature_enabled` conditions; feature toggles gate crafting. |
| #54 | Villager pathfinding on slabs — `WalkNodeEvaluatorMixin` (+ Radium/Lithium twin `PathNodeDefaultsMixin`) substitutes `SMOOTH_STONE_SLAB` in `getPathTypeFromState`; mixin registered and target verified present in 1.21.1. |
| #127 | Copycat sliding doors — implemented upstream and shipped. |
| #400 | NPE `bite.x` on migrated copycat byte (Sodium chunk build) — `getVectorFromProperty` null-guards unknown property keys and `MaterialItemStorage.deserialize` drops keys not declared by the storage. |
| #435 | `MaterialItem.enableCT()` NPE during section batching — all `enableCT()` call sites are null-guarded (`IMultiStateCopycatBlockEntity:103`, `CopycatModelNeoForge:133`, `IMultiStateCopycatBlock:203`). |

## Likely fixed in the fork (needs one in-game verification)

| Issue | Why we believe it's covered | Verification needed |
|---|---|---|
| #279 | Crash placing cogwheel (1.21.1 NeoForge) — null MaterialItem guards (PR #2) + cogwheel storage invalidation (PR #3) | Place copycat cogwheel, apply materials |
| #326 | Schematicannon material duplication — `ICopycatBlockEntity#getRequiredItems` returns consumed item as `CONSUME`; multistate returns all material stacks | Schematicannon with filled copycats |
| #391 | Slices crash on train assembly — crash was in upstream `sable$getNormal` (Sable compat injection); slice family was rewritten in PR #13 and carries no `sable$` methods | Assemble train with slices (+ Sable) |
| #165 | Slopes culled when viewed from opposite direction — same `hidesNeighborFace` occlusion mechanism as #307 | View angled copycats from multiple directions |
| #222 | Crash pasting flipped schematic with sliding doors (1.20.1, logs expired) — same BE-transform-on-paste class as #410 | Paste rotated/mirrored schematic with doors |
| #227 | No light emission in contraptions — aux-light-manager-less path now delegates to material (PR #24) | Light-emitting material on a contraption |
| #341, #342 | Copycats not emissive with glowing blocks — block-light level delegation shipped (PR #24) | Glowing/emissive modded blocks as material |
| #221 | Slopes only reach half height (v2.2.2-era) — current `CCShapes` slope heights reach 16px; reworked in 3.0 | Walk a slope in 1.21.1 |
| #338 | Doors reject materials (code path verified sound end-to-end; reporter's pack has ~150 mods incl. interaction mods) | Vanilla+mod env check; try other halves/items |

## Queued bounded bugs (next candidates)

| Issue | Notes |
|---|---|
| #175 | Light propagates through opaque-material copycats — **harder than it looks**: 1.21.1 caches `getLightBlock`/`propagatesSkylightDown` per blockstate (position/material-blind), so it needs a light-engine mixin or per-position occlusion strategy, not a simple delegate. Needs design. |


## Environment-dependent (cannot reproduce without third-party setup)

These report against specific mods, loaders, or MC versions outside the fork's
1.21.1 NeoForge dev target. Actionable only with a dedicated reproduction env.

- **Aeronautics**: #381 (feature), #434
- **VulkanMod**: #415
- **MTR + Create Connected**: #416, #439
- **Axiom**: #388
- **WorldEdit**: #138, #265
- **Sodium/Iris/Indium**: #47, #55, #356, #117
- **EMI**: #270, #374, #339
- **Continuity/CTM**: #51
- **Quark**: #191, #333
- **Marioverse**: #283 | **ScalableLux**: #232 | **Tinkers**: #278 | **Caupona/Handcrafted**: #429 | **createunlimited**: #254
- **Sable**: #437 (unknown block report — Sable-side or needs Sable env)
- **1.20.1/Forge/Fabric-era reports, loader outside target**: #33, #36 (Quilt), #212, #297, #302, #331, #340, #347, #349, #353, #411
- **Crash reports with expired/no logs (needs-info)**: #7, #251, #253, #269, #291, #301, #317, #321, #327, #335, #351, #404, #408, #427, #438, #355

## Feature requests (separate milestones — not bugfix scope)

#9, #38, #46, #59, #78, #82, #98, #115, #122, #143, #146, #154, #158, #163,
#181, #185, #197, #198, #200, #207, #208, #209, #214, #216, #219, #226, #228,
#246, #256, #263, #271, #272, #289, #290, #293, #294, #295, #330, #332, #337, #346,
#348, #354, #357, #364, #366, #376, #378, #380, #384, #385, #387, #398, #401,
#405, #418, #424, #426, #432

## Non-actionable

- #436 — question about promo-image corner (answered upstream).
- #262 — question about build naming.
- #264 — OSI-approved license: **maintainer decision required** (relicensing policy question, out of code scope).

## Remaining bug reports — classified

**Contraption / train / Create-tooling issues** (need Create contraption or addon
environment to reproduce; transform paths were hardened by PR #25):
#329 (sliding door render on train), #255 (doors on trains), #31 (slab collision
stops flight), #34 (fence gate can't open on train), #39 (half-panel rotation on
Interactive train — transform math verified correct for yaw; addon-specific),
#21 (Mirror Wand placement), #239 (placement assist: no initial render).

**Connected-texture directionality**: #231 (stair CT), #168 (slope CT vertical),
#53 (stairs on bent connections), #196 (pillar/casing connection) — need CT
connectivity rule review per rotated face.

**Emissive texture pass-through** (light *level* fixed by PR #24; layered
emissive quads from e.g. Ars Lumos are a separate render-path question):
#324 (Ars Nouveau/Lumos, 1.21.1 NF — reproducible env exists).

**Performance reports**: #370 (slope/layer lag), #187 (trains), #225 (CT on
contraptions), #73 (general) — need profiling setup.

**Legacy-version reports** (1.19.2/1.20.1, mechanism may persist but unverified):
#220 (door z-fighting — check coplanar quads in door model core), #241 (Create's
own panel block shows borders with bar materials when Copycats+ installed —
our mixin alters Create's block, worth a 1.21.1 check), #170 (fence CT),
#159 (corner stair x-ray, Fabric 1.20.1), #80 (vertical stairs).
