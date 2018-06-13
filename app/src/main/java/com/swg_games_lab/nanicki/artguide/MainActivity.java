package com.swg_games_lab.nanicki.artguide;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.swg_games_lab.nanicki.artguide.attraction_info.WikiActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button wikiBT, start_journeyBT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wikiBT = (Button) findViewById(R.id.main_wikiBT);
        wikiBT.setOnClickListener(this);

        start_journeyBT = (Button) findViewById(R.id.main_start_journeyBT);
        start_journeyBT.setOnClickListener(this);
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
