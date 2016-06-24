package com.android.master.mad.todo.helper;

/**
 * Created by MISSLERT on 04.06.2016.
 * Interface for AsyncTask callback when checking server connection.
 */
public interface IAsyncConnectionResponse {
    void evaluateConnection(boolean result);
}