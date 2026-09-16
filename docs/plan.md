# Remaining work

This roadmap is scoped to Zalo Android APK `26.08.01` (version code
`260801903`). APKs, smali, logs, screenshots, and generated analysis files stay
under the ignored `analysis/zalo/26.08.01/` directory.

zStyle is excluded. Video Original quality is also excluded: the pinned APK
contains `VIDEO` and `VIDEO_HD`, but no `VIDEO_ORIGINAL` path.

## Photo Original quality

- Fix the remaining post-selection quality-chip label, which can still display
  `HD` even though the outgoing photo is marked Original.
- Confirm that the current patch does not affect video sending.
- Complete control testing with identical source photos and compare source and
  received hashes, dimensions, metadata, and encoding.
- Verify ordinary quality selection and multi-image selection.
- Do not claim recovery of originals discarded by the client or server.

## Feature feasibility investigations

Before implementing any item, record the exact smali gate, callers, local data
flow, server dependencies, narrow proposed change, and regression risks in
`analysis/zalo/26.08.01/notes/`. Classify each result as **ready to implement**,
**needs runtime proof**, or **server-dependent**. Do not globally spoof a paid
account or mutate HTTP traffic.

### zBusiness product catalog

- Trace entry points, product creation/editing, count limits, storage, sync, and
  sharing.
- Determine whether local templates are usable without an authorized backend.
- Verify persistence after restart and what recipients see when a product is
  shared.

### zCloud local backup/export

- Trace existing backup and phone-transfer machinery, including messages, media
  associations, database snapshots/WAL, schema, and encryption keys.
- If an extension is needed, export only to a user-selected external location.
- Prove stock-to-patched migration, patched reinstall, and cross-device restore
  separately.
- Require integrity/version checks, bounded extraction, recoverable staging, and
  tests for corrupt archives, low space, and interrupted transfers.

### Gold Business badge

- Identify the exact asset and entitlement/display path.
- Separate local cosmetic rendering from server-visible verification.
- Treat server-issued verification as server-dependent unless contrary evidence
  is established.

### Notifications from strangers

- Trace privacy settings, request routing, push delivery, notification channels,
  and local suppression independently.
- Preserve blocks, mutes, spam protections, and promotional filtering.
- Validate foreground and background delivery with a consenting non-contact
  account.

### Change username

- Distinguish display name, unique handle, and business contact link.
- Trace validation, cooldowns, persistence, update requests, and visibility from
  another account.
- Patch only proven local restrictions; a local alias is not a server rename.

### Inactivity deletion

- Verify the current policy and what counts as activity.
- Determine whether deletion is server-managed; do not silently generate account
  activity.
- If no client enforcement point exists, classify prevention as server-dependent.
  Optional reminders and backup are separate mitigations.

### Automatic backup and restore

- Trace zCloud and Google Drive independently: scheduling, constraints,
  authentication, token issuance, upload completion, retention, and restore order.
- Test retries, backoff, process death, reboot, offline recovery, quota errors,
  low storage, incompatible versions, duplicate work, and wrong-account restore.
- Keep the last usable backup and live data on failure; never log tokens or
  backup contents.
- Separate text backup from media backup and preserve attachment links.
- Treat OAuth authorization and zCloud subscription/storage limits as backend
  boundaries, not local unlocks.

## Deferred validation

- Validate expired-media behavior in chat and My Cloud, including missing local
  files, unusable remote URLs, restore, and deleted messages.
- Validate the existing `SOCIAL_STORY` / `ZALO_VIDEO` notification filter without
  suppressing chat, group activity, friend requests, calls, or alerts.
- Validate QR login, read/delivery state, message history, media scope, and
  session revocation across Android, Web, and Desktop.
- Update the MicroG-RE source only after a stable upstream release or official
  project page provides the OAuth SHA-1 normalization fix; verify provenance and
  checksum first.

## Acceptance evidence

For every implemented candidate, record a positive behavior check and an
unmodified/control comparison. Include remote visibility or a complete restore
round trip where relevant. Before release, repeat build, original-APKM repatch,
installation, and device validation with the published `.mpp`; record the APK
hash, patch bundle hash, enabled patches, device, Android version, and signing
certificate fingerprint outside this repository.
