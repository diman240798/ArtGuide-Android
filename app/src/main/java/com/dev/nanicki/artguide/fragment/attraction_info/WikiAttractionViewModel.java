package com.dev.nanicki.artguide.fragment.attraction_info;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.view.View;

public class WikiAttractionViewModel extends ViewModel {

    private boolean isPlaying = false;
    public MutableLiveData<Boolean> isPlayingLV = new MutableLiveData<>();

    public WikiAttractionViewModel() {
        super();
        isPlayingLV.setValue(false);
    }

    public void onPlayClicked(View v) {
        isPlaying = !isPlaying;
        isPlayingLV.setValue(isPlaying);
    }

    void onStop() {
        isPlayingLV.setValue(false);
    }

}
