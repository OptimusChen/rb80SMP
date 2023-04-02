package com.optimus.rb80SMP.aztec;

import com.optimus.rb80SMP.SMP;
import com.optimus.rb80SMP.aztec.entity.npc.LoneWanderer;
import com.optimus.rb80SMP.aztec.entity.npc.Purempecha;
import com.optimus.rb80SMP.aztec.entity.npc.Tezcatlipoca;
import com.optimus.rb80SMP.aztec.entity.npc.TribeMember;
import com.optimus.rb80SMP.aztec.gui.CodexTable;
import com.optimus.rb80SMP.util.Util;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class AztecListener implements Listener {

    private static final Aztec aztec = Aztec.getInstance();

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        ItemStack item = e.getItemInHand();

        if (!item.getType().equals(Material.TRIPWIRE_HOOK)) return;
        if (!item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();

        if (!meta.hasDisplayName()) return;
        if (!Aztec.getInstance().isPortal(e.getBlockAgainst())) return;

        if (meta.getDisplayName().equals(ChatColor.GOLD + "Key to the One World")) {
            World world = e.getBlock().getWorld();
            world.strikeLightningEffect(e.getBlock().getLocation());

            Advancements.grantAdvancement("The One World", e.getPlayer());

            Location pos1 = e.getBlockAgainst().getLocation().clone().add(1, 0, 1);
            Location pos2 = e.getBlockAgainst().getLocation().clone().add(-1, 0, -1);

            List<Block> blocks = Util.blocksFromTwoPoints(pos1, pos2);

            for (Block b : blocks) if (b.getType().equals(Material.AIR) || b.getType().equals(Material.TRIPWIRE_HOOK)) b.setType(Material.END_PORTAL);
        }
    }

    @EventHandler
    public void onCreate(PortalCreateEvent e) {
        if (!e.getWorld().getName().equals("aztec")) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Location below = e.getTo().clone().subtract(0, 1, 0);
        Player p = e.getPlayer();

        if (below.getBlock().getType().equals(Material.END_PORTAL)) {
            if (e.getTo().getWorld().getName().equals("aztec")) {
                World world = Bukkit.getWorld("world");
                p.teleport(world.getSpawnLocation());
            } else {
                Location pos1 = below.getBlock().getLocation().clone().add(1, 0, 1);
                Location pos2 = below.getBlock().getLocation().clone().add(-1, 0, -1);

                List<Block> blocks = Util.blocksFromTwoPoints(pos1, pos2);

                for (Block b : blocks) {
                    if (b.getType().equals(Material.LODESTONE)) {
                        World world = Bukkit.getWorld("aztec");

                        p.teleport(world.getSpawnLocation());
                        break;
                    }
                }
            }
        }

        if (!e.getTo().getWorld().getName().equals("aztec")) return;

        if (e.getFrom().getX() == e.getTo().getX() && e.getFrom().getY() == e.getTo().getY() && e.getFrom().getZ() == e.getTo().getZ()) return;

        double x = e.getTo().getX() + Util.random(0, 20), z = e.getTo().getZ() + Util.random(0, 20);
        int y = Util.getHighestY(e.getTo().getWorld(), (int) x, (int) z);

        Location loc = new Location(e.getTo().getWorld(), x, y, z);

        if (Util.random(0, 1000) == 0) {
            switch (Util.random(0, 2)) {
                case 0:
                    new LoneWanderer().spawn(loc);
                    break;
                case 1:
                    new Purempecha().spawn(loc);
                    break;
                case 2:
                    new TribeMember().spawn(loc);
                    break;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlace(BlockPlaceEvent e) {
        if (e.isCancelled()) return;
        if (!Util.isItem(e.getItemInHand(), AztecItem.CODEX_STAND)) return;

        aztec.getCodex().add(e.getBlockPlaced().getLocation());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlace(BlockBreakEvent e) {
        if (e.isCancelled()) return;
        if (!aztec.getCodex().contains(e.getBlock().getLocation())) return;

        aztec.getCodex().remove(e.getBlock().getLocation());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (!aztec.getCodex().contains(e.getClickedBlock().getLocation())) return;

        e.getPlayer().openInventory(new CodexTable());
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent e) {
        Entity entity = e.getRightClicked();

        if (!entity.getType().equals(EntityType.VILLAGER)) return;

        Villager villager = (Villager) entity;

        if (villager.getProfession().equals(Villager.Profession.NITWIT) && villager.getVillagerType().equals(Villager.Type.JUNGLE)) {

            List<MerchantRecipe> recipes = new ArrayList<>(Arrays.asList(
                    createRecipe(2, new ItemStack(Material.EMERALD, 5), null, new ItemStack(Material.COCOA_BEANS), 16),
                    createRecipe(2, new ItemStack(Material.COCOA_BEANS, 3), null, new ItemStack(Material.DIAMOND), 12)
            ));

            if (villager.getVillagerLevel() >= 2) {
                recipes.add(createRecipe(10, new ItemStack(Material.PURPLE_DYE), null, new ItemStack(Material.COCOA_BEANS, 3), 10));
                recipes.add(createRecipe(10, new ItemStack(Material.COCOA_BEANS, 10), null, new ItemStack(Material.SCUTE), 4));
            }

            if (villager.getVillagerLevel() >= 3) {
                recipes.add(createRecipe(15, new ItemStack(Material.COCOA_BEANS, 10), null, new ItemStack(Material.SPYGLASS), 4));
                recipes.add(createRecipe(15, new ItemStack(Material.COCOA_BEANS, 5), null, new ItemStack(Material.AXOLOTL_BUCKET), 5));
            }

            if (villager.getVillagerLevel() >= 4) {
                recipes.add(createRecipe(20, new ItemStack(Material.COCOA_BEANS, 48), new ItemStack(Material.GOLD_INGOT, 16), AztecItem.SPEAR.getItem(), 4));
                recipes.add(createRecipe(20, new ItemStack(Material.IRON_INGOT, 5), null, new ItemStack(Material.COCOA_BEANS), 16));
            }

            if (villager.getVillagerLevel() >= 5) {
                recipes.add(createRecipe(120, new ItemStack(Material.COCOA_BEANS, 64), new ItemStack(Material.BOOK), new ItemStack(Material.ENCHANTED_BOOK), 5));
                recipes.add(createRecipe(120, new ItemStack(Material.COCOA_BEANS, 32), new ItemStack(Material.NAUTILUS_SHELL, 4), new ItemStack(Material.HEART_OF_THE_SEA), 2));
            }

            villager.setRecipes(recipes);

            e.getPlayer().openMerchant(villager, true);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        InventoryHolder holder = e.getView().getTopInventory().getHolder();

        if (holder == null) return;

        if (holder instanceof Chest) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Chest chest = (Chest) holder;
                    Location below = chest.getLocation().clone().subtract(0, 1, 0);

                    if (!below.getBlock().getType().equals(Material.BARRIER)) return;

                    Location pos1 = chest.getLocation().clone().add(2, 0, 2);
                    Location pos2 = chest.getLocation().clone().subtract(2, 0, 2);

                    List<Block> blocks = Util.blocksFromTwoPoints(pos1, pos2);
                    List<Chest> chests = new ArrayList<>();

                    Block anchor = null;

                    for (Block b : blocks) {
                        if (b.getType().equals(Material.CHEST)) chests.add((Chest) b.getState());
                        if (b.getType().equals(Material.RESPAWN_ANCHOR)) anchor = b;
                    }

                    List<ItemStack> items = new ArrayList<>();

                    for (Chest c : chests) {
                        items.addAll(Arrays.asList(c.getBlockInventory().getContents()));
                    }

                    Bukkit.broadcastMessage(items + "");

                    Bukkit.broadcastMessage(anchor + "");

                    if (anchor == null) return;

                    boolean blood = false;

                    for (ItemStack item : items) {
                        if (item == null) continue;
                        if (Util.isItem(item, AztecItem.BLOOD) && item.getAmount() >= 64) {
                            blood = true;
                            break;
                        }
                    }

                    Bukkit.broadcastMessage(items.contains(AztecItem.SKULL.getItem()) + "");
                    Bukkit.broadcastMessage(items.contains(AztecItem.HEART.getItem()) + "");
                    Bukkit.broadcastMessage(items.contains(AztecItem.TISSUE.getItem()) + "");
                    Bukkit.broadcastMessage(blood + "");

                    if (items.contains(AztecItem.SKULL.getItem()) &&
                            items.contains(AztecItem.HEART.getItem()) &&
                            items.contains(AztecItem.TISSUE.getItem()) &&
                            blood) {

                        Location spawn = anchor.getLocation().clone().add(0, 1, 0);

                        new Tezcatlipoca().spawn(spawn);

                        for (Chest c : chests) c.getBlockInventory().clear();
                    }
                }
            }.runTaskLater(SMP.getPlugin(), 10);
        }
    }

    private MerchantRecipe createRecipe(int xp, ItemStack in, ItemStack in2, ItemStack out, int max) {
        MerchantRecipe recipe = new MerchantRecipe(out, max);

        recipe.addIngredient(in);
        if (in2 != null) recipe.addIngredient(in2);

        recipe.setExperienceReward(true);
        recipe.setVillagerExperience(xp);

        return recipe;
    }
}
