# Issue 01 — Rework material storage + access subsystem

**Type:** refactor/architecture · **Status:** ready-for-handoff · **Owner:** unassigned
**Milestone:** v2 · **Priority:** highest (foundation for issue 04)

## Problem

`MaterialItemStorage` is a stringly-keyed `Map<String, MaterialItem>` that:

- accepts **any** key on `deserialize` — foreign keys (e.g. `bottom_southeast` on a
  byte panel) persist and poison downstream property lookups;
- tolerates null entries, so ~30 call sites each had to grow their own null guards
  (see PR #2 — the guard-everywhere pattern treats symptoms, not causes);
- is created from `storageProperties()` but queried with arbitrary strings, so
  stale/mismatched block entities crash on `getMaterialItem(key).material()`.

## Intended behavior

1. **Schema-validated storage.** On deserialize, keys outside the block's declared
   `storageProperties()` are ignored (and ideally logged once at FINE/debug).
   Storage can never contain a part the block doesn't own.
2. **Typed part keys.** Part identifiers become a typed value (enum or record) per
   block family rather than raw strings, so "query a part that doesn't exist" is a
   compile-time/registration error, not a runtime NPE.
3. **One accessor contract.** All material reads go through a single API with
   defined fallback semantics (missing part → copycat-base default); write paths
   no-op or create-with-default per contract. The per-call-site null guards from
   PR #2 collapse into the accessor — grep must show zero raw
   `.getMaterialItem(` dereferences outside the accessor itself.
4. **Versioned serialization + migration.** `material_data` gains a schema version;
   `MigrationManager` (or the BE load path) upgrades pre-rework NBT, preserving
   valid materials and discarding foreign keys. Existing worlds must load intact.

## Affected areas

`common/.../multistate/MaterialItemStorage.java`,
`IMultiStateCopycatBlockEntity`, `MultiStateCopycatBlockEntity`,
`IMultiStateCopycatBlock`, `MigrationManager`, every call site guarded by PR #2
(slab, half-layer, byte/byte-panel/board, cogwheel, kinetic render data, both
platform mixins, `CopycatModelNeoForge`, `BlockEntityUtils`).

## Non-goals

- Changing quad assembly / model cores / CT logic.
- Fabric module (dead upstream — issue 05 decides its fate).
- New content blocks.

## Acceptance criteria

- [ ] Foreign keys in `material_data` NBT are dropped at load; `data get` shows only
      declared parts afterward.
- [ ] `grep` for `getMaterialItem(` outside the accessor module returns zero hits.
- [ ] A world saved with pre-rework copycats loads with materials intact (migration
      proof — keep a test world NBT fixture).
- [ ] In-game re-verified on dedicated server + client: placement, material
      assignment, NBT merge/clone, chunk unload/reload, TNT removal — zero exceptions.
- [ ] `:neoforge:build` passes; no new warnings.

## Verification surface

Real Minecraft server (place/poison/unload cycle as done in the fix test pass) +
client render check. Compile-only is not sufficient.

## Dependencies

None.

## PR contract

PR body must repeat: intent, expected behavior, acceptance criteria checklist,
affected areas, non-goals, verification evidence (commands run + in-game results),
assumptions/open decisions.
