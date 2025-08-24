package org.obsidian.omot.core.crypto;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class AesGcm {
    private static final int IV_BYTES = 12;     // 96-bit IV for GCM
    private static final int TAG_BITS = 128;    // 128-bit tag

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String encryptToBase64(String plain) throws Exception {
        SecretKey key = KeystoreManager.getOrCreateAesKey();
        byte[] iv = new byte[IV_BYTES];
        new SecureRandom().nextBytes(iv);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, iv));
        byte[] ct = cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8));

        // Package: IV || ciphertext
        ByteBuffer bb = ByteBuffer.allocate(iv.length + ct.length);
        bb.put(iv);
        bb.put(ct);
        byte[] combined = bb.array();

        return Base64.getEncoder().encodeToString(combined);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String decryptFromBase64(String base64) throws Exception {
        SecretKey key = KeystoreManager.getOrCreateAesKey();
        byte[] combined = Base64.getDecoder().decode(base64);

        byte[] iv = new byte[IV_BYTES];
        System.arraycopy(combined, 0, iv, 0, IV_BYTES);

        byte[] ct = new byte[combined.length - IV_BYTES];
        System.arraycopy(combined, IV_BYTES, ct, 0, ct.length);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, iv));
        byte[] pt = cipher.doFinal(ct);
        return new String(pt, StandardCharsets.UTF_8);
    }
}