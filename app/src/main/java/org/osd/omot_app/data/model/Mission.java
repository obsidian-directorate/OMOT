package org.osd.omot_app.data.model;

/**
 * Represents a mission or operation undertaken by the Directorate.
 */
public class Mission {
    public enum Status { ACTIVE, COMPLETED, DEACTIVATED, PENDING }
    public enum Priority { HIGH, MEDIUM, LOW }

    private String missionID;
    private String title;
    private Status status;
    private Priority priority;
    private long startDate;             // Unix timestamp
    private Long endDate;               // Nullable, Unix timestamp
    private String briefingFilePath;    // Path to a local file or asset

    public Mission() {
    }

    public Mission(String missionID, String title, Status status, Priority priority, long startDate, Long endDate, String briefingFilePath) {
        this.missionID = missionID;
        this.title = title;
        this.status = status;
        this.priority = priority;
        this.startDate = startDate;
        this.endDate = endDate;
        this.briefingFilePath = briefingFilePath;
    }

    public String getMissionID() {
        return missionID;
    }

    public void setMissionID(String missionID) {
        this.missionID = missionID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    public String getBriefingFilePath() {
        return briefingFilePath;
    }

    public void setBriefingFilePath(String briefingFilePath) {
        this.briefingFilePath = briefingFilePath;
    }
}