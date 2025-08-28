package com.simpleauth.plugin.commands;

import com.simpleauth.plugin.AuthManager;
import com.simpleauth.plugin.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Command for checking the authentication status
 */
public class StatusCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final AuthManager authManager;

    /**
     * Constructor for StatusCommand
     * 
     * @param plugin The plugin instance
     * @param authManager The auth manager
     */
    public StatusCommand(JavaPlugin plugin, AuthManager authManager) {
        this.plugin = plugin;
        this.authManager = authManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if sender has permission
        if (!sender.hasPermission("simpleauth.admin.status")) {
            if (sender instanceof Player player) {
                MessageUtil.sendRawMessage(player, "&cYou don't have permission to use this command!");
            } else {
                sender.sendMessage("You don't have permission to use this command!");
            }
            return true;
        }
        
        // Get status information
        int registeredCount = authManager.getRegisteredCount();
        int authenticatedCount = authManager.getAuthenticatedCount();
        int onlineCount = plugin.getServer().getOnlinePlayers().size();
        
        // Send status information
        if (sender instanceof Player player) {
            MessageUtil.sendRawMessage(player, "&6=== SimpleAuth Status ===");
            MessageUtil.sendRawMessage(player, "&7Registered players: &f" + registeredCount);
            MessageUtil.sendRawMessage(player, "&7Authenticated players: &f" + authenticatedCount + "&7/&f" + onlineCount);
            MessageUtil.sendRawMessage(player, "&7Database type: &f" + plugin.getConfig().getString("database.type", "SQLITE"));
            MessageUtil.sendRawMessage(player, "&7Session timeout: &f" + plugin.getConfig().getLong("session.timeout", 60) + " minutes");
        } else {
            sender.sendMessage("=== SimpleAuth Status ===");
            sender.sendMessage("Registered players: " + registeredCount);
            sender.sendMessage("Authenticated players: " + authenticatedCount + "/" + onlineCount);
            sender.sendMessage("Database type: " + plugin.getConfig().getString("database.type", "SQLITE"));
            sender.sendMessage("Session timeout: " + plugin.getConfig().getLong("session.timeout", 60) + " minutes");
        }
        
        return true;
    }
}

