package org.osd.omot_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import org.osd.omot_app.data.repository.RepositoryProvider;
import org.osd.omot_app.security.SecurePreferencesManager;
import org.osd.omot_app.ui.auth.LoginActivity;
import org.osd.omot_app.ui.main.MainActivity;

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

    private SecurePreferencesManager spManager;
    private RepositoryProvider provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        // Setup a condition to keep the splash screen on screen.
        splashScreen.setKeepOnScreenCondition(() -> keepSplashOnScreen);

        // Start the app initialization routine.
        initializeApp();
    }

    private void initializeApp() {
        provider = RepositoryProvider.getInstance(this);
        spManager = provider.getSpManager();

        // Simulate initialization work
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // This code will run after a short delay, simulating initialization.

            Class<?> nextActivity = spManager.isUserLoggedIn() ? MainActivity.class :
                    LoginActivity.class;

            // Navigate to the next activity
            Intent intent = new Intent(SplashActivity.this, nextActivity);
            startActivity(intent);

            // Dismiss the splash screen and finish this activity.
            keepSplashOnScreen = false;
            finish();
        }, 1500);
    }
}