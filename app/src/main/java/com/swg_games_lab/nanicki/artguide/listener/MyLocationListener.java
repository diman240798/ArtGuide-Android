package com.swg_games_lab.nanicki.artguide.listener;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.swg_games_lab.nanicki.artguide.fragment.map.MapFragment;

import java.lang.ref.WeakReference;

public class MyLocationListener implements LocationListener {
    private final static long minTimeForUpdate = 6000;

    private static final String TAG = "MyLocationListener";
    public WeakReference<MapFragment> mapFragment;
    private long lastUpdate;

    public MyLocationListener(MapFragment mapFragment) {
        this.mapFragment = new WeakReference<>(mapFragment);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "New location received");
        if (mapFragment == null || mapFragment.get() == null) {
            Log.d(TAG, "MapFragment is null. Return;");
            return;
        }

        Log.d(TAG, location.toString());

        MapFragment mapFragment = this.mapFragment.get();
        long currentTimeMillis = System.currentTimeMillis();
        if (mapFragment != null && currentTimeMillis - lastUpdate > minTimeForUpdate) {
            mapFragment.onLocationChanged();
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
