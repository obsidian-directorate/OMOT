package org.obsidian.omot.data.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.obsidian.omot.core.crypto.Hashing;
import org.obsidian.omot.data.db.DBContract;
import org.obsidian.omot.data.db.DBHelper;

public class AgentDAO {
    private final DBHelper helper;

    public AgentDAO(DBHelper helper) {
        this.helper = helper;
    }

    public boolean insertAgent(String agentId, String codename, String cipherKeyPlain,
                               String securityQuestion, String securityAnswerPlain, String clearanceCode) {
        String pwdHash = Hashing.bcryptHash(cipherKeyPlain, 12);
        String secAnsHash = securityAnswerPlain != null ? Hashing.bcryptHash(securityAnswerPlain, 12) : null;
        String randomSalt = Hashing.generateRandomSalt(32);

        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBContract.Agents.COLUMN_AGENT_ID, agentId);
        cv.put(DBContract.Agents.COLUMN_CODENAME, codename);
        cv.put(DBContract.Agents.COLUMN_PASSWORD_HASH, pwdHash);
        cv.put(DBContract.Agents.COLUMN_SALT, randomSalt);
        cv.put(DBContract.Agents.COLUMN_SECURITY_QUESTION, securityQuestion);
        cv.put(DBContract.Agents.COLUMN_CLEARANCE_CODE, clearanceCode);
        cv.put(DBContract.Agents.COLUMN_BIOMETRIC_ENABLED, 0);
        cv.put(DBContract.Agents.COLUMN_ACCOUNT_LOCKED, 0);

        long rowId = db.insert(DBContract.Agents.TB_NAME, null, cv);
        return rowId != -1;
    }

    public Cursor findByCodename(String codename) {
        SQLiteDatabase db = helper.getReadableDatabase();
        return db.query(DBContract.Agents.TB_NAME,
                null,
                DBContract.Agents.COLUMN_CODENAME + " = ?",
                new String[]{codename},
                null, null, null);
    }
}