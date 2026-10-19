# Milestone: Mantelcats v2 — material divergence from upstream

**Status:** ready-for-handoff (issue 05 is `needs-input` on one owner decision)

## Outcome

Mantelcats v2 is a materially diverged mod — not a bugfix fork with cosmetic churn —
such that redistribution on CurseForge/Modrinth is defensible under upstream's
"substantial portion of the mod's code" clause **and** Modrinth's own fork rule
("diverged substantially from the original project").

Divergence is achieved through **substance**, not diff size:

1. Rework the material-storage subsystem — the component all the fork's fixes live in.
2. Ship 2–3 genuinely new copycat features absent from upstream.
3. Present as its own product (Mantelcats identity, own docs, own release line).

Parallel track (repo owner's task, not this milestone): ask upstream for explicit
publish permission — see decision context in
`docs/maintainer/CURSEFORGE-SETUP.md` / `MODRINTH-SETUP.md`.

## Scope boundary

**In:** the issues below, NeoForge/Minecraft 1.21.1, `multiloader` branch.

**Out:** Fabric loader support (upstream-broken; fate decided by issue 05), ports to
other MC versions, mechanical mass-refactoring (explicitly rejected as a divergence
strategy — it is cosmetic churn, not substance).

## Issues

| # | Title | Status | Depends on |
|---|-------|--------|-----------|
| 01 | [Rework material storage + access subsystem](ISSUE-01-material-subsystem.md) | ready | — |
| 02 | [Feature: Copycat Casing](ISSUE-02-copycat-casing.md) | ready | — |
| 03 | [Feature: Copycat Corner (Half) Panel](ISSUE-03-corner-half-panel.md) | ready | — |
| 04 | [Feature: multistate slices](ISSUE-04-multistate-slices.md) | ready | 01 |
| 05 | [Product identity pass + fabric module fate](ISSUE-05-identity.md) | needs-input | — |
| 06 | [Release v2.0.0-alpha](ISSUE-06-release-alpha.md) | ready | 01–05 + maintainer secrets |
| 07 | [Optional: offer merged fixes upstream as PRs](ISSUE-07-upstream-prs.md) | ready (optional) | — |

## Execution order

Issues 01, 02, 03, 05, 07 are independent. 04 depends on 01. 06 last.

## Milestone risks

- **License is the gating risk, not code.** Even a strong v2 benefits from upstream's
  written blessing — the owner's Option-A track should not be skipped.
- Issue 01 changes the on-disk NBT schema → world migration risk; acceptance criteria
  require old saves to load intact.
- Feature issues (02–04) need in-game verification, not compile-only sign-off — same
  standard the fix PRs were held to.
- Each new block type expands the surface the material subsystem must cover; landing
  01 first keeps 02–04 cheaper.
