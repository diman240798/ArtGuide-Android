package com.swg_games_lab.nanicki.artguide.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.swg_games_lab.nanicki.artguide.R;
import com.swg_games_lab.nanicki.artguide.fragment.attraction_info.WikiAttractionFragment;
import com.swg_games_lab.nanicki.artguide.fragment.attraction_info.WikiFragment;
import com.swg_games_lab.nanicki.artguide.ui.AnimationSetting;
import com.swg_games_lab.nanicki.artguide.ui.VerticalViewPager;

import java.util.Stack;

public class PagerFragment extends Fragment {

    VerticalViewPager verticalViewPager;
    MainFragment MAIN_FRAGMENT;
    WikiFragment WIKI_FRAGMENT;
    WikiAttractionFragment WIKI_ATTRACTION_FRAGMENT;
    private Fragment CURRENT;
    Stack<Fragment> screens;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_application, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        screens = new Stack<>();
        MAIN_FRAGMENT = new MainFragment();
        WIKI_FRAGMENT = new WikiFragment();
        WIKI_ATTRACTION_FRAGMENT = new WikiAttractionFragment();

        CURRENT = MAIN_FRAGMENT;
        screens.push(CURRENT);

        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.activity_screen, MAIN_FRAGMENT)
                .commit();
    }

    private void setScreenWithAnimation(Fragment screen) {
        screens.push(CURRENT);
        AnimationSetting animationSetting = AnimationSetting.DOWN;
        int start = animationSetting.getStart();
        int end = animationSetting.getEnd();
        getChildFragmentManager()
                .beginTransaction()
                .setCustomAnimations(start, end)
                .replace(R.id.activity_screen, screen)
                .commit();
        CURRENT = screen;
    }


    public void startWikiScreen() {
        setScreenWithAnimation(WIKI_FRAGMENT);
    }


    private void setPrevScreenWithAnimation(Fragment previous, AnimationSetting animationSetting) {
        int start = animationSetting.getStart();
        int end = animationSetting.getEnd();
        countIsAnimating();
        getChildFragmentManager()
                .beginTransaction()
                .setCustomAnimations(start, end)
                .replace(R.id.activity_screen, previous)
                .commit();
        CURRENT = previous;
    }

    public void startPreviousScreen() {
        Fragment previous = screens.pop();
        AnimationSetting animation = AnimationSetting.UP;
        setPrevScreenWithAnimation(previous, animation);
    }

    public void startWikiDetailsScreen(int id) {
        Bundle args = new Bundle();
        args.putInt("TAG", id);
        WIKI_ATTRACTION_FRAGMENT.setArguments(args);
        setScreenWithAnimation(WIKI_ATTRACTION_FRAGMENT);
    }

    public volatile boolean animating = false;
    void countIsAnimating() {
        animating = true;
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                animating = false;
            }
            animating = false;
        }).start();
    }

    public void onBackPressed() {
        if (screens.size() > 1) {
            if (!animating)
                startPreviousScreen();
        } else {
            getActivity().finish();
        }
    }
}
