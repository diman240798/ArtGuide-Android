package com.swg_games_lab.nanicki.artguide.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

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
    private void showMapPermissionDialog(Context context) {

    }

    public static void requestMapRequiredPermissions(Activity activity) {
        new AlertDialog.Builder(activity)
                .setTitle("Разрешения")
                .setMessage("Следующие разрешения необходимы для работы карты")
                .setPositiveButton("Ок", (d,w) -> ActivityCompat.requestPermissions(activity, MAP_PERMISSIONS, 654))
                .setCancelable(true)
                .create().show();
    }

    public static void requestMapRequiredPermissions(Context context) {
        requestMapRequiredPermissions((AppCompatActivity) context);
    }

    public static boolean hasMapRequiredPermissions(Context context) {
        return hasPermissions(context, MAP_PERMISSIONS);
    }
}
