package com.swg_games_lab.nanicki.artguide.util;

import com.swg_games_lab.nanicki.artguide.R;

public class MarkerUtil {
    public static int getMapMarkerByPlaceId(int id) {
        if (id >= 0 && id <= 100) {
            return R.drawable.map_marker_museam;
        } else if (id >= 101 && id <= 200) {
            return R.drawable.map_marker_theatre;
        } else if (id >= 201 && id <= 300) {
            return R.drawable.map_marker_memorial;
        } else if (id >= 301 && id <= 400) {
            return R.drawable.map_marker_stadium;
        } else if (id >= 401 && id <= 500) {
            return R.drawable.map_marker_park;
        }
        throw new RuntimeException("Unknown id: " + id);
    }
}
