package com.optimus.rb80SMP.aztec.entity.npc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.optimus.rb80SMP.aztec.AztecItem;
import com.optimus.rb80SMP.util.Util;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.trait.waypoint.WanderWaypointProvider;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Purempecha extends NPCEntity {

    private static final List<String> dialogue = Arrays.asList(
            "Who are you and what do you want here?",
            "This land belongs to the Purepecha people, and we will defend it against any who seek to take it from us."
    );

    public Purempecha() {
        super("Purempecha", 25);
    }

    @Override
    public void loadData() {
        Equipment equipment = npc.getOrAddTrait(Equipment.class);
        equipment.set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.FISHING_ROD));

        EntityPlayer entityPlayer = entity.getHandle();

        GameProfile gameProfile = entityPlayer.fD();
        gameProfile.getProperties().removeAll("textures");

        PropertyMap map = gameProfile.getProperties();

        map.put("textures", new Property("textures", ""));

        WanderWaypointProvider provider = new WanderWaypointProvider();

        provider.addRegionCentre(entity.getLocation());
        provider.onSpawn(npc);

        entity.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(20);
        entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(15);

        List<Entity> nearby = npc.getEntity().getNearbyEntities(30, 30, 30).stream().filter((e) -> e instanceof Player).collect(Collectors.toList());

        for (Entity e : nearby) {
            e.sendMessage(ChatColor.WHITE + "<Purempecha> " + dialogue.get(Util.random(0, dialogue.size() - 1)));
        }
    }

    @Override
    protected void death() {
        World world = entity.getWorld();

        for (int i = 0; i < Util.random(0, 5); i++) {
            world.dropItemNaturally(entity.getLocation(), AztecItem.BLOOD.getItem());
        }

        if (Util.random(0, 10) != 0) return;

        world.dropItemNaturally(entity.getLocation(), organs.get(Util.random(0, 2)));
    }

    @Override
    public void damage(NPCDamageByEntityEvent e) {
        npc.getNavigator().setTarget(e.getDamager(), true);
    }

    @Override
    public void AI() {

    }
}
