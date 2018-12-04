package com.dontletbehind.factory;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.dontletbehind.R;
import com.dontletbehind.domain.contract.TaskContract;
import com.dontletbehind.domain.entity.TaskEntity;
import com.dontletbehind.provider.database.TaskEntityProvider;
import com.dontletbehind.util.ParserUtil;

/**
 * {@link android.widget.RemoteViewsService.RemoteViewsFactory} for
 * {@link com.dontletbehind.provider.widget.TaskListWidgetProvider}.
 *
 */
public class TaskListWidgetViewFactory implements RemoteViewsService.RemoteViewsFactory {


    private Context mContext;
    private Cursor mCursor;

    /**
     * Constructor.
     *
     * @param context to transact
     */
    public TaskListWidgetViewFactory(Context context) {
        this.mContext = context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate() {
        //do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDataSetChanged() {
        if (mCursor != null) {
            mCursor.close();
        }
        final long callerIdentity = Binder.clearCallingIdentity();
        mCursor = mContext.getContentResolver()
                .query(
                    TaskEntityProvider.CONTENT_URI,
                    TaskContract.ALL_COLUMNS,
                    null,
                    null,
                    TaskContract.COLUMN_ID + "desc"
                );
        Binder.restoreCallingIdentity(callerIdentity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCount() {
        if (mCursor != null) {
            return mCursor.getCount();
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RemoteViews getViewAt(int position) {
        if (mCursor.moveToPosition(position)) {
            RemoteViews remoteViews =
                new RemoteViews(mContext.getPackageName(), R.layout.widget_task_list_item);
            remoteViews.setTextViewText(R.id.tv_title_widget_task_list_item,
                    mCursor.getString(mCursor.getColumnIndex(TaskContract.COLUMN_TITLE)));

            Intent taskFillIntent = new Intent();
            taskFillIntent.putExtra(TaskEntity.class.getName(), ParserUtil.cursorToParcelBytes(mCursor));
            remoteViews.setOnClickFillInIntent(R.id.ll_widget_task_list_item, taskFillIntent);

            return remoteViews;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getViewTypeCount() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getItemId(int position) {
        if (mCursor.moveToPosition(position)) {
            return mCursor.getLong(mCursor.getColumnIndex(TaskContract.COLUMN_ID));
        }
        return position;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasStableIds() {
        return true;
    }
}
