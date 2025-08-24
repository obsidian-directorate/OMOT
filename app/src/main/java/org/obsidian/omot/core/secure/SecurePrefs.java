package org.obsidian.omot.core.secure;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

public class SecurePrefs {
    private static final String PREF_NAME = "omot_secure_prefs";

    private final Context context;

    public SecurePrefs(Context context) {
        this.context = context;
    }

    private SharedPreferences getPrefs() throws Exception {
        MasterKey masterKey = new MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

        return EncryptedSharedPreferences.create(
                context,
                PREF_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
    }

    public void putString(String key, String value) {
        try {
            getPrefs().edit().putString(key, value).apply();
        } catch (Exception ignored) {}
    }

    public String getString(String key, String defaultValue) {
        try {
            return getPrefs().getString(key, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public void clear() {
        try {
            getPrefs().edit().clear().apply();
        } catch (Exception ignored) {}
    }
}