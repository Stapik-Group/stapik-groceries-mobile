# Stapik Groceries (Android)

A mobile companion to [Stapik Groceries](https://github.com/Stapik-Group/stapik-groceries), the desktop grocery shopping tracker. Written in Kotlin with Jetpack Compose, styled after the same retro old-school aesthetic as the desktop version and its sibling apps.

## Features

- **Month list** – browse monthly shopping lists, newest first, with an optional year filter
- **Month detail** – planned vs. actual cost, savings, off-list spending and a projected total for the month, plus every product with its category color, planned/purchased quantity and price
- **Statistics** – list and bar-chart views of the last 12 months (planned, actual and savings)
- **This week's list** – generates a shopping list for the month you're viewing, splitting whatever is still unbought across the remaining weeks of that month; shows a clear message instead of a list once the month has already ended
- **Local check-offs** – tick items off the weekly list as you shop; check marks are kept on the device only and are not synced to the cloud or the desktop app
- **Cloud sync (read-only for the grocery data)** – fetches the document from the Stapik Cloud compatible self-hosted API, same `documents/{slotKey}` endpoint as the desktop app
- **Offline cache** – automatically caches the last retrieved document, with a banner showing when you're looking at cached data and from when
- **Pull-to-refresh** – drag down to fetch the latest data on demand
- **Themes** – Classic, Modern and Classic Pink, matching the desktop app, applied consistently across every screen
- **Encrypted connection config** – server URL and API key are encrypted with a key held in the Android Keystore before being stored on device; the API key is masked on screen with a show/hide toggle
- **Multilingual UI** – Polish, English and German, following the phone's system language
- **Retro aesthetic** – same raised-button, blue-navbar, grey-cell look as the desktop app

This app does not add, edit, or delete months, categories, or products – all of that happens in the desktop app. It only reads and displays whatever is currently in the cloud, aside from the local weekly check-off marks described above.

## Requirements

- Android 8.0 (API 26) or newer
- A running instance of the Stapik Cloud compatible sync API used by the desktop app (see [Stapik Groceries](https://github.com/Stapik-Group/stapik-groceries))

## Building

Requires [Android Studio](https://developer.android.com/studio) (Kotlin, Jetpack Compose).
