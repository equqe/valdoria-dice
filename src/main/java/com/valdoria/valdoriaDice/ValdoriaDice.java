package com.valdoria.valdoriaDice;

import java.io.File;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.valdoria.valdoriaDice.commands.RollACommand;
import com.valdoria.valdoriaDice.commands.RollAddCommand;
import com.valdoria.valdoriaDice.commands.RollDCommand;
import com.valdoria.valdoriaDice.commands.RollRCommand;
import com.valdoria.valdoriaDice.commands.RollRemoveCommand;

public final class ValdoriaDice extends JavaPlugin {
    private HashMap<Player, Integer> offsets = new HashMap<>();
    private Random random = new Random();
    private YamlConfiguration messages;

    @Override
    public void onEnable() {
        getLogger().info("Hello!");
        loadMessages(); // Загружаем messages.yml
        this.getCommand("rolla").setExecutor(new RollACommand(this));
        this.getCommand("rolld").setExecutor(new RollDCommand(this));
        this.getCommand("rollr").setExecutor(new RollRCommand(this));
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
            saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public int rollDice(Player player) {
        int offset = offsets.getOrDefault(player, 0);
        return random.nextInt(12) + 1 + offset;
    }

    public void addOffset(Player player, int amount) {
        offsets.put(player, offsets.getOrDefault(player, 0) + amount);
    }

    public void removeOffset(Player player, int amount) {
        int currentOffset = offsets.getOrDefault(player, 0);
        offsets.put(player, Math.max(currentOffset - amount, 0));
    }

    public String formatUIMessage(String key, String... vars) {
        String msg = messages.getString(key, "&cСообщение не найдено: " + key);
        if (msg == null) return null;

        switch (key) {
            case "rolld":
            case "rolla":
            case "rollr":
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