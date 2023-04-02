package com.optimus.rb80SMP.aztec;

import com.optimus.rb80SMP.SMP;
import com.optimus.rb80SMP.util.Util;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Aztec {

    private static final SMP plugin = SMP.getPlugin();
    private static Aztec instance;

    public List<Location> codex;
    public AztecConfig config;

    public Aztec() {
        instance = this;

        keyToOneWorld();

        codex = new ArrayList<>();
        config = new AztecConfig();

        if (!config.hasValue("codex")) config.setField("codex", new ArrayList<>());

        codex = (List<Location>) config.getField("codex");

        codex();

        Bukkit.getPluginManager().registerEvents(new AztecListener(), plugin);
    }

    public void disable() {
        config.setField("codex", codex);
    }

    private void keyToOneWorld() {
        ItemStack key = new ItemStack(Material.TRIPWIRE_HOOK);
        ItemMeta meta = key.getItemMeta();

        meta.setDisplayName(ChatColor.GOLD + "Key to the One World");
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Place this key on", ChatColor.GRAY + "lodestone to unlock the portal."));
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(Enchantment.DIG_SPEED, 1, true);

        key.setItemMeta(meta);

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(SMP.getPlugin(), "rb80_one_world_key"), key);

        recipe.shape(
            "abc",
            "bdb",
            "eba"
        );

        recipe.setIngredient('a', Material.NETHER_STAR);
        recipe.setIngredient('b', Material.NETHERITE_INGOT);
        recipe.setIngredient('c', Material.NETHERITE_SWORD);
        recipe.setIngredient('d', new RecipeChoice.ExactChoice(new ItemStack(Material.GOLD_BLOCK, 64)));
        recipe.setIngredient('e', Material.TRIDENT);

        Bukkit.addRecipe(recipe);
    }

    public boolean isPortal(Block block) {
        if (!block.getType().equals(Material.LODESTONE)) return false;

        Location below = block.getLocation().clone().subtract(0, 1, 0);

        return below.getBlock().getType().equals(Material.BARRIER);
    }

    public static Aztec getInstance() { return instance; }

    public List<Location> getCodex() { return codex; }

    public void playerTick(Player p) {
        PlayerInventory inv = p.getInventory();

        // Totems
        if (Util.isItem(inv.getItemInOffHand(), AztecItem.JAGUAR_TOTEM)) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, 0));
        }

        if (Util.isItem(inv.getItemInOffHand(), AztecItem.EAGLE_TOTEM)) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20, 0));
        }

        if (Util.isItem(inv.getItemInOffHand(), AztecItem.ARROW_TOTEM)) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20, 0));
        }
    }

    private void codex() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Location loc : codex) {
                    World world = loc.getWorld();
                    Collection<Entity> nearby = world.getNearbyEntities(loc, 8, 8, 8);

                    for (Entity e : nearby) {
                        if (!e.getType().equals(EntityType.VILLAGER)) continue;

                        Villager villager = (Villager) e;

                        if (!villager.getProfession().equals(Villager.Profession.NONE)) continue;

                        villager.setProfession(Villager.Profession.NITWIT);
                        villager.setVillagerType(Villager.Type.JUNGLE);

                        world.spawnParticle(Particle.VILLAGER_HAPPY, villager.getLocation().clone().add(0, 2, 0), 10);
                    }
                }
            }
        }.runTaskTimer(SMP.getPlugin(), 1, 20L);
    }
}
