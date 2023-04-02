package com.optimus.rb80SMP.aztec.entity.npc;

import com.optimus.rb80SMP.aztec.AztecItem;
import com.optimus.rb80SMP.util.Util;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.trait.waypoint.WanderWaypointProvider;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LoneWanderer extends NPCEntity {

    private static final List<String> dialogue = Arrays.asList(
            "Please help me! I've been lost in this rainforest for days and I don't know how to get out.",
            "Do you have any food or water? I've been surviving on berries and rainwater, but I'm running low.",
            "Watch out for the snakes and spiders around here. They're everywhere.",
            "I've seen some strange plants and animals in this rainforest that I've never seen before. It's like a whole other world.",
            "I can't believe how quiet it is here. It's almost like the rainforest has its own peaceful energy.",
            "I never realized how important basic survival skills were until I got lost in this rainforest. It's amazing what the human body can endure.",
            "I've been trying to find my way out of here for hours. Do you know which way leads to civilization?"
    );

    public LoneWanderer() {
        super("Lone Wanderer", 20);
    }

    @Override
    public void loadData() {
        Equipment equipment = npc.getOrAddTrait(Equipment.class);

        equipment.set(Equipment.EquipmentSlot.HELMET, new ItemStack(Material.IRON_HELMET));
        equipment.set(Equipment.EquipmentSlot.CHESTPLATE, new ItemStack(Material.IRON_CHESTPLATE));
        equipment.set(Equipment.EquipmentSlot.LEGGINGS, new ItemStack(Material.IRON_LEGGINGS));
        equipment.set(Equipment.EquipmentSlot.BOOTS, new ItemStack(Material.IRON_BOOTS));
        equipment.set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.DIAMOND_AXE));
        equipment.set(Equipment.EquipmentSlot.OFF_HAND, new ItemStack(Material.SHIELD));

        WanderWaypointProvider provider = new WanderWaypointProvider();

        provider.addRegionCentre(entity.getLocation());
        provider.onSpawn(npc);

        entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(15);

        List<Entity> nearby = npc.getEntity().getNearbyEntities(30, 30, 30).stream().filter((e) -> e instanceof Player).collect(Collectors.toList());

        for (Entity e : nearby) {
            e.sendMessage(ChatColor.WHITE + "<Lone Wanderer> " + dialogue.get(Util.random(0, dialogue.size() - 1)));
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
