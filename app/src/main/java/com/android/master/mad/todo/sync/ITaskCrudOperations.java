package com.android.master.mad.todo.sync;

import com.android.master.mad.todo.data.Task;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by misslert on 02.06.2016.
 * Interface for CURD operations on web service.
 */
public interface ITaskCrudOperations {

    @GET("todos/{itemId}")
    Call<Task> read(@Path("itemId")long id);

    @GET("todos")
    Call<List<Task>> readAll();

    @POST("todos")
    Call<Task> insert(@Body Task task);

    @PUT("todos/{itemId}")
    Call<Task> update(@Path("itemId") long id, @Body Task task);

    @DELETE("todos/{itemId}")
    Call<Boolean> delete(@Path("itemId")long id);
}
