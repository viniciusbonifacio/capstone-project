package com.dontletbehind.provider.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.dontletbehind.R;
import com.dontletbehind.activity.TaskDetailActivity;

/**
 * Widget provider for {@code NewTaskWidget}.
 *
 */
public class NewTaskWidgetProvider extends AppWidgetProvider {


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
     * Update the widget view.
     *
     * @param context          to transact
     * @param appWidgetManager handle widget
     * @param widgetId         app widget id to update
     */
    private void updateWidget(Context context, AppWidgetManager appWidgetManager, int widgetId) {
        RemoteViews remoteView =
                new RemoteViews(context.getPackageName(), R.layout.widget_button_shortcut);
        remoteView.setOnClickPendingIntent(R.id.btn_widget_shortcut, createPendingIntent(context));
        appWidgetManager.updateAppWidget(widgetId, remoteView);
    }

    /**
     * Creates a {@code PendingIntent} to call {@link TaskDetailActivity}.
     *
     * @param context to transact
     * @return pending intent calling {@code TaskDetailActivity}
     */
    private PendingIntent createPendingIntent(Context context) {
        Intent newTaskIntent = new Intent(context, TaskDetailActivity.class);
        return PendingIntent.getActivity(context, 0, newTaskIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
