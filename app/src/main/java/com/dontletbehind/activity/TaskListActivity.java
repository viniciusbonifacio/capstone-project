package com.dontletbehind.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dontletbehind.R;
import com.dontletbehind.adapter.TaskItemTouchHelper;
import com.dontletbehind.adapter.TaskListAdapter;
import com.dontletbehind.domain.entity.TaskEntity;
import com.dontletbehind.loader.TaskLoader;

import java.util.List;

public class TaskListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<TaskEntity>> {

    //task loader identifier
    private static final int sTaskLoaderId = 123;

    private RecyclerView mTaskListRecyclerView;
    private RecyclerView.Adapter mTaskListAdapter;
    private RecyclerView.LayoutManager mTaskListLayoutManager;


    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab_task_list);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), TaskDetailActivity.class));
            }
        });

        //init recycler view
        initRecyclerView();

        //init loader
        if (getSupportLoaderManager().getLoader(0) == null) {
            getSupportLoaderManager().initLoader(0, null, this);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_task_list, menu);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                break;

            case R.id.fab_task_list:
                startActivity(new Intent(this, TaskDetailActivity.class));
                break;

            default:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public Loader<List<TaskEntity>> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new TaskLoader(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onLoadFinished(@NonNull Loader<List<TaskEntity>> loader, List<TaskEntity> taskEntities) {
        ((TaskListAdapter) mTaskListAdapter).setData(taskEntities);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onLoaderReset(@NonNull Loader<List<TaskEntity>> loader) {
        ((TaskListAdapter) mTaskListAdapter).setData(null);
    }

    /*
     * Init the recycler view Task List.
     */
    private void initRecyclerView() {
        mTaskListRecyclerView = findViewById(R.id.rv_task_list);

        mTaskListLayoutManager = new LinearLayoutManager(this);
        mTaskListRecyclerView.setLayoutManager(mTaskListLayoutManager);

        mTaskListAdapter = new TaskListAdapter(this);
        mTaskListRecyclerView.setAdapter(mTaskListAdapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new TaskItemTouchHelper(
                0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT,
                (TaskListAdapter) mTaskListAdapter);

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mTaskListRecyclerView);
    }
}
