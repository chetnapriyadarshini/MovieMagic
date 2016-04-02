package com.application.chetna_priya.moviemagic;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

/**
 * Created by chetna_priya on 3/25/2016.
 */
public class MovieItemAnimator extends RecyclerView.ItemAnimator {
    private static final String LOG_TAG = MovieItemAnimator.class.getSimpleName();

    @Override
    public boolean animateDisappearance(RecyclerView.ViewHolder viewHolder, ItemHolderInfo itemHolderInfo, ItemHolderInfo itemHolderInfo1) {
        return false;
    }

    @Override
    public boolean animateAppearance(RecyclerView.ViewHolder viewHolder, ItemHolderInfo itemHolderInfo, ItemHolderInfo itemHolderInfo1) {
        Log.d(LOG_TAG, "I AM ANIMATINGGGGGGGGGGGGG");
        return false;
    }

    @Override
    public boolean animatePersistence(RecyclerView.ViewHolder viewHolder, ItemHolderInfo itemHolderInfo, ItemHolderInfo itemHolderInfo1) {
        return false;
    }

    @Override
    public boolean animateChange(RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1, ItemHolderInfo itemHolderInfo, ItemHolderInfo itemHolderInfo1) {
        return false;
    }

    @Override
    public void runPendingAnimations() {

    }

    @Override
    public void endAnimation(RecyclerView.ViewHolder viewHolder) {

    }

    @Override
    public void endAnimations() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }
}
