package com.valdoria.valdoriaDice;

import java.io.File;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.valdoria.valdoriaDice.commands.RollACommand;
import com.valdoria.valdoriaDice.commands.RollAMCommand;
import com.valdoria.valdoriaDice.commands.RollAddCommand;
import com.valdoria.valdoriaDice.commands.RollDCommand;
import com.valdoria.valdoriaDice.commands.RollDMCommand;
import com.valdoria.valdoriaDice.commands.RollRCommand;
import com.valdoria.valdoriaDice.commands.RollRMCommand;
import com.valdoria.valdoriaDice.commands.RollRemoveCommand;

public final class ValdoriaDice extends JavaPlugin implements Listener {
    private HashMap<UUID, HashMap<String, Integer>> offsets = new HashMap<>();
    private Random random = new Random();
    private YamlConfiguration messages;
    private Database database;

    @Override
    public void onEnable() {
        database = new Database(this);
        database.connect();

        loadMessages();

        this.getCommand("rolla").setExecutor(new RollACommand(this));
        this.getCommand("rolld").setExecutor(new RollDCommand(this));
        this.getCommand("rollr").setExecutor(new RollRCommand(this));
        this.getCommand("rollam").setExecutor(new RollAMCommand(this));
        this.getCommand("rolldm").setExecutor(new RollDMCommand(this));
        this.getCommand("rollrm").setExecutor(new RollRMCommand(this));
        this.getCommand("rolladd").setExecutor(new RollAddCommand(this));
        this.getCommand("rollremove").setExecutor(new RollRemoveCommand(this));

        getServer().getPluginManager().registerEvents(this, this);

        for (Player player : getServer().getOnlinePlayers()) {
            loadPlayerData(player);
        }

        getLogger().info("Плагин успешно включен!");
    }

    @Override
    public void onDisable() {
        for (Player player : getServer().getOnlinePlayers()) {
            savePlayerData(player);
        }

        database.disconnect();

        getLogger().info("Плагин успешно выключен!");
    }

    private void loadMessages() {
        File messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        loadPlayerData(event.getPlayer());
    }

    private void loadPlayerData(Player player) {
        HashMap<String, Integer> bonuses = database.loadBonuses(player.getUniqueId());
        offsets.put(player.getUniqueId(), bonuses);
    }

    private void savePlayerData(Player player) {
        HashMap<String, Integer> playerBonuses = offsets.get(player.getUniqueId());
        if (playerBonuses != null) {
            for (String diceType : playerBonuses.keySet()) {
                database.saveBonus(player.getUniqueId(), diceType, playerBonuses.get(diceType));
            }
        }
    }

    public int rollDice(Player player, String diceType) {
        int offset = offsets.getOrDefault(player.getUniqueId(), new HashMap<>()).getOrDefault(diceType, 0);
        return random.nextInt(12) + 1 + offset;
    }

    public void addOffset(Player player, String diceType, int amount) {
        HashMap<String, Integer> playerOffsets = offsets.getOrDefault(player.getUniqueId(), new HashMap<>());
        int newBonus = playerOffsets.getOrDefault(diceType, 0) + amount;
        playerOffsets.put(diceType, newBonus);
        offsets.put(player.getUniqueId(), playerOffsets);
        
        database.saveBonus(player.getUniqueId(), diceType, newBonus);
    }

    public void removeOffset(Player player, String diceType, int amount) {
        HashMap<String, Integer> playerOffsets = offsets.getOrDefault(player.getUniqueId(), new HashMap<>());
        int currentOffset = playerOffsets.getOrDefault(diceType, 0);
        int newBonus = Math.max(currentOffset - amount, 0);
        
        if (newBonus > 0) {
            playerOffsets.put(diceType, newBonus);
            database.saveBonus(player.getUniqueId(), diceType, newBonus);
        } else {
            playerOffsets.remove(diceType);
            database.removeBonus(player.getUniqueId(), diceType);
        }
        
        offsets.put(player.getUniqueId(), playerOffsets);
    }

    public String formatUIMessage(String key, String... vars) {
        String msg = messages.getString(key, "&cСообщение не найдено: " + key);
        if (msg == null) return null;

        switch (key) {
            case "rolld":
            case "rolla":
            case "rollr":
            case "rolldm":
            case "rollam":
            case "rollrm":
                msg = msg.replace("%dice%", vars[0]).replace("%player%", vars[1]);
                break;
            case "rolladd":
            case "rollremove":
                msg = msg.replace("%player%", vars[0]).replace("%amount%", vars[1]);
                break;
        }

        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}