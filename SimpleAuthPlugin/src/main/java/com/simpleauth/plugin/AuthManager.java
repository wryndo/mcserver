package com.simpleauth.plugin;

import com.simpleauth.plugin.database.AuthDataSource;
import com.simpleauth.plugin.events.PlayerLoginEvent;
import com.simpleauth.plugin.events.PlayerLogoutEvent;
import com.simpleauth.plugin.events.PlayerRegisterEvent;
import com.simpleauth.plugin.events.PlayerUnregisterEvent;
import com.simpleauth.plugin.model.PlayerAuth;
import com.simpleauth.plugin.session.SessionManager;
import com.simpleauth.plugin.util.LocationUtil;
import com.simpleauth.plugin.util.MessageUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AuthManager {

    private final JavaPlugin plugin;
    private final AuthDataSource dataSource;
    private final SessionManager sessionManager;
    private final Set<String> authenticatedPlayers;
    private final Set<String> registeredPlayers;
    private final Set<UUID> premiumUuids;

    public AuthManager(JavaPlugin plugin, AuthDataSource dataSource, SessionManager sessionManager) {
        this.plugin = plugin;
        this.dataSource = dataSource;
        this.sessionManager = sessionManager;
        this.authenticatedPlayers = new HashSet<>();
        this.registeredPlayers = new HashSet<>();
        this.premiumUuids = new HashSet<>();
        
        // Load registered players into memory for quick access
        dataSource.getAllPlayers().forEach(auth -> registeredPlayers.add(auth.getUsername().toLowerCase()));
    }

    /**
     * Check if a player is registered
     * 
     * @param username The player's username
     * @return True if the player is registered, false otherwise
     */
    public boolean isRegistered(String username) {
        return registeredPlayers.contains(username.toLowerCase());
    }

    /**
     * Check if a player is authenticated
     * 
     * @param username The player's username
     * @return True if the player is authenticated, false otherwise
     */
    public boolean isAuthenticated(String username) {
        return authenticatedPlayers.contains(username.toLowerCase());
    }

    /**
     * Check if a player is a premium (paid Minecraft) player
     * 
     * @param uuid The player's UUID
     * @return True if the player is premium, false otherwise
     */
    public boolean isPremium(UUID uuid) {
        return premiumUuids.contains(uuid);
    }

    /**
     * Register a player
     * 
     * @param player The player to register
     * @param password The player's password (stored in plain text)
     * @param email The player's email (optional)
     * @return True if registration was successful, false otherwise
     */
    public boolean register(Player player, String password, String email) {
        String username = player.getName();
        String lowercaseUsername = username.toLowerCase();
        
        // Check if player is already registered
        if (isRegistered(lowercaseUsername)) {
            return false;
        }
        
        // Create player auth data
        PlayerAuth auth = new PlayerAuth(
            username,
            lowercaseUsername,
            password,
            player.getAddress().getAddress().getHostAddress(),
            System.currentTimeMillis(),
            email,
            player.getLocation()
        );
        
        // Save to database
        boolean success = dataSource.saveAuth(auth);
        
        if (success) {
            // Add to registered players set
            registeredPlayers.add(lowercaseUsername);
            
            // Call register event
            PlayerRegisterEvent event = new PlayerRegisterEvent(player);
            plugin.getServer().getPluginManager().callEvent(event);
            
            // Login the player
            login(player);
        }
        
        return success;
    }

    /**
     * Login a player
     * 
     * @param player The player to login
     * @return True if login was successful, false otherwise
     */
    public boolean login(Player player) {
        String username = player.getName();
        String lowercaseUsername = username.toLowerCase();
        
        // Check if player is already authenticated
        if (isAuthenticated(lowercaseUsername)) {
            return false;
        }
        
        // Add to authenticated players set
        authenticatedPlayers.add(lowercaseUsername);
        
        // Update last login time and IP
        PlayerAuth auth = dataSource.getAuth(lowercaseUsername);
        if (auth != null) {
            auth.setLastLogin(System.currentTimeMillis());
            auth.setLastIp(player.getAddress().getAddress().getHostAddress());
            dataSource.updateAuth(auth);
        }
        
        // Create session
        sessionManager.createSession(player);
        
        // Remove blindness effect if applied
        if (plugin.getConfig().getBoolean("restrictions.applyBlindness", true)) {
            player.removePotionEffect(PotionEffectType.BLINDNESS);
        }
        
        // Teleport back to original location if needed
        if (plugin.getConfig().getBoolean("spawn.teleportBackAfterLogin", true)) {
            Location originalLocation = LocationUtil.getStoredLocation(plugin, player.getUniqueId());
            if (originalLocation != null) {
                player.teleport(originalLocation);
                LocationUtil.removeStoredLocation(plugin, player.getUniqueId());
            }
        }
        
        // Call login event
        PlayerLoginEvent event = new PlayerLoginEvent(player);
        plugin.getServer().getPluginManager().callEvent(event);
        
        return true;
    }

    /**
     * Check if a password is correct for a player
     * 
     * @param username The player's username
     * @param password The password to check
     * @return True if the password is correct, false otherwise
     */
    public boolean checkPassword(String username, String password) {
        PlayerAuth auth = dataSource.getAuth(username.toLowerCase());
        
        if (auth == null) {
            return false;
        }
        
        // Direct plain text comparison
        return auth.getPassword().equals(password);
    }

    /**
     * Change a player's password
     * 
     * @param username The player's username
     * @param newPassword The new password
     * @return True if the password was changed, false otherwise
     */
    public boolean changePassword(String username, String newPassword) {
        PlayerAuth auth = dataSource.getAuth(username.toLowerCase());
        
        if (auth == null) {
            return false;
        }
        
        // Update password (plain text)
        auth.setPassword(newPassword);
        
        // Save to database
        return dataSource.updateAuth(auth);
    }

    /**
     * Logout a player
     * 
     * @param player The player to logout
     * @return True if logout was successful, false otherwise
     */
    public boolean logout(Player player) {
        String username = player.getName();
        String lowercaseUsername = username.toLowerCase();
        
        // Check if player is authenticated
        if (!isAuthenticated(lowercaseUsername)) {
            return false;
        }
        
        // Remove from authenticated players set
        authenticatedPlayers.remove(lowercaseUsername);
        
        // Remove session
        sessionManager.removeSession(player);
        
        // Apply restrictions
        applyRestrictions(player);
        
        // Call logout event
        PlayerLogoutEvent event = new PlayerLogoutEvent(player);
        plugin.getServer().getPluginManager().callEvent(event);
        
        return true;
    }

    /**
     * Unregister a player
     * 
     * @param username The player's username
     * @return True if unregistration was successful, false otherwise
     */
    public boolean unregister(String username) {
        String lowercaseUsername = username.toLowerCase();
        
        // Check if player is registered
        if (!isRegistered(lowercaseUsername)) {
            return false;
        }
        
        // Remove from database
        boolean success = dataSource.removeAuth(lowercaseUsername);
        
        if (success) {
            // Remove from registered players set
            registeredPlayers.remove(lowercaseUsername);
            
            // Remove from authenticated players set
            authenticatedPlayers.remove(lowercaseUsername);
            
            // Remove session
            Player player = plugin.getServer().getPlayer(username);
            if (player != null && player.isOnline()) {
                sessionManager.removeSession(player);
                
                // Apply restrictions
                applyRestrictions(player);
                
                // Call unregister event
                PlayerUnregisterEvent event = new PlayerUnregisterEvent(player);
                plugin.getServer().getPluginManager().callEvent(event);
            }
        }
        
        return success;
    }

    /**
     * Apply restrictions to a player (for unauthenticated players)
     * 
     * @param player The player to apply restrictions to
     */
    public void applyRestrictions(Player player) {
        // Apply blindness effect only for login (not for register)
        if (plugin.getConfig().getBoolean("restrictions.applyBlindness", true) && isRegistered(player.getName())) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1, false, false));
        }
        
        // Teleport to spawn if configured
        if (plugin.getConfig().getBoolean("spawn.teleportToSpawn", true)) {
            // Store original location first
            LocationUtil.storeLocation(plugin, player.getUniqueId(), player.getLocation());
            
            // Teleport to spawn
            player.teleport(player.getWorld().getSpawnLocation());
        }
        
        // Send login/register message
        if (isRegistered(player.getName())) {
            MessageUtil.sendMessage(player, "login");
        } else {
            MessageUtil.sendMessage(player, "register");
        }
    }

    /**
     * Mark a player as premium (paid Minecraft account)
     * 
     * @param uuid The player's UUID
     */
    public void markAsPremium(UUID uuid) {
        premiumUuids.add(uuid);
    }

    /**
     * Get the number of registered players
     * 
     * @return The number of registered players
     */
    public int getRegisteredCount() {
        return registeredPlayers.size();
    }

    /**
     * Get the number of authenticated players
     * 
     * @return The number of authenticated players
     */
    public int getAuthenticatedCount() {
        return authenticatedPlayers.size();
    }
    
    /**
     * Get the data source
     * 
     * @return The data source
     */
    public AuthDataSource getDataSource() {
        return dataSource;
    }
    
    /**
     * Get the session manager
     * 
     * @return The session manager
     */
    public SessionManager getSessionManager() {
        return sessionManager;
    }
}
