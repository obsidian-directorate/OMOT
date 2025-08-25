package org.obsidian.omot.domain;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import java.util.concurrent.Executor;

public class BiometricAuth {
    public interface Callback {
        void onSuccess();
        void onFailure(String reason);
    }

    public static void authenticate(Context context, Callback callback) {
        BiometricManager bm = BiometricManager.from(context);
        int canAuth = bm.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK);

        if (canAuth != BiometricManager.BIOMETRIC_SUCCESS) {
            callback.onFailure("Biometrics not available");
            return;
        }

        Executor executor = ContextCompat.getMainExecutor(context);
        BiometricPrompt prompt = new BiometricPrompt((FragmentActivity) context,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                callback.onSuccess();
            }

            @Override
            public void onAuthenticationFailed() {
                callback.onFailure("Authentication failed");
            }
        });

        BiometricPrompt.PromptInfo info = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Obsidian Directorate - Retinal/Print Scan")
                .setSubtitle("Authenticate clearance with biometric scanner")
                .setNegativeButtonText("Use Cipher Key instead")
                .build();

        prompt.authenticate(info);
    }
}