# Plan — remaining work

## Zalo MicroG status

| Capability | Status |
| --- | --- |
| Launch-time missing-provider check | Implemented and device-validated |
| Initial photo-restore flow | Implemented and device-validated |
| Full Google Drive backup/restore cycle | Device-validated on Zalo 26.08.01; continue regression QA |

- [ ] Track upstream MicroG-RE PR for OAuth SHA-1 normalization. Once merged,
      revert the download URL in `ZaloMicroGSupport.java` back to the official
      MicroG page (or point to a stable tag release under
      `zeldrisho/MicroG-RE/releases` once published).
- [ ] Complete the [QA checklist](qa-checklist.md) for remaining startup,
      lifecycle, crypto/DAO, integrity, ad-surface, notification, and
      provider-backed restore coverage.
- [ ] Run SDK-verified re-patching, or document a verifier waiver after device
      QA and reproducible toolchain evidence.
- [ ] Stage and ship a versioned release only after authentication, restore, and
      device QA pass, following the [release process](release.md).
