package com.simpleauth.plugin.commands;

import com.simpleauth.plugin.AuthManager;
import com.simpleauth.plugin.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Command for registering
 */
public class RegisterCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final AuthManager authManager;

    /**
     * Constructor for RegisterCommand
     * 
     * @param plugin The plugin instance
     * @param authManager The auth manager
     */
    public RegisterCommand(JavaPlugin plugin, AuthManager authManager) {
        this.plugin = plugin;
        this.authManager = authManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            MessageUtil.sendRawMessage((Player) sender, "&cOnly players can use this command!");
            return true;
        }
        
        // Check if player is already registered
        if (authManager.isRegistered(player.getName())) {
            MessageUtil.sendMessage(player, "alreadyRegistered");
            return true;
        }
        
        // Check if registration is enabled
        if (!plugin.getConfig().getBoolean("registration.enabled", true)) {
            MessageUtil.sendRawMessage(player, "&cRegistration is disabled!");
            return true;
        }
        
        // Check if password is provided
        if (args.length < 1) {
            MessageUtil.sendRawMessage(player, "&cUsage: /register <password> [email]");
            return true;
        }
        
        String password = args[0];
        String email = args.length > 1 ? args[1] : null;
        
        // Check if email is required
        if (plugin.getConfig().getBoolean("registration.requireEmail", false) && email == null) {
            MessageUtil.sendMessage(player, "emailRequired");
            return true;
        }
        
        // Check password length
        int minLength = plugin.getConfig().getInt("registration.minPasswordLength", 4);
        int maxLength = plugin.getConfig().getInt("registration.maxPasswordLength", 30);
        
        if (password.length() < minLength) {
            MessageUtil.sendMessage(player, "passwordTooShort");
            return true;
        }
        
        if (password.length() > maxLength) {
            MessageUtil.sendMessage(player, "passwordTooLong");
            return true;
        }
        
        // Check max accounts per IP
        int maxAccountsPerIp = plugin.getConfig().getInt("registration.maxAccountsPerIp", 3);
        if (maxAccountsPerIp > 0) {
            String ip = player.getAddress().getAddress().getHostAddress();
            int accountsForIp = authManager.getDataSource().countAccountsForIp(ip);
            
            if (accountsForIp >= maxAccountsPerIp) {
                MessageUtil.sendMessage(player, "tooManyAccounts");
                return true;
            }
        }
        
        // Register the player
        if (authManager.register(player, password, email)) {
            MessageUtil.sendMessage(player, "registerSuccess");
        } else {
            MessageUtil.sendRawMessage(player, "&cFailed to register! Please try again later.");
        }
        
        return true;
    }
}
