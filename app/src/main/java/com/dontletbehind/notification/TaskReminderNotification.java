package com.dontletbehind.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.dontletbehind.R;
import com.dontletbehind.activity.TaskDetailActivity;
import com.dontletbehind.domain.entity.TaskEntity;
import com.dontletbehind.receiver.TaskReminderReceiver;
import com.dontletbehind.util.ParserUtil;

import java.util.Objects;

/**
 * Helper for handle {@code Notification} for Task Reminder feature.
 */
public class TaskReminderNotification {

    public static final String ACTION_REMOVE = "com.dontletbehind.notification.ACTION_REMOVE";
    public static final String ACTION_SET_REMINDER = "com.dontletbehind.notification.ACTION_SET_REMINDER";
    public static final String KEY_NOTIFICATION_ID = "com.dontletbehind.notification.KEY_NOTIFICATION_ID";
    private Context mContext;
    private NotificationManager mNotificationManager;

    /**
     * Constructor.
     *
     * @param context to transact.
     */
    public TaskReminderNotification(Context context) {
        this.mContext = context;
        this.mNotificationManager = createNotificationManager();

    }

    /**
     * Notify the user with a reminder for the given task.
     * <p>The notification can be tapped to show the {@link TaskDetailActivity} for the give task.
     * <p>The notification has a button to run a exclusion flow for the task if the user wants to.
     *
     * @param taskEntity to be reminded
     */
    public void notifyTaskReminder(TaskEntity taskEntity) {
        NotificationManager notificationManager = createNotificationManager();
        //cancel a previous notification if any
        notificationManager.cancel(taskEntity.getId());
        //creates a new notitication
        notificationManager.notify(taskEntity.getId(), createNotification(taskEntity));
    }

    /**
     * Creates a {@link android.support.v4.app.NotificationManagerCompat} to handle the
     * notification for the given {@code NotificationChannel}.
     *
     */
    private NotificationManager createNotificationManager() {
        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager.createNotificationChannel(Objects.requireNonNull(createChannelNotification()));
            }
        } else {
            Log.e(getClass().getName(), "NotificationManager is null.");
            Toast.makeText(mContext, "Can't create a notification channel.",
                    Toast.LENGTH_LONG).show();
        }
        return notificationManager;
    }

    /**
     * Creates a notification channel.
     */
    private NotificationChannel createChannelNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel =
                new NotificationChannel(
                    mContext.getString(R.string.notification_reminder_channel_id),
                    mContext.getString(R.string.notification_reminder_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.WHITE);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Task's reminder notification.");
            return notificationChannel;
        }
        return null;
    }

    /**
     * Creates a {@link Notification} for task reminder.
     *
     * @param taskEntity to be shown on {@code TaskDetailActivity} by an tap event.
     * @return an instance of {@code Notification}
     */
    private Notification createNotification(TaskEntity taskEntity) {
        return new NotificationCompat.Builder(mContext, mContext.getString(R.string.notification_reminder_channel_id))
            .setSmallIcon(android.R.drawable.ic_menu_recent_history)
            .setContentTitle(taskEntity.getTitle())
            .setContentText("Task Reminder: " + taskEntity.getTitle())
            .setContentIntent(createContentIntent(taskEntity))
            .setAutoCancel(true)
            .addAction(android.R.drawable.ic_menu_recent_history, "Remove Task", createRemoveTaskIntent(taskEntity))
            .setStyle(new NotificationCompat.BigTextStyle().bigText(taskEntity.getDescription()))
            .build();
    }

    /**
     * Creates a {@code ContentIntent} for a notification tap event.
     *
     * @param taskEntity that will be show on {@link TaskDetailActivity}.
     * @return pedingintent with {@code TaskEntity} in a bundle.
     */
    private PendingIntent createContentIntent(TaskEntity taskEntity) {
        Intent contentIntent = new Intent(mContext, TaskDetailActivity.class);
        contentIntent.putExtra(TaskEntity.class.getName(), ParserUtil.taskToParcelBytes(taskEntity));
        return PendingIntent.getActivity(mContext, 0,
                contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Creates a {@code PendingIntent} to remove task from database.
     *
     * @return intent for remove task.
     */
    private PendingIntent createRemoveTaskIntent(TaskEntity taskEntity) {
        Intent removeIntent = new Intent(mContext, TaskReminderReceiver.class);
        removeIntent.setAction(ACTION_REMOVE);
        removeIntent.putExtra(TaskEntity.class.getName(), ParserUtil.taskToParcelBytes(taskEntity));
        removeIntent.putExtra(KEY_NOTIFICATION_ID, taskEntity.getId());
        return PendingIntent.getBroadcast(
                mContext,
                taskEntity.getId(),
                removeIntent,
                PendingIntent.FLAG_ONE_SHOT);
    }

    /**
     * Cancel a {@link Notification} for a {@link TaskEntity} if any.
     *
     * @param taskEntity to cancel
     */
    public void cancelNotification(TaskEntity taskEntity) {
        mNotificationManager.cancel(taskEntity.getId());
    }
}
