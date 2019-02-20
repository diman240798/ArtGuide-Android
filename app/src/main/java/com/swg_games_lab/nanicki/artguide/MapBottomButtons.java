package com.swg_games_lab.nanicki.artguide;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.swg_games_lab.nanicki.artguide.activity.MapInitActivity;

public class MapBottomButtons extends MapInitActivity implements View.OnClickListener {


    protected void initBottomSortingButtons() {

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
