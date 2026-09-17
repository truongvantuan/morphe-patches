package com.zeldrisho.patches.zalo.privacy

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import app.morphe.patcher.methodCall
import app.morphe.patcher.opcode
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.string
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import com.zeldrisho.patches.zalo.shared.Constants.COMPATIBILITY_ZALO

/**
 * Keeps the username source enabled in Manage search and friend-request sources.
 *
 * Zalo already renders this row and persists it through the normal privacy
 * setting endpoint; this only overrides the locally cached checkbox value.
 * The server may still reject or ignore the setting.
 */
@Suppress("unused")
val enableZaloUsernameFriendSearchPatch = bytecodePatch(
    name = "Enable username friend search",
    description = "Enables the username option under Manage search and friend-request sources.",
    default = true,
) {
    compatibleWith(COMPATIBILITY_ZALO)

    execute {
        val match = UsernameSettingRead.matchAll(1..1).single()
        val settingRead = match.instructionMatches
            .single { it.instruction.opcode == Opcode.INVOKE_STATIC }
        check(match.method.implementation!!.instructions[settingRead.index + 1].opcode == Opcode.MOVE_RESULT) {
            "Username privacy read moved; re-hunt SettingManageSourceFriendView.C6()"
        }
        match.method.replaceInstruction(settingRead.index + 1, "const/4 v1, 0x1")
    }
}

private object UsernameSettingRead : Fingerprint(
    definingClass = "Lcom/zing/zalo/ui/settings/subsettings/SettingManageSourceFriendView;",
    name = "C6",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "V",
    parameters = emptyList(),
    filters = listOf(
        string("PRIVACY_SETTING_FRIEND_REQUEST_USERNAME_%s"),
        methodCall(
            opcode = Opcode.INVOKE_STATIC,
            definingClass = "Lu40/p0;",
            name = "W",
            parameters = listOf("Ljava/lang/String;", "Z", "Z", "Z"),
            returnType = "Z",
        ),
    ),
)
