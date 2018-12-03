package com.dontletbehind.activity;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.dontletbehind.R;
import com.dontletbehind.domain.contract.TaskContract;
import com.dontletbehind.domain.entity.TaskEntity;
import com.dontletbehind.notification.TaskReminderAlarm;
import com.dontletbehind.provider.database.TaskEntityProvider;
import com.dontletbehind.util.ParserUtil;

import java.util.concurrent.TimeUnit;

/**
 * Controller for {@code activity_task_detail.xml} layout.
 */
public class TaskDetailActivity extends AppCompatActivity {

    private TaskEntity mTaskEntity;
    private RadioGroup mReminderRadioGroup;
    private EditText mTitleEditText;
    private EditText mDescEditText;
    private Button mClearButton;
    private Button mSaveButton;
    private boolean mEditMode;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        bindView();
        setListeners();
        checkEditMode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Bind the UI widgets on member variables.
     */
    private void bindView() {
        mReminderRadioGroup = findViewById(R.id.rg_reminder_task_detail);
        mTitleEditText = findViewById(R.id.et_task_detail_title);
        mDescEditText = findViewById(R.id.et_task_detail_description);
        mClearButton = findViewById(R.id.btn_task_detail_clear);
        mSaveButton = findViewById(R.id.btn_task_detail_save);
    }

    /**
     * Set listeners for UI widgets.
     */
    private void setListeners() {
        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClearButtonPressed();
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveButtonPressed();
            }
        });
    }

    /**
     * Called when the {@code Button Clear} is pressed.
     * <p>
     * Clear all the input fields and set the {@link TaskEntity} member variable
     * to {@code null}.
     */
    private void onClearButtonPressed() {
        mTaskEntity = null;
        mTitleEditText.setText(null);
        mDescEditText.setText(null);
        mReminderRadioGroup.clearCheck();
        mEditMode = false;
    }

    /**
     * Called when the {@code Button Save} is pressed.
     */
    private void onSaveButtonPressed() {
        if (mTaskEntity == null) {
            mTaskEntity = new TaskEntity();
        }

        //read UI inputs
        mTaskEntity.setTitle(mTitleEditText.getText().toString());
        mTaskEntity.setDescription(mDescEditText.getText().toString());
        mTaskEntity.setTimer(getReminderTime());

        if (mEditMode) {
            updateTaskEntity();

        } else {
            saveTaskEntity();
        }

        onBackPressed();
    }

    /**
     * Updates the current {@link TaskEntity}.
     */
    private void updateTaskEntity() {
        ContentValues values = ParserUtil.parseTasktoContentValues(mTaskEntity);
        int affectedRows = getContentResolver().update(
                Uri.parse(TaskEntityProvider.CONTENT_URI.toString() + "/" + mTaskEntity.getId()),
                values,
                TaskContract.COLUMN_ID + "=" + mTaskEntity.getId(),
                null
        );

        if (affectedRows > 0) {
            Toast.makeText(this, "Task updated.", Toast.LENGTH_SHORT).show();
            new TaskReminderAlarm(this).setTaskReminder(mTaskEntity);
        } else {
            Toast.makeText(this, "Cannot update Task!", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Save a new {@link TaskEntity}.
     */
    private void saveTaskEntity() {
        ContentValues values = ParserUtil.parseTasktoContentValues(mTaskEntity);
        Uri insertUri = getContentResolver().insert(TaskEntityProvider.CONTENT_URI, values);
        if (insertUri != null) {
            Cursor cursor = getContentResolver()
                    .query(
                            insertUri,
                            TaskContract.ALL_COLUMNS,
                            null,
                            null,
                            null
                    );
            //set reminder
            if (cursor != null) {
                mTaskEntity = ParserUtil.cursorToListTaskEntity(cursor).get(0);
                if (mTaskEntity != null) {
                    new TaskReminderAlarm(this).setTaskReminder(mTaskEntity);
                }
            }
        }
    }


    /**
     * Get the milliseconds time referenced by the checked {@code RadionButton} option.
     *
     * @return time in milliseconds
     */
    private long getReminderTime() {
        switch (mReminderRadioGroup.getCheckedRadioButtonId()) {
            case R.id.btn_task_detail_reminder_30m:
                return TimeUnit.MINUTES.toMillis(30);

            case R.id.btn_task_detail_reminder_1h:
                return TimeUnit.HOURS.toMillis(1);

            case R.id.btn_task_detail_reminder_1d:
                return TimeUnit.DAYS.toMillis(1);

            case R.id.btn_task_detail_reminder_1w:
                return TimeUnit.DAYS.toMillis(8);
        }
        return 0;
    }

    /**
     * Check if this activity was invoked to detail an {@link TaskEntity}.
     */
    private void checkEditMode() {
        if (getIntent().getByteArrayExtra(TaskEntity.class.getName()) != null) {
            mTaskEntity = ParserUtil.parcelBytesToTask(
                    getIntent().getByteArrayExtra(TaskEntity.class.getName()));

            mTitleEditText.setText(mTaskEntity.getTitle());
            mDescEditText.setText(mTaskEntity.getDescription());

            long timer = mTaskEntity.getTimer();
            if (timer == TimeUnit.MINUTES.toMillis(30)) {
                mReminderRadioGroup.check(R.id.btn_task_detail_reminder_30m);

            } else if (timer == TimeUnit.HOURS.toMillis(1)) {
                mReminderRadioGroup.check(R.id.btn_task_detail_reminder_1h);

            } else if (timer == TimeUnit.DAYS.toMillis(1)) {
                mReminderRadioGroup.check(R.id.btn_task_detail_reminder_1d);

            } else if (timer == TimeUnit.DAYS.toMillis(8)) {
                mReminderRadioGroup.check(R.id.btn_task_detail_reminder_1w);
            }

            //sets the behavior to update current task
            mEditMode = true;
        }
    }
}
