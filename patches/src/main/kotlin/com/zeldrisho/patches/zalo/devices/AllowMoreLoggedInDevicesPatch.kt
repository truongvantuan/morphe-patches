package com.zeldrisho.patches.zalo.devices

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import app.morphe.patcher.opcode
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.string
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import com.zeldrisho.patches.zalo.shared.Constants.COMPATIBILITY_ZALO

/**
 * Preserves the trusted-device limits returned by the server in the login-history UI.
 *
 * Zalo 26.08.01 clamps every max_trusted_devices_* value above one to one before
 * rendering HistoryLoginView. This patch removes only those three client-side clamps.
 * It does not bypass server-side login, trust, or session enforcement.
 */
@Suppress("unused")
val allowMoreLoggedInDevicesPatch = bytecodePatch(
    name = "Allow more logged-in devices",
    description = "Preserves Zalo's server-provided trusted-device limits in login history. " +
        "WARNING: server-side device and session limits are unaffected.",
    default = false,
) {
    compatibleWith(COMPATIBILITY_ZALO)

    execute {
        val match = TrustedDeviceLimitParser.matchAll(1..1).single()
        val instructions = match.method.implementation!!.instructions
        val limitFields = match.instructionMatches
            .filter { it.instruction.opcode == Opcode.CONST_STRING }
            .map { it.index }
            .sorted()

        check(limitFields.size == 3) {
            "Expected mobile, PC, and web trusted-device limits"
        }

        limitFields.forEach { fieldIndex ->
            val clamp = instructions.drop(fieldIndex)
                .windowed(3)
                .firstOrNull { window ->
                    window[0].opcode == Opcode.IF_LE && window[1].opcode == Opcode.MOVE
                }
                ?: error("Trusted-device clamp moved after field at $fieldIndex")
            match.method.replaceInstruction(instructions.indexOf(clamp[1]), "nop")
        }
    }
}

private object TrustedDeviceLimitParser : Fingerprint(
    definingClass = "Lbi0/e;",
    name = "o",
    accessFlags = listOf(AccessFlags.PRIVATE, AccessFlags.FINAL),
    returnType = "V",
    parameters = listOf("Ljava/lang/Object;"),
    filters = listOf(
        string("max_trusted_devices_mobile"),
        string("max_trusted_devices_pc"),
        string("max_trusted_devices_web"),
        opcode(Opcode.IF_LE),
        opcode(Opcode.MOVE),
    ),
)
