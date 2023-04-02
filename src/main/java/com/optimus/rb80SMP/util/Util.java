package com.optimus.rb80SMP.util;

import com.optimus.rb80SMP.SMP;
import com.optimus.rb80SMP.aztec.AztecItem;
import com.optimus.rb80SMP.gui.Gui;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@UtilityClass
public class Util {

    public List<Block> blocksFromTwoPoints(Location loc1, Location loc2) {
        List<Block> blocks = new ArrayList<>();

        return getBlocks(loc1, loc2, blocks);
    }

    public static List<Block> getBlocks(Location loc1, Location loc2, List<Block> blocks) {
        int topBlockX = (Math.max(loc1.getBlockX(), loc2.getBlockX()));
        int bottomBlockX = (Math.min(loc1.getBlockX(), loc2.getBlockX()));

        int topBlockY = (Math.max(loc1.getBlockY(), loc2.getBlockY()));
        int bottomBlockY = (Math.min(loc1.getBlockY(), loc2.getBlockY()));

        int topBlockZ = (Math.max(loc1.getBlockZ(), loc2.getBlockZ()));
        int bottomBlockZ = (Math.min(loc1.getBlockZ(), loc2.getBlockZ()));

        for(int x = bottomBlockX; x <= topBlockX; x++)
        {
            for(int z = bottomBlockZ; z <= topBlockZ; z++)
            {
                for(int y = bottomBlockY; y <= topBlockY; y++)
                {
                    Block block = loc1.getWorld().getBlockAt(x, y, z);

                    blocks.add(block);
                }
            }
        }

        return blocks;
    }

    public Clipboard pasteSchematic(Location loc, String fileName) {
        File schematic = new File(SMP.getPlugin().getDataFolder() + File.separator + fileName + ".schematic");

        Clipboard clipboard = null;

        ClipboardFormat format = ClipboardFormats.findByFile(schematic);
        try (ClipboardReader reader = format.getReader(Files.newInputStream(schematic.toPath()))) {
            clipboard = reader.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(loc.getWorld()), -1)) {
            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(loc.getX(), loc.getY(), loc.getZ()))
                    .ignoreAirBlocks(true)
                    .build();
            Operations.complete(operation);
        } catch (WorldEditException e) {
            throw new RuntimeException(e);
        }

        return clipboard;
    }

    public int random(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }

    public double random(double min, double max) {
        return Math.random() * (max - min) + min;
    }

    public int getHighestY(World world, int x, int z) {
        int y = 255;
        while(world.getBlockAt(x, y, z).getType() != Material.GRASS_BLOCK) {
            y--;

            if (y <= 0) break;
        }
        return y;
    }

    public boolean isItem(ItemStack item, AztecItem aztec) {
        if (!item.hasItemMeta()) return false;
        if (!item.getItemMeta().hasDisplayName()) return false;

        return item.getItemMeta().getDisplayName().equals(aztec.getItem().getItemMeta().getDisplayName());
    }

    public void fillEmpty(Gui gui) {
        for (int i = 0; i < gui.getSlots(); i++)
            gui.addItem(i, new ItemBuilder(" ", Material.GRAY_STAINED_GLASS_PANE).toItemStack());
    }

    public void fillEmpty(Inventory gui) {
        for (int i = 0; i < gui.getSize(); i++)
            gui.setItem(i, new ItemBuilder(" ", Material.GRAY_STAINED_GLASS_PANE).toItemStack());
    }

    public <T> void shuffle(List<T> list) {
        Random rnd = ThreadLocalRandom.current();
        for (int i = list.size() - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            T t = list.get(index);
            list.set(index, list.get(i));
            list.set(i, t);
        }
    }
}
