package com.swg_games_lab.nanicki.artguide;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.AsyncTask;

import com.swg_games_lab.nanicki.artguide.attraction_info.WikiActivity;
import com.swg_games_lab.nanicki.artguide.attraction_info.Wiki_Attraction_Activity;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;


public class MapActivity extends Activity implements LocationListener {
    MapView map = null;
    MyLocationNewOverlay myLocationOverlay;
    LocationManager locationManager;
    Criteria criteria;
    UpdateRoadTask updateRoadTask;
    Location locationNet;
    Location locationGPS;
    OverlayItem lastMarker;
    boolean routeWasDrown = false;
    Context context;
    boolean postExecuteComplited = false;
    View markerView;
    ImageView map_markdesc_imageView;
    TextView map_markdesc_titleTextView, map_markdesc_brief_descriptionTextView;
    Button map_markdesc_show_moreBT, map_markdesc_build_routeBT, map_wikiBT;
    private AlertDialog alertDialog;

    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
            boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);

            NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            NetworkInfo otherNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

            if(currentNetworkInfo.isConnected()){
                //TODO: Каким то образом вовзращать активити (ВООБЩЕ ХЗ КАК)
                //Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
                //setContentView(R.layout.activity_map);
            }else{
                //Toast.makeText(getApplicationContext(), "Not Connected", Toast.LENGTH_LONG).show();
                setContentView(R.layout.out_of_connection);
            }
        }
    };


    private void init() {
        //load/initialize the osmdroid configuration, this can be done
        context = getApplicationContext();
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
        //inflate and create the map
        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        map_wikiBT = (Button) findViewById(R.id.map_wikiBT);
        map_wikiBT.setOnClickListener(v -> startActivity(new Intent(this, WikiActivity.class)));

        // Получение текущих координат
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        criteria = new Criteria();
        locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        this.registerReceiver(mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));


    }

    private void setUpMap(Context context) {
        IMapController mapController = map.getController();
        mapController.setZoom(12);
        GeoPoint startPoint = null;// = new GeoPoint(locationGPS.getLatitude(), locationGPS.getAltitude());
        if (locationGPS != null) {
            startPoint = new GeoPoint(locationGPS.getLatitude(), locationGPS.getLongitude());
        } else if (locationNet != null) {
            startPoint = new GeoPoint(locationNet.getLatitude(), locationNet.getLongitude());
        } else {
            Toast.makeText(MapActivity.this, "Failed to load Current Location", Toast.LENGTH_SHORT).show();
        }
        mapController.setCenter(startPoint);

        map.setBuiltInZoomControls(false);
        map.setMultiTouchControls(true);
        map.setMinZoomLevel(6.);

        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), map);
        myLocationOverlay.enableMyLocation();
        map.getOverlays().add(myLocationOverlay);

        RotationGestureOverlay mRotationGestureOverlay = new RotationGestureOverlay(context, map);
        mRotationGestureOverlay.setEnabled(true);
        map.setMultiTouchControls(true);
        map.getOverlays().add(mRotationGestureOverlay);
    }

    private void addingMarkers() {


        // Создаем лист маркеров
        List<OverlayItem> overlayItems = new ArrayList<>();
        // Добавляем маркеры
        // TODO: Доделать
        // Аксайский скейт парк
        OverlayItem overlayItem = new OverlayItem("Парк", "Скейт Парк",
                new GeoPoint(47.271563, 39.856363));
        overlayItem.setMarker(this.getDrawable(R.drawable.aksay_park));
        overlayItems.add(overlayItem);
        // Дом Маргариты Черновой
        overlayItem = new OverlayItem("Музей", "Дом Маргариты Черновой",
                new GeoPoint(47.219196, 39.702261));
        overlayItem.setMarker(this.getDrawable(R.drawable.museum_small));
        overlayItems.add(overlayItem);
        // OnClickListener на маркер
        ItemizedIconOverlay<OverlayItem> anotherItemizedIconOverlay
                = new ItemizedIconOverlay<>(
                this, overlayItems, new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
            @Override
            public boolean onItemSingleTapUp(int index, OverlayItem item) {

                // marker Info
                markerView = getLayoutInflater().inflate(R.layout.custom_alert_dialog, null);
                map_markdesc_imageView = (ImageView) markerView.findViewById(R.id.map_markdesc_image);
                map_markdesc_titleTextView = (TextView) markerView.findViewById(R.id.map_markdesc_titleTextView);
                map_markdesc_brief_descriptionTextView = (TextView) markerView.findViewById(R.id.map_markdesc_brief_descriptionTextView);
                map_markdesc_show_moreBT = (Button) markerView.findViewById(R.id.map_markdesc_show_moreBT);
                map_markdesc_build_routeBT = (Button) markerView.findViewById(R.id.map_markdesc_build_routeBT);


                map_markdesc_titleTextView.setText(item.getTitle());
                map_markdesc_brief_descriptionTextView.setText(item.getSnippet());
                map_markdesc_imageView.setImageDrawable(item.getDrawable());

                map_markdesc_show_moreBT.setOnClickListener((View v) -> {
                    Intent intent = new Intent(v.getContext(), Wiki_Attraction_Activity.class);
                    intent.putExtra("TAG", map_markdesc_titleTextView.getText());
                    v.getContext().startActivity(intent);
                });

                map_markdesc_build_routeBT.setOnClickListener(v -> {
                        if (routeWasDrown) {
                            map.getOverlays().remove(map.getOverlays().size() - 1);
                            postExecuteComplited = false;
                            map.invalidate();
                            Toast.makeText(MapActivity.this, "Старый маршрут был удален", Toast.LENGTH_SHORT).show();
                        }
                        routeWasDrown = true;
                        if (myLocationOverlay == null) {
                            Toast.makeText(MapActivity.this, "Погодь, еще не определил местоположение", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(MapActivity.this, "Погодь, ща построим", Toast.LENGTH_SHORT).show();
                        lastMarker = item;
                        updateRoadTask = new UpdateRoadTask();
                        List<GeoPoint> waypoints = updateRoadTask.makeRoad(null, item);
                        updateRoadTask.execute(waypoints);
                        alertDialog.cancel();
                    });
                alertDialog = new AlertDialog.Builder(MapActivity.this)
                        .setView(markerView)
                        .create();
                alertDialog.show();
                return false;
            }

            @Override
            public boolean onItemLongPress(int index, OverlayItem item) {
                return false;
            }
        });
        // Маркеры настроены можно добавить
        map.getOverlays().add(anotherItemizedIconOverlay);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Инициализация layoutов
        init();
        // Настройка карты
        setUpMap(context);
        // Добавление маркеров
        addingMarkers();
    }


    private class UpdateRoadTask extends AsyncTask<Object, Void, Road[]> {

        protected Road[] doInBackground(Object... params) {
            ArrayList<GeoPoint> waypoints = (ArrayList<GeoPoint>) params[0];
            RoadManager roadManager = new OSRMRoadManager(MapActivity.this);
            return roadManager.getRoads(waypoints);
        }

        @Override
        protected void onPostExecute(Road[] roads) {
            //Toast.makeText(map.getContext(), "Route was received successfully", Toast.LENGTH_SHORT).show();
            if (roads == null)
                return;
            if (roads[0].mStatus == Road.STATUS_TECHNICAL_ISSUE)
                Toast.makeText(map.getContext(), "Technical issue when getting the route",
                        Toast.LENGTH_SHORT).show();
            else if (roads[0].mStatus > Road.STATUS_TECHNICAL_ISSUE) //functional issues
                Toast.makeText(map.getContext(), "No possible route here",
                        Toast.LENGTH_SHORT).show();


            List<Overlay> mapOverlays = map.getOverlays();
            Polyline roadPolyline = RoadManager.buildRoadOverlay(roads[0]);

            String routeDesc = roads[0].getLengthDurationText(MapActivity.this, -1);
            roadPolyline.setTitle(getString(R.string.app_name) + " - " + routeDesc);
            roadPolyline.setInfoWindow(new BasicInfoWindow(org.osmdroid.bonuspack.R.layout.bonuspack_bubble, map));
            roadPolyline.setRelatedObject(0);
            roadPolyline.setWidth(5);
            //roadPolyline.setOnClickListener(new RoadOnClickListener());

            if (postExecuteComplited) {
                mapOverlays.set(mapOverlays.size() - 1, roadPolyline);
            } else mapOverlays.add(roadPolyline);
            postExecuteComplited = true;
        }

        public List<GeoPoint> makeRoad(Location location, OverlayItem item) {
            List<GeoPoint> waypoints = new ArrayList<>();
            if (location == null) {
                // Еще один способ получить текущие координаты
                GeoPoint current_location = null;

                if (locationGPS != null) {
                    current_location = new GeoPoint(locationGPS.getLatitude(), locationGPS.getLongitude());
                } else if (locationNet != null) {
                    current_location = new GeoPoint(locationNet.getLatitude(), locationNet.getLongitude());
                } else {
                    Toast.makeText(MapActivity.this, "Failed to load Current Location", Toast.LENGTH_SHORT).show();
                }
                waypoints.add(current_location);
            } else
                waypoints.add(new GeoPoint(location.getLatitude(), location.getLongitude()));
            IGeoPoint markerpoint = item.getPoint();
            GeoPoint marker_location = new GeoPoint(markerpoint.getLatitude(), markerpoint.getLongitude());
            waypoints.add(marker_location);
            return waypoints;
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        // если местоположение не загружено, то выходим
        if (myLocationOverlay == null) {
            Toast.makeText(MapActivity.this, "Определяем местоположение", Toast.LENGTH_SHORT).show();
            return;
        }
        // если маркер не выбран, то выходим
        if (lastMarker == null)
            return;
        // если запрос не завершен, то выходим
        if (!postExecuteComplited)
            return;
        // Перерисовываем маршрут
        updateRoadTask = new UpdateRoadTask();// передаем текщие координаты (location)
        List<GeoPoint> waypoints = updateRoadTask.makeRoad(location, lastMarker);
        updateRoadTask.execute(waypoints);
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


    public void onResume() {
        super.onResume();
        map.onResume();
    }

    public void onPause() {
        super.onPause();
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        map.destroyDrawingCache();
    }
}