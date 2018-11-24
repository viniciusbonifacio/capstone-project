package com.dontletbehind.loader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import com.dontletbehind.domain.dao.TaskDao;
import com.dontletbehind.domain.entity.TaskEntity;

import java.util.List;

public class TaskLoader extends AsyncTaskLoader<List<TaskEntity>> {


    /**
     * Constructor.
     *
     * @param context to transact
     */
    public TaskLoader(@NonNull Context context) {
        super(context);
        onContentChanged();
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Nullable
    @Override
    public List<TaskEntity> loadInBackground() {
        return new TaskDao(getContext()).findAllEntity();
    }
}
