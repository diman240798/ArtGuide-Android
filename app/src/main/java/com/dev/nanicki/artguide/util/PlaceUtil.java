package com.dev.nanicki.artguide.util;

import com.dev.nanicki.artguide.enums.AttractionType;

public class PlaceUtil {
    public static AttractionType getTypeByPlaceId(int id) {
        if (id >= 0 && id <= 100) {
            return AttractionType.Museum;
        } else if (id >= 101 && id <= 200) {
            return AttractionType.Theatre;
        } else if (id >= 201 && id <= 300) {
            return AttractionType.Memorial;
        } else if (id >= 301 && id <= 400) {
            return AttractionType.Stadium;
        } else if (id >= 401 && id <= 500) {
            return AttractionType.Park;
        }
        throw new RuntimeException("Unknown id: " + id);
    }
}
