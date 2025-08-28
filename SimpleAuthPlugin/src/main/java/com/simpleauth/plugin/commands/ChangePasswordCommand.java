package com.simpleauth.plugin.commands;

import com.simpleauth.plugin.AuthManager;
import com.simpleauth.plugin.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Command for changing password
 */
public class ChangePasswordCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final AuthManager authManager;

    /**
     * Constructor for ChangePasswordCommand
     * 
     * @param plugin The plugin instance
     * @param authManager The auth manager
     */
    public ChangePasswordCommand(JavaPlugin plugin, AuthManager authManager) {
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
        
        // Check if player is registered
        if (!authManager.isRegistered(player.getName())) {
            MessageUtil.sendMessage(player, "notRegistered");
            return true;
        }
        
        // Check if old and new passwords are provided
        if (args.length < 2) {
            MessageUtil.sendRawMessage(player, "&cUsage: /changepassword <oldPassword> <newPassword>");
            return true;
        }
        
        String oldPassword = args[0];
        String newPassword = args[1];
        
        // Check if old password is correct
        if (!authManager.checkPassword(player.getName(), oldPassword)) {
            MessageUtil.sendRawMessage(player, "&cOld password is incorrect!");
            return true;
        }
        
        // Check new password length
        int minLength = plugin.getConfig().getInt("registration.minPasswordLength", 4);
        int maxLength = plugin.getConfig().getInt("registration.maxPasswordLength", 30);
        
        if (newPassword.length() < minLength) {
            MessageUtil.sendRawMessage(player, "&cNew password is too short! Minimum length is " + minLength + " characters.");
            return true;
        }
        
        if (newPassword.length() > maxLength) {
            MessageUtil.sendRawMessage(player, "&cNew password is too long! Maximum length is " + maxLength + " characters.");
            return true;
        }
        
        // Change the password
        if (authManager.changePassword(player.getName(), newPassword)) {
            MessageUtil.sendMessage(player, "passwordChanged");
        } else {
            MessageUtil.sendRawMessage(player, "&cFailed to change password! Please try again later.");
        }
        
        return true;
    }
}

