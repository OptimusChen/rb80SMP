package com.optimus.rb80SMP.aztec.entity.npc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.optimus.rb80SMP.SMP;
import com.optimus.rb80SMP.aztec.AztecItem;
import com.optimus.rb80SMP.util.Util;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.trait.SkinTrait;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.stream.Collectors;

public class Tezcatlipoca extends NPCEntity {

    private int tick = 0;
    private Location spawn;
    private Equipment equipment;

    public Tezcatlipoca() {
        super(ChatColor.RED + "Tezcatlipoca", 500);
    }

    @Override
    public void loadData() {
        equipment = npc.getOrAddTrait(Equipment.class);

        ItemStack item = AztecItem.XIUHCOATL_SWORD.getItem().clone();
        item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 30);

        equipment.set(Equipment.EquipmentSlot.HAND, item);
        equipment.set(Equipment.EquipmentSlot.OFF_HAND, AztecItem.JAGUAR_TOTEM.getItem());

        EntityPlayer entityPlayer = entity.getHandle();

        GameProfile gameProfile = entityPlayer.fD();
        gameProfile.getProperties().removeAll("textures");

        PropertyMap map = gameProfile.getProperties();

        map.put("textures", new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTY0NDQ3MzI4MzA5NSwKICAicHJvZmlsZUlkIiA6ICIzNmMxODk4ZjlhZGE0NjZlYjk0ZDFmZWFmMjQ0MTkxMyIsCiAgInByb2ZpbGVOYW1lIiA6ICJMdW5haWFuIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzE3NjFlMDViOTJiZGRlMjVkYWJiZWU3NzVhMzFhMjlkODcyYjk4ZWNjMWRkYmZjOGNiZmExYjBmZmY0OTI1NGMiCiAgICB9CiAgfQp9", "w9PJAR9PZT1E3/iR195fm0saef6bUyRSrl5uC9fHfMdCm5Yh2RvLClP99vbwfxxRegZ8UQ94ckT9KWc/wT4RGWSV0ibO8CJs+axpVamld7eP+HoICHoIwa8f9VFRT+gwVZg/jEwy5x3t/m7w9wJKkYEDCtvtmFD1iWbJSS97HjwmUdpSjlefpSZ9v2rKVqNGtYrNuEgbrYd6nUrPyKGAgXxza/1oYMWHHh0w2LPm59ERccMH979COTVz1zb8JxnYGWielCW+3/iH/+vakiLT0D70JKly9oPrJko+mUXs2eK9JtU6q1SoWMtP+bavTe5GnkYUcmAUoS5tQQ5Z6FOm9NXt7qdEVRgJFhCEKTlmU6oy0/hqfhUFRKsSGjOYniINM43YwHoDub/o8gt0PeUQI5J42W2CaHFRFbLZIBnkkrfju4v976bChWPPfcr6zoAYb+NW/O2pb4o3vcS/42xcbez8+wR17aud+YapuPfF6nqPf0PG5SqVvN+8IS93WRpYEkme/FMmd3SzWxwsR3iscqZPGMdJdXbAgyNmoSJO/Z9ttcP77xhhk7jozkej8wW3oyD67BCPFdvrh1V4MBZN4pdVBVe4U+fPyQ2HtxyH3wZlM4L1bY5qEPxGaYFTRDxUw69ChcO6jy5bd5S3a5tjfNldEhy0Ftg6KuBBbrnTZCc="));

        npc.setProtected(false);

        entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(75);

        spawn = entity.getLocation();
    }

    @Override
    public void AI() {
        if (npc.getEntity() == null) return;

        tick++;

        List<Entity> nearby = npc.getEntity().getNearbyEntities(10, 10, 10);

        for (Entity e : nearby) {
            if (!(e instanceof Player)) continue;
            if (e.hasMetadata("NPC")) continue;

            npc.getNavigator().setTarget(e, true);
        }

        if (tick % 100 == 0) {
            switch (Util.random(0, 4)) {
                case 0:
                    darkness();
                    break;
                case 1:
                    mirror();
                    break;
                case 2:
                    sorcery();
                    break;
                case 4:
                    sacrifice();
                    break;
            }
        }
    }

    @Override
    protected void death() {
        Block b = aztec.getBlockAt(0, 63, 0);

        if (!b.getType().equals(Material.END_PORTAL)) {
            Bukkit.broadcastMessage(ChatColor.GOLD + "Tezcatlipoca has been killed and his curse has been broken.");

        }
    }

