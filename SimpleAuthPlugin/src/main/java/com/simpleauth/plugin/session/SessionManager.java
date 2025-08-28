package com.simpleauth.plugin.session;

import com.simpleauth.plugin.database.AuthDataSource;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages player sessions
 */
public class SessionManager {
    private final JavaPlugin plugin;
    private final AuthDataSource dataSource;
    private final Map<UUID, PlayerSession> sessions;
    
    private boolean enabled;
    private long timeout;
    private boolean validateIp;

    /**
     * Constructor for SessionManager
     * 
     * @param plugin The plugin instance
     * @param dataSource The data source
     */
    public SessionManager(JavaPlugin plugin, AuthDataSource dataSource) {
        this.plugin = plugin;
        this.dataSource = dataSource;
        this.sessions = new HashMap<>();
        
        // Load settings
        reload();
    }

    /**
     * Reload the session manager
     */
    public void reload() {
        enabled = plugin.getConfig().getBoolean("session.enabled", true);
        timeout = plugin.getConfig().getLong("session.timeout", 60) * 60 * 1000; // Convert minutes to milliseconds
        validateIp = plugin.getConfig().getBoolean("session.validateIp", true);
    }

    /**
     * Create a session for a player
     * 
     * @param player The player
     */
    public void createSession(Player player) {
        if (!enabled) {
            return;
        }
        
        UUID uuid = player.getUniqueId();
        String ip = player.getAddress().getAddress().getHostAddress();
        
        PlayerSession session = new PlayerSession(uuid, ip, System.currentTimeMillis());
        sessions.put(uuid, session);
    }

    /**
     * Check if a player has a valid session
     * 
     * @param player The player
     * @return True if the player has a valid session, false otherwise
     */
    public boolean hasValidSession(Player player) {
        if (!enabled) {
            return false;
        }
        
        UUID uuid = player.getUniqueId();
        String ip = player.getAddress().getAddress().getHostAddress();
        
        PlayerSession session = sessions.get(uuid);
        
        if (session == null) {
            return false;
        }
        
        // Check if session has expired
        if (System.currentTimeMillis() - session.getCreationTime() > timeout) {
            sessions.remove(uuid);
            return false;
        }
        
        // Check if IP matches
        if (validateIp && !session.getIp().equals(ip)) {
            sessions.remove(uuid);
            return false;
        }
        
        return true;
    }

    /**
     * Remove a player's session
     * 
     * @param player The player
     */
    public void removeSession(Player player) {
        sessions.remove(player.getUniqueId());
    }

    /**
     * Clear all sessions
     */
    public void clearSessions() {
        sessions.clear();
    }

    /**
     * Get the number of active sessions
     * 
     * @return The number of active sessions
     */
    public int getSessionCount() {
        return sessions.size();
    }
}

