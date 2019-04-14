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
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.expensemanager.app.R;
import com.expensemanager.app.models.BusDailySummary;
import com.expensemanager.app.service.SyncBusDailySummary;
import com.expensemanager.app.tripsheet.conductor_picker.ConductorPickerFragment;

import com.expensemanager.app.helpers.DatePickerFragment;
import com.expensemanager.app.helpers.Helpers;
import com.expensemanager.app.helpers.PhotoSourceAdapter;
import com.expensemanager.app.helpers.TimePickerFragment;
import com.expensemanager.app.main.BaseActivity;
import com.expensemanager.app.models.User;
import com.expensemanager.app.models.EAction;
import com.expensemanager.app.models.Group;
import com.expensemanager.app.models.Member;
import com.expensemanager.app.models.PhotoSource;
import com.expensemanager.app.models.User;
import com.expensemanager.app.profile.ProfileActivity;
import com.expensemanager.app.service.Constant;
import com.expensemanager.app.service.PermissionsManager;
import com.expensemanager.app.service.SyncPhoto;
import com.expensemanager.app.service.enums.EIcon;
import com.jakewharton.rxbinding.widget.RxAdapterView;

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
import java.util.concurrent.TimeUnit;

import bolts.Continuation;
import bolts.Task;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class BusDailySummaryDetailActivity extends BaseActivity {
    private static final String TAG = BusDailySummaryDetailActivity.class.getSimpleName();

    public static final String DATE_PICKER = "date_picker";
    public static final String TIME_PICKER = "time_picker";
    private static final String BUS_DAILY_SUMMARY_ID = "BUS_DAILY_SUMMARY_ID";
    private static final int PROGRESS_BAR_DISPLAY_LENGTH = 6000;

    private Handler handler;


    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;
    private String busdailysummaryId;
    private BusDailySummary busdailysummary;
    private User driver;
    private User conductor;
    private Calendar calendar;
    private boolean isEditable = false;
    private String groupId;
    private String loginUserId;
    private Group group;
    private User createdBy;
    private boolean isDeleteAction = false;

    private BottomSheetDialog bottomSheetDialog;

    @BindView(R.id.toolbar_id)
    Toolbar toolbar;
    @BindView(R.id.toolbar_back_image_view_id)
    ImageView backImageView;
    @BindView(R.id.toolbar_extra_image_view_id)
    ImageView extraImageView;
    @BindView(R.id.toolbar_title_text_view_id)
    TextView titleTextView;
    @BindView(R.id.toolbar_edit_text_view_id)
    TextView editTextView;
    @BindView(R.id.toolbar_save_text_view_id)
    TextView saveTextView;
    @BindView(R.id.busdailysummary_detail_activity_user_info_relative_layout_id)
    RelativeLayout userInfoRelativeLayout;
    @BindView(R.id.busdailysummary_detail_activity_user_photo_image_view_id)
    ImageView userPhotoImageView;
    @BindView(R.id.busdailysummary_detail_activity_fullname_text_view_id)
    TextView fullNameTextView;
    @BindView(R.id.busdailysummary_detail_activity_email_text_view_id)
    TextView emailTextView;
    @BindView(R.id.busdailysummary_detail_activity_single1Collection_text_view_id)
    EditText amountTextView;
    @BindView(R.id.busdailysummary_detail_activity_note_text_view_id)
    EditText noteTextView;
    @BindView(R.id.busdailysummary_detail_activity_grid_view_id)
    GridView photoGridView;
    @BindView(R.id.busdailysummary_detail_activity_new_photo_grid_view_id)
    GridView newPhotoGridView;
    @BindView(R.id.busdailysummary_detail_activity_delete_button_id)
    Button deleteButton;
    @BindView(R.id.busdailysummary_detail_activity_progress_bar_id)
    ProgressBar progressBar;
    @BindView(R.id.busdailysummary_detail_activity_conductor_hint_text_view_id)
    TextView conductorHintTextView;
    @BindView(R.id.busdailysummary_detail_activity_conductor_relative_layout_id)
    RelativeLayout conductorRelativeLayout;
    @BindView(R.id.busdailysummary_detail_activity_conductor_color_image_view_id)
    CircleImageView conductorColorImageView;
    @BindView(R.id.busdailysummary_detail_activity_icon_image_view_id)
    ImageView iconImageView;
    @BindView(R.id.busdailysummary_detail_activity_conductor_name_text_view_id)
    TextView conductorNameTextView;

    @BindView(R.id.busdailysummary_detail_activity_busdailysummary_date_text_view_id) TextView busdailysummaryDateTextView;

    @BindView(R.id.busdailysummary_detail_activity_busdailysummary_time_text_view_id)
    TextView busdailysummaryTimeTextView;

    public static void newInstance(Context context, String id) {
        Intent intent = new Intent(context, BusDailySummaryDetailActivity.class);
        intent.putExtra(BUS_DAILY_SUMMARY_ID, id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.busdailysummary_detail_activity);
        ButterKnife.bind(this);

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences_session_key), 0);
        groupId = sharedPreferences.getString(Group.ID_KEY, null);
        loginUserId = sharedPreferences.getString(User.USER_ID, null);

        group = Group.getGroupById(groupId);

        busdailysummaryId = getIntent().getStringExtra(BUS_DAILY_SUMMARY_ID);
        busdailysummary = BusDailySummary.getBusDailySummaryById(busdailysummaryId);
        if (busdailysummary != null) {
            createdBy = User.getUserById(busdailysummary.getSubmittedById());
            driver = User.getUserById(busdailysummary.getDriverId());
            conductor = User.getUserById(busdailysummary.getConductorId());

        }

        setupToolbar();
        setupDateAndTime();
