package com.dontletbehind.util;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.support.annotation.NonNull;

import com.dontletbehind.domain.contract.TaskContract;
import com.dontletbehind.domain.entity.TaskEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Util class to handle {@code Parsing} operations.
 */
public class ParserUtil {


    /**
     * Private constructor.
     */
    private ParserUtil() {}

    /**
     * Parse a {@code {@link Cursor } to an instance of {@code {@link TaskEntity}}.
     *
     * @param cursor with retrieved value
     * @return {@code TaskEntity} filled with cursor values
     */
    public static TaskEntity parseCursorToTaskEntity(Cursor cursor) {
        //extract values
        int id = cursor.getInt(
                cursor.getColumnIndex(TaskContract.COLUMN_ID));
        String title = cursor.getString(
                cursor.getColumnIndex(TaskContract.COLUMN_TITLE));
        String description = cursor.getString(
                cursor.getColumnIndex(TaskContract.COLUMN_DESCRIPTION));
        long timer = cursor.getLong(
                cursor.getColumnIndex(TaskContract.COLUMN_TIMER));

        //set entity
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(id);
        taskEntity.setTitle(title);
        taskEntity.setDescription(description);
        taskEntity.setTimer(timer);

        return taskEntity;
    }

    /**
     * Parse an instance of {@link TaskEntity} to a {@link ContentValues} instance.
     *
     * @param task to be parsed
     * @return an instance of content values.
     */
    public static ContentValues parseTasktoContentValues(@NonNull TaskEntity task) {
        ContentValues values = new ContentValues();
        values.put(TaskContract.COLUMN_ID, task.getId());
        values.put(TaskContract.COLUMN_TITLE, task.getTitle());
        values.put(TaskContract.COLUMN_DESCRIPTION, task.getDescription());
        values.put(TaskContract.COLUMN_TIMER, task.getTimer());
        return values;
    }

    /**
     * Parse an {@code byte} array representation of a {@link Parcel} of a {@link TaskEntity} to
     * a new {@code TaskEntity} instance.
     *
     * @param taskBytes representation of a {@code TaskEntity}.
     * @return a {@code TaskEntity} instance.
     */
    public static TaskEntity parcelBytesToTask(byte[] taskBytes) {
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(taskBytes, 0, taskBytes.length);
        parcel.setDataPosition(0);

        TaskEntity result = TaskEntity.CREATOR.createFromParcel(parcel);
        parcel.recycle();

        return result;
    }

    /**
     * Parse a {@link Parcel} of a {@link TaskEntity} instance to a new {@code byte}
     * array representation.
     *
     * @param taskEntity instance to be parsed
     * @return a {@code byte} array of a {@code Parcel}.
     */
    public static byte[] taskToParcelBytes(TaskEntity taskEntity) {
        Parcel parcel = Parcel.obtain();
        taskEntity.writeToParcel(parcel, 0);
        byte[] bytes = parcel.marshall();
        parcel.recycle();
        return bytes;
    }

    /**
     * Parse the {@code Cursor} result.
     *
     * @param cursor pointing to result set.
     * @return query parsed result
     */
    public static List<TaskEntity> cursorToListTaskEntity(Cursor cursor) {
        List<TaskEntity> result = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            result.add(ParserUtil.parseCursorToTaskEntity(cursor));
            cursor.moveToNext();
        }
        cursor.close();

        return result;
    }

}
