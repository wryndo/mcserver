package com.simpleauth.plugin.commands;

import com.simpleauth.plugin.AuthManager;
import com.simpleauth.plugin.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Command for logging out
 */
public class LogoutCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final AuthManager authManager;

    /**
     * Constructor for LogoutCommand
     * 
     * @param plugin The plugin instance
     * @param authManager The auth manager
     */
    public LogoutCommand(JavaPlugin plugin, AuthManager authManager) {
        this.plugin = plugin;
        this.authManager = authManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            MessageUtil.sendRawMessage((Player) sender, "&cOnly players can use this command!");
            return true;
        }
        
        // Check if player is authenticated
        if (!authManager.isAuthenticated(player.getName())) {
            MessageUtil.sendMessage(player, "notLoggedIn");
            return true;
        }
        
        // Logout the player
        if (authManager.logout(player)) {
            MessageUtil.sendMessage(player, "loggedOut");
        } else {
            MessageUtil.sendRawMessage(player, "&cFailed to logout! Please try again later.");
        }
        
        return true;
    }
}

