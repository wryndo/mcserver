package com.simpleauth.plugin.listeners;

import com.simpleauth.plugin.AuthManager;
import com.simpleauth.plugin.util.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Listener for player authentication events
 */
public class PlayerAuthListener implements Listener {
    private final JavaPlugin plugin;
    private final AuthManager authManager;

    /**
     * Constructor for PlayerAuthListener
     * 
     * @param plugin The plugin instance
     * @param authManager The auth manager
     */
    public PlayerAuthListener(JavaPlugin plugin, AuthManager authManager) {
        this.plugin = plugin;
        this.authManager = authManager;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Check if player is premium and premium auto-login is enabled
        if (plugin.getConfig().getBoolean("premium.enabled", true) && authManager.isPremium(player.getUniqueId())) {
            // Auto-login premium player
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.isOnline() && !authManager.isAuthenticated(player.getName())) {
                        authManager.login(player);
                        MessageUtil.sendMessage(player, "loginSuccess");
                    }
                }
            }.runTaskLater(plugin, 1L);
            return;
        }
        
        // Check if player has a valid session
        if (plugin.getConfig().getBoolean("session.enabled", true) && 
            authManager.isRegistered(player.getName()) && 
            authManager.getSessionManager().hasValidSession(player)) {
            
            // Auto-login player with valid session
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.isOnline() && !authManager.isAuthenticated(player.getName())) {
                        authManager.login(player);
                        MessageUtil.sendMessage(player, "loginSuccess");
                    }
                }
            }.runTaskLater(plugin, 1L);
            return;
        }
        
        // Apply restrictions for unauthenticated player
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline() && !authManager.isAuthenticated(player.getName())) {
                    authManager.applyRestrictions(player);
                }
            }
        }.runTaskLater(plugin, 1L);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // Update quit location if player is authenticated
        if (authManager.isAuthenticated(player.getName())) {
            // Get player auth data
            String username = player.getName().toLowerCase();
            
            // Update quit location in database
            authManager.getDataSource().getAuth(username).setQuitLocation(player.getLocation());
            authManager.getDataSource().updateAuth(authManager.getDataSource().getAuth(username));
        }
    }
}

