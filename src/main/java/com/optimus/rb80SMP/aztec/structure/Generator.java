package com.optimus.rb80SMP.aztec.structure;

import com.optimus.rb80SMP.SMP;
import com.optimus.rb80SMP.aztec.Aztec;
import com.optimus.rb80SMP.aztec.AztecItem;
import com.optimus.rb80SMP.util.Util;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Generator {

    @Getter
    public
    enum Structure {
        PYRAMID,
        TEMPLE,
        TOMB,
        FORTRESS;

        private final String file;

        Structure() {
            file = name().toLowerCase();
        }
    }

    private static final World aztec = Bukkit.getWorld("aztec");

    public static void generateStructure(Structure structure) {
        if (aztec == null) return;

        double max = aztec.getWorldBorder().getSize() / 2 - 20;
        double min = aztec.getWorldBorder().getSize() / -2 + 20;

        double x = Util.random(min, max);
        double z = Util.random(min, max);
        double y = Util.getHighestY(aztec, (int) x, (int) z) + 1;

        while (true) {
            Location pos1 = new Location(aztec, x + 20, y - 5, z + 20);
            Location pos2 = new Location(aztec, x - 20, y, z - 20);

            List<Block> blocks = Util.blocksFromTwoPoints(pos1, pos2);

            boolean foundWater = false;

            for (Block b : blocks) {
                if (b.getType().equals(Material.WATER)) {
                    foundWater = true;
                    break;
                }
            }

            if (foundWater || y == 0) {
                x = Util.random(min, max);
                z = Util.random(min, max);
                y = Util.getHighestY(aztec, (int) x, (int) z) + 1;
            } else {
                break;
            }
        }

        Location location = new Location(aztec, x, y, z);

        Util.pasteSchematic(location, structure.getFile());

        new BukkitRunnable() {
            @Override
            public void run() {
                populateChests(location);
                deleteBedrock(location);

                if (structure.equals(Structure.FORTRESS)) populateSpawners(location);
            }
        }.runTaskLater(SMP.getPlugin(), 20);

        Bukkit.broadcastMessage("Structure placed at: " + x + ", " + y + ", " + z);
    }

    private static void deleteBedrock(Location location) {
        double x = location.getX(), y = location.getY(), z = location.getZ();

        Location pos1 = new Location(aztec, x + 20, y - 10, z + 20);
        Location pos2 = new Location(aztec, x - 20, y + 15, z - 20);

        List<Block> blocks = Util.blocksFromTwoPoints(pos1, pos2);

        for (Block b : blocks) {
            if (!b.getType().equals(Material.BEDROCK)) continue;

            b.setType(Material.AIR);
        }
    }

    private static void populateChests(Location location) {
        double x = location.getX(), y = location.getY(), z = location.getZ();

        Location pos1 = new Location(aztec, x + 20, y - 10, z + 20);
        Location pos2 = new Location(aztec, x - 20, y + 15, z - 20);

        List<Block> blocks = Util.blocksFromTwoPoints(pos1, pos2);

        for (Block b : blocks) {
            if (!b.getType().equals(Material.CHEST)) continue;
            if (Aztec.getInstance().isPortal(b)) continue;

            Chest chest = (Chest) b.getState();

            Inventory inv = chest.getBlockInventory();

            populate(inv);
        }
    }

    private static final List<ChestDrop> drops = Arrays.asList(
            new ChestDrop(0.8, Material.IRON_INGOT),
            new ChestDrop(0.7, Material.GOLD_INGOT),
            new ChestDrop(0.7, Material.EMERALD),
            new ChestDrop(0.5, Material.DIAMOND),
            new ChestDrop(0.2, Material.NETHERITE_SCRAP),
            new ChestDrop(0.6, Material.BONE),
            new ChestDrop(0.7, Material.ROTTEN_FLESH),
            new ChestDrop(0.6, Material.BREAD),
            new ChestDrop(0.3, Material.OBSIDIAN),
            new ChestDrop(0.2, Material.DIAMOND_AXE),
            new ChestDrop(0.2, Material.DIAMOND_PICKAXE),
            new ChestDrop(0.6, Material.GOLDEN_APPLE),
            new ChestDrop(0.7, Material.EXPERIENCE_BOTTLE),
            new ChestDrop(0.3, Material.SPECTRAL_ARROW),
            new ChestDrop(0.1, AztecItem.XIUHCOATL_SWORD.getItem()),
            new ChestDrop(0.05, AztecItem.FEATHERED_BOW.getItem()),
            new ChestDrop(0.2, AztecItem.CODEX_STAND.getItem()),
            new ChestDrop(0.2, AztecItem.SPEAR.getItem())
    );

    private static void populate(Inventory inv) {
        Util.shuffle(drops);

        List<ItemStack> items = new ArrayList<>();
        int req = 8;

        int i = 0;
        while (items.size() < req) {
            ChestDrop drop = drops.get(i);

            if (drop.test()) {
                items.add(drop.getItem());
            }

            i++;

            if (i == drops.size()) i = 0;
        }

        List<Integer> slots = new ArrayList<>();

        for (ItemStack item : items) {
            int slot = Util.random(0, inv.getSize() - 1);

            while (slots.contains(slot)) {
                slot = Util.random(0, inv.getSize() - 1);
            }

            inv.setItem(slot, item);

            slots.add(slot);
        }
    }

    private static void populateSpawners(Location location) {
        double x = location.getX(), y = location.getY(), z = location.getZ();

        Location pos1 = new Location(aztec, x + 20, y - 10, z + 20);
        Location pos2 = new Location(aztec, x - 20, y + 15, z - 20);

        List<Block> blocks = Util.blocksFromTwoPoints(pos1, pos2);

        for (Block b : blocks) {
            if (!b.getType().equals(Material.SPAWNER)) continue;

            CreatureSpawner spawner = (CreatureSpawner) b.getState();

            spawner.setSpawnedType(EntityType.ILLUSIONER);
        }
    }
}
