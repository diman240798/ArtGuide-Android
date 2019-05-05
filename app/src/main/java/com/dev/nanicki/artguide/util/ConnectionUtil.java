package com.dev.nanicki.artguide.util;

import android.content.Context;
import android.content.DialogInterface;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.appcompat.app.AlertDialog;

public class ConnectionUtil {
    public static boolean isConnected(LocationManager manager, Context context) {

        boolean isGPSconnected = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkConnected = isNetworkConnected(context);
        return isGPSconnected && isNetworkConnected;
    }

    public static void buildAlertMessageNoConncetion(Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder
                .setTitle("Соединение отсутсвует")
                .setMessage("Если вы видите данное сообщение, значит на вашем телефоне отсуствует интернет-соединение или отключен GPS. Обе эти функциональности необходимы для корректной работы карты.")
                .setCancelable(false)
                .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
//                        context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    private static boolean isNetworkConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo connection = manager.getActiveNetworkInfo();
        return connection != null && connection.isConnectedOrConnecting();
    }


}
