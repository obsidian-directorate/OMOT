package org.obsidian.omot.core.util;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generates unique IDs.
 * Two modes: sequential and timestamp-based.
 */
public class IDGenerator {
    private static final AtomicInteger agentCounter = new AtomicInteger(1);
    private static final AtomicInteger missionCounter = new AtomicInteger(1);

    public static String nextAgentID() {
        int id = agentCounter.getAndIncrement();
        return String.format(Locale.US, "AGENT-%03d", id);
    }

    public static String nextMissionID() {
        int id = missionCounter.getAndIncrement();
        return String.format(Locale.US, "MISSION-%03d", id);
    }
}