package org.obsidian.omot.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import org.obsidian.omot.R;
import org.obsidian.omot.domain.SessionManager;
import org.obsidian.omot.ui.auth.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            SessionManager sm = new SessionManager(this);
            if (sm.isSessionActive()) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
        }, 2000);
    }
}