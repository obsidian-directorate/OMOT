package org.osd.omot_app.data;

/**
 * Define the contract for the OMOT database, including table and column names.
 * This class cannot be instantiated.
 */
public final class DBContract {

    // Private constructor to prevent instantiation.
    private DBContract() {}

    /* Inner class that defines the ClearanceLevel table contents */
    public static class ClearanceLevelEntry {
        public static final String TABLE_NAME = "tb_clearance_levels";
        public static final String COLUMN_CLEARANCE_CODE = "clearance_code";
        public static final String COLUMN_LEVEL_NAME = "level_name";
        public static final String COLUMN_DESCRIPTION = "role_description";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_CLEARANCE_CODE + " TEXT PRIMARY KEY NOT NULL,"
                + COLUMN_LEVEL_NAME + " TEXT NOT NULL UNIQUE,"
                + COLUMN_DESCRIPTION + " TEXT NOT NULL"
                + ");";

        public static final String SQL_POPULATE_DATA =
                "INSERT INTO " + TABLE_NAME + " ("
                + COLUMN_CLEARANCE_CODE + ", "
                + COLUMN_LEVEL_NAME + ", "
                + COLUMN_DESCRIPTION
                + ") VALUES "
                + "('BETA', 'Field Agent', 'Regular ops, basic missions'),"
                + "('ALPHA', 'Senior Operative', 'Advanced dossiers, encrypted channels'),"
                + "('OMEGA', 'Command Authority', 'Full app access, manage agents, override'),"
                + "('SHADOW', 'Rogue Operative', 'Special conditions, monitored access');";
    }

    /* Inner class that defines the Agent table contents */
    public static class AgentEntry {
        public static final String TABLE_NAME = "tb_agents";
        public static final String COLUMN_AGENT_ID = "agent_id";
        public static final String COLUMN_CODENAME = "codename";
        public static final String COLUMN_PASSWORD_HASH = "password_hash";  // Will be encrypted
        public static final String COLUMN_SALT = "salt";    // Will be encrypted
        public static final String COLUMN_SECURITY_ANSWER_HASH = "security_answer_hash";    //
        // Will be encrypted
        public static final String COLUMN_SECURITY_QUESTION = "security_question"; // Will be encrypted
        public static final String COLUMN_CLEARANCE_CODE = "clearance_code";
        public static final String COLUMN_BIOMETRIC_ENABLED = "biometric_enabled";
        public static final String COLUMN_LAST_LOGIN_TIMESTAMP = "last_login_timestamp";
        public static final String COLUMN_FAILED_ATTEMPTS = "failed_attempts";
        public static final String COLUMN_LAST_FAILED_TIMESTAMP = "last_failed_login_timestamp";
        public static final String COLUMN_ACCOUNT_LOCKED = "account_locked";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_AGENT_ID + " TEXT PRIMARY KEY NOT NULL,"
                + COLUMN_CODENAME + " TEXT UNIQUE NOT NULL,"
                + COLUMN_PASSWORD_HASH + " BLOB NOT NULL,"  // Storing encrypted data as BLOB
                + COLUMN_SALT + " BLOB NOT NULL UNIQUE,"
                + COLUMN_SECURITY_QUESTION + " BLOB,"
                + COLUMN_SECURITY_ANSWER_HASH + " BLOB,"
                + COLUMN_CLEARANCE_CODE + " TEXT NOT NULL,"
                + COLUMN_BIOMETRIC_ENABLED + " INTEGER DEFAULT 0,"
                + COLUMN_LAST_LOGIN_TIMESTAMP + " INTEGER,"
                + COLUMN_FAILED_ATTEMPTS + " INTEGER DEFAULT 0,"
                + COLUMN_LAST_FAILED_TIMESTAMP + " INTEGER,"
                + COLUMN_ACCOUNT_LOCKED + " INTEGER DEFAULT 0,"
                + "FOREIGN KEY (" + COLUMN_CLEARANCE_CODE + ") REFERENCES "
                + ClearanceLevelEntry.TABLE_NAME + "(" + ClearanceLevelEntry.COLUMN_CLEARANCE_CODE + ")"
                + ");";
    }

    /* Inner class that defines the Mission table contents */
    public static class MissionEntry {
        public static final String TABLE_NAME = "tb_missions";
        public static final String COLUMN_MISSION_ID = "mission_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_PRIORITY = "priority";
        public static final String COLUMN_START_DATE = "start_date";
        public static final String COLUMN_END_DATE = "end_date";
        public static final String COLUMN_BRIEFING_FILE = "briefing_file";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_MISSION_ID + " TEXT PRIMARY KEY NOT NULL,"
                + COLUMN_TITLE + " TEXT NOT NULL,"
                + COLUMN_STATUS + " TEXT NOT NULL CHECK(" + COLUMN_STATUS + " IN ('Active', 'Completed', 'Deactivated', 'Pending')),"
                + COLUMN_PRIORITY + " TEXT NOT NULL CHECK(" + COLUMN_PRIORITY + " IN ('High', 'Medium', 'Low')),"
                + COLUMN_START_DATE + " INTEGER NOT NULL,"
                + COLUMN_END_DATE + " INTEGER,"
                + COLUMN_BRIEFING_FILE + " TEXT"
                + ");";
    }
}