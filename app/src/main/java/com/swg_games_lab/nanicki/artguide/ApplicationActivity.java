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

    private void setScreen(Fragment screen) {
        screens.push(CURRENT);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_screen, screen)
                .commit();
        CURRENT = screen;
    }

    private void setPrevScreen(Fragment screen) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_screen, screen)
                .commit();
        CURRENT = screen;
    }

    private void setMainScreen() {
        setScreen(MAIN_SCREEN);
    }

    public void startWikiScreen() {
        setScreen(WIKI_SCREEN);
    }

    public void startMapScreen() {
        setScreen(MAP_SCREEN);
    }

    public void startPreviousScreen() {
        Fragment previous = screens.pop();
        setPrevScreen(previous);
    }

    public void startWikiDetailsScreen(int id) {
        Bundle args = new Bundle();
        args.putInt("TAG", id);
        WIKI_DETAIL_SCREEN.setArguments(args);
        setScreen(WIKI_DETAIL_SCREEN);
    }

    public void startMapScreen(int id) {
        Bundle args = new Bundle();
        args.putInt("TAG", id);
        MAP_SCREEN.setArguments(args);
        setScreen(MAP_SCREEN);
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
