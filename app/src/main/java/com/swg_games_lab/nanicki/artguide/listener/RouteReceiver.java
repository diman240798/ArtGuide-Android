package com.swg_games_lab.nanicki.artguide.listener;

import org.osmdroid.bonuspack.routing.Road;

public interface RouteReceiver {
    void onRouteReceived(Road[] roads);
}
