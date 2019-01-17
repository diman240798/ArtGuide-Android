package com.swg_games_lab.nanicki.artguide.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

public class PermissionUtil {
    private static final String[] MAP_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void requestMapRequiredPermissions(Activity activity) {
        ActivityCompat.requestPermissions(activity, MAP_PERMISSIONS, 654);
    }

    public static boolean hasMapRequiredPermissions(Context context) {
        return hasPermissions(context, MAP_PERMISSIONS);
    }
}
