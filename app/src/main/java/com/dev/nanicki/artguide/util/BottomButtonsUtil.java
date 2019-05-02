package com.dev.nanicki.artguide.util;

import android.widget.Button;

import com.dev.nanicki.artguide.R;
import com.dev.nanicki.artguide.enums.AttractionType;

import java.util.Arrays;
import java.util.List;

public class BottomButtonsUtil {
    public static final List<AttractionType> ATTRACTION_TYPES = Arrays.asList(
            AttractionType.Museum,
            AttractionType.Theatre,
            AttractionType.Memorial,
            AttractionType.Stadium,
            AttractionType.Park);

    private static final List<Integer> MAP_BOTTOM_BTN_IDS = Arrays.asList(
            R.id.map_bt_museum,
            R.id.map_bt_theatre,
            R.id.map_bt_memorial,
            R.id.map_bt_stadium,
            R.id.map_bt_park);


    public static void setBottomImages(AttractionType attractionType, List<Button> bottomButtons) {
        List<Integer> images = Arrays.asList(
                attractionType.equals(AttractionType.Museum)
                        ? R.drawable.item_museum_chosen
                        : R.drawable.item_museum,
                attractionType.equals(AttractionType.Theatre)
                        ? R.drawable.item_theatre_chosen
                        : R.drawable.item_theatre,
                attractionType.equals(AttractionType.Memorial)
                        ? R.drawable.item_memorial_chosen
                        : R.drawable.item_memorial,
                attractionType.equals(AttractionType.Stadium)
                        ? R.drawable.item_stadium_chosen
                        : R.drawable.item_stadium,
                attractionType.equals(AttractionType.Park)
                        ? R.drawable.item_park_chosen
                        : R.drawable.item_park
        );

        for (int i = 0; i < images.size(); i++) {
            Integer image = images.get(i);
            Button button = bottomButtons.get(i);
            button.setBackgroundResource(image);
        }
    }

    public static AttractionType getAttractionTypeForMapById(int id) {
        int index = getAttractionIndexForMapById(id) ;
        return ATTRACTION_TYPES.get(index);
    }

    public static int getAttractionIndexForMapById(int id) {
        if (!MAP_BOTTOM_BTN_IDS.contains(id))
            throw new RuntimeException("Wrong id for map bottom button" + id);
        int index = MAP_BOTTOM_BTN_IDS.indexOf(id);
        return index;
    }
}
