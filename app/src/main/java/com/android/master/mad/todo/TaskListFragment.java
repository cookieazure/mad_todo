package com.android.master.mad.todo;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.master.mad.todo.data.TaskContract;

/**
 * Created by Cookie on 04.06.2016.
 * Fragment for displaying the task list.
 */
public class TaskListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = TaskActivity.class.getSimpleName();

    private static final int TASK_LOADER = 1;

    private TaskAdapter taskAdapter;
    private ListView taskList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);
        taskAdapter = new TaskAdapter(getActivity(), null, 0);

        taskList = (ListView) view.findViewById(R.id.task_list);
        taskList.setAdapter(taskAdapter);
        getActivity().getLoaderManager().initLoader(TASK_LOADER, null, this);
        return view;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        Uri taskUri = TaskContract.Task.CONTENT_URI;

        //TODO initialize sort order

        return new CursorLoader(getActivity(), taskUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        taskAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        taskAdapter.swapCursor(null);
    }
}
