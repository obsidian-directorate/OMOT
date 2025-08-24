package org.obsidian.omot.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "OMOT.db";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Clearance levels table
        String create_tb_clearance_levels = "CREATE TABLE IF NOT EXISTS " + DBContract.ClearanceLevels.TB_NAME + "("
                + DBContract.ClearanceLevels.COLUMN_CLEARANCE_CODE + " TEXT PRIMARY KEY NOT NULL,"
                + DBContract.ClearanceLevels.COLUMN_CLEARANCE_NAME + " TEXT NOT NULL UNIQUE,"
                + DBContract.ClearanceLevels.COLUMN_ROLE_DESC + " TEXT NOT NULL"
                + ");";
        db.execSQL(create_tb_clearance_levels);

        // Agents table
        String create_tb_agents = "CREATE TABLE IF NOT EXISTS " + DBContract.Agents.TB_NAME + "("
                + DBContract.Agents.COLUMN_AGENT_ID + " PRIMARY KEY NOT NULL,"          // e.g., 'AGENT-001'
                + DBContract.Agents.COLUMN_CODENAME + " TEXT UNIQUE NOT NULL,"
                + DBContract.Agents.COLUMN_PASSWORD_HASH + " TEXT NOT NULL,"            // bcrypt
                + DBContract.Agents.COLUMN_SALT + " TEXT NOT NULL UNIQUE,"              // random salt for bcrypt (kept for reference)
                + DBContract.Agents.COLUMN_SECURITY_QUESTION + " TEXT,"
                + DBContract.Agents.COLUMN_SECURITY_ANSWER_HASH + " TEXT,"              // bcrypt
                + DBContract.Agents.COLUMN_CLEARANCE_CODE + " TEXT NOT NULL,"
                + DBContract.Agents.COLUMN_BIOMETRIC_ENABLED + " INTEGER DEFAULT 0,"    // 0: false, 1: true
                + DBContract.Agents.COLUMN_LAST_LOGIN_TIMESTAMP + " INTEGER,"           // Unix time
                + DBContract.Agents.COLUMN_FAILED_LOGIN_ATTEMPTS + " INTEGER,"
                + DBContract.Agents.COLUMN_LAST_FAILED_LOGIN_TIMESTAMP + " INTEGER,"
                + DBContract.Agents.COLUMN_ACCOUNT_LOCKED + " INTEGER DEFAULT 0,"
                + "FOREIGN KEY (" + DBContract.Agents.COLUMN_CLEARANCE_CODE + ") REFERENCES " + DBContract.ClearanceLevels.TB_NAME + "(" + DBContract.ClearanceLevels.COLUMN_CLEARANCE_CODE + ")"
                + ");";
        db.execSQL(create_tb_agents);

        // Missions table
        String create_tb_missions = "CREATE TABLE IF NOT EXISTS " + DBContract.Missions.TB_NAME + "("
                + DBContract.Missions.COLUMN_MISSION_ID + " PRIMARY KEY NOT NULL,"      // e.g., 'MISSION-001'
                + DBContract.Missions.COLUMN_TITLE + " TEXT NOT NULL,"
                + DBContract.Missions.COLUMN_STATUS + " TEXT NOT NULL CHECK(" + DBContract.Missions.COLUMN_STATUS + " IN ('Active', 'Completed', 'Deactivated', 'Pending')),"
                + DBContract.Missions.COLUMN_PRIORITY + " TEXT NOT NULL CHECK(" + DBContract.Missions.COLUMN_PRIORITY + " IN ('High', 'Medium', 'Low')),"
                + DBContract.Missions.COLUMN_START_DATE + " INTEGER NOT NULL,"
                + DBContract.Missions.COLUMN_END_DATE + " INTEGER,"
                + DBContract.Missions.COLUMN_BRIEFING_FILE + " TEXT"                    // path/uri (encrypt path if sensitive at rest)
                + ");";
        db.execSQL(create_tb_missions);

        // Agent missions table
        String create_tb_agent_missions = "CREATE TABLE IF NOT EXISTS " + DBContract.AgentMissions.TB_NAME + "("
                + DBContract.AgentMissions.COLUMN_AGENT_ID + " TEXT NOT NULL,"
                + DBContract.AgentMissions.COLUMN_MISSION_ID + " TEXT NOT NULL,"
                + DBContract.AgentMissions.COLUMN_ASSIGNMENT_DATE + " INTEGER NOT NULL,"
                + "PRIMARY KEY (" + DBContract.AgentMissions.COLUMN_AGENT_ID + ", " + DBContract.AgentMissions.COLUMN_MISSION_ID + "),"
                + "FOREIGN KEY (" + DBContract.AgentMissions.COLUMN_AGENT_ID + ") REFERENCES " + DBContract.Agents.TB_NAME + "(" + DBContract.Agents.COLUMN_AGENT_ID + "),"
                + "FOREIGN KEY (" + DBContract.AgentMissions.COLUMN_MISSION_ID + ") REFERENCES " + DBContract.Missions.TB_NAME + "(" + DBContract.Missions.COLUMN_MISSION_ID + ")"
                + ");";
        db.execSQL(create_tb_agent_missions);

        // Dossiers table
        String create_tb_dossiers = "CREATE TABLE IF NOT EXISTS " + DBContract.Dossiers.TB_NAME + "("
                + DBContract.Dossiers.COLUMN_DOSSIER_ID + " PRIMARY KEY NOT NULL,"
                + DBContract.Dossiers.COLUMN_TITLE + " TEXT NOT NULL,"
                + DBContract.Dossiers.COLUMN_CLEARANCE_REQUIRED + " TEXT NOT NULL,"
                + DBContract.Dossiers.COLUMN_CONTENT_FILE + " TEXT NOT NULL,"           // path/uri (encrypt path if sensitive at rest)
                + DBContract.Dossiers.COLUMN_CREATED_AT + " INTEGER NOT NULL,"
                + "FOREIGN KEY (" + DBContract.Dossiers.COLUMN_CLEARANCE_REQUIRED + ") REFERENCES " + DBContract.ClearanceLevels.TB_NAME + "(" + DBContract.ClearanceLevels.COLUMN_CLEARANCE_CODE + ")"
                + ");";
        db.execSQL(create_tb_dossiers);

        // Secure communications table
        String create_tb_secure_comms = "CREATE TABLE IF NOT EXISTS " + DBContract.SecureComms.TB_NAME + "("
                + DBContract.SecureComms.COLUMN_MESSAGE_ID + " TEXT PRIMARY KEY NOT NULL,"
                + DBContract.SecureComms.COLUMN_SENDER_ID + " TEXT NOT NULL,"
                + DBContract.SecureComms.COLUMN_RECIPIENT_ID + " TEXT NOT NULL,"
                + DBContract.SecureComms.COLUMN_ENCRYPTED_MESSAGE + " TEXT NOT NULL,"   // AES-GCM ciphertext (Base64)
                + DBContract.SecureComms.COLUMN_SENT_AT + " INTEGER NOT NULL,"
                + "FOREIGN KEY (" + DBContract.SecureComms.COLUMN_SENDER_ID + ") REFERENCES " + DBContract.Agents.TB_NAME + "(" + DBContract.Agents.COLUMN_AGENT_ID + "),"
                + "FOREIGN KEY (" + DBContract.SecureComms.COLUMN_RECIPIENT_ID + ") REFERENCES " + DBContract.Agents.TB_NAME + "(" + DBContract.Agents.COLUMN_AGENT_ID + ")"
                + ");";
        db.execSQL(create_tb_secure_comms);

        // Tactical updates table
        String create_tb_tactical_updates = "CREATE TABLE IF NOT EXISTS " + DBContract.TacticalUpdates.TB_NAME + "("
                + DBContract.TacticalUpdates.COLUMN_UPDATE_ID + " PRIMARY KEY NOT NULL,"
                + DBContract.TacticalUpdates.COLUMN_MISSION_ID + " TEXT NOT NULL,"
                + DBContract.TacticalUpdates.COLUMN_AGENT_ID + " TEXT,"
                + DBContract.TacticalUpdates.COLUMN_UPDATE_TIMESTAMP + " INTEGER NOT NULL,"     // AES-GCM ciphertext (Base64)
                + DBContract.TacticalUpdates.COLUMN_UPDATE_DATA + " TEXT NOT NULL,"
                + DBContract.TacticalUpdates.COLUMN_UPDATE_TYPE + " TEXT NOT NULL CHECK (" + DBContract.TacticalUpdates.COLUMN_UPDATE_TYPE + " IN ('Location', 'Intel', 'Status')),"
                + "FOREIGN KEY (" + DBContract.TacticalUpdates.COLUMN_MISSION_ID + ") REFERENCES " + DBContract.Missions.TB_NAME + "(" + DBContract.Missions.COLUMN_MISSION_ID + "),"
                + "FOREIGN KEY (" + DBContract.TacticalUpdates.COLUMN_AGENT_ID + ") REFERENCES " + DBContract.Agents.TB_NAME + "(" + DBContract.Agents.COLUMN_AGENT_ID + ")"
                + ");";
        db.execSQL(create_tb_tactical_updates);

        // System logs table
        String create_tb_system_logs = "CREATE TABLE IF NOT EXISTS " + DBContract.SystemLogs.TB_NAME + "("
                + DBContract.SystemLogs.COLUMN_LOG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DBContract.SystemLogs.COLUMN_AGENT_ID + " TEXT,"
                + DBContract.SystemLogs.COLUMN_ACTIONS + " TEXT NOT NULL,"
                + DBContract.SystemLogs.COLUMN_LOG_TIMESTAMP + " INTEGER NOT NULL,"
                + DBContract.SystemLogs.COLUMN_DETAILS + " TEXT,"
                + "FOREIGN KEY (" + DBContract.SystemLogs.COLUMN_AGENT_ID + ") REFERENCES " + DBContract.Agents.TB_NAME + "(" + DBContract.Agents.COLUMN_AGENT_ID + ")"
                + ");";
        db.execSQL(create_tb_system_logs);

        // Seed core clearance levels
        String clearance_seed = "INSERT OR IGNORE INTO " + DBContract.ClearanceLevels.TB_NAME + "("
                + DBContract.ClearanceLevels.COLUMN_CLEARANCE_CODE + ", "
                + DBContract.ClearanceLevels.COLUMN_CLEARANCE_NAME + ", "
                + DBContract.ClearanceLevels.COLUMN_ROLE_DESC + ") VALUES "
                + "('BETA', 'Field Agent', 'Regular ops, basic missions'),"
                + "('ALPHA','Senior Operative','Advanced dossiers, encrypted channels'),"
                + "('OMEGA','Command Authority','Full app access, override'),"
                + "('SHADOW','Rogue Operative','Read-only, monitored');";
        db.execSQL(clearance_seed);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.ClearanceLevels.TB_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.Agents.TB_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.Missions.TB_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.AgentMissions.TB_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.Dossiers.TB_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.SecureComms.TB_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.TacticalUpdates.TB_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.SystemLogs.TB_NAME);
    }
}