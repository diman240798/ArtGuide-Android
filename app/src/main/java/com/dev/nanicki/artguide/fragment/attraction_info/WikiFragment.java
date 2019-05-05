package com.dev.nanicki.artguide.fragment.attraction_info;

import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dev.nanicki.artguide.ApplicationActivity;
import com.dev.nanicki.artguide.R;
import com.dev.nanicki.artguide.adapters.WikiAdapter;
import com.dev.nanicki.artguide.csv.CSVreader;
import com.dev.nanicki.artguide.databinding.FragmentWikiBinding;
import com.dev.nanicki.artguide.enums.AttractionType;
import com.dev.nanicki.artguide.fragment.PermissionFragment;
import com.dev.nanicki.artguide.model.Place;
import com.dev.nanicki.artguide.ui.BottomNavigationBehavior;
import com.dev.nanicki.artguide.util.BottomButtonsUtil;
import com.dev.nanicki.artguide.util.PermissionUtil;

import java.util.Arrays;
import java.util.List;

public class WikiFragment extends PermissionFragment {
    private static final String TAG = "WikiFragment";
    // ui
    private RecyclerView mRecyclerView;
    private List<Button> bottomButtons;
    private WikiAdapter wikiAdapter;
    // viewModel
    private WikiFragmentViewModel viewModel;
    // data
    private List<Place> places;
    private FragmentWikiBinding binding;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.<FragmentWikiBinding>inflate(inflater, R.layout.fragment_wiki, container, false);
        //View view = inflater.inflate(R.layout.fragment_wiki, container, false);
        return binding.getRoot();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        viewModel = ViewModelProviders.of(getActivity()).get(WikiFragmentViewModel.class);
        binding.setViewmodel(viewModel);
        viewModel.getAtractionType().observe(this, this::sortList);
    }

    private void initSortingButtons(View view) {
        Button btMuseum = (Button) view.findViewById(R.id.wiki_bt_museum),
                btTheatre = (Button) view.findViewById(R.id.wiki_bt_theatre),
                btMemorial = (Button) view.findViewById(R.id.wiki_bt_memorial),
                btStadium = (Button) view.findViewById(R.id.wiki_bt_stadium),
                btPark = (Button) view.findViewById(R.id.wiki_bt_park);
        bottomButtons = Arrays.asList(btMuseum, btTheatre, btMemorial, btStadium, btPark);
    }

    private void setupAdapter() {
        if (wikiAdapter == null)
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
            PermissionUtil.requestMapRequiredPermissions(this);
    }

    private List<Place> getPlaces() {
        return CSVreader.getData(getContext());
    }

    void sortList(AttractionType attractionType) {
        BottomButtonsUtil.setBottomImages(attractionType, bottomButtons);
        wikiAdapter.sortList(places, attractionType);
    }
}
