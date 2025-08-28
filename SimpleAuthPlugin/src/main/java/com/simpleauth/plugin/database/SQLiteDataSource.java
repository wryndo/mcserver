package com.simpleauth.plugin.database;

import com.simpleauth.plugin.model.PlayerAuth;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * SQLite implementation of the AuthDataSource interface
 */
public class SQLiteDataSource implements AuthDataSource {
    private final JavaPlugin plugin;
    private final File databaseFile;
    private final String tableName;
    private Connection connection;

    /**
     * Constructor for SQLiteDataSource
     * 
     * @param plugin The plugin instance
     * @param databaseFile The database file
     * @param tableName The table name
     */
    public SQLiteDataSource(JavaPlugin plugin, File databaseFile, String tableName) {
        this.plugin = plugin;
        this.databaseFile = databaseFile;
        this.tableName = tableName;
    }

    @Override
    public void initialize() {
        try {
            // Ensure parent directory exists
            if (!databaseFile.getParentFile().exists()) {
                databaseFile.getParentFile().mkdirs();
            }
            
            // Load SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            
            // Create connection
            connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile.getAbsolutePath());
            
            // Create table if it doesn't exist
            createTable();
        } catch (ClassNotFoundException | SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not initialize SQLite connection", e);
        }
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " ("
            + "username TEXT PRIMARY KEY, "
            + "realname TEXT NOT NULL, "
            + "password TEXT NOT NULL, "
            + "ip TEXT NOT NULL, "
            + "lastlogin INTEGER NOT NULL, "
            + "email TEXT, "
            + "x REAL DEFAULT 0, "
            + "y REAL DEFAULT 0, "
            + "z REAL DEFAULT 0, "
            + "world TEXT DEFAULT 'world', "
            + "yaw REAL DEFAULT 0, "
            + "pitch REAL DEFAULT 0, "
            + "quitx REAL DEFAULT 0, "
            + "quity REAL DEFAULT 0, "
            + "quitz REAL DEFAULT 0, "
            + "quitworld TEXT DEFAULT 'world', "
            + "quityaw REAL DEFAULT 0, "
            + "quitpitch REAL DEFAULT 0"
            + ")";

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not create auth table", e);
        }
    }

    @Override
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not close SQLite connection", e);
        }
    }

    @Override
    public PlayerAuth getAuth(String username) {
        String sql = "SELECT * FROM " + tableName + " WHERE username = ? LIMIT 1";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username.toLowerCase());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Location loc = new Location(
                        plugin.getServer().getWorld(rs.getString("world")),
                        rs.getDouble("x"),
                        rs.getDouble("y"),
                        rs.getDouble("z"),
                        rs.getFloat("yaw"),
                        rs.getFloat("pitch")
                    );
                    
                    Location quitLoc = new Location(
                        plugin.getServer().getWorld(rs.getString("quitworld")),
                        rs.getDouble("quitx"),
                        rs.getDouble("quity"),
                        rs.getDouble("quitz"),
                        rs.getFloat("quityaw"),
                        rs.getFloat("quitpitch")
                    );
                    
                    PlayerAuth auth = new PlayerAuth(
                        rs.getString("realname"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("ip"),
                        rs.getLong("lastlogin"),
                        rs.getString("email"),
                        loc
                    );
                    
                    auth.setQuitLocation(quitLoc);
                    
                    return auth;
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not get auth data for " + username, e);
        }
        
        return null;
    }

    @Override
    public boolean saveAuth(PlayerAuth auth) {
        String sql = "INSERT INTO " + tableName + " (username, realname, password, ip, lastlogin, "
            + "email, x, y, z, world, yaw, pitch, quitx, quity, quitz, quitworld, quityaw, quitpitch) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, auth.getUsername().toLowerCase());
            stmt.setString(2, auth.getRealName());
            stmt.setString(3, auth.getPassword());
            stmt.setString(4, auth.getLastIp());
            stmt.setLong(5, auth.getLastLogin());
            stmt.setString(6, auth.getEmail());
            
            Location loc = auth.getLastLocation();
            if (loc != null && loc.getWorld() != null) {
                stmt.setDouble(7, loc.getX());
                stmt.setDouble(8, loc.getY());
                stmt.setDouble(9, loc.getZ());
                stmt.setString(10, loc.getWorld().getName());
                stmt.setFloat(11, loc.getYaw());
                stmt.setFloat(12, loc.getPitch());
            } else {
                stmt.setDouble(7, 0);
                stmt.setDouble(8, 0);
                stmt.setDouble(9, 0);
                stmt.setString(10, "world");
                stmt.setFloat(11, 0);
                stmt.setFloat(12, 0);
            }
            
            Location quitLoc = auth.getQuitLocation();
            if (quitLoc != null && quitLoc.getWorld() != null) {
                stmt.setDouble(13, quitLoc.getX());
                stmt.setDouble(14, quitLoc.getY());
                stmt.setDouble(15, quitLoc.getZ());
                stmt.setString(16, quitLoc.getWorld().getName());
                stmt.setFloat(17, quitLoc.getYaw());
                stmt.setFloat(18, quitLoc.getPitch());
            } else {
                stmt.setDouble(13, 0);
                stmt.setDouble(14, 0);
                stmt.setDouble(15, 0);
                stmt.setString(16, "world");
                stmt.setFloat(17, 0);
                stmt.setFloat(18, 0);
            }
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save auth data for " + auth.getUsername(), e);
            return false;
        }
    }

    @Override
    public boolean updateAuth(PlayerAuth auth) {
        String sql = "UPDATE " + tableName + " SET realname = ?, password = ?, ip = ?, lastlogin = ?, "
            + "email = ?, x = ?, y = ?, z = ?, world = ?, yaw = ?, pitch = ?, "
            + "quitx = ?, quity = ?, quitz = ?, quitworld = ?, quityaw = ?, quitpitch = ? "
            + "WHERE username = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, auth.getRealName());
            stmt.setString(2, auth.getPassword());
            stmt.setString(3, auth.getLastIp());
            stmt.setLong(4, auth.getLastLogin());
            stmt.setString(5, auth.getEmail());
            
            Location loc = auth.getLastLocation();
            if (loc != null && loc.getWorld() != null) {
                stmt.setDouble(6, loc.getX());
                stmt.setDouble(7, loc.getY());
                stmt.setDouble(8, loc.getZ());
                stmt.setString(9, loc.getWorld().getName());
                stmt.setFloat(10, loc.getYaw());
                stmt.setFloat(11, loc.getPitch());
            } else {
                stmt.setDouble(6, 0);
                stmt.setDouble(7, 0);
                stmt.setDouble(8, 0);
                stmt.setString(9, "world");
                stmt.setFloat(10, 0);
                stmt.setFloat(11, 0);
            }
            
            Location quitLoc = auth.getQuitLocation();
            if (quitLoc != null && quitLoc.getWorld() != null) {
                stmt.setDouble(12, quitLoc.getX());
                stmt.setDouble(13, quitLoc.getY());
                stmt.setDouble(14, quitLoc.getZ());
                stmt.setString(15, quitLoc.getWorld().getName());
                stmt.setFloat(16, quitLoc.getYaw());
                stmt.setFloat(17, quitLoc.getPitch());
            } else {
                stmt.setDouble(12, 0);
                stmt.setDouble(13, 0);
                stmt.setDouble(14, 0);
                stmt.setString(15, "world");
                stmt.setFloat(16, 0);
                stmt.setFloat(17, 0);
            }
            
            stmt.setString(18, auth.getUsername().toLowerCase());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not update auth data for " + auth.getUsername(), e);
            return false;
        }
    }

    @Override
    public boolean removeAuth(String username) {
        String sql = "DELETE FROM " + tableName + " WHERE username = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username.toLowerCase());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not remove auth data for " + username, e);
            return false;
        }
    }

    @Override
    public boolean isAuthAvailable(String username) {
        String sql = "SELECT 1 FROM " + tableName + " WHERE username = ? LIMIT 1";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username.toLowerCase());
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not check auth availability for " + username, e);
            return false;
        }
    }

    @Override
    public List<PlayerAuth> getAllPlayers() {
        List<PlayerAuth> players = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName;
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Location loc = new Location(
                    plugin.getServer().getWorld(rs.getString("world")),
                    rs.getDouble("x"),
                    rs.getDouble("y"),
                    rs.getDouble("z"),
                    rs.getFloat("yaw"),
                    rs.getFloat("pitch")
                );
                
                Location quitLoc = new Location(
                    plugin.getServer().getWorld(rs.getString("quitworld")),
                    rs.getDouble("quitx"),
                    rs.getDouble("quity"),
                    rs.getDouble("quitz"),
                    rs.getFloat("quityaw"),
                    rs.getFloat("quitpitch")
                );
                
                PlayerAuth auth = new PlayerAuth(
                    rs.getString("realname"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("ip"),
                    rs.getLong("lastlogin"),
                    rs.getString("email"),
                    loc
                );
                
                auth.setQuitLocation(quitLoc);
                
                players.add(auth);
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not get all players", e);
        }
        
        return players;
    }

    @Override
    public int countAccountsForIp(String ip) {
        String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE ip = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, ip);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not count accounts for IP " + ip, e);
        }
        
        return 0;
    }

    @Override
    public List<String> getAccountsForIp(String ip) {
        List<String> accounts = new ArrayList<>();
        String sql = "SELECT username FROM " + tableName + " WHERE ip = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, ip);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    accounts.add(rs.getString("username"));
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not get accounts for IP " + ip, e);
        }
        
        return accounts;
    }

    @Override
    public int purgeOldData(int days) {
        long maxAge = System.currentTimeMillis() - (days * 24L * 60L * 60L * 1000L);
        String sql = "DELETE FROM " + tableName + " WHERE lastlogin < ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, maxAge);
            
            return stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not purge old data", e);
            return 0;
        }
    }
}

