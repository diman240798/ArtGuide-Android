package com.dev.nanicki.artguide.fragment.attraction_info;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.util.MutableBoolean;
import android.view.View;

import com.dev.nanicki.artguide.R;

public class WikiAttractionViewModel extends ViewModel {
    private final int playImage = R.drawable.listen_play;
    private final int pauseImage = R.drawable.listen_pause;

    public ObservableInt btnListenImage = new ObservableInt();
    public ObservableBoolean isPlaying = new ObservableBoolean();
    public MutableLiveData<Boolean> isPlayingLV = new MutableLiveData<>();

    public WikiAttractionViewModel() {
        btnListenImage.set(playImage);
    }

    public void onPlayClicked(View v) {
        boolean isPlay = !isPlaying.get();
        onPlayingChanged(isPlay);
    }

    void onStop() {
        onPlayingChanged(false);
    }

    private void onPlayingChanged(boolean current) {
        isPlaying.set(current);
        isPlayingLV.setValue(current);
        changePlayImage();
    }

    private void changePlayImage() {
        btnListenImage.set(
                isPlaying.get() ? pauseImage : playImage
        );
    }
}
