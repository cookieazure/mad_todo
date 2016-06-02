package com.android.master.mad.todo.sync;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by misslert on 02.06.2016.
 */
public class TaskWebSyncService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public TaskWebSyncService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
