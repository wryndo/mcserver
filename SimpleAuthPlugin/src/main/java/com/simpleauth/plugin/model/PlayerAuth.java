package com.simpleauth.plugin.model;

import org.bukkit.Location;

/**
 * Represents a player's authentication data
 */
public class PlayerAuth {
    private final String username;
    private final String realName;
    private String password;
    private String lastIp;
    private long lastLogin;
    private String email;
    private Location lastLocation;
    private Location quitLocation;

    /**
     * Constructor for PlayerAuth
     * 
     * @param realName The player's real name (case-sensitive)
     * @param username The player's username (lowercase)
     * @param password The player's password (plain text)
     * @param ip The player's IP address
     * @param lastLogin The player's last login time
     * @param email The player's email (optional)
     * @param lastLocation The player's last location
     */
    public PlayerAuth(String realName, String username, String password, String ip, long lastLogin, String email, Location lastLocation) {
        this.realName = realName;
        this.username = username;
        this.password = password;
        this.lastIp = ip;
        this.lastLogin = lastLogin;
        this.email = email;
        this.lastLocation = lastLocation;
        this.quitLocation = lastLocation;
    }

    /**
     * Get the player's username (lowercase)
     * 
     * @return The player's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Get the player's real name (case-sensitive)
     * 
     * @return The player's real name
     */
    public String getRealName() {
        return realName;
    }

    /**
     * Get the player's password (plain text)
     * 
     * @return The player's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the player's password (plain text)
     * 
     * @param password The new password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Get the player's last IP address
     * 
     * @return The player's last IP address
     */
    public String getLastIp() {
        return lastIp;
    }

    /**
     * Set the player's last IP address
     * 
     * @param lastIp The new last IP address
     */
    public void setLastIp(String lastIp) {
        this.lastIp = lastIp;
    }

    /**
     * Get the player's last login time
     * 
     * @return The player's last login time
     */
    public long getLastLogin() {
        return lastLogin;
    }

    /**
     * Set the player's last login time
     * 
     * @param lastLogin The new last login time
     */
    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }

    /**
     * Get the player's email
     * 
     * @return The player's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the player's email
     * 
     * @param email The new email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get the player's last location
     * 
     * @return The player's last location
     */
    public Location getLastLocation() {
        return lastLocation;
    }

    /**
     * Set the player's last location
     * 
     * @param lastLocation The new last location
     */
    public void setLastLocation(Location lastLocation) {
        this.lastLocation = lastLocation;
    }

    /**
     * Get the player's quit location
     * 
     * @return The player's quit location
     */
    public Location getQuitLocation() {
        return quitLocation;
    }

    /**
     * Set the player's quit location
     * 
     * @param quitLocation The new quit location
     */
    public void setQuitLocation(Location quitLocation) {
        this.quitLocation = quitLocation;
    }
}

