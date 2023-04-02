package com.optimus.rb80SMP.aztec.entity.npc;

import com.optimus.rb80SMP.SMP;
import com.optimus.rb80SMP.aztec.AztecItem;
import lombok.Getter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.event.NPCSpawnEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

@Getter
public abstract class NPCEntity implements Listener {

    protected static final List<ItemStack> organs = Arrays.asList(
            AztecItem.SKULL.getItem(),
            AztecItem.HEART.getItem(),
            AztecItem.TISSUE.getItem()
    );

    protected static final World aztec = Bukkit.getWorld("aztec");

    protected NPC npc;
    protected final String name;
    protected int health;
    protected final int maxHealth;
    protected CraftPlayer entity;

    public NPCEntity(String name, int health) {
        this.name = name;
        this.health = health;
        this.maxHealth = health;
        this.entity = null;

        Bukkit.getPluginManager().registerEvents(this, SMP.getPlugin());
    }

    public abstract void loadData();

    public abstract void AI();

    public void spawn(Location location) {
        npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);
        npc.spawn(location);

        entity = (CraftPlayer) npc.getEntity();

        entity.setMaxHealth(maxHealth);
        entity.setHealth(maxHealth);

        npc.setProtected(false);

        new BukkitRunnable() {
            @Override
            public void run() {
                AI();
            }
        }.runTaskTimer(SMP.getPlugin(), 1, 1);
    }

    protected void death() {

    }

    protected void damage(NPCDamageByEntityEvent e) {

    }

    @EventHandler
    public void onDamage(NPCDamageByEntityEvent e) {
        if (e.getNPC().equals(npc)) {
            damage(e);
        }
    }

    @EventHandler
    public void onSpawn(NPCSpawnEvent e) {
        if (e.getNPC().equals(npc)) {
            entity = (CraftPlayer) npc.getEntity();
            loadData();
        }
    }

    @EventHandler
    public void onDeath(NPCDeathEvent e) {
        if (e.getNPC().equals(npc)) {
            death();

            HandlerList.unregisterAll(this);
            npc.despawn();
            npc.destroy();
        }
    }
}