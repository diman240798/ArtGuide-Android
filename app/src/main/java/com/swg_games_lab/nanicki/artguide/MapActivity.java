package com.swg_games_lab.nanicki.artguide;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.swg_games_lab.nanicki.artguide.attraction_info.WikiActivity;
import com.swg_games_lab.nanicki.artguide.attraction_info.Wiki_Attraction_Activity;
import com.swg_games_lab.nanicki.artguide.background.UpdateRoadTask;
import com.swg_games_lab.nanicki.artguide.listener.MyLocationListener;
import com.swg_games_lab.nanicki.artguide.util.LocationUtil;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

import static com.swg_games_lab.nanicki.artguide.util.LocationUtil.getUserLocation;


public class MapActivity extends Activity {
    // Views
    public MapView map;
    private MyLocationNewOverlay myLocationOverlay;
    private LocationManager locationManager;
    // Fields
    private UpdateRoadTask updateRoadTask;
    private MyLocationListener myLocationListener;
    private boolean routeWasDrown = false;
    // Marker things
    private ImageView map_markdesc_imageView;
    private TextView map_markdesc_titleTextView, map_markdesc_brief_descriptionTextView;
    private Button map_markdesc_show_moreBT, map_markdesc_build_routeBT;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Инициализация layoutов
        init();
        // Настройка карты
        setUpMap();
        // Setting up dialog (appears on tap up)
        initMarkerView();
        // Добавление маркеров
        addingMarkers();
    }

    private void init() {
        //load/initialize the osmdroid configuration, this can be done
        Context context = this;
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
        //inflate and create the map
        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        Button map_wikiBT = (Button) findViewById(R.id.map_wikiBT);
        map_wikiBT.setOnClickListener(v -> startActivity(new Intent(this, WikiActivity.class)));

        // Получение текущих координат
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        myLocationListener = new MyLocationListener();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, myLocationListener);
        // TODO: Add Later
//        this.registerReceiver(mConnReceiver,
//                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

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
        // TODO: Set Custom Icon
        //myLocationOverlay.setPersonIcon();
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
        ItemizedIconOverlay<OverlayItem> anotherItemizedIconOverlay = new ItemizedIconOverlay<>(
                this, overlayItems, new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
            @Override
            public boolean onItemSingleTapUp(int index, OverlayItem item) {
                onOverlayTapUp(item);
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

    private void onOverlayTapUp(OverlayItem item) {

        map_markdesc_titleTextView.setText(item.getTitle());
        map_markdesc_brief_descriptionTextView.setText(item.getSnippet());
        map_markdesc_imageView.setImageDrawable(item.getDrawable());

        map_markdesc_show_moreBT.setOnClickListener((View v) -> {
            Intent intent = new Intent(v.getContext(), Wiki_Attraction_Activity.class);
            intent.putExtra("TAG", map_markdesc_titleTextView.getText());
            v.getContext().startActivity(intent);
        });
        // FIXME CRUTCH ПИЗДЕЦ
//        map_markdesc_build_routeBT.setOnClickListener(v -> {
//            if (routeWasDrown) {
//                map.getOverlays().remove(map.getOverlays().size() - 1);
//                map.invalidate();
//                Toast.makeText(MapActivity.this, "Старый маршрут был удален", Toast.LENGTH_SHORT).show();
//            }
//            routeWasDrown = true;
//            if (myLocationOverlay == null) {
//                Toast.makeText(MapActivity.this, "Погодь, еще не определил местоположение", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            Toast.makeText(MapActivity.this, "Погодь, ща построим", Toast.LENGTH_SHORT).show();
//            if (updateRoadTask != null)
//                updateRoadTask.cancel(true);
//            updateRoadTask = new UpdateRoadTask(getUserLocation(locationManager), item, MapActivity.this);
//            updateRoadTask.execute();
//            alertDialog.cancel();
//        });
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
        locationManager.removeUpdates(myLocationListener);
    }
}
