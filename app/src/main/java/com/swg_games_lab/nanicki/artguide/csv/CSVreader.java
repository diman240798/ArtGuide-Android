package com.swg_games_lab.nanicki.artguide.csv;

import android.content.Context;
import android.util.Log;

import com.swg_games_lab.nanicki.artguide.R;
import com.swg_games_lab.nanicki.artguide.model.NewPlace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class CSVreader {
    private static volatile List<NewPlace> data;

    public static List<NewPlace> getData(Context context) {
        if (data == null) {
            data = readData(context);
        }
        return data;
    }


    private static List<NewPlace> readData(Context context) {
        List<NewPlace> result = new ArrayList<>();

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
                String[] tokens = line.split(separator);

                // Read the data and store it.
                String id = tokens[0];
                String title = tokens[1];
                String latitude = tokens[2];
                String  longitude = tokens[3];
                String imageSmall = tokens[4];
                String imageBig = tokens[5];
                String description = tokens[6];

                NewPlace newPlace = new NewPlace(
                        Integer.parseInt(id),
                        title,
                        Double.parseDouble(latitude),
                        Double.parseDouble(longitude),
                        context.getResources().getIdentifier(imageSmall, "drawable", context.getPackageName()),
                        context.getResources().getIdentifier(imageBig, "drawable", context.getPackageName()),
                        description
                );
                result.add(newPlace);
            }
        } catch (IOException e1) {
            Log.e("CSVreader", "Error" + line, e1);
            e1.printStackTrace();
        }
        return result;
    }
}
