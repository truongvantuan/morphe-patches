package com.zeldrisho.patches.zalo.ads

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.fieldAccess
import app.morphe.patcher.methodCall
import app.morphe.patcher.opcode
import app.morphe.patcher.string
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

/*
 * Zalo 26.08.02 ad gates (versionCode 260802903, APKMirror arm64-v8a).
 *
 * Obfuscated holders (Lhb/u, Li62/d classes) are matched only under the pinned
 * COMPATIBILITY_ZALO version; re-verify per update.
 */

/** Offline-ads time window: `Lhb/u.o()Z` checks `Li62/d.I+G+time`. Forced false. */
internal object OfflineAdsWindow : Fingerprint(
    definingClass = "Lhb/u;",
    name = "o",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC),
    returnType = "Z",
    parameters = emptyList(),
    filters = listOf(
        fieldAccess(definingClass = "Li62/d;", name = "I", type = "Z"),
        methodCall(definingClass = "Ljava/lang/Long;", name = "longValue"),
    ),
)

/** Offline-ads tracker gate: `Lhb/u.k()Z` consults o() plus `Li62/d.J`. Forced false. */
internal object OfflineAdsGate : Fingerprint(
    definingClass = "Lhb/u;",
    name = "k",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC),
    returnType = "Z",
    parameters = emptyList(),
    filters = listOf(
        methodCall(definingClass = "Lhb/u;", name = "o", returnType = "Z"),
        fieldAccess(definingClass = "Li62/d;", name = "J", type = "Z"),
    ),
)

/**
 * Google-network switch: `Adtima.updateSupportNetwork()` drops admob/dfp/ima
 * only when the server flag says so. The patch NOPs the skip-branch so the
 * removal always runs. Stable SDK class; strings are unordered content keys.
 */
internal object GoogleAdsNetworkGate : Fingerprint(
    definingClass = "Lcom/adtima/Adtima;",
    name = "updateSupportNetwork",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC),
    returnType = "V",
    parameters = emptyList(),
    filters = listOf(
        fieldAccess(definingClass = "Li62/d;", name = "L", type = "Z"),
        opcode(Opcode.IF_NEZ),
        string("admob"),
        string("dfp"),
        string("ima"),
    ),
)

/**
 * Story-ads config reads (`social@story@story_ads@enable` via `Lxj0/m.f`).
 * Two call sites on 26.08.02 (StoryDetailsView + pz0/t); the patch zeroes the
 * config result register after MOVE_RESULT in every matched method.
 */
internal object StoryAdsConfig : Fingerprint(
    filters = listOf(
        string("social@story@story_ads@enable"),
        methodCall(definingClass = "Lxj0/m;", name = "f", returnType = "I"),
        opcode(Opcode.MOVE_RESULT),
        opcode(Opcode.IF_NE),
    ),
)

/**
 * Adtima limit-ad-tracking read: `com/adtima/d.doInBackground()` fetches
 * `AdvertisingIdClient.getAdvertisingIdInfo()` and stores the opt-out flag in
 * `Adtima.mIsLat` (consumed by the ad-roll renderers). Stable `com.adtima`
 * holder; the patch reports opted-out without touching the Play API.
 */
internal object AdtimaLatRead : Fingerprint(
    definingClass = "Lcom/adtima/d;",
    name = "doInBackground",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "Ljava/lang/Object;",
    parameters = emptyList(),
    filters = listOf(
        methodCall(
            definingClass = "Lcom/google/android/gms/ads/identifier/AdvertisingIdClient;",
            name = "getAdvertisingIdInfo",
        ),
        methodCall(
            definingClass = "Lcom/google/android/gms/ads/identifier/AdvertisingIdClient\$Info;",
            name = "isLimitAdTrackingEnabled",
            returnType = "Z",
        ),
    ),
)

/**
 * Community-ads config reads (`community.community_ads.enable` via `Lxj0/m.f`).
 * Two call sites on 26.08.02 (jt/m.c + jt/e.Q); patched the same way as story.
 */
internal object CommunityAdsConfig : Fingerprint(
    filters = listOf(
        string("community.community_ads.enable"),
        methodCall(definingClass = "Lxj0/m;", name = "f", returnType = "I"),
        opcode(Opcode.MOVE_RESULT),
        opcode(Opcode.IF_NE),
    ),
)

/**
 * Newsfeed sponsored banner bind: `FeedItemSuggestBanner.w(II,r31/a,ws0/a)V` is
 * called by the timeline RecyclerView adapter during onBindViewHolder for every
 * sponsored banner slot. Stable unobfuscated class; `getFeedZinstantBanner` is a
 * unique method kept across ProGuard passes. The patch forces GONE on the root view.
 */
internal object NewsfeedSponsoredBannerBind : Fingerprint(
    definingClass = "Lcom/zing/zalo/social/presentation/timeline/components/suggest/FeedItemSuggestBanner;",
    name = "w",
    returnType = "V",
    parameters = listOf("I", "I", "Lr31/a;", "Lws0/a;"),
    filters = listOf(
        methodCall(
            definingClass = "Lcom/zing/zalo/social/presentation/timeline/components/suggest/FeedItemSuggestBanner;",
            name = "getFeedZinstantBanner",
        ),
    ),
)

/**
 * Newsfeed OA (Official Account) sponsored post bind:
 * `FeedItemSuggestOA.c(kn1/h2)V` is called by the adapter for every
 * OA-promoted sponsored post. Stable unobfuscated class; `getFeedType` is unique
 * to this class. The patch forces GONE on the root view.
 */
internal object NewsfeedSponsoredOABind : Fingerprint(
    definingClass = "Lcom/zing/zalo/social/presentation/timeline/components/suggest/FeedItemSuggestOA;",
    name = "c",
    returnType = "V",
    filters = listOf(
        methodCall(
            definingClass = "Lcom/zing/zalo/social/presentation/timeline/components/suggest/FeedItemSuggestOA;",
            name = "getFeedType",
        ),
    ),
)
