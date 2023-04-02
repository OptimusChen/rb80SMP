package com.optimus.rb80SMP.aztec.gui;

import com.optimus.rb80SMP.SMP;
import com.optimus.rb80SMP.aztec.Advancements;
import com.optimus.rb80SMP.aztec.AztecItem;
import com.optimus.rb80SMP.gui.Gui;
import com.optimus.rb80SMP.util.ItemBuilder;
import com.optimus.rb80SMP.util.Util;
import net.minecraft.world.item.ItemArmor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftInventoryCustom;
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class CodexTable extends CraftInventoryCustom implements Listener {

    public CodexTable() {
        super(null, 27, "Codex Table");

        Util.fillEmpty(this);

        setItem(10, new ItemStack(Material.AIR));
        setItem(12, new ItemStack(Material.AIR));

        setItem(15, new ItemBuilder(ChatColor.RED + "Invalid Combonation", Material.BARRIER).toItemStack());

        Bukkit.getPluginManager().registerEvents(this, SMP.getPlugin());
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (!e.getInventory().equals(this)) return;

        HandlerList.unregisterAll(this);
        if (getItem(10) != null) e.getPlayer().getInventory().addItem(getItem(10));
        if (getItem(12) != null) e.getPlayer().getInventory().addItem(getItem(12));
        if (getItem(12) == null && getItem(10) == null && !getItem(15).getType().equals(Material.BARRIER)) e.getPlayer().getInventory().addItem(getItem(15));
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;

        if (!e.getClickedInventory().equals(this)) return;

        Player player = (Player) e.getWhoClicked();

        if (e.getSlot() == 10 || e.getSlot() == 12) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    update();
                }
            }.runTaskLater(SMP.getPlugin(), 10);
        } else if (e.getSlot() == 15) {
            if (!getItem(15).getType().equals(Material.BARRIER)) {
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 1);

                Advancements.grantAdvancement("Obsidian Armor", player);

                player.getInventory().addItem(getItem(15));

                reset();

                setItem(10, new ItemStack(Material.AIR));
                setItem(12, new ItemStack(Material.AIR));

            }

            e.setCancelled(true);
        } else {
            e.setCancelled(true);
        }
    }

    private void reset() {
        setItem(15, new ItemBuilder(ChatColor.RED + "Invalid Combonation", Material.BARRIER).toItemStack());
    }

    private void success() {
        ItemStack first = getItem(10), second = getItem(12);

        ItemStack obsidian = first.clone();
        obsidian.setType(Material.valueOf(first.getType().name().replaceAll("DIAMOND", "NETHERITE")));
        setItem(15, obsidian);
    }

    private void update() {
        ItemStack first = getItem(10), second = getItem(12);

        if (first == null || second == null) {
            reset();
            return;
        }

        if (!(CraftItemStack.asNMSCopy(first).c() instanceof ItemArmor && first.getType().name().startsWith("DIAMOND"))) {
            reset();
            return;
        }

        if (!Util.isItem(second, AztecItem.OBSIDIAN_PLATING)) {
            reset();
            return;
        }

        success();
    }
}
