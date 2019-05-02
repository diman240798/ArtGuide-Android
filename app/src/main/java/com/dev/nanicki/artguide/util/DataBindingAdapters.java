package com.dev.nanicki.artguide.util;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

public class DataBindingAdapters {
    @BindingAdapter({"android:src"})
    public static void setImageViewResource(ImageView imageView, int resource) {
        imageView.setImageResource(resource);
    }
}
