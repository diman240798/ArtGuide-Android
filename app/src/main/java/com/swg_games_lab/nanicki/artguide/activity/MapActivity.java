package com.swg_games_lab.nanicki.artguide.activity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.swg_games_lab.nanicki.artguide.R;
import com.swg_games_lab.nanicki.artguide.activity.attraction_info.Wiki_Attraction_Activity;
import com.swg_games_lab.nanicki.artguide.background.UpdateRoadTask;
import com.swg_games_lab.nanicki.artguide.listener.MyLocationListener;
import com.swg_games_lab.nanicki.artguide.listener.RouteReceiver;
import com.swg_games_lab.nanicki.artguide.util.LocationUtil;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
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

import static com.swg_games_lab.nanicki.artguide.util.LocationUtil.getUserLocation;


public class MapActivity extends AppCompatActivity implements RouteReceiver, View.OnClickListener {
    // Views
    public MapView map;
    private MyLocationNewOverlay myLocationOverlay;
    private LocationManager locationManager;
    private Button bt_museum, bt_theatre, bt_memorial, bt_stadium;
    private ImageView markdesc_closeIV;
    // Fields
    private UpdateRoadTask updateRoadTask;
    private MyLocationListener myLocationListener;
    private boolean routeWasDrown = false;
    // Marker things
    private ImageView map_markdesc_imageView;
    private TextView map_markdesc_titleTextView, map_markdesc_brief_descriptionTextView;
    private Button map_markdesc_show_moreBT, map_markdesc_build_routeBT;
    private ConstraintLayout map_marker;

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

        bt_museum = (Button) findViewById(R.id.map_bt_museum);
        bt_theatre = (Button) findViewById(R.id.map_bt_theatre);
        bt_memorial = (Button) findViewById(R.id.map_bt_memorial);
        bt_stadium = (Button) findViewById(R.id.map_bt_stadium);

        bt_museum.setOnClickListener(this);
        bt_theatre.setOnClickListener(this);
        bt_memorial.setOnClickListener(this);
        bt_stadium.setOnClickListener(this);

        map_marker = (ConstraintLayout) findViewById(R.id.map_marker);
        markdesc_closeIV = (ImageView) findViewById(R.id.map_markdesc_closeIV);
        markdesc_closeIV.setOnClickListener(v -> map_marker.setVisibility(View.GONE));

        // Получение текущих координат
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        myLocationListener = new MyLocationListener(this, locationManager);
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
                map_marker.setVisibility(View.VISIBLE);
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
        map_markdesc_build_routeBT.setOnClickListener(v -> {
//            if (routeWasDrown) {
//                map.getOverlays().remove(map.getOverlays().size() - 1);
//                map.invalidate();
//                Toast.makeText(MapActivity.this, "Старый маршрут был удален", Toast.LENGTH_SHORT).show();
//            }
//            routeWasDrown = true;
            if (myLocationOverlay == null) {
                Toast.makeText(MapActivity.this, "Погодь, еще не определил местоположение", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(MapActivity.this, "Погодь, ща построим", Toast.LENGTH_SHORT).show();
            if (updateRoadTask != null)
                updateRoadTask.cancel(true);
            updateRoadTask = new UpdateRoadTask(getUserLocation(locationManager), item, MapActivity.this);
            updateRoadTask.execute(MapActivity.this);
        });
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

    @Override
    public void onRouteReceived(@NonNull Road[] roads) {
        Context context = this;
        Road firstRoad = roads[0];
        if (firstRoad.mStatus == Road.STATUS_TECHNICAL_ISSUE) {
            Toast.makeText(context, "Technical issue when getting the route", Toast.LENGTH_SHORT).show();
            return;
        } else if (firstRoad.mStatus > Road.STATUS_TECHNICAL_ISSUE) {
            Toast.makeText(context, "No possible route here", Toast.LENGTH_SHORT).show();
            return;
        }


        List<Overlay> mapOverlays = map.getOverlays();
        Polyline roadPolyline = RoadManager.buildRoadOverlay(firstRoad);

        String routeDesc = firstRoad.getLengthDurationText(context, -1);
        roadPolyline.setTitle(context.getString(R.string.app_name) + " - " + routeDesc);
        roadPolyline.setInfoWindow(new BasicInfoWindow(org.osmdroid.bonuspack.R.layout.bonuspack_bubble, map));
        roadPolyline.setColor(getResources().getColor(R.color.colorGreen));
        roadPolyline.setRelatedObject(0);
        roadPolyline.setWidth(5);
        // TODO: ADD Later
        //roadPolyline.setOnClickListener(new RoadOnClickListener());
        mapOverlays.add(roadPolyline);
    }

    private void setDefaultImages() {
        bt_theatre.setBackgroundResource(R.drawable.item_theatre);
        bt_museum.setBackgroundResource(R.drawable.item_museam);
        bt_memorial.setBackgroundResource(R.drawable.item_memorial);
        bt_stadium.setBackgroundResource(R.drawable.item_stadium);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        setDefaultImages();

        if (id == R.id.map_bt_museum) {
            bt_museum.setBackgroundResource(R.drawable.item_museam_chosen);
//            adapter.sortList(places, "Музей");
        } else if (id == R.id.map_bt_stadium) {
            bt_stadium.setBackgroundResource(R.drawable.item_stadium_chosen);
//            adapter.sortList(places, "Стадион");
        } else if (id == R.id.map_bt_memorial) {
            bt_memorial.setBackgroundResource(R.drawable.item_memorial_chosen);
//            adapter.sortList(places, "Памятник");
        } else if (id == R.id.map_bt_theatre) {
            bt_theatre.setBackgroundResource(R.drawable.item_theatre_chosen);
//            adapter.sortList(places, "Театр");
        }
    }
}
