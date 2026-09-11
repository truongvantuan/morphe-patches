package com.zeldrisho.patches.zalo.chat

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import app.morphe.patcher.methodCall
import app.morphe.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.Opcode
import com.zeldrisho.patches.zalo.shared.Constants.COMPATIBILITY_ZALO

/**
 * Drops the recall event from Zalo's server delete-message processor.
 *
 * The target is the single `Lwn1/q0` dispatch in `Lt00/g;.d`; the broader
 * `Lwn1/q0` and message-repository deletion methods are intentionally untouched.
 */
@Suppress("unused")
val antiRecallPatch = bytecodePatch(
    name = "Anti-Recall",
    description = "Prevents incoming server recall commands from removing messages. " +
        "Local user-initiated message deletion and cache cleanup remain unchanged.",
    default = true,
) {
    compatibleWith(COMPATIBILITY_ZALO)

    execute {
        val match = AntiRecallDispatchFingerprint.matchAll(1..1).single()
        val dispatch = match.instructionMatches.single {
            it.instruction.opcode == Opcode.INVOKE_VIRTUAL
        }
        match.method.replaceInstruction(dispatch.index, "nop")
    }
}

internal object AntiRecallDispatchFingerprint : Fingerprint(
    definingClass = "Lt00/g;",
    name = "d",
    filters = listOf(
        methodCall(
            definingClass = "Lwn1/r0;",
            name = "t",
            parameters = listOf("Ljava/lang/Object;"),
            returnType = "V",
        ),
    ),
)
