# iosApp

This folder is intentionally empty of an `.xcodeproj` for now — a valid Xcode
project file isn't something worth hand-authoring; it's a generated,
binary-ish `project.pbxproj` that's easy to corrupt by typing.

## Generate it

1. Open this repo's root in **Android Studio** (Ladybug+ with the Kotlin
   Multiplatform plugin) or **Fleet**, and use *File > New > Kotlin
   Multiplatform App*, pointing it at this existing `composeApp` module —
   or simpler:
2. Use the official wizard at **https://kmp.jetbrains.com** to generate a
   fresh iosApp skeleton (uncheck everything except "iOS"), then copy just
   the generated `iosApp/` folder into this repo, replacing this README.
3. In the generated `iosApp/iosApp/iOSApp.swift`, point the root view at the
   shared Compose UI:

   ```swift
   import SwiftUI
   import ComposeApp

   struct ContentView: View {
       var body: some View {
           ComposeView().ignoresSafeArea(edges: .all)
       }
   }

   struct ComposeView: UIViewControllerRepresentable {
       func makeUIViewController(context: Context) -> UIViewController {
           MainViewControllerKt.MainViewController()
       }
       func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
   }
   ```

4. Drop `GoogleService-Info.plist` (from the Firebase console) into
   `iosApp/iosApp/` once the Firebase project exists.
5. Open `iosApp/iosApp.xcodeproj` in Xcode and run — it links against the
   `ComposeApp.framework` produced by `composeApp`'s iOS targets.
