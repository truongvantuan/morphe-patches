package com.zeldrisho.zalo.extension;

import android.view.View;
import android.view.ViewGroup;

public class HideAdsHelper {
  public static void hideIfAd(View view, Object itemWrapper) {
    if (view == null || itemWrapper == null) return;
    try {
      String viewClassName = view.getClass().getName();
      if (viewClassName.endsWith("PromotedModuleView")
          || viewClassName.endsWith("MediaBoxModuleView")
          || viewClassName.endsWith("BizBoxModuleView")) {
        forceHide(view);
        return;
      }

      boolean isAd = false;
      Class<?> wrapperClass = itemWrapper.getClass();

      // Check if it's an ad from Zalo-specific item fields (usually boolean flags)
      // T0 was the old flag in ContactProfile.

      // Let's recursively search all objects in the wrapper fields to see if they are ads.
      // We will look for a string "SenTia School" or "[AD]" or check boolean fields.

      // Instead of complex reflection, let's just check the string fields directly on the View or Wrapper!
      // But wait, the view hasn't fully rendered text yet.

      // Let's dump the wrapper fields
      Object profileObj = null;
      try {
          // In 26.08.02, field 'e' holds Conversation
          profileObj = wrapperClass.getField("e").get(itemWrapper);
      } catch (Exception ignored) {
          try {
              // Older version, field 'c' holds ContactProfile
              profileObj = wrapperClass.getField("c").get(itemWrapper);
          } catch (Exception ignored2) {}
      }

      if (profileObj == null) profileObj = itemWrapper; // fallback

      Class<?> profileClass = profileObj.getClass();

      // Old T0 flag
      try {
        isAd = profileClass.getField("T0").getBoolean(profileObj);
      } catch (Exception e) {}

      // New Conversation might have different fields for ads.
      // But ads usually have something like "isPromoted" or a specific category.
      // Another way: Search for string fields containing "[AD]" or "Media Box"
      java.lang.reflect.Field[] fields = profileClass.getDeclaredFields();
      for (java.lang.reflect.Field f : fields) {
          if (f.getType() == String.class) {
              f.setAccessible(true);
              String val = (String) f.get(profileObj);
              if (val != null) {
                  if (val.contains("[AD]") || val.equals("Media Box") || val.contains("SenTia")) {
                      isAd = true;
                      break;
                  }
              }
          }
      }

      if (isAd) {
        forceHide(view);
      } else {
        restore(view);
      }
    } catch (Exception e) {
    }
  }

  public static void forceHide(View view) {
    if (view == null) return;
    try {
      view.setVisibility(View.GONE);
      ViewGroup.LayoutParams params = view.getLayoutParams();
      if (params != null) {
        params.height = 1;
        params.width = 0;
        view.setLayoutParams(params);
      }
    } catch (Exception e) {}
  }

  public static void restore(View view) {
    if (view == null) return;
    try {
      ViewGroup.LayoutParams params = view.getLayoutParams();
      if (params != null && params.height == 1) {
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        view.setLayoutParams(params);
        view.setVisibility(View.VISIBLE);
      }
    } catch (Exception e) {}
  }
}
