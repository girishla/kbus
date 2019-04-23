package com.bigmantra.natco.welcome;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.bigmantra.natco.R;
import com.bigmantra.natco.main.MainActivity;
import com.bigmantra.natco.models.User;

/**
 * Created by Girish Lakshmanan on 8/18/19.
 */

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();

    public static void newInstance(Context context) {
        Intent intent = new Intent(context, SplashActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences_session_key), 0);
        String sessionToken = sharedPreferences.getString(User.SESSION_TOKEN, null);

        Log.d(TAG, "sessionToken:" + sessionToken);
        if (TextUtils.isEmpty(sessionToken)) {
            WelcomeActivity.newInstance(this);
        } else {
            MainActivity.newInstance(this);
        }

        finish();
    }
}
