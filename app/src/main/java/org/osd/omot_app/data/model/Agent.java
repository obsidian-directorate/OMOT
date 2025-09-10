package org.osd.omot_app.data.model;

/**
 * Represents an operative of the Obsidian Directorate.
 * This is the main user entity for authentication and authorization.
 */
public class Agent {
    private String agentID;
    private String codename;
    private String passwordHash;
    private String salt;
    private String securityQuestion;
    private String securityAnswerHash;
    private ClearanceLevel clearanceLevel;
    private boolean biometricEnabled;
    private long lastLoginTimestamp;
    private int failedLoginAttempts;
    private long lastFailedLoginTimestamp;
    private boolean accountLocked;

    public Agent() {
    }

    public Agent(String agentID, String codename, String passwordHash, String salt, String securityQuestion, String securityAnswerHash, ClearanceLevel clearanceLevel, boolean biometricEnabled, long lastLoginTimestamp, int failedLoginAttempts, long lastFailedLoginTimestamp, boolean accountLocked) {
        this.agentID = agentID;
        this.codename = codename;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.securityQuestion = securityQuestion;
        this.securityAnswerHash = securityAnswerHash;
        this.clearanceLevel = clearanceLevel;
        this.biometricEnabled = biometricEnabled;
        this.lastLoginTimestamp = lastLoginTimestamp;
        this.failedLoginAttempts = failedLoginAttempts;
        this.lastFailedLoginTimestamp = lastFailedLoginTimestamp;
        this.accountLocked = accountLocked;
    }

    // Helper method to check if the account is temporarily locked due to failed attempts
    public boolean isTemporarilyLocked() {
        if (!accountLocked) return false;
        // Check if the lockout period (e.g., 10 minutes) has expired since the last failed attempt
        long lockedDurationMs = 10 * 60 * 1000; // 10 minutes in milliseconds
        return (System.currentTimeMillis() - lastFailedLoginTimestamp) < lockedDurationMs;
    }

    public String getAgentID() {
        return agentID;
    }

    public void setAgentID(String agentID) {
        this.agentID = agentID;
    }

    public String getCodename() {
        return codename;
    }

    public void setCodename(String codename) {
        this.codename = codename;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public String getSecurityAnswerHash() {
        return securityAnswerHash;
    }

    public void setSecurityAnswerHash(String securityAnswerHash) {
        this.securityAnswerHash = securityAnswerHash;
    }

    public ClearanceLevel getClearanceLevel() {
        return clearanceLevel;
    }

    public void setClearanceLevel(ClearanceLevel clearanceLevel) {
        this.clearanceLevel = clearanceLevel;
    }

    public boolean isBiometricEnabled() {
        return biometricEnabled;
    }

    public void setBiometricEnabled(boolean biometricEnabled) {
        this.biometricEnabled = biometricEnabled;
    }

    public long getLastLoginTimestamp() {
        return lastLoginTimestamp;
    }

    public void setLastLoginTimestamp(long lastLoginTimestamp) {
        this.lastLoginTimestamp = lastLoginTimestamp;
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public long getLastFailedLoginTimestamp() {
        return lastFailedLoginTimestamp;
    }

    public void setLastFailedLoginTimestamp(long lastFailedLoginTimestamp) {
        this.lastFailedLoginTimestamp = lastFailedLoginTimestamp;
    }

    public boolean isAccountLocked() {
        return accountLocked;
    }

    public void setAccountLocked(boolean accountLocked) {
        this.accountLocked = accountLocked;
    }
}