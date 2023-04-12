package com.optimus.rb80SMP.aztec.enchantment;

import com.optimus.rb80SMP.SMP;
import enchantmentapi.enchantmentapi.CustomEnchantment;
import enchantmentapi.enchantmentapi.EnchantmentAPI;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Chimali extends CustomEnchantment {

    private static final List<Entity> affected = new ArrayList<>();
    @Override
    protected String getName() {
        return "Chimali";
    }

    @Override
    protected String getNamespace() {
        return "aztec";
    }

    @Override
    protected int getMaxLevel() {
        return 5;
    }

    @Override
    protected boolean canEnchant(ItemStack itemStack) {
        return itemStack.getType().name().contains("SWORD") || itemStack.getType().name().contains("AXE") || itemStack.getType().equals(Material.ENCHANTED_BOOK);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            Player p = (Player) e.getDamager();
            Entity entity = e.getEntity();

            if (EnchantmentAPI.hasEnchantment(p.getItemInHand(), this)) {
                if (affected.contains(entity)) return;

                affected.add(entity);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!affected.contains(entity)) cancel();
                        if (entity.isDead()) cancel();

                        Location loc = entity.getLocation().clone().add(0, 1, 0);
                        loc.getWorld().spawnParticle(Particle.BLOCK_CRACK, loc, 50, 0.2, 0.2, 0.2, 0.5, Material.REDSTONE_BLOCK.createBlockData());

                        if (entity instanceof LivingEntity) ((LivingEntity) entity).damage(5, p);
                    }
                }.runTaskTimer(SMP.getPlugin(), 0, 20);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        affected.remove(entity);
                    }
                }.runTaskLater(SMP.getPlugin(), 20 * 5);
            }
        }
    }
}
