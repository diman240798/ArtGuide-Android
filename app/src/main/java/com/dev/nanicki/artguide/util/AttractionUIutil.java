package com.dev.nanicki.artguide.util;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;

import com.dev.nanicki.artguide.R;

import java.io.IOException;
import java.util.Locale;
import java.util.Set;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class AttractionUIutil {
    public static TextToSpeech setUpTTS(Context context, TextToSpeech.OnInitListener callBack) {
        TextToSpeech result = new TextToSpeech(context, callBack);
        return result;
    }

    public static void onTTSinit(TextToSpeech textToSpeech) {
        textToSpeech.setLanguage(new Locale("ru"));
        Set<Voice> voices = textToSpeech.getVoices();
        for (Voice voice : voices) {
            if (voice.getName().equals("ru-ru-x-dfc#male_1-local"))
                textToSpeech.setVoice(voice);
        }
    }


    public static GifDrawable setUpG(GifImageView guideSpeaker) throws IOException {
        GifDrawable gifFromResource = new GifDrawable(guideSpeaker.getResources(), R.drawable.gif_speaking_hero);
        gifFromResource.stop();
        guideSpeaker.setImageDrawable(gifFromResource);
        return gifFromResource;
    }
}
