package com.simpleauth.plugin.config;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Manages the plugin configuration
 */
public class ConfigManager {
    private final JavaPlugin plugin;
    
    private boolean registrationEnabled;
    private boolean registrationForced;
    private int maxAccountsPerIp;
    private int minPasswordLength;
    private int maxPasswordLength;
    private boolean requireEmail;
    private boolean verifyEmail;
    
    private int maxLoginAttempts;
    private int loginTimeout;
    private boolean kickOnWrongPassword;
    private String wrongPasswordMessage;
    
    private boolean sessionEnabled;
    private long sessionTimeout;
    private boolean sessionValidateIp;
    
    private boolean teleportToSpawn;
    private boolean teleportBackAfterLogin;
    private boolean useFirstSpawn;
    
    private boolean allowChat;
    private boolean hideChat;
    private boolean allowMovement;
    private double allowedMovementRadius;
    private boolean protectInventory;
    private boolean forceSurvivalMode;
    private boolean applyBlindness;
    
    private boolean premiumEnabled;
    private boolean premiumForceUsername;

    /**
     * Constructor for ConfigManager
     * 
     * @param plugin The plugin instance
     */
    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Load the configuration
     */
    public void loadConfig() {
        // Registration settings
        registrationEnabled = plugin.getConfig().getBoolean("registration.enabled", true);
        registrationForced = plugin.getConfig().getBoolean("registration.forced", true);
        maxAccountsPerIp = plugin.getConfig().getInt("registration.maxAccountsPerIp", 3);
        minPasswordLength = plugin.getConfig().getInt("registration.minPasswordLength", 4);
        maxPasswordLength = plugin.getConfig().getInt("registration.maxPasswordLength", 30);
        requireEmail = plugin.getConfig().getBoolean("registration.requireEmail", false);
        verifyEmail = plugin.getConfig().getBoolean("registration.verifyEmail", false);
        
        // Login settings
        maxLoginAttempts = plugin.getConfig().getInt("login.maxLoginAttempts", 5);
        loginTimeout = plugin.getConfig().getInt("login.loginTimeout", 300);
        kickOnWrongPassword = plugin.getConfig().getBoolean("login.kickOnWrongPassword", true);
        wrongPasswordMessage = plugin.getConfig().getString("login.wrongPasswordMessage", "Wrong password!");
        
        // Session settings
        sessionEnabled = plugin.getConfig().getBoolean("session.enabled", true);
        sessionTimeout = plugin.getConfig().getLong("session.timeout", 60);
        sessionValidateIp = plugin.getConfig().getBoolean("session.validateIp", true);
        
        // Spawn settings
        teleportToSpawn = plugin.getConfig().getBoolean("spawn.teleportToSpawn", true);
        teleportBackAfterLogin = plugin.getConfig().getBoolean("spawn.teleportBackAfterLogin", true);
        useFirstSpawn = plugin.getConfig().getBoolean("spawn.useFirstSpawn", false);
        
        // Restriction settings
        allowChat = plugin.getConfig().getBoolean("restrictions.allowChat", false);
        hideChat = plugin.getConfig().getBoolean("restrictions.hideChat", true);
        allowMovement = plugin.getConfig().getBoolean("restrictions.allowMovement", false);
        allowedMovementRadius = plugin.getConfig().getDouble("restrictions.allowedMovementRadius", 100);
        protectInventory = plugin.getConfig().getBoolean("restrictions.protectInventory", true);
        forceSurvivalMode = plugin.getConfig().getBoolean("restrictions.forceSurvivalMode", true);
        applyBlindness = plugin.getConfig().getBoolean("restrictions.applyBlindness", true);
        
        // Premium settings
        premiumEnabled = plugin.getConfig().getBoolean("premium.enabled", true);
        premiumForceUsername = plugin.getConfig().getBoolean("premium.forceUsername", true);
    }

    /**
     * Check if registration is enabled
     * 
     * @return True if registration is enabled, false otherwise
     */
    public boolean isRegistrationEnabled() {
        return registrationEnabled;
    }

    /**
     * Check if registration is forced
     * 
     * @return True if registration is forced, false otherwise
     */
    public boolean isRegistrationForced() {
        return registrationForced;
    }

    /**
     * Get the maximum number of accounts per IP
     * 
     * @return The maximum number of accounts per IP
     */
    public int getMaxAccountsPerIp() {
        return maxAccountsPerIp;
    }

