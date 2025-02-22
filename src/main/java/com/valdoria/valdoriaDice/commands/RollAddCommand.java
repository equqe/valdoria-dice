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
        if (args.length == 3 && sender.hasPermission("dice.admin")) {
            try {
                String diceType = args[0];
                int amount = Integer.parseInt(args[1]);
                Player target = plugin.getServer().getPlayer(args[2]);
                if (target != null) {
                    plugin.addOffset(target, diceType, amount);
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
            sender.sendMessage("Использование: /rolladd [тип кубика] [amount] [player]");
        }
        return true;
    }
}