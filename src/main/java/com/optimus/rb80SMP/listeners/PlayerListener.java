package com.optimus.rb80SMP.listeners;

import com.optimus.rb80SMP.Config;
import com.optimus.rb80SMP.SMP;
import com.optimus.rb80SMP.SMPCommand;
import com.optimus.rb80SMP.ToggleChatCommand;
import org.bukkit.*;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
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
        int rand = new Random().nextInt(5);
        if (p.getName().equals("LynxBit") && rand == 1) {
            p.sendTitle(ChatColor.GREEN + "varsity chess team", ChatColor.RED + "tenniz");
        }

        if (p.getName().equals("A1omic") && rand == 1) {
            p.sendTitle(ChatColor.GREEN + "hail", ChatColor.RED + "honka");
        }

        if (p.getName().equals("IDoCrackDaily") && rand == 1) {
            p.sendTitle(ChatColor.GREEN + "imagine", ChatColor.RED + "getting haxed");
        }

        if (p.getName().equals("rb80") && rand == 1) {
            p.sendTitle(ChatColor.GREEN + "roy da boy", ChatColor.RED + "onetwothree");
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
    public void onChat(AsyncPlayerChatEvent e) {
        List<Player> team = ToggleChatCommand.teamChat;

        if (team.contains(e.getPlayer())) {
            SMP.getPlugin().messageTeam(e.getPlayer(), e.getMessage());
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Arrow && e.getEntity() instanceof Player) {
            Arrow arrow = (Arrow) e.getDamager();
            if (arrow.getShooter() instanceof Player) {
                Player shooter = (Player) arrow.getShooter();
                if (Config.getTeamId((Player) e.getEntity()) == Config.getTeamId(shooter)) {
                    e.setCancelled(true);
                }
            }
        }

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
    public void onCraft(CraftItemEvent e) {
        Recipe recipe = e.getRecipe();
        ItemStack item = recipe.getResult();

        if (item.getType().equals(Material.AIR)) return;
        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;

        if (item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Border Expander")) {
            int[] matricies = {0, 2, 6, 8};

            for (int i : matricies) {
                // andrew tate ! ! ! !!
                ItemStack[] matrix = e.getInventory().getMatrix();
                matrix[i].setAmount(matrix[i].getAmount() - 15);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        ItemStack item = e.getPlayer().getItemInHand();
        if (!item.getType().equals(Material.AIR)) {
            if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                if (item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Border Expander")) {
                    expandBorder(e.getPlayer());
                }
            }
        }

        if (e.getClickedBlock() == null) return;
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (e.getClickedBlock().getType().equals(Material.RESPAWN_ANCHOR) &&
                !e.getPlayer().getLocation().getWorld().getName().equals("world_nether")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + "bruh");
        }
    }

    private void expandBorder(Player p) {
        if (SMP.expandingBorder) {
            p.sendMessage(ChatColor.RED + "Border is already expanding!");
            return;
        }

        World world = Bukkit.getWorld("world");

        if (world == null) return;

        final int max = (int) (world.getWorldBorder().getSize() + 200);

        SMP.expandingBorder = true;

        Bukkit.broadcastMessage(ChatColor.GREEN + p.getName() + " is expanding the border to: " + max + " blocks");

        p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (world.getWorldBorder().getSize() >= max) {
                    Bukkit.broadcastMessage(ChatColor.GREEN + "Border has finished expanding at " + max + " blocks");
                    SMP.expandingBorder = false;
                    cancel();
                }

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

                for (int y = -64; y <= 320; y++) {
                    world.getBlockAt(0, y, (int) (-1 * border)).setType(Material.BARRIER);
                }
            }
        }.runTaskTimer(SMP.getPlugin(), 1L, 20 * 20);
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
