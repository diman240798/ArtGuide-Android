package com.dev.nanicki.artguide.fragment.attraction_info;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableField;

import com.dev.nanicki.artguide.enums.AttractionType;

public class WikiFragmentViewModel extends ViewModel {

    // liveData
    private MutableLiveData<AttractionType> atractionType = new MutableLiveData<>();
    // observable
    public ObservableField<AttractionType> obsAttrType = new ObservableField<>();

    public void setAtractionType(AttractionType value) {
        this.atractionType.setValue(value);
        this.obsAttrType.set(value);
    }

    public LiveData<AttractionType> getAtractionType() {
        return atractionType;
    }
}
