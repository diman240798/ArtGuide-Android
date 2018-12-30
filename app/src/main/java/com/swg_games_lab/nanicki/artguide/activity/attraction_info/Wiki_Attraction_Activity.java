package com.swg_games_lab.nanicki.artguide.activity.attraction_info;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.swg_games_lab.nanicki.artguide.R;

public class Wiki_Attraction_Activity extends AppCompatActivity {

    ImageView imageView;
    TextView titleTW, descrTW;
    Button listenBTN;
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_wiki__attraction);

        imageView = (ImageView) findViewById(R.id.wiki_attr_Image);
        titleTW = (TextView) findViewById(R.id.wiki_attr_titleTW);
        descrTW = (TextView) findViewById(R.id.wiki_attr_descriptionTW);
        listenBTN = (Button) findViewById(R.id.wiki_attr_listenBTN);


        if (bundle == null) {
            Bundle extras = getIntent().getExtras();

            if (extras.getString("TAG") != null)
            switch (extras.getString("TAG")) {
                case "Музей Искусств":

                    break;
            }
        }
    }

    private void setInfo(int mDrawable, String title, String description) {
        imageView.setImageResource(mDrawable);
        titleTW.setText(title);
        descrTW.setText(description);
    }
}
