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
import android.text.Editable;
import android.text.TextWatcher;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
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
    EditText single1CollectionTextView;

    @BindView(R.id.busdailysummary_detail_activity_single2Collection_text_view_id)
    EditText single2CollectionTextView;


    @BindView(R.id.busdailysummary_detail_activity_single3Collection_text_view_id)
    EditText single3CollectionTextView;
    @BindView(R.id.busdailysummary_detail_activity_single4Collection_text_view_id)
    EditText single4CollectionTextView;
    @BindView(R.id.busdailysummary_detail_activity_single5Collection_text_view_id)
    EditText single5CollectionTextView;
    @BindView(R.id.busdailysummary_detail_activity_single6Collection_text_view_id)
    EditText single6CollectionTextView;
    @BindView(R.id.busdailysummary_detail_activity_single7Collection_text_view_id)
    EditText single7CollectionTextView;
    @BindView(R.id.busdailysummary_detail_activity_single8Collection_text_view_id)
    EditText single8CollectionTextView;
    @BindView(R.id.busdailysummary_detail_activity_single9Collection_text_view_id)
    EditText single9CollectionTextView;
    @BindView(R.id.busdailysummary_detail_activity_single10Collection_text_view_id)
    EditText single10CollectionTextView;


    @BindView(R.id.busdailysummary_detail_activity_dieselExpense_text_view_id)
    EditText dieselExpenseTextView;
    @BindView(R.id.busdailysummary_detail_activity_oilExpense_text_view_id)
    EditText oilExpenseTextView;
    @BindView(R.id.busdailysummary_detail_activity_waterExpense_text_view_id)
    EditText waterExpenseTextView;
    @BindView(R.id.busdailysummary_detail_activity_driverPathaExpense_text_view_id)
    EditText driverPathaExpenseTextView;
    @BindView(R.id.busdailysummary_detail_activity_driverSalaryAllowanceExpense_text_view_id)
    EditText driverSalaryAllowanceExpenseTextView;
    @BindView(R.id.busdailysummary_detail_activity_conductorPathaExpense_text_view_id)
    EditText conductorPathaExpenseTextView;
    @BindView(R.id.busdailysummary_detail_activity_conductorSalaryAllowanceExpense_text_view_id)
    EditText conductorSalaryAllowanceExpenseTextView;
    @BindView(R.id.busdailysummary_detail_activity_checkingPathaExpense_text_view_id)
    EditText checkingPathaExpenseTextView;
    @BindView(R.id.busdailysummary_detail_activity_commissionExpense_text_view_id)
    EditText commissionExpenseTextView;
    @BindView(R.id.busdailysummary_detail_activity_otherExpense_text_view_id)
    EditText otherExpenseTextView;
    @BindView(R.id.busdailysummary_detail_activity_unionExpense_text_view_id)
    EditText unionExpenseTextView;
    @BindView(R.id.busdailysummary_detail_activity_cleanerExpense_text_view_id)
    EditText cleanerExpenseTextView;





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

    @BindView(R.id.busdailysummary_detail_activity_busdailysummary_date_text_view_id)
    TextView busdailysummaryDateTextView;

