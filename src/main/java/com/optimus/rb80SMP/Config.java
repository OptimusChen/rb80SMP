package com.optimus.rb80SMP;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class Config {

    private static final SMP smp = SMP.getPlugin();

    public static void setTeam(Player player, int id) {
        try {
            File playerFile = new File(smp.getDataFolder() + File.separator + "players" + File.separator + player.getUniqueId() + ".yml");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);

            config.set("teamId", id);

            config.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static int getTeamId(Player player) { return getConfig(player).getInt("teamId"); }
    public static YamlConfiguration getConfig(Player player) {
        File playerFile = new File(smp.getDataFolder() + File.separator + "players" + File.separator + player.getUniqueId() + ".yml");
        return YamlConfiguration.loadConfiguration(playerFile);
    }

    public static void onJoin(Player player) {
        File folder = new File(smp.getDataFolder() + File.separator + "players");
        if (!folder.exists()) folder.mkdirs();
        File playerFile = new File(smp.getDataFolder() + File.separator + "players" + File.separator + player.getUniqueId() + ".yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        if (!playerFile.exists()) {
            try {
                playerFile.createNewFile();

                config.set("teamId", -1);
                config.set("playTime", 0);

                config.save(playerFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
