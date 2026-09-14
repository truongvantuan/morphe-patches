package com.zeldrisho.patches.zalo.media

import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import app.morphe.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.zeldrisho.patches.zalo.shared.Constants.COMPATIBILITY_ZALO

/**
 * Keeps locally available large chat files out of Zalo's expired state.
 *
 * Zalo's classifier uses BIG_FILE_EXPIRED to select the subscription/expired
 * UI even when the restored file remains on disk. Replacing only that enum
 * load preserves all other media states and lets the normal local-file path
 * serve the existing file.
 *
 * Limits: this is a client-side status bypass. It cannot recreate a file that
 * is absent locally, bypass server authorization for a new download, or make
 * a remote URL live again.
 */
@Suppress("unused")
val keepZaloMediaAccessiblePatch = bytecodePatch(
    name = "Keep expired media accessible",
    description = "Keeps locally stored large chat media usable after Zalo's " +
        "client-side expiry window by bypassing the expired/subscription state. " +
        "It does not restore missing files or bypass server download authorization.",
    default = true,
) {
    compatibleWith(COMPATIBILITY_ZALO)

    execute {
        val expiredLoad = MediaExpiryStatus.instructionMatches
            .single { match -> 
                val inst = match.instruction
                inst is com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction && 
                (inst.reference as? com.android.tools.smali.dexlib2.iface.reference.FieldReference)?.name == "BIG_FILE_EXPIRED"
            }
        val inst = expiredLoad.instruction as com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
        val fieldRef = inst.reference as com.android.tools.smali.dexlib2.iface.reference.FieldReference
        val definingClass = fieldRef.definingClass
        val type = fieldRef.type
        val register = (inst as OneRegisterInstruction).registerA
        
        MediaExpiryStatus.method.replaceInstruction(
            expiredLoad.index,
            "sget-object v$register, $definingClass->BIG_FILE_NOT_EXPIRED:$type",
        )
    }
}
