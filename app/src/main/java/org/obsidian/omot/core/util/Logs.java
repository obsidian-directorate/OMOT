package org.obsidian.omot.core.util;

import android.util.Log;

import org.obsidian.omot.OMOTApp;
import org.obsidian.omot.data.db.dao.LogDAO;

public class Logs {
    private static final String TAG = "OMOT-LOGS";
    private static final LogDAO dao = new LogDAO(OMOTApp.db());

    public static void write(String agentId, String actions, String details) {
        long ts = System.currentTimeMillis();
        dao.insertLog(agentId, actions, ts, details);

        // Also log to Logcat for dev builds
        Log.d(TAG, "[" + agentId + "]" + actions + "::" + details);
    }
}