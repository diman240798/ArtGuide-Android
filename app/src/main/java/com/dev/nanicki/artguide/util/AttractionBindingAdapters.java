package com.dev.nanicki.artguide.util;

import android.databinding.BindingAdapter;
import android.widget.Button;
import android.widget.ImageView;

public class AttractionBindingAdapters {
    @BindingAdapter({"android:btnLeftImage"})
    public static void setImageViewResource(Button button, int resource) {
        button.setCompoundDrawablesWithIntrinsicBounds(resource, 0, 0, 0);
    }
}
