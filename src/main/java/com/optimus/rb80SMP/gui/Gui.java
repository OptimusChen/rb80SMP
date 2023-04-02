package com.optimus.rb80SMP.gui;

import com.optimus.rb80SMP.SMP;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Getter
public class Gui implements Listener {

    private static final HashMap<Gui, Boolean> REGISTERED_LISTENERS = new HashMap<>();

    public final HashMap<String, Runnable> clickEvents;
    public final HashMap<Integer, ItemStack> items;
    public final HashMap<Player, Boolean> opened;
    public final List<ItemStack> addableItems;
    public final int slots;
    public String name;
    public Gui(String name, int slots, HashMap<String, Runnable> clickEvents) {
        this.name = name;
        this.slots = slots;

        this.clickEvents = clickEvents;

        this.items = new HashMap<>();
        this.addableItems = new ArrayList<>();
        this.opened = new HashMap<>();
    }

    public void show(Player player) {
        Inventory inventory = player.getServer().createInventory(null, slots, name);

        for (int i = 0; i < slots; i++) {
            if (items.containsKey(i)) {
                inventory.setItem(i, items.get(i));
            }
        }

        for (ItemStack stack : this.addableItems) inventory.addItem(stack);

        player.openInventory(inventory);

        Bukkit.getPluginManager().registerEvents(this, SMP.getPlugin());

        opened.put(player, true);
    }

    public void hide(Player player) {
        player.closeInventory();
    }

    public void addItem(int slot, ItemStack stack) {
        this.items.put(slot, stack);
    }

    public void addItem(ItemStack stack) {
        this.addableItems.add(stack);
    }

    public ItemStack getItem(int slot) {
        return this.items.get(slot);
    }

    protected boolean getSpecificClickSound() { return true; }

    public void fillEmpty(ItemStack stack) {
        for (int i = 0; i < this.slots; i++) {
            if (!this.items.containsKey(i)) this.items.put(i, stack);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(name) && opened.containsKey((Player) event.getWhoClicked())) {
            event.setCancelled(true);

            if (clickEvents.containsKey(event.getCurrentItem().getItemMeta().getDisplayName())) {
                clickEvents.get(event.getCurrentItem().getItemMeta().getDisplayName()).run();
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (e.getView().getTitle().equals(name) && opened.containsKey((Player) e.getPlayer())) {
            HandlerList.unregisterAll(this);
            opened.remove((Player) e.getPlayer());
        }
    }
}