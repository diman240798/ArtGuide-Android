package com.swg_games_lab.nanicki.artguide.util;

import android.location.Location;
import android.support.annotation.NonNull;

import org.osmdroid.util.GeoPoint;

public class LocationUtil {
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
