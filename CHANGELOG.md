# Changelog

Release and changelog policy: [docs/release.md](docs/release.md#changelog-policy).

## Unreleased

## [1.4.1](https://github.com/truongvantuan/morphe-patches/compare/v1.3.9...v1.4.1) (2026-09-16)
### ✨ New Features
* **Zalo:** Enable original photo quality feature merged from upstream.

### 🐛 Bug Fixes
* **Zalo:** Fix PromotedModuleView ads hiding for Zalo 26.08.02.
* **CI:** Fix GitHub Actions failing due to deprecated Android SDK `tools` package.

## [1.4.0](https://github.com/zeldrisho/morphe-patches/compare/v1.3.0...v1.4.0) (2026-09-15)
### ✨ New Features
* **Zalo - Prefer original photo quality:** Enables Zalo's existing original-quality photo path for `26.08.01`; picker defaults, video handling, server limits, and account restrictions are unchanged.

### 🐛 Bug Fixes
* **Zalo - Keep expired media accessible:** Fixes patching Zalo `26.08.01` APKMirror bundles by matching the final media-status classifier method correctly.

## [1.3.9](https://github.com/truongvantuan/morphe-patches/compare/v1.3.8...v1.3.9) (2026-09-16)
### 🐛 Bug Fixes
* **CI:** Fix GitHub Actions failing due to deprecated Android SDK `tools` package.

## [1.3.8](https://github.com/truongvantuan/morphe-patches/compare/v1.3.7...v1.3.8) (2026-09-16)
### 🐛 Bug Fixes
* **Zalo:** Hide PromotedModuleView to completely block SenTia School and other sponsored ads from the conversation list on Zalo 26.08.02.

## [1.3.7](https://github.com/truongvantuan/morphe-patches/compare/v1.3.6...v1.3.7) (2026-09-14)
### 🐛 Bug Fixes
* **Zalo:** Restore microG support for Zalo 26.08.02 by updating account picker and media restore UI fingerprints.

## [1.3.6](https://github.com/truongvantuan/morphe-patches/compare/v1.3.5...v1.3.6) (2026-09-14)
### 🐛 Bug Fixes
* **Zalo:** Fix `KeepMediaAccessiblePatch` failing on Zalo 26.08.02 by making fingerprint extremely minimal and dynamically parsing dexlib2 instructions.

## [1.3.5](https://github.com/truongvantuan/morphe-patches/compare/v1.3.4...v1.3.5) (2026-09-14)
### 🐛 Bug Fixes
* **Zalo:** Relax `KeepMediaAccessiblePatch` fingerprint to avoid strict `accessFlags` mismatch failures on Zalo 26.08.02.

## [1.3.4](https://github.com/truongvantuan/morphe-patches/compare/v1.3.3...v1.3.4) (2026-09-14)
### 🐛 Bug Fixes
* **Zalo:** Restore original ad-blocking patches by reverting experimental hooks and updating `HideBusinessBoxPatch` fingerprint for Zalo 26.08.02.

## [1.3.3](https://github.com/truongvantuan/morphe-patches/compare/v1.3.2...v1.3.3) (2026-09-14)
### 🐛 Bug Fixes
* **Zalo:** Fix BusinessBoxListInsertionFingerprint patch error by updating method parameter list to match Zalo 26.08.02.

## [1.3.2](https://github.com/truongvantuan/morphe-patches/compare/v1.3.1...v1.3.2) (2026-09-14)
### 🐛 Bug Fixes
* **Zalo:** Update BusinessBoxListInsertionFingerprint parameters and method invocation for Zalo 26.08.02 compatibility.

## [1.3.1](https://github.com/truongvantuan/morphe-patches/compare/v1.3.0...v1.3.1) (2026-09-14)
### 🐛 Bug Fixes
* **Zalo:** Update HideBusinessBoxPatch to hook Zalo 26.08.02 correctly.

## [1.3.0](https://github.com/zeldrisho/morphe-patches/releases/tag/v1.3.0) (2026-09-08)
### ✨ New Features
* **Zalo - Z Cloud microG support:** Restores Drive backup and restore flows by intercepting account requests and routing them to microG's selector.

## [1.2.0](https://github.com/zeldrisho/morphe-patches/releases/tag/v1.2.0) (2026-09-02)
### ✨ New Features
* **Threads - Hide algorithmic timelines:** Defaults to the chronological "Following" feed and completely disables the "For you" timeline.

## [1.1.0](https://github.com/zeldrisho/morphe-patches/releases/tag/v1.1.0) (2026-08-30)
### ✨ New Features
* **Zalo - Enable background playback:** Allows sending and playing local videos in the background and removes premium picture-in-picture restrictions.

## [1.0.0](https://github.com/zeldrisho/morphe-patches/releases/tag/v1.0.0) (2026-08-27)
### ✨ New Features
* **Zalo:** Removes standard feed advertisements and sponsored Official Account listings.
* **Zalo:** Prevents chat media items from automatically expiring.
* **Zalo:** Enforces minimum UI bounds for otherwise collapsed premium banners.
