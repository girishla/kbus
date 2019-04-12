package com.expensemanager.app.tripsheet;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.expensemanager.app.R;
import com.expensemanager.app.tripsheet.conductor_picker.ConductorPickerFragment;
import com.expensemanager.app.helpers.DatePickerFragment;
import com.expensemanager.app.helpers.Helpers;
import com.expensemanager.app.helpers.PhotoSourceAdapter;
import com.expensemanager.app.helpers.TimePickerFragment;
import com.expensemanager.app.main.BaseActivity;
import com.expensemanager.app.models.Category;
import com.expensemanager.app.models.BusDailySummary;
import com.expensemanager.app.models.Group;
import com.expensemanager.app.models.PhotoSource;
import com.expensemanager.app.models.User;
import com.expensemanager.app.service.Constant;
import com.expensemanager.app.service.PermissionsManager;
import com.expensemanager.app.service.SyncBusDailySummary;
import com.expensemanager.app.tripsheet.conductor_picker.ConductorPickerFragment;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import bolts.Continuation;
import bolts.Task;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.expensemanager.app.service.Constant.CROP_PHOTO_CODE;
import static com.expensemanager.app.service.Constant.PICK_PHOTO_CODE;
import static com.expensemanager.app.service.Constant.TAKE_PHOTO_CODE;

public class NewBusDailySummaryActivity extends BaseActivity {
    public static final String TAG = NewBusDailySummaryActivity.class.getSimpleName();

    public static final String DATE_PICKER = "date_picker";
    public static final String TIME_PICKER = "time_picker";

    private ArrayList<byte[]> photoList;
    private Uri outputFileUri;
    private String photoFileName;
    private AlertDialog.Builder choosePhotoSource;
    private Calendar calendar;
    private BusDailySummary busdailySummary;
    private User conductor;
    private Runnable pendingRunnable;
    private Handler handler;

    @BindView(R.id.toolbar_id) Toolbar toolbar;
    @BindView(R.id.toolbar_back_image_view_id) ImageView backImageView;
    @BindView(R.id.toolbar_title_text_view_id) TextView titleTextView;
    @BindView(R.id.toolbar_edit_text_view_id) TextView editTextView;
    @BindView(R.id.toolbar_save_text_view_id) TextView saveTextView;
    @BindView(R.id.new_expense_activity_amount_text_view_id) TextView amountTextView;
    @BindView(R.id.new_expense_activity_note_text_view_id) TextView noteTextView;
    @BindView(R.id.new_expense_activity_grid_view_id) GridView photoGridView;
    @BindView(R.id.progress_bar_id) ProgressBar progressBar;
    @BindView(R.id.new_expense_activity_user_hint_text_view_id) TextView userHintTextView;
    @BindView(R.id.new_expense_activity_user_relative_layout_id) RelativeLayout userRelativeLayout;
    @BindView(R.id.new_expense_activity_user_color_image_view_id) CircleImageView userColorImageView;
    @BindView(R.id.new_expense_activity_user_name_text_view_id) TextView userNameTextView;
    @BindView(R.id.new_expense_activity_expense_date_text_view_id) TextView expenseDateTextView;
    @BindView(R.id.new_expense_activity_expense_time_text_view_id) TextView expenseTimeTextView;

