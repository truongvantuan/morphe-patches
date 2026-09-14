#!/usr/bin/env python3
"""One-pass triage over decompiled or smali sources."""

import argparse
import re
from pathlib import Path

BUCKETS = [
    (
        "protections",
        [
            (
                "integrity/license",
                r"pairip|PairIp|PlayIntegrity|IntegrityManager|processLicenseResponse|validateLicenseResponse",
            ),
            (
                "signature",
                r"GET_SIGNATURES|checkSignature|verifySignature|getPackageInfo",
            ),
            ("root", r"isRooted|checkRoot|RootBeer|magisk|Superuser"),
            (
                "pinning/trust",
                r"CertificatePinner|checkServerTrusted|TrustManager|HostnameVerifier",
            ),
            (
                "emulator/debug",
                r"isEmulator|goldfish|isDebuggerConnected|waitForDebugger",
            ),
            (
                "hmac/signing",
                r"HmacSHA|Mac.getInstance|SecretKeySpec|Signature.getInstance|x-signature|computeSignature",
            ),
        ],
    ),
    (
        "billing/gates",
        [
            ("revenuecat", r"revenuecat|CustomerInfo|EntitlementInfos|getEntitlements"),
            ("adapty/qonversion/superwall", r"adapty|qonversion|superwall"),
            (
                "play-billing",
                r"BillingClient|queryPurchases|isAcknowledged|BillingResponseCode",
            ),
            ("lvl", r"LicenseChecker|Policy.LICENSED|allowAccess"),
            (
                "local-gates",
                r"isPro|isPremium|isSubscribed|hasPremium|hasPurchased|isFeatureEnabled|canAccess|isUnlocked",
            ),
            ("remote-config", r"RemoteConfig|getBoolean|featureFlag"),
        ],
    ),
    (
        "ads",
        [
            (
                "ads",
                r"MobileAds|AdRequest|interstitial|rewarded|UnityAds|AppLovin|IronSource|AudienceNetwork|loadAd|showAd",
            )
        ],
    ),
    (
        "modern stacks (kotlin)",
        [
            (
                "ktor",
                r"HttpClient|client\.get\(|client\.post\(|defaultRequest|BearerTokens|loadTokens|refreshTokens",
            ),
            ("apollo/graphql", r"ApolloClient|serverUrl|OPERATION_DOCUMENT"),
            ("koin", r"org\.koin|module \{|single<|factory<|singleOf|by inject"),
            (
                "hilt/dagger",
                r"@HiltAndroidApp|@AndroidEntryPoint|@Provides|@Binds|@Inject",
            ),
        ],
    ),
]


def main():
    p = argparse.ArgumentParser()
    p.add_argument("directory", type=Path)
    p.add_argument("--files", action="store_true")
    a = p.parse_args()
    if not a.directory.is_dir():
        p.error(f"Not a directory: {a.directory}")
    files = [x for x in a.directory.rglob("*") if x.is_file()]
    print(f"=== Hunt signals: {a.directory} ===")
    contents = {f: f.read_text(errors="replace") for f in files}
    for group, buckets in BUCKETS:
        print(f"-- {group} --")
        for label, pattern in buckets:
            hits = [
                str(f)
                for f in files
                if re.search(pattern, contents[f])
            ]
            print(f"  {label + ':':-28} {len(hits)} files")
            if a.files and 0 < len(hits) <= 20:
                print("\n".join("      " + x for x in hits))
    print(
        "\nNext: run targeted searches per bucket above, then smali-verify (see docs/reverse-engineering.md)."
    )


if __name__ == "__main__":
    main()
