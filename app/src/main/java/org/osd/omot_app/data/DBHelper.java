package org.osd.omot_app.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import org.osd.omot_app.security.CryptoManager;

/**
 * Manages the OMOT application database creation, version management, and provides access.
 * Handles encryption of sensitive fields before storage.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String TAG = "DBHelper";

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "OMOT.db";

    private final CryptoManager cryptoManager;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.cryptoManager = new CryptoManager(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create all tables
        try {
            db.execSQL(DBContract.ClearanceLevelEntry.SQL_CREATE_TABLE);
            db.execSQL(DBContract.AgentEntry.SQL_CREATE_TABLE);
            db.execSQL(DBContract.MissionEntry.SQL_CREATE_TABLE);
            // TODO: Execute other table creation SQL statements (Dossiers, SecureCommunications, etc.)

            // Populate the ClearanceLevel table with default data
            db.execSQL(DBContract.ClearanceLevelEntry.SQL_POPULATE_DATA);

            Log.i(TAG, "Database created successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error creating database", e);
            throw new RuntimeException("Database creation failed", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
        // Drop tables if they exist
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.ClearanceLevelEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.AgentEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.MissionEntry.TABLE_NAME);

        // Create table again
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        // Enable foreign key constraints
        db.setForeignKeyConstraintsEnabled(true);
    }

    /**
     * Helper method to encrypt a value before inserting it into the database.
     * Returns null if encryption fails.
     */
    @Nullable
    public String encryptField(String plaintext) {
        if (plaintext == null) return null;
        return cryptoManager.encrypt(plaintext);
    }

    /**
     * Helper method to decrypt a value after reading it from the database.
     * Returns null if decryption fails.
     */
    @Nullable
    public String decryptField(String encryptedText) {
        if (encryptedText == null) return null;
        return cryptoManager.decrypt(encryptedText);
    }

    /**
     * Insert an agent with encrypted sensitive fields.
     */
    public long insertAgent(ContentValues values) {
        // Encrypt sensitive fields before inserting
        String passwordHash = values.getAsString(DBContract.AgentEntry.COLUMN_PASSWORD_HASH);
        if (passwordHash != null) {
            values.put(DBContract.AgentEntry.COLUMN_PASSWORD_HASH, encryptField(passwordHash));
        }

        String salt = values.getAsString(DBContract.AgentEntry.COLUMN_SALT);
        if (salt != null) {
            values.put(DBContract.AgentEntry.COLUMN_SALT, encryptField(salt));
        }

        String securityQuestion = values.getAsString(DBContract.AgentEntry.COLUMN_SECURITY_QUESTION);
        if (securityQuestion != null) {
            values.put(DBContract.AgentEntry.COLUMN_SECURITY_QUESTION, encryptField(securityQuestion));
        }

        String securityAnswerHash = values.getAsString(DBContract.AgentEntry.COLUMN_SECURITY_ANSWER_HASH);
        if (securityAnswerHash != null) {
            values.put(DBContract.AgentEntry.COLUMN_SECURITY_ANSWER_HASH, encryptField(securityAnswerHash));
        }

        SQLiteDatabase db = this.getWritableDatabase();
        return db.insert(DBContract.AgentEntry.TABLE_NAME, null, values);
    }

    /**
     * Performs a security self-check. This should be called during app startup
     * to ensure the encryption system is functional before any data operations.
     * @return true if the encryption key is available and working, false otherwise.
     */
    public boolean isDatabaseSecure() {
        return cryptoManager.isSecurityKeyAvailable();
    }
}