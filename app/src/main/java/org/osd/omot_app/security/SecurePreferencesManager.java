package org.osd.omot_app.security;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Manages secure storage of sensitive data using EncryptedSharedPreferences.
 * All data is encrypted at rest using Android's Keystore system.
 */
public class SecurePreferencesManager {

    private static final String TAG = "SecurePreferencesManager";
    private static final String PREFS_NAME = "OMOT_Secure_Preferences";

    // Preference keys
    public static final String KEY_AGENT_ID = "agent_id";
    public static final String KEY_AGENT_CODENAME = "agent_codename";
    public static final String KEY_AGENT_CLEARANCE = "agent_clearance";
    public static final String KEY_IS_LOGGED_IN = "is_logged_in";
    public static final String KEY_BIOMETRIC_ENABLED = "biometric_enabled";
    public static final String KEY_LAST_LOGIN_TIMESTAMP = "last_login_timestamp";
    public static final String KEY_SESSION_TOKEN = "session_token"; // For future use with backend

    private final SharedPreferences encryptedSharedPreferences;

    public SecurePreferencesManager(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            encryptedSharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    PREFS_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Failed to initialize EncryptedSharedPreferences", e);
        }
    }

    /**
     * Stores agent session information upon successful login.
     */
    public void saveLoginSession(String agentID, String codename, String clearanceLevel,
                                 boolean biometricEnabled) {
        SharedPreferences.Editor editor = encryptedSharedPreferences.edit();
        editor.putString(KEY_AGENT_ID, agentID);
        editor.putString(KEY_AGENT_CODENAME, codename);
        editor.putString(KEY_AGENT_CLEARANCE, clearanceLevel);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putBoolean(KEY_BIOMETRIC_ENABLED, biometricEnabled);
        editor.putLong(KEY_LAST_LOGIN_TIMESTAMP, System.currentTimeMillis());
        editor.apply();
    }

    /**
     * Clears all session data upon logout.
     */
    public void clearLoginSession() {
        SharedPreferences.Editor editor = encryptedSharedPreferences.edit();
        editor.remove(KEY_AGENT_ID);
        editor.remove(KEY_AGENT_CODENAME);
        editor.remove(KEY_AGENT_CLEARANCE);
        editor.remove(KEY_IS_LOGGED_IN);
        editor.remove(KEY_BIOMETRIC_ENABLED);
        editor.remove(KEY_LAST_LOGIN_TIMESTAMP);
        editor.remove(KEY_SESSION_TOKEN);
        editor.apply();
    }

    /**
     * Checks if a user is currently logged in.
     */
    public boolean isUserLoggedIn() {
        return encryptedSharedPreferences != null && encryptedSharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Retrieves the logged-in agent's ID.
     */
    public String getAgentID() {
        return encryptedSharedPreferences != null ?
                encryptedSharedPreferences.getString(KEY_AGENT_ID, null) : null;
    }

    /**
     * Retrieves the logged-in agent's codename.
     */
    public String getAgentCodename() {
        return encryptedSharedPreferences != null ?
                encryptedSharedPreferences.getString(KEY_AGENT_CODENAME, null) : null;
    }

    /**
     * Retrieves the logged-in agent's clearance levels.
     */
    public String getAgentClearance() {
        return encryptedSharedPreferences != null ?
                encryptedSharedPreferences.getString(KEY_AGENT_CLEARANCE, null) : null;
    }

    /**
     * Checks if biometric authentication is enabled for the current agent.
     */
    public boolean isBiometricEnabled() {
        return encryptedSharedPreferences != null && encryptedSharedPreferences.getBoolean(KEY_BIOMETRIC_ENABLED, false);
    }

    /**
     * Enables or disables biometric authentication for the current session.
     */
    public void setBiometricEnabled(boolean enabled) {
        if (encryptedSharedPreferences != null) {
            encryptedSharedPreferences.edit()
                    .putBoolean(KEY_BIOMETRIC_ENABLED, enabled)
                    .apply();
        }
    }

    /**
     * Stores a secure value with the given key.
     */
    public void putSecureString(String key, String value) {
        if (encryptedSharedPreferences != null) {
            encryptedSharedPreferences.edit().putString(key, value).apply();
        }
    }

    /**
     * Retrieves a secure value with the given key.
     */
    public String getSecureString(String key, String defaultValue) {
        return encryptedSharedPreferences != null ? encryptedSharedPreferences.getString(key,
                defaultValue) : null;
    }

    /**
     * Clears all stored preferences (use with caution).
     */
    public void clearAllPreferences() {
        if (encryptedSharedPreferences != null) {
            encryptedSharedPreferences.edit().clear().apply();
        }
    }
}