package com.android.master.mad.todo.sync;

/**
 * Created by Cookie on 04.06.2016.
 * Interface for AsyncTask callback when syncing against web server.
 */
public interface IAsyncSyncResponse {
    void onSyncResult(boolean result);
}
