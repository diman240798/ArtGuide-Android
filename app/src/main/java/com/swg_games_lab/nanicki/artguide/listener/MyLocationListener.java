package com.swg_games_lab.nanicki.artguide.listener;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.swg_games_lab.nanicki.artguide.activity.MapActivity;

import java.lang.ref.WeakReference;

public class MyLocationListener implements LocationListener {

    private static final String TAG = "MyLocationListener";
    public WeakReference<MapActivity> mapActivity;
    private long minTimeForUpdate = 2000;
    private long lastUpdate;

    public MyLocationListener(MapActivity mapActivity) {
        this.mapActivity = new WeakReference<>(mapActivity);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "New location received");
        if (mapActivity == null || mapActivity.get() == null) {
            Log.d(TAG, "MapActivity is null. Return;");
            return;
        }

        Log.d(TAG, location.toString());

        MapActivity mapActivity = this.mapActivity.get();
        long currentTimeMillis = System.currentTimeMillis();
        if (mapActivity != null && currentTimeMillis - lastUpdate > minTimeForUpdate) {
            mapActivity.requestDrawRoute(null);
            lastUpdate = currentTimeMillis;
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
