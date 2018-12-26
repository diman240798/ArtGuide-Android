package com.swg_games_lab.nanicki.artguide.background;


import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.widget.Toast;

import com.swg_games_lab.nanicki.artguide.MapActivity;
import com.swg_games_lab.nanicki.artguide.R;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class UpdateRoadTask extends AsyncTask<Object, Void, Road[]> {

    private final WeakReference<MapActivity> activity;
    private final ArrayList<GeoPoint> waypoints;

    public UpdateRoadTask(Location userLocation, OverlayItem item, MapActivity mapActivity) {
        this.activity = new WeakReference<MapActivity>(mapActivity);
        Context context = mapActivity;

        ArrayList<GeoPoint> waypoints = new ArrayList<>();
        waypoints.add(new GeoPoint(userLocation.getLatitude(), userLocation.getLongitude()));

        // crutch
        IGeoPoint markerpoint = item.getPoint();
        GeoPoint marker_location = new GeoPoint(markerpoint.getLatitude(), markerpoint.getLongitude());
        waypoints.add(marker_location);


        this.waypoints = waypoints;
    }


    protected Road[] doInBackground(Object... params) {
        MapActivity mapActivity = activity.get();
        RoadManager roadManager = new OSRMRoadManager(mapActivity);
        return roadManager.getRoads(waypoints);
    }

    @Override
    protected void onPostExecute(Road[] roads) {
        MapView map = activity.get().map;
        Context context = activity.get();
        //Toast.makeText(map.getContext(), "Route was received successfully", Toast.LENGTH_SHORT).show();
        if (roads == null)
            return;
        if (roads[0].mStatus == Road.STATUS_TECHNICAL_ISSUE)
            Toast.makeText(context, "Technical issue when getting the route",
                    Toast.LENGTH_SHORT).show();
        else if (roads[0].mStatus > Road.STATUS_TECHNICAL_ISSUE) //functional issues
            Toast.makeText(context, "No possible route here",
                    Toast.LENGTH_SHORT).show();


        List<Overlay> mapOverlays = map.getOverlays();
        Polyline roadPolyline = RoadManager.buildRoadOverlay(roads[0]);

        String routeDesc = roads[0].getLengthDurationText(context, -1);
        roadPolyline.setTitle(context.getString(R.string.app_name) + " - " + routeDesc);
        roadPolyline.setInfoWindow(new BasicInfoWindow(org.osmdroid.bonuspack.R.layout.bonuspack_bubble, map));
        roadPolyline.setRelatedObject(0);
        roadPolyline.setWidth(5);
        //roadPolyline.setOnClickListener(new RoadOnClickListener());

        // FIXME WHY it here
//            if (postExecuteComplited) {
//                mapOverlays.set(mapOverlays.size() - 1, roadPolyline);
//            } else mapOverlays.add(roadPolyline);
//            postExecuteComplited = true;
    }

}
