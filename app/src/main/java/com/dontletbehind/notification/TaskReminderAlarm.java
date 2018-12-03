package com.dontletbehind.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.dontletbehind.domain.entity.TaskEntity;
import com.dontletbehind.receiver.TaskReminderReceiver;
import com.dontletbehind.util.ParserUtil;

/**
 * Handles the {@code AlarmManager}
 */
public class TaskReminderAlarm {


    private Context mContext;
    private AlarmManager mAlarmManager;

    /**
     * Constructor.
     * 
     * @param mContext to transact
     */
    public TaskReminderAlarm(Context mContext) {
        this.mContext = mContext;
        this.mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
    }
    
    /**
     * Creates an {@code Alarm} event to set a {@code Reminder} for a
     * {@link com.dontletbehind.domain.entity.TaskEntity}.
     *
     */
    public void setTaskReminder(TaskEntity taskEntity) {
        if (mAlarmManager != null) {
            PendingIntent alarmPendingIntent = getAlarmPendinIntent(taskEntity);

            //cancel previous alarm if any
            cancelAlarm(taskEntity);

            //sets a new inexact repeating alarm
            mAlarmManager.setInexactRepeating(
                    AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + taskEntity.getTimer(),
                    taskEntity.getTimer(),
                    alarmPendingIntent);
            
        } else {
            Log.e(TaskReminderAlarm.class.getName(), "Cannot set reminder: AlarmManager is null.");
            Toast.makeText(mContext, "Cannot set reminder: AlarmManager is null.", 
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Creates a new {@link PendingIntent} to remind of a {@link TaskEntity}
     *
     * @param taskEntity to remind
     * @return alarm reminder pending intent
     */
    private PendingIntent getAlarmPendinIntent(TaskEntity taskEntity) {
        return PendingIntent.getBroadcast(mContext, taskEntity.getId(),
                getAlarmIntent(taskEntity), PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Creates a new {@link Intent} to set in {@link AlarmManager} to remind
     * of a {@link TaskEntity}.
     *
     * @param taskEntity to remind
     * @return alarm reminder intent
     */
    private Intent getAlarmIntent(TaskEntity taskEntity) {
        Intent alarmIntent = new Intent(mContext, TaskReminderReceiver.class);
        alarmIntent.setAction(TaskReminderNotification.ACTION_SET_REMINDER);
        alarmIntent.putExtra(TaskEntity.class.getName(), ParserUtil.taskToParcelBytes(taskEntity));

        return alarmIntent;
    }

    /**
     * Cancel a previous {@code Alarm} if any.
     *
     * @param taskEntity to remind
     */
    public void cancelAlarm(TaskEntity taskEntity) {
        mAlarmManager.cancel(getAlarmPendinIntent(taskEntity));
    }

}
