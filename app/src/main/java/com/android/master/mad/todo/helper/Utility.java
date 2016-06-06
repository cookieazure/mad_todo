package com.android.master.mad.todo.helper;

import android.database.Cursor;
import android.util.Log;

import com.android.master.mad.todo.data.Task;
import com.android.master.mad.todo.data.TaskContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cookie on 29.05.2016.
 * Basic utility class.
 */
public class Utility {

    private static final String LOG_TAG = Utility.class.getSimpleName();

    public static List<Task> createTasksFromCursor(Cursor cursor){
        Log.v(LOG_TAG, " : createTasksFromCursor");
        ArrayList<Task> tasks = new ArrayList<>();
        Task task;
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            task = new Task();
            task.setId(cursor.getLong(cursor.getColumnIndex(TaskContract.Task._ID)));
            task.setName(cursor.getString(cursor.getColumnIndex(TaskContract.Task.COLUMN_NAME)));
            task.setDescription(cursor.getString(cursor.getColumnIndex(TaskContract.Task.COLUMN_DESC)));
            task.setExpiry(cursor.getLong(cursor.getColumnIndex(TaskContract.Task.COLUMN_DATE)));
            task.setDone(cursor.getInt(cursor.getColumnIndex(TaskContract.Task.COLUMN_DONE)) != 0);
            task.setFavourite(cursor.getInt(cursor.getColumnIndex(TaskContract.Task.COLUMN_FAV)) != 0);
            task.setSimpleContacts(cursor.getString(cursor.getColumnIndex(TaskContract.Task.COLUMN_CONTACTS)));
            tasks.add(task);
            Log.v(LOG_TAG, " : create Task from cursor: " + task.toString());
            cursor.moveToNext();

        }
        return tasks;
    }

}
