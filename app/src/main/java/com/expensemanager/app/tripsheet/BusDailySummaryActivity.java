package com.expensemanager.app.tripsheet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.expensemanager.app.R;
import com.expensemanager.app.models.BusDailySummary;

import com.expensemanager.app.helpers.Helpers;
import com.expensemanager.app.main.Analytics;
import com.expensemanager.app.main.BaseActivity;
import com.expensemanager.app.models.Category;
import com.expensemanager.app.models.Member;
import com.expensemanager.app.models.User;
import com.expensemanager.app.service.SyncBusDailySummary;
import com.twotoasters.jazzylistview.effects.SlideInEffect;
import com.twotoasters.jazzylistview.recyclerview.JazzyRecyclerViewScrollListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import bolts.Continuation;
import bolts.Task;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class BusDailySummaryActivity extends BaseActivity {

    private static final String TAG = BusDailySummaryActivity.class.getSimpleName();

    public static final String CATEGORY_ID = "category_id";
    public static final String START_END_DATE = "startEnd";
    public static final String IS_CATEGORY_FILTERED = "is_category_filtered";
    public static final String CATEGORY_FRAGMENT = "Category_Fragment";
    public static final String DATE_FRAGMENT = "Date_Fragment";
    public static final String USER_FRAGMENT = "User_Fragment";

    private ArrayList<BusDailySummary> busdailysummaries;
    private Member member;
    private boolean isMemberFiltered;
    private Category category;
    private boolean isCategoryFiltered;
    private Date startDate;
    private Date endDate;
    private boolean isDateFiltered;
    private String groupId;
    private long syncTimeInMillis;
    private String syncTimeKey;

    private BusDailySummaryAdapter busdailysummaryAdapter;

    @BindView(R.id.toolbar_id) Toolbar toolbar;
    @BindView(R.id.toolbar_back_image_view_id) ImageView backImageView;
    @BindView(R.id.toolbar_extra_image_view_id) ImageView extraImageView;
    @BindView(R.id.toolbar_title_text_view_id) TextView titleTextView;
    @BindView(R.id.busdailysummary_activity_recycler_view_id) RecyclerView recyclerView;
    @BindView(R.id.swipeContainer_id) SwipeRefreshLayout swipeContainer;

    public static void newInstance(Context context) {
        Intent intent = new Intent(context, BusDailySummaryActivity.class);
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public static void newInstance(Context context, String categoryId) {
        Intent intent = new Intent(context, BusDailySummaryActivity.class);
        intent.putExtra(CATEGORY_ID, categoryId);
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public static void newInstance(Context context, Date[] startEnd) {
        Intent intent = new Intent(context, BusDailySummaryActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(START_END_DATE, startEnd);
        intent.putExtras(bundle);
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public static void newInstance(Context context, String categoryId, Date[] startEnd) {
        Intent intent = new Intent(context, BusDailySummaryActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(START_END_DATE, startEnd);
        intent.putExtras(bundle);
        intent.putExtra(CATEGORY_ID, categoryId);
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.busdailysummary_activity);
        ButterKnife.bind(this);

        setupToolbar();

        groupId = Helpers.getCurrentGroupId();
        syncTimeKey = Helpers.getSyncTimeKey(TAG, groupId);
        syncTimeInMillis = Helpers.getSyncTimeInMillis(syncTimeKey);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            isDateFiltered = true;
            Date[] startEnd = (Date[]) bundle.getSerializable(START_END_DATE);
            startDate = startEnd[0];
            endDate = startEnd[1];
        }
        if (getIntent().hasExtra(CATEGORY_ID)) {
            isCategoryFiltered = true;
            String categoryId = getIntent().getStringExtra(CATEGORY_ID);
            category = Category.getCategoryById(categoryId);
        }

        busdailysummaries = new ArrayList<>();
        busdailysummaryAdapter = new BusDailySummaryAdapter(this, busdailysummaries);
        setupRecyclerView();
        setupSwipeToRefresh();



        if (isCategoryFiltered && category != null) {
            BusDailySummaryActivity.this.toolbar.setBackgroundColor(Color.parseColor(category.getColor()));
        }
    }

    private Continuation<Void, Void> onGetTripSheetFinished = new Continuation<Void, Void>() {
        @Override
        public Void then(Task<Void> task) throws Exception {
            if (task.isFaulted()) {
                Log.e(TAG, "Error:", task.getError());
            }

            if (swipeContainer != null) {
                swipeContainer.setRefreshing(false);
            }

            return null;
        }
    };

    private void invalidateViews() {
        busdailysummaryAdapter.clear();
        busdailysummaryAdapter.setIsBackgroundPrimary(!isCategoryFiltered);
        recyclerView.setBackgroundColor(ContextCompat.getColor(this, isCategoryFiltered? R.color.white : R.color.colorPrimaryDark));

        // Check size of group members
        if (Member.getAllAcceptedMembersByGroupId(groupId).size() > 1) {
            busdailysummaryAdapter.setShowMember(true);
        } else {
            busdailysummaryAdapter.setShowMember(false);
        }
        busdailysummaryAdapter.addAll(BusDailySummary.getAllBusDailySummariesByGroupId(groupId));


        if (Helpers.needToSync(syncTimeInMillis)) {
            SyncBusDailySummary.getAllBusDailySummariesByGroupId(groupId);
            syncTimeInMillis = Calendar.getInstance().getTimeInMillis();
            Helpers.saveSyncTime(this, syncTimeKey, syncTimeInMillis);
        }
    }

    private void setupToolbar() {
        toolbar.setContentInsetsAbsolute(0,0);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        titleTextView.setText(getString(R.string.busdailysummary));
        titleTextView.setOnClickListener(v -> close());
        backImageView.setOnClickListener(v -> close());
        extraImageView.setVisibility(View.GONE);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(busdailysummaryAdapter);
        // Add JazzyListView scroll effect
        JazzyRecyclerViewScrollListener jazzyScrollListener = new JazzyRecyclerViewScrollListener();
        recyclerView.addOnScrollListener(jazzyScrollListener);
        jazzyScrollListener.setTransitionEffect(new SlideInEffect());
    }

    private void setupSwipeToRefresh() {
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SyncBusDailySummary.getAllBusDailySummariesByGroupId(groupId).continueWith(onGetTripSheetFinished, Task.UI_THREAD_EXECUTOR);
            }
        });

        swipeContainer.setColorSchemeResources(R.color.colorPrimary);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.busdailysummary_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filter:
//                showFilteringPopUpMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }







    @Override
    public void onResume() {
        super.onResume();
        Realm realm = Realm.getDefaultInstance();
        realm.addChangeListener(v -> invalidateViews());

//        setupMemberFilter(member);
//        setupCategoryFilter(category);

        invalidateViews();
    }

    @Override
    public void onPause() {
        super.onPause();
        Realm realm = Realm.getDefaultInstance();
        realm.removeAllChangeListeners();
    }
}
