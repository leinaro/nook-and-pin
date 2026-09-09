# Nook & Pin

![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-7F52FF?logo=kotlin&logoColor=white)
![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-1.7.3-4285F4?logo=jetpackcompose&logoColor=white)
![Platforms](https://img.shields.io/badge/platforms-Android%20%7C%20iOS-lightgrey)
![Status](https://img.shields.io/badge/status-early%20scaffold-orange)

A shared corkboard for the people you don't see every day. Create a **pile**
with your partner, your best friend, or your family; pin a note; they get a
push and watch it land like a real note pinned to a board — read it, and it
comes to life; like it if it made their day.

Built as a Kotlin Multiplatform portfolio piece — a deliberate counterpart
to [HeroDex](../marvel), which is Android-only, native Clean Architecture.
This one shares UI *and* business logic across Android and iOS with Compose
Multiplatform, to show the other half of the range.

## Why this app (and why KMP)

- **Real use case, not a todo-list demo**: sync has to be realtime, offline
  has to work, and push notifications have to feel instant — that's an
  honest test of a shared-Kotlin architecture, not a toy problem.
- **Genuine platform divergence**: push notification registration/handling
  differs enough between Android and iOS that it earns a real
  `expect`/`actual`, not a decorative one.
- **Different stack from HeroDex on purpose**: Compose Multiplatform instead
  of Android-only Compose, Firebase (GitLive KMP SDK) instead of Retrofit,
  and a lighter DI approach — to show range, not repeat the same recipe.

## Status

**The whole loop is real on Android, end to end, between two actual
people**: sign in with Google (Credential Manager, a real
Firebase-verified session), create a pile, invite a second person by
email, pin a note, reveal it, like it — all of it hitting the actual
`nook-and-pin` Firestore project, gated by
[`firestore.rules`](firestore.rules) so only a pile's members can touch it.
Verified on device with two real Google accounts and checked server-side
via the Firestore REST API at every step, not just "the UI updated."

The invite flow (see [`FirebaseUserDirectory.kt`](composeApp/src/commonMain/kotlin/com/leinaro/nookandpin/data/FirebaseUserDirectory.kt))
looks someone up by email via a `users/{uid}` profile written on sign-in —
a deliberate choice to avoid needing a Cloud Function, which would need
this project on a paid plan. The doc there spells out the privacy tradeoff.

What's still missing: push notifications, offline caching, and iOS (see
[`iosApp/README.md`](iosApp/README.md) for why there's no `.xcodeproj`
committed yet, and why Google Sign-In there is a documented TODO).

`InMemoryPileRepository` still exists as a reference/fallback implementation
of the same `PileRepository` interface, but `App()` now wires up
`FirebasePileRepository` by default.

### Running this yourself

`google-services.json` is intentionally not committed. If you clone this
repo, drop your own Firebase Android app's config file at
`composeApp/google-services.json` — without it, `FirebasePileRepository`
has nothing to talk to.

## Tech stack (planned)

- **UI:** Compose Multiplatform (Android + iOS), Material 3
- **Language/runtime:** Kotlin 2.1, Coroutines & Flow, kotlinx-datetime
- **Backend:** Firebase — Firestore (realtime sync), FCM (push, including
  iOS via APNs), Auth — via the [GitLive Kotlin Firebase SDK](https://github.com/GitLiveApp/firebase-kotlin-sdk)
- **Architecture:** shared domain layer (`Pile`, `Note`, `PileRepository`)
  with a swappable data-layer implementation
- **Build:** Gradle Kotlin DSL, version catalog (`gradle/libs.versions.toml`)

## Project layout

```
composeApp/
  src/
    commonMain/   -> domain models, PileRepository contract + in-memory fake,
                     shared UI (App.kt, PileListScreen, PileDetailScreen, PinnedNoteCard)
    androidMain/  -> MainActivity, Android manifest
    iosMain/      -> MainViewController (hands the shared UI to Xcode)
iosApp/           -> generated separately, see iosApp/README.md
```

## Run it

**Android:**

```bash
./gradlew :composeApp:assembleDebug
```

**iOS:** follow [`iosApp/README.md`](iosApp/README.md) to generate the Xcode
project, then build from Xcode.

## Roadmap

- [x] Pile list screen + note detail screen
- [x] Note pinning + "unpin to reveal" animation, likes
- [x] Firebase project created, Android app registered, Firestore + rules deployed
- [x] Google Sign-In on Android (Credential Manager -> Firebase Auth), verified on device
- [x] `FirebasePileRepository` real implementation, verified end-to-end against
      the live project (server-side, not just UI state)
- [x] Create-pile flow
- [x] Invite a second real member to a pile by email, verified with two
      real Google accounts sharing a pile
- [ ] Google Sign-In on iOS (GIDSignIn bridged from Swift, once iosApp exists)
- [ ] `GoogleService-Info.plist` for iOS (once the iOS app is registered)
- [ ] `PushNotifier` expect/actual (FCM token registration, notification handling)
- [ ] Offline cache (SQLDelight) so notes already fetched survive no connection
- [ ] iOS project generated and building
- [ ] Freemium model: unlimited piles free, small non-intrusive ads;
      one-time purchase removes them

## About the developer

https://www.linkedin.com/in/ingenieraadela/
