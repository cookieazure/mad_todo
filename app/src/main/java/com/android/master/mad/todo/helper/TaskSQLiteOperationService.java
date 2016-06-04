package com.android.master.mad.todo.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.android.master.mad.todo.data.Task;
import com.android.master.mad.todo.data.TaskContract;
import com.android.master.mad.todo.sync.ITaskSQLiteOperations;

/**
 * Created by Cookie on 04.06.2016.
 */
public class TaskSQLiteOperationService implements ITaskSQLiteOperations{

    Context context;

    public TaskSQLiteOperationService(Context context){
        this.context = context;
    }
    @Override
    public Task read(long id) {
        return null;
    }

    @Override
    public Cursor readAll() {
        return context.getContentResolver().query(TaskContract.Task.CONTENT_URI, null, null, null, null);
    }

    @Override
    public Uri insert(Task task) {
        ContentValues newValues = generateContentValues(task);
        return context.getContentResolver().insert(TaskContract.Task.CONTENT_URI, newValues);
    }

    @Override
    public Task update(long id, Task task) {
        return null;
    }

    @Override
    public Boolean delete(long id) {
        return null;
    }

    private ContentValues generateContentValues(Task task){
        ContentValues values = new ContentValues();

        values.put(TaskContract.Task.COLUMN_NAME, task.getName());
        values.put(TaskContract.Task.COLUMN_DESC, task.getDescription());
        values.put(TaskContract.Task.COLUMN_DATE, task.getExpiry());
        values.put(TaskContract.Task.COLUMN_DONE, task.isDone());
        values.put(TaskContract.Task.COLUMN_FAV, task.isFavourite());
        values.put(TaskContract.Task.COLUMN_CONTACTS, task.getSimpleContacts());
        return values;
    }
}
