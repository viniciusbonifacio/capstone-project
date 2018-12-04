package com.dontletbehind.service;

import android.content.Intent;
import android.widget.RemoteViewsService;

import com.dontletbehind.factory.TaskListWidgetViewFactory;

/**
 * Serves the apropriated {@code {@link android.widget.RemoteViewsService.RemoteViewsFactory}}
 * for {@link com.dontletbehind.provider.widget.TaskListWidgetProvider}.
 *
 */
public class TaskListWidgetRemoteViewService extends RemoteViewsService {

    /**
     * {@inheritDoc}
     */
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new TaskListWidgetViewFactory(getApplicationContext());
    }
}
