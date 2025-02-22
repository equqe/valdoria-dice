package com.valdoria.valdoriaDice;

import com.valdoria.valdoriaDice.commands.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.util.HashMap;
import java.util.Random;

public final class ValdoriaDice extends JavaPlugin {
    private HashMap<Player, HashMap<String, Integer>> offsets = new HashMap<>();
    private Random random = new Random();
    private YamlConfiguration messages;

    @Override
    public void onEnable() {
        getLogger().info("Hello!");
        loadMessages(); // Загружаем messages.yml
        this.getCommand("rolla").setExecutor(new RollACommand(this));
        this.getCommand("rolld").setExecutor(new RollDCommand(this));
        this.getCommand("rollr").setExecutor(new RollRCommand(this));
        this.getCommand("rollam").setExecutor(new RollAMCommand(this));
        this.getCommand("rolldm").setExecutor(new RollDMCommand(this));
        this.getCommand("rollrm").setExecutor(new RollRMCommand(this));
        this.getCommand("rolladd").setExecutor(new RollAddCommand(this));
        this.getCommand("rollremove").setExecutor(new RollRemoveCommand(this));
    }

    @Override
    public void onDisable() {
        getLogger().info("Bye!");
    }

    private void loadMessages() {
        File messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false); // Создаем messages.yml, если его нет
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile); // Загружаем messages.yml
    }

    public int rollDice(Player player, String diceType) {
        int offset = offsets.getOrDefault(player, new HashMap<>()).getOrDefault(diceType, 0);
        return random.nextInt(12) + 1 + offset;
    }

    public void addOffset(Player player, String diceType, int amount) {
        HashMap<String, Integer> playerOffsets = offsets.getOrDefault(player, new HashMap<>());
        playerOffsets.put(diceType, playerOffsets.getOrDefault(diceType, 0) + amount);
        offsets.put(player, playerOffsets);
    }

    public void removeOffset(Player player, String diceType, int amount) {
        HashMap<String, Integer> playerOffsets = offsets.getOrDefault(player, new HashMap<>());
        int currentOffset = playerOffsets.getOrDefault(diceType, 0);
        playerOffsets.put(diceType, Math.max(currentOffset - amount, 0));
        offsets.put(player, playerOffsets);
    }

    public String formatUIMessage(String key, String... vars) {
        String msg = messages.getString(key, "&cСообщение не найдено: " + key); // Получаем сообщение из messages.yml
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

        return ChatColor.translateAlternateColorCodes('&', msg); // Преобразуем цветовые коды
    }
}