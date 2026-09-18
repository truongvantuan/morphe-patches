package com.zeldrisho.patches.zalo.privacy

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions

import app.morphe.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.AccessFlags
import com.zeldrisho.patches.shared.bytecode.clearBody
import com.zeldrisho.patches.zalo.shared.Constants.COMPATIBILITY_ZALO

/**
 * Stops the dedicated outbound typing-status send path while leaving message
 * transport and incoming typing rendering untouched.
 */
@Suppress("unused")
val suppressTypingStatusPatch = bytecodePatch(
    name = "Suppress outbound typing status",
    description = "Stops Zalo from sending typing indicators. Incoming status rendering and messages remain unchanged.",
    default = true,
) {
    compatibleWith(COMPATIBILITY_ZALO)

    execute {
        classDefForEach { classDef ->
            if (classDef.type == "Lm00/q;" || classDef.type == "Ll00/r;") {
                val mutableClass = mutableClassDefBy(classDef)
                
                val method = mutableClass.methods.firstOrNull {
                    it.name == "S" && 
                    it.returnType == "V" && 
                    AccessFlags.PUBLIC.isSet(it.accessFlags) &&
                    AccessFlags.FINAL.isSet(it.accessFlags) &&
                    it.parameterTypes == listOf("Ljava/lang/String;", "I", "Z", "Z")
                }
                
                if (method != null) {
                    method.clearBody()
                    method.addInstructions(0, "return-void")
                }
            }
        }
    }
}
