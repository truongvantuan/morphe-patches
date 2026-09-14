package com.zeldrisho.zalo.extension;

import android.view.View;
import android.view.ViewGroup;

public class HideAdsHelper {
    public static void hideIfAd(View view, Object profileObj) {
        if (view == null || profileObj == null) return;
        try {
            boolean isAd = false;
            Class<?> profileClass = profileObj.getClass();
            
            // Check T0 boolean (OA ad promo flag)
            try {
                java.lang.reflect.Field t0Field = profileClass.getField("T0");
                isAd = t0Field.getBoolean(profileObj);
            } catch (Exception e) {
                // Ignore if field is obfuscated or missing
            }
            
            // Check for Media Box by name or known string fields
            if (!isAd) {
                // Try standard fields that might hold the display name
                String[] possibleNameFields = {"d", "S0", "c", "b", "I0"};
                for (String fieldName : possibleNameFields) {
                    try {
                        java.lang.reflect.Field nameField = profileClass.getField(fieldName);
                        Object name = nameField.get(profileObj);
                        if (name instanceof String && "Media Box".equals(name)) {
                            isAd = true;
                            break;
                        }
                    } catch (Exception e) {
                        // Ignore
                    }
                }
            }
            
            if (isAd) {
                view.setVisibility(View.GONE);
                ViewGroup.LayoutParams params = view.getLayoutParams();
                if (params != null) {
                    params.height = 1; // 1px height to avoid division by zero or recycling bugs
                    params.width = 0;
                    view.setLayoutParams(params);
                }
            } else {
                // Restore visibility if it was recycled
                ViewGroup.LayoutParams params = view.getLayoutParams();
                if (params != null && params.height == 1) {
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    view.setLayoutParams(params);
                    view.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            // Failsafe
        }
    }
}
