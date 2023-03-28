package com.optimus.rb80SMP;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ToggleChatCommand implements CommandExecutor {

    public static final List<Player> teamChat = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;

        Player p = (Player) sender;
        boolean team = !teamChat.contains(p);

        if (team) {
            teamChat.add(p);
            p.sendMessage(ChatColor.GREEN + "You are now on the team chat channel.");
        } else {
            teamChat.remove(p);
            p.sendMessage(ChatColor.GREEN + "You are now on the public chat channel.");
        }

        return false;
    }
}