//    @BindView(R.id.busdailysummary_detail_activity_busdailysummary_time_text_view_id)
//    TextView busdailysummaryTimeTextView;


    Map<String, Double> validatedAmounts = new HashMap<>();


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
        groupId = sharedPreferences.getString(Group.GROUP_ID_KEY, null);
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


    }

    private void invalidateViews() {
        Log.d(TAG, "invalidateViews()");

        if (busdailysummary == null) {
            return;
        }



        single1CollectionTextView.setText(String.valueOf(busdailysummary.getSingle1Collection()));
        single2CollectionTextView.setText(String.valueOf(busdailysummary.getSingle2Collection()));
        single3CollectionTextView.setText(String.valueOf(busdailysummary.getSingle3Collection()));
        single4CollectionTextView.setText(String.valueOf(busdailysummary.getSingle4Collection()));
        single5CollectionTextView.setText(String.valueOf(busdailysummary.getSingle5Collection()));
        single6CollectionTextView.setText(String.valueOf(busdailysummary.getSingle6Collection()));
        single7CollectionTextView.setText(String.valueOf(busdailysummary.getSingle7Collection()));
        single8CollectionTextView.setText(String.valueOf(busdailysummary.getSingle8Collection()));
        single9CollectionTextView.setText(String.valueOf(busdailysummary.getSingle9Collection()));
        single10CollectionTextView.setText(String.valueOf(busdailysummary.getSingle10Collection()));


        dieselExpenseTextView.setText(String.valueOf(busdailysummary.getDieselExpense()));
        oilExpenseTextView.setText(String.valueOf(busdailysummary.getOilExpense()));
        waterExpenseTextView.setText(String.valueOf(busdailysummary.getWaterExpense()));
        driverPathaExpenseTextView.setText(String.valueOf(busdailysummary.getDriverPathaExpense()));
        driverSalaryAllowanceExpenseTextView.setText(String.valueOf(busdailysummary.getDriverSalaryAllowanceExpense()));
        conductorPathaExpenseTextView.setText(String.valueOf(busdailysummary.getConductorPathaExpense()));
        conductorSalaryAllowanceExpenseTextView.setText(String.valueOf(busdailysummary.getConductorSalaryAllowanceExpense()));
        checkingPathaExpenseTextView.setText(String.valueOf(busdailysummary.getCheckingPathaExpense()));
        commissionExpenseTextView.setText(String.valueOf(busdailysummary.getCommissionExpense()));
        otherExpenseTextView.setText(String.valueOf(busdailysummary.getOtherExpense()));
        unionExpenseTextView.setText(String.valueOf(busdailysummary.getUnionExpense()));
        cleanerExpenseTextView.setText(String.valueOf(busdailysummary.getCleanerExpense()));


        setupConductor();



        userInfoRelativeLayout.setVisibility(View.GONE);

        deleteButton.setOnClickListener(v -> delete());

        if (loginUserId.equals(busdailysummary.getSubmittedById()) || loginUserId.equals(group.getUserId())) {
            editTextView.setVisibility(isEditable ? View.GONE : View.VISIBLE);
        } else {
            editTextView.setVisibility(View.GONE);
        }

        saveTextView.setVisibility(isEditable ? View.VISIBLE : View.GONE);
        deleteButton.setVisibility(isEditable ? View.VISIBLE : View.GONE);

        setupEditableViews(isEditable);


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
//        busdailysummaryTimeTextView.setText(timeFormat.format(date));
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


        setupEditField(single1CollectionTextView);
        setupEditField(single2CollectionTextView);
        setupEditField(single3CollectionTextView);
        setupEditField(single4CollectionTextView);
        setupEditField(single5CollectionTextView);
        setupEditField(single6CollectionTextView);
        setupEditField(single7CollectionTextView);
        setupEditField(single8CollectionTextView);
        setupEditField(single9CollectionTextView);
        setupEditField(single10CollectionTextView);


        setupEditField(dieselExpenseTextView);
        setupEditField(oilExpenseTextView);
        setupEditField(waterExpenseTextView);
        setupEditField(driverPathaExpenseTextView);
        setupEditField(driverSalaryAllowanceExpenseTextView);
        setupEditField(conductorPathaExpenseTextView);
        setupEditField(conductorSalaryAllowanceExpenseTextView);
        setupEditField(checkingPathaExpenseTextView);
        setupEditField(commissionExpenseTextView);
        setupEditField(otherExpenseTextView);
        setupEditField(unionExpenseTextView);
        setupEditField(cleanerExpenseTextView);




    }

    private void setupEditField(EditText editText) {
        editText.setFocusable(isEditable);
        editText.setFocusableInTouchMode(isEditable);
        editText.setClickable(isEditable);
        if (isEditable) {
            editText.requestFocus();
            editText.setSelection(editText.length());

        }
    }

    private void setEditMode(boolean isEditable) {
        this.isEditable = isEditable;
        invalidateViews();
        single1CollectionTextView.requestFocus();

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

    private void getValidatedAmount(String amountName, EditText editText) {

        double amount = 0;
        try {
            amount = Double.valueOf(editText.getText().toString());
            amount = Helpers.formatNumToDouble(amount);
            if (amount < 0) {
                Toast.makeText(this, String.format("%s cannot be zero: ", amountName), Toast.LENGTH_SHORT).show();
                throw new RuntimeException("Amount cannot be less than zero");
            }
        } catch (Exception e) {
            Log.d(TAG, "Cannot convert Amount to double. :" + amountName, e);
            Toast.makeText(this, String.format("Incorrect %s format.",amountName), Toast.LENGTH_SHORT).show();
            throw new RuntimeException("Incorrect Amount format.");
        }

        validatedAmounts.put(amountName, amount);
    }




    private void save() {

        try {
            getValidatedAmount(getString(R.string.single1Collection), single1CollectionTextView);
            getValidatedAmount(getString(R.string.single2Collection), single2CollectionTextView);
            getValidatedAmount(getString(R.string.single3Collection), single3CollectionTextView);
            getValidatedAmount(getString(R.string.single4Collection), single4CollectionTextView);
            getValidatedAmount(getString(R.string.single5Collection), single5CollectionTextView);
            getValidatedAmount(getString(R.string.single6Collection), single6CollectionTextView);
            getValidatedAmount(getString(R.string.single7Collection), single7CollectionTextView);
            getValidatedAmount(getString(R.string.single8Collection), single8CollectionTextView);
            getValidatedAmount(getString(R.string.single9Collection), single9CollectionTextView);
            getValidatedAmount(getString(R.string.single10Collection), single10CollectionTextView);

            getValidatedAmount(getString(R.string.dieselExpense), dieselExpenseTextView);
            getValidatedAmount(getString(R.string.oilExpense), oilExpenseTextView);
            getValidatedAmount(getString(R.string.waterExpense), waterExpenseTextView);
            getValidatedAmount(getString(R.string.driverPathaExpense), driverPathaExpenseTextView);
            getValidatedAmount(getString(R.string.driverSalaryAllowanceExpense), driverSalaryAllowanceExpenseTextView);
            getValidatedAmount(getString(R.string.conductorPathaExpense), conductorPathaExpenseTextView);
            getValidatedAmount(getString(R.string.conductorSalaryAllowanceExpense), conductorSalaryAllowanceExpenseTextView);
            getValidatedAmount(getString(R.string.checkingPathaExpense), checkingPathaExpenseTextView);
            getValidatedAmount(getString(R.string.commissionExpense), commissionExpenseTextView);
            getValidatedAmount(getString(R.string.otherExpense), otherExpenseTextView);
            getValidatedAmount(getString(R.string.unionExpense), unionExpenseTextView);
            getValidatedAmount(getString(R.string.cleanerExpense), cleanerExpenseTextView);




        } catch (Exception e) {
            Log.d(TAG, "Amount Validation Error..", e);
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences_session_key), 0);
        String loginUserId = sharedPreferences.getString(User.USER_ID, null);
        String groupId = sharedPreferences.getString(Group.GROUP_ID_KEY, null);

        if (loginUserId == null || groupId == null) {
            Log.i(TAG, "Error getting login user id or group id.");
            return;
        }

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        busdailysummary.setSingle1Collection(validatedAmounts.get(getString(R.string.single1Collection)));
        busdailysummary.setSingle2Collection(validatedAmounts.get(getString(R.string.single2Collection)));
        busdailysummary.setSingle3Collection(validatedAmounts.get(getString(R.string.single3Collection)));
        busdailysummary.setSingle4Collection(validatedAmounts.get(getString(R.string.single4Collection)));
        busdailysummary.setSingle5Collection(validatedAmounts.get(getString(R.string.single5Collection)));
        busdailysummary.setSingle6Collection(validatedAmounts.get(getString(R.string.single6Collection)));
        busdailysummary.setSingle7Collection(validatedAmounts.get(getString(R.string.single7Collection)));
        busdailysummary.setSingle8Collection(validatedAmounts.get(getString(R.string.single8Collection)));
        busdailysummary.setSingle9Collection(validatedAmounts.get(getString(R.string.single9Collection)));
        busdailysummary.setSingle10Collection(validatedAmounts.get(getString(R.string.single10Collection)));


        busdailysummary.setDieselExpense(validatedAmounts.get(getString(R.string.dieselExpense)));
        busdailysummary.setOilExpense(validatedAmounts.get(getString(R.string.oilExpense)));
        busdailysummary.setWaterExpense(validatedAmounts.get(getString(R.string.waterExpense)));
        busdailysummary.setDriverPathaExpense(validatedAmounts.get(getString(R.string.waterExpense)));
        busdailysummary.setDriverSalaryAllowanceExpense(validatedAmounts.get(getString(R.string.driverSalaryAllowanceExpense)));
        busdailysummary.setConductorPathaExpense(validatedAmounts.get(getString(R.string.conductorPathaExpense)));
        busdailysummary.setConductorSalaryAllowanceExpense(validatedAmounts.get(getString(R.string.conductorSalaryAllowanceExpense)));
        busdailysummary.setCheckingPathaExpense(validatedAmounts.get(getString(R.string.checkingPathaExpense)));
        busdailysummary.setCommissionExpense(validatedAmounts.get(getString(R.string.commissionExpense)));
        busdailysummary.setOtherExpense(validatedAmounts.get(getString(R.string.otherExpense)));
        busdailysummary.setUnionExpense(validatedAmounts.get(getString(R.string.unionExpense)));
        busdailysummary.setCleanerExpense(validatedAmounts.get(getString(R.string.cleanerExpense)));

        busdailysummary.setConductorId(conductor != null ? conductor.getId() : null);
        busdailysummary.setGroupId(groupId);
//        busdailysummary.setSummaryDate(calendar.getTime());
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
