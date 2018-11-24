package com.dontletbehind.domain.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.concurrent.atomic.AtomicInteger;

/*
 * This class follows the design suggested by Dmytro Danylyk
 * to avoid crashes due to concurrent access to local app's database.
 *
 * {@see https://dmytrodanylyk.com/articles/concurrent-database}
 */

/**
 * App's database connection handler.
 *
 */
public class DatabaseConnectionHandler {

    private AtomicInteger mCounter;

    private static DatabaseConnectionHandler sConnHandler;
    private static DatabaseHelper sDBHelper;
    private SQLiteDatabase mDatabase;

    /*
     * Private constructor.
     */
    private DatabaseConnectionHandler() {
        mCounter = new AtomicInteger();
    }

    /*
     * Initiates this connection handler context.
     *
     * @param context to transact
     */
    private static synchronized void init(Context context) {
        if (sConnHandler == null) {
            sConnHandler = new DatabaseConnectionHandler();
            sDBHelper = new DatabaseHelper(context);
        }
    }

    /**
     * Retrieves a {@link DatabaseConnectionHandler} instance.
     *
     * @param context to transact
     * @return {@code {@link DatabaseConnectionHandler}} instance
     */
    public static DatabaseConnectionHandler getInstance(Context context) {
        if (sConnHandler == null) {

            synchronized (DatabaseConnectionHandler.class) {
                if (sConnHandler == null) {
                    init(context);
                }
            }
        }

        return sConnHandler;
    }

    /**
     * Retrieves an instance of app's {@link SQLiteDatabase} in writable mode.
     *
     * @return {@code {@link SQLiteDatabase}} in writable mode.
     */
    public SQLiteDatabase openConnection() {
        if (mCounter.incrementAndGet() == 1) {
            mDatabase = sDBHelper.getWritableDatabase();
        }
       return mDatabase;
    }

    /**
     * Close an instance of app's {@link SQLiteDatabase}.
     *
     * @return {@code {@link SQLiteDatabase}} in writable mode.
     */
    public void closeConnection() {
        if (mCounter.decrementAndGet() == 0) {
            sDBHelper.close();
        }
    }
}
