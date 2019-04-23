package com.bigmantra.natco.report;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigmantra.natco.R;
import com.bigmantra.natco.main.BaseActivity;
import com.bigmantra.natco.report.main.ReportFragmentAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Girish Lakshmanan on 8/20/19.
 */

public class ReportActivity extends BaseActivity {
    private static final String TAG = ReportActivity.class.getSimpleName();

    @BindView(R.id.toolbar_id) Toolbar toolbar;
    @BindView(R.id.toolbar_back_image_view_id) ImageView backImageView;
    @BindView(R.id.toolbar_title_text_view_id) TextView titleTextView;
    @BindView(R.id.report_activity_tab_layout_id) TabLayout tabLayout;
    @BindView(R.id.report_activity_view_pager_id) ViewPager viewPager;

    public static void newInstance(Context context) {
        Intent intent = new Intent(context, ReportActivity.class);
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_activity);
        ButterKnife.bind(this);

        setupToolbar();
        setupViewPager(viewPager);
    }

    private void setupToolbar() {
        toolbar.setContentInsetsAbsolute(0,0);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        titleTextView.setText(getString(R.string.report));
        titleTextView.setOnClickListener(v -> close());
        backImageView.setOnClickListener(v -> close());
    }

    private void setupViewPager(ViewPager viewPager) {
        ReportFragmentAdapter adapter = new ReportFragmentAdapter(getFragmentManager());

        adapter.addFragment(ReportFragment.newInstance(ReportFragment.WEEKLY), "Weekly");
        adapter.addFragment(ReportFragment.newInstance(ReportFragment.MONTHLY), "Monthly");
        adapter.addFragment(ReportFragment.newInstance(ReportFragment.YEARLY), "Yearly");
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

        viewPager.setCurrentItem(ReportFragment.MONTHLY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.report_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                close();
                break;
        }

        return true;
    }
}
