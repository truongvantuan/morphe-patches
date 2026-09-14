package com.zeldrisho.patches.zalo.native

import app.morphe.patcher.patch.rawResourcePatch
import com.zeldrisho.patches.zalo.shared.Constants.COMPATIBILITY_ZALO
import java.io.RandomAccessFile

private const val LIBRARY_PATH = "lib/arm64-v8a/libnative_utils.so"
private val INIT_PATTERN = byteArrayOf(
    0xe1.toByte(), 0x03, 0x15, 0xaa.toByte(), 0xdb.toByte(), 0x0d, 0x00, 0x94.toByte(),
    0x00, 0x01, 0x00, 0x36,
    0xa8.toByte(), 0x01, 0x00, 0xb0.toByte()
)

private val EXIT_PATTERN = byteArrayOf(
    0x08, 0x39, 0x42, 0xf9.toByte(), 0xa0.toByte(), 0x87.toByte(), 0x3d, 0xad.toByte(),
    0x00, 0x01, 0x3f, 0xd6.toByte(),
    0x68, 0x16, 0x40, 0xf9.toByte()
)

private val NOP = byteArrayOf(0x1f, 0x20, 0x03, 0xd5.toByte())

private fun indexOf(haystack: ByteArray, needle: ByteArray): Int {
    for (i in 0..haystack.size - needle.size) {
        var match = true
        for (j in needle.indices) {
            if (haystack[i + j] != needle[j]) {
                match = false
                break
            }
        }
        if (match) return i
    }
    return -1
}

/**
 * Forces the successful native cryptographic initialization path while
 * suppressing the JNI CallStaticVoidMethodV that dispatches
 * java/lang/System.exit(I)V.
 *
 * Uses dynamic byte pattern scanning to locate the instructions in the arm64 native library,
 * making the patch resilient to minor compiler shifts.
 */
@Suppress("unused")
val bypassZaloNativeStartupTamperPatch = rawResourcePatch(
    name = "Bypass native startup tamper check",
    description = "Preserves native key initialization and NOPs only the JNI System.exit " +
        "dispatch in the arm64 binary. Uses pattern scanning for resiliency.",
    default = true,
) {
    compatibleWith(COMPATIBILITY_ZALO)

    execute {
        val library = get(LIBRARY_PATH, true)
        RandomAccessFile(library, "rw").use { file ->
            val fileBytes = ByteArray(file.length().toInt())
            file.readFully(fileBytes)

            val initMatch = indexOf(fileBytes, INIT_PATTERN)
            check(initMatch != -1) { "Could not find initialization branch pattern in libnative_utils.so" }
            val initOffset = initMatch + 8L // offset to the 00 01 00 36 instruction

            val exitMatch = indexOf(fileBytes, EXIT_PATTERN)
            check(exitMatch != -1) { "Could not find exit call pattern in libnative_utils.so" }
            val exitOffset = exitMatch + 8L // offset to the 00 01 3f d6 instruction

            file.seek(initOffset)
            file.write(NOP)
            file.seek(exitOffset)
            file.write(NOP)
        }
    }
}

