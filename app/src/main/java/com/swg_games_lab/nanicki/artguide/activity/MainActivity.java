package com.swg_games_lab.nanicki.artguide.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.swg_games_lab.nanicki.artguide.R;
import com.swg_games_lab.nanicki.artguide.activity.attraction_info.WikiActivity;
import com.swg_games_lab.nanicki.artguide.csv.CSVreader;
import com.swg_games_lab.nanicki.artguide.util.PermissionUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button wikiBT, startJourneyBT;

    private final static int mRequestCode = 10127;
    private final static String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

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
                if (!PermissionUtil.hasPermissions(this, PERMISSIONS))
                    PermissionUtil.requestMapRequiredPermissions(this);

                else
                    startActivity(new Intent(MainActivity.this, MapActivity.class));
                break;
        }
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == mRequestCode) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        onPermissionFailed();
                        return;
                    }
                }
                onPermissionGrunted();
            } else {
                onPermissionFailed();
            }
        }
    }

    private void onPermissionGrunted() {
        startActivity(new Intent(MainActivity.this, MapActivity.class));
    }

    private void onPermissionFailed() {

    }

}
