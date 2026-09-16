package com.zeldrisho.patches.zalo.media

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.fieldAccess
import app.morphe.patcher.methodCall
import app.morphe.patcher.string
import com.android.tools.smali.dexlib2.AccessFlags

internal object MediaExpiryStatus : Fingerprint(
    definingClass = "Lvk0/g;",
    name = "n",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC, AccessFlags.FINAL),
    returnType = "Lvk0/a;",
    parameters = listOf("Lo00/q;", "Lo00/e2;"),
    filters = listOf(
        fieldAccess(definingClass = "Lvk0/a;", name = "BIG_FILE_EXPIRED", type = "Lvk0/a;"),
        fieldAccess(definingClass = "Lvk0/a;", name = "BIG_FILE_NOT_EXPIRED", type = "Lvk0/a;"),
    ),
)

internal object SelectedMediaQuality : Fingerprint(
    definingClass = "Luh1/u;",
    name = "c",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC),
    returnType = "I",
    parameters = emptyList(),
    filters = listOf(string("LAST_SELECTION_MEDIA_QUALITY_")),
)

internal object OriginalMediaQualityEnabled : Fingerprint(
    definingClass = "Luh1/u;",
    name = "b",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC),
    returnType = "Z",
    parameters = emptyList(),
)

internal object OriginalMediaQualityEntitled : Fingerprint(
    definingClass = "Luh1/u;",
    name = "f",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC),
    returnType = "Z",
    parameters = emptyList(),
)

internal object OriginalMediaQualityAvailable : Fingerprint(
    definingClass = "Luh1/u;",
    name = "e",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC),
    returnType = "Z",
    parameters = emptyList(),
    filters = listOf(
        methodCall(definingClass = "Luh1/u;", name = "b", returnType = "Z"),
        methodCall(definingClass = "Luh1/u;", name = "f", returnType = "Z"),
    ),
)

/** Builds the quality-picker arguments; its first parameter is the current quality. */
internal object QualityPickerArguments : Fingerprint(
    definingClass = "Luh1/b;",
    name = "a",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC),
    returnType = "V",
    parameters = listOf("I", "Landroid/os/Bundle;", "Ljava/lang/String;", "Ljava/lang/String;"),
    filters = listOf(string("EXTRA_CURRENT_QUALITY")),
)

internal object PickerQualityInitialization : Fingerprint(
    definingClass = "Lcom/zing/zalo/ui/picker/mediapicker/MediaPickerView;",
    name = "b7",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "V",
    parameters = listOf("Z"),
    filters = listOf(
        fieldAccess(definingClass = "Lvh1/d;", name = "HD", type = "Lvh1/d;"),
    ),
)

internal object PhotoQualityChipUpdate : Fingerprint(
    definingClass = "Lcom/zing/zalo/ui/picker/mediapicker/MediaPickerView;",
    name = "y6",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC, AccessFlags.FINAL),
    returnType = "V",
    parameters = listOf("Lcom/zing/zalo/ui/picker/mediapicker/MediaPickerView;", "I"),
    filters = listOf(methodCall(definingClass = "Lvh1/c;", name = "a", returnType = "Ljava/lang/String;")),
)

internal object SelectedPhotoOriginalFlag : Fingerprint(
    definingClass = "Lbq0/g;",
    name = "a",
    accessFlags = listOf(AccessFlags.PRIVATE, AccessFlags.FINAL),
    returnType = "V",
    parameters = emptyList(),
    filters = listOf(
        fieldAccess(
            definingClass = "Lcom/zing/zalo/data/mediapicker/model/MediaItem;",
            name = "q",
            type = "Z",
        ),
        fieldAccess(definingClass = "Lo00/k0;", name = "s0", type = "Z"),
    ),
)
