package com.swg_games_lab.nanicki.artguide.activity.attraction_info;

import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


import com.swg_games_lab.nanicki.artguide.R;
import com.swg_games_lab.nanicki.artguide.adapters.Adapter;
import com.swg_games_lab.nanicki.artguide.model.Place;
import com.swg_games_lab.nanicki.artguide.ui.BottomNavigationBehavior;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WikiActivity extends AppCompatActivity implements View.OnClickListener, SearchView.OnQueryTextListener {

    private RecyclerView mRecyclerView;
    private Button bt_museum, bt_theatre, bt_memorial, bt_stadium;
    private Adapter adapter;
    private List<Place> places = getPlaces();


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wiki);
        bt_museum = (Button) findViewById(R.id.wiki_bt_museum);
        bt_theatre = (Button) findViewById(R.id.wiki_bt_theatre);
        bt_memorial = (Button) findViewById(R.id.wiki_bt_memorial);
        bt_stadium = (Button) findViewById(R.id.wiki_bt_stadium);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.wiki_bottom_navig_with_buttons);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        setupAdapter();
        // TODO: ENABLE IN THE END
        //mRecyclerView.setHasFixedSize(true);
        bt_museum.setOnClickListener(v -> adapter.sortList(places, "Музей"));
        bt_theatre.setOnClickListener(v -> adapter.sortList(places, "Театр"));
        bt_memorial.setOnClickListener(v -> adapter.sortList(places, "Памятник"));
        bt_stadium.setOnClickListener(v -> adapter.sortList(places, "Стадион"));
    }

    private void setupAdapter() {

        adapter = new Adapter(places);
        mRecyclerView.setAdapter(adapter);
    }

    // TODO: ADD DATA
    private List<Place> getPlaces() {
        List<Place> list = new ArrayList<>();
        list.addAll(Arrays.asList(new Place[]{
                new Place(R.drawable.muzey_kraevedeniya_small, "Музей", "Ростовский областной музей изобразительных."),
                new Place(R.drawable.muzey_kraevedeniya_small, "НЕМузей", "Ростовский областной музей изобразительных."),
                new Place(R.drawable.muzey_kraevedeniya_small, "НЕМузей", "Ростовский областной музей изобразительных."),
                new Place(R.drawable.muzey_kraevedeniya_small, "НЕМузей", "Ростовский областной музей изобразительных."),
                new Place(R.drawable.muzey_kraevedeniya_small, "НЕМузей", "Ростовский областной музей изобразительных."),
                new Place(R.drawable.muzey_kraevedeniya_small, "НЕМузей", "Ростовский областной музей изобразительных."),
                new Place(R.drawable.muzey_kraevedeniya_small, "1", "Ростовский областной музей изобразительных.")
        }));
        return list;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        adapter.sortList(places, s);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }
}
