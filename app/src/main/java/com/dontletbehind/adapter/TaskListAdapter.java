package com.dontletbehind.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dontletbehind.R;
import com.dontletbehind.activity.TaskDetailActivity;
import com.dontletbehind.domain.contract.TaskContract;
import com.dontletbehind.domain.entity.TaskEntity;
import com.dontletbehind.notification.TaskReminderAlarm;
import com.dontletbehind.notification.TaskReminderNotification;
import com.dontletbehind.provider.database.TaskEntityProvider;
import com.dontletbehind.util.ParserUtil;

import java.util.List;

/**
 * {@code RecyclerView} for {@code TaskList} in {@link com.dontletbehind.activity.TaskListActivity}.
 */
public class TaskListAdapter extends android.support.v7.widget.RecyclerView.Adapter<TaskListAdapter.TaskListAdapterViewHolder>
        implements ItemTouchHelperListener {


    /**
     * <code>ViewHolder</code> for {@link TaskListAdapter}
     */
    static class TaskListAdapterViewHolder extends RecyclerView.ViewHolder {

        final View mItemView;
        final View mItemRemoveView;
        private final TextView mTaskItemTextView;

        /**
         * Constructor.
         *
         * @param taskItemView the Task's item view.
         */
        TaskListAdapterViewHolder(View taskItemView) {
            super(taskItemView);
            this.mTaskItemTextView = taskItemView.findViewById(R.id.tv_item_title_task_list);
            this.mItemView = taskItemView.findViewById(R.id.cv_item_view);
            this.mItemRemoveView = taskItemView.findViewById(R.id.ll_item_remove_view);
        }
    }


    //data set
    private List<TaskEntity> mTaskDataSet;
    private Context mContext;


    /**
     * Constructor.
     *
     * @param context to transact
     */
    public TaskListAdapter(Context context) {
        this.mContext = context;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public TaskListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        View cv = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_task_list, viewGroup, false);
        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailIntent = new Intent(mContext, TaskDetailActivity.class);
                detailIntent.putExtra(TaskEntity.class.getName(), ParserUtil.taskToParcelBytes(mTaskDataSet.get(i)));
                mContext.startActivity(detailIntent);
            }
        });
        return new TaskListAdapterViewHolder(cv);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBindViewHolder(@NonNull TaskListAdapterViewHolder viewHolder, int i) {
        if (mTaskDataSet != null) {
            TaskEntity task = mTaskDataSet.get(i);
            viewHolder.mTaskItemTextView.setText(task.getTitle());

        } else {
            Log.e(getClass().getName(), "Task data set is null");
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getItemCount() {
        if (mTaskDataSet != null) {
            return mTaskDataSet.size();

        } else {
            return -1;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, final int position) {
        if (viewHolder instanceof TaskListAdapterViewHolder) {
            final TaskEntity task = mTaskDataSet.get(viewHolder.getAdapterPosition());
            //remove the task from database and dataset
            removeTask(task);

            //shows undo action
            Snackbar snackbar = Snackbar
                    .make(((TaskListAdapterViewHolder) viewHolder).mItemView,
                            task.getTitle() + " removed!",
                            Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //restore previous task entity
                    reInsertTask(task, position);
                }
            }).show();
        }
    }


    /**
     * Set the adapter's {@code {@link TaskEntity}} data set.
     *
     * @param taskList to set
     */
    public void setData(List<TaskEntity> taskList) {
        this.mTaskDataSet = taskList;
        notifyDataSetChanged();
    }

    /**
     * Removes the {@code TaskEntity} from app's database and adapter's dataset.
     *
     * @param taskEntity to remove
     */
    private void removeTask(TaskEntity taskEntity) {
        int affectedRows = mContext.getContentResolver().delete(
                Uri.parse(TaskEntityProvider.CONTENT_URI.toString() + "/" + taskEntity.getId()),
                TaskContract.COLUMN_ID + "=" + taskEntity.getId(),
                null);

        if (affectedRows > 0) {
            int position = mTaskDataSet.indexOf(taskEntity);
            mTaskDataSet.remove(position);
            notifyItemRemoved(position);

            //cancel an existing alarm if any
            new TaskReminderAlarm(mContext).cancelAlarm(taskEntity);
            //cancel an existing notification if any
            new TaskReminderNotification(mContext).cancelNotification(taskEntity);
        }
    }

    /**
     * Restore the {@code TaskEntity} on app's database and adapter's dataset.
     *
     * This method makes a new insertion of previous {@code TaskEntity} on local database.
     *
     * @param taskEntity to restore
     * @param position previous
     */
    private void reInsertTask(TaskEntity taskEntity, int position) {
        ContentValues values = new ContentValues();
        values.put(TaskContract.COLUMN_TITLE, taskEntity.getTitle());
        values.put(TaskContract.COLUMN_DESCRIPTION, taskEntity.getDescription());
        values.put(TaskContract.COLUMN_TIMER, taskEntity.getTimer());

        Uri insert = mContext.getContentResolver().insert(
                TaskEntityProvider.CONTENT_URI,
                values
        );

        if (insert != null) {
            mTaskDataSet.add(position, taskEntity);
            notifyItemInserted(position);
        }
    }
}
