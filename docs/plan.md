# Project status and maintenance roadmap

This page describes the current Zalo MicroG support status and the conditions
for future maintenance and release work. It is intentionally descriptive rather
than a task list. Execution evidence belongs in the release or pull-request
record; repeatable validation steps live in [validation and release qualification](validation.md).

## Zalo MicroG support status

| Capability | Current status |
| --- | --- |
| Launch-time missing-provider check | Implemented and device-validated. |
| Initial photo-restore flow | Implemented and device-validated. |
| Full Google Drive backup/restore cycle | Device-validated on Zalo 26.08.01; regression validation remains part of future target updates. |

## Upstream OAuth compatibility

The temporary MicroG-RE download source remains until upstream MicroG-RE publishes
the OAuth SHA-1 normalization fix. Once that fix is available, restore the
`ZaloMicroGSupport.java` download URL to the official MicroG page, or use a stable
tagged release from `zeldrisho/MicroG-RE/releases`. Treat the upstream release as
the replacement condition, not as a recurring manual procedure.

Provider transport, account selection, OAuth authorization, and restore behavior
are separate compatibility boundaries. A successful picker or transport path does
not prove that the OAuth project accepts the package and signing certificate.
Refer to [provider boundaries](validation.md#provider-boundaries)
when interpreting these results.

## Release readiness

A release is ready only after the required authentication, restore, and device
validation evidence is recorded, including SDK verification or a documented
verifier waiver. Follow the [release process](release.md) for staging and
publishing; follow [validation and release qualification](validation.md) for the
actual build and device procedure.
