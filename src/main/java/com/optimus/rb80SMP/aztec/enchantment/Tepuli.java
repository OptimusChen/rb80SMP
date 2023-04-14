package com.optimus.rb80SMP.aztec.enchantment;

import enchantmentapi.enchantmentapi.CustomEnchantment;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Tepuli extends CustomEnchantment {
    @Override
    protected String getName() {
        return "Tepuli";
    }

    @Override
    protected String getNamespace() {
        return "aztec";
    }

    @Override
    protected int getMaxLevel() {
        return 3;
    }

    @Override
    protected boolean canEnchant(ItemStack itemStack) {
        return itemStack.getType().name().contains("LEGGINGS") || itemStack.getType().equals(Material.ENCHANTED_BOOK);
    }
}
