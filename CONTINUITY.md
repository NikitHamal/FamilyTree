Goal (incl. success criteria):
- Rebuild Famy into a more premium, minimal, Notion-like Android app with substantially improved UI/UX, better tree visualization, stronger onboarding, and retained offline-first behavior.
- Deliver a corrected project zip that builds and reflects the upgraded design language.

Constraints/Assumptions:
- Kotlin + Jetpack Compose + Material 3.
- Offline-first local storage only.
- Must remain buildable on GitHub Actions with existing signing workflow.
- No TODOs or placeholder architecture; keep files modular.
- User wants references used as inspiration, not exact copies.

Key decisions:
- Keep single-module app for deliverability, but improve design system and screen composition.
- Introduce a premium dark theme with Notion-like spacing, elevated cards, cleaner typography, and less clutter.
- Improve tree experience with better node cards, branch tinting, legend, layout switcher, and smoother gestures.
- Improve home/dashboard and member profile flows first because these most affect perceived quality.

State:
- Done: Extracted prior Famy_fixed_v2 project. Reviewed current failure history and new UX feedback.
- Now: Refactoring UI/design system and selected screens for higher quality experience.
- Next: Package upgraded zip and provide handoff notes.

Open questions (UNCONFIRMED if needed):
- UNCONFIRMED: Full live compile cannot be run in sandbox due lack of Android SDK/network; validate structure and package carefully.

Working set (files/ids/commands):
- Workspace: /mnt/data/famy_work/project/Famy
- References: /mnt/data/ghostwriter_images/context/*.png

- Done: Applied upgraded theme, redesigned app shell/drawer, rebuilt onboarding, home, members, search, member profile, and tree experience.
- Constraint note: attempted Gradle assemble, but wrapper download failed because network access to services.gradle.org is blocked in this sandbox.
- Next: user can build locally or in CI using the included project zip.
