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
            val viewType = classDef.type
            if (viewType == "Lcom/zing/zalo/ui/moduleview/message/NormalMsgModuleView;" ||
                viewType == "Lcom/zing/zalo/ui/moduleview/message/PromotedModuleView;" ||
                viewType == "Lcom/zing/zalo/ui/moduleview/message/MediaBoxModuleView;" ||
                viewType == "Lcom/zing/zalo/ui/moduleview/message/BizBoxModuleView;"
            ) {
                val mutableClass = mutableClassDefBy(classDef)

                val bindMethod = mutableClass.methods.firstOrNull {
                    it.name == "e" && it.parameterTypes.size == 2 && it.parameterTypes[1] == "I"
                }

                if (bindMethod != null) {
                    val injection = if (viewType == "Lcom/zing/zalo/ui/moduleview/message/NormalMsgModuleView;") {
                        """
                        if-eqz p1, :skip_hide_ads_rv
                        invoke-static { p0, p1 }, Lcom/zeldrisho/zalo/extension/HideAdsHelper;->hideIfAd(Landroid/view/View;Ljava/lang/Object;)V

                        :skip_hide_ads_rv
                        """.trimIndent()
                    } else {
                        """
                        invoke-static { p0 }, Lcom/zeldrisho/zalo/extension/HideAdsHelper;->forceHide(Landroid/view/View;)V
                        """.trimIndent()
                    }

                    bindMethod.addInstructions(0, injection)
                }
            }
            // Remove the legacy ListView getView() hook as it is no longer valid or needed in newer Zalo versions which use RecyclerViews for Inbox.

        }
    }
}
