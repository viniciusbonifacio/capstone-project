package com.dontletbehind.domain.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dontletbehind.domain.contract.TaskContract;
import com.dontletbehind.domain.database.DatabaseConnectionHandler;
import com.dontletbehind.domain.entity.TaskEntity;
import com.dontletbehind.util.ParserUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Database access class to {@code {@link com.dontletbehind.domain.entity.TaskEntity}}.
 */
public class TaskDao {

    private final Context mContext;
    private SQLiteDatabase mDatabase;

    /**
     * Constructor.
     *
     * @param context to transact.
     */
    public TaskDao(Context context) {
        this.mContext = context;
    }

    /**
     * Insert a {@code {@link TaskEntity}} record on database.
     *
     * @param task to record
     * @return task entity with id
     */
    public TaskEntity insert(TaskEntity task) {
        //entity parse
        ContentValues values = ParserUtil.taskToContentValues(task);
        return insert(values);
    }


    /**
     * Insert a {@code {@link TaskEntity}} record on database.
     *
     * @param values to record
     * @return task entity with id
     */
    public TaskEntity insert(ContentValues values) {
        TaskEntity task;//open connection
        mDatabase = DatabaseConnectionHandler.getInstance(mContext).openConnection();

        //record id
        long taskId = mDatabase.insert(TaskContract.TABLE_NAME, null, values);

        //record pointer
        Cursor cursor = mDatabase.query(
                TaskContract.TABLE_NAME,
                TaskContract.ALL_COLUMNS,
                TaskContract.COLUMN_ID + " = " + taskId,
                null,
                null,
                null,
                null
        );

        //retrieve the record
        cursor.moveToFirst();
        task = ParserUtil.cursorToTaskEntity(cursor);
        cursor.close();

        //close connection
        DatabaseConnectionHandler.getInstance(mContext).closeConnection();

        return task;
    }

    /**
     * Removes the {@code {@link TaskEntity}} record.
     *
     * @param taskEntity to remove
     * @return number of affected records
     */
    public int delete(TaskEntity taskEntity) {
        int id = taskEntity.getId();

        //open connection
        mDatabase = DatabaseConnectionHandler.getInstance(mContext).openConnection();

        //remove the record
        int countRemoved = mDatabase.delete(
                TaskContract.TABLE_NAME,
                TaskContract.COLUMN_ID + " = " + id,
                null
        );

        //close connection
        DatabaseConnectionHandler.getInstance(mContext).closeConnection();

        return countRemoved;
    }

    /**
     * Update the {@code {@link TaskEntity}} referred by ID.
     *
     * @param task with values to update
     * @return number of affected records
     */
    public int update(TaskEntity task) {
        //entity parse
        ContentValues values = new ContentValues();
        values.put(TaskContract.COLUMN_ID, task.getId());
        values.put(TaskContract.COLUMN_TITLE, task.getTitle());
        values.put(TaskContract.COLUMN_DESCRIPTION, task.getDescription());
        values.put(TaskContract.COLUMN_TIMER, task.getTimer());

        return update(values);
    }

    /**
     * Update the {@code {@link TaskEntity}} referred by ID.
     *
     * @param contentValues with values to update
     * @return number of affected records
     */
    public int update(ContentValues contentValues) {
        //open connection
        mDatabase = DatabaseConnectionHandler.getInstance(mContext).openConnection();

        //update the record
        int affectedRecords = mDatabase.update(
                TaskContract.TABLE_NAME,
                contentValues,
                TaskContract.COLUMN_ID + "=" + contentValues.get(TaskContract.COLUMN_ID),
                null
        );

        //close connection
        DatabaseConnectionHandler.getInstance(mContext).closeConnection();

        return affectedRecords;
    }

    /**
     * Retrieves all the {@code {@link TaskEntity}} record from database.
     *
     * @return cursor for retrieved values
     */
    public Cursor findAllCursor() {

        //open connection
        mDatabase = DatabaseConnectionHandler.getInstance(mContext).openConnection();

        return mDatabase.query(
                TaskContract.TABLE_NAME,
                TaskContract.ALL_COLUMNS,
                null,
                null,
                null,
                null,
                null
        );
    }

    /**
     * Retrieves all the {@code {@link TaskEntity}} record from database.
     *
     * @return a list of all the retrieved records.
     */
    public List<TaskEntity> findAllEntity() {
        List<TaskEntity> result = new ArrayList<>();

        //search
        Cursor cursor = findAllCursor();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            result.add(ParserUtil.cursorToTaskEntity(cursor));
            cursor.moveToNext();
        }
        cursor.close();

        //close connection
        DatabaseConnectionHandler.getInstance(mContext).closeConnection();

        return result;
    }

    /**
     * Retrieve a {@code {@link TaskEntity}} by an ID.
     *
     * @param taskId to find
     * @return {@code {@link TaskEntity}} found.
     */
    public TaskEntity findByIdEntity(int taskId) {
        //open connection
        mDatabase = DatabaseConnectionHandler.getInstance(mContext).openConnection();

        //retrieve the record
        Cursor cursor = findByIdCursor(taskId);

        //parse result
        cursor.moveToFirst();
        TaskEntity task = ParserUtil.cursorToTaskEntity(cursor);
        cursor.close();

        //close connection
        DatabaseConnectionHandler.getInstance(mContext).closeConnection();

        return task;
    }

    /**
     * Retrieve a {@code {@link TaskEntity}} by an ID.
     *
     * @param taskId to find
     * @return {@code {@link Cursor}} pointing to result
     */
    public Cursor findByIdCursor(int taskId) {
        return mDatabase.query(
                TaskContract.TABLE_NAME,
                TaskContract.ALL_COLUMNS,
                TaskContract.COLUMN_ID + " = " + taskId,
                null,
                null,
                null,
                null
        );
    }

}
