package com.dev.nanicki.artguide.fragment.attraction_info;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dev.nanicki.artguide.ApplicationActivity;
import com.dev.nanicki.artguide.R;
import com.dev.nanicki.artguide.adapters.WikiAdapter;
import com.dev.nanicki.artguide.csv.CSVreader;
import com.dev.nanicki.artguide.databinding.FragmentWikiBinding;
import com.dev.nanicki.artguide.enums.AttractionType;
import com.dev.nanicki.artguide.fragment.PermissionFragment;
import com.dev.nanicki.artguide.model.Place;
import com.dev.nanicki.artguide.ui.BottomNavigationBehavior;
import com.dev.nanicki.artguide.util.PermissionUtil;
import com.dev.nanicki.artguide.util.SortingUtil;

import java.util.List;

import jp.wasabeef.recyclerview.adapters.SlideInLeftAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

public class WikiFragment extends PermissionFragment {
    private static final String TAG = "WikiFragment";
    // ui
    private RecyclerView mRecyclerView;
    private WikiAdapter wikiAdapter;
    // viewModel
    private WikiFragmentViewModel viewModel;
    // binding
    private FragmentWikiBinding binding;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.<FragmentWikiBinding>inflate(inflater, R.layout.fragment_wiki, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(WikiFragmentViewModel.class);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) view.findViewById(R.id.wiki_bottom_navig_with_buttons);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());

        SlideInLeftAnimator animator = new SlideInLeftAnimator();
        animator.setRemoveDuration(200);
        animator.setAddDuration(300);
        mRecyclerView.setItemAnimator(animator);

        viewModel.currentAttrType.observe(this, this::setListData);

        setupAdapter();
    }

    private void setupAdapter() {
        if (wikiAdapter == null)
            wikiAdapter = new WikiAdapter(CSVreader.getData(getContext()), this::onLearnMoreClicked, this::onBuildRouteClicked);
        mRecyclerView.setAdapter(new SlideInLeftAnimationAdapter(wikiAdapter));
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

    private void setListData(AttractionType attractionType) {
        List<Place> places = SortingUtil.sortList(attractionType, CSVreader.getData(getContext()));
        wikiAdapter.setData(places);
    }
}