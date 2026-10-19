# Issue 06 — Release v2.0.0-alpha

**Type:** release · **Status:** ready-for-handoff (blocked until deps done) ·
**Owner:** maintainer · **Milestone:** v2 · **Priority:** last

## Problem

Ship the first v2 build through the tag-driven pipeline as an **alpha** channel
release.

## Intended behavior

- `mod_version` bumped to `2.0.0` (check `gradle.properties` / neoforge metadata for
  where the version string lives).
- `CHANGELOG.md` gets a `## 2.0.0-alpha` section describing reworked material core +
  new features.
- Tag `v2.0.0-alpha+mc1.21.1` pushed → release workflow builds `:neoforge:build` →
  publishes to CurseForge + Modrinth as **alpha** → creates GitHub release.

## Acceptance criteria

- [ ] Tag push triggers the workflow; the build step passes.
- [ ] Published files land in the **alpha** channel on both platforms (the channel
      mapping from PR #1: `*alpha*` in tag → ALPHA).
- [ ] GitHub release exists with the built jar.
- [ ] `RELEASING.md` steps were followed; secrets + project IDs confirmed set by the
      maintainer checklists (`docs/maintainer/`).

## Verification surface

The release run itself: workflow log + CurseForge/Modrinth file listing showing
alpha type.

## Dependencies

- Issues 01–05 complete (the v2 content).
- Maintainer has completed `docs/maintainer/CURSEFORGE-SETUP.md` and
  `MODRINTH-SETUP.md` (project IDs replaced, both secrets configured).
- Upstream permission answer (Option-A track) ideally resolved before a **release**
  (non-alpha) ship — alpha is the right stage to prove the pipeline.

## Non-goals

- Fabric artifact publishing (module is dead unless issue 05 restores it).

## PR contract

Version/changelog PR repeats: intent, what's in the release, acceptance checklist.
