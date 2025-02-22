package com.valdoria.valdoriaDice.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.valdoria.valdoriaDice.ValdoriaDice;

public class RollACommand implements CommandExecutor {
    private ValdoriaDice plugin;

    public RollACommand(ValdoriaDice plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            int roll = plugin.rollDice(player);
            String message = plugin.formatUIMessage("rolla", String.valueOf(roll), player.getName());
            if (message != null) {
                player.sendMessage(message);
            }
        }
        return true;
    }
}