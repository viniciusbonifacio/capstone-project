package com.dontletbehind.provider.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.dontletbehind.R;
import com.dontletbehind.activity.TaskDetailActivity;
import com.dontletbehind.service.TaskListWidgetRemoteViewService;

/**
 * Widget provider for {@code TaskListWidget}.
 *
 */
public class TaskListWidgetProvider extends AppWidgetProvider {

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int widgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, widgetId);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null) {

            if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {

                ComponentName taskListWidgetComponent =
                        new ComponentName(context, TaskListWidgetProvider.class);

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                appWidgetManager.notifyAppWidgetViewDataChanged(
                        appWidgetManager.getAppWidgetIds(taskListWidgetComponent), R.id.lv_task_list);
            }
        }
        super.onReceive(context, intent);
    }

    /**
     * Update the widget view and data.
     *
     * @param context to transact
     * @param appWidgetManager handle widget
     * @param widgetId app widget id to update
     */
    private void updateWidget(Context context, AppWidgetManager appWidgetManager, int widgetId) {
        Intent remoteViewServiceIntent =
                new Intent(context, TaskListWidgetRemoteViewService.class);
        RemoteViews remoteView =
                new RemoteViews(context.getPackageName(), R.layout.widget_task_list);
        remoteView.setRemoteAdapter(R.id.lv_task_list, remoteViewServiceIntent);
        remoteView.setPendingIntentTemplate(R.id.lv_task_list, createPendingIntent(context));

        appWidgetManager.updateAppWidget(widgetId, remoteView);
    }

    /**
     * Creates a {@code PendingIntent} to call {@link TaskDetailActivity}.
     *
     * @param context to transact
     * @return pending intent calling {@code TaskDetailActivity}
     */
    private PendingIntent createPendingIntent(Context context) {
        Intent taskDetailIntent = new Intent(context, TaskDetailActivity.class);
        return TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(taskDetailIntent)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Updates all instances of {@code {@link TaskListWidgetProvider}}.
     *
     * @param context to transact
     */
    public static void updateTaskListWidget(Context context) {
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.setComponent(new ComponentName(context, TaskListWidgetProvider.class));
        context.sendBroadcast(intent);
    }

}
