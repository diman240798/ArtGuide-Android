package com.swg_games_lab.nanicki.artguide.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectivityReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
        String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
        boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);

        NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
        NetworkInfo otherNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

        if (currentNetworkInfo.isConnected()) {
            //Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
            //setContentView(R.layout.activity_map);
        } else {
            //Toast.makeText(getApplicationContext(), "Not Connected", Toast.LENGTH_LONG).show();
//                setContentView(R.layout.out_of_connection);
        }
    }
}