    /**
     * Get the minimum password length
     * 
     * @return The minimum password length
     */
    public int getMinPasswordLength() {
        return minPasswordLength;
    }

    /**
     * Get the maximum password length
     * 
     * @return The maximum password length
     */
    public int getMaxPasswordLength() {
        return maxPasswordLength;
    }

    /**
     * Check if email is required for registration
     * 
     * @return True if email is required, false otherwise
     */
    public boolean isRequireEmail() {
        return requireEmail;
    }

    /**
     * Check if email verification is enabled
     * 
     * @return True if email verification is enabled, false otherwise
     */
    public boolean isVerifyEmail() {
        return verifyEmail;
    }

    /**
     * Get the maximum number of login attempts
     * 
     * @return The maximum number of login attempts
     */
    public int getMaxLoginAttempts() {
        return maxLoginAttempts;
    }

    /**
     * Get the login timeout in seconds
     * 
     * @return The login timeout in seconds
     */
    public int getLoginTimeout() {
        return loginTimeout;
    }

    /**
     * Check if players should be kicked on wrong password
     * 
     * @return True if players should be kicked on wrong password, false otherwise
     */
    public boolean isKickOnWrongPassword() {
        return kickOnWrongPassword;
    }

    /**
     * Get the wrong password message
     * 
     * @return The wrong password message
     */
    public String getWrongPasswordMessage() {
        return wrongPasswordMessage;
    }

    /**
     * Check if sessions are enabled
     * 
     * @return True if sessions are enabled, false otherwise
     */
    public boolean isSessionEnabled() {
        return sessionEnabled;
    }

    /**
     * Get the session timeout in minutes
     * 
     * @return The session timeout in minutes
     */
    public long getSessionTimeout() {
        return sessionTimeout;
    }

    /**
     * Check if IP validation is enabled for sessions
     * 
     * @return True if IP validation is enabled, false otherwise
     */
    public boolean isSessionValidateIp() {
        return sessionValidateIp;
    }

    /**
     * Check if teleportation to spawn is enabled
     * 
     * @return True if teleportation to spawn is enabled, false otherwise
     */
    public boolean isTeleportToSpawn() {
        return teleportToSpawn;
    }

    /**
     * Check if teleportation back after login is enabled
     * 
     * @return True if teleportation back after login is enabled, false otherwise
     */
    public boolean isTeleportBackAfterLogin() {
        return teleportBackAfterLogin;
    }

    /**
     * Check if first spawn is enabled
     * 
     * @return True if first spawn is enabled, false otherwise
     */
    public boolean isUseFirstSpawn() {
        return useFirstSpawn;
    }

    /**
     * Check if chat is allowed for unauthenticated players
     * 
     * @return True if chat is allowed, false otherwise
     */
    public boolean isAllowChat() {
        return allowChat;
    }

    /**
     * Check if chat should be hidden for unauthenticated players
     * 
     * @return True if chat should be hidden, false otherwise
     */
    public boolean isHideChat() {
        return hideChat;
    }

    /**
     * Check if movement is allowed for unauthenticated players
     * 
     * @return True if movement is allowed, false otherwise
     */
    public boolean isAllowMovement() {
        return allowMovement;
    }

    /**
     * Get the allowed movement radius
     * 
     * @return The allowed movement radius
     */
    public double getAllowedMovementRadius() {
        return allowedMovementRadius;
    }

    /**
     * Check if inventory protection is enabled
     * 
     * @return True if inventory protection is enabled, false otherwise
     */
    public boolean isProtectInventory() {
        return protectInventory;
    }

    /**
     * Check if survival mode should be forced
     * 
     * @return True if survival mode should be forced, false otherwise
     */
    public boolean isForceSurvivalMode() {
        return forceSurvivalMode;
    }

    /**
     * Check if blindness effect should be applied
     * 
     * @return True if blindness effect should be applied, false otherwise
     */
    public boolean isApplyBlindness() {
        return applyBlindness;
    }

    /**
     * Check if premium (paid Minecraft) account support is enabled
     * 
     * @return True if premium account support is enabled, false otherwise
     */
    public boolean isPremiumEnabled() {
        return premiumEnabled;
    }

    /**
     * Check if premium users should be forced to use their Minecraft username
     * 
     * @return True if premium users should be forced to use their Minecraft username, false otherwise
     */
    public boolean isPremiumForceUsername() {
        return premiumForceUsername;
    }
}

