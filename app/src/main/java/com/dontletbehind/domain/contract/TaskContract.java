package com.dontletbehind.domain.contract;

import android.database.sqlite.SQLiteDatabase;

/**
 * Database model contract for {@link com.dontletbehind.domain.entity.TaskEntity}.
 */
public class TaskContract {

    /**
     * Task table identifier.
     */
    public static final String TABLE_NAME = "task";

    /**
     * Column id identifier.
     */
    public static final String COLUMN_ID = "_id";

    /**
     * Column title identifier.
     */
    public static final String COLUMN_TITLE = "title";

    /**
     * Column description identifier.
     */
    public static final String COLUMN_DESCRIPTION = "description";

    /**
     * Column timer identifier.
     */
    public static final String COLUMN_TIMER = "timer";

    /**
     * All columns identifiers for table {@code task}.
     */
    public static final String[] ALL_COLUMNS =
            {COLUMN_ID, COLUMN_TITLE, COLUMN_DESCRIPTION, COLUMN_TIMER};

    /**
     * Create table DDL.
     */
    private static final String CREATE_TABLE_TASK =
            "create table " +
                    TABLE_NAME +
                    "(" +
                        COLUMN_ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_TITLE + " TEXT NOT NULL, " +
                        COLUMN_DESCRIPTION + " TEXT, " +
                        COLUMN_TIMER + " INTEGER" +
                    ");";

    /**
     * Drop table DDL.
     */
    private static final String DROP_TABLE_TASK =
            "drop table if exists " + TABLE_NAME + ";";

    /**
     * Executes the DDL for create {@link com.dontletbehind.domain.entity.TaskEntity} table.
     *
     * @param database instance
     */
    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_TASK);
//        database.execSQL("INSERT INTO task values(null, 'First task', 'First task to test insert manually', '12316864');");
    }

    /**
     * Executes the DDL for drop {@link com.dontletbehind.domain.entity.TaskEntity} table.
     *
     * @param database instance
     */
    public static void onUpgrade(SQLiteDatabase database) {
        database.execSQL(DROP_TABLE_TASK);
        onCreate(database);
    }
}
