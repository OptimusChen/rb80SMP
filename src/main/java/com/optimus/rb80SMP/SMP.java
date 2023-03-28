package com.optimus.rb80SMP;

import com.optimus.rb80SMP.listeners.PlayerListener;
import lombok.Data;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.command.PluginCommand;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public final class SMP extends JavaPlugin {

    public static boolean expandingBorder = false;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        PluginCommand main = getCommand("smp");
        SMPCommand cmd = new SMPCommand();
        main.setExecutor(cmd);
        main.setTabCompleter(cmd);

        getCommand("togglechat").setExecutor(new ToggleChatCommand());

        ItemStack expander = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta meta = expander.getItemMeta();

        meta.setDisplayName(ChatColor.GREEN + "Border Expander");
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Right click with this", ChatColor.GRAY + "to expand border by", ChatColor.GRAY + "200 blocks!"));
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(Enchantment.DIG_SPEED, 1, true);

        expander.setItemMeta(meta);

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(this, "rb80_border_expander"), expander);

        recipe.shape(
          "abc",
          "ded",
          "cba"
        );

        recipe.setIngredient('a', new RecipeChoice.ExactChoice(new ItemStack(Material.EMERALD_BLOCK, 16)));
        recipe.setIngredient('c', new RecipeChoice.ExactChoice(new ItemStack(Material.OBSIDIAN, 16)));
        recipe.setIngredient('b', Material.NETHERITE_INGOT);
        recipe.setIngredient('d', Material.NETHERITE_PICKAXE);
        recipe.setIngredient('e', Material.NETHER_STAR);

        Bukkit.addRecipe(recipe);
    }

    @Override
    public void onDisable() {

    }

    public void messageTeam(Player p, String message) {
        for (Player pl : Bukkit.getOnlinePlayers()) {
            if (Config.getTeamId(pl) == Config.getTeamId(p)) {
                ChatColor color = SMPCommand.teamColors.get(Config.getTeamId(p));
                pl.sendMessage(color + "TEAM " + ChatColor.DARK_GRAY + "> " + color + p.getName() + ChatColor.WHITE + ": " + message);
            }
        }
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
