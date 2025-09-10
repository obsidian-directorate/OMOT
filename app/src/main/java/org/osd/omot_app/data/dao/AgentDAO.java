package org.osd.omot_app.data.dao;

import org.osd.omot_app.data.model.Agent;

import java.util.List;

/**
 * Data Access Object (DAO) for the Agent table.
 * Defines the database operations available for the Agent entity.
 */
public interface AgentDAO {

    /**
     * Inserts a new agent into the database.
     * @param agent The agent to insert.
     * @return The row ID of the newly inserted agent, or -1 if an error occurred.
     */
    long insertAgent(Agent agent);

    /**
     * Retrieves an agent by their unique codename.
     * @param codename The agent's codename.
     * @return The Agent object if found, null otherwise.
     */
    Agent getAgentByCodename(String codename);

    /**
     * Retrieves an agent by their unique agent ID.
     * @param agentID The agent's ID (e.g., "AGENT-001").
     * @return The Agent object if found, null otherwise.
     */
    Agent getAgentByID(String agentID);

    /**
     * Retrieves all agents from the database.
     * @return A list of all agents.
     */
    List<Agent> getAllAgents();

    /**
     * Updates an existing agent's information in the database.
     * @param agent The agent object with updated values.
     * @return The number of rows affected (should be 1 if successful).
     */
    int updateAgent(Agent agent);

    /**
     * Deletes an agent from the database.
     * @param agentID The ID of the agent to delete.
     * @return The number of rows affected (should be 1 if successful).
     */
    int deleteAgent(String agentID);

    /**
     * Increments the failed login attempts counter for an agent and timestamps the attempt.
     * @param codename The codename of the agent who failed to log in.
     * @return true if the operation was successful, false otherwise.
     */
    boolean recordFailedLoginAttempt(String codename);

    /**
     * Resets the failed login attempts counter and updates the last login timestamp upon successful login.
     * @param codename The codename of the agent who successfully logged in.
     * @return true if the operation was successful, false otherwise.
     */
    boolean recordSuccessfulLogin(String codename);

    /**
     * Locks or unlocks an agent's account.
     * @param codename The codename of the agent.
     * @param locked true to lock the account, false to unlock it.
     * @return true if the operation was successful, false otherwise.
     */
    boolean setAccountLockStatus(String codename, boolean locked);
}