package com.android.master.mad.todo.sync;

import android.database.Cursor;
import android.net.Uri;

import com.android.master.mad.todo.data.Task;

import java.util.List;

/**
 * Created by MISSLERT on 04.06.2016.
 * Interface for basic operations on SQLite database.
 */
public interface ITaskSQLiteOperations {

    Cursor read(long id);

    Cursor readAll();

    Uri insert(Task task);

    int bulkInsert(List<Task> tasks);

    int update(long id, Task task);

    int delete(long id);

}
