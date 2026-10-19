# Maintainer task: publish Mantelcats on CurseForge

> GitHub issues are disabled on this fork (Settings → General → Features → Issues to
> enable). This file is the "issue" until then — complete the checklist and delete it.

The tag-driven release pipeline already exists
(`.github/workflows/release.yml` + the `publishMods` blocks). What remains:

## Checklist

- [ ] Create a CurseForge project for Mantelcats (new mod listing; approval can take
      a day — do this first)
- [ ] Get the project ID (shown on the project's About page)
- [ ] Replace the **upstream copycats+ project ID** in both publish blocks:
  - `neoforge/build.gradle` → `curseforge { projectId = "968398" }` → your ID
  - `fabric/build.gradle` → same line → your ID
- [ ] Add repo secret `CURSEFORGE_TOKEN` (Settings → Secrets and variables → Actions)
      — API token from https://console.curseforge.com/#/api-keys
- [ ] Add `MODRINTH_TOKEN` too — the publish step is **skipped silently unless both
      secrets exist** (`if: env.MODRINTH_TOKEN != '' && env.CURSEFORGE_TOKEN != ''`)
- [ ] Verify with a beta tag: `git tag v3.0.10-beta+mc1.21.1 multiloader && git push
      origin v3.0.10-beta+mc1.21.1` — the file should land on CurseForge as **beta**

## Tag → channel convention

| Tag | Channel |
|---|---|
| `*alpha*` anywhere in name | Alpha |
| `*beta*` anywhere in name | Beta |
| anything else | Release |
| `v*-dev*` | workflow never runs |

## Cautions

- **License**: upstream Copycats+ is all-rights-reserved; its terms require a
  *substantial* code modification before redistribution. Confirm our standing — or
  get upstream's blessing — before the first public upload.
- Only `:neoforge:publishMods` runs. The fabric module is upstream-broken (Create
  dependency commented out), so no fabric file will publish until that's fixed.
- Leave the `requires`/`optional` dependency IDs alone — they point at Create/JEI.
- Local dry run: `./gradlew :neoforge:publishMods -PdoDryRun=true -PreleaseType=beta`

See `RELEASING.md` for the full pipeline doc.
