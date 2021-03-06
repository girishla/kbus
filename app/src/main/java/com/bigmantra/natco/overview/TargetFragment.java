package com.bigmantra.natco.overview;

import android.app.Fragment;
import android.graphics.drawable.ClipDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bigmantra.natco.R;
import com.bigmantra.natco.helpers.Helpers;
import com.bigmantra.natco.models.BusDailySummary;
import com.bigmantra.natco.models.Group;
import com.bigmantra.natco.service.SyncBusDailySummary;
import com.bigmantra.natco.service.SyncCategory;
import com.bigmantra.natco.service.SyncExpense;
import com.bigmantra.natco.service.SyncGroup;
import com.bigmantra.natco.service.SyncMember;

import java.util.Date;

import bolts.Continuation;
import bolts.Task;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Girish Lakshmanan on 9/16/19.
 */

public class TargetFragment extends Fragment implements FragmentLifecycle {
    private static final String TAG = TargetFragment.class.getSimpleName();

    private int levelTotal = 10000;
    private int levelStatus;
    private int level;
    private int steps = 100;
    private int animationTime = 1500;
    private double collectiontargetMonthly;
    private double amountLeftMonthly;
    private double collectiontargetWeekly;
    private double amountLeftWeekly;
    private double monthlyExpense;
    private double weeklyExpense;
    private String groupId;
    private long lastTimeAnimateLevel = 0;
    private boolean isFirstAnimation = true;

    private ClipDrawable clipDrawable;
    private Handler handler = new Handler();

    @BindView(R.id.circle_solid_view_id) View collectiontargetView;
    @BindView(R.id.circle_amount_text_view_id) TextView circleAmountTextView;
    @BindView(R.id.monthly_amount_text_view_id) TextView monthlyAmountTextView;
    @BindView(R.id.weekly_amount_text_view_id) TextView weeklyAmountTextView;
    @BindView(R.id.circle_month_text_view_id) TextView circleMonthTextView;

    public static Fragment newInstance() {
        return new TargetFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.collectiontarget_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        groupId = Helpers.getCurrentGroupId();
    }

    public void invalidateViews() {
        Log.d(TAG, "invalidateViews");
        if (groupId == null) {
            groupId = Helpers.getCurrentGroupId();
        }

        Group group = Group.getGroupById(groupId);

        if (group != null) {
            collectiontargetMonthly = group.getMonthlyBudget();
            collectiontargetWeekly = group.getWeeklyBudget();
        } else {
            if (groupId != null) {
                SyncGroup.getGroupById(groupId).continueWith(onGetGroupsFinished, Task.UI_THREAD_EXECUTOR);
            } else {
                Log.e(TAG, "groupId is null");
            }
        }

        monthlyAmountTextView.setText(Helpers.doubleToCurrency(collectiontargetMonthly));
        weeklyAmountTextView.setText(Helpers.doubleToCurrency(collectiontargetWeekly));

        monthlyExpense = getMonthlyExpense();
        weeklyExpense = getWeeklyExpense();

        amountLeftMonthly = collectiontargetMonthly - monthlyExpense;
        amountLeftWeekly = collectiontargetWeekly - weeklyExpense;

        circleAmountTextView.setText(Helpers.doubleToCurrency(amountLeftMonthly));
        circleMonthTextView.setText(Helpers.getShortMonthStringOnlyFromDate(new Date()));

        invalidateProgressBars();
    }

    private void invalidateProgressBars() {

        if (collectiontargetMonthly == 0) {
            level = levelTotal;
        } else {
            level = (int) (Math.min(monthlyExpense, collectiontargetMonthly) / collectiontargetMonthly * levelTotal);
        }

        levelStatus = levelTotal;
        clipDrawable = (ClipDrawable) collectiontargetView.getBackground();
        clipDrawable.setLevel(levelTotal);
        long currentTime = System.currentTimeMillis();

        Log.d(TAG, "level: " + level + ", level status: " + levelStatus + ", level total: "
                + levelTotal + "lastTime:" + lastTimeAnimateLevel + "currentTime:" + currentTime);
        if (currentTime - lastTimeAnimateLevel > 3000 && level != levelTotal) {
            Log.d(TAG, "start animation.");
            isFirstAnimation = false;
            lastTimeAnimateLevel = currentTime;
            handler.removeCallbacks(animateRunnable);
            handler.postDelayed(animateRunnable, 500);
        } else {
            Log.d(TAG, "last animation less than 3 seconds, cancel.");
        }
    }

    private Continuation<Void, Void> onGetGroupsFinished = new Continuation<Void, Void>() {
        @Override
        public Void then(Task<Void> task) throws Exception {
            if (task.isFaulted()) {
                Log.e(TAG, "Error:", task.getError());
            }

            if (groupId != null) {
                // Sync all categories of current group
                SyncCategory.getAllCategoriesByGroupId(groupId);
                // Sync all expenses of current group
                SyncExpense.getAllExpensesByGroupId(groupId);
                // Sync all members of current group
                SyncMember.getMembersByGroupId(groupId);
                SyncBusDailySummary.getAllBusDailySummariesByGroupId(groupId);
            }

            invalidateViews();
            return null;
        }
    };

    private double getWeeklyExpense() {
        Date currentDate = new Date();
        Date[] weekStartEnd = Helpers.getWeekStartEndDate(currentDate);
        RealmResults<BusDailySummary> weeklyExpenses = BusDailySummary.getBusDailySummariesByRangeAndGroupId(weekStartEnd, groupId);

        double weeklyTotal = 0;
        for (BusDailySummary summary : weeklyExpenses) {
            weeklyTotal += summary.getTotalCollection();
        }

        return Math.round(weeklyTotal * 100.0) / 100.0;
    }

    private double getMonthlyExpense() {
        Date currentDate = new Date();
        Date[] monthStartEnd = Helpers.getMonthStartEndDate(currentDate);
        RealmResults<BusDailySummary> monthlyExpenses = BusDailySummary.getBusDailySummariesByRangeAndGroupId(monthStartEnd, groupId);

        double monthlyTotal = 0;
        for (BusDailySummary summary : monthlyExpenses) {
            monthlyTotal += summary.getTotalCollection();
        }

        return Math.round(monthlyTotal * 100.0) / 100.0;
    }

    private Runnable animateRunnable = new Runnable() {
        @Override
        public void run() {
            animateClipDrawable(animationTime);
        }
    };

    private void animateClipDrawable(int milliseconds) {
        if (levelStatus <= level) {
            handler.removeCallbacks(animateRunnable);
            return;
        }

        int stepTime = milliseconds / steps;
        levelStatus -= level/steps;
        clipDrawable.setLevel(levelStatus);

        handler.postDelayed(animateRunnable, stepTime);
    }

    @Override
    public void onPauseFragment() {
        Log.d(TAG, "onPauseFragment()");
        handler.removeCallbacks(animateRunnable);
    }

    @Override
    public void onResumeFragment() {
        Log.d(TAG, "onResumeFragment()");
        if (!isFirstAnimation) {
            invalidateProgressBars();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Realm realm = Realm.getDefaultInstance();
        realm.addChangeListener(v -> {
            Log.d(TAG, "realmChanged");
            invalidateViews();
        });

        invalidateViews();

    }

    @Override
    public void onPause() {
        super.onPause();
        Realm realm = Realm.getDefaultInstance();
        realm.removeAllChangeListeners();

    }
}
