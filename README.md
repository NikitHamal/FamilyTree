# Famy

Famy is a modern, offline-first family tree app for Android. It is built with Kotlin, Jetpack Compose, Material 3, Material Icons, and a local JSON data store so family data stays on the device.

## Highlights

- Home dashboard with quick stats, empty state, and quick actions
- Canvas-based family tree visualization with zoom, pan, viewport culling, and vertical, horizontal, fan, and pedigree layouts
- Family member profiles with vital data, notes, custom fields, relationships, event timeline, and media references
- Relationship editor with validation for duplicate, impossible, and self relationships
- Search and filters for generation, branch, dates, and living/deceased status
- Timeline, statistics, media gallery, settings, help, GEDCOM import/export, and JSON backup/restore
- One-time onboarding after splash
- Minimal crash screen showing the raw stack trace with Copy and Restart buttons
- Public release keystore and CI workflow that builds a signed release APK renamed with the commit SHA

## Build locally

Open the project in Android Studio Panda 4 or newer and sync Gradle.

Command line builds require Android SDK API 36 installed. The GitHub workflow installs/configures Gradle automatically.

```bash
gradle assembleRelease
```

The release signing config uses:

- Store file: `keystore/famy-release.jks`
- Store password: `famy_release_store_password`
- Key alias: `famy-release`
- Key password: `famy_release_key_password`

## GitHub Actions

Push this repository to GitHub. On relevant pushes to any branch except `wip/**`, `.github/workflows/android.yml` builds a signed release APK and uploads it as an artifact named `Famy-release-<short_sha>`.

## Data model

Famy stores the complete app state in `filesDir/famy_state.json`. JSON backup/restore uses the same schema, and GEDCOM import/export supports core genealogy fields for interoperability.

## Fonts

The theme is prepared with a clean geometric sans-serif style. Font binaries are intentionally not bundled. If you own or have rights to distribute Poppins font files, place them in `app/src/main/res/font/` and wire them into `presentation/theme/Type.kt`.

## License

MIT
