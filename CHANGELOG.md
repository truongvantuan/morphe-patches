# Changelog

Release and changelog policy: [docs/release.md](docs/release.md#changelog-policy).

## Unreleased

## [1.5.2](https://github.com/truongvantuan/morphe-patches/compare/v1.4.1...v1.5.2) (2026-09-18)
### ✨ New Features
* **Zalo:** Open web links externally (force links to open in your default browser instead of Zalo's restricted in-app browser).
* **Zalo:** Remove media backup age limit (upstream).
* **Zalo:** Suppress outbound typing status (upstream).

### 🐛 Bug Fixes
* **Zalo:** Prefer original photo quality updates (upstream).

## [1.4.1](https://github.com/truongvantuan/morphe-patches/compare/v1.4.0...v1.4.1) (2026-09-16)
### ✨ New Features
* **Zalo:** Enable original photo quality feature merged from upstream.

### 🐛 Bug Fixes
* **Zalo:** Fix PromotedModuleView ads hiding for Zalo 26.08.02.
* **CI:** Fix GitHub Actions failing due to deprecated Android SDK `tools` package.

## [1.4.0](https://github.com/truongvantuan/morphe-patches/compare/v1.3.0...v1.4.0) (2026-09-15)

### ✨ New Features
* **Zalo - Prefer original photo quality:** Allows selecting original quality when sending photos. Note: only sends original quality if you check the box each time.

## [1.3.0](https://github.com/truongvantuan/morphe-patches/compare/v1.2.0...v1.3.0) (2026-09-14)

### ✨ New Features
* **Threads:** Bypass ads in the home and following feeds. Promoted posts are hidden before rendering.
* **Zalo:** Filter promo notifications. Blocks promotional push messages (digests, missed feeds, dating, stories) before the UI handles them.

## [1.2.0](https://github.com/truongvantuan/morphe-patches/compare/v1.1.0...v1.2.0) (2026-09-14)

### 🐛 Bug Fixes
* **Zalo:** Bypass native tamper checks that crash the app when repackaged or re-signed.
* **Zalo:** Disable crash reporting and telemetry (Crashlytics, Google Analytics, internal SDK metrics) across native and JVM layers.

## [1.1.0](https://github.com/truongvantuan/morphe-patches/compare/v1.0.0...v1.1.0) (2026-09-14)

### ✨ New Features
* **Zalo:** Support MicroG Drive backups. Fixes Google Drive backups using MicroG by unpinning OAuth constraints and forcing intent dispatch via the host app. Requires a MicroG build patched with signature spoofing.

## [1.0.0](https://github.com/truongvantuan/morphe-patches/releases/tag/v1.0.0) (2026-09-13)

### ✨ New Features
* **Zalo:** Initial release. Hide ads, disable sponsored placements, keep expired media accessible, remove ad ID permission, change app name, and change package name.
