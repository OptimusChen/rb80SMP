package com.optimus.rb80SMP;

import lombok.NonNull;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class SMPCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {
        if (!(sender instanceof Player)) return false;
        if (!sender.isOp()) {
            TextComponent message = new TextComponent("Click this for free op ");
            message.setColor(net.md_5.bungee.api.ChatColor.WHITE);

            ComponentBuilder cb = new ComponentBuilder("Click!").color(net.md_5.bungee.api.ChatColor.YELLOW);

            TextComponent click = new TextComponent("Free OP!");
            click.setColor(net.md_5.bungee.api.ChatColor.DARK_GREEN);
            click.setBold(true);
            click.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, cb.create()));
            click.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.youtube.com/watch?v=dQw4w9WgXcQ"));

            message.addExtra(click);

            sender.spigot().sendMessage(message);
            return false;
        }

        if (args[0].equals("team")) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "u fucked up");
                return false;
            }

            int team = Integer.parseInt(args[2]);

            Config.setTeam(target, team);

            target.setGameMode(GameMode.SURVIVAL);
            target.teleport(SMP.getPlugin().getSpawnPoint(team));
            target.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*10, 20));

            target.sendMessage(ChatColor.GREEN + "You are now on team: " + team);
            sender.sendMessage(ChatColor.GREEN + "success");
        }

        if (args[0].equals("givekits")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.getInventory().addItem(new ItemStack(Material.OAK_SAPLING, 3),
                        new ItemStack(Material.OAK_LOG, 10),
                        new ItemStack(Material.POTATO, 1),
                        new ItemStack(Material.WHEAT_SEEDS, 1),
                        new ItemStack(Material.CARROT, 1),
                        new ItemStack(Material.PUMPKIN_SEEDS, 1),
                        new ItemStack(Material.MELON_SEEDS, 1),
                        new ItemStack(Material.BONE_MEAL, 16),
                        new ItemStack(Material.BREAD, 32));
            }
        }

        if (args[0].equals("initborder")) {
            Bukkit.broadcastMessage(ChatColor.GREEN + "Initializing worldborder.");

            World world = ((Player) sender).getWorld();
            double border = world.getWorldBorder().getSize() / 2;
            for (int x = (int) (-1 * border); x <= border; x++) {
                for (int y = -64; y <= 320; y++) {
                    world.getBlockAt(x, y, 0).setType(Material.BARRIER);
                }
            }

            for (int z = (int) (-1 * border); z <= border; z++) {
                for (int y = -64; y <= 320; y++) {
                    world.getBlockAt(0, y, z).setType(Material.BARRIER);
                }
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    world.getWorldBorder().setSize(world.getWorldBorder().getSize() + 2);

                    double border = world.getWorldBorder().getSize() / 2;
                    for (int y = -64; y <= 320; y++) {
                        world.getBlockAt((int) border, y, 0).setType(Material.BARRIER);
                    }

                    for (int y = -64; y <= 320; y++) {
                        world.getBlockAt((int) (-1 * border), y, 0).setType(Material.BARRIER);
                    }

                    for (int y = -64; y <= 320; y++) {
                        world.getBlockAt(0, y, (int) border).setType(Material.BARRIER);
                    }
                }
            }.runTaskTimer(SMP.getPlugin(), 1L, 40 * 20);
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String[] args) {
        if (args.length == 1) return Arrays.asList("team", "givekits", "initborder");

        if (args[0].equals("team")) {
            if (args.length == 3) {
                return Arrays.asList("1", "2", "3", "4");
            } else if (args.length == 2) {
                List<String> players = new ArrayList<>();
                for (Player p : Bukkit.getOnlinePlayers()) {
                    players.add(p.getName());
                }
                return players;
            }
        }

        return Collections.emptyList();
    }
}
