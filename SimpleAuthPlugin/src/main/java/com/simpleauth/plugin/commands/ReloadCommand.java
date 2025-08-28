package com.simpleauth.plugin.commands;

import com.simpleauth.plugin.SimpleAuthPlugin;
import com.simpleauth.plugin.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Command for reloading the plugin
 */
public class ReloadCommand implements CommandExecutor {
    private final SimpleAuthPlugin plugin;

    /**
     * Constructor for ReloadCommand
     * 
     * @param plugin The plugin instance
     */
    public ReloadCommand(SimpleAuthPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if sender has permission
        if (!sender.hasPermission("simpleauth.admin.reload")) {
            if (sender instanceof Player player) {
                MessageUtil.sendRawMessage(player, "&cYou don't have permission to use this command!");
            } else {
                sender.sendMessage("You don't have permission to use this command!");
            }
            return true;
        }
        
        // Reload the plugin
        plugin.reload();
        
        // Send success message
        if (sender instanceof Player player) {
            MessageUtil.sendMessage(player, "reloaded");
        } else {
            sender.sendMessage("SimpleAuthPlugin has been reloaded!");
        }
        
        return true;
    }
}

