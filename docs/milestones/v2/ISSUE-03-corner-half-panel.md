# Issue 03 — Feature: Copycat Corner (Half) Panel

**Type:** feature · **Status:** ready-for-handoff · **Owner:** unassigned
**Milestone:** v2 · **Priority:** medium

## Problem

Upstream request [copycats-plus/copycats#399](https://github.com/copycats-plus/copycats/issues/399):
a corner-sized panel variant — between the half panel and the byte panel family,
covering shapes upstream doesn't offer.

## Intended behavior

A new multistate copycat block providing corner half-panels:

- two quarter-panel corners per block face (or per issue's chosen shape model —
  state the chosen geometry in the PR);
- each corner is an independently assignable material part (multistate storage,
  string-keyed as in existing multistate blocks until issue 01 lands);
- placement follows the byte-panel interaction model (hit position picks the part);
- registered with item, recipe, lang, feature toggle like other copycats.

## Affected areas

New block + model core + block entity usage under
`common/.../content/copycat/corner_panel/` (or analogous naming), registration,
datagen, feature toggle.

## Non-goals

- Double-sided panels (that's upstream #398 — separate follow-up if wanted).
- Changes to existing panel blocks' shape or state.

## Acceptance criteria

- [ ] Both corners placeable/removable independently; per-part materials render
      correctly (screenshot evidence).
- [ ] Storage NBT survives save/load and chunk unload/reload; foreign-key NBT does
      not crash (inherits the issue-01 or PR-#2-style guards).
- [ ] Sneak-wrench removal semantics match `CopycatBytePanelBlock.onSneakWrenched`.
- [ ] Feature toggle disables cleanly.

## Verification surface

In-game server + client: multi-part placement, per-part materials, wrench removal,
unload/reload.

## Dependencies

None strictly; implementing after issue 01 is cheaper (typed parts).

## PR contract

Same mandatory contract as issue 01: intent, expectation, acceptance, affected
areas, non-goals, verification evidence, assumptions.
