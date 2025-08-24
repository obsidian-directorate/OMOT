package org.obsidian.omot.data.repo;

import android.database.Cursor;

import org.obsidian.omot.core.util.Logs;
import org.obsidian.omot.core.util.Result;
import org.obsidian.omot.core.util.Validators;
import org.obsidian.omot.data.db.dao.AgentDAO;

public class AgentRepository {
    private final AgentDAO dao;

    public AgentRepository(AgentDAO dao) {
        this.dao = dao;
    }

    public Result<Boolean> registerAgent(String agentId, String codename, String cipherKeyPlain,
                                         String securityQuestion, String securityAnswerPlain, String clearanceCode) {
        if (!Validators.isCodenameValid(codename)) {
            return Result.failure(new Exception("Invalid codename"));
        }
        if (!Validators.isClearanceValid(clearanceCode)) {
            return Result.failure(new Exception("Invalid clearance code"));
        }
        if (!Validators.isCipherKeyStrong(cipherKeyPlain)) {
            return Result.failure(new Exception("Cipher key too weak"));
        }

        try {
            boolean inserted = dao.insertAgent(agentId, codename, cipherKeyPlain, securityQuestion, securityAnswerPlain, clearanceCode);
            if (!inserted) {
                Logs.write(agentId, "REGISTER_FAIL", "Codename: " + codename);
                return Result.failure(new Exception("Failed to insert agent"));
            }
            Logs.write(agentId, "REGISTER_SUCCESS", "Codename: " + codename);
            return Result.success(true);
        } catch (Exception e) {
            return Result.failure(e);
        }
    }

    public Result<Cursor> findByCodename(String codename) {
        try {
            Cursor c = dao.findByCodename(codename);
            return Result.success(c);
        } catch (Exception e) {
            return Result.failure(e);
        }
    }
}