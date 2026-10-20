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

## Likely fixed in the fork (needs one in-game verification)

| Issue | Why we believe it's covered | Verification needed |
|---|---|---|
| #279 | Crash placing cogwheel (1.21.1 NeoForge) — null MaterialItem guards (PR #2) + cogwheel storage invalidation (PR #3) | Place copycat cogwheel, apply materials |
| #326 | Schematicannon material duplication — `ICopycatBlockEntity#getRequiredItems` returns consumed item as `CONSUME`; multistate returns all material stacks | Schematicannon with filled copycats |
| #391 | Slices crash on train assembly — crash was in upstream `sable$getNormal` (Sable compat injection); slice family was rewritten in PR #13 and carries no `sable$` methods | Assemble train with slices (+ Sable) |

## Queued bounded bugs (next candidates)

| Issue | Notes |
|---|---|
| #369 | Vertical slope incorrect hitbox — inspect voxel shapes vs. vanilla. |
| #334 | Door textures don't rotate when opened — material model orientation per door state. |
| #182, #175 | Light emission/propagation not copied from material — inspect `getLightEmission` chain. |
| #221 | Slope steepness at half of expected amount — shape/property math. |
| #290 | Fill multiple copycats with material (feature-ish but small). |

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
- **Crash reports with expired/no logs (needs-info)**: #7, #251, #253, #269, #291, #301, #317, #321, #327, #335, #338, #351, #400, #404, #408, #410, #412, #427, #435, #438, #355, #307

## Feature requests (separate milestones — not bugfix scope)

#9, #38, #46, #59, #78, #82, #98, #115, #122, #143, #146, #154, #158, #163,
#181, #185, #197, #198, #200, #207, #208, #209, #214, #216, #219, #226, #228,
#246, #256, #263, #271, #272, #289, #293, #294, #295, #330, #332, #337, #346,
#348, #354, #357, #364, #366, #376, #378, #380, #384, #385, #387, #398, #401,
#405, #418, #424, #426, #432

## Non-actionable

- #436 — question about promo-image corner (answered upstream).
- #262 — question about build naming.
- #264 — OSI-approved license: **maintainer decision required** (relicensing policy question, out of code scope).

## Remaining bug reports needing deeper triage (not yet classified)

#21, #31, #34, #39, #53, #73, #80, #159, #165, #168, #170, #187, #196, #220,
#222, #225, #227, #231, #239, #241, #255, #324, #329, #341, #342, #370
