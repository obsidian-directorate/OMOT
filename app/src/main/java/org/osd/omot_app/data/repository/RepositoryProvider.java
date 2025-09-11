package org.osd.omot_app.data.repository;

import android.content.Context;

import org.osd.omot_app.data.DBHelper;
import org.osd.omot_app.data.dao.AgentDAO;
import org.osd.omot_app.data.dao.AgentDAOImpl;
import org.osd.omot_app.data.dao.ClearanceLevelDAO;
import org.osd.omot_app.data.dao.ClearanceLevelDAOImpl;

/**
 * Provides centralized access to repository instances.
 * This simplifies dependency throughout the application.
 */
public class RepositoryProvider {
    private static RepositoryProvider instance;
    private final Context context;
    private final DBHelper helper;

    private AgentRepository agentRepository;
    private ClearanceLevelDAO clearanceLevelDAO;

    private RepositoryProvider(Context context) {
        this.context = context;
        this.helper = new DBHelper(this.context);
    }

    public static synchronized RepositoryProvider getInstance(Context context) {
        if (instance == null) {
            instance = new RepositoryProvider(context);
        }
        return instance;
    }

    public AgentRepository getAgentRepository() {
        if (agentRepository == null) {
            AgentDAO agentDAO = new AgentDAOImpl(helper);
            agentRepository = new AgentRepository(agentDAO);
        }
        return agentRepository;
    }

    public ClearanceLevelDAO getClearanceLevelDAO() {
        if (clearanceLevelDAO == null) {
            clearanceLevelDAO = new ClearanceLevelDAOImpl(helper);
        }
        return clearanceLevelDAO;
    }

    /**
     * Close the database connection. Call this when the application is terminating.
     */
    public void close() {
        if (helper != null) {
            helper.close();
        }
    }
}