# Issue 07 — Optional: offer merged fixes upstream as PRs

**Type:** optional/community · **Status:** ready-for-handoff · **Owner:** unassigned
**Milestone:** v2 · **Priority:** low

## Problem

The six merged fixes (PRs #1–#6) exist as clean single-purpose commits — they are
nearly PR-shaped for upstream already. Upstream explicitly invites contributions,
and sending them earns goodwill that supports the license-permission conversation
(Option A, owner track) — plus fixes reach the upstream user base.

## Intended behavior

Port the fork-side fixes to upstream PRs, adapted to upstream conventions:

- null-safe material lookups (#423 follow-ups, #430),
- cogwheel `invalidate()` storage clear (#417/#420 gap),
- non-throwing property resolution (#396/#431/#371),
- redraw-on-client-read (#419),
- level-independent migration registry access (#414).

The CI/release-channel change is fork-specific — do **not** send it.

## Non-goals

- Forcing upstream to accept them; they may decline, rewrite, or fold them into
  their own fixes. That's fine — the point is the offer.

## Acceptance criteria

- [ ] Each fix has an upstream PR (or an upstream issue comment with the patch)
      referencing the corresponding upstream issue.
- [ ] Any upstream-requested changes are tracked back here or in the PR threads.

## Dependencies

None. May be skipped if the owner prefers to land the permission conversation
(Option A) first — do not treat this as a blocker for anything.

## PR contract

N/A — delivers upstream PRs, not PRs to this repo.
