package com.simpleauth.plugin;

import com.simpleauth.plugin.commands.*;
import com.simpleauth.plugin.config.ConfigManager;
import com.simpleauth.plugin.database.AuthDataSource;
import com.simpleauth.plugin.database.DataSourceType;
import com.simpleauth.plugin.database.MySQLDataSource;
import com.simpleauth.plugin.database.SQLiteDataSource;
import com.simpleauth.plugin.database.YamlDataSource;
import com.simpleauth.plugin.listeners.PlayerAuthListener;
import com.simpleauth.plugin.listeners.PlayerProtectionListener;
import com.simpleauth.plugin.session.SessionManager;
import com.simpleauth.plugin.util.MessageUtil;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;
import java.util.logging.Level;

public class SimpleAuthPlugin extends JavaPlugin {

    private ConfigManager configManager;
    private AuthDataSource dataSource;
    private SessionManager sessionManager;
    private AuthManager authManager;

    @Override
    public void onEnable() {
        // Create data folder if it doesn't exist
        if (!getDataFolder().exists() && !getDataFolder().mkdirs()) {
            getLogger().severe("Could not create data folder!");
        }

        // Initialize config
        saveDefaultConfig();
        configManager = new ConfigManager(this);
        configManager.loadConfig();

        // Initialize message utility
        MessageUtil.init(this);

        // Initialize data source
        initializeDataSource();

        // Initialize session manager
        sessionManager = new SessionManager(this, dataSource);

        // Initialize auth manager
        authManager = new AuthManager(this, dataSource, sessionManager);

        // Register commands
        registerCommands();

        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerAuthListener(this, authManager), this);
        getServer().getPluginManager().registerEvents(new PlayerProtectionListener(this, authManager), this);

        getLogger().info("SimpleAuthPlugin has been enabled!");
    }

    @Override
    public void onDisable() {
        // Close data source connection
        if (dataSource != null) {
            dataSource.close();
        }

        getLogger().info("SimpleAuthPlugin has been disabled!");
    }

    private void initializeDataSource() {
        String dbType = getConfig().getString("database.type", "SQLITE");
        
        try {
            DataSourceType type = DataSourceType.valueOf(dbType.toUpperCase());
            
            switch (type) {
                case MYSQL -> {
                    String host = getConfig().getString("database.mysql.host", "localhost");
                    int port = getConfig().getInt("database.mysql.port", 3306);
                    String database = getConfig().getString("database.mysql.database", "simpleauth");
                    String username = getConfig().getString("database.mysql.username", "root");
                    String password = getConfig().getString("database.mysql.password", "");
                    String tableName = getConfig().getString("database.tableName", "simpleauth");
                    
                    dataSource = new MySQLDataSource(this, host, port, database, username, password, tableName);
                }
                case SQLITE -> {
                    String filename = getConfig().getString("database.sqlite.filename", "simpleauth.db");
                    String tableName = getConfig().getString("database.tableName", "simpleauth");
                    File databaseFile = new File(getDataFolder(), filename);
                    
                    dataSource = new SQLiteDataSource(this, databaseFile, tableName);
                }
                case YAML -> {
                    File dataFile = new File(getDataFolder(), "userdata.yml");
                    dataSource = new YamlDataSource(this, dataFile);
                }
                default -> {
                    getLogger().warning("Unknown database type: " + dbType + ". Defaulting to SQLite.");
                    File databaseFile = new File(getDataFolder(), "simpleauth.db");
                    String tableName = getConfig().getString("database.tableName", "simpleauth");
                    
                    dataSource = new SQLiteDataSource(this, databaseFile, tableName);
                }
            }
            
            // Initialize the data source
            dataSource.initialize();
            
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to initialize data source", e);
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void registerCommands() {
        // Register login command
        PluginCommand loginCommand = getCommand("login");
        if (loginCommand != null) {
            loginCommand.setExecutor(new LoginCommand(this, authManager));
        }

        // Register register command
        PluginCommand registerCommand = getCommand("register");
        if (registerCommand != null) {
            registerCommand.setExecutor(new RegisterCommand(this, authManager));
        }

        // Register changepassword command
        PluginCommand changePasswordCommand = getCommand("changepassword");
        if (changePasswordCommand != null) {
            changePasswordCommand.setExecutor(new ChangePasswordCommand(this, authManager));
        }

        // Register logout command
        PluginCommand logoutCommand = getCommand("logout");
        if (logoutCommand != null) {
            logoutCommand.setExecutor(new LogoutCommand(this, authManager));
        }

        // Register unregister command
        PluginCommand unregisterCommand = getCommand("unregister");
        if (unregisterCommand != null) {
            unregisterCommand.setExecutor(new UnregisterCommand(this, authManager));
        }

        // Register authreload command
        PluginCommand reloadCommand = getCommand("authreload");
        if (reloadCommand != null) {
            reloadCommand.setExecutor(new ReloadCommand(this));
        }

        // Register authstatus command
        PluginCommand statusCommand = getCommand("authstatus");
        if (statusCommand != null) {
            statusCommand.setExecutor(new StatusCommand(this, authManager));
        }
    }

    public void reload() {
        // Reload config
        reloadConfig();
        configManager.loadConfig();
        
        // Reload message utility
        MessageUtil.init(this);
        
        // Reload session manager
        sessionManager.reload();
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public AuthDataSource getDataSource() {
        return dataSource;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public AuthManager getAuthManager() {
        return authManager;
    }
}

