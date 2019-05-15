package com.dev.nanicki.artguide.fragment.attraction_info;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dev.nanicki.artguide.ApplicationActivity;
import com.dev.nanicki.artguide.R;
import com.dev.nanicki.artguide.csv.CSVreader;
import com.dev.nanicki.artguide.databinding.FragmentWikiAttractionBinding;
import com.dev.nanicki.artguide.fragment.PermissionFragment;
import com.dev.nanicki.artguide.model.Place;
import com.dev.nanicki.artguide.util.PermissionUtil;
import com.dev.nanicki.artguide.util.AttractionUIutil;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class WikiAttractionFragment extends PermissionFragment {

    private static final String TAG = "WikiAttractionFragment";
    private GifDrawable gifFromResource;
    private WikiAttractionViewModel viewModel;
    private FragmentWikiAttractionBinding binding;
    private TextToSpeech textToSpeech;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.<FragmentWikiAttractionBinding>inflate(inflater, R.layout.fragment_wiki__attraction, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        viewModel = ViewModelProviders.of(getActivity()).get(WikiAttractionViewModel.class);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle bundle) {
        super.onViewCreated(view, bundle);
        setUpGif(view);
        textToSpeech = AttractionUIutil.setUpTTS(getContext(), status -> {
            AttractionUIutil.onTTSinit(textToSpeech);
        });

        viewModel.isPlayingLV.observe(this, isPlaying -> {
            if (isPlaying)
                onStartPlaying(binding.getPlace().getDescription());
            else
                onStopPlaying();
        });
    }

    private void setUpGif(View view) {
        GifImageView guideSpeaker = (GifImageView) view.findViewById(R.id.wiki_attr_speaking_heroIV);
        try {
            gifFromResource = AttractionUIutil.setUpG(guideSpeaker);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle bundle = getArguments();
        if (bundle == null) throw new RuntimeException("No Bundle Here (:");

        int placeId = bundle.getInt("TAG");
        Place place = CSVreader.getPlaceById(placeId);
        // set place
        binding.setPlace(place);
        // on Back Clicked
        binding.wikiAttrBottomBT.setOnClickListener(this::onBtnBackClick);
        // on Show Map Clicked
        binding.wikiAttrShowOnMapBT.setOnClickListener(v -> {
            if (PermissionUtil.hasMapRequiredPermissions(v.getContext())) {
                Log.d(TAG, place.getTitle());
                ApplicationActivity activity = (ApplicationActivity) getActivity();
                activity.startMapScreen(placeId);
            } else
                PermissionUtil.requestMapRequiredPermissions(this);

        });


    }


    // CallBacks
    private void onBtnBackClick(View view) {
        ApplicationActivity activity = (ApplicationActivity) getActivity();
        activity.startPreviousScreen();
    }

    private void onStartPlaying(String text) {
        gifFromResource.start();
        textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null);
    }

    private void onStopPlaying() {
        gifFromResource.pause();
        textToSpeech.stop();
    }

    ////////////

    @Override
    public void onStop() {
        super.onStop();
        gifFromResource.pause();
        gifFromResource.stop();
        viewModel.onStop();
        textToSpeech.stop();
        textToSpeech.shutdown();
    }
}
