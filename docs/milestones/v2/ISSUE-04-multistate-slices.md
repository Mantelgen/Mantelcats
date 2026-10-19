# Issue 04 — Feature: multistate slices

**Type:** feature · **Status:** ready-for-handoff · **Owner:** unassigned
**Milestone:** v2 · **Priority:** medium · **Depends on:** issue 01

## Problem

Upstream request [copycats-plus/copycats#392](https://github.com/copycats-plus/copycats/issues/392):
slice / vertical-slice / corner-slice blocks currently hold one material for the
whole block; players want each slice segment to hold its own material (the
multistate pattern, applied to slices).

## Intended behavior

- `copycat_slice`, `copycat_vertical_slice`, `copycat_corner_slice` become (or gain
  multistate variants where) each occupied segment is an independently assignable
  material part.
- Interaction model follows the byte-panel convention: hit position picks the part,
  right-click assigns that part's material.
- Existing single-material slices in loaded worlds migrate to the new storage with
  the material applied to all occupied segments (no visual regression on load).

## Affected areas

`common/.../content/copycat/slice/`, `vertical_slice/`, `corner_slice/` block
classes, their model cores, block-state properties, storage wiring, migration path.

## Non-goals

- Changing slice geometry or segment counts.
- Migration of slices inside Create contraption NBT if it requires deeper Create
  internals — scope that at implementation time and state it in the PR.

## Acceptance criteria

- [ ] Per-segment materials assignable and rendered (screenshot evidence: two
      segments of one slice with different materials).
- [ ] Old single-material slices load intact after upgrade (world fixture test).
- [ ] Placement/dismantling, wrench cycling, transformStorage behave per-part.
- [ ] Foreign/missing storage keys cannot crash (issue-01 schema behavior).

## Verification surface

In-game: per-part material assignment, migration world load, unload/reload,
screenshots.

## Dependencies

Issue 01 (typed multistate storage makes per-segment parts cheap and safe).

## PR contract

Mandatory contract as in issue 01.
