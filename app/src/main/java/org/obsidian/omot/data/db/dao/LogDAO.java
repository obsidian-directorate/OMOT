package org.obsidian.omot.data.db.dao;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import org.obsidian.omot.data.db.DBContract;
import org.obsidian.omot.data.db.DBHelper;

public class LogDAO {
    private final DBHelper helper;

    public LogDAO(DBHelper helper) {
        this.helper = helper;
    }

    public long insertLog(String agentId, String actions, long timestamp, String details) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBContract.SystemLogs.COLUMN_AGENT_ID, agentId);
        cv.put(DBContract.SystemLogs.COLUMN_ACTIONS, actions);
        cv.put(DBContract.SystemLogs.COLUMN_LOG_TIMESTAMP, timestamp);
        cv.put(DBContract.SystemLogs.COLUMN_DETAILS, details);
        return db.insert(DBContract.SystemLogs.TB_NAME, null, cv);
    }
}