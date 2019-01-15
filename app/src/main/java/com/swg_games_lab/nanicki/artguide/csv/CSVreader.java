package com.swg_games_lab.nanicki.artguide.csv;

import android.content.Context;
import android.util.Log;

import com.swg_games_lab.nanicki.artguide.R;
import com.swg_games_lab.nanicki.artguide.model.Place;
import com.swg_games_lab.nanicki.artguide.util.PlaceUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class CSVreader {
    private static volatile List<Place> data;

    public static synchronized List<Place> getData(Context context) {
        if (data == null) {
            data = readData(context);
        }
        return data;
    }


    private static List<Place> readData(Context context) {
        List<Place> result = new ArrayList<>();

        InputStream is = context.getResources().openRawResource(R.raw.data);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8")));

        String line = "";
        String commentariy = "#";
        String separator = ",";

        try {
            while ((line = reader.readLine()) != null) {
                // Split the line into different tokens (using the comma as a separator).
                if (line.startsWith(commentariy))
                    continue;
                String[] splitDescription = line.split("::");

                String[] tokens = splitDescription[0].split(separator);
                String description = splitDescription[1];

                // Read the data and store it.
                String id_s = tokens[0];
                int id = Integer.parseInt(id_s);
                Log.d("__ID__", String.valueOf(id));
                String title = tokens[1];
                String latitude = tokens[2];
                String longitude = tokens[3];
                String imageSmall = tokens[4];
                String imageBig = tokens[5];

                Place place = new Place(
                        id,
                        title,
                        Double.parseDouble(latitude),
                        Double.parseDouble(longitude),
                        context.getResources().getIdentifier(imageSmall, "drawable", context.getPackageName()),
                        context.getResources().getIdentifier(imageBig, "drawable", context.getPackageName()),
                        description,
                        PlaceUtil.getTypeByPlaceId(id));
                result.add(place);
            }
        } catch (IOException e1) {
            Log.e("CSVreader", "Error" + line, e1);
            e1.printStackTrace();
        }
        return result;
    }

    public static Place getPlaceById(int placeId) {
        for (Place place : data) {
            if (place.getId() == placeId)
                return place;
        }
        throw new RuntimeException("No such place with id: " + placeId);
    }
}
