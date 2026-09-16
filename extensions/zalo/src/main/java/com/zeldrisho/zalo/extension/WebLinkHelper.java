package com.zeldrisho.zalo.extension;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import java.lang.reflect.Method;

public class WebLinkHelper {
    public static boolean tryOpenExternal(Object presenter, String url, Bundle bundle) {
        if (url == null || url.isEmpty()) return false;
        
        if (bundle != null) {
            if (bundle.getBoolean("from_mini_app", false)) return false;
            if (bundle.containsKey("oa_h5")) return false;
        }
        
        try {
            Context hostContext = resolveHostContext(presenter);
            if (hostContext == null) return false;
            
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.trim()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            hostContext.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException missingHandler) {
            return false;
        } catch (Throwable startFailure) {
            return false;
        }
    }

    private static Context resolveHostContext(Object host) {
        if (host instanceof Context) {
            return (Context) host;
        }
        if (host != null) {
            Class<?> current = host.getClass();
            while (current != null && current != Object.class) {
                for (Method method : current.getDeclaredMethods()) {
                    if (method.getParameterTypes().length == 0
                            && method.getReturnType() == Context.class) {
                        try {
                            method.setAccessible(true);
                            Object result = method.invoke(host);
                            if (result instanceof Context) {
                                return (Context) result;
                            }
                        } catch (Throwable ignored) {
                        }
                    }
                }
                current = current.getSuperclass();
            }
        }
        return null;
    }
}
