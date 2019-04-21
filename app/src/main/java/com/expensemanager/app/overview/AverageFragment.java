package com.expensemanager.app.overview;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.expensemanager.app.R;
import com.expensemanager.app.helpers.Helpers;
import com.expensemanager.app.models.BusDailySummary;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Girish Lakshmanan on 9/16/19.
 */

public class AverageFragment extends Fragment implements FragmentLifecycle {
    private static final String TAG = AverageFragment.class.getSimpleName();

    public static int SLEEP_LENGTH = 300;

    private Handler handler = new Handler();
    private int totalStatus = 0;
    private int weeklyStatus = 0;
    private int monthlyStatus = 0;
    private String groupId;
    private String oldGroupId;
    private double totalBusDailySummary = 0.0;
    private double weeklyBusDailySummary = 0.0;
    private double weeklyAve = 0.0;
    private double monthlyBusDailySummary = 0.0;
    private double monthlyAve = 0.0;
    private double oldWeeklyBusDailySummary = 0.0;
    private double oldMonthlyBusDailySummary = 0.0;

    @BindView(R.id.average_fragment_total_text_view_id) TextView totalTextView;
    @BindView(R.id.average_fragment_weekly_total_text_view_id) TextView weeklyTextView;
    @BindView(R.id.average_fragment_monthly_total_text_view_id) TextView monthlyTextView;
    @BindView(R.id.average_fragment_weekly_circular_progress_text) TextView weeklyProgressTextView;
    @BindView(R.id.average_fragment_monthly_circular_progress_text) TextView monthlyProgressTextView;
    @BindView(R.id.average_fragment_total_progressBar) ProgressBar totalProgressBar;
    @BindView(R.id.average_fragment_weekly_progressBar) ProgressBar weeklyProgressBar;
    @BindView(R.id.average_fragment_monthly_progressBar) ProgressBar monthlyProgressBar;

