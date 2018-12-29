package com.swg_games_lab.nanicki.artguide.listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.swg_games_lab.nanicki.artguide.activity.MapActivity;

public class MyLocationListener implements LocationListener {

    private final LocationManager locationManager;

    public MyLocationListener(MapActivity mapActivity, LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    @Override
    public void onLocationChanged(Location location) {
//        // если местоположение не загружено, то выходим
//        if (myLocationOverlay == null) {
//            Toast.makeText(MapActivity.this, "Определяем местоположение", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        // если маркер не выбран, то выходим
//        if (lastMarker == null)
//            return;
//        // если запрос не завершен, то выходим
//        if (routeWasDrown)
//            return;
//        // Перерисовываем маршрут
////        updateRoadTask = new UpdateRoadTask(location, lastMarker, locationNet, locationGPS, MapActivity.this);;// передаем текщие координаты (location)
////        updateRoadTask.execute();
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
