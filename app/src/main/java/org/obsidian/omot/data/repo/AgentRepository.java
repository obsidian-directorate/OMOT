package org.obsidian.omot.data.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.obsidian.omot.core.util.Logs;
import org.obsidian.omot.core.util.Result;
import org.obsidian.omot.core.util.Validators;
import org.obsidian.omot.data.db.DBContract;
import org.obsidian.omot.data.db.DBHelper;
import org.obsidian.omot.data.db.dao.AgentDAO;

public class AgentRepository {
    private final AgentDAO dao;
    DBHelper helper;

    public AgentRepository(AgentDAO dao) {
        this.dao = dao;
    }

    public Result<Boolean> registerAgent(String agentId, String codename, String cipherKeyPlain,
                                         String securityQuestion, String securityAnswerPlain, String clearanceCode) {
        if (!Validators.isCodenameValid(codename)) {
            return Result.failure(new Exception("Invalid codename"));
        }
        if (!Validators.isClearanceValid(clearanceCode)) {
            return Result.failure(new Exception("Invalid clearance code"));
        }
        if (!Validators.isCipherKeyStrong(cipherKeyPlain)) {
            return Result.failure(new Exception("Cipher key too weak"));
        }

        try {
            boolean inserted = dao.insertAgent(agentId, codename, cipherKeyPlain, securityQuestion, securityAnswerPlain, clearanceCode);
            if (!inserted) {
                Logs.write(agentId, "REGISTER_FAIL", "Codename: " + codename);
                return Result.failure(new Exception("Failed to insert agent"));
            }
            Logs.write(agentId, "REGISTER_SUCCESS", "Codename: " + codename);
            return Result.success(true);
        } catch (Exception e) {
            return Result.failure(e);
        }
    }

    public Result<Cursor> findByCodename(String codename) {
        try {
            Cursor c = dao.findByCodename(codename);
            return Result.success(c);
        } catch (Exception e) {
            return Result.failure(e);
        }
    }

    public void incrementFailedAttempts(String agentId, int fails, long ts) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBContract.Agents.COLUMN_FAILED_LOGIN_ATTEMPTS, fails);
        cv.put(DBContract.Agents.COLUMN_LAST_FAILED_LOGIN_TIMESTAMP, ts);
        db.update(DBContract.Agents.TB_NAME, cv, DBContract.Agents.COLUMN_AGENT_ID + " = ?", new String[]{agentId});
    }

    public void lockAccount(String agentId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBContract.Agents.COLUMN_ACCOUNT_LOCKED, 1);
        db.update(DBContract.Agents.TB_NAME, cv, DBContract.Agents.COLUMN_AGENT_ID + " = ?", new String[]{agentId});
    }

    public void resetFailedAttempts(String agentId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBContract.Agents.COLUMN_FAILED_LOGIN_ATTEMPTS, 0);
        cv.put(DBContract.Agents.COLUMN_ACCOUNT_LOCKED, 0);
        db.update(DBContract.Agents.TB_NAME, cv, DBContract.Agents.COLUMN_AGENT_ID + " = ?", new String[]{agentId});
    }
}