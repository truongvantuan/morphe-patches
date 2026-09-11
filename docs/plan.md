# Project status and maintenance roadmap

This page lists only remaining work for future releases and target updates.
Repeatable build and device-validation procedures live in
[validation and release qualification](validation.md).

## Completed milestones

- Added scoped Zalo chat patches for Hide Business Box and Anti-recall.
- Built the 1.2.0 patch bundle, successfully repatched the pinned Zalo 26.08.01 APKM with all 9 compatible patches, and installed the signed APK on the connected device.

## Remaining work

### Upstream OAuth compatibility

The temporary MicroG-RE download source remains until upstream MicroG-RE
publishes the OAuth SHA-1 normalization fix. When available, restore the
`ZaloMicroGSupport.java` download URL to the official MicroG page, or use a
stable tagged release from `zeldrisho/MicroG-RE/releases`.

### Target update regression

For each newly supported Zalo version, re-verify fingerprints, ABI compatibility,
and patch semantics against the exact APKMirror artifact. Repeat build,
re-patch, signing, and device validation before changing target metadata.

### Release qualification

Complete the required SDK-verified re-patch and device checks before release,
including authentication, provider behavior, restore flows, messaging/calling,
and enabled-patch positive and negative checks. Record results in the release or
pull-request record; do not store APKs, credentials, screenshots, or raw logs in
Git.
