package com.swg_games_lab.nanicki.artguide.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.swg_games_lab.nanicki.artguide.R;
import com.swg_games_lab.nanicki.artguide.activity.attraction_info.wikiAttractionActivity;
import com.swg_games_lab.nanicki.artguide.background.UpdateRoadTask;
import com.swg_games_lab.nanicki.artguide.csv.CSVreader;
import com.swg_games_lab.nanicki.artguide.enums.AttractionType;
import com.swg_games_lab.nanicki.artguide.listener.MyLocationListener;
import com.swg_games_lab.nanicki.artguide.listener.RouteReceiver;
import com.swg_games_lab.nanicki.artguide.model.Place;
import com.swg_games_lab.nanicki.artguide.util.MarkerUtil;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.IconOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.swg_games_lab.nanicki.artguide.util.LocationUtil.getUserLocation;

public class MapInitActivity extends AppCompatActivity implements RouteReceiver {

    protected static final String TAG = "MapInitActivity";
    protected volatile boolean routeBuilding = true;


    // Views
    public MapView map;
    protected MyLocationNewOverlay myLocationOverlay;
    protected LocationManager locationManager;

    // Markers
    protected RadiusMarkerClusterer museumMarkers, theatreMarkers, memorialMarkers, stadiumMarkers, parkMarkers;
    protected RadiusMarkerClusterer lastMarkers;

    // Markers sorting
    protected Button bt_museum, bt_theatre, bt_memorial, bt_stadium, bt_park;

    // Route Building
    protected UpdateRoadTask updateRoadTask;
    protected MyLocationListener myLocationListener;
    protected Marker lastItem;
    protected Polyline lastPolyline;
    protected IconOverlay lastDrownItem;

    // Marker things
    protected ConstraintLayout mapMarker;
    protected ImageView map_markdesc_imageView;
    protected TextView map_markdesc_titleTextView, map_markdesc_brief_descriptionTextView, map_markdesc_distanceTextView;
    protected Button map_markdesc_show_moreBT, map_markdesc_build_routeBT;
    protected LinearLayout layoutBottomButtons;

    // Route info things
    protected ConstraintLayout mapRouteInfo;
    protected ImageView mapRouteImage, mapRouteClose, mapRouteWalkImage;
    protected TextView mapRouteLength, mapRouteTime, mapRouteTitle;
    protected ProgressBar mapRouteProgressBar;

    // Close Route info things
    protected ConstraintLayout closeRouteView;
    protected ImageView closeRouteImage, closeRouteCloseImage;
    protected Button closeRouteYes, closeRouteNo;
    protected volatile boolean isAlive = true;


    protected void initCloseRouteView() {
        closeRouteView = (ConstraintLayout) findViewById(R.id.map_close_route);
        closeRouteImage = (ImageView) findViewById(R.id.close_request_image);
        closeRouteCloseImage = (ImageView) findViewById(R.id.close_request_close);
        closeRouteYes = (Button) findViewById(R.id.close_request_yes);
        closeRouteNo = (Button) findViewById(R.id.close_request_no);

        View.OnClickListener closeRouteDialog = v -> closeRouteView.setVisibility(View.GONE);
        closeRouteNo.setOnClickListener(closeRouteDialog);
        closeRouteCloseImage.setOnClickListener(closeRouteDialog);

        closeRouteYes.setOnClickListener(v -> {
            isAlive = false;
            if (lastDrownItem != null) {
                map.getOverlays().remove(lastDrownItem);
                lastDrownItem = null;
            }
            routeBuilding = false;
            if (updateRoadTask != null)
                updateRoadTask.cancel(true);
            updateRoadTask = null;

            if (lastItem != null) {
                map.getOverlays().remove(lastItem);
            }
            if (lastPolyline != null) {
                map.getOverlays().remove(lastPolyline);
            }
            map.getOverlays().add(lastMarkers);

            mapRouteInfo.setVisibility(View.GONE);
            closeRouteDialog.onClick(v);

            layoutBottomButtons.setVisibility(View.VISIBLE);
        });
    }

