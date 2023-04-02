package com.optimus.rb80SMP.aztec.entity.npc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.optimus.rb80SMP.aztec.Aztec;
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

public class TribeMember extends NPCEntity {

    private static final List<String> dialogue = Arrays.asList(
            "Who goes there? This land belongs to our people.",
            "I do not recognize your face. What is your purpose in this forest?",
            "Be warned, stranger. You have entered sacred Aztec territory.",
            "You are not welcome here. Leave now or face the consequences.",
            "Our people have lived in harmony with this land for generations. You are a disruptor.",
            "I will not allow you to harm the plants and creatures that inhabit this forest.",
            "Your presence here is an insult to our culture and beliefs.",
            "This forest is our home. We will protect it at all costs.",
            "You may have weapons, but we have strength in numbers and the power of the earth on our side.",
            "You may have come from far away, but this land will always belong to the Aztecs.",
            "Hi, my name is LynxBit."
    );

    public TribeMember() {
        super("Aztec Tribe Member", 40);
    }

    @Override
    public void loadData() {
        Equipment equipment = npc.getOrAddTrait(Equipment.class);

        equipment.set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.DIAMOND_SWORD));

        EntityPlayer entityPlayer = entity.getHandle();

        GameProfile gameProfile = entityPlayer.fD();
        gameProfile.getProperties().removeAll("textures");

        PropertyMap map = gameProfile.getProperties();

        map.put("textures", new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTU5NDQyOTI1MzA1OSwKICAicHJvZmlsZUlkIiA6ICI3MmNiMDYyMWU1MTA0MDdjOWRlMDA1OTRmNjAxNTIyZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJNb3M5OTAiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmE5YmM4MjcyMjM5ZDA0N2E0NTlmMzQ3ZGNlMGFjNzEwNTg2MWQ3ZjliOWE5NjMwMDliN2I5ZTZmNWIzNTMyMiIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9", "XcZVc4vI4Ewvpta6f+/hg7M4CxnSmJhvvhdTwkXFLPHhyeNEwC/JYiAfyXsglBBdQcRQzMW0Ud8DHl0Gj5OoVI/GrIL4+4ud1mldlnaHdwaLB43wFVZWcdxSsdHcJqbaHdMoQGovJ6X9LcxcnGFSos+etUe6mKiamAtFr8af1Nv+VWpRIpP0u4n2+xlfiw+XgTDuu3yd3IY3d1IC2Rgi0FDRoQfTePQzFSksI+AMkytfc8oZ3YNUot3dCP7d9oAurmPnenE1M/p3GvagkmjmHU+vDARpgdCVoK4Ru5YHn3lShig3idnIgjLPP+akyGuYzEEUzrHAe5orPsGW8yr38rvYMT5S7cvA8aH0mYRwCNSDgg2vxSlPsmXzZe2jVWlPZQshC61DBdHZQJoKRXqbAe6+OUECJ8XBVAUkkNjbOggvIdtpxjk6jDqwPE7uVXjPsEhG0VhGuy/03QlgNqBtg2jBRQY4UYphAcfi3hutakTte37Gzx7Zw+DQcUQXyj8mGpbwfTrFuoG4RFyYFScXXXCk371I9gZPD5gS2fWBL/n378J6S4e9421CUp1YAfu1UKm+llRSv7kGw+4LP85b2uC2AAaezfQY0e7q2yYptPFZXUkeExkWYfGUm/3gSxTeO01p8kS6Ri+OXk3lmFHBZFKH+mitEnUtdG5K6BGteWw="));

        WanderWaypointProvider provider = new WanderWaypointProvider();

        provider.addRegionCentre(entity.getLocation());
        provider.onSpawn(npc);

        entity.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(20);
        entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(15);

        List<Entity> nearby = npc.getEntity().getNearbyEntities(30, 30, 30).stream().filter((e) -> e instanceof Player).collect(Collectors.toList());

        for (Entity e : nearby) {
            e.sendMessage(ChatColor.WHITE + "<Aztec Tribe Member> " + dialogue.get(Util.random(0, dialogue.size() - 1)));
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
