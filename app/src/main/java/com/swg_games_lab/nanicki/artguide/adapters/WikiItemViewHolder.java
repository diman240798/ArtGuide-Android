package com.swg_games_lab.nanicki.artguide.adapters;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.swg_games_lab.nanicki.artguide.R;
import com.swg_games_lab.nanicki.artguide.databinding.WikiItemBinding;
import com.swg_games_lab.nanicki.artguide.listener.BuildRouteListener;
import com.swg_games_lab.nanicki.artguide.listener.LearnMoreListener;
import com.swg_games_lab.nanicki.artguide.model.Place;

public class WikiItemViewHolder extends RecyclerView.ViewHolder {

    private final WikiItemBinding binding;
    private Button learn_moreBT;
    private Button buildRouteBT;

    public WikiItemViewHolder(View itemView, LearnMoreListener learnMoreListener, BuildRouteListener buildRouteListener) {
        super(itemView);
        binding = DataBindingUtil.bind(itemView);

        learn_moreBT = (Button) itemView.findViewById(R.id.wiki_item_learn_more_Button);
        buildRouteBT = (Button) itemView.findViewById(R.id.wiki_item_build_routeBT);

        learn_moreBT.setText(R.string.Learn_More_btn);

        // learn more click
        View.OnClickListener learnMore = v -> learnMoreListener.onLearnMoreClicked(itemView.getContext(), getAdapterPosition());
        itemView.setOnClickListener(learnMore);
        learn_moreBT.setOnClickListener(learnMore);
        // build route click
        buildRouteBT.setOnClickListener(v -> buildRouteListener.onBuildRouteClicked(itemView.getContext(), getAdapterPosition()));
    }


    public void bind(Place place) {
        binding.setPlace(place);
    }
}
