package com.swg_games_lab.nanicki.artguide.model;

public class Place {
    private int mDrawable;
    private String title;
    private String brief_description;

    public Place(int mDrawable, String title, String brief_description) {
        this.mDrawable = mDrawable;
        this.title = title;
        this.brief_description = brief_description;
    }

    public int getDrawable() {
        return mDrawable;
    }

    public void setDrawable(int mDrawable) {
        this.mDrawable = mDrawable;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBrief_description() {
        return brief_description;
    }

    public void setBrief_description(String brief_description) {
        this.brief_description = brief_description;
    }

}
