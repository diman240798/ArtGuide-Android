package com.dev.nanicki.artguide.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

public class PermissionUtil {
    public static final int MAP_PERM_REQUEST_CODE = 645;
    private static final String[] MAP_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
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
    private void showMapPermissionDialog(Context context) {

    }

    public static void requestMapRequiredPermissions(Activity activity) {
        new AlertDialog.Builder(activity)
                .setTitle("Разрешения")
                .setMessage("Следующие разрешения необходимы для работы карты")
                .setPositiveButton("Ок", (d,w) -> ActivityCompat.requestPermissions(activity, MAP_PERMISSIONS, MAP_PERM_REQUEST_CODE))
                .setCancelable(true)
                .create().show();
    }

    public static void requestMapRequiredPermissions(Fragment fragment) {
        new AlertDialog.Builder(fragment.getActivity())
                .setTitle("Разрешения")
                .setMessage("Следующие разрешения необходимы для работы карты")
                .setPositiveButton("Ок", (d,w) -> fragment.requestPermissions(MAP_PERMISSIONS, MAP_PERM_REQUEST_CODE))
                .setCancelable(true)
                .create().show();
    }

    public static boolean hasMapRequiredPermissions(Context context) {
        return hasPermissions(context, MAP_PERMISSIONS);
    }
}
