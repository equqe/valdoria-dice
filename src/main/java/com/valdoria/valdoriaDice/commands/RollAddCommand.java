package com.valdoria.valdoriaDice.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.valdoria.valdoriaDice.ValdoriaDice;

public class RollAddCommand implements CommandExecutor {
    private ValdoriaDice plugin;

    public RollAddCommand(ValdoriaDice plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 2 && sender.hasPermission("dice.admin")) {
            try {
                int amount = Integer.parseInt(args[0]);
                Player target = plugin.getServer().getPlayer(args[1]);
                if (target != null) {
                    plugin.addOffset(target, amount);
                    String message = plugin.formatUIMessage("rolladd", target.getName(), String.valueOf(amount));
                    if (message != null) {
                        sender.sendMessage(message);
                    }
                } else {
                    sender.sendMessage("Игрок не найден.");
                }
            } catch (NumberFormatException e) {
                sender.sendMessage("Неверное число.");
            }
        } else {
            sender.sendMessage("Использование: /rolladd [amount] [player]");
        }
        return true;
    }
}