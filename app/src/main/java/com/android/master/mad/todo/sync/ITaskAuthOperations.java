package com.android.master.mad.todo.sync;

import com.android.master.mad.todo.data.User;

import retrofit2.http.PUT;

/**
 * Created by Cookie on 04.06.2016.
 * Interface for authentication operations on web service.
 */
public interface ITaskAuthOperations {

    @PUT("auth")
    public boolean authenticateUser(User user);
}
