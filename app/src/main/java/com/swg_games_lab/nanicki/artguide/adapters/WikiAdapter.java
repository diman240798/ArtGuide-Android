package com.swg_games_lab.nanicki.artguide.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.swg_games_lab.nanicki.artguide.R;
import com.swg_games_lab.nanicki.artguide.enums.AttractionType;
import com.swg_games_lab.nanicki.artguide.listener.BuildRouteListener;
import com.swg_games_lab.nanicki.artguide.listener.LearnMoreListener;
import com.swg_games_lab.nanicki.artguide.listener.OnBottomReachedListener;
import com.swg_games_lab.nanicki.artguide.model.Place;

import java.util.ArrayList;
import java.util.List;

public class WikiAdapter extends RecyclerView.Adapter<WikiAdapter.ViewHolder> {

    private static final String TAG = "WikiAdapter";
    private final LearnMoreListener learnMoreListener;
    private final BuildRouteListener buildRouteListener;
    private List<Place> mPlaces;

    public WikiAdapter(List<Place> places, LearnMoreListener learnMoreListener, BuildRouteListener buildRouteListener) {
        mPlaces = places;
        this.learnMoreListener = learnMoreListener;
        this.buildRouteListener = buildRouteListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wiki_item, parent, false);
        return new ViewHolder(view, learnMoreListener, buildRouteListener);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Place place = mPlaces.get(position);
//        Glide.with(holder.itemView)
//                .load(place.getImageSmall())
//                .into(holder.imageView);
        holder.imageView.setImageResource(place.getImageSmall());
        holder.titleTextView.setText(place.getTitle());
        holder.brief_descriptionTextView.setText(place.getDescription());
        holder.learn_moreBT.setText(R.string.Learn_More_btn);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mPlaces.size();
    }

    public List<Place> getList() {
        return mPlaces;
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


    static class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;
        private TextView titleTextView;
        private TextView brief_descriptionTextView;
        private Button learn_moreBT;
        private Button buildRouteBT;

        public ViewHolder(View itemView, LearnMoreListener learnMoreListener, BuildRouteListener buildRouteListener) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.wiki_item_imageView);
            titleTextView = (TextView) itemView.findViewById(R.id.wiki_item_titleTextView);
            brief_descriptionTextView = (TextView) itemView.findViewById(R.id.wiki_item_descriptionTextView);

            learn_moreBT = (Button) itemView.findViewById(R.id.wiki_item_learn_more_Button);
            buildRouteBT = (Button) itemView.findViewById(R.id.wiki_item_build_routeBT);

            View.OnClickListener learnMore = v -> learnMoreListener.onLearnMoreClicked(itemView.getContext(), getAdapterPosition());
            itemView.setOnClickListener(learnMore);
            learn_moreBT.setOnClickListener(learnMore);
            buildRouteBT.setOnClickListener(v -> buildRouteListener.onBuildRouteClicked(itemView.getContext(), getAdapterPosition()));

        }
    }



}
