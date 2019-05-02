package com.swg_games_lab.nanicki.artguide.fragment.attraction_info;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.swg_games_lab.nanicki.artguide.enums.AttractionType;

public class WikiFragmentViewModel extends AndroidViewModel {
    private MutableLiveData<AttractionType> atractionType = new MutableLiveData<>();

    public WikiFragmentViewModel(@NonNull Application application) {
        super(application);
    }

    public void setAtractionType(AttractionType value) {
        this.atractionType.setValue(value);
    }

    public LiveData<AttractionType> getAtractionType() {
        return atractionType;
    }

    public void sayHi(View view) {
        Toast.makeText(getApplication(), "HI", Toast.LENGTH_SHORT).show();
    }
}
