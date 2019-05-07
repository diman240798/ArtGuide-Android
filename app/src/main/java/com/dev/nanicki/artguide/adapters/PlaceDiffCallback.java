package com.dev.nanicki.artguide.adapters;

import android.support.v7.util.DiffUtil;

import com.dev.nanicki.artguide.model.Place;

import java.util.List;

class PlaceDiffCallback extends DiffUtil.Callback {

    private final List<Place> oldPosts, newPosts;

    public PlaceDiffCallback(List<Place> oldPosts, List<Place> newPosts) {
        this.oldPosts = oldPosts;
        this.newPosts = newPosts;
    }

    @Override
    public int getOldListSize() {
        return oldPosts.size();
    }

    @Override
    public int getNewListSize() {
        return newPosts.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldPosts.get(oldItemPosition).getId() == newPosts.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldPosts.get(oldItemPosition).equals(newPosts.get(newItemPosition));
    }
}