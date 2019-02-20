package com.swg_games_lab.nanicki.artguide.activity.attraction_info;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.swg_games_lab.nanicki.artguide.ApplicationActivity;
import com.swg_games_lab.nanicki.artguide.R;
import com.swg_games_lab.nanicki.artguide.adapters.WikiAdapter;
import com.swg_games_lab.nanicki.artguide.csv.CSVreader;
import com.swg_games_lab.nanicki.artguide.enums.AttractionType;
import com.swg_games_lab.nanicki.artguide.model.Place;
import com.swg_games_lab.nanicki.artguide.ui.BottomNavigationBehavior;
import com.swg_games_lab.nanicki.artguide.util.PermissionUtil;

import java.util.List;

public class WikiFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "WikiFragment";

    private RecyclerView mRecyclerView;
    private Button bt_museum, bt_theatre, bt_memorial, bt_stadium, bt_park;
    private WikiAdapter wikiAdapter;
    private List<Place> places;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return LayoutInflater.from(container.getContext()).inflate(R.layout.activity_wiki, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: ENABLE IN THE END
        //mRecyclerView.setHasFixedSize(true);
        // TODO: Add animations
        //overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        places = getPlaces();

        initSortingButtons(view);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) view.findViewById(R.id.wiki_bottom_navig_with_buttons);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        setupAdapter();
    }

    private void initSortingButtons(View view) {
        bt_museum = (Button) view.findViewById(R.id.wiki_bt_museum);
        bt_theatre = (Button) view.findViewById(R.id.wiki_bt_theatre);
        bt_memorial = (Button) view.findViewById(R.id.wiki_bt_memorial);
        bt_stadium = (Button) view.findViewById(R.id.wiki_bt_stadium);
        bt_park = (Button) view.findViewById(R.id.wiki_bt_park);

        bt_museum.setOnClickListener(this);
        bt_theatre.setOnClickListener(this);
        bt_memorial.setOnClickListener(this);
        bt_stadium.setOnClickListener(this);
        bt_park.setOnClickListener(this);
    }
/*
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
    }*/

    private void setupAdapter() {

        wikiAdapter = new WikiAdapter(places, this::onLearnMoreClicked, this::onBuildRouteClicked);
        mRecyclerView.setAdapter(wikiAdapter);
    }

    private void onLearnMoreClicked(Context context, int adapterPosition) {
        Place place = wikiAdapter.getList().get(adapterPosition);
        Log.d(TAG, place.getTitle());
        ApplicationActivity activity = (ApplicationActivity) getActivity();
        activity.startWikiDetailsScreen(place.getId());
    }

    private void onBuildRouteClicked(Context context, int adapterPosition) {
        Place place = wikiAdapter.getList().get(adapterPosition);
        Log.d(TAG, place.getTitle());
        if (PermissionUtil.hasMapRequiredPermissions(context)) {
            ApplicationActivity activity = (ApplicationActivity) getActivity();
            activity.startMapScreen(place.getId());

        } else
            PermissionUtil.requestMapRequiredPermissions(context);
    }

    private List<Place> getPlaces() {
        return CSVreader.getData(getContext());
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        setDefaultImages();

        if (id == R.id.wiki_bt_museum) {
            bt_museum.setBackgroundResource(R.drawable.item_museum_chosen);
            wikiAdapter.sortList(places, AttractionType.Museum);
        } else if (id == R.id.wiki_bt_stadium) {
            bt_stadium.setBackgroundResource(R.drawable.item_stadium_chosen);
            wikiAdapter.sortList(places, AttractionType.Stadium);
        } else if (id == R.id.wiki_bt_memorial) {
            bt_memorial.setBackgroundResource(R.drawable.item_memorial_chosen);
            wikiAdapter.sortList(places, AttractionType.Memorial);
        } else if (id == R.id.wiki_bt_theatre) {
            bt_theatre.setBackgroundResource(R.drawable.item_theatre_chosen);
            wikiAdapter.sortList(places, AttractionType.Theatre);
        } else if (id == R.id.wiki_bt_park) {
            bt_park.setBackgroundResource(R.drawable.item_park_chosen);
            wikiAdapter.sortList(places, AttractionType.Park);
        }
    }

    private void setDefaultImages() {
        bt_theatre.setBackgroundResource(R.drawable.item_theatre);
        bt_museum.setBackgroundResource(R.drawable.item_museum);
        bt_memorial.setBackgroundResource(R.drawable.item_memorial);
        bt_stadium.setBackgroundResource(R.drawable.item_stadium);
        bt_park.setBackgroundResource(R.drawable.item_park);
    }

}
