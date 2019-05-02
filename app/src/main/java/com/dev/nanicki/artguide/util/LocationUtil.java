package com.dev.nanicki.artguide.util;

import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;

import org.osmdroid.util.GeoPoint;

public class LocationUtil {

    public static Location getUserLocation(LocationManager locationManager) {
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        return location;
    }

    public static GeoPoint getGeoPointByLocation(@NonNull Location location) {
        return new GeoPoint(location.getLatitude(), location.getLongitude());
    }

    public static GeoPoint getGeoPointByLocationOrDefault(Location location, Location defaultLocation) {
        return location == null
                ? getGeoPointByLocation(defaultLocation)
                : getGeoPointByLocation(location);
    }

    public static GeoPoint getGeoPointByLocationOrDefault(Location location, GeoPoint defaultPoint) {
        return location == null
                ? defaultPoint
                : getGeoPointByLocation(location);
    }
}
