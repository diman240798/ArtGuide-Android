package com.swg_games_lab.nanicki.artguide.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.swg_games_lab.nanicki.artguide.MapBottomButtons;
import com.swg_games_lab.nanicki.artguide.R;
import com.swg_games_lab.nanicki.artguide.csv.CSVreader;
import com.swg_games_lab.nanicki.artguide.listener.MyLocationListener;
import com.swg_games_lab.nanicki.artguide.listener.RouteReceiver;
import com.swg_games_lab.nanicki.artguide.model.Place;
import com.swg_games_lab.nanicki.artguide.util.ConnectionUtil;
import com.swg_games_lab.nanicki.artguide.util.MarkerUtil;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.IconOverlay;

import java.lang.ref.WeakReference;


public class MapActivity extends MapBottomButtons implements RouteReceiver {

    // Fields
    private static final String TAG = "MapActivity";
    private boolean NO_CONNECTION_MODE;


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


    protected void init() {
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


    public void onLocationChanged() {
        Log.d(TAG, "onLocationChanged called");
        if (lastDrownItem == null) {
            Log.d(TAG, "Will not rebuild route because lastDrownItem is null");
            return;
        }

        routeBuilding = true;
        Toast.makeText(MapActivity.this, "Перестраиваю", Toast.LENGTH_SHORT).show();
        if (updateRoadTask != null)
            updateRoadTask.cancel(true);
        updateRoadTask = null;
        postUserLocationAndCallUpdateRoadTask((GeoPoint) lastDrownItem.getPosition());
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onStart() {
        super.onStart();
        if (NO_CONNECTION_MODE) {
            return;
        }
        isAlive = true;
        map.onResume();
        myLocationListener.mapActivity = new WeakReference<>(this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, myLocationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, myLocationListener);
        if (lastItem != null)
            requestDrawRoute(lastItem);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (NO_CONNECTION_MODE)
            return;
        isAlive = false;
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

}