    public static Fragment newInstance() {
        return new AverageFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.average_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    public void invalidateViews() {
        groupId = Helpers.getCurrentGroupId();

        totalBusDailySummary = getTotalBusDailySummary();
        weeklyBusDailySummary = getWeeklyBusDailySummary();
        weeklyAve = getWeeklyAverage();
        monthlyBusDailySummary = getMonthlyBusDailySummary();
        monthlyAve = getMonthlyAverage();

        weeklyTextView.setText(Helpers.doubleToCurrency(weeklyBusDailySummary));
        monthlyTextView.setText(Helpers.doubleToCurrency(monthlyBusDailySummary));

        invalidateProgressBars();
    }

    private void invalidateProgressBars() {
        if (weeklyBusDailySummary == 0) {
            weeklyProgressBar.setProgress(0);
            weeklyTextView.setText("$0");
        }

        if (monthlyBusDailySummary == 0) {
            monthlyProgressBar.setProgress(0);
            monthlyTextView.setText("$0");
        }

        if (oldWeeklyBusDailySummary == weeklyBusDailySummary && oldMonthlyBusDailySummary == monthlyBusDailySummary
                && groupId != null && groupId.equals(oldGroupId)) {
            return;
        }

        totalStatus = 0;
        weeklyStatus = 0;
        monthlyStatus = 0;

        int weeklyProgress = 0;
        int monthlyProgress = 0;

        if (weeklyAve != 0) {
            weeklyProgress = (int)(weeklyBusDailySummary / weeklyAve * 100);
        }

        if (monthlyAve != 0) {
            monthlyProgress = (int) (monthlyBusDailySummary / monthlyAve * 100);
        }

        setupTotalProgress(totalBusDailySummary);
        setupWeeklyProgress(weeklyProgress);
        setupMonthlyProgress(monthlyProgress);
        oldWeeklyBusDailySummary = weeklyBusDailySummary;
        oldGroupId = groupId;
    }

    private void setupTotalProgress(final double totalProgress) {
        totalProgressBar.setMax((int)totalProgress);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(SLEEP_LENGTH);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int step = 1;
                if (totalProgress >= 100) {
                    step = (int) (totalProgress/100);
                }

                final int finalStep = step;
                while (totalStatus < totalProgress) {
                    totalStatus += step;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            totalProgressBar.setProgress(totalStatus);

                            if (totalStatus + finalStep >= totalProgress) {
                                if (totalProgress >1000) {
                                    String totalString = Helpers.doubleToCurrency(totalProgress);
                                    int lastDotIndex = totalString.lastIndexOf(".");
                                    String totalResult;
                                    if (lastDotIndex != -1) {
                                        totalResult = totalString.substring(0, lastDotIndex);
                                    } else {
                                        totalResult = totalString;
                                    }

                                    totalTextView.setText(totalResult);
                                } else {
                                    totalTextView.setText(Helpers.doubleToCurrency(totalProgress));
                                }
                                totalProgressBar.setProgress((int)totalProgress);
                            } else {
                                totalTextView.setText("₹" + totalStatus);
                            }
                        }
                    });
                    try {
                        Thread.sleep(15);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void setupWeeklyProgress(int weeklyProgress) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(SLEEP_LENGTH);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (weeklyStatus < weeklyProgress) {
                    weeklyStatus += 1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            weeklyProgressBar.setProgress(weeklyStatus);
                            weeklyProgressTextView.setText(weeklyStatus + "%");
                        }
                    });
                    try {
                        Thread.sleep(15);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void setupMonthlyProgress(int monthlyProgress) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(SLEEP_LENGTH);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                while (monthlyStatus < monthlyProgress) {
                    monthlyStatus += 1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            monthlyProgressBar.setProgress(monthlyStatus);
                            monthlyProgressTextView.setText(monthlyStatus + "%");
                        }
                    });
                    try {
                        Thread.sleep(15);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private double getWeeklyBusDailySummary() {
        Date currentDate = new Date();
        Date[] weekStartEnd = Helpers.getWeekStartEndDate(currentDate);
        RealmResults<BusDailySummary> weeklyBusDailySummaries = BusDailySummary.getBusDailySummariesByRangeAndGroupId(weekStartEnd, groupId);

        double weeklyTotal = 0;
        for (BusDailySummary summary : weeklyBusDailySummaries) {
            weeklyTotal += summary.getTotalCollection();
        }

        return Math.round(weeklyTotal * 100.0) / 100.0;
    }

    private double getMonthlyBusDailySummary() {
        Date currentDate = new Date();
        Date[] monthStartEnd = Helpers.getMonthStartEndDate(currentDate);
        RealmResults<BusDailySummary> monthlyBusDailySummaries = BusDailySummary.getBusDailySummariesByRangeAndGroupId(monthStartEnd, groupId);

        double monthlyTotal = 0;
        for (BusDailySummary summary : monthlyBusDailySummaries) {
            monthlyTotal += summary.getTotalCollection();
        }

        return Math.round(monthlyTotal * 100.0) / 100.0;
    }

    private double getWeeklyAverage() {
        List<Date[]> allWeeks = Helpers.getAllWeeks(groupId);

        if (allWeeks == null) {
            return 0;
        }

        int weeks = allWeeks.size();
        return getTotalBusDailySummary()/weeks;
    }

    private double getMonthlyAverage() {
        List<Date[]> allMonths = Helpers.getAllMonths(groupId);

        if (allMonths == null) {
            return 0;
        }
        int months = allMonths.size();
        return getTotalBusDailySummary()/months;
    }

    private double  getTotalBusDailySummary() {
        double total = 0.0;

        RealmResults<BusDailySummary> allBusDailySummaries = BusDailySummary.getAllBusDailySummariesByGroupId(groupId);
        for (BusDailySummary summary : allBusDailySummaries) {
            total += summary.getTotalCollection();
        }

        return (double) Math.round(total * 100) / 100;
    }

    @Override
    public void onPauseFragment() {
        Log.d(TAG, "onPauseFragment()");
    }

    @Override
    public void onResumeFragment() {
        Log.d(TAG, "onResumeFragment()");
        invalidateProgressBars();
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
