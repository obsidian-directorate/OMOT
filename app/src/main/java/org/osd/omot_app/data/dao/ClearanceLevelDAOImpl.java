package org.osd.omot_app.data.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.osd.omot_app.data.DBContract;
import org.osd.omot_app.data.DBHelper;
import org.osd.omot_app.data.model.ClearanceLevel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClearanceLevelDAOImpl implements ClearanceLevelDAO {

    private final DBHelper helper;

    public ClearanceLevelDAOImpl(DBHelper helper) {
        this.helper = helper;
    }

    @Override
    public ClearanceLevel getClearanceByCode(String code) {
        SQLiteDatabase db = helper.getReadableDatabase();
        ClearanceLevel level = null;

        String selection = DBContract.ClearanceLevelEntry.COLUMN_CLEARANCE_CODE + " = ?";
        String[] selectionArgs = { code };

        try (Cursor cursor = db.query(
                DBContract.ClearanceLevelEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null, null, null
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                level = new ClearanceLevel(
                        cursor.getString(cursor.getColumnIndexOrThrow(DBContract.ClearanceLevelEntry.COLUMN_CLEARANCE_CODE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBContract.ClearanceLevelEntry.COLUMN_LEVEL_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBContract.ClearanceLevelEntry.COLUMN_DESCRIPTION))
                );
            }
        }
        return level;
    }

    @Override
    public List<ClearanceLevel> getAllClearanceLevels() {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<ClearanceLevel> levelList = new ArrayList<>();

        try (Cursor cursor = db.query(
                DBContract.ClearanceLevelEntry.TABLE_NAME,
                null,
                null,
                null,
                null, null, null
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    ClearanceLevel level = new ClearanceLevel(
                            cursor.getString(cursor.getColumnIndexOrThrow(DBContract.ClearanceLevelEntry.COLUMN_CLEARANCE_CODE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBContract.ClearanceLevelEntry.COLUMN_LEVEL_NAME)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBContract.ClearanceLevelEntry.COLUMN_DESCRIPTION))
                    );
                    levelList.add(level);
                } while (cursor.moveToNext());
            }
        }
        return levelList;
    }
}