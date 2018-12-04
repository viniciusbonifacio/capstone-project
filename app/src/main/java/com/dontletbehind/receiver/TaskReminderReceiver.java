package com.dontletbehind.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.dontletbehind.activity.TaskRemoveActionAsyncTask;
import com.dontletbehind.domain.entity.TaskEntity;
import com.dontletbehind.notification.TaskReminderNotification;
import com.dontletbehind.util.ParserUtil;

/**
 * A {@link BroadcastReceiver} to manager the {@code Alarm} event.
 */
public class TaskReminderReceiver extends BroadcastReceiver {

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        TaskEntity result =
                ParserUtil.bytesToTask(intent.getByteArrayExtra(TaskEntity.class.getName()));

        if (result != null) {
            checkIntentAction(context, result, intent);

        } else {
            Log.e(getClass().getName(), "Can't set a Reminder Notification: Task is null!");
            Toast.makeText(context, "Can't set a Reminder Notification: Task is null!",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Check which action to trigger.
     *
     * @param context to transact
     * @param taskEntity to transact
     * @param intent  received intent
     */
    private void checkIntentAction(@NonNull Context context, @NonNull TaskEntity taskEntity, @NonNull Intent intent) {
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case TaskReminderNotification.ACTION_REMOVE:
                    removeTask(context, taskEntity);
                    break;
                case TaskReminderNotification.ACTION_SET_REMINDER:
                    setReminderNotification(context, taskEntity);
                    break;
            }

            dismissNotification(context, intent);
        }
    }

    /**
     * Set a {@link android.app.Notification} to remind the user about a {@link TaskEntity}.
     *
     * @param context    to transact
     * @param taskEntity to set on the reminder {@link android.app.PendingIntent}
     */
    private void setReminderNotification(@NonNull Context context, @NonNull TaskEntity taskEntity) {
        TaskReminderNotification notification = new TaskReminderNotification(context);
        notification.notifyTaskReminder(taskEntity);
    }


    /**
     * Handles the remove {@link TaskEntity} {@code Notification} action.
     *
     * @param context    to transact
     * @param taskEntity to remove
     */
    private void removeTask(@NonNull Context context, @NonNull TaskEntity taskEntity) {
        new TaskRemoveActionAsyncTask(context).execute(taskEntity.getId());
    }

    /**
     * Dismiss a notification by id.
     *
     * @param context to transact
     * @param intent received intent
     * @see TaskReminderNotification
     */
    private void dismissNotification(@NonNull Context context, @NonNull Intent intent) {
        int notificationId =
                intent.getIntExtra(TaskReminderNotification.KEY_NOTIFICATION_ID, -1);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancel(notificationId);
        }
    }

}
