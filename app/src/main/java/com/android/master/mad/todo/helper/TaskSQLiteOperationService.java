package com.android.master.mad.todo.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.android.master.mad.todo.data.Task;
import com.android.master.mad.todo.data.TaskContract;
import com.android.master.mad.todo.sync.ITaskSQLiteOperations;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cookie on 04.06.2016.
 */
public class TaskSQLiteOperationService implements ITaskSQLiteOperations{

    private final String LOG_TAG = TaskSQLiteOperationService.class.getSimpleName();

    private Context context;

    public TaskSQLiteOperationService(Context context){
        this.context = context;
    }
    @Override
    public Cursor read(long id) {
        return context.getContentResolver().query(TaskContract.Task.buildTaskUri(id), null, null, null, null);
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
    public int bulkInsert(List<Task> tasks) {
        ArrayList<ContentValues> bulkValues = new ArrayList<>();
        for (Task task : tasks) {
            ContentValues taskValue = generateContentValues(task);
            bulkValues.add(taskValue);
        }
        return context.getContentResolver().bulkInsert(TaskContract.Task.CONTENT_URI, bulkValues.toArray(new ContentValues[bulkValues.size()]));
    }

    @Override
    public int update(long id, Task task) {
        ContentValues newValues = generateContentValues(task);
        return context.getContentResolver().update(TaskContract.Task.buildTaskUri(id), newValues, null, null);
    }

    @Override
    public int delete(long id) {
        return context.getContentResolver().delete(TaskContract.Task.buildTaskUri(id), null, null);
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