    public static void newInstance(Context context) {
        Intent intent = new Intent(context, NewBusDailySummaryActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_expense_activity);
        ButterKnife.bind(this);

        handler = new Handler();
        busdailySummary = new BusDailySummary();
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.blue), PorterDuff.Mode.SRC_ATOP);
        setupToolbar();
        setupConductor();
        setupDateAndTime();
    }

    private void setupDateAndTime() {
        calendar = Calendar.getInstance();
        formatDateAndTime(calendar.getTime());
        expenseDateTextView.setOnClickListener(v -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(year, month, day);
            datePickerFragment.setListener(onDateSetListener);
            datePickerFragment.show(getSupportFragmentManager(), DATE_PICKER);
        });

        expenseTimeTextView.setOnClickListener(v -> {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            TimePickerFragment timePickerFragment = TimePickerFragment.newInstance(hour, minute);
            timePickerFragment.setListener(onTimeSetListener);
            timePickerFragment.show(getSupportFragmentManager(), TIME_PICKER);
        });
    }

    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
            calendar.set(year, monthOfYear, dayOfMonth);
            formatDateAndTime(calendar.getTime());
        }
    };

    private TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            formatDateAndTime(calendar.getTime());
        }
    };

    private void formatDateAndTime(Date date) {
        // Create format
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.US);
        // Parse date and set text
        expenseDateTextView.setText(dateFormat.format(date));
        expenseTimeTextView.setText(timeFormat.format(date));
    }

    private void setupConductor() {
        loadUser(conductor);

        conductorHintTextView.setOnClickListener(v -> {
            selectCategory();
        });
        conductorRelativeLayout.setOnClickListener(v -> {
            selectCategory();
        });
    }

    private void selectConductor() {
        ConductorPickerFragment conductorPickerFragment = ConductorPickerFragment.newInstance();
        conductorPickerFragment.setListener(conductorPickerListener);
        conductorPickerFragment.show(getSupportFragmentManager(), ConductorPickerFragment.class.getSimpleName());
    }

    private ConductorPickerFragment.ConductorPickerListener conductorPickerListener = new ConductorPickerFragment.ConductorPickerListener() {
        @Override
        public void onFinishTripSheetConductorDialog(User conductor) {
            loadUser(conductor);

        }

    };

    private void loadUser(User conductor) {
        if (conductor == null) {
            // Show conductor hint
            conductorHintTextView.setVisibility(View.VISIBLE);
            conductorRelativeLayout.setVisibility(View.INVISIBLE);
        } else {
            // Hide conductor hint
            conductorHintTextView.setVisibility(View.INVISIBLE);
            conductorRelativeLayout.setVisibility(View.VISIBLE);

            ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("black"));
            conductorColorImageView.setImageDrawable(colorDrawable);
            conductorNameTextView.setText(conductor.getFullname());
        }
        // Update conductor
        this.conductor = conductor;
    }

    private void setupToolbar() {
        toolbar.setContentInsetsAbsolute(0,0);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        titleTextView.setText(getString(R.string.title_activity_new_expense));
        saveTextView.setVisibility(View.VISIBLE);
        backImageView.setImageResource(R.drawable.ic_window_close);
        saveTextView.setText(R.string.create);

        titleTextView.setOnClickListener(v -> close());
        backImageView.setOnClickListener(v -> close());
        saveTextView.setOnClickListener(v -> save());
    }


    private void save() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences_session_key), 0);
        String loginUserId = sharedPreferences.getString(User.USER_ID, null);
        String groupId = sharedPreferences.getString(Group.ID_KEY, null);
        if (loginUserId == null || groupId == null) {
            Log.i(TAG, "Error getting login conductor id or group id.");
            return;
        }
        busdailySummary.setSubmittedBy(loginUserId);
        busdailySummary.setGroupId(groupId);

        double amount;
        try {
            amount = Double.valueOf(amountTextView.getText().toString());
            amount = Helpers.formatNumToDouble(amount);
            if (amount <= 0) {
                Toast.makeText(this, "Amount cannot be zero.", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            Log.e(TAG, "Cannot convert amount to double.", e);
            Toast.makeText(this, "Incorrect amount format.", Toast.LENGTH_SHORT).show();
            return;
        }
        busdailySummary.setAmount(amount);


        busdailySummary.setConductorId(conductor != null ? conductor.getId() : null);
        busdailySummary.setSummaryDate(calendar.getTime());

        progressBar.setVisibility(View.VISIBLE);
        SyncBusDailySummary.create(busdailySummary).continueWith(onCreateSuccess, Task.UI_THREAD_EXECUTOR);
        closeSoftKeyboard();
    }

    private Continuation<JSONObject, Void> onCreateSuccess = new Continuation<JSONObject, Void>() {
        @Override
        public Void then(Task<JSONObject> task) throws Exception {
            progressBar.setVisibility(View.GONE);
            if (task.isFaulted()) {
                Log.e(TAG, "Error in creating new busdailySummary.", task.getError());
            }

            close();

            return null;
        }
    };

    public void closeSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        View view = this.getCurrentFocus();
        if (inputMethodManager != null && view != null){
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void closeWithUnSavedChangesCheck() {
        if (amountTextView.getText().length() == 0 && noteTextView.getText().length() == 0 && photoList.size() <= 1) {
            close();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.unsaved_changes)
                .setMessage(R.string.unsaved_changes_message)
                .setPositiveButton(R.string.discard, (DialogInterface dialog, int which) -> close())
                .setNegativeButton(R.string.cancel, (DialogInterface dialog, int which) -> dialog.dismiss())
                .show();
    }

    @Override
    protected void close() {
        closeSoftKeyboard();

        pendingRunnable = new Runnable() {
            @Override
            public void run() {
                finish();
                overridePendingTransition(0, R.anim.right_out);
            }
        };

        // Wait soft keyboard to close
        handler.postDelayed(pendingRunnable, 50);
        pendingRunnable = null;
    }

    @Override
    public void onBackPressed() {
        closeWithUnSavedChangesCheck();
    }


}
