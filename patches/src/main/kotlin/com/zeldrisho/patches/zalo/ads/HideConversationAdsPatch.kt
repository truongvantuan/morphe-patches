package com.zeldrisho.patches.zalo.ads

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.Opcode
import com.zeldrisho.patches.zalo.shared.Constants.COMPATIBILITY_ZALO

/**
 * Hides third-party server-stitched Official Account (OA) ads (e.g. [AD]) and
 * first-party system promotional accounts (e.g. Media Box) from the main chat list.
 *
 * It hooks `bw/r.smali` (ConversationAdapter) `getView()` and invokes a companion
 * extension method that forces the View height to 0 if the `ContactProfile` is
 * flagged as an ad or matches targeted names.
 */
@Suppress("unused")
val hideConversationAdsPatch = bytecodePatch(
    name = "Hide conversation list ads",
    description = "Completely hides injected Official Account promos (like SenTia School [AD]) " +
        "and first-party promotional accounts (Media Box) from the main Messages tab.",
    default = true,
) {
    compatibleWith(COMPATIBILITY_ZALO)
    extendWith("extensions/zalo.mpe")

    execute {
        classDefForEach { classDef ->
            if (classDef.type == "Lcom/zing/zalo/ui/moduleview/message/NormalMsgModuleView;") {
                val mutableClass = mutableClassDefBy(classDef)
                
                val bindMethod = mutableClass.methods.firstOrNull {
                    it.name == "e" && it.parameterTypes == listOf("Lr00/c0;", "I")
                }
                
                if (bindMethod != null) {
                    val impl = bindMethod.implementation
                    if (impl != null) {
                        val returnIndex = impl.instructions.indexOfLast { it.opcode == Opcode.RETURN_VOID }
                        if (returnIndex != -1) {
                            val injection = """
                                # p1 is Lr00/c0;
                                if-eqz p1, :skip_hide_ads_rv
                                
                                iget-object v0, p1, Lr00/c0;->c:Lcom/zing/zalo/control/ContactProfile;
                                if-eqz v0, :skip_hide_ads_rv
                                
                                # p0 is the View (NormalMsgModuleView)
                                # v0 is the ContactProfile
                                invoke-static { p0, v0 }, Lcom/zeldrisho/zalo/extension/HideAdsHelper;->hideIfAd(Landroid/view/View;Ljava/lang/Object;)V
                                
                                :skip_hide_ads_rv
                            """.trimIndent()
                            
                            bindMethod.addInstructions(returnIndex, injection)
                        }
                    }
                }
            }
            if (classDef.type == "Lbw/r;") {
                val mutableClass = mutableClassDefBy(classDef)
                val getViewMethod = mutableClass.methods.firstOrNull {
                    it.name == "getView" && it.parameterTypes == listOf("I", "Landroid/view/View;", "Landroid/view/ViewGroup;")
                } ?: return@classDefForEach

                val impl = getViewMethod.implementation ?: return@classDefForEach

                // Find the return-object instruction
                val returnIndex = impl.instructions.indexOfLast { it.opcode == Opcode.RETURN_OBJECT }
                if (returnIndex != -1) {
                    val returnInstruction = impl.instructions[returnIndex] as com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
                    val viewReg = returnInstruction.registerA

                    val injection = """
                        # Fetch the ContactProfile wrapper (Lbw/o;)
                        invoke-virtual/range { p0 .. p1 }, Lbw/r;->a(I)Lbw/o;
                        move-result-object v0
                        if-eqz v0, :skip_hide_ads

                        # Get the ContactProfile object
                        iget-object v0, v0, Lbw/o;->a:Lcom/zing/zalo/control/ContactProfile;
                        if-eqz v0, :skip_hide_ads

                        # Move view to a low register (v1) to guarantee invoke-static works
                        move-object v1, v$viewReg

                        # Call the companion extension helper
                        invoke-static { v1, v0 }, Lcom/zeldrisho/zalo/extension/HideAdsHelper;->hideIfAd(Landroid/view/View;Ljava/lang/Object;)V

                        :skip_hide_ads
                    """.trimIndent()

                    getViewMethod.addInstructions(returnIndex, injection)
                }
            }
        }
    }
}
