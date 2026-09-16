package com.zeldrisho.patches.zalo.media

import app.morphe.patcher.PackageMetadata
import app.morphe.patcher.PatcherConfig
import app.morphe.patcher.patch.BytecodePatchContext
import com.android.tools.smali.dexlib2.DexFileFactory
import com.android.tools.smali.dexlib2.Opcodes
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ZaloMediaFingerprintsTest {
    @get:Rule val temporary = TemporaryFolder()

    private fun context(): BytecodePatchContext {
        val config = PatcherConfig(
            apkFile = temporary.newFile("input.apk"),
            temporaryFilesPath = temporary.newFolder(),
        )
        val metadata = PackageMetadata::class.java.constructors.single().newInstance(
            "com.zing.zalo",
            "26.08.01",
            "260801903",
            null,
        )
        return BytecodePatchContext::class.java
            .getConstructor(PatcherConfig::class.java, PackageMetadata::class.java)
            .newInstance(config, metadata)
    }

    @Test
    fun matchesPinnedZaloApk() {
        val path = System.getenv("ZALO_TEST_APK")
        assumeTrue("Set ZALO_TEST_APK to the pinned Zalo base APK", !path.isNullOrBlank())
        val container = DexFileFactory.loadDexContainer(File(path!!), Opcodes.getDefault())
        val clazz = container.dexEntryNames.asSequence()
            .map { container.getEntry(it)!!.dexFile.classes }
            .flatMap { it.asSequence() }
            .first { it.type == "Lvk0/g;" }

        with(context()) {
            MediaExpiryStatus.clearMatch()
            val matches = MediaExpiryStatus.matchAll(clazz, 1..1)
            assertEquals(1, matches.size)
            val method = matches.single().originalMethod
            assertEquals("n", method.name)
            assertTrue(method.accessFlags and 0x10 != 0, "target method must remain final")
        }
    }

    @Test
    fun matchesPinnedOriginalPhotoQualityMethods() {
        val path = System.getenv("ZALO_TEST_APK")
        assumeTrue("Set ZALO_TEST_APK to the pinned Zalo base APK", !path.isNullOrBlank())
        val container = DexFileFactory.loadDexContainer(File(path!!), Opcodes.getDefault())
        val classes = container.dexEntryNames.asSequence()
            .map { container.getEntry(it)!!.dexFile.classes }
            .flatMap { it.asSequence() }
            .associateBy { it.type }

        with(context()) {
            val qualityClass = classes.getValue("Luh1/u;")
            val fingerprints = listOf(
                SelectedMediaQuality to "c",
                OriginalMediaQualityEnabled to "b",
                OriginalMediaQualityEntitled to "f",
                OriginalMediaQualityAvailable to "e",
            )
            fingerprints.forEach { (fingerprint, expectedName) ->
                fingerprint.clearMatch()
                val matches = fingerprint.matchAll(qualityClass, 1..1)
                assertEquals(1, matches.size, "${fingerprint::class.simpleName} must match once")
                assertEquals(expectedName, matches.single().originalMethod.name)
            }

            QualityPickerArguments.clearMatch()
            val pickerMatches = QualityPickerArguments.matchAll(
                classes.getValue("Luh1/b;"),
                1..1,
            )
            assertEquals(1, pickerMatches.size)
            assertEquals("a", pickerMatches.single().originalMethod.name)

            PickerQualityInitialization.clearMatch()
            val initializationMatches = PickerQualityInitialization.matchAll(
                classes.getValue("Lcom/zing/zalo/ui/picker/mediapicker/MediaPickerView;"),
                1..1,
            )
            assertEquals(1, initializationMatches.size)
            assertEquals("b7", initializationMatches.single().originalMethod.name)

            PhotoQualityChipUpdate.clearMatch()
            val chipMatches = PhotoQualityChipUpdate.matchAll(
                classes.getValue("Lcom/zing/zalo/ui/picker/mediapicker/MediaPickerView;"),
                1..1,
            )
            assertEquals(1, chipMatches.size)
            assertEquals("y6", chipMatches.single().originalMethod.name)

            LandingPageQualityChipUpdate.clearMatch()
            val landingMatches = LandingPageQualityChipUpdate.matchAll(
                classes.getValue("Lcom/zing/zalo/ui/picker/landingpage/LandingPageView;"),
                1..1,
            )
            assertEquals(1, landingMatches.size)
            assertEquals("B6", landingMatches.single().originalMethod.name)

            LandingPageQualityChipInitialization.clearMatch()
            val landingInitializationMatches = LandingPageQualityChipInitialization.matchAll(
                classes.getValue("Lcom/zing/zalo/ui/picker/landingpage/LandingPageView;"),
                1..1,
            )
            assertEquals(1, landingInitializationMatches.size)
            assertEquals("W4", landingInitializationMatches.single().originalMethod.name)

            ChatInputBarQualityChipUpdate.clearMatch()
            val chatInputBarMatches = ChatInputBarQualityChipUpdate.matchAll(
                classes.getValue("Lcom/zing/zalo/ui/chat/widget/inputbar/ChatInputBar;"),
                1..1,
            )
            assertEquals(1, chatInputBarMatches.size)
            assertEquals("r", chatInputBarMatches.single().originalMethod.name)

            QualityChipLabel.clearMatch()
            val labelMatches = QualityChipLabel.matchAll(
                classes.getValue("Lcom/zing/zalo/ui/picker/mediapicker/MediaPickerQualityChip;"),
                1..1,
            )
            assertEquals(1, labelMatches.size)
            assertEquals("setText", labelMatches.single().originalMethod.name)
        }
    }
}
