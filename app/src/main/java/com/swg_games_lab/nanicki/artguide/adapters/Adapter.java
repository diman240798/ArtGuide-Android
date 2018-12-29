package com.swg_games_lab.nanicki.artguide.adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.swg_games_lab.nanicki.artguide.R;
import com.swg_games_lab.nanicki.artguide.activity.attraction_info.Wiki_Attraction_Activity;
import com.swg_games_lab.nanicki.artguide.model.Place;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private List<Place> mPlaces;

    public Adapter(List<Place> places) {
        mPlaces = places;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_item, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Place place = mPlaces.get(position);
        holder.imageView.setImageResource(place.getDrawable());
        holder.titleTextView.setText(place.getTitle());
        holder.brief_descriptionTextView.setText(String.valueOf(place.getBrief_description()));
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

    public void sortList(List<Place> places, String key) {
        //Collections.sort(mPlaces, ((place, t1) -> place.getTitle().compareTo(key)));
        List<Place> result = new ArrayList<>();
        for (Place place : places) {
            if (place.getTitle().toLowerCase().contains(key.toLowerCase()) ||
                    place.getBrief_description().toLowerCase().contains(key.toLowerCase()))
                result.add(place);
        }
        mPlaces = result;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imageView;
        public TextView titleTextView;
        public TextView brief_descriptionTextView;
        public Button learn_moreBT;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
            brief_descriptionTextView = (TextView) itemView.findViewById(R.id.brief_descriptionTextView);
            learn_moreBT = (Button) itemView.findViewById(R.id.learn_more_btn);
            learn_moreBT.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d("App", mPlaces.get(getAdapterPosition()).getTitle());
            Intent intent = new Intent(v.getContext(), Wiki_Attraction_Activity.class);
            intent.putExtra("TAG", titleTextView.getText());
            v.getContext().startActivity(intent);

        }
    }

}
