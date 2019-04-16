package com.expensemanager.app.overview;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v13.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.expensemanager.app.R;
import com.expensemanager.app.helpers.Helpers;
import com.expensemanager.app.models.Expense;
import com.expensemanager.app.models.Member;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Girish Lakshmanan on 8/27/19.
 */

public class OverviewMainFragment extends Fragment {
    private static final String TAG = OverviewMainFragment.class.getSimpleName();

    public static int SLEEP_LENGTH = 1200;

    private String groupId;
    private ArrayList<Expense> expenses;
    private OverviewAdapter overviewAdapter;
    private OverviewFragmentAdapter overviewFragmentAdapter;
    private boolean isPersonal = false;

    @BindView(R.id.overview_fragment_scroll_view_id) ScrollView scrollView;
    @BindView(R.id.overview_fragment_view_pager_id) ViewPager viewPager;
    @BindView(R.id.overview_fragment_pager_indicator_container_id) LinearLayout mLinearLayout;
    @BindView(R.id.overview_fragment_recycler_view_id) RecyclerView recyclerView;

    public static OverviewMainFragment newInstance() {
        return new OverviewMainFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.overview_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        expenses = new ArrayList<>();
        overviewAdapter = new OverviewAdapter(getActivity(), expenses);
        setupToolbar();

        setupViewPager(viewPager);
    }

    public void invalidateViews() {
        groupId = Helpers.getCurrentGroupId();

        overviewAdapter.clear();

        if (Member.getAllAcceptedMembersByGroupId(groupId).size() > 1) {
            overviewAdapter.setShowMember(true);
            isPersonal = false;
        } else {
            overviewAdapter.setShowMember(false);
            isPersonal = true;
        }

        overviewAdapter.addAll(Expense.getAllExpensesByGroupId(groupId));
        setupRecyclerView();
        scrollView.fullScroll(ScrollView.FOCUS_UP);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.main_activity_toolbar_id);
        TextView titleTextView = (TextView) toolbar.findViewById(R.id.main_activity_toolbar_title_text_view_id);
        titleTextView.setText(getString(R.string.nav_header));
        ViewCompat.setElevation(toolbar, getResources().getInteger(R.integer.toolbar_elevation));
    }

    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity()){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recyclerView.setFocusable(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(overviewAdapter);

        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
        params.height = (int)getResources().getDimension(isPersonal?
                R.dimen.overview_recycler_view_height_personal : R.dimen.overview_recycler_view_height_group);
        recyclerView.setLayoutParams(params);
    }

    private void setupViewPager(ViewPager viewPager) {
        overviewFragmentAdapter = new OverviewFragmentAdapter(getFragmentManager());

        overviewFragmentAdapter.addFragment(BudgetFragment.newInstance(), "Monthly");
        overviewFragmentAdapter.addFragment(AverageFragment.newInstance(), "Average");
        viewPager.setAdapter(overviewFragmentAdapter);
        viewPager.setOnPageChangeListener(pageChangeListener);

        CustomPageIndicator viewPagerIndicator = new CustomPageIndicator(getActivity(), mLinearLayout,
                viewPager, R.drawable.indicator_circle_accent);
        viewPagerIndicator.setPageCount(overviewFragmentAdapter.getCount());
        viewPagerIndicator.setSpacingRes(R.dimen.space_medium);
        viewPagerIndicator.show();
    }

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {

        int currentPosition = 0;

        @Override
        public void onPageSelected(int newPosition) {

            FragmentLifecycle fragmentToShow = (FragmentLifecycle)overviewFragmentAdapter.getItem(newPosition);
            fragmentToShow.onResumeFragment();

            FragmentLifecycle fragmentToHide = (FragmentLifecycle)overviewFragmentAdapter.getItem(currentPosition);
            fragmentToHide.onPauseFragment();

            currentPosition = newPosition;
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) { }

        public void onPageScrollStateChanged(int arg0) { }
    };

    private double getWeeklyExpense() {
        Date currentDate = new Date();
        Date[] weekStartEnd = Helpers.getWeekStartEndDate(currentDate);
        RealmResults<Expense> weeklyExpenses = Expense.getExpensesByRangeAndGroupId(weekStartEnd, groupId);

        double weeklyTotal = 0;
        for (Expense expense : weeklyExpenses) {
            weeklyTotal += expense.getAmount();
        }

        return Math.round(weeklyTotal * 100.0) / 100.0;
    }

    private double getMonthlyExpense() {
        Date currentDate = new Date();
        Date[] monthStartEnd = Helpers.getMonthStartEndDate(currentDate);
        RealmResults<Expense> monthlyExpenses = Expense.getExpensesByRangeAndGroupId(monthStartEnd, groupId);

        double monthlyTotal = 0;
        for (Expense expense : monthlyExpenses) {
            monthlyTotal += expense.getAmount();
        }

        return Math.round(monthlyTotal * 100.0) / 100.0;
    }

    private double getWeeklyAverage() {
        List<Date[]> allWeeks = Helpers.getAllWeeks(groupId);

        if (allWeeks == null) {
            return 0;
        }

        int weeks = allWeeks.size();
        return getTotalExpense()/weeks;
    }

    private double getMonthlyAverage() {
        List<Date[]> allMonths = Helpers.getAllMonths(groupId);

        if (allMonths == null) {
            return 0;
        }
        int months = allMonths.size();
        return getTotalExpense()/months;
    }

    private double  getTotalExpense() {
        double total = 0.0;

        RealmResults<Expense> allExpenses = Expense.getAllExpensesByGroupId(groupId);
        for (Expense expense : allExpenses) {
            total += expense.getAmount();
        }

        return (double) Math.round(total * 100) / 100;
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
}
