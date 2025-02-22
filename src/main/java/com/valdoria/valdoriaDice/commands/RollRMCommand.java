package com.valdoria.valdoriaDice.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.valdoria.valdoriaDice.ValdoriaDice;

public class RollRMCommand implements CommandExecutor {
    private ValdoriaDice plugin;

    public RollRMCommand(ValdoriaDice plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            int roll = plugin.rollDice(player, "mresist");
            String message = plugin.formatUIMessage("rollrm", String.valueOf(roll), player.getName());
            if (message != null) {
                player.sendMessage(message);
            }
        }
        return true;
    }
}