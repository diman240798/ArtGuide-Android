package com.dev.nanicki.artguide.util;

import android.content.Context;

import com.dev.nanicki.artguide.csv.CSVreader;
import com.dev.nanicki.artguide.enums.AttractionType;
import com.dev.nanicki.artguide.model.Place;

import java.util.ArrayList;
import java.util.List;

public class SortingUtil {
    public static List<Place> sortList(AttractionType type, List<Place> places) {
        List<Place> result = new ArrayList<>();
        for (Place place : places) {
            if (place.getType() == type)
                result.add(place);
        }
        return result;
    }
}
