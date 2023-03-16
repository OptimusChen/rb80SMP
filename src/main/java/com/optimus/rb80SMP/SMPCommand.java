package com.optimus.rb80SMP;

import lombok.NonNull;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SMPCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {
        if (!(sender instanceof Player)) return false;

        if (args[0].equals("setTeam")) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "u fucked up");
                return false;
            }

            int team = Integer.parseInt(args[2]);

            Config.setTeam(target, team);

            target.setGameMode(GameMode.SURVIVAL);
            target.teleport(getSpawnPoint(team));
            target.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*10, 20));

            target.sendMessage(ChatColor.GREEN + "You are now on team: " + team);
            sender.sendMessage(ChatColor.GREEN + "success");
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String[] args) {
        if (args.length == 0) return Arrays.asList("setTeam");

        if (args[0].equals("setTeam")) {
            if (args.length == 2) {
                return Arrays.asList("1", "2", "3", "4");
            }
        }

        return Collections.emptyList();
    }

    public Location getSpawnPoint(int team) {
        World world = Bukkit.getWorld("world");
        switch (team) {
            case 1:
                // pos pos quadrant
                return new Location(world, 5, 100, 5);
            case 2:
                // pos neg quadrant
                return new Location(world, 5, 100, -5);
            case 3:
                // neg neg quadrant
                return new Location(world, -5, 100, -5);
            case 4:
                // neg pos quadrant
                return new Location(world, -5, 100, 5);
        }

        return new Location(world, 0, 100, 0);
    }
}
