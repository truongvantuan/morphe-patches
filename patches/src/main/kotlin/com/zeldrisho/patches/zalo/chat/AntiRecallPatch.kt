package com.zeldrisho.patches.zalo.chat

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.opcode
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.NarrowLiteralInstruction
import com.zeldrisho.patches.zalo.shared.Constants.COMPATIBILITY_ZALO

/**
 * Prevents recalled-message mutations from reaching the database mutation path.
 * The guard is deliberately limited to f0(); the Object consumer r0.t() is left
 * untouched because it may receive event types other than Lwn1/q0.
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
        // Replace the existing recall-arm goto with return-void. This is one
        // instruction for one instruction: no new registers,
        // locals, labels, or verifier frames are introduced.
        val databaseMatch = AntiRecallDatabaseFingerprint.matchAll(1..1).single()
        checkRecallModePattern(databaseMatch.originalMethod)
        val originalInstructions = databaseMatch.originalMethod.implementation
            ?.instructions
            ?.toList()
            .orEmpty()
        val recallConstantIndex = originalInstructions.indexOfFirst { instruction ->
            instruction.opcode == Opcode.CONST_16 &&
                (instruction as? NarrowLiteralInstruction)?.narrowLiteral == 0x21
        }
        check(recallConstantIndex >= 0) {
            "Anti-recall target changed: recall mode constant 0x21 not found"
        }
        check(originalInstructions.getOrNull(recallConstantIndex + 1)?.opcode == Opcode.GOTO) {
            "Anti-recall target changed: recall mode is no longer followed by goto"
        }
        val databaseMethod = databaseMatch.classDef.methods.first { candidate ->
            candidate.name == databaseMatch.originalMethod.name &&
                candidate.parameterTypes == databaseMatch.originalMethod.parameterTypes &&
                candidate.returnType == databaseMatch.originalMethod.returnType
        }
        databaseMethod.replaceInstruction(recallConstantIndex + 1, "return-void")
    }
}

/** Exact f0 signature plus its mode-constant opcode anchor. */
private object AntiRecallDatabaseFingerprint : Fingerprint(
    definingClass = "Lu40/d;",
    name = "f0",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "V",
    parameters = listOf("Lo00/q;", "Z", "Lo00/t0;"),
    filters = listOf(
        opcode(Opcode.CONST_16),
        opcode(Opcode.GOTO),
    ),
)

/** Fails closed if the pinned recall/delete mode constants move or change. */
internal fun checkRecallModePattern(method: com.android.tools.smali.dexlib2.iface.Method) {
    val modeConstants = method.implementation?.instructions?.toList().orEmpty()
        .filter { it.opcode == Opcode.CONST_16 }
        .mapNotNull { (it as? NarrowLiteralInstruction)?.narrowLiteral }
    check(0x21 in modeConstants && 0x24 in modeConstants) {
        "Anti-recall target changed: f0 no longer contains both 0x21 and 0x24 mode constants"
    }
}
