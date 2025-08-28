package com.simpleauth.plugin.database;

import com.simpleauth.plugin.model.PlayerAuth;

import java.util.List;

/**
 * Interface for authentication data sources
 */
public interface AuthDataSource {

    /**
     * Initialize the data source
     */
    void initialize();

    /**
     * Close the data source
     */
    void close();

    /**
     * Get a player's authentication data
     * 
     * @param username The player's username
     * @return The player's authentication data, or null if not found
     */
    PlayerAuth getAuth(String username);

    /**
     * Save a player's authentication data
     * 
     * @param auth The player's authentication data
     * @return True if the save was successful, false otherwise
     */
    boolean saveAuth(PlayerAuth auth);

    /**
     * Update a player's authentication data
     * 
     * @param auth The player's authentication data
     * @return True if the update was successful, false otherwise
     */
    boolean updateAuth(PlayerAuth auth);

    /**
     * Remove a player's authentication data
     * 
     * @param username The player's username
     * @return True if the removal was successful, false otherwise
     */
    boolean removeAuth(String username);

    /**
     * Check if a player is registered
     * 
     * @param username The player's username
     * @return True if the player is registered, false otherwise
     */
    boolean isAuthAvailable(String username);

    /**
     * Get all registered players
     * 
     * @return A list of all registered players
     */
    List<PlayerAuth> getAllPlayers();

    /**
     * Get the number of registered accounts for an IP address
     * 
     * @param ip The IP address
     * @return The number of registered accounts
     */
    int countAccountsForIp(String ip);

    /**
     * Get all registered accounts for an IP address
     * 
     * @param ip The IP address
     * @return A list of usernames registered with the IP address
     */
    List<String> getAccountsForIp(String ip);

    /**
     * Purge old data
     * 
     * @param days The number of days of inactivity before purging
     * @return The number of accounts purged
     */
    int purgeOldData(int days);
}

