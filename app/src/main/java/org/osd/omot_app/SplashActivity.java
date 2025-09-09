package org.osd.omot_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

/**
 * The initial entry point of the OMOT application.
 * This activity displays the branded splash screen while performing
 * critical, brief initialization checks before proceeding to the
 * authentication gate or main terminal.
 */
public class SplashActivity extends AppCompatActivity {

    // Controls how long the splash screen is kept visible.
    // Set to 'true' to keep it on screen, 'false' to dismiss.
    private boolean keepSplashOnScreen = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        // Setup a condition to keep the splash screen on screen.
        splashScreen.setKeepOnScreenCondition(() -> keepSplashOnScreen);

        // Start the app initialization routine.
        initializeApp();
    }

    private void initializeApp() {
        // Simulate initialization work (e.g., checking for existing session, security checks).
        // In a real scenario, this might involve reading from EncryptedSharedPreferences.
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // This code will run after a short delay, simulating initialization.

            // TODO: Replace this with actual logic.
            // Example logic to check if a user is already logged in:
            // boolean isLoggedIn = checkUserSession();
            // Class<?> nextActivity = isLoggedIn ? MainActivity.class : LoginActivity.class;

            Class<?> nextActivity = LoginActivity.class;

            // Navigate to the next activity
            Intent intent = new Intent(SplashActivity.this, nextActivity);
            startActivity(intent);

            // Dismiss the splash screen and finish this activity.
            keepSplashOnScreen = false;
            finish();
        }, 1500);
    }
}