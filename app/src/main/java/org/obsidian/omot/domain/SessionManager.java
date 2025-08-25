package org.obsidian.omot.domain;

import org.obsidian.omot.core.secure.SecurePrefs;

import java.util.UUID;

public class SessionManager {
    private static final String KEY_LAST_AGENT = "last_agent_id";
    private static final String KEY_SESSION_TOKEN = "session_token";

    private final SecurePrefs prefs;

    public SessionManager(SecurePrefs prefs) {
        this.prefs = prefs;
    }

    public void startSession(String agentId) {
        prefs.putString(KEY_LAST_AGENT, agentId);
        prefs.putString(KEY_SESSION_TOKEN, UUID.randomUUID().toString());
    }

    public void endSession() {
        prefs.clear();
    }

    public String getLastAgentId() {
        return prefs.getString(KEY_LAST_AGENT, null);
    }

    public String getSessionToken() {
        return prefs.getString(KEY_SESSION_TOKEN, null);
    }

    public boolean isSessionActive() {
        return getSessionToken() != null;
    }
}