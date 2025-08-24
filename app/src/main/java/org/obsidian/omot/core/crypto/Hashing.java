package org.obsidian.omot.core.crypto;

import org.mindrot.jbcrypt.BCrypt;

import java.security.SecureRandom;
import java.util.Base64;

public class Hashing {
    public static String generateRandomSalt(int bytes) {
        byte[] salt = new byte[bytes];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String bcryptHash(String plain, int workFactor) {
        String gensalt = BCrypt.gensalt(workFactor);
        return BCrypt.hashpw(plain, gensalt);
    }

    public static boolean bcryptVerify(String plain, String storedHash) {
        return BCrypt.checkpw(plain, storedHash);
    }
}