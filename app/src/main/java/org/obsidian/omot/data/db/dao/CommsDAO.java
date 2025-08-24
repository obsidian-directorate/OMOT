package org.obsidian.omot.data.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.obsidian.omot.core.crypto.AesGcm;
import org.obsidian.omot.data.db.DBContract;
import org.obsidian.omot.data.db.DBHelper;

public class CommsDAO {
    private final DBHelper helper;

    public CommsDAO(DBHelper helper) {
        this.helper = helper;
    }

    public boolean insertEncryptedMessage(String messageId, String senderId, String recipientId, String messagePlain, long sentAt) {
        try {
            String enc = AesGcm.encryptToBase64(messagePlain);
            SQLiteDatabase db = helper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(DBContract.SecureComms.COLUMN_MESSAGE_ID, messageId);
            cv.put(DBContract.SecureComms.COLUMN_SENDER_ID, senderId);
            cv.put(DBContract.SecureComms.COLUMN_RECIPIENT_ID, recipientId);
            cv.put(DBContract.SecureComms.COLUMN_ENCRYPTED_MESSAGE, enc);
            cv.put(DBContract.SecureComms.COLUMN_SENT_AT, sentAt);
            return db.insert(DBContract.SecureComms.TB_NAME, null, cv) != -1;
        } catch (Exception e) {
            return false;
        }
    }

    public String getMessagePlaintext(String messageId) {
        Cursor c = null;
        try {
            SQLiteDatabase db = helper.getReadableDatabase();
            c = db.query(DBContract.SecureComms.TB_NAME,
                    new String[]{DBContract.SecureComms.COLUMN_ENCRYPTED_MESSAGE},
                    DBContract.SecureComms.COLUMN_MESSAGE_ID + " = ?",
                    new String[]{messageId},
                    null, null, null);
            if (c.moveToFirst()) {
                String enc = c.toString();
                return AesGcm.decryptFromBase64(enc);
            }
        } catch (Exception ignored) {

        } finally {
            if (c != null) c.close();
        }
        return null;
    }
}