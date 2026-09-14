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

## Remaining work

### 1. Accept or defer Anti-Recall

The current build retains recalled text messages after force-closing and
reopening the app and prefixes the retained content with Zalo's localized
`[Recalled]`/`[Đã thu hồi]` label. The patch guards the recall-mode branches in
`Lu40/d.f0(Lo00/q;ZLo00/t0;)V` and `Lwn1/r0.u(MessageId;Z;Lo00/t0;Z)V`, plus
the narrow `Lt00/g.d(...)` server-delete dispatch. It does not modify the
generic `wn1/r0.t(Object)` consumer.

Keep the patch opt-in until the marker path is confirmed crash-free and the
following cases pass: background/locked delivery, local-only deletion, and
media/group messages. Confirm that the prefix is applied once and remains
correct after restart; do not persist a destructive replacement of the
original content.

#### Acceptance test

Use two test accounts/devices and the same complete patch set, including the
native startup bypass:

1. Send a text message from the secondary device.
2. Recall it while the primary device has the chat open; record crashes and
   whether the message remains.
3. Repeat with the primary app backgrounded or locked.
4. Restart Zalo and confirm persistence.
5. Delete a separate message using the local-only deletion action and confirm
   that local deletion still works.
6. If text passes, repeat for media and group messages.

Record sanitized results and the input APK, bundle, device, and signing
certificate fingerprints in the PR or release record, not in this repository.

### 2. Investigate candidates

Prioritize only after Anti-Recall is validated or explicitly deferred:

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