    private void darkness() {
        List<Entity> nearby = npc.getEntity().getNearbyEntities(10, 10, 10).stream().filter((e) -> e instanceof Player).collect(Collectors.toList());

        npc.teleport(spawn.clone().add(0, 2, 0), PlayerTeleportEvent.TeleportCause.PLUGIN);

        for (Entity e : nearby) {
            if (e.hasMetadata("NPC")) continue;

            Player player = (Player) e;

            player.playSound(spawn, Sound.ENTITY_GENERIC_EXPLODE, 10, 1);
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20*4, 0, true));
            player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20*4, 0, true));

            player.playSound(spawn, Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 10, 1);

            player.damage(10, entity);
        }
    }

    private void mirror() {
        NPC copy = npc.clone();

        copy.getNavigator().setTarget(npc.getNavigator().getEntityTarget().getTarget(), true);

        EntityPlayer entityPlayer = ((CraftPlayer) copy.getEntity()).getHandle();

        GameProfile gameProfile = entityPlayer.fD();
        gameProfile.getProperties().removeAll("textures");

        PropertyMap map = gameProfile.getProperties();

        Location loc = entity.getLocation();
        Vector dir = entity.getLocation().getDirection();
        dir.normalize();
        dir.multiply(3);
        loc.add(dir);

        copy.teleport(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);

        map.put("textures", new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTY0NDQ3MzI4MzA5NSwKICAicHJvZmlsZUlkIiA6ICIzNmMxODk4ZjlhZGE0NjZlYjk0ZDFmZWFmMjQ0MTkxMyIsCiAgInByb2ZpbGVOYW1lIiA6ICJMdW5haWFuIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzE3NjFlMDViOTJiZGRlMjVkYWJiZWU3NzVhMzFhMjlkODcyYjk4ZWNjMWRkYmZjOGNiZmExYjBmZmY0OTI1NGMiCiAgICB9CiAgfQp9", "w9PJAR9PZT1E3/iR195fm0saef6bUyRSrl5uC9fHfMdCm5Yh2RvLClP99vbwfxxRegZ8UQ94ckT9KWc/wT4RGWSV0ibO8CJs+axpVamld7eP+HoICHoIwa8f9VFRT+gwVZg/jEwy5x3t/m7w9wJKkYEDCtvtmFD1iWbJSS97HjwmUdpSjlefpSZ9v2rKVqNGtYrNuEgbrYd6nUrPyKGAgXxza/1oYMWHHh0w2LPm59ERccMH979COTVz1zb8JxnYGWielCW+3/iH/+vakiLT0D70JKly9oPrJko+mUXs2eK9JtU6q1SoWMtP+bavTe5GnkYUcmAUoS5tQQ5Z6FOm9NXt7qdEVRgJFhCEKTlmU6oy0/hqfhUFRKsSGjOYniINM43YwHoDub/o8gt0PeUQI5J42W2CaHFRFbLZIBnkkrfju4v976bChWPPfcr6zoAYb+NW/O2pb4o3vcS/42xcbez8+wR17aud+YapuPfF6nqPf0PG5SqVvN+8IS93WRpYEkme/FMmd3SzWxwsR3iscqZPGMdJdXbAgyNmoSJO/Z9ttcP77xhhk7jozkej8wW3oyD67BCPFdvrh1V4MBZN4pdVBVe4U+fPyQ2HtxyH3wZlM4L1bY5qEPxGaYFTRDxUw69ChcO6jy5bd5S3a5tjfNldEhy0Ftg6KuBBbrnTZCc="));

        new BukkitRunnable() {
            @Override
            public void run() {
                copy.despawn();
                copy.destroy();
            }
        }.runTaskLater(SMP.getPlugin(), 20 * 5);

        List<Entity> nearby = npc.getEntity().getNearbyEntities(10, 10, 10).stream().filter((e) -> e instanceof Player).collect(Collectors.toList());

        for (Entity e : nearby) {
            if (e.hasMetadata("NPC")) continue;

            Player player = (Player) e;

            player.playSound(spawn, Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR, 10, 1);
        }
    }

    private void sorcery() {
        List<Entity> nearby = npc.getEntity().getNearbyEntities(10, 10, 10).stream().filter((e) -> e instanceof Player).collect(Collectors.toList());

        for (Entity e : nearby) {
            if (e.hasMetadata("NPC")) continue;

            Player player = (Player) e;

            player.playSound(spawn, Sound.ENTITY_BLAZE_AMBIENT, 10, 1);

            player.getLocation().getBlock().setType(Material.FIRE);
        }
    }

    private void sacrifice() {
        equipment.set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.GOLDEN_APPLE));

        List<Entity> nearby = npc.getEntity().getNearbyEntities(10, 10, 10).stream().filter((e) -> e instanceof Player).collect(Collectors.toList());

        for (Entity e : nearby) {
            if (e.hasMetadata("NPC")) continue;

            Player player = (Player) e;

            player.playSound(spawn, Sound.ENTITY_GENERIC_EAT, 10, 1);
        }

        entity.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 1));

        new BukkitRunnable() {
            @Override
            public void run() {
                equipment.set(Equipment.EquipmentSlot.HAND, AztecItem.XIUHCOATL_SWORD.getItem());
            }
        }.runTaskLater(SMP.getPlugin(), 20);
    }
}
