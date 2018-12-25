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
import com.swg_games_lab.nanicki.artguide.background.UpdateRoadTask;

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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


public class MapActivity extends Activity implements LocationListener {
    public MapView map ;
    MyLocationNewOverlay myLocationOverlay;
    LocationManager locationManager;
    Criteria criteria;
    UpdateRoadTask updateRoadTask;
    Location locationNet;
    Location locationGPS;
    OverlayItem lastMarker;
    boolean routeWasDrown = false;
    boolean postExecuteComplited = false;
    View markerView;
    Button map_wikiBT;
    private AlertDialog alertDialog;

    private void init() {
        //load/initialize the osmdroid configuration, this can be done
        Context context = this;
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
        //inflate and create the map
        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        map_wikiBT = (Button) findViewById(R.id.map_wikiBT);
        map_wikiBT.setOnClickListener(v -> startActivity(new Intent(this, WikiActivity.class)));

        // Получение текущих координат
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        criteria = new Criteria();
        locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

//        this.registerReceiver(mConnReceiver,
//                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

    }

    private void setUpMap() {
        Context context = this;
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
                ImageView map_markdesc_imageView = (ImageView) markerView.findViewById(R.id.map_markdesc_image);
                TextView map_markdesc_titleTextView = (TextView) markerView.findViewById(R.id.map_markdesc_titleTextView);
                TextView map_markdesc_brief_descriptionTextView = (TextView) markerView.findViewById(R.id.map_markdesc_brief_descriptionTextView);
                Button map_markdesc_show_moreBT = (Button) markerView.findViewById(R.id.map_markdesc_show_moreBT);
                Button map_markdesc_build_routeBT = (Button) markerView.findViewById(R.id.map_markdesc_build_routeBT);


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
        setUpMap();
        // Добавление маркеров
        addingMarkers();
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
        updateRoadTask.execute();
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
