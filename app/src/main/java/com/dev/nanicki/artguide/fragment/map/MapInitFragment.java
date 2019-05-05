package com.dev.nanicki.artguide.fragment.map;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.nanicki.artguide.R;
import com.dev.nanicki.artguide.background.UpdateRoadTask;
import com.dev.nanicki.artguide.csv.CSVreader;
import com.dev.nanicki.artguide.enums.AttractionType;
import com.dev.nanicki.artguide.listener.MyLocationListener;
import com.dev.nanicki.artguide.listener.RouteReceiver;
import com.dev.nanicki.artguide.model.Place;
import com.dev.nanicki.artguide.ui.CurrentMarkers;
import com.dev.nanicki.artguide.util.MarkerUtil;

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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.dev.nanicki.artguide.util.LocationUtil.getUserLocation;

public class MapInitFragment extends Fragment implements RouteReceiver {

    protected static final String TAG = "MapInitFragment";
    protected volatile boolean routeBuilding = true;


    // Views
    protected ConstraintLayout mapMainParent;
    protected MapView map;
    protected MyLocationNewOverlay myLocationOverlay;
    protected LocationManager locationManager;

    // Markers
    protected List<CurrentMarkers> markersList;
    protected CurrentMarkers lastMarkers;

    // Markers sorting
    protected List<Button> bottomButtons;
    ;

    // Route Building
    protected UpdateRoadTask updateRoadTask;
    protected MyLocationListener myLocationListener;
    protected Marker lastItem;
    protected Polyline lastPolyline;
    protected IconOverlay lastDrownItem;
    protected int lastId = -1;

    // Marker things
    protected ConstraintLayout mapMarker;
    protected ImageView mapMarkdescImageView, markdesc_closeIV;
    protected TextView mapMarkdescTitleTextView, mapMarkdescBriefDescriptionTextView, mapMarkdescDistanceTextView;
    protected Button mapMarkdescShowMoreBT, mapMarkdescBuildRouteBT;
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
    protected volatile boolean isAlive = false;

    // Map Place DescriptionView
    protected ConstraintLayout mapPlaceDescParent;
    protected ImageView mapPlaceDescImage, mapClosePlaceDesc;
    protected TextView mapPlaceDescDesc, mapPlaceDescTitle;


