package com.android.master.mad.todo.sync;

import com.android.master.mad.todo.data.Task;

/**
 * Created by misslert on 02.06.2016.
 */
@Path("/dataitems")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public interface ITaskCrudOperations {

    @GET
    @Path("/{itemId}")
    public Task read(@PathParan("itemId")long id);

    @GET
    public List<Task> readAll();

    @POST
    public Task insert(Task task);

    @PUT
    public Task update(Task task);

    @DELETE
    @Path("/{itemId}")
    public boolean delete(@PathParam("/itemId")long id);
}
