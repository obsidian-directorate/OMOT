package org.osd.omot_app.data.model;

import androidx.annotation.NonNull;

/**
 * Represents an agent's access level within the Obsidian Directorate.
 * This is a reference entity stored in the database.
 */
public class ClearanceLevel {
    private String clearanceCode;
    private String name;
    private String description;

    public ClearanceLevel() {
    }

    public ClearanceLevel(String clearanceCode, String name, String description) {
        this.clearanceCode = clearanceCode;
        this.name = name;
        this.description = description;
    }

    public String getClearanceCode() {
        return clearanceCode;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setClearanceCode(String clearanceCode) {
        this.clearanceCode = clearanceCode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Pre-defined instances for known clearance levels
    public static final ClearanceLevel BETA = new ClearanceLevel("BETA", "Field Agent", "Regular ops, basic missions");
    public static final ClearanceLevel ALPHA = new ClearanceLevel("ALPHA", "Senior Operative", "Advanced dossiers, encrypted channels");
    public static final ClearanceLevel OMEGA = new ClearanceLevel("OMEGA", "Command Authority", "Full app access, manage agents, override");
    public static final ClearanceLevel SHADOW = new ClearanceLevel("SHADOW", "Rogue Operative", "Special conditions, monitored access");

    /**
     * Helper method to get a ClearanceLevel from its code.
     * @param code The clearance code (e.g., "BETA")
     * @return The corresponding ClearanceLevel, or null if not found.
     */
    public static ClearanceLevel fromCode(String code) {
        if (code == null) return null;
        switch (code) {
            case "BETA": return BETA;
            case "ALPHA": return ALPHA;
            case "OMEGA": return OMEGA;
            case "SHADOW": return SHADOW;
            default: return null;
        }
    }
}