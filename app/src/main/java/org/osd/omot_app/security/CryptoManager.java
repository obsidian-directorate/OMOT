package org.osd.omot_app.security;

import android.content.Context;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

/**
 * Handle encryption and decryption of sensitive data using AES-GCM stored in the Android KeyStore.
 * This provides a secure way to manage the encryption key.
 */
public class CryptoManager {
    private static final String TAG = "CryptoManager";
    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String KEY_ALIAS = "OMOT_Database_Encryption_Key";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;    // 12 bytes is recommended for GCM
    private static final int TAG_LENGTH = 128;  // 128 bits is standard for GCM

    private final KeyStore keyStore;
    private final Context context;

    public CryptoManager(Context context) {
        this.context = context.getApplicationContext();
        try {
            keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
            keyStore.load(null);
        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to initialize KeyStore", e);
        }
    }

    private SecretKey getOrCreateSecretKey() {
        try {
            // First, try to retrieve the existing key
            if (keyStore.containsAlias(KEY_ALIAS)) {
                KeyStore.SecretKeyEntry secretKeyEntry =
                        (KeyStore.SecretKeyEntry) keyStore.getEntry(KEY_ALIAS, null);
                if (secretKeyEntry != null) {
                    return secretKeyEntry.getSecretKey();
                }
            }
            // If the key doesn't exist, generate a new one
            KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT
            )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(256)
                    .setUserAuthenticationRequired(false)   // Key is available without user auth
                    .build();

            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,
                    ANDROID_KEYSTORE);
            keyGenerator.init(keyGenParameterSpec);
            return keyGenerator.generateKey();
        } catch (Exception e) {
            Log.e(TAG, "Failed to get or create secret key", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Encrypts a plaintext string.
     * @param plaintext The string to encrypt.
     * @return A Base64 encoded string containing the IV + ciphertext, or null if encryption failed.
     */
    @Nullable
    public String encrypt(String plaintext) {
        if (plaintext == null || plaintext.isEmpty()) {
            return plaintext;
        }
        try {
            SecretKey secretKey = getOrCreateSecretKey();
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            // Get the IV that was generated
            byte[] iv = cipher.getIV();
            // Perform the encryption
            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            // Combine IV and ciphertext for storage
            byte[] encryptedData = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, encryptedData, 0, iv.length);
            System.arraycopy(ciphertext, 0, encryptedData, iv.length, ciphertext.length);

            // Return as a Base64 string
            return Base64.encodeToString(encryptedData, Base64.NO_WRAP);
        } catch (Exception e) {
            Log.e(TAG, "Encryption failed", e);
            return null;
        }
    }

    /**
     * Decrypts a string previously encrypted by this manager.
     * @param encryptedDataBase64 The Base64 encoded string containing IV + ciphertext.
     * @return The decrypted plaintext string, or null if decryption failed.
     */
    @Nullable
    public String decrypt(String encryptedDataBase64) {
        if (encryptedDataBase64 == null || encryptedDataBase64.isEmpty()) {
            return encryptedDataBase64;
        }

        try {
            byte[] encryptedData = Base64.decode(encryptedDataBase64, Base64.NO_WRAP);
            if (encryptedData.length < IV_LENGTH) {
                throw new IllegalArgumentException("Encrypted data is too short");
            }

            // Extract the IV (first IV_LENGTH bytes)
            byte[] iv = new byte[IV_LENGTH];
            System.arraycopy(encryptedData, 0, iv, 0, IV_LENGTH);

            // Extract the ciphertext (the rest)
            byte[] ciphertext = new byte[encryptedData.length - IV_LENGTH];
            System.arraycopy(encryptedData, IV_LENGTH, ciphertext, 0, ciphertext.length);
            SecretKey secretKey = getOrCreateSecretKey();
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

            byte[] decryptedBytes = cipher.doFinal(ciphertext);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            Log.e(TAG, "Decryption failed", e);
            return null;
        }
    }

    /**
     * Checks if the KeyStore and encryption key are available and functional.
     * @return true if encryption/decryption is ready, false otherwise.
     */
    public boolean isSecurityKeyAvailable() {
        try {
            // Try a simple encrypt/decrypt cycle
            String testText = "OMOT_TEST_STRING";
            String encrypted = encrypt(testText);
            if (encrypted == null) return false;
            String decrypted = decrypt(encrypted);
            return testText.equals(decrypted);
        } catch (Exception e) {
            Log.e(TAG, "Security key self-test failed", e);
            return false;
        }
    }
}