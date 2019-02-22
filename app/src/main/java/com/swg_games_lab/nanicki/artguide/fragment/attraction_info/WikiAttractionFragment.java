package com.swg_games_lab.nanicki.artguide.fragment.attraction_info;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.swg_games_lab.nanicki.artguide.MainActivity;
import com.swg_games_lab.nanicki.artguide.R;
import com.swg_games_lab.nanicki.artguide.csv.CSVreader;
import com.swg_games_lab.nanicki.artguide.model.Place;
import com.swg_games_lab.nanicki.artguide.util.PermissionUtil;

import java.io.IOException;
import java.util.Locale;
import java.util.Set;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class WikiAttractionFragment extends Fragment {

    private static final String TAG = "WikiAttractionFragment";
    ImageView imageView;
    TextView titleTW, descrTW;
    Button listenBTN, bottomBTN, showOnMap;
    private GifImageView guideSpeaker;
    private GifDrawable gifFromResource;
    private Drawable playImage;
    private Drawable pauseImage;
    private TextToSpeech textToSpeech;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wiki__attraction, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle bundle) {
        super.onViewCreated(view, bundle);

        bottomBTN = (Button) view.findViewById(R.id.wiki_attr_bottomBT);
        bottomBTN.setOnClickListener(v -> {
            MainActivity activity = (MainActivity) getActivity();
            activity.startPreviousScreen();
        });
        imageView = (ImageView) view.findViewById(R.id.wiki_attr_Image);
        titleTW = (TextView) view.findViewById(R.id.wiki_attr_titleTW);
        descrTW = (TextView) view.findViewById(R.id.wiki_attr_descriptionTW);
        listenBTN = (Button) view.findViewById(R.id.wiki_attr_listenBTN);
        showOnMap = (Button) view.findViewById(R.id.wiki_attr_show_on_mapBT);

        playImage = getResources().getDrawable(R.drawable.listen_play);
        pauseImage = getResources().getDrawable(R.drawable.listen_pause);

        Context context = getContext();
        textToSpeech = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(new Locale("ru"));
                Set<Voice> voices = textToSpeech.getVoices();
                for (Voice voice : voices) {
                    if (voice.getName().equals("ru-ru-x-dfc#male_1-local"))
                        textToSpeech.setVoice(voice);
                }
            }
        });

        ///////////
        guideSpeaker = (GifImageView) view.findViewById(R.id.wiki_attr_speaking_heroIV);
        try {
            gifFromResource = new GifDrawable(getResources(), R.drawable.gif_speaking_hero);
            gifFromResource.stop();
            guideSpeaker.setImageDrawable(gifFromResource);
        } catch (IOException e) {
            e.printStackTrace();
        }

        listenBTN.setOnClickListener(v -> {
            boolean running = gifFromResource.isRunning();
            if (running) {
                gifFromResource.pause();
                listenBTN.setCompoundDrawablesWithIntrinsicBounds(playImage, null, null, null);
                textToSpeech.stop();
            } else {
                gifFromResource.start();
                listenBTN.setCompoundDrawablesWithIntrinsicBounds(pauseImage, null, null, null);
                textToSpeech.speak(descrTW.getText().toString(), TextToSpeech.QUEUE_ADD, null);
            }
        });

//        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle != null) {
                int placeId = bundle.getInt("TAG");
                Place place = CSVreader.getPlaceById(placeId);
                // get data
                int placeImageBig = place.getImageBig();
                String title = place.getTitle();
                String description = place.getDescription();
                // set data
                imageView.setImageResource(placeImageBig);
                titleTW.setText(title);
                descrTW.setText(description);
                showOnMap.setOnClickListener(v -> {
                    if (PermissionUtil.hasMapRequiredPermissions(v.getContext())) {
                        Log.d(TAG, place.getTitle());
                        MainActivity activity = (MainActivity) getActivity();
                        activity.startMapScreen();
                    } else
                        PermissionUtil.requestMapRequiredPermissions(getContext());

                });
            } else
                throw new RuntimeException("No Bundle Here (:");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        gifFromResource.stop();
        textToSpeech.stop();
        textToSpeech.shutdown();
    }

    private void setInfo(int mDrawable, String title, String description) {
        imageView.setImageResource(mDrawable);
        titleTW.setText(title);
        descrTW.setText(description);
    }

    /*
    @Override
    public void finish() {
        super.finish();
        // TODO: Add Animations
        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }*/
}
