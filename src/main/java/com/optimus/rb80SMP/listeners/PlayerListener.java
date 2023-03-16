package com.optimus.rb80SMP.listeners;

import com.optimus.rb80SMP.Config;
import com.optimus.rb80SMP.SMP;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerListener implements Listener {

    private static final SMP smp = SMP.getPlugin();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        Config.onJoin(p);

        // No team assigned
        if (Config.getTeamId(p) == -1 && !p.isOp()) {
            p.teleport(new Location(p.getWorld(), 0, 1000, 0));
            p.setGameMode(GameMode.SPECTATOR);
            p.sendMessage(ChatColor.GREEN + "u are currently in limbo wait for a admin to assign u to ur team");
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Location to = e.getTo();

        if (to == null) return;

        if (!checkQuadrant(p, to)) e.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player) && !(e.getDamager() instanceof Player)) return;

        if (Config.getTeamId((Player) e.getEntity()) == Config.getTeamId((Player) e.getDamager())) {
            e.setCancelled(true);
        }
    }

    private boolean checkQuadrant(Player p, Location to) {
        switch (Config.getTeamId(p)) {
            case 1:
                // pos pos quadrant
                if (!(to.getX() > 0 && to.getY() > 0)) return false;
                break;
            case 2:
                // pos neg quadrant
                if (!(to.getX() > 0 && to.getY() < 0)) return false;
                break;
            case 3:
                // neg neg quadrant
                if (!(to.getX() < 0 && to.getY() < 0)) return false;
                break;
            case 4:
                // neg pos quadrant
                if (!(to.getX() < 0 && to.getY() > 0)) return false;
                break;
            default:
                return false;
        }

        return true;
    }
}
