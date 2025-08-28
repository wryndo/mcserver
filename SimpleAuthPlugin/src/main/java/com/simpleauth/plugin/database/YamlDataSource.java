package com.simpleauth.plugin.database;

import com.simpleauth.plugin.model.PlayerAuth;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * YAML implementation of the AuthDataSource interface
 */
public class YamlDataSource implements AuthDataSource {
    private final JavaPlugin plugin;
    private final File dataFile;
    private YamlConfiguration data;

    /**
     * Constructor for YamlDataSource
     * 
     * @param plugin The plugin instance
     * @param dataFile The data file
     */
    public YamlDataSource(JavaPlugin plugin, File dataFile) {
        this.plugin = plugin;
        this.dataFile = dataFile;
    }

    @Override
    public void initialize() {
        try {
            // Ensure parent directory exists
            if (!dataFile.getParentFile().exists()) {
                dataFile.getParentFile().mkdirs();
            }
            
            // Create file if it doesn't exist
            if (!dataFile.exists()) {
                dataFile.createNewFile();
            }
            
            // Load data
            data = YamlConfiguration.loadConfiguration(dataFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not initialize YAML data source", e);
        }
    }

    @Override
    public void close() {
        // Save data
        try {
            data.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save YAML data", e);
        }
    }

    @Override
    public PlayerAuth getAuth(String username) {
        String lowercaseUsername = username.toLowerCase();
        
        if (!data.contains(lowercaseUsername)) {
            return null;
        }
        
        ConfigurationSection section = data.getConfigurationSection(lowercaseUsername);
        
        if (section == null) {
            return null;
        }
        
        String realName = section.getString("realname");
        String password = section.getString("password");
        String ip = section.getString("ip");
        long lastLogin = section.getLong("lastlogin");
        String email = section.getString("email");
        
        // Get last location
        Location lastLocation = null;
        if (section.contains("location")) {
            ConfigurationSection locationSection = section.getConfigurationSection("location");
            if (locationSection != null) {
                String world = locationSection.getString("world", "world");
                double x = locationSection.getDouble("x", 0);
                double y = locationSection.getDouble("y", 0);
                double z = locationSection.getDouble("z", 0);
                float yaw = (float) locationSection.getDouble("yaw", 0);
                float pitch = (float) locationSection.getDouble("pitch", 0);
                
                lastLocation = new Location(plugin.getServer().getWorld(world), x, y, z, yaw, pitch);
            }
        }
        
        // Get quit location
        Location quitLocation = null;
        if (section.contains("quitlocation")) {
            ConfigurationSection quitLocationSection = section.getConfigurationSection("quitlocation");
            if (quitLocationSection != null) {
                String world = quitLocationSection.getString("world", "world");
                double x = quitLocationSection.getDouble("x", 0);
                double y = quitLocationSection.getDouble("y", 0);
                double z = quitLocationSection.getDouble("z", 0);
                float yaw = (float) quitLocationSection.getDouble("yaw", 0);
                float pitch = (float) quitLocationSection.getDouble("pitch", 0);
                
                quitLocation = new Location(plugin.getServer().getWorld(world), x, y, z, yaw, pitch);
            }
        }
        
        // Create PlayerAuth object
        PlayerAuth auth = new PlayerAuth(
            realName,
            lowercaseUsername,
            password,
            ip,
            lastLogin,
            email,
            lastLocation != null ? lastLocation : new Location(plugin.getServer().getWorld("world"), 0, 0, 0)
        );
        
        if (quitLocation != null) {
            auth.setQuitLocation(quitLocation);
        }
        
        return auth;
    }

    @Override
    public boolean saveAuth(PlayerAuth auth) {
        String lowercaseUsername = auth.getUsername().toLowerCase();
        
        ConfigurationSection section = data.createSection(lowercaseUsername);
        
        section.set("realname", auth.getRealName());
        section.set("password", auth.getPassword());
        section.set("ip", auth.getLastIp());
        section.set("lastlogin", auth.getLastLogin());
        section.set("email", auth.getEmail());
        
        // Save last location
        Location lastLocation = auth.getLastLocation();
        if (lastLocation != null && lastLocation.getWorld() != null) {
            ConfigurationSection locationSection = section.createSection("location");
            locationSection.set("world", lastLocation.getWorld().getName());
            locationSection.set("x", lastLocation.getX());
            locationSection.set("y", lastLocation.getY());
            locationSection.set("z", lastLocation.getZ());
            locationSection.set("yaw", lastLocation.getYaw());
            locationSection.set("pitch", lastLocation.getPitch());
        }
        
        // Save quit location
        Location quitLocation = auth.getQuitLocation();
        if (quitLocation != null && quitLocation.getWorld() != null) {
            ConfigurationSection quitLocationSection = section.createSection("quitlocation");
            quitLocationSection.set("world", quitLocation.getWorld().getName());
            quitLocationSection.set("x", quitLocation.getX());
            quitLocationSection.set("y", quitLocation.getY());
            quitLocationSection.set("z", quitLocation.getZ());
            quitLocationSection.set("yaw", quitLocation.getYaw());
            quitLocationSection.set("pitch", quitLocation.getPitch());
        }
        
        try {
            data.save(dataFile);
            return true;
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save auth data for " + auth.getUsername(), e);
            return false;
        }
    }

    @Override
    public boolean updateAuth(PlayerAuth auth) {
        // For YAML, save and update are the same
        return saveAuth(auth);
    }

    @Override
    public boolean removeAuth(String username) {
        String lowercaseUsername = username.toLowerCase();
        
        if (!data.contains(lowercaseUsername)) {
            return false;
        }
        
        data.set(lowercaseUsername, null);
        
        try {
            data.save(dataFile);
            return true;
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not remove auth data for " + username, e);
            return false;
        }
    }

    @Override
    public boolean isAuthAvailable(String username) {
        return data.contains(username.toLowerCase());
    }

    @Override
    public List<PlayerAuth> getAllPlayers() {
        List<PlayerAuth> players = new ArrayList<>();
        
        for (String key : data.getKeys(false)) {
            PlayerAuth auth = getAuth(key);
            if (auth != null) {
                players.add(auth);
            }
        }
        
        return players;
    }

    @Override
    public int countAccountsForIp(String ip) {
        int count = 0;
        
        for (String key : data.getKeys(false)) {
            ConfigurationSection section = data.getConfigurationSection(key);
            if (section != null && ip.equals(section.getString("ip"))) {
                count++;
            }
        }
        
        return count;
    }

    @Override
    public List<String> getAccountsForIp(String ip) {
        List<String> accounts = new ArrayList<>();
        
        for (String key : data.getKeys(false)) {
            ConfigurationSection section = data.getConfigurationSection(key);
            if (section != null && ip.equals(section.getString("ip"))) {
                accounts.add(key);
            }
        }
        
        return accounts;
    }

    @Override
    public int purgeOldData(int days) {
        long maxAge = System.currentTimeMillis() - (days * 24L * 60L * 60L * 1000L);
        int count = 0;
        
        for (String key : new ArrayList<>(data.getKeys(false))) {
            ConfigurationSection section = data.getConfigurationSection(key);
            if (section != null && section.getLong("lastlogin", 0) < maxAge) {
                data.set(key, null);
                count++;
            }
        }
        
        try {
            data.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save data after purging", e);
        }
        
        return count;
    }
}

