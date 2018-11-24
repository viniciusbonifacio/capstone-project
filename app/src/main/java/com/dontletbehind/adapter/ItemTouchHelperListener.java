package com.dontletbehind.adapter;

import android.support.v7.widget.RecyclerView;

/**
 * Defines the behaviour when swiping items.
 */
public interface ItemTouchHelperListener {

    /**
     * Defines the behaviour for swiped items.
     *
     * @param viewHolder adapter's view holder item
     * @param direction swiped
     * @param position adapter's position
     */
    void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);

}
