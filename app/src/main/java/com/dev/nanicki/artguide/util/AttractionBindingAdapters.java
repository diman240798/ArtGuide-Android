package com.dev.nanicki.artguide.util;

import android.databinding.BindingAdapter;
import android.widget.Button;

import com.dev.nanicki.artguide.R;

public class AttractionBindingAdapters {
    private static final int playImage = R.drawable.listen_play;
    private static final int pauseImage = R.drawable.listen_pause;


    @BindingAdapter({"android:btnLeftImage"})
    public static void setImageViewResource(Button button, boolean running) {
        if (running)
            button.setCompoundDrawablesWithIntrinsicBounds(pauseImage, 0, 0, 0);
        else
            button.setCompoundDrawablesWithIntrinsicBounds(playImage, 0, 0, 0);
    }
}
