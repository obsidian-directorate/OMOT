package org.obsidian.omot.data.db;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.obsidian.omot.core.crypto.AesGcm;
import org.obsidian.omot.data.db.dao.CommsDAO;

@RunWith(AndroidJUnit4.class)
public class DatabaseCryptoTest {
    private DBHelper helper;
    private CommsDAO dao;

    @Before
    public void setup() {
        Context ctx = ApplicationProvider.getApplicationContext();
        helper = new DBHelper(ctx);
        dao = new CommsDAO(helper);
    }

    @Test
    public void schemaCreatesWithoutCrash() {
        Assert.assertNotNull(helper.getReadableDatabase());
    }

    @Test
    public void encryptedMessage_roundTrip() throws Exception {
        String msgId = "MSG-001";
        String sender = "AGENT-001";
        String recipient = "AGENT-002";
        String message = "Test secret comms.";

        long now = System.currentTimeMillis();
        boolean inserted = dao.insertEncryptedMessage(msgId, sender, recipient, message, now);
        Assert.assertTrue(inserted);

        String decrypted = dao.getMessagePlaintext(msgId);
        Assert.assertEquals(message, decrypted);
    }

    @Test
    public void aesGcm_roundTrip() throws Exception {
        String input = "Hello encrypted world!";
        String enc = AesGcm.encryptToBase64(input);
        Assert.assertNotNull(enc);

        String dec = AesGcm.decryptFromBase64(enc);
        Assert.assertEquals(input, dec);
    }
}