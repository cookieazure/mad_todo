package com.android.master.mad.todo.sync;

/**
 * Created by Cookie on 04.06.2016.
 * Interface for AsyncTask callback when checking server connection.
 */
public interface IAsyncConnectionResponse {
    void evaluateConnection(boolean result);
}