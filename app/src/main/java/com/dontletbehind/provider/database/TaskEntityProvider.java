package com.dontletbehind.provider.database;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dontletbehind.domain.contract.TaskContract;
import com.dontletbehind.domain.dao.TaskDao;
import com.dontletbehind.domain.entity.TaskEntity;

import java.util.Objects;

/**
 * Provides {@code {@link com.dontletbehind.domain.entity.TaskEntity}} content stored
 * in local database.
 */
public class TaskEntityProvider extends ContentProvider {

    private static final int TASK_ID = 10;
    private static final int ALL_TASKS = 20;

    private static final String AUTHORITY = "com.dontletbehind.contentprovider";
    private static final String CONTENT_PATH = "task";

    public static final Uri CONTENT_URI = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + AUTHORITY + "/" + CONTENT_PATH);
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + AUTHORITY + "." + CONTENT_PATH;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + AUTHORITY + "." + CONTENT_PATH;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(AUTHORITY, CONTENT_PATH, ALL_TASKS);
        sUriMatcher.addURI(AUTHORITY, CONTENT_PATH + "/#", TASK_ID);
    }

    private TaskDao mTaskDao;


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreate() {
        this.mTaskDao = new TaskDao(getContext());
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TaskContract.TABLE_NAME);

        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case ALL_TASKS:
                cursor = mTaskDao.findAllCursor();
                break;

            case TASK_ID:
                cursor = mTaskDao.findByIdCursor(Integer.valueOf(uri.getLastPathSegment()));
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        //notify listeners
        if (getContext() != null && getContext().getContentResolver() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursor;
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        switch (sUriMatcher.match(uri)) {
            case TASK_ID:
                return CONTENT_ITEM_TYPE;

            case ALL_TASKS:
                return CONTENT_TYPE;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int id;
        switch (sUriMatcher.match(uri)) {
            case ALL_TASKS:
                TaskEntity task = mTaskDao.insert(values);
                id = task.getId();
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        //notify listeners
        if (getContext() != null && getContext().getContentResolver() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return Uri.parse(CONTENT_URI + "/" + id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int affectedRows = 0;
        switch (sUriMatcher.match(uri)) {
            case ALL_TASKS:
                for (TaskEntity task : mTaskDao.findAllEntity()) {
                    mTaskDao.delete(task);
                    affectedRows++;
                }
                break;

            case TASK_ID:
                Integer id = Integer.valueOf(Objects.requireNonNull(uri.getLastPathSegment()));
                TaskEntity task = mTaskDao.findByIdEntity(id);
                affectedRows = mTaskDao.delete(task);
                break;

            default:
                throw new IllegalArgumentException("URI desconhecida: " + uri);

        }

        //notify listeners
        if (getContext() != null && getContext().getContentResolver() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return affectedRows;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int affectedRows = 0;
        switch (sUriMatcher.match(uri)) {
            case ALL_TASKS:
                for (TaskEntity task : mTaskDao.findAllEntity()) {
                    task.setTitle(values.getAsString(TaskContract.COLUMN_TITLE));
                    task.setDescription(values.getAsString(TaskContract.COLUMN_DESCRIPTION));
                    task.setTimer(values.getAsLong(TaskContract.COLUMN_TIMER));

                    mTaskDao.update(task);
                    affectedRows++;
                }
                break;

            case TASK_ID:
                affectedRows = mTaskDao.update(values);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return affectedRows;
    }
}
