package com.zeldrisho.patches.zalo.misc

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.AccessFlags

val openWebLinksExternallyPatch = bytecodePatch(
    name = "Open web links externally",
    description = "Forces web links to open in your default browser instead of Zalo's restricted in-app browser.",
) {
    compatibleWith(com.zeldrisho.patches.zalo.shared.Constants.COMPATIBILITY_ZALO)
    execute {
        classDefForEach { classDef ->
            if (classDef.type == "Lcom/zing/zalo/ui/zviews/vt;") {
                val mutableClass = mutableClassDefBy(classDef)
                
                val bindMethod = mutableClass.methods.firstOrNull {
                    it.name == "k" && 
                    it.returnType == "V" && 
                    AccessFlags.STATIC.isSet(it.accessFlags) &&
                    it.parameterTypes.size == 6 &&
                    it.parameterTypes[1] == "Ljava/lang/String;" &&
                    it.parameterTypes[2] == "Landroid/os/Bundle;" &&
                    it.parameterTypes[3] == "Z" &&
                    it.parameterTypes[4] == "I"
                }

                if (bindMethod != null) {
                    bindMethod.addInstructions(
                        0,
                        """
                        invoke-static {p0, p1, p2}, Lcom/zeldrisho/zalo/extension/WebLinkHelper;->tryOpenExternal(Ljava/lang/Object;Ljava/lang/String;Landroid/os/Bundle;)Z
                        move-result v0
                        if-eqz v0, :continue
                        return-void
                        :continue
                        """.trimIndent(),
                    )
                }
            }
        }
    }
}
