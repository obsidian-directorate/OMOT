package org.obsidian.omot.data.repo;

import org.obsidian.omot.core.util.Result;
import org.obsidian.omot.data.db.dao.CommsDAO;

public class CommsRepository {
    private final CommsDAO dao;

    public CommsRepository(CommsDAO dao) {
        this.dao = dao;
    }

    public Result<Boolean> sendMessage(String messageId, String senderId, String recipientId, String messagePlain, long sentAt) {
        try {
            boolean ok = dao.insertEncryptedMessage(messageId, senderId, recipientId, messagePlain, sentAt);
            if (!ok) return Result.failure(new Exception("Failed to insert message."));
            return Result.success(true);
        } catch (Exception e) {
            return Result.failure(e);
        }
    }

    public Result<String> readMessage(String messageId) {
        try {
            String msg = dao.getMessagePlaintext(messageId);
            if (msg == null) return Result.failure(new Exception("Message not found or decryption error."));
            return Result.success(msg);
        } catch (Exception e) {
            return Result.failure(e);
        }
    }
}