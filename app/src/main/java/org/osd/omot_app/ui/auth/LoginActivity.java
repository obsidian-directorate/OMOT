package org.osd.omot_app.ui.auth;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.osd.omot_app.R;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText edCodename, edCipherKey;
    private TextInputLayout edLayoutCodename, edLayoutCipherKey;
    private MaterialButton btnAuthenticate, btnBiometric;
    private TextView tvLostCredentials, tvRegisterLink, tvTermsLink, tvPrivacyLink;

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

        initializeViews();
    }

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
}