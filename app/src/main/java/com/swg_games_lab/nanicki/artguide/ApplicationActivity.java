package com.swg_games_lab.nanicki.artguide;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.swg_games_lab.nanicki.artguide.csv.CSVreader;
import com.swg_games_lab.nanicki.artguide.fragment.NoConnectionFragment;
import com.swg_games_lab.nanicki.artguide.fragment.PagerFragment;
import com.swg_games_lab.nanicki.artguide.fragment.attraction_info.WikiAttractionFragment;
import com.swg_games_lab.nanicki.artguide.fragment.attraction_info.WikiFragment;
import com.swg_games_lab.nanicki.artguide.fragment.map.MapFragment;
import com.swg_games_lab.nanicki.artguide.ui.FragmentAdapter;
import com.swg_games_lab.nanicki.artguide.ui.NoScrollingViewPager;
import com.swg_games_lab.nanicki.artguide.util.ConnectionUtil;

import java.util.ArrayList;
import java.util.List;

public class ApplicationActivity extends AppCompatActivity {

    private NoScrollingViewPager viewPager;
    private PagerFragment pagerFragment;
    private MapFragment mapFragment;
    private WikiFragment wikiFragment;
    private WikiAttractionFragment wikiAttractionFragment;
    private FragmentAdapter pagerAdapter;
    private NoConnectionFragment noConnectionFragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        CSVreader.getData(this);
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        viewPager = (NoScrollingViewPager) findViewById(R.id.app_view_pager);

        pagerFragment = new PagerFragment();



        wikiFragment = new WikiFragment();
        wikiAttractionFragment = new WikiAttractionFragment();


        List<Fragment> horizontelFragments = new ArrayList<Fragment>() {
            {
                add(pagerFragment);
            }
        };

        boolean connected = isConnected();

        if (connected) {
            mapFragment = new MapFragment();
            horizontelFragments.add(mapFragment);
        } else {
            noConnectionFragment = new NoConnectionFragment();
            horizontelFragments.add(noConnectionFragment);
        }
        pagerAdapter = new FragmentAdapter(getSupportFragmentManager(), horizontelFragments);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setPagingEnabled(false);

    }

    private boolean isConnected() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        return ConnectionUtil.isConnected(locationManager, this);
    }

    public void startWikiScreen() {
        pagerFragment.startWikiScreen();
    }

    private void startMapScreen() {
        mapFragment.onStart();
        viewPager.setCurrentItem(1, true);
    }

    public void startMapScreen(Integer id) {
        if (mapFragment == null) {
            if (isConnected()) {
                rebindMapFragment();
            }
        }
        if (id != null) {
            Bundle args = new Bundle();
            args.putInt("TAG", id);
            mapFragment.setArguments(args);
        }

        startMapScreen();
    }

    public void startPreviousScreen() {
        pagerFragment.startPreviousScreen();
    }

    public void startWikiDetailsScreen(int id) {
        pagerFragment.startWikiDetailsScreen(id);
    }

    @Override
    public void onBackPressed() {
        int currentItem = viewPager.getCurrentItem();
        if (currentItem > 0) {
            mapFragment.stopRoute();
            viewPager.setCurrentItem(currentItem - 1, true);
        } else {
            pagerFragment.onBackPressed();
        }
    }

    public void rebindMapFragment() {
        viewPager.setCurrentItem(0, true);
        mapFragment = new MapFragment();
        ArrayList<Fragment> fragments = new ArrayList<Fragment>() {
            {
                add(pagerFragment);
                add(mapFragment);
            }
        };
        pagerAdapter.setFragments(fragments);
        viewPager.setAdapter(pagerAdapter);
    }
}
