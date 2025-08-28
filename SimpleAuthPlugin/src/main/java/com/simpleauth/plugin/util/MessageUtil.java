package com.simpleauth.plugin.util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Utility class for messages
 */
public class MessageUtil {
    private static JavaPlugin plugin;
    private static String prefix;

    /**
     * Initialize the message utility
     * 
     * @param plugin The plugin instance
     */
    public static void init(JavaPlugin plugin) {
        MessageUtil.plugin = plugin;
        prefix = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.prefix", "&8[&6SimpleAuth&8] &r"));
    }

    /**
     * Send a message to a player
     * 
     * @param player The player
     * @param message The message
     */
    public static void sendMessage(Player player, String message) {
        player.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages." + message, message)));
    }

    /**
     * Send a raw message to a player
     * 
     * @param player The player
     * @param message The message
     */
    public static void sendRawMessage(Player player, String message) {
        player.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', message));
    }

    /**
     * Format a message with color codes
     * 
     * @param message The message
     * @return The formatted message
     */
    public static String formatMessage(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Get a message from the config
     * 
     * @param key The message key
     * @return The message
     */
    public static String getMessage(String key) {
        return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages." + key, key));
    }

    /**
     * Get the message prefix
     * 
     * @return The message prefix
     */
    public static String getPrefix() {
        return prefix;
    }
}

