package org.osd.omot_app.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.osd.omot_app.R;
import org.osd.omot_app.data.repository.RepositoryProvider;
import org.osd.omot_app.security.SecurePreferencesManager;
import org.osd.omot_app.security.SecurityChecker;
import org.osd.omot_app.ui.auth.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private Button btnLogout;

    private SecurePreferencesManager spManager;
    private boolean isDevelopment = SecurityChecker.isDebugBuild(this) || SecurityChecker.isRunningOnEmulator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Security check on main activity launch
        if (SecurityChecker.detectSecurityThreats(this, isDevelopment)) {
            handleSecurityBreach();
            return;
        }

        RepositoryProvider provider = RepositoryProvider.getInstance(this);
        spManager = provider.getSpManager();

        initializeViews();
        displayWelcomeMessage();
        setupLogout();
    }

    private void initializeViews() {
        tvWelcome = findViewById(R.id.tv_welcome);
        btnLogout = findViewById(R.id.btn_logout);
    }

    private void displayWelcomeMessage() {
        String codename = spManager.getAgentCodename();
        String clearance = spManager.getAgentClearance();

        String welcomeMessage = String.format("Welcome %s\nClearance: %s\nOMOT Terminal Online",
                codename != null ? codename : "Agent", clearance != null ? clearance : "BETA");

        tvWelcome.setText(welcomeMessage);
    }

    private void setupLogout() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spManager.clearLoginSession();
                navigateToLogin();
            }
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    
    private void handleSecurityBreach() {
        spManager.clearLoginSession();
        Toast.makeText(this, "Security breach detected. Logging out.", Toast.LENGTH_SHORT).show();
        navigateToLogin();
    }
}