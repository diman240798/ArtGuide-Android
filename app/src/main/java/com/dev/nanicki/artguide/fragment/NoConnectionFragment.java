package com.dev.nanicki.artguide.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dev.nanicki.artguide.R;
import com.dev.nanicki.artguide.util.ConnectionUtil;

public class NoConnectionFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.out_of_connection, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView imageView = (ImageView) view.findViewById(R.id.out_of_connection_show_dialog);
        imageView.setOnClickListener(v -> ConnectionUtil.buildAlertMessageNoConncetion(view.getContext()));
    }

}
