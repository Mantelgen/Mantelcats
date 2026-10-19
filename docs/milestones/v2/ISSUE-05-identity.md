# Issue 05 — Product identity pass + fabric module fate

**Type:** docs/metadata · **Status:** `needs-input` (one owner decision) ·
**Owner:** unassigned · **Milestone:** v2 · **Priority:** medium

## Problem

For v2 to present as its own product — and for redistribution to read as a diverged
mod rather than a reupload — the distributed metadata should carry Mantelcats
identity. Separately, the fabric module has been dead since before the fork
(upstream commented out the Create dependency and deleted `CopycatMaterialStore`)
and drags CI surface for no benefit.

## Intended behavior

- `neoforge.mods.toml` / `fabric.mod.json` and related metadata: display name
  **Mantelcats**, own description text, own logo file (reuse `icon.png` only if it
  isn't upstream's branding — check and replace if it is), author/credit lines
  naming Mantelcats + crediting copycats-plus.
- `update.json` URL points at this repo's update source, not upstream's.
- **Decision recorded (recommended default): keep the `copycats:` mod ID and block
  namespace.** Renaming the namespace breaks every existing world using the blocks;
  divergence does not require it. The PR must state the decision and rationale.
- **Owner decision needed: fabric module.** Recommendation — **remove it**
  (`fabric/` + its CI wiring): it's dead upstream, it halves the build surface, and
  "NeoForge-focused" is an honest, defensible fork scope. Alternative: restore it
  (port `CopycatMaterialStore` + re-enable the Create dep) — only worth it if a
  fabric release is actually wanted.

## Affected areas

`neoforge/src/main/resources/META-INF/neoforge.mods.toml` (or equivalent),
`fabric/src/main/resources/fabric.mod.json`, `update.json`, `icon.png`,
`gradle.properties` metadata fields, `fabric/` module + CI wiring.

## Non-goals

- Renaming the `copycats` package or registry namespace (documented decision).
- Marketing copy beyond the mod metadata.

## Acceptance criteria

- [ ] The built jar reports itself as Mantelcats in the mod list (in-game or
      `mods.toml` content check).
- [ ] No remaining references to upstream donation/sponsor/Discord surfaces in the
      shipped metadata.
- [ ] Fabric decision recorded in the PR + `RELEASING.md` updated to match.
- [ ] `:neoforge:build` still passes.

## Verification surface

Jar metadata inspection + server boot log (`copycats`/`mantelcats` mod entry).

## Dependencies

None.

## Unresolved decisions

- Fabric module: remove (recommended) vs restore — **owner call**.

## PR contract

Mandatory contract as in issue 01, plus the recorded namespace + fabric decisions.
