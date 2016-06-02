package com.android.master.mad.todo.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by misslert on 02.06.2016.
 * Stub authenticator service for tasks.
 */
public class TaskAuthenticatorService extends Service {

    private TaskAuthenticator taskAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        taskAuthenticator = new TaskAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return taskAuthenticator.getIBinder();
    }
}
