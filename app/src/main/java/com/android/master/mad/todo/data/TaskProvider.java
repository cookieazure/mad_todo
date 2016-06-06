package com.android.master.mad.todo.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Cookie on 29.05.2016.
 * Content provider for tasks.
 */
public class TaskProvider extends ContentProvider{

    private final String LOG_TAG = TaskProvider.class.getSimpleName();

    // The URI Matcher used by this content provider.
    private static final UriMatcher uriMatcher = buildUriMatcher();
    private TaskDbHelper dbHelper;

    static final int TASKS = 100;
    static final int SINGLE_TASK = 200;

    //location.location_setting = ?
    private static final String idSelection =
            TaskContract.Task.TABLE_NAME+"." +  TaskContract.Task._ID + " = ? ";

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = TaskContract.CONTENT_AUTHORITY;

        // For each type of URI...
        matcher.addURI(authority, TaskContract.PATH_TASKS, TASKS);
        matcher.addURI(authority, TaskContract.PATH_TASKS + "/#", SINGLE_TASK);
        return matcher;
    }

    /**
     * Initialize your content provider on startup.
     *
     * @return true if the provider was successfully loaded, false otherwise
     */
    @Override
    public boolean onCreate() {
        Log.v(LOG_TAG, ": onCreate()");

        dbHelper = new TaskDbHelper(getContext());
        return true;
    }

    /**
     * Handle requests for the MIME type of the data at the given URI.
     *
     * @param uri the URI to query.
     *
     * @return a MIME type string, or {@code null} if there is no type.
     */
    @Nullable
    @Override
    public String getType(Uri uri) {
        Log.v(LOG_TAG, ": getType().");

        final int match = uriMatcher.match(uri);
        switch (match){
            case TASKS:
                return TaskContract.Task.CONTENT_TYPE;
            case SINGLE_TASK:
                return TaskContract.Task.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown URI: "+ uri);
        }
    }

    /**
     * Handle query requests from clients.
     *
     * @param uri           The URI to query. This will be the full URI sent by the client;
     *                      if the client is requesting a specific record, the URI will end in a record number
     *                      that the implementation should parse and add to a WHERE or HAVING clause, specifying
     *                      that _id value.
     * @param projection    The list of columns to put into the cursor. If
     *                      {@code null} all columns are included.
     * @param selection     A selection criteria to apply when filtering rows.
     *                      If {@code null} then all rows are included.
     * @param selectionArgs You may include ?s in selection, which will be replaced by
     *                      the values from selectionArgs, in order that they appear in the selection.
     *                      The values will be bound as Strings.
     * @param sortOrder     How the rows in the cursor should be sorted.
     *                      If {@code null} then the provider is free to define the sort order.
     *
     * @return a Cursor or {@code null}.
     */
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.v(LOG_TAG, ": query().");

        Cursor result;

        switch (uriMatcher.match(uri)){
            case TASKS:
                Log.v(LOG_TAG, ": Query TASKS.");

                result = dbHelper.getReadableDatabase().query(
                        TaskContract.Task.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case SINGLE_TASK:
                Log.v(LOG_TAG, ": Query SINGLE_TASK.");
                long id = TaskContract.Task.getTaskIdFromUri(uri);
                result = dbHelper.getReadableDatabase().query(
                        TaskContract.Task.TABLE_NAME,
                        projection,
                        idSelection,
                        new String[]{Long.toString(id)},
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: "+ uri);
        }
        result.setNotificationUri(getContext().getContentResolver(), uri);
        return result;
    }

    /**
     * Handle requests to insert a new row.
     *
     * @param uri    The content:// URI of the insertion request. This must not be {@code null}.
     * @param values A set of column_name/value pairs to add to the database.
     *               This must not be {@code null}.
     *
     * @return The URI for the newly inserted item.
     */
    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.v(LOG_TAG, ": insert().");

        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case TASKS: {
                long _id = db.insert(TaskContract.Task.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = TaskContract.Task.buildTaskUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    /**
     * Handle requests to insert a new row.
     *
     * @param uri    The content:// URI of the insertion request. This must not be {@code null}.
     * @param values A set of column_name/value pairs to add to the database.
     *               This must not be {@code null}.
     *
     * @return The URI for the newly inserted item.
     */
    @Nullable
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        Log.v(LOG_TAG, ": bulkInsert().");

        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);

        switch (match) {
            case TASKS: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(TaskContract.Task.TABLE_NAME, null, value);
                        if (_id != -1){
                            returnCount++;
                        }
                    }
                } finally {
                    db.setTransactionSuccessful();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            default:
                return super.bulkInsert(uri, values);
        }
    }

    /**
     * Handle requests to delete one rows.
     *
     * @param uri           The full URI to query, including a row ID (if a specific record is requested).
     * @param selection     An optional restriction to apply to rows when deleting.
     * @param selectionArgs Arguments for selection.
     *
     * @return The number of rows affected.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.v(LOG_TAG, ": delete().");

        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsDeleted;

        switch (match) {
            case SINGLE_TASK:
                long id = TaskContract.Task.getTaskIdFromUri(uri);
                rowsDeleted = db.delete(
                        TaskContract.Task.TABLE_NAME,
                        idSelection,
                        new String[]{Long.toString(id)});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    /**
     * IHandle requests to update one row.
     *
     * @param uri           The URI to query. This can potentially have a record ID if this
     *                      is an update request for a specific record.
     * @param values        A set of column_name/value pairs to update in the database.
     *                      This must not be {@code null}.
     * @param selection     An optional filter to match rows to update.
     * @param selectionArgs Arguments for selection.
     *
     * @return the number of rows affected.
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.v(LOG_TAG, ": update()");

        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case SINGLE_TASK:
                long id = TaskContract.Task.getTaskIdFromUri(uri);
                rowsUpdated = db.update(TaskContract.Task.TABLE_NAME,
                        values,
                        idSelection,
                        new String[]{Long.toString(id)});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
