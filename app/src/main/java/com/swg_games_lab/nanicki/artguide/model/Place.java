package com.swg_games_lab.nanicki.artguide.model;

public class Place {
    private final int id;
    private final String title;
    private final double latitude;
    private final double longitude;
    private final int imageSmall;
    private final int  imageBig;
    private final String description;

    public Place(int id, String title, double latitude, double longitude, int imageSmall, int imageBig, String description) {
        this.id = id;
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageSmall = imageSmall;
        this.imageBig = imageBig;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getImageSmall() {
        return imageSmall;
    }

    public int  getImageBig() {
        return imageBig;
    }

    public String getDescription() {
        return description;
    }
}
