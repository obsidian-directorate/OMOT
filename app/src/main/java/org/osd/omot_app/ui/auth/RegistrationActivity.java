package org.osd.omot_app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
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
        setupTextWatchers();
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

    private void setupTextWatchers() {
        // Real-time validation for password matching
        edConfirmCipherKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validatePasswordMatch();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
        });

        // Clear errors when user starts typing
        setupClearErrorOnType(edCodename, edLayoutCodename);
        setupClearErrorOnType(edCipherKey, edLayoutCipherKey);
        setupClearErrorOnType(edSecurityQuestion, edLayoutQuestion);
        setupClearErrorOnType(edSecurityAnswer, edLayoutAnswer);
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

        // Terms checkbox validation on submit
        chkTerms.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked && chkTerms.isPressed()) {
                UIFeedback.showErrorSnackbar(btnSubmit, getString(R.string.registration_error_terms));
            }
        });
    }

    // -------------------------------------
    // ---------- Private methods ----------
    // -------------------------------------

    private void setupClearErrorOnType(TextInputEditText editText, TextInputLayout layout) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (layout.getError() != null) {
                    layout.setError(null);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
        });
    }

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
                Thread.sleep(1500); // Simulate processing delay

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
        } else if (codename.length() < 3) {
            edLayoutCodename.setError(getString(R.string.validation_codename_length));
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
        } else if (securityQuestion.length() < 10) {
            edLayoutQuestion.setError(getString(R.string.validation_question_length));
            isValid = false;
        }

        if (securityAnswer.isEmpty()) {
            edLayoutAnswer.setError(getString(R.string.validation_field_required));
            isValid = false;
        } else if (securityAnswer.length() < 3) {
            edLayoutAnswer.setError(getString(R.string.validation_answer_length));
            isValid = false;
        }

        if (!acceptedTerms) {
            UIFeedback.showErrorSnackbar(btnSubmit, getString(R.string.registration_error_terms));
            isValid = false;
        }

        return isValid;
    }

    private void validatePasswordMatch() {
        String cipherKey = edCipherKey.getText().toString().trim();
        String confirmCipherKey = edConfirmCipherKey.getText().toString().trim();

        if (!confirmCipherKey.isEmpty() && !cipherKey.equals(confirmCipherKey)) {
            edLayoutConfirm.setError(getString(R.string.registration_error_password_mismatch));
        } else if (edLayoutConfirm.getError() != null) {
            edLayoutConfirm.setError(null);
        }
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
                handleRegistrationSuccess(codename, result.getAgentID());
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

        // Disable form interactions during loading
        setFormEnabled(!isLoading);
    }

    private void setFormEnabled(boolean enabled) {
        edCodename.setEnabled(enabled);
        edCipherKey.setEnabled(enabled);
        edConfirmCipherKey.setEnabled(enabled);
        edSecurityQuestion.setEnabled(enabled);
        edSecurityAnswer.setEnabled(enabled);
        chkBiometric.setEnabled(enabled);
        chkTerms.setEnabled(enabled);
        tvBackToLogin.setEnabled(enabled);
    }

    private void handleRegistrationSuccess(String codename, String agentID) {
        // Show success message with agent ID
        String successMessage = getString(R.string.registration_success) + " Your agent ID: " + agentID;
        UIFeedback.showSuccessSnackbar(btnSubmit, successMessage);

        // Delay navigation to show success message
        new Handler().postDelayed(() -> {
            navigateToLoginWithPrefilledCodename(codename);
        }, 2000);
    }

    private void handleRegistrationError(String errorMessage) {
        // Handle specific error types with appropriate UI feedback
        if (errorMessage.contains("already taken")) {
            edLayoutCodename.setError(getString(R.string.registration_error_codename_taken));
            edCodename.requestFocus();
        } else if (errorMessage.contains("Cipher key")) {
            edLayoutCipherKey.setError(errorMessage);
            edCipherKey.requestFocus();
        } else {
            UIFeedback.showErrorSnackbar(btnSubmit, errorMessage);
        }

        setLoadingState(false);
    }

    private void navigateToLoginWithPrefilledCodename(String codename) {
        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
        intent.putExtra("prefilled_codename", codename);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}