package org.osd.omot_app.data.results;

/**
 * Result class for registration operations.
 */
public class RegistrationResult {

    private boolean success;
    private String message;
    private String agentID;
    private String codename;

    public RegistrationResult() {
    }

    public RegistrationResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public RegistrationResult(boolean success, String message, String agentID) {
        this.success = success;
        this.message = message;
        this.agentID = agentID;
    }

    public RegistrationResult(boolean success, String message, String agentID, String codename) {
        this.success = success;
        this.message = message;
        this.agentID = agentID;
        this.codename = codename;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getAgentID() {
        return agentID;
    }

    public String getCodename() {
        return codename;
    }
}