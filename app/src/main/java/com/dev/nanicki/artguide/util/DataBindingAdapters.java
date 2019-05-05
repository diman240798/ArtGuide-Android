package com.dev.nanicki.artguide.util;

import android.databinding.BindingAdapter;
import android.widget.Button;
import android.widget.ImageView;

import com.dev.nanicki.artguide.R;
import com.dev.nanicki.artguide.enums.AttractionType;

public class DataBindingAdapters {
    @BindingAdapter({"android:src"})
    public static void setImageViewResource(ImageView imageView, int resource) {
        imageView.setImageResource(resource);
    }

    @BindingAdapter({"android:backgroundMuseam"})
    public static void setImageViewResourceMuseum(Button button, AttractionType attractionType) {
        if (attractionType == AttractionType.Museum)
            button.setBackgroundResource(R.drawable.item_museum_chosen);
        else
            button.setBackgroundResource(R.drawable.item_museum);
    }

    @BindingAdapter({"android:backgroundTheatre"})
    public static void setImageViewResourceTheatre(Button button, AttractionType attractionType) {
        if (attractionType == AttractionType.Theatre)
            button.setBackgroundResource(R.drawable.item_theatre_chosen);
        else
            button.setBackgroundResource(R.drawable.item_theatre);
    }

    @BindingAdapter({"android:backgroundMemorial"})
    public static void setImageViewResourceMemorial(Button button, AttractionType attractionType) {
        if (attractionType == AttractionType.Memorial)
            button.setBackgroundResource(R.drawable.item_memorial_chosen);
        else
            button.setBackgroundResource(R.drawable.item_memorial);
    }

    @BindingAdapter({"android:backgroundStadium"})
    public static void setImageViewResourceStadium(Button button, AttractionType attractionType) {
        if (attractionType == AttractionType.Stadium)
            button.setBackgroundResource(R.drawable.item_stadium_chosen);
        else
            button.setBackgroundResource(R.drawable.item_stadium);
    }

    @BindingAdapter({"android:backgroundPark"})
    public static void setImageViewResourcePark(Button button, AttractionType attractionType) {
        if (attractionType == AttractionType.Park)
            button.setBackgroundResource(R.drawable.item_park_chosen);
        else
            button.setBackgroundResource(R.drawable.item_park);
    }
}
