package com.dev.nanicki.artguide.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private final FragmentManager fm;
    private List<Fragment> fragments;

    public PagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fm = fm;
        this.fragments = fragments;
    }

    public List<Fragment> getFragments() {
        return fragments;
    }

    public void setFragments(List<Fragment> fragments) {
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public void clearAll() {
        for(int i=0; i<fragments.size(); i++)
            fm.beginTransaction().remove(fragments.get(i)).commit();
    }
}