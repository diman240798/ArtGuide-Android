package com.swg_games_lab.nanicki.artguide.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.swg_games_lab.nanicki.artguide.R;
import com.swg_games_lab.nanicki.artguide.activity.attraction_info.WikiActivity;
import com.swg_games_lab.nanicki.artguide.csv.CSVreader;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button wikiBT, startJourneyBT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CSVreader.getData(this);
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wikiBT = (Button) findViewById(R.id.main_wikiBT);
        wikiBT.setOnClickListener(this);

        startJourneyBT = (Button) findViewById(R.id.main_start_journeyBT);
        startJourneyBT.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_wikiBT:
            startActivity(new Intent(MainActivity.this, WikiActivity.class));
            break;
            case R.id.main_start_journeyBT:
                startActivity(new Intent(MainActivity.this, MapActivity.class));
                break;
        }
    }
}
