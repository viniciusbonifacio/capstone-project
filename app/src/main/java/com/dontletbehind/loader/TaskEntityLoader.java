package com.dontletbehind.loader;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import com.dontletbehind.domain.contract.TaskContract;
import com.dontletbehind.domain.entity.TaskEntity;
import com.dontletbehind.provider.database.TaskEntityProvider;
import com.dontletbehind.util.ParserUtil;

import java.util.List;
import java.util.Objects;

/**
 * Data {@link android.support.v4.content.Loader} for {@link TaskEntity}.
 */
public class TaskEntityLoader extends AsyncTaskLoader<List<TaskEntity>> {

    private Bundle mArgs;

    /**
     * Constructor.
     *
     * @param context to transact
     */
    public TaskEntityLoader(@NonNull Context context, Bundle args) {
        super(context);
        this.mArgs = args;
        onContentChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public List<TaskEntity> loadInBackground() {
        TaskEntity taskEntity = new TaskEntity();
        if (mArgs != null && mArgs.getParcelable(TaskEntity.class.getName()) != null) {
            taskEntity = mArgs.getParcelable(TaskEntity.class.getName());
        }

        Cursor cursor = getContext().getContentResolver().query(
                TaskEntityProvider.CONTENT_URI,
                TaskContract.ALL_COLUMNS,
                taskEntity != null ? TaskContract.COLUMN_ID + "=" + taskEntity.getId() : null,
                null,
                null);

        return ParserUtil.cursorToListTaskEntity(Objects.requireNonNull(cursor));
    }

}
