package com.bigmantra.natco.tripsheet;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigmantra.natco.R;
import com.bigmantra.natco.models.BusDailySummary;
import com.bigmantra.natco.service.SyncBusDailySummary;

import com.bigmantra.natco.helpers.Helpers;
import com.bigmantra.natco.models.Category;
import com.bigmantra.natco.models.Member;
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

public class BusDailySummaryFragment extends Fragment {
    private static final String TAG = BusDailySummaryFragment.class.getSimpleName();


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
    private Toolbar toolbar;
    private ImageView extraImageView;
    private TextView titleTextView;

    private BusDailySummaryAdapter busdailysummaryAdapter;

    @BindView(R.id.busdailysummary_fragment_recycler_view_id) RecyclerView recyclerView;
    @BindView(R.id.swipeContainer_id) SwipeRefreshLayout swipeContainer;

    public static BusDailySummaryFragment newInstance() {
        return new BusDailySummaryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
        setUserVisibleHint(true);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.busdailysummary_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        groupId = Helpers.getCurrentGroupId();
        syncTimeKey = Helpers.getSyncTimeKey(TAG, groupId);
        syncTimeInMillis = Helpers.getSyncTimeInMillis(syncTimeKey);

        busdailysummaries = new ArrayList<>();
        busdailysummaryAdapter = new BusDailySummaryAdapter(getActivity(), busdailysummaries);

        if(BusDailySummary.getAllBusDailySummariesByGroupId(groupId).size()<=0){
            SyncBusDailySummary.getAllBusDailySummariesByGroupId(groupId).continueWith(onGetTripSheetFinished, Task.UI_THREAD_EXECUTOR);
        }


        setupToolbar();
        setupRecyclerView();
        setupSwipeToRefresh();
    }

    public void invalidateViews() {
        busdailysummaryAdapter.clear();
        busdailysummaryAdapter.setIsBackgroundPrimary(!isCategoryFiltered);
        recyclerView.setBackgroundColor(ContextCompat.getColor(getActivity(), isCategoryFiltered? R.color.white : R.color.colorPrimaryDark));

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
            Helpers.saveSyncTime(getActivity(), syncTimeKey, syncTimeInMillis);
        }
    }

    private void setupToolbar() {
        this.toolbar = (Toolbar) getActivity().findViewById(R.id.main_activity_toolbar_id);
        this.titleTextView = (TextView) toolbar.findViewById(R.id.main_activity_toolbar_title_text_view_id);
        this.extraImageView = (ImageView) toolbar.findViewById(R.id.main_activity_toolbar_extra_image_view_id);
        ViewCompat.setElevation(toolbar, getResources().getInteger(R.integer.toolbar_elevation));
    }

    private void updateToolbar() {

    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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

    private Continuation<Void, Void> onGetTripSheetFinished = new Continuation<Void, Void>() {
        @Override
        public Void then(Task<Void> task) throws Exception {
            if (task.isFaulted()) {
                Log.e(TAG, "Error:", task.getError());
            }

            if (swipeContainer != null) {
                swipeContainer.setRefreshing(false);

            }

//            busdailysummaryAdapter.notifyDataSetChanged();
            invalidateViews();

            return null;
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.busdailysummary_menu, menu);
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);
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


        invalidateViews();
    }

    @Override
    public void onPause() {
        super.onPause();
        Realm realm = Realm.getDefaultInstance();
        realm.removeAllChangeListeners();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (toolbar != null) {
            int background = ContextCompat.getColor(getActivity(), R.color.colorPrimary);
            toolbar.setBackgroundColor(background);
        }

        if (extraImageView != null) {
            extraImageView.setVisibility(View.GONE);
        }
    }
}