    protected void initRouteInfoLayout() {
        mapRouteInfo = (ConstraintLayout) findViewById(R.id.map_route_info);
        mapRouteImage = (ImageView) findViewById(R.id.route_info_image);
        mapRouteClose = (ImageView) findViewById(R.id.route_info_close);
        mapRouteClose.setOnClickListener(v -> {
            closeRouteView.setVisibility(View.VISIBLE);
        });
        mapRouteWalkImage = (ImageView) findViewById(R.id.route_info_walk_image);
        mapRouteTitle = (TextView) findViewById(R.id.route_info_title);
        mapRouteTime = (TextView) findViewById(R.id.route_info_time);
        mapRouteLength = (TextView) findViewById(R.id.route_info_length);
        mapRouteProgressBar = (ProgressBar) findViewById(R.id.route_info_progress_bar);
    }


    protected void initMapMarker() {
        mapMarker = (ConstraintLayout) findViewById(R.id.map_marker);
        ImageView markdesc_closeIV = (ImageView) findViewById(R.id.map_markdesc_closeIV);
        markdesc_closeIV.setOnClickListener(v -> {
            mapMarker.setVisibility(View.GONE);
            map_markdesc_distanceTextView.setVisibility(View.GONE);
        });

    }

    protected void setUpMap() {
        Context context = this;

        IMapController mapController = map.getController();
        mapController.setZoom(12);

        GeoPoint startPoint = new GeoPoint(47.219196, 39.702261);
        mapController.setCenter(startPoint);

        map.setBuiltInZoomControls(false);
        map.setMultiTouchControls(true);
        map.setMinZoomLevel(6.);

        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), map);
        // Fixme: Set Custom item
        //Bitmap myCustomLocationOverlay = BitmapFactory.decodeResource(getResources(), R.drawable.my_location_overlay_small);
        //myLocationOverlay.setPersonIcon(myCustomLocationOverlay);
        myLocationOverlay.enableMyLocation();
        map.getOverlays().add(myLocationOverlay);

        RotationGestureOverlay mRotationGestureOverlay = new RotationGestureOverlay(context, map);
        mRotationGestureOverlay.setEnabled(true);
        map.setMultiTouchControls(true);
        map.getOverlays().add(mRotationGestureOverlay);
    }

    protected void initMarkerView() {
        // marker Info
        map_markdesc_imageView = (ImageView) findViewById(R.id.map_markdesc_image);
        map_markdesc_titleTextView = (TextView) findViewById(R.id.map_markdesc_titleTextView);
        map_markdesc_brief_descriptionTextView = (TextView) findViewById(R.id.map_markdesc_brief_descriptionTextView);
        map_markdesc_show_moreBT = (Button) findViewById(R.id.map_markdesc_show_moreBT);
        map_markdesc_build_routeBT = (Button) findViewById(R.id.map_markdesc_build_routeBT);
        map_markdesc_distanceTextView = (TextView) findViewById(R.id.map_markdesc_distanceTW);

    }

    protected void loadMarkers() {
        museumMarkers = new RadiusMarkerClusterer(this);
        theatreMarkers = new RadiusMarkerClusterer(this);
        memorialMarkers = new RadiusMarkerClusterer(this);
        stadiumMarkers = new RadiusMarkerClusterer(this);
        parkMarkers = new RadiusMarkerClusterer(this);
        // Создаем лист маркеров
        List<RadiusMarkerClusterer> overlayItems = new ArrayList<>();
        // Добавляем маркеры
        List<Place> data = CSVreader.getData(this);
        for (int i = 0; i < data.size(); i++) {
            // retrieve data
            Place place = data.get(i);
            int id = place.getId();
            double latitude = place.getLatitude();
            double longitude = place.getLongitude();
            GeoPoint markerPosition = new GeoPoint(latitude, longitude);
            // create Marker
            Marker marker = new Marker(map);
            marker.setTitle(String.valueOf(id));
            marker.setPosition(markerPosition);
            marker.setIcon(this.getDrawable(MarkerUtil.getMapMarkerByPlaceId(id)));
            marker.setOnMarkerClickListener((Marker mark, MapView map) -> {
                onOverlayTapUp(mark);
                mapMarker.setVisibility(View.VISIBLE);
                return false;
            });
            if (place.getType() == AttractionType.Museum) {
                museumMarkers.add(marker);
            } else if (place.getType() == AttractionType.Theatre) {
                theatreMarkers.add(marker);
            } else if (place.getType() == AttractionType.Memorial) {
                memorialMarkers.add(marker);
            } else if (place.getType() == AttractionType.Stadium) {
                stadiumMarkers.add(marker);
            } else if (place.getType() == AttractionType.Park) {
                parkMarkers.add(marker);
            }
            lastMarkers = museumMarkers;

        }
    }

    protected void onOverlayTapUp(Marker item) {

        int id = Integer.parseInt(item.getTitle());
        Place place = CSVreader.getPlaceById(id);
        int imageSmall = place.getImageSmall();
        String title = place.getTitle();
        String description = place.getDescription();

        Drawable imageSmallDrawable = this.getDrawable(imageSmall);


        map_markdesc_titleTextView.setText(title);
        map_markdesc_brief_descriptionTextView.setText(description);
        map_markdesc_imageView.setImageDrawable(imageSmallDrawable);

        map_markdesc_show_moreBT.setOnClickListener((View v) -> {
            Intent intent = new Intent(v.getContext(), wikiAttractionActivity.class);
            intent.putExtra("TAG", id);
            v.getContext().startActivity(intent);
        });
        map_markdesc_build_routeBT.setOnClickListener(v -> {
            isAlive = true;
            Location userLocation = getUserLocation(locationManager);
            Toast.makeText(MapInitActivity.this, "User location: " + String.valueOf(userLocation), Toast.LENGTH_SHORT).show();
            if (userLocation == null || myLocationOverlay == null) {
                Toast.makeText(MapInitActivity.this, "Погодь, еще не определил местоположение", Toast.LENGTH_SHORT).show();
                return;
            }

            layoutBottomButtons.setVisibility(View.GONE);
            mapMarker.setVisibility(View.GONE);

            List<Overlay> overlays = map.getOverlays();
            overlays.remove(lastMarkers);
            //overlays.add(myLocationOverlay);
            lastDrownItem = new IconOverlay(item.getPosition(), this.getDrawable(MarkerUtil.getMapMarkerByPlaceId(id)));
            overlays.add(lastDrownItem);

            lastItem = item;
            mapRouteImage.setImageDrawable(imageSmallDrawable);
            closeRouteImage.setImageDrawable(this.getDrawable(imageSmall));
            mapRouteTitle.setText(title);
            // Hide description
            mapRouteTime.setVisibility(View.GONE);
            mapRouteWalkImage.setVisibility(View.GONE);
            mapRouteLength.setVisibility(View.GONE);
            // Show progress bar
            mapRouteProgressBar.setVisibility(View.VISIBLE);
            // Show
            mapRouteInfo.setVisibility(View.VISIBLE);
            requestDrawRoute(item);
        });

        Location userLocation = getUserLocation(locationManager);
        if (userLocation != null) {
            Location itemLocation = new Location("");
            itemLocation.setLatitude(item.getPosition().getLatitude());
            itemLocation.setLongitude(item.getPosition().getLongitude());
            double distanceTo = userLocation.distanceTo(itemLocation);
            int porog = 0;
            String suffix;
            if (distanceTo > 1000) {
                porog = 100;
                suffix = "км";
                distanceTo = Math.floor(distanceTo / porog) * porog / 1000;
            } else {
                porog = 100;
                suffix = "м";
                distanceTo = Math.floor(distanceTo / porog) * porog;
            }
            map_markdesc_distanceTextView.setText(String.valueOf(distanceTo + suffix));
            map_markdesc_distanceTextView.setVisibility(View.VISIBLE);
        }
    }


    public void requestDrawRoute(@NonNull Marker item) {
        Log.d(TAG, "requestDrawRoute called");
        if (myLocationListener == null || lastItem == null) {
            Log.d(TAG, "Will not request new route because locationListener or lastItem is: null");
            return;
        }
        routeBuilding = true;
        Toast.makeText(MapInitActivity.this, "Погодь, ща построим", Toast.LENGTH_SHORT).show();
        if (updateRoadTask != null)
            updateRoadTask.cancel(true);
        postUserLocationAndCallUpdateRoadTask(item.getPosition());
    }

    protected void postUserLocationAndCallUpdateRoadTask(GeoPoint geoPoint) {
        Log.d(TAG, "postUserLoaction is called");
        Thread threadGettingUserLocation = new Thread(() -> {
            Location userLocation = null;
            Log.d(TAG, "Start getting user location");
            while (userLocation == null && isAlive) {
                try {
                    userLocation = getUserLocation(locationManager);
                    if (userLocation == null) {
                        Log.d(TAG, "User location is null. Will sleep 2000!");
                        Thread.sleep(2000);
                    }
                } catch (Exception ignored) {
                }
            }

            if (!isAlive) {
                Log.d(TAG, "Not alive. Return.");
                return;
            }

            Log.d(TAG, "Have user location " + userLocation.toString());

            Location finalUserLocation = userLocation;
            runOnUiThread(() -> {
                Log.d(TAG, "Have user location. Request road building!");
                RouteReceiver routeReceiver = MapInitActivity.this;
                if (updateRoadTask == null && routeBuilding) {
                    updateRoadTask = new UpdateRoadTask(finalUserLocation, geoPoint, routeReceiver);
                    Context context = MapInitActivity.this;
                    updateRoadTask.execute(context);
                }
            });
        });
        threadGettingUserLocation.start();
    }


    @Override
    public void onRouteReceived(@NonNull Road road) {

        if (!routeBuilding || lastDrownItem == null)
            return;
        Context context = this;

        double roadLength = road.mLength;

        Locale defaultLoacale = Locale.getDefault();
        mapRouteLength.setText(String.format(defaultLoacale, "%.3f км", roadLength));
        mapRouteTime.setText(String.format(defaultLoacale, "%.0f мин", roadLength * 12));

        if (mapRouteProgressBar.getVisibility() == View.VISIBLE) {
            mapRouteProgressBar.setVisibility(View.GONE);
            mapRouteWalkImage.setVisibility(View.VISIBLE);
            mapRouteLength.setVisibility(View.VISIBLE);
            mapRouteTime.setVisibility(View.VISIBLE);
        }

        List<Overlay> mapOverlays = map.getOverlays();
        Polyline roadPolyline = RoadManager.buildRoadOverlay(road);

        String routeDesc = road.getLengthDurationText(context, -1);
        roadPolyline.setTitle(context.getString(R.string.app_name) + " - " + routeDesc);
        roadPolyline.setInfoWindow(new BasicInfoWindow(org.osmdroid.bonuspack.R.layout.bonuspack_bubble, map));
        roadPolyline.setColor(getResources().getColor(R.color.colorGreen));
        roadPolyline.setRelatedObject(0);
        roadPolyline.setWidth(5);

        // TODO: ADD Later
        //roadPolyline.setOnClickListener(new RoadOnClickListener());

        if (lastPolyline != null) {
            map.getOverlays().remove(lastPolyline);
        }
        lastPolyline = roadPolyline;
        mapOverlays.add(roadPolyline);
        if (updateRoadTask != null) {
            updateRoadTask.cancel(true);
        }
        updateRoadTask = null;
    }

    @Override
    public void onRouteBadReceived() {
        if (updateRoadTask != null) {
            updateRoadTask.cancel(true);
        }
        updateRoadTask = null;
    }

}
