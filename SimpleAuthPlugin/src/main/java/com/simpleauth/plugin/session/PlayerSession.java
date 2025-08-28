package com.simpleauth.plugin.session;

import java.util.UUID;

/**
 * Represents a player session
 */
public class PlayerSession {
    private final UUID playerUuid;
    private final String ip;
    private final long creationTime;

    /**
     * Constructor for PlayerSession
     * 
     * @param playerUuid The player's UUID
     * @param ip The player's IP address
     * @param creationTime The session creation time
     */
    public PlayerSession(UUID playerUuid, String ip, long creationTime) {
        this.playerUuid = playerUuid;
        this.ip = ip;
        this.creationTime = creationTime;
    }

    /**
     * Get the player's UUID
     * 
     * @return The player's UUID
     */
    public UUID getPlayerUuid() {
        return playerUuid;
    }

    /**
     * Get the player's IP address
     * 
     * @return The player's IP address
     */
    public String getIp() {
        return ip;
    }

    /**
     * Get the session creation time
     * 
     * @return The session creation time
     */
    public long getCreationTime() {
        return creationTime;
    }
}

