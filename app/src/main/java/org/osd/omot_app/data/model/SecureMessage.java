package org.osd.omot_app.data.model;

/**
 * Represents an encrypted message sent between agents.
 */
public class SecureMessage {
    private String messageID;
    private String senderID;
    private String recipientID;
    private String encryptedContent;    // Base64 encoded encrypted data
    private long sentAt;                // Unix timestamp
    private Long readAt;                // Nullable, Unix timestamp
    private Long selfDestructAt;        // Nullable, Unix timestamp

    public SecureMessage() {
    }

    public SecureMessage(String messageID, String senderID, String recipientID, String encryptedContent, long sentAt, Long readAt, Long selfDestructAt) {
        this.messageID = messageID;
        this.senderID = senderID;
        this.recipientID = recipientID;
        this.encryptedContent = encryptedContent;
        this.sentAt = sentAt;
        this.readAt = readAt;
        this.selfDestructAt = selfDestructAt;
    }

    // Helper method to check if the message has been read
    public boolean isRead() {
        return readAt != null;
    }

    // Helper method to check if the message should self-destruct
    public boolean shouldSelfDestruct() {
        return selfDestructAt != null && System.currentTimeMillis() > selfDestructAt;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getRecipientID() {
        return recipientID;
    }

    public void setRecipientID(String recipientID) {
        this.recipientID = recipientID;
    }

    public String getEncryptedContent() {
        return encryptedContent;
    }

    public void setEncryptedContent(String encryptedContent) {
        this.encryptedContent = encryptedContent;
    }

    public long getSentAt() {
        return sentAt;
    }

    public void setSentAt(long sentAt) {
        this.sentAt = sentAt;
    }

    public Long getReadAt() {
        return readAt;
    }

    public void setReadAt(Long readAt) {
        this.readAt = readAt;
    }

    public Long getSelfDestructAt() {
        return selfDestructAt;
    }

    public void setSelfDestructAt(Long selfDestructAt) {
        this.selfDestructAt = selfDestructAt;
    }
}