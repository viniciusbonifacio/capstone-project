package com.dontletbehind.domain.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dontletbehind.domain.contract.TaskContract;

/**
 * Database helper.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    /**
     * Database version identifier.
     */
    public static final Integer DATABASE_VERSION = 1;

    /**
     * Datbase name.
     */
    public static final String DATABASE_NAME = "dontletbehind.db";


    /**
     * Constructor.
     *
     * @param context to transact.
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(getClass().getName(), "Creating database.");

        //must add a call for all entities on domain
        TaskContract.onCreate(db);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(getClass().getName(), "Updating database.");

        //must add a call for all entities on domain
        TaskContract.onUpgrade(db);
    }
}
