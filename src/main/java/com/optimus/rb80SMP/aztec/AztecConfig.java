package com.optimus.rb80SMP.aztec;

import com.optimus.rb80SMP.SMP;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class AztecConfig {

    public static final String AZTEC_FILE_NAME = "aztec.yml";
    private final File file;

    public AztecConfig() {
        this.file = new File(SMP.getPlugin().getDataFolder() + File.separator + AZTEC_FILE_NAME);

        this.init();
    }

    public Object getField(String field) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        return config.get(field);
    }

    public void setField(String path, Object value) {
        try {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            config.set(path, value);

            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasValue(String path) { return YamlConfiguration.loadConfiguration(file).contains(path); }

    public void init() {
        if (file.exists()) return;

        try {
            boolean success = file.createNewFile();

            if (!success) return;

            YamlConfiguration.loadConfiguration(file).save(file);
        } catch (Exception ignored) { }
    }

}
