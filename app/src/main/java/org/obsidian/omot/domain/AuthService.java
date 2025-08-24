package org.obsidian.omot.domain;

import android.database.Cursor;

import org.obsidian.omot.core.crypto.Hashing;
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

            boolean valid = Hashing.bcryptVerify(cipherKeyPlain, hash);
            if (!valid) {
                Logs.write(agentId, "LOGIN_FAIL", "Invalid cipher key");
                return Result.failure(new Exception("Invalid credentials"));
            }

            String clearance = c.getString(c.getColumnIndexOrThrow(DBContract.Agents.COLUMN_CLEARANCE_CODE));
            Logs.write(agentId, "LOGIN_SUCCESS", "Clearance: " + clearance);

            AuthResult ar = new AuthResult(agentId, codename, clearance);
            return Result.success(ar);
        } catch (Exception e) {
            return Result.failure(e);
        }
    }
}