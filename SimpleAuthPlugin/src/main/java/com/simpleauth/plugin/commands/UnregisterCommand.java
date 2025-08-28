package com.simpleauth.plugin.commands;

import com.simpleauth.plugin.AuthManager;
import com.simpleauth.plugin.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Command for unregistering
 */
public class UnregisterCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final AuthManager authManager;

    /**
     * Constructor for UnregisterCommand
     * 
     * @param plugin The plugin instance
     * @param authManager The auth manager
     */
    public UnregisterCommand(JavaPlugin plugin, AuthManager authManager) {
        this.plugin = plugin;
        this.authManager = authManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if sender is a player or console
        if (sender instanceof Player player) {
            // Player is unregistering themselves
            
            // Check if player is registered
            if (!authManager.isRegistered(player.getName())) {
                MessageUtil.sendMessage(player, "notRegistered");
                return true;
            }
            
            // Check if password is provided
            if (args.length < 1) {
                MessageUtil.sendRawMessage(player, "&cUsage: /unregister <password>");
                return true;
            }
            
            String password = args[0];
            
            // Check if password is correct
            if (!authManager.checkPassword(player.getName(), password)) {
                MessageUtil.sendRawMessage(player, "&cIncorrect password!");
                return true;
            }
            
            // Unregister the player
            if (authManager.unregister(player.getName())) {
                MessageUtil.sendMessage(player, "unregistered");
            } else {
                MessageUtil.sendRawMessage(player, "&cFailed to unregister! Please try again later.");
            }
        } else {
            // Console is unregistering a player
            
            // Check if player name is provided
            if (args.length < 1) {
                sender.sendMessage("Usage: /unregister <player>");
                return true;
            }
            
            String playerName = args[0];
            
            // Check if player is registered
            if (!authManager.isRegistered(playerName)) {
                sender.sendMessage("Player " + playerName + " is not registered!");
                return true;
            }
            
            // Unregister the player
            if (authManager.unregister(playerName)) {
                sender.sendMessage("Player " + playerName + " has been unregistered!");
            } else {
                sender.sendMessage("Failed to unregister player " + playerName + "! Please try again later.");
            }
        }
        
        return true;
    }
}

