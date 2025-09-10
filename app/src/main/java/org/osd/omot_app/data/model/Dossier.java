package org.osd.omot_app.data.model;

/**
 * Represents a intelligence dossier file, access to which is restricted by clearance level.
 */
public class Dossier {
    private String dossierID;
    private String title;
    private ClearanceLevel clearanceRequired;
    private String contentFilePath;             // Path to encrypted content
    private long createdAt;                     // Unix timestamp

    public Dossier() {
    }

    public Dossier(String dossierID, String title, ClearanceLevel clearanceRequired, String contentFilePath, long createdAt) {
        this.dossierID = dossierID;
        this.title = title;
        this.clearanceRequired = clearanceRequired;
        this.contentFilePath = contentFilePath;
        this.createdAt = createdAt;
    }

    // Helper method to check if an agent has sufficient clarance to view this dossier
    public boolean canBeAccessedBy(Agent agent) {
        // Logic to determine access based on clearance level hierarchy
        // This is a placeholder. Implement your clearance hierarchy logic here.
        // For example, OMEGA > ALPHA > BETA. SHADOW might have special rules.
        if (agent == null || agent.getClearanceLevel() == null) return false;
        // Simple implementation: Check if the agent's clearance code matches or is higher
        // This requires defining a hierarchy order.
        return agent.getClearanceLevel().getClearanceCode().equals(clearanceRequired.getClearanceCode());
    }

    public String getDossierID() {
        return dossierID;
    }

    public void setDossierID(String dossierID) {
        this.dossierID = dossierID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ClearanceLevel getClearanceRequired() {
        return clearanceRequired;
    }

    public void setClearanceRequired(ClearanceLevel clearanceRequired) {
        this.clearanceRequired = clearanceRequired;
    }

    public String getContentFilePath() {
        return contentFilePath;
    }

    public void setContentFilePath(String contentFilePath) {
        this.contentFilePath = contentFilePath;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}