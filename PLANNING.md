# Naira Assistant Project Planning

This file is the project-root source of truth for active project plans and planning conventions. Keep project planning in Markdown so decisions, scope, implementation steps, dependencies, and verification criteria remain reviewable alongside the project.

## Planning requirements

- Before a multi-file, architectural, security-sensitive, data-related, or otherwise high-risk change, add or update a plan here before implementation.
- Keep a plan concise and actionable. State the problem, intended outcome, scope and non-goals, relevant existing architecture, implementation steps, dependencies, risks, and acceptance/validation criteria.
- Use checkboxes for trackable steps and keep their status current. Remove or archive completed plans only when their decisions and outcomes are no longer needed; do not silently discard open work.
- For a small, isolated change, a full plan is unnecessary. Add a brief entry only when it changes an existing plan or records a decision that affects future work.
- If a plan needs substantial detail, create a clearly named companion `.md` file in an appropriate documentation location and link it here. Do not maintain competing copies of the same plan.
- Keep project plans and their detailed planning instructions in `.md` files. Do not use this document as a substitute for issue tracking, code, tests, executable Firebase rules/configuration, or data migration scripts.
- Update the plan when implementation discovers a material change in scope, approach, dependency, or risk. Mark completion only after the stated acceptance and validation checks have been performed; note any remaining work.

## Active plans

### App Kotlin package layout

- [x] Confirm current UI package references and map source files to feature-oriented `presentation` packages.
- [x] Move app screens and theme files to `presentation/home/screen`, `presentation/splash/screen`, and `presentation/theme`; update package declarations and imports.
- [x] Verify no stale `ui` package references remain; `:app:testDebugUnitTest` and `:app:assembleDebug` pass.

The Gradle module and Android source-set paths remain unchanged. Preserve navigation, application identity, and UI behavior.

### Home screen

- [x] Implement the Home screen top bar, accessible navigation, dark content layout, and five-item bottom navigation with a prominent Long mic action.
- [x] Keep Chat, Library, notifications, and microphone behavior honest where their implementations are not present.
- [x] Add focused Compose UI coverage; `:app:testDebugUnitTest`, `:app:compileDebugAndroidTestKotlin`, and `:app:assembleDebug` pass.

#### Home navigation update

- [x] Replace Memories with Camera in the bottom bar and Home quick access; keep camera unavailable until implemented.
- [x] Replace the drawer with a top-left Settings action; keep settings behavior explicit until a Settings screen exists.
- [x] Update focused Compose tests; `:app:testDebugUnitTest`, `:app:compileDebugAndroidTestKotlin`, and `:app:assembleDebug` pass.

#### Long mic floating action

- [x] Replace the center Long mic navigation item with a centered floating action button, preserving its unavailable feedback and no-capture behavior.
- [x] Preserve a center slot in the four-item bottom bar and verify top action placement and navigation order.
- [x] Run app unit tests, Android test compilation, and debug assembly.
- [x] Enlarge the FAB and overlap it across the navigation bar's top edge; add geometry assertions and compile the instrumentation tests.

Keep the existing navigation and app identity; do not imply microphone capture, camera access, or unavailable destination behavior is implemented.
The connected Android device already has `io.naira.assistant` installed, so instrumentation tests were compiled but not run to avoid replacing that installation. Android's [NavigationBar guidance](https://developer.android.com/develop/ui/compose/components/navigation-bar) supports three to five compact destinations; [Compose semantics guidance](https://developer.android.com/develop/ui/compose/accessibility/semantics) informed accessible labels and the Long mic unavailable state. A scholarly search found no directly relevant research paper for this screen implementation.

#### Home package organization

- [x] Place the Home screen under `presentation/home/main/screen`.
- [x] Place Home content and bottom navigation under `presentation/home/main/component`.
- [x] Move shared Home glyphs into a focused component file; update imports and tests, then verify the app.
- [x] Run app unit tests, Android test compilation, and debug assembly.

#### Empty bottom-nav destinations

- [x] Add saveable selected-destination state for Home, Chat, Camera, and Library; keep Long mic as its existing unavailable action.
- [x] Render an empty page for each selected destination and update navigation item selection on tap.
- [x] Add Compose UI coverage for all four destinations, item selection, and preserved Long mic behavior.
- [x] Run `:app:testDebugUnitTest`, `:app:compileDebugAndroidTestKotlin`, `:app:compileDebugKotlin`, and `:app:assembleDebug`.

Keep the existing top bar and four-destination order. These pages are empty scaffolds; do not imply Chat, Camera, or Library functionality is implemented. Android's [NavigationBar guidance](https://developer.android.com/develop/ui/compose/components/navigation-bar) defines selected and click behavior for destination items, and [state-saving guidance](https://developer.android.com/develop/ui/compose/state-saving) supports using `rememberSaveable` for navigation-related UI state. A scholarly search found no directly relevant research paper.
