package com.dev.nanicki.artguide.fragment.main;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dev.nanicki.artguide.ApplicationActivity;
import com.dev.nanicki.artguide.R;
import com.dev.nanicki.artguide.fragment.PermissionFragment;
import com.dev.nanicki.artguide.util.PermissionUtil;

public class MainFragment extends PermissionFragment implements View.OnClickListener {

    Button wikiBT, startJourneyBT;


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
                if (!PermissionUtil.hasMapRequiredPermissions(context))
                    PermissionUtil.requestMapRequiredPermissions(this);

                else
                    activity.startMapScreen(null);
                break;
        }
    }
}
