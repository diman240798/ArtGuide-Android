package com.swg_games_lab.nanicki.artguide.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.swg_games_lab.nanicki.artguide.util.ConnectionUtil;
import com.swg_games_lab.nanicki.artguide.util.LocationUtil;
import com.swg_games_lab.nanicki.artguide.util.MarkerUtil;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.swg_games_lab.nanicki.artguide.util.LocationUtil.getUserLocation;


public class MapActivity extends AppCompatActivity implements RouteReceiver, View.OnClickListener {

    // Fields
    private static final String TAG = "MapActivity";
    private boolean NO_CONNECTION_MODE;

    // Views
    public MapView map;
    private MyLocationNewOverlay myLocationOverlay;
    private LocationManager locationManager;

    // Markers
    private RadiusMarkerClusterer museumMarkers, theatreMarkers, memorialMarkers, stadiumMarkers, parkMarkers;
    private RadiusMarkerClusterer lastMarkers;

    // Markers sorting
    private Button bt_museum, bt_theatre, bt_memorial, bt_stadium, bt_park;

    // Route Building
    private UpdateRoadTask updateRoadTask;
    private MyLocationListener myLocationListener;
    private Marker lastItem;
    private Polyline lastPolyline;
    private IconOverlay lastDrownItem;

    // Marker things
    private ConstraintLayout mapMarker;
    private ImageView map_markdesc_imageView;
    private TextView map_markdesc_titleTextView, map_markdesc_brief_descriptionTextView, map_markdesc_distanceTextView;
    private Button map_markdesc_show_moreBT, map_markdesc_build_routeBT;
    private LinearLayout layoutBottomButtons;

    // Route info things
    private ConstraintLayout mapRouteInfo;
    private ImageView mapRouteImage, mapRouteClose, mapRouteWalkImage;
    private TextView mapRouteLength, mapRouteTime, mapRouteTitle;
    private ProgressBar mapRouteProgressBar;

    // Close Route info things
    private ConstraintLayout closeRouteView;
    private ImageView closeRouteImage, closeRouteCloseImage;
    private Button closeRouteYes, closeRouteNo;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;

        boolean connected = ConnectionUtil.isConnected(locationManager, this);
        if (!connected) {
            setContentView(R.layout.out_of_connection);
            ImageView imageView = (ImageView) findViewById(R.id.out_of_connection_show_dialog);
            imageView.setOnClickListener(v -> ConnectionUtil.buildAlertMessageNoConncetion(this));
            NO_CONNECTION_MODE = true;
            return;
        }

        setContentView(R.layout.activity_map);
        // Инициализация layoutов
        init();
        // Place Description layout
        initMapMarker();
        // Bottom srting buttons
        initBottomSortingButtons();
        // Route Info
        initRouteInfoLayout();
        // Настройка карты
        setUpMap();
        // Setting up dialog (appears on tap up)
        initMarkerView();
        // Setting up close route Dialog
        initCloseRouteView();
        // Loading markers
        loadMarkers();

