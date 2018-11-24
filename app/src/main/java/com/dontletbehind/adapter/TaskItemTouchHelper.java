package com.dontletbehind.adapter;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

/**
 * Helper for Swipe adapter's item view behaviour.
 */
public class TaskItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private ItemTouchHelperListener mItemTouchListener;

    /**
     * Constructor.
     *
     * @param dragDirs
     * @param swipeDirs
     * @param itemTouchListener {@code ItemTouchHelperListener} instance
     */
    public TaskItemTouchHelper(int dragDirs, int swipeDirs, ItemTouchHelperListener itemTouchListener) {
        super(dragDirs, swipeDirs);
        this.mItemTouchListener = itemTouchListener;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        mItemTouchListener.onSwiped(viewHolder, i, viewHolder.getAdapterPosition());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
            View item = ((TaskListAdapter.TaskListAdapterViewHolder) viewHolder).mItemView;
            getDefaultUIUtil().onSelected(item);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View item = ((TaskListAdapter.TaskListAdapterViewHolder) viewHolder).mItemView;
        getDefaultUIUtil().onDraw(c, recyclerView, item, dX, dY, actionState, isCurrentlyActive);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        View item = ((TaskListAdapter.TaskListAdapterViewHolder) viewHolder).mItemView;
        getDefaultUIUtil().clearView(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View item = ((TaskListAdapter.TaskListAdapterViewHolder) viewHolder).mItemView;
        getDefaultUIUtil().onDraw(c, recyclerView, item, dX, dY, actionState, isCurrentlyActive);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }
}
