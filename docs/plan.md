# Project maintenance roadmap

Repeatable build and device-validation procedures live in
[validation and release qualification](validation.md).

## Current target

- App: Zalo `26.08.01` (version code `260801903`)
- Scope: Android APK patching only; Web/Desktop are research context, not
  additional patch targets.
- Re-patching must use the persistent signing configuration described in
  [CLI documentation](cli.md). Only update an installed app when the signing
  certificate matches.

## Completed

### Clone package and branding (issue #7)

Added opt-in Zalo patches for changing the package name and application name.
Package rewriting updates package-owned provider authorities and permission
identities, including Zalo's legacy permission prefix, while preserving
third-party authorities. The patches are pinned to the current Zalo target and
remain disabled by default because package- and certificate-bound login, push,
sharing, deep links, and backup may not work after renaming.

The manifest rewrite and label behavior have unit coverage. APKM coexistence,
login, push, and parallel-account behavior still require device validation.

## Remaining work

### 1. Investigate candidates

1. Trace hide-read-receipt and hide-typing-indicator candidates without
   blocking messaging or socket synchronization.
2. Investigate original-media download limits and media auto-delete/expiry.
3. Investigate orphaned media after restore; do not guess file ownership.
4. Validate complete backup/restore, background token refresh, and restore when
   contacts permission is denied.
5. Trace forced-update/version-warning behavior as a compatibility risk.
6. Evaluate broader promotional/service-thread filtering while preserving
   legitimate Official Account conversations.
7. Document APK relationships with Web/Desktop sessions, QR login, device
   limits, and read-state synchronization. Treat server-enforced limits as out
   of scope unless client-side enforcement is proven.
8. Research consent and data-collection behavior separately from telemetry
   patching; do not infer a patch point from policy or news reports.

Subscription/paywall limits, concurrent-login limits, and call recording remain
uncommitted until feasibility, legal, and server-side boundaries are
established.

## Target maintenance

- Replace the temporary MicroG-RE download source when the upstream OAuth
  SHA-1 normalization fix is available; use the official page or a stable
  tagged `zeldrisho/MicroG-RE` release.
- For each new Zalo version, re-verify fingerprints, ABI compatibility, and
  patch semantics against the exact APKMirror artifact before changing target
  metadata.
- Complete the SDK-verified re-patch and device checks before release;
  compilation and unit tests alone do not establish APK compatibility.
- Keep APKs, smali, logs, screenshots, and generated decompiler files local
  under the ignored `analysis/` directory. Record only verified durable
  conclusions in this plan or the relevant patch and validation documentation.
