package org.obsidian.omot;

import android.app.Application;

import org.obsidian.omot.data.db.DBHelper;

public class OMOTApp extends Application {
    private static DBHelper helper;

    @Override
    public void onCreate() {
        super.onCreate();
        helper = new DBHelper(this);
        // Trigger DB creation eagerly
        helper.getWritableDatabase();
    }

    public static DBHelper db() {
        return helper;
    }
}