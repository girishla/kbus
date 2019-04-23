package com.bigmantra.natco.welcome;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.bigmantra.natco.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WelcomeActivity extends AppCompatActivity {
    private static final String TAG = WelcomeActivity.class.getSimpleName();

    @BindView(R.id.welcome_activity_sign_up_button_id) Button signupButton;
    @BindView(R.id.welcome_activity_login_button_id) Button loginButton;

    public static void newInstance(Context context) {
        Intent intent = new Intent(context, WelcomeActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);
        ButterKnife.bind(this);


        signupButton.setVisibility(View.GONE);

        signupButton.setOnClickListener(v -> {
            SignUpActivity.newInstance(this);
        });

        loginButton.setOnClickListener(v -> {
            LoginActivity.newInstance(this);
        });
    }
}
