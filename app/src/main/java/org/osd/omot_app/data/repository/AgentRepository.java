package org.osd.omot_app.data.repository;

import android.util.Base64;
import android.util.Log;

import org.osd.omot_app.data.dao.AgentDAO;
import org.osd.omot_app.data.model.Agent;
import org.osd.omot_app.data.model.ClearanceLevel;
import org.osd.omot_app.data.results.RegistrationResult;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class AgentRepository {
    private static final String TAG = "AgentRepository";
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;  // 16 bytes for the salt

    private final AgentDAO agentDAO;
    private final SecureRandom secureRandom;

    public AgentRepository(AgentDAO agentDAO) {
        this.agentDAO = agentDAO;
        this.secureRandom = new SecureRandom();
    }

    /**
     * Attempts to authenticate an agent using their codename and password.
     * Handles all security logic: account lock checks, password hashing, and attempt tracking.
     *
     * @param codename The agent's codename.
     * @param password The plaintext password (Cipher Key) entered by the user.
     * @return The authenticated Agent object if successful, null otherwise.
     */
    public Agent loginAgent(String codename, String password) {
        // 1. Retrieve the agent from the database
        Agent agent = agentDAO.getAgentByCodename(codename);
        if (agent == null) {
            Log.w(TAG, "Login failed: Agent not found with codename: " + codename);

            return null;    // Agent doesn't exist
        }

        // 2. Check if account is locked
        if (agent.isAccountLocked()) {
            if (agent.isTemporarilyLocked()) {
                Log.w(TAG, "Login failed: Account temporarily locked fo: " + codename);

                return null;
            } else {
                // Lockout period has expired, auto-unlock the account
                agentDAO.setAccountLockStatus(codename, false);
                agent = agentDAO.getAgentByCodename(codename);  // Refresh agent object

                if (agent == null) return null;
            }
        }

        // 3. Hash the input password with the stored salt
        String hashedInputPassword = hashPassword(password, agent.getSalt());

        // 4. Compare the hashed input with the stored hash
        if (hashedInputPassword != null && hashedInputPassword.equals(agent.getPasswordHash())) {
            // 5. Password matches - record successful login
            boolean success = agentDAO.recordSuccessfulLogin(codename);
            if (success) {
                Log.i(TAG, "Login successful for agent: " + codename);
                return agentDAO.getAgentByCodename(codename);   // Return refreshed agent object
            }
        } else {
            // 6. Password doesn't match - record failed attempt
            Log.w(TAG, "Login failed: Invalid credentials for: " + codename);
            agentDAO.recordFailedLoginAttempt(codename);
        }

        return null;    // Authentication failed
    }

    /**
     * Registers a new agent in the system. Generates a new salt and hashes the password.
     *
     * @param codename           The chosen codename (must be unique).
     * @param password           The plaintext password (Cipher Key).
     * @param securityQuestion   The security question for recovery.
     * @param securityAnswer     The plaintext answer to the security question.
     * @param enableBiometric    Whether to enable biometric authentication.
     * @return true if registration was successful, false otherwise.
     */
    public RegistrationResult registerAgent(String codename, String password,
                                            String securityQuestion, String securityAnswer,
                                            boolean enableBiometric) {
        try {
            // 1. Validate input parameters
            if (codename == null || codename.trim().isEmpty()) {
                return new RegistrationResult(false, "Codename can't be empty");
            }

            if (codename.length() < 3) {
                return new RegistrationResult(false, "Codename must be at least 3 characters");
            }

            if (password == null || password.length() < 8) {
                return new RegistrationResult(false, "Cipher key must be at least 8 characters");
            }

            if (securityQuestion == null || securityQuestion.trim().isEmpty()) {
                return new RegistrationResult(false, "Security question cannot be empty");
            }

            if (securityQuestion.length() < 10) {
                return new RegistrationResult(false, "Security question must be more specific");
            }

            if (securityAnswer == null || securityAnswer.trim().isEmpty()) {
                return new RegistrationResult(false, "Security answer cannot be empty");
            }

            if (securityAnswer.length() < 3) {
                return new RegistrationResult(false, "Security answer too short");
            }

            // 2. Check if codename is already taken
            if (!agentDAO.isCodenameAvailable(codename)) {
                return new RegistrationResult(false, "Codename already taken. Choose another");
            }

            // 3. Generate unique agent ID
            String agentID = generateAgentID();
            if (agentID == null) {
                return new RegistrationResult(false, "Failed to generate agent ID");
            }

            // 4. Generate a unique salt for this agent
            String salt = generateSalt();

            // 5. Hash the password with the generated salt
            String hashedPassword = hashPassword(password, salt);
            if (hashedPassword == null) {
                return new RegistrationResult(false, "Password hashing failed");
            }

            // 6. Hash the security answer (using the same salt for simplicity)
            String hashedSecurityAnswer = hashPassword(securityAnswer, salt);
            if (hashedSecurityAnswer == null) {
                return new RegistrationResult(false, "Security answer hashing failed");
            }

            // 7. Create the new Agent object with BETA clearance (default for new agents)
            Agent newAgent = new Agent(
                    agentID,
                    codename.trim(),
                    hashedPassword,
                    salt,
                    securityQuestion.trim(),
                    hashedSecurityAnswer,
                    ClearanceLevel.BETA,    // New agents start at BETAA level
                    enableBiometric,
                    0,     // last_login_timestamp
                    0,     // failed_login_attempts
                    0,     // last_failed_login_timestamp
                    false  // account_locked
            );

            // 8. Insert into database
            long result = agentDAO.insertAgent(newAgent);
            boolean success = result != -1;

            if (success) {
                Log.i(TAG, "Agent registered successfully: " + codename + " (ID: " + agentID + ")");
                return new RegistrationResult(true, "Registration successful", agentID, codename);
            } else {
                Log.e(TAG, "Registration failed: Database insertion failed for: " + codename);
                return new RegistrationResult(false, "Database error. Please try again.", codename);
            }
        } catch (Exception e) {
            Log.e(TAG, "Registration failed with error: " + e.getMessage(), e);
            return new RegistrationResult(false,
                    "Unexpected error during registration: " + e.getMessage());
        }
    }

    /**
     * Retrieves an agent by their codename without performing authentication checks.
     * Useful for biometric authentication and profile management.
     *
     * @param codename The agent's codename.
     * @return The Agent object if found, null otherwise.
     */
    public Agent getAgentByCodename(String codename) {
        try {
            agentDAO.getAgentByCodename(codename);
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving agent by codename: " + codename, e);
            return null;
        }
        return null;
    }

    /**
     * Initiates the Lost Credentials Protocol for password recovery.
     *
     * @param codename        The agent's codename.
     * @param securityAnswer The plaintext answer to the security question.
     * @return The security question if the agent exists and the answer is correct, null otherwise.
     */
    public String initiatePasswordRecovery(String codename, String securityAnswer) {
        Agent agent = agentDAO.getAgentByCodename(codename);
        if (agent == null) {
            Log.w(TAG, "Recovery failed: Agent not found: " + codename);
            return null;
        }

        // Hash the provided answer and compare with stored hash
        String hashedInputAnswer = hashPassword(securityAnswer, agent.getSalt());
        if (hashedInputAnswer != null && hashedInputAnswer.equals(agent.getSecurityAnswerHash())) {
            Log.i(TAG, "Recovery question answered correctly for: " + codename);
            return agent.getSecurityQuestion();
        } else {
            Log.w(TAG, "Recovery failed: Incorrect security answer for: " + codename);
            return null;
        }
    }

    /**
     * Resets an agent's password after successful recovery authentication.
     *
     * @param codename    The agent's codename.
     * @param newPassword The new plaintext password.
     * @return true if the password was reset successfully, false otherwise.
     */
    public boolean resetPassword(String codename, String newPassword) {
        Agent agent = agentDAO.getAgentByCodename(codename);
        if (agent == null) return false;

        // Use the existing salt to hash the new password
        String newHashedPassword = hashPassword(newPassword, agent.getSalt());
        if (newHashedPassword == null) return false;

        // For a robust implementation, we would need an update method in the DAO that updates specific fields.
        // This is a simplified approach creating a new agent object with updated password.
        Agent updatedAgent = new Agent(
                agent.getAgentID(),
                agent.getCodename(),
                newHashedPassword,
                agent.getSalt(),
                agent.getSecurityQuestion(),
                agent.getSecurityAnswerHash(),
                agent.getClearanceLevel(),
                agent.isBiometricEnabled(),
                agent.getLastLoginTimestamp(),
                agent.getFailedLoginAttempts(),
                agent.getLastFailedLoginTimestamp(),
                agent.isAccountLocked()
        );
        
        int rowsAffected = agentDAO.updateAgent(updatedAgent);
        boolean success = rowsAffected > 0;
        
        if (success) {
            Log.i(TAG, "Password reset successfully for: " + codename);
        } else {
            Log.e(TAG, "Password reset failed for: " + codename);
        }

        return success;
    }

    /**
     * Enables or disables biometric authentication for an agent.
     *
     * @param codename The agent's codename.
     * @param enabled  true to enable biometric auth, false to disable.
     * @return true if the operation was successful, false otherwise.
     */
    public boolean setBiometricEnabled(String codename, boolean enabled) {
        Agent agent = agentDAO.getAgentByCodename(codename);
        if (agent == null) return false;

        // Create updated agent object
        Agent updatedAgent = new Agent(
                agent.getAgentID(),
                agent.getCodename(),
                agent.getPasswordHash(),
                agent.getSalt(),
                agent.getSecurityQuestion(),
                agent.getSecurityAnswerHash(),
                agent.getClearanceLevel(),
                enabled,
                agent.getLastLoginTimestamp(),
                agent.getFailedLoginAttempts(),
                agent.getLastFailedLoginTimestamp(),
                agent.isAccountLocked()
        );

        int rowsAffected = agentDAO.updateAgent(updatedAgent);
        return rowsAffected > 0;
    }

    /**
     * Checks if an agent exists with the given codename.
     *
     * @param codename The codename to check.
     * @return true if the agent exists, false otherwise.
     */
    public boolean agentExists(String codename) {
        return agentDAO.getAgentByCodename(codename) != null;
    }

    /**
     * Hashes a password with a given salt using SHA-256.
     *
     * @param password The plaintext password to hash.
     * @param salt     The salt to use for hashing (Base64 encoded).
     * @return The hashed password, or null if hashing failed.
     */
    private String hashPassword(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            // Decode the salt from Base64 back to bytes
            byte[] saltBytes = Base64.decode(salt, Base64.NO_WRAP);

            // Combine password and salt bytes
            digest.update(saltBytes);
            byte[] hashedBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            // Return the hash as a Base64 string
            return Base64.encodeToString(hashedBytes, Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Hashing algorithm not available: " + HASH_ALGORITHM, e);
            return null;
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Invalid salt format", e);
            return null;
        }
    }

    /**
     * Generates a cryptographically secure random salt.
     *
     * @return A Base64 encoded string of the random salt.
     */
    private String generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);
        return Base64.encodeToString(salt, Base64.NO_WRAP);
    }

    private String generateAgentID() {
        try {
            int maxID = agentDAO.getMaxAgentIDNumber();
            int nextID = maxID + 1;
            return String.format("AGENT-%03d", nextID);
        } catch (Exception e) {
            Log.e(TAG, "Failed to generate agent ID", e);
            return null;
        }
    }
}