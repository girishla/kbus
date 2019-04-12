package com.expensemanager.app.tripsheet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.expensemanager.app.R;
import com.expensemanager.app.main.BaseActivity;
import com.expensemanager.app.models.User;

public class ProfileTripSheetActivity extends BaseActivity {
    private static final String TAG = ProfileTripSheetActivity.class.getSimpleName();

    private static final String USER_ID = "userId";
    private User user;

    public static void newInstance(Context context, String userId) {
        Intent intent = new Intent(context, ProfileTripSheetActivity.class);
        intent.putExtra(USER_ID, userId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_expense_activity);

        String userId = getIntent().getStringExtra(USER_ID);
        user = User.getUserById(userId);
    }
}