    protected void initCloseRouteView(View view) {
        closeRouteView = (ConstraintLayout) view.findViewById(R.id.map_close_route);
        closeRouteImage = (ImageView) view.findViewById(R.id.close_request_image);
        closeRouteCloseImage = (ImageView) view.findViewById(R.id.close_request_close);
        closeRouteYes = (Button) view.findViewById(R.id.close_request_yes);
        closeRouteNo = (Button) view.findViewById(R.id.close_request_no);

        View.OnClickListener closeRouteDialog = v -> closeRouteView.setVisibility(View.GONE);
        closeRouteNo.setOnClickListener(closeRouteDialog);
        closeRouteCloseImage.setOnClickListener(closeRouteDialog);

        closeRouteYes.setOnClickListener(v -> {
            isAlive = false;
            if (lastDrownItem != null) {
                map.getOverlays().remove(lastDrownItem);
                lastDrownItem = null;
                lastId = -1;
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
            map.getOverlays().add(lastMarkers.getMarkers());

            mapRouteInfo.setVisibility(View.GONE);
            closeRouteDialog.onClick(v);

            layoutBottomButtons.setVisibility(View.VISIBLE);
        });
    }

    protected void initRouteInfoLayout(View view) {
        mapMainParent = (ConstraintLayout) view.findViewById(R.id.map_main_parent);
        mapRouteInfo = (ConstraintLayout) view.findViewById(R.id.map_route_info);
        mapRouteImage = (ImageView) view.findViewById(R.id.route_info_image);
        mapRouteClose = (ImageView) view.findViewById(R.id.route_info_close);
        mapRouteClose.setOnClickListener(v -> {
            closeRouteView.setVisibility(View.VISIBLE);
        });
        mapRouteWalkImage = (ImageView) view.findViewById(R.id.route_info_walk_image);
        mapRouteTitle = (TextView) view.findViewById(R.id.route_info_title);
        mapRouteTime = (TextView) view.findViewById(R.id.route_info_time);
        mapRouteLength = (TextView) view.findViewById(R.id.route_info_length);
        mapRouteProgressBar = (ProgressBar) view.findViewById(R.id.route_info_progress_bar);
    }


    protected void initMapMarker(View view) {
        mapMarker = (ConstraintLayout) view.findViewById(R.id.map_marker);
        markdesc_closeIV = (ImageView) view.findViewById(R.id.map_markdesc_closeIV);
        markdesc_closeIV.setOnClickListener(v -> {
            mapMarker.setVisibility(View.GONE);
            mapMarkdescDistanceTextView.setVisibility(View.GONE);
        });

    }

    protected void setUpMap() {
        Context context = getContext();

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

    protected void initMarkerView(View view) {
        // marker Info
        mapMarkdescImageView = (ImageView) view.findViewById(R.id.map_markdesc_image);
        mapMarkdescTitleTextView = (TextView) view.findViewById(R.id.map_markdesc_titleTextView);
        mapMarkdescBriefDescriptionTextView = (TextView) view.findViewById(R.id.map_markdesc_brief_descriptionTextView);
        mapMarkdescShowMoreBT = (Button) view.findViewById(R.id.map_markdesc_show_moreBT);
        mapMarkdescBuildRouteBT = (Button) view.findViewById(R.id.map_markdesc_build_routeBT);
        mapMarkdescDistanceTextView = (TextView) view.findViewById(R.id.map_markdesc_distanceTW);

    }

    protected void loadMarkers() {
        Context context = getContext();
        CurrentMarkers museumMarkers = new CurrentMarkers(AttractionType.Museum, new RadiusMarkerClusterer(context)),
                theatreMarkers = new CurrentMarkers(AttractionType.Theatre, new RadiusMarkerClusterer(context)),
                memorialMarkers = new CurrentMarkers(AttractionType.Memorial, new RadiusMarkerClusterer(context)),
                stadiumMarkers = new CurrentMarkers(AttractionType.Stadium, new RadiusMarkerClusterer(context)),
                parkMarkers = new CurrentMarkers(AttractionType.Park, new RadiusMarkerClusterer(context));

        // Создаем лист маркеров
        List<RadiusMarkerClusterer> overlayItems = new ArrayList<>();
        // Добавляем маркеры
        List<Place> data = CSVreader.getData(context);
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
            marker.setIcon(context.getDrawable(MarkerUtil.getMapMarkerByPlaceId(id)));
            marker.setOnMarkerClickListener((Marker mark, MapView map) -> {
                onOverlayTapUp(mark);
                mapMarker.setVisibility(View.VISIBLE);
                return false;
            });
            if (place.getType() == AttractionType.Museum) {
                museumMarkers.getMarkers().add(marker);
            } else if (place.getType() == AttractionType.Theatre) {
                theatreMarkers.getMarkers().add(marker);
            } else if (place.getType() == AttractionType.Memorial) {
                memorialMarkers.getMarkers().add(marker);
            } else if (place.getType() == AttractionType.Stadium) {
                stadiumMarkers.getMarkers().add(marker);
            } else if (place.getType() == AttractionType.Park) {
                parkMarkers.getMarkers().add(marker);
            }
            lastMarkers = museumMarkers;

        }
        markersList = Arrays.asList(
                museumMarkers,
                theatreMarkers,
                memorialMarkers,
                stadiumMarkers,
                parkMarkers);
    }

    protected void onOverlayTapUp(Marker item) {

        int id = Integer.parseInt(item.getTitle());
        Place place = CSVreader.getPlaceById(id);
        int imageSmall = place.getImageSmall();
        int bigImage = place.getImageBig();
        String title = place.getTitle();
        String description = place.getDescription();

        Drawable imageSmallDrawable = getContext().getDrawable(imageSmall);


        mapMarkdescTitleTextView.setText(title);
        mapMarkdescBriefDescriptionTextView.setText(description);
        mapMarkdescImageView.setImageDrawable(imageSmallDrawable);

        mapMarkdescShowMoreBT.setOnClickListener((View v) -> {

            long slideLeftDuration = 1500;

            mapMainParent.setLayoutTransition(null);

            Animation leftAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_left);
            leftAnimation.setDuration(slideLeftDuration);
            Animation rightAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);
            rightAnimation.setDuration(slideLeftDuration);

            mapMarker.setAnimation(leftAnimation);
            leftAnimation.startNow();

            mapMarker.postDelayed(() -> {
                mapMarker.setVisibility(View.GONE);
                mapMarker.setTranslationX(0);
            }, slideLeftDuration + 200);

            mapMarker.setVisibility(View.GONE);

            mapPlaceDescImage.setImageResource(bigImage);
            mapPlaceDescDesc.setText(description);
            mapPlaceDescTitle.setText(title.length() < 25 ? title : title.substring(0, 25) + "...");

            mapMarker.postDelayed(() -> {
                LayoutTransition lt = new LayoutTransition();
                lt.disableTransitionType(LayoutTransition.DISAPPEARING);
                mapMainParent.setLayoutTransition(lt);

                mapPlaceDescParent.setVisibility(View.VISIBLE);
            }, 800);

        });

        mapMarkdescBuildRouteBT.setOnClickListener(v -> {
            Context context = v.getContext();
            isAlive = true;
            Location userLocation = getUserLocation(locationManager);
            Toast.makeText(context, "User location: " + String.valueOf(userLocation), Toast.LENGTH_SHORT).show();
            if (userLocation == null || myLocationOverlay == null) {
                Toast.makeText(context, "Погодь, еще не определил местоположение", Toast.LENGTH_SHORT).show();
                return;
            }

            layoutBottomButtons.setVisibility(View.GONE);
            mapMarker.setVisibility(View.GONE);

            List<Overlay> overlays = map.getOverlays();
            overlays.remove(lastMarkers.getMarkers());
            //overlays.add(myLocationOverlay);
            lastDrownItem = new IconOverlay(item.getPosition(), context.getDrawable(MarkerUtil.getMapMarkerByPlaceId(id)));
            overlays.add(lastDrownItem);

            lastItem = item;
            mapRouteImage.setImageDrawable(imageSmallDrawable);
            closeRouteImage.setImageDrawable(context.getDrawable(imageSmall));
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
            mapMarkdescDistanceTextView.setText(String.valueOf(distanceTo + suffix));
            mapMarkdescDistanceTextView.setVisibility(View.VISIBLE);
        }
    }


    public void requestDrawRoute(@NonNull Marker item) {
        Log.d(TAG, "requestDrawRoute called");
        if (myLocationListener == null || lastItem == null) {
            Log.d(TAG, "Will not request new route because locationListener or lastItem is: null");
            return;
        }
        routeBuilding = true;
        Toast.makeText(getContext(), "Погодь, ща построим", Toast.LENGTH_SHORT).show();
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
            getActivity().runOnUiThread(() -> {
                Log.d(TAG, "Have user location. Request road building!");
                RouteReceiver routeReceiver = MapInitFragment.this;
                if (updateRoadTask == null && routeBuilding) {
                    updateRoadTask = new UpdateRoadTask(finalUserLocation, geoPoint, routeReceiver);
                    Context context = getContext();
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
        Context context = getContext();

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
