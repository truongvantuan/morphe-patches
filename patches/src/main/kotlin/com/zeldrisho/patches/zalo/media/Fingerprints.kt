package com.zeldrisho.patches.zalo.media

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.fieldAccess
import app.morphe.patcher.methodCall
import app.morphe.patcher.string
import com.android.tools.smali.dexlib2.AccessFlags

internal object MediaExpiryStatus : Fingerprint(
    filters = listOf(
        fieldAccess(name = "BIG_FILE_EXPIRED"),
        fieldAccess(name = "BIG_FILE_NOT_EXPIRED"),
    ),
)

internal object SelectedMediaQuality : Fingerprint(
    name = "c",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC),
    returnType = "I",
    parameters = emptyList(),
    filters = listOf(string("LAST_SELECTION_MEDIA_QUALITY_")),
)

internal object OriginalMediaQualityAvailable : Fingerprint(
    name = "e",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC),
    returnType = "Z",
    parameters = emptyList(),
    filters = listOf(
        methodCall(name = "b", returnType = "Z"),
        methodCall(name = "f", returnType = "Z"),
    ),
)
