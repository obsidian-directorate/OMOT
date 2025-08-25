package org.obsidian.omot.domain;

import android.database.Cursor;

import org.obsidian.omot.core.crypto.Hashing;
import org.obsidian.omot.core.util.IDGenerator;
import org.obsidian.omot.core.util.Logs;
import org.obsidian.omot.core.util.Result;
import org.obsidian.omot.data.db.DBContract;
import org.obsidian.omot.data.repo.AgentRepository;

public class AuthService {
    private final AgentRepository agentRepo;

    public AuthService(AgentRepository agentRepo) {
        this.agentRepo = agentRepo;
    }

    public Result<AuthResult> login(String codename, String cipherKeyPlain) {
        try {
            Result<Cursor> res = agentRepo.findByCodename(codename);
            if (!res.isSuccess()) {
                return Result.failure(res.getError());
            }

            Cursor c = res.getData();
            if (c == null || !c.moveToFirst()) {
                Logs.write("UNKNOWN", "LOGIN_FAIL", "Codename not found: " + codename);
                return Result.failure(new Exception("Codename not found"));
            }

            String agentId = c.getString(c.getColumnIndexOrThrow(DBContract.Agents.COLUMN_AGENT_ID));
            String hash = c.getString(c.getColumnIndexOrThrow(DBContract.Agents.COLUMN_PASSWORD_HASH));
            int failedAttempts = c.getInt(c.getColumnIndexOrThrow(DBContract.Agents.COLUMN_FAILED_LOGIN_ATTEMPTS));
            long lastFailed = c.getLong(c.getColumnIndexOrThrow(DBContract.Agents.COLUMN_LAST_FAILED_LOGIN_TIMESTAMP));
            int locked = c.getInt(c.getColumnIndexOrThrow(DBContract.Agents.COLUMN_ACCOUNT_LOCKED));

            // Lockout check
            long now = System.currentTimeMillis();
            if (locked == 1 && (now - lastFailed < 10 * 60 * 1000)) {   // 10 minutes
                Logs.write(agentId, "LOGIN_LOCKED", "Account temporarily locked.");
                return Result.failure(new Exception("Account locked. Try later."));
            }

            boolean valid = Hashing.bcryptVerify(cipherKeyPlain, hash);
            if (!valid) {
                int newFails = failedAttempts + 1;
                agentRepo.incrementFailedAttempts(agentId, newFails, now);

                if (newFails >= 5) {
                    agentRepo.lockAccount(agentId);
                    Logs.write(agentId, "LOGIN_LOCKED", "Too many failed attempts.");
                    return Result.failure(new Exception("Account locked due to failed attempts."));
                }

                Logs.write(agentId, "LOGIN_FAIL", "Invalid cipher key");
                return Result.failure(new Exception("Invalid credentials"));
            }

            // Success: reset fails
            agentRepo.resetFailedAttempts(agentId);
            String clearance = c.getString(c.getColumnIndexOrThrow(DBContract.Agents.COLUMN_CLEARANCE_CODE));
            Logs.write(agentId, "LOGIN_SUCCESS", "Clearance: " + clearance);

            AuthResult ar = new AuthResult(agentId, codename, clearance);
            return Result.success(ar);
        } catch (Exception e) {
            return Result.failure(e);
        }
    }

    public Result<Boolean> register(String codename, String cipherKey, String securityQuestion, String securityAnswer, String clearanceCode) {
        try {
            String agentId = IDGenerator.nextAgentID();
            Result<Boolean> res = agentRepo.registerAgent(agentId, codename, cipherKey, securityQuestion, securityAnswer, clearanceCode);

            if (!res.isSuccess()) {
                Logs.write("UNKNOWN", "REGISTER_FAIL", "Codename: " + codename);
                return Result.failure(res.getError());
            }

            Logs.write(agentId, "REGISTER_SUCCESS", "Codename: " + codename);
            return Result.success(true);
        } catch (Exception e) {
            return Result.failure(e);
        }
    }

    public Result<Boolean> recoverCipherKey(String codename, String answerPlain, String newCipherKey) {
        try {
            Result<Boolean> verify = agentRepo.verifySecurityAnswer(codename, answerPlain);
            if (!verify.isSuccess() || !verify.getData()) {
                return Result.failure(new Exception("Recovery failed: wrong answer"));
            }
            return agentRepo.resetCipherKey(codename, newCipherKey);
        } catch (Exception e) {
            return Result.failure(e);
        }
    }
}