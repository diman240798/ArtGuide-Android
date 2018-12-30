package com.swg_games_lab.nanicki.artguide.activity.attraction_info;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.swg_games_lab.nanicki.artguide.R;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class Wiki_Attraction_Activity extends AppCompatActivity {

    ImageView imageView;
    TextView titleTW, descrTW;
    Button listenBTN;
    private GifImageView guideSpeaker;
    private GifDrawable gifFromResource;
    private Drawable playImage;
    private Drawable pauseImage;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_wiki__attraction);

        imageView = (ImageView) findViewById(R.id.wiki_attr_Image);
        titleTW = (TextView) findViewById(R.id.wiki_attr_titleTW);
        descrTW = (TextView) findViewById(R.id.wiki_attr_descriptionTW);
        listenBTN = (Button) findViewById(R.id.wiki_attr_listenBTN);

        playImage = getResources().getDrawable(R.drawable.listen_play);
        pauseImage = getResources().getDrawable(R.drawable.listen_pause);


        ///////////
        guideSpeaker = (GifImageView) findViewById(R.id.wiki_attr_speaking_heroIV);
        try {
            gifFromResource = new GifDrawable( getResources(), R.drawable.gif_speaking_hero);
            gifFromResource.stop();
            guideSpeaker.setImageDrawable(gifFromResource);
        } catch (IOException e) {
            e.printStackTrace();
        }

        listenBTN.setOnClickListener(v-> {
            boolean running = gifFromResource.isRunning();
            if (running) {
                gifFromResource.pause();
                listenBTN.setCompoundDrawablesWithIntrinsicBounds(playImage, null, null, null);
            } else {
                gifFromResource.start();
                listenBTN.setCompoundDrawablesWithIntrinsicBounds(pauseImage, null, null, null);
            }
        });

        if (bundle == null) {
            Bundle extras = getIntent().getExtras();

            if (extras.getString("TAG") != null)
            switch (extras.getString("TAG")) {
                case "Музей Искусств":

                    break;
            }
        }
    }

    private void setInfo(int mDrawable, String title, String description) {
        imageView.setImageResource(mDrawable);
        titleTW.setText(title);
        descrTW.setText(description);
    }
}
