package com.android.master.mad.todo.sync;

import android.database.Cursor;
import android.net.Uri;

import com.android.master.mad.todo.data.Task;

/**
 * Created by Cookie on 04.06.2016.
 * Interface for simple operations on SQLite database.
 */
public interface ITaskSQLiteOperations {

    Task read(long id);

    Cursor readAll();

    Uri insert(Task task);

    Task update(long id, Task task);

    Boolean delete(long id);

}
