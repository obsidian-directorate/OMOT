package org.osd.omot_app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.osd.omot_app.R;
import org.osd.omot_app.data.repository.RepositoryProvider;
import org.osd.omot_app.security.SecurePreferencesManager;
import org.osd.omot_app.ui.main.MainActivity;
import org.osd.omot_app.utils.UIFeedback;

import java.security.KeyStore;
import java.util.concurrent.Executor;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final String KEY_ALIAS = "OMOT_Biometric_Encryption_Key";

    private TextInputEditText edCodename, edCipherKey;
    private TextInputLayout edLayoutCodename, edLayoutCipherKey;
    private MaterialButton btnAuthenticate, btnBiometric;
    private TextView tvLostCredentials, tvRegisterLink, tvTermsLink, tvPrivacyLink;

    private RepositoryProvider provider;
    private BiometricPrompt prompt;
    private BiometricPrompt.PromptInfo promptInfo;

    private SecurePreferencesManager spManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        try {
            initializeViews();
            initializeDependencies();

            if (areDependenciesInitialized()) {
                checkExistingSession();
                setupBiometricAuth();
                setupClickListeners();
            } else {
                handleInitializationFailure();
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize LoginActivity", e);
            handleInitializationFailure();
        }
    }

    private boolean areDependenciesInitialized() {
        return provider != null && spManager != null;
    }

    private void handleInitializationFailure() {
        UIFeedback.showSnackbar(findViewById(android.R.id.content),
                getString(R.string.security_system_failed), true);

        // Disable login functionality
        btnAuthenticate.setEnabled(false);
        btnBiometric.setEnabled(false);

        Log.e(TAG, "Critical initialization failure - authentication disabled");
    }

    // ----------------------------------------
    // ---------- Overridden methods ----------
    // ----------------------------------------

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up resources if needed
    }

    // --------------------------------------
    // ---------- onCreate methods ----------
    // --------------------------------------

    private void initializeViews() {
        edCodename = findViewById(R.id.ed_codename);
        edCipherKey = findViewById(R.id.ed_cipher_key);
        edLayoutCodename = findViewById(R.id.ed_layout_codename);
        edLayoutCipherKey = findViewById(R.id.ed_layout_cipher_key);
        btnAuthenticate = findViewById(R.id.btn_authenticate);
        btnBiometric = findViewById(R.id.btn_biometric);
        tvLostCredentials = findViewById(R.id.tv_lost_credentials);
        tvRegisterLink = findViewById(R.id.tv_register_link);
        tvTermsLink = findViewById(R.id.tv_terms_link);
        tvPrivacyLink = findViewById(R.id.tv_privacy_link);
    }

    private void initializeDependencies() {
        provider = RepositoryProvider.getInstance(this);
        spManager = provider.getSpManager();
    }

    private void checkExistingSession() {
        try {
            if (spManager.isUserLoggedIn()) {
                String storedCodename = spManager.getAgentCodename();
                if (storedCodename != null) {
                    edCodename.setText(storedCodename);

                    // If biometric is enabled, auto-prompt for biometric auth
                    if (spManager.isBiometricEnabled()) {
                        btnBiometric.postDelayed(() -> {
                            if (prompt != null) {
                                prompt.authenticate(promptInfo);
                            }
                        }, 500);    // Short delay for better UX
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking existing session", e);;
        }
    }

    private void setupBiometricAuth() {
        // Check biometric capabilities
        BiometricManager manager = BiometricManager.from(this);
        switch (manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d(TAG, "Biometric authentication is available");
                setupBiometricPrompt();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.d(TAG, "No biometric features available on this device");
                btnBiometric.setVisibility(View.GONE);
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.d(TAG, "Biometric features are currently unavailable");
                btnBiometric.setVisibility(View.GONE);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Log.d(TAG, "No biometric credentials enrolled");
                btnBiometric.setVisibility(View.GONE);
                UIFeedback.showSnackbar(btnAuthenticate,
                        getString(R.string.biometric_not_enrolled), true);
                break;
        }
    }

    private void setupClickListeners() {
        btnAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        btnBiometric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prompt != null && promptInfo != null) {
                    prompt.authenticate(promptInfo);
                }
            }
        });

        tvLostCredentials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRecoveryProtocol();
            }
        });

        tvRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRegisterScreen();
            }
        });
    }

    // -------------------------------------
    // ---------- Private methods ----------
    // -------------------------------------

    private void setupBiometricPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);

        prompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Log.e(TAG, "Biometric authentication error: " + errorCode + " - " + errString);
                UIFeedback.showSnackbar(btnAuthenticate, getString(R.string.biometric_error), true);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Log.i(TAG, "Biometric authentication succeeded");
                UIFeedback.showSnackbar(btnBiometric, getString(R.string.biometric_success), false);

                authWithBiometric();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Log.w(TAG, "Biometric authentication failed");
                UIFeedback.showSnackbar(btnAuthenticate, getString(R.string.biometric_failed), true);
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.biometric_prompt_title))
                .setSubtitle(getString(R.string.biometric_prompt_subtitle))
                .setDescription(getString(R.string.biometric_prompt_description))
                .setNegativeButtonText(getString(R.string.button_biometric_prompt_negative))
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                .build();
    }

    private void attemptLogin() {
        // Get input values
        String codename = edCodename.getText().toString().trim();
        String password = edCipherKey.getText().toString().trim();

        // Validate inputs
        if (codename.isEmpty()) {
            edLayoutCodename.setError(getString(R.string.login_validation_codename_empty));
            return;
        } else {
            edLayoutCodename.setError(null);
        }

        if (password.isEmpty()) {
            edLayoutCipherKey.setError(getString(R.string.login_validation_password_empty));
        } else {
            edLayoutCipherKey.setError(null);
        }

        // Show loading state
        btnAuthenticate.setEnabled(false);
        btnAuthenticate.setText(R.string.authenticating);

        // Attempt authentication on a background thread to avoid blocking UI
        new Thread(() -> {
            try {
                // Simulate network/database delay for realism
                Thread.sleep(1000);

                runOnUiThread(() -> {
                    performAuthentication(codename, password);
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                runOnUiThread(() -> {
                    handleAuthenticationError();
                });
            }
        }).start();
    }

    private void performAuthentication(String codename, String password) {
        try {
            var agentRepository = provider.getAgentRepository();
            var agent = agentRepository.loginAgent(codename, password);

            if (agent != null) {
                // Authentication successful
                UIFeedback.showSnackbar(btnAuthenticate, getString(R.string.login_success), false);
                Log.i(TAG, "Authentication successful for agent: " + codename);
                navigateToMain();
            } else {
                // Authentication failed
                UIFeedback.showSnackbar(btnAuthenticate,
                        getString(R.string.login_failed_credentials), true);
                Log.w(TAG, "Authentication failed for agent: " + codename);
            }
        } catch (Exception e) {
            Log.e(TAG, "Authentication error", e);
            handleAuthenticationError();
        } finally {
            // Restore button state
            btnAuthenticate.setEnabled(true);
            btnAuthenticate.setText(R.string.button_authenticate);
        }
    }

    private void handleAuthenticationError() {
        UIFeedback.showSnackbar(btnAuthenticate, getString(R.string.login_error_generic), true);
        btnAuthenticate.setEnabled(true);
        btnAuthenticate.setText(R.string.button_authenticate);
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();   // Prevent returning to login with back button
    }

    private void showRecoveryProtocol() {
        // TODO: Implement Lost Credentials Protocol flow
        Toast.makeText(this, "Lost Credentials Protocol initiated", Toast.LENGTH_SHORT).show();
    }

    private void showRegisterScreen() {
        // TODO: Implement user registration flow
        Toast.makeText(this, "Directorate Access Request screen", Toast.LENGTH_SHORT).show();
    }

    /**
     * Generates a secret key for biometric authentication encryption.
     * This is needed for more advanced biometric flows where you want to encrypt/decrypt data.
     */
    private void generateSecretKey() {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

            if (!keyStore.containsAlias(KEY_ALIAS)) {
                KeyGenerator keyGenerator =
                        KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,
                                "AndroidKeyStore");

                keyGenerator.init(new KeyGenParameterSpec.Builder(
                        KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .setUserAuthenticationRequired(true)
                        .build());

                keyGenerator.generateKey();
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to generate secret key for biometrics", e);
        }
    }

    /**
     * Creates a cipher for biometric authentication. Used for more advanced flows.
     */
    private Cipher createCipher() {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            SecretKey secretKey = (SecretKey) keyStore.getKey(KEY_ALIAS, null);
            
            Cipher cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7
            );
            
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher;
        } catch (Exception e) {
            Log.e(TAG, "Failed to create cipher for biometrics", e);
            return null;
        }
    }

    /**
     * Enhanced biometric authentication that uses stored credentials.
     */
    private void authWithBiometric() {
        String storedCodename = spManager.getAgentCodename();
        if (storedCodename != null) {
            var agentRepository = provider.getAgentRepository();
            var agent = agentRepository.getAgentByCodename(storedCodename);

            if (agent != null) {
                // Simulate successful authentication
                spManager.saveLoginSession(
                        agent.getAgentID(),
                        agent.getCodename(),
                        agent.getClearanceLevel().getClearanceCode(),
                        agent.isBiometricEnabled()
                );

                navigateToMain();
            } else {
                UIFeedback.showSnackbar(btnAuthenticate, getString(R.string.store_session_failed)
                        , true);
                spManager.clearLoginSession();
            }
        } else {
            UIFeedback.showSnackbar(btnAuthenticate, getString(R.string.no_stored_credentials), true);
        }
    }
}