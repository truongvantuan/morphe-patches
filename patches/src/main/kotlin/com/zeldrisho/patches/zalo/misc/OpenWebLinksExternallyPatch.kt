package com.zeldrisho.patches.zalo.misc

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.AppTarget
import app.morphe.patcher.patch.Compatibility
import app.morphe.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.AccessFlags
import com.zeldrisho.patches.shared.Constants.ZALO_PACKAGE

object OpenWebLinksExternallyFingerprint : Fingerprint(
    definingClass = "Lcom/zing/zalo/ui/zviews/vt;",
    name = "k",
    returnType = "V",
    parameters = listOf("L", "Ljava/lang/String;", "Landroid/os/Bundle;", "Z", "I", "L"),
    accessFlags = listOf(AccessFlags.STATIC)
)

val OpenWebLinksExternallyPatch = bytecodePatch(
    name = "Open web links externally",
    description = "Forces web links to open in your default browser instead of Zalo's restricted in-app browser.",
    compatibility = Compatibility(
        targets = setOf(AppTarget(ZALO_PACKAGE, versionCode = 260802903))
    )
) {
    execute {
        OpenWebLinksExternallyFingerprint.method.addInstructions(
            0,
            """
            invoke-static {p0, p1, p2}, Lcom/zeldrisho/zalo/extension/WebLinkHelper;->tryOpenExternal(Ljava/lang/Object;Ljava/lang/String;Landroid/os/Bundle;)Z
            move-result v0
            if-eqz v0, :continue
            return-void
            :continue
            """.trimIndent()
        )
    }
}
