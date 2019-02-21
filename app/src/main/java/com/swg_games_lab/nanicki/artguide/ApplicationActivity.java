package com.swg_games_lab.nanicki.artguide;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.swg_games_lab.nanicki.artguide.activity.MainFragment;
import com.swg_games_lab.nanicki.artguide.activity.MapFragment;
import com.swg_games_lab.nanicki.artguide.activity.attraction_info.WikiFragment;
import com.swg_games_lab.nanicki.artguide.activity.attraction_info.WikiAttractionFragment;
import com.swg_games_lab.nanicki.artguide.csv.CSVreader;
import com.swg_games_lab.nanicki.artguide.ui.AnimationSetting;

import java.util.Stack;

public class ApplicationActivity extends AppCompatActivity {

    private Fragment MAIN_SCREEN;
    private Fragment MAP_SCREEN;
    private Fragment WIKI_SCREEN;
    private Fragment WIKI_DETAIL_SCREEN;
    private Fragment CURRENT;
    Stack<Fragment> screens;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CSVreader.getData(this);
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application);

        FrameLayout screen = (FrameLayout) findViewById(R.id.activity_screen);

        screens = new Stack<>();
        MAIN_SCREEN = new MainFragment();
        CURRENT = MAIN_SCREEN;
        MAP_SCREEN = new MapFragment();
        WIKI_SCREEN = new WikiFragment();
        WIKI_DETAIL_SCREEN = new WikiAttractionFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.activity_screen, MAIN_SCREEN)
                .commit();
        screens.push(MAIN_SCREEN);
    }

    private void setPrevScreen(Fragment screen) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_screen, screen)
                .commit();
        CURRENT = screen;
    }

    public void startWikiScreen() {
        setScreenWithAnimation(WIKI_SCREEN, AnimationSetting.DOWN);
    }

    public void startMapScreen() {
        setScreenWithAnimation(MAP_SCREEN, AnimationSetting.RIGHT);
    }

    public void startPreviousScreen() {
        Fragment previous = screens.pop();
        AnimationSetting animation = AnimationSetting.LEFT;
        if (previous == MAP_SCREEN) {
            animation = AnimationSetting.LEFT;
        } else if (previous == MAIN_SCREEN) {
            if (CURRENT == WIKI_SCREEN)
            animation = AnimationSetting.UP;
            else
                animation = AnimationSetting.LEFT;
        } else if (previous == WIKI_SCREEN) {
            animation = AnimationSetting.LEFT;
        }
        setPrevScreenWithAnimation(previous, animation);
    }

    private void setPrevScreenWithAnimation(Fragment previous, AnimationSetting animationSetting) {
        int start = animationSetting.getStart();
        int end = animationSetting.getEnd();
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(start, end)
                .replace(R.id.activity_screen, previous)
                .commit();
        CURRENT = previous;
    }

    public void startWikiDetailsScreen(int id) {
        Bundle args = new Bundle();
        args.putInt("TAG", id);
        WIKI_DETAIL_SCREEN.setArguments(args);
        setScreenWithAnimation(WIKI_DETAIL_SCREEN, AnimationSetting.RIGHT);
    }

    private void setScreenWithAnimation(Fragment screen, AnimationSetting animationSetting) {
        screens.push(CURRENT);
        int start = animationSetting.getStart();
        int end = animationSetting.getEnd();
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(start, end)
                .replace(R.id.activity_screen, screen)
                .commit();
        CURRENT = screen;
    }

    public void startMapScreen(int id) {
        Bundle args = new Bundle();
        args.putInt("TAG", id);
        MAP_SCREEN.setArguments(args);
        setScreenWithAnimation(MAP_SCREEN, AnimationSetting.RIGHT);
    }

    @Override
    public void onBackPressed() {
        if (screens.size() > 1) {
            startPreviousScreen();
        } else {
            super.onBackPressed();
        }
    }
}
