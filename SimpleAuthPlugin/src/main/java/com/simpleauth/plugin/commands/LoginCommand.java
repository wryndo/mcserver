package com.simpleauth.plugin.commands;

import com.simpleauth.plugin.AuthManager;
import com.simpleauth.plugin.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Command for logging in
 */
public class LoginCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final AuthManager authManager;

    /**
     * Constructor for LoginCommand
     * 
     * @param plugin The plugin instance
     * @param authManager The auth manager
     */
    public LoginCommand(JavaPlugin plugin, AuthManager authManager) {
        this.plugin = plugin;
        this.authManager = authManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            MessageUtil.sendRawMessage((Player) sender, "&cOnly players can use this command!");
            return true;
        }
        
        // Check if player is already authenticated
        if (authManager.isAuthenticated(player.getName())) {
            MessageUtil.sendMessage(player, "alreadyLoggedIn");
            return true;
        }
        
        // Check if player is registered
        if (!authManager.isRegistered(player.getName())) {
            MessageUtil.sendMessage(player, "notRegistered");
            return true;
        }
        
        // Check if password is provided
        if (args.length < 1) {
            MessageUtil.sendRawMessage(player, "&cUsage: /login <password>");
            return true;
        }
        
        String password = args[0];
        
        // Check if password is correct
        if (!authManager.checkPassword(player.getName(), password)) {
            MessageUtil.sendMessage(player, "wrongPasswordMessage");
            
            // Kick player if configured
            if (plugin.getConfig().getBoolean("login.kickOnWrongPassword", true)) {
                String kickMessage = MessageUtil.formatMessage(plugin.getConfig().getString("login.wrongPasswordMessage", "Wrong password!"));
                player.kick(net.kyori.adventure.text.Component.text(kickMessage));
            }
            
            return true;
        }
        
        // Login the player
        if (authManager.login(player)) {
            MessageUtil.sendMessage(player, "loginSuccess");
        }
        
        return true;
    }
}
