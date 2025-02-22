package com.valdoria.valdoriaDice.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.valdoria.valdoriaDice.ValdoriaDice;

public class RollRemoveCommand implements CommandExecutor {
    private ValdoriaDice plugin;

    public RollRemoveCommand(ValdoriaDice plugin) {
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
                    plugin.removeOffset(target, diceType, amount);
                    String message = plugin.formatUIMessage("rollremove", target.getName(), String.valueOf(amount));
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
            sender.sendMessage("Использование: /rollremove [тип кубика] [amount] [player]");
        }
        return true;
    }
}