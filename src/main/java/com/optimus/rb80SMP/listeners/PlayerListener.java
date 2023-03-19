package com.optimus.rb80SMP.listeners;

import com.optimus.rb80SMP.Config;
import com.optimus.rb80SMP.SMP;
import com.optimus.rb80SMP.SMPCommand;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.HashMap;
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

    private final HashMap<Player, Integer> violations = new HashMap<>();

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Location to = e.getTo();

        if (to == null) return;
        if (p.isOp()) return;

        if (!checkQuadrant(p, to)) {
            e.setCancelled(true);

            violations.put(p, (violations.containsKey(p) ? violations.get(p) + 1 : 1));

            if (violations.get(p) > 5) {
                p.teleport(SMP.getPlugin().getSpawnPoint(Config.getTeamId(p)));
                violations.put(p, 0);
            }
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
    public void onRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        e.setRespawnLocation(SMP.getPlugin().getSpawnPoint(Config.getTeamId(p)));
    }

    private boolean checkQuadrant(Player p, Location to) {
        String worldName = to.getWorld().getName();
        if (worldName.contains("world_the_end")) return true;
        if (!to.getWorld().getName().contains("world")) return true;
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
