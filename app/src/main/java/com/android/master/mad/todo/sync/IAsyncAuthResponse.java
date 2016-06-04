package com.android.master.mad.todo.sync;

/**
 * Created by Cookie on 04.06.2016.
 * Interface for AsyncTask callback when authenticating against web server.
 */
public interface IAsyncAuthResponse {
    void onAuthenticationResult(boolean result);
}
