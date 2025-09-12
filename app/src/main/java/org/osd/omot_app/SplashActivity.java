package org.osd.omot_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import org.osd.omot_app.data.repository.RepositoryProvider;
import org.osd.omot_app.security.SecurePreferencesManager;
import org.osd.omot_app.security.SecurityChecker;
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

    private static final String TAG = "SplashActivity";
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

        // Perform security check before any other initialization
        boolean isEnvironmentSecure = SecurityChecker.performSecurityCheck(this, spManager);

        if (!isEnvironmentSecure) {
            // Security threat detected - abort app launch
            handleSecurityBreach();
            return;
        }

        // Security check passed - continue with normal initialization
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Class<?> nextActivity;

            if (spManager != null && spManager.isUserLoggedIn()) {
                nextActivity = MainActivity.class;
            } else {
                nextActivity = LoginActivity.class;
            }

            // Navigate to the next activity
            Intent intent = new Intent(SplashActivity.this, nextActivity);
            startActivity(intent);

            // Dismiss the splash screen and finish this activity.
            keepSplashOnScreen = false;
            finish();
        }, 1500);
    }

    /**
     * Handles the scenario when a security threat is detected.
     * Shows a security warning and prevents further app operation.
     */
    private void handleSecurityBreach() {
        // Show a security warning message (could be a custom dialog or activity)
        Log.e(TAG, "SECURITY BREACH - Application launch aborted");

        // In a real scenario, you might want to show a security warning screen
        // and then close the app, or implement a limited functionality mode.

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Show security warning and close app
            Toast.makeText(this, "Security breach detected. Application terminated.", Toast.LENGTH_LONG).show();
            finishAffinity(); // Close all activities and exit app
        }, 2000);
    }
}