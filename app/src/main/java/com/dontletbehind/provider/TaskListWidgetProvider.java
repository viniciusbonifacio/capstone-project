package com.dontletbehind.provider;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;

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
    public void onEnabled(Context context) {
        //on enable widget
    }

    /**
     * Update the widget view and data.
     *
     * @param context to transact
     * @param appWidgetManager handle widget
     * @param widgetId app widget id to update
     */
    private void updateWidget(Context context, AppWidgetManager appWidgetManager, int widgetId) {
        //update widget
    }

}
