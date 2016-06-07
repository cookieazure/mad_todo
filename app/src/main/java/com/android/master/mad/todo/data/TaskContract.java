package com.android.master.mad.todo.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by MISSLERT on 29.05.2016.#
 * Defines table and column name for the SQLite database.
 */
public class TaskContract{


    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.android.master.mad.todo";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    public static final String PATH_TASKS = "tasks";

    public static final class Task implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TASKS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TASKS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TASKS;

        public static final String TABLE_NAME = "tasks";

        // Name of the task
        public static final String COLUMN_NAME = "name";
        // Task description
        public static final String COLUMN_DESC = "description";
        // Task due date and time
        public static final String COLUMN_DATE = "date";
        // Indicator if task is finished
        public static final String COLUMN_DONE = "done";
        // Indicator if task is a priority
        public static final String COLUMN_FAV = "priority";
        // Associated contacts for task
        public static final String COLUMN_CONTACTS = "contacts";
//        // Associated location for task
//        public static final String COLUMN_LOC = "location";

        public static Uri buildTaskUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getTaskIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }

}
