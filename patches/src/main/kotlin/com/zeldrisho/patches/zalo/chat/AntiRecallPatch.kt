package com.zeldrisho.patches.zalo.chat

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.Match
import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import app.morphe.patcher.literal
import app.morphe.patcher.methodCall
import app.morphe.patcher.opcode
import app.morphe.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.NarrowLiteralInstruction
import com.zeldrisho.patches.zalo.shared.Constants.COMPATIBILITY_ZALO

private const val LOCAL_UNDO_MODE = 0x21
private const val RECALL_MODE = 0x24

/**
 * Prevents recalled-message mutations from reaching the message mutation paths.
 * The generic Object consumer r0.t() is left untouched; its typed mutation
 * helpers and the narrow server-delete dispatch are guarded.
 */
@Suppress("unused")
val antiRecallPatch = bytecodePatch(
    name = "Anti-Recall",
    description = "WARNING: Experimental client-side protection against incoming server recall commands; " +
        "runtime retention is not yet accepted across all delivery states. " +
        "Local user-initiated message deletion and cache cleanup remain unchanged.",
    default = false,
) {
    compatibleWith(COMPATIBILITY_ZALO)

    execute {
        // Replace the recall-mode constant with return-void. This is one
        // instruction for one instruction: no new registers, locals, labels,
        // or verifier frames are introduced. The 0x21 local-undo arm remains.
        disableRecallArm(AntiRecallDatabaseFingerprint.matchAll(1..1).single())
        disableRecallArm(AntiRecallTabMessageFingerprint.matchAll(1..1).single())

        // The server-delete dispatcher can reach additional mutation work after
        // the typed helpers. Stop only its q0 dispatch; other event types keep
        // their existing path.
        val dispatch = AntiRecallDispatchFingerprint.matchAll(1..1).single()
        val dispatchCall = dispatch.instructionMatches.single()
        dispatch.method.replaceInstruction(
            dispatchCall.index,
            """
            sget v1, Lei/x2;->recalled_group_msg:I
            invoke-static {v1}, Lxo1/l3;->R(I)Ljava/lang/String;
            move-result-object v1
            const-string v0, "["
            invoke-virtual {v0, v1}, Ljava/lang/String;->concat(Ljava/lang/String;)Ljava/lang/String;
            move-result-object v0
            const-string v1, "] "
            invoke-virtual {v0, v1}, Ljava/lang/String;->concat(Ljava/lang/String;)Ljava/lang/String;
            move-result-object v0
            iget-object v1, p1, Lo00/q;->c:Ljava/lang/String;
            invoke-virtual {v0, v1}, Ljava/lang/String;->concat(Ljava/lang/String;)Ljava/lang/String;
            move-result-object v0
            iput-object v0, p1, Lo00/q;->c:Ljava/lang/String;
            return-void
            """.trimIndent(),
        )
    }
}

/** Exact f0 signature plus its mode-constant opcode anchor. */
private object AntiRecallDatabaseFingerprint : Fingerprint(
    definingClass = "Lu40/d;",
    name = "f0",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "V",
    parameters = listOf("Lo00/q;", "Z", "Lo00/t0;"),
    filters = recallArmFilters,
)

/** Narrow server-delete dispatch into the q0 mutation consumer. */
private object AntiRecallDispatchFingerprint : Fingerprint(
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

/** The follow-up tab-message update also runs for incoming recalls. */
private object AntiRecallTabMessageFingerprint : Fingerprint(
    definingClass = "Lwn1/r0;",
    name = "u",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "V",
    parameters = listOf("Lcom/zing/zalo/data/entity/chat/message/MessageId;", "Z", "Lo00/t0;", "Z"),
    filters = recallArmFilters,
)

private val recallArmFilters = listOf(
    // Anchor the fingerprint to the recall mode, rather than an arbitrary
    // constant in this method.
    literal(RECALL_MODE),
)

private fun disableRecallArm(match: Match) {
    checkRecallModePattern(match.originalMethod)
    val instructions = match.originalMethod.implementation?.instructions?.toList().orEmpty()
    val recallConstantIndex = instructions.indexOfFirst { instruction ->
        instruction.opcode == Opcode.CONST_16 &&
            (instruction as? NarrowLiteralInstruction)?.narrowLiteral == RECALL_MODE
    }
    check(recallConstantIndex >= 0) {
        "Anti-recall target changed: recall mode constant 0x24 not found"
    }
    // Keep the original text, but prefix it with Zalo's localized recalled
    // label. Returning afterwards preserves the message instead of persisting
    // a destructive delete/update operation.
    match.method.replaceInstruction(
        recallConstantIndex,
        """
        sget p2, Lei/x2;->recalled_group_msg:I
        invoke-static {p2}, Lxo1/l3;->R(I)Ljava/lang/String;
        move-result-object p2
        const-string v1, "["
        invoke-virtual {v1, p2}, Ljava/lang/String;->concat(Ljava/lang/String;)Ljava/lang/String;
        move-result-object v1
        const-string v0, "] "
        invoke-virtual {v1, v0}, Ljava/lang/String;->concat(Ljava/lang/String;)Ljava/lang/String;
        move-result-object v1
        iget-object v0, p1, Lo00/q;->c:Ljava/lang/String;
        invoke-virtual {v1, v0}, Ljava/lang/String;->concat(Ljava/lang/String;)Ljava/lang/String;
        move-result-object v0
        iput-object v0, p1, Lo00/q;->c:Ljava/lang/String;
        return-void
        """.trimIndent(),
    )
}

/** Fails closed if the pinned recall/delete mode constants move or change. */
internal fun checkRecallModePattern(method: com.android.tools.smali.dexlib2.iface.Method) {
    val modeConstants = method.implementation?.instructions?.toList().orEmpty()
        .filter { it.opcode == Opcode.CONST_16 }
        .mapNotNull { (it as? NarrowLiteralInstruction)?.narrowLiteral }
    check(RECALL_MODE in modeConstants && LOCAL_UNDO_MODE in modeConstants) {
        "Anti-recall target changed: mutation method no longer contains both 0x21 and 0x24 mode constants"
    }
}
