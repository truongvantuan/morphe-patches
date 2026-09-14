# Changelog

Release and changelog policy: [docs/release.md](docs/release.md#changelog-policy).

## Unreleased

## [1.2.13](https://github.com/truongvantuan/morphe-patches/compare/v1.2.12...v1.2.13) (2026-09-14)
### 🔧 Build
* **CI:** Fix trailing whitespaces causing CI Spotless check failure again (accidentally committed temp files).


## [1.2.12](https://github.com/truongvantuan/morphe-patches/compare/v1.2.11...v1.2.12) (2026-09-14)
### 🔧 Build
* **CI:** Fix trailing whitespaces causing CI Spotless check failure.


## [1.2.11](https://github.com/truongvantuan/morphe-patches/compare/v1.2.10...v1.2.11) (2026-09-14)
### 🐛 Bug Fixes
* **Zalo - Hide conversation list ads:** Fix app crash (`VerifyError: target dex pc is not at instruction start`) on startup when opening Zalo.


## [1.2.10](https://github.com/truongvantuan/morphe-patches/compare/v1.2.9...v1.2.10) (2026-09-14)
### ✨ Features
* **Zalo - Hide conversation list ads:** Added support for Zalo 26.08+ modern `RecyclerView` architecture (hooking `NormalMsgModuleView`).
* **Zalo - Hide Newsfeed ads:** Added support for modern `FeedItemZInstantAds` engine which replaced legacy suggested banners.


## [1.2.9](https://github.com/truongvantuan/morphe-patches/compare/v1.2.8...v1.2.9) (2026-09-14)
### 🔧 Build
* **CI:** Fix Spotless Java formatting error in extension helper that caused the Check workflow to fail.


## [1.2.8](https://github.com/truongvantuan/morphe-patches/compare/v1.2.7...v1.2.8) (2026-09-14)
### 🐛 Bug Fixes
* **Zalo - Hide Newsfeed ads:** Fix high-register bytecode compilation errors (`Invalid register: v25`) that caused the patcher to ignore instructions, preventing ads from actually being hidden.


## [1.2.7](https://github.com/truongvantuan/morphe-patches/compare/v1.2.6...v1.2.7) (2026-09-14)
### 🔧 Improvements
* **Zalo - Hide Newsfeed ads:** Improved hidden ad elements to fully collapse spacing rather than just turning invisible.


## [1.2.6](https://github.com/truongvantuan/morphe-patches/compare/v1.2.5...v1.2.6) (2026-09-14)
### 🐛 Bug Fixes
* **Zalo - Hide Newsfeed ads:** Fix fingerprint match failure on Zalo 26.08.02 caused by an invalid method call filter.


## [1.2.5](https://github.com/truongvantuan/morphe-patches/compare/v1.2.4...v1.2.5) (2026-09-14)

### ✨ New Features
* **Zalo - Hide Newsfeed ads:** Hides sponsored banner and OA promoted-post cards from the Zalo Newsfeed.

## [1.2.4](https://github.com/truongvantuan/morphe-patches/compare/v1.2.3...v1.2.4) (2026-09-14)
### 🐛 Bug Fixes
* **Zalo - microG Drive support:** Fix patcher crash on `26.08.02` caused by Zalo removing their internal account picker method in favor of a native AccountManager UI.

## [1.2.3](https://github.com/truongvantuan/morphe-patches/compare/v1.2.2...v1.2.3) (2026-09-14)
### 🐛 Bug Fixes
* **Zalo - Filter promo notifications:** Fix runtime patcher crash on `26.08.02` caused by the compiler upgrading short jumps to `goto/16`.

## [1.2.2](https://github.com/truongvantuan/morphe-patches/compare/v1.2.1...v1.2.2) (2026-09-14)
### ✨ New Features
* **Zalo - Hide conversation ads:** Sponsored Official Account posts and "Media Box" promos no longer appear in the chat list.

### 🔧 Improvements
* **Zalo - Bypass native startup tamper check:** Migrated from hardcoded offsets to dynamic byte pattern scanning.
* **Zalo:** Updated all patch fingerprints to flawlessly match the `26.08.02` app obfuscation.

## [1.2.1](https://github.com/truongvantuan/morphe-patches/compare/v1.2.0...v1.2.1) (2026-09-14)
### 🚀 Updated App Support
* **Zalo:** Add support for `26.08.02`.

## [1.2.0](https://github.com/truongvantuan/morphe-patches/compare/v1.1.0...v1.2.0) (2026-09-11)

### ✨ New Features
* **Zalo - microG Drive support:** Adds provider-backed Google Drive account selection and backup/restore support for `26.08.01`.
* **Zalo - Bypass native startup tamper check:** Initial patch for `26.08.01` — preserves native initialization while disabling the verified re-signing exit dispatch on arm64.
* **Zalo - Disable ads:** Initial patch for `26.08.01` — forces the Adtima offline gates closed, always drops admob/dfp/ima from the supported-network map, and reports limit-ad-tracking opted-out without calling the Play API.
* **Zalo - Disable sponsored placements:** Initial patch for `26.08.01` — forces the Story/community ad-enable flags off at their config reads (normal content path kept; server-stitched or OA-message promos may remain).
* **Zalo - Remove AD_ID permission:** Initial patch for `26.08.01` — strips the advertising-id manifest entries (in-app readers fall back to "unknown"); pairs with the limit-ad-tracking opt-out now in Disable Zalo ads.
* **Zalo - Filter promo notifications:** Initial patch for `26.08.01` — drops Timeline/Stories and Zalo Video pushes in the push dispatcher; message, call, friend-request and birthday notifications are untouched.

## [1.1.0](https://github.com/truongvantuan/morphe-patches/compare/v1.0.0...v1.1.0) (2026-09-09)

### 🚀 Updated App Support
* **Threads:** Add support for `445.0.0.46.83`.

## 1.0.0 (2026-09-07)

### ✨ New Features
* **Threads - Hide ads:** Initial patch for 434.0.0.41.74 — removes sponsored posts from the feed.
* **Threads - Remove AD_ID permission:** Initial patch for 434.0.0.41.74.
* **Threads - Change app name:** Initial patch for 434.0.0.41.74.
* **Threads - Change package name:** Initial patch for 434.0.0.41.74 (opt-in; renaming can break login, providers, or push).
