package com.dev.nanicki.artguide.fragment.attraction_info;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.dev.nanicki.artguide.enums.AttractionType;

public class WikiFragmentViewModel extends ViewModel {
    private MutableLiveData<AttractionType> atractionType = new MutableLiveData<>();


    public void setAtractionType(AttractionType value) {
        this.atractionType.setValue(value);
    }

    public LiveData<AttractionType> getAtractionType() {
        return atractionType;
    }
}