        Bundle extras = getIntent().getExtras();
        if (extras != null) { // пришел id
            layoutBottomButtons.setVisibility(View.GONE);

            int id = extras.getInt("TAG");
            Place place = CSVreader.getPlaceById(id);
            String title = place.getTitle();
            int imageSmall = place.getImageSmall();
            double latitude = place.getLatitude();
            double longitude = place.getLongitude();


            lastDrownItem = new IconOverlay(new GeoPoint(latitude, longitude), this.getDrawable(MarkerUtil.getMapMarkerByPlaceId(id)));
            map.getOverlays().add(lastDrownItem);


            mapRouteImage.setImageDrawable(this.getDrawable(imageSmall));
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


            postUserLocationAndCallUpdateRoadTask(new GeoPoint(latitude, longitude));


        } else
            // Маркеры настроены можно добавить
            map.getOverlays().add(lastMarkers);
    }

    private void postUserLocationAndCallUpdateRoadTask(GeoPoint geoPoint) {
        new Thread(() -> {
            Location userLocation = null;
            int times = 10;
            while (userLocation == null && times > 0) {
                try {
                    userLocation = getUserLocation(locationManager);
                    times -= 1;
                    Thread.sleep(1000);
                } catch (Exception ignored) {}
            }
            Location finalUserLocation = userLocation;
            assert finalUserLocation != null;
            runOnUiThread(() -> {
                MapActivity mapActivity = MapActivity.this;
                updateRoadTask = new UpdateRoadTask(finalUserLocation, geoPoint, mapActivity);
                updateRoadTask.execute(mapActivity);
            });
        }).start();
    }

    private void initCloseRouteView() {
        closeRouteView = (ConstraintLayout) findViewById(R.id.map_close_route);
        closeRouteImage = (ImageView) findViewById(R.id.close_request_image);
        closeRouteCloseImage = (ImageView) findViewById(R.id.close_request_close);
        closeRouteYes = (Button) findViewById(R.id.close_request_yes);
        closeRouteNo = (Button) findViewById(R.id.close_request_no);

        View.OnClickListener closeRouteDialog = v -> closeRouteView.setVisibility(View.GONE);
        closeRouteNo.setOnClickListener(closeRouteDialog);
        closeRouteCloseImage.setOnClickListener(closeRouteDialog);

        closeRouteYes.setOnClickListener(v -> {
            if (lastDrownItem != null) {
                map.getOverlays().remove(lastDrownItem);
                lastDrownItem = null;
            }
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

    private void initRouteInfoLayout() {
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

    private void init() {
        //load/initialize the osmdroid configuration, this can be done
        Context context = this;
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
        //inflate and create the map
        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        // Получение текущих координат
        myLocationListener = new MyLocationListener(this);

        // TODO: Add Later
//        this.registerReceiver(mConnReceiver,
//                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

    }

    private void initBottomSortingButtons() {

        layoutBottomButtons = (LinearLayout) findViewById(R.id.bottom_linear_with_buttons);

        bt_museum = (Button) findViewById(R.id.map_bt_museum);
        bt_theatre = (Button) findViewById(R.id.map_bt_theatre);
        bt_memorial = (Button) findViewById(R.id.map_bt_memorial);
        bt_stadium = (Button) findViewById(R.id.map_bt_stadium);
        bt_park = (Button) findViewById(R.id.map_bt_park);

        bt_museum.setOnClickListener(this);
        bt_theatre.setOnClickListener(this);
        bt_memorial.setOnClickListener(this);
        bt_stadium.setOnClickListener(this);
        bt_park.setOnClickListener(this);

    }

    private void initMapMarker() {
        mapMarker = (ConstraintLayout) findViewById(R.id.map_marker);
        ImageView markdesc_closeIV = (ImageView) findViewById(R.id.map_markdesc_closeIV);
        markdesc_closeIV.setOnClickListener(v -> {
            mapMarker.setVisibility(View.GONE);
            map_markdesc_distanceTextView.setVisibility(View.GONE);
        });

    }

    private void setUpMap() {
        Context context = this;

        IMapController mapController = map.getController();
        mapController.setZoom(12);

        Location userLocation = getUserLocation(locationManager);
        GeoPoint startPoint = LocationUtil
                .getGeoPointByLocationOrDefault
                        (userLocation, new GeoPoint(47.219196, 39.702261));

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

    private void initMarkerView() {
        // marker Info
        map_markdesc_imageView = (ImageView) findViewById(R.id.map_markdesc_image);
        map_markdesc_titleTextView = (TextView) findViewById(R.id.map_markdesc_titleTextView);
        map_markdesc_brief_descriptionTextView = (TextView) findViewById(R.id.map_markdesc_brief_descriptionTextView);
        map_markdesc_show_moreBT = (Button) findViewById(R.id.map_markdesc_show_moreBT);
        map_markdesc_build_routeBT = (Button) findViewById(R.id.map_markdesc_build_routeBT);
        map_markdesc_distanceTextView = (TextView) findViewById(R.id.map_markdesc_distanceTW);

    }

    private void loadMarkers() {
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

    private void onOverlayTapUp(Marker item) {

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
            Location userLocation = getUserLocation(locationManager);
            Toast.makeText(MapActivity.this, "User location: " + String.valueOf(userLocation), Toast.LENGTH_SHORT).show();
            if (userLocation == null || myLocationOverlay == null) {
                Toast.makeText(MapActivity.this, "Погодь, еще не определил местоположение", Toast.LENGTH_SHORT).show();
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

        Toast.makeText(MapActivity.this, "Погодь, ща построим", Toast.LENGTH_SHORT).show();
        if (updateRoadTask != null)
            updateRoadTask.cancel(true);
        postUserLocationAndCallUpdateRoadTask(item.getPosition());
    }


    public void onLocationChanged() {
        Log.d(TAG, "onLocationChanged called");
        if (lastDrownItem == null) {
            Log.d(TAG, "Will not rebuild route because lastDrownItem is null");
            return;
        }

        Toast.makeText(MapActivity.this, "Перестраиваю", Toast.LENGTH_SHORT).show();
        if (updateRoadTask != null)
            updateRoadTask.cancel(true);
        postUserLocationAndCallUpdateRoadTask((GeoPoint) lastDrownItem.getPosition());
    }

    @Override
    public void onRouteReceived(@NonNull Road road) {
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
    }


    public void onResume() {
        super.onResume();
        if (NO_CONNECTION_MODE)
            return;
        map.onResume();
        myLocationListener.mapActivity = new WeakReference<>(this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, myLocationListener);
        if (lastItem != null)
            requestDrawRoute(lastItem);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (NO_CONNECTION_MODE)
            return;
        locationManager.removeUpdates(myLocationListener);
        myLocationListener.mapActivity = null;
        if (updateRoadTask != null)
            updateRoadTask.cancel(true);
        updateRoadTask = null;

        // FIXME: Sould this method call exist???
        //map.destroyDrawingCache();
    }

    public void onPause() {
        super.onPause();
        if (NO_CONNECTION_MODE)
            return;
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.map_bt_museum) { // Museum
            if (lastMarkers == museumMarkers)
                return;
            map.getOverlays().remove(lastMarkers);
            map.getOverlays().add(museumMarkers);
            lastMarkers = museumMarkers;

            setDefaultImages();
            bt_museum.setBackgroundResource(R.drawable.item_museum_chosen);
        } else if (id == R.id.map_bt_stadium) { // Stadium
            if (lastMarkers == stadiumMarkers)
                return;
            map.getOverlays().add(stadiumMarkers);
            map.getOverlays().remove(lastMarkers);
            lastMarkers = stadiumMarkers;

            setDefaultImages();
            bt_stadium.setBackgroundResource(R.drawable.item_stadium_chosen);
        } else if (id == R.id.map_bt_memorial) { // Memorial
            if (lastMarkers == memorialMarkers)
                return;
            map.getOverlays().add(memorialMarkers);
            map.getOverlays().remove(lastMarkers);
            lastMarkers = memorialMarkers;

            setDefaultImages();
            bt_memorial.setBackgroundResource(R.drawable.item_memorial_chosen);
        } else if (id == R.id.map_bt_theatre) { // Theatre
            if (lastMarkers == theatreMarkers)
                return;
            map.getOverlays().remove(lastMarkers);
            map.getOverlays().add(theatreMarkers);
            lastMarkers = theatreMarkers;

            setDefaultImages();
            bt_theatre.setBackgroundResource(R.drawable.item_theatre_chosen);
        } else if (id == R.id.map_bt_park) { // Park
            if (lastMarkers == parkMarkers)
                return;
            map.getOverlays().remove(lastMarkers);
            map.getOverlays().add(parkMarkers);
            lastMarkers = parkMarkers;

            setDefaultImages();
            bt_park.setBackgroundResource(R.drawable.item_park_chosen);
        }
    }

    private void setDefaultImages() {
        bt_theatre.setBackgroundResource(R.drawable.item_theatre);
        bt_museum.setBackgroundResource(R.drawable.item_museum);
        bt_memorial.setBackgroundResource(R.drawable.item_memorial);
        bt_stadium.setBackgroundResource(R.drawable.item_stadium);
        bt_park.setBackgroundResource(R.drawable.item_park);
    }

}