//        setupNewPhoto();

    }

    private void invalidateViews() {
        Log.d(TAG, "invalidateViews()");

        if (busdailysummary == null) {
            return;
        }

        photoGridView.setFocusable(false);
        newPhotoGridView.setFocusable(false);

        amountTextView.setText(String.valueOf(busdailysummary.getSingle1Collection()));
        setupConductor();

        if (createdBy != null && Member.getAllAcceptedMembersByGroupId(groupId).size() > 1) {
            userInfoRelativeLayout.setVisibility(View.VISIBLE);

            Helpers.loadProfilePhoto(userPhotoImageView, createdBy.getPhotoUrl());
            fullNameTextView.setText(createdBy.getFullname());
            emailTextView.setText(createdBy.getEmail());
            userPhotoImageView.setOnClickListener(v -> ProfileActivity.newInstance(this, createdBy.getId()));
        } else {
            userInfoRelativeLayout.setVisibility(View.GONE);
        }

        deleteButton.setOnClickListener(v -> delete());

        if (loginUserId.equals(busdailysummary.getSubmittedById()) || loginUserId.equals(group.getUserId())) {
            editTextView.setVisibility(isEditable ? View.GONE : View.VISIBLE);
        } else {
            editTextView.setVisibility(View.GONE);
        }

        saveTextView.setVisibility(isEditable ? View.VISIBLE : View.GONE);
        deleteButton.setVisibility(isEditable ? View.VISIBLE : View.GONE);
        // If set GONE, newPhotoGridView would not show up after click edit again
        newPhotoGridView.setVisibility(isEditable ? View.VISIBLE : View.INVISIBLE);

        setupEditableViews(isEditable);
    }

    private void showActionSheet() {
        ArrayList<EAction> actionsList = new ArrayList<>();
        actionsList.add(new EAction(R.string.edit, R.mipmap.ic_launcher));
        actionsList.add(new EAction(R.string.save, R.mipmap.ic_launcher));
        actionsList.add(new EAction(R.string.add, R.mipmap.ic_launcher));
        actionsList.add(new EAction(R.string.delete, R.mipmap.ic_launcher));

        ActionSheetAdapter adapter = new ActionSheetAdapter(actionsList);
        adapter.setOnItemClickListener(new ActionSheetAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ActionSheetAdapter.ItemHolder item, int position) {
                Log.d(TAG, "clicked position:" + position);
                bottomSheetDialog.dismiss();
            }
        });

        View view = getLayoutInflater().inflate(R.layout.action_sheet, null);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    private void setupDateAndTime() {
        calendar = Calendar.getInstance();
        calendar.setTime(busdailysummary.getSummaryDate());
        formatDateAndTime(calendar.getTime());
        busdailysummaryDateTextView.setOnClickListener(v -> {
            if (isEditable) {
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerFragment datePickerFragment = DatePickerFragment
                        .newInstance(year, month, day);
                datePickerFragment.setListener(onDateSetListener);
                datePickerFragment.show(getSupportFragmentManager(), DATE_PICKER);
            }
        });

        busdailysummaryTimeTextView.setOnClickListener(v -> {
            if (isEditable) {
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerFragment timePickerFragment = TimePickerFragment
                        .newInstance(hour, minute);
                timePickerFragment.setListener(onTimeSetListener);
                timePickerFragment.show(getSupportFragmentManager(), TIME_PICKER);
            }
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd yyyy", Locale.US);
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.US);
        // Parse date and set text
        busdailysummaryDateTextView.setText(dateFormat.format(date));
        busdailysummaryTimeTextView.setText(timeFormat.format(date));
    }

    private void setupToolbar() {
        toolbar.setContentInsetsAbsolute(0, 0);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        titleTextView.setText(getString(R.string.title_activity_busdailysummary_detail));
        backImageView.setOnClickListener(v -> close());
        editTextView.setOnClickListener(v -> {
            setEditMode(true);
//            showActionSheet(); // Example of bottom sheet
        });
        saveTextView.setOnClickListener(v -> save());
    }

    private void setupConductor() {
        loadUser(conductor);

        conductorHintTextView.setOnClickListener(v -> {
            selectUser();
        });
        conductorRelativeLayout.setOnClickListener(v -> {
            selectUser();
        });
    }

    private void selectUser() {
        if (isEditable) {
            ConductorPickerFragment conductorPickerFragment = ConductorPickerFragment
                    .newInstance();
            conductorPickerFragment.setListener(conductorPickerListener);
            conductorPickerFragment.show(getSupportFragmentManager(), ConductorPickerFragment.class.getSimpleName());
        }
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

            ColorDrawable colorDrawable = new ColorDrawable(Color.BLACK);
            conductorColorImageView.setImageDrawable(colorDrawable);
            conductorNameTextView.setText(conductor.getFullname());

            EIcon eIcon = EIcon.instanceFromName("face");
            if (eIcon != null) {
                iconImageView.setImageResource(eIcon.getValueRes());
                iconImageView.setVisibility(View.VISIBLE);
            } else {
                iconImageView.setVisibility(View.INVISIBLE);
            }
        }
        // Update conductor
        this.conductor = conductor;
    }

    private void setupEditableViews(boolean isEditable) {
        amountTextView.setFocusable(isEditable);
        amountTextView.setFocusableInTouchMode(isEditable);
        amountTextView.setClickable(isEditable);

        noteTextView.setFocusable(isEditable);
        noteTextView.setFocusableInTouchMode(isEditable);
        noteTextView.setClickable(isEditable);

        if (isEditable) {
            amountTextView.requestFocus();
            amountTextView.setSelection(amountTextView.length());
        }
    }

    private void setEditMode(boolean isEditable) {
        this.isEditable = isEditable;
        invalidateViews();
    }



    private Continuation<Void, Void> onUpdateSuccess = new Continuation<Void, Void>() {
        @Override
        public Void then(Task<Void> task) throws Exception {
            Log.d(TAG, "onUpdateSuccess");


            if (task.isFaulted()) {
                Log.e(TAG, "Error in updating busdailysummary.", task.getError());
            }

            Log.d(TAG, "Update busdailysummary success.");

            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            busdailysummary.setSynced(true);
            realm.copyToRealmOrUpdate(busdailysummary);
            realm.commitTransaction();
            realm.close();

            return null;
        }
    };

    private void save() {
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

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences_session_key), 0);
        String loginUserId = sharedPreferences.getString(User.USER_ID, null);
        String groupId = sharedPreferences.getString(Group.ID_KEY, null);

        if (loginUserId == null || groupId == null) {
            Log.i(TAG, "Error getting login user id or group id.");
            return;
        }

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        busdailysummary.setSingle1Collection(amount);
        busdailysummary.setConductorId(conductor != null ? conductor.getId() : null);
        busdailysummary.setGroupId(groupId);
        busdailysummary.setSummaryDate(calendar.getTime());
        busdailysummary.setSynced(false);
        realm.copyToRealmOrUpdate(busdailysummary);
        realm.commitTransaction();
        realm.close();


        progressBar.setVisibility(View.VISIBLE);
        SyncBusDailySummary.update(busdailysummary).continueWith(onUpdateSuccess, Task.UI_THREAD_EXECUTOR);

        isEditable = false;
        closeSoftKeyboard();
        invalidateViews();
    }





    private Continuation<Void, Void> onDeleteSuccess = new Continuation<Void, Void>() {
        @Override
        public Void then(Task<Void> task) throws Exception {
            progressBar.setVisibility(View.GONE);
            if (task.isFaulted()) {
                Log.e(TAG, "Error in deleting busdailysummary.", task.getError());
            }

            isDeleteAction = true;
            Log.d(TAG, "Delete busdailysummary success.");
            close();
            return null;
        }
    };

    public void closeSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        View view = this.getCurrentFocus();
        if (inputMethodManager != null && view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void delete() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_busdailysummary_title)
                .setMessage(R.string.delete_busdailysummary_message)
                .setPositiveButton(R.string.delete, (DialogInterface dialog, int which) -> {
                    progressBar.setVisibility(View.VISIBLE);
                    SyncBusDailySummary.delete(busdailysummary.getId()).continueWith(onDeleteSuccess, Task.UI_THREAD_EXECUTOR);
                })
                .setNegativeButton(R.string.cancel, (DialogInterface dialog, int which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void close() {
        removeHandler();
        finish();
        overridePendingTransition(0, R.anim.right_out);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (isDeleteAction) {
            BusDailySummary.delete(busdailysummary.getId());
        }
    }

    @Override
    public void onBackPressed() {
        close();
    }

    @Override
    public void onResume() {
        super.onResume();
        Realm realm = Realm.getDefaultInstance();
        realm.addChangeListener(v -> {
            Log.d(TAG, "RealmChangeListener");

            progressBar.setVisibility(View.GONE);
            invalidateViews();
        });

        invalidateViews();
        handler = new Handler();
        handler.postDelayed(progressBarRunnable, PROGRESS_BAR_DISPLAY_LENGTH);
    }

    @Override
    public void onPause() {
        super.onPause();
        Realm realm = Realm.getDefaultInstance();
        realm.removeAllChangeListeners();

        removeHandler();
    }

    private void removeHandler() {
        if (handler != null) {
            handler.removeCallbacks(progressBarRunnable);
            handler = null;
        }
    }

    private Runnable progressBarRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "progressBarRunnable");
            progressBar.setVisibility(View.GONE);
        }
    };


}
