# Issue 02 — Feature: Copycat Casing

**Type:** feature · **Status:** ready-for-handoff · **Owner:** unassigned
**Milestone:** v2 · **Priority:** high (divergence driver)

## Problem

Upstream request [copycats-plus/copycats#428](https://github.com/copycats-plus/copycats/issues/428):
players want copycat versions of Create casings — a cover that lets pipes and shafts
pass through walls/floors without being seen.

## Intended behavior

A new `copycat_casing` block that:

- wraps around a shaft or cogwheel axis like a Create casing (casing attaches to a
  kinetic block face), OR is a standalone decorative casing block — **implementation
  decides which of Create's casing mechanics is in scope; state the choice in the PR**;
- accepts a material via right-click like every other copycat;
- renders the assigned material; supports CT where the shape allows;
- has a recipe consistent with upstream's casing recipe conventions;
- is registered behind the feature-toggle system like all other copycats.

## Affected areas

New block + block entity + model core under `common/.../content/copycat/casing/`,
block/item registration (`CCBlocks`/`CCBlockEntityTypes`/creative tab), recipes
datagen or json, lang entries, feature-toggle config (`CFeatures`/`CCConfigs`).

## Non-goals

- Encased-fan or full Create-encasement mechanics beyond the visual casing role.
- Casing for fluid pipes in this issue (can be a follow-up).

## Acceptance criteria

- [ ] Block placeable, material-assignable, material survives save/load and
      chunk unload/reload.
- [ ] Renders assigned material correctly from all angles (in-game screenshot
      evidence).
- [ ] Breaks/drops correctly; consumed-item behavior matches other copycats.
- [ ] Feature toggle in config disables it cleanly.
- [ ] No behavior change to existing copycats or Create casings.

## Verification surface

In-game server + client test (place, assign material, unload/reload, screenshot).

## Dependencies

None. (Benefits from issue 01's accessor but doesn't require it.)

## PR contract

PR body must repeat: intent, expected behavior, acceptance checklist, affected
areas, non-goals, verification evidence, assumptions/open decisions — including the
casing-mechanics scope choice.
