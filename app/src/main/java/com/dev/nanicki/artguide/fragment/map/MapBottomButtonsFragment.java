package com.dev.nanicki.artguide.fragment.map;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.dev.nanicki.artguide.R;
import com.dev.nanicki.artguide.enums.AttractionType;
import com.dev.nanicki.artguide.util.BottomButtonsUtil;

import java.util.Arrays;
import java.util.Objects;

public class MapBottomButtonsFragment extends MapInitFragment implements View.OnClickListener {


    protected void initBottomSortingButtons(View view) {

        layoutBottomButtons = (LinearLayout) view.findViewById(R.id.bottom_linear_with_buttons);

        Button btMuseum = (Button) view.findViewById(R.id.map_bt_museum),
                btTheatre = (Button) view.findViewById(R.id.map_bt_theatre),
                btMemorial = (Button) view.findViewById(R.id.map_bt_memorial),
                btStadium = (Button) view.findViewById(R.id.map_bt_stadium),
                btPark = (Button) view.findViewById(R.id.map_bt_park);

        btMuseum.setOnClickListener(this);
        btTheatre.setOnClickListener(this);
        btMemorial.setOnClickListener(this);
        btStadium.setOnClickListener(this);
        btPark.setOnClickListener(this);

        bottomButtons = Arrays.asList(btMuseum, btTheatre, btMemorial, btStadium, btPark);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        AttractionType newAttractionType = BottomButtonsUtil.getAttractionTypeForMapById(id);
        AttractionType oldAttractionType = lastMarkers.getAttractionType();

        if (Objects.equals(newAttractionType, oldAttractionType))
            return;

        map.getOverlays().remove(lastMarkers.getMarkers());
        BottomButtonsUtil.setBottomImages(newAttractionType, bottomButtons);

        int attractionIndex = BottomButtonsUtil.getAttractionIndexForMapById(id);
        lastMarkers = markersList.get(attractionIndex);
        map.getOverlays().add(lastMarkers.getMarkers());

    }

}
