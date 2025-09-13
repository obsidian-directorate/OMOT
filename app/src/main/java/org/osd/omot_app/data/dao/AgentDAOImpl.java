package org.osd.omot_app.data.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.osd.omot_app.data.DBContract;
import org.osd.omot_app.data.DBHelper;
import org.osd.omot_app.data.model.Agent;
import org.osd.omot_app.data.model.ClearanceLevel;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of the AgentDAO interface using SQLite.
 */
public class AgentDAOImpl implements AgentDAO {
    private static final String TAG = "AgentDAOImpl";
    private final DBHelper helper;

    public AgentDAOImpl(DBHelper helper) {
        this.helper = helper;
    }

    @Override
    public long insertAgent(Agent agent) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBContract.AgentEntry.COLUMN_AGENT_ID, agent.getAgentID());
        values.put(DBContract.AgentEntry.COLUMN_CODENAME, agent.getCodename());
        values.put(DBContract.AgentEntry.COLUMN_PASSWORD_HASH, agent.getPasswordHash());    // Will be encrypted by DBHelper
        values.put(DBContract.AgentEntry.COLUMN_SALT, agent.getSalt());
        values.put(DBContract.AgentEntry.COLUMN_SECURITY_QUESTION, agent.getSecurityQuestion());    // Will be encrypted
        values.put(DBContract.AgentEntry.COLUMN_SECURITY_ANSWER_HASH,
                agent.getSecurityAnswerHash());   // Will be encrypted
        values.put(DBContract.AgentEntry.COLUMN_CLEARANCE_CODE, agent.getClearanceLevel().getClearanceCode());
        values.put(DBContract.AgentEntry.COLUMN_BIOMETRIC_ENABLED, agent.isBiometricEnabled() ?
                1 : 0);
        values.put(DBContract.AgentEntry.COLUMN_LAST_LOGIN_TIMESTAMP, agent.getLastLoginTimestamp());
        values.put(DBContract.AgentEntry.COLUMN_FAILED_ATTEMPTS, agent.getFailedLoginAttempts());
        values.put(DBContract.AgentEntry.COLUMN_LAST_FAILED_TIMESTAMP, agent.getLastFailedLoginTimestamp());
        values.put(DBContract.AgentEntry.COLUMN_ACCOUNT_LOCKED, agent.isAccountLocked() ? 1 : 0);

        // The DBHelper's insertAgent method handles the encryption
        return helper.insertAgent(values);
    }

    @Override
    public Agent getAgentByCodename(String codename) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Agent agent = null;

        String selection = DBContract.AgentEntry.COLUMN_CODENAME + " = ?";
        String[] selectionArgs = { codename };

        try (Cursor cursor = db.query(
                DBContract.AgentEntry.TABLE_NAME,
                null, // get all columns
                selection,
                selectionArgs,
                null, null, null
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                agent = cursorToAgent(cursor);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting agent by codename: " + codename, e);
        }
        return agent;
    }

    @Override
    public Agent getAgentByID(String agentID) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Agent agent = null;

        String selection = DBContract.AgentEntry.COLUMN_AGENT_ID + " = ?";
        String[] selectionArgs = { agentID };

        try (Cursor cursor = db.query(
                DBContract.AgentEntry.TABLE_NAME,
                null, // get all columns
                selection,
                selectionArgs,
                null, null, null
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                agent = cursorToAgent(cursor);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting agent by ID: " + agentID, e);
        }
        return agent;
    }

    @Override
    public List<Agent> getAllAgents() {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<Agent> agentList = new ArrayList<>();

        try (Cursor cursor = db.query(
                DBContract.AgentEntry.TABLE_NAME,
                null,
                null,
                null,
                null, null, null
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Agent agent = cursorToAgent(cursor);
                    agentList.add(agent);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting all agents", e);
        }
        return agentList;
    }

    @Override
    public boolean isCodenameAvailable(String codename) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String selection = DBContract.AgentEntry.COLUMN_CODENAME + " = ?";
        String[] selectionArgs = { codename };

        try (Cursor cursor = db.query(
                DBContract.AgentEntry.TABLE_NAME,
                new String[]{DBContract.AgentEntry.COLUMN_AGENT_ID},
                selection,
                selectionArgs,
                null, null, null
        )) {
            return cursor == null || cursor.getCount() == 0;
        } catch (Exception e) {
            Log.e(TAG, "Error checking codename availability: " + codename, e);
            return false; // On error, assume codename is not available
        }
    }

    @Override
    public int getMaxAgentIDNumber() {
        SQLiteDatabase db = helper.getReadableDatabase();
        int maxID = 0;

        try (Cursor cursor = db.query(
                DBContract.AgentEntry.TABLE_NAME,
                new String[]{"MAX(CAST(substr(" + DBContract.AgentEntry.COLUMN_AGENT_ID + ", 7) " +
                        "AS INTEGER)) as max_id"},
                null, null, null, null, null
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                maxID = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting max agent ID", e);
        }

        return maxID;
    }


    @Override
    public int updateAgent(Agent agent) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBContract.AgentEntry.COLUMN_CODENAME, agent.getCodename());
        values.put(DBContract.AgentEntry.COLUMN_PASSWORD_HASH, agent.getPasswordHash());    // Encryption handled in helper if we call a custom method
        values.put(DBContract.AgentEntry.COLUMN_SALT, agent.getSalt());
        values.put(DBContract.AgentEntry.COLUMN_SECURITY_QUESTION, agent.getSecurityQuestion());
        values.put(DBContract.AgentEntry.COLUMN_SECURITY_ANSWER_HASH,
                agent.getSecurityAnswerHash());
        values.put(DBContract.AgentEntry.COLUMN_CLEARANCE_CODE, agent.getClearanceLevel().getClearanceCode());
        values.put(DBContract.AgentEntry.COLUMN_BIOMETRIC_ENABLED, agent.isBiometricEnabled() ?
                1 : 0);
        values.put(DBContract.AgentEntry.COLUMN_LAST_LOGIN_TIMESTAMP, agent.getLastLoginTimestamp());
        values.put(DBContract.AgentEntry.COLUMN_FAILED_ATTEMPTS, agent.getFailedLoginAttempts());
        values.put(DBContract.AgentEntry.COLUMN_LAST_FAILED_TIMESTAMP, agent.getLastFailedLoginTimestamp());
        values.put(DBContract.AgentEntry.COLUMN_ACCOUNT_LOCKED, agent.isAccountLocked() ? 1 : 0);

        String whereClause = DBContract.AgentEntry.COLUMN_AGENT_ID + " = ?";
        String[] whereArgs = { agent.getAgentID() };

        // For a robust update, we would need to encrypt the fields here as well.
        // This is a simplified version. A better approach is to create an update method in DatabaseHelper.
        return db.update(DBContract.AgentEntry.TABLE_NAME, values, whereClause, whereArgs);
    }

    @Override
    public int deleteAgent(String agentID) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String whereClause = DBContract.AgentEntry.COLUMN_AGENT_ID + " = ?";
        String[] whereArgs = { agentID };
        return db.delete(DBContract.AgentEntry.TABLE_NAME, whereClause, whereArgs);
    }

    @Override
    public boolean recordFailedLoginAttempt(String codename) {
        Agent agent = getAgentByCodename(codename);
        if (agent == null) return false;

        int newFailedAttempts = agent.getFailedLoginAttempts() + 1;
        long currentTimestamp = System.currentTimeMillis();

        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBContract.AgentEntry.COLUMN_FAILED_ATTEMPTS, newFailedAttempts);
        values.put(DBContract.AgentEntry.COLUMN_LAST_FAILED_TIMESTAMP, currentTimestamp);

        // Auto-lock account after 5 failed attempts
        if (newFailedAttempts >= 5) {
            values.put(DBContract.AgentEntry.COLUMN_ACCOUNT_LOCKED, 1);
            Log.w(TAG, "Account locked due to too many failed attempts: " + codename);
        }

        String whereClause = DBContract.AgentEntry.COLUMN_CODENAME + " = ?";
        String[] whereArgs = { codename };

        int rowsAffected = db.update(DBContract.AgentEntry.TABLE_NAME, values, whereClause,
                whereArgs);
        return rowsAffected > 0;
    }

    @Override
    public boolean recordSuccessfulLogin(String codename) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBContract.AgentEntry.COLUMN_FAILED_ATTEMPTS, 0);    // Reset counter
        values.put(DBContract.AgentEntry.COLUMN_LAST_LOGIN_TIMESTAMP, System.currentTimeMillis());
        values.put(DBContract.AgentEntry.COLUMN_ACCOUNT_LOCKED, 0);     // Unlock account on success

        String whereClause = DBContract.AgentEntry.COLUMN_CODENAME + " = ?";
        String[] whereArgs = { codename };

        int rowsAffected = db.update(DBContract.AgentEntry.TABLE_NAME, values, whereClause,
                whereArgs);
        return rowsAffected > 0;
    }

    @Override
    public boolean setAccountLockStatus(String codename, boolean locked) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBContract.AgentEntry.COLUMN_ACCOUNT_LOCKED, locked ? 1 : 0);

        String whereClause = DBContract.AgentEntry.COLUMN_CODENAME + " = ?";
        String[] whereArgs = { codename };

        int rowsAffected = db.update(DBContract.AgentEntry.TABLE_NAME, values, whereClause,
                whereArgs);
        return rowsAffected > 0;
    }

    /**
     * Helper method to map a database Cursor row to an Agent object.
     * This is where decryption of sensitive fields happens.
     * @param cursor TheCursor pointing to the tow containing agent data.
     * @return A fully populated Agent object.
     */
    private Agent cursorToAgent(Cursor cursor) {
        // Decrypt the sensitive fields retrieved from the database
        String decryptedPasswordHash =
                helper.decryptField(cursor.getString((cursor.getColumnIndexOrThrow(DBContract.AgentEntry.COLUMN_PASSWORD_HASH))));
        String decryptedSalt =
                helper.decryptField(cursor.getString(cursor.getColumnIndexOrThrow(DBContract.AgentEntry.COLUMN_SALT)));
        String decryptedSecurityQuestion =
                helper.decryptField(cursor.getString(cursor.getColumnIndexOrThrow(DBContract.AgentEntry.COLUMN_SECURITY_QUESTION)));
        String decryptedSecurityAnswerHash =
                helper.decryptField(cursor.getString(cursor.getColumnIndexOrThrow(DBContract.AgentEntry.COLUMN_SECURITY_ANSWER_HASH)));

        String clearanceCode =
                cursor.getString(cursor.getColumnIndexOrThrow(DBContract.AgentEntry.COLUMN_CLEARANCE_CODE));
        ClearanceLevel clearanceLevel = ClearanceLevel.fromCode(clearanceCode);

        return new Agent(
                cursor.getString(cursor.getColumnIndexOrThrow(DBContract.AgentEntry.COLUMN_AGENT_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(DBContract.AgentEntry.COLUMN_CODENAME)),
                decryptedPasswordHash,
                decryptedSalt,
                decryptedSecurityQuestion,
                decryptedSecurityAnswerHash,
                clearanceLevel,
                cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.AgentEntry.COLUMN_BIOMETRIC_ENABLED)) == 1,
                cursor.getLong(cursor.getColumnIndexOrThrow(DBContract.AgentEntry.COLUMN_LAST_LOGIN_TIMESTAMP)),
                cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.AgentEntry.COLUMN_FAILED_ATTEMPTS)),
                cursor.getLong(cursor.getColumnIndexOrThrow(DBContract.AgentEntry.COLUMN_LAST_FAILED_TIMESTAMP)),
                cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.AgentEntry.COLUMN_ACCOUNT_LOCKED)) == 1
        );
    }
}