package com.dev.nanicki.artguide.fragment.attraction_info;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.dev.nanicki.artguide.enums.AttractionType;

public class WikiFragmentViewModel extends ViewModel {

    // observable
    public MutableLiveData<AttractionType> currentAttrType = new MutableLiveData<>();

    public void setAtractionType(AttractionType value) {
        this.currentAttrType.setValue(value);
    }
}
