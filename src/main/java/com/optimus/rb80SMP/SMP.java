package com.optimus.rb80SMP;

import com.optimus.rb80SMP.listeners.PlayerListener;
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

    public static SMP getPlugin() { return getPlugin(SMP.class); }
}
