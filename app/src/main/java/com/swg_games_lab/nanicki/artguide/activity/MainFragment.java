package com.swg_games_lab.nanicki.artguide.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.swg_games_lab.nanicki.artguide.ApplicationActivity;
import com.swg_games_lab.nanicki.artguide.R;
import com.swg_games_lab.nanicki.artguide.util.PermissionUtil;

public class MainFragment extends Fragment implements View.OnClickListener {

    Button wikiBT, startJourneyBT;

    private final static int mRequestCode = 10127;
    private final static String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        wikiBT = (Button) view.findViewById(R.id.main_wikiBT);
        wikiBT.setOnClickListener(this);

        startJourneyBT = (Button) view.findViewById(R.id.main_start_journeyBT);
        startJourneyBT.setOnClickListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onClick(View view) {
        ApplicationActivity activity = (ApplicationActivity) getActivity();
        switch (view.getId()) {
            case R.id.main_wikiBT:
                activity.startWikiScreen();
                break;
            case R.id.main_start_journeyBT:
                Context context = getContext();
                if (!PermissionUtil.hasPermissions(context, PERMISSIONS))
                    PermissionUtil.requestMapRequiredPermissions(context);

                else
                    activity.startMapScreen();
                break;
        }
    }


    @Override
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
        ApplicationActivity activity = (ApplicationActivity) getActivity();
        activity.startMapScreen();
    }

    private void onPermissionFailed() {

    }

}
