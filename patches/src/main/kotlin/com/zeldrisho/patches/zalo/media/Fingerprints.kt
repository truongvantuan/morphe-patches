package com.zeldrisho.patches.zalo.media

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.fieldAccess
import com.android.tools.smali.dexlib2.AccessFlags

/**
 * Zalo 26.08.01's local status classifier for cloud-backed chat files.
 *
 * `Lvk0/g;.n()` returns BIG_FILE_EXPIRED after comparing the message age with
 * the configured large-file lifetime and Z Cloud state. The file may still be
 * present locally at this point; callers use this result to replace the local
 * preview with the subscription/expired UI.
 */
internal object MediaExpiryStatus : Fingerprint(
    returnType = "Lxk0/a;",
    filters = listOf(
        fieldAccess(name = "BIG_FILE_EXPIRED"),
        fieldAccess(name = "BIG_FILE_NOT_EXPIRED"),
    ),
)
