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

Early scaffold — module structure, domain models, and repository contract
are in place; Firebase wiring, the pile/note screens, and the "unpin to
reveal" animation are next. See [`iosApp/README.md`](iosApp/README.md) for
why there's no `.xcodeproj` committed yet.

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
    commonMain/   -> domain models, PileRepository contract, shared UI (App.kt)
    androidMain/  -> MainActivity, Android manifest
    iosMain/      -> MainViewController (hands the shared UI to Xcode)
iosApp/           -> generated separately, see iosApp/README.md
```

## Run it

**Android:**

```bash
./gradlew :composeApp:assembleDebug
```

(No `gradlew` wrapper is committed yet — run `gradle wrapper --gradle-version 8.11`
once, or just open the project in Android Studio and let it generate one.)

**iOS:** follow [`iosApp/README.md`](iosApp/README.md) to generate the Xcode
project, then build from Xcode.

## Roadmap

- [ ] Firebase project + `google-services.json` / `GoogleService-Info.plist`
- [ ] `FirebasePileRepository` real implementation (Firestore queries + writes)
- [ ] `PushNotifier` expect/actual (FCM token registration, notification handling)
- [ ] Pile list screen + create-pile flow
- [ ] Note pinning + "unpin to reveal" animation, likes
- [ ] Offline cache (SQLDelight) so notes already fetched survive no connection
- [ ] iOS project generated and building
- [ ] Freemium model: unlimited piles free, small non-intrusive ads;
      one-time purchase removes them

## About the developer

https://www.linkedin.com/in/ingenieraadela/
