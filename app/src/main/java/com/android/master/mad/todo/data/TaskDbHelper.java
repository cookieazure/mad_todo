package com.android.master.mad.todo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.android.master.mad.todo.data.TaskContract.Task;

/**
 * Created by Cookie on 29.05.2016.
 * Database helper class.
 */
public class TaskDbHelper extends SQLiteOpenHelper {

    private final String LOG_TAG = TaskDbHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "tasks.db";

    public TaskDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_TAG, " : onCreate().");
        // Create a table to hold locations.  A location consists of the string supplied in the
        // location setting, the city name, and the latitude and longitude
        final String SQL_CREATE_TASKS_TABLE = "CREATE TABLE " + Task.TABLE_NAME + " (" +
                Task._ID + " INTEGER PRIMARY KEY, " +
                Task.COLUMN_NAME + " TEXT, " +
                Task.COLUMN_DESC + " TEXT, " +
                Task.COLUMN_DATE + " INTEGER, " +
                Task.COLUMN_DONE + " INTEGER NOT NULL, " +
                Task.COLUMN_FAV + " INTEGER NOT NULL, " +
                Task.COLUMN_CONTACTS + " TEXT " +
                " );";

        db.execSQL(SQL_CREATE_TASKS_TABLE);
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p/>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(LOG_TAG, " : onUpdate().");
        // Hard database upgrade.
        db.execSQL("DROP TABLE IF EXISTS " + Task.TABLE_NAME);
        onCreate(db);
    }
}
