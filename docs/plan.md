# Project status and maintenance roadmap

This page records durable project decisions, verified behavior, and remaining
research. Repeatable build and device-validation procedures live in
[validation and release qualification](validation.md).

## Current target

- App: Zalo `26.08.01` (version code `260801903`)
- Scope: Android APK patching only; Web/Desktop are research context, not
  additional patch targets.
- Re-patching must use the checked-in/persistent signing configuration described
  in [CLI documentation](cli.md). Only update an installed app when the signing
  certificate matches.

## Completed and device-verified

- microG Drive support: missing-provider launch guidance is non-blocking, and
  initial OAuth plus Google Drive photo restore was verified on-device.
- Hide Business Box: the structurally typed Business Box item is hidden from
  the main chat list and did not reappear after the periodic update interval;
  ordinary conversations and user-accessed OA chats remain in scope.
- Deep telemetry suppression: Room analytics writes, Firebase Crashlytics
  diagnostics, and native crash-handler registration are suppressed; basic
  navigation and chat smoke tests passed.
- Signing continuity: consecutive repatched builds can be installed with
  `adb install -r` when the existing installation uses the same project key.
- Native startup tamper-check bypass, ads, sponsored placements, promo
  notification filtering, and AD_ID removal remain supported for the pinned
  target.

## Anti-recall status

The Anti-Recall patch is implemented but **runtime behavior is not yet
accepted as complete**. Earlier tests produced contradictory results: some
builds retained recalled messages, while others allowed the recall or crashed.
A crash caused by invalid injected bytecode was fixed, but crash-free retention
and local-delete behavior must be demonstrated with a final controlled build.

Current implementation targets the `Lu40/d.f0(Lo00/q;ZLo00/t0;)V` recall-mode
branch and avoids modifying the generic `wn1/r0.t(Object)` consumer. Do not
assume that this is the complete incoming-recall path without runtime evidence.
Do not add a localized `[Đã thu hồi]`/`[Recalled]` marker until retention is
stable; the marker must be added only through a verifier-safe, tested path.

### Anti-recall acceptance test

Use two test accounts/devices and the same complete patch set, including the
native startup bypass:

1. Send a text message from the secondary device.
2. Recall it from the secondary device while the primary device has the chat
   open; record whether the UI crashes and whether the message remains.
3. Repeat with the primary app backgrounded or the device locked.
4. Restart Zalo and confirm persistence.
5. Delete a separate message using the local-only deletion action and confirm
   that local deletion still works.
6. Repeat for media/group messages if text behavior passes.

Record sanitized results and the input APK, bundle, device, and signing
certificate fingerprints in the PR/release record—not in this repository.

## Remaining research backlog

Prioritize only after Anti-Recall is either validated or explicitly deferred:

1. Trace and evaluate hide-read-receipt and hide-typing-indicator candidates;
   do not block generic messaging or socket synchronization.
2. Investigate original-media download limits and media auto-delete/expiry.
3. Investigate orphaned media after restore and whether safe re-indexing is
   possible without guessing file ownership.
4. Validate the complete backup/restore cycle, background token refresh, and
   restore behavior when contacts permission is denied.
5. Trace forced-update/version-warning behavior as a future compatibility risk.
6. Evaluate broader promotional/service-thread filtering while preserving
   legitimate OA conversations.
7. Document APK relationships with Web/Desktop sessions, QR login, device
   limits, and read-state synchronization. Treat server-enforced limits as
   likely out of scope unless client-side enforcement is proven.
8. Research consent/data-collection behavior separately from telemetry patching;
   do not infer a patch point from policy or news reports alone.

Subscription/paywall limits, concurrent-login limits, and call recording are
not committed patch targets until feasibility, legal, and server-side boundaries
are established.

## Upstream and target maintenance

- The temporary MicroG-RE download source remains until the upstream OAuth
  SHA-1 normalization fix is available. Then use the official page or a stable
  tagged `zeldrisho/MicroG-RE` release.
- For each new Zalo version, re-verify fingerprints, ABI compatibility, and
  patch semantics against the exact APKMirror artifact before changing target
  metadata.
- Complete the required SDK-verified re-patch and device checks before release;
  compilation and unit tests alone do not establish APK compatibility.
