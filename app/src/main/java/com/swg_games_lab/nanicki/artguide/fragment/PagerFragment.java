package com.swg_games_lab.nanicki.artguide.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.swg_games_lab.nanicki.artguide.R;
import com.swg_games_lab.nanicki.artguide.fragment.attraction_info.WikiAttractionFragment;
import com.swg_games_lab.nanicki.artguide.fragment.attraction_info.WikiFragment;
import com.swg_games_lab.nanicki.artguide.fragment.main.MainFragment;
import com.swg_games_lab.nanicki.artguide.ui.AnimationSetting;

import java.util.Stack;

public class PagerFragment extends Fragment {

    private MainFragment MAIN_FRAGMENT;
    private WikiFragment WIKI_FRAGMENT;
    private WikiAttractionFragment WIKI_ATTRACTION_FRAGMENT;
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
        if (MAIN_FRAGMENT == null) {
            screens = new Stack<>();
            MAIN_FRAGMENT = new MainFragment();
            WIKI_FRAGMENT = new WikiFragment();
            WIKI_ATTRACTION_FRAGMENT = new WikiAttractionFragment();
        }

        if (screens.size() == 0) {
            CURRENT = MAIN_FRAGMENT;
            screens.push(CURRENT);
        } else {
            CURRENT = screens.peek();
        }

        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.activity_screen, CURRENT)
                .commit();
    }

    private void setScreenWithAnimation(Fragment screen, AnimationSetting animationSetting) {
        countIsAnimating();
        setPrevScreenWithAnimation(screen, animationSetting);
        screens.push(CURRENT);
    }


    public void startWikiScreen() {
        setScreenWithAnimation(WIKI_FRAGMENT, AnimationSetting.DOWN);
    }


    private void setPrevScreenWithAnimation(Fragment previous, AnimationSetting animationSetting) {
        int start = animationSetting.getStart();
        int end = animationSetting.getEnd();
        getChildFragmentManager()
                .beginTransaction()
                .setCustomAnimations(start, end)
                .replace(R.id.activity_screen, previous)
                .commit();
        CURRENT = previous;
    }

    public void startPreviousScreen() {
        Fragment current = screens.pop();
        Fragment previous = screens.peek();
        AnimationSetting animation = AnimationSetting.UP;
        if (CURRENT == WIKI_ATTRACTION_FRAGMENT)
            animation = AnimationSetting.LEFT;
        setPrevScreenWithAnimation(previous, animation);
    }

    public void startWikiDetailsScreen(int id) {
        Bundle args = new Bundle();
        args.putInt("TAG", id);
        WIKI_ATTRACTION_FRAGMENT.setArguments(args);
        setScreenWithAnimation(WIKI_ATTRACTION_FRAGMENT, AnimationSetting.RIGHT);
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

    public MainFragment getMAIN_FRAGMENT() {
        return MAIN_FRAGMENT;
    }

    public WikiFragment getWIKI_FRAGMENT() {
        return WIKI_FRAGMENT;
    }

    public WikiAttractionFragment getWIKI_ATTRACTION_FRAGMENT() {
        return WIKI_ATTRACTION_FRAGMENT;
    }

    public void setMAIN_FRAGMENT(MainFragment MAIN_FRAGMENT) {
        this.MAIN_FRAGMENT = MAIN_FRAGMENT;
    }

    public void setWIKI_FRAGMENT(WikiFragment WIKI_FRAGMENT) {
        this.WIKI_FRAGMENT = WIKI_FRAGMENT;
    }

    public void setWIKI_ATTRACTION_FRAGMENT(WikiAttractionFragment WIKI_ATTRACTION_FRAGMENT) {
        this.WIKI_ATTRACTION_FRAGMENT = WIKI_ATTRACTION_FRAGMENT;
    }

    public void setScreens(Stack<Fragment> screens) {
        this.screens = screens;
    }

    public Stack<Fragment> getScreens() {
        return screens;
    }
}
