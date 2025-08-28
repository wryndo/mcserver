package com.simpleauth.plugin.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Utility class for messages
 */
public class MessageUtil {
    private static JavaPlugin plugin;
    private static String prefix;
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .build();
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    /**
     * Initialize the message utility
     * 
     * @param plugin The plugin instance
     */
    public static void init(JavaPlugin plugin) {
        MessageUtil.plugin = plugin;
        prefix = formatMessage(plugin.getConfig().getString("messages.prefix", "&8[&6SimpleAuth&8] &r"));
    }

    /**
     * Send a message to a player
     * 
     * @param player The player
     * @param message The message
     */
    public static void sendMessage(Player player, String message) {
        String configMessage = plugin.getConfig().getString("messages." + message, message);
        player.sendMessage(Component.text(prefix).append(LEGACY_SERIALIZER.deserialize(configMessage)));
    }

    /**
     * Send a raw message to a player
     * 
     * @param player The player
     * @param message The message
     */
    public static void sendRawMessage(Player player, String message) {
        player.sendMessage(Component.text(prefix).append(LEGACY_SERIALIZER.deserialize(message)));
    }

    /**
     * Format a message with color codes
     * 
     * @param message The message
     * @return The formatted message
     */
    public static String formatMessage(String message) {
        if (message == null) {
            return "";
        }
        // Convert legacy color codes to plain text with colors
        return LEGACY_SERIALIZER.serialize(LEGACY_SERIALIZER.deserialize(message));
    }

    /**
     * Get a message from the config
     * 
     * @param key The message key
     * @return The message
     */
    public static String getMessage(String key) {
        String configMessage = plugin.getConfig().getString("messages." + key, key);
        return formatMessage(configMessage);
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

