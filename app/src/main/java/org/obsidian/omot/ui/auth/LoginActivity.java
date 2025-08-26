package org.obsidian.omot.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import org.obsidian.omot.OMOTApp;
import org.obsidian.omot.R;
import org.obsidian.omot.core.util.Result;
import org.obsidian.omot.data.db.dao.AgentDAO;
import org.obsidian.omot.data.repo.AgentRepository;
import org.obsidian.omot.domain.AuthResult;
import org.obsidian.omot.domain.AuthService;
import org.obsidian.omot.domain.BiometricAuth;
import org.obsidian.omot.domain.SessionManager;
import org.obsidian.omot.ui.MainActivity;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText inputCodename, inputCipher;
    private TextView loginStatus;
    private Button btnLogin, btnBiometric;
    private AuthService authService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputCodename = findViewById(R.id.input_codename);
        inputCipher = findViewById(R.id.input_cipher);
        loginStatus = findViewById(R.id.login_status);
        btnLogin = findViewById(R.id.btn_login);
        btnBiometric = findViewById(R.id.btn_biometric);

        authService = new AuthService(new AgentRepository(new AgentDAO(OMOTApp.db())));
        sessionManager = new SessionManager(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLogin();
            }
        });

        btnBiometric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doBiometric();
            }
        });
    }

    private void doLogin() {
        String codename = inputCodename.getText().toString().trim();
        String cipher = inputCipher.getText().toString().trim();

        Result<AuthResult> result = authService.login(codename, cipher);
        if (result.isSuccess()) {
            AuthResult ar = result.getData();
            sessionManager.startSession(ar.getAgentId());
            goToMain(ar);
        } else {
            loginStatus.setText(result.getError().getMessage());
        }
    }

    private void doBiometric() {
        // For demonstration assume agentId loaded elsewhere
        String agentId = "TEMP";
        BiometricAuth.authenticate(this, new BiometricAuth.Callback() {
            @Override
            public void onSuccess() {
                sessionManager.startSession(agentId);
                goToMain(new AuthResult(agentId, "BiometricAgent", "OMEGA"));
            }

            @Override
            public void onFailure(String reason) {
                loginStatus.setText(reason);
            }
        }, agentId);
    }

    private void goToMain(AuthResult ar) {
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        i.putExtra("AGENT_ID", ar.getAgentId());
        i.putExtra("CLEARANCE", ar.getClearance());
        startActivity(i);
        finish();
    }
}