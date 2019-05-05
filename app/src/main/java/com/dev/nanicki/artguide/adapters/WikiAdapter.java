package com.dev.nanicki.artguide.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.dev.nanicki.artguide.databinding.WikiItemBinding;
import com.dev.nanicki.artguide.enums.AttractionType;
import com.dev.nanicki.artguide.listener.BuildRouteListener;
import com.dev.nanicki.artguide.listener.LearnMoreListener;
import com.dev.nanicki.artguide.model.Place;

import java.util.ArrayList;
import java.util.List;

public class WikiAdapter extends RecyclerView.Adapter<WikiItemViewHolder> {

    private static final String TAG = "WikiAdapter";
    private final LearnMoreListener learnMoreListener;
    private final BuildRouteListener buildRouteListener;
    private List<Place> mPlaces;

    public List<Place> getList() {
        return mPlaces;
    }

    public WikiAdapter(List<Place> places, LearnMoreListener learnMoreListener, BuildRouteListener buildRouteListener) {
        mPlaces = places;
        this.learnMoreListener = learnMoreListener;
        this.buildRouteListener = buildRouteListener;
    }

    @Override
    public WikiItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        WikiItemBinding binding = WikiItemBinding.inflate(inflater, parent, false);
        return new WikiItemViewHolder(binding.getRoot(), learnMoreListener, buildRouteListener);
    }

    @Override
    public void onBindViewHolder(WikiItemViewHolder holder, int position) {
        Place place = mPlaces.get(position);
        holder.bind(place);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mPlaces.size();
    }

    public void sortList(List<Place> places, AttractionType type) {
        //Collections.sort(mPlaces, ((place, t1) -> place.getTitle().compareTo(key)));
        List<Place> result = new ArrayList<>();
        for (Place place : places) {
            if (place.getType() == type)
                result.add(place);
        }
        mPlaces = result;
        notifyDataSetChanged();
    }
}
