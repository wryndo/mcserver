package com.simpleauth.plugin.util;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Utility class for locations
 */
public class LocationUtil {
    private static final String LOCATIONS_FILE = "locations.yml";

    /**
     * Store a player's location
     * 
     * @param plugin The plugin instance
     * @param uuid The player's UUID
     * @param location The location to store
     */
    public static void storeLocation(JavaPlugin plugin, UUID uuid, Location location) {
        File file = new File(plugin.getDataFolder(), LOCATIONS_FILE);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        String path = uuid.toString();
        config.set(path + ".world", location.getWorld().getName());
        config.set(path + ".x", location.getX());
        config.set(path + ".y", location.getY());
        config.set(path + ".z", location.getZ());
        config.set(path + ".yaw", location.getYaw());
        config.set(path + ".pitch", location.getPitch());
        
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save location for " + uuid, e);
        }
    }

    /**
     * Get a player's stored location
     * 
     * @param plugin The plugin instance
     * @param uuid The player's UUID
     * @return The stored location, or null if not found
     */
    public static Location getStoredLocation(JavaPlugin plugin, UUID uuid) {
        File file = new File(plugin.getDataFolder(), LOCATIONS_FILE);
        if (!file.exists()) {
            return null;
        }
        
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        String path = uuid.toString();
        if (!config.contains(path)) {
            return null;
        }
        
        String worldName = config.getString(path + ".world");
        double x = config.getDouble(path + ".x");
        double y = config.getDouble(path + ".y");
        double z = config.getDouble(path + ".z");
        float yaw = (float) config.getDouble(path + ".yaw");
        float pitch = (float) config.getDouble(path + ".pitch");
        
        return new Location(plugin.getServer().getWorld(worldName), x, y, z, yaw, pitch);
    }

    /**
     * Remove a player's stored location
     * 
     * @param plugin The plugin instance
     * @param uuid The player's UUID
     */
    public static void removeStoredLocation(JavaPlugin plugin, UUID uuid) {
        File file = new File(plugin.getDataFolder(), LOCATIONS_FILE);
        if (!file.exists()) {
            return;
        }
        
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        String path = uuid.toString();
        config.set(path, null);
        
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not remove location for " + uuid, e);
        }
    }
}

