package org.obsidian.omot.domain;

public class AuthResult {
    private final String agentId;
    private final String codename;
    private final String clearance;

    public AuthResult(String agentId, String codename, String clearance) {
        this.agentId = agentId;
        this.codename = codename;
        this.clearance = clearance;
    }

    public String getAgentId() {
        return agentId;
    }

    public String getCodename() {
        return codename;
    }

    public String getClearance() {
        return clearance;
    }
}