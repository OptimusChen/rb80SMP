package com.optimus.rb80SMP.aztec;

import com.optimus.rb80SMP.Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Advancements {

    public static void grantAdvancement(String name, Player p) {
        boolean inConfig = Config.hasValue(p, "advancements." + name);

        if (!inConfig) Config.setValue(p, "advancements." + name, false);

        boolean achieved = (boolean) Config.getValue(p, "advancements." + name);

        if (achieved) return;

        Config.setValue(p, "advancements." + name, true);

        // TODO: Hover thingy
        Bukkit.broadcastMessage(p.getName() + " has made the advancement " + ChatColor.GREEN + "[" + name + "]");
        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 1);
    }
}
