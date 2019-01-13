package com.swg_games_lab.nanicki.artguide.background;


import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;

import com.swg_games_lab.nanicki.artguide.listener.RouteReceiver;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class UpdateRoadTask extends AsyncTask<Object, Void, Road[]> {

    private WeakReference<RouteReceiver> routeReceiver;
    private ArrayList<GeoPoint> waypoints = null;

    public UpdateRoadTask(Location userLocation, Marker item, RouteReceiver routeReceiver) {
        this.routeReceiver = new WeakReference<>(routeReceiver);
        ArrayList<GeoPoint> waypoints = new ArrayList<>();
        waypoints.add(new GeoPoint(userLocation.getLatitude(), userLocation.getLongitude()));
        // crutch
        IGeoPoint markerpoint = item.getPosition();
        GeoPoint marker_location = new GeoPoint(markerpoint.getLatitude(), markerpoint.getLongitude());
        waypoints.add(marker_location);


        this.waypoints = waypoints;
    }


    protected Road[] doInBackground(Object... params) {
        Context contexts = (Context) params[0];
        RoadManager roadManager = new OSRMRoadManager(contexts);
        return roadManager.getRoads(waypoints);
    }

    @Override
    protected void onPostExecute(Road[] roads) {
        if (roads != null && !isCancelled())
            routeReceiver.get().onRouteReceived(roads);
        routeReceiver = null;
    }
}
