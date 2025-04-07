package com.valdoria.valdoriaDice;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.UUID;

public class Database {
    private final ValdoriaDice plugin;
    private Connection connection;

    public Database(ValdoriaDice plugin) {
        this.plugin = plugin;
    }

    public void connect() {
        try {
            File dataFolder = plugin.getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }

            Class.forName("org.sqlite.JDBC");
            String dbPath = new File(dataFolder, "dice_bonuses.db").getAbsolutePath();
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            createTable();
            
            plugin.getLogger().info("База данных успешно подключена: " + dbPath);
        } catch (ClassNotFoundException | SQLException e) {
            plugin.getLogger().severe("Не удалось подключиться к базе данных: " + e.getMessage());
        }
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Не удалось отключиться от базы данных: " + e.getMessage());
        }
    }

    private void createTable() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS dice_bonuses (" +
                    "player_uuid TEXT NOT NULL, " +
                    "dice_type TEXT NOT NULL, " +
                    "bonus INTEGER NOT NULL, " +
                    "PRIMARY KEY (player_uuid, dice_type))");
        }
    }

    public void saveBonus(UUID playerUuid, String diceType, int bonus) {
        String sql = "INSERT OR REPLACE INTO dice_bonuses(player_uuid, dice_type, bonus) VALUES(?,?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerUuid.toString());
            stmt.setString(2, diceType);
            stmt.setInt(3, bonus);
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Не удалось сохранить бонус: " + e.getMessage());
        }
    }

    public HashMap<String, Integer> loadBonuses(UUID playerUuid) {
        HashMap<String, Integer> bonuses = new HashMap<>();
        String sql = "SELECT dice_type, bonus FROM dice_bonuses WHERE player_uuid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerUuid.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                bonuses.put(rs.getString("dice_type"), rs.getInt("bonus"));
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Не удалось загрузить бонусы: " + e.getMessage());
        }
        return bonuses;
    }

    public void removeBonus(UUID playerUuid, String diceType) {
        String sql = "DELETE FROM dice_bonuses WHERE player_uuid = ? AND dice_type = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerUuid.toString());
            stmt.setString(2, diceType);
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Не удалось удалить бонус: " + e.getMessage());
        }
    }
}