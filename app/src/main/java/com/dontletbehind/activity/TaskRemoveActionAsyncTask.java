package com.dontletbehind.activity;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.dontletbehind.adapter.TaskListAdapter;
import com.dontletbehind.domain.contract.TaskContract;
import com.dontletbehind.domain.entity.TaskEntity;
import com.dontletbehind.notification.TaskReminderAlarm;
import com.dontletbehind.notification.TaskReminderNotification;
import com.dontletbehind.provider.database.TaskEntityProvider;
import com.dontletbehind.provider.widget.TaskListWidgetProvider;
import com.dontletbehind.util.ParserUtil;

import java.lang.ref.WeakReference;

/**
 * Removes a {@link TaskEntity} in a background transaction.
 */
public class TaskRemoveActionAsyncTask extends AsyncTask<Integer, Void, Integer> {

    private final WeakReference<Context> mContext;

    /**
     * Constructor.
     *
     * @param context to transact.
     */
    public TaskRemoveActionAsyncTask(Context context) {
        this.mContext = new WeakReference<>(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Integer doInBackground(Integer... integers) {
        if (mContext.get() != null && integers[0] != null) {
            Cursor cursor = mContext.get().getContentResolver()
                    .query(
                            Uri.parse(TaskEntityProvider.CONTENT_URI.toString() + "/" + integers[0]),
                            TaskContract.ALL_COLUMNS,
                            TaskContract.COLUMN_ID + "=" + integers[0],
                            null,
                            null
                    );

            if (cursor != null) {
                TaskEntity taskEntity = ParserUtil.cursorToTaskEntity(cursor);
                Integer affectedRow = mContext.get().getContentResolver()
                        .delete(
                            Uri.parse(TaskEntityProvider.CONTENT_URI.toString() + "/" + taskEntity),
                            TaskContract.COLUMN_ID + "=" + integers[0],
                            null);

                //cancel a previous alarm if any
                new TaskReminderAlarm(mContext.get()).cancelAlarm(taskEntity);
                //cancel a previous notification if any
                new TaskReminderNotification(mContext.get()).cancelNotification(taskEntity);
                //notify adapter
                new TaskListAdapter(mContext.get()).notifyDataSetChanged();
                //notify task list widget
                TaskListWidgetProvider.updateTaskListWidget(mContext.get());

                return affectedRow;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPostExecute(Integer integer) {
        if (mContext.get() != null) {
            if (integer != null) {
                Toast.makeText(mContext.get(), "Task removed successfuly.", Toast.LENGTH_SHORT).show();

            } else {
                Log.e(TaskRemoveActionAsyncTask.class.getName(), "Cannot remove task.");
                Toast.makeText(mContext.get(), "Cannot remove task.", Toast.LENGTH_LONG).show();
            }
        }
    }

}
