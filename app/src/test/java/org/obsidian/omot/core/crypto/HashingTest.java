package org.obsidian.omot.core.crypto;

import org.junit.Assert;
import org.junit.Test;

public class HashingTest {
    @Test
    public void bcryptHashAndVerify_success() {
        String plain = "Secret123!";
        String hash = Hashing.bcryptHash(plain, 10);
        Assert.assertTrue(Hashing.bcryptVerify(plain, hash));
    }

    @Test
    public void bcryptHashAndVerify_fail() {
        String plain = "Secret123!";
        String hash = Hashing.bcryptHash(plain, 10);
        Assert.assertFalse(Hashing.bcryptVerify("WrongPassword", hash));
    }
}