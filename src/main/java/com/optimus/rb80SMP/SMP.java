package com.optimus.rb80SMP;

import com.optimus.rb80SMP.listeners.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class SMP extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        PluginCommand main = getCommand("smp");
        SMPCommand cmd = new SMPCommand();
        main.setExecutor(cmd);
        main.setTabCompleter(cmd);
    }

    @Override
    public void onDisable() {

    }

    public Location getSpawnPoint(int team) {
        World world = Bukkit.getWorld("world");
        Location l = new Location(world, 0, 100, 0);
        switch (team) {
            case 1:
                // pos pos quadrant
                l = new Location(world, 25, 100, 25);
                break;
            case 2:
                // pos neg quadrant
                l = new Location(world, 25, 100, -25);
                break;
            case 3:
                // neg neg quadrant
                l = new Location(world, -25, 100, -25);
                break;
            case 4:
                // neg pos quadrant
                l = new Location(world, -25, 100, 25);
                break;
        }

        l.setY(world.getHighestBlockYAt((int) l.getX(), (int) l.getZ()) + 2);

        return l;
    }

    public static SMP getPlugin() { return getPlugin(SMP.class); }
}
