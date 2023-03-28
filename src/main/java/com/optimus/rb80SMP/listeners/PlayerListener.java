package com.optimus.rb80SMP.listeners;

import com.optimus.rb80SMP.Config;
import com.optimus.rb80SMP.SMP;
import com.optimus.rb80SMP.SMPCommand;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class PlayerListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        Config.onJoin(p);

        // No team assigned
        if (Config.getTeamId(p) == -1 && !p.isOp()) {
            p.teleport(new Location(p.getWorld(), 0, 1000, 0));
            p.setGameMode(GameMode.SPECTATOR);
            p.sendMessage(ChatColor.GREEN + "u are currently in limbo wait for a admin to assign u to ur team");
        } else {
            int team = Config.getTeamId(p);

            SMPCommand.applyTeamColor(p, team);
        }

        // ignore this
        int rand = new Random().nextInt(6);
        if (p.getName().equals("LynxBit") && rand == 1) {
            p.sendTitle(ChatColor.GREEN + "varsity chess team", ChatColor.RED + "tenniz");
        }

        if (p.getName().equals("A1omic") && rand == 1) {
            p.sendTitle(ChatColor.GREEN + "hail", ChatColor.RED + "honka");
        }

        if (p.getName().equals("IDoCrackDaily") && rand == 1) {
            p.sendTitle(ChatColor.GREEN + "imagine", ChatColor.RED + "getting haxed");
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Location to = e.getTo();

        if (to == null) return;
        if (p.isOp()) return;

        if (!checkQuadrant(p, to)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player)) return;

        if (Config.getTeamId((Player) e.getEntity()) == Config.getTeamId((Player) e.getDamager())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlock(BlockBreakEvent e) {
        Player p = e.getPlayer();

        if (p.isOp()) return;

        e.setCancelled(!checkQuadrant(p, e.getBlock().getLocation()));
    }

    @EventHandler
    public void onBlock(BlockPlaceEvent e) {
        Player p = e.getPlayer();

        if (p.isOp()) return;

        e.setCancelled(!checkQuadrant(p, e.getBlockPlaced().getLocation()));
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        if (e.getFrom().getName().equals("world_nether")) {
            Player p = e.getPlayer();

            if (!checkQuadrant(p, p.getLocation())) p.teleport(SMP.getPlugin().getSpawnPoint(Config.getTeamId(p)));
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();

        if (!checkQuadrant(p, e.getRespawnLocation())) {
            e.setRespawnLocation(SMP.getPlugin().getSpawnPoint(Config.getTeamId(p)));
        }
    }

    @EventHandler
    public void onExplode(ExplosionPrimeEvent e) {
        if (e.getEntity() instanceof EnderCrystal) {
            e.setRadius(0.0f);
        }
    }

    List<Player> totemCooldowns = new ArrayList<>();

    @EventHandler
    public void onResurrect(EntityResurrectEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (totemCooldowns.contains(p)) {
                p.sendMessage(ChatColor.RED + "ur still on cooldown");
                e.setCancelled(true);
            } else {
                totemCooldowns.add(p);
                p.sendMessage(ChatColor.GREEN + "you are on totem cooldown for: 2m30s");

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        p.sendMessage(ChatColor.GREEN + "u can use a totem again");
                        totemCooldowns.remove(p);
                    }
                }.runTaskLater(SMP.getPlugin(), 150 * 20);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (e.getClickedBlock().getType().equals(Material.RESPAWN_ANCHOR) &&
                !e.getPlayer().getLocation().getWorld().getName().equals("world_nether")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + "bruh");
        }
    }

    private boolean checkQuadrant(Player p, Location to) {
        if (!to.getWorld().getName().equals("world")) return true;
        switch (Config.getTeamId(p)) {
            case 1:
                // pos pos quadrant
                if (!(to.getX() > 0 && to.getZ() > 0)) return false;
                break;
            case 2:
                // pos neg quadrant
                if (!(to.getX() > 0 && to.getZ() < 0)) return false;
                break;
            case 3:
                // neg neg quadrant
                if (!(to.getX() < 0 && to.getZ() < 0)) return false;
                break;
            case 4:
                // neg pos quadrant
                if (!(to.getX() < 0 && to.getZ() > 0)) return false;
                break;
            default:
                return false;
        }

        return true;
    }
}
