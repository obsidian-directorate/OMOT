package org.obsidian.omot.data.db;

import android.provider.BaseColumns;

public final class DBContract {
    private DBContract() {}

    // Clearance levels
    public static class ClearanceLevels implements BaseColumns {
        public static final String TB_NAME = "tb_clearance_levels";
        public static final String COLUMN_CLEARANCE_CODE = "clearance_code";
        public static final String COLUMN_CLEARANCE_NAME = "clearance_name";
        public static final String COLUMN_ROLE_DESC = "role_description";
    }

    // Agents
    public static class Agents implements BaseColumns {
        public static final String TB_NAME = "tb_agents";
        public static final String COLUMN_AGENT_ID = "agent_id";
        public static final String COLUMN_CODENAME = "codename";
        public static final String COLUMN_PASSWORD_HASH = "password_hash";
        public static final String COLUMN_SALT = "salt";
        public static final String COLUMN_SECURITY_QUESTION = "security_question";
        public static final String COLUMN_SECURITY_ANSWER_HASH = "security_answer_hash";
        public static final String COLUMN_CLEARANCE_CODE = "clearance_code";
        public static final String COLUMN_BIOMETRIC_ENABLED = "biometric_enabled";
        public static final String COLUMN_LAST_LOGIN_TIMESTAMP = "last_login_timestamp";
        public static final String COLUMN_FAILED_LOGIN_ATTEMPTS = "failed_login_attempts";
        public static final String COLUMN_LAST_FAILED_LOGIN_TIMESTAMP = "last_failed_login_timestamp";
        public static final String COLUMN_ACCOUNT_LOCKED = "account_locked";
    }

    // Missions
    public static class Missions implements BaseColumns {
        public static final String TB_NAME = "tb_missions";
        public static final String COLUMN_MISSION_ID = "mission_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_PRIORITY = "priority";
        public static final String COLUMN_START_DATE = "start_date";
        public static final String COLUMN_END_DATE = "end_date";
        public static final String COLUMN_BRIEFING_FILE = "briefing_file";
    }

    // Agent missions
    public static class AgentMissions implements BaseColumns {
        public static final String TB_NAME = "tb_agent_missions";
        public static final String COLUMN_AGENT_ID = "agent_id";
        public static final String COLUMN_MISSION_ID = "mission_id";
        public static final String COLUMN_ASSIGNMENT_DATE = "assignment_date";
    }

    // Dossiers
    public static class Dossiers implements BaseColumns {
        public static final String TB_NAME = "tb_dossiers";
        public static final String COLUMN_DOSSIER_ID = "dossier_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_CLEARANCE_REQUIRED = "clearance_required_code";
        public static final String COLUMN_CONTENT_FILE = "content_file";
        public static final String COLUMN_CREATED_AT = "created_at";
    }

    // Secure communications
    public static class SecureComms implements BaseColumns {
        public static final String TB_NAME = "tb_secure_comms";
        public static final String COLUMN_MESSAGE_ID = "message_id";
        public static final String COLUMN_SENDER_ID = "sender_id";
        public static final String COLUMN_RECIPIENT_ID = "recipient_id";
        public static final String COLUMN_ENCRYPTED_MESSAGE = "encrypted_message";
        public static final String COLUMN_SENT_AT = "sent_at";
    }

    // Tactical updates
    public static class TacticalUpdates implements BaseColumns {
        public static final String TB_NAME = "tb_tactical_updates";
        public static final String COLUMN_UPDATE_ID = "update_id";
        public static final String COLUMN_MISSION_ID = "mission_id";
        public static final String COLUMN_AGENT_ID = "agent_id";
        public static final String COLUMN_UPDATE_TIMESTAMP = "update_timestamp";
        public static final String COLUMN_UPDATE_DATA = "update_data";
        public static final String COLUMN_UPDATE_TYPE = "update_type";
    }

    // System logs
    public static class SystemLogs implements BaseColumns {
        public static final String TB_NAME = "tb_system_logs";
        public static final String COLUMN_LOG_ID = "log_id";
        public static final String COLUMN_AGENT_ID = "agent_id";
        public static final String COLUMN_ACTIONS = "actions";
        public static final String COLUMN_LOG_TIMESTAMP = "log_timestamp";
        public static final String COLUMN_DETAILS = "details";
    }
}