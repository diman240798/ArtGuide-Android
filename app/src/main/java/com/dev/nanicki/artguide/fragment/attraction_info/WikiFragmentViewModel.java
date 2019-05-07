package com.dev.nanicki.artguide.fragment.attraction_info;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.dev.nanicki.artguide.csv.CSVreader;
import com.dev.nanicki.artguide.enums.AttractionType;
import com.dev.nanicki.artguide.model.Place;

import java.util.ArrayList;
import java.util.List;

public class WikiFragmentViewModel extends AndroidViewModel {

    // data
    private final List<Place> places = CSVreader.getData(getApplication());;

    // observable
    public ObservableField<AttractionType> obsAttrType = new ObservableField<>();
    private MutableLiveData<List<Place>> currentPlaces = new MutableLiveData<>();

    public WikiFragmentViewModel(@NonNull Application application) {
        super(application);
    }

    public void setAtractionType(AttractionType value) {
        this.obsAttrType.set(value);
        currentPlaces.setValue(sortList(value));
    }

    private List<Place> sortList(AttractionType type) {
        List<Place> result = new ArrayList<>();
        for (Place place : places) {
            if (place.getType() == type)
                result.add(place);
        }
        return result;
    }

    public LiveData<List<Place>> getPlaces() {
        return currentPlaces;
    }

    public List<Place> getAllPlaces() {
        return places;
    }
}
