package org.osd.omot_app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.osd.omot_app.R;
import org.osd.omot_app.data.repository.AgentRepository;
import org.osd.omot_app.data.repository.RepositoryProvider;
import org.osd.omot_app.data.results.RegistrationResult;
import org.osd.omot_app.utils.UIFeedback;

public class RegistrationActivity extends AppCompatActivity {

    private TextInputLayout edLayoutCodename, edLayoutCipherKey, edLayoutConfirm, edLayoutQuestion, edLayoutAnswer;
    private TextInputEditText edCodename, edCipherKey, edConfirmCipherKey, edSecurityQuestion, edSecurityAnswer;
    private MaterialCheckBox chkBiometric, chkTerms;
    private MaterialButton btnSubmit;
    private TextView tvBackToLogin;

    private RepositoryProvider provider;
    private AgentRepository agentRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        initializeDependencies();
        setupClickListener();
    }

    // ----------------------------------------
    // ---------- Overridden methods ----------
    // ----------------------------------------

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up if needed
    }


    // --------------------------------------
    // ---------- onCreate methods ----------
    // --------------------------------------

    private void initializeViews() {
        edLayoutCodename = findViewById(R.id.ed_layout_codename);
        edLayoutCipherKey = findViewById(R.id.ed_layout_cipher_key);
        edLayoutConfirm = findViewById(R.id.ed_layout_confirm);
        edLayoutQuestion = findViewById(R.id.ed_layout_question);
        edLayoutAnswer = findViewById(R.id.ed_layout_answer);
        edCodename = findViewById(R.id.ed_codename);
        edCipherKey = findViewById(R.id.ed_cipher_key);
        edConfirmCipherKey = findViewById(R.id.ed_confirm_cipher_key);
        edSecurityQuestion = findViewById(R.id.ed_security_question);
        edSecurityAnswer = findViewById(R.id.ed_security_answer);
        chkBiometric = findViewById(R.id.chk_biometric);
        chkTerms = findViewById(R.id.chk_terms);
        btnSubmit = findViewById(R.id.btn_submit);
        tvBackToLogin = findViewById(R.id.tv_back_to_login);
    }

    private void initializeDependencies() {
        provider = RepositoryProvider.getInstance(this);
        agentRepository = provider.getAgentRepository();
    }

    private void setupClickListener() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegistration();
            }
        });

        tvBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToLogin();
            }
        });
    }

    // -------------------------------------
    // ---------- Private methods ----------
    // -------------------------------------

    private void attemptRegistration() {
        // Get input values
        String codename = edCodename.getText().toString().trim();
        String cipherKey = edCipherKey.getText().toString().trim();
        String confirmCipherKey = edConfirmCipherKey.getText().toString().trim();
        String securityQuestion = edSecurityQuestion.getText().toString().trim();
        String securityAnswer = edSecurityAnswer.getText().toString().trim();
        boolean enableBiometric = chkBiometric.isChecked();
        boolean acceptedTerms = chkTerms.isChecked();

        // Clear previous errors
        clearAllErrors();

        // Validate inputs
        if (!validateInputs(codename, cipherKey, confirmCipherKey, securityQuestion,
                securityAnswer, acceptedTerms)) {
            return;
        }

        // Show loading state
        setLoadingState(true);

        // Attempt registration on background thread
        new Thread(() -> {
            try {
                Thread.sleep(1000); // Simulate processing delay

                runOnUiThread(() -> {
                    performRegistration(codename, cipherKey, securityQuestion, securityAnswer, enableBiometric);
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                runOnUiThread(() -> {
                    handleRegistrationError(getString(R.string.registration_error_generic));
                });
            }
        }).start();
    }

    private boolean validateInputs(String codename, String cipherKey, String confirmCipherKey,
                                   String securityQuestion, String securityAnswer, boolean acceptedTerms) {
        boolean isValid = true;

        if (codename.isEmpty()) {
            edLayoutCodename.setError(getString(R.string.validation_field_required));
            isValid = false;
        }

        if (cipherKey.isEmpty()) {
            edLayoutCipherKey.setError(getString(R.string.validation_field_required));
            isValid = false;
        } else if (cipherKey.length() < 8) {
            edLayoutCipherKey.setError(getString(R.string.validation_field_required));
            isValid = false;
        }

        if (confirmCipherKey.isEmpty()) {
            edLayoutConfirm.setError(getString(R.string.validation_field_required));
            isValid = false;
        } else if (!confirmCipherKey.equals(cipherKey)) {
            edLayoutConfirm.setError(getString(R.string.validation_field_required));
            isValid = false;
        }

        if (securityQuestion.isEmpty()) {
            edLayoutQuestion.setError(getString(R.string.validation_field_required));
            isValid = false;
        }

        if (securityAnswer.isEmpty()) {
            edLayoutAnswer.setError(getString(R.string.validation_field_required));
            isValid = false;
        }

        if (!acceptedTerms) {
            UIFeedback.showErrorSnackbar(btnSubmit, getString(R.string.registration_error_terms));
            isValid = false;
        }

        return isValid;
    }

    private void clearAllErrors() {
        edLayoutCodename.setError(null);
        edLayoutCipherKey.setError(null);
        edLayoutConfirm.setError(null);
        edLayoutQuestion.setError(null);
        edLayoutAnswer.setError(null);
    }

    private void performRegistration(String codename, String password, String securityQuestion,
                                     String securityAnswer, boolean enableBiometric) {
        try {
            RegistrationResult result = agentRepository.registerAgent(codename, password,
                    securityQuestion, securityAnswer, enableBiometric);

            if (result.isSuccess()) {
                UIFeedback.showSuccessSnackbar(btnSubmit, getString(R.string.registration_success));
                // Auto-login after successful registration]
                navigateToLoginWithPrefilledCodename(codename);
            } else {
                handleRegistrationError(getString(R.string.registration_error_generic));
            }
        } catch (Exception e) {
            handleRegistrationError(getString(R.string.registration_error_generic));
        } finally {
            setLoadingState(false);
        }
    }

    private void setLoadingState(boolean isLoading) {
        btnSubmit.setEnabled(!isLoading);
        btnSubmit.setText(isLoading ? getString(R.string.processing) : getString(R.string.button_registration));
    }

    private void handleRegistrationError(String errorMessage) {
        UIFeedback.showErrorSnackbar(btnSubmit, errorMessage);
        setLoadingState(false);
    }

    private void navigateToLoginWithPrefilledCodename(String codename) {
        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
        intent.putExtra("prefilled_codename", codename);
        startActivity(intent);
        finish();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}