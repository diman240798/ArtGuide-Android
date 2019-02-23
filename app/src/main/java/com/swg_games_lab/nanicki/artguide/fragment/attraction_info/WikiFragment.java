package com.swg_games_lab.nanicki.artguide.fragment.attraction_info;

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
    private Button btMuseum, btTheatre, btMemorial, btStadium, btPark;
    private WikiAdapter wikiAdapter;
    private List<Place> places;
    private BottomNavigationView wikiBottomButtons;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wiki, container, false);
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
        wikiBottomButtons = (BottomNavigationView) view.findViewById(R.id.wiki_bottom_navig_with_buttons);
        btMuseum = (Button) view.findViewById(R.id.wiki_bt_museum);
        btTheatre = (Button) view.findViewById(R.id.wiki_bt_theatre);
        btMemorial = (Button) view.findViewById(R.id.wiki_bt_memorial);
        btStadium = (Button) view.findViewById(R.id.wiki_bt_stadium);
        btPark = (Button) view.findViewById(R.id.wiki_bt_park);

        btMuseum.setOnClickListener(this);
        btTheatre.setOnClickListener(this);
        btMemorial.setOnClickListener(this);
        btStadium.setOnClickListener(this);
        btPark.setOnClickListener(this);
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
        int id = place.getId();
        ApplicationActivity activity = (ApplicationActivity) getActivity();
        activity.startWikiDetailsScreen(id);

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
            btMuseum.setBackgroundResource(R.drawable.item_museum_chosen);
            wikiAdapter.sortList(places, AttractionType.Museum);
        } else if (id == R.id.wiki_bt_stadium) {
            btStadium.setBackgroundResource(R.drawable.item_stadium_chosen);
            wikiAdapter.sortList(places, AttractionType.Stadium);
        } else if (id == R.id.wiki_bt_memorial) {
            btMemorial.setBackgroundResource(R.drawable.item_memorial_chosen);
            wikiAdapter.sortList(places, AttractionType.Memorial);
        } else if (id == R.id.wiki_bt_theatre) {
            btTheatre.setBackgroundResource(R.drawable.item_theatre_chosen);
            wikiAdapter.sortList(places, AttractionType.Theatre);
        } else if (id == R.id.wiki_bt_park) {
            btPark.setBackgroundResource(R.drawable.item_park_chosen);
            wikiAdapter.sortList(places, AttractionType.Park);
        }
    }

    private void setDefaultImages() {
        btTheatre.setBackgroundResource(R.drawable.item_theatre);
        btMuseum.setBackgroundResource(R.drawable.item_museum);
        btMemorial.setBackgroundResource(R.drawable.item_memorial);
        btStadium.setBackgroundResource(R.drawable.item_stadium);
        btPark.setBackgroundResource(R.drawable.item_park);
    }
}
