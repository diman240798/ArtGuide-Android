package com.dev.nanicki.artguide.ui;

import com.dev.nanicki.artguide.enums.AttractionType;

import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;

public class CurrentMarkers {
    private final AttractionType attractionType;
    private final RadiusMarkerClusterer markers;

    public CurrentMarkers(AttractionType attractionType, RadiusMarkerClusterer markers) {
        this.attractionType = attractionType;
        this.markers = markers;
    }

    public AttractionType getAttractionType() {
        return attractionType;
    }

    public RadiusMarkerClusterer getMarkers() {
        return markers;
    }
}
