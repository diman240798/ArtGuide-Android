package com.dev.nanicki.artguide;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.dev.nanicki.artguide.csv.CSVreader;
import com.dev.nanicki.artguide.fragment.NoConnectionFragment;
import com.dev.nanicki.artguide.fragment.PagerFragment;
import com.dev.nanicki.artguide.fragment.attraction_info.WikiAttractionFragment;
import com.dev.nanicki.artguide.fragment.attraction_info.WikiFragment;
import com.dev.nanicki.artguide.fragment.main.MainFragment;
import com.dev.nanicki.artguide.fragment.map.MapFragment;
import com.dev.nanicki.artguide.ui.PagerAdapter;
import com.dev.nanicki.artguide.ui.NoScrollingViewPager;
import com.dev.nanicki.artguide.util.ConnectionUtil;
import com.dev.nanicki.artguide.util.PermissionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ApplicationActivity extends AppCompatActivity {

    private NoScrollingViewPager viewPager;
    private PagerAdapter pagerAdapter;

    private PagerFragment pagerFragment;
    private MapFragment mapFragment;
    private NoConnectionFragment noConnectionFragment;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        CSVreader.getData(this);
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        viewPager = (NoScrollingViewPager) findViewById(R.id.app_view_pager);

        pagerFragment = new PagerFragment();


        List<Fragment> horizontelFragments = new ArrayList<Fragment>() {
            {
                add(pagerFragment);
            }
        };

        boolean connected = isConnected();
        boolean hasMapRequiredPermissions = PermissionUtil.hasMapRequiredPermissions(this);

        if (!connected || !hasMapRequiredPermissions) {
            noConnectionFragment = new NoConnectionFragment();
            horizontelFragments.add(noConnectionFragment);
        } else {
            mapFragment = new MapFragment();
            horizontelFragments.add(mapFragment);
        }

        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), horizontelFragments);
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
        if (mapFragment != null) {
            mapFragment.startRoute();
        }
        viewPager.setCurrentItem(1, true);
    }

    public void startMapScreen(Integer id) {
        boolean connected = isConnected();

        if (mapFragment == null && connected) {
                rebindMapFragment(id);
        }
        setMapFragmentBundle(id);

        startMapScreen();
    }

    private void setMapFragmentBundle(Integer id) {
        if (mapFragment != null && id != null) {
            Bundle args = new Bundle();
            args.putInt("TAG", id);
            mapFragment.setArguments(args);
        }
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
            if (mapFragment != null) {
                mapFragment.stopRoute();
            }
            viewPager.setCurrentItem(currentItem - 1, true);
        } else {
            pagerFragment.onBackPressed();
        }
    }

    public void rebindMapFragment(Integer id) {
        // get Old
        MainFragment mainFragment = pagerFragment.getMAIN_FRAGMENT();
        WikiFragment wikiFragment = pagerFragment.getWIKI_FRAGMENT();
        WikiAttractionFragment wikiAttractionFragment = pagerFragment.getWIKI_ATTRACTION_FRAGMENT();
        Stack<Fragment> screens = pagerFragment.getScreens();
        // create new
        pagerFragment = new PagerFragment();
        // fill new
        pagerFragment.setMAIN_FRAGMENT(mainFragment);
        pagerFragment.setWIKI_FRAGMENT(wikiFragment);
        pagerFragment.setWIKI_ATTRACTION_FRAGMENT(wikiAttractionFragment);
        pagerFragment.setScreens(screens);
        // create map
        mapFragment = new MapFragment();
        setMapFragmentBundle(id);

        ArrayList<Fragment> fragments = new ArrayList<Fragment>() {
            {
                add(pagerFragment);
                add(mapFragment);
            }
        };

        pagerAdapter.clearAll();
        viewPager.setAdapter(null);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(pagerAdapter);
    }
}