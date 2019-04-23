package com.bigmantra.natco.expense;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bigmantra.natco.R;
import com.bigmantra.natco.main.BaseActivity;
import com.bigmantra.natco.models.User;

public class ProfileExpenseActivity extends BaseActivity {
    private static final String TAG = ProfileExpenseActivity.class.getSimpleName();

    private static final String USER_ID = "userId";
    private User user;

    public static void newInstance(Context context, String userId) {
        Intent intent = new Intent(context, ProfileExpenseActivity.class);
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
