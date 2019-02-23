package com.swg_games_lab.nanicki.artguide;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.swg_games_lab.nanicki.artguide.fragment.MapFragment;
import com.swg_games_lab.nanicki.artguide.fragment.PagerFragment;
import com.swg_games_lab.nanicki.artguide.fragment.attraction_info.WikiAttractionFragment;
import com.swg_games_lab.nanicki.artguide.fragment.attraction_info.WikiFragment;
import com.swg_games_lab.nanicki.artguide.csv.CSVreader;
import com.swg_games_lab.nanicki.artguide.ui.NoScrollingViewPager;
import com.swg_games_lab.nanicki.artguide.ui.FragmentAdapter;

import java.util.ArrayList;
import java.util.List;

public class ApplicationActivity extends AppCompatActivity {

    private NoScrollingViewPager viewPager;
    private PagerFragment pagerFragment;
    private MapFragment mapFragment;
    private WikiFragment wikiFragment;
    private WikiAttractionFragment wikiAttractionFragment;
    private FragmentAdapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        CSVreader.getData(this);
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        viewPager = (NoScrollingViewPager) findViewById(R.id.app_view_pager);

        pagerFragment = new PagerFragment();
        mapFragment = new MapFragment();


        wikiFragment = new WikiFragment();
        wikiAttractionFragment = new WikiAttractionFragment();


        List<Fragment> horizontelFragments = new ArrayList<Fragment>() {{
                add(pagerFragment);
                add(mapFragment);
            }
        };

        adapter = new FragmentAdapter(getSupportFragmentManager(), horizontelFragments);
        viewPager.setAdapter(adapter);
        viewPager.setPagingEnabled(false);

    }

    public void startWikiScreen() {
        pagerFragment.startWikiScreen();
    }

    public void startMapScreen() {
        viewPager.setCurrentItem(1, true);
    }

    public void startPreviousScreen() {
        pagerFragment.startPreviousScreen();
    }

    public void startWikiDetailsScreen(int id) {
        pagerFragment.startWikiDetailsScreen(id);
    }

    public void startMapScreen(int id) {
        Bundle args = new Bundle();
        args.putInt("TAG", id);
        mapFragment.setArguments(args);
        startMapScreen();
    }

    @Override
    public void onBackPressed() {
        int currentItem = viewPager.getCurrentItem();
        if (currentItem > 0) {
            viewPager.setCurrentItem(currentItem - 1, true);
        }else {
            pagerFragment.onBackPressed();
        }
    }
}
